//Raddon On Top!

package okhttp3.internal.cache;

import okhttp3.internal.io.*;
import okhttp3.internal.platform.*;
import okhttp3.internal.*;
import java.util.concurrent.*;
import java.io.*;
import javax.annotation.*;
import java.util.regex.*;
import okio.*;
import java.util.*;

public final class DiskLruCache implements Closeable, Flushable
{
    static final String JOURNAL_FILE = "journal";
    static final String JOURNAL_FILE_TEMP = "journal.tmp";
    static final String JOURNAL_FILE_BACKUP = "journal.bkp";
    static final String MAGIC = "libcore.io.DiskLruCache";
    static final String VERSION_1 = "1";
    static final long ANY_SEQUENCE_NUMBER = -1L;
    static final Pattern LEGAL_KEY_PATTERN;
    private static final String CLEAN = "CLEAN";
    private static final String DIRTY = "DIRTY";
    private static final String REMOVE = "REMOVE";
    private static final String READ = "READ";
    final FileSystem fileSystem;
    final File directory;
    private final File journalFile;
    private final File journalFileTmp;
    private final File journalFileBackup;
    private final int appVersion;
    private long maxSize;
    final int valueCount;
    private long size;
    BufferedSink journalWriter;
    final LinkedHashMap<String, Entry> lruEntries;
    int redundantOpCount;
    boolean hasJournalErrors;
    boolean initialized;
    boolean closed;
    boolean mostRecentTrimFailed;
    boolean mostRecentRebuildFailed;
    private long nextSequenceNumber;
    private final Executor executor;
    private final Runnable cleanupRunnable;
    
    DiskLruCache(final FileSystem fileSystem, final File directory, final int appVersion, final int valueCount, final long maxSize, final Executor executor) {
        this.size = 0L;
        this.lruEntries = new LinkedHashMap<String, Entry>(0, 0.75f, true);
        this.nextSequenceNumber = 0L;
        this.cleanupRunnable = new Runnable() {
            @Override
            public void run() {
                synchronized (DiskLruCache.this) {
                    if (!DiskLruCache.this.initialized | DiskLruCache.this.closed) {
                        return;
                    }
                    try {
                        DiskLruCache.this.trimToSize();
                    }
                    catch (IOException ignored) {
                        DiskLruCache.this.mostRecentTrimFailed = true;
                    }
                    try {
                        if (DiskLruCache.this.journalRebuildRequired()) {
                            DiskLruCache.this.rebuildJournal();
                            DiskLruCache.this.redundantOpCount = 0;
                        }
                    }
                    catch (IOException e) {
                        DiskLruCache.this.mostRecentRebuildFailed = true;
                        DiskLruCache.this.journalWriter = Okio.buffer(Okio.blackhole());
                    }
                }
            }
        };
        this.fileSystem = fileSystem;
        this.directory = directory;
        this.appVersion = appVersion;
        this.journalFile = new File(directory, "journal");
        this.journalFileTmp = new File(directory, "journal.tmp");
        this.journalFileBackup = new File(directory, "journal.bkp");
        this.valueCount = valueCount;
        this.maxSize = maxSize;
        this.executor = executor;
    }
    
    public synchronized void initialize() throws IOException {
        assert Thread.holdsLock(this);
        if (this.initialized) {
            return;
        }
        if (this.fileSystem.exists(this.journalFileBackup)) {
            if (this.fileSystem.exists(this.journalFile)) {
                this.fileSystem.delete(this.journalFileBackup);
            }
            else {
                this.fileSystem.rename(this.journalFileBackup, this.journalFile);
            }
        }
        if (this.fileSystem.exists(this.journalFile)) {
            try {
                this.readJournal();
                this.processJournal();
                this.initialized = true;
                return;
            }
            catch (IOException journalIsCorrupt) {
                Platform.get().log(5, "DiskLruCache " + this.directory + " is corrupt: " + journalIsCorrupt.getMessage() + ", removing", journalIsCorrupt);
                try {
                    this.delete();
                }
                finally {
                    this.closed = false;
                }
            }
        }
        this.rebuildJournal();
        this.initialized = true;
    }
    
    public static DiskLruCache create(final FileSystem fileSystem, final File directory, final int appVersion, final int valueCount, final long maxSize) {
        if (maxSize <= 0L) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        if (valueCount <= 0) {
            throw new IllegalArgumentException("valueCount <= 0");
        }
        final Executor executor = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), Util.threadFactory("OkHttp DiskLruCache", true));
        return new DiskLruCache(fileSystem, directory, appVersion, valueCount, maxSize, executor);
    }
    
    private void readJournal() throws IOException {
        final BufferedSource source = Okio.buffer(this.fileSystem.source(this.journalFile));
        Throwable x0 = null;
        try {
            final String magic = source.readUtf8LineStrict();
            final String version = source.readUtf8LineStrict();
            final String appVersionString = source.readUtf8LineStrict();
            final String valueCountString = source.readUtf8LineStrict();
            final String blank = source.readUtf8LineStrict();
            if (!"libcore.io.DiskLruCache".equals(magic) || !"1".equals(version) || !Integer.toString(this.appVersion).equals(appVersionString) || !Integer.toString(this.valueCount).equals(valueCountString) || !"".equals(blank)) {
                throw new IOException("unexpected journal header: [" + magic + ", " + version + ", " + valueCountString + ", " + blank + "]");
            }
            int lineCount = 0;
            try {
                while (true) {
                    this.readJournalLine(source.readUtf8LineStrict());
                    ++lineCount;
                }
            }
            catch (EOFException endOfJournal) {
                this.redundantOpCount = lineCount - this.lruEntries.size();
                if (!source.exhausted()) {
                    this.rebuildJournal();
                }
                else {
                    this.journalWriter = this.newJournalWriter();
                }
            }
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (source != null) {
                $closeResource(x0, (AutoCloseable)source);
            }
        }
    }
    
    private BufferedSink newJournalWriter() throws FileNotFoundException {
        final Sink fileSink = this.fileSystem.appendingSink(this.journalFile);
        final Sink faultHidingSink = (Sink)new FaultHidingSink(fileSink) {
            @Override
            protected void onException(final IOException e) {
                assert Thread.holdsLock(DiskLruCache.this);
                DiskLruCache.this.hasJournalErrors = true;
            }
        };
        return Okio.buffer(faultHidingSink);
    }
    
    private void readJournalLine(final String line) throws IOException {
        final int firstSpace = line.indexOf(32);
        if (firstSpace == -1) {
            throw new IOException("unexpected journal line: " + line);
        }
        final int keyBegin = firstSpace + 1;
        final int secondSpace = line.indexOf(32, keyBegin);
        String key;
        if (secondSpace == -1) {
            key = line.substring(keyBegin);
            if (firstSpace == "REMOVE".length() && line.startsWith("REMOVE")) {
                this.lruEntries.remove(key);
                return;
            }
        }
        else {
            key = line.substring(keyBegin, secondSpace);
        }
        Entry entry = this.lruEntries.get(key);
        if (entry == null) {
            entry = new Entry(key);
            this.lruEntries.put(key, entry);
        }
        if (secondSpace != -1 && firstSpace == "CLEAN".length() && line.startsWith("CLEAN")) {
            final String[] parts = line.substring(secondSpace + 1).split(" ");
            entry.readable = true;
            entry.currentEditor = null;
            entry.setLengths(parts);
        }
        else if (secondSpace == -1 && firstSpace == "DIRTY".length() && line.startsWith("DIRTY")) {
            entry.currentEditor = new Editor(entry);
        }
        else if (secondSpace != -1 || firstSpace != "READ".length() || !line.startsWith("READ")) {
            throw new IOException("unexpected journal line: " + line);
        }
    }
    
    private void processJournal() throws IOException {
        this.fileSystem.delete(this.journalFileTmp);
        final Iterator<Entry> i = this.lruEntries.values().iterator();
        while (i.hasNext()) {
            final Entry entry = i.next();
            if (entry.currentEditor == null) {
                for (int t = 0; t < this.valueCount; ++t) {
                    this.size += entry.lengths[t];
                }
            }
            else {
                entry.currentEditor = null;
                for (int t = 0; t < this.valueCount; ++t) {
                    this.fileSystem.delete(entry.cleanFiles[t]);
                    this.fileSystem.delete(entry.dirtyFiles[t]);
                }
                i.remove();
            }
        }
    }
    
    synchronized void rebuildJournal() throws IOException {
        if (this.journalWriter != null) {
            this.journalWriter.close();
        }
        final BufferedSink writer = Okio.buffer(this.fileSystem.sink(this.journalFileTmp));
        Throwable x0 = null;
        try {
            writer.writeUtf8("libcore.io.DiskLruCache").writeByte(10);
            writer.writeUtf8("1").writeByte(10);
            writer.writeDecimalLong((long)this.appVersion).writeByte(10);
            writer.writeDecimalLong((long)this.valueCount).writeByte(10);
            writer.writeByte(10);
            for (final Entry entry : this.lruEntries.values()) {
                if (entry.currentEditor != null) {
                    writer.writeUtf8("DIRTY").writeByte(32);
                    writer.writeUtf8(entry.key);
                    writer.writeByte(10);
                }
                else {
                    writer.writeUtf8("CLEAN").writeByte(32);
                    writer.writeUtf8(entry.key);
                    entry.writeLengths(writer);
                    writer.writeByte(10);
                }
            }
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (writer != null) {
                $closeResource(x0, (AutoCloseable)writer);
            }
        }
        if (this.fileSystem.exists(this.journalFile)) {
            this.fileSystem.rename(this.journalFile, this.journalFileBackup);
        }
        this.fileSystem.rename(this.journalFileTmp, this.journalFile);
        this.fileSystem.delete(this.journalFileBackup);
        this.journalWriter = this.newJournalWriter();
        this.hasJournalErrors = false;
        this.mostRecentRebuildFailed = false;
    }
    
    public synchronized Snapshot get(final String key) throws IOException {
        this.initialize();
        this.checkNotClosed();
        this.validateKey(key);
        final Entry entry = this.lruEntries.get(key);
        if (entry == null || !entry.readable) {
            return null;
        }
        final Snapshot snapshot = entry.snapshot();
        if (snapshot == null) {
            return null;
        }
        ++this.redundantOpCount;
        this.journalWriter.writeUtf8("READ").writeByte(32).writeUtf8(key).writeByte(10);
        if (this.journalRebuildRequired()) {
            this.executor.execute(this.cleanupRunnable);
        }
        return snapshot;
    }
    
    @Nullable
    public Editor edit(final String key) throws IOException {
        return this.edit(key, -1L);
    }
    
    synchronized Editor edit(final String key, final long expectedSequenceNumber) throws IOException {
        this.initialize();
        this.checkNotClosed();
        this.validateKey(key);
        Entry entry = this.lruEntries.get(key);
        if (expectedSequenceNumber != -1L && (entry == null || entry.sequenceNumber != expectedSequenceNumber)) {
            return null;
        }
        if (entry != null && entry.currentEditor != null) {
            return null;
        }
        if (this.mostRecentTrimFailed || this.mostRecentRebuildFailed) {
            this.executor.execute(this.cleanupRunnable);
            return null;
        }
        this.journalWriter.writeUtf8("DIRTY").writeByte(32).writeUtf8(key).writeByte(10);
        this.journalWriter.flush();
        if (this.hasJournalErrors) {
            return null;
        }
        if (entry == null) {
            entry = new Entry(key);
            this.lruEntries.put(key, entry);
        }
        final Editor editor = new Editor(entry);
        return entry.currentEditor = editor;
    }
    
    public File getDirectory() {
        return this.directory;
    }
    
    public synchronized long getMaxSize() {
        return this.maxSize;
    }
    
    public synchronized void setMaxSize(final long maxSize) {
        this.maxSize = maxSize;
        if (this.initialized) {
            this.executor.execute(this.cleanupRunnable);
        }
    }
    
    public synchronized long size() throws IOException {
        this.initialize();
        return this.size;
    }
    
    synchronized void completeEdit(final Editor editor, final boolean success) throws IOException {
        final Entry entry = editor.entry;
        if (entry.currentEditor != editor) {
            throw new IllegalStateException();
        }
        if (success && !entry.readable) {
            for (int i = 0; i < this.valueCount; ++i) {
                if (!editor.written[i]) {
                    editor.abort();
                    throw new IllegalStateException("Newly created entry didn't create value for index " + i);
                }
                if (!this.fileSystem.exists(entry.dirtyFiles[i])) {
                    editor.abort();
                    return;
                }
            }
        }
        for (int i = 0; i < this.valueCount; ++i) {
            final File dirty = entry.dirtyFiles[i];
            if (success) {
                if (this.fileSystem.exists(dirty)) {
                    final File clean = entry.cleanFiles[i];
                    this.fileSystem.rename(dirty, clean);
                    final long oldLength = entry.lengths[i];
                    final long newLength = this.fileSystem.size(clean);
                    entry.lengths[i] = newLength;
                    this.size = this.size - oldLength + newLength;
                }
            }
            else {
                this.fileSystem.delete(dirty);
            }
        }
        ++this.redundantOpCount;
        entry.currentEditor = null;
        if (entry.readable | success) {
            entry.readable = true;
            this.journalWriter.writeUtf8("CLEAN").writeByte(32);
            this.journalWriter.writeUtf8(entry.key);
            entry.writeLengths(this.journalWriter);
            this.journalWriter.writeByte(10);
            if (success) {
                entry.sequenceNumber = this.nextSequenceNumber++;
            }
        }
        else {
            this.lruEntries.remove(entry.key);
            this.journalWriter.writeUtf8("REMOVE").writeByte(32);
            this.journalWriter.writeUtf8(entry.key);
            this.journalWriter.writeByte(10);
        }
        this.journalWriter.flush();
        if (this.size > this.maxSize || this.journalRebuildRequired()) {
            this.executor.execute(this.cleanupRunnable);
        }
    }
    
    boolean journalRebuildRequired() {
        final int redundantOpCompactThreshold = 2000;
        return this.redundantOpCount >= 2000 && this.redundantOpCount >= this.lruEntries.size();
    }
    
    public synchronized boolean remove(final String key) throws IOException {
        this.initialize();
        this.checkNotClosed();
        this.validateKey(key);
        final Entry entry = this.lruEntries.get(key);
        if (entry == null) {
            return false;
        }
        final boolean removed = this.removeEntry(entry);
        if (removed && this.size <= this.maxSize) {
            this.mostRecentTrimFailed = false;
        }
        return removed;
    }
    
    boolean removeEntry(final Entry entry) throws IOException {
        if (entry.currentEditor != null) {
            entry.currentEditor.detach();
        }
        for (int i = 0; i < this.valueCount; ++i) {
            this.fileSystem.delete(entry.cleanFiles[i]);
            this.size -= entry.lengths[i];
            entry.lengths[i] = 0L;
        }
        ++this.redundantOpCount;
        this.journalWriter.writeUtf8("REMOVE").writeByte(32).writeUtf8(entry.key).writeByte(10);
        this.lruEntries.remove(entry.key);
        if (this.journalRebuildRequired()) {
            this.executor.execute(this.cleanupRunnable);
        }
        return true;
    }
    
    public synchronized boolean isClosed() {
        return this.closed;
    }
    
    private synchronized void checkNotClosed() {
        if (this.isClosed()) {
            throw new IllegalStateException("cache is closed");
        }
    }
    
    @Override
    public synchronized void flush() throws IOException {
        if (!this.initialized) {
            return;
        }
        this.checkNotClosed();
        this.trimToSize();
        this.journalWriter.flush();
    }
    
    @Override
    public synchronized void close() throws IOException {
        if (!this.initialized || this.closed) {
            this.closed = true;
            return;
        }
        for (final Entry entry : this.lruEntries.values().toArray(new Entry[this.lruEntries.size()])) {
            if (entry.currentEditor != null) {
                entry.currentEditor.abort();
            }
        }
        this.trimToSize();
        this.journalWriter.close();
        this.journalWriter = null;
        this.closed = true;
    }
    
    void trimToSize() throws IOException {
        while (this.size > this.maxSize) {
            final Entry toEvict = this.lruEntries.values().iterator().next();
            this.removeEntry(toEvict);
        }
        this.mostRecentTrimFailed = false;
    }
    
    public void delete() throws IOException {
        this.close();
        this.fileSystem.deleteContents(this.directory);
    }
    
    public synchronized void evictAll() throws IOException {
        this.initialize();
        for (final Entry entry : this.lruEntries.values().toArray(new Entry[this.lruEntries.size()])) {
            this.removeEntry(entry);
        }
        this.mostRecentTrimFailed = false;
    }
    
    private void validateKey(final String key) {
        final Matcher matcher = DiskLruCache.LEGAL_KEY_PATTERN.matcher(key);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("keys must match regex [a-z0-9_-]{1,120}: \"" + key + "\"");
        }
    }
    
    public synchronized Iterator<Snapshot> snapshots() throws IOException {
        this.initialize();
        return new Iterator<Snapshot>() {
            final Iterator<Entry> delegate = new ArrayList<Entry>(DiskLruCache.this.lruEntries.values()).iterator();
            Snapshot nextSnapshot;
            Snapshot removeSnapshot;
            
            @Override
            public boolean hasNext() {
                if (this.nextSnapshot != null) {
                    return true;
                }
                synchronized (DiskLruCache.this) {
                    if (DiskLruCache.this.closed) {
                        return false;
                    }
                    while (this.delegate.hasNext()) {
                        final Entry entry = this.delegate.next();
                        if (!entry.readable) {
                            continue;
                        }
                        final Snapshot snapshot = entry.snapshot();
                        if (snapshot == null) {
                            continue;
                        }
                        this.nextSnapshot = snapshot;
                        return true;
                    }
                }
                return false;
            }
            
            @Override
            public Snapshot next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.removeSnapshot = this.nextSnapshot;
                this.nextSnapshot = null;
                return this.removeSnapshot;
            }
            
            @Override
            public void remove() {
                if (this.removeSnapshot == null) {
                    throw new IllegalStateException("remove() before next()");
                }
                try {
                    DiskLruCache.this.remove(this.removeSnapshot.key);
                }
                catch (IOException ex) {}
                finally {
                    this.removeSnapshot = null;
                }
            }
        };
    }
    
    private static /* synthetic */ void $closeResource(final Throwable x0, final AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            }
            catch (Throwable t) {
                x0.addSuppressed(t);
            }
        }
        else {
            x1.close();
        }
    }
    
    static {
        LEGAL_KEY_PATTERN = Pattern.compile("[a-z0-9_-]{1,120}");
    }
    
    public final class Snapshot implements Closeable
    {
        private final String key;
        private final long sequenceNumber;
        private final Source[] sources;
        private final long[] lengths;
        
        Snapshot(final String key, final long sequenceNumber, final Source[] sources, final long[] lengths) {
            this.key = key;
            this.sequenceNumber = sequenceNumber;
            this.sources = sources;
            this.lengths = lengths;
        }
        
        public String key() {
            return this.key;
        }
        
        @Nullable
        public Editor edit() throws IOException {
            return DiskLruCache.this.edit(this.key, this.sequenceNumber);
        }
        
        public Source getSource(final int index) {
            return this.sources[index];
        }
        
        public long getLength(final int index) {
            return this.lengths[index];
        }
        
        @Override
        public void close() {
            for (final Source in : this.sources) {
                Util.closeQuietly((Closeable)in);
            }
        }
    }
    
    public final class Editor
    {
        final Entry entry;
        final boolean[] written;
        private boolean done;
        
        Editor(final Entry entry) {
            this.entry = entry;
            this.written = (boolean[])(entry.readable ? null : new boolean[DiskLruCache.this.valueCount]);
        }
        
        void detach() {
            if (this.entry.currentEditor == this) {
                for (int i = 0; i < DiskLruCache.this.valueCount; ++i) {
                    try {
                        DiskLruCache.this.fileSystem.delete(this.entry.dirtyFiles[i]);
                    }
                    catch (IOException ex) {}
                }
                this.entry.currentEditor = null;
            }
        }
        
        public Source newSource(final int index) {
            synchronized (DiskLruCache.this) {
                if (this.done) {
                    throw new IllegalStateException();
                }
                if (!this.entry.readable || this.entry.currentEditor != this) {
                    return null;
                }
                try {
                    return DiskLruCache.this.fileSystem.source(this.entry.cleanFiles[index]);
                }
                catch (FileNotFoundException e) {
                    return null;
                }
            }
        }
        
        public Sink newSink(final int index) {
            synchronized (DiskLruCache.this) {
                if (this.done) {
                    throw new IllegalStateException();
                }
                if (this.entry.currentEditor != this) {
                    return Okio.blackhole();
                }
                if (!this.entry.readable) {
                    this.written[index] = true;
                }
                final File dirtyFile = this.entry.dirtyFiles[index];
                Sink sink;
                try {
                    sink = DiskLruCache.this.fileSystem.sink(dirtyFile);
                }
                catch (FileNotFoundException e) {
                    return Okio.blackhole();
                }
                return (Sink)new FaultHidingSink(sink) {
                    @Override
                    protected void onException(final IOException e) {
                        synchronized (DiskLruCache.this) {
                            Editor.this.detach();
                        }
                    }
                };
            }
        }
        
        public void commit() throws IOException {
            synchronized (DiskLruCache.this) {
                if (this.done) {
                    throw new IllegalStateException();
                }
                if (this.entry.currentEditor == this) {
                    DiskLruCache.this.completeEdit(this, true);
                }
                this.done = true;
            }
        }
        
        public void abort() throws IOException {
            synchronized (DiskLruCache.this) {
                if (this.done) {
                    throw new IllegalStateException();
                }
                if (this.entry.currentEditor == this) {
                    DiskLruCache.this.completeEdit(this, false);
                }
                this.done = true;
            }
        }
        
        public void abortUnlessCommitted() {
            synchronized (DiskLruCache.this) {
                if (!this.done && this.entry.currentEditor == this) {
                    try {
                        DiskLruCache.this.completeEdit(this, false);
                    }
                    catch (IOException ex) {}
                }
            }
        }
    }
    
    private final class Entry
    {
        final String key;
        final long[] lengths;
        final File[] cleanFiles;
        final File[] dirtyFiles;
        boolean readable;
        Editor currentEditor;
        long sequenceNumber;
        
        Entry(final String key) {
            this.key = key;
            this.lengths = new long[DiskLruCache.this.valueCount];
            this.cleanFiles = new File[DiskLruCache.this.valueCount];
            this.dirtyFiles = new File[DiskLruCache.this.valueCount];
            final StringBuilder fileBuilder = new StringBuilder(key).append('.');
            final int truncateTo = fileBuilder.length();
            for (int i = 0; i < DiskLruCache.this.valueCount; ++i) {
                fileBuilder.append(i);
                this.cleanFiles[i] = new File(DiskLruCache.this.directory, fileBuilder.toString());
                fileBuilder.append(".tmp");
                this.dirtyFiles[i] = new File(DiskLruCache.this.directory, fileBuilder.toString());
                fileBuilder.setLength(truncateTo);
            }
        }
        
        void setLengths(final String[] strings) throws IOException {
            if (strings.length != DiskLruCache.this.valueCount) {
                throw this.invalidLengths(strings);
            }
            try {
                for (int i = 0; i < strings.length; ++i) {
                    this.lengths[i] = Long.parseLong(strings[i]);
                }
            }
            catch (NumberFormatException e) {
                throw this.invalidLengths(strings);
            }
        }
        
        void writeLengths(final BufferedSink writer) throws IOException {
            for (final long length : this.lengths) {
                writer.writeByte(32).writeDecimalLong(length);
            }
        }
        
        private IOException invalidLengths(final String[] strings) throws IOException {
            throw new IOException("unexpected journal line: " + Arrays.toString(strings));
        }
        
        Snapshot snapshot() {
            if (!Thread.holdsLock(DiskLruCache.this)) {
                throw new AssertionError();
            }
            final Source[] sources = new Source[DiskLruCache.this.valueCount];
            final long[] lengths = this.lengths.clone();
            try {
                for (int i = 0; i < DiskLruCache.this.valueCount; ++i) {
                    sources[i] = DiskLruCache.this.fileSystem.source(this.cleanFiles[i]);
                }
                return new Snapshot(this.key, this.sequenceNumber, sources, lengths);
            }
            catch (FileNotFoundException e) {
                for (int j = 0; j < DiskLruCache.this.valueCount && sources[j] != null; ++j) {
                    Util.closeQuietly((Closeable)sources[j]);
                }
                try {
                    DiskLruCache.this.removeEntry(this);
                }
                catch (IOException ex) {}
                return null;
            }
        }
    }
}
