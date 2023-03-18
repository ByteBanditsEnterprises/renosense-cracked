//Raddon On Top!

package org.apache.commons.lang3.builder;

import org.apache.commons.lang3.*;
import java.util.*;

public class DiffResult<T> implements Iterable<Diff<?>>
{
    public static final String OBJECTS_SAME_STRING = "";
    private static final String DIFFERS_STRING = "differs from";
    private final List<Diff<?>> diffList;
    private final T lhs;
    private final T rhs;
    private final ToStringStyle style;
    
    DiffResult(final T lhs, final T rhs, final List<Diff<?>> diffList, final ToStringStyle style) {
        Validate.notNull(lhs, "lhs", new Object[0]);
        Validate.notNull(rhs, "rhs", new Object[0]);
        Validate.notNull(diffList, "diffList", new Object[0]);
        this.diffList = diffList;
        this.lhs = lhs;
        this.rhs = rhs;
        if (style == null) {
            this.style = ToStringStyle.DEFAULT_STYLE;
        }
        else {
            this.style = style;
        }
    }
    
    public T getLeft() {
        return this.lhs;
    }
    
    public T getRight() {
        return this.rhs;
    }
    
    public List<Diff<?>> getDiffs() {
        return Collections.unmodifiableList((List<? extends Diff<?>>)this.diffList);
    }
    
    public int getNumberOfDiffs() {
        return this.diffList.size();
    }
    
    public ToStringStyle getToStringStyle() {
        return this.style;
    }
    
    @Override
    public String toString() {
        return this.toString(this.style);
    }
    
    public String toString(final ToStringStyle style) {
        if (this.diffList.isEmpty()) {
            return "";
        }
        final ToStringBuilder lhsBuilder = new ToStringBuilder(this.lhs, style);
        final ToStringBuilder rhsBuilder = new ToStringBuilder(this.rhs, style);
        for (final Diff<?> diff : this.diffList) {
            lhsBuilder.append(diff.getFieldName(), diff.getLeft());
            rhsBuilder.append(diff.getFieldName(), diff.getRight());
        }
        return String.format("%s %s %s", lhsBuilder.build(), "differs from", rhsBuilder.build());
    }
    
    @Override
    public Iterator<Diff<?>> iterator() {
        return this.diffList.iterator();
    }
}
