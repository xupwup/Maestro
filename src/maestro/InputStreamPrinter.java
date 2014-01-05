/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maestro;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rick
 */
public class InputStreamPrinter extends Thread {
    InputStream in;
    public InputStreamPrinter(InputStream in){
        this.in = in;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1){
                //System.err.print(new String(buffer, 0, read));
            }
        } catch (IOException ex) {
            Logger.getLogger(InputStreamPrinter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
