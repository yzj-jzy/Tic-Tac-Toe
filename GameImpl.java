import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GameImpl extends UnicastRemoteObject implements GameInterface {

    private List<Player> allPlayers = new ArrayList<Player>();
    private List<Game> allGames = new ArrayList<Game>();
    private List<Player> MatchPairs = new ArrayList<Player>();

    public GameImpl() throws RemoteException {
        
    }

    public Player joinGame(String username) throws RemoteException {
        Boolean playerExisting = false;
        Player player = null;

        for (Player p : allPlayers) {
            if (p.username.equals(username)){
                playerExisting = true;
                player = p;
                p.active = true;
                p.isPlaying = false;
                break; 
            }
        }
        
        if(player == null){
            player = new Player(username, null);
            player.username = username;
            player.active = true;
            player.isPlaying = false;
            allPlayers.add(player);
        }

        MatchPairs.add(player);

        System.out.print("The players are: ");
        for(Player p: MatchPairs){
            System.out.print(p.username + " active: " + p.active + p.mark);
        }

        System.out.println(MatchPairs.size());
        if(MatchPairs.size() == 2){
            player.mark = "O";
            Game game = new Game(MatchPairs.get(0),MatchPairs.get(1));
            allGames.add(game);
            MatchPairs.clear();
        }else{
            player.mark = "X";
        }

        return player;
    }


    public Game getGameByUsername(String username){
        Game game = null;
        for(Game g: allGames){
            if( (g.getPlayerX().username.equals(username) || g.getPlayerO().username.equals(username))){
                game = g;
            }
        }
        return game;
    }
     
    // public Player getPlayerX(String username){
    //     Game g = getGameByUsername(username);
    //     return g.getPlayerX();
    // }

    // public Player getPlayerO(String username){
    //     Game g = getGameByUsername(username);
    //     return g.getPlayerO();
    // }

    public Player getWinner(String username) throws RemoteException{
        Game g = getGameByUsername(username);
        return g.getWinner();
    }

    public Boolean gameOver(String username) throws RemoteException{
        Game g = getGameByUsername(username);
        return g.gameOver();
    }

    public Player getCurrentPlayer(String username) throws RemoteException {
        Game g = getGameByUsername(username);
        try {
            return g.getCurrentPlayer();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void playerLeave(String username){
        for(Player p: allPlayers){
            if(p.username.equals(username)){
                p.active = false;
                System.out.print("The player  ");
                System.out.print(p.username + " exist and its active: " + p.active);
                System.out.println(" ");
            }
        }
        
    }

    public String[][] getBoard(String username) throws RemoteException{
        Game g = getGameByUsername(username);
        return g.getBoard();
    }

    public String checkWin(String username) throws RemoteException{
        Game g = getGameByUsername(username);
        return g.checkWin();
    }

    public List<String> getChatMessages(String username) throws RemoteException{
        Game g = getGameByUsername(username);
        return g.getChatMessages();
    }

    public void sendChatMessage(String username,String message) throws RemoteException{
        Game g = getGameByUsername(username);
        g.sendChatMessage(message);
    }

    public String makeMove(String username,int row, int col) throws RemoteException{
        Game g = getGameByUsername(username);
        return g.makeMove(row, col);
    }

    public void removeGame(String username) throws RemoteException{
        Game g = getGameByUsername(username);
        if(g != null){
            for(Player p: allPlayers){
                if(g.getWinner()!=null){
                    if(p.username.equals(g.getWinner().username)){
                        p.points += 5;
                    }
                    if(p.username.equals(g.loser.username)){
                        p.points -= 5;
                    }
                }
            }
            allGames.remove(g);
        }
    }

    public void quiteGame(String username) throws RemoteException{
        // 
        Game g = getGameByUsername(username);
        g.quiteGame(username);
    }

    public int getcurrentPlayerRank(String username) throws RemoteException{
        Game g = getGameByUsername(username);
        Player currentPlayer = g.getCurrentPlayer();
        List<Player> rankedPlayers = new ArrayList<>(allPlayers);
        
    
        // Sort the players by points in descending order
        rankedPlayers.sort((p1, p2) -> Integer.compare(p2.points, p1.points));
    
        int rank = 1;
        for(Player p:rankedPlayers){
            if(p.username.equals(currentPlayer.username)){
                return rank;
            }
            rank += 1;
        }
        // Player not found, return a value indicating they are not ranked
        return -1; // You can choose a different value to indicate "not ranked"
    }
    public int getTimerCount(String username) throws RemoteException{
        Game g = getGameByUsername(username);
        return g.getTimerCount();
    }

    public boolean getIsdraw(String username) throws RemoteException{
        Game g = getGameByUsername(username);
        return g.isDraw;
    }

    public boolean getPaused(String username) throws RemoteException{
        Game g = getGameByUsername(username);
        return g.paused;
    }

    private transient Timer pausedTimer;
    private transient int pausedTimeOut = 30;
    private long pausedStartTime;
    
    public void playerPaused(String username) throws RemoteException{
        Game g = getGameByUsername(username);
        g.paused = true;
        pausedTimer = new Timer();
        pausedStartTime = System.currentTimeMillis();
        pausedTimer.schedule(new TimerTask() {
        @Override
            public void run() {
                if(g.paused){
                    g.isDraw = true;
                    g.gameOver = true;
                    if(g.playerX.username.equals(username)){
                        g.playerX.isPlaying = false;
                        g.playerX.active = false;
                    }else{
                        g.playerO.isPlaying = false;
                        g.playerO.active = false;
                    }
                    
                }
                // Time's up, make a random move
            }
        }, pausedTimeOut * 1000); // Convert seconds to milliseconds
    }

    
    
}

