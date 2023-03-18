//Raddon On Top!

package org.apache.commons.lang3.compare;

import java.util.function.*;

public class ComparableUtils
{
    public static <A extends Comparable<A>> Predicate<A> between(final A b, final A c) {
        return a -> is(a).between(b, c);
    }
    
    public static <A extends Comparable<A>> Predicate<A> betweenExclusive(final A b, final A c) {
        return a -> is(a).betweenExclusive(b, c);
    }
    
    public static <A extends Comparable<A>> Predicate<A> ge(final A b) {
        return a -> is(a).greaterThanOrEqualTo(b);
    }
    
    public static <A extends Comparable<A>> Predicate<A> gt(final A b) {
        return a -> is(a).greaterThan(b);
    }
    
    public static <A extends Comparable<A>> ComparableCheckBuilder<A> is(final A a) {
        return new ComparableCheckBuilder<A>((Comparable)a);
    }
    
    public static <A extends Comparable<A>> Predicate<A> le(final A b) {
        return a -> is(a).lessThanOrEqualTo(b);
    }
    
    public static <A extends Comparable<A>> Predicate<A> lt(final A b) {
        return a -> is(a).lessThan(b);
    }
    
    private ComparableUtils() {
    }
    
    public static class ComparableCheckBuilder<A extends Comparable<A>>
    {
        private final A a;
        
        private ComparableCheckBuilder(final A a) {
            this.a = a;
        }
        
        public boolean between(final A b, final A c) {
            return this.betweenOrdered(b, c) || this.betweenOrdered(c, b);
        }
        
        public boolean betweenExclusive(final A b, final A c) {
            return this.betweenOrderedExclusive(b, c) || this.betweenOrderedExclusive(c, b);
        }
        
        private boolean betweenOrdered(final A b, final A c) {
            return this.greaterThanOrEqualTo(b) && this.lessThanOrEqualTo(c);
        }
        
        private boolean betweenOrderedExclusive(final A b, final A c) {
            return this.greaterThan(b) && this.lessThan(c);
        }
        
        public boolean equalTo(final A b) {
            return this.a.compareTo(b) == 0;
        }
        
        public boolean greaterThan(final A b) {
            return this.a.compareTo(b) > 0;
        }
        
        public boolean greaterThanOrEqualTo(final A b) {
            return this.a.compareTo(b) >= 0;
        }
        
        public boolean lessThan(final A b) {
            return this.a.compareTo(b) < 0;
        }
        
        public boolean lessThanOrEqualTo(final A b) {
            return this.a.compareTo(b) <= 0;
        }
    }
}
