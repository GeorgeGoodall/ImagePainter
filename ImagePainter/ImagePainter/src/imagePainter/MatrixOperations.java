package imagePainter;

// static class to store 2D matrix operations
// made by George Goodall, c1511942
public class MatrixOperations {
	
	static double[][] rotatePixel(int [][] pixelLocation,double rotationAngleClockwise) throws Exception{
		// calculate the rotation matrix
		double[][] rotationMatrix = new double[][]{
			{Math.cos(-rotationAngleClockwise),-Math.sin(-rotationAngleClockwise)},
			{Math.sin(-rotationAngleClockwise),Math.sin(-rotationAngleClockwise)}};
		
		// change int [][] to double[][]
		double[][] pixelLocationD = convert2DIntArrayToDouble(pixelLocation);
			
		// perform matrix multiplication
		return matrixMultiplication(pixelLocationD, rotationMatrix);
	}
	
	static double[][] matrixMultiplication(double[][] matrix1, double[][] matrix2) throws Exception
	{
		int aRows = matrix1.length;
	    int aColumns = matrix1[0].length;
        int bRows = matrix2.length;
        int bColumns = matrix2[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        double[][] result = new double[aRows][bColumns];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
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

        return result;
	}
	
	private static double[][] convert2DIntArrayToDouble(int[][] toConvert){
		double[][] toReturn = new double[toConvert.length][toConvert[0].length];
		for(int i = 0; i < toConvert.length; i++){
			for(int j = 0; j < toConvert[0].length; j++){
				toReturn[i][j] = (double)toConvert[i][j];
			}	
		}
		return toReturn;
	}
	
}
