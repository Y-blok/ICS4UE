/**
 * public class Stack <br>
 * 03-31-19 <br>
 * This class is a stack data structure that is used for decoding a .mzip file <br>
 *
 * @param <E> Generic object identifying the stack
 * @author Kolby Chong
 */
public class Stack<E> {

    //Class variable
    private StackNode<E> head;

    /**
     * Stack
     * Constructor with nothing
     */
    Stack(){
        head = null;
    }

    /**
     * push
     * This method takes in a generic object and puts a new Node with that object on top of the stack
     *
     * @param object The generic object that will be stored in the new head StackNode
     */
    public void push(E object) {
        if (head == null) {
            head = new StackNode<E>(object); //New node linked to nothing
        } else {
            head = new StackNode<E>(object, head); //Creates the new node on top, linked to the original head
        }
    }

    /**
     * pop
     * This method returns the object on top of the stack and moves everything else up
     *
     * @return E, the object stored in the head node
     */
    public E pop() {
        if (head == null){ //null condition just in case
            return null;
        }
        //Obtain the item of type E and store it before changing head to the next node, then return
        E tempItem = head.getItem();
        head = head.getNext();
        return tempItem;

    }

}

/**
 * class StackNode
 * 03-31-19
 * This class is for the individual nodes for the Stack class with methods for getting class data
 *
 * @param <T> Generic object used in the Node (item)
 * @author Kolby Chong
 */
class StackNode<T> {
    //Class variables
    private T item;
    private StackNode<T> next;

    /**
     * StackNode
     * Constructor with an item and not linked to anything
     *
     * @param item The item that is stored in the Node
     */
    StackNode(T item) {
        this.item = item;
        this.next = null;
    }

    /**
     * StackNode
     * Constructor with an item and link (next)
     *
     * @param item The item that is stored in the Node
     * @param next The Node that this node is linked to
     */
    StackNode(T item, StackNode<T> next) {
        this.item = item;
        this.next = next;
    }

    /**
     * getNext
     * This method returns the Node that is linked as the next node
     *
     * @return StackNode, the Node that is next of this Node
     */
    public StackNode<T> getNext() {
        return this.next;
    }

    /**
     * getItem
     * This method returns the item that is stored in this Node
     *
     * @return T, the item stored in this Node
     */
    public T getItem() {
        return item;
    }
}
