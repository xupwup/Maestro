package maestro;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.Arrays;

/**
 *
 * @author Rick
 */
public class Maestro {

    /*
     * Type: 8 (MAESTROMESSAGETYPE_GAMECLIENT_LAUNCHED)
     * Type: 10 (MAESTROMESSAGETYPE_GAMECLIENT_CONNECTED_TO_SERVER)
     * Type: 5 (MAESTROMESSAGETYPE_REPLY)
     * Type: 12 (MAESTROMESSAGETYPE_CHATMESSAGE_FROM_GAME)
     * Type: 11 (MAESTROMESSAGETYPE_CHATMESSAGE_TO_GAME)
     * 
     * 
     */
    public static ServerSocket socket;
    public static final int EXIT                  = 3;
    public static final int ACK                   = 5;
    public static final int HEARTBEAT             = 4;
    public static final int GAMESTART             = 0;
    public static final int GAMEEND               = 1; // COULD BE WRONG
    
    public static final int GAMECLIENT_LAUNCHED   = 8;
    public static final int GAMECLIENT_CONNECTED  = 10;
    public static final int CHATMESSAGE_TO_GAME   = 11;
    public static final int CHATMESSAGE_FROM_GAME = 12;
    

    // game start = [16, 1, 0, 53]
    public Maestro() throws IOException {
        socket = new ServerSocket(8393);
    }

    public void run(String exeDir) throws IOException {
        Listener l = new Listener(socket, new AirMessageHandler(exeDir), true);
        l.start();
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        PrintStream ps = new PrintStream(new File("MaestoLog.txt"));
        System.setOut(ps);
        System.setErr(ps);
        String releaseDir;
        if (args.length > 0) {
            releaseDir = args[0];
        }else{
            System.out.println("missing argument 'restOfPath/lol_game_client_sln/releases' directory");
            return;
        }
        File releasesDir = new File(releaseDir);
        if (!releasesDir.exists()) {
            System.out.println("No league folder found :(");
            System.exit(1);
        }
        File[] releases = releasesDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        Arrays.sort(releases);
        if (releases.length == 0) {
            System.out.println("No league folder found :(");
        }
        new Maestro().run(releases[0].getAbsolutePath() + "/deploy");
    }
}
