import java.rmi.ConnectIOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ConnectException;
import java.net.ConnectException;
import java.util.List;

public class GameClient {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("java -jar client.jar username server_ip server_port");
            System.exit(1);
        }
        String username = args[0];
        String server_ip = args[1];
        int server_port = Integer.parseInt(args[2]);
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", server_port);
            GameInterface gameImpl = (GameInterface) registry.lookup("GameServer");
            // Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //     System.out.println("Program is terminating...");
                
            // }));
            
            SwingUtilities.invokeLater(() -> {
                try {
                    new Client(gameImpl,username);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
        } catch (ConnectIOException e) {
            // Handle server not available or not responding

        }catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}

class Client extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private GameInterface gameImpl;
    private Game game;
    private Player player;
    private JTextArea chatTextArea;
    private JTextField chatTextField;

    public Client(GameInterface gameImpl,String username) throws RemoteException {
        this.gameImpl = gameImpl;
        this.player = gameImpl.joinGame(username);

        setTitle("Player: "+ player.username + "("+ player.mark + ")");
        
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a panel for separating sections
        JPanel panel = new JPanel(new BorderLayout());

        // Create a label for the "Currently Player:" section
        JLabel currentlyPlayerLabel = new JLabel("Finding Player");
        currentlyPlayerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(currentlyPlayerLabel, BorderLayout.NORTH);

        // Create a panel for displaying buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 3));
        panel.add(buttonPanel, BorderLayout.CENTER);

        // Create a label for the "Result Player:" section
        JLabel resultPlayerLabel = new JLabel("Winner Player:");
        resultPlayerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(resultPlayerLabel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        // chat feature 
        JPanel chatPanel = new JPanel(new BorderLayout());

            // Create a label for the chat section
            JLabel chatLabel = new JLabel("Chat");
            chatLabel.setHorizontalAlignment(SwingConstants.CENTER);
            chatPanel.add(chatLabel, BorderLayout.NORTH);

            // Create a text area to display chat messages
            chatTextArea = new JTextArea(10, 20);
            chatTextArea.setEditable(false);
            JScrollPane chatScrollPane = new JScrollPane(chatTextArea);
            chatPanel.add(chatScrollPane, BorderLayout.CENTER);

            // Create a text field for entering chat messages
            chatTextField = new JTextField();
            chatTextField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String message = chatTextField.getText();
                        if (game!=null && !message.isEmpty()) {
                            gameImpl.sendChatMessage(username,"Rank#" + gameImpl.getcurrentPlayerRank(username) + " " + username + ": " + message);
                            chatTextField.setText("");
                        }
                    } catch (RemoteException ex) {
                        exist();
                        ex.printStackTrace();
                    }
                }
            });
            chatPanel.add(chatTextField, BorderLayout.SOUTH);

            // Add the chat panel to the main panel
            panel.add(chatPanel, BorderLayout.EAST);


        // Quit
        // Create a "Quit" button
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    try {
                        gameImpl.playerLeave(player.username);
                        gameImpl.quiteGame(player.username);
                    } catch (RemoteException e1) {
                        // TODO Auto-generated catch block
                        exist();
                        e1.printStackTrace();
                    }
                    System.exit(0);
            }
        });
        panel.add(quitButton, BorderLayout.SOUTH);

        // Create buttons and add them to the button panel
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 40));
                buttons[i][j].addActionListener(new ButtonClickListener(i, j));
                buttonPanel.add(buttons[i][j]);
            }
        }

        setVisible(true);

        // Periodically update the board
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // still matching
                try {
                    game = gameImpl.getGameByUsername(player.username);
                } catch (RemoteException e1) {
                    // TODO Auto-generated catch block
                    exist();
                    e1.printStackTrace();
                }

                if(game==null){
                    System.out.println(username + " still matching");
            currentlyPlayerLabel.setText("Finding Player");
                }
                

                // matching success
                if(game!=null ){
                    setTitle("Player: "+ player.username + "("+ player.mark + ")");
                    try {
                        String[][] updatedBoard = gameImpl.getBoard(username);
                        updateBoard(updatedBoard);
                        currentlyPlayerLabel.setText("Rank#" + gameImpl.getcurrentPlayerRank(username) +" " + gameImpl.getCurrentPlayer(username).username + "'s turn(" + gameImpl.getCurrentPlayer(username).mark + ")\nTime count: " + gameImpl.getTimerCount(player.username));
                        List<String> chatMessages = gameImpl.getChatMessages(player.username);
                        updateChat(chatMessages);
                        String result = gameImpl.checkWin(player.username);
                        Player winner = gameImpl.getWinner(player.username);
                        if ( winner!=null) {
                            String message = "";
                            if (winner.mark.equals(player.mark)) {
                                message = "You win!";
                            }else {
                                message = "You lose.";
                            }
                            currentlyPlayerLabel.setText("Player " + winner.username + " wins");
                            showGameOverDialog(message);
                        }
                        if(result != null && result.equals("Draw")){
                            currentlyPlayerLabel.setText("Game Draw");
                            showGameOverDialog("Game Draw");
                        } 
                    } catch (RemoteException ex) {
                        exist();
                        ex.printStackTrace();
                    }
                }
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    private void showGameOverDialog(String message) throws RemoteException {
        int choice = JOptionPane.showOptionDialog(Client.this,
                message,
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Exit", "Find New Match"},
                "Exit");
        if (choice == JOptionPane.YES_OPTION) {
            gameImpl.playerLeave(player.username);
            gameImpl.removeGame(player.username);
            System.exit(0); // User chose to exit the game
        } else if (choice == JOptionPane.NO_OPTION) {
            // User chose to find a new match
            // Add the necessary code to start a new match here
            // You can reset the game or perform other actions as needed
            gameImpl.removeGame(player.username);
            player = gameImpl.joinGame(player.username);
        }
    }

    private void updateBoard(String[][] board) {
        SwingUtilities.invokeLater(() -> {
            // Update the buttons with the board state
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    buttons[i][j].setText(board[i][j]);
                }
            }
            // Update the current player label
        });
    }

    private void updateChat(List<String> messages) {
        SwingUtilities.invokeLater(() -> {
            chatTextArea.setText("");
            for (String message : messages) {
                chatTextArea.append(message + "\n");
            }
        });
    }

    public void exist(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    // Rest of your code including updateBoard and checkWin methods

    class ButtonClickListener implements ActionListener {
        private int row, col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            // System.out.println(game!= null);
            // try {
            //     System.out.println(gameImpl.getCurrentPlayer(player.username).mark.equals(player.mark));
            // } catch (RemoteException e1) {
            //     // TODO Auto-generated catch block
            //     e1.printStackTrace();
            // }
            // System.out.println(button.getText().equals(""));
            try {
                if ( game!= null && button.getText().equals("") && gameImpl.getCurrentPlayer(player.username).mark.equals(player.mark) && !gameImpl.gameOver(player.username) ) {
                    button.setText(player.mark);
                    // Send the move to the server
                    gameImpl.makeMove(player.username,row, col);
                }
            } catch (RemoteException e1) {
                exist();
                e1.printStackTrace();
            }
        }
    }
}
