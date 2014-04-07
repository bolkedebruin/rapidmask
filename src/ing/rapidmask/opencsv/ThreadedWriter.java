/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ing.rapidmask.opencsv;

import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author bolke
 */
public class ThreadedWriter extends Writer {
    protected final Writer target;
    protected int bufSize = 64; // default buffer size of 64 lines
    protected volatile int threadsCount = 0;
    protected Thread creatorThread;

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    protected class ThreadStreamHolder {
        int index = 0;
        int size = 0;
        String[] buffer = new String[bufSize];
        
        public ThreadStreamHolder(int index) {
            super();
            this.index = index;
        }
        
        public void flush() throws IOException {
            if (size > 0) {
                synchronized (target) {
                    for (int i=0; i<size; i++) {
                        target.write(buffer[i]);
                    }
                    size = 0;
                }
            }
        }
        
        public void write(String line) throws IOException {
            buffer[size++] = line;
            if (size>=bufSize) 
                flush();
        }

                            
    }
    
    protected ThreadLocal<ThreadStreamHolder> threads = 
                new ThreadLocal<>();

    public ThreadedWriter(Writer target) {
        super();
        this.target = target;
        creatorThread = Thread.currentThread();
    }
    
    @Override
    public void write(int b) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void write(String line) throws IOException {
        ThreadStreamHolder sh = threads.get();
        if (sh == null) {
            synchronized (this) {
                if (threadsCount == Integer.MAX_VALUE) {
                    throw new IOException("Cannot server for more than Integer.MAX_VALUE");
                }
                
                sh = new ThreadStreamHolder(threadsCount++);
                threads.set(sh);
            }
        }
        sh.write(line);
    }
    
    @Override
    public void flush() throws IOException {    
        ThreadStreamHolder sh = threads.get();
        if (sh != null) {
            sh.flush();
        }
    }
    
    @Override
    public void close() throws IOException {
        flush();
        
        threads.remove();
        
        if (Thread.currentThread().equals(creatorThread)) 
            target.close();
    }
}
