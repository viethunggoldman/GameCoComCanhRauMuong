package view;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class BackgroundPanel extends JPanel{
	private Image backgroundImage;
	
	

	  // Constructor chính dùng để set ảnh nền
    public BackgroundPanel(String imagePath) {
        try {
            backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
        } catch (Exception e) {
            System.err.println("Không tìm thấy ảnh nền: " + imagePath);
        }
    }

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		 g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
	}
   
}
