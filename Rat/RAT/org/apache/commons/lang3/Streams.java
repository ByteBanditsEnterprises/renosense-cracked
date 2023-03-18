//Raddon On Top!

package org.apache.commons.lang3;

import java.util.stream.*;
import java.util.function.*;
import java.lang.reflect.*;
import java.util.*;

@Deprecated
public class Streams
{
    public static <O> FailableStream<O> stream(final Stream<O> stream) {
        return new FailableStream<O>(stream);
    }
    
    public static <O> FailableStream<O> stream(final Collection<O> stream) {
        return stream(stream.stream());
    }
    
    public static <O> Collector<O, ?, O[]> toArray(final Class<O> pElementType) {
        return new ArrayCollector<O>(pElementType);
    }
    
    @Deprecated
    public static class FailableStream<O>
    {
        private Stream<O> stream;
        private boolean terminated;
        
        public FailableStream(final Stream<O> stream) {
            this.stream = stream;
        }
        
        protected void assertNotTerminated() {
            if (this.terminated) {
                throw new IllegalStateException("This stream is already terminated.");
            }
        }
        
        protected void makeTerminated() {
            this.assertNotTerminated();
            this.terminated = true;
        }
        
        public FailableStream<O> filter(final Functions.FailablePredicate<O, ?> predicate) {
            this.assertNotTerminated();
            this.stream = this.stream.filter(Functions.asPredicate((Functions.FailablePredicate)predicate));
            return this;
        }
        
        public void forEach(final Functions.FailableConsumer<O, ?> action) {
            this.makeTerminated();
            this.stream().forEach(Functions.asConsumer((Functions.FailableConsumer)action));
        }
        
        public <A, R> R collect(final Collector<? super O, A, R> collector) {
            this.makeTerminated();
            return this.stream().collect(collector);
        }
        
        public <A, R> R collect(final Supplier<R> pupplier, final BiConsumer<R, ? super O> accumulator, final BiConsumer<R, R> combiner) {
            this.makeTerminated();
            return this.stream().collect(pupplier, accumulator, combiner);
        }
        
        public O reduce(final O identity, final BinaryOperator<O> accumulator) {
            this.makeTerminated();
            return this.stream().reduce(identity, accumulator);
        }
        
        public <R> FailableStream<R> map(final Functions.FailableFunction<O, R, ?> mapper) {
            this.assertNotTerminated();
            return new FailableStream<R>(this.stream.map((Function<? super O, ? extends R>)Functions.asFunction((Functions.FailableFunction)mapper)));
        }
        
        public Stream<O> stream() {
            return this.stream;
        }
        
        public boolean allMatch(final Functions.FailablePredicate<O, ?> predicate) {
            this.assertNotTerminated();
            return this.stream().allMatch(Functions.asPredicate((Functions.FailablePredicate)predicate));
        }
        
        public boolean anyMatch(final Functions.FailablePredicate<O, ?> predicate) {
            this.assertNotTerminated();
            return this.stream().anyMatch(Functions.asPredicate((Functions.FailablePredicate)predicate));
        }
    }
    
    @Deprecated
    public static class ArrayCollector<O> implements Collector<O, List<O>, O[]>
    {
        private static final Set<Characteristics> characteristics;
        private final Class<O> elementType;
        
        public ArrayCollector(final Class<O> elementType) {
            this.elementType = elementType;
        }
        
        @Override
        public Supplier<List<O>> supplier() {
            return (Supplier<List<O>>)ArrayList::new;
        }
        
        @Override
        public BiConsumer<List<O>, O> accumulator() {
            return List::add;
        }
        
        @Override
        public BinaryOperator<List<O>> combiner() {
            return (BinaryOperator<List<O>>)((left, right) -> {
                left.addAll(right);
                return left;
            });
        }
        
        @Override
        public Function<List<O>, O[]> finisher() {
            final O[] array;
            return (Function<List<O>, O[]>)(list -> {
                array = (O[])Array.newInstance(this.elementType, list.size());
                return list.toArray(array);
            });
        }
        
        @Override
        public Set<Characteristics> characteristics() {
            return ArrayCollector.characteristics;
        }
        
        static {
            characteristics = Collections.emptySet();
        }
    }
}
