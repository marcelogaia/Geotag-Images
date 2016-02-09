package br.com.inforsec.GeotagImages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JOptionPane;

public class GeotagImages {

	private static final int IMG_MAX_HEIGHT = 768;
	private static final int IMG_MAX_WIDTH = 1024;
	public static PrintStream LOG;
	
	public static void save(String inputFolder, String projectName, boolean generateExcel) {
		try {
			FileOutputStream fos = new FileOutputStream(new File("log.log"),
					true);
			LOG = new PrintStream(fos);

			// Setting the main input and output folder
			LOG.println("GeotagImages: Setting the main input and output folder");

			//String inputFolder = fc.getSelectedFile().getPath();
			String outputFolder = inputFolder + "/output";

			ImageFolder folder;
			folder = new ImageFolder(inputFolder, outputFolder);

			// Getting the list of images to an ArrayList
			LOG.println("GeotagImages: Getting the list of images to an ArrayList");
			ArrayList<Image> images = folder.getImages();

			// Connecting to the Database
			DatabaseManager dm = new DatabaseManager("config.properties");
			
			// Connecting to the FTP Server
			FTPManager ftpm = new FTPManager("config.properties");
			
			if(images.size() > 0){
				LOG.println("GeotagImages: Connecting to the Database");
				dm.connect();
				
				LOG.println("GeotagImages: Connecting to the FTP Server");
				ftpm.connect();
			}
			// Starts the processing of images
			LOG.println("GeotagImages: Starts the processing of images");
			for (int i = 0; i < images.size(); i++) {

				Image img = images.get(i);

				/* 
				 * If img equals null, it means it is not an image, maybe a 
				 * folder or other files
				 */
				if (img != null) {
					// Creates the imgData object to prepare the image to be resized
					ImageData imgData = new ImageData(new FileInputStream(
							img.getPath()), img.getPath());
					imgData.resize(IMG_MAX_WIDTH, IMG_MAX_HEIGHT);

					Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.parse(img.getDatetime());

					String newDate = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss")
							.format(date);

					imgData.addText("Data: " + newDate + "\nLatitude: "
							+ img.getLatitude() + "\nLongitude: "
							+ img.getLongitude());

					/* 
					 * After resizing, sets the final path of the file and 
					 * creates it.
					 */
					String resultPath = outputFolder + "/"
							+ img.getFile().getName();
					imgData.writeJPEG(new FileOutputStream(resultPath));

					Image resultImg = new Image(resultPath);

					// Gets the Google Maps information
					LOG.println("GeotagImages: Gets the Google Maps information");
					HashMap<String, Object> gmaps = img.getGMaps();

					// Uploads the file and gets the url to access the file
					LOG.println("GeotagImages: Uploads the file and gets the url to access the file");
					String responseSubmit = ftpm.uploadFile(resultImg.getFile());
					//responseSubmit = "teste";

					// Creates the queryBuilder object and populates it.
					LOG.println("GeotagImages: Creates the queryBuilder object and populates it");
					HashMap<String, Object> queryBuilder = new HashMap<String, Object>();
					queryBuilder.put("path", responseSubmit);
					queryBuilder.put("longitude", img.getLongitude());
					queryBuilder.put("latitude", img.getLatitude());
					queryBuilder.put("gmaps_url", gmaps.get("mapsURL"));
					queryBuilder.put("gmaps_address", gmaps.get("address"));
					queryBuilder.put("datetime", img.getDatetime());
					queryBuilder.put("label", projectName);
					// queryBuilder.put("gmaps_json", gmaps.get("jsonObject"));

					if(responseSubmit == "teste") continue;
					
					String comma = "";
					String fields = "";
					String values = "";

					// Build the Strings containing both the fields and values
					// in order to create the insert
					LOG.println("GeotagImages: Build the Strings containing both the fields and values in order to create the insert");
					for (String key : queryBuilder.keySet()) {
						fields += comma + key;
						String value = "";
						if (queryBuilder.get(key) == null)
							value = "\"\"";
						else
							value = queryBuilder.get(key) instanceof String ? "\""
									+ queryBuilder.get(key) + "\""
									: queryBuilder.get(key).toString();

						values += comma + value;

						comma = ",";
					}

					// Inserts the values in the database table
					LOG.println("GeotagImages: Inserts the values in the database table");
					dm.insert(fields, values);

					/*
					 * eFile.addRow(
					 * img.getPath(),"","",img.getLatitude(),img.getLongitude(),
					 * "",gmaps.get("mapsURL"),gmaps.get("address"));
					 */
				}
				LOG.println("GeotagImages: Resizing and Uploading File "
						+ (i + 1) + "/" + images.size());
			}

			ftpm.disconnect();
			dm.disconnect();

			JOptionPane.showMessageDialog(null,"Imagens inseridas com sucesso.");

			// eFile.close();
		} catch (Exception e) {
			if (e.getMessage() != null)
				JOptionPane.showMessageDialog(null, e.getMessage());

			e.printStackTrace(LOG);
		}

		System.exit(0);
	}
}
