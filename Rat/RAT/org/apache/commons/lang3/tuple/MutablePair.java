//Raddon On Top!

package org.apache.commons.lang3.tuple;

import java.util.*;

public class MutablePair<L, R> extends Pair<L, R>
{
    public static final MutablePair<?, ?>[] EMPTY_ARRAY;
    private static final long serialVersionUID = 4954918890077093841L;
    public L left;
    public R right;
    
    public static <L, R> MutablePair<L, R>[] emptyArray() {
        return (MutablePair<L, R>[])MutablePair.EMPTY_ARRAY;
    }
    
    public static <L, R> MutablePair<L, R> of(final L left, final R right) {
        return new MutablePair<L, R>(left, right);
    }
    
    public static <L, R> MutablePair<L, R> of(final Map.Entry<L, R> pair) {
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
        return new MutablePair<L, R>(left, right);
    }
    
    public MutablePair() {
    }
    
    public MutablePair(final L left, final R right) {
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
    
    public void setLeft(final L left) {
        this.left = left;
    }
    
    public void setRight(final R right) {
        this.right = right;
    }
    
    @Override
    public R setValue(final R value) {
        final R result = this.getRight();
        this.setRight(value);
        return result;
    }
    
    static {
        EMPTY_ARRAY = new MutablePair[0];
    }
}
