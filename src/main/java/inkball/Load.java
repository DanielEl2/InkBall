package inkball;

import processing.core.PApplet;
import processing.core.PImage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
/**
 * loads all image resources
 */
public class Load {
    private PApplet app;
    private HashMap<String, PImage> game_images;
    
    public Load(PApplet app) {
        this.app = app;
        this.game_images = new HashMap<>();
    }
    /**
     * loads all the images from our resources folder and puts them into the game_images Hashmap
     * @param app App instance
     */ 
    public void loadImages() {
        for (int i = 0; i < 5; i++) {
            try {
                game_images.put("wall" + i, app.loadImage(URLDecoder.decode(this.getClass().getResource("wall" + i + ".png").getPath(), StandardCharsets.UTF_8.name())));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        for (int i = 0; i < 5; i++) {
            try {
                game_images.put("ball" + i, app.loadImage(URLDecoder.decode(this.getClass().getResource("ball" + i + ".png").getPath(), StandardCharsets.UTF_8.name())));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        for (int i = 0; i < 5; i++) {
            try {
                game_images.put("hole" + i, app.loadImage(URLDecoder.decode(this.getClass().getResource("hole" + i + ".png").getPath(), StandardCharsets.UTF_8.name())));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        for (int i = 0; i < 5; i++) {
            try {
                game_images.put("Bwall" + i, app.loadImage(URLDecoder.decode(this.getClass().getResource("Bwall" + i + ".png").getPath(), StandardCharsets.UTF_8.name())));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            game_images.put("tile", app.loadImage(URLDecoder.decode(this.getClass().getResource("tile" + ".png").getPath(), StandardCharsets.UTF_8.name())));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        try {
            game_images.put("entry", app.loadImage(URLDecoder.decode(this.getClass().getResource("entrypoint" + ".png").getPath(), StandardCharsets.UTF_8.name())));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, PImage> getGameImages() {
        return game_images;
    }
}
