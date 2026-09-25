package ARVBIN;

public class BST extends BinaryTree {

    public BST() {
        super();
    }
    
    public BTNode search(String id) {
        return SearchNode(root, id);
    }
    
    public BTNode SearchNode(BTNode node, String id) {

	    if (node == null) {
	        return null;
	    }

	    else if (node.getData().equals(id)) {
	        return node;
	    }

	    else if (id.compareTo(node.getData()) < 0) {
	        return SearchNode(node.getLeft(), id);
	    }

	    else {
	        return SearchNode(node.getRight(), id);
	    }
	}
	
    
    public void insert(String id) {
	    root = InsertNode(null, root, id);

	    if (root != null) {
	        root.setParent(null);
	    }
	}
	
	public BTNode InsertNode(BTNode parent, BTNode node, String id) {

	    if (node == null) {
	        node = new BTNode(id, parent);
	        node.setParent(parent);
	        return node;
	    }

	    else if (id.compareTo(node.getData()) < 0) {
	        node.setLeft(
	            InsertNode(node, node.getLeft(), id)
	        );
	    }

	    else if (id.compareTo(node.getData()) > 0) {
	        node.setRight(
	            InsertNode(node, node.getRight(), id)
	        );
	    }

	    return node;
	}
	
	public void remove(String id) {
	    root = RemoveNode(root, id);

	    if (root != null) {
	        root.setParent(null);
	    }
	}

	
	public BTNode RemoveNode(BTNode node, String id) {

	    if (node == null) {
	        return null;
	    }

	    if (id.compareTo(node.getData()) < 0) {
	        node.setLeft(RemoveNode(node.getLeft(), id));
	    }

	    else if (id.compareTo(node.getData()) > 0) {
	        node.setRight(RemoveNode(node.getRight(), id));
	    }

	    else {
	        // CASO 1: folha
	        if (node.getLeft() == null && node.getRight() == null) {
	            return null;
	        }

	        // CASO 2: só tem filho direito
	        else if (node.getLeft() == null) {
	            BTNode right = node.getRight();

	            if (right != null) {
	                right.setParent(node.getParent());
	            }

	            return right;
	        }

	        // CASO 3: só tem filho esquerdo
	        else if (node.getRight() == null) {
	            BTNode left = node.getLeft();

	            if (left != null) {
	                left.setParent(node.getParent());
	            }

	            return left;
	        }

	        // CASO 4: tem dois filhos
	        else {
	            BTNode predecessor = FindMax(node.getLeft());

	            node.setData(predecessor.getData());

	            node.setLeft(
	                RemoveNode(node.getLeft(), predecessor.getData())
	            );
	        }
	    }

	    return node;
	}
	
	public BTNode findMin() {
	    return FindMin(root);
	}

	public BTNode findMax() {
	    return FindMax(root);
	}
	
	private BTNode FindMax(BTNode node) {

	    if (node == null) {
	        return null;
	    }

	    while (node.getRight() != null) {
	        node = node.getRight();
	    }

	    return node;
	}
	
	private BTNode FindMin(BTNode node) {

	    if (node == null) {
	        return null;
	    }

	    while (node.getLeft() != null) {
	        node = node.getLeft();
	    }

	    return node;
	}
	
	public BTNode findPredecessor(String id) {
	    return FindPredecessor(id);
	}

	public BTNode findSuccessor(String id) {
	    return FindSuccessor(id);
	}

	
	public BTNode FindPredecessor(String id) {

	    BTNode node = SearchNode(root, id);

	    if (node == null) {
	        return null;
	    }

	    if (node.getLeft() != null) {
	        return FindMax(node.getLeft());
	    }

	    BTNode parent = node.getParent();

	    while (parent != null && node == parent.getLeft()) {
	        node = parent;
	        parent = parent.getParent();
	    }

	    return parent;
	}
	
	
	public BTNode FindSuccessor(String id) {

	    BTNode node = SearchNode(root, id);

	    if (node == null) {
	        return null;
	    }

	    if (node.getRight() != null) {
	        return FindMin(node.getRight());
	    }

	    BTNode parent = node.getParent();

	    while (parent != null && node == parent.getRight()) {
	        node = parent;
	        parent = parent.getParent();
	    }

	    return parent;
	}
	
	public void clear() {
	    root = null;
	}
	
}