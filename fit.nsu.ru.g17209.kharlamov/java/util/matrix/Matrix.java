package util.matrix;

import java.util.Arrays;

/**
 * Class Matrix
 *
 * Implements matrix algebra
 */
public class Matrix {

    public double[] values;

    public final int width;
    public final int height;

    public int size;

    public Matrix(int height, int width, double[] data){
        size = width*height;
        values = new double[size];

        System.arraycopy(data,0,values,0, size);

        this.width = width;
        this.height = height;
    }

    public Matrix plusScalar(double scalar){
        double[] results = new double[height*width];
        for (int i = 0; i < size; i++) {
            results[i] = values[i]+scalar;
        }
        return new Matrix(height,width,results);
    }

    public Matrix multiMatrix(Matrix matrix){
        if(width == matrix.height){
            double[] results = new double[height*matrix.width];
            int resultIndex = 0;

            for (int rowI = 0; rowI < height; rowI++) {
                double[] row = new double[width];

                System.arraycopy(values, rowI * width, row, 0, width);

                for (int colI = 0; colI < matrix.width; colI++) {
                    for (int rowI2 = 0; rowI2 < matrix.height; rowI2++) {
                        results[resultIndex] += matrix.values[rowI2*matrix.width+colI]*row[rowI2];
                    }
                    resultIndex++;
                }
            }
            return new Matrix(height, matrix.width, results);
        }
        System.out.println("wrong matrix format");
        return null;
    }

    public Matrix multiScalar(double scalar){
        double[] newMatrixValues = new double[height*width];
        for (int i = 0; i < height*width; i++) {
            newMatrixValues[i] = values[i]*scalar;
        }
        return new Matrix(height, width, newMatrixValues);
    }

    public Matrix glue4SquareMatrix(Matrix matrix2, Matrix matrix3, Matrix matrix4){
        int newMatrixHeight = height+matrix3.height;
        int newMatrixWidth = width+matrix2.width;
        int newMatrixSize = (newMatrixHeight)*(newMatrixWidth);
        double[] newMatrixValues = new double[newMatrixSize];
        int newMatrixIterator = 0;
        for (int row = 0; row < height; row++) {
            for (int i = row*width; i < (row+1)*width; i++, newMatrixIterator++) {
                newMatrixValues[newMatrixIterator] = values[i];
            }
            for (int i = row*matrix2.width; i < (row+1)*matrix2.width; i++, newMatrixIterator++) {
                newMatrixValues[newMatrixIterator] = matrix2.values[i];
            }
        }
        for (int row = 0; row < matrix3.height; row++) {
            for (int i = row*matrix3.width; i < (row+1)*matrix3.width; i++, newMatrixIterator++) {
                newMatrixValues[newMatrixIterator] = matrix3.values[i];
            }
            for (int i = row*matrix4.width; i < (row+1)*matrix4.width; i++, newMatrixIterator++) {
                newMatrixValues[newMatrixIterator] = matrix4.values[i];
            }
        }
        return new Matrix(newMatrixHeight, newMatrixWidth,newMatrixValues);
    }

    public boolean equalsM(Matrix obj) {
        return Arrays.equals(values, obj.values);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        int iterator = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++, iterator++) {
                string.append(values[iterator]).append(" ");
            }
            string.append("\n");
        }
        return string.toString();
    }
}
