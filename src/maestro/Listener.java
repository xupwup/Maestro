package maestro;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static maestro.StreamUtils.getInt;

/**
 *
 * @author Rick
 */
public class Listener extends Thread {

    MessageHandler mhl;
    boolean exitOnClose;
    ServerSocket sock;
    boolean closed = true;
    
    
    OutputStream out;
    InputStream in;

    public Listener(ServerSocket sock, MessageHandler mhl, boolean exitOnClose) {
        this.mhl = mhl;
        this.exitOnClose = exitOnClose;
        this.sock = sock;
    }

    @Override
    public void run() {
        try {
            Socket socket = sock.accept();
            mhl.onConnect(socket);
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        closed = false;
        
        try {
            int read;
            byte[] buffer = new byte[16];
            while ((read = in.read(buffer)) != -1) {
                if (read < 16) { // make sure you read 16 bytes
                    int idx = read;
                    while (idx < buffer.length && (read = in.read(buffer, idx, buffer.length - idx)) != -1) {
                        idx += read;
                    }
                }

                int datalength = getInt(buffer, 12);
                byte[] data = null;
                if (datalength > 0) {
                    data = new byte[datalength];
                    int idx = 0;
                    while (idx < data.length && (read = in.read(data, idx, data.length - idx)) != -1) {
                        idx += read;
                    }
                }

                if(mhl.handleMessage(new Message(buffer, data), out)){
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Maestro.class.getName()).log(Level.SEVERE, null, ex);
        }
        closed = true;
        if (exitOnClose) {
            Logger.getLogger(AirMessageHandler.class.getName())
                    .log(Level.INFO, "Closing listener and terminating application. Message handler: {0}", mhl.getClass().getName());
            System.exit(0);
        }else{
            Logger.getLogger(AirMessageHandler.class.getName())
                    .log(Level.INFO, "Closing listener. Message handler: {0}", mhl.getClass().getName());
        }
    }
}
