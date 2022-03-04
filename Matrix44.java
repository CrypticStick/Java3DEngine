/**
 * Stores a 4x4 matrix of data, useful for defining general transformations.
 */
public class Matrix44 {

    private double[][] mat;
    
    /**
     * Defines a 4x4 matrix of values.
     * 
     * @param mat A 4x4 matrix of values.
     */
    public Matrix44(double[][] mat) 
    {
        if (mat.length != 4 || mat[0].length != 4) {
            System.err.println("The proxided matrix is not 4x4.");
            System.exit(-1);
        }
        this.mat = mat;
    }

        /**
     * Defines a new 4x4 transformation matrix.
     * 
     * @param mat A 4x4 transformation matrix.
     */
    public Matrix44() 
    {
        this.mat = new double[][] {
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
        };
    }

    /**
     * Multiplies a 3D point with the current Matrix.
     * In most cases when using homogeneous points, you can safely set w = 1.
     * 
     * @param vector3 The vector to multiply.
     * @param w The 4th coordinate, usually equal to 1.
     * @return The product of the matrix and vector.
     */
    public Vector4 multiply(Vector3 vector3, double w) {
        return new Vector4(
                vector3.getX() * mat[0][0] + 
                vector3.getY() * mat[0][1] + 
                vector3.getZ() * mat[0][2] +
                w * mat[0][3]
                ,
                vector3.getX() * mat[1][0] + 
                vector3.getY() * mat[1][1] + 
                vector3.getZ() * mat[1][2] +
                w * mat[1][3]
                ,
                vector3.getX() * mat[2][0] + 
                vector3.getY() * mat[2][1] + 
                vector3.getZ() * mat[2][2] +
                w * mat[2][3]
                ,
                vector3.getX() * mat[3][0] + 
                vector3.getY() * mat[3][1] + 
                vector3.getZ() * mat[3][2] +
                w * mat[3][3]
            );
    }

    /**
     * Multiplies a matrix with the current matrix.
     * Useful for combining general transformations.
     * 
     * @param mat The matrix to multiply by.
     * @return The product of two matrices.
     */
    public Matrix44 multiply(Matrix44 mat) {
        double[][] product = new double[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                for (int i = 0; i < 4; i++) {
                 product[row][col] += this.mat[row][i] * mat.mat[i][col];
                }
            }
        }
        return new Matrix44(product);
    }

    /**
     * Gets the rotation matrix portion of the current transformation matrix.
     * 
     * @return The rotation matrix.
     */
    public Matrix33 getRotation() {
        double[][] rotation = new double[3][3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                rotation[row][col] += this.mat[row][col];
            }
        }
        return new Matrix33(rotation);
    }

    /**
     * Creates a new 4x4 Matrix from a translation.
     * 
     * @param translation A vector representing a change in location.
     * @return
     */
    public static Matrix44 fromTranslation(Vector3 translation) {
        double[][] translated = new double[4][4];
        translated[0][0] = 1;
        translated[1][1] = 1;
        translated[2][2] = 1;
        translated[3][3] = 1;
        translated[0][3] += translation.getX();
        translated[1][3] += translation.getY();
        translated[2][3] += translation.getZ();
        return new Matrix44(translated);
    }

    /**
     * Creates a new 4x4 Matrix from a list of scalars.
     * 
     * @param translation A vector representing a change in scale.
     * @return
     */
    public static Matrix44 fromScale(Vector3 scale) {
        double[][] scaled = new double[4][4];
        scaled[0][0] = scale.getX();
        scaled[1][1] = scale.getY();
        scaled[2][2] = scale.getZ();
        scaled[3][3] = 1;
        return new Matrix44(scaled);
    }

    /**
     * Shifts the transformation matrix by the given amount.
     * 
     * @param translation A vector representing a change in location.
     * @return
     */
    public Matrix44 addTranslation(Vector3 translation) {
        double[][] translated = mat;
        translated[0][3] += translation.getX();
        translated[1][3] += translation.getY();
        translated[2][3] += translation.getZ();
        return new Matrix44(translated);
    }

    /**
     * Transposes the current matrix (swap rows and columns).
     * 
     * @return The transpose of the current matrix.
     */
    public Matrix44 transpose() {
        double[][] transpose = new double[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                transpose[row][col] = this.mat[col][row];
            }
        }
        return new Matrix44(transpose);
    }

    /**
     * Scales every value in the matrix.
     * 
     * @param scalar The scalar to multiply by.
     * @return The scaled matrix.
     */
    public Matrix44 scale(double scalar) {
        double[][] toScale = mat;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                toScale[row][col] *= scalar;
            }
        }
        return new Matrix44(toScale);
    }
}
