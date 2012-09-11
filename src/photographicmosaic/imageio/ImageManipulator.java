package photographicmosaic.imageio;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public final class ImageManipulator
{	
	public ImageManipulator()
	{
	}
	
	public static int[] averagePixelValues(BufferedImage image, boolean grayscale)
	{
		return averagePixelValuesPerArea(image, grayscale, 0, 0, image.getWidth(), image.getHeight());
	}
	
	public static BufferedImage getImage(String filename) throws IOException
	{
		return ImageIO.read(new File(filename));
	}
	
	public static BufferedImage createImage(BufferedImage[][] subimages)
	{
		return null;
	}
	
	//perhaps include a method that identifies the average pixel values for portions of the image? maybe like this?
	public static int[] averagePixelValuesPerArea(BufferedImage image, boolean grayscale, int x, int y, int width, int height)
	{
		return null;
	}

	public static void main(String[] args)
	{
		BufferedImage image = null;
		boolean doesNotHaveValidName = true;
		while(doesNotHaveValidName)
			try
			{
				image = ImageManipulator.getImage("test.jpg");
				doesNotHaveValidName = false;
			}
			catch(IOException e)
			{}
				
		System.out.println(image);
		System.out.println(image.getWidth());
		System.out.println(image.getHeight());
	}
}
