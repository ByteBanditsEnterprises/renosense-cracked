//Raddon On Top!

package okhttp3;

import javax.annotation.*;
import okhttp3.internal.*;
import java.nio.charset.*;
import okio.*;
import java.io.*;

public abstract class ResponseBody implements Closeable
{
    @Nullable
    private Reader reader;
    
    @Nullable
    public abstract MediaType contentType();
    
    public abstract long contentLength();
    
    public final InputStream byteStream() {
        return this.source().inputStream();
    }
    
    public abstract BufferedSource source();
    
    public final byte[] bytes() throws IOException {
        final long contentLength = this.contentLength();
        if (contentLength > 2147483647L) {
            throw new IOException("Cannot buffer entire body for content length: " + contentLength);
        }
        final BufferedSource source = this.source();
        Throwable x0 = null;
        byte[] bytes;
        try {
            bytes = source.readByteArray();
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
        if (contentLength != -1L && contentLength != bytes.length) {
            throw new IOException("Content-Length (" + contentLength + ") and stream length (" + bytes.length + ") disagree");
        }
        return bytes;
    }
    
    public final Reader charStream() {
        final Reader r = this.reader;
        return (r != null) ? r : (this.reader = new BomAwareReader(this.source(), this.charset()));
    }
    
    public final String string() throws IOException {
        final BufferedSource source = this.source();
        Throwable x0 = null;
        try {
            final Charset charset = Util.bomAwareCharset(source, this.charset());
            return source.readString(charset);
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
    
    private Charset charset() {
        final MediaType contentType = this.contentType();
        return (contentType != null) ? contentType.charset(StandardCharsets.UTF_8) : StandardCharsets.UTF_8;
    }
    
    @Override
    public void close() {
        Util.closeQuietly((Closeable)this.source());
    }
    
    public static ResponseBody create(@Nullable MediaType contentType, final String content) {
        Charset charset = StandardCharsets.UTF_8;
        if (contentType != null) {
            charset = contentType.charset();
            if (charset == null) {
                charset = StandardCharsets.UTF_8;
                contentType = MediaType.parse(contentType + "; charset=utf-8");
            }
        }
        final Buffer buffer = new Buffer().writeString(content, charset);
        return create(contentType, buffer.size(), (BufferedSource)buffer);
    }
    
    public static ResponseBody create(@Nullable final MediaType contentType, final byte[] content) {
        final Buffer buffer = new Buffer().write(content);
        return create(contentType, content.length, (BufferedSource)buffer);
    }
    
    public static ResponseBody create(@Nullable final MediaType contentType, final ByteString content) {
        final Buffer buffer = new Buffer().write(content);
        return create(contentType, content.size(), (BufferedSource)buffer);
    }
    
    public static ResponseBody create(@Nullable final MediaType contentType, final long contentLength, final BufferedSource content) {
        if (content == null) {
            throw new NullPointerException("source == null");
        }
        return new ResponseBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return contentType;
            }
            
            @Override
            public long contentLength() {
                return contentLength;
            }
            
            @Override
            public BufferedSource source() {
                return content;
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
    
    static final class BomAwareReader extends Reader
    {
        private final BufferedSource source;
        private final Charset charset;
        private boolean closed;
        @Nullable
        private Reader delegate;
        
        BomAwareReader(final BufferedSource source, final Charset charset) {
            this.source = source;
            this.charset = charset;
        }
        
        @Override
        public int read(final char[] cbuf, final int off, final int len) throws IOException {
            if (this.closed) {
                throw new IOException("Stream closed");
            }
            Reader delegate = this.delegate;
            if (delegate == null) {
                final Charset charset = Util.bomAwareCharset(this.source, this.charset);
                final InputStreamReader delegate2 = new InputStreamReader(this.source.inputStream(), charset);
                this.delegate = delegate2;
                delegate = delegate2;
            }
            return delegate.read(cbuf, off, len);
        }
        
        @Override
        public void close() throws IOException {
            this.closed = true;
            if (this.delegate != null) {
                this.delegate.close();
            }
            else {
                this.source.close();
            }
        }
    }
}
