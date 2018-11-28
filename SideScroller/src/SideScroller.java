import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.awt.color.*;

import javax.imageio.ImageIO;
import javax.swing.*;

public class SideScroller extends JPanel implements Runnable,KeyListener{

	BufferedImage GB1,GB2,GB3,GB4,GC1,GC2,tileset,gameOverImage,powerupImage,emptyblockImage,starImage,winImage;
	BufferedImage[][] tiles;
	BufferedImage[] enemyImg;
	private int b2x,b3x,b4x,cx,
				px,py,count;
	private boolean rightPressed,leftPressed,upPressed,downPressed;
	boolean lose_ending,win_ending;
	private JFrame frame;
	private Thread t;
	private int scl;
	ArrayList<Block> blocks;
	ArrayList<Enemy> enemies;
	Block powerupBlock,endBlock;
	
	Player player;
	int levelWidth;
	static String BELOW = "BELOW";
	static String ABOVE = "BELOW";
	static String RIGHT = "RIGHT";
	static String LEFT = "LEFT";
	
	
	public SideScroller(){
		scl=3;
		frame = new JFrame();
		blocks = new ArrayList<Block>();
		enemies = new ArrayList<Enemy>();
		b2x=0;
		b3x=0;
		b4x=0;
		cx=0;
		px=500;
		py=400;
		count=0;
		tiles = new BufferedImage[11][8];
		
		
		try {
			
			GB1 = ImageIO.read(this.getClass().getResource("/GrassLand_Background_1.png"));
			GB2 = ImageIO.read(this.getClass().getResource("/GrassLand_Background_2.png"));
			GB3 = ImageIO.read(this.getClass().getResource("/GrassLand_Background_3.png"));
			GB4 = ImageIO.read(this.getClass().getResource("/GrassLand_Background_4.png"));
			GC1 = ImageIO.read(this.getClass().getResource("/GrassLand_Cloud_1.png"));			
			GC2 = ImageIO.read(this.getClass().getResource("/GrassLand_Cloud_2.png"));		
			tileset = ImageIO.read(this.getClass().getResource("/GrassLand_Terrain_Tileset.png"));
			gameOverImage = ImageIO.read(this.getClass().getResource("/game-over.png"));
			BufferedImage sheet = (BufferedImage) ImageIO.read(this.getClass().getResource("/Toad_SpriteSheet.png"));
			BufferedImage greenSheet = (BufferedImage) ImageIO.read(this.getClass().getResource("/Toad_SpriteSheet_Green.png"));
			BufferedImage[][] sprites = new BufferedImage[10][4];
			enemyImg = new BufferedImage[2];
			enemyImg[0]=ImageIO.read(this.getClass().getResource("/goomba1.png"));
			enemyImg[1]=ImageIO.read(this.getClass().getResource("/goomba2.png"));
			powerupImage=ImageIO.read(this.getClass().getResource("/powerup.png"));
			emptyblockImage=ImageIO.read(this.getClass().getResource("/empty_powerup.png"));
			starImage=ImageIO.read(this.getClass().getResource("/star.png"));
			winImage=ImageIO.read(this.getClass().getResource("/the_end.png"));
			
			//enemies.add(new Enemy(700,480,enemyImg,false));
			//enemies.add(new Enemy(1000,480,enemyImg,false));
			//enemies.add(new Enemy(1250,480,enemyImg,false));
			
			
			for(int x=0;x<10;x++) {
						sprites[x][1]=sheet.getSubimage(1+x*40,42,16,26);
						sprites[x][3]=greenSheet.getSubimage(1+x*40,42,16,26);
			}

			sprites[0][0]=sheet.getSubimage(0, 0, 18, 29);
			sprites[9][0]=sheet.getSubimage(360, 0, 18, 29);
			sprites[0][2]=greenSheet.getSubimage(0, 0, 18, 29);
			sprites[9][2]=greenSheet.getSubimage(360, 0, 18, 29);
			
			player=new Player(200,449,sprites);
			//player=new Player(2700,449,sprites);
			for(int x=0;x<11;x++)
				for(int y=0;y<8;y++)
					tiles[x][y]=tileset.getSubimage(x*16,y*16,16,16);
					
			
		}catch(Exception e) {
			System.out.println(e);
		}	
		
		try
		{
			BufferedReader input = new BufferedReader(new FileReader(new File("layout.txt")));
			

			String text,output="";
			int y=0;
			BufferedImage img=tiles[6][6];
			while( (text=input.readLine())!= null)
			{
				levelWidth = text.length()*16*scl;
				
				for(int x=0;x<text.length();x++) {
					if(text.charAt(x)!='0') {
						
						switch(text.charAt(x)) {
						
						case '1': img = tiles[0][0];
							break;
						case '2': img = tiles[1][0];
							break;
						case '3': img = tiles[2][0];
							break;
						case '4': img = tiles[0][1];
							break;
						case '5': img = tiles[1][1];
							break;
						case '6': img = tiles[2][1];
							break;
						case '7': img = tiles[0][2];
							break;
						case '8': img = tiles[1][2];
							break;
						case '9': img = tiles[2][2];
							break;
						case 'Q': img = tiles[0][5];
							break;
						case 'W': img = tiles[1][5];
							break;	
						case 'A': img = tiles[4][7];
							break;
						case 'B':img = tiles[5][7];
							break;
						case 'C':img = tiles[6][7];
							break;
						case 'E': enemies.add(new Enemy(x*16*scl,y*16*scl,enemyImg,false));
							break;
						case 'P': img = powerupImage;
							powerupBlock = new Block(x*16*scl,y*16*scl,img);
							break;
						case 'L': img = emptyblockImage;
							break;
						case 'S': img = starImage;
							endBlock = new Block(x*16*scl,y*16*scl,img);
							break;
						}
						
						if(text.charAt(x)!='E')
							blocks.add(new Block(x*16*scl,y*16*scl,img));
					}
				}
				y++;
			}


		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		lose_ending=false;
		win_ending=false;
		frame.add(this);
		frame.addKeyListener(this);
		frame.setSize(368*3,208*3);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		t=new Thread(this);
		t.start();
		
	}

	public static void main(String args[]) {
		SideScroller app = new SideScroller();
	}

	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
//Draw Parallax Background
		g2d.drawImage(GB1,0,0,GB1.getWidth()*scl,GB1.getHeight()*scl,null);
		
		g2d.drawImage(GC1,cx,20,GC1.getWidth()*scl,GC1.getHeight()*scl,null);
		g2d.drawImage(GC1,cx+250,70,GC1.getWidth()*scl,GC1.getHeight()*scl,null);
		g2d.drawImage(GC1,cx+700,100,GC1.getWidth()*scl,GC1.getHeight()*scl,null);		
		g2d.drawImage(GC2,cx+500,0,GC2.getWidth()*scl,GC2.getHeight()*scl,null);
		g2d.drawImage(GC2,cx+820,30,GC2.getWidth()*scl,GC2.getHeight()*scl,null);

		g2d.drawImage(GC1,cx+1128,20,GC1.getWidth()*scl,GC1.getHeight()*scl,null);
		g2d.drawImage(GC1,cx+250+1128,70,GC1.getWidth()*scl,GC1.getHeight()*scl,null);
		g2d.drawImage(GC1,cx+700+1128,100,GC1.getWidth()*scl,GC1.getHeight()*scl,null);		
		g2d.drawImage(GC2,cx+500+1128,0,GC2.getWidth()*scl,GC2.getHeight()*scl,null);
		g2d.drawImage(GC2,cx+820+1128,30,GC2.getWidth()*scl,GC2.getHeight()*scl,null);
		
		
		g2d.drawImage(GB2,b2x,0,GB2.getWidth()*scl,GB2.getHeight()*scl,null);
		g2d.drawImage(GB2,b2x+GB2.getWidth()*scl,0,GB2.getWidth()*scl,GB2.getHeight()*scl,null);
		g2d.drawImage(GB2,b2x+2*GB2.getWidth()*scl,0,GB2.getWidth()*scl,GB2.getHeight()*scl,null);
		
		g2d.drawImage(GB3,b3x,0,GB3.getWidth()*scl,GB3.getHeight()*scl,null);
		g2d.drawImage(GB3,b3x+GB3.getWidth()*scl,0,GB3.getWidth()*scl,GB3.getHeight()*scl,null);
		g2d.drawImage(GB3,b3x+2*GB3.getWidth()*scl,0,GB3.getWidth()*scl,GB3.getHeight()*scl,null);
		g2d.drawImage(GB3,b3x+3*GB3.getWidth()*scl,0,GB3.getWidth()*scl,GB3.getHeight()*scl,null);

		g2d.drawImage(GB4,b4x,0,GB4.getWidth()*scl,GB4.getHeight()*scl,null);
		g2d.drawImage(GB4,b4x+GB4.getWidth()*scl,0,GB4.getWidth()*scl,GB4.getHeight()*scl,null);
		g2d.drawImage(GB4,b4x+2*GB4.getWidth()*scl,0,GB4.getWidth()*scl,GB4.getHeight()*scl,null);
		
//Draw Character & Blocks
		if((player.getX()<levelWidth-frame.getWidth()/2)&&(player.getX()>3+frame.getWidth()/2)) {
		
			for(Block b : blocks) {
				g2d.drawImage(b.getImage(),b.getX()-player.getX()+frame.getWidth()/2,b.getY(),16*scl,16*scl,null);
				//g2d.fillRect(b.getX()-player.getX()+frame.getWidth()/2,b.getY(),16*scl,16*scl);
				//g2d.setColor(Color.RED);
				//g2d.drawOval();
			}
			
			for(Enemy e : enemies) {
				//g2d.drawRect(e.getX()-player.getX()+frame.getWidth()/2, e.getY(), 48, 48);
				g2d.drawImage(e.getImage(),e.getX()-player.getX()+frame.getWidth()/2,e.getY(),16*scl,16*scl,null);
			}
				
			g2d.drawImage(player.getImage(),frame.getWidth()/2,player.getY(),player.getImage().getWidth()*scl,player.getImage().getHeight()*scl,null);

		//Draw Character & Blocks: Boundary Condition: Left	
		}else if(player.getX()<levelWidth-frame.getWidth()/2){
			
			for(Block b : blocks) {
				g2d.drawImage(b.getImage(),b.getX(),b.getY(),16*scl,16*scl,null);
			}
			
			for(Enemy e : enemies) {
				g2d.drawImage(e.getImage(),e.getX(),e.getY(),16*scl,16*scl,null);
			}
		
			g2d.drawImage(player.getImage(),player.getX(),player.getY(),player.getImage().getWidth()*scl,player.getImage().getHeight()*scl,null);		
		//Draw Character & Blocks: Boundary Condition: Right
		}else {
			
			for(Block b : blocks) {
				g2d.drawImage(b.getImage(),b.getX()-levelWidth+frame.getWidth(),b.getY(),16*scl,16*scl,null);
			}
			
			for(Enemy e : enemies) {
				g2d.drawImage(e.getImage(),e.getX()-levelWidth+frame.getWidth(),e.getY(),16*scl,16*scl,null);
			}
		
			g2d.drawImage(player.getImage(),frame.getWidth()-(levelWidth-player.getX()),player.getY(),player.getImage().getWidth()*scl,player.getImage().getHeight()*scl,null);			
		}
		
		
		if(lose_ending) {
			
			g2d.drawImage(gameOverImage,400,200,304,224,null);
			
		}

		if(win_ending) {
			
			g2d.drawImage(winImage,250,200,604,224,null);
			
		}
		
	}
	
	public void run() {
		
		while(true) {
			
		if(!lose_ending&&!win_ending) {
			
//PLAYER			
			if((upPressed)&&(isBlock("BELOW"))) {
				
					player.setVY(-38);
				
				player.setJumping(true);
			}else if(!isBlock("BELOW")) {
				player.setVY(player.getVY()+1);
			}else {
				if((player.getY()+player.getHeight()*3)%48>1)
					player.setY(player.getY()-(player.getY()+player.getHeight()*3)%48);
				
				player.setVY(0);
				player.setJumping(false);

			}
//ENEMIES			
			for(Enemy e : enemies) {
				
				Rectangle eRect = new Rectangle(e.getX(),e.getY(),e.getWidth()*scl,e.getHeight()*scl);
				if(!isBlock("BELOW",eRect)||isBlock("RIGHT",eRect)||isBlock("LEFT",eRect))
					e.switchMovingRight();
				
				
					e.move();
				
			}
			
			if(isBlock("ABOVE")&&player.getVY()<0)
				player.setVY(0);
			player.moveY();
			
			if(rightPressed&&!isBlock(RIGHT)) {
			
			if((player.getX()<levelWidth-frame.getWidth()/2)&&(player.getX()>3+frame.getWidth()/2)) {
				count++;
				if(count>=2000)
					count-=20000;				

					b4x-=3;
					b3x-=2;		
					b2x-=1;
				if(count%3==0)
					cx-=1;
			}

				if(player.getX()<levelWidth-3-player.getWidth()*scl)
					player.moveRight();
				
			}
			if(leftPressed&&!isBlock(LEFT)) {

			if((player.getX()<levelWidth-frame.getWidth()/2)&&(player.getX()>3+frame.getWidth()/2)) {
				count++;
				if(count>=2000)
					count-=20000;	
				
					b4x+=3;
					b3x+=2;		
					b2x+=1;				
				if(count%3==0)
					cx+=1;	
			}
			
				if(player.getX()>3)
					player.moveLeft();
			}
			
			if(b2x<GB2.getWidth()*scl*-1)
				b2x+=GB2.getWidth()*scl;
			if(b3x<GB3.getWidth()*scl*-1)
				b3x+=GB3.getWidth()*scl;
			if(b4x<GB4.getWidth()*scl*-1)
				b4x+=GB4.getWidth()*scl;
			if(cx<-1128)
				cx+=1128;

			if(b2x>0)
				b2x-=GB2.getWidth()*scl;
			if(b3x>0)
				b3x-=GB3.getWidth()*scl;
			if(b4x>0)
				b4x-=GB4.getWidth()*scl;
			if(cx>0)
				cx-=1128;

//Has powerUp
			
			Rectangle pRect = new Rectangle(player.getX(),player.getY(),player.getWidth()*scl,player.getHeight()*scl);
			Rectangle bRect = new Rectangle(powerupBlock.getX()-4,powerupBlock.getY()-4,16*scl+8,16*scl+8);
			
			if(bRect.intersects(pRect))
				player.setPowerup(true);

//Jumping on Enemies			
			for(int a=0;a<enemies.size();a++) {
				
				Enemy e = enemies.get(a);
				
				Rectangle eRect = new Rectangle(e.getX(),e.getY(),e.getWidth()*scl,e.getHeight()*scl);
				pRect = new Rectangle(player.getX(),player.getY(),player.getWidth()*scl,player.getHeight()*scl);
				if(eRect.contains(player.getX()+5,player.getY()+player.getHeight()*scl)
					||eRect.contains(player.getX()+player.getWidth()*scl-5,player.getY()+player.getHeight()*scl)) {
						enemies.remove(a);
						a--;
						player.setVY(-24);
				}
			}
			
		
			
			
//Killed by Enemies			
			for(int a=0;a<enemies.size();a++) {
				
				Enemy e = enemies.get(a);
				
				Rectangle eRect = new Rectangle(e.getX(),e.getY(),e.getWidth()*scl,e.getHeight()*scl);
				pRect = new Rectangle(player.getX()+15,player.getY(),player.getWidth()*scl-15,player.getHeight()*scl);
				if(eRect.contains(player.getX(),player.getY()+player.getHeight()*scl-5)
					||eRect.contains(player.getX()+player.getWidth()*scl,player.getY()+player.getHeight()*scl-5)) {
						lose_ending=true;
				}
			}
			
//Killed by Falling
			
			if(player.getY()>frame.getHeight())
				lose_ending=true;

//WINNING
			bRect = new Rectangle(endBlock.getX()-4,endBlock.getY()-4,16*scl+8,16*scl+8);
			pRect = new Rectangle(player.getX(),player.getY(),player.getWidth()*scl,player.getHeight()*scl);
			if(pRect.intersects(bRect)) {
				win_ending=true;
				player.setJumping(true);
				player.setPowerup(false);
			}
				

		}

			//System.out.println(b2x);
			repaint();
			
			try {
				t.sleep(10);
			}catch(Exception e) {
				
			}
			
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_W)
			upPressed=true;
		if(e.getKeyCode()==KeyEvent.VK_A)
			leftPressed=true;
		if(e.getKeyCode()==KeyEvent.VK_S)
			downPressed=true;
		if(e.getKeyCode()==KeyEvent.VK_D)
			rightPressed=true;
	}

	@Override
	public void keyReleased(KeyEvent e) {

		if(e.getKeyCode()==KeyEvent.VK_W)
			upPressed=false;
		if(e.getKeyCode()==KeyEvent.VK_A)
			leftPressed=false;
		if(e.getKeyCode()==KeyEvent.VK_S)
			downPressed=false;
		if(e.getKeyCode()==KeyEvent.VK_D)
			rightPressed=false;
		
	}
	
public boolean isBlock(String str){
		int p1x,p1y,p2x,p2y;
		p1x=0;
		p1y=0;
		p2x=0;
		p2y=0;
	if(str.equals("BELOW")) {
		p1x=8;
		p1y=player.getHeight()*scl;
		p2x=player.getWidth()*scl-8;
		p2y=player.getHeight()*scl;
	}else if(str.equals("ABOVE")) {
		p1x=2;
		p1y=0;
		p2x=player.getWidth()*scl-2;
		p2y=0;
	}else if(str.equals("RIGHT")) {
		p1x=player.getWidth()*scl;
		p1y=0;
		p2x=player.getWidth()*scl;
		p2y=player.getHeight()*scl-2;
	}else if(str.equals("LEFT")) {
		p1x=0;
		p1y=0;
		p2x=0;
		p2y=player.getHeight()*scl-2;
	}
		Rectangle bRect;
		for(Block b: blocks) {
			bRect = new Rectangle(b.getX(),b.getY(),16*scl,16*scl);
			
			
			if(bRect.contains(player.getX()+p1x,player.getY()+p1y)||
					bRect.contains(player.getX()+p2x,player.getY()+p2y))
						return true;
			
		}
		
		return false;

	}

public boolean isBlock(String str, Rectangle r) {
	
	int p1x,p1y,p2x,p2y;
	
	p1x=0;
	p2x=0;
	p1y=0;
	p2y=0;
	
	if(str=="BELOW") {
		p1x=(int)r.getX()+8;
		p1y=(int)(r.getY()+r.getHeight());
		p2x=(int)(r.getX()+r.getWidth());
		p2y=p1y;
	}
	else if(str=="RIGHT") {
		p1x=(int)(r.getX()+r.getWidth());
		p1y=(int)r.getY();
		p2x=p1x;
		p2y=(int)(r.getY()+r.getHeight())-2;
	}
	else if(str=="LEFT"){
		p1x=(int)r.getX();
		p1y=(int)r.getY();
		p2x=p1x;
		p2y=(int)(r.getY()+r.getHeight())-2;
	}
	int count=0;
	Rectangle bRect;
	for(Block b: blocks) {
		bRect = new Rectangle(b.getX(),b.getY(),16*scl,16*scl);
		
		if(str=="BELOW") {
			
			if(bRect.contains(p1x,p1y))
				count++;
			if(bRect.contains(p2x,p2y))
				count++;		
			
		}else {
			
			if(bRect.contains(p1x,p1y)||
					bRect.contains(p2x,p2y))
						return true;
			
		}
		

		
	}
	if(count>=2)
		return true;
	return false;
}
	
}
