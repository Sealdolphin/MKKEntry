package Window;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * An empty JPanel containing a custom image
 */
public class ImagePanel extends JPanel  {
    private BufferedImage image = null;
    private Dimension imgDimension = new Dimension(300,100);
    private String imgPath;

    public ImagePanel(String imagePath){
        imgPath = imagePath;
        if(imagePath != null)
            try {
                image = ImageIO.read(new File(imagePath));
            } catch (IOException e) {
                //Some warning
                JOptionPane.showMessageDialog(new JFrame(),"Nem találom a vonalkódképet a kedvezményhez\n" +
                        imagePath + ":\n" + e.getMessage(),"Hiba",JOptionPane.ERROR_MESSAGE);
            }
        //Set layout to fill available space
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        if(validatePicture()) {
            //Adding Empty space to fill out image
            imgDimension = new Dimension(Math.min(image.getWidth(),imgDimension.width), Math.min(image.getHeight(),imgDimension.height));
        }
        //Create border for picture
        add(Box.createRigidArea(imgDimension),BorderLayout.CENTER);
        //Set Maximum Height to fit side panel
        setMaximumSize(new Dimension(getMaximumSize().width,imgDimension.height));
    }

    public boolean validatePicture(){
        return image != null;
    }

    public String getPath(){
        return imgPath;
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(image,0,0,imgDimension.width,imgDimension.height,this);
    }
}