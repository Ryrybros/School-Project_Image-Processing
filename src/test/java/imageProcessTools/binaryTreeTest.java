package imageProcessTools;

import org.junit.jupiter.api.Test;

class binaryTreeTest {
    binaryTree<Integer> testTree = new binaryTree<>(10, null, null, true);

    @Test
    void insert() {
        testTree.insert(testTree, 5);
        assert(testTree.left != null);
        testTree.insert(testTree, 42);
        assert(testTree.right.getValue() == 42);
        testTree.insert(testTree, null);
    }


    @Test
    void getValue() {
        testTree.insert(testTree, 42);
        assert(testTree.right.getValue() == 42);
    }

}