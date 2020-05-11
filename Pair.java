//===================================================================
/**
 * Pair class from prior labs
 * Only change is the added functionality of the normalizeVector() method
 * which allowed us to treat pairs representing velocity like 
 * vectors (physics, not Java), and get a pair's unit vector.
 **/
//===================================================================
class Pair{
    public double x;
    public double y;
    
    public Pair(double initX, double initY){
		this.x = initX;
		this.y = initY;
    }

    public Pair add(Pair toAdd){
		return new Pair(x + toAdd.x, y + toAdd.y);
    }

    public Pair divide(double denom){
		return new Pair(x / denom, y / denom);
    }

    public Pair times(double val){
		return new Pair(x * val, y * val);
    }
    //===================================================================
    /**
     * method normalizeVector() returns a pair representing the unit vector
     * of the pair it is called on. Used on boid velocities. 
     * It's all over the place in the boid class.
     **/
    public Pair normalizeVector(){
	//mag = vector magnitude
	double mag = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	//return new pair normalized by magnitude
	return new Pair(x/mag, y/mag);
	
    }
}
