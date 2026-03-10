package com.team2.Crowdsourced_Waste_Collection_Recycling_System.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
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
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
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

        // Bỏ kiểm tra kích thước tối thiểu 1080px theo yêu cầu
    }
     public static String getFileName(final String name) {
         final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
         final String date = dateFormat.format(System.currentTimeMillis());
         return String.format(FILE_NAME_FORMAT, name, date);
     }

    public static byte[] compressImage(MultipartFile file) throws IOException {
        long fileSize = file.getSize();
        if (fileSize <= 1024 * 1024) {
            return file.getBytes();
        }

        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            return file.getBytes();
        }

        // 1. Resize max 1280x1280
        BufferedImage resizedImage = resizeImage(originalImage, 1280, 1280);

        // 2. Compress 0.85
        byte[] result = compressToJpg(resizedImage, 0.85f);
        if (result.length <= 1024 * 1024) {
            return result;
        }

        // 3. Binary search
        float min = 0.0f;
        float max = 0.85f;
        byte[] bestResult = null;

        for (int i = 0; i < 6; i++) {
            float mid = (min + max) / 2;
            byte[] compressed = compressToJpg(resizedImage, mid);
            if (compressed.length <= 1024 * 1024) {
                bestResult = compressed;
                min = mid; // Try higher quality
            } else {
                max = mid; // Need lower quality
            }
        }

        if (bestResult != null) {
            return bestResult;
        }

        // 4. Fallback: resize more and compress 0.55
        // Resize to 0.7 of 1280 (approx 896)
        BufferedImage fallbackImage = resizeImage(resizedImage, 896, 896);
        return compressToJpg(fallbackImage, 0.55f);
    }

    private static BufferedImage resizeImage(BufferedImage original, int maxWidth, int maxHeight) {
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        boolean needResize = originalWidth > maxWidth || originalHeight > maxHeight;

        if (!needResize && original.getType() == BufferedImage.TYPE_INT_RGB) {
            return original;
        }

        int newWidth = originalWidth;
        int newHeight = originalHeight;

        if (needResize) {
            double ratio = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);
            newWidth = (int) (originalWidth * ratio);
            newHeight = (int) (originalHeight * ratio);
        }

        BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, newWidth, newHeight);

        if (needResize) {
            Image resultingImage = original.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            g2d.drawImage(resultingImage, 0, 0, null);
        } else {
            g2d.drawImage(original, 0, 0, null);
        }
        
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
