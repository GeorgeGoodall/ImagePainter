package imagePainter;
import java.io.File;

public class ImagePainter {
	
	static int density;
	static double mapRotation = (2 * Math.PI) / 16; // used to calculate what rotation should be used based on the sobel orientation
	static int mapSize = 255 / 5; // used to calculate what brush size should be used based on the sobel magnitude
	
	public static void main(String[] args)
	{
		if(checkFolderStructure()){
			System.out.println("Images Folder was made");
		}

		ImagePPM imPPM = new ImagePPM();
		try {imPPM.ReadPPM(args[0]);}
		catch(Exception e){System.out.println("image " + args[1] + " not found"); return;}
		System.out.println("image loaded sucessfully");
		
		System.out.print("greyscale image produced: ");
		Image imGS = imPPM.getGreyScaleImage();
		System.out.print("Successfully | Saved: ");
		imGS.WritePGM("../Images/OutputImages/greyScaleImage.PGM");
		System.out.println("Successfully");
		
		System.out.print("Blured Image Produced: ");
		Image imBlured = imGS.blurImage();
		System.out.print("Successfully | Saved: ");
		imBlured.WritePGM("../Images/OutputImages/BluredImage.PGM");
		System.out.println("Successfully");
		
		System.out.print("Sobel Magnitude Image Produced: ");
		Image sobelMagnitude = imBlured.sobelMagnitude();
		System.out.print("Successfully | Saved: ");
		sobelMagnitude.WritePGM("../Images/OutputImages/SobelMagImage.PGM");
		System.out.println("Successfully");
		
		double[][] sobelOrientation = imBlured.sobelOrientation();
		
		Image brush = new Image();
		System.out.print("Brush Loaded: ");
		try{ brush.ReadPGM(args[1]);}
		catch(Exception e){ System.out.println("brush " + args[1] + " not found"); return;}
		System.out.println("Successfully");
		System.out.print("brushes scaled and rotated :");
		Brushes brushes = new Brushes(brush.pixels);
		System.out.println("Successfully");
		
		try{
			//density = Integer.parseInt(args[1]);
			density = 4;
			System.out.println("density peramiter \""+density+"\" loaded successfully");
		}
		catch(NumberFormatException e){
			System.out.println("Pram 2 \"Density\" was not in the correct format");
			return;
		}
		
		
		// make black canvas
		ImagePPM canvas = new ImagePPM(imPPM.depth,imPPM.width,imPPM.height);
		canvas.whiteWash();
		
		for(int brushSize = 0; brushSize < 5; brushSize++){
			System.out.println("using brush size: " + brushSize);
			double dissimilarity = Integer.MAX_VALUE;
			
			// generate N random positions where N = P / (D * brushSize), 
			int n = (int)Math.round(((canvas.width *  canvas.height) / density)/(5-brushSize));
			
			for(int i = 0; i < n; i++){
				int xLocation = (int)Math.round(Math.random() * (canvas.width-1));
				int yLocation = (int)Math.round(Math.random() * (canvas.height-1));
				
				// get brush size to use
				int edgeMagnitude = sobelMagnitude.pixels[xLocation][yLocation];
				int brushSizeToUse = (int)Math.floor(edgeMagnitude / 7);
				if (brushSizeToUse == brushSize)
				{
					// get brush rotation to use
					double rotationRads = sobelOrientation[xLocation][yLocation];
					int brushRotationToUse = (int)(Math.floor(rotationRads/mapRotation));
					
					// get brush
					int[][] brushToUse = brushes.getBrush(brushSizeToUse, brushRotationToUse);

					int[] averageColour = imPPM.getAverageColour(brushToUse, xLocation, yLocation);
					
					// if applying this colour makes the image closer to the absolute value of the colours in the actual image then apply the brush
					ImagePPM canvasCopy = new ImagePPM(canvas.depth,canvas.width,canvas.height);
					canvasCopy.pixels = canvas.pixels;
					canvasCopy.paintStroke(brushToUse, averageColour, xLocation, yLocation);
					double newDissimilarity = getDissimilarity(imPPM,canvasCopy);
					if(dissimilarity > newDissimilarity){
						canvas.paintStroke(brushToUse, averageColour, xLocation, yLocation);
						dissimilarity = newDissimilarity;
					}
				}
			}
			
			canvas.WritePPM("../Images/ImageAfter"+brushSize+"BrushSizeUsed.ppm");
			
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
