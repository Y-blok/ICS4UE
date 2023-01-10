import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ClientFrame.java.
 * This frame contains a panel which displays the board state
 *
 * @author Kolby Chong, Kamron Zaidi, Will Jeong
 * @version 2.0
 * @since 2019-05-01
 */

public class ClientFrame extends JFrame {
    //Class variables
    private JFrame window; //window in which the client shows
    private JPanel mainPanel; //Panel that goes on the main client frame
    private JTextPane message; //Message that displays -- mostly useless
    private boolean inGame; //Boolean variable to tell if the client is in game or not
    private int[][] fullMap; //Stored map of piece positions
    private int[][] myPieces; //Stored map of only the player's piece positions
    private double resolutionRatio; //Ratio for good resolution to actual screen resolution

    ClientFrame() {
        //Initializes client title and window
        super("Chinese Checkers - KKW Client");
        this.window = this;
    }

    /**
     * This method initializes JFrame info and displays the JFrame
     */
    public void displayClient() {
        //****Initiate variables****
        //Initiate Frame stuff
        this.setSize((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2), (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2));
        resolutionRatio = Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 1080;
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Set without border
        this.setUndecorated(true);

        //Set message and JTextPane bounds and variables
        message = new JTextPane();
        message.setBounds((int) (50 * resolutionRatio), (int) (50 * resolutionRatio), (int) (300 * resolutionRatio), (int) (40 * resolutionRatio));
        message.setBackground(new Color(0, 0, 0, 0));
        message.setBorder(BorderFactory.createEmptyBorder());
        message.setFont(new Font("Arial", Font.PLAIN, (int) (30 * resolutionRatio)));
        message.setEditable(false);
        message.setText("Connecting...");

        //configure the location and specifications of the exit button
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ExitButtonListener());
        exitButton.setBounds((int) (window.getSize().getWidth() - 200 * resolutionRatio), (int) (window.getSize().getHeight() - 50 * resolutionRatio), 150, 35);
        exitButton.setBackground(Color.decode("#8780B8"));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new Font("Arial", Font.PLAIN, (int) (25 * resolutionRatio)));

        //Create a ClientPanel
        mainPanel = new ClientPanel();

        //Add the message to the panel
        mainPanel.add(message);
        //Add the exit button to the panel
        mainPanel.add(exitButton);

        mainPanel.setLayout(null);

        //Add mainPanel to JFrame window
        window.add(mainPanel);

        //Start app
        this.requestFocusInWindow();
        this.setVisible(true);
    }

    /**
     * This method sets the client to the inGame state
     *
     * @param tf, represents true/false state of
     */
    public void setInGame(boolean tf) {
        if (tf) {
            message.setText("Waiting for input...");
        } else {
            //Not in game message if needed
        }
        inGame = tf;
    }

    /**
     * This method updates the board within the Frame, then repaints the graphics accordingly
     *
     * @param fullMap  int[][], represents the map with all positions of pieces
     * @param myPieces int[][], represents only the player's pieces
     */
    public void updateBoard(int[][] fullMap, int[][] myPieces) {
        //Set fullMap and myPieces according to parameters
        this.fullMap = fullMap;
        this.myPieces = myPieces;
        for (int i = 0; i < 10; i++) {
            fullMap[myPieces[i][0]][myPieces[i][1]] = -3; //Change all locations of myPieces to -3 to differentiate
        }
        //Repaint the components of the ClientPanel
        repaint();
    }

    /**
     * private class ClientPanel <br>
     * extends JPanel <br>
     * this class extends JPanel and draws the interface onto the screen
     */
    private class ClientPanel extends JPanel {
        /**
         * paintComponent
         * overrides the paintComponent of JComponent to draw the interface onto the screen
         *
         * @param g the Graphics object to protect
         */
        @Override
        public void paintComponent(Graphics g) {
            //required
            super.paintComponent(g);
            setDoubleBuffered(true);

            //If client is in game, paint the board with pieces according to stored array
            if (inGame) {

                for (int i = 1; i < 18; i++) {
                    for (int j = 1; j < 18; j++) {

                        //Paint each spot on the board according to empty spaces and piece spaces
                        if (fullMap[i + 8][j] == 1) { //Paint all empty spaces
                            g.setColor(Color.BLACK); //Empty colour
                            //Paint circles in location according to circle size
                            g.fillOval((int) (window.getSize().getWidth() / 2 - 100 * resolutionRatio +
                                            20 * j * resolutionRatio - 10 * i * resolutionRatio), //X-coord
                                    (int) (window.getSize().getHeight() / 2 - 190 * resolutionRatio +
                                            20 * i * resolutionRatio), //Y-coord
                                    (int) (20 * resolutionRatio), (int) (20 * resolutionRatio));
                        } else if (fullMap[i + 8][j] == -3) { //Paint player pieces
                            g.setColor(Color.BLUE); //Player colour
                            //Paint circles
                            g.fillOval((int) (window.getSize().getWidth() / 2 - 100 * resolutionRatio +
                                            20 * j * resolutionRatio - 10 * i * resolutionRatio), //X-coord
                                    (int) (window.getSize().getHeight() / 2 - 190 * resolutionRatio +
                                            20 * i * resolutionRatio), //Y-coord
                                    (int) (20 * resolutionRatio), (int) (20 * resolutionRatio));
                        } else if (fullMap[i + 8][j] == -2) { //Paint all enemy pieces
                            g.setColor(Color.RED); //Enemy colour
                            //Paint circles
                            g.fillOval((int) (window.getSize().getWidth() / 2 - 100 * resolutionRatio +
                                            20 * j * resolutionRatio - 10 * i * resolutionRatio), //X-coord
                                    (int) (window.getSize().getHeight() / 2 - 190 * resolutionRatio +
                                            20 * i * resolutionRatio), //Y-coord
                                    (int) (20 * resolutionRatio), (int) (20 * resolutionRatio));
                        }

                    }
                }
            }
        }
    }

    /**
     * private class ExitButtonListener <br>
     * implements ActionListener <br>
     * this class is used for the closing of this JFrame
     */
    private class ExitButtonListener implements ActionListener {
        /**
         * actionPerformed
         * closes this JFrame
         *
         * @param event (required ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            //dispose of this frame
            window.dispose();
        }
    }
}
