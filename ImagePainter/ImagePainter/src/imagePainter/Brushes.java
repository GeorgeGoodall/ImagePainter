package imagePainter;

import java.text.DecimalFormat;

public class Brushes {
	
    static DecimalFormat df = new DecimalFormat("#.##");
	
	private int[][][][] brushes;
									
	public Brushes(int[][] brushPixels)
	{
		makeSubBrushes(brushPixels);
		//this.brushPixels = brushPixels;
		
		// generate 16 brush strokes, with each stroke being 360/16 (22.5) deg from the last
		saveBrushes();
	
	}
	
	public int[][] getBrush(int size, int rotation){
		return brushes[size][rotation];
	}
	
	// responsible for scaling and rotation
	private void makeSubBrushes(int[][] brush){
		brushes = new int[5][][][];
		
		//toDo shorten this
		// scale the brush stroke to 5 sizes
		// to create various versions at S = 5 different sizes, scaled by factors 1=S; 2=S; : : : S=S = 1; just use the simplest sampling scheme
		int[][][] scaledBrushes = new int[5][][];
		for(int i = 0; i < 5; i++){
			double scale = Double.parseDouble(df.format(1-(i/5f)));
			scaledBrushes[i] = scale(brush,scale);
			brushes[i] = blackCanvases(16,scaledBrushes[i].length,scaledBrushes[i][0].length);
		}
		
		// for each scaled brush
		for(int scale = 0; scale < 5; scale++){
			for(int currentRotation = 0; currentRotation < 16; currentRotation++){
				// rotate and save
				for(int x = 0; x < brushes[scale][currentRotation].length; x++){
					for(int y = 0; y < brushes[scale][currentRotation][0].length; y++){
						double[] point;
						try {
							double currentPixelX = x-((scaledBrushes[scale].length)/2);
							double currentPixelY = y-((scaledBrushes[scale][0].length)/2);
							
							point = MatrixOperations.rotatePixel(new double[]{currentPixelX,currentPixelY}, -currentRotation * Math.PI / 8);
							
							point[0] += ((scaledBrushes[scale].length)/2);
							point[1] += ((scaledBrushes[scale][0].length)/2);
							
							double a = point[0]-Math.floor(point[0]);
							double b = point[1]-Math.floor(point[1]);
							int fa = scaledBrushes[scale][(int) Math.floor(point[0])][(int) Math.floor(point[1])];
							int fb = scaledBrushes[scale][(int) Math.ceil(point[0])][(int) Math.floor(point[1])];
							int fd = scaledBrushes[scale][(int) Math.floor(point[0])][(int) Math.ceil(point[1])];
							int fc = scaledBrushes[scale][(int) Math.ceil(point[0])][(int) Math.ceil(point[1])];

							//fR = fB + b(fC- fB)
							double fr = fb + b*(fc - fb);
							double fs = fa + b*(fd - fa);
							double ft = fr + a*(fs - fr);
							
							brushes[scale][currentRotation][x][y] = (int)Math.round(ft);
						} catch (Exception e) {
							brushes[scale][currentRotation][x][y] = 255;
						}
					}
				}
			}
		}
	}
	
	// consider changing scale function to use matrix's?
	private int[][] scale(int[][] brush,double factor){
		
		int[][] scaledBrush = new int[(int)Math.ceil(brush.length * factor)][(int)Math.ceil(brush[0].length * factor)];
		double sampleRate = 1/factor;
		
		for (int i = 0; i < scaledBrush.length; i++){
			for (int j = 0; j < scaledBrush[0].length; j++)
			{				
				scaledBrush[i][j] = brush[(int) Math.floor((i)*sampleRate)][(int) Math.floor((j)*sampleRate)];
			}
		}
		return scaledBrush;
	}
	
	private int[][][] blackCanvases(int count, int width, int height){
		int [][][] blackCanvases = new int[count][width][height];
		for(int i = 0; i < count; i++){
			blackCanvases[i] = blackCanvas(width,height);
		}
		return blackCanvases;
	}
	
	private int[][] blackCanvas(int width, int height)
	{
		int[][] blackCanvas = new int[width][height];
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				blackCanvas[i][j] = 255;
			}
		}
		return blackCanvas;
	}
	
	private void saveBrushes(){
		for(int brushScale = 0; brushScale < 5; brushScale++){
			for(int brushRotation = 0; brushRotation < 16; brushRotation++){
				
				Image brush = new Image(1,brushes[brushScale][brushRotation].length,brushes[brushScale][brushRotation][0].length);
				brush.pixels = brushes[brushScale][brushRotation];
				brush.WritePGM("Images/OutputImages/Brushes/brush-"+brushScale+"-"+brushRotation+".pgm");
			}
		}
	}
}
