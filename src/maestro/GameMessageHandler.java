package maestro;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rick
 */
public class GameMessageHandler implements MessageHandler {

    final OutputStream toAir;
    
    public GameMessageHandler(OutputStream out) {
        this.toAir = out;
    }
    
    @Override
    public boolean handleMessage(Message m, OutputStream out) throws IOException {
        if (m.type != Maestro.HEARTBEAT && m.type != Maestro.ACK) {
            Logger.getLogger(AirMessageHandler.class.getName()).log(Level.INFO, "Forwarding: ({0})", m.toString());
            synchronized (toAir) {
                toAir.write(StreamUtils.getIntBytes(16));
                toAir.write(StreamUtils.getIntBytes(1));
                toAir.write(StreamUtils.getIntBytes(m.type));
                toAir.write(StreamUtils.getIntBytes(m.datalength));
                if (m.data != null) {
                    toAir.write(m.data);
                }
                toAir.flush();
            }
        }
        if(m.type == Maestro.HEARTBEAT){
            synchronized(out){
                out.write(StreamUtils.getIntBytes(16));
                out.write(StreamUtils.getIntBytes(1));
                out.write(StreamUtils.getIntBytes(Maestro.HEARTBEAT));
                out.write(StreamUtils.getIntBytes(0));
                out.flush();
            }
        }
        if(m.type != Maestro.ACK){
            synchronized(out){
                out.write(StreamUtils.getIntBytes(16));
                out.write(StreamUtils.getIntBytes(1));
                out.write(StreamUtils.getIntBytes(Maestro.ACK));
                out.write(StreamUtils.getIntBytes(0));
                out.flush();
            }
        }
        return m.type == Maestro.GAMEEND;
    }

    @Override
    public void onConnect(Socket s) {
        Logger.getLogger(AirMessageHandler.class.getName()).log(Level.INFO, "Game connected!");
    }
}
