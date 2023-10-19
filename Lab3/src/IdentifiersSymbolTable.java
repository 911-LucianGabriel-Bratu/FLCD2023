import java.util.Objects;
import java.util.Vector;

public class IdentifiersSymbolTable {
    private IdentifiersNode root;
    
    public IdentifiersSymbolTable(){
        this.root = null;
    }
    
    public IdentifiersSymbolTable(IdentifiersNode node){
        this.root = node;
    }

    public IdentifiersNode getRoot() {
        return root;
    }

    public void setRoot(IdentifiersNode root) {
        this.root = root;
    }
    
    public IdentifiersNode insert(IdentifiersNode rootNode, IdentifiersNode identifiersNode){
        if(rootNode == null){
            rootNode = identifiersNode;
            return rootNode;
        } else if (rootNode.getValue().getFirst().compareTo(identifiersNode.getValue().getFirst()) < 0) {
            rootNode.setLeftChild(insert(rootNode.getLeftChild(), identifiersNode));
        } else if (rootNode.getValue().getFirst().compareTo(identifiersNode.getValue().getFirst()) > 0) {
            rootNode.setRightChild(insert(rootNode.getRightChild(), identifiersNode));
        }
        return rootNode;
    }

    public IdentifiersNode search(IdentifiersNode rootNode, String key){
        if(rootNode == null || Objects.equals(rootNode.getValue().getFirst(), key)){
            return rootNode;
        }

        if(root.getValue().getFirst().compareTo(key) < 0){
            return search(rootNode.getRightChild(), key);
        }

        return search(rootNode.getLeftChild(), key);
    }

    public IdentifiersNode deleteNode(IdentifiersNode rootNode, String key){
        if(rootNode == null){
            return null;
        }

        if(rootNode.getValue().getFirst().compareTo(key) > 0){
            rootNode.setLeftChild(deleteNode(rootNode.getLeftChild(), key));
            return rootNode;
        } else if (rootNode.getValue().getFirst().compareTo(key) < 0) {
            rootNode.setRightChild(deleteNode(rootNode.getRightChild(), key));
            return rootNode;
        }

        if(root.getLeftChild() == null){
            return rootNode.getRightChild();
        } else if (rootNode.getRightChild() == null) {
            return rootNode.getLeftChild();
        }
        else{
            IdentifiersNode successorParent = rootNode;
            IdentifiersNode successor = rootNode.getRightChild();
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

    public void preOrder(IdentifiersNode identifiersNode){
        if(identifiersNode == null){
            return;
        }
        System.out.println("(" + identifiersNode.getValue().getFirst() + ", " + identifiersNode.getValue().getSecond() + ") ");
        preOrder(identifiersNode.getLeftChild());
        preOrder(identifiersNode.getRightChild());
    }

    public void storeBSTNodes(IdentifiersNode rootNode, Vector<IdentifiersNode> nodes){
        if(rootNode == null){
            return;
        }
        storeBSTNodes(rootNode.getRightChild(), nodes);
        nodes.add(rootNode);
        storeBSTNodes(rootNode.getLeftChild(), nodes);
    }

    public IdentifiersNode buildTreeUtil(Vector<IdentifiersNode> nodes, int start, int end){
        if(start > end){
            return null;
        }

        int mid = (start + end)/2;
        IdentifiersNode node = nodes.get(mid);

        node.setLeftChild(buildTreeUtil(nodes, start, mid-1));
        node.setRightChild(buildTreeUtil(nodes, mid + 1, end));

        return node;
    }

    public IdentifiersNode buildTree(IdentifiersNode rootNode){
        Vector<IdentifiersNode> nodes = new Vector<>();
        storeBSTNodes(rootNode, nodes);

        int n = nodes.size();
        return buildTreeUtil(nodes, 0, n-1);
    }
}
