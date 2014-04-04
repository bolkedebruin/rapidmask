/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ing.rapidmask.opencsv;

import java.util.ArrayList;

/**
 *
 * @author bolke
 */
public class BufferedHasher {
    public static final int DEFAULT_BUFFER_SIZE = 1000;
    
    protected int maxSize = DEFAULT_BUFFER_SIZE;
    
    protected ArrayList<String[]> buffer;
    protected int count = 0;
    
    protected int seqno;
    
    public BufferedHasher(int seqno) {
        buffer = new ArrayList<>(DEFAULT_BUFFER_SIZE);
        this.seqno = seqno;
    }
    
    public BufferedHasher(int seqno, int bufferSize) {
        buffer = new ArrayList<>(bufferSize);
        maxSize = bufferSize;
        this.seqno = seqno;
    }
    
    public boolean addLine(String[] line) {
        buffer.add(line);
        buffer.
        return true;
    }
}
