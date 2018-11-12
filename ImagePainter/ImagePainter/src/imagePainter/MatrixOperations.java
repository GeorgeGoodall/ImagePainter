package imagePainter;

import java.text.DecimalFormat;

// static class to store 2D matrix operations
// made by George Goodall, c1511942
public class MatrixOperations {
	
    static DecimalFormat df = new DecimalFormat("#.##");
	
	static double[] rotatePixel(double [] _pixelLocation,double rotationAngleClockwise) throws Exception{
		// Transpose pixel location matrix
		double[][] pixelLocation = new double[][]{{_pixelLocation[0],_pixelLocation[1]}};
		
		// calculate the rotation matrix and round each entry to 2d.p
		double[][] rotationMatrix = new double[][]{
			{Double.parseDouble(df.format(Math.cos(-rotationAngleClockwise))),Double.parseDouble(df.format(-Math.sin(-rotationAngleClockwise)))},
			{Double.parseDouble(df.format(Math.sin(-rotationAngleClockwise))),Double.parseDouble(df.format(Math.cos(-rotationAngleClockwise)))}};
		
			
		// perform matrix multiplication
		double[][] result = matrixMultiplication(pixelLocation, rotationMatrix);
		double[] toReturn = new double[]{result[0][0],result[0][1]};
		return toReturn;
	}
	
	static double[][] matrixMultiplication(double[][] matrix1, double[][] matrix2) throws Exception
	{
		int aRows = matrix1.length;
	    int aColumns = matrix1[0].length;
        int bRows = matrix2.length;
        int bColumns = matrix2[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:coulumns: " + aColumns + " did not match B:rows " + bRows + ".");
        }

        double[][] result = new double[aRows][aColumns];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < aColumns; j++) {
                result[i][j] = 0.00000;
            }
        }

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    result[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }
        
        // round result to 2DP
        for (int i = 0; i < aRows; i++) { 
            for (int j = 0; j < bColumns; j++) { 
            	result[i][j] = Double.parseDouble(df.format(result[i][j]));
            }
        }

        return result;
	}	
}
