public class IdentifiersNode {
    private IdentifiersNode leftChild;

    private IdentifiersNode rightChild;

    private Pair<String, Integer> value;

    public IdentifiersNode(Pair<String, Integer> value){
        this.leftChild = null;
        this.rightChild = null;
        this.value = value;
    }

    public IdentifiersNode(String identifier){
        this.leftChild = null;
        this.rightChild = null;
        this.value = new Pair<>(identifier, -1);
    }

    public IdentifiersNode getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(IdentifiersNode leftChild) {
        this.leftChild = leftChild;
    }

    public IdentifiersNode getRightChild() {
        return rightChild;
    }

    public void setRightChild(IdentifiersNode rightChild) {
        this.rightChild = rightChild;
    }

    public Pair<String, Integer> getValue() {
        return value;
    }

    public void setValue(Pair<String, Integer> value) {
        this.value = value;
    }
}
