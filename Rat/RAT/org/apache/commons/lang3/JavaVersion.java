//Raddon On Top!

package org.apache.commons.lang3;

import org.apache.commons.lang3.math.*;

public enum JavaVersion
{
    JAVA_0_9(1.5f, "0.9"), 
    JAVA_1_1(1.1f, "1.1"), 
    JAVA_1_2(1.2f, "1.2"), 
    JAVA_1_3(1.3f, "1.3"), 
    JAVA_1_4(1.4f, "1.4"), 
    JAVA_1_5(1.5f, "1.5"), 
    JAVA_1_6(1.6f, "1.6"), 
    JAVA_1_7(1.7f, "1.7"), 
    JAVA_1_8(1.8f, "1.8"), 
    @Deprecated
    JAVA_1_9(9.0f, "9"), 
    JAVA_9(9.0f, "9"), 
    JAVA_10(10.0f, "10"), 
    JAVA_11(11.0f, "11"), 
    JAVA_12(12.0f, "12"), 
    JAVA_13(13.0f, "13"), 
    JAVA_14(14.0f, "14"), 
    JAVA_15(15.0f, "15"), 
    JAVA_16(16.0f, "16"), 
    JAVA_17(17.0f, "17"), 
    JAVA_RECENT(maxVersion(), Float.toString(maxVersion()));
    
    private final float value;
    private final String name;
    
    private JavaVersion(final float value, final String name) {
        this.value = value;
        this.name = name;
    }
    
    public boolean atLeast(final JavaVersion requiredVersion) {
        return this.value >= requiredVersion.value;
    }
    
    public boolean atMost(final JavaVersion requiredVersion) {
        return this.value <= requiredVersion.value;
    }
    
    static JavaVersion getJavaVersion(final String nom) {
        return get(nom);
    }
    
    static JavaVersion get(final String versionStr) {
        if (versionStr == null) {
            return null;
        }
        switch (versionStr) {
            case "0.9": {
                return JavaVersion.JAVA_0_9;
            }
            case "1.1": {
                return JavaVersion.JAVA_1_1;
            }
            case "1.2": {
                return JavaVersion.JAVA_1_2;
            }
            case "1.3": {
                return JavaVersion.JAVA_1_3;
            }
            case "1.4": {
                return JavaVersion.JAVA_1_4;
            }
            case "1.5": {
                return JavaVersion.JAVA_1_5;
            }
            case "1.6": {
                return JavaVersion.JAVA_1_6;
            }
            case "1.7": {
                return JavaVersion.JAVA_1_7;
            }
            case "1.8": {
                return JavaVersion.JAVA_1_8;
            }
            case "9": {
                return JavaVersion.JAVA_9;
            }
            case "10": {
                return JavaVersion.JAVA_10;
            }
            case "11": {
                return JavaVersion.JAVA_11;
            }
            case "12": {
                return JavaVersion.JAVA_12;
            }
            case "13": {
                return JavaVersion.JAVA_13;
            }
            case "14": {
                return JavaVersion.JAVA_14;
            }
            case "15": {
                return JavaVersion.JAVA_15;
            }
            case "16": {
                return JavaVersion.JAVA_16;
            }
            case "17": {
                return JavaVersion.JAVA_17;
            }
            default: {
                final float v = toFloatVersion(versionStr);
                if (v - 1.0 < 1.0) {
                    final int firstComma = Math.max(versionStr.indexOf(46), versionStr.indexOf(44));
                    final int end = Math.max(versionStr.length(), versionStr.indexOf(44, firstComma));
                    if (Float.parseFloat(versionStr.substring(firstComma + 1, end)) > 0.9f) {
                        return JavaVersion.JAVA_RECENT;
                    }
                }
                else if (v > 10.0f) {
                    return JavaVersion.JAVA_RECENT;
                }
                return null;
            }
        }
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    private static float maxVersion() {
        final float v = toFloatVersion(System.getProperty("java.specification.version", "99.0"));
        if (v > 0.0f) {
            return v;
        }
        return 99.0f;
    }
    
    private static float toFloatVersion(final String value) {
        final int defaultReturnValue = -1;
        if (!value.contains(".")) {
            return NumberUtils.toFloat(value, -1.0f);
        }
        final String[] toParse = value.split("\\.");
        if (toParse.length >= 2) {
            return NumberUtils.toFloat(toParse[0] + '.' + toParse[1], -1.0f);
        }
        return -1.0f;
    }
}
