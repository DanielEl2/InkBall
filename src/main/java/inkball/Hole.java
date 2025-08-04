package inkball;

import processing.core.PImage;
import java.util.*;
/**
 * Hole class represents a target or circular gap where balls get captured in order to
 * progress the game.
 */
public class Hole extends Object{
    
    public int score;
    public int radius;
    public ArrayList<Ball> Ball_removal = new ArrayList<>();
    public static ArrayList<String> Balls_to_add = new ArrayList<>();
    public static ArrayList<PImage> Balls_im = new ArrayList<>();
    private int size;
    private ArrayList<Ball> Ball_warp = new ArrayList<>();
    
    
    public Hole(float x, float y, String symbol){
        super(x*32,y*32,symbol);
        
        
    }
    /**
     * updates the top_bar balls list when the topbar is not shifting
     * @param app App instance
     */
    public void update_item(App app){
        // checks if the app is currently shifting topbar and adds the corresponding captured balls to be placed in the topbar 
        // and released by the spawner once more.
        if (!app.is_shifting()) {
            
            
            while (!Balls_im.isEmpty() &&!Balls_to_add.isEmpty()) {
                size = app.get_ballpos_size();
                app.bimage.add(Balls_im.get(0));
                app.ballPos.add(10 + (size * 30));
                app.add_config_balls(Balls_to_add.get(0));
    
                
                Balls_im.remove(0);
                Balls_to_add.remove(0);
                
            }
            
                  
        }
    }
    /**
     * responsible for drawing all of the holes in the game
     * @param app App instance
     */
    public void draw(App app){
        char hole_num = this.symbol.charAt(1);
        
        PImage image = app.get_images("hole" + hole_num);
        app.image(image, this.x, this.y + App.TOPBAR);
        //update_item(app);
    }

    
   /**
    * handles warping the ball size and calling functions to capture it
    * handles the removal of balls aswell after capture
    * @param app App instance
    */
    public void warpball(App app) {
        // get lists of all balls and holes
        Ball_warp = app.get_Balls();
        ArrayList<Hole> holes = app.get_holes(); 
        
        // loop through each ball to check if it is within 32 pixels of a hole
        for (Ball b : Ball_warp) {
            boolean changeS = false; 
            
            // for each hole check if the ball is captured or within 32 pixels we set changeS to true
            for (Hole h : holes) {
                if (BallinHole(b, h, app)) {
                    changeS = true; 
                }
            }
            // if changeS is not set we set the ball back to its original size as it is no longer near the holes
            // we do this so that if a ball is within one hole but over 32 pixels away from another hole it still changes size
            if (!changeS) {
                b.setSize(12); 
            }
        }
        remove_balls(app);
    }
    /**
     * Reduces the size of balls if they are within close proximity of holes
     * creates and attraction force and captures balls that are ontop of holes
     * @param ball Ball object
     * @param h Hole object
     * @param app App instance
     * @return if ball is capture or within the hole
     */
    public boolean BallinHole(Ball ball, Hole h, App app) {
        // get the coordinates of given ball and hole
        float ballCX = ball.getx() + ball.getRadius();
        float ballCY = ball.gety() + ball.getRadius();
        float holeCX = h.getx() + 32; 
        float holeCY = h.gety() + 32; 
        //calculate the distance between the centre of the ball and centre of the hole
        float distance_ball = (float) Math.sqrt(Math.pow(holeCX - ballCX, 2) + Math.pow(holeCY - ballCY, 2));
        
        // if the distance is less than 32 create an attractive force to alter the velocity of the ball
        // set the size of the ball depending on the deisctance away from the hole
        // if the distance is less than the radius + newsize tolerance then capture the ball 
        if (distance_ball <= 32) { 
            
            float ForceX = 0.0035f * (holeCX - ballCX);
            float ForceY = 0.0035f * (holeCY - ballCY);
    
        
            ball.setVelocity(ball.getVX() + ForceX, ball.getVY() + ForceY);
    
            float newSize = Math.max(6, 12 * (distance_ball / 32.0f)); 
            ball.setSize(newSize);
            //if ball is approximately on the hole it will capture it, i did +2 so it is easier to score the ball
            if (distance_ball <= ball.getRadius()+2) {
                score_hole(ball, h, app);
                add_removal(ball); 
                 
                return true; 
            }
            return true; 
        }
        
        return false; 
    }
    /**
     * responsible for checking and changing the score of the game 
     * @param ball Ball object
     * @param hole Hole Object
     * @param app App instance
     */
    public void score_hole(Ball ball, Hole hole, App app) {
        // gets the hole and ball symbols
        char bsym = ball.getSymbol().charAt(1);
        char hsym = hole.getsymbol().charAt(1);
        String Type_of_change;
        int newscore = 0;
        
        // create a hashmap of the ball symbols and their colours
        HashMap<String, String> ballColors = new HashMap<>();
        ballColors.put("B0", "grey");
        ballColors.put("B1", "orange");
        ballColors.put("B2", "blue");
        ballColors.put("B3", "green");
        ballColors.put("B4", "yellow");
    
        
    
        // we check if the ball and hole symbol matches or if it is a grey ball or hole and set the type of chane to positive
        // we add score depending on the colour of the balls
        // else we set the type of change to negative to reduce the score and add the ball back to topbar
        if (bsym == hsym || hole.getsymbol().equals("H0") || ball.getSymbol().equals("B0")) {
            String color = ballColors.get(ball.getSymbol());
            newscore = app.scoreinc.get(color);
            Type_of_change = "Positive";
        } else {
            String color = ballColors.get(ball.getSymbol());
            newscore = app.scoredec.get(color);
            add_ball_im(app.get_images("ball" + ball.getSymbol().charAt(1))); 
            add_ball(color); 
            Type_of_change = "Negative";
        }
    
        app.change_score(newscore, Type_of_change);
    }
    /**
     * adds a colour of a ball to the Balls_to_add Arraylist.
     * @param s String representing colour of a ball
     */
    public void add_ball(String s){
        Balls_to_add.add(s);
    }
    /**
     * Adds a PImage to Balls_im Arraylist.
     * @param p PImage to add
     */
    public void add_ball_im(PImage p){
        Balls_im.add(p);
    }
    
    /**
     * add balls to be removed
     * @param ball Ball object
     */
    public void add_removal(Ball ball){
        Ball_removal.add(ball);

    }
    /**
     * removes balls in our remove_balls arraylist
     * @param app App instance
     */
    public void remove_balls(App app){
        for (Ball b:Ball_removal){
            app.remove_balls(b);
        }
    }
    public float getx(){
        return this.x;
    }
    public float gety(){
        return this.y;
    }
    public String getsymbol(){
        return this.symbol;
    }
}
