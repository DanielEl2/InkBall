package inkball;
import processing.core.PImage;
/**
 * represents various tiles within the game including walls, background tiles and coloured tiles.
 * balls reflect and bounce off of mnost Wall objects
 */
public class Wall extends Object{
    
    private PImage imageToDraw = null;
    
    public Wall(float x, float y, String symbol){
        super(x,y,symbol);   
    }
    public float get_x(){
        return this.x;
    }
    public float get_y(){
        return this.y;
    }
    /**
     * changes the image drawn depending on symbol of Wall
     * @param app App instance
     */
    public void update_item(App app){
        imageToDraw = set_image(symbol);
    }
    public String get_symb(){
        return this.symbol;
    }
    /**
     * draws the image of the wall
     * @param app App instance
     */
    public void draw(App app) {
        
        
         if (imageToDraw != null) {
            app.image(imageToDraw, x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR,32,32);
        } 
    }
}
