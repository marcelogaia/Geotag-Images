package br.com.inforsec.GeotagImages;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.activation.MimetypesFileTypeMap;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class ImageFolder {

	private ArrayList<Image> images = new ArrayList<Image>();

	public ImageFolder(String path, String resultPath)
			throws InvocationTargetException, InterruptedException,
			FileNotFoundException, SecurityException {

		GeotagImages.LOG.println("ImageFolder: Starting");
		File folder = new File(path);

		if (!folder.isDirectory()) {
			throw new FileNotFoundException(
					"The path passed is not a directory");
		}

		File resultFolder = new File(resultPath);

		if (!resultFolder.exists()) {
			if (!resultFolder.mkdir()) {
				throw new SecurityException("Failed to create directory.");
			}
		}

		GeotagImages.LOG.println("ImageFolder: Setting up the jProgressBar");

		JFrame f = new JFrame("Geotag Images - Progresso");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = f.getContentPane();
		JProgressBar progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		Border border = BorderFactory.createTitledBorder("Lendo as imagens...");
		progressBar.setBorder(border);
		content.add(progressBar, BorderLayout.NORTH);
		JLabel label = new JLabel("Calculando tempo estimado...",
				SwingConstants.CENTER);
		label.setFont(new Font("Sans serif", Font.PLAIN, 10));
		f.setResizable(false);
		f.add(label);
		f.setSize(400, 100);
		f.setVisible(true);

		File[] files = folder.listFiles();
		GeotagImages.LOG.println("ImageFolder: files.length=" + files.length);

		progressBar.setMinimum(0);
		progressBar.setMaximum(files.length);

		long startTime = System.currentTimeMillis();

		Runnable runner = new Runnable() {
			@Override
			public void run() {
				int value = progressBar.getValue();
				progressBar.setValue(value + 1);
			}
		};

		GeotagImages.LOG.println("ImageFolder: Starting to read the files.");
		for (int i = 0; i < files.length; i++) {
			if (i > 0) {
				double passedTime = System.currentTimeMillis() - startTime;

				long[] passedTimeArr = { (long) ((passedTime / 1000) % 60),
						(long) ((passedTime / (1000 * 60)) % 60),
						(long) (passedTime / (1000 * 60 * 60)) };

				double totalTime = ((double) files.length / (double) i)
						* passedTime;
				double timeLeft = totalTime - passedTime;

				long[] estimatedTimeArr = { (long) ((timeLeft / 1000) % 60),
						(long) ((timeLeft / (1000 * 60)) % 60),
						(long) (timeLeft / (1000 * 60 * 60)) };

				GeotagImages.LOG.println("ImageFolder: 3");
				String time = String.format("%01dh %01dm %01ds",
						estimatedTimeArr[2], estimatedTimeArr[1],
						estimatedTimeArr[0]);

				String currentTime = String.format("%01dh %01dm %01ds",
						passedTimeArr[2], passedTimeArr[1], passedTimeArr[0]);

				label.setText("Tempo restante: " + time + " / Decorrido: "
						+ currentTime);
			}

			File f1 = files[i];
			String mimetype = new MimetypesFileTypeMap().getContentType(f1);
			String type = mimetype.split("/")[0];

			if (type.equals("image")) {
				this.images.add(new Image(f1.getPath()));
			}

			progressBar.setValue(i);

			Thread t = new Thread(runner);
			//SwingUtilities.invokeAndWait(runner);
			t.start();
			
			GeotagImages.LOG.println("ImageFolder: Reading File " + (i + 1)
					+ "/" + files.length);
		}

		f.setVisible(false);
	}

	public ArrayList<Image> getImages() {
		return this.images;
	}

}
