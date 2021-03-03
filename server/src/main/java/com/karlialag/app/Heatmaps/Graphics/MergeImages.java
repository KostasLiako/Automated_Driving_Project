package com.karlialag.app.Heatmaps.Graphics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MergeImages {

    private final BufferedImage mergedimage;

    public MergeImages(String gridImage, String overlay, String resultPhoto) throws IOException {


        BufferedImage im = ImageIO.read(new File(overlay));
        BufferedImage im2 = ImageIO.read(new File(gridImage));
        Graphics2D g = im.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        g.drawImage(im2, (im.getWidth() - im2.getWidth()) / 2, (im.getHeight() - im2.getHeight()) / 2, null);
        g.dispose();
        ImageIO.write(im, "png", new File(resultPhoto));
        mergedimage = im;

    }


}


