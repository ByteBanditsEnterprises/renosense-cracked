//Raddon On Top!

package com.sun.jna;

public class FunctionResultContext extends FromNativeContext
{
    private Function function;
    private Object[] args;
    
    FunctionResultContext(final Class<?> resultClass, final Function function, final Object[] args) {
        super((Class)resultClass);
        this.function = function;
        this.args = args;
    }
    
    public Function getFunction() {
        return this.function;
    }
    
    public Object[] getArguments() {
        return this.args;
    }
}
