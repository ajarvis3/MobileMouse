import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * invoked with <code> java Server numClientsToAccept </code>
 * Possibly refactor some stuff to make this more generic - extend the class and provide name, UUID, protocol, numAccept
 */
public class Server {
    private static final String UUIDString = "04c6093b00001000800000805f9b34fb";
    private final LocalDevice LOCAL_DEVICE;
    private final UUID MY_UUID;
    private final String SERVICE_NAME;
    private StreamConnectionNotifier scn;
    private List<StreamConnection> clients;
    private int numAccept;

    /**
     * Default constructor.
     * Initializes member variables.
     * @throws BluetoothStateException
     */
    public Server() throws BluetoothStateException {
        this.LOCAL_DEVICE = LocalDevice.getLocalDevice();
        this.MY_UUID = new UUID(UUIDString, false);
        this.SERVICE_NAME = "PCMobileMouse";
        this.scn = null;
        this.clients = new ArrayList<>();
    }

    /**
     * User-defined constructor
     * @param numAccept
     */
    public Server(int numAccept) throws BluetoothStateException{
        this();
        this.numAccept = numAccept;
    }

    /**
     * Initializes the server to be discoverable by other devices for one minute.
     * Creates RFCOMM connection and accepts a single client.
     */
    private void initServer() {
        try {
            LOCAL_DEVICE.setDiscoverable(DiscoveryAgent.LIAC);
            String connUrl = "btspp://localhost:" + MY_UUID.toString() + ";" + "name=" + SERVICE_NAME;
            this.scn = (StreamConnectionNotifier) Connector.open(connUrl);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Accepts clients and creates a thread to handle those requests.
     */
    private void acceptClients() throws BluetoothStateException{
        for (int numClients = 0; numClients != this.numAccept; numClients++) {
            try {
                StreamConnection scan = this.scn.acceptAndOpen();
                this.clients.add(scan);
                new MouseThread(scan).run();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (AWTException e) {
                e.printStackTrace();
            }
        }
        LOCAL_DEVICE.setDiscoverable(DiscoveryAgent.NOT_DISCOVERABLE);
    }

    /**
     * Starts the server
     * @param args the String array representing program arguments
     */
    public static void main(String[] args) {
        int numAccept = Integer.parseInt(args[0]);
        try {
            Server serve = new Server(numAccept);
            serve.initServer();
            serve.acceptClients();
        }
        catch (BluetoothStateException e) {
            e.printStackTrace();
        }
    }
}
