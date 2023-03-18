//Raddon On Top!

package org.apache.commons.lang3.function;

import java.util.concurrent.*;
import java.util.function.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.lang3.stream.*;
import java.util.stream.*;

public class Failable
{
    public static <T, U, E extends Throwable> void accept(final FailableBiConsumer<T, U, E> consumer, final T object1, final U object2) {
        run(() -> consumer.accept(object1, object2));
    }
    
    public static <T, E extends Throwable> void accept(final FailableConsumer<T, E> consumer, final T object) {
        run(() -> consumer.accept(object));
    }
    
    public static <E extends Throwable> void accept(final FailableDoubleConsumer<E> consumer, final double value) {
        run(() -> consumer.accept(value));
    }
    
    public static <E extends Throwable> void accept(final FailableIntConsumer<E> consumer, final int value) {
        run(() -> consumer.accept(value));
    }
    
    public static <E extends Throwable> void accept(final FailableLongConsumer<E> consumer, final long value) {
        run(() -> consumer.accept(value));
    }
    
    public static <T, U, R, E extends Throwable> R apply(final FailableBiFunction<T, U, R, E> function, final T input1, final U input2) {
        return get(() -> function.apply(input1, input2));
    }
    
    public static <T, R, E extends Throwable> R apply(final FailableFunction<T, R, E> function, final T input) {
        return get(() -> function.apply(input));
    }
    
    public static <E extends Throwable> double applyAsDouble(final FailableDoubleBinaryOperator<E> function, final double left, final double right) {
        return getAsDouble(() -> function.applyAsDouble(left, right));
    }
    
    public static <T, U> BiConsumer<T, U> asBiConsumer(final FailableBiConsumer<T, U, ?> consumer) {
        return (input1, input2) -> accept((FailableBiConsumer<Object, Object, Throwable>)consumer, input1, input2);
    }
    
    public static <T, U, R> BiFunction<T, U, R> asBiFunction(final FailableBiFunction<T, U, R, ?> function) {
        return (BiFunction<T, U, R>)((input1, input2) -> apply((FailableBiFunction<Object, Object, R, Throwable>)function, input1, input2));
    }
    
    public static <T, U> BiPredicate<T, U> asBiPredicate(final FailableBiPredicate<T, U, ?> predicate) {
        return (input1, input2) -> test((FailableBiPredicate<Object, Object, Throwable>)predicate, input1, input2);
    }
    
    public static <V> Callable<V> asCallable(final FailableCallable<V, ?> callable) {
        return () -> call(callable);
    }
    
    public static <T> Consumer<T> asConsumer(final FailableConsumer<T, ?> consumer) {
        return input -> accept((FailableConsumer<Object, Throwable>)consumer, input);
    }
    
    public static <T, R> Function<T, R> asFunction(final FailableFunction<T, R, ?> function) {
        return (Function<T, R>)(input -> apply((FailableFunction<Object, R, Throwable>)function, input));
    }
    
    public static <T> Predicate<T> asPredicate(final FailablePredicate<T, ?> predicate) {
        return input -> test((FailablePredicate<Object, Throwable>)predicate, input);
    }
    
    public static Runnable asRunnable(final FailableRunnable<?> runnable) {
        return () -> run(runnable);
    }
    
    public static <T> Supplier<T> asSupplier(final FailableSupplier<T, ?> supplier) {
        return () -> get(supplier);
    }
    
    public static <V, E extends Throwable> V call(final FailableCallable<V, E> callable) {
        return get(callable::call);
    }
    
    public static <T, E extends Throwable> T get(final FailableSupplier<T, E> supplier) {
        try {
            return supplier.get();
        }
        catch (Throwable t) {
            throw rethrow(t);
        }
    }
    
    public static <E extends Throwable> boolean getAsBoolean(final FailableBooleanSupplier<E> supplier) {
        try {
            return supplier.getAsBoolean();
        }
        catch (Throwable t) {
            throw rethrow(t);
        }
    }
    
    public static <E extends Throwable> double getAsDouble(final FailableDoubleSupplier<E> supplier) {
        try {
            return supplier.getAsDouble();
        }
        catch (Throwable t) {
            throw rethrow(t);
        }
    }
    
    public static <E extends Throwable> int getAsInt(final FailableIntSupplier<E> supplier) {
        try {
            return supplier.getAsInt();
        }
        catch (Throwable t) {
            throw rethrow(t);
        }
    }
    
    public static <E extends Throwable> long getAsLong(final FailableLongSupplier<E> supplier) {
        try {
            return supplier.getAsLong();
        }
        catch (Throwable t) {
            throw rethrow(t);
        }
    }
    
    public static <E extends Throwable> short getAsShort(final FailableShortSupplier<E> supplier) {
        try {
            return supplier.getAsShort();
        }
        catch (Throwable t) {
            throw rethrow(t);
        }
    }
    
    public static RuntimeException rethrow(final Throwable throwable) {
        Objects.requireNonNull(throwable, "throwable");
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException)throwable;
        }
        if (throwable instanceof Error) {
            throw (Error)throwable;
        }
        if (throwable instanceof IOException) {
            throw new UncheckedIOException((IOException)throwable);
        }
        throw new UndeclaredThrowableException(throwable);
    }
    
    public static <E extends Throwable> void run(final FailableRunnable<E> runnable) {
        try {
            runnable.run();
        }
        catch (Throwable t) {
            throw rethrow(t);
        }
    }
    
    public static <E> Streams.FailableStream<E> stream(final Collection<E> collection) {
        return new Streams.FailableStream<E>(collection.stream());
    }
    
    public static <T> Streams.FailableStream<T> stream(final Stream<T> stream) {
        return new Streams.FailableStream<T>(stream);
    }
    
    public static <T, U, E extends Throwable> boolean test(final FailableBiPredicate<T, U, E> predicate, final T object1, final U object2) {
        return getAsBoolean(() -> predicate.test(object1, object2));
    }
    
    public static <T, E extends Throwable> boolean test(final FailablePredicate<T, E> predicate, final T object) {
        return getAsBoolean(() -> predicate.test(object));
    }
    
    @SafeVarargs
    public static void tryWithResources(final FailableRunnable<? extends Throwable> action, final FailableConsumer<Throwable, ? extends Throwable> errorHandler, final FailableRunnable<? extends Throwable>... resources) {
        FailableConsumer<Throwable, ? extends Throwable> actualErrorHandler;
        if (errorHandler == null) {
            actualErrorHandler = Failable::rethrow;
        }
        else {
            actualErrorHandler = errorHandler;
        }
        if (resources != null) {
            for (final FailableRunnable<? extends Throwable> failableRunnable : resources) {
                Objects.requireNonNull(failableRunnable, "runnable");
            }
        }
        Throwable th = null;
        try {
            action.run();
        }
        catch (Throwable t) {
            th = t;
        }
        if (resources != null) {
            for (final FailableRunnable<?> runnable : resources) {
                try {
                    runnable.run();
                }
                catch (Throwable t2) {
                    if (th == null) {
                        th = t2;
                    }
                }
            }
        }
        if (th != null) {
            try {
                actualErrorHandler.accept(th);
            }
            catch (Throwable t) {
                throw rethrow(t);
            }
        }
    }
    
    @SafeVarargs
    public static void tryWithResources(final FailableRunnable<? extends Throwable> action, final FailableRunnable<? extends Throwable>... resources) {
        tryWithResources(action, (FailableConsumer<Throwable, ? extends Throwable>)null, resources);
    }
    
    private Failable() {
    }
}
