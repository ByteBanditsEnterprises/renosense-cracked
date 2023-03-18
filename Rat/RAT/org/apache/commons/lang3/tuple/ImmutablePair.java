//Raddon On Top!

package org.apache.commons.lang3.tuple;

import java.util.*;

public final class ImmutablePair<L, R> extends Pair<L, R>
{
    public static final ImmutablePair<?, ?>[] EMPTY_ARRAY;
    private static final ImmutablePair NULL;
    private static final long serialVersionUID = 4954918890077093841L;
    public final L left;
    public final R right;
    
    public static <L, R> ImmutablePair<L, R>[] emptyArray() {
        return (ImmutablePair<L, R>[])ImmutablePair.EMPTY_ARRAY;
    }
    
    public static <L, R> Pair<L, R> left(final L left) {
        return (Pair<L, R>)of(left, (Object)null);
    }
    
    public static <L, R> ImmutablePair<L, R> nullPair() {
        return (ImmutablePair<L, R>)ImmutablePair.NULL;
    }
    
    public static <L, R> ImmutablePair<L, R> of(final L left, final R right) {
        return new ImmutablePair<L, R>(left, right);
    }
    
    public static <L, R> ImmutablePair<L, R> of(final Map.Entry<L, R> pair) {
        L left;
        R right;
        if (pair != null) {
            left = pair.getKey();
            right = pair.getValue();
        }
        else {
            left = null;
            right = null;
        }
        return new ImmutablePair<L, R>(left, right);
    }
    
    public static <L, R> Pair<L, R> right(final R right) {
        return (Pair<L, R>)of((Object)null, right);
    }
    
    public ImmutablePair(final L left, final R right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public L getLeft() {
        return this.left;
    }
    
    @Override
    public R getRight() {
        return this.right;
    }
    
    @Override
    public R setValue(final R value) {
        throw new UnsupportedOperationException();
    }
    
    static {
        EMPTY_ARRAY = new ImmutablePair[0];
        NULL = of((Object)null, (Object)null);
    }
}
