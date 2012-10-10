package photographicmosaic.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import photographicmosaic.imageio.ImageManipulator;
import photographicmosaic.imageio.MetaImage;

public class MosaicCreator
{
	static MetaImage[] metaimages;
	
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

	public static void convertFromFilesToMetaImages(final ArrayList<File> list)
	{
		System.out.println("greetings");
		for(File f : list)
		{
			System.out.println(f.toString());
		}
		ExecutorService pool = Executors.newCachedThreadPool();
		metaimages = new MetaImage[list.size()];
		for(int x = 0; x < metaimages.length; x++)
		{
			System.out.println("hello");
			final int xx = x;
			pool.execute(new Runnable()
				{
					public void run()
					{
						System.out.println("hi");
						try {
							metaimages[xx] = new MetaImage(list.get(xx).getAbsolutePath());
						} catch (IOException e) {
							e.printStackTrace();
							System.exit(1);
						}
					}
				}
			);

			System.out.println("Have finished converting " + x + " out of " + metaimages.length);
		}
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
		//File imageDirectory = new File("/home/amn/RandomArtAssignmentPictures/");
		//File imageDirectory = new File("/home/amn/Mom/");
		File imageDirectory = new File("/afs/csl.tjhsst.edu/students/2013/2013amann/SomePics/");
		System.out.println(imageDirectory.exists() ? "Found the directory" : "Did not find the directory");
		convertFromFilesToMetaImages(getImageFiles(imageDirectory));
		System.out.println("This has probably taken quite some time.");

		double[] base = {0, 0, 0};

		for(MetaImage i : metaimages)
		{
			System.out.println(i + " " + ImageManipulator.distance(i.getAverageValues(), base));
		}
		
		//MetaImage source = new MetaImage("/home/amn/Programming/Java/Photographic-Mosaic/mom.jpg");
		MetaImage source = new MetaImage("/afs/csl.tjhsst.edu/students/2013/2013amann/ihnRuSQAn8rI.jpg");

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

				selectedImages[y][x] = bestImage(ave, metaimages);

				System.out.println("Got through " + x + ", " + y);
			}
		}

		ImageManipulator.makeImageFile("/home/amn/MOMMY2.jpg", ImageManipulator.createImage(selectedImages, 5000, 5000));
		System.out.println("done.");
	}

}
