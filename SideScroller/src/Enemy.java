import java.awt.Image;
import java.awt.image.BufferedImage;

public class Enemy {
	int count;
	int x,y;
	BufferedImage[] img;
	int vy;
	boolean movingRight,isJumping;
	
	public Enemy(int a, int b,BufferedImage[] i,boolean mr) {
		x=a;
		y=b;
		vy=0;		
		img= i;
		count=0;
		movingRight=mr;
		isJumping=false;
		
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	public BufferedImage getImage() {
		if(count<10)
			return img[0];
		else
			return img[1];
		
	}
	
	public void move(){
		
		if(movingRight)
			x+=1;
		else
			x-=1;
		
		count++;
		
		if(count==20)
			count=0;
	}
	
	public int getWidth() {
		return 16;
	}
	public int getHeight() {
		return 16;
	}
	public void switchMovingRight() {
		movingRight=!movingRight;
	}
	
}
