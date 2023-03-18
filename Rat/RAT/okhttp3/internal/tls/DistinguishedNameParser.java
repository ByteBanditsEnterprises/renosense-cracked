//Raddon On Top!

package okhttp3.internal.tls;

import javax.security.auth.x500.*;

final class DistinguishedNameParser
{
    private final String dn;
    private final int length;
    private int pos;
    private int beg;
    private int end;
    private int cur;
    private char[] chars;
    
    DistinguishedNameParser(final X500Principal principal) {
        this.dn = principal.getName("RFC2253");
        this.length = this.dn.length();
    }
    
    private String nextAT() {
        while (this.pos < this.length && this.chars[this.pos] == ' ') {
            ++this.pos;
        }
        if (this.pos == this.length) {
            return null;
        }
        this.beg = this.pos;
        ++this.pos;
        while (this.pos < this.length && this.chars[this.pos] != '=' && this.chars[this.pos] != ' ') {
            ++this.pos;
        }
        if (this.pos >= this.length) {
            throw new IllegalStateException("Unexpected end of DN: " + this.dn);
        }
        this.end = this.pos;
        if (this.chars[this.pos] == ' ') {
            while (this.pos < this.length && this.chars[this.pos] != '=' && this.chars[this.pos] == ' ') {
                ++this.pos;
            }
            if (this.chars[this.pos] != '=' || this.pos == this.length) {
                throw new IllegalStateException("Unexpected end of DN: " + this.dn);
            }
        }
        ++this.pos;
        while (this.pos < this.length && this.chars[this.pos] == ' ') {
            ++this.pos;
        }
        if (this.end - this.beg > 4 && this.chars[this.beg + 3] == '.' && (this.chars[this.beg] == 'O' || this.chars[this.beg] == 'o') && (this.chars[this.beg + 1] == 'I' || this.chars[this.beg + 1] == 'i') && (this.chars[this.beg + 2] == 'D' || this.chars[this.beg + 2] == 'd')) {
            this.beg += 4;
        }
        return new String(this.chars, this.beg, this.end - this.beg);
    }
    
    private String quotedAV() {
        ++this.pos;
        this.beg = this.pos;
        this.end = this.beg;
        while (this.pos != this.length) {
            if (this.chars[this.pos] == '\"') {
                ++this.pos;
                while (this.pos < this.length && this.chars[this.pos] == ' ') {
                    ++this.pos;
                }
                return new String(this.chars, this.beg, this.end - this.beg);
            }
            if (this.chars[this.pos] == '\\') {
                this.chars[this.end] = this.getEscaped();
            }
            else {
                this.chars[this.end] = this.chars[this.pos];
            }
            ++this.pos;
            ++this.end;
        }
        throw new IllegalStateException("Unexpected end of DN: " + this.dn);
    }
    
    private String hexAV() {
        if (this.pos + 4 >= this.length) {
            throw new IllegalStateException("Unexpected end of DN: " + this.dn);
        }
        this.beg = this.pos;
        ++this.pos;
        while (true) {
            while (this.pos != this.length && this.chars[this.pos] != '+' && this.chars[this.pos] != ',' && this.chars[this.pos] != ';') {
                if (this.chars[this.pos] == ' ') {
                    this.end = this.pos;
                    ++this.pos;
                    while (this.pos < this.length && this.chars[this.pos] == ' ') {
                        ++this.pos;
                    }
                    final int hexLen = this.end - this.beg;
                    if (hexLen < 5 || (hexLen & 0x1) == 0x0) {
                        throw new IllegalStateException("Unexpected end of DN: " + this.dn);
                    }
                    final byte[] encoded = new byte[hexLen / 2];
                    int i = 0;
                    int p = this.beg + 1;
                    while (i < encoded.length) {
                        encoded[i] = (byte)this.getByte(p);
                        p += 2;
                        ++i;
                    }
                    return new String(this.chars, this.beg, hexLen);
                }
                else {
                    if (this.chars[this.pos] >= 'A' && this.chars[this.pos] <= 'F') {
                        final char[] chars = this.chars;
                        final int pos = this.pos;
                        chars[pos] += ' ';
                    }
                    ++this.pos;
                }
            }
            this.end = this.pos;
            continue;
        }
    }
    
    private String escapedAV() {
        this.beg = this.pos;
        this.end = this.pos;
        while (this.pos < this.length) {
            switch (this.chars[this.pos]) {
                case '+':
                case ',':
                case ';': {
                    return new String(this.chars, this.beg, this.end - this.beg);
                }
                case '\\': {
                    this.chars[this.end++] = this.getEscaped();
                    ++this.pos;
                    continue;
                }
                case ' ': {
                    this.cur = this.end;
                    ++this.pos;
                    this.chars[this.end++] = ' ';
                    while (this.pos < this.length && this.chars[this.pos] == ' ') {
                        this.chars[this.end++] = ' ';
                        ++this.pos;
                    }
                    if (this.pos == this.length || this.chars[this.pos] == ',' || this.chars[this.pos] == '+' || this.chars[this.pos] == ';') {
                        return new String(this.chars, this.beg, this.cur - this.beg);
                    }
                    continue;
                }
                default: {
                    this.chars[this.end++] = this.chars[this.pos];
                    ++this.pos;
                    continue;
                }
            }
        }
        return new String(this.chars, this.beg, this.end - this.beg);
    }
    
    private char getEscaped() {
        ++this.pos;
        if (this.pos == this.length) {
            throw new IllegalStateException("Unexpected end of DN: " + this.dn);
        }
        switch (this.chars[this.pos]) {
            case ' ':
            case '\"':
            case '#':
            case '%':
            case '*':
            case '+':
            case ',':
            case ';':
            case '<':
            case '=':
            case '>':
            case '\\':
            case '_': {
                return this.chars[this.pos];
            }
            default: {
                return this.getUTF8();
            }
        }
    }
    
    private char getUTF8() {
        int res = this.getByte(this.pos);
        ++this.pos;
        if (res < 128) {
            return (char)res;
        }
        if (res >= 192 && res <= 247) {
            int count;
            if (res <= 223) {
                count = 1;
                res &= 0x1F;
            }
            else if (res <= 239) {
                count = 2;
                res &= 0xF;
            }
            else {
                count = 3;
                res &= 0x7;
            }
            for (int i = 0; i < count; ++i) {
                ++this.pos;
                if (this.pos == this.length || this.chars[this.pos] != '\\') {
                    return '?';
                }
                ++this.pos;
                final int b = this.getByte(this.pos);
                ++this.pos;
                if ((b & 0xC0) != 0x80) {
                    return '?';
                }
                res = (res << 6) + (b & 0x3F);
            }
            return (char)res;
        }
        return '?';
    }
    
    private int getByte(final int position) {
        if (position + 1 >= this.length) {
            throw new IllegalStateException("Malformed DN: " + this.dn);
        }
        int b1 = this.chars[position];
        if (b1 >= 48 && b1 <= 57) {
            b1 -= 48;
        }
        else if (b1 >= 97 && b1 <= 102) {
            b1 -= 87;
        }
        else {
            if (b1 < 65 || b1 > 70) {
                throw new IllegalStateException("Malformed DN: " + this.dn);
            }
            b1 -= 55;
        }
        int b2 = this.chars[position + 1];
        if (b2 >= 48 && b2 <= 57) {
            b2 -= 48;
        }
        else if (b2 >= 97 && b2 <= 102) {
            b2 -= 87;
        }
        else {
            if (b2 < 65 || b2 > 70) {
                throw new IllegalStateException("Malformed DN: " + this.dn);
            }
            b2 -= 55;
        }
        return (b1 << 4) + b2;
    }
    
    public String findMostSpecific(final String attributeType) {
        this.pos = 0;
        this.beg = 0;
        this.end = 0;
        this.cur = 0;
        this.chars = this.dn.toCharArray();
        String attType = this.nextAT();
        if (attType == null) {
            return null;
        }
        while (true) {
            String attValue = "";
            if (this.pos == this.length) {
                return null;
            }
            switch (this.chars[this.pos]) {
                case '\"': {
                    attValue = this.quotedAV();
                    break;
                }
                case '#': {
                    attValue = this.hexAV();
                    break;
                }
                case '+':
                case ',':
                case ';': {
                    break;
                }
                default: {
                    attValue = this.escapedAV();
                    break;
                }
            }
            if (attributeType.equalsIgnoreCase(attType)) {
                return attValue;
            }
            if (this.pos >= this.length) {
                return null;
            }
            if (this.chars[this.pos] != ',') {
                if (this.chars[this.pos] != ';') {
                    if (this.chars[this.pos] != '+') {
                        throw new IllegalStateException("Malformed DN: " + this.dn);
                    }
                }
            }
            ++this.pos;
            attType = this.nextAT();
            if (attType == null) {
                throw new IllegalStateException("Malformed DN: " + this.dn);
            }
        }
    }
}
