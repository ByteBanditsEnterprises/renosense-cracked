//Raddon On Top!

package org.apache.commons.lang3.function;

import java.util.*;

public interface FailableDoubleUnaryOperator<E extends Throwable>
{
    public static final FailableDoubleUnaryOperator NOP = t -> 0.0;
    
    default <E extends Throwable> FailableDoubleUnaryOperator<E> identity() {
        return t -> t;
    }
    
    default <E extends Throwable> FailableDoubleUnaryOperator<E> nop() {
        return (FailableDoubleUnaryOperator<E>)FailableDoubleUnaryOperator.NOP;
    }
    
    default FailableDoubleUnaryOperator<E> andThen(final FailableDoubleUnaryOperator<E> after) {
        Objects.requireNonNull(after);
        return t -> after.applyAsDouble(this.applyAsDouble(t));
    }
    
    double applyAsDouble(final double p0) throws E, Throwable;
    
    default FailableDoubleUnaryOperator<E> compose(final FailableDoubleUnaryOperator<E> before) {
        Objects.requireNonNull(before);
        return v -> this.applyAsDouble(before.applyAsDouble(v));
    }
}
