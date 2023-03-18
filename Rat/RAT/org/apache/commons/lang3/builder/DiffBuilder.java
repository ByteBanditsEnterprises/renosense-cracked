//Raddon On Top!

package org.apache.commons.lang3.builder;

import org.apache.commons.lang3.*;
import java.util.*;

public class DiffBuilder<T> implements Builder<DiffResult<T>>
{
    private final List<Diff<?>> diffs;
    private final boolean objectsTriviallyEqual;
    private final T left;
    private final T right;
    private final ToStringStyle style;
    
    public DiffBuilder(final T lhs, final T rhs, final ToStringStyle style, final boolean testTriviallyEqual) {
        Validate.notNull(lhs, "lhs", new Object[0]);
        Validate.notNull(rhs, "rhs", new Object[0]);
        this.diffs = new ArrayList<Diff<?>>();
        this.left = lhs;
        this.right = rhs;
        this.style = style;
        this.objectsTriviallyEqual = (testTriviallyEqual && (lhs == rhs || lhs.equals(rhs)));
    }
    
    public DiffBuilder(final T lhs, final T rhs, final ToStringStyle style) {
        this(lhs, rhs, style, true);
    }
    
    public DiffBuilder<T> append(final String fieldName, final boolean lhs, final boolean rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (lhs != rhs) {
            this.diffs.add(new Diff<Boolean>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Boolean getLeft() {
                    return lhs;
                }
                
                public Boolean getRight() {
                    return rhs;
                }
            });
        }
        return this;
    }
    
    public DiffBuilder<T> append(final String fieldName, final boolean[] lhs, final boolean[] rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Boolean[]>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Boolean[] getLeft() {
                    return ArrayUtils.toObject(lhs);
                }
                
                public Boolean[] getRight() {
                    return ArrayUtils.toObject(rhs);
                }
            });
        }
        return this;
    }
    
    public DiffBuilder<T> append(final String fieldName, final byte lhs, final byte rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (lhs != rhs) {
            this.diffs.add(new Diff<Byte>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Byte getLeft() {
                    return lhs;
                }
                
                public Byte getRight() {
                    return rhs;
                }
            });
        }
        return this;
    }
    
    public DiffBuilder<T> append(final String fieldName, final byte[] lhs, final byte[] rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Byte[]>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Byte[] getLeft() {
                    return ArrayUtils.toObject(lhs);
                }
                
                public Byte[] getRight() {
                    return ArrayUtils.toObject(rhs);
                }
            });
        }
        return this;
    }
    
    public DiffBuilder<T> append(final String fieldName, final char lhs, final char rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (lhs != rhs) {
            this.diffs.add(new Diff<Character>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Character getLeft() {
                    return lhs;
                }
                
                public Character getRight() {
                    return rhs;
                }
            });
        }
        return this;
    }
    
    public DiffBuilder<T> append(final String fieldName, final char[] lhs, final char[] rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Character[]>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Character[] getLeft() {
                    return ArrayUtils.toObject(lhs);
                }
                
                public Character[] getRight() {
                    return ArrayUtils.toObject(rhs);
                }
            });
        }
        return this;
    }
    
    public DiffBuilder<T> append(final String fieldName, final double lhs, final double rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (Double.doubleToLongBits(lhs) != Double.doubleToLongBits(rhs)) {
            this.diffs.add(new Diff<Double>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Double getLeft() {
                    return lhs;
                }
                
                public Double getRight() {
                    return rhs;
                }
            });
        }
        return this;
    }
    
    public DiffBuilder<T> append(final String fieldName, final double[] lhs, final double[] rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Double[]>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Double[] getLeft() {
                    return ArrayUtils.toObject(lhs);
                }
                
                public Double[] getRight() {
                    return ArrayUtils.toObject(rhs);
                }
            });
        }
        return this;
    }
    
    public DiffBuilder<T> append(final String fieldName, final float lhs, final float rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (Float.floatToIntBits(lhs) != Float.floatToIntBits(rhs)) {
            this.diffs.add(new Diff<Float>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Float getLeft() {
                    return lhs;
                }
                
                public Float getRight() {
                    return rhs;
                }
            });
        }
        return this;
    }
    
    public DiffBuilder<T> append(final String fieldName, final float[] lhs, final float[] rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Float[]>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Float[] getLeft() {
                    return ArrayUtils.toObject(lhs);
                }
                
                public Float[] getRight() {
                    return ArrayUtils.toObject(rhs);
                }
            });
        }
        return this;
    }
    
    public DiffBuilder<T> append(final String fieldName, final int lhs, final int rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (lhs != rhs) {
            this.diffs.add(new Diff<Integer>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Integer getLeft() {
                    return lhs;
                }
                
                public Integer getRight() {
                    return rhs;
                }
            });
        }
        return this;
    }
    
    public DiffBuilder<T> append(final String fieldName, final int[] lhs, final int[] rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Integer[]>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Integer[] getLeft() {
                    return ArrayUtils.toObject(lhs);
                }
                
                public Integer[] getRight() {
                    return ArrayUtils.toObject(rhs);
                }
            });
        }
        return this;
    }
    
    public DiffBuilder<T> append(final String fieldName, final long lhs, final long rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (lhs != rhs) {
            this.diffs.add(new Diff<Long>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Long getLeft() {
                    return lhs;
                }
                
                public Long getRight() {
                    return rhs;
                }
            });
        }
        return this;
    }
    
    public DiffBuilder<T> append(final String fieldName, final long[] lhs, final long[] rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Long[]>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Long[] getLeft() {
                    return ArrayUtils.toObject(lhs);
                }
                
                public Long[] getRight() {
                    return ArrayUtils.toObject(rhs);
                }
            });
        }
        return this;
    }
    
    public DiffBuilder<T> append(final String fieldName, final short lhs, final short rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (lhs != rhs) {
            this.diffs.add(new Diff<Short>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Short getLeft() {
                    return lhs;
                }
                
                public Short getRight() {
                    return rhs;
                }
            });
        }
        return this;
    }
    
    public DiffBuilder<T> append(final String fieldName, final short[] lhs, final short[] rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Short[]>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Short[] getLeft() {
                    return ArrayUtils.toObject(lhs);
                }
                
                public Short[] getRight() {
                    return ArrayUtils.toObject(rhs);
                }
            });
        }
        return this;
    }
    
    public DiffBuilder<T> append(final String fieldName, final Object lhs, final Object rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        Object objectToTest;
        if (lhs != null) {
            objectToTest = lhs;
        }
        else {
            objectToTest = rhs;
        }
        if (objectToTest.getClass().isArray()) {
            if (objectToTest instanceof boolean[]) {
                return this.append(fieldName, (boolean[])lhs, (boolean[])rhs);
            }
            if (objectToTest instanceof byte[]) {
                return this.append(fieldName, (byte[])lhs, (byte[])rhs);
            }
            if (objectToTest instanceof char[]) {
                return this.append(fieldName, (char[])lhs, (char[])rhs);
            }
            if (objectToTest instanceof double[]) {
                return this.append(fieldName, (double[])lhs, (double[])rhs);
            }
            if (objectToTest instanceof float[]) {
                return this.append(fieldName, (float[])lhs, (float[])rhs);
            }
            if (objectToTest instanceof int[]) {
                return this.append(fieldName, (int[])lhs, (int[])rhs);
            }
            if (objectToTest instanceof long[]) {
                return this.append(fieldName, (long[])lhs, (long[])rhs);
            }
            if (objectToTest instanceof short[]) {
                return this.append(fieldName, (short[])lhs, (short[])rhs);
            }
            return this.append(fieldName, (Object[])lhs, (Object[])rhs);
        }
        else {
            if (lhs != null && lhs.equals(rhs)) {
                return this;
            }
            this.diffs.add(new Diff<Object>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Object getLeft() {
                    return lhs;
                }
                
                public Object getRight() {
                    return rhs;
                }
            });
            return this;
        }
    }
    
    public DiffBuilder<T> append(final String fieldName, final Object[] lhs, final Object[] rhs) {
        this.validateFieldNameNotNull(fieldName);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        if (!Arrays.equals(lhs, rhs)) {
            this.diffs.add(new Diff<Object[]>(fieldName) {
                private static final long serialVersionUID = 1L;
                
                public Object[] getLeft() {
                    return lhs;
                }
                
                public Object[] getRight() {
                    return rhs;
                }
            });
        }
        return this;
    }
    
    public DiffBuilder<T> append(final String fieldName, final DiffResult<T> diffResult) {
        this.validateFieldNameNotNull(fieldName);
        Validate.notNull(diffResult, "diffResult", new Object[0]);
        if (this.objectsTriviallyEqual) {
            return this;
        }
        for (final Diff<?> diff : diffResult.getDiffs()) {
            this.append(fieldName + "." + diff.getFieldName(), diff.getLeft(), diff.getRight());
        }
        return this;
    }
    
    public DiffResult<T> build() {
        return new DiffResult<T>(this.left, this.right, this.diffs, this.style);
    }
    
    private void validateFieldNameNotNull(final String fieldName) {
        Validate.notNull(fieldName, "fieldName", new Object[0]);
    }
}
