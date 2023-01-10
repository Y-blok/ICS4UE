//import Graphics

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

//import FileIO
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//import util
import java.util.ArrayList;

/**
 * public class FloorPlan <br>
 * extends JFrame <br>
 * 02-27-19 <br>
 * This class is an extended version of JFrame that displays a floor plan based on a list of tables and students. <br>
 * The user is able to edit the floor plan and search for student locations
 *
 * @author Kolby Chong, Eric Wang
 * @version 2.0
 */

public class FloorPlan extends JFrame {

    //class variables
    private JFrame window;
    private JPanel mainPanel;
    private int[] tableNumStud;
    private double halfLength;
    private int planTable;
    private int planRow;
    private int planColumn;
    private int planRemain;
    private double tableToScreenRatio;
    private int focusTableX;
    private int focusTableY;
    private int focusTableRadius;
    private String directory;
    private DisplayTable movingTable;
    private int moveDiffX;
    private int moveDiffY;
    private JTextField searchMessage;
    private JTextField searchInput;
    private JTextField searchOutput;
    private JTextPane focusMessage1;
    private JTextPane focusMessage2;
    private JTextPane focusMessage3;
    private JTextPane infoMessage;
    private ArrayList<DisplayTable> displayTables;
    private DisplayTable focusTable;
    private DisplayStudent focusStudent;
    private JFileChooser fileChooser;
    private ArrayList<Rectangle> snapBoxes;
    private boolean displayed;

    /**
     * this is the main constructor for FloorPlan
     */
    FloorPlan() {
        //call to super
        super("Floor Plan");

        //set various variables
        //scale the contents of this display to the user's screen size
        tableToScreenRatio = Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 1080;
        //calculate the location of the focus table display
        focusTableX = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() + 50 * tableToScreenRatio);
        focusTableY = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 + 50 * tableToScreenRatio);
        focusTableRadius = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 6);
        //initiate displayTables array
        displayTables = new ArrayList<DisplayTable>();
        //set displayed to false;
        displayed = false;
        this.window = this;
    }

    /**
     * generateFloorPlan
     * generates a floor plan and table locations based on an array of tables and then saves it to a file of the user's specification
     *
     * @param tables the list of table objects the user wishes to display onto the screen
     * @throws IOException if there is an error writing to file
     */
    public void generateFloorPlan(ArrayList<Table> tables) throws IOException {

        //message the user to enter the name of the file that will be saved to
        int selection = JOptionPane.showConfirmDialog(null, "Please enter the .txt file you would like to save to.", "Message", JOptionPane.OK_CANCEL_OPTION, 1);
        if (selection == 0) {
            //calculate the row and column size to optimally fit the amount of tables into the screen
            int rowSize = (int) (Math.round(Math.sqrt(tables.size())));
            int columnSize = (int) (Math.ceil((double) (tables.size()) / rowSize));
            int remainder = 0;
            //find if the tables cannot be put into perfectly equal rows and columns and assign a remainder to represent the remaining tables
            if (rowSize * columnSize != tables.size()) {
                remainder = tables.size() - (rowSize - 1) * columnSize;
            }

            //Calculate "half length" dimensions (the length of half a table)
            double halfTableDimension = (Toolkit.getDefaultToolkit().getScreenSize().getHeight()) / (columnSize * 3 + 1);

            //File directory for saving to a text file
            this.directory = System.getProperty("user.dir") + "/plans/";
            //Creates folder if it doesn't exist
            new File(this.directory).mkdirs();
            fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(this.directory));
            fileChooser.setDialogTitle("Save to .txt");
            try {
                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    //make the file that the user chose to save to
                    File file = fileChooser.getSelectedFile();

                    //check if the file is of the right type (.txt files)
                    if (((file.getName().indexOf(".txt")) == (file.getName().length() - 4)) && (file.getName().length() != 3)) {

                        //create a PrintWriter to output to the file
                        PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(file)));

                        // ---- Output to text file for saving information
                        output.print(tables.size() + " " + halfTableDimension); // Size and half-table size
                        output.println(" " + rowSize + " " + columnSize + " " + remainder); //Rows, columns, remainder

                        //Table information - n^2 tables
                        for (int i = 0; i < rowSize; i++) { //Rows
                            if ((i != rowSize - 1) || (remainder == 0)) { //Exclude remainder row
                                for (int j = 0; j < columnSize; j++) { //Each table in the i'th row
                                    //x and y coordinates of the tables
                                    output.print(((int) (j * halfTableDimension * 2) + (int) ((j + 1) * halfTableDimension)) + " " + ((int) (i * halfTableDimension * 2) + (int) ((i + 1) * halfTableDimension)) + " ");
                                    int temp = tables.get(i * columnSize + j).getStudents().size();
                                    output.println(temp); //number of students at the table

                                    //list of students per table
                                    ArrayList<Student> tempStudentList = tables.get(i * columnSize + j).getStudents();
                                    for (int k = 0; k < tempStudentList.size(); k++) {
                                        output.println("[" + tempStudentList.get(k).getName() + "]"); //output student's name
                                        output.println(tempStudentList.get(k).getStudentNumber()); //output student's student number
                                        ArrayList<String> tempAllergyList = tempStudentList.get(k).getDietaryRestrictions();
                                        for (int m = 0; m < tempAllergyList.size(); m++) {
                                            output.print(tempAllergyList.get(m) + ", "); //output student's dietary restrictions
                                        }
                                        output.println();
                                    }
                                }
                            } else if ((i == rowSize - 1)) { //Table information - Remaining tables
                                for (int j = 0; j < remainder; j++) {
                                    //x and y coordinates of the tables
                                    output.print(((int) (j * halfTableDimension * 2) + (int) ((j + 1) * halfTableDimension)) + " " + ((int) (i * halfTableDimension * 2) + (int) ((i + 1) * halfTableDimension)) + " ");
                                    int temp = tables.get(i * columnSize + j).getStudents().size();
                                    output.println(temp); //number of students at the table

                                    //list of students per table
                                    //same information printed as the last part
                                    ArrayList<Student> tempStudentList = tables.get(i * columnSize + j).getStudents();
                                    for (int k = 0; k < tempStudentList.size(); k++) {
                                        output.println("[" + tempStudentList.get(k).getName() + "]");
                                        output.println(tempStudentList.get(k).getStudentNumber());
                                        ArrayList<String> tempAllergyList = tempStudentList.get(k).getDietaryRestrictions();
                                        for (int m = 0; m < tempAllergyList.size(); m++) {
                                            output.print(tempAllergyList.get(m) + ", ");
                                        }
                                        output.println();
                                    }
                                }
                            }
                        }

                        //close the PrintWriter
                        output.close();

                        //Tell user generation successful
                        JOptionPane.showConfirmDialog(null, "Generation successful.", "Message", JOptionPane.DEFAULT_OPTION, 1);

                    } else { //Inform user of bad file name
                        JOptionPane.showConfirmDialog(null, "Invalid file name. Please save as a .txt file", "Invalid file name", JOptionPane.DEFAULT_OPTION, 2);
                    }
                }
            } catch (Exception e) {
                System.out.print("error"); //error message
            }
        }
    }

    /**
     * displayFloorPlan
     * displays a floor plan onto the screen based on a .txt file the user selects
     */
    public void displayFloorPlan() {
        //Bring in information from the file
        File file;
        //Create a new fileChooser and set it's directory to the relevant files
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir") + "/plans/"));
        boolean fileFound = false;
        try {
            //open the file chooser
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                //get the file selected by the user
                file = fileChooser.getSelectedFile();
                //checks if file is of valid type
                if (((file.getName().indexOf(".txt")) == (file.getName().length() - 4)) && (file.getName().length() != 3)) {

                    //create new BufferedReader to take input from the file
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    fileFound = true;

                    //take first line of file input
                    String[] input = br.readLine().split(" ");
                    planTable = Integer.parseInt(input[0]);
                    halfLength = Double.parseDouble(input[1]);
                    planRow = Integer.parseInt(input[2]);
                    planColumn = Integer.parseInt(input[3]);
                    planRemain = Integer.parseInt(input[4]);
                    tableNumStud = new int[planTable];

                    //loop that goes through the rest of the file to read information of the tables
                    for (int i = 0; i < planTable; i++) {
                        input = br.readLine().split(" ");
                        //read the X and Y locations of the table
                        int tempX = Integer.parseInt(input[0]);
                        int tempY = Integer.parseInt(input[1]);
                        tableNumStud[i] = Integer.parseInt(input[2]);
                        ArrayList<DisplayStudent> tempStuds = new ArrayList<>();
                        //loop through all students in this table
                        for (int j = 0; j < tableNumStud[i]; j++) {
                            String tempLong = br.readLine();
                            String tempName = tempLong.substring(1, tempLong.length() - 1);//read the student's name
                            String tempStudNum = br.readLine();//read the student's student number
                            input = br.readLine().split(", ");
                            ArrayList<String> tempAllergy = new ArrayList<>();
                            for (int k = 0; k < input.length; k++) {
                                tempAllergy.add(input[k]); //read the student's dietary restrictions
                            }
                            tempStuds.add(new DisplayStudent(tempName, tempStudNum, tempAllergy, j + 1));//combine all students in the table into a single ArrayList
                        }

                        //create a new DisplayTable object based on the information given by the file
                        displayTables.add(new DisplayTable(tableNumStud[i], tempX, tempY, i + 1));
                        displayTables.get(i).setStudents(tempStuds);
                        displayTables.get(i).setBoundingBox((int) (halfLength * 2.0), (int) (halfLength * 2.0));
                    }

                    //create snapboxes, basically hitboxes used to snap tables into a more organized location
                    //the locations of these hitboxes are based on the locations of the generated tables
                    snapBoxes = new ArrayList<>();
                    int snapBoxWidth = (int) (halfLength / 1.5);
                    //loop through all columns and rows
                    for (int i = 0; i < planColumn; i++) {
                        for (int j = 0; j < planColumn; j++) {
                            //create a new snapBox centered around the location of the table
                            snapBoxes.add(new Rectangle(((int) (j * halfLength * 2) + (int) ((j + 1) * halfLength)) - snapBoxWidth / 2, ((int) (i * halfLength * 2) + (int) ((i + 1) * halfLength)) - snapBoxWidth / 2, snapBoxWidth, snapBoxWidth));
                        }
                    }

                    br.close(); //close the file reader
                } else { //Inform user of invalid file selected
                    JOptionPane.showConfirmDialog(null, "Invalid file selected. Please select a .txt file", "Invalid file", JOptionPane.DEFAULT_OPTION, 2);
                }
            } else {
                fileFound = false;
            }
        } catch (Exception e) {
            fileFound = false;
            System.out.println("ERROR"); //error message
        }

        // Set the frame to size
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setLocationRelativeTo(null); //start the frame in the center of the screen
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Set without border
        this.setUndecorated(true);

        //create a FloorPlanPanel
        mainPanel = new FloorPlanPanel();
        mainPanel.setLayout(null);

        //if not been displayed before
        if (!displayed) {

            //configure the location and specifications of the save button
            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(new SaveButtonListener());
            saveButton.setBounds((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()), (int) (20 * tableToScreenRatio), (int) (200 * tableToScreenRatio), (int) (60 * tableToScreenRatio));
            saveButton.setBackground(Color.decode("#8780B8"));
            saveButton.setForeground(Color.WHITE);
            saveButton.setFont(new Font("Arial", Font.PLAIN, (int) (30 * tableToScreenRatio)));
            //add save button to the panel
            mainPanel.add(saveButton);

            //configure the location and specifications of the focus table information displays
            focusMessage1 = new JTextPane();
            focusMessage1.setBounds((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()), (int) ((Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2) - 110 * tableToScreenRatio), (int) (700 * tableToScreenRatio), (int) (35 * tableToScreenRatio));
            focusMessage1.setBackground(new Color(0, 0, 0, 0));
            focusMessage1.setBorder(BorderFactory.createEmptyBorder());
            focusMessage1.setFont(new Font("Arial", Font.PLAIN, (int) (30 * tableToScreenRatio)));
            focusMessage1.setEditable(false);
            focusMessage2 = new JTextPane();
            focusMessage2.setBounds((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()), (int) ((Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2) - 75 * tableToScreenRatio), (int) (400 * tableToScreenRatio), (int) (35 * tableToScreenRatio));
            focusMessage2.setBackground(new Color(0, 0, 0, 0));
            focusMessage2.setBorder(BorderFactory.createEmptyBorder());
            focusMessage2.setFont(new Font("Arial", Font.PLAIN, (int) (30 * tableToScreenRatio)));
            focusMessage2.setEditable(false);
            focusMessage3 = new JTextPane();
            focusMessage3.setBounds((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()), (int) ((Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2) - 40 * tableToScreenRatio), (int) (700 * tableToScreenRatio), (int) (35 * tableToScreenRatio));
            focusMessage3.setBackground(new Color(0, 0, 0, 0));
            focusMessage3.setBorder(BorderFactory.createEmptyBorder());
            focusMessage3.setFont(new Font("Arial", Font.PLAIN, (int) (30 * tableToScreenRatio)));
            focusMessage3.setEditable(false);
            //add the focus table information displays into the panel
            mainPanel.add(focusMessage1);
            mainPanel.add(focusMessage2);
            mainPanel.add(focusMessage3);

            //configure the location and specifications of the FloorPlan controls
            infoMessage = new JTextPane();
            infoMessage.setBounds((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()), (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 70 * tableToScreenRatio), (int) (700 * tableToScreenRatio), (int) (70 * tableToScreenRatio));
            infoMessage.setBackground(new Color(0, 0, 0, 0));
            infoMessage.setBorder(BorderFactory.createEmptyBorder());
            infoMessage.setFont(new Font("Arial", Font.PLAIN, (int) (25 * tableToScreenRatio)));
            infoMessage.setEditable(false);
            infoMessage.setText("Use Left Click to select tables and students. Use Right Click to move tables around.");
            //add the control info display into the panel
            mainPanel.add(infoMessage);

            //configure the location and specification of the search function input text field
            searchInput = new JTextField();
            searchInput.setBounds((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()), (int) (155 * tableToScreenRatio), (int) (300 * tableToScreenRatio), (int) (60 * tableToScreenRatio));
            searchInput.setFont(new Font("Arial", Font.PLAIN, (int) (30 * tableToScreenRatio)));
            //configure the location and specification of the search function result text field
            searchOutput = new JTextField();
            searchOutput.setBounds((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()), (int) (220 * tableToScreenRatio), (int) (400 * tableToScreenRatio), (int) (35 * tableToScreenRatio));
            searchOutput.setBackground(new Color(0, 0, 0, 0));
            searchOutput.setBorder(BorderFactory.createEmptyBorder());
            searchOutput.setFont(new Font("Arial", Font.PLAIN, (int) (30 * tableToScreenRatio)));
            searchOutput.setEditable(false);
            //configure the location and specification of the search function message text field
            searchMessage = new JTextField();
            searchMessage.setBounds((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()), (int) (120 * tableToScreenRatio), (int) (600 * tableToScreenRatio), (int) (35 * tableToScreenRatio));
            searchMessage.setBackground(new Color(0, 0, 0, 0));
            searchMessage.setBorder(BorderFactory.createEmptyBorder());
            searchMessage.setFont(new Font("Arial", Font.PLAIN, (int) (30 * tableToScreenRatio)));
            searchMessage.setText("Search Student by Student Number: ");
            searchMessage.setEditable(false);
            //configure the location and specification of the search function activation button
            JButton searchButton = new JButton("Search");
            searchButton.addActionListener(new StudentSearchListener());
            searchButton.setBounds((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() + 310 * tableToScreenRatio), (int) (155 * tableToScreenRatio), (int) (200 * tableToScreenRatio), (int) (60 * tableToScreenRatio));
            searchButton.setBackground(Color.decode("#8780B8"));
            searchButton.setForeground(Color.WHITE);
            searchButton.setFont(new Font("Arial", Font.PLAIN, (int) (30 * tableToScreenRatio)));
            //add the search function to the panel
            mainPanel.add(searchInput);
            mainPanel.add(searchOutput);
            mainPanel.add(searchButton);
            mainPanel.add(searchMessage);

            //configure the location and specification of the exit button
            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(new ExitButtonListener());
            exitButton.setBounds((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 225 * tableToScreenRatio), (int) (20 * tableToScreenRatio), (int) (200 * tableToScreenRatio), (int) (60 * tableToScreenRatio));
            exitButton.setBackground(Color.decode("#8780B8"));
            exitButton.setForeground(Color.WHITE);
            exitButton.setFont(new Font("Arial", Font.PLAIN, (int) (30 * tableToScreenRatio)));
            //add the exit button to the panel
            mainPanel.add(exitButton);

            //add the panel to the main JFrame
            window.add(mainPanel);

            //create new mouse listener for the JFrame
            MyMouseListener mouseListener = new MyMouseListener();
            window.addMouseListener(mouseListener);
        }
        //REQUIRED SO THAT THERE AREN'T MULTIPLE LISTENERS
        displayed = true;

        //Start the app if file found
        if (fileFound) {
            this.setVisible(true);
        }
    }

    /**
     * save
     * saves the current floor plan to a file of the user's choosing
     */
    private void save() {
        //message the user to enter the name of the file that will be saved to
        int selection = JOptionPane.showConfirmDialog(null, "Please enter the name of the .txt file you would like to save to.", "Message", JOptionPane.OK_CANCEL_OPTION, 1);
        if (selection == 0) {
            //File directory for saving to a text file
            this.directory = System.getProperty("user.dir") + "/plans/";
            fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(this.directory));
            fileChooser.setDialogTitle("Save to .txt");
            try {
                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    //make the file that the user chose to save to
                    File file = fileChooser.getSelectedFile();
                    //make a PrintWriter to write to the file
                    PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(file)));

                    //print the information to the file
                    output.print(displayTables.size() + " " + halfLength); // Size and half-table size
                    output.println(" " + planRow + " " + planColumn + " " + planRemain); //Rows, columns, remainder
                    for (int i = 0; i < displayTables.size(); i++) {
                        //output the information of the table
                        output.print(displayTables.get(i).getX() + " " + displayTables.get(i).getY() + " ");// the x and y location of the table
                        output.println(displayTables.get(i).getStudents().size()); //number of students at the table

                        //output the information of the students at the table
                        for (int j = 0; j < displayTables.get(i).getStudents().size(); j++) {
                            output.println("[" + displayTables.get(i).getStudents().get(j).getName() + "]"); //student name
                            output.println(displayTables.get(i).getStudents().get(j).getStudentNumber()); //student's student number
                            for (int k = 0; k < displayTables.get(i).getStudents().get(j).getDietaryRestrictions().size(); k++) {
                                output.print(displayTables.get(i).getStudents().get(j).getDietaryRestrictions().get(k) + ", "); //student's dietary restrictions
                            }
                            output.println();
                        }
                    }
                    //close the PrintWriter
                    output.close();
                }
            } catch (Exception e) {
                System.out.println("Error saving file"); //error message
            }
        }
    }

    /**
     * autoSnap
     * moves a table object to a location based on the snapBoxes
     *
     * @param table a DisplayTable that will be snapped to a location based on the snapBoxes
     */
    private void autoSnap(DisplayTable table) {
        //loop through all snapBoxes
        for (int i = 0; i < snapBoxes.size(); i++) {
            if (snapBoxes.get(i).contains(table.getX(), table.getY())) { //if the table's location is inside a snapBox
                //set the table's location to the snapbox's center
                table.setX((int) (snapBoxes.get(i).getX() + snapBoxes.get(i).getWidth() / 2));
                table.setY((int) (snapBoxes.get(i).getY() + snapBoxes.get(i).getHeight() / 2));
            }
        }
    }

    //INNER CLASSES BEGIN HERE

    /**
     * private class FloorPlanPanel <br>
     * extends JPanel <br>
     * this class extends JPanel and draws the tables onto the screen
     */
    private class FloorPlanPanel extends JPanel {
        /**
         * paintComponent
         * overrides the paintComponent of JComponent to draw the tables onto the screen
         *
         * @param g the Graphics object to protect
         * @return
         */
        @Override
        public void paintComponent(Graphics g) {
            //required
            super.paintComponent(g);
            setDoubleBuffered(true);

            //if there is a table currently being moved
            if (movingTable != null) {
                //update the table's x and y locations based off of the mouse's x and y locations
                movingTable.setX((int) MouseInfo.getPointerInfo().getLocation().getX() - moveDiffX);
                movingTable.setY((int) (MouseInfo.getPointerInfo().getLocation().getY()) - moveDiffY);
                //check if the table is out of bounds and make sure they are in bounds
                if (movingTable.getX() > (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 15 * tableToScreenRatio) - movingTable.getBoundingBox().getWidth()) {
                    movingTable.setX((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 15 * tableToScreenRatio - movingTable.getBoundingBox().getWidth()));
                }
                if (movingTable.getX() < 0) {
                    movingTable.setX(0);
                }
                if (movingTable.getY() > (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 15 * tableToScreenRatio) - movingTable.getBoundingBox().getHeight()) {
                    movingTable.setY((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 15 * tableToScreenRatio - movingTable.getBoundingBox().getHeight()));
                }
                if (movingTable.getY() < 0) {
                    movingTable.setY(0);
                }
            }

            //Paint tables
            for (int i = 0; i < planTable; i++) {
                //set color to desired color
                g.setColor(Color.decode("#BDA7D4"));
                //draw table
                g.fillOval(displayTables.get(i).getX(), displayTables.get(i).getY(), (int) (halfLength * 2.0), (int) (halfLength * 2.0));
                //set color and font for the table number
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.PLAIN, (int) (halfLength / 3)));
                //display table number
                g.drawString(Integer.toString(i + 1), (int) (displayTables.get(i).getX() + halfLength - g.getFontMetrics().stringWidth(Integer.toString(i + 1)) / 2), (int) (displayTables.get(i).getY() + halfLength + g.getFontMetrics().getHeight() / 2 - g.getFontMetrics().getDescent()));

                //draw representations of students around the table
                g.setColor(Color.decode("#8780B8"));
                for (int j = 0; j < displayTables.get(i).getSize(); j++) {
                    //use the power of trigonometry to lay the students around the outer ring of the table
                    double angle = ((Math.PI * 2) / displayTables.get(i).getSize()) * j + Math.PI;
                    int tempX = (int) (displayTables.get(i).getX() + halfLength - halfLength / 6 + halfLength * Math.sin(angle));
                    int tempY = (int) (displayTables.get(i).getY() + halfLength - halfLength / 6 + halfLength * Math.cos(angle));
                    g.fillOval(tempX, tempY, (int) (halfLength / 3), (int) (halfLength / 3)); //draw student
                }
            }

            //draw line seperating sections of the screen
            g.setColor(Color.BLACK);
            //Vert line
            g.drawLine((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 15 * tableToScreenRatio), 0, (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 15 * tableToScreenRatio), (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
            //Hor line
            g.drawLine((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 15 * tableToScreenRatio), (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2), (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()), (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2));

            //if there is a focus table (table the user wishes to see in more detail)
            if (focusTable != null) {
                //draw a larger table based on the focus table locations
                g.setColor(Color.decode("#BDA7D4"));
                g.fillOval(focusTableX, focusTableY, focusTableRadius * 2, focusTableRadius * 2);
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.PLAIN, (int) (50 * tableToScreenRatio)));
                g.drawString(Integer.toString(focusTable.getTableNumber()), (focusTableX + focusTableRadius - g.getFontMetrics().stringWidth(Integer.toString(focusTable.getTableNumber())) / 2), (focusTableY + focusTableRadius + g.getFontMetrics().getHeight() / 2 - g.getFontMetrics().getDescent()));

                //draw the students of the focus table
                for (int i = 0; i < focusTable.getSize(); i++) {
                    g.setColor(Color.decode("#8780B8"));
                    double angle = ((Math.PI * 2) / focusTable.getSize()) * i + Math.PI;
                    int tempX = (int) (focusTableX + focusTableRadius - focusTableRadius / 6 + focusTableRadius * Math.sin(angle));
                    int tempY = (int) (focusTableY + focusTableRadius - focusTableRadius / 6 + focusTableRadius * Math.cos(angle));
                    g.fillOval(tempX, tempY, focusTableRadius / 3, focusTableRadius / 3);
                    g.setFont(new Font("Arial", Font.PLAIN, (int) (20 * tableToScreenRatio)));
                    g.setColor(Color.WHITE);
                    g.drawString(Integer.toString(focusTable.getStudents().get(i).getSeatNumber()), (int) (tempX + focusTableRadius / 6 - g.getFontMetrics().stringWidth(Integer.toString(focusTable.getStudents().get(i).getSeatNumber())) / 2), (int) (tempY + focusTableRadius / 6 + g.getFontMetrics().getHeight() / 2 - g.getFontMetrics().getDescent()));
                }

                //if there is a focus student (student the user wishes to see in more detail)
                if (focusStudent != null) {
                    //set the focus messages to display information of the student
                    focusMessage1.setText(focusStudent.getName() + ": Table " + focusTable.getTableNumber() + ", Seat number " + focusStudent.getSeatNumber()); //display name, table and seat number
                    focusMessage2.setText(focusStudent.getStudentNumber()); //display student number
                    String dietaryRestrict = "Restricted Foods: ";
                    for (int j = 0; j < focusStudent.getDietaryRestrictions().size(); j++) {
                        if (j == focusStudent.getDietaryRestrictions().size() - 1) {
                            dietaryRestrict += (focusStudent.getDietaryRestrictions().get(j));
                        } else {
                            dietaryRestrict += (focusStudent.getDietaryRestrictions().get(j) + ", "); //combine all dietary restrictions into a string
                        }
                    }
                    g.setFont(focusMessage3.getFont());
                    if (g.getFontMetrics().stringWidth(dietaryRestrict) > focusMessage3.getWidth()) { //check if the string is too long for the text field to accommodate
                        focusMessage3.setFont(focusMessage3.getFont().deriveFont((float) (focusMessage3.getFont().getSize2D() * focusMessage3.getWidth() / g.getFontMetrics().stringWidth(dietaryRestrict)))); //scale the text size so that the string always fits
                    }
                    focusMessage3.setText(dietaryRestrict); //display dietary restrictions
                } else {
                    //clear focus messages
                    focusMessage1.setText("");
                    focusMessage2.setText("");
                    focusMessage3.setFont(new Font("Arial", Font.PLAIN, (int) (30 * tableToScreenRatio)));
                    focusMessage3.setText("");
                }
            } else {
                //clear focus messages
                focusMessage1.setText("");
                focusMessage2.setText("");
                focusMessage3.setFont(new Font("Arial", Font.PLAIN, (int) (30 * tableToScreenRatio)));
                focusMessage3.setText("");
            }

            //refresh the screen
            repaint();
        }
    }

    /**
     * private class MyMouseListener <br>
     * implements MouseListener <br>
     * this class takes mouse input from the user to be used inside the program
     */
    private class MyMouseListener implements MouseListener {
        /**
         * mouseClicked
         * unused method, here to overwrite the interface
         *
         * @param e (required MouseEvent)
         */
        @Override
        public void mouseClicked(MouseEvent e) {

        }

        /**
         * mousePressed
         * detects if mouse is clicked and performs actions accordingly
         *
         * @param e(required MouseEvent)
         */
        @Override
        public void mousePressed(MouseEvent e) {
            //if mouse click is a leftclick
            if (e.getButton() == MouseEvent.BUTTON1) {
                //check if the mouse location is in the left side of the screen (clicking on empty space here will clear focusTable)
                if (MouseInfo.getPointerInfo().getLocation().getX() < (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 15 * tableToScreenRatio)) {
                    focusTable = null;
                } else if (focusTable != null) { //if there is a focus table and mouse is on right of screen
                    //loop through students of that table
                    for (int i = 0; i < focusTable.getSize(); i++) {
                        double angle = ((Math.PI * 2) / focusTable.getSize()) * i + Math.PI;
                        int tempX = (int) (focusTableX + focusTableRadius - focusTableRadius / 6 + focusTableRadius * Math.sin(angle));
                        int tempY = (int) (focusTableY + focusTableRadius - focusTableRadius / 6 + focusTableRadius * Math.cos(angle));
                        Rectangle tempRect = new Rectangle(tempX, tempY, focusTableRadius / 3, focusTableRadius / 3); //create a tempoary rectangle used as a hitbox
                        if (tempRect.contains(e.getX(), e.getY())) { //check if this mouse is clicking on a student
                            focusStudent = focusTable.getStudents().get(i); //set focusStudent accordingly
                            focusMessage3.setFont(new Font("Arial", Font.PLAIN, (int) (30 * tableToScreenRatio)));
                        }
                    }
                }

                //loop through all display tables
                for (int i = 0; i < displayTables.size(); i++) {
                    if (displayTables.get(i).getBoundingBox().contains(e.getX(), e.getY())) { //check if this mouse is clicking on a table
                        focusTable = displayTables.get(i); //update focusTable accordingly
                        focusStudent = null;
                    }
                }
            } else if (e.getButton() == MouseEvent.BUTTON3) { //if mouse click is a rightclick
                for (int i = 0; i < displayTables.size(); i++) { //loop through all display tables
                    if (displayTables.get(i).getBoundingBox().contains(e.getX(), e.getY())) { //check if this mouse is clicking on a table
                        movingTable = displayTables.get(i); //update movingTable accordingly
                        //keep track of the mouse's click relative to the table location (to make dragging around smoother)
                        moveDiffX = (e.getX() - displayTables.get(i).getX());
                        moveDiffY = (e.getY() - displayTables.get(i).getY());
                    }
                }
            }
        }

        /**
         * mouseReleased
         * detects if mouse click is released and performs actions accordingly
         *
         * @param e(required MouseEvent)
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            //if mouse click is a rightclick
            if (e.getButton() == MouseEvent.BUTTON3) {
                autoSnap(movingTable); //snap the movingTable
                movingTable = null; //clear the movingTable as nothing is being moved anymore
                //clear move differences
                moveDiffX = 0;
                moveDiffY = 0;
            }
        }

        /**
         * mouseEntered
         * unused method, here to overwrite the interface
         *
         * @param e (required MouseEvent)
         */
        @Override
        public void mouseEntered(MouseEvent e) {

        }

        /**
         * mouseExited
         * unused method, here to overwrite the interface
         *
         * @param e (required MouseEvent)
         */
        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    /**
     * private class StudentSearchListener <br>
     * implements ActionListener <br>
     * this class is used for the student search function
     */
    private class StudentSearchListener implements ActionListener {
        /**
         * actionPerformed
         * searches the displayTables ArrayList for a student and sets that student to the focusStudent
         *
         * @param event (required ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            boolean searchSuccessful = false;
            //loop through the displayTables
            for (int i = 0; i < displayTables.size(); i++) {
                //loop through the students in a table
                for (int j = 0; j < displayTables.get(i).getStudents().size(); j++) {
                    //if the current student is the student inputted in the searchInput textfield
                    if (displayTables.get(i).getStudents().get(j).getStudentNumber().equals(searchInput.getText())) {
                        //set focus table and student accordingly
                        focusTable = displayTables.get(i);
                        focusStudent = displayTables.get(i).getStudents().get(j);
                        searchSuccessful = true;
                    }
                }
            }
            if (searchSuccessful) { //if the search is successful display output accordingly in searchOutput
                searchOutput.setText("Student Found");
            } else { //if the search is unsuccessful display output accordingly in searchOutput
                searchOutput.setText("Student Not Found");
            }
        }
    }

    /**
     * private class SaveButtonListener <br>
     * implements ActionListener <br>
     * this class is used for the save function
     */
    private class SaveButtonListener implements ActionListener {
        /**
         * actionPerformed
         * calls the save method
         *
         * @param event (required ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            try {
                save(); //call save to save the file
            } catch (Exception e) {
                System.out.println("Error Writing to File"); //error message
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
            //clear displayTables, searchOutput and searchInput
            displayTables.clear();
            searchOutput.setText("");
            searchInput.setText("");
            focusStudent = null;
            focusTable = null;
            //dispose of this frame
            window.dispose();
        }
    }
}
