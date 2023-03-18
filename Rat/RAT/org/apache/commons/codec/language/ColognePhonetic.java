//Raddon On Top!

package org.apache.commons.codec.language;

import org.apache.commons.codec.*;
import java.util.*;

public class ColognePhonetic implements StringEncoder
{
    private static final char[] AEIJOUY;
    private static final char[] CSZ;
    private static final char[] FPVW;
    private static final char[] GKQ;
    private static final char[] CKQ;
    private static final char[] AHKLOQRUX;
    private static final char[] SZ;
    private static final char[] AHKOQUX;
    private static final char[] DTX;
    private static final char CHAR_IGNORE = '-';
    
    private static boolean arrayContains(final char[] arr, final char key) {
        for (final char element : arr) {
            if (element == key) {
                return true;
            }
        }
        return false;
    }
    
    public String colognePhonetic(final String text) {
        if (text == null) {
            return null;
        }
        final CologneInputBuffer input = new CologneInputBuffer(this.preprocess(text));
        final CologneOutputBuffer output = new CologneOutputBuffer(input.length() * 2);
        char lastChar = '-';
        while (input.length() > 0) {
            final char chr = input.removeNext();
            char nextChar;
            if (input.length() > 0) {
                nextChar = input.getNextChar();
            }
            else {
                nextChar = '-';
            }
            if (chr >= 'A') {
                if (chr > 'Z') {
                    continue;
                }
                if (arrayContains(ColognePhonetic.AEIJOUY, chr)) {
                    output.put('0');
                }
                else if (chr == 'B' || (chr == 'P' && nextChar != 'H')) {
                    output.put('1');
                }
                else if ((chr == 'D' || chr == 'T') && !arrayContains(ColognePhonetic.CSZ, nextChar)) {
                    output.put('2');
                }
                else if (arrayContains(ColognePhonetic.FPVW, chr)) {
                    output.put('3');
                }
                else if (arrayContains(ColognePhonetic.GKQ, chr)) {
                    output.put('4');
                }
                else if (chr == 'X' && !arrayContains(ColognePhonetic.CKQ, lastChar)) {
                    output.put('4');
                    output.put('8');
                }
                else if (chr == 'S' || chr == 'Z') {
                    output.put('8');
                }
                else if (chr == 'C') {
                    if (output.length() == 0) {
                        if (arrayContains(ColognePhonetic.AHKLOQRUX, nextChar)) {
                            output.put('4');
                        }
                        else {
                            output.put('8');
                        }
                    }
                    else if (arrayContains(ColognePhonetic.SZ, lastChar) || !arrayContains(ColognePhonetic.AHKOQUX, nextChar)) {
                        output.put('8');
                    }
                    else {
                        output.put('4');
                    }
                }
                else if (arrayContains(ColognePhonetic.DTX, chr)) {
                    output.put('8');
                }
                else if (chr == 'R') {
                    output.put('7');
                }
                else if (chr == 'L') {
                    output.put('5');
                }
                else if (chr == 'M' || chr == 'N') {
                    output.put('6');
                }
                else if (chr == 'H') {
                    output.put('-');
                }
                lastChar = chr;
            }
        }
        return output.toString();
    }
    
    public Object encode(final Object object) throws EncoderException {
        if (!(object instanceof String)) {
            throw new EncoderException("This method's parameter was expected to be of the type " + String.class.getName() + ". But actually it was of the type " + object.getClass().getName() + ".");
        }
        return this.encode((String)object);
    }
    
    @Override
    public String encode(final String text) {
        return this.colognePhonetic(text);
    }
    
    public boolean isEncodeEqual(final String text1, final String text2) {
        return this.colognePhonetic(text1).equals(this.colognePhonetic(text2));
    }
    
    private char[] preprocess(final String text) {
        final char[] chrs = text.toUpperCase(Locale.GERMAN).toCharArray();
        for (int index = 0; index < chrs.length; ++index) {
            switch (chrs[index]) {
                case '\u00c4': {
                    chrs[index] = 'A';
                    break;
                }
                case '\u00dc': {
                    chrs[index] = 'U';
                    break;
                }
                case '\u00d6': {
                    chrs[index] = 'O';
                    break;
                }
            }
        }
        return chrs;
    }
    
    static {
        AEIJOUY = new char[] { 'A', 'E', 'I', 'J', 'O', 'U', 'Y' };
        CSZ = new char[] { 'C', 'S', 'Z' };
        FPVW = new char[] { 'F', 'P', 'V', 'W' };
        GKQ = new char[] { 'G', 'K', 'Q' };
        CKQ = new char[] { 'C', 'K', 'Q' };
        AHKLOQRUX = new char[] { 'A', 'H', 'K', 'L', 'O', 'Q', 'R', 'U', 'X' };
        SZ = new char[] { 'S', 'Z' };
        AHKOQUX = new char[] { 'A', 'H', 'K', 'O', 'Q', 'U', 'X' };
        DTX = new char[] { 'D', 'T', 'X' };
    }
    
    private abstract class CologneBuffer
    {
        protected final char[] data;
        protected int length;
        
        public CologneBuffer(final char[] data) {
            this.length = 0;
            this.data = data;
            this.length = data.length;
        }
        
        public CologneBuffer(final int buffSize) {
            this.length = 0;
            this.data = new char[buffSize];
            this.length = 0;
        }
        
        protected abstract char[] copyData(final int p0, final int p1);
        
        public int length() {
            return this.length;
        }
        
        @Override
        public String toString() {
            return new String(this.copyData(0, this.length));
        }
    }
    
    private class CologneOutputBuffer extends CologneBuffer
    {
        private char lastCode;
        
        public CologneOutputBuffer(final int buffSize) {
            super(buffSize);
            this.lastCode = '/';
        }
        
        public void put(final char code) {
            if (code != '-' && this.lastCode != code && (code != '0' || this.length == 0)) {
                this.data[this.length] = code;
                ++this.length;
            }
            this.lastCode = code;
        }
        
        @Override
        protected char[] copyData(final int start, final int length) {
            final char[] newData = new char[length];
            System.arraycopy(this.data, start, newData, 0, length);
            return newData;
        }
    }
    
    private class CologneInputBuffer extends CologneBuffer
    {
        public CologneInputBuffer(final char[] data) {
            super(data);
        }
        
        @Override
        protected char[] copyData(final int start, final int length) {
            final char[] newData = new char[length];
            System.arraycopy(this.data, this.data.length - this.length + start, newData, 0, length);
            return newData;
        }
        
        public char getNextChar() {
            return this.data[this.getNextPos()];
        }
        
        protected int getNextPos() {
            return this.data.length - this.length;
        }
        
        public char removeNext() {
            final char ch = this.getNextChar();
            --this.length;
            return ch;
        }
    }
}
