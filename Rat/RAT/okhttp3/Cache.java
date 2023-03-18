//Raddon On Top!

package okhttp3;

import okhttp3.internal.io.*;
import java.io.*;
import javax.annotation.*;
import okhttp3.internal.cache.*;
import okhttp3.internal.*;
import okhttp3.internal.http.*;
import java.util.*;
import java.security.cert.*;
import okhttp3.internal.platform.*;
import okio.*;

public final class Cache implements Closeable, Flushable
{
    private static final int VERSION = 201105;
    private static final int ENTRY_METADATA = 0;
    private static final int ENTRY_BODY = 1;
    private static final int ENTRY_COUNT = 2;
    final InternalCache internalCache;
    final DiskLruCache cache;
    int writeSuccessCount;
    int writeAbortCount;
    private int networkCount;
    private int hitCount;
    private int requestCount;
    
    public Cache(final File directory, final long maxSize) {
        this(directory, maxSize, FileSystem.SYSTEM);
    }
    
    Cache(final File directory, final long maxSize, final FileSystem fileSystem) {
        this.internalCache = new InternalCache() {
            @Nullable
            @Override
            public Response get(final Request request) throws IOException {
                return Cache.this.get(request);
            }
            
            @Nullable
            @Override
            public CacheRequest put(final Response response) throws IOException {
                return Cache.this.put(response);
            }
            
            @Override
            public void remove(final Request request) throws IOException {
                Cache.this.remove(request);
            }
            
            @Override
            public void update(final Response cached, final Response network) {
                Cache.this.update(cached, network);
            }
            
            @Override
            public void trackConditionalCacheHit() {
                Cache.this.trackConditionalCacheHit();
            }
            
            @Override
            public void trackResponse(final CacheStrategy cacheStrategy) {
                Cache.this.trackResponse(cacheStrategy);
            }
        };
        this.cache = DiskLruCache.create(fileSystem, directory, 201105, 2, maxSize);
    }
    
    public static String key(final HttpUrl url) {
        return ByteString.encodeUtf8(url.toString()).md5().hex();
    }
    
    @Nullable
    Response get(final Request request) {
        final String key = key(request.url());
        DiskLruCache.Snapshot snapshot;
        try {
            snapshot = this.cache.get(key);
            if (snapshot == null) {
                return null;
            }
        }
        catch (IOException e) {
            return null;
        }
        Entry entry;
        try {
            entry = new Entry(snapshot.getSource(0));
        }
        catch (IOException e) {
            Util.closeQuietly(snapshot);
            return null;
        }
        final Response response = entry.response(snapshot);
        if (!entry.matches(request, response)) {
            Util.closeQuietly(response.body());
            return null;
        }
        return response;
    }
    
    @Nullable
    CacheRequest put(final Response response) {
        final String requestMethod = response.request().method();
        if (HttpMethod.invalidatesCache(response.request().method())) {
            try {
                this.remove(response.request());
            }
            catch (IOException ex) {}
            return null;
        }
        if (!requestMethod.equals("GET")) {
            return null;
        }
        if (HttpHeaders.hasVaryAll(response)) {
            return null;
        }
        final Entry entry = new Entry(response);
        DiskLruCache.Editor editor = null;
        try {
            editor = this.cache.edit(key(response.request().url()));
            if (editor == null) {
                return null;
            }
            entry.writeTo(editor);
            return new CacheRequestImpl(editor);
        }
        catch (IOException e) {
            this.abortQuietly(editor);
            return null;
        }
    }
    
    void remove(final Request request) throws IOException {
        this.cache.remove(key(request.url()));
    }
    
    void update(final Response cached, final Response network) {
        final Entry entry = new Entry(network);
        final DiskLruCache.Snapshot snapshot = ((CacheResponseBody)cached.body()).snapshot;
        DiskLruCache.Editor editor = null;
        try {
            editor = snapshot.edit();
            if (editor != null) {
                entry.writeTo(editor);
                editor.commit();
            }
        }
        catch (IOException e) {
            this.abortQuietly(editor);
        }
    }
    
    private void abortQuietly(@Nullable final DiskLruCache.Editor editor) {
        try {
            if (editor != null) {
                editor.abort();
            }
        }
        catch (IOException ex) {}
    }
    
    public void initialize() throws IOException {
        this.cache.initialize();
    }
    
    public void delete() throws IOException {
        this.cache.delete();
    }
    
    public void evictAll() throws IOException {
        this.cache.evictAll();
    }
    
    public Iterator<String> urls() throws IOException {
        return new Iterator<String>() {
            final Iterator<DiskLruCache.Snapshot> delegate = Cache.this.cache.snapshots();
            @Nullable
            String nextUrl;
            boolean canRemove;
            
            @Override
            public boolean hasNext() {
                if (this.nextUrl != null) {
                    return true;
                }
                this.canRemove = false;
                while (this.delegate.hasNext()) {
                    try (final DiskLruCache.Snapshot snapshot = this.delegate.next()) {
                        final BufferedSource metadata = Okio.buffer(snapshot.getSource(0));
                        this.nextUrl = metadata.readUtf8LineStrict();
                        return true;
                    }
                    catch (IOException ex) {
                        continue;
                    }
                    break;
                }
                return false;
            }
            
            @Override
            public String next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                final String result = this.nextUrl;
                this.nextUrl = null;
                this.canRemove = true;
                return result;
            }
            
            @Override
            public void remove() {
                if (!this.canRemove) {
                    throw new IllegalStateException("remove() before next()");
                }
                this.delegate.remove();
            }
        };
    }
    
    public synchronized int writeAbortCount() {
        return this.writeAbortCount;
    }
    
    public synchronized int writeSuccessCount() {
        return this.writeSuccessCount;
    }
    
    public long size() throws IOException {
        return this.cache.size();
    }
    
    public long maxSize() {
        return this.cache.getMaxSize();
    }
    
    @Override
    public void flush() throws IOException {
        this.cache.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.cache.close();
    }
    
    public File directory() {
        return this.cache.getDirectory();
    }
    
    public boolean isClosed() {
        return this.cache.isClosed();
    }
    
    synchronized void trackResponse(final CacheStrategy cacheStrategy) {
        ++this.requestCount;
        if (cacheStrategy.networkRequest != null) {
            ++this.networkCount;
        }
        else if (cacheStrategy.cacheResponse != null) {
            ++this.hitCount;
        }
    }
    
    synchronized void trackConditionalCacheHit() {
        ++this.hitCount;
    }
    
    public synchronized int networkCount() {
        return this.networkCount;
    }
    
    public synchronized int hitCount() {
        return this.hitCount;
    }
    
    public synchronized int requestCount() {
        return this.requestCount;
    }
    
    static int readInt(final BufferedSource source) throws IOException {
        try {
            final long result = source.readDecimalLong();
            final String line = source.readUtf8LineStrict();
            if (result < 0L || result > 2147483647L || !line.isEmpty()) {
                throw new IOException("expected an int but was \"" + result + line + "\"");
            }
            return (int)result;
        }
        catch (NumberFormatException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    private final class CacheRequestImpl implements CacheRequest
    {
        private final DiskLruCache.Editor editor;
        private Sink cacheOut;
        private Sink body;
        boolean done;
        
        CacheRequestImpl(final DiskLruCache.Editor editor) {
            this.editor = editor;
            this.cacheOut = editor.newSink(1);
            this.body = (Sink)new ForwardingSink(this.cacheOut) {
                public void close() throws IOException {
                    synchronized (Cache.this) {
                        if (CacheRequestImpl.this.done) {
                            return;
                        }
                        CacheRequestImpl.this.done = true;
                        final Cache this$0 = Cache.this;
                        ++this$0.writeSuccessCount;
                    }
                    super.close();
                    editor.commit();
                }
            };
        }
        
        @Override
        public void abort() {
            synchronized (Cache.this) {
                if (this.done) {
                    return;
                }
                this.done = true;
                final Cache this$0 = Cache.this;
                ++this$0.writeAbortCount;
            }
            Util.closeQuietly((Closeable)this.cacheOut);
            try {
                this.editor.abort();
            }
            catch (IOException ex) {}
        }
        
        @Override
        public Sink body() {
            return this.body;
        }
    }
    
    private static final class Entry
    {
        private static final String SENT_MILLIS;
        private static final String RECEIVED_MILLIS;
        private final String url;
        private final Headers varyHeaders;
        private final String requestMethod;
        private final Protocol protocol;
        private final int code;
        private final String message;
        private final Headers responseHeaders;
        @Nullable
        private final Handshake handshake;
        private final long sentRequestMillis;
        private final long receivedResponseMillis;
        
        Entry(final Source in) throws IOException {
            try {
                final BufferedSource source = Okio.buffer(in);
                this.url = source.readUtf8LineStrict();
                this.requestMethod = source.readUtf8LineStrict();
                final Headers.Builder varyHeadersBuilder = new Headers.Builder();
                for (int varyRequestHeaderLineCount = Cache.readInt(source), i = 0; i < varyRequestHeaderLineCount; ++i) {
                    varyHeadersBuilder.addLenient(source.readUtf8LineStrict());
                }
                this.varyHeaders = varyHeadersBuilder.build();
                final StatusLine statusLine = StatusLine.parse(source.readUtf8LineStrict());
                this.protocol = statusLine.protocol;
                this.code = statusLine.code;
                this.message = statusLine.message;
                final Headers.Builder responseHeadersBuilder = new Headers.Builder();
                for (int responseHeaderLineCount = Cache.readInt(source), j = 0; j < responseHeaderLineCount; ++j) {
                    responseHeadersBuilder.addLenient(source.readUtf8LineStrict());
                }
                final String sendRequestMillisString = responseHeadersBuilder.get(Entry.SENT_MILLIS);
                final String receivedResponseMillisString = responseHeadersBuilder.get(Entry.RECEIVED_MILLIS);
                responseHeadersBuilder.removeAll(Entry.SENT_MILLIS);
                responseHeadersBuilder.removeAll(Entry.RECEIVED_MILLIS);
                this.sentRequestMillis = ((sendRequestMillisString != null) ? Long.parseLong(sendRequestMillisString) : 0L);
                this.receivedResponseMillis = ((receivedResponseMillisString != null) ? Long.parseLong(receivedResponseMillisString) : 0L);
                this.responseHeaders = responseHeadersBuilder.build();
                if (this.isHttps()) {
                    final String blank = source.readUtf8LineStrict();
                    if (blank.length() > 0) {
                        throw new IOException("expected \"\" but was \"" + blank + "\"");
                    }
                    final String cipherSuiteString = source.readUtf8LineStrict();
                    final CipherSuite cipherSuite = CipherSuite.forJavaName(cipherSuiteString);
                    final List<Certificate> peerCertificates = this.readCertificateList(source);
                    final List<Certificate> localCertificates = this.readCertificateList(source);
                    final TlsVersion tlsVersion = source.exhausted() ? TlsVersion.SSL_3_0 : TlsVersion.forJavaName(source.readUtf8LineStrict());
                    this.handshake = Handshake.get(tlsVersion, cipherSuite, peerCertificates, localCertificates);
                }
                else {
                    this.handshake = null;
                }
            }
            finally {
                in.close();
            }
        }
        
        Entry(final Response response) {
            this.url = response.request().url().toString();
            this.varyHeaders = HttpHeaders.varyHeaders(response);
            this.requestMethod = response.request().method();
            this.protocol = response.protocol();
            this.code = response.code();
            this.message = response.message();
            this.responseHeaders = response.headers();
            this.handshake = response.handshake();
            this.sentRequestMillis = response.sentRequestAtMillis();
            this.receivedResponseMillis = response.receivedResponseAtMillis();
        }
        
        public void writeTo(final DiskLruCache.Editor editor) throws IOException {
            final BufferedSink sink = Okio.buffer(editor.newSink(0));
            sink.writeUtf8(this.url).writeByte(10);
            sink.writeUtf8(this.requestMethod).writeByte(10);
            sink.writeDecimalLong((long)this.varyHeaders.size()).writeByte(10);
            for (int i = 0, size = this.varyHeaders.size(); i < size; ++i) {
                sink.writeUtf8(this.varyHeaders.name(i)).writeUtf8(": ").writeUtf8(this.varyHeaders.value(i)).writeByte(10);
            }
            sink.writeUtf8(new StatusLine(this.protocol, this.code, this.message).toString()).writeByte(10);
            sink.writeDecimalLong((long)(this.responseHeaders.size() + 2)).writeByte(10);
            for (int i = 0, size = this.responseHeaders.size(); i < size; ++i) {
                sink.writeUtf8(this.responseHeaders.name(i)).writeUtf8(": ").writeUtf8(this.responseHeaders.value(i)).writeByte(10);
            }
            sink.writeUtf8(Entry.SENT_MILLIS).writeUtf8(": ").writeDecimalLong(this.sentRequestMillis).writeByte(10);
            sink.writeUtf8(Entry.RECEIVED_MILLIS).writeUtf8(": ").writeDecimalLong(this.receivedResponseMillis).writeByte(10);
            if (this.isHttps()) {
                sink.writeByte(10);
                sink.writeUtf8(this.handshake.cipherSuite().javaName()).writeByte(10);
                this.writeCertList(sink, this.handshake.peerCertificates());
                this.writeCertList(sink, this.handshake.localCertificates());
                sink.writeUtf8(this.handshake.tlsVersion().javaName()).writeByte(10);
            }
            sink.close();
        }
        
        private boolean isHttps() {
            return this.url.startsWith("https://");
        }
        
        private List<Certificate> readCertificateList(final BufferedSource source) throws IOException {
            final int length = Cache.readInt(source);
            if (length == -1) {
                return Collections.emptyList();
            }
            try {
                final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                final List<Certificate> result = new ArrayList<Certificate>(length);
                for (int i = 0; i < length; ++i) {
                    final String line = source.readUtf8LineStrict();
                    final Buffer bytes = new Buffer();
                    bytes.write(ByteString.decodeBase64(line));
                    result.add(certificateFactory.generateCertificate(bytes.inputStream()));
                }
                return result;
            }
            catch (CertificateException e) {
                throw new IOException(e.getMessage());
            }
        }
        
        private void writeCertList(final BufferedSink sink, final List<Certificate> certificates) throws IOException {
            try {
                sink.writeDecimalLong((long)certificates.size()).writeByte(10);
                for (int i = 0, size = certificates.size(); i < size; ++i) {
                    final byte[] bytes = certificates.get(i).getEncoded();
                    final String line = ByteString.of(bytes).base64();
                    sink.writeUtf8(line).writeByte(10);
                }
            }
            catch (CertificateEncodingException e) {
                throw new IOException(e.getMessage());
            }
        }
        
        public boolean matches(final Request request, final Response response) {
            return this.url.equals(request.url().toString()) && this.requestMethod.equals(request.method()) && HttpHeaders.varyMatches(response, this.varyHeaders, request);
        }
        
        public Response response(final DiskLruCache.Snapshot snapshot) {
            final String contentType = this.responseHeaders.get("Content-Type");
            final String contentLength = this.responseHeaders.get("Content-Length");
            final Request cacheRequest = new Request.Builder().url(this.url).method(this.requestMethod, null).headers(this.varyHeaders).build();
            return new Response.Builder().request(cacheRequest).protocol(this.protocol).code(this.code).message(this.message).headers(this.responseHeaders).body(new CacheResponseBody(snapshot, contentType, contentLength)).handshake(this.handshake).sentRequestAtMillis(this.sentRequestMillis).receivedResponseAtMillis(this.receivedResponseMillis).build();
        }
        
        static {
            SENT_MILLIS = Platform.get().getPrefix() + "-Sent-Millis";
            RECEIVED_MILLIS = Platform.get().getPrefix() + "-Received-Millis";
        }
    }
    
    private static class CacheResponseBody extends ResponseBody
    {
        final DiskLruCache.Snapshot snapshot;
        private final BufferedSource bodySource;
        @Nullable
        private final String contentType;
        @Nullable
        private final String contentLength;
        
        CacheResponseBody(final DiskLruCache.Snapshot snapshot, final String contentType, final String contentLength) {
            this.snapshot = snapshot;
            this.contentType = contentType;
            this.contentLength = contentLength;
            final Source source = snapshot.getSource(1);
            this.bodySource = Okio.buffer((Source)new ForwardingSource(source) {
                public void close() throws IOException {
                    snapshot.close();
                    super.close();
                }
            });
        }
        
        @Override
        public MediaType contentType() {
            return (this.contentType != null) ? MediaType.parse(this.contentType) : null;
        }
        
        @Override
        public long contentLength() {
            try {
                return (this.contentLength != null) ? Long.parseLong(this.contentLength) : -1L;
            }
            catch (NumberFormatException e) {
                return -1L;
            }
        }
        
        @Override
        public BufferedSource source() {
            return this.bodySource;
        }
    }
}
