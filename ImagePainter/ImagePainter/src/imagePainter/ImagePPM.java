package imagePainter;
/* rather simplistic implementation of PPM reader/writer
 *
 * Paul Rosin, 2003
 *
 * update to use 2 input streams - avoiding deprecation errors
 * Matt Gee, 2004
 *
 * constructor added to allow image size to be specified
 * Graham Daniell, 2004
 *
 * added function that returns grey scale image
 * George Goodall, c1511942
 */

import java.io.*;
import java.util.*;

public class ImagePPM
{
    public int [][][] pixels;
    public int depth,width,height;
    private int[] averageRGB;

    public ImagePPM()
    {
        pixels = new int[3][1500][1500];
        depth = width = height = 0;
    }

    public ImagePPM(int inDepth, int inWidth, int inHeight)
    {
        pixels = new int[3][inWidth][inHeight];
        width = inWidth;
        height = inHeight;
        depth = inDepth;
    }

    public void ReadPPM(String fileName)
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

            // read PPM image header

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

            // read pixels now
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    for (int i = 0; i < 3; i++)
                        pixels[i][x][y] = in2.readUnsignedByte();

            in.close();
            in2.close();
        } catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: image in "+fileName+" too big");
        } catch(FileNotFoundException e) {
            System.out.println("Error: file "+fileName+" not found");
        } catch(IOException e) {
            System.out.println("Error: end of stream encountered when reading "+fileName);
        }
    }

    public void WritePPM(String fileName)
    {

        try {
            DataOutputStream out =
              new DataOutputStream(
                new BufferedOutputStream(
                  new FileOutputStream(fileName)));

            out.writeBytes("P6\n");
            out.writeBytes("#created by Paul Rosin\n");
            out.writeBytes(width+" "+height+"\n255\n");

            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    for (int i = 0; i < 3; i++)
                        out.writeByte((byte)pixels[i][x][y]);

            out.close();
        } catch(IOException e) {
            System.out.println("ERROR: cannot write output file");
        }
    }

    public Image getGreyScaleImage(){
    	Image greyImage =  new Image(this.depth,this.width,this.height);
    	
    	for(int j = 0; j < width;j++){
    		for(int i = 0; i < height; i++){
    			greyImage.pixels[j][i] = (this.pixels[0][j][i] + this.pixels[1][j][i] + this.pixels[2][j][i]) / 3;
    		}
    	}
    	
    	return greyImage;
    }
    
    // todo make mask offsets for X and Y
    public int[] getAverageColour(int[][] mask, int xLocation, int yLocation){
    	double count = 0; // count the number of pixels in filter to calculate average
        int[] sum = new int[3];
        int maskOffsetX = (mask.length)/2;
        int maskOffsetY = (mask[0].length)/2;
        int remanderX = (mask.length)%2;
        int remanderY = (mask[0].length)%2;
        
    	for (int i = maskOffsetX - (mask.length) + remanderX; i < maskOffsetX; i++)
        {
            for(int j = maskOffsetY - (mask[0].length) + remanderY; j < maskOffsetY; j++)
            {
            	// if the masks current pixel is on the image
            	if(!(xLocation+i < 0 || xLocation+i >= this.height || yLocation+j < 0 || yLocation+j >= this.height))
            	{
            		double maskPixelValue = (1 - mask[i + maskOffsetX][j + maskOffsetY] / 255);
            		for(int colour = 0; colour < sum.length; colour++){	
                        sum[colour] += (int)Math.round(this.pixels[colour][xLocation+i][yLocation+j] * maskPixelValue); 
            		}
            		count += maskPixelValue;
            	}

            }
        }
		for(int colour = 0; colour < sum.length; colour++){
            sum[colour] = (int)Math.round(sum[colour] / count);
		}
    	return sum;
    }
    
    public void paintStroke(int[][] brush, int[] colour, int xLocation, int yLocation){
    	int maskOffset = (brush.length)/2;
    	int r = (brush.length)%2;
    	
    	for (int i = maskOffset - (brush.length) ; i < maskOffset; i++){
            for(int j = maskOffset - (brush[0].length); j < maskOffset; j++){
            	if(!(xLocation+i < 0 || xLocation+i >= this.width || yLocation+j < 0 || yLocation+j >= this.height)){
            		for(int k = 0; k < colour.length; k++){
            			// ToDo factor in old colour to make transparent stroke
            			double currentMaskPixel = (double)(brush[i+maskOffset+r][j+maskOffset+r]); 
            			double alpha = 1 - (currentMaskPixel/255);
            			int currentPixelColour = this.pixels[k][xLocation+i][yLocation+j];
            			int newPixelValue = (int)Math.round(((colour[k] * alpha)+(currentPixelColour * (1 - alpha))));
            			this.pixels[k][xLocation+i][yLocation+j] = newPixelValue; 
            		}	
            	}
            }
        }
    }
    
    public int[] getAverageRGB(boolean recalculate){
    	if(averageRGB != null && recalculate == false){ return averageRGB;}
    	else{
    		int[] sums = new int[3];
    		int count = 0;
    		for(int i = 0; i < this.pixels[0].length; i++){
    			for(int j = 0; j < this.pixels[0][0].length; j++){
    				for(int k = 0; k < 3; k++){
    					sums[k] += this.pixels[k][i][j];
    				}
    				count++;
    			}
    		}
    		for(int k = 0; k < 3; k++){
				sums[k] = sums[k] / count;
			}
    		averageRGB = sums;
    		return averageRGB;
    	}
    }
    
    public void whiteWash() {
    	for(int i = 0; i < pixels[0].length; i++) {
        	for(int j = 0; j < pixels[0].length; j++) {
            	for(int c = 0; c < 3; c++) {
            		pixels[c][i][j] = 255;
            	}
        	}
    	}
    }
}