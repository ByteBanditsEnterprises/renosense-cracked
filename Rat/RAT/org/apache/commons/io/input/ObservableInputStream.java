//Raddon On Top!

package org.apache.commons.io.input;

import java.io.*;
import org.apache.commons.io.*;
import java.util.*;

public class ObservableInputStream extends ProxyInputStream
{
    private final List<Observer> observers;
    
    public ObservableInputStream(final InputStream inputStream) {
        this(inputStream, new ArrayList<Observer>());
    }
    
    private ObservableInputStream(final InputStream inputStream, final List<Observer> observers) {
        super(inputStream);
        this.observers = observers;
    }
    
    public ObservableInputStream(final InputStream inputStream, final Observer... observers) {
        this(inputStream, Arrays.asList(observers));
    }
    
    public void add(final Observer observer) {
        this.observers.add(observer);
    }
    
    @Override
    public void close() throws IOException {
        IOException ioe = null;
        try {
            super.close();
        }
        catch (IOException e) {
            ioe = e;
        }
        if (ioe == null) {
            this.noteClosed();
        }
        else {
            this.noteError(ioe);
        }
    }
    
    public void consume() throws IOException {
        final byte[] buffer = IOUtils.byteArray();
        while (this.read(buffer) != -1) {}
    }
    
    public List<Observer> getObservers() {
        return this.observers;
    }
    
    protected void noteClosed() throws IOException {
        for (final Observer observer : this.getObservers()) {
            observer.closed();
        }
    }
    
    protected void noteDataByte(final int value) throws IOException {
        for (final Observer observer : this.getObservers()) {
            observer.data(value);
        }
    }
    
    protected void noteDataBytes(final byte[] buffer, final int offset, final int length) throws IOException {
        for (final Observer observer : this.getObservers()) {
            observer.data(buffer, offset, length);
        }
    }
    
    protected void noteError(final IOException exception) throws IOException {
        for (final Observer observer : this.getObservers()) {
            observer.error(exception);
        }
    }
    
    protected void noteFinished() throws IOException {
        for (final Observer observer : this.getObservers()) {
            observer.finished();
        }
    }
    
    private void notify(final byte[] buffer, final int offset, final int result, final IOException ioe) throws IOException {
        if (ioe != null) {
            this.noteError(ioe);
            throw ioe;
        }
        if (result == -1) {
            this.noteFinished();
        }
        else if (result > 0) {
            this.noteDataBytes(buffer, offset, result);
        }
    }
    
    @Override
    public int read() throws IOException {
        int result = 0;
        IOException ioe = null;
        try {
            result = super.read();
        }
        catch (IOException ex) {
            ioe = ex;
        }
        if (ioe != null) {
            this.noteError(ioe);
            throw ioe;
        }
        if (result == -1) {
            this.noteFinished();
        }
        else {
            this.noteDataByte(result);
        }
        return result;
    }
    
    @Override
    public int read(final byte[] buffer) throws IOException {
        int result = 0;
        IOException ioe = null;
        try {
            result = super.read(buffer);
        }
        catch (IOException ex) {
            ioe = ex;
        }
        this.notify(buffer, 0, result, ioe);
        return result;
    }
    
    @Override
    public int read(final byte[] buffer, final int offset, final int length) throws IOException {
        int result = 0;
        IOException ioe = null;
        try {
            result = super.read(buffer, offset, length);
        }
        catch (IOException ex) {
            ioe = ex;
        }
        this.notify(buffer, offset, result, ioe);
        return result;
    }
    
    public void remove(final Observer observer) {
        this.observers.remove(observer);
    }
    
    public void removeAllObservers() {
        this.observers.clear();
    }
    
    public abstract static class Observer
    {
        public void closed() throws IOException {
        }
        
        public void data(final byte[] buffer, final int offset, final int length) throws IOException {
        }
        
        public void data(final int value) throws IOException {
        }
        
        public void error(final IOException exception) throws IOException {
            throw exception;
        }
        
        public void finished() throws IOException {
        }
    }
}
