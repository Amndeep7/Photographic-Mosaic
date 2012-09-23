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
	public static double[] averagePixelValues(BufferedImage image)
	{
		return averagePixelValuesPerArea(image, 0, 0, image.getWidth(), image.getHeight());
	}

	public static BufferedImage getImage(String filename) throws IOException
	{
		return ImageIO.read(new File(filename));
	}

	public static File makeImageFile(String filename, BufferedImage image) throws IOException
	{
		File file = new File(filename);
		
		System.out.println("Writing the image now");

		ImageIO.write(image, "jpg", file);

		return file;
	}
	
	public static BufferedImage createImageLine(BufferedImage[] subimages, int index, int length, int width, int height)
	{
		BufferedImage temp = new BufferedImage(length * width, height, BufferedImage.TYPE_INT_RGB);
		
		if(length < 100)
		{
			for(int x = 0; x < length; x++)
			{
				temp.createGraphics().drawImage(subimages[x], x * width, 0, width, height, null);
			}
		}
		else
		{
			temp.createGraphics().drawImage(createImageLine(subimages, index, length/2, width, height), 0, 0, length*width/2, height, null);
			temp.createGraphics().drawImage(createImageLine(subimages, index+length/2, length/2, width, height), length*width/2, 0, length*width, height, null);
		}
		
		return temp;
	}

	public static BufferedImage createImage(BufferedImage[][] subimages, int width, int height)
	{
		System.out.println("Entered merging area");
		
		int tempwidth = width - width % subimages[0].length;
		int tempheight = height - height % subimages.length;
		BufferedImage temp = new BufferedImage(tempwidth, tempheight, BufferedImage.TYPE_INT_RGB);

		if(subimages.length == 0)
		{
			if(subimages[0].length == 0)
			{
				System.out.println("0 and 0");
				temp.createGraphics().drawImage(subimages[0][0], 0, 0, tempwidth, tempheight, null);
			}
			else
			{
				for(int x = 0; x < subimages[0].length; x++)
				{
					System.out.println("only columns " + x);
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
					System.out.println("Adding in x:" + x + " y:" + y);
					temp.createGraphics().drawImage(subimages[y][x], x * tempwidth / subimages[y].length, y * tempheight / subimages.length, tempwidth / subimages[y].length,
					          tempheight / subimages.length, null);
				}
//				System.out.println("In row " + y);
//				temp.createGraphics().drawImage(createImageLine(subimages[y], 0, subimages.length, tempwidth / subimages[y].length, tempheight / subimages.length), 0, y * tempheight / subimages.length, tempwidth, tempheight / subimages.length, null);
			}
		}
		
		System.out.println("Final resize");

		BufferedImage ret = new BufferedImage(width, height, temp.getType());
		ret.createGraphics().drawImage(temp, 0, 0, ret.getWidth(), ret.getHeight(), null);

		return ret;
	}

	public static double[] averagePixelValuesPerArea(BufferedImage image, int xstart, int ystart, int width, int height)
	{
		double[] ret = new double[3];
		for(int y = ystart; y < ystart + height; y++)
		{
			for(int x = xstart; x < xstart + width; x++)
			{
				ret[0] += (image.getRGB(x, y) >> 16) & 0x000000FF;
				ret[1] += (image.getRGB(x, y) >> 8) & 0x000000FF;
				ret[2] += image.getRGB(x, y) & 0x000000FF;
			}
		}

		ret[0] /= width * height;
		ret[1] /= width * height;
		ret[2] /= width * height;

		return ret;
	}

	// credit to here for color distance: http://www.easyrgb.com/index.php?X=MATH and Wikipedia for explanations
	public static double[] rgbToXYZ(double[] colors)
	{
		double[] rgb = {colors[0] / 255.0, colors[1] / 255.0, colors[2] / 255.0};

		for(double c : rgb)
		{
			if(c > 0.04045)
			{
				c = Math.pow((c + 0.055) / 1.055, 2.4);
			}
			else
			{
				c /= 12.92;
			}

			c *= 100;
		}

		double[] xyz = new double[3];
		xyz[0] = rgb[0] * 0.4124 + rgb[1] * 0.3576 + rgb[2] * 0.1805;
		xyz[1] = rgb[0] * 0.2126 + rgb[1] * 0.7152 + rgb[2] * 0.0722;
		xyz[2] = rgb[0] * 0.0193 + rgb[1] * 0.1192 + rgb[2] * 0.9505;

		return xyz;
	}

	public static double[] xyzToCIE_L_times_ab(double[] colors)
	{
		double refX = 95.047;
		double refY = 100.000;
		double refZ = 108.883;

		double xyz[] = new double[3];
		xyz[0] = colors[0] / refX;
		xyz[1] = colors[1] / refY;
		xyz[2] = colors[2] / refZ;

		for(double c : xyz)
		{
			if(c > 0.008856)
			{
				c = Math.pow(c, 1 / 3.0);
			}
			else
			{
				c = 7.787 * c + 16 / 116.0;
			}
		}

		double lab[] = new double[3];
		lab[0] = (116 * xyz[0]) - 16;
		lab[1] = 500 * (xyz[0] - xyz[1]);
		lab[2] = 200 * (xyz[1] - xyz[2]);

		return lab;
	}

	public static double distance(double[] colors1, double[] colors2)
	{
		double[] lab1 = xyzToCIE_L_times_ab(rgbToXYZ(colors1));
		double[] lab2 = xyzToCIE_L_times_ab(rgbToXYZ(colors2));

		return Math.sqrt((lab2[0] - lab1[0]) * (lab2[0] - lab1[0]) + (lab2[1] - lab1[1]) * (lab2[1] - lab1[1]) + (lab2[2] - lab1[2]) * (lab2[2] - lab1[2]));
	}

	public static void main(String[] args)
	{
		BufferedImage image = null;
		boolean doesNotHaveValidName = true;
		int chances = 0;
		while(doesNotHaveValidName)
		{
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

		try
		{
			ImageIO.write(test1, "jpg", new File("test1.jpg"));
			ImageIO.write(test2, "jpg", new File("test2.jpg"));
			ImageIO.write(test3, "jpg", new File("test3.jpg"));
		}
		catch(IOException e)
		{
			System.out.println("The image didn't become written.");
			System.exit(1);
		}

		BufferedImage black = null;
		BufferedImage white = null;
		try
		{
			black = ImageManipulator.getImage("black_test.jpg");
			white = ImageManipulator.getImage("white_test.jpg");
		}
		catch(IOException e)
		{
			System.out.println("You aren't putting in the right path/filename for your picture.");
			System.exit(1);
		}

		System.out.println(black + " " + black.getWidth() + " " + black.getHeight() + " " + averagePixelValues(black)[0] + " " + averagePixelValues(black)[1] + " "
		          + averagePixelValues(black)[2]);
		System.out.println(white + " " + white.getWidth() + " " + white.getHeight() + " " + averagePixelValues(white)[0] + " " + averagePixelValues(white)[1] + " "
		          + averagePixelValues(white)[2]);

		BufferedImage gray = null;
		try
		{
			gray = ImageManipulator.getImage("gray_test.jpg");
		}
		catch(IOException e)
		{
			System.out.println("You aren't putting in the right path/filename for your picture.");
			System.exit(1);
		}
		System.out.println(gray + " " + gray.getWidth() + " " + gray.getHeight() + " " + averagePixelValues(gray)[0]);

		MetaImage testing = null;
		try
		{
			testing = new MetaImage("mom.jpg");
		}
		catch(Exception e)
		{
			System.out.println("You aren't putting in the right path/filename for your picture.");
			System.exit(1);
		}

		double[] tests = ImageManipulator.averagePixelValues(testing.getImage());
		System.out.println(tests[0] + " " + tests[1] + " " + tests[2]);

		int width = testing.getImage().getWidth();
		int height = testing.getImage().getHeight();
		int columns = 20;
		int rows = 20;

		System.out.println("Got through preliminaries");

		int y, x;
		for(y = 0; y < rows; y++)
		{
			int rowheight = (y == rows - 1) ? height % rows + height / rows : height / rows;
			for(x = 0; x < columns; x++)
			{
				int columnwidth = (x == columns - 1) ? width % columns + width / columns : width / columns;

				System.out.println((x * (width / columns)) + " " + (y * (height / rows)) + " " + columnwidth + " " + rowheight);

				double[] ave = ImageManipulator.averagePixelValuesPerArea(testing.getImage(), x * (width / columns), y * (height / rows), columnwidth, rowheight);
				System.out.println(ave[0] + " " + ave[1] + " " + ave[2]);

				System.out.println("Got through " + x + ", " + y);
			}
		}
	}
}
