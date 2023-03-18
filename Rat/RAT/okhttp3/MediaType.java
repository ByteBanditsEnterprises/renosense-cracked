//Raddon On Top!

package okhttp3;

import javax.annotation.*;
import java.util.*;
import java.util.regex.*;
import java.nio.charset.*;

public final class MediaType
{
    private static final String TOKEN = "([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)";
    private static final String QUOTED = "\"([^\"]*)\"";
    private static final Pattern TYPE_SUBTYPE;
    private static final Pattern PARAMETER;
    private final String mediaType;
    private final String type;
    private final String subtype;
    @Nullable
    private final String charset;
    
    private MediaType(final String mediaType, final String type, final String subtype, @Nullable final String charset) {
        this.mediaType = mediaType;
        this.type = type;
        this.subtype = subtype;
        this.charset = charset;
    }
    
    public static MediaType get(final String string) {
        final Matcher typeSubtype = MediaType.TYPE_SUBTYPE.matcher(string);
        if (!typeSubtype.lookingAt()) {
            throw new IllegalArgumentException("No subtype found for: \"" + string + '\"');
        }
        final String type = typeSubtype.group(1).toLowerCase(Locale.US);
        final String subtype = typeSubtype.group(2).toLowerCase(Locale.US);
        String charset = null;
        final Matcher parameter = MediaType.PARAMETER.matcher(string);
        for (int s = typeSubtype.end(); s < string.length(); s = parameter.end()) {
            parameter.region(s, string.length());
            if (!parameter.lookingAt()) {
                throw new IllegalArgumentException("Parameter is not formatted correctly: \"" + string.substring(s) + "\" for: \"" + string + '\"');
            }
            final String name = parameter.group(1);
            if (name != null) {
                if (name.equalsIgnoreCase("charset")) {
                    final String token = parameter.group(2);
                    String charsetParameter;
                    if (token != null) {
                        charsetParameter = ((token.startsWith("'") && token.endsWith("'") && token.length() > 2) ? token.substring(1, token.length() - 1) : token);
                    }
                    else {
                        charsetParameter = parameter.group(3);
                    }
                    if (charset != null && !charsetParameter.equalsIgnoreCase(charset)) {
                        throw new IllegalArgumentException("Multiple charsets defined: \"" + charset + "\" and: \"" + charsetParameter + "\" for: \"" + string + '\"');
                    }
                    charset = charsetParameter;
                }
            }
        }
        return new MediaType(string, type, subtype, charset);
    }
    
    @Nullable
    public static MediaType parse(final String string) {
        try {
            return get(string);
        }
        catch (IllegalArgumentException ignored) {
            return null;
        }
    }
    
    public String type() {
        return this.type;
    }
    
    public String subtype() {
        return this.subtype;
    }
    
    @Nullable
    public Charset charset() {
        return this.charset(null);
    }
    
    @Nullable
    public Charset charset(@Nullable final Charset defaultValue) {
        try {
            return (this.charset != null) ? Charset.forName(this.charset) : defaultValue;
        }
        catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
    
    @Override
    public String toString() {
        return this.mediaType;
    }
    
    @Override
    public boolean equals(@Nullable final Object other) {
        return other instanceof MediaType && ((MediaType)other).mediaType.equals(this.mediaType);
    }
    
    @Override
    public int hashCode() {
        return this.mediaType.hashCode();
    }
    
    static {
        TYPE_SUBTYPE = Pattern.compile("([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)/([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)");
        PARAMETER = Pattern.compile(";\\s*(?:([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)=(?:([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)|\"([^\"]*)\"))?");
    }
}
