package view;

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
    private Dimension imgDimension = new Dimension(420,140);
    private String imgPath;

    public ImagePanel(String imagePath){
        imgPath = imagePath;
        if(imagePath != null)
            changePicture(imagePath);
        setImageLayout();
    }

    private void setImageLayout(){
        //Set layout to fill available space
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        if(validatePicture()) {
            //Adding Empty space to fill out image
            imgDimension = new Dimension(imgDimension.width, imgDimension.height);
        }
        //Create border for picture
        add(Box.createRigidArea(imgDimension),BorderLayout.CENTER);
        //Set Maximum Height to fit side panel
        setMaximumSize(new Dimension(getMaximumSize().width,imgDimension.height));
    }

    public void changePicture(String newPath){
        try {
            image = ImageIO.read(new File(newPath));
            imgPath = newPath;
            setImageLayout();
        } catch (IOException e) {
            //Some warning
            JOptionPane.showMessageDialog(new JFrame(),"Nem találom a képet a panelhez\n" +
                    newPath + ":\n" + e.getMessage(),"Hiba",JOptionPane.ERROR_MESSAGE);
        }

    }

    private boolean validatePicture(){
        return image != null;
    }

    public String getPath(){
        return imgPath;
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(image,0,0,imgDimension.width,imgDimension.height,
                0,0,image.getWidth(),image.getHeight(),
                this);
    }
}