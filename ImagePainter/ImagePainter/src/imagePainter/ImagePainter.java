package imagePainter;
import java.io.File;

public class ImagePainter {
	
	static int density;
	static double mapRotation = (2 * Math.PI) / 16; // used to calculate what rotation should be used based on the sobel orientation
	static int mapSize = 255 / 5; // used to calculate what brush size should be used based on the sobel magnitude
	
	public static void main(String[] args)
	{
		if(args.length != 3){
			System.out.println("input args incorrect | expected: 'java imagePainter <path to picture> <path to brush> <denisity>.'");
			return;
		}

		if(checkFolderStructure()){
			System.out.println("Images Folder was made");
		}

		ImagePPM imPPM = new ImagePPM();
		try {imPPM.ReadPPM(args[0]);}
		catch(Exception e){System.out.println("image at " + args[0] + " not found"); return;}
		System.out.println("image found sucessfully");

		Image brush = new Image();
		try{ brush.ReadPGM(args[1]);}
		catch(Exception e){ System.out.println("brush at " + args[1] + " not found"); return;}
		System.out.println("brush found successfully");
		
		Image imGS = imPPM.getGreyScaleImage();
		System.out.println("Greyscale image produced successfully");
		
		Image imBlured = imGS.blurImage();
		System.out.println("Blured image produced successfully");
		
		Image sobelMagnitude = imBlured.sobelMagnitude();
		//sobelMagnitude = sobelMagnitude.getHistogram();
		sobelMagnitude.WritePGM("../Images/OutputImages/magnitude.PGM");
		System.out.println("Sobel magnitude image produced and saved successfully");
		
		// ToDo
		double[][] sobelOrientation = imBlured.sobelOrientation();
		ImagePPM so = new ImagePPM(255,imPPM.width,imPPM.height);
		so.pixels = imBlured.sobelOrientationImage();
		so.WritePPM("../Images/OutputImages/orientation.ppm");
		System.out.println("Sobel orientation image produced and saved successfully");

		Brushes brushes = new Brushes(brush.pixels);
		System.out.println("Brushes scaled and rotated successfully");
		brushes.saveBrushes();
		System.out.println("Brushes saved successfully");

		
		try{
			density = Integer.parseInt(args[2]);
			if(density > brushes.getBrush(0, 0).length){
				System.out.println("Warning: your brush spacing value(density) is too large for the size of brush \n I recomend using a density of "+brushes.getBrush(0, 0).length+" or less for your brush size of "+ brush.width +" x " + brush.height +" to avoid excessive unpainted areas.");
			}
			if(density > brushes.getBrush(4, 0).length){
				System.out.println("Notice: your brush spacing value(density) may be too large for the size of brush \n I recomend using a density of "+brushes.getBrush(4, 0).length+" or less for your brush size of "+ brush.width +" x " + brush.height +" for optimal quality.");
			}
			else{
				System.out.println("Density O.K.");
			}
			
		}
		catch(NumberFormatException e){
			System.out.println("Pram 3 \"Density\" was not in the correct format");
			return;
		}
		
		
		ImagePPM canvas = new ImagePPM(imPPM.depth,imPPM.width,imPPM.height);
		canvas.whiteWash();
		
		for(int brushSize = 0; brushSize < 5; brushSize++){
			System.out.print("using brush size: " + brushSize + ",");
			double dissimilarity = Integer.MAX_VALUE;
			
			//to store the current size strokes being applied
			ImagePPM currentStrokes = new ImagePPM(imPPM.depth,imPPM.width,imPPM.height);
			currentStrokes.whiteWash();
			int numberOfStrokes = 0;
			
			// generate N random positions where N = P / (D * brushSize), 
			int n = (int)Math.round(((canvas.width *  canvas.height) / density));
			
			for(int i = 0; i < n; i++){
				int xLocation = (int)Math.round(Math.random() * (canvas.width-1));
				int yLocation = (int)Math.round(Math.random() * (canvas.height-1));
				
				// get brush size to use
				int edgeMagnitude = sobelMagnitude.pixels[xLocation][yLocation];
				int brushSizeToUse = (int)Math.floor(edgeMagnitude / 7);
				if (brushSizeToUse > 4){brushSizeToUse = 4;}
				if (brushSizeToUse == brushSize)
				{
					// get brush rotation to use
					double rotationRads = sobelOrientation[xLocation][yLocation];
					int brushRotationToUse = (int)(Math.floor((rotationRads)/mapRotation));
					//if(brushRotationToUse == 16)
					
					// get brush
					int[][] brushToUse = brushes.getBrush(brushSizeToUse, brushRotationToUse);

					int[] averageColor = imPPM.getAverageColor(brushToUse, xLocation, yLocation);
					
					// if applying this color makes the image closer to the absolute value of the colors in the actual image then apply the brush
					double oldDissimilarity = getLocalDissimilarity(xLocation,yLocation,brushToUse.length,imPPM,canvas);
					canvas.paintStroke(brushToUse, averageColor, xLocation, yLocation);
					// todo improve this dissimilarity function as it very time consuming
					double newDissimilarity = getLocalDissimilarity(xLocation,yLocation,brushToUse.length,imPPM,canvas);
					//canvas.WritePPM("../Images/current.ppm");
					if(oldDissimilarity > newDissimilarity){
						currentStrokes.paintStroke(brushToUse, averageColor, xLocation, yLocation);
						numberOfStrokes++;
					}
					else{
						canvas.revertStroke();
					}
				}
			}
			
			// ToDo: modify so this only outputs one brush size
			currentStrokes.WritePPM("../Images/ImageAfter"+brushSize+"BrushSizeUsed.ppm");
			System.out.println(" "+numberOfStrokes+" strokes used.");
			
		}
		
		canvas.WritePPM("../Images/PaintedImage.ppm");
		System.out.println("Painted Image Made Successfully");
	}
	
	private static double getDissimilarity(ImagePPM i1, ImagePPM i2){
		int[] c1 = i1.getAverageRGB(false);
		int[] c2 = i2.getAverageRGB(true);
		
		double a = (c1[0]-c2[0]);
		double b = (c1[1]-c2[1]);
		double c = (c1[2]-c2[2]);
		
		double dis = Math.sqrt(a*a+b*b+c*c);
		
		return dis;
	}
	
	private static double getLocalDissimilarity(int xLocation, int yLocation, int regionRadius, ImagePPM i1, ImagePPM i2){
		int[] i1AvRGB = new int[]{0,0,0};
		int[] i2AvRGB = new int[]{0,0,0};
		int count = 0;
		
		for(int i = -regionRadius; i <= regionRadius; i++){
			for(int j = -regionRadius; j <= regionRadius; j++){
				if(xLocation+i > 0 && xLocation+i < i1.width && yLocation+j > 0 && yLocation+j < i1.height){
					for(int c = 0; c < 3; c++){
						count++;
						i1AvRGB[c] += i1.pixels[c][xLocation+i][yLocation+j];
						i2AvRGB[c] += i2.pixels[c][xLocation+i][yLocation+j];
						
					}
				}
			}
		}
		for(int c = 0; c < 3; c++){
			i1AvRGB[c] = i1AvRGB[c] / count;
			i2AvRGB[c] = i2AvRGB[c] / count;
		}
		
		double a = (i1AvRGB[0]-i2AvRGB[0]);
		double b = (i1AvRGB[1]-i2AvRGB[1]);
		double c = (i1AvRGB[2]-i2AvRGB[2]);
		
		double dis = Math.sqrt(a*a+b*b+c*c);
		
		return dis;
	}

	private static boolean checkFolderStructure(){
		boolean folderStructureModified = false;

		File iamgeFolder = new File("../Images/");
		File outputImages = new File(iamgeFolder.getPath() + "/OutputImages/");
		File brushes = new File(outputImages.getPath() + "/Brushes/");

		if (!iamgeFolder.exists()){iamgeFolder.mkdirs(); folderStructureModified = true;}
		if (!outputImages.exists()){outputImages.mkdirs();folderStructureModified = true;}
		if (!brushes.exists()){brushes.mkdirs();folderStructureModified = true;}

		return folderStructureModified;
	}
	
}
