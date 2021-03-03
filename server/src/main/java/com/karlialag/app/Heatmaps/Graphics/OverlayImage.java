package com.karlialag.app.Heatmaps.Graphics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

public class OverlayImage {

    public OverlayImage(String srcpath, String outputpath) throws IOException {
        File srcfile = new File(srcpath);
        BufferedImage img = ImageIO.read(srcfile);

        Image img2 = TransformingColours(img, new Color(0, 50, 77), new Color(200, 200, 255));
        BufferedImage resultImg = ImageToBufferedImage(img2, img.getWidth(), img.getHeight());

        File out = new File(outputpath);
        ImageIO.write(resultImg, "PNG", out);

    }

    private Image TransformingColours(BufferedImage img, Color color1, Color color2) {
        final int r1 = color1.getRed();
        final int g1 = color1.getGreen();
        final int b1 = color1.getBlue();
        final int r2 = color2.getRed();
        final int g2 = color2.getGreen();
        final int b2 = color2.getBlue();
        ImageFilter filter = new RGBImageFilter() {
            public final int filterRGB(int x, int y, int rgb) {
                int r = (rgb & 0xFF0000) >> 16;
                int g = (rgb & 0xFF00) >> 8;
                int b = rgb & 0xFF;
                if (r >= r1 && r <= r2 &&
                        g >= g1 && g <= g2 &&
                        b >= b1 && b <= b2) {

                    return rgb & 0xFFFFFF;
                }
                return rgb;
            }
        };

        ImageProducer ip = new FilteredImageSource(img.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }


    private BufferedImage ImageToBufferedImage(Image image, int width, int height) {
        BufferedImage dest = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return dest;
    }


}
