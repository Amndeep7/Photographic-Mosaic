package photographicmosaic.imageio;

import java.awt.image.BufferedImage;

public class MetaImage
{
	private String path;
	private BufferedImage image;
	private double[] averageColorValues;
	
	public MetaImage(String p) throws Exception
	{
		path = p;
		image = ImageManipulator.getImage(path);
		averageColorValues = ImageManipulator.averagePixelValues(image);
	}
	
	public String toString()
	{
		return getPath().substring(getPath().lastIndexOf("/")+1);
	}
	
	public String getPath()
	{
		return path;
	}
	public BufferedImage getImage()
	{
		return image;
	}
	public double[] getAverageValues()
	{
		return averageColorValues;
	}
}
