import java.util.Objects;
import java.util.Vector;

public class ConstantsSymbolTable {
    private ConstantsNode root;

    public ConstantsSymbolTable(){
        this.root = null;
    }

    public ConstantsNode getRoot() {
        return root;
    }

    public void setRoot(ConstantsNode root) {
        this.root = root;
    }

    public ConstantsSymbolTable(ConstantsNode constantsNode){
        this.root = constantsNode;
    }

    public ConstantsNode insert(ConstantsNode rootConstantsNode, ConstantsNode insertConstantsNode){
        if(rootConstantsNode == null){
            rootConstantsNode = insertConstantsNode;
            return rootConstantsNode;
        } else if (rootConstantsNode.getLeftChild().getValue().getFirst() < insertConstantsNode.getValue().getFirst()) {
            rootConstantsNode.setLeftChild(insert(rootConstantsNode.getLeftChild(), insertConstantsNode));
        } else if (rootConstantsNode.getLeftChild().getValue().getFirst() > insertConstantsNode.getValue().getFirst()) {
            rootConstantsNode.setRightChild(insert(rootConstantsNode.getRightChild(), insertConstantsNode));
        }
        return rootConstantsNode;
    }

    public ConstantsNode search(ConstantsNode rootConstantsNode, Integer key){
        if(rootConstantsNode == null || Objects.equals(rootConstantsNode.getValue().getFirst(), key)){
            return rootConstantsNode;
        }

        if(rootConstantsNode.getValue().getFirst() > key){
            return search(rootConstantsNode.getLeftChild(), key);
        }

        return search(rootConstantsNode.getRightChild(), key);
    }

    public ConstantsNode deleteNode(ConstantsNode rootConstantsNode, Integer key){
        if(rootConstantsNode == null){
            return null;
        }

        if(rootConstantsNode.getValue().getFirst() > key){
            rootConstantsNode.setLeftChild(deleteNode(rootConstantsNode.getLeftChild(), key));
        } else if (rootConstantsNode.getValue().getFirst() < key) {
            rootConstantsNode.setRightChild(deleteNode(rootConstantsNode.getRightChild(), key));
        }

        if(rootConstantsNode.getLeftChild() == null){
            return rootConstantsNode.getRightChild();
        } else if (rootConstantsNode.getRightChild() == null) {
            return rootConstantsNode.getLeftChild();
        }
        else{
            ConstantsNode successorParent = rootConstantsNode;
            ConstantsNode successor = rootConstantsNode.getRightChild();
            while(successor.getLeftChild() != null){
                successorParent = successor;
                successor = successor.getLeftChild();
            }

            if(successorParent != rootConstantsNode){
                successorParent.setLeftChild(successor.getRightChild());
            }
            else{
                successorParent.setRightChild(successor.getRightChild());
            }

            rootConstantsNode.setValue(successor.getValue());

            return rootConstantsNode;
        }
    }

    public void preOrder(ConstantsNode constantsNode){
        if(constantsNode == null){
            return;
        }
        System.out.println("(" + constantsNode.getValue().getFirst() + ", " + constantsNode.getValue().getSecond() + ") ");
        preOrder(constantsNode.getLeftChild());
        preOrder(constantsNode.getRightChild());
    }

    public void storeBSTNodes(ConstantsNode rootNode, Vector<ConstantsNode> nodes){
        if(rootNode == null){
            return;
        }
        storeBSTNodes(rootNode.getRightChild(), nodes);
        nodes.add(rootNode);
        storeBSTNodes(rootNode.getLeftChild(), nodes);
    }

    public ConstantsNode buildTreeUtil(Vector<ConstantsNode> nodes, int start, int end){
        if(start > end){
            return null;
        }

        int mid = (start + end)/2;
        ConstantsNode node = nodes.get(mid);

        node.setLeftChild(buildTreeUtil(nodes, start, mid-1));
        node.setRightChild(buildTreeUtil(nodes, mid + 1, end));

        return node;
    }

    ConstantsNode buildTree(ConstantsNode rootNode){
        Vector<ConstantsNode> nodes = new Vector<>();
        storeBSTNodes(rootNode, nodes);

        int n = nodes.size();
        return buildTreeUtil(nodes, 0, n-1);
    }
}
