public class ConstantsNode {
    private ConstantsNode leftChild;
    private ConstantsNode rightChild;
    private Pair<Integer, Object> value;
    private static Integer position = 0;

    public ConstantsNode(Object o){
        this.leftChild = null;
        this.rightChild = null;
        this.value = new Pair<>(position, o);
        ConstantsNode.position += 1;
    }

    public ConstantsNode getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(ConstantsNode leftChild) {
        this.leftChild = leftChild;
    }

    public ConstantsNode getRightChild() {
        return rightChild;
    }

    public void setRightChild(ConstantsNode rightChild) {
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
        ConstantsNode.position = position;
    }
}
