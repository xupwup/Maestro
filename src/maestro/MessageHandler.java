package maestro;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author Rick
 */
public interface MessageHandler {

    public void onConnect(Socket s);

    /**
     * @param m
     * @param out
     * @return Whether or not the listener should close after this
     * @throws IOException 
     */
    public boolean handleMessage(Message m, OutputStream out) throws IOException;
    
    public void onCrash();
}
