package com.team2.Crowdsourced_Waste_Collection_Recycling_System.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

@UtilityClass
public class FileUpLoadUtil {
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB upload limit
    public static final long MAX_COMPRESSED_SIZE = 1024 * 1024; // 1MB Cloudinary limit
    public static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "bmp");
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    public static final String FILE_NAME_FORMAT = "%s_%s_%s";

    /**
     * Kiểm tra file name có đúng extension cho phép không
     */
    public static boolean isAllowedExtension(String fileName, String pattern) {
        return Pattern
                .compile(pattern, Pattern.CASE_INSENSITIVE)
                .matcher(fileName)
                .matches();
    }

    public static void assertAllowedImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không hợp lệ");
        }

        final long size = file.getSize();
        if (size > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Max file size is 10MB");
        }

        final String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Thiếu tên file");
        }

        final String extension = FilenameUtils.getExtension(fileName);
        if (extension == null || extension.isBlank()) {
            throw new IllegalArgumentException("File không có phần mở rộng");
        }

        final String normalizedExtension = extension.toLowerCase();
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(normalizedExtension)) {
            throw new IllegalArgumentException("Only jpg, jpeg, png, gif, bmp files are supported");
        }
    }

    public static String getFileName(final String name) {
        final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        final String date = dateFormat.format(System.currentTimeMillis());
        return String.format(FILE_NAME_FORMAT, name, date);
    }

    public static byte[] compressImage(MultipartFile file) throws IOException {
        long fileSize = file.getSize();
        if (fileSize <= MAX_COMPRESSED_SIZE) {
            return file.getBytes();
        }

        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            // Cannot read image (e.g. corrupted or unsupported format), return original bytes
            return file.getBytes();
        }

        // Initial settings
        int targetWidth = 1920;
        int targetHeight = 1080;
        float quality = 0.85f;
        
        byte[] result = null;
        int attempts = 0;
        
        // Loop to reduce size until under 1MB
        // Strategy: Reduce quality first, then dimensions if needed
        while (attempts < 10) {
            BufferedImage resized = resizeImage(originalImage, targetWidth, targetHeight);
            result = compressToJpg(resized, quality);
            
            if (result.length <= MAX_COMPRESSED_SIZE) {
                return result;
            }
            
            // Adjust parameters for next attempt
            if (quality > 0.6f) {
                quality -= 0.15f; // Reduce quality significantly
            } else {
                // If quality is already low, reduce dimensions
                targetWidth = (int)(targetWidth * 0.75);
                targetHeight = (int)(targetHeight * 0.75);
                // Reset quality slightly to avoid artifacts at small resolution
                quality = 0.8f;
            }
            attempts++;
        }
        
        // Final fallback: aggressive resize/compression if loop failed
        if (result == null || result.length > MAX_COMPRESSED_SIZE) {
             BufferedImage fallback = resizeImage(originalImage, 800, 800);
             return compressToJpg(fallback, 0.5f);
        }
        
        return result;
    }

    private static BufferedImage resizeImage(BufferedImage original, int maxWidth, int maxHeight) {
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        // Calculate scale to fit within bounds while maintaining aspect ratio
        double scale = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);
        
        // Do not upscale small images
        if (scale > 1.0) {
            scale = 1.0;
        }
        
        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);
        
        // Ensure at least 1px
        newWidth = Math.max(1, newWidth);
        newHeight = Math.max(1, newHeight);

        BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = outputImage.createGraphics();
        
        // Fill white background (handles transparency for JPG conversion)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, newWidth, newHeight);

        // Quality settings for resizing
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return outputImage;
    }

    private static byte[] compressToJpg(BufferedImage image, float quality) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No JPG writer found");
        }
        ImageWriter writer = writers.next();

        ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        writer.write(null, new IIOImage(image, null, null), param);

        writer.dispose();
        ios.close();
        return outputStream.toByteArray();
    }
}
