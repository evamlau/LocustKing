//=======================================================================
/**
 * This is the Boid Class of the Plague! game. 
 * Within the game, we use the subclasses kingBoid and Locust. There is no 
 * instance of the parent class, Boid.
 * Locusts exhibit flocking behavior inspired by the Boids model. They
 * also follow the kingBoid.
 * The kingBoid does not exhibit flocking behavior. It moves by following
 * the mouse position.
 * @authors Eva Lau, Ethan Lebowitz, Karina Thanawala
 *
 * Code inspiration/sources: 
 * Boids 101:
 ** https://www.red3d.com/cwr/boids/
 *
 * Flocking inspiration (not in Java):
 ** https://gamedevelopment.tutsplus.com/tutorials/3-simple-rules-of-flocking-behaviors-alignment-cohesion-and-separation--gamedev-3444 
 ** https://processing.org/examples/flocking.html
 *
 * Paper on how to make boids more computationally affordable:
 * (Implemented a very approximate version that did work) 
 ** http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.595.442&rep=rep1&type=pdf
 *
 * Leader/Follower inspiration (not in Java):
 ** https://gamedevelopment.tutsplus.com/tutorials/understanding-steering-behaviors-leader-following--gamedev-10810
 *
 * References/inspiration for rotation of sprite in KingBoid.draw():
 ** https://stackoverflow.com/questions/20275424/rotating-image-with-affinetransform
 ** http://beginwithjava.blogspot.com/2009/02/rotating-image-with-java.html
 ** https://stackoverflow.com/questions/8639567/java-rotating-images
 ** https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#atan2-double-double-
 ** https://docs.oracle.com/javase/7/docs/api/java/awt/geom/AffineTransform.html
 ** https://gamedev.stackexchange.com/questions/62196/rotate-image-around-a-specified-origin
 *
 * Format inspired by Professor Kaplan's CaesarCipher.java code format
 **/
//=======================================================================



//=======================================================================
// IMPORTS
import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.MouseListener;//so we can listen to the mouse
import java.awt.event.MouseEvent;

import java.lang.Math;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;

import java.awt.geom.AffineTransform;
//=======================================================================



//=======================================================================
/**
 * Locust subclass
 *@param life life of the swarm. Changes number of locusts in the swarm. Affected by alive/dead status of tile occupied. 
 *@param decayRate increment at which life decreases over time
 *@param healRate increment at which life increases on live tiles
 **/
class Locust extends Boid{
	
	static double life = 100; 
	double decayRate = .1; //speed at which life is lost 
	double healRate = .9;
    //=======================================================================



    //=======================================================================
    /**
     *Locust constructor
     * same as parent class constructor, except it increases the numBoids counter
     * in the world when a locust is created. Resets life to 100 after it drops to 0.
     **/
	public Locust(double X, double Y, World w){
		super(X, Y, w);
		world.numBoids++;
		if(life <= 0){life = 100;}
	}// subclass Locust constructor
    //=======================================================================



    //=======================================================================
    /**
     * update() method calls flock() to govern locust movement based on 
     * boid model rules.
     * Changes position based on velocity.
     * Increases locust life and decreases tile life if tile boid is on is alive.
     * Decreases boid life if tile boid is on is dead.
     **/
    
    public void	update(double time, Tile[][] map, ArrayList<Locust> boids, Boid kingBoid){
	flock(boids, kingBoid);
	position = position.add(velocity.times(time));
	Tile tile = getTile(map);
		reduceTileLife(tile);
        updateBoidLife(tile);
    }// update ()
     //=======================================================================



    //=======================================================================
    /**
     * updateBoidLife() method is used to change boid life based on the tile a boid is on.
     * If the tile is alive, boid life increases based on healRate.
     * If the tile is dead boid life decreases based on decayRate.
     * If life drops to zero, die() is called and a locust drops out of the swarm.
     **/
	public void updateBoidLife(Tile tile){
		if(tile.alive && life < 100){
			life += this.healRate;
		}
		else if((!tile.alive) && life > 0){
			life -= this.decayRate;
		}
		if(life <= 0){
			die();
		}
	}// updateBoidLife ()
	//=======================================================================



        //=======================================================================
        /**
	 * die() method causes the numBoids counter to decrease by 1, removes the last 
	 * locust to be added to the boids arraylist, and then resets life to 100 if 
	 * there are any follower locusts remaining.
        **/
	public void die(){
		world.numBoids --;
		world.boids.remove(world.numBoids);
		if(world.numBoids > 0){
			life = 100;
		}
	}// die ()
     //=======================================================================


     //=======================================================================
    /**
     * neighborhood() method implements 3 basic boid rules, based on the "neighborhood"
     * a given boid is in. Neighborhood is defined by other locusts that are both near to 
     * and in front of a given boid.
     * Outputs one pair each time it is called, pair represents new velocity of myBoid based
     * on boid rules. 
     *
     * Boid rules: 1. Alignment, 2. Cohesion, 3. Separation
     * Inspired by pseudo-code here: 
     * https://gamedevelopment.tutsplus.com/tutorials/3-simple-rules-of-flocking-behaviors-alignment-cohesion-and-separation--gamedev-3444
     * Modified so neighborhood only includes other boids a boid "sees" based on this paper:
     * http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.595.442&rep=rep1&type=pdf
     **/
    
    public Pair neighborhood(Boid myBoid, ArrayList<Locust> boids){

	Pair vel = myBoid.velocity.normalizeVector();//velocity copy (yea, not a real copy)
	Pair align = new Pair(0,0);//alignment
	Pair centroid = new Pair(0,0);//cohesion (aka head towards neighborhood centroid, thus the var name)
	Pair separate = new Pair(0,0);//separation
	
	int neighborCount  = 0;//used for normalization
	Boid otherBoid = new Boid(0,0, world);//placeholder boid. assignement changes as it gets iterated over

	for(int i = 0; i < boids.size(); i++){
	    otherBoid = boids.get(i);
	    
	    if(otherBoid != myBoid){
		if(distanceFrom(myBoid.position, otherBoid.position) < 25 && distanceFrom(myBoid.position.add(myBoid.velocity.normalizeVector().times(25)), otherBoid.position) < 25){
		    //boids "see" boids that fall within 25 unit radius of them & that are in front of them 
		    neighborCount++;
	        
		    
		    align.x += otherBoid.velocity.x;
		    align.y += otherBoid.velocity.y;
		    
		    centroid.x += otherBoid.position.x;
		    centroid.y += otherBoid.position.y;

		    separate.x += otherBoid.position.x - myBoid.position.x;
		    separate.y += otherBoid.position.y - myBoid.position.y;
        
			
		    
		}
	    }
	}
	if(neighborCount == 0){
	    return vel;//no change to velocity if boid has no neighbors
	}

	//normalize all pairs by neighborCount, then use normalizeVector() to convert to unit vectors
	align.x /= neighborCount;
	align.y /= neighborCount;
	align = align.normalizeVector();//normalization prevents speed from radically increasing among other things
	
	
	centroid.x  /= neighborCount;
	centroid.y /= neighborCount;
	Pair towardsCentroid = new Pair(centroid.x - myBoid.position.x, centroid.y - myBoid.position.y);//direction of centroid relative to boid
	towardsCentroid = towardsCentroid.normalizeVector();

	separate.x = -1*(separate.x/neighborCount);
	separate.y = -1*(separate.y/neighborCount);
	separate = separate.normalizeVector();

	vel.x += ((align.x)*0 + (towardsCentroid.x)*0 + (separate.x)*0.7); //modify velocity based on weighted calculations
	vel.y += ((align.y)*0 + (towardsCentroid.y)*0 + (separate.y)*0.7); //different weights = different boid behavior
	
	vel = vel.normalizeVector().times(50); //multiply or divide this to slow down or speed up
	return vel;
    }//neighborhoor ()
    //=======================================================================

    
    //=======================================================================
    /**
     * flock() method changes velocity of every locust in an arraylist of locusts
     *based on neighborhood () and follow () output.
     **/
    public void flock(ArrayList<Locust> boids, Boid kingBoid){
	    //for(int i = 0; i < boids.size(); i++){
		this.velocity = neighborhood(this, boids).add(follow(kingBoid)).times(4);
	    //}
    }//flock ()
    //=======================================================================

    
    //=======================================================================
    /**
     * follow () method outputs pair corresponding to a locust's new velocity
     * when it follows the position of the boid instance passed to the method.
     * Locusts follow a point slightly behind the leader boid, for increased visibility
     * on screen. and also out of respect, probably.
     **/
    public Pair follow(Boid kingBoid){
		
		Pair towardsKing = new Pair(kingBoid.position.x - position.x, kingBoid.position.y - position.y); 
		towardsKing = towardsKing.normalizeVector();
		double angleTowardsKing = Math.atan(towardsKing.y/towardsKing.x); //following if/else statements are to convert to positive angle from the x axis (0-2pi)
		if(towardsKing.x < 0.0){angleTowardsKing = (Math.PI) + angleTowardsKing;} //if in second or third quadrant
		else if(towardsKing.x > 0.0 && towardsKing.y < 0.0){angleTowardsKing = (Math.PI * 2) + angleTowardsKing;} //if in fourth quadrant
		double currentAngle = Math.atan(this.velocity.y/this.velocity.x);
		if(this.velocity.x < 0.0){currentAngle = (Math.PI) + currentAngle;} //if in second or third quadrant
		else if(this.velocity.x > 0.0 && this.velocity.y < 0.0){currentAngle = (Math.PI * 2) + currentAngle;} //if in fourth quadrant
		double angleChange = (Math.PI/180)*5; //5 degrees in radians
		if( currentAngle > angleTowardsKing && Math.abs(currentAngle - angleTowardsKing) > Math.PI ){ // there are four possible cases used to determine if toward is clockwise or counter clockwise
			angleChange = angleChange;
		}
		else if( currentAngle > angleTowardsKing && Math.abs(currentAngle - angleTowardsKing) < Math.PI ){ 
			angleChange = -angleChange; 
		}
		else if( currentAngle < angleTowardsKing && Math.abs(currentAngle - angleTowardsKing) > Math.PI ){ 
			angleChange = -angleChange; 
		}
		else if( currentAngle < angleTowardsKing && Math.abs(currentAngle - angleTowardsKing) < Math.PI ){ 
			angleChange = angleChange; 
		}
		
		double xDifference = kingBoid.position.x - position.x;
		double yDifference = kingBoid.position.y - position.y;
		if( Math.sqrt((yDifference*yDifference) + (xDifference*xDifference)) < 50){ //if distance from this boid to king is small steer away, otherwise steer towards
			angleChange = -angleChange*2; //flip to away instead of toward
		}
		
		double newAngle = currentAngle + angleChange;
		Pair newVelocity = new Pair(Math.cos(newAngle), Math.sin(newAngle)).times(100);
		//System.out.println(newVelocity.x);
		//System.out.println(newVelocity.y);
		
		/* float xDifference = kingBoid.position.x - position.x;
		float yDifference = kingBoid.position.y - position.y;
		Pair towardsKing = new Pair(behindKing.x - position.x, behindKing.y - position.y); 
		towardsKing = towardsKing.normalizeVector().times(100); */
        return newVelocity;	
    }// follow ()
    

}// subclass Locust
//=======================================================================


//=======================================================================
/**
 * KingBoid subclass
 *@param spriteLocation a string representing the filepath of the KingBoid's sprite.
 **/
class KingBoid extends Boid{
	
    String spriteLocation;
    //=======================================================================


    //=======================================================================
    /**
     * KingBoid constructor
     * spriteLocation is set to the correct sprite filepath
     * the image is loaded using loadImage() and spriteLocation
     * boidWidth and boidHeight are set based on image size, which helps rotate the 
     * kingboid sprite correctly.
     **/
	public KingBoid(double X, double Y, World w){
		super(X, Y, w);
		spriteLocation = "sprites/king_crown_small.png";
		Image image = loadImage();
		boidWidth = image.getWidth(null);
                boidHeight = image.getHeight(null);
	}// KingBoid constructor
        //=======================================================================


        //=======================================================================
    /**
     * method update() changes position based on velocity, slows down boid over mountains
     * calls contain() to keep boid within world bounds.
     **/
     public void update(double time, Tile[][] map, Pair Target){
	    if(getTile(world.map).isMountain){
		 	position = position.add(velocity.times(time/2.5));
		}
		 else{
		 	position = position.add(velocity.times(time));
		 }
		contain();
     }// update ()
     //=======================================================================


     //=======================================================================
    /**
     * follow() makes the king follow the mouse position by changing its velocity.
     **/
    @Override
    public void follow(Pair target){
	Pair towardsTarget = new Pair(target.x - (.5*Main.WIDTH), target.y - (.5*Main.HEIGHT));
	velocity = towardsTarget;
	
    } //follow ()
    //=======================================================================


    //=======================================================================
    /**
     * method getKingPos() returns the pair representing a kingboid's position
     **/
    public Pair getKingPos(){
		return position;
    }// getKingPos ()
    //=======================================================================


    //=======================================================================
    /**
     * method draw () draws the kingboid's sprite at the kindboid's position
     * it applies a transformation to rotate the sprite based on the direction the 
     * king boid is flying in.
     *
     * References: 
     * https://stackoverflow.com/questions/20275424/rotating-image-with-affinetransform
     * http://beginwithjava.blogspot.com/2009/02/rotating-image-with-java.html
     * https://stackoverflow.com/questions/8639567/java-rotating-images
     * https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#atan2-double-double-
     * https://docs.oracle.com/javase/7/docs/api/java/awt/geom/AffineTransform.html
     * 
     *For some reason figuring out how to get the transformation to work correctly was extremely difficult.
     **/
    protected void draw(Graphics g, Main mainInstance){
		Image image = loadImage();
		 Graphics2D g2D = (Graphics2D)g;
		 AffineTransform backup = g2D.getTransform();
		 AffineTransform a  = AffineTransform.getRotateInstance(Math.atan2(velocity.normalizeVector().y, velocity.normalizeVector().x)+.5*Math.PI,(.5*Main.WIDTH) , (.5*Main.HEIGHT));
		 g2D.setTransform(a);
 
		 g2D.drawImage(loadImage(),(int)((.5*Main.WIDTH) - (.5*boidWidth)), (int)((.5*Main.HEIGHT) - (.5*boidHeight)), mainInstance);
		 g2D.setTransform(backup);
	        
    }// draw ()
     //=======================================================================


     //=======================================================================
    /**
     * method contain() keeps the king within the dimensions of the world
     **/
	public void contain(){
		double x = this.position.x;
		double y = this.position.y;
		double worldWidth = world.worldDimensions.x * Tile.width;
		double worldHeight = world.worldDimensions.y * Tile.height;
		if(x > worldWidth){this.position.x = worldWidth;}
		if(x < 0){this.position.x = 0;}
		if(y > worldHeight){this.position.y = worldHeight;}
		if(y < 0){this.position.y = 0;}
	}// contain ()
     //=======================================================================


     //=======================================================================

    /**
     * loadImage () loads and returns an image from a class param which is a string
     * called spriteLocation, the string being a filepath to an image file. 
     * Source:  http://zetcode.com/tutorials/javagamestutorial/movingsprites/
     **/
	 public Image loadImage() {
        
        ImageIcon ii = new ImageIcon(spriteLocation);
        Image image = ii.getImage();   
	        
	return image;
	 }// loadImage ()
  
}// subclass KingBoid
//=======================================================================


//=======================================================================
public class Boid{
//=======================================================================


    //===================================================================
    /**
     *@param position pair defining a boid's position
     *@param velocity pair defining a boid's velocity, gets treated like a (physics, not Java) vector
     *@param world the instance of the world class that a boid is in
     *@param boidWidth used for rotation animation of kingBoid
     *@param boidHeight used for rotation animation of kingBoid
     **/
    Pair position;
    Pair velocity;
    Color color;
    World world;
    double boidWidth = 0;
    double boidHeight = 0;
    //===================================================================

    
    //===================================================================
    /**
     * The Boid constructor. position coordinates and world are passed in.
     * Starting velocity is random; x and y velocity start between -5 and 5.
     * Default color is black. 
     **/
    public Boid(double X, double Y, World w){
	    Random r = new Random();
		this.position = new Pair(X, Y);
		this.velocity = new Pair((r.nextDouble()-.5)*10,(r.nextDouble()-.5)*10);
		color = new Color((float).674, (float).083, (float).000);
		this.world = w;
    }// Parent class Boid Constructor
    //===================================================================

    
    //===================================================================
    /**
     * method draw() draws a black circle with radius 7.5 at a boid's 
     * position coords. 
     **/
    public void draw(Graphics g, World world){
		Color c = g.getColor();
		g.setColor(color);
		Pair relativePosition = world.toDisplayCoords(position, world.kingBoid.position);
		g.fillOval((int)(relativePosition.x), (int)(relativePosition.y),  15, 15);
		g.setColor(c);
    }// draw ()
    //===================================================================


    //===================================================================
    /**
     * method getTile() returns the tile on the tile map that a boid is occupying
     **/
    public Tile getTile(Tile[][] map){
		double tileHeight = Tile.height;
		double tileWidth = Tile.width;
		int tilePosX = (int)Math.floor((position.x+(0.5*boidWidth)) / tileWidth);//boid's position x in tile map
		int tilePosY = (int)Math.floor((position.y+(0.5*boidHeight)) / tileHeight);//boid's position x in tile map
    
		if(tilePosX < map[0].length && tilePosX >= 0 && tilePosY < map.length && tilePosY >= 0){
			return map[tilePosY][tilePosX]; 
		}
		else{return map[0][0];}
    } //getTile ()
    //===================================================================


    //===================================================================
    /**
     * method reduceTileLife() incrementally reduces a tiles life value
     * also updates the tile sprite as life decreases to show life level.
     **/
	public void reduceTileLife(Tile tile){ //if tile is alive reduce its life
		if(tile.alive && tile.life > 0){
			tile.life -= .1;
		}
		tile.updateSprite();
	}// reduceTileLife ()
    //===================================================================

    
    //===================================================================
    /**
     * follow () method stub, overridden in locust and kingboid subclasses
     **/
    public void follow(Pair targetPosition){}
    //===================================================================


    //===================================================================
    /**
     * method distanceFrom() returns a double representing the euclidian distance
     * between two pairs, representing two positions on a coordinate plane.
     * used in locust.neighborhood() to get boids within a certain radius.
     **/
    public double distanceFrom(Pair a, Pair b){
	double x = Math.abs(a.x - b.x);
	double y = Math.abs(a.y - b.y);
	double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	return distance;
    }// distanceFrom ()
    
		
}// Parent class Boid
