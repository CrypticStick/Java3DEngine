/**
 * Stores a 3x3 matrix of data, useful for defining rotation or barycentric matrices.
 */
public class Matrix33 {

    private double[][] mat;
    
    /**
     * Defines a 3x3 matrix of values.
     * 
     * @param mat A 3x3 matrix of values.
     */
    public Matrix33(double[][] mat) 
    {
        if (mat.length != 3 || mat[0].length != 3) {
            System.err.println("The proxided matrix is not 3x3.");
            System.exit(-1);
        }
        this.mat = mat;
    }

    /**
     * Multiplies a 3D point with the current Matrix.
     * Useful for applying rotation or converting to barycentric coordinates.
     * 
     * @param vector3 The vector to multiply.
     * @return The product of the matrix and vector.
     */
    public Vector3 multiply(Vector3 vector3) {
        return new Vector3(
                vector3.getX() * mat[0][0] + 
                vector3.getY() * mat[0][1] + 
                vector3.getZ() * mat[0][2]
                ,
                vector3.getX() * mat[1][0] + 
                vector3.getY() * mat[1][1] + 
                vector3.getZ() * mat[1][2]
                ,
                vector3.getX() * mat[2][0] + 
                vector3.getY() * mat[2][1] + 
                vector3.getZ() * mat[2][2]
            );
    }

    /**
     * Multiplies a matrix with the current matrix.
     * Useful for combining rotations.
     * 
     * @param mat The matrix to multiply by.
     * @return The product of two matrices.
     */
    public Matrix33 multiply(Matrix33 mat) {
        double[][] product = new double[3][3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                for (int i = 0; i < 3; i++) {
                 product[row][col] += this.mat[row][i] * mat.mat[i][col];
                }
            }
        }
        return new Matrix33(product);
    }

    /**
     * Transposes the current matrix (swap rows and columns).
     * 
     * @return The transpose of the current matrix.
     */
    public Matrix33 transpose() {
        double[][] transpose = new double[3][3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                transpose[row][col] = this.mat[col][row];
            }
        }
        return new Matrix33(transpose);
    }

    /**
     * Gets the cofactor of the specified matrix element.
     * @param row The target row (starting from 0).
     * @param col The target column (starting from 0).
     * @return The cofactor of the specified matrix element.
     */
    public double cofactor(int row, int col) {
        double a1 = mat[row == 0 ? 1 : 0][col == 0 ? 1 : 0];
        double a2 = mat[row == 0 ? 1 : 0][col == 2 ? 1 : 2];
        double b1 = mat[row == 2 ? 1 : 2][col == 0 ? 1 : 0];
        double b2 = mat[row == 2 ? 1 : 2][col == 2 ? 1 : 2];
        return a1 * b2 - a2 * b1;
    }

    /**
     * Gets the cofactor matrix of the current matrix.
     * 
     * @return The cofactor matrix of the current matrix.
     */
    public Matrix33 cofactorMatrix() {
        double[][] cofactorMat = new double[3][3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                cofactorMat[row][col] = Math.pow(-1, (row + col)) * cofactor(row, col);
            }
        }
        return new Matrix33(cofactorMat);
    }

    /**
     * Gets the adjugate of the current matrix.
     * 
     * @return The adjugate of the current matrix.
     */
    public Matrix33 adjugate() {
        return cofactorMatrix().transpose();
    }

    /**
     * Gets the determinant of the current matrix.
     * 
     * @return The determinant of the current matrix.
     */
    public double determinant() {
        // Essentially calculated like a cross product
        return mat[0][0] * (mat[1][1] * mat[2][2] - mat[1][2] * mat[2][1]) -
            mat[0][1] * (mat[1][0] * mat[2][2] - mat[1][2] * mat[2][0]) +
            mat[0][2] * (mat[1][0] * mat[2][1] - mat[1][1] * mat[2][0]);
    }

    /**
     * Gets the inverse of the current matrix.
     * 
     * @return The inverse of the current matrix.
     */
    public Matrix33 inverse() {
        // Will throw error if determinant is zero.
        return adjugate().scale(1/determinant());
    }

    /**
     * Scales every value in the matrix.
     * 
     * @param scalar The scalar to multiply by.
     * @return The scaled matrix.
     */
    public Matrix33 scale(double scalar) {
        double[][] toScale = mat;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                toScale[row][col] *= scalar;
            }
        }
        return new Matrix33(toScale);
    }
}
