package imagePainter;

public class Brushes {
	
	private int[][][][] brushes;
	
	// 20 by 10 cube with 5 px padding (making image 30 by 20)
	int[][] cubeBrush = new int[][]{{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
									{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
									{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
									{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
									{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
									{0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
									{0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
									{0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
									{0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
									{0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
									{0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
									{0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
									{0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
									{0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
									{0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
									{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
									{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
									{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
									{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
									{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};
		
	public Brushes(){
		makeSubBrushes(cubeBrush);
	}
									
	public Brushes(int[][] brushPixels)
	{
		makeSubBrushes(brushPixels);
		//this.brushPixels = brushPixels;
		
		// generate 16 brush strokes, with each stroke being 360/16 (22.5) deg from the last
		
	
	}
	
	// responsible for scaling and rotation
	private void makeSubBrushes(int[][] brush){
		// scale the brush stroke to 5 sizes
		// to create various versions at S = 5 different sizes, scaled by factors 1=S; 2=S; : : : S=S = 1; just use the simplest sampling scheme
		int[][][] scaledBrushes = new int[5][brush.length][brush[0].length];
		scaledBrushes[0] = brush;
		scaledBrushes[1] = scale(brush,0.8);
		scaledBrushes[2] = scale(brush,0.6);
		scaledBrushes[3] = scale(brush,0.4);
		scaledBrushes[4] = scale(brush,0.2);

//		// for debuging
//		for(int i = 0; i < 5; i++)
//		{
//			Image im = new Image(1,scaledBrushes[i].length,scaledBrushes[i][0].length);
//			im.pixels = scaledBrushes[i];
//			im.WritePGM("Images/OutputImages/BrushImage"+i+".PGM");
//		}
		
		// for each scaled brush
		for(int i = 0; i < 5; i++)
		{
			// rotate and save
			
		}
	}
	
	private int[][] scale(int[][] brush,double factor){
		
		int[][] scaledBrush = new int[(int)Math.ceil(brush.length * factor)][(int)Math.ceil(brush[0].length * factor)];
		double sampleRate = 1/factor;
		
		for (int i = 0; i < scaledBrush.length; i++){
			for (int j = 0; j < scaledBrush[0].length; j++)
			{				
				scaledBrush[i][j] = brush[(int) Math.round((i+0.5)*sampleRate)][(int) Math.round((j+0.5)*sampleRate)];
			}
		}
		return scaledBrush;
	}
}
