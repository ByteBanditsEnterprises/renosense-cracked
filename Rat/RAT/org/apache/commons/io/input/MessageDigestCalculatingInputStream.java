//Raddon On Top!

package org.apache.commons.io.input;

import java.security.*;
import java.io.*;

public class MessageDigestCalculatingInputStream extends ObservableInputStream
{
    private final MessageDigest messageDigest;
    
    public MessageDigestCalculatingInputStream(final InputStream inputStream, final MessageDigest messageDigest) {
        super(inputStream, new Observer[] { new MessageDigestMaintainingObserver(messageDigest) });
        this.messageDigest = messageDigest;
    }
    
    public MessageDigestCalculatingInputStream(final InputStream inputStream, final String algorithm) throws NoSuchAlgorithmException {
        this(inputStream, MessageDigest.getInstance(algorithm));
    }
    
    public MessageDigestCalculatingInputStream(final InputStream inputStream) throws NoSuchAlgorithmException {
        this(inputStream, MessageDigest.getInstance("MD5"));
    }
    
    public MessageDigest getMessageDigest() {
        return this.messageDigest;
    }
    
    public static class MessageDigestMaintainingObserver extends Observer
    {
        private final MessageDigest messageDigest;
        
        public MessageDigestMaintainingObserver(final MessageDigest messageDigest) {
            this.messageDigest = messageDigest;
        }
        
        @Override
        public void data(final int input) throws IOException {
            this.messageDigest.update((byte)input);
        }
        
        @Override
        public void data(final byte[] input, final int offset, final int length) throws IOException {
            this.messageDigest.update(input, offset, length);
        }
    }
}
