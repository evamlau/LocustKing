//=======================================================================
/**
 * This is the Main Class of the Plague! game. It creates and manages the
 * information within the world. It also updates graphics to reflect the 
 * state of the world, including pausing and printing text on screen when 
 * the win or lose conditions are met.
 *
 * @authors Eva Lau, Ethan Lebowitz, Karina Thanawala
 * Code inspiration/source: KeyboardSpheres Lab Source Code
 * loadImage() source: http://zetcode.com/tutorials/javagamestutorial/movingsprites/
 * scaleImage() source: //https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon
 * Format inspired by Professor Kaplan's CaesarCipher.java code format
 **/
//=======================================================================



//=======================================================================
// IMPORTS
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;

import java.util.ArrayList;
import java.util.Random;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints; 

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;
//=======================================================================



//=======================================================================
public class Main extends JPanel implements MouseMotionListener{
//=======================================================================


    //=======================================================================
    /**
     *@param WIDTH width of the JPanel
     *@param HEIGHT height of the JPanel
     *@param FPS frames per second, used for animation
     *@param worldDimensions pair representing x and y dimensions of world in number of tiles
     *@param world the world instance in which the game takes place
     *@param mousePosition the pair representing the x and y coords of the mouse, used to control the leader(king) boid
     *@param lost boolean representing whether losing conditions have been met
     *@param won boolean representing whether winning conditions have been met
     **/
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public static final int FPS = 60;
    public Pair worldDimensions = new Pair(100, 100);
    World world;
    public Pair mousePosition;
    boolean lost = false;
    boolean won = false;
    //=======================================================================


    //=======================================================================
    /**
     * Calls update methods at regular increments of time based on FPS value,
     * as long as losing or winning conditions have not been met.
     **/
    class Runner implements Runnable{
		public void run()
		{
			while(true){
				if((!lost) && (!won)){
					world.updateBoids(1.0 / (double)FPS); 
					//world.updateTiles(1.0 / (double)FPS);
				}
				repaint();
				try{
					Thread.sleep(1000/FPS);
				}
				catch(InterruptedException e){}
			}

		}
    
    }// end class Runner
	
    //=======================================================================

    
    //=======================================================================
    /**
     * Where MouseListener implementation happens. Mouse coords are put in a pair 
     * which is passed to kingBoid.follow(), resulting in the kingBoid (leader) 
     * being led by the mouse around the map.
     *
     * Player can seamlessly transition between moving the leader by moving the 
     * cursor and moving the leader by dragging the mouse. I don't know why people
     * would want to do this but it seems important for ~robustness~.
     **/
	@Override
    public void mouseMoved(MouseEvent e){
		int x =  e.getXOnScreen() ;
		int y =  e.getYOnScreen() ;
	       
		Pair m = new Pair((double)x, (double)y);
		mousePosition = m;
		world.kingBoid.follow(m);
	}// mouseMoved ()
	
    	@Override
    public void mouseDragged(MouseEvent e){
	    int x =  e.getXOnScreen() ;
	    int y =  e.getYOnScreen() ;
	       
	    Pair m = new Pair((double)x, (double)y);
	    mousePosition = m;
	    world.kingBoid.follow(m);
	}// mouseDragged
    //=======================================================================


    //=======================================================================
    /**
     * Still not 100% certain what addNotify() does. Looked around and found 
     * this: https://stackoverflow.com/questions/23690937/what-does-addnotify-do
     * which seems well beyond the scope of what we should expect to understand
     * after taking CS112. 
     **/
     public void addNotify() {
        super.addNotify();
        requestFocus();//this line is a mood  //facts
     }// addNotify ()
    //=======================================================================


    //=======================================================================
    /**
     * Set values for losing and winning booleans.
     **/
	
	public void lose(){
		this.lost = true;
	}// lose()
	public void win(){
		this.won = true;
	}// win ()
    //=======================================================================


    //=======================================================================
    /**
     * The constructor for the Main class. Creates the world based on parameters 
     * outlined above. Adds the MouseMotionListener which is implemented.
     * Sets JPanel dimensions. Creates a new Thread for the Runner class and 
     * starts the thread upon opening.
     **/
    public Main(){ 
		world = new World(worldDimensions, this); 
		addMouseMotionListener(this);
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		Thread mainThread = new Thread(new Runner());
		mainThread.start();
    }// Main () constructor
    //=======================================================================


    //=======================================================================
    /**
     * Entry point for program. Creates JFrame to display graphics, sets JFrame
     * to cease running when window is closed. Creates instance of Main and makes
     * graphics from Main visible in the JFrame.
     **/
    public static void main(String[] args){
		JFrame frame = new JFrame("Plague!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Main mainInstance = new Main();
		frame.setContentPane(mainInstance);
		frame.pack();
		frame.setVisible(true);
    } // main (String[] args)
    //=======================================================================


    //=======================================================================
    /**
     * Source:http://zetcode.com/tutorials/javagamestutorial/movingsprites/
     * Method to load image from a png file using string representing the filepath.
     **/
    public static Image loadImage(String path) {
        
        ImageIcon ii = new ImageIcon(path);
        Image image = ii.getImage(); 
        
		return image;
    }// loadImage ()
    //=======================================================================


    //=======================================================================
    /**
     * Source:https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageiconhttp://zetcode.com/tutorials/javagamestutorial/movingsprites/
     * Method that scales an image given width and height parameters.
     * Returns the scaled image.
     **/
	public static Image scaleImage(Image srcImg, int w, int h){
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();

		return resizedImg;
	}// scaleImage ()
    //=======================================================================


    //=======================================================================
    /**
     * Draws the score information panel in the upper left corner.
     **/
	
	private void drawPanel(Graphics g){
		int border = 9;
		Pair dimensions = new Pair(135, 200);
		Pair position = new Pair(20,20);

		//score panel color and dimensions
		g.setColor(new Color((float).9647,(float).7765,(float)0.0));
		g.fillRect((int)position.x, (int)position.y, (int)dimensions.x, (int)dimensions.y);
		g.setColor(new Color((float).996,(float).976,(float).706));
		g.fillRect((int)position.x + border, (int)position.y + border, (int)dimensions.x - (2*border), (int)dimensions.y - (2*border));
		//draws sprites representing a live tile, a dead tile, or a locust to be displayed next to corresponding info
		Graphics2D g2d = (Graphics2D) g;
    	g2d.drawImage(scaleImage(loadImage("sprites/live2.png"), 40, 40), (int)position.x + 20, (int)position.y + 20, this);
		g2d.drawImage(scaleImage(loadImage("sprites/dead2.png"), 40, 40), (int)position.x + 20, (int)position.y + 80, this);
		g2d.drawImage(scaleImage(loadImage("sprites/locust_swarm.png"), 35, 35), (int)position.x + 20, (int)position.y + 140, this);

		//draws strings representing % alive tiles, % dead tiles, & # locusts in current swarm
		g.setColor(Color.BLACK);
		int percentAlive = world.getPercentAlive();
		g.drawString(String.valueOf(percentAlive)+"%", (int)position.x + 80, (int)position.y + 45); // % alive
		g.drawString(String.valueOf(100-percentAlive)+"%", (int)position.x + 80, (int)position.y + 105); // % dead
		g.drawString(String.valueOf(world.numBoids), (int)position.x + 80, (int)position.y + 165); // % dead
	}// drawPanel ()
    //=======================================================================


    //=======================================================================
    /**
     * Used to draw either "YOU WIN" or "YOU LOSE" when winning/losing conditions
     * are met.
	 *https://docs.oracle.com/javase/7/docs/api/java/awt/Font.html#BOLD
	 *https://docs.oracle.com/javase/7/docs/api/java/awt/Font.html#getStringBounds(java.lang.String,%20int,%20int,%20java.awt.font.FontRenderContext)
	 *https://docs.oracle.com/javase/7/docs/api/java/awt/font/FontRenderContext.html
	 *https://docs.oracle.com/javase/7/docs/api/java/awt/FontMetrics.html#getStringBounds(java.lang.String,%20java.awt.Graphics)
	 *https://docs.oracle.com/javase/tutorial/2d/text/measuringtext.html
	 *https://docs.oracle.com/javase/7/docs/api/java/awt/geom/Rectangle2D.html
     **/
	private void displayMessage(Graphics g, String message, Color color){
		int border = 0;
		Font font = new Font("Monospaced", Font.BOLD, 175); //https://stackoverflow.com/questions/18249592/how-to-change-font-size-in-drawstring-java
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics(font);
		Rectangle2D stringBounds = metrics.getStringBounds(message, g);
		int textHeight = (int)stringBounds.getHeight();
		int textWidth = (int)stringBounds.getWidth();
		int textPosX = (WIDTH/2) - (int)(0.5*textWidth);
		int textPosY = (HEIGHT/2) + (int)(0.5*textHeight);
		
		g.setColor(color);
		g.drawString(message, textPosX, textPosY);
	}// displayMessage ()
        //=======================================================================


        //=======================================================================
        /**
	 * Draws graphics as they update until winning or losing conditions are met.
	 * Calls displayMessage() and stops updating graphics when winning or 
	 * losing conditions are met.
	 **/

    public void paintComponent(Graphics g) {
		
		super.paintComponent(g);    	

		world.drawTiles(g, this);
		if(! (lost || won)){
			world.drawBoids(g, this);
		}
		
		drawPanel(g);
		
		if(lost){
			displayMessage(g, "YOU LOST", Color.RED);
			System.out.println(1111);
		}
		if(won){
			displayMessage(g, "YOU WON", Color.GREEN);
		}
		
    }

    
}
