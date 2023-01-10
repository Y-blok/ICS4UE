/**
 * public class HuffmanTree
 * 03-31-19
 * This class is a binary tree data structure that is used specifically for decoding a .mzip file
 *
 * @param <E> Generic object used in the Tree (Node item)
 * @author Kolby Chong
 */
public class HuffmanTree<E> {
    //Class variables
    private BinaryTreeNode<E> root;
    private BinaryTreeNode<E> traverseNode;

    /**
     * HuffmanTree
     * Constructor with no item
     */
    HuffmanTree(){
        root = new BinaryTreeNode<E>();
        traverseNode = root;
    }

    /**
     * HuffmanTree
     * Constructor with an item
     *
     * @param item The item that will be stored in the BinaryTreeNode at the root
     */
    HuffmanTree(E item){
        root = new BinaryTreeNode<E>(item);
    }

    /**
     * mergeNodes
     * Takes in two HuffmanTree objects and sets them to the left and right links of the root
     *
     * @param tree1 Tree that is set to the root's left link
     * @param tree2 Tree that is set to the root's right link
     */
    public void mergeNodes(HuffmanTree<E> tree1, HuffmanTree<E> tree2){
        root.setLeft(tree1.getRoot()); //Set root's left as tree1's root
        root.setRight(tree2.getRoot()); //Set root's right as tree2's root
    }

    /**
     * getRoot
     * Returns the root of the tree on method call
     *
     * @return BinaryTreeNode, the root of this HuffmanTree
     */
    public BinaryTreeNode<E> getRoot(){
        return root;
    }

    /**
     * traverse
     * Traverses the tree using the tempNode variable after taking in a character representing a direction,
     * then returns tempNode's stored item
     *
     * @param direction The character representation of the direction to traverse
     * @return E, The item stored in the traverseNode or null
     */
    public E traverse(char direction){
        if (direction == '0'){ //Go left if binary value was '0'
            traverseNode = traverseNode.getLeft();
        } else { //Go right if binary value was '1'
            traverseNode = traverseNode.getRight();
        }
        //Returns tempNode's item. May be null.
        return traverseNode.getItem();
    }

    /**
     * reset
     * Brings traverseNode back to the tree's root on method call
     */
    public void reset(){
        traverseNode = root;
    }

}

/**
 * class BinaryTreeNode
 * 03-31-19
 * This class is for the individual nodes for the HuffmanTree class. It has methods used for traversing
 * and returning information
 *
 * @param <T> Generic object used in the Node (item)
 * @author Kolby Chong
 */
class BinaryTreeNode<T> {
    //Class variables
    private BinaryTreeNode<T> left;
    private BinaryTreeNode<T> right;
    private T item;

    /**
     * BinaryTreeNode
     * Constructor setting class variables to null
     */
    BinaryTreeNode(){
        this.item = null;
        this.left = null;
        this.right = null;
    }

    /**
     * BinaryTreeNode
     * Constructor with an item, storing the item and assigning links to null
     *
     * @param item Item of type T that is stored as this Node's item
     */
    BinaryTreeNode(T item){
        this.item = item;
        this.left = null;
        this.right = null;
    }

    /**
     * getLeft
     * This method returns whatever is linked to this Node's left
     *
     * @return BinaryTreeNode, this Node's left link
     */
    public BinaryTreeNode<T> getLeft(){
        return left;
    }

    /**
     * getRight
     * This method returns whatever is linked to this Node's right
     *
     * @return BinaryTreeNode, this Node's left link
     */
    public BinaryTreeNode<T> getRight(){
        return right;
    }

    /**
     * setLeft
     * This method takes in a BinaryTreeNode and sets the left link to that Node
     *
     * @param left The BinaryTreeNode that this Node will be linked to on the left
     */
    public void setLeft(BinaryTreeNode<T> left){
        this.left = left;
    }

    /**
     * setRight
     * This method takes in a BinaryTreeNode and sets the right link to that Node
     *
     * @param right The BinaryTreeNode that this Node will be linked to on the right
     */
    public void setRight(BinaryTreeNode<T> right){
        this.right = right;
    }

    /**
     * getItem
     * This method returns whatever is stored as this Node's item
     *
     * @return T, this Node's item
     */
    public T getItem(){
        return item;
    }

}

