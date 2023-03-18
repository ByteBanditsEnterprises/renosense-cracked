//Raddon On Top!

package okhttp3.internal.http2;

public enum ErrorCode
{
    NO_ERROR(0), 
    PROTOCOL_ERROR(1), 
    INTERNAL_ERROR(2), 
    FLOW_CONTROL_ERROR(3), 
    REFUSED_STREAM(7), 
    CANCEL(8), 
    COMPRESSION_ERROR(9), 
    CONNECT_ERROR(10), 
    ENHANCE_YOUR_CALM(11), 
    INADEQUATE_SECURITY(12), 
    HTTP_1_1_REQUIRED(13);
    
    public final int httpCode;
    
    private ErrorCode(final int httpCode) {
        this.httpCode = httpCode;
    }
    
    public static ErrorCode fromHttp2(final int code) {
        for (final ErrorCode errorCode : values()) {
            if (errorCode.httpCode == code) {
                return errorCode;
            }
        }
        return null;
    }
}
