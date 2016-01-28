package br.com.inforsec.GeotagImages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Image {
	private Date datetime;
	private File file;
	private HashMap<String, Object> gMaps;
	private double latitude;
	private double longitude;
	private String path;

	public Image(String path) {

		GeotagImages.LOG.println("Image: Starting");

		this.path = path;
		this.file = new File(path);
		
		GeotagImages.LOG.println("Image: path = " + path);
		
		Metadata drewmetadata = null;
		GeoLocation geoLocation;
		
		try {
			drewmetadata = ImageMetadataReader.readMetadata(this.file);

			if (drewmetadata != null) {
				// Getting the GPS data and putting into the fields latitude and longitude
	            Collection<GpsDirectory> gpsDirectories = drewmetadata.getDirectoriesOfType(GpsDirectory.class);
	            
	            if (gpsDirectories != null) {
	            	for (GpsDirectory gpsDirectory : gpsDirectories) {
	                    // Try to read out the location, making sure it's non-zero
	                    geoLocation = gpsDirectory.getGeoLocation();
	                    if (geoLocation != null && !geoLocation.isZero()) {
	            			this.latitude = geoLocation.getLatitude();
	            			this.longitude = geoLocation.getLongitude();
	                    }
	                }
	            } else {
        			this.latitude = 0.0;
        			this.longitude = 0.0;
                }
				// Getting the Image data and putting into the field datetime
	            ExifSubIFDDirectory directory = drewmetadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

	            Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
		        
	            if (date != null) 	this.datetime = date;
	            else 				this.datetime = new Date(0);
	            
			}
		} catch (ImageProcessingException | IOException ipx) { }
	}

	public String getDatetime() {
		if (this.datetime != null) {
			SimpleDateFormat dateConverter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			return dateConverter.format(this.datetime);
		}
		return "";
	}

	public File getFile() {
		return file;
	}

	/**
	 * @return the gMaps
	 */
	public HashMap<String, Object> getGMaps() {
		gMaps = new HashMap<String, Object>();

		if (this.latitude == 0.0 || this.longitude == 0.0) {

			gMaps.put("mapsURL", "");
			gMaps.put("address", "");
			gMaps.put("jsonObject", "");

		} else {

			String mapsURL = "http://maps.google.com/?q=" + this.latitude + ","
					+ this.longitude;
			String JSON = "http://maps.googleapis.com/maps/api/geocode/json?latlng="
					+ this.latitude + "," + this.longitude + "&sensor=false";

			try {
				URL url = new URL(JSON);
				HttpURLConnection request = (HttpURLConnection) url
						.openConnection();
				request.connect();

				JsonParser jp = new JsonParser();
				JsonElement root = jp.parse(new InputStreamReader(
						(InputStream) request.getContent()));
				JsonObject rootobj = root.getAsJsonObject();

				rootobj = rootobj.get("results").getAsJsonArray().get(0)
						.getAsJsonObject();

				String formattedAddress = rootobj.get("formatted_address")
						.getAsString();

				gMaps.put("mapsURL", mapsURL);
				gMaps.put("address", formattedAddress);
				gMaps.put("jsonObject", rootobj);

			} catch (IOException e) {
				e.printStackTrace(GeotagImages.LOG);
			}
		}

		return gMaps;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getPath() {
		return path;
	}

	public Image resizeTo(int maxWidth, int maxHeight, String resultPath)
			throws IOException {
		File srcfile = new File(this.path);
		String fileName = srcfile.getName();

		String name = resultPath + "/" + fileName;

		javaxt.io.Image xtImage = new javaxt.io.Image(this.path);

		ImageInputStream stream = ImageIO.createImageInputStream(new File(
				this.path));
		Iterator<?> iter = ImageIO.getImageReaders(stream);
		ImageOutputStream ios = ImageIO.createImageOutputStream(new File(name));

		ImageReader reader = (ImageReader) iter.next();
		reader.setInput(stream);
		IIOMetadata iIOMetadata = reader.getImageMetadata(0);

		xtImage = xtImage.copy();
		xtImage.setOutputQuality(0.3);
		xtImage.resize(maxWidth, maxHeight, true);

		xtImage = new javaxt.io.Image(xtImage.getByteArray());

		IIOImage iioim = new IIOImage(xtImage.getBufferedImage(), null,
				iIOMetadata);

		Iterator<?> writers = ImageIO.getImageWritersByFormatName("jpg");
		ImageWriter writer = (ImageWriter) writers.next();

		writer.setOutput(ios);
		writer.write(iioim);
		ios.close();

		return new Image(name);
	}
}
