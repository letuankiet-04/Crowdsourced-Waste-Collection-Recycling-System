package com.team2.Crowdsourced_Waste_Collection_Recycling_System.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

@UtilityClass
public class VnptImagePreprocessUtil {
    public static final int MAX_DIMENSION = 2200;
    public static final float JPEG_QUALITY = 0.92f;

    public static byte[] preprocess(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File không hợp lệ");
        }
        try {
            BufferedImage original = ImageIO.read(file.getInputStream());
            if (original == null) {
                return file.getBytes();
            }

            BufferedImage rgb = toRgbWhiteBackground(original);
            BufferedImage resized = resizeDown(rgb, MAX_DIMENSION);
            BufferedImage sharpened = sharpenLight(resized);
            return encodeJpeg(sharpened, JPEG_QUALITY);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không đọc được ảnh đầu vào");
        }
    }

    private static BufferedImage toRgbWhiteBackground(BufferedImage input) {
        if (input.getType() == BufferedImage.TYPE_INT_RGB) {
            return input;
        }
        int w = Math.max(1, input.getWidth());
        int h = Math.max(1, input.getHeight());
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = output.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, w, h);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(input, 0, 0, null);
        g2d.dispose();
        return output;
    }

    private static BufferedImage resizeDown(BufferedImage input, int maxDim) {
        int w = input.getWidth();
        int h = input.getHeight();
        int longest = Math.max(w, h);
        if (longest <= maxDim) {
            return input;
        }
        double scale = (double) maxDim / (double) longest;
        int newW = Math.max(1, (int) Math.round(w * scale));
        int newH = Math.max(1, (int) Math.round(h * scale));

        BufferedImage out = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = out.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, newW, newH);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(input, 0, 0, newW, newH, null);
        g2d.dispose();
        return out;
    }

    private static BufferedImage sharpenLight(BufferedImage input) {
        float[] kernel = new float[]{
                0f, -1f, 0f,
                -1f, 5f, -1f,
                0f, -1f, 0f
        };
        ConvolveOp op = new ConvolveOp(new Kernel(3, 3, kernel), ConvolveOp.EDGE_NO_OP, null);
        BufferedImage dest = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_RGB);
        op.filter(input, dest);
        return dest;
    }

    private static byte[] encodeJpeg(BufferedImage image, float quality) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No JPG writer found");
        }
        ImageWriter writer = writers.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(Math.max(0.1f, Math.min(1.0f, quality)));
        }

        writer.write(null, new IIOImage(image, null, null), param);
        writer.dispose();
        ios.close();
        return outputStream.toByteArray();
    }
}

