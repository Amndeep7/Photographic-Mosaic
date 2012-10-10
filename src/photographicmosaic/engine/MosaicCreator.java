package photographicmosaic.engine;

import java.io.File;
import java.util.ArrayList;

import photographicmosaic.imageio.ImageManipulator;
import photographicmosaic.imageio.MetaImage;

public class MosaicCreator
{
	public static ArrayList<File> getImageFiles(File root)
	{
		ArrayList<File> images = new ArrayList<File>();
		for(File f : root.listFiles())
		{
			if(!f.getName().toLowerCase().endsWith(".jpg"))
			{
				if(f.isDirectory())
				{
					images.addAll(getImageFiles(f));
				}
			}
			else
			{
				images.add(f);
			}
		}

		return images;
	}

	public static MetaImage[] convertFromFilesToMetaImages(ArrayList<File> list) throws Exception
	{
		MetaImage[] images = new MetaImage[list.size()];
		for(int x = 0; x < images.length; x++)
		{
			images[x] = new MetaImage(list.get(x).getAbsolutePath());

			System.out.println("Have finished converting " + x + " out of " + images.length);
		}

		return images;
	}

	public static MetaImage bestImage(double[] source, MetaImage[] images)
	{
		MetaImage best = images[0];
		double bestDistance = ImageManipulator.distance(source, best.getAverageValues());

		for(MetaImage m : images)
		{
			if(ImageManipulator.distance(source, m.getAverageValues()) < bestDistance)
			{
				best = m;
				bestDistance = ImageManipulator.distance(source, best.getAverageValues());
			}
		}

		System.out.println("Just making sure that the best is still the best: " + best);

		return best;
	}

	public static void main(String[] args) throws Exception
	{
		//File imageDirectory = new File("/home/amn/Programming/Java/Photographic-Mosaic/ColorImages/");
		File imageDirectory = new File("/home/amn/RandomArtAssignmentPictures/");
		//File imageDirectory = new File("/home/amn/Mom/");
		System.out.println("Found the directory");
		MetaImage[] images = convertFromFilesToMetaImages(getImageFiles(imageDirectory));
		System.out.println("This has probably taken quite some time.");

		double[] base = {0, 0, 0};

		for(MetaImage i : images)
		{
			System.out.println(i + " " + ImageManipulator.distance(i.getAverageValues(), base));
		}
		
		MetaImage source = new MetaImage("/home/amn/Programming/Java/Photographic-Mosaic/mom.jpg");

		int width = source.getImage().getWidth();
		int height = source.getImage().getHeight();
		int columns = 100;
		int rows = 100;

		System.out.println("Got through preliminaries");

		MetaImage[][] selectedImages = new MetaImage[rows][columns];

		int y, x;
		for(y = 0; y < rows; y++)
		{
			int rowheight = (y == rows - 1) ? height % rows + height / rows : height / rows;
			for(x = 0; x < columns; x++)
			{
				int columnwidth = (x == columns - 1) ? width % columns + width / columns : width / columns;

				double[] ave = ImageManipulator.averagePixelValuesPerArea(source.getImage(), x * (width / columns), y * (height / rows), columnwidth, rowheight);
				System.out.println(ImageManipulator.distance(ave, base) + " " + ave[0] + " " + ave[1] + " " + ave[2]);

				selectedImages[y][x] = bestImage(ave, images);

				System.out.println("Got through " + x + ", " + y);
			}
		}

		ImageManipulator.makeImageFile("/home/amn/MOMMY2.jpg", ImageManipulator.createImage(selectedImages, 5000, 5000));
		System.out.println("done.");
	}

}
