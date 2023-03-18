//Raddon On Top!

package org.sqlite.date;

public class ExceptionUtils
{
    public static <R> R rethrow(final Throwable throwable) {
        return (R)typeErasure(throwable);
    }
    
    private static <R, T extends Throwable> R typeErasure(final Throwable throwable) throws T, Throwable {
        throw throwable;
    }
}
