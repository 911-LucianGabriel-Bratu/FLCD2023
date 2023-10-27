package symbol_table;

import java.util.Objects;
import java.util.Vector;

public class SymbolTable {
    private BSTNode root;

    public SymbolTable(){
        this.root = null;
    }

    public BSTNode getRoot() {
        return root;
    }

    public void setRoot(BSTNode root) {
        this.root = root;
    }

    public SymbolTable(BSTNode constantsNode){
        this.root = constantsNode;
    }

    public BSTNode insert(BSTNode rootNode, BSTNode insertNode){
        if(rootNode == null){
            rootNode = insertNode;
            return rootNode;
        } else if (rootNode.getValue().getFirst() < insertNode.getValue().getFirst()) {
            rootNode.setLeftChild(insert(rootNode.getLeftChild(), insertNode));
        } else if (rootNode.getValue().getFirst() > insertNode.getValue().getFirst()) {
            rootNode.setRightChild(insert(rootNode.getRightChild(), insertNode));
        }
        return rootNode;
    }

    public BSTNode search(BSTNode rootNode, Integer key){
        if(rootNode == null || Objects.equals(rootNode.getValue().getFirst(), key)){
            return rootNode;
        }

        if(rootNode.getValue().getFirst() > key){
            return search(rootNode.getLeftChild(), key);
        }

        return search(rootNode.getRightChild(), key);
    }

    public BSTNode searchForValue(BSTNode rootNode, Object value) {
        if (rootNode == null) {
            return null;
        }

        if (rootNode.getValue().getSecond().equals(value)) {
            return rootNode;
        }

        BSTNode leftResult = searchForValue(rootNode.leftChild, value);
        if (leftResult != null) {
            return leftResult;
        }

        return searchForValue(rootNode.rightChild, value);
    }

    public Object findValue(Integer key){
        return search(this.root, key).getValue().getSecond();
    }

    public BSTNode deleteNode(BSTNode rootNode, Integer key){
        if(rootNode == null){
            return null;
        }

        if(rootNode.getValue().getFirst() > key){
            rootNode.setLeftChild(deleteNode(rootNode.getLeftChild(), key));
        } else if (rootNode.getValue().getFirst() < key) {
            rootNode.setRightChild(deleteNode(rootNode.getRightChild(), key));
        }

        if(rootNode.getLeftChild() == null){
            return rootNode.getRightChild();
        } else if (rootNode.getRightChild() == null) {
            return rootNode.getLeftChild();
        }
        else{
            BSTNode successorParent = rootNode;
            BSTNode successor = rootNode.getRightChild();
            while(successor.getLeftChild() != null){
                successorParent = successor;
                successor = successor.getLeftChild();
            }

            if(successorParent != rootNode){
                successorParent.setLeftChild(successor.getRightChild());
            }
            else{
                successorParent.setRightChild(successor.getRightChild());
            }

            rootNode.setValue(successor.getValue());

            return rootNode;
        }
    }

    public void preOrder(BSTNode node){
        if(node == null){
            return;
        }
        System.out.println("(" + node.getValue().getFirst() + ", " + node.getValue().getSecond() + ") ");
        preOrder(node.getLeftChild());
        preOrder(node.getRightChild());
    }

    public String preOrderStr(BSTNode node) {
        if (node == null) {
            return "";
        }

        String nodeString = "(" + node.getValue().getFirst() + ", " + node.getValue().getSecond() + ")\n";
        String leftSubtree = preOrderStr(node.getLeftChild());
        String rightSubtree = preOrderStr(node.getRightChild());

        return nodeString + leftSubtree + rightSubtree;
    }

    public void storeBSTNodes(BSTNode rootNode, Vector<BSTNode> nodes){
        if(rootNode == null){
            return;
        }
        storeBSTNodes(rootNode.getRightChild(), nodes);
        nodes.add(rootNode);
        storeBSTNodes(rootNode.getLeftChild(), nodes);
    }

    public BSTNode buildTreeUtil(Vector<BSTNode> nodes, int start, int end){
        if(start > end){
            return null;
        }

        int mid = (start + end)/2;
        BSTNode node = nodes.get(mid);

        node.setLeftChild(buildTreeUtil(nodes, start, mid-1));
        node.setRightChild(buildTreeUtil(nodes, mid + 1, end));

        return node;
    }

    public BSTNode buildTree(BSTNode rootNode){
        Vector<BSTNode> nodes = new Vector<>();
        storeBSTNodes(rootNode, nodes);

        int n = nodes.size();
        return buildTreeUtil(nodes, 0, n-1);
    }
}
