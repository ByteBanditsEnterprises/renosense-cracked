//Raddon On Top!

package org.apache.commons.lang3;

import java.util.concurrent.*;
import java.util.function.*;
import org.apache.commons.lang3.function.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

@Deprecated
public class Functions
{
    public static <O1, O2, T extends Throwable> void accept(final FailableBiConsumer<O1, O2, T> consumer, final O1 object1, final O2 object2) {
        run(() -> consumer.accept(object1, object2));
    }
    
    public static <O, T extends Throwable> void accept(final FailableConsumer<O, T> consumer, final O object) {
        run(() -> consumer.accept(object));
    }
    
    public static <O1, O2, O, T extends Throwable> O apply(final FailableBiFunction<O1, O2, O, T> function, final O1 input1, final O2 input2) {
        return get(() -> function.apply(input1, input2));
    }
    
    public static <I, O, T extends Throwable> O apply(final FailableFunction<I, O, T> function, final I input) {
        return get(() -> function.apply(input));
    }
    
    public static <O1, O2> BiConsumer<O1, O2> asBiConsumer(final FailableBiConsumer<O1, O2, ?> consumer) {
        return (input1, input2) -> accept((FailableBiConsumer<Object, Object, Throwable>)consumer, input1, input2);
    }
    
    public static <O1, O2, O> BiFunction<O1, O2, O> asBiFunction(final FailableBiFunction<O1, O2, O, ?> function) {
        return (BiFunction<O1, O2, O>)((input1, input2) -> apply((FailableBiFunction<Object, Object, O, Throwable>)function, input1, input2));
    }
    
    public static <O1, O2> BiPredicate<O1, O2> asBiPredicate(final FailableBiPredicate<O1, O2, ?> predicate) {
        return (input1, input2) -> test((FailableBiPredicate<Object, Object, Throwable>)predicate, input1, input2);
    }
    
    public static <O> Callable<O> asCallable(final FailableCallable<O, ?> callable) {
        return () -> call(callable);
    }
    
    public static <I> Consumer<I> asConsumer(final FailableConsumer<I, ?> consumer) {
        return input -> accept((FailableConsumer<Object, Throwable>)consumer, input);
    }
    
    public static <I, O> Function<I, O> asFunction(final FailableFunction<I, O, ?> function) {
        return (Function<I, O>)(input -> apply((FailableFunction<Object, O, Throwable>)function, input));
    }
    
    public static <I> Predicate<I> asPredicate(final FailablePredicate<I, ?> predicate) {
        return input -> test((FailablePredicate<Object, Throwable>)predicate, input);
    }
    
    public static Runnable asRunnable(final FailableRunnable<?> runnable) {
        return () -> run(runnable);
    }
    
    public static <O> Supplier<O> asSupplier(final FailableSupplier<O, ?> supplier) {
        return () -> get(supplier);
    }
    
    public static <O, T extends Throwable> O call(final FailableCallable<O, T> callable) {
        return get(callable::call);
    }
    
    public static <O, T extends Throwable> O get(final FailableSupplier<O, T> supplier) {
        try {
            return supplier.get();
        }
        catch (Throwable t) {
            throw rethrow(t);
        }
    }
    
    private static <T extends Throwable> boolean getAsBoolean(final FailableBooleanSupplier<T> supplier) {
        try {
            return supplier.getAsBoolean();
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
    
    public static <T extends Throwable> void run(final FailableRunnable<T> runnable) {
        try {
            runnable.run();
        }
        catch (Throwable t) {
            throw rethrow(t);
        }
    }
    
    public static <O> Streams.FailableStream<O> stream(final Collection<O> collection) {
        return new Streams.FailableStream<O>(collection.stream());
    }
    
    public static <O> Streams.FailableStream<O> stream(final Stream<O> stream) {
        return new Streams.FailableStream<O>(stream);
    }
    
    public static <O1, O2, T extends Throwable> boolean test(final FailableBiPredicate<O1, O2, T> predicate, final O1 object1, final O2 object2) {
        return getAsBoolean((org.apache.commons.lang3.function.FailableBooleanSupplier<Throwable>)(() -> predicate.test(object1, object2)));
    }
    
    public static <O, T extends Throwable> boolean test(final FailablePredicate<O, T> predicate, final O object) {
        return getAsBoolean((org.apache.commons.lang3.function.FailableBooleanSupplier<Throwable>)(() -> predicate.test(object)));
    }
    
    @SafeVarargs
    public static void tryWithResources(final FailableRunnable<? extends Throwable> action, final FailableConsumer<Throwable, ? extends Throwable> errorHandler, final FailableRunnable<? extends Throwable>... resources) {
        FailableConsumer<Throwable, ? extends Throwable> actualErrorHandler;
        if (errorHandler == null) {
            actualErrorHandler = Functions::rethrow;
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
    
    @Deprecated
    @FunctionalInterface
    public interface FailableSupplier<R, T extends Throwable>
    {
        R get() throws T, Throwable;
    }
    
    @Deprecated
    @FunctionalInterface
    public interface FailableRunnable<T extends Throwable>
    {
        void run() throws T, Throwable;
    }
    
    @Deprecated
    @FunctionalInterface
    public interface FailablePredicate<I, T extends Throwable>
    {
        boolean test(final I p0) throws T, Throwable;
    }
    
    @Deprecated
    @FunctionalInterface
    public interface FailableFunction<I, R, T extends Throwable>
    {
        R apply(final I p0) throws T, Throwable;
    }
    
    @Deprecated
    @FunctionalInterface
    public interface FailableConsumer<O, T extends Throwable>
    {
        void accept(final O p0) throws T, Throwable;
    }
    
    @Deprecated
    @FunctionalInterface
    public interface FailableCallable<R, T extends Throwable>
    {
        R call() throws T, Throwable;
    }
    
    @Deprecated
    @FunctionalInterface
    public interface FailableBiPredicate<O1, O2, T extends Throwable>
    {
        boolean test(final O1 p0, final O2 p1) throws T, Throwable;
    }
    
    @Deprecated
    @FunctionalInterface
    public interface FailableBiFunction<O1, O2, R, T extends Throwable>
    {
        R apply(final O1 p0, final O2 p1) throws T, Throwable;
    }
    
    @Deprecated
    @FunctionalInterface
    public interface FailableBiConsumer<O1, O2, T extends Throwable>
    {
        void accept(final O1 p0, final O2 p1) throws T, Throwable;
    }
}
