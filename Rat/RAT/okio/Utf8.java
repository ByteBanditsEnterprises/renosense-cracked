//Raddon On Top!

package okio;

public final class Utf8
{
    private Utf8() {
    }
    
    public static long size(final String string) {
        return size(string, 0, string.length());
    }
    
    public static long size(final String string, final int beginIndex, final int endIndex) {
        if (string == null) {
            throw new IllegalArgumentException("string == null");
        }
        if (beginIndex < 0) {
            throw new IllegalArgumentException("beginIndex < 0: " + beginIndex);
        }
        if (endIndex < beginIndex) {
            throw new IllegalArgumentException("endIndex < beginIndex: " + endIndex + " < " + beginIndex);
        }
        if (endIndex > string.length()) {
            throw new IllegalArgumentException("endIndex > string.length: " + endIndex + " > " + string.length());
        }
        long result = 0L;
        int i = beginIndex;
        while (i < endIndex) {
            final int c = string.charAt(i);
            if (c < 128) {
                ++result;
                ++i;
            }
            else if (c < 2048) {
                result += 2L;
                ++i;
            }
            else if (c < 55296 || c > 57343) {
                result += 3L;
                ++i;
            }
            else {
                final int low = (i + 1 < endIndex) ? string.charAt(i + 1) : '\0';
                if (c > 56319 || low < 56320 || low > 57343) {
                    ++result;
                    ++i;
                }
                else {
                    result += 4L;
                    i += 2;
                }
            }
        }
        return result;
    }
}
