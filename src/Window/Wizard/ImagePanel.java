package Window.Wizard;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * An empty JPanel containing a custom image
 */
public class ImagePanel extends JPanel  {
    private BufferedImage image;
    private Dimension imgDimension = new Dimension(300,100);
    public ImagePanel(BufferedImage image){
        this.image = image;
        //Set layout to fill available space
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        if(image != null) {
            //Adding Empty space to fill out image
            imgDimension = new Dimension(Math.min(image.getWidth(),imgDimension.width), Math.min(image.getHeight(),imgDimension.height));
        }
        add(Box.createRigidArea(imgDimension),BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(image,0,0,imgDimension.width,imgDimension.height,this);
    }
}