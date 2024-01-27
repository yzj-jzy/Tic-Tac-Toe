import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Game implements Serializable {
    public Player playerX;
    public Player playerO;
    public String[][] board;
    public Player currentPlayer;
    public boolean gameOver = false;
    Player winner = null;
    Player loser = null;
    boolean isDraw = false;
    public transient Timer moveTimer;
    public transient int TimeOut = 20;
    public long turnStartTime;
    public boolean paused = false;

    public Game(Player p1, Player p2) {
        p1.isPlaying = true;
        p2.isPlaying = true;
        if (p1.mark.equals("X")) {
            playerX = p1;
            playerO = p2;
        } else {
            playerX = p2;
            playerO = p1;
        }
        this.currentPlayer = playerX;
        board = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "";
            }
        }
        moveTimer = new Timer();
        turnStartTime = System.currentTimeMillis();
        moveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Time's up, make a random move
                try {
                    makeRandomMove();
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, TimeOut * 1000); // Convert seconds to milliseconds
    }

    public Player getPlayerX(){
        return playerX;
    }

    public Player getPlayerO(){
        return playerO;
    }
    public Boolean gameOver(){
        return gameOver;
    }

    public String[][] getBoard() throws RemoteException {
        return board;
    }

    public Player getCurrentPlayer() throws RemoteException {
        return currentPlayer;
    }
    public Player getWinner() throws RemoteException{
        return winner;
    }

    public String checkWin() throws RemoteException {

        // Check rows, columns, and diagonals
        for (int i = 0; i < 3; i++) {
            // Check rows
            if (board[i][0].equals("X") && board[i][1].equals("X") && board[i][2].equals("X")) {
                winner = playerX;
                loser = playerO;
            }
            if (board[i][0].equals("O") && board[i][1].equals("O") && board[i][2].equals("O")) {
                winner = playerO;
                loser = playerX;
            }
            // Check columns
            if (board[0][i].equals("X") && board[1][i].equals("X") && board[2][i].equals("X")) {
                winner = playerX;
                loser = playerO;
            }
            if (board[0][i].equals("O") && board[1][i].equals("O") && board[2][i].equals("O")) {
                winner = playerO;
                loser = playerX;
            }
        }
        // Check diagonals
        if (board[0][0].equals("X") && board[1][1].equals("X") && board[2][2].equals("X")) {
            winner = playerX;
            loser = playerO;
        }
        if (board[0][2].equals("X") && board[1][1].equals("X") && board[2][0].equals("X")) {
            winner = playerX;
            loser = playerO;
        }
        if (board[0][0].equals("O") && board[1][1].equals("O") && board[2][2].equals("O")) {
            winner = playerO;
            loser = playerX;
        }
        if (board[0][2].equals("O") && board[1][1].equals("O") && board[2][0].equals("O")) {
            winner = playerO;
            loser = playerX;
        }

        // Check for a draw
        boolean isDraw = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].isEmpty()) {
                    isDraw = false;
                    break;
                }
            }
        }

        if (isDraw && winner==null && loser==null) {
            gameOver = true;
            this.playerX.isPlaying = false;
            this.playerO.isPlaying = false;
            return "Draw";
        }

        // No winner or draw yet
        if (winner != null) {
            gameOver = true;
            this.playerX.isPlaying = false;
            this.playerO.isPlaying = false;

            return winner.mark;
        }
        return null;
    }

    public void togglePlayer() {
        currentPlayer = (currentPlayer == playerX) ? playerO : playerX;
    }

    public Queue<String> chatQueue = new LinkedList<>();

    public void sendChatMessage(String message) throws RemoteException {
        if (chatQueue.size() >= 10) {
            chatQueue.poll(); // Remove the oldest message if the queue is full
        }
        chatQueue.offer(message); // Add the new message to the queue
    }

    public List<String> getChatMessages() throws RemoteException {
        return new ArrayList<>(chatQueue);
    }

    // make move

    public String makeMove(int row, int col) throws RemoteException {
        
        if (!gameOver && board[row][col].isEmpty()) {
            // Cancel the previous timer task (if any)
            if (moveTimer != null) {
                moveTimer.cancel();
            }

            this.board[row][col] = this.currentPlayer.mark;
            System.out.println("Player " + currentPlayer.mark + " moved to (" + row + ", " + col + ")");
            togglePlayer();
            // count 30s for against player
            // Schedule a new timer task to reset the timer
            moveTimer = new Timer();
            turnStartTime = System.currentTimeMillis();
            moveTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // Time's up, make a random move
                    try {
                        makeRandomMove();
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }, TimeOut * 1000); // Convert seconds to milliseconds
            return "success move";
        }

        return "Move not success";
    }
    public int getTimerCount() throws RemoteException{
        long currentTime = System.currentTimeMillis();
        long timeElapsed = turnStartTime - currentTime;
        long TimeLeft = this.TimeOut*1000 - timeElapsed;
        return  (int) (TimeLeft/1000) - this.TimeOut ;
    }

    public void makeRandomMove() throws RemoteException {
        Random random = new Random();
        int emptyRow, emptyCol;
        do {
            emptyRow = random.nextInt(3); // Generate a random row (0-2)
            emptyCol = random.nextInt(3); // Generate a random column (0-2)
        } while (!board[emptyRow][emptyCol].isEmpty() && !gameOver);
        makeMove(emptyRow, emptyCol);
        System.out.println("Player " + currentPlayer.mark + " made a random move to (" + emptyRow + ", " + emptyCol + ")");
    }

    void quiteGame(String username){
        this.playerO.isPlaying = false;
        this.playerX.isPlaying = false;
        gameOver = true;
        if(this.playerX.username.equals(username)){
            
            winner = this.playerO;
            loser =  this.playerX;
        }else{
            winner = this.playerX;
            loser =  this.playerO;
        }
    }


}
