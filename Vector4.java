/**
 * Represents a set of 4 floating-point values. 
 * Can be used to define 4D points or homogeneous 3D vectors.
 */
public class Vector4 {

    private double x;
    private double y;
    private double z;
    private double w;
    
    /**
     * Creates a vector in 4D space at the given coordinates.
     * 
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param z The z-coordinate.
     * @param w The w-coordinate.
     */
    public Vector4(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Creates a homogeneous vector in 4D space at the given coordinates.
     * 
     * @param vector3 A 3D vector.
     */
    public Vector4(Vector3 vector3) {
        this.x = vector3.getX();
        this.y = vector3.getY();
        this.z = vector3.getZ();
        this.w = 1.0;
    }

    /**
     * Creates a vector in 4D space at the origin <0, 0, 0, 0>.
     */
    public Vector4() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }

    /**
     * Gets the x-coordinate of the vector.
     * 
     * @return The x-coordinate of the vector.
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the vector.
     * 
     * @return The y-coordinate of the vector.
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the z-coordinate of the vector.
     * 
     * @return The z-coordinate of the vector.
     */
    public double getZ() {
        return z;
    }

    /**
     * Gets the w-coordinate of the vector.
     * 
     * @return The w-coordinate of the vector.
     */
    public double getW() {
        return w;
    }

    /**
     * Gets the Vector3 position of the current vector, ignoring the 4th dimension.
     * 
     * @return The Vector3 equivalent position.
     */
    public Vector3 toVector3() {
        return new Vector3(x, y, z);
    }

    /**
     * Gets the String representation of the current vector.
     * 
     * @return A formatted String representing this vector.
     */
    public String toString() {
        return String.format("<%f, %f, %f, %f>", this.x, this.y, this.z, this.w);
    }

    /**
     * Checks whether the given vector shares the same magnitude and direction.
     * 
     * @param vector The vector to compare.
     * @return Whether the vectors have equal magnitude and direction.
     */
    public boolean isEqual(Vector4 vector) {
        return this.x == vector.x && this.y == vector.y && this.z == vector.z && 
            this.w == vector.w;
    }

    /**
     * Translates the vector.
     * 
     * @param x Distance to move x.
     * @param y Distance to move y.
     * @param z Distance to move z.
     * @param w Distance to move w.
     * @return The translated vector.
     */
    public Vector4 translate(double x, double y, double z, double w) {
        return new Vector4(this.x + x, this.y + y, this.z + z, this.w + w);
    }

    /**
     * Adds a vector to the current vector.
     * 
     * @param toAdd The vector to add.
     * @return The sum of two vectors.
     */
    public Vector4 add(Vector4 toAdd) {
        return new Vector4(
            this.x + toAdd.x, 
            this.y + toAdd.y, 
            this.z + toAdd.z,
            this.w + toAdd.w);
    }

    /**
     * Subtracts a vector from the current vector.
     * 
     * @param toAdd The vector to subtract by.
     * @return The difference of two vectors.
     */
    public Vector4 subtract(Vector4 toSubtract) {
        return new Vector4(
            this.x - toSubtract.x, 
            this.y - toSubtract.y, 
            this.z - toSubtract.z,
            this.w - toSubtract.w);
    }

    /**
     * Calculates a dot product with the current vector.
     * 
     * @param toDot The vector to dot with.
     * @return The dot product of two vectors.
     */
    public double dot(Vector4 toDot) {
        return this.x * toDot.x + this.y * toDot.y + this.z * toDot.z + this.w * toDot.w;
    }

    /**
     * Scales the current vector's magnitude.
     * 
     * @param scalar The scalar to multiply by.
     * @return The scaled vector.
     */
    public Vector4 scale(double scalar) {
        return new Vector4(x * scalar, y * scalar, z * scalar, w* scalar);
    }

    /**
     * Gets the magnitude of the current vector.
     * 
     * @return The magnitude of the current vector.
     */
    public double magnitude() {
        return Math.sqrt(x * x + y * y + z * z + w * w);
    }

    /**
     * Returns the vector as an array of coordinates.
     * 
     * @return The vector in array form.
     */
    public double[] asArray() {
        return new double[] { x, y, z, w };
    }

    /**
     * Normalizes the current vector so that its magnitude is 1.
     * 
     * @return The normalized vector.
     */
    public Vector4 normalize() {
        double magnitude = magnitude();
        return new Vector4(x / magnitude, y / magnitude, z / magnitude, w / magnitude);
    }
}