package com.hxy.zdfz.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;

public class ImageUtils {
	public enum Mode {
		AUTOMATIC, FIT_TO_WIDTH, FIT_TO_HEIGHT
	}

	public static byte[] resize(BufferedImage srcBufferedImage,
			int targetWidth, int targetHeight, Mode mode) throws IOException {
		return resize(srcBufferedImage, targetWidth, targetHeight, 1f,
				mode, false);
	}

	public static byte[] resize(BufferedImage srcBufferedImage,
			int targetWidth, int targetHeight, boolean square)
			throws IOException {
		return resize(srcBufferedImage, targetWidth, targetHeight, 1f,
				Mode.AUTOMATIC, square);
	}

	public static byte[] resize(BufferedImage srcBufferedImage,
			int targetWidth, int targetHeight, float quality, Mode mode,
			boolean square) throws IOException {
		if (quality > 1) {
			throw new IllegalArgumentException(
					"Quality has to be between 0 and 1");
		}
		int width = 0, height = 0;
		if (square) {
			// 正方形，需要提前进行裁剪
			width = srcBufferedImage.getWidth();
			height = srcBufferedImage.getHeight();
			if (width > height) {
				int x = (width - height) / 2;
				srcBufferedImage = srcBufferedImage.getSubimage(x, 0, height,
						height);
			} else if (width < height) {
				int y = (height - width) / 2;
				srcBufferedImage = srcBufferedImage.getSubimage(0, y, width,
						width);
			}
		}

		Image resizedImage = null;
		width = srcBufferedImage.getWidth();
		height = srcBufferedImage.getHeight();
		if(mode == Mode.FIT_TO_WIDTH){
			resizedImage = srcBufferedImage.getScaledInstance(targetWidth,
					(targetWidth * height) / width, Image.SCALE_SMOOTH);
		} else if(mode == Mode.FIT_TO_HEIGHT) {
			resizedImage = srcBufferedImage.getScaledInstance((targetHeight * width) / height,
					targetHeight, Image.SCALE_SMOOTH);
		} else {
			if (width > height) {
				resizedImage = srcBufferedImage.getScaledInstance(targetWidth,
						(targetWidth * height) / width, Image.SCALE_SMOOTH);
			} else {
				resizedImage = srcBufferedImage.getScaledInstance(
						(targetWidth * width) / height, targetWidth,
						Image.SCALE_SMOOTH);
			}
		}

		// This code ensures that all the pixels in the image are loaded.
		Image temp = new ImageIcon(resizedImage).getImage();

		// Create the buffered image.
		BufferedImage bufferedImage = new BufferedImage(temp.getWidth(null),
				temp.getHeight(null), BufferedImage.TYPE_INT_RGB);

		// Copy image to buffered image.
		Graphics g = bufferedImage.createGraphics();

		// Clear background and paint the image.
		g.setColor(Color.white);
		g.fillRect(0, 0, temp.getWidth(null), temp.getHeight(null));
		g.drawImage(temp, 0, 0, null);
		g.dispose();

		// Soften.
		float softenFactor = 0.05f;
		float[] softenArray = { 0, softenFactor, 0, softenFactor,
				1 - (softenFactor * 4), softenFactor, 0, softenFactor, 0 };
		Kernel kernel = new Kernel(3, 3, softenArray);
		ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
		bufferedImage = cOp.filter(bufferedImage, null);

		ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
		ImageWriteParam param = writer.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(1.0F); // Highest quality
		// Write the JPEG to our ByteArray stream
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageOutputStream imageOutputStream = ImageIO
				.createImageOutputStream(byteArrayOutputStream);
		writer.setOutput(imageOutputStream);
		writer.write(null, new IIOImage(bufferedImage, null, null), param);
		return byteArrayOutputStream.toByteArray();
	}

	// Example usage
	public static void main(String[] args) throws IOException {
		File originalImage = new File("E:/111.jpg");
		byte[] bytes = resize(ImageIO.read(originalImage), 100, 100, 1f, Mode.AUTOMATIC, true);
		FileOutputStream out = new FileOutputStream(new File("E:/66.jpg"));
		out.write(bytes);
		out.close();
		System.out.println("Ok");
	}
}
