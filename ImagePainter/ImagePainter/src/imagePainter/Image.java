package imagePainter;
/* rather simplistic implementation of PGM reader/writer
 *
 * Paul Rosin, 2002
 *
 * update to use 2 input streams - avoiding deprecation errors
 * Matt Gee, 2004
 *
 * constructor added to allow image size to be specified
 * Graham Daniell, 2004
 *
 * added function that returns a blank Image copy
 * added function that performs sobel edge detection
 * added function to return blured image
 * George Goodall, c151194
 *  
 */

import java.io.*;
import java.util.*;

public class Image
{
    public int [][] pixels;
    public int depth,width,height;
    public int[] histogram;

    
    private Image sobelMagnitude = null;
    private double[][] sobelOrientation = null;
    private Image blur = null;

    public Image()
    {
        pixels = new int[1500][1500];
        depth = width = height = 0;
    }

    public Image(int inDepth, int inWidth, int inHeight)
    {
        pixels = new int[inWidth][inHeight];
        width = inWidth;
        height = inHeight;
        depth = inDepth;
    }

    public void ReadPGM(String fileName) throws Exception
    {
        String line;
        StringTokenizer st;

        try {
            BufferedReader in =
              new BufferedReader(new InputStreamReader(
                new BufferedInputStream(
                  new FileInputStream(fileName))));

            DataInputStream in2 =
              new DataInputStream(
                new BufferedInputStream(
                  new FileInputStream(fileName)));

            // read PGM image header

            // skip comments
            line = in.readLine();
            in2.skip((line+"\n").getBytes().length);
            do {
                line = in.readLine();
                in2.skip((line+"\n").getBytes().length);
            } while (line.charAt(0) == '#');

            // the current line has dimensions
            st = new StringTokenizer(line);
            width = Integer.parseInt(st.nextToken());
            height = Integer.parseInt(st.nextToken());

            // next line has pixel depth
            line = in.readLine();
            in2.skip((line+"\n").getBytes().length);
            st = new StringTokenizer(line);
            depth = Integer.parseInt(st.nextToken());

            this.pixels = new int[width][height];
            
            // read pixels now
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    pixels[x][y] = in2.readUnsignedByte();
            
            in.close();
            in2.close();
        } catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: image in "+fileName+" too big");
            throw e;
        } catch(FileNotFoundException e) {
            System.out.println("Error: file "+fileName+" not found");
            throw e;
        } catch(IOException e) {
            System.out.println("Error: end of stream encountered when reading "+fileName);
            throw e;
        }
    }

    public void WritePGM(String fileName)
    {
        try {
            DataOutputStream out =
              new DataOutputStream(
                new BufferedOutputStream(
                  new FileOutputStream(fileName)));

            out.writeBytes("P5\n");
            out.writeBytes("#created by Paul Rosin\n");
            out.writeBytes(width+" "+height+"\n255\n");

            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    out.writeByte((byte)pixels[x][y]);

            out.close();
        } catch(IOException e) {
            System.out.println("ERROR: cannot write output file");
        }
    }
    
    public Image copyBlankCanvas(){
    	return new Image(this.depth,this.width,this.height);
    }
    
    public Image blurImage(){
    	if(blur != null)
    	{
    		return blur;
    	}
    	else
    	{
        	int[][] blurMask = new int[][]{{1,1,1},{1,1,1},{1,1,1}};
        	Image bluredImage = applyMask(blurMask);
        	blur = bluredImage;
        	return bluredImage;
    	}
    }
    
    public Image sobelMagnitude(){	
    	if(sobelMagnitude == null){ performSobel(); }
    	return sobelMagnitude;	
   }
    
    public double[][] sobelOrientation(){  	
    	if(sobelMagnitude == null){ performSobel(); }
    	return sobelOrientation;
   }

    public int[][][] sobelOrientationImage(){
        if(sobelMagnitude == null){ performSobel(); }
        int[][][] sobelOrientationIamge = new int[3][this.width][this.height];
        for(int i = 0; i < sobelOrientation.length; i++){
            for(int j = 0; j < sobelOrientation[0].length; j++){
                // 1530 points on a color wheel
            	if(sobelMagnitude.pixels[i][j] > 10){
	                int[] color = new int[3];
	                float a = (float) ((float)sobelOrientation[i][j] / (2 * Math.PI));
	                int colorVal = (int)(a * 1530);
	                int colorBucket = colorVal / 255;
	                int colorOverflow = colorVal % 255;
	                
	                if(colorBucket == 0)	 {color = new int[]{0,255-colorOverflow,255};}
	                else if(colorBucket == 1){color = new int[]{colorOverflow,0,255};}
	                else if(colorBucket == 2){color = new int[]{colorOverflow,0,0};}
	                else if(colorBucket == 3){color = new int[]{255,colorOverflow,0};}
	                else if(colorBucket == 4){color = new int[]{255-colorOverflow,255,0};}
	                else if(colorBucket == 5){color = new int[]{0,255,colorOverflow};}

	                
	                for(int c = 0; c < 3; c++){
	                    sobelOrientationIamge[c][i][j] = color[c]; 
	                }
                }
	                
            }
        }
        return sobelOrientationIamge;
        
    }
    
    public void performSobel(){
    	int[][] horizontalMask = new int[][]{{-1,0,1},{-2,0,1},{-1,0,1}};
    	int[][] verticalMask = new int[][]{{-1,-2,-1},{ 0, 0, 0},{ 1, 2, 1}};
        
//    	int[][] horizontalMask = new int[][]{{-1,-2,0,2,1},{-1,-2,0,2,1},{-1,-2,0,2,1},{-1,-2,0,2,1},{-1,-2,0,2,1}};
//      int[][] verticalMask = new int[][]{{-1,-1,-1,-1,-1},{-2,-2,-2,-2,-2},{0,0,0,0,0},{2,2,2,2,2},{1,1,1,1,1}};
        
//      int[][] horizontalMask = new int[][]{{-1,-2,-4,0,4,2,1},{-1,-2,-4,0,4,2,1},{-1,-2,-4,0,4,2,1},{-1,-2,-4,0,4,2,1},{-1,-2,-4,0,4,2,1},{-1,-2,-4,0,4,2,1},{-1,-2,-4,0,4,2,1}};
//      int[][] verticalMask = new int[][]{{-1,-1,-1,-1,-1,-1,-1},{-2,-2,-2,-2,-2,-2,-2},{-4,-4,-4,-4,-4,-4,-4},{0,0,0,0,0,0,0},{4,4,4,4,4,4,4},{2,2,2,2,2,2,2},{1,1,1,1,1,1,1}};
//        
        
        Image sobelX = applyMask(horizontalMask);
        Image sobelY = applyMask(verticalMask);
        
        sobelX.WritePGM("../Images/OutputImages/sobelX.pgm");
        sobelY.WritePGM("../Images/OutputImages/sobelY.pgm");
        
        sobelMagnitude = this.copyBlankCanvas();
        sobelOrientation = new double[this.width][this.height];
        
        for (int y = 1; y < this.height - 1; y++)
        {
            for (int x = 1; x < this.width - 1; x++)
            {
            	sobelMagnitude.pixels[x][y] = (int)Math.round(Math.sqrt((sobelX.pixels[x][y] * sobelX.pixels[x][y]) + (sobelY.pixels[x][y] * sobelY.pixels[x][y])));
            	if(sobelY.pixels[x][y] == 0){sobelOrientation[x][y] = Math.PI / 2;}
            	else
            	{
            		double xChange = sobelX.pixels[x][y];
            		double yChange = -sobelY.pixels[x][y];
            		double theta = yChange/xChange;
            		sobelOrientation[x][y] = Math.atan(theta);
            		if(sobelOrientation[x][y] < 0){
            			sobelOrientation[x][y] = (2 * Math.PI) + sobelOrientation[x][y];
            		}
            	}
            }
        }
    }
    
    public Image applyMask(int[][] mask){
    	
        Image filteredImage = this.copyBlankCanvas();
        
        // forgetting about the pixel border for now
        for (int y = 1; y < this.height - 1; y++)
        {
            for (int x = 1; x < this.width - 1; x++)
            {
            	int count = 0; // count the number of pixels in filter to calculate average
                int sum = 0;
                int maskOffset = (mask.length - 1)/2; // 
                for (int i = maskOffset - (mask.length - 1) ; i <= maskOffset; i++)
                {
                    for(int j = maskOffset - (mask[0].length - 1); j <= maskOffset; j++)
                    {
                    	if(!(x+i < 0 || x+i >= this.width || y+j < 0 || y+j >= this.height))
                    	{
                            sum += this.pixels[x+i][y+j] * mask[i + maskOffset][j + maskOffset];
                            count++;
                    	}

                    }
                }

                int pixelValue = Math.round(sum / count);
                filteredImage.pixels[x][y] = pixelValue;  
            }
        }
        
        return filteredImage;
    }
    
    public Image getHistogram(){
    	int[] histogram = new int[255];
    	int[] cumulitiveHist = new int[255];
    	int[] map = new int[255];
    	for(int i = 0; i < this.width; i++){
    		for(int j = 0; j < this.width; j++){
    			histogram[this.pixels[i][j]]++;
    		}
    	}
    	cumulitiveHist[0] = histogram[0];
    	for(int i = 1; i < histogram.length; i++){
    		cumulitiveHist[i] = cumulitiveHist[i-1] + histogram[i];
    	}
    	
    	for(int i = 1; i < histogram.length; i++){
    		map[i] = Math.round((255*cumulitiveHist[i])/(this.width*this.height));
    	}
    	Image equalised = new Image(255,this.width,this.height);
    	for(int i = 0; i < this.width; i++){
    		for(int j = 0; j < this.width; j++){
    			equalised.pixels[i][j] = map[this.pixels[i][j]];
    		}
    	}
		return equalised;
    }
    
    
   
    

}