//Raddon On Top!

package org.apache.commons.lang3.function;

import java.util.*;

public interface FailableIntUnaryOperator<E extends Throwable>
{
    public static final FailableIntUnaryOperator NOP = t -> 0;
    
    default <E extends Throwable> FailableIntUnaryOperator<E> identity() {
        return t -> t;
    }
    
    default <E extends Throwable> FailableIntUnaryOperator<E> nop() {
        return (FailableIntUnaryOperator<E>)FailableIntUnaryOperator.NOP;
    }
    
    default FailableIntUnaryOperator<E> andThen(final FailableIntUnaryOperator<E> after) {
        Objects.requireNonNull(after);
        return t -> after.applyAsInt(this.applyAsInt(t));
    }
    
    int applyAsInt(final int p0) throws E, Throwable;
    
    default FailableIntUnaryOperator<E> compose(final FailableIntUnaryOperator<E> before) {
        Objects.requireNonNull(before);
        return v -> this.applyAsInt(before.applyAsInt(v));
    }
}
