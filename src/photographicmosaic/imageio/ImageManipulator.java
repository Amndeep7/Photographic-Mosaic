/*
 * Compiling: in path/src/photographicmosaic/imageio javac -d . ImageManipulator.java Running: in path/src/ java -cp photographicmosaic/imageio/ photographicmosaic.imageio.ImageManipulator
 */

package photographicmosaic.imageio;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageManipulator
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

	public static BufferedImage createImage(BufferedImage[][] subimages, int width, int height)
	{
		int tempwidth = width - width % subimages[0].length;
		int tempheight = height - height % subimages.length;
		BufferedImage temp = new BufferedImage(tempwidth, tempheight, subimages[0][0].getType());

		if(subimages.length == 0)
		{
			if(subimages[0].length == 0)
			{
				temp.createGraphics().drawImage(subimages[0][0], 0, 0, tempwidth, tempheight, null);
			}
			else
			{
				for(int x = 0; x < subimages[0].length; x++)
				{
					temp.createGraphics().drawImage(subimages[0][x], x * tempwidth / subimages[0].length, 0, tempwidth / subimages[0].length, tempheight / subimages.length, null);
				}
			}
		}
		else
		{
			for(int y = 0; y < subimages.length; y++)
			{
				for(int x = 0; x < subimages[y].length; x++)
				{
					temp.createGraphics().drawImage(subimages[y][x], x * tempwidth / subimages[y].length, y * tempheight / subimages.length, tempwidth / subimages[y].length,
					          tempheight / subimages.length, null);
				}
			}
		}

		BufferedImage ret = new BufferedImage(width, height, temp.getType());
		ret.createGraphics().drawImage(temp, 0, 0, ret.getWidth(), ret.getHeight(), null);

		return ret;
	}

	// perhaps include a method that identifies the average pixel values for portions of the image? maybe like this?
	public static int[] averagePixelValuesPerArea(BufferedImage image, boolean grayscale, int x, int y, int width, int height)
	{
		return null;
	}

	public static void main(String[] args)
	{
		BufferedImage image = null;
		boolean doesNotHaveValidName = true;
		int chances = 0;
		while(doesNotHaveValidName)
			try
			{
				chances += 1;
				image = ImageManipulator.getImage("test.jpg");
				doesNotHaveValidName = false;
			}
			catch(IOException e)
			{
				if(chances >= 3)
				{
					System.out.println("You aren't putting in the right path/filename for your picture.");
					System.exit(1);
				}
			}

		System.out.println(image + " " + image.getWidth() + " " + image.getHeight());
		
		BufferedImage[][] mergearray1 = new BufferedImage[1][1];
		mergearray1[0][0] = image;
		BufferedImage[][] mergearray2 = new BufferedImage[1][5];
		for(int x = 0; x < 5; x++)
		{
			mergearray2[0][x] = image;
		}
		BufferedImage[][] mergearray3 = new BufferedImage[5][5];
		for(int y = 0; y < 5; y++)
		{
			for(int x = 0; x < 5; x++)
			{
				mergearray3[y][x] = image;
			}
		}
		
		BufferedImage test1 = createImage(mergearray1, 500, 500);
		BufferedImage test2 = createImage(mergearray2, 500, 500);
		BufferedImage test3 = createImage(mergearray3, 500, 500);

		System.out.println(test1 + " " + test1.getWidth() + " " + test1.getHeight());
		System.out.println(test2 + " " + test2.getWidth() + " " + test2.getHeight());
		System.out.println(test3 + " " + test3.getWidth() + " " + test3.getHeight());
		
		try{
		ImageIO.write(test1, "jpg", new File("test1.jpg"));
		ImageIO.write(test2, "jpg", new File("test2.jpg"));
		ImageIO.write(test3, "jpg", new File("test3.jpg"));
		}
		catch(IOException e)
		{
			System.out.println("The image didn't become written.");
			System.exit(1);
		}
	}
}
