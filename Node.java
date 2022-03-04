import java.util.LinkedList;
import java.util.List;

/** 
 * Represents a hierarchical entity in 3D space.
 */
public class Node {

    protected Vector3 lTranslation;
    protected Quaternion lOrientation;

    // Parent node is essential for climbing up the 3D hierarchy to calculate absolute positions.
    private Node parentNode;
    private LinkedList<Node> childNodes = new LinkedList<>();


    /**
     * Create a 3D entity with the given translation and rotation.
     * If creating a child node, its orientation will be relative to the parent node.
     * Note: By default, the 3D space is oriented along the positive z-axis.
     * 
     * @param position The relative position of the node.
     * @param orientation The relative orientation of the node.
     */
    public Node(Vector3 translation, Quaternion orientation) {
        this.lTranslation = translation;
        this.lOrientation = orientation;
    }

    
    /**
     * Create a 3D entity with the given translation and rotation.
     * If creating a child node, its orientation will be relative to the parent node.
     * Note: By default, the 3D space is oriented along the positive z-axis.
     * 
     * @param rotation The relative rotation of the node.
     */
    public Node(Quaternion orientation) {
        this.lTranslation = new Vector3();
        this.lOrientation = orientation;
    }

    /**
     * Create a 3D entity with the given translation and rotation.
     * If creating a child node, its orientation will be relative to the parent node.
     * Note: By default, the 3D space is oriented along the positive z-axis.
     */
    public Node() {
        this.lTranslation = new Vector3();
        this.lOrientation = new Quaternion();
    }

    /**
     * Adds a child node to the current node.
     * 
     * @param child The child node to add.
     */
    public void addChild(Node child) {
        if (child.parentNode != null) {
            System.err.println("Child node already has an assigned parent.");
            System.exit(-1);
        }
        childNodes.add(child);
        child.parentNode = this;
    }

    /**
     * Gets the direct children of the current node.
     * 
     * @return A list of children nodes.
     */
    public List<Node> getChildren() {
        return childNodes;
    }

    /**
     * Rotates the node by the given rotation quaternion.
     *
     * @param rotation The rotation quaternion to apply.
     */
    public void rotate(Quaternion rotation) {
        lOrientation = lOrientation.multiply(rotation);
    }

    /**
     * Translates the node by the given distance.
     * 
     * @param translation The translation vector to apply.
     */
    public void translate(Vector3 translation) {
        lTranslation = lTranslation.add(translation);
    }

    /**
     * Gets the local position of the node.
     * 
     * @return The node's local position.
     */
    public Vector3 getLocalPosition() {
        return lTranslation;
    }
    
    /**
     * Gets the global position of the node.
     * 
     * @return The node's global position.
     */
    public Vector3 getGlobalPosition() {
        if (parentNode != null) {
            Vector3 parentPosition = parentNode.getGlobalPosition();
            return lTranslation.rotate(parentNode.getLocalOrientation()).add(parentPosition);
        }
        return lTranslation;
    }

    /**
     * Gets the local orientation of the node.
     * 
     * @return The node's local orientation quaternion.
     */
    public Quaternion getLocalOrientation() {
        return lOrientation;
    }

    /**
     * Gets the global orientation of the node.
     * 
     * @return The node's global orientation quaternion.
     */
    public Quaternion getGlobalOrientation() {
        if (parentNode != null) {
            return parentNode.getGlobalOrientation().multiply(lOrientation);
        }
        return lOrientation;
    }
}
