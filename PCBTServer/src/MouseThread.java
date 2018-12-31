import javax.microedition.io.StreamConnection;
import java.awt.*;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 * A thread to handle requests from a client
 */
public class MouseThread extends Thread {
    private final StreamConnection sc;
    private final Robot robot;
    private final MouseProtocol mp;

    /**
     * Constructor for the MouseThread.
     * @param sc The StreamConnection from a client.
     */
    public MouseThread(StreamConnection sc) throws AWTException{
        super();
        this.sc = sc;
        this.robot = new Robot();
        this.mp = new MouseProtocol(robot);
    }

    @Override
    public void run() {
        try {
            DataInputStream dataIn = sc.openDataInputStream();
            byte[] buffer = new byte[1024];
            while (true) {
                dataIn.read(buffer);
                String data = getStringFromBytes(buffer);
                mp.handleInput(data);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a byte array to a string
     * @param buffer the byte array to be converted to a String
     * @return the String of converted bytes
     */
    private String getStringFromBytes(byte[] buffer) {
        String ret = "";
        for (int i = 0; i < buffer.length && buffer[i] != 0; i++) {
            ret += (char)buffer[i];
        }
        return ret;
    }
}
