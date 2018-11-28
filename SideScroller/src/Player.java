import java.awt.Image;
import java.awt.image.BufferedImage;

public class Player {
	int count;
	int x,y;
	BufferedImage[][] img;
	int vy;
	boolean movingRight,isJumping,hasPowerup;
	
	public Player(int a, int b,BufferedImage[][] i) {
		x=a;
		y=b;
		vy=0;
		img= i;
		count=0;
		movingRight=true;
		isJumping=false;
		hasPowerup=false;
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public void setY(int num) {
		y=num;
	}
	public int getVY() {
		return vy;
	}
	public void setVY(int num) {
		vy=num;
	}
	public void moveY() {
		y+=vy/3;
	}
	public BufferedImage getImage() {
		
		int greenShift=0;
		if(hasPowerup) {
			greenShift=2;
		}
			
		
		if(movingRight&&isJumping)
			return img[9][greenShift];
		else if(isJumping)
			return img[0][greenShift];	
		if(movingRight) {
			return img[(count+50)/10][1+greenShift];
			
		}
		else
			return img[(50-count)/10][1+greenShift];
	}
	
	public void moveRight() {
		x+=3;
		
		if(hasPowerup&&isJumping)
			x+=3;
		
		movingRight=true;	
		count++;
		
		if(count==20)
			count=0;
		
	

		
	}
	public void moveLeft() {
		x-=3;
		
		if(hasPowerup&&isJumping)
			x-=3;
		
		movingRight=false;
		count++;
		if(count==20)
			count=0;
		
	}
	
	public int getWidth() {
		return 16;
	}

	public int getHeight() {
		return 26;
	}
	
	public void setJumping(boolean b) {
		isJumping=b;
	}
	
	public void setPowerup(boolean b) {
		hasPowerup=b;
	}
	
	public boolean hasPowerup() {
		return hasPowerup;
	}
	
}
