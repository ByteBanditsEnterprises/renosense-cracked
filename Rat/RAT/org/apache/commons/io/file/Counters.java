//Raddon On Top!

package org.apache.commons.io.file;

import java.util.*;
import java.math.*;

public class Counters
{
    public static Counter bigIntegerCounter() {
        return new BigIntegerCounter();
    }
    
    public static PathCounters bigIntegerPathCounters() {
        return new BigIntegerPathCounters();
    }
    
    public static Counter longCounter() {
        return new LongCounter();
    }
    
    public static PathCounters longPathCounters() {
        return new LongPathCounters();
    }
    
    public static Counter noopCounter() {
        return NoopCounter.INSTANCE;
    }
    
    public static PathCounters noopPathCounters() {
        return NoopPathCounters.INSTANCE;
    }
    
    private static class AbstractPathCounters implements PathCounters
    {
        private final Counter byteCounter;
        private final Counter directoryCounter;
        private final Counter fileCounter;
        
        protected AbstractPathCounters(final Counter byteCounter, final Counter directoryCounter, final Counter fileCounter) {
            this.byteCounter = byteCounter;
            this.directoryCounter = directoryCounter;
            this.fileCounter = fileCounter;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof AbstractPathCounters)) {
                return false;
            }
            final AbstractPathCounters other = (AbstractPathCounters)obj;
            return Objects.equals(this.byteCounter, other.byteCounter) && Objects.equals(this.directoryCounter, other.directoryCounter) && Objects.equals(this.fileCounter, other.fileCounter);
        }
        
        @Override
        public Counter getByteCounter() {
            return this.byteCounter;
        }
        
        @Override
        public Counter getDirectoryCounter() {
            return this.directoryCounter;
        }
        
        @Override
        public Counter getFileCounter() {
            return this.fileCounter;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.byteCounter, this.directoryCounter, this.fileCounter);
        }
        
        @Override
        public void reset() {
            this.byteCounter.reset();
            this.directoryCounter.reset();
            this.fileCounter.reset();
        }
        
        @Override
        public String toString() {
            return String.format("%,d files, %,d directories, %,d bytes", this.fileCounter.get(), this.directoryCounter.get(), this.byteCounter.get());
        }
    }
    
    private static final class BigIntegerCounter implements Counter
    {
        private BigInteger value;
        
        private BigIntegerCounter() {
            this.value = BigInteger.ZERO;
        }
        
        @Override
        public void add(final long val) {
            this.value = this.value.add(BigInteger.valueOf(val));
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Counter)) {
                return false;
            }
            final Counter other = (Counter)obj;
            return Objects.equals(this.value, other.getBigInteger());
        }
        
        @Override
        public long get() {
            return this.value.longValueExact();
        }
        
        @Override
        public BigInteger getBigInteger() {
            return this.value;
        }
        
        @Override
        public Long getLong() {
            return this.value.longValueExact();
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }
        
        @Override
        public void increment() {
            this.value = this.value.add(BigInteger.ONE);
        }
        
        @Override
        public String toString() {
            return this.value.toString();
        }
        
        @Override
        public void reset() {
            this.value = BigInteger.ZERO;
        }
    }
    
    private static final class BigIntegerPathCounters extends AbstractPathCounters
    {
        protected BigIntegerPathCounters() {
            super(Counters.bigIntegerCounter(), Counters.bigIntegerCounter(), Counters.bigIntegerCounter());
        }
    }
    
    public interface Counter
    {
        void add(final long p0);
        
        long get();
        
        BigInteger getBigInteger();
        
        Long getLong();
        
        void increment();
        
        default void reset() {
        }
    }
    
    private static final class LongCounter implements Counter
    {
        private long value;
        
        @Override
        public void add(final long add) {
            this.value += add;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Counter)) {
                return false;
            }
            final Counter other = (Counter)obj;
            return this.value == other.get();
        }
        
        @Override
        public long get() {
            return this.value;
        }
        
        @Override
        public BigInteger getBigInteger() {
            return BigInteger.valueOf(this.value);
        }
        
        @Override
        public Long getLong() {
            return this.value;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }
        
        @Override
        public void increment() {
            ++this.value;
        }
        
        @Override
        public String toString() {
            return Long.toString(this.value);
        }
        
        @Override
        public void reset() {
            this.value = 0L;
        }
    }
    
    private static final class LongPathCounters extends AbstractPathCounters
    {
        protected LongPathCounters() {
            super(Counters.longCounter(), Counters.longCounter(), Counters.longCounter());
        }
    }
    
    private static final class NoopCounter implements Counter
    {
        static final NoopCounter INSTANCE;
        
        @Override
        public void add(final long add) {
        }
        
        @Override
        public long get() {
            return 0L;
        }
        
        @Override
        public BigInteger getBigInteger() {
            return BigInteger.ZERO;
        }
        
        @Override
        public Long getLong() {
            return 0L;
        }
        
        @Override
        public void increment() {
        }
        
        static {
            INSTANCE = new NoopCounter();
        }
    }
    
    private static final class NoopPathCounters extends AbstractPathCounters
    {
        static final NoopPathCounters INSTANCE;
        
        private NoopPathCounters() {
            super(Counters.noopCounter(), Counters.noopCounter(), Counters.noopCounter());
        }
        
        static {
            INSTANCE = new NoopPathCounters();
        }
    }
    
    public interface PathCounters
    {
        Counter getByteCounter();
        
        Counter getDirectoryCounter();
        
        Counter getFileCounter();
        
        default void reset() {
        }
    }
}
