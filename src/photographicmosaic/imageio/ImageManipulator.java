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
	/****************
	 * * Image IO * *
	 ****************/

	/**
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */

	public static BufferedImage getImage(String filename) throws IOException
	{
		return ImageIO.read(new File(filename));
	}

	/**
	 * 
	 * @param filename
	 * @param image
	 * @return
	 * @throws IOException
	 */

	public static File makeImageFile(String filename, BufferedImage image) throws IOException
	{
		File file = new File(filename);

		System.out.println("Writing the image now");

		ImageIO.write(image, "jpg", file);

		return file;
	}

	/**
	 * Creates the final image from the selected images, the images should be in a matrix that represents how they will look in the final image.
	 * 
	 * @param subimages
	 *             The selected subimages that make up the image
	 * @param width
	 *             The width of the final image, an integer representing the number of X pixels.
	 * @param height
	 *             The height of the final image, an integer representing the number of y pixels.
	 * @return A BufferedImage that is a composite image compiled from the subimages.
	 * @throws IOException
	 */

	public static BufferedImage createImage(MetaImage[][] subimages, int width, int height) throws IOException
	{
		System.out.println("Entered merging area");

		int tempwidth = width - width % subimages[0].length;
		int tempheight = height - height % subimages.length;
		BufferedImage temp = new BufferedImage(tempwidth, tempheight, BufferedImage.TYPE_INT_RGB);

		if(subimages.length == 0)
		{
			if(subimages[0].length == 0)
			{
				// System.out.println("0 and 0");
				temp.createGraphics().drawImage(subimages[0][0].getImage(), 0, 0, tempwidth, tempheight, null);
			}
			else
			{
				for(int x = 0; x < subimages[0].length; x++)
				{
					// System.out.println("only columns " + x);
					temp.createGraphics().drawImage(subimages[0][x].getImage(), x * tempwidth / subimages[0].length, 0, tempwidth / subimages[0].length, tempheight / subimages.length,
					          null);
				}
			}
		}
		else
		{
			for(int y = 0; y < subimages.length; y++)
			{
				for(int x = 0; x < subimages[y].length; x++)
				{
					// System.out.println("Adding in x:" + x + " y:" + y);
					temp.createGraphics().drawImage(subimages[y][x].getImage(), x * tempwidth / subimages[y].length, y * tempheight / subimages.length, tempwidth / subimages[y].length,
					          tempheight / subimages.length, null);
				}
			}
		}

		// System.out.println("Final resize");

		BufferedImage ret = new BufferedImage(width, height, temp.getType());
		ret.createGraphics().drawImage(temp, 0, 0, ret.getWidth(), ret.getHeight(), null);

		return ret;
	}

	/**********************
	 * * Image Analysis * *
	 **********************/

	/**
	 * Finds the average RGB values of an image.
	 * 
	 * @param image
	 *             The image from which the average is being found.
	 * @return The average color values in RGB form of an image; it should be a double array with three values.
	 */

	public static double[] averagePixelValues(BufferedImage image)
	{
		return averagePixelValuesPerArea(image, 0, 0, image.getWidth(), image.getHeight());
	}

	/**
	 * Finds the average RGB values of an image sector.
	 * 
	 * @param image
	 *             The image from which the average is being found.
	 * @param xstart
	 *             The index of the pixel on the left side of the sector to be averaged.
	 * @param ystart
	 *             The index of the pixel on the top side of the sector to be averaged.
	 * @param width
	 *             The width of the sector to be averaged.
	 * @param height
	 *             The height of the sector to be averaged.
	 * @return The average color values in RGB form of an image sector; it should be a double array with three values.
	 */

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

	/**
	 * Converts the color values of an image from RGB form to XYZ form. Credit for the algorithm goes to http://www.easyrgb.com/index.php?X=MATH and to Wikipedia for the explanations.
	 * 
	 * @param colors
	 *             The average color values in RGB form of an image sector; it should be a double array with three values.
	 * @return The corresponding XYZ values; it should be a double array with three values.
	 */

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

	/**
	 * Converts the color values of an image from XYZ form to CIELab form. Credit for the algorithm goes to http://www.easyrgb.com/index.php?X=MATH and to Wikipedia for the explanations.
	 * 
	 * @param colors
	 *             The average color values in XYZ form of an image sector; it should be a double array with three values.
	 * @return The corresponding CIELab values; it should be a double array with three values.
	 */

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
				c = Math.pow(c, (1.0 / 3.0));
			}
			else
			{
				c = (7.787 * c) + (16.0 / 116.0);
			}
		}

		double lab[] = new double[3];
		lab[0] = (116 * xyz[0]) - 16;
		lab[1] = 500 * (xyz[0] - xyz[1]);
		lab[2] = 200 * (xyz[1] - xyz[2]);

		return lab;
	}

	/**
	 * Converts the average RGB color values into the equivalent CIELab values (which by having small changes in their values represent small changes in the color, as opposed to RGB),
	 * whereupon it finds the distance between the two colors using the distance formula (for 3 variables), i.e. sqrt(x^2+y^2+z^2).
	 * 
	 * @param colors1
	 *             The average colors of a sector in the source image; it should be a double array with three values that represent the average R, the average G, and the average B.
	 * @param colors2
	 *             The average colors of an image; it should be a double array with three values that represent the average R, the average G, and the average B.
	 * @return The absolute distance between two color values in the form of a double.
	 */

	public static double distance(double[] colors1, double[] colors2)
	{
		double[] lab1 = xyzToCIE_L_times_ab(rgbToXYZ(colors1));
		double[] lab2 = xyzToCIE_L_times_ab(rgbToXYZ(colors2));

		return Math.sqrt((lab2[0] - lab1[0]) * (lab2[0] - lab1[0]) + (lab2[1] - lab1[1]) * (lab2[1] - lab1[1]) + (lab2[2] - lab1[2]) * (lab2[2] - lab1[2]));
	}
}
