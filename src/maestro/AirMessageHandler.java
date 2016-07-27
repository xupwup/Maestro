package maestro;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import static maestro.Maestro.*;

/**
 *
 * @author Rick
 */
public class AirMessageHandler implements MessageHandler {

    private final String exeDir;
    private Listener gameListener = null;

    public AirMessageHandler(String exeDir) {
        this.exeDir = exeDir;
    }

    @Override
    public boolean handleMessage(Message m, OutputStream out) throws IOException {
        if(m.type != ACK){ // acknowledge every non-ack message
            synchronized (out) {
                out.write(StreamUtils.getIntBytes(16));
                out.write(StreamUtils.getIntBytes(1));
                out.write(StreamUtils.getIntBytes(ACK));
                out.write(StreamUtils.getIntBytes(0));
                out.flush();
            }
        }
        if (m.type == GAMESTART) {
            String exeLoc = exeDir + "/League of Legends.exe";

            gameListener = new Listener(Maestro.socket, new GameMessageHandler(out, exeDir), false);
            gameListener.start();
            
            String command = exeLoc + " \"8393\" \"LolLauncher.exe\" \"\" \""+new String(m.data)+"\" -UseRads";
            Logger.getLogger(AirMessageHandler.class.getName()).log(Level.INFO, "command: {0}", command);
            
            Process game;
            
            if(System.getProperty("os.name").equals("Linux")){
                game = Runtime.getRuntime().exec(new String[]{
                    "wine", exeLoc, "8393", "LolLauncher.exe", "LolClient.exe", 
                    new String(m.data), "-UseRads"}, null, new File(exeDir));
            }else{
                game = Runtime.getRuntime().exec(new String[]{
                    exeLoc, "8393", "LolLauncher.exe", "LolClient.exe", 
                    new String(m.data), "-UseRads"}, null, new File(exeDir));
            }
            new InputStreamPrinter(game.getInputStream()).start();
            new InputStreamPrinter(game.getErrorStream()).start();
            
            Logger.getLogger(AirMessageHandler.class.getName()).log(Level.INFO, "Game called...");
        }else if(m.type == CHATMESSAGE_TO_GAME){
            if(gameListener != null && !gameListener.closed){
                synchronized(gameListener.out){
                    gameListener.out.write(StreamUtils.getIntBytes(16));
                    gameListener.out.write(StreamUtils.getIntBytes(1));
                    gameListener.out.write(StreamUtils.getIntBytes(m.type));
                    gameListener.out.write(StreamUtils.getIntBytes(m.datalength));
                    if (m.data != null) {
                        gameListener.out.write(m.data);
                    }
                    gameListener.out.flush();
                }
            }
        }else{
            if(m.type != ACK && m.type != HEARTBEAT){
                System.out.println("Unknown message: " + m.toString());
            }
        }
        return m.type == EXIT;
    }

    @Override
    public void onConnect(Socket s) {
        System.out.println("Starting heartbeater");
        Timer t = new Timer();
        try {
            t.schedule(new HeartBeater(s.getOutputStream()), 100, 25000);
        } catch (IOException ex) {
            Logger.getLogger(AirMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onCrash() {
        System.out.println("Air client closed unexpectedly.");
    }

    private class HeartBeater extends TimerTask {

        final OutputStream out;

        public HeartBeater(OutputStream out) {
            this.out = out;
        }

        @Override
        public void run() {
            try {
                synchronized (out) {
                    out.write(StreamUtils.getIntBytes(16));
                    out.write(StreamUtils.getIntBytes(1));
                    out.write(StreamUtils.getIntBytes(HEARTBEAT));
                    out.write(StreamUtils.getIntBytes(0));
                    out.flush();
                }
                //Logger.getLogger(AirMessageHandler.class.getName()).log(Level.INFO, "Heartbeat sent");
            } catch (IOException ex) {
                Logger.getLogger(Maestro.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
