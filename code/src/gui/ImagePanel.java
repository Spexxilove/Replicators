package gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	public ImagePanel(String imagePath){
		try{
			image = ImageIO.read(ImagePanel.class
					.getResourceAsStream(imagePath));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(image, 0, 0,this.getWidth(),this.getHeight(), null);
	}
}
