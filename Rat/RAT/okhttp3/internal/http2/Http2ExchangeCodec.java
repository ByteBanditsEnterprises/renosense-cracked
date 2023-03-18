//Raddon On Top!

package okhttp3.internal.http2;

import okhttp3.internal.connection.*;
import java.io.*;
import java.util.concurrent.*;
import okhttp3.*;
import java.util.*;
import java.net.*;
import okhttp3.internal.http.*;
import okio.*;
import okhttp3.internal.*;

public final class Http2ExchangeCodec implements ExchangeCodec
{
    private static final String CONNECTION = "connection";
    private static final String HOST = "host";
    private static final String KEEP_ALIVE = "keep-alive";
    private static final String PROXY_CONNECTION = "proxy-connection";
    private static final String TRANSFER_ENCODING = "transfer-encoding";
    private static final String TE = "te";
    private static final String ENCODING = "encoding";
    private static final String UPGRADE = "upgrade";
    private static final List<String> HTTP_2_SKIPPED_REQUEST_HEADERS;
    private static final List<String> HTTP_2_SKIPPED_RESPONSE_HEADERS;
    private final Interceptor.Chain chain;
    private final RealConnection realConnection;
    private final Http2Connection connection;
    private volatile Http2Stream stream;
    private final Protocol protocol;
    private volatile boolean canceled;
    
    public Http2ExchangeCodec(final OkHttpClient client, final RealConnection realConnection, final Interceptor.Chain chain, final Http2Connection connection) {
        this.realConnection = realConnection;
        this.chain = chain;
        this.connection = connection;
        this.protocol = (client.protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE) ? Protocol.H2_PRIOR_KNOWLEDGE : Protocol.HTTP_2);
    }
    
    public RealConnection connection() {
        return this.realConnection;
    }
    
    public Sink createRequestBody(final Request request, final long contentLength) {
        return this.stream.getSink();
    }
    
    public void writeRequestHeaders(final Request request) throws IOException {
        if (this.stream != null) {
            return;
        }
        final boolean hasRequestBody = request.body() != null;
        final List<Header> requestHeaders = http2HeadersList(request);
        this.stream = this.connection.newStream((List)requestHeaders, hasRequestBody);
        if (this.canceled) {
            this.stream.closeLater(ErrorCode.CANCEL);
            throw new IOException("Canceled");
        }
        this.stream.readTimeout().timeout((long)this.chain.readTimeoutMillis(), TimeUnit.MILLISECONDS);
        this.stream.writeTimeout().timeout((long)this.chain.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
    }
    
    public void flushRequest() throws IOException {
        this.connection.flush();
    }
    
    public void finishRequest() throws IOException {
        this.stream.getSink().close();
    }
    
    public Response.Builder readResponseHeaders(final boolean expectContinue) throws IOException {
        final Headers headers = this.stream.takeHeaders();
        final Response.Builder responseBuilder = readHttp2HeadersList(headers, this.protocol);
        if (expectContinue && Internal.instance.code(responseBuilder) == 100) {
            return null;
        }
        return responseBuilder;
    }
    
    public static List<Header> http2HeadersList(final Request request) {
        final Headers headers = request.headers();
        final List<Header> result = new ArrayList<Header>(headers.size() + 4);
        result.add(new Header(Header.TARGET_METHOD, request.method()));
        result.add(new Header(Header.TARGET_PATH, RequestLine.requestPath(request.url())));
        final String host = request.header("Host");
        if (host != null) {
            result.add(new Header(Header.TARGET_AUTHORITY, host));
        }
        result.add(new Header(Header.TARGET_SCHEME, request.url().scheme()));
        for (int i = 0, size = headers.size(); i < size; ++i) {
            final String name = headers.name(i).toLowerCase(Locale.US);
            if (!Http2ExchangeCodec.HTTP_2_SKIPPED_REQUEST_HEADERS.contains(name) || (name.equals("te") && headers.value(i).equals("trailers"))) {
                result.add(new Header(name, headers.value(i)));
            }
        }
        return result;
    }
    
    public static Response.Builder readHttp2HeadersList(final Headers headerBlock, final Protocol protocol) throws IOException {
        StatusLine statusLine = null;
        final Headers.Builder headersBuilder = new Headers.Builder();
        for (int i = 0, size = headerBlock.size(); i < size; ++i) {
            final String name = headerBlock.name(i);
            final String value = headerBlock.value(i);
            if (name.equals(":status")) {
                statusLine = StatusLine.parse("HTTP/1.1 " + value);
            }
            else if (!Http2ExchangeCodec.HTTP_2_SKIPPED_RESPONSE_HEADERS.contains(name)) {
                Internal.instance.addLenient(headersBuilder, name, value);
            }
        }
        if (statusLine == null) {
            throw new ProtocolException("Expected ':status' header not present");
        }
        return new Response.Builder().protocol(protocol).code(statusLine.code).message(statusLine.message).headers(headersBuilder.build());
    }
    
    public long reportedContentLength(final Response response) {
        return HttpHeaders.contentLength(response);
    }
    
    public Source openResponseBodySource(final Response response) {
        return this.stream.getSource();
    }
    
    public Headers trailers() throws IOException {
        return this.stream.trailers();
    }
    
    public void cancel() {
        this.canceled = true;
        if (this.stream != null) {
            this.stream.closeLater(ErrorCode.CANCEL);
        }
    }
    
    static {
        HTTP_2_SKIPPED_REQUEST_HEADERS = Util.immutableList("connection", "host", "keep-alive", "proxy-connection", "te", "transfer-encoding", "encoding", "upgrade", ":method", ":path", ":scheme", ":authority");
        HTTP_2_SKIPPED_RESPONSE_HEADERS = Util.immutableList("connection", "host", "keep-alive", "proxy-connection", "te", "transfer-encoding", "encoding", "upgrade");
    }
}
