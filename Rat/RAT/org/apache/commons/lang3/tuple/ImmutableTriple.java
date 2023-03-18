//Raddon On Top!

package org.apache.commons.lang3.tuple;

public final class ImmutableTriple<L, M, R> extends Triple<L, M, R>
{
    public static final ImmutableTriple<?, ?, ?>[] EMPTY_ARRAY;
    private static final ImmutableTriple NULL;
    private static final long serialVersionUID = 1L;
    public final L left;
    public final M middle;
    public final R right;
    
    public static <L, M, R> ImmutableTriple<L, M, R>[] emptyArray() {
        return (ImmutableTriple<L, M, R>[])ImmutableTriple.EMPTY_ARRAY;
    }
    
    public static <L, M, R> ImmutableTriple<L, M, R> nullTriple() {
        return (ImmutableTriple<L, M, R>)ImmutableTriple.NULL;
    }
    
    public static <L, M, R> ImmutableTriple<L, M, R> of(final L left, final M middle, final R right) {
        return new ImmutableTriple<L, M, R>(left, middle, right);
    }
    
    public ImmutableTriple(final L left, final M middle, final R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }
    
    @Override
    public L getLeft() {
        return this.left;
    }
    
    @Override
    public M getMiddle() {
        return this.middle;
    }
    
    @Override
    public R getRight() {
        return this.right;
    }
    
    static {
        EMPTY_ARRAY = new ImmutableTriple[0];
        NULL = of((Object)null, (Object)null, (Object)null);
    }
}
