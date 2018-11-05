package imagePainter;

public class ImagePainter {
	
	public static void main(String[] args)
	{
		ImagePPM imPPM = new ImagePPM();
		imPPM.ReadPPM(args[0]);
		System.out.println("image loaded sucessfully");
		Image imGS = imPPM.getGreyScaleImage();
		System.out.println("greyscale image produced sucessfully");
		imGS.WritePGM("OutputImages/greyScaleImage.PGM");
		Image imBlured = imGS.blurImage();
		imBlured.WritePGM("OutputImages/BluredImage.PGM");
		Image sobelMagnitude = imBlured.sobelMagnitude();
		sobelMagnitude.WritePGM("OutputImages/SobelMagImage.PGM");
		
		
		
		
		
		
	}
	
}
