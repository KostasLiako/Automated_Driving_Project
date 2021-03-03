package com.karlialag.app.Heatmaps.Graphics;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DrawGrid extends JPanel {

    BufferedImage img;

    public DrawGrid(String path) throws IOException {
        img = ImageIO.read(new File(path));
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();
        if (img != null) {
            graphics.drawImage(img, 0, 0, this);//Draws the currently image
            graphics.dispose();

            Graphics2D graphics2 = img.createGraphics();
            int cellHeight = (int) (result.getHeight() / 4.0);   // Set the cell Height
            int cellWidth = (int) (result.getWidth() / 10.0);    // Set the cell Width
            for (int y = 0; y < result.getHeight(); y += cellHeight) {     // Draws the lines to divide the image
                for (int x = 0; x < result.getWidth(); x += cellWidth) {
                    graphics2.setColor(Color.BLACK);
                    graphics2.draw(new Rectangle2D.Double(x, y, result.getWidth(), result.getHeight()));

                }
            }
            graphics2.dispose();
            ImageIO.write(img, "png", new File("output/grid.png"));  //Save the output image

        }
    }
}




