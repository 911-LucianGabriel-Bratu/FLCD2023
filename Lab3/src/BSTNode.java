public class BSTNode {
    BSTNode leftChild;
    BSTNode rightChild;

    private Pair<Integer, Object> value;
    private static Integer position = 0;

    public BSTNode(Object o){
        this.leftChild = null;
        this.rightChild = null;
        this.value = new Pair<>(position, o);
        BSTNode.position += 1;
    }

    public BSTNode getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(BSTNode leftChild) {
        this.leftChild = leftChild;
    }

    public BSTNode getRightChild() {
        return rightChild;
    }

    public void setRightChild(BSTNode rightChild) {
        this.rightChild = rightChild;
    }

    public Pair<Integer, Object> getValue() {
        return value;
    }

    public void setValue(Pair<Integer, Object> value) {
        this.value = value;
    }

    public static Integer getPosition() {
        return position;
    }

    public static void setPosition(Integer position) {
        BSTNode.position = position;
    }
}
