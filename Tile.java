//=======================================================================
/**
 * This is the Tile and MountainTile Classes of the Plague! game. It contains the code
 * that draws the tiles, updates tile sprites, and checks if tiles are in view.
 **/
//=======================================================================



//=======================================================================
// IMPORTS
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.MouseListener;//so we can listen to the mouse
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Color;

import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import javax.swing.JPanel;
import javax.swing.Timer;
//=======================================================================

//***** Tile subclass. Mountains start off & remain dead. They slow leader boid movement. *****//
class MountainTile extends Tile{
	
	public MountainTile(double x, double y, double l, World w){ 
		super(x, y, l, w);
		spriteLocation = "sprites/mtn.png";
		isMountain = true;
	}
    

}

//***** Tile class *****//
class Tile{
    Pair position; //position x and y in 2d array of world.map
    public static double height; //static so we can access it from boid without a tile instance
    public static double width;
    double life; //life to be reduced by locusts
    boolean alive; 
	World world;
	String spriteLocation; //image location
	boolean isMountain = false;

    public Tile(double x, double y, double l, World w){
		this.position = new Pair(x, y);
	
		height = 300.0;
		width = 300.0;
		life = l;
		spriteLocation = "sprites/live4.png";
		if(l>0){ 
			alive = true;
			updateSprite();
		}
		else{
			alive = false;
			updateSprite();
		}
		
		world = w;
		
	}
    //***** Tiles display different sprites as life level changes. Greener sprites are more alive, yellower ones more dead. Dead tiles are brown/yellow.*****//
	public void updateSprite(){
		if(alive && life >= 75){
			spriteLocation = "sprites/live4.png";
		}
		else if(alive && life >= 50){
			spriteLocation = "sprites/live3.png";
		}
		else if(alive && life >= 25){
			spriteLocation = "sprites/live2.png";
		}
		else if(alive && life >= 0){
			spriteLocation = "sprites/live1.png";
		}
		else if(alive && life <= 0){
			makeDead();
		}
	}
	//*** Method adapted from: http://zetcode.com/tutorials/javagamestutorial/movingsprites/
	 private Image loadImage() {
        
        ImageIcon ii = new ImageIcon(spriteLocation);
        Image image = ii.getImage(); 
        
        width = image.getWidth(null);
        height = image.getHeight(null);
		return image;
    }
	/////////////////////////////////////////////////////////////////////////////////////////
    
    //***** When tile life reaches zero, tile sprite changes to 1 of the 2 dead sprites & a new locust is spawned*****//
    public void makeDead(){
		life = 0;
		alive = false;
		Random r = new Random();
		if(r.nextDouble() > .5){
		    spriteLocation = "sprites/dead1.png";
		}
		else{ 
			spriteLocation = "sprites/dead2.png";
		}
		int boidNum = r.nextInt(3);
		for(int i = 0; i < boidNum; i++){ //spawns 0-2 boids at a random position near the king
		    world.boids.add(new Locust((r.nextDouble()-.5)*400 + world.kingBoid.position.x, (r.nextDouble()-.5)*400 + world.kingBoid.position.y, world));
		}
    }
    
    //***** Draws tiles that are visible, based on isInView(), relative to viewing window *****//
	public void draw(Graphics g, Pair displayCenter, Main mainInstance){ //draws the tile if isInView
		if(isInView(displayCenter)){
			Pair coords = new Pair(position.x*Tile.width, position.y*Tile.height);
			Pair displayCoords = World.toDisplayCoords(coords, displayCenter);
			Graphics2D g2d = (Graphics2D) g;
    
    		g2d.drawImage(loadImage(), (int)displayCoords.x, (int)displayCoords.y, mainInstance);
		}	
	}
    
    //***** Checks which tiles should be visible based on position of the leader (i.e., displayCenter)*****//
	public boolean isInView(Pair displayCenter){ //checks if the tile is in the region displayed
	    int offscreenTiles = 3; //number of offscreen tiles to display to avoid rendering jitters around edges
		double tileCoordX = (position.x * Tile.width);
	    double tileCoordY = (position.y * Tile.height); //these are coords of the top left corner 
		Pair displayXBounds = new Pair(displayCenter.x-((Main.WIDTH/2)+(offscreenTiles*Tile.width)), displayCenter.x+((Main.WIDTH/2)+(offscreenTiles*Tile.width))); //left and right bounds respectively
		Pair displayYBounds = new Pair(displayCenter.y-((Main.HEIGHT/2)+(offscreenTiles*Tile.height)), displayCenter.y+((Main.HEIGHT/2)+(offscreenTiles*Tile.height))); //top and bottom bounds respectively
		if(tileCoordX > displayXBounds.x  && tileCoordX < displayXBounds.y ){
			if(tileCoordY > displayYBounds.x && tileCoordY < displayYBounds.y ){
				return true;
			}
		}
		return false; //tiles over 3 tile spaces out of bounds are not drawn to preserve framerate
	} 

}
