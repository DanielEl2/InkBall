package inkball;

import java.util.ArrayList;

import processing.core.PImage;
/**
 * BWall class which represents a Brick or tile which breaks after being hit by ball objects
 */
public class BWall extends Object {
   
    public int hits;
    public boolean cracked = false;
    static ArrayList<BWall> bwall_remove = new ArrayList<>();

    public BWall(float x, float y, String Symbol, int hits){
        super(x, y, Symbol);
        this.hits = hits;
    }
    /**
     * updates how many hits a BWall has taken and its removal
     * @param app App instance
     */
    public void update_item(App app){
        this.hits++;
        if (this.hits == 2){
            this.cracked = true;

        }
        if (this.hits == 3){
            add_bwall_removal(this);
        }
    }
    
    /**
     * draws and renders all of the BWall objects in the game
     * @param app App instance 
     */
    
    public void draw(App app){
        PImage imageToDraw = null;
        int newsymb = Integer.parseInt(this.symbol) - 5; // minusing by 5 in order to retreive normal wall images
        String uncrackedSymbol = ""+newsymb;
        String crackedSymbol = "" + (newsymb+5);
       // draws normal wall images if it is not cracked else draws a cracked wall
        if (!this.cracked) {
            
            imageToDraw = set_image(uncrackedSymbol);
            
        } else {
            
            imageToDraw = set_image(crackedSymbol);  
        }
        app.image(imageToDraw, x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR, 32, 32);
    }
    public float get_x(){
        return this.x;
    }

    public float get_y(){
        return this.y;
    }
    
    public String get_symb(){
        return this.symbol;
    }
    /**
     * adds a BWall object to be removed later
     * @param item BWall object
     */
    public static void add_bwall_removal(BWall item){
        bwall_remove.add(item);
    }
    /**
     * retrieves the list of BWall objects that are going to be removed
     * @return returns BWall ArrayList
     */
    public static ArrayList<BWall> get_bwall_removal(){
        return bwall_remove;

    }
    
}
