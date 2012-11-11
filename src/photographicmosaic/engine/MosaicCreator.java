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
		System.out.println("in method " + metaimages + " " + metaimages.length);
		for(int x = 0; x < metaimages.length; x++)
		{
			System.out.println("hello");
			final int xx = x;
			pool.execute(new Runnable()
			{
				public void run()
				{
					System.out.println("hi");
					try
					{
						metaimages[xx] = new MetaImage(list.get(xx).getAbsolutePath());
						System.out.println("inside parallel " + metaimages[xx]);
					}
					catch(IOException e)
					{
						e.printStackTrace();
						System.exit(1);
					}
				}
			});

			System.out.println("Have finished converting " + x + " out of " + metaimages.length);
		}

		pool.shutdown();
		while(!pool.isTerminated())// while jobs aren't finished - this is the part of the program that takes the longest
		{
			try
			{
				Thread.sleep(1000);
				System.out.println("working");
			}
			catch(InterruptedException ie)
			{
			}
		}

		for(MetaImage i : metaimages)
			System.out.println("End of method: " + i);
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
		final MetaImage source = new MetaImage(args[0]);
		final File imageDirectory = new File(args[1]);
		final String destination = args[2];
		final int rows = Integer.parseInt(args[3]);
		final int columns = Integer.parseInt(args[4]);
		final int destinationWidth = Integer.parseInt(args[5]);
		final int destinationHeight = Integer.parseInt(args[6]);
		
		System.out.println(imageDirectory.exists() ? "Found the directory" : "Did not find the directory");
		convertFromFilesToMetaImages(getImageFiles(imageDirectory));
		System.out.println("This has probably taken quite some time.");

		final double[] base = {0, 0, 0};

		System.out.println(metaimages);

		for(MetaImage i : metaimages)
			System.out.println(i);

		for(MetaImage i : metaimages)
		{
			System.out.println(i + " " + ImageManipulator.distance(i.getAverageValues(), base));
		}


		final int width = source.getImage().getWidth();
		final int height = source.getImage().getHeight();

		System.out.println("Got through preliminaries");

		final MetaImage[][] selectedImages = new MetaImage[rows][columns];

		ExecutorService pool = Executors.newCachedThreadPool();

		for(int y = 0; y < rows; y++)
		{
			final int rowheight = (y == rows - 1) ? height % rows + height / rows : height / rows;

			final int yy = y;

			pool.execute(new Runnable()
			{
				public void run()
				{
					System.out.println("hi");

					for(int x = 0; x < columns; x++)
					{
						final int columnwidth = (x == columns - 1) ? width % columns + width / columns : width / columns;

						try{
							double[] ave = ImageManipulator.averagePixelValuesPerArea(source.getImage(), x * (width / columns), yy * (height / rows), columnwidth, rowheight);
							System.out.println(ImageManipulator.distance(ave, base) + " " + ave[0] + " " + ave[1] + " " + ave[2]);

							selectedImages[yy][x] = bestImage(ave, metaimages);
						}catch(IOException e)
						{
							e.printStackTrace();
							System.exit(1);
						}
						System.out.println("Got through " + x + ", " + yy);
					}
				}
			});
		}

		pool.shutdown();
		while(!pool.isTerminated())// while jobs aren't finished - this is the part of the program that takes the longest
		{
			try
			{
				Thread.sleep(1000);
				System.out.println("working");
			}
			catch(InterruptedException ie)
			{
			}
		}

		ImageManipulator.makeImageFile(destination, ImageManipulator.createImage(selectedImages, destinationWidth, destinationHeight));
		System.out.println("done.");
	}

}
