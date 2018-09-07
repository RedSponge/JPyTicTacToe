package com.redsponge.tictactoeclient;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

public class Client {

    private SocketHandler connection;

    private JFrame frame;
    private JLabel playerIndicator;
    private JLabel turn;
    private String player;
    private int[] board;
    private GameButton[] buttons;
    private Thread receiver;

    public Client() {
        createGUI();
        String ip = JOptionPane.showInputDialog(frame, "Ip:");
        int port = Integer.parseInt(JOptionPane.showInputDialog(frame, "Port:"));

        try {
            connection = new SocketHandler(ip, port) {
                @Override
                public void onDataReceived(String data) {
                    super.onDataReceived(data);
                    handleInput(data);
                }
            };
        }
        catch (IOException e) {
            playerIndicator.setText("Couldn't Connect To Server!");
            return;
        }
        createBoard();
    }

    private void handleInput(String input) {
        if(input.startsWith(Constants.PLAYER_SET_START)) {
            input = input.substring(Constants.PLAYER_SET_START.length());
            if(input.equals("Full")) {
                playerIndicator.setText("Server Full!");
                connection.close();
                return;
            }
            player = input;
            playerIndicator.setText("You Are [" + player + "]");
        } else if(input.startsWith(Constants.BOARDSEND)) {
            String board = input.substring(Constants.BOARDSEND.length());
            String[] boardPieces = board.split(Constants.SPLITTER);
            for (int i = 0; i < boardPieces.length; i++) {
                this.board[i] = Integer.parseInt(boardPieces[i]);
                System.out.println(Arrays.toString(this.board));
            }
            updateBoard();
        } else if(input.startsWith(Constants.WINNER)) {
            String winner = input.substring(Constants.WINNER.length());
            String winMessage = winner + " Wins!";
            if(winner.equals("draw")) {
                winMessage = "Its A Draw!";
            }
            JOptionPane.showMessageDialog(frame, winMessage, "Breaking News!", JOptionPane.INFORMATION_MESSAGE);
            connection.close();
        }
    }

    private void createBoard() {
        board = new int[9];
    for(int i = 0; i < 9; i++) {
            board[i] = 0;
        }
        updateBoard();
    }

    private void updateBoard() {
        for (int i = 0; i < buttons.length; i++) {
            System.out.println("UPDAING");
            buttons[i].mark(board[i]);
        }
    }


    private void createGUI() {

        frame = new JFrame("");

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel info = new JPanel();

        turn = new JLabel("It is nobody's turn!");
        turn.setBorder(Util.getTitledBorder("Current Turn:"));
        turn.setFont(Constants.LABEL_FONT);

        info.add(turn, BorderLayout.EAST);
        playerIndicator = new JLabel("Determining What Will You Be...");
        playerIndicator.setBorder(Util.getTitledBorder("You Are:"));

        playerIndicator.setFont(Constants.LABEL_FONT);
        info.add(playerIndicator, BorderLayout.WEST);

        panel.add(info, BorderLayout.NORTH);

        JPanel game = new JPanel();
        game.setLayout(new GridLayout(3, 3, 20, 20));

        buttons = new GameButton[9];
        for(int i = 0; i < 9; i++) {
            GameButton b = new GameButton(i);
            game.add(b);
            buttons[i] = b;
        }

        panel.add(game, BorderLayout.CENTER);

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        frame.add(panel);

        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }

    private void sendPress(int id) {
        connection.send(Constants.BUTTON_PRESS + id);
    }

    class GameButton extends JButton {

        private int id;
        private int mark;

        public GameButton(int id) {
            this.id = id;

            this.setFont(new Font("Calibri", Font.BOLD, 32));
            addActionListener(e -> {if(!isMarked()) sendPress(id);});

            this.markX();
        }

        public void markX() {
            this.mark(Constants.X_MARK);
        }

        public void markO() {
            this.mark(Constants.O_MARK);
        }

        public void clear() {
            this.mark(0);
        }

        public void mark(int mark) {
            this.mark = mark;
            updateDisplay();
        }

        public boolean isMarked() {
            return mark != 0;
        }

        private void updateDisplay() {
//            SwingUtilities.invokeLater(() -> {
            System.out.println("mark is " + mark);
                if (this.mark == 0) this.setText("");
                if (this.mark == Constants.X_MARK) this.setText("X");
                if (this.mark == Constants.O_MARK) this.setText("O");
            //});
        }
    }
}
