//Raddon On Top!

package org.apache.commons.lang3.tuple;

import java.io.*;
import org.apache.commons.lang3.builder.*;
import java.util.*;

public abstract class Triple<L, M, R> implements Comparable<Triple<L, M, R>>, Serializable
{
    private static final long serialVersionUID = 1L;
    public static final Triple<?, ?, ?>[] EMPTY_ARRAY;
    
    public static <L, M, R> Triple<L, M, R>[] emptyArray() {
        return (Triple<L, M, R>[])Triple.EMPTY_ARRAY;
    }
    
    public static <L, M, R> Triple<L, M, R> of(final L left, final M middle, final R right) {
        return (Triple<L, M, R>)new ImmutableTriple((Object)left, (Object)middle, (Object)right);
    }
    
    @Override
    public int compareTo(final Triple<L, M, R> other) {
        return new CompareToBuilder().append(this.getLeft(), (Object)other.getLeft()).append(this.getMiddle(), (Object)other.getMiddle()).append(this.getRight(), (Object)other.getRight()).toComparison();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Triple) {
            final Triple<?, ?, ?> other = (Triple<?, ?, ?>)obj;
            return Objects.equals(this.getLeft(), other.getLeft()) && Objects.equals(this.getMiddle(), other.getMiddle()) && Objects.equals(this.getRight(), other.getRight());
        }
        return false;
    }
    
    public abstract L getLeft();
    
    public abstract M getMiddle();
    
    public abstract R getRight();
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.getLeft()) ^ Objects.hashCode(this.getMiddle()) ^ Objects.hashCode(this.getRight());
    }
    
    @Override
    public String toString() {
        return "(" + this.getLeft() + "," + this.getMiddle() + "," + this.getRight() + ")";
    }
    
    public String toString(final String format) {
        return String.format(format, this.getLeft(), this.getMiddle(), this.getRight());
    }
    
    static {
        EMPTY_ARRAY = new TripleAdapter[0];
    }
    
    private static final class TripleAdapter<L, M, R> extends Triple<L, M, R>
    {
        private static final long serialVersionUID = 1L;
        
        @Override
        public L getLeft() {
            return null;
        }
        
        @Override
        public M getMiddle() {
            return null;
        }
        
        @Override
        public R getRight() {
            return null;
        }
    }
}
