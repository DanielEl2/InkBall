package inkball;
import java.util.*;
import processing.core.PImage;
/**
 * represents a tile which spawns or releases balls held in the configuration file or wrongly captured balls.
 */
public class Spawner extends Object{
    
    public ArrayList<String> Balls_to_r;
    public String Symbol;
    /**
     * static Hashmap which contains colours and their symbols
     */
    private static final Map<String, String> ballC = new HashMap<>();

    static {
        ballC.put("blue", "B2");
        ballC.put("orange", "B1");
        ballC.put("grey", "B0");
        ballC.put("green", "B3");
        ballC.put("yellow", "B4");
    }

    public static Random random = new Random();
    public Spawner(float x, float y, String Symbol){
        super(x,y,Symbol);  
    }
    /**
     * creates and releases a new ball object from config balls
     * @param app App instance
     */
    public void update_item(App app){
        // gets a random int either 2 or -2 to set it as the balls velocity
        int[] speed = {2,-2};
        int randomX = speed[random.nextInt(speed.length)];
        int randomY = speed[random.nextInt(speed.length)];
        // gets all of the config balls
        Balls_to_r = app.get_config_balls();
        
        // if there are config balls remaining it retreives their colour and symbol, and create a new instance of Ball
        // it adds this new ball to the App's Balls_List array and removes the ball from the config list
        if (Balls_to_r.size() > 0) {
            String Colour = Balls_to_r.get(0);
            String ballSymbol = ballC.get(Colour); 
            
            Ball newBall = new Ball(this.x, this.y, randomX, randomY, ballSymbol);
            app.add_ball(newBall);
            app.remove_ball(0); 
        }
    }
    /**
     * draws the hole images in the game
     * @param app App instance
     */
    public void draw(App app){
        PImage image = app.get_images("entry");
        app.image(image, x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
    }
    public float getx(){
        return this.x;
    }
    public float gety(){
        return this.y;
    }
    public String getSymbol(){
        return this.Symbol;
    }
 
}
