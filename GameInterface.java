import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GameInterface extends Remote {
    Player joinGame(String username) throws RemoteException;
    // boolean matchPlayer() throws RemoteException;
    Game getGameByUsername(String username) throws RemoteException;
    // void quitGame(String username) throws RemoteException;
    String[][] getBoard(String username) throws RemoteException;
    String checkWin(String username) throws RemoteException;
    List<String> getChatMessages(String username) throws RemoteException;
    void sendChatMessage(String username,String message) throws RemoteException;
    String makeMove(String username,int row, int col) throws RemoteException;
    void quiteGame(String username) throws RemoteException;
    Player getCurrentPlayer(String username) throws RemoteException;
    Boolean gameOver(String username) throws RemoteException;
    Player getWinner(String username) throws RemoteException;
    void playerLeave(String username) throws RemoteException;
    void removeGame(String username) throws RemoteException;
    int getcurrentPlayerRank(String username) throws RemoteException;
    int getTimerCount(String username) throws RemoteException;
    void playerPaused(String username) throws RemoteException;
    boolean getIsdraw(String username) throws RemoteException;
    boolean getPaused(String username) throws RemoteException;
}
