//Import statements
import java.util.ArrayList;

public class DisplayStudent {
    private String name;
    private String studentNumber;
    private ArrayList<String> dietaryRestrictions;
    private int seatNumber;
    DisplayStudent(String name, String studentNumber, ArrayList<String> dietaryRestrictions, int seatNumber){
      this.name = name;
      this.studentNumber = studentNumber;
      this.dietaryRestrictions = dietaryRestrictions;
      this.seatNumber = seatNumber;
    }
    public String getName(){
      return name;
    }
    public String getStudentNumber(){
      return studentNumber;
    }
    public ArrayList<String> getDietaryRestrictions(){
      return dietaryRestrictions;
    }
    public void setDietaryRestrictions(ArrayList<String> dietaryRestrictions){
      this.dietaryRestrictions = dietaryRestrictions;
    }
    public int getSeatNumber(){
      return seatNumber;
    }
    public void setSeatNumber(int i){
      this.seatNumber = i;
    }
}
