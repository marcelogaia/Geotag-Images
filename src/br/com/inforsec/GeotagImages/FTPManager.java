package br.com.inforsec.GeotagImages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class FTPManager {

	private static int getResponseCode(String urlString)
			throws MalformedURLException, IOException {
		try {
			final URL url = new URL(urlString);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			huc.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
			int responseCode = huc.getResponseCode();
			return responseCode;
			// Handle response code here...
		} catch (UnknownHostException | FileNotFoundException e) {
			e.printStackTrace(GeotagImages.LOG);
		} catch (Exception e) {
			e.printStackTrace(GeotagImages.LOG);
		}

		return -1;
	}

	private FTPClient ftpClient;

	private Properties props;

	public FTPManager(String propertiesFile) {

		Properties props = new Properties();
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(propertiesFile);
			props.load(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(GeotagImages.LOG);
		}

		this.props = props;
		this.ftpClient = new FTPClient();
	}

	public void connect() throws NumberFormatException, SocketException,
			IOException {
		System.out.println("FTPManager: Connecting to the FTP Server...");
		this.ftpClient.connect(this.props.getProperty("FTP_HOST"),
				Integer.parseInt(this.props.getProperty("FTP_PORT")));
		this.ftpClient.login(this.props.getProperty("FTP_USERNAME"),
				this.props.getProperty("FTP_PASSWORD"));

		this.ftpClient.enterLocalPassiveMode();
		System.out.println("FTPManager: Connected");
	}

	public void disconnect() throws IOException {
		this.ftpClient.disconnect();
	}

	private boolean isOnline(String urlString) throws MalformedURLException,
			IOException {
		urlString = urlString.replaceAll(" ", "%20");
		return getResponseCode(urlString) == HttpURLConnection.HTTP_OK;
	}

	public String uploadFile(File file) throws IOException {
		if (this.ftpClient.isConnected()) {
			System.out.println("FTPManager: Uploading File: \""
					+ file.getName() + "\"...");
			InputStream fileInputStream = new FileInputStream(file);
			this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			boolean done = this.ftpClient.storeFile(
					this.props.getProperty("FTP_ABSOLUTE_PATH")
							+ file.getName(), fileInputStream);
			fileInputStream.close();

			String uploadedFile = this.props.getProperty("FTP_WEB_PATH")
					+ file.getName();

			if (done) {
				if (isOnline(uploadedFile)) {
					System.out.println("FTPManager: File \"" + file.getName()
							+ "\" successfully uploaded.");
					return uploadedFile;
				}
			}
		}

		System.err.println("FTPManager: Error in uploading the file \""
				+ file.getName() + "\".");
		return null;
	}
}
