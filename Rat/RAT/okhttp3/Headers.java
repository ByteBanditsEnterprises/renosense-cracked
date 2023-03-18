//Raddon On Top!

package okhttp3;

import javax.annotation.*;
import okhttp3.internal.http.*;
import java.time.*;
import org.codehaus.mojo.animal_sniffer.*;
import java.util.*;
import okhttp3.internal.*;

public final class Headers
{
    private final String[] namesAndValues;
    
    Headers(final Builder builder) {
        this.namesAndValues = builder.namesAndValues.toArray(new String[builder.namesAndValues.size()]);
    }
    
    private Headers(final String[] namesAndValues) {
        this.namesAndValues = namesAndValues;
    }
    
    @Nullable
    public String get(final String name) {
        return get(this.namesAndValues, name);
    }
    
    @Nullable
    public Date getDate(final String name) {
        final String value = this.get(name);
        return (value != null) ? HttpDate.parse(value) : null;
    }
    
    @Nullable
    @IgnoreJRERequirement
    public Instant getInstant(final String name) {
        final Date value = this.getDate(name);
        return (value != null) ? value.toInstant() : null;
    }
    
    public int size() {
        return this.namesAndValues.length / 2;
    }
    
    public String name(final int index) {
        return this.namesAndValues[index * 2];
    }
    
    public String value(final int index) {
        return this.namesAndValues[index * 2 + 1];
    }
    
    public Set<String> names() {
        final TreeSet<String> result = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0, size = this.size(); i < size; ++i) {
            result.add(this.name(i));
        }
        return Collections.unmodifiableSet((Set<? extends String>)result);
    }
    
    public List<String> values(final String name) {
        List<String> result = null;
        for (int i = 0, size = this.size(); i < size; ++i) {
            if (name.equalsIgnoreCase(this.name(i))) {
                if (result == null) {
                    result = new ArrayList<String>(2);
                }
                result.add(this.value(i));
            }
        }
        return (result != null) ? Collections.unmodifiableList((List<? extends String>)result) : Collections.emptyList();
    }
    
    public long byteCount() {
        long result = this.namesAndValues.length * 2;
        for (int i = 0, size = this.namesAndValues.length; i < size; ++i) {
            result += this.namesAndValues[i].length();
        }
        return result;
    }
    
    public Builder newBuilder() {
        final Builder result = new Builder();
        Collections.addAll(result.namesAndValues, this.namesAndValues);
        return result;
    }
    
    @Override
    public boolean equals(@Nullable final Object other) {
        return other instanceof Headers && Arrays.equals(((Headers)other).namesAndValues, this.namesAndValues);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.namesAndValues);
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        for (int i = 0, size = this.size(); i < size; ++i) {
            result.append(this.name(i)).append(": ").append(this.value(i)).append("\n");
        }
        return result.toString();
    }
    
    public Map<String, List<String>> toMultimap() {
        final Map<String, List<String>> result = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0, size = this.size(); i < size; ++i) {
            final String name = this.name(i).toLowerCase(Locale.US);
            List<String> values = result.get(name);
            if (values == null) {
                values = new ArrayList<String>(2);
                result.put(name, values);
            }
            values.add(this.value(i));
        }
        return result;
    }
    
    @Nullable
    private static String get(final String[] namesAndValues, final String name) {
        for (int i = namesAndValues.length - 2; i >= 0; i -= 2) {
            if (name.equalsIgnoreCase(namesAndValues[i])) {
                return namesAndValues[i + 1];
            }
        }
        return null;
    }
    
    public static Headers of(String... namesAndValues) {
        if (namesAndValues == null) {
            throw new NullPointerException("namesAndValues == null");
        }
        if (namesAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("Expected alternating header names and values");
        }
        namesAndValues = namesAndValues.clone();
        for (int i = 0; i < namesAndValues.length; ++i) {
            if (namesAndValues[i] == null) {
                throw new IllegalArgumentException("Headers cannot be null");
            }
            namesAndValues[i] = namesAndValues[i].trim();
        }
        for (int i = 0; i < namesAndValues.length; i += 2) {
            final String name = namesAndValues[i];
            final String value = namesAndValues[i + 1];
            checkName(name);
            checkValue(value, name);
        }
        return new Headers(namesAndValues);
    }
    
    public static Headers of(final Map<String, String> headers) {
        if (headers == null) {
            throw new NullPointerException("headers == null");
        }
        final String[] namesAndValues = new String[headers.size() * 2];
        int i = 0;
        for (final Map.Entry<String, String> header : headers.entrySet()) {
            if (header.getKey() == null || header.getValue() == null) {
                throw new IllegalArgumentException("Headers cannot be null");
            }
            final String name = header.getKey().trim();
            final String value = header.getValue().trim();
            checkName(name);
            checkValue(value, name);
            namesAndValues[i] = name;
            namesAndValues[i + 1] = value;
            i += 2;
        }
        return new Headers(namesAndValues);
    }
    
    static void checkName(final String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name is empty");
        }
        for (int i = 0, length = name.length(); i < length; ++i) {
            final char c = name.charAt(i);
            if (c <= ' ' || c >= '\u007f') {
                throw new IllegalArgumentException(Util.format("Unexpected char %#04x at %d in header name: %s", (int)c, i, name));
            }
        }
    }
    
    static void checkValue(final String value, final String name) {
        if (value == null) {
            throw new NullPointerException("value for name " + name + " == null");
        }
        for (int i = 0, length = value.length(); i < length; ++i) {
            final char c = value.charAt(i);
            if ((c <= '\u001f' && c != '\t') || c >= '\u007f') {
                throw new IllegalArgumentException(Util.format("Unexpected char %#04x at %d in %s value: %s", (int)c, i, name, value));
            }
        }
    }
    
    public static final class Builder
    {
        final List<String> namesAndValues;
        
        public Builder() {
            this.namesAndValues = new ArrayList<String>(20);
        }
        
        Builder addLenient(final String line) {
            final int index = line.indexOf(":", 1);
            if (index != -1) {
                return this.addLenient(line.substring(0, index), line.substring(index + 1));
            }
            if (line.startsWith(":")) {
                return this.addLenient("", line.substring(1));
            }
            return this.addLenient("", line);
        }
        
        public Builder add(final String line) {
            final int index = line.indexOf(":");
            if (index == -1) {
                throw new IllegalArgumentException("Unexpected header: " + line);
            }
            return this.add(line.substring(0, index).trim(), line.substring(index + 1));
        }
        
        public Builder add(final String name, final String value) {
            Headers.checkName(name);
            Headers.checkValue(value, name);
            return this.addLenient(name, value);
        }
        
        public Builder addUnsafeNonAscii(final String name, final String value) {
            Headers.checkName(name);
            return this.addLenient(name, value);
        }
        
        public Builder addAll(final Headers headers) {
            for (int i = 0, size = headers.size(); i < size; ++i) {
                this.addLenient(headers.name(i), headers.value(i));
            }
            return this;
        }
        
        public Builder add(final String name, final Date value) {
            if (value == null) {
                throw new NullPointerException("value for name " + name + " == null");
            }
            this.add(name, HttpDate.format(value));
            return this;
        }
        
        @IgnoreJRERequirement
        public Builder add(final String name, final Instant value) {
            if (value == null) {
                throw new NullPointerException("value for name " + name + " == null");
            }
            return this.add(name, new Date(value.toEpochMilli()));
        }
        
        public Builder set(final String name, final Date value) {
            if (value == null) {
                throw new NullPointerException("value for name " + name + " == null");
            }
            this.set(name, HttpDate.format(value));
            return this;
        }
        
        @IgnoreJRERequirement
        public Builder set(final String name, final Instant value) {
            if (value == null) {
                throw new NullPointerException("value for name " + name + " == null");
            }
            return this.set(name, new Date(value.toEpochMilli()));
        }
        
        Builder addLenient(final String name, final String value) {
            this.namesAndValues.add(name);
            this.namesAndValues.add(value.trim());
            return this;
        }
        
        public Builder removeAll(final String name) {
            for (int i = 0; i < this.namesAndValues.size(); i += 2) {
                if (name.equalsIgnoreCase(this.namesAndValues.get(i))) {
                    this.namesAndValues.remove(i);
                    this.namesAndValues.remove(i);
                    i -= 2;
                }
            }
            return this;
        }
        
        public Builder set(final String name, final String value) {
            Headers.checkName(name);
            Headers.checkValue(value, name);
            this.removeAll(name);
            this.addLenient(name, value);
            return this;
        }
        
        @Nullable
        public String get(final String name) {
            for (int i = this.namesAndValues.size() - 2; i >= 0; i -= 2) {
                if (name.equalsIgnoreCase(this.namesAndValues.get(i))) {
                    return this.namesAndValues.get(i + 1);
                }
            }
            return null;
        }
        
        public Headers build() {
            return new Headers(this);
        }
    }
}
