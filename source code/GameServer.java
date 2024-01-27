import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class GameServer {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar server.jar <ip> <port>");
            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Program is terminating...");
        }));

        String ipAddress = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            // Create an instance of the GameImpl class
            GameInterface game = new GameImpl();

            // Create an RMI registry on the specified IP and port
            Registry registry = LocateRegistry.createRegistry(port, null, null);

            // Bind the remote object to the registry with a name
            registry.rebind("GameServer", game);

            System.out.println("Game server is running on " + ipAddress + ":" + port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
