/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ing.rapidmask;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author bolke
 */
public class StreamHelper {
    public static OutputStream asyncOutputStream(final GZIPOutputStream out) throws IOException {
        PipedOutputStream pos = new PipedOutputStream();
        final PipedInputStream pis = new PipedInputStream(pos);
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] bytes = new byte[8129];
                    for (int len; (len = pis.read(bytes)) > 0;) {
                        out.write(bytes, 0, len);
                    }
                    out.finish();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } finally {
                    try {
                        pis.close();
                        out.close();
                    } catch (IOException ex) {
                        Logger.getLogger(StreamHelper.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }, "async-output-stream").start();
        
        return pos;
    }
}
