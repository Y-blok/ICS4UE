//Import statements
import javax.swing.JOptionPane;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * public class Decoder <br>
 * 03-31-19 <br>
 * This class opens up an encoded .mzip file and decompresses it to its original file. <br>
 *
 * @author Kolby Chong
 * @version 1.0
 */
public class Decoder {

    /**
     * decompress
     * Decompresses a compressed .mzip file and creates the original file
     *
     * @param fileToDecompress The file that will be decoded
     * @throws IOException If there is an error writing to file
     */
    private void decompress(String fileToDecompress) throws IOException{

        //Method variables
        BufferedInputStream in = null;
        BufferedOutputStream output = null;
        String fileName = "";
        String stringTree = "";
        int extraBits = 0;

        //Try-catch loop for decoding
        try {
            //Begin timer
            long startTime = System.nanoTime();

            //Set input file
            in = new BufferedInputStream(new FileInputStream(fileToDecompress));
            int c;

            System.out.println("Decompiling..."); //Output "Decompiling..." to console

            //Read the name of the file that will be written to
            while ((c = in.read()) != -1 && c != 13) {
                fileName += (char) c;
            }
            System.out.println("Saving to: " + fileName); //Output file name to console
            in.read(); //Get rid of garbage '10' character

            //Set output file
            output  = new BufferedOutputStream(new FileOutputStream(fileName));

            //Read the tree as a String
            while ((c = in.read()) != -1 && c != 13) {
                stringTree += (char) c;
            }
            in.read(); //Get rid of garbage '10' character

            //Turns the stringTree into a single tree
            HuffmanTree<Character> finalTree = stringToTree(stringTree);

            //Reads and stores the number of extra bits for later
            String stringBytes = "";
            while ((c = in.read()) != -1 && c != 13) {
                stringBytes += (char) c;
            }
            in.read();

            //Variables for reading encoded data
            Character tempItem;
            int next; //This variable reads the next byte in order to check for the end
            boolean foundEnd = false; //Boolean for the end of the data

            c = in.read(); //Reads and stores the first byte of data

            while (!foundEnd) { //Decodes until the end is found
                //Condition for ignoring the extra bits of data: when the next byte of data is read is the end
                if ((next = in.read()) == -1){
                    extraBits = Integer.parseInt(stringBytes);
                    foundEnd = true;
                }

                //Reads through each byte of data, ignoring extra bits if the end of data was found
                for (int i = 7; i >= extraBits; i--) {
                    if ((c & (char) 1<<i) > 0) { //If the i+1'th bit is 1, go right in the tree
                        tempItem = (Character)finalTree.traverse('1'); //Receives item after traversing
                    } else { //If 0, go left in the tree
                        tempItem = (Character)finalTree.traverse('0'); //Receives item after traversing
                    }
                    //If the item received is a character, write to the output file
                    if (tempItem != null){
                        output.write(tempItem);
                        finalTree.reset(); //Reset the tree's tempNode
                    }
                }

                c = next; //Set the byte to read as the next byte (which has already been read)
            }

            //Output final statements including time spent running
            System.out.println("Completed");
            System.out.println("Finished in " + (System.nanoTime() - startTime)/1000000000.0 + " seconds");

        } catch (IOException e){ //IOException catch statement
            System.out.println("File not found");
        } finally { //Close input and output
            if (in != null) {
                in.close();
            }
            if (output != null){
                output.close();
            }
        }
    }

    /**
     * stringToTree
     * Takes in a string version of a Huffman tree and turns it into a single Huffman tree structure
     *
     * @param stringTree The String version of the tree that will be converted to a full tree
     * @return HuffmanTree, The final Huffman tree that will be used for decompiling data
     */
    private HuffmanTree<Character> stringToTree(String stringTree){

        //Method variables
        Stack<HuffmanTree<Character>> treeStack = new Stack<HuffmanTree<Character>>();
        int tempPos = 0;
        boolean foundInt = false;
        Character foundConverted;

        //Go through the String and create the full HuffmanTree
        for (int i = 0; i < stringTree.length(); i++) {
            //Condition for finding integer versions of characters
            if (Character.isDigit(stringTree.charAt(i)) && !foundInt) {
                tempPos = i; //Mark the beginning of the integer
                foundInt = true; //Change boolean
            } else if (stringTree.charAt(i) == ' ' || stringTree.charAt(i) == ')') { //Condition for converting String
                //If an integer was previously found, create a new tree with the Character version
                if (foundInt) {
                    foundConverted = ((char) Integer.parseInt(stringTree.substring(tempPos, i)));
                    treeStack.push(new HuffmanTree<Character>(foundConverted)); //Push new tree
                    foundInt = false;
                }
                //If the character read is a close bracket, merge the top two trees
                if (stringTree.charAt(i) == ')') {
                    HuffmanTree<Character> temp1 = treeStack.pop();
                    HuffmanTree<Character> temp2 = treeStack.pop();
                    HuffmanTree<Character> newTree = new HuffmanTree<Character>(); //Create new tree for merging
                    newTree.mergeNodes(temp2, temp1); //Set temp2 and temp1 as left and right, respectively
                    treeStack.push(newTree); //Push new tree
                }
            }
        }

        //Return final tree, the one that will be used for decoding
        return treeStack.pop();
    }

    /**
     * main
     * Asks user for a file to decode and runs the code that decompresses it
     *
     * @param args Command line arguments
     * @throws IOException If there is an error reading/writing to a file
     */
    public static void main(String args[]) throws IOException {

        Decoder runner = new Decoder();
        String fileToDecompress = JOptionPane.showInputDialog("Please enter the name of the file to decode");
        runner.decompress(fileToDecompress);
    }
}
