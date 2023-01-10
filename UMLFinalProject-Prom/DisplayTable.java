//Import statements
import java.util.ArrayList;
import java.awt.Rectangle;

/**
 * class DisplayTable <br>
 * 02-28-19 <br>
 * This class is used to store all necessary information of a table to be used for displaying
 * @version 2.0
 * @author Kolby Chong, Eric Wang
 */
//Beginning of class
class DisplayTable{
  //private variables
  private int x;
  private int y;
  private int tableNumber;
  private Rectangle boundingBox;
  private int size;
  private ArrayList<DisplayStudent> students = new ArrayList<>();

  //Start of constructor
  DisplayTable(int size, int x, int y, int tableNumber){
    this.size = size;
    this.x = x;
    this.y = y;
    this.boundingBox = new Rectangle(x, y, 0, 0);
    this.tableNumber = tableNumber;
  } //End of constructor

  /**
   * getBoundingBox
   * Method returns this DisplayTable's Rectangle boundingBox upon call
   * @return Rectangle, boundingBox of object when called
   */
  public Rectangle getBoundingBox(){
    return boundingBox;
  }

  /**
   * setBoundingBox
   * Method accepts two integers and sets the DisplayTable's boundingBox
   * @param width, an integer representing the boundingBox's width
   * @param height, an integer representing the boundingBox's height
   */
  public void setBoundingBox(int width, int height){
    this.boundingBox = new Rectangle(this.x, this.y, width, height);
  }

  /**
   * getX
   * Method returns the DisplayTable's x position upon call
   * @return int, DisplayTable's x position
   */
  public int getX(){
    return x;
  }

  /**
   * setX
   * Method sets the DisplayTable's x position to a given integer
   * @param i, DisplayTable's x position
   */
  public void setX(int i){
    this.x = i;
    this.boundingBox = new Rectangle(this.x, this.y, (int)this.boundingBox.getWidth(), (int)this.boundingBox.getHeight());
  }

  /**
   * getY
   * Method returns the DisplayTable's y position upon call
   * @return int, DisplayTable's y position
   */
  public int getY(){
    return y;
  }

  /**
   * setY
   * Method sets the DisplayTable's y position to a given integer
   * @param i, DisplayTable's x position
   */
  public void setY(int i){
    this.y = i;
    this.boundingBox = new Rectangle(this.x, this.y, (int)this.boundingBox.getWidth(), (int)this.boundingBox.getHeight());
  }

  /**
   * getSize
   * Method returns the DisplayTable's size (number of students) upon call
   * @return int, DisplayTable's size (number of students)
   */
  public int getSize(){
    return size;
  }

  /**
   * getTableNumber
   * Method returns the DisplayTable's table number upon call
   * @return int, DisplayTable's table number
   */
  public int getTableNumber(){
    return tableNumber;
  }

  /**
   * getStudents
   * Method returns an ArrayList of DisplayStudents that are stored in the DisplayTable upon call
   * @return ArrayList, DisplayTable's ArrayList of DisplayStudents
   */
  public ArrayList<DisplayStudent> getStudents(){
    return students;
  }

  /**
   * setStudents
   * Method accepts an ArrayList of students and stores them in the students variable
   * @param students, an ArrayList of DisplayStudents that will be saved in the DisplayTable
   */
  public void setStudents(ArrayList<DisplayStudent> students){
    this.students = students;
  }
} //End of class