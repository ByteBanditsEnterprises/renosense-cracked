//Raddon On Top!

package org.apache.commons.codec.net;

import java.nio.charset.*;
import org.apache.commons.codec.binary.*;
import java.io.*;
import org.apache.commons.codec.*;

public class BCodec extends RFC1522Codec implements StringEncoder, StringDecoder
{
    private static final CodecPolicy DECODING_POLICY_DEFAULT;
    private final Charset charset;
    private final CodecPolicy decodingPolicy;
    
    public BCodec() {
        this(StandardCharsets.UTF_8);
    }
    
    public BCodec(final Charset charset) {
        this(charset, BCodec.DECODING_POLICY_DEFAULT);
    }
    
    public BCodec(final Charset charset, final CodecPolicy decodingPolicy) {
        this.charset = charset;
        this.decodingPolicy = decodingPolicy;
    }
    
    public BCodec(final String charsetName) {
        this(Charset.forName(charsetName));
    }
    
    public boolean isStrictDecoding() {
        return this.decodingPolicy == CodecPolicy.STRICT;
    }
    
    @Override
    protected String getEncoding() {
        return "B";
    }
    
    @Override
    protected byte[] doEncoding(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return Base64.encodeBase64(bytes);
    }
    
    @Override
    protected byte[] doDecoding(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new Base64(0, BaseNCodec.getChunkSeparator(), false, this.decodingPolicy).decode(bytes);
    }
    
    public String encode(final String strSource, final Charset sourceCharset) throws EncoderException {
        if (strSource == null) {
            return null;
        }
        return this.encodeText(strSource, sourceCharset);
    }
    
    public String encode(final String strSource, final String sourceCharset) throws EncoderException {
        if (strSource == null) {
            return null;
        }
        try {
            return this.encodeText(strSource, sourceCharset);
        }
        catch (UnsupportedEncodingException e) {
            throw new EncoderException(e.getMessage(), (Throwable)e);
        }
    }
    
    @Override
    public String encode(final String strSource) throws EncoderException {
        if (strSource == null) {
            return null;
        }
        return this.encode(strSource, this.getCharset());
    }
    
    @Override
    public String decode(final String value) throws DecoderException {
        if (value == null) {
            return null;
        }
        try {
            return this.decodeText(value);
        }
        catch (UnsupportedEncodingException | IllegalArgumentException ex2) {
            final Exception ex;
            final Exception e = ex;
            throw new DecoderException(e.getMessage(), (Throwable)e);
        }
    }
    
    public Object encode(final Object value) throws EncoderException {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return this.encode((String)value);
        }
        throw new EncoderException("Objects of type " + value.getClass().getName() + " cannot be encoded using BCodec");
    }
    
    public Object decode(final Object value) throws DecoderException {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return this.decode((String)value);
        }
        throw new DecoderException("Objects of type " + value.getClass().getName() + " cannot be decoded using BCodec");
    }
    
    public Charset getCharset() {
        return this.charset;
    }
    
    public String getDefaultCharset() {
        return this.charset.name();
    }
    
    static {
        DECODING_POLICY_DEFAULT = CodecPolicy.LENIENT;
    }
}
