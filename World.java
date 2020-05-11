//=======================================================================
/**
 * This is the World Class of the Plague! game. 
 * This class is responsible for keeping track of the boids and the map.
 * From here we call the draw() functions of all the boids and tiles, as well
 * as call update() on the boids. World is also responsible for map generation.
 */
//=======================================================================



//=======================================================================
// IMPORTS
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.MouseListener;//so we can listen to the mouse
import java.awt.event.MouseEvent;
//=======================================================================


//=======================================================================
class World{
//=======================================================================
	
	
	//=======================================================================
    /**
     *@param worldDimensions dimensions of the world in tiles
     *@param mainInstance instance of Main
     *@param initNumBoids starting number of locusts
     *@param numBoids int keeping track of the current number of locusts
     *@param boids ArrayList of all the locusts
     *@param kingBoid contains the instance of KingBoid
     *@param map 2D array of the tiles
     **/
    Pair worldDimensions;
	Main mainInstance;
    
	int initNumBoids = 1; //starting number of locusts to be generated
	int numBoids = 0; //keeps track of how many locusts there are
	public ArrayList<Locust> boids = new ArrayList<Locust>(); //ArrayList containing alll the locusts
    
	KingBoid kingBoid = new KingBoid(2000,2000,this); 
	public Tile[][] map; // 2D array will contain all the tiles. Their positions in the array represent their positions in the game
	//=======================================================================

	
	//=======================================================================
    /**
     * Constructor for world. Sets fields, calls generateMap() and 
	 * creates the initial number of locusts near the king
     **/
    public World(Pair worldDimensions, Main mainInstance){
		this.worldDimensions = worldDimensions;
		map = generateMap(worldDimensions.x, worldDimensions.y);
		this.mainInstance = mainInstance;
		Random r = new Random();
		for(int i = 0; i < initNumBoids; i++){ //create boids near the king
		    boids.add(new Locust((r.nextDouble()-.5)*400 + kingBoid.position.x, (r.nextDouble()-.5)*400 + kingBoid.position.y, this));
		}
	
	} //World() constructor
	//=======================================================================
	
	
	//=======================================================================
	/**
	*Returns the percent of tiles left alive. Calls win() in Main if none are left.
	**/
	public int getPercentAlive(){ 
		int alive = 0;
		int dead = 0;
		for(int i = 0; i < map.length; i++){ //iterates through all tiles
			for(int j = 0; j < map[0].length; j++){
				if(map[i][j].alive){
					alive++;
				}
				else if(map[i][j].isMountain == false && map[i][j].alive == false){ //mountains don't count towards the dead tile count
					dead++;
				}
			}
		}
		double percent = (((double)alive/(double)(alive+dead))*100);
		int percentInt = (int)percent;
		if(alive == 0){mainInstance.win();} //if there are none alive win
		return percentInt;
	} //getPercentAlive()
	//=======================================================================

	
	//=======================================================================
	/**
	*Calling the draw functions of all the Boids and Tiles.
	**/
    public void drawBoids(Graphics g, Main mainInstance){ //calls every locust's draw() method as well as the king's
	
		for (int i = 0; i < numBoids; i++){
		    boids.get(i).draw(g, this);
		}
		kingBoid.draw(g, mainInstance);
		
    }
	
	public void drawTiles(Graphics g, Main mainInstance){  //iterates through all the tiles and calls their draw functions.
		for(int i = 0; i < map.length; i++){
			for(int j = 0; j < map[0].length; j++){
				map[i][j].draw(g, kingBoid.position, mainInstance);
			}
		}
	}
	//=======================================================================
	

	//=======================================================================
	/**
	*Calling the update functions of all the Boids.
	**/
    public void updateBoids(double time){ //calls every boid's update() method. If there aren't any locusts left calls the lose() method of Main. 
		kingBoid.update(time, map, mainInstance.mousePosition);//here's where we call the mouse listener from the main method
		for (int i = 0; i < numBoids; i ++){
		    boids.get(i).update(time, map, boids, kingBoid);
		}
		if(this.numBoids == 0){
			mainInstance.lose();
		}
	}
	//=======================================================================
	
	
	//=======================================================================
	/**
	* Generating the map. Starts by seeding it randomly with a mountain or live tile here and there,
	* then fills in the gaps. Sets borders as mountains.
	**/
    public Tile[][] generateMap(double x, double y){ //generates map of tiles and puts them in the map 2d array
		
		map = new Tile[(int)x][(int)y]; //set map dimensions
		Random r = new Random();//randomize tile life levels with r.nextDouble()*100
		
		int[][] seededMap = generateSeedMap(x, y); //generates seeded map
		
		for(int i = 0; i < map.length; i++){//fill gaps
			for(int j = 0; j < map[0].length; j++){
				Pair tilePos = new Pair(j, i);
				double[] distances = getSeedDistances(seededMap, tilePos);
				if(distances[0]<distances[1]){map[i][j] = new MountainTile(j, i, 0, this);} //if closer to a mountain than a live tile set as mountain
				else{map[i][j] = new Tile(j, i, r.nextDouble()*100+.1, this);} //otherwise set as a live tile with random life.
			}
		}
		
		
		for(int i = 0; i < map.length; i++){
			for(int j = 0; j < map[0].length; j++){
				if(i == 0||i == map.length-1|| j == 0||j == map[0].length-1){
					  map[i][j] = new MountainTile(j, i, 0, this);//put mountains at edges
				}
			}
		}
		
		return map;
	    
	} //generateMap()
	
	public int[][] generateSeedMap(double x, double y){ //creates a 2d array of ints representing mountain seeds, live seeds, and undefined tiles
		Random r = new Random();//generate seeds
		
		int[][] seededMap = new int[(int)x][(int)y];
		double mountainOdds = 0.1; 
		double aliveOdds = 0.2;
		
		for(int i = 0; i < seededMap.length; i++){ //generate seeds
			for(int j = 0; j < seededMap[i].length; j++){
				double rand = r.nextDouble();
				if(rand < mountainOdds){seededMap[i][j] = 0;} //0=mountains   these are also the indexes for distance from that biome later
				else if(rand>mountainOdds && rand<mountainOdds+aliveOdds){seededMap[i][j] = 1;}//1=alive
				else{seededMap[i][j] = 2;}//2=none
			}
		}
		
		return seededMap;
	} //generateSeedMap()
	
	public double[] getSeedDistances(int[][] seededMap, Pair position){ //gets smallest distance of position to mountain and alive seeds
		
		double[] distances = new double[2]; // index 0 = mtn distance index 1 = alive distance
		distances[0]=100000;
		distances[1]=100000; //start em real high or else if there isn't a single seed of 1 type it will always be registered as 0 distance. That would be bad.
		for(int i = 0; i < seededMap.length; i++){ 
			for(int j = 0; j < seededMap[i].length; j++){
				if(!(seededMap[i][j] == 2)){//if the tile is a seed and not nothing
					double distance = Math.sqrt(Math.pow((position.x - j),2) + Math.pow((position.y - i),2));
					if(distance==0.0){//if tile is on a seed make it that seed
						distances[seededMap[i][j]] = 0.0;
						distances[(seededMap[i][j]+1)%2] = 100000.0; //set the other tile type distance to an absurdly high number so it won't become that
						return distances;
					}
					else if(distances[seededMap[i][j]] == 0.0 || distances[seededMap[i][j]] > distance){
						distances[seededMap[i][j]] = distance;
					}
				}
			}
		}
		return distances;
		
	} //getSeedDistances
	//=======================================================================
	
	
	//=======================================================================
	/**
	* Convert coordinates in the world to coordinates on the screen. Static so that it can be used by any object.
	**/
	public static Pair toDisplayCoords(Pair coords, Pair displayCenter){ 
		
		double tileCoordX = (coords.x);
		double tileCoordY = (coords.y); //these are coords of the top left corner
		Pair topLeftDisplayCorner = new Pair(displayCenter.x-(Main.WIDTH/2), displayCenter.y-(Main.HEIGHT/2));
		double relativeX = tileCoordX - topLeftDisplayCorner.x;
		double relativeY = tileCoordY - topLeftDisplayCorner.y;
		Pair displayCoords = new Pair(relativeX, relativeY);
		return displayCoords;
		
	} //toDisplayCoords()
	//=======================================================================

} //class World
