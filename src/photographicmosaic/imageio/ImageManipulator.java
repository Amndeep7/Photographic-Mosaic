package photographicmosaic.imageio;

import java.awt.Image;
import javax.imageio.ImageIO;

public final class ImageManipulator
{
	public ImageManipulator()
	{
	}
	
	public static int[] averagePixelValues(Image image, boolean grayscale)
	{
		return null;
	}
	
	public static Image getImage(String filename)
	{
		return null;
	}
	
	public static Image createImage(Image[][] subimages)
	{
		return null;
	}
	
	//perhaps include a method that identifies the average pixel values for portions of the image? maybe like this?
	public static int[][][] averagePixelValuesPerArea(Image image, boolean grayscale, int unitsWide, int unitsHigh)
	{
		return null;
	}
}
