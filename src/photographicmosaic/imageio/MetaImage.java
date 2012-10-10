package photographicmosaic.imageio;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class MetaImage
{
	private String path;
	private double[] averageColorValues;
	
	public MetaImage(String p) throws IOException
	{
		path = p;
		averageColorValues = ImageManipulator.averagePixelValues(ImageManipulator.getImage(path));
	}
	
	public String toString()
	{
		return getPath().substring(getPath().lastIndexOf("/")+1);
	}
	
	public String getPath()
	{
		return path;
	}
	public BufferedImage getImage() throws IOException
	{
		return ImageManipulator.getImage(path);
	}
	public double[] getAverageValues()
	{
		return averageColorValues;
	}
}
