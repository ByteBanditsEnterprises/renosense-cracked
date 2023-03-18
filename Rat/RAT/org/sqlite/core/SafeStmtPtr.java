//Raddon On Top!

package org.sqlite.core;

import java.sql.*;

public class SafeStmtPtr
{
    private final DB db;
    private final long ptr;
    private volatile boolean closed;
    private int closedRC;
    private SQLException closeException;
    
    public SafeStmtPtr(final DB db, final long ptr) {
        this.closed = false;
        this.db = db;
        this.ptr = ptr;
    }
    
    public boolean isClosed() {
        return this.closed;
    }
    
    public int close() throws SQLException {
        synchronized (this.db) {
            return this.internalClose();
        }
    }
    
    private int internalClose() throws SQLException {
        try {
            if (!this.closed) {
                return this.closedRC = this.db.finalize(this, this.ptr);
            }
            if (this.closeException != null) {
                throw this.closeException;
            }
            return this.closedRC;
        }
        catch (SQLException ex) {
            throw this.closeException = ex;
        }
        finally {
            this.closed = true;
        }
    }
    
    public <E extends Throwable> int safeRunInt(final SafePtrIntFunction<E> run) throws SQLException, E, Throwable {
        synchronized (this.db) {
            this.ensureOpen();
            return run.run(this.db, this.ptr);
        }
    }
    
    public <E extends Throwable> long safeRunLong(final SafePtrLongFunction<E> run) throws SQLException, E, Throwable {
        synchronized (this.db) {
            this.ensureOpen();
            return run.run(this.db, this.ptr);
        }
    }
    
    public <E extends Throwable> double safeRunDouble(final SafePtrDoubleFunction<E> run) throws SQLException, E, Throwable {
        synchronized (this.db) {
            this.ensureOpen();
            return run.run(this.db, this.ptr);
        }
    }
    
    public <T, E extends Throwable> T safeRun(final SafePtrFunction<T, E> run) throws SQLException, E, Throwable {
        synchronized (this.db) {
            this.ensureOpen();
            return run.run(this.db, this.ptr);
        }
    }
    
    public <E extends Throwable> void safeRunConsume(final SafePtrConsumer<E> run) throws SQLException, E, Throwable {
        synchronized (this.db) {
            this.ensureOpen();
            run.run(this.db, this.ptr);
        }
    }
    
    private void ensureOpen() throws SQLException {
        if (this.closed) {
            throw new SQLException("stmt pointer is closed");
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final SafeStmtPtr that = (SafeStmtPtr)o;
        return this.ptr == that.ptr;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(this.ptr);
    }
    
    @FunctionalInterface
    public interface SafePtrConsumer<E extends Throwable>
    {
        void run(final DB p0, final long p1) throws E, Throwable;
    }
    
    @FunctionalInterface
    public interface SafePtrFunction<T, E extends Throwable>
    {
        T run(final DB p0, final long p1) throws E, Throwable;
    }
    
    @FunctionalInterface
    public interface SafePtrDoubleFunction<E extends Throwable>
    {
        double run(final DB p0, final long p1) throws E, Throwable;
    }
    
    @FunctionalInterface
    public interface SafePtrLongFunction<E extends Throwable>
    {
        long run(final DB p0, final long p1) throws E, Throwable;
    }
    
    @FunctionalInterface
    public interface SafePtrIntFunction<E extends Throwable>
    {
        int run(final DB p0, final long p1) throws E, Throwable;
    }
}
