package inkball;
import java.util.*;

import processing.core.PImage;
/**
 * Abstract class which generalises various items in the game
 */
public abstract class Object {
    protected float x;
    protected float y;
    
    protected String symbol;

    protected static Map<String, PImage> images = new HashMap<>();

    public Object(float x, float y, String symbol){
        this.x = x;
        this.y = y;
        
        this.symbol = symbol;
    }
    public String get_symb(){
        return this.symbol;
    }
    /**
     * Load all images for walls and breakable walls
     * @param app App instance
     */
    public static void loadImages(App app) {
        images.put("0", app.get_images("wall0"));
        images.put("X", app.get_images("wall0"));
        images.put("1", app.get_images("wall1"));
        images.put("2", app.get_images("wall2"));
        images.put("3", app.get_images("wall3"));
        images.put("4", app.get_images("wall4"));
        images.put(" ", app.get_images("tile"));
        
        // BWall images with symbols 5 to 9
        images.put("5", app.get_images("Bwall0")); 
        images.put("6", app.get_images("Bwall1")); 
        images.put("7", app.get_images("Bwall2")); 
        images.put("8", app.get_images("Bwall3")); 
        images.put("9", app.get_images("Bwall4")); 
    }

    /**
     * gets the image from Images ArrayList
     * @param s String image name
     * @return PImage of requested image name
     */
    public PImage set_image(String s){
        return images.get(s);
    }

    /**
     * abstract method implemented by subclasses to update their items
     * @param app App instance
     */
    public abstract void update_item(App app);
    /**
     * abstract method implemented by subclasses to draw their images
     * @param app App instance
     */
    public abstract void draw(App app);
    
}

