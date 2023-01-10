import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Client.java.
 * In this version, the program looks one-zero move(s) ahead and only focuses on its own pieces
 *
 * @author Will Jeong, Kolby Chong, Kamron Zaidi
 * @version 9.0
 * @since 2019-05-09
 */

public class Client {
    //Constants, these determine scoring
    private double ADD_MOVE_VALUE = 1; //Value for adding to score
    private double SUB_MOVE_VALUE = 3; //Value for subtracting from score
    private double LEAVE_SPAWN_VALUE = 2.5; //Value for prioritizing leaving spawn zone
    private double JOIN_SPAWN_VALUE = 6; //Value for re-entering spawn zone
    private double SUCCESS_VALUE = 5; //Value for good moves
    private double FAIL_VALUE = 15; //Value for bad moves
    private double MAGNITUDE_OF_MOVE = 0.1; //Value for adjusting score magnitude

    //Timing
    private long time; //Time that is tracked for move calculation

    //Networking
    private Socket socket; //Socket to connect to
    private InputStreamReader stream; //StreamReader to get input from server
    private BufferedReader myReader; //BufferedReader to make stream better
    private PrintWriter myWriter; //PrintWriter for outputting to server

    //Board
    private int[][][] pieces = new int[6][10][2]; //piece[0][x][y] represent your own pieces, myPieces is just an easier way to access them
    private int[][] myPieces = new int[10][2]; //Coordinates of player pieces
    private int[][] goalMap = {{25, 13}, {24, 13}, {24, 12}, {23, 13}, {23, 12}, {23, 11}, {22, 13}, {22, 12}, {22, 11}, {22, 10}}; //Coordinates of the goal zone
    private int[][] spawnMap = {{9, 5}, {10, 6}, {11, 7}, {12, 8}, {10, 5}, {11, 5}, {12, 5}, {12, 6}, {12, 7}, {11, 6}}; //Coordinates of original spawn zone
    //Coordinates of other zones (zones that a move cannot end on)
    private int[][] otherMaps = {{21, 17}, {21, 16}, {21, 15}, {21, 14}, {20, 16}, {19, 15}, {18, 14}, {19, 14},
            {20, 14}, {20, 15}, {13, 13}, {13, 12}, {13, 11}, {13, 10}, {14, 13}, {15, 13}, {16, 13}, {15, 12},
            {14, 12}, {14, 11}, {21, 5}, {20, 5}, {19, 5}, {18, 5}, {21, 6}, {21, 7}, {21, 8}, {20, 6}, {19, 6},
            {20, 7}, {13, 1}, {13, 2}, {13, 3}, {13, 4}, {14, 2}, {15, 3}, {16, 4}, {15, 4}, {14, 3}, {14, 4}};
    private int endNum = 3; //End num represents how many turns it will look into the future
    private int[][] fullMap = {
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {-1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {-1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {-1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {-1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {-1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0},
            {-1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0},
            {-1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0},
            {-1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0},
            {-1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0},
            {-1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0},
            {-1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
            {-1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
            {-1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0},
            {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0},
            {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0},
            {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
    }; //Complete map of the board in ints (1 = empty spot, 0 = no spot, -1 = boundary)

    //Path variables
    private double tempScore; //Score of the temporary move that will be compared with the bestScore variable
    private ArrayList<int[]> tempPath = new ArrayList<int[]>(); //Temporary path that will be checked with best move
    private double bestScore; //Score of current best move to be compared with other moves
    private ArrayList<int[]> bestPath = new ArrayList<int[]>(); //Best move that will be sent at the end

    //Frame
    private ClientFrame myFrame; //ClientFrame to display board domestically

    /**
     * This is the main method to start the AI
     *
     * @param args String, this is the basic input
     */
    public static void main(String[] args) {
        new Client().go();
    }

    /**
     * This method is used to begin the program without having to make the class variables static
     */
    public void go() {
        myFrame = new ClientFrame();
        int[][] testMap = new int[fullMap.length][fullMap[0].length];//Used to display
        for (int i = 0; i < fullMap.length; i++) {
            for (int j = 0; j < fullMap[0].length; j++) {
                testMap[i][j] = fullMap[i][j];
            }
        }
        myFrame.updateBoard(testMap, myPieces);
        myFrame.displayClient();
        connect();
        Scanner input = new Scanner(System.in);
        int activePlayers = 0;
        int inactivePlayers = 0;
        try {
            stream = new InputStreamReader(socket.getInputStream());
            myReader = new BufferedReader(stream);
            myWriter = new PrintWriter(socket.getOutputStream());
            boolean valid = false;
            while (!valid) {
                //String roomName = input.nextLine();
                String roomName = JOptionPane.showInputDialog(null, "Enter the room name " +
                        "you want to join:", "Message", 1);
                myWriter.println("JOINROOM " + roomName);
                myWriter.flush();
                boolean escaped = false;
                while (!escaped) {
                    if (myReader.ready()) {
                        if (myReader.readLine().substring(0, 2).equals("OK")) {
                            valid = true;
                        }
                        escaped = true;
                    }
                }
            }
            valid = false;
            while (!valid) {
                String name = JOptionPane.showInputDialog(null, "Enter the name you would like:",
                        "Message", 1);
                myWriter.println("CHOOSENAME " + name);
                myWriter.flush();
                boolean escaped = false;
                while (!escaped) {
                    if (myReader.ready()) {
                        if (myReader.readLine().substring(0, 2).equals("OK")) {
                            valid = true;
                        }
                        escaped = true;
                    }
                }
            }
            myFrame.setInGame(true);
            for (int i = 0; i < fullMap.length; i++) {
                for (int j = 0; j < fullMap[0].length; j++) {
                    testMap[i][j] = fullMap[i][j];
                }
            }
            myFrame.updateBoard(testMap, myPieces);
            while ((!complete(myPieces))) {//Remove  &&(count != 45)
                if (myReader.ready()) {
                    String boardString = myReader.readLine();
                    if (boardString.substring(0, 5).equals("BOARD")) {
                        activePlayers = Integer.parseInt("" + boardString.charAt(6));
                        inactivePlayers = Integer.parseInt("" + boardString.charAt(8));
                        System.out.println(activePlayers + " " + inactivePlayers);
                        boardString = boardString.substring(9);
                        //Split string
                        String[] arrOfStr = boardString.split("\\)", -1);
                        for (int i = 0; i < arrOfStr.length - 1; i++) {//IGNORE THE LAST ONE, AS THERE IS AN UNECESSARY BRACKET
                            arrOfStr[i] = arrOfStr[i].substring(2);
                        }
                        //Reset all
                        for (int i = 0; i < 6; i++) {
                            for (int j = 0; j < 10; j++) {
                                pieces[i][j][0] = 0;
                                pieces[i][j][1] = 0;
                            }
                        }
                        //For your pieces
                        for (int i = 0; i < arrOfStr.length - 1; i++) {
                            pieces[i / 10][i % 10][0] = Integer.parseInt(arrOfStr[i].substring(0, arrOfStr[i].indexOf(",")));
                            pieces[i / 10][i % 10][1] = Integer.parseInt(arrOfStr[i].substring(arrOfStr[i].indexOf(",") + 1));
                            if (i < 10) {
                                myPieces[i % 10][0] = Integer.parseInt(arrOfStr[i].substring(0, arrOfStr[i].indexOf(",")));
                                myPieces[i % 10][1] = Integer.parseInt(arrOfStr[i].substring(arrOfStr[i].indexOf(",") + 1));
                            }
                        }
                        bestPath.clear();
                        bestScore = -100000;//Necessary at the start, makes any possible score better than it
                        resetBoard();
                        fillBoard();
                        time = System.nanoTime();
                        initializer(fullMap, -1, 0, new ArrayList<int[]>());
                        System.out.println((System.nanoTime() - time) / Math.pow(10, 9));
                    }
                    String printString = "MOVE";
                    for (int i = 0; i < bestPath.size(); i++) {
                        printString = printString + " (" + bestPath.get(i)[0] + "," + bestPath.get(i)[1] + ")";
                    }
                    boolean escape = false;
                    if (myReader.ready()) {
                        System.out.println("Server response: " + myReader.readLine());
                        escape = true;
                    } else {
                        myWriter.println(printString);
                        myWriter.flush();
                    }
                    for (int i = 0; i < 10; i++) {
                        if ((pieces[0][i][0] == bestPath.get(0)[0]) && ((pieces[0][i][1] == bestPath.get(0)[1]))) {
                            pieces[0][i][0] = bestPath.get(bestPath.size() - 1)[0];
                            pieces[0][i][1] = bestPath.get(bestPath.size() - 1)[1];
                        }
                    }
                    fullMap[bestPath.get(0)[0]][bestPath.get(0)[1]] = 1;
                    fullMap[bestPath.get(bestPath.size() - 1)[0]][bestPath.get(bestPath.size() - 1)[1]] = -2;
                    for (int i = 0; i < myPieces.length; i++) {
                        if ((bestPath.get(0)[0] == myPieces[i][0]) && (bestPath.get(0)[1] == myPieces[i][1])) {
                            myPieces[i][0] = bestPath.get(bestPath.size() - 1)[0];
                            myPieces[i][1] = bestPath.get(bestPath.size() - 1)[1];
                        }
                    }
                    for (int i = 0; i < fullMap.length; i++) {
                        for (int j = 0; j < fullMap[0].length; j++) {
                            testMap[i][j] = fullMap[i][j];
                        }
                    }
                    if ((activePlayers + inactivePlayers) <= 4) {
                        if (endNum > 0) {
                            endNum--;
                        } else {
                            endNum = 1;
                        }
                    } else {
                        endNum = 0;
                    }
                    System.out.println(bestScore);
                    myFrame.updateBoard(testMap, myPieces);
                    while (!escape) {
                        if (myReader.ready()) {
                            System.out.println("Server response: " + myReader.readLine());
                            escape = true;
                        }
                    }
                }
            }
        } catch (IOException E) {
            System.out.println("Unable to connect");
        }
    }

    /**
     * Returns a boolean based on whether or not the piece exists in the goalMap
     *
     * @param myPieces int [][], a 2D array that refers to the player's pieces
     * @return boolean, determined by whether or not the piece exists in the goalMap
     */
    public boolean complete(int[][] myPieces) {
        int count = 0;
        for (int i = 0; i < myPieces.length; i++) {
            for (int j = 0; j < goalMap.length; j++) {
                if ((myPieces[i][0] == goalMap[j][0]) && (myPieces[i][1] == goalMap[j][1])) {
                    count++;
                }
            }
        }
        if (count == 10) {
            return (true);
        } else {
            return (false);
        }
    }

    /**
     * This is the primary method to process all of the players pieces, a higher endNum means that this is called more times
     *
     * @param thisFullMap int[][], represents the current full map
     * @param turnNum     int, represents the turn the pieces are currently on
     * @param savedScore  double, represents the total score
     * @param savedPath   ArrayList of integer[], represents the path the piece took in turnNum=0
     */
    public void initializer(int[][] thisFullMap, int turnNum, double savedScore, ArrayList<int[]> savedPath) {
        int[][] newPieces = new int[myPieces.length][myPieces[0].length];
        for (int i = 0; i < myPieces.length; i++) {
            newPieces[i][0] = myPieces[i][0];
            newPieces[i][1] = myPieces[i][1];
        }
        turnNum++;//Turn 0 is the first turn,
        if (turnNum == 0) {
            SUB_MOVE_VALUE = 1;
        } else {
            SUB_MOVE_VALUE = 3;
        }
        tempScore = -100;//Necessary at the start
        int[][] newMap = new int[thisFullMap.length][thisFullMap[0].length];
        for (int i = 0; i < thisFullMap.length; i++) {
            for (int k = 0; k < thisFullMap[0].length; k++) {
                newMap[i][k] = thisFullMap[i][k];
            }
        }
        if (turnNum != 0) {//Anywhere but the beginning and end case
            if (turnNum == 1) {
                newMap[savedPath.get(0)[0]][savedPath.get(0)[1]] = 1;//Free it up
                newMap[savedPath.get(savedPath.size() - 1)[0]][savedPath.get(savedPath.size() - 1)[1]] = -2;//Fill it up with the appropriate one
                for (int i = 0; i < newPieces.length; i++) {
                    if ((savedPath.get(0)[0] == newPieces[i][0]) && (savedPath.get(0)[1] == newPieces[i][1])) {//If they equal their original positons
                        //Above, it is -1 because you want to get the previous one, right before the turnNum was added
                        newPieces[i][0] = savedPath.get(savedPath.size() - 1)[0];
                        newPieces[i][1] = savedPath.get(savedPath.size() - 1)[1];//Not sure if this would work for sure
                    }
                }
            } else {
                newMap[tempPath.get(0)[0]][tempPath.get(0)[1]] = 1;//Free it up
                newMap[tempPath.get(tempPath.size() - 1)[0]][tempPath.get(tempPath.size() - 1)[1]] = -2;//Fill it up with the appropriate one
                for (int i = 0; i < newPieces.length; i++) {
                    if ((tempPath.get(0)[0] == newPieces[i][0]) && (tempPath.get(0)[1] == newPieces[i][1])) {//If they equal their original positons
                        //Above, it is -1 because you want to get the previous one, right before the turnNum was added
                        newPieces[i][0] = tempPath.get(tempPath.size() - 1)[0];
                        newPieces[i][1] = tempPath.get(tempPath.size() - 1)[1];//Not sure if this would work for sure
                    }
                }
            }
        }
        tempPath.clear();
        prePathFinder(newMap, turnNum, savedScore, savedPath, newPieces);
    }

    /**
     * This method is used to organize each of the players pieces and then calls the recursive method pathFinder to determine their worth
     *
     * @param thisFullMap int [][], represents the current full map
     * @param turnNum     int, represents the current turn
     * @param savedScore  double, represents the saved score
     * @param savedPath   ArrayList of integer [], represents the path saved when turnNum=0
     * @param thesePieces int[][], represents the players pieces
     */
    public void prePathFinder(int[][] thisFullMap, int turnNum, double savedScore, ArrayList<int[]> savedPath, int[][] thesePieces) {
        for (int i = 0; i < thesePieces.length; i++) {
            ArrayList<int[]> currentPath = new ArrayList<int[]>();
            int[] firstCoords = {thesePieces[i][0], thesePieces[i][1]};
            currentPath.add(firstCoords);
            //The following lines temporarily save the score and the path to see if the next one is better
            pathFinder(thisFullMap, turnNum, savedScore, savedPath, firstCoords, currentPath, 0);
        }
    }

    /**
     * The main recursive method used to sort, store, and find the best paths
     *
     * @param thisFullMap  int[][], represents the best map at the moment
     * @param turnNum      int, represents the current turn
     * @param savedScore   double, represents the score saved at turnNum=0
     * @param savedPath    ArrayList of int[], represents the path saved at turnNum=0
     * @param rc           int[], represents the piece's current position
     * @param currentPath  ArrayList int[], represents the current path of the piece
     * @param currentScore double, represents the current score of the piece
     */
    public void pathFinder(int[][] thisFullMap, int turnNum, double savedScore, ArrayList<int[]> savedPath, int[] rc, ArrayList<int[]> currentPath, double currentScore) {
        //At the end of each base, see if the currentScore is better than the tempScore and replace if this is the case
        //However, this is only if the turnNum is not 0
        //This will make it so that when the pathFinders are all done, the best score and map will be stored as tempScore
        //Basic set up below
        double adjustedAdd = ADD_MOVE_VALUE + (25 - rc[0]) * MAGNITUDE_OF_MOVE;
        double adjustedSub = SUB_MOVE_VALUE + (25 - rc[0]) * MAGNITUDE_OF_MOVE;
        if (((System.nanoTime() - time) / Math.pow(10, 9)) < 9.5) {
            boolean originallyOnSpawn = checkOnSpawn(rc);
            boolean originallyOnGoal = checkOnGoal(rc);
            if (currentPath.size() == 1) {
                //Scan top right
                if (rc[0] > 0) {//Check for bounds
                    if (thisFullMap[rc[0] - 1][rc[1]] > 0) {
                        double newScore = currentScore;
                        newScore -= adjustedSub;
                        int[] walkedCoords = {rc[0] - 1, rc[1]};
                        if ((!originallyOnGoal) && (checkOnGoal(walkedCoords))) {
                            newScore += SUCCESS_VALUE;
                        } else if ((originallyOnGoal) && (!checkOnGoal(walkedCoords))) {
                            newScore -= FAIL_VALUE;
                        }
                        if (checkOnSpawn(walkedCoords)) {
                            newScore -= JOIN_SPAWN_VALUE;
                        }//Turn has ended, account for base case
                        if ((turnNum == 0) && (endNum != 0)) {
                            if (!(illegalGoal(walkedCoords))) {
                                savedScore = newScore;
                                currentPath.add(walkedCoords);
                                if (endGame(currentPath.get(0), walkedCoords)) {
                                    savedScore += 1000;
                                }
                                initializer(thisFullMap, turnNum, savedScore, currentPath);
                                currentPath.remove(1);
                            }
                        } else if (turnNum == endNum) {
                            if (!illegalGoal(walkedCoords)) {
                                if (newScore + savedScore > bestScore) {
                                    bestScore = newScore + savedScore;
                                    bestPath.clear();
                                    if (endNum == 0) {
                                        bestPath.add(currentPath.get(0));
                                        bestPath.add(walkedCoords);
                                    } else {
                                        for (int i = 0; i < savedPath.size(); i++) {
                                            bestPath.add(savedPath.get(i));
                                        }
                                    }
                                }
                            }
                        } else {
                            savedScore = currentScore + savedScore;
                            tempPath.clear();
                            for (int i = 0; i < currentPath.size(); i++) {
                                tempPath.add(currentPath.get(i));
                            }
                            tempPath.add(walkedCoords);
                            initializer(thisFullMap, turnNum, savedScore, savedPath);
                        }

                    }
                }

                //Scan top left
                if ((rc[0] > 0) && (rc[1] > 0)) {
                    if (thisFullMap[rc[0] - 1][rc[1] - 1] > 0) {
                        double newScore = currentScore;
                        newScore -= adjustedSub;
                        int[] walkedCoords = {rc[0] - 1, rc[1] - 1};
                        if ((!originallyOnGoal) && (checkOnGoal(walkedCoords))) {
                            newScore += SUCCESS_VALUE;
                        } else if ((originallyOnGoal) && (!checkOnGoal(walkedCoords))) {
                            newScore -= FAIL_VALUE;
                        }
                        if (checkOnSpawn(walkedCoords)) {
                            newScore -= JOIN_SPAWN_VALUE;
                        }//Turn has ended, account for base case
                        if ((turnNum == 0) && (endNum != 0)) {
                            if (!(illegalGoal(walkedCoords))) {
                                savedScore = newScore;
                                currentPath.add(walkedCoords);
                                if (endGame(currentPath.get(0), walkedCoords)) {
                                    savedScore += 1000;
                                }
                                initializer(thisFullMap, turnNum, savedScore, currentPath);
                                currentPath.remove(1);
                            }
                        } else if (turnNum == endNum) {
                            if (!illegalGoal(walkedCoords)) {
                                if (newScore + savedScore > bestScore) {
                                    bestScore = newScore + savedScore;
                                    bestPath.clear();
                                    if (endNum == 0) {
                                        bestPath.add(currentPath.get(0));
                                        bestPath.add(walkedCoords);
                                    } else {
                                        for (int i = 0; i < savedPath.size(); i++) {
                                            bestPath.add(savedPath.get(i));
                                        }
                                    }
                                }
                            }
                        } else {
                            savedScore = newScore + savedScore;
                            tempPath.clear();
                            for (int i = 0; i < currentPath.size(); i++) {
                                tempPath.add(currentPath.get(i));
                            }
                            tempPath.add(walkedCoords);
                            initializer(thisFullMap, turnNum, savedScore, savedPath);
                        }

                    }
                }

                //Scan right
                if (rc[1] < thisFullMap[0].length - 1) {
                    if (thisFullMap[rc[0]][rc[1] + 1] > 0) {
                        double newScore = currentScore;
                        int[] walkedCoords = {rc[0], rc[1] + 1};
                        if ((!originallyOnGoal) && (checkOnGoal(walkedCoords))) {
                            newScore += SUCCESS_VALUE;
                        } else if ((originallyOnGoal) && (!checkOnGoal(walkedCoords))) {
                            newScore -= FAIL_VALUE;
                        }//Turn has ended, account for base case
                        if ((turnNum == 0) && (endNum != 0)) {
                            if (!(illegalGoal(walkedCoords))) {
                                savedScore = newScore;
                                currentPath.add(walkedCoords);
                                if (endGame(currentPath.get(0), walkedCoords)) {
                                    savedScore += 1000;
                                }
                                initializer(thisFullMap, turnNum, savedScore, currentPath);
                                currentPath.remove(1);
                            }
                        } else if (turnNum == endNum) {
                            if (!illegalGoal(walkedCoords)) {
                                if (newScore + savedScore > bestScore) {
                                    bestScore = newScore + savedScore;
                                    bestPath.clear();
                                    if (endNum == 0) {
                                        bestPath.add(currentPath.get(0));
                                        bestPath.add(walkedCoords);
                                    } else {
                                        for (int i = 0; i < savedPath.size(); i++) {
                                            bestPath.add(savedPath.get(i));
                                        }
                                    }
                                }
                            }
                        } else {
                            savedScore = newScore + savedScore;
                            tempPath.clear();
                            for (int i = 0; i < currentPath.size(); i++) {
                                tempPath.add(currentPath.get(i));
                            }
                            tempPath.add(walkedCoords);
                            initializer(thisFullMap, turnNum, savedScore, savedPath);
                        }

                    }
                }

                //Scan left
                if (rc[1] > 0) {
                    if (thisFullMap[rc[0]][rc[1] - 1] > 0) {
                        double newScore = currentScore;
                        int[] walkedCoords = {rc[0], rc[1] - 1};
                        if ((!originallyOnGoal) && (checkOnGoal(walkedCoords))) {
                            newScore += SUCCESS_VALUE;
                        } else if ((originallyOnGoal) && (!checkOnGoal(walkedCoords))) {
                            newScore -= FAIL_VALUE;
                        }//Turn has ended, account for base case
                        if ((turnNum == 0) && (endNum != 0)) {
                            if (!(illegalGoal(walkedCoords))) {
                                savedScore = newScore;
                                currentPath.add(walkedCoords);
                                if (endGame(currentPath.get(0), walkedCoords)) {
                                    savedScore += 1000;
                                }
                                initializer(thisFullMap, turnNum, savedScore, currentPath);
                                currentPath.remove(1);
                            }
                        } else if (turnNum == endNum) {
                            if (!illegalGoal(walkedCoords)) {
                                if (newScore + savedScore > bestScore) {
                                    bestScore = newScore + savedScore;
                                    bestPath.clear();
                                    if (endNum == 0) {
                                        bestPath.add(currentPath.get(0));
                                        bestPath.add(walkedCoords);
                                    } else {
                                        for (int i = 0; i < savedPath.size(); i++) {
                                            bestPath.add(savedPath.get(i));
                                        }
                                    }
                                }
                            }
                        } else {
                            savedScore = newScore + savedScore;
                            tempPath.clear();
                            for (int i = 0; i < currentPath.size(); i++) {
                                tempPath.add(currentPath.get(i));
                            }
                            tempPath.add(walkedCoords);
                            initializer(thisFullMap, turnNum, savedScore, savedPath);
                        }
                    }
                }

                //Scan bot right
                if ((rc[0] < thisFullMap.length - 1) && (rc[1] < thisFullMap[0].length - 1)) {
                    if (thisFullMap[rc[0] + 1][rc[1] + 1] > 0) {
                        double newScore = currentScore;
                        newScore += adjustedAdd;
                        int[] walkedCoords = {rc[0] + 1, rc[1] + 1};
                        if ((!originallyOnGoal) && (checkOnGoal(walkedCoords))) {
                            newScore += SUCCESS_VALUE;
                        } else if ((originallyOnGoal) && (!checkOnGoal(walkedCoords))) {
                            newScore -= FAIL_VALUE;
                        }
                        if (originallyOnSpawn) {
                            newScore += LEAVE_SPAWN_VALUE;
                        }//Turn has ended, account for base case
                        if ((turnNum == 0) && (endNum != 0)) {
                            if (!(illegalGoal(walkedCoords))) {
                                savedScore = newScore;
                                currentPath.add(walkedCoords);
                                if (endGame(currentPath.get(0), walkedCoords)) {
                                    savedScore += 1000;
                                }
                                initializer(thisFullMap, turnNum, savedScore, currentPath);
                                currentPath.remove(1);
                            }
                        } else if (turnNum == endNum) {
                            if (!illegalGoal(walkedCoords)) {
                                if (newScore + savedScore > bestScore) {
                                    bestScore = newScore + savedScore;
                                    bestPath.clear();
                                    if (endNum == 0) {
                                        bestPath.add(currentPath.get(0));
                                        bestPath.add(walkedCoords);
                                    } else {
                                        for (int i = 0; i < savedPath.size(); i++) {
                                            bestPath.add(savedPath.get(i));
                                        }
                                    }
                                }
                            }
                        } else {
                            savedScore = newScore + savedScore;
                            tempPath.clear();
                            for (int i = 0; i < currentPath.size(); i++) {
                                tempPath.add(currentPath.get(i));
                            }
                            tempPath.add(walkedCoords);
                            initializer(thisFullMap, turnNum, savedScore, savedPath);
                        }

                    }
                }

                //Scan bot left
                if (rc[0] < thisFullMap.length - 1) {
                    if (thisFullMap[rc[0] + 1][rc[1]] > 0) {
                        double newScore = currentScore;
                        newScore += adjustedAdd;
                        int[] walkedCoords = {rc[0] + 1, rc[1]};
                        if ((!originallyOnGoal) && (checkOnGoal(walkedCoords))) {
                            newScore += SUCCESS_VALUE;
                        } else if ((originallyOnGoal) && (!checkOnGoal(walkedCoords))) {
                            newScore -= FAIL_VALUE;
                        }
                        if (originallyOnSpawn) {
                            newScore += LEAVE_SPAWN_VALUE;
                        }//Turn has ended, account for base case
                        if ((turnNum == 0) && (endNum != 0)) {
                            if (!(illegalGoal(walkedCoords))) {
                                savedScore = newScore;
                                currentPath.add(walkedCoords);
                                if (endGame(currentPath.get(0), walkedCoords)) {
                                    savedScore += 1000;
                                }
                                initializer(thisFullMap, turnNum, savedScore, currentPath);
                                currentPath.remove(1);
                            }
                        } else if (turnNum == endNum) {
                            if (!illegalGoal(walkedCoords)) {
                                if (newScore + savedScore > bestScore) {
                                    bestScore = newScore + savedScore;
                                    bestPath.clear();
                                    if (endNum == 0) {
                                        bestPath.add(currentPath.get(0));
                                        bestPath.add(walkedCoords);
                                    } else {
                                        for (int i = 0; i < savedPath.size(); i++) {
                                            bestPath.add(savedPath.get(i));
                                        }
                                    }
                                }
                            }
                        } else {
                            savedScore = newScore + savedScore;
                            tempPath.clear();
                            for (int i = 0; i < currentPath.size(); i++) {
                                tempPath.add(currentPath.get(i));
                            }
                            tempPath.add(walkedCoords);
                            initializer(thisFullMap, turnNum, savedScore, savedPath);
                        }
                    }
                }
            }

            ///////////////////////////Begin the recursive cases///////////////////////////
            //Scan top right x 2
            if (rc[0] > 1) {
                if ((thisFullMap[rc[0] - 2][rc[1]] > 0) && (thisFullMap[rc[0] - 1][rc[1]] == -2) && (!checkDuplicate(currentPath, rc[0] - 2, rc[1]))) {
                    double newScore = currentScore;//Created so that there is no need to reset the current score
                    //  ArrayList<int[]> newPath = new ArrayList<int[]>();//created so that a new object does not interfere with older ones, and so that there is no need to reset
                    newScore -= adjustedSub * 2;
                    int[] walkedCoords = {rc[0] - 2, rc[1]};
                    if ((!originallyOnGoal) && (checkOnGoal(walkedCoords))) {
                        newScore += SUCCESS_VALUE;
                    } else if ((originallyOnGoal) && (!checkOnGoal(walkedCoords))) {
                        newScore -= FAIL_VALUE;
                    }
                    if (checkOnSpawn(walkedCoords)) {
                        newScore -= JOIN_SPAWN_VALUE * 2;
                    }
                    currentPath.add(walkedCoords);
                    pathFinder(thisFullMap, turnNum, savedScore, savedPath, walkedCoords, currentPath, newScore);
                    if ((turnNum == 0) && (endNum != 0)) {
                        savedScore = newScore;
                        if (endGame(currentPath.get(0), currentPath.get(currentPath.size() - 1))) {
                            savedScore += 1000;
                        }
                        if (!(illegalGoal(currentPath.get(currentPath.size() - 1)))) {
                            initializer(thisFullMap, turnNum, savedScore, currentPath);
                        }
                    } else if (turnNum == endNum) {
                        if (!(illegalGoal(currentPath.get(currentPath.size() - 1)))) {
                            if (newScore + savedScore > bestScore) {
                                bestScore = newScore + savedScore;
                                bestPath.clear();
                                if (endNum == 0) {
                                    for (int i = 0; i < currentPath.size(); i++) {
                                        bestPath.add(currentPath.get(i));
                                    }
                                } else {
                                    for (int i = 0; i < savedPath.size(); i++) {
                                        bestPath.add(savedPath.get(i));
                                    }
                                }
                            }
                        }
                    } else {
                        savedScore = newScore + savedScore;
                        tempPath.clear();
                        for (int i = 0; i < currentPath.size(); i++) {
                            tempPath.add(currentPath.get(i));
                        }
                        tempPath.add(walkedCoords);
                        initializer(thisFullMap, turnNum, savedScore, savedPath);
                    }
                    currentPath.remove(currentPath.size() - 1);
                }
            }
            //Scan top left x 2
            if ((rc[0] > 1) && (rc[1] > 1)) {
                if ((thisFullMap[rc[0] - 2][rc[1] - 2] > 0) && (thisFullMap[rc[0] - 1][rc[1] - 1] == -2) && (!checkDuplicate(currentPath, rc[0] - 2, rc[1] - 2))) {
                    double newScore = currentScore;//Created so that there is no need to reset the current score
                    newScore -= adjustedSub * 2;
                    int[] walkedCoords = {rc[0] - 2, rc[1] - 2};
                    if ((!originallyOnGoal) && (checkOnGoal(walkedCoords))) {
                        newScore += SUCCESS_VALUE;
                    } else if ((originallyOnGoal) && (!checkOnGoal(walkedCoords))) {
                        newScore -= FAIL_VALUE;
                    }
                    if (checkOnSpawn(walkedCoords)) {
                        newScore -= JOIN_SPAWN_VALUE * 2;
                    }
                    currentPath.add(walkedCoords);
                    pathFinder(thisFullMap, turnNum, savedScore, savedPath, walkedCoords, currentPath, newScore);
                    if ((turnNum == 0) && (endNum != 0)) {
                        savedScore = newScore;
                        if (endGame(currentPath.get(0), currentPath.get(currentPath.size() - 1))) {
                            savedScore += 1000;
                        }
                        if (!(illegalGoal(currentPath.get(currentPath.size() - 1)))) {
                            initializer(thisFullMap, turnNum, savedScore, currentPath);
                        }
                    } else if (turnNum == endNum) {
                        if (!(illegalGoal(currentPath.get(currentPath.size() - 1)))) {
                            if (newScore + savedScore > bestScore) {
                                bestScore = newScore + savedScore;
                                bestPath.clear();
                                if (endNum == 0) {
                                    for (int i = 0; i < currentPath.size(); i++) {
                                        bestPath.add(currentPath.get(i));
                                    }
                                } else {
                                    for (int i = 0; i < savedPath.size(); i++) {
                                        bestPath.add(savedPath.get(i));
                                    }
                                }
                            }
                        }
                    } else {
                        savedScore = newScore + savedScore;
                        tempPath.clear();
                        for (int i = 0; i < currentPath.size(); i++) {
                            tempPath.add(currentPath.get(i));
                        }
                        tempPath.add(walkedCoords);
                        initializer(thisFullMap, turnNum, savedScore, savedPath);
                    }
                    currentPath.remove(currentPath.size() - 1);
                }
            }
            //Scan right x 2
            if (rc[1] < thisFullMap[0].length - 2) {
                if ((thisFullMap[rc[0]][rc[1] + 2] > 0) && (thisFullMap[rc[0]][rc[1] + 1] == -2) && (!checkDuplicate(currentPath, rc[0], rc[1] + 2))) {
                    double newScore = currentScore;//Created so that there is no need to reset the current score
                    int[] walkedCoords = {rc[0], rc[1] + 2};
                    if ((!originallyOnGoal) && (checkOnGoal(walkedCoords))) {
                        newScore += SUCCESS_VALUE;
                    } else if ((originallyOnGoal) && (!checkOnGoal(walkedCoords))) {
                        newScore -= FAIL_VALUE;
                    }
                    currentPath.add(walkedCoords);
                    pathFinder(thisFullMap, turnNum, savedScore, savedPath, walkedCoords, currentPath, newScore);
                    if ((turnNum == 0) && (endNum != 0)) {
                        savedScore = newScore;
                        if (endGame(currentPath.get(0), currentPath.get(currentPath.size() - 1))) {
                            savedScore += 1000;
                        }
                        if (!(illegalGoal(currentPath.get(currentPath.size() - 1)))) {
                            initializer(thisFullMap, turnNum, savedScore, currentPath);
                        }
                    } else if (turnNum == endNum) {
                        if (!(illegalGoal(currentPath.get(currentPath.size() - 1)))) {
                            if (newScore + savedScore > bestScore) {
                                bestScore = newScore + savedScore;
                                bestPath.clear();
                                if (endNum == 0) {
                                    for (int i = 0; i < currentPath.size(); i++) {
                                        bestPath.add(currentPath.get(i));
                                    }
                                } else {
                                    for (int i = 0; i < savedPath.size(); i++) {
                                        bestPath.add(savedPath.get(i));
                                    }
                                }
                            }
                        }
                    } else {
                        savedScore = newScore + savedScore;
                        tempPath.clear();
                        for (int i = 0; i < currentPath.size(); i++) {
                            tempPath.add(currentPath.get(i));
                        }
                        tempPath.add(walkedCoords);
                        initializer(thisFullMap, turnNum, savedScore, savedPath);
                    }
                    currentPath.remove(currentPath.size() - 1);
                }
            }
            //Scan left x 2
            if (rc[1] > 1) {
                if ((thisFullMap[rc[0]][rc[1] - 2] > 0) && (thisFullMap[rc[0]][rc[1] - 1] == -2) && (!checkDuplicate(currentPath, rc[0], rc[1] - 2))) {
                    double newScore = currentScore;//Created so that there is no need to reset the current score
                    int[] walkedCoords = {rc[0], rc[1] - 2};
                    if ((!originallyOnGoal) && (checkOnGoal(walkedCoords))) {
                        newScore += SUCCESS_VALUE;
                    } else if ((originallyOnGoal) && (!checkOnGoal(walkedCoords))) {
                        newScore -= FAIL_VALUE;
                    }
                    currentPath.add(walkedCoords);
                    pathFinder(thisFullMap, turnNum, savedScore, savedPath, walkedCoords, currentPath, newScore);
                    if ((turnNum == 0) && (endNum != 0)) {
                        savedScore = newScore;
                        if (endGame(currentPath.get(0), currentPath.get(currentPath.size() - 1))) {
                            savedScore += 1000;
                        }
                        if (!(illegalGoal(currentPath.get(currentPath.size() - 1)))) {
                            initializer(thisFullMap, turnNum, savedScore, currentPath);
                        }
                    } else if (turnNum == endNum) {
                        if (!(illegalGoal(currentPath.get(currentPath.size() - 1)))) {
                            if (newScore + savedScore > bestScore) {
                                bestScore = newScore + savedScore;
                                bestPath.clear();
                                if (endNum == 0) {
                                    for (int i = 0; i < currentPath.size(); i++) {
                                        bestPath.add(currentPath.get(i));
                                    }
                                } else {
                                    for (int i = 0; i < savedPath.size(); i++) {
                                        bestPath.add(savedPath.get(i));
                                    }
                                }
                            }
                        }
                    } else {
                        savedScore = newScore + savedScore;
                        tempPath.clear();
                        for (int i = 0; i < currentPath.size(); i++) {
                            tempPath.add(currentPath.get(i));
                        }
                        tempPath.add(walkedCoords);
                        initializer(thisFullMap, turnNum, savedScore, savedPath);
                    }
                    currentPath.remove(currentPath.size() - 1);
                }
            }
            //Scan bot right x 2
            if ((rc[0] < thisFullMap.length - 2) && (rc[1] < thisFullMap[0].length - 2)) {
                if ((thisFullMap[rc[0] + 2][rc[1] + 2] > 0) && (thisFullMap[rc[0] + 1][rc[1] + 1] == -2) && (!checkDuplicate(currentPath, rc[0] + 2, rc[1] + 2))) {
                    double newScore = currentScore;//Created so that there is no need to reset the current score
                    newScore += adjustedAdd * 2;
                    int[] walkedCoords = {rc[0] + 2, rc[1] + 2};
                    if ((!originallyOnGoal) && (checkOnGoal(walkedCoords))) {
                        newScore += SUCCESS_VALUE;
                    } else if ((originallyOnGoal) && (!checkOnGoal(walkedCoords))) {
                        newScore -= FAIL_VALUE;
                    }
                    if (originallyOnSpawn) {
                        newScore += LEAVE_SPAWN_VALUE * 2;
                    }
                    currentPath.add(walkedCoords);
                    pathFinder(thisFullMap, turnNum, savedScore, savedPath, walkedCoords, currentPath, newScore);
                    if ((turnNum == 0) && (endNum != 0)) {
                        savedScore = newScore;
                        if (endGame(currentPath.get(0), currentPath.get(currentPath.size() - 1))) {
                            savedScore += 1000;
                        }
                        if (!(illegalGoal(currentPath.get(currentPath.size() - 1)))) {
                            initializer(thisFullMap, turnNum, savedScore, currentPath);
                        }
                    } else if (turnNum == endNum) {
                        if (!(illegalGoal(currentPath.get(currentPath.size() - 1)))) {
                            if (newScore + savedScore > bestScore) {
                                bestScore = newScore + savedScore;
                                bestPath.clear();
                                if (endNum == 0) {
                                    for (int i = 0; i < currentPath.size(); i++) {
                                        bestPath.add(currentPath.get(i));
                                    }
                                } else {
                                    for (int i = 0; i < savedPath.size(); i++) {
                                        bestPath.add(savedPath.get(i));
                                    }
                                }
                            }
                        }
                    } else {
                        savedScore = newScore + savedScore;
                        tempPath.clear();
                        for (int i = 0; i < currentPath.size(); i++) {
                            tempPath.add(currentPath.get(i));
                        }
                        tempPath.add(walkedCoords);
                        initializer(thisFullMap, turnNum, savedScore, savedPath);
                    }
                    currentPath.remove(currentPath.size() - 1);
                }
            }
            //Scan bot left x 2
            if (rc[0] < thisFullMap.length - 2) {
                if ((thisFullMap[rc[0] + 2][rc[1]] > 0) && (thisFullMap[rc[0] + 1][rc[1]] == -2) && (!checkDuplicate(currentPath, rc[0] + 2, rc[1]))) {
                    double newScore = currentScore;//Created so that there is no need to reset the current score
                    newScore += adjustedAdd * 2;
                    int[] walkedCoords = {rc[0] + 2, rc[1]};
                    if ((!originallyOnGoal) && (checkOnGoal(walkedCoords))) {
                        newScore += SUCCESS_VALUE;
                    } else if ((originallyOnGoal) && (!checkOnGoal(walkedCoords))) {
                        newScore -= FAIL_VALUE;
                    }
                    if (originallyOnSpawn) {
                        newScore += LEAVE_SPAWN_VALUE * 2;
                    }
                    currentPath.add(walkedCoords);
                    pathFinder(thisFullMap, turnNum, savedScore, savedPath, walkedCoords, currentPath, newScore);
                    if ((turnNum == 0) && (endNum != 0)) {
                        savedScore = newScore;
                        if (endGame(currentPath.get(0), currentPath.get(currentPath.size() - 1))) {
                            savedScore += 1000;
                        }
                        if (!(illegalGoal(currentPath.get(currentPath.size() - 1)))) {
                            initializer(thisFullMap, turnNum, savedScore, currentPath);
                        }
                    } else if (turnNum == endNum) {
                        if (!(illegalGoal(currentPath.get(currentPath.size() - 1)))) {
                            if (newScore + savedScore > bestScore) {
                                bestScore = newScore + savedScore;
                                bestPath.clear();
                                if (endNum == 0) {
                                    for (int i = 0; i < currentPath.size(); i++) {
                                        bestPath.add(currentPath.get(i));
                                    }
                                } else {
                                    for (int i = 0; i < savedPath.size(); i++) {
                                        bestPath.add(savedPath.get(i));
                                    }
                                }
                            }
                        }
                    } else {
                        savedScore = newScore + savedScore;
                        tempPath.clear();
                        for (int i = 0; i < currentPath.size(); i++) {
                            tempPath.add(currentPath.get(i));
                        }
                        tempPath.add(walkedCoords);
                        initializer(thisFullMap, turnNum, savedScore, savedPath);
                    }
                    currentPath.remove(currentPath.size() - 1);
                }
            }
        }
    }

    /**
     * This method determines if the next move can end the game
     *
     * @param start int[], represents the initial position in the path
     * @param end   int[], represents the final position in the path
     * @return boolean, returns true if the game can be ended in one turn
     */
    public boolean endGame(int[] start, int[] end) {
        int[][] myTempPieces = new int[myPieces.length][2];
        for (int i = 0; i < myPieces.length; i++) {
            if ((myPieces[i][0] == start[0]) && (myPieces[i][1] == start[1])) {
                myTempPieces[i][0] = end[0];
                myTempPieces[i][1] = end[1];
            } else {
                myTempPieces[i][0] = myPieces[i][0];
                myTempPieces[i][1] = myPieces[i][1];
            }
        }
        return (complete(myTempPieces));
    }

    /**
     * Used to connect to the server
     */
    public void connect() {
        //Asks ip address until valid or cancelled
        boolean valid = false;
        while (!valid) {
            int selection = JOptionPane.showConfirmDialog(null, "Do you want to connect?",
                    "Message", JOptionPane.YES_NO_OPTION, 1);
            if (selection == 0) {
                String address = JOptionPane.showInputDialog("Enter the ip address you would like:");
                try {
                    //socket = new Socket("10.242.161.157", 6666);
                    socket = new Socket(address, 6666);
                    System.out.println("Successfully connected");
                    valid = true;
                } catch (Exception e) {
                    System.out.println("Unable to connect. Try again.");
                }
            } else {
                valid = true;
                JOptionPane.showMessageDialog(null, "Bye", "Message", 1);
            }
        }
    }

    /**
     * Resets all the filled spaces marked on the board back to clear ones
     */
    public void resetBoard() {
        for (int i = 0; i < fullMap.length; i++) {
            for (int j = 0; j < fullMap[0].length; j++) {
                if (fullMap[i][j] == -2) {
                    fullMap[i][j] = 1;//Reset back to empty
                }
            }
        }
    }

    /**
     * Fills all the appropriate spaces on the board
     */
    public void fillBoard() {
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces[0].length; j++) {
                if (pieces[i][j][0] != 0) {
                    fullMap[pieces[i][j][0]][pieces[i][j][1]] = -2;
                }
            }
        }
    }

    /**
     * Checks to see if the path was already taken to avoid stack overflow
     *
     * @param thisWalkedOn ArrayList of integer [], represents what the piece has stepped on already
     * @param row          int, represents the row on the board it is currently on
     * @param column       int, represents the column on the board it is currently on
     * @return boolean, returns true if the current position has been walked on before
     */
    public boolean checkDuplicate(ArrayList<int[]> thisWalkedOn, int row, int column) {
        boolean contains = false;
        for (int k = 0; k < thisWalkedOn.size(); k++) {
            if ((row == thisWalkedOn.get(k)[0]) && (column == thisWalkedOn.get(k)[1])) {
                contains = true;
            }
        }
        return (contains);
    }

    /**
     * Determines if the piece is on any of the goal positions
     *
     * @param test int[], represents the position to be tested
     * @return boolean, returns true if it the tested position is on a goal position
     */
    public boolean checkOnGoal(int[] test) {
        for (int i = 0; i < goalMap.length; i++) {
            if ((goalMap[i][0] == test[0]) && (goalMap[i][1] == test[1])) {
                return (true);
            }
        }
        return (false);//If it goes through it all without breaking, then it is false
    }

    /**
     * Determines if the piece is on any of the spawn positions
     *
     * @param test int[], represents the position to be tested
     * @return boolean, returns true if it the tested position is on a spawn position
     */
    public boolean checkOnSpawn(int[] test) {
        for (int i = 0; i < spawnMap.length; i++) {
            if ((spawnMap[i][0] == test[0]) && (spawnMap[i][1] == test[1])) {
                return (true);
            }
        }
        return (false);//If it goes through it all without breaking, then it is false
    }

    /**
     * Determines if the piece is on any of the other goal positions
     *
     * @param test int[], represents the position to be tested
     * @return boolean, returns true if it the tested position is on a different goal position
     */
    public boolean illegalGoal(int[] test) {
        for (int i = 0; i < otherMaps.length; i++) {
            if ((otherMaps[i][0] == test[0]) && (otherMaps[i][1] == test[1])) {
                return (true);
            }
        }
        return (false);
    }
}