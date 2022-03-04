/**
 * Stores information about a triangle face.
 */
public class Triangle {
    private int[] vertices;
    private int[] normals;
    private int[] texCoords;
    private int[] colors;

    /**
     * Creates a triangle with the given vertex attributes.
     * 
     * @param vertices Indices of vertex coordinates.
     * @param normals Indices of vertex normals.
     * @param texCoords Indices of vertex texture coordinates.
     */
    public Triangle(int[] vertices, int[] normals, int[] texCoords) {
        this.vertices = vertices;
        this.normals = normals;
        this.texCoords = texCoords;
    }

    /**
     * Creates a triangle, assuming all vertex attributes share the same indices.
     * 
     * @param vertices Indices of vertex coordinates.
     */
    public Triangle(int[] vertices) {
        this.vertices = vertices;
        this.normals = vertices;
        this.texCoords = vertices;
    }

    /**
     * Creates a new triangle.
     */
    public Triangle() {
        vertices = new int[3];
        normals = new int[3];
        texCoords = new int[3];
    }

    /**
     * Sets the vertex attributes at the given index (domain [0, 2]).
     * If an optional attribute doesn't exist, its value should be -1.
     * 
     * @param index Index of vertex on triangle.
     * @param v Index of vertex coordinates.
     * @param vn Index of vertex normal (optional).
     * @param vt Index of vertex texture coords (optional).
     */
    public void setVertex(int index, int v, int vn, int vt) {
        vertices[index] = v;
        normals[index] = vn;
        texCoords[index] = vt;
    }

    /**
     * Returns an array containing the indices of every vertices' coordinates.
     * 
     * @return The vertex coordinates of the triangle.
     */
    public int[] getCoords() {
        return vertices;
    }

    /**
     * Gets the index of the specified vertices' coordinates.
     * 
     * @param index The index of the vertex desired (Domain: [0, 2]).
     * 
     * @return The vertex coordinates.
     */
    public int getCoord(int index) {
        return vertices[index];
    }

    /**
     * Returns an array containing the indices of every vertices' normal.
     * 
     * @return The vertex normals of the triangle.
     */
    public int[] getNorms() {
        return normals;
    }

    /**
     * Gets the index of the specified vertices' normal.
     * 
     * @param index The index of the vertex desired (Domain: [0, 2]).
     * 
     * @return The vertex normal.
     */
    public int getNorm(int index) {
        return normals[index];
    }

    /**
     * Returns an array containing the indices of every vertices' texture coordinates.
     * 
     * @return The vertex texture coordinates of the triangle.
     */
    public int[] getTexCoords() {
        return texCoords;
    }

    /**
     * Gets the index of the specified vertices' texture coordinates.
     * 
     * @param index The index of the vertex desired (Domain: [0, 2]).
     * 
     * @return The vertex texture coordinates.
     */
    public int getTexCoord(int index) {
        return texCoords[index];
    }

    /**
     * Returns an array containing the indices of every vertices' color.
     * 
     * @return The vertex colors of the triangle.
     */
    public int[] getColors() {
        return colors;
    }

    /**
     * Gets the index of the specified vertices' color.
     * 
     * @param index The index of the vertex desired (Domain: [0, 2]).
     * 
     * @return The vertex color.
     */
    public int getColor(int index) {
        return colors[index];
    }
}
