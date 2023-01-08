package server;
/**
 * JuggernautQStun.java
 *
 * This is the class representing the status storing the JuggernautQStun info
 *
 * @author Will Jeong, Jonathan Xu, Kamron Zaidi, Artem Sotnikov, Kolby Chong, Bill Liu
 * @version 1.0
 * @since 2019-05-19
 */
class JuggernautQStun extends Stun{
  private static int DURATION = 100;
  private static int ID = 12;
   /**
   * Constructor to create a new JuggernautQStun
   */
  JuggernautQStun(){
    super(DURATION,ID);
  }
}