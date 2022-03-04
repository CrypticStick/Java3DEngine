/**
 * A quaternion is used to represent orientation in 3D space.
 */
public class Quaternion {
    
    // The quaternion a + bi + cj + dk can be represented by a real number and a vector of 
    // imaginary numbers.
    private double w;
    private Vector3 v;

    /**
     * Creates a quaternion with the given components.
     * 
     * @param w The real number value of the quaternion.
     * @param v A vector with imaginary components i, j, and k.
     */
    public Quaternion(double w, Vector3 v) {
        this.w = w;
        this.v = v;
    }


    /**
     * Creates a quaternion with the given orientation.
     * 
     * @param axis A unit vector whose direction represents the axis of rotation.
     * @param angle The angle to rotate around the given axis (in radians).
     */
    public Quaternion(Vector3 axis, double angle) {
        // Formula from https://danceswithcode.net/engineeringnotes/quaternions/quaternions.html
        double sA = Math.sin(angle / 2);
        this.w = Math.cos(angle/2);
        this.v = new Vector3(
            axis.getX() * sA,
            axis.getY() * sA,
            axis.getZ() * sA
            );
    }

    /**
     * Creates a quaternion with the given orientation.
     * 
     * @param angles Angles of rotation around the coordinate axes (zyx rotation, in radians).
     */
    public Quaternion(Vector3 angles) {
        // Formula from https://danceswithcode.net/engineeringnotes/quaternions/quaternions.html
        double cX = Math.cos(angles.getX() / 2);
        double cY = Math.cos(angles.getY() / 2);
        double cZ = Math.cos(angles.getZ() / 2);
        double sX = Math.sin(angles.getX() / 2);
        double sY = Math.sin(angles.getY() / 2);
        double sZ = Math.sin(angles.getZ() / 2);

        this.w = cX * cY * cZ + sX * sY * sZ;
        this.v = new Vector3(
            sX * cY * cZ - cX * sY * sZ,
            cX * sY * cZ + sX * cY * sZ,
            cX * cY * sZ - sX * sY * cZ
        );
    }

    /**
     * Creates an identity quaternion with no rotation.
     */
    public Quaternion() {
        this.w = 1;
        this.v = new Vector3();
    }

    /**
     * Gets the quaternion's axis of rotation.
     * If the quaternion's angle is zero, this will return a zero vector.
     * 
     * @return The axis of rotation (or a zero vector, if the angle is zero).
     */
    public Vector3 getAxis() {
        // Formula from https://danceswithcode.net/engineeringnotes/quaternions/quaternions.html
        if (w != 1.0) {
            double sA = Math.sin(getAngle() / 2);
            return new Vector3(
                v.getX() / sA,
                v.getY() / sA,
                v.getZ() / sA
            );
        }
        return new Vector3();
    }

    /**
     * Gets the real number value of the quaternion.
     * 
     * @return The quaternion's real number.
     */
    public double getRealNumber() {
        return this.w;
    }

    /**
     * Gets the imaginary numbers i, j, and k from the quaternion.
     * 
     * @return The quaternion's imaginary numbers.
     */
    public Vector3 getImaginaryNumbers() {
        return this.v;
    }

    /**
     * Gets the quaternion's angle around its axis of rotation.
     * 
     * @return The angle around the axis of rotation (in radians).
     */
    public double getAngle() {
        return 2* Math.acos(this.w);
    }

    /**
     * Gets the quaternion's orientation as Euler angles (zyx rotation, in radians).
     * 
     * @return The quaternion's orientation as Euler angles.
     */
    public Vector3 getAngles() {
        // Formulas from https://danceswithcode.net/engineeringnotes/quaternions/quaternions.html
        double pitch = Math.asin(
            2 * (w * v.getY() - v.getX() * v.getZ())
        );
        double roll;
        double yaw;

        // Avoid "gimbal lock" when pitch is exactly +-90 degrees.
        // This video does a great job at explaining the issue: 
        // https://www.youtube.com/watch?v=zc8b2Jo7mno&t=30s
        if (Math.abs(pitch) == Math.PI/2) {
            roll = 0;
            yaw = Math.signum(pitch) * -2 * Math.atan2(v.getX(), w);
        } else {
            roll = Math.atan2(
                2 * (w * v.getX() + v.getY() * v.getZ()),
                w * w - v.getX() * v.getX() - v.getY() * v.getY() + v.getZ() * v.getZ()
            );

            yaw = Math.atan2(
                2 * (w * v.getZ() + v.getX() * v.getY()),
                w * w + v.getX() * v.getX() - v.getY() * v.getY() - v.getZ() * v.getZ()
            );
        }

        return new Vector3(roll, pitch, yaw);
    }

    /**
     * Gets the quaternion's orientation as a rotation matrix.
     * 
     * @return The quaternion's rotation matrix.
     */
    public Matrix44 rotationMatrix() {
        // Formula from https://danceswithcode.net/engineeringnotes/quaternions/quaternions.html
        return new Matrix44(new double[][] {
            {
                1 - 2 * v.getY() * v.getY() - 2 * v.getZ() * v.getZ(),
                2 * v.getX() * v.getY() - 2 * w * v.getZ(),
                2 * v.getX() * v.getZ() + 2 * w * v.getY(),
                0
            },
            {
                2 * v.getX() * v.getY() + 2 * w * v.getZ(),
                1 - 2 * v.getX() * v.getX() - 2 * v.getZ() * v.getZ(),
                2 * v.getY() * v.getZ() - 2 * w * v.getX(),
                0
            },
            {
                2 * v.getX() * v.getZ() - 2 * w * v.getY(),
                2 * v.getY() * v.getZ() + 2 * w * v.getX(),
                1 - 2 * v.getX() * v.getX() - 2 * v.getY() * v.getY(),
                0
            },
            {
                0, 0, 0, 1
            }
        });
    }

    /**
     * Checks whether the given quaternion shares the same orientation.
     * 
     * @param vector The quaternion to compare.
     * @return Whether the quaternion has equal orientation.
     */
    public boolean equalsOrientation(Quaternion quat) {
        // Dot product will return 1 when pointing in the same direction.
        // The subtraction accounts for floating point error.
        // Equation obtained from 
        // https://gamedev.stackexchange.com/questions/75072/how-can-i-compare-two-quaternions-for-logical-equality
        return Math.abs(dot(quat)) > 1 - 0.000001;
    }

    /**
     * Adds the components of two quaternions.
     * 
     * @param quat The quaternion to add.
     * @return The sum of two quaternions.
     */
    public Quaternion add(Quaternion toAdd) {
        return new Quaternion(this.w + toAdd.w, this.v.add(toAdd.v));
    }

    /**
     * Subtracts a quaternion from the current quaternion.
     * 
     * @param quat The quaternion to subtract by.
     * @return The difference of two quaternions.
     */
    public Quaternion subtract(Quaternion toSubtract) {
        return new Quaternion(
            this.w - toSubtract.w, 
            this.v.subtract(toSubtract.v)
        );
    }

    /**
     * Gets the magnitude of the current quaternion.
     * 
     * @return The magnitude of the current quaternion.
     */
    public double magnitude() {
        return Math.sqrt(w * w + v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ());
    }

    /**
     * Normalizes the current quaternion so that its magnitude is 1.
     * 
     * @return The normalized quaternion.
     */
    public Quaternion normalize() {
        double magnitude = magnitude();
        return new Quaternion(w / magnitude, v.scale(1 / magnitude));
    }

    /**
     * Gets the conjugate of the current quaternion.
     * Note: For rotation quaternions, this is the same as the inverse.
     * 
     * @return The conjugate quaternion.
     */
    public Quaternion conjugate() {
        return new Quaternion(this.w, this.v.scale(-1));
    }

    /**
     * Gets the inverse of the current quaternion.
     * Note: For unit or rotation quaternions, use conjugate() instead.
     * 
     * @return The inverse quaternion.
     */
    public Quaternion inverse() {
        Quaternion conjugate = conjugate();
        double magSquared = magnitude();
        magSquared *= magSquared;
        return new Quaternion(conjugate.w / magSquared, conjugate.v.scale(1 / magSquared));
    }

    /**
     * Calculates a dot product with the current quaternion.
     * 
     * @param toDot The quaternion to dot with.
     * @return The dot product of two quaternion.
     */
    public double dot(Quaternion toDot) {
        return this.w * toDot.w + this.v.dot(toDot.v);
    }

    /**
     * Multiplies two quaternions together.
     * Useful for "adding" rotations of objects.
     * 
     * @param quat The quaterion to multiply by.
     * @return The product of two quaternions.
     */
    public Quaternion multiply(Quaternion quat) {
        // A neat technique from https://youtu.be/BXajpAy5-UI?t=437,
        // appears to equate to formula from
        // https://danceswithcode.net/engineeringnotes/quaternions/quaternions.html
        return new Quaternion(
            this.w * quat.w - this.v.dot(quat.v),
            quat.v.scale(this.w).add(this.v.scale(quat.w)).add(quat.v.cross(this.v))
        );
        // return new Quaternion(
        //     this.w * quat.w - this.v.dot(quat.v), 
        //     new Vector3(
        //         this.w * quat.v.getX() + this.v.getX() * quat.w - this.v.getY() * quat.v.getZ() + this.v.getZ() * quat.v.getY(),
        //         this.w * quat.v.getY() + this.v.getX() * quat.v.getZ() + this.v.getY() * quat.w - this.v.getZ() * quat.v.getX(),
        //         this.w * quat.v.getZ() - this.v.getX() * quat.v.getY() + this.v.getY() * quat.v.getX() + this.v.getZ() * quat.w
        //     )
        // );
    }
}
