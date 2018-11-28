import java.awt.image.BufferedImage;

public class Block {
//All blocks are of dimension 16x16
int x,y;
BufferedImage img;

	public Block(int a,int b,BufferedImage i) {
		
		x=a;
		y=b;
		img=i;
		
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public BufferedImage getImage() {
		return img;
	}
	public void setImage(BufferedImage b) {
		img=b;
	}
	
	
}
