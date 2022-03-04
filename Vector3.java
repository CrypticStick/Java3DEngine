/**
 * Represents a set of 3 floating-point values. 
 * Can be used to define 3D points or vectors in space.
 */
public class Vector3 {

    private double x;
    private double y;
    private double z;
    
    /**
     * Creates a vector in 3D space at the given coordinates.
     * 
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param z The z-coordinate.
     */
    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creates a vector in 3D space at the origin <0, 0, 0>.
     */
    public Vector3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
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
     * Gets the homogeneous Vector4 position of the current vector.
     * 
     * @return The Vector4 equivalent position.
     */
    public Vector4 toVector4() {
        return new Vector4(x, y, z, 1);
    }

    /**
     * Modifies the current vector's coordinates.
     * 
     * @param vector New coordinates.
     */
    public void set(Vector3 vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
    }

    /**
     * Gets the String representation of the current vector.
     * 
     * @return A formatted String representing this vector.
     */
    public String toString() {
        return String.format("<%f, %f, %f>", this.x, this.y, this.z);
    }

    /**
     * Gets the coordinates as an array.
     * 
     * @return An array of coordinates.
     */
    public double[] toArray() {
        return new double[] {x, y, z};
    }

    /**
     * Checks whether the given vector shares the same magnitude and direction.
     * 
     * @param vector The vector to compare.
     * @return Whether the vectors have equal magnitude and direction.
     */
    public boolean isEqual(Vector3 vector) {
        return this.x == vector.x && this.y == vector.y && this.z == vector.z;
    }

    /**
     * Translates the vector.
     * 
     * @param x Distance to move x.
     * @param y Distance to move y.
     * @param z Distance to move z.
     * @return The translated vector.
     */
    public Vector3 translate(double x, double y, double z) {
        return new Vector3(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Adds a vector to the current vector.
     * 
     * @param toAdd The vector to add.
     * @return The sum of two vectors.
     */
    public Vector3 add(Vector3 toAdd) {
        return new Vector3(
            this.x + toAdd.x, 
            this.y + toAdd.y, 
            this.z + toAdd.z);
    }

    /**
     * Subtracts a vector from the current vector.
     * 
     * @param toAdd The vector to subtract by.
     * @return The difference of two vectors.
     */
    public Vector3 subtract(Vector3 toSubtract) {
        return new Vector3(
            this.x - toSubtract.x, 
            this.y - toSubtract.y, 
            this.z - toSubtract.z);
    }

    /**
     * Calculates a dot product with the current vector.
     * 
     * @param toDot The vector to dot with.
     * @return The dot product of two vectors.
     */
    public double dot(Vector3 toDot) {
        return this.x * toDot.x + this.y * toDot.y + this.z * toDot.z;
    }

    /**
     * Calculates a cross product with the current vector.
     * The current vector comes first in the product.
     * 
     * @param toCross The vector to cross with.
     * @return The cross product of two vectors.
     */
    public Vector3 cross(Vector3 toCross) {
        return new Vector3(
            this.y * toCross.z - this.z * toCross.y,
            this.z * toCross.x - this.x * toCross.z,
            this.x * toCross.y - this.y * toCross.x
        );
    }

    /**
     * Scales the current vector's magnitude.
     * 
     * @param scalar The scalar to multiply by.
     * @return The scaled vector.
     */
    public Vector3 scale(double scalar) {
        return new Vector3(x * scalar, y * scalar, z * scalar);
    }

    /**
     * Scales the current vector's magnitude.
     * 
     * @param scalar The scalar to multiply by.
     * @return The scaled vector.
     */
    public Vector3 scale(Vector3 scalars) {
        return new Vector3(x * scalars.getX(), y * scalars.getY(), z * scalars.getZ());
    }

    /**
     * Gets the magnitude of the current vector.
     * 
     * @return The magnitude of the current vector.
     */
    public double magnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Normalizes the current vector so that its magnitude is 1.
     * 
     * @return The normalized vector.
     */
    public Vector3 normalize() {
        double magnitude = magnitude();
        return new Vector3(x / magnitude, y / magnitude, z / magnitude);
    }

    /**
     * Rotates the vector counterclockwise around the yaw (z) axis.
     * 
     * @param angle Radians to rotate around the z axis.
     * @param origin Vector pointing to the origin of rotation.
     * @return The rotated vector.
     */
    public Vector3 rotateYaw(double angle, Vector3 origin) {
        // Rotation works relative to the origin, so we must shift the vector.
        return subtract(origin).rotateYaw(angle).add(origin);
    }

    /**
     * Rotates the vector counterclockwise around the yaw (z) axis.
     * 
     * @param angle Radians to rotate around the z axis.
     * @return The rotated vector.
     */
    public Vector3 rotateYaw(double angle) {
        // Using rotation matrix Rz(θ) from https://en.wikipedia.org/wiki/Rotation_matrix
        return new Vector3(
            this.x * Math.cos(angle) - this.y * Math.sin(angle),
            this.x * Math.sin(angle) + this.y * Math.cos(angle),
            this.z
        );
    }

    /**
     * Rotates the vector counterclockwise around the pitch (y) axis.
     * 
     * @param angle Radians to rotate around the y axis.
     * @param origin Vector pointing to the origin of rotation.
     * @return The rotated vector.
     */
    public Vector3 rotatePitch(double angle, Vector3 origin) {
        // Rotation works relative to the origin, so we must shift the vector.
        return subtract(origin).rotatePitch(angle).add(origin);
    }

    /**
     * Rotates the vector counterclockwise around the pitch (y) axis.
     * 
     * @param angle Radians to rotate around the y axis.
     * @return The rotated vector.
     */
    public Vector3 rotatePitch(double angle) {
        // Using rotation matrix Ry(θ) from https://en.wikipedia.org/wiki/Rotation_matrix
        return new Vector3(
            this.x * Math.cos(angle) + this.z * Math.sin(angle),
            this.y,
            this.z * Math.cos(angle) - this.x * Math.sin(angle)
        );
    }

    /**
     * Rotates the vector counterclockwise around the roll (x) axis.
     * 
     * @param angle Radians to rotate around the x axis.
     * @param origin Vector pointing to the origin of rotation.
     * @return The rotated vector.
     */
    public Vector3 rotateRoll(double angle, Vector3 origin) {
        // Rotation works relative to the origin, so we must shift the vector.
        return subtract(origin).rotateRoll(angle).add(origin);
    }

    /**
     * Rotates the vector counterclockwise around the roll (x) axis.
     * 
     * @param angle Radians to rotate around the x axis.
     * @return The rotated vector.
     */
    public Vector3 rotateRoll(double angle) {
        // Using rotation matrix Rx(θ) from https://en.wikipedia.org/wiki/Rotation_matrix
        return new Vector3(
            this.x,
            this.y * Math.cos(angle) - this.z * Math.sin(angle),
            this.y * Math.sin(angle) + this.z * Math.cos(angle)
        );
    }

    /**
     * Rotates the vector counterclockwise around the origin (zyx rotation).
     * 
     * @param angle A vector defining counterclockwise angles around the x, y, and z axes.
     * @param origin Vector pointing to the origin of rotation.
     * @return The rotated vector.
     */
    public Vector3 rotate(Vector3 angle, Vector3 origin) {
        //Calculates intrinsic yaw-pitch-roll rotation
        return subtract(origin).rotate(angle).add(origin);
    }

    /**
     * Rotates the vector counterclockwise around the origin <0, 0, 0>  (zyx rotation).
     * 
     * @param angle A vector defining counterclockwise angles around the x, y, and z axes.
     * @return The rotated vector.
     */
    public Vector3 rotate(Vector3 angle) {
        //Calculates intrinsic yaw-pitch-roll rotation
        return rotateYaw(angle.z).rotatePitch(angle.y).rotateRoll(angle.x);
    }

    /**
     * Rotates the vector around the given point by a rotation quaternion.
     * 
     * @param rotation A rotation quaternion.
     * @param origin Vector pointing to the origin of rotation.
     * @return The rotated vector.
     */
    public Vector3 rotate(Quaternion rotation, Vector3 origin) {
        //Calculates passive rotation (coordinate system rotates with respect to point).
        return rotation
            .multiply(new Quaternion(0, subtract(origin)))
            .multiply(rotation.conjugate())
            .getImaginaryNumbers()
            .add(origin);
    }

    /**
     * Rotates the vector around the origin <0, 0, 0> by a rotation quaternion.
     * 
     * @param rotation A rotation quaternion.
     * @return The rotated vector.
     */
    public Vector3 rotate(Quaternion rotation) {
            //Calculates passive rotation (coordinate system rotates with respect to point).
            return rotation
                .multiply(new Quaternion(0, this))
                .multiply(rotation.conjugate())
                .getImaginaryNumbers();
    }

    /**
     * Rotates the vector around the origin <0, 0, 0> by a rotation quaternion.
     * 
     * @param rotation A rotation quaternion.
     * @return The rotated vector.
     */
    public Vector3 activeRotate(Quaternion rotation) {
        //Calculates passive rotation (coordinate system rotates with respect to point).
        return rotation.conjugate()
            .multiply(new Quaternion(0, this))
            .multiply(rotation)
            .getImaginaryNumbers();
    }
}

