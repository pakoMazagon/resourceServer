package com.tpv.mesas.infrastructure.adapters;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EscPosImageHelper {
    public static byte[] imageToRasterBitImageCommand(BufferedImage image) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        final int width = image.getWidth();
        final int height = image.getHeight();
        final int widthBytes = (width + 7) / 8;
        final byte[] imageBytes = new byte[widthBytes * height];

        // blanco = 0, negro = 1
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int pixel = image.getRGB(x, y);
                final int alpha = (pixel >> 24) & 0xFF;
                final int r = (pixel >> 16) & 0xFF;
                final int g = (pixel >> 8) & 0xFF;
                final int b = pixel & 0xFF;
                final int luminance = (int) (0.299 * r + 0.587 * g + 0.114 * b);

                // ignoramos píxeles transparentes
                if (alpha > 50 && luminance < 128) {
                    imageBytes[y * widthBytes + (x / 8)] |= (1 << (7 - (x % 8)));
                }
            }
        }

        final int xL = widthBytes & 0xFF;
        final int xH = (widthBytes >> 8) & 0xFF;
        final int yL = height & 0xFF;
        final int yH = (height >> 8) & 0xFF;

        output.write(new byte[]{0x1B, 0x33, 0x00});
        output.write(new byte[]{0x1D, 0x76, 0x30, 0x00, (byte) xL, (byte) xH, (byte) yL, (byte) yH});
        output.write(imageBytes);

        return output.toByteArray();
    }
}
