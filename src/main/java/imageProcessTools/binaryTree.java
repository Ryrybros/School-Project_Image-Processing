package imageProcessTools;

public class binaryTree<T extends Comparable<T>> {
    private T value;
    public binaryTree<T> left;
    public binaryTree<T> right;
    public boolean isXNode = false; // False by default

    public binaryTree(T initValue, binaryTree<T> leftChild, binaryTree<T> rightChild, boolean xNode) {
        this.value = initValue;
        this.left = leftChild;
        this.right = rightChild;
        this.isXNode = xNode;
    }

    public T getValue(){
        return value;
    }

    public boolean insert(binaryTree<T> b_tree, T value) { // Returns true if inserted successfully
        if (value == null){
            return false;
        }
        if (value.compareTo(b_tree.value) == 0) {
            return false; // Object already there
        } else if (value.compareTo(b_tree.value) > 0) {
            if (b_tree.right == null) { // If no right child and value > nodeValue, create right child
                b_tree.right = new binaryTree<T>(value, null, null, false);
                return true;
            } else {
                return insert(b_tree.right, value); // Else continue within right child
            }
        } else {
            if (b_tree.left == null) {
                b_tree.left = new binaryTree<T>(value, null, null, false); // If no left child and value < nodeValue, create left child
                return true;
            } else {
                return insert(b_tree.left, value); // Else continue within
            }
        }
    }
}