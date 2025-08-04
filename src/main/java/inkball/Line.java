package inkball;
import java.util.*;

/**
 * Represents a drawable line object formed from many points or line segments
 */
public class Line extends Object {

    public ArrayList<float[]> points;
    public ArrayList<float[]> line_seg;
    
    public Line(float x, float y){
        super(x,y,"L");
        this.points = new ArrayList<>();
        this.line_seg = new ArrayList<>();
    }
    /**
     * checks if two points are close together and removes them
     * @param app App instance
     */
    public void update_item(App app){
        float threshold = 10.0f; // Threshold distance for close points

        for (int i = 0; i < points.size() - 1; i++) {
            float[] point1 = points.get(i);
            float[] point2 = points.get(i + 1);

            // Calculate the distance between two consecutive points
            float distance = (float) Math.sqrt(Math.pow(point2[0] - point1[0], 2) + Math.pow(point2[1] - point1[1], 2));

            // If the distance is below the threshold, remove one of the points
            if (distance < threshold) {
                points.remove(i + 1); // Remove the second point
                i--; 
            }
        } 
    }
    /**
     * adds points to the current line
     * @param X x coordinate
     * @param Y y coordinate
     */
    public void add_points(float X, float Y){
        float[] point = {X,Y};
        this.points.add(point);

    }
    /**
     * Connects all the points of the line into line segments and addts them to an arraylist
     * draws these linesegments connecting each point to one another
     * @param app App instance
     */
    public void draw(App app) {
        app.stroke(0); 
        app.strokeWeight(10);
        float lX = this.x; 
        float lY = this.y; 
        
        
        this.line_seg.clear();
        for (float[] point : this.points) {
            
            app.line(lX, lY, point[0], point[1]);

            this.line_seg.add(new float[]{lX, lY, point[0], point[1]});
            
            lX = point[0];
            lY = point[1];
        }
    }
    
    /**
     * checks if the given mouse coordinates are on the line
     * @param mouseX mouse x coordinate
     * @param mouseY mouse y coordinate
     * @return if mouse is currently on the line
     */
    public boolean mouse_on_line(float mouseX, float mouseY) {
         
        // retreives all of the line segements
        for (float[] segment : line_seg) {
            float[] P1 = {segment[0], segment[1]}; 
            float[] P2 = {segment[2], segment[3]}; 
            //calculates the distance between 2 points of a line segment
            double distanceP1P2 = Math.sqrt(((P2[0] - P1[0]) * (P2[0] - P1[0])) + ((P2[1] - P1[1]) * (P2[1] - P1[1])));
            // calculate the perpendicular distance between the 2 points and our mouse
            double perp_dist = Math.abs((P2[1] - P1[1]) * mouseX - (P2[0] - P1[0]) * mouseY + P2[0] * P1[1] - P2[1] * P1[0]) / distanceP1P2;
            // checks if our mouse is within the x and y bounds, kind of like creating a square and checking if the mouse is within that
            boolean within_x_bounds = mouseX >= Math.min(P1[0], P2[0]) && mouseX <= Math.max(P1[0], P2[0]);
            boolean withing_y_bounds = mouseY >= Math.min(P1[1], P2[1]) && mouseY <= Math.max(P1[1], P2[1]);
            // checks if the perpendicualr distance is less than line thickness and it is within bounds and returns true
            if (perp_dist < 10 && within_x_bounds&&withing_y_bounds) {
                return true;
            }
        }
        return false; 
    }

    public float getx(){
        return this.x;
    }

    public float gety(){
        return this.y;
    }

    public ArrayList<float[]> getSeg(){
    
    return line_seg;
    }

    
}
