//Raddon On Top!

package okhttp3;

import okhttp3.internal.*;
import java.io.*;
import javax.annotation.*;
import okio.*;
import java.util.*;

public final class MultipartBody extends RequestBody
{
    public static final MediaType MIXED;
    public static final MediaType ALTERNATIVE;
    public static final MediaType DIGEST;
    public static final MediaType PARALLEL;
    public static final MediaType FORM;
    private static final byte[] COLONSPACE;
    private static final byte[] CRLF;
    private static final byte[] DASHDASH;
    private final ByteString boundary;
    private final MediaType originalType;
    private final MediaType contentType;
    private final List<Part> parts;
    private long contentLength;
    
    MultipartBody(final ByteString boundary, final MediaType type, final List<Part> parts) {
        this.contentLength = -1L;
        this.boundary = boundary;
        this.originalType = type;
        this.contentType = MediaType.get(type + "; boundary=" + boundary.utf8());
        this.parts = (List<Part>)Util.immutableList((List)parts);
    }
    
    public MediaType type() {
        return this.originalType;
    }
    
    public String boundary() {
        return this.boundary.utf8();
    }
    
    public int size() {
        return this.parts.size();
    }
    
    public List<Part> parts() {
        return this.parts;
    }
    
    public Part part(final int index) {
        return this.parts.get(index);
    }
    
    @Override
    public MediaType contentType() {
        return this.contentType;
    }
    
    @Override
    public long contentLength() throws IOException {
        final long result = this.contentLength;
        if (result != -1L) {
            return result;
        }
        return this.contentLength = this.writeOrCountBytes(null, true);
    }
    
    @Override
    public void writeTo(final BufferedSink sink) throws IOException {
        this.writeOrCountBytes(sink, false);
    }
    
    private long writeOrCountBytes(@Nullable BufferedSink sink, final boolean countBytes) throws IOException {
        long byteCount = 0L;
        Buffer byteCountBuffer = null;
        if (countBytes) {
            byteCountBuffer = (Buffer)(sink = (BufferedSink)new Buffer());
        }
        for (int p = 0, partCount = this.parts.size(); p < partCount; ++p) {
            final Part part = this.parts.get(p);
            final Headers headers = part.headers;
            final RequestBody body = part.body;
            sink.write(MultipartBody.DASHDASH);
            sink.write(this.boundary);
            sink.write(MultipartBody.CRLF);
            if (headers != null) {
                for (int h = 0, headerCount = headers.size(); h < headerCount; ++h) {
                    sink.writeUtf8(headers.name(h)).write(MultipartBody.COLONSPACE).writeUtf8(headers.value(h)).write(MultipartBody.CRLF);
                }
            }
            final MediaType contentType = body.contentType();
            if (contentType != null) {
                sink.writeUtf8("Content-Type: ").writeUtf8(contentType.toString()).write(MultipartBody.CRLF);
            }
            final long contentLength = body.contentLength();
            if (contentLength != -1L) {
                sink.writeUtf8("Content-Length: ").writeDecimalLong(contentLength).write(MultipartBody.CRLF);
            }
            else if (countBytes) {
                byteCountBuffer.clear();
                return -1L;
            }
            sink.write(MultipartBody.CRLF);
            if (countBytes) {
                byteCount += contentLength;
            }
            else {
                body.writeTo(sink);
            }
            sink.write(MultipartBody.CRLF);
        }
        sink.write(MultipartBody.DASHDASH);
        sink.write(this.boundary);
        sink.write(MultipartBody.DASHDASH);
        sink.write(MultipartBody.CRLF);
        if (countBytes) {
            byteCount += byteCountBuffer.size();
            byteCountBuffer.clear();
        }
        return byteCount;
    }
    
    static void appendQuotedString(final StringBuilder target, final String key) {
        target.append('\"');
        for (int i = 0, len = key.length(); i < len; ++i) {
            final char ch = key.charAt(i);
            switch (ch) {
                case '\n': {
                    target.append("%0A");
                    break;
                }
                case '\r': {
                    target.append("%0D");
                    break;
                }
                case '\"': {
                    target.append("%22");
                    break;
                }
                default: {
                    target.append(ch);
                    break;
                }
            }
        }
        target.append('\"');
    }
    
    static {
        MIXED = MediaType.get("multipart/mixed");
        ALTERNATIVE = MediaType.get("multipart/alternative");
        DIGEST = MediaType.get("multipart/digest");
        PARALLEL = MediaType.get("multipart/parallel");
        FORM = MediaType.get("multipart/form-data");
        COLONSPACE = new byte[] { 58, 32 };
        CRLF = new byte[] { 13, 10 };
        DASHDASH = new byte[] { 45, 45 };
    }
    
    public static final class Part
    {
        @Nullable
        final Headers headers;
        final RequestBody body;
        
        public static Part create(final RequestBody body) {
            return create(null, body);
        }
        
        public static Part create(@Nullable final Headers headers, final RequestBody body) {
            if (body == null) {
                throw new NullPointerException("body == null");
            }
            if (headers != null && headers.get("Content-Type") != null) {
                throw new IllegalArgumentException("Unexpected header: Content-Type");
            }
            if (headers != null && headers.get("Content-Length") != null) {
                throw new IllegalArgumentException("Unexpected header: Content-Length");
            }
            return new Part(headers, body);
        }
        
        public static Part createFormData(final String name, final String value) {
            return createFormData(name, null, RequestBody.create(null, value));
        }
        
        public static Part createFormData(final String name, @Nullable final String filename, final RequestBody body) {
            if (name == null) {
                throw new NullPointerException("name == null");
            }
            final StringBuilder disposition = new StringBuilder("form-data; name=");
            MultipartBody.appendQuotedString(disposition, name);
            if (filename != null) {
                disposition.append("; filename=");
                MultipartBody.appendQuotedString(disposition, filename);
            }
            final Headers headers = new Headers.Builder().addUnsafeNonAscii("Content-Disposition", disposition.toString()).build();
            return create(headers, body);
        }
        
        private Part(@Nullable final Headers headers, final RequestBody body) {
            this.headers = headers;
            this.body = body;
        }
        
        @Nullable
        public Headers headers() {
            return this.headers;
        }
        
        public RequestBody body() {
            return this.body;
        }
    }
    
    public static final class Builder
    {
        private final ByteString boundary;
        private MediaType type;
        private final List<Part> parts;
        
        public Builder() {
            this(UUID.randomUUID().toString());
        }
        
        public Builder(final String boundary) {
            this.type = MultipartBody.MIXED;
            this.parts = new ArrayList<Part>();
            this.boundary = ByteString.encodeUtf8(boundary);
        }
        
        public Builder setType(final MediaType type) {
            if (type == null) {
                throw new NullPointerException("type == null");
            }
            if (!type.type().equals("multipart")) {
                throw new IllegalArgumentException("multipart != " + type);
            }
            this.type = type;
            return this;
        }
        
        public Builder addPart(final RequestBody body) {
            return this.addPart(Part.create(body));
        }
        
        public Builder addPart(@Nullable final Headers headers, final RequestBody body) {
            return this.addPart(Part.create(headers, body));
        }
        
        public Builder addFormDataPart(final String name, final String value) {
            return this.addPart(Part.createFormData(name, value));
        }
        
        public Builder addFormDataPart(final String name, @Nullable final String filename, final RequestBody body) {
            return this.addPart(Part.createFormData(name, filename, body));
        }
        
        public Builder addPart(final Part part) {
            if (part == null) {
                throw new NullPointerException("part == null");
            }
            this.parts.add(part);
            return this;
        }
        
        public MultipartBody build() {
            if (this.parts.isEmpty()) {
                throw new IllegalStateException("Multipart body must have at least one part.");
            }
            return new MultipartBody(this.boundary, this.type, this.parts);
        }
    }
}
