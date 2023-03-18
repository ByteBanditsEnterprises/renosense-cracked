//Raddon On Top!

package org.apache.commons.lang3;

import java.io.*;
import java.util.*;

public final class Range<T> implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final Comparator<T> comparator;
    private transient int hashCode;
    private final T maximum;
    private final T minimum;
    private transient String toString;
    
    public static <T extends Comparable<T>> Range<T> between(final T fromInclusive, final T toInclusive) {
        return between(fromInclusive, toInclusive, null);
    }
    
    public static <T> Range<T> between(final T fromInclusive, final T toInclusive, final Comparator<T> comparator) {
        return new Range<T>(fromInclusive, toInclusive, comparator);
    }
    
    public static <T extends Comparable<T>> Range<T> is(final T element) {
        return between(element, element, null);
    }
    
    public static <T> Range<T> is(final T element, final Comparator<T> comparator) {
        return between(element, element, comparator);
    }
    
    private Range(final T element1, final T element2, final Comparator<T> comp) {
        if (element1 == null || element2 == null) {
            throw new IllegalArgumentException("Elements in a range must not be null: element1=" + element1 + ", element2=" + element2);
        }
        if (comp == null) {
            this.comparator = (Comparator<T>)ComparableComparator.INSTANCE;
        }
        else {
            this.comparator = comp;
        }
        if (this.comparator.compare(element1, element2) < 1) {
            this.minimum = element1;
            this.maximum = element2;
        }
        else {
            this.minimum = element2;
            this.maximum = element1;
        }
    }
    
    public boolean contains(final T element) {
        return element != null && this.comparator.compare(element, this.minimum) > -1 && this.comparator.compare(element, this.maximum) < 1;
    }
    
    public boolean containsRange(final Range<T> otherRange) {
        return otherRange != null && this.contains(otherRange.minimum) && this.contains(otherRange.maximum);
    }
    
    public int elementCompareTo(final T element) {
        Validate.notNull(element, "element", new Object[0]);
        if (this.isAfter(element)) {
            return -1;
        }
        if (this.isBefore(element)) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        final Range<T> range = (Range<T>)obj;
        return this.minimum.equals(range.minimum) && this.maximum.equals(range.maximum);
    }
    
    public Comparator<T> getComparator() {
        return this.comparator;
    }
    
    public T getMaximum() {
        return this.maximum;
    }
    
    public T getMinimum() {
        return this.minimum;
    }
    
    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (this.hashCode == 0) {
            result = 17;
            result = 37 * result + this.getClass().hashCode();
            result = 37 * result + this.minimum.hashCode();
            result = 37 * result + this.maximum.hashCode();
            this.hashCode = result;
        }
        return result;
    }
    
    public Range<T> intersectionWith(final Range<T> other) {
        if (!this.isOverlappedBy(other)) {
            throw new IllegalArgumentException(String.format("Cannot calculate intersection with non-overlapping range %s", other));
        }
        if (this.equals(other)) {
            return this;
        }
        final T min = (this.getComparator().compare(this.minimum, other.minimum) < 0) ? other.minimum : this.minimum;
        final T max = (this.getComparator().compare(this.maximum, other.maximum) < 0) ? this.maximum : other.maximum;
        return between(min, max, this.getComparator());
    }
    
    public boolean isAfter(final T element) {
        return element != null && this.comparator.compare(element, this.minimum) < 0;
    }
    
    public boolean isAfterRange(final Range<T> otherRange) {
        return otherRange != null && this.isAfter(otherRange.maximum);
    }
    
    public boolean isBefore(final T element) {
        return element != null && this.comparator.compare(element, this.maximum) > 0;
    }
    
    public boolean isBeforeRange(final Range<T> otherRange) {
        return otherRange != null && this.isBefore(otherRange.minimum);
    }
    
    public boolean isEndedBy(final T element) {
        return element != null && this.comparator.compare(element, this.maximum) == 0;
    }
    
    public boolean isNaturalOrdering() {
        return this.comparator == ComparableComparator.INSTANCE;
    }
    
    public boolean isOverlappedBy(final Range<T> otherRange) {
        return otherRange != null && (otherRange.contains(this.minimum) || otherRange.contains(this.maximum) || this.contains(otherRange.minimum));
    }
    
    public boolean isStartedBy(final T element) {
        return element != null && this.comparator.compare(element, this.minimum) == 0;
    }
    
    public T fit(final T element) {
        Validate.notNull(element, "element", new Object[0]);
        if (this.isAfter(element)) {
            return this.minimum;
        }
        if (this.isBefore(element)) {
            return this.maximum;
        }
        return element;
    }
    
    @Override
    public String toString() {
        if (this.toString == null) {
            this.toString = "[" + this.minimum + ".." + this.maximum + "]";
        }
        return this.toString;
    }
    
    public String toString(final String format) {
        return String.format(format, this.minimum, this.maximum, this.comparator);
    }
    
    private enum ComparableComparator implements Comparator
    {
        INSTANCE;
        
        @Override
        public int compare(final Object obj1, final Object obj2) {
            return ((Comparable)obj1).compareTo(obj2);
        }
    }
}
