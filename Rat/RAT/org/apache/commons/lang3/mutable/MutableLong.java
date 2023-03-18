//Raddon On Top!

package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.*;

public class MutableLong extends Number implements Comparable<MutableLong>, Mutable<Number>
{
    private static final long serialVersionUID = 62986528375L;
    private long value;
    
    public MutableLong() {
    }
    
    public MutableLong(final long value) {
        this.value = value;
    }
    
    public MutableLong(final Number value) {
        this.value = value.longValue();
    }
    
    public MutableLong(final String value) {
        this.value = Long.parseLong(value);
    }
    
    public Long getValue() {
        return this.value;
    }
    
    public void setValue(final long value) {
        this.value = value;
    }
    
    public void setValue(final Number value) {
        this.value = value.longValue();
    }
    
    public void increment() {
        ++this.value;
    }
    
    public long getAndIncrement() {
        final long last = this.value;
        ++this.value;
        return last;
    }
    
    public long incrementAndGet() {
        return ++this.value;
    }
    
    public void decrement() {
        --this.value;
    }
    
    public long getAndDecrement() {
        final long last = this.value;
        --this.value;
        return last;
    }
    
    public long decrementAndGet() {
        return --this.value;
    }
    
    public void add(final long operand) {
        this.value += operand;
    }
    
    public void add(final Number operand) {
        this.value += operand.longValue();
    }
    
    public void subtract(final long operand) {
        this.value -= operand;
    }
    
    public void subtract(final Number operand) {
        this.value -= operand.longValue();
    }
    
    public long addAndGet(final long operand) {
        return this.value += operand;
    }
    
    public long addAndGet(final Number operand) {
        return this.value += operand.longValue();
    }
    
    public long getAndAdd(final long operand) {
        final long last = this.value;
        this.value += operand;
        return last;
    }
    
    public long getAndAdd(final Number operand) {
        final long last = this.value;
        this.value += operand.longValue();
        return last;
    }
    
    @Override
    public int intValue() {
        return (int)this.value;
    }
    
    @Override
    public long longValue() {
        return this.value;
    }
    
    @Override
    public float floatValue() {
        return (float)this.value;
    }
    
    @Override
    public double doubleValue() {
        return (double)this.value;
    }
    
    public Long toLong() {
        return this.longValue();
    }
    
    public boolean equals(final Object obj) {
        return obj instanceof MutableLong && this.value == ((MutableLong)obj).longValue();
    }
    
    public int hashCode() {
        return (int)(this.value ^ this.value >>> 32);
    }
    
    @Override
    public int compareTo(final MutableLong other) {
        return NumberUtils.compare(this.value, other.value);
    }
    
    public String toString() {
        return String.valueOf(this.value);
    }
}
