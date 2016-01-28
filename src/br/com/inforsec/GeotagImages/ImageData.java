package br.com.inforsec.GeotagImages;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.IImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;

public class ImageData {

	private Graphics2D g2d;
	private byte[] imageData;

	public ImageData(InputStream instream, String path) throws IOException {
		GeotagImages.LOG.println("ImageData: Starting");
		this.imageData = IOUtils.toByteArray(instream);
		instream.close();
	}

	public synchronized void addText(String text) throws IOException,
			ImageReadException, ImageWriteException {
		GeotagImages.LOG.println("ImageData: addText");
		BufferedImage image = readImage(this.imageData);

		// Save existing metadata, if any
		TiffImageMetadata metadata = readExifMetadata(this.imageData);
		this.imageData = null; // allow immediate GC

		this.g2d = image.createGraphics();
		this.g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		this.g2d.setFont(new Font("SansSerif", Font.BOLD, 16));

		String[] split = text.split("\n");
		FontRenderContext frc = this.g2d.getFontRenderContext();

		int maxLineWidth = 0;
		int lineHeight = 0;

		for (String line : split) {
			Rectangle2D bounds = this.g2d.getFont().getStringBounds(line, frc);
			lineHeight = (int) bounds.getHeight();
			maxLineWidth = (int) (bounds.getWidth() > maxLineWidth ? bounds
					.getWidth() : maxLineWidth);
		}

		int baseX = image.getWidth() - maxLineWidth - 15;
		int baseY = image.getHeight() - (lineHeight * split.length) - 15;

		for (String line : split) {
			this.g2d.setColor(Color.BLACK);
			this.g2d.drawString(line, baseX, baseY += this.g2d.getFontMetrics()
					.getHeight());
			this.g2d.setColor(Color.WHITE);
			this.g2d.drawString(line, baseX - 2, baseY - 2);
		}

		// rewrite resized image as byte[]
		byte[] resizedData = writeJPEG(image);
		image = null; // allow immediate GC

		// Re-code new image + metadata to imageData
		if (metadata != null) {
			this.imageData = writeExifMetadata(metadata, resizedData);
		} else {
			this.imageData = resizedData;
		}
	}

	public synchronized byte[] getJPEGData() {
		return imageData;
	}

	private TiffImageMetadata readExifMetadata(byte[] jpegData)
			throws ImageReadException, IOException {
		IImageMetadata imageMetadata = Imaging.getMetadata(jpegData);
		if (imageMetadata == null) {
			return null;
		}
		JpegImageMetadata jpegMetadata = (JpegImageMetadata) imageMetadata;
		TiffImageMetadata exif = jpegMetadata.getExif();
		if (exif == null) {
			return null;
		}
		return exif;
	}

	private BufferedImage readImage(byte[] data) throws IOException {
		return ImageIO.read(new ByteArrayInputStream(data));
	}

	public synchronized void resize(int maxWidth, int maxHeight)
			throws IOException, ImageReadException, ImageWriteException {
		GeotagImages.LOG.println("ImageData: resize");
		// Resize the image if necessary
		BufferedImage image = readImage(imageData);
		if (image.getWidth() > maxWidth || image.getHeight() > maxHeight) {

			// Save existing metadata, if any
			TiffImageMetadata metadata = readExifMetadata(imageData);
			imageData = null; // allow immediate GC

			// resize
			image = Scalr.resize(image, maxWidth, maxHeight);

			// rewrite resized image as byte[]
			byte[] resizedData = writeJPEG(image);
			image = null; // allow immediate GC

			// Re-code resizedData + metadata to imageData
			if (metadata != null) {
				this.imageData = writeExifMetadata(metadata, resizedData);
			} else {
				this.imageData = resizedData;
			}
		}
	}

	private byte[] writeExifMetadata(TiffImageMetadata metadata, byte[] jpegData)
			throws ImageReadException, ImageWriteException, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new ExifRewriter().updateExifMetadataLossless(jpegData, out,
				metadata.getOutputSet());
		out.close();
		return out.toByteArray();
	}

	private byte[] writeJPEG(BufferedImage image) throws IOException {
		ByteArrayOutputStream jpegOut = new ByteArrayOutputStream();
		ImageIO.write(image, "JPEG", jpegOut);
		jpegOut.close();
		return jpegOut.toByteArray();
	}

	public synchronized void writeJPEG(OutputStream outstream)
			throws IOException {
		IOUtils.write(imageData, outstream);
	}
}