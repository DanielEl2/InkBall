package inkball;
import java.util.*;
import processing.core.PImage;

/**
 * This class represents a Ball in the Inkball game.
 * A Ball object has properties such as position, velocity, and size, 
 * and is responsible for updating its movement and checking for collisions.
 */
public class Ball extends Object{
    
    private float velocityX;
    private float velocityY;
    
    
    private float initialx;
    private float initialy;
    public float radius;
     
    public static boolean paused;
    private int collisionBuffer = 3;
    
    
    boolean flag =false;
    private String newsymb;
    public boolean breaking;
    private ArrayList<Object> item_array = new ArrayList<>();
    private ArrayList<Line> line_array = new ArrayList<>();
    private ArrayList<BWall> Bwall_array = new ArrayList<>();
    

    public Ball(float x, float y, float velocityX, float velocityY, String symbol) {
        super(x*32, y*32, symbol);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.initialy = y;
        this.initialx=x;
        this.radius = 12;
        
        
  
        
    }
    
    
    

    
    /**
     * Responsible to updating all ball actions, such as velocity and calling collision functions
     * @param app App instance
     * 
     */
    public void update_item(App app) {
        if (paused){
            return;

        }
        if (collisionBuffer>0){
            collisionBuffer --;
        }
        add_velocity(); // updates ball's position by adding velocity
        
        if (item_array.isEmpty()) {
            item_array = app.get_items_list(); 
            app.items_list();
        }
        if (line_array.isEmpty()) {
            line_array = app.get_line_l(); 
        }
        if (Bwall_array.isEmpty()){
            Bwall_array = app.get_BWalls();
        }
        
        for (BWall b : Bwall_array){
            this.checkCollision(b, app); // checks collision with BWall objects
        }
        for (Object s:item_array){
            this.checkCollision(s,app); // Checks collision with walls
                
        }
        if (collisionBuffer == 0){
            this.checkCollisionLine(line_array, app); // checks collision with Line objects when collision buffer is 0
        }    
        // removes BWall objects after they break only after iterating through them so no error occurs
        ArrayList<BWall> remove_bwall = BWall.get_bwall_removal();
        for (BWall item : remove_bwall){
            app.remove_bwall(item);
            Bwall_array.remove(item);

        }
        // these check if the ball is colliding with the edge of the board, has a tolerance of -4
        if (this.x <= -4 || this.x + this.radius * 2 >= App.WIDTH-4) {
            velocityX = -velocityX; 
        }
        if (this.y <= -4 || this.y + this.radius * 2 >= App.HEIGHT - App.TOPBAR - 4) {
            velocityY = -velocityY; 
        }
    }
    
    /**
     * responsible for drawing and rendering the balls in the game
     * @param app App instance
     */
    public void draw(App app) {
        
        char ball_num = this.symbol.charAt(1); // gets the second character to check for ball colour
            if (ball_num =='B'){
                
            }
        PImage image = app.get_images("ball" + ball_num);
        if (flag == false ) {
            flag = true;
            app.image(image, (initialx * App.CELLSIZE)+4, (initialy * App.CELLSIZE)+4 + App.TOPBAR,this.radius*2,this.radius*2); // draws the initial image of the ball once
        }
        else{
            app.image(image, this.x, App.TOPBAR+this.y,this.radius*2,this.radius*2); // draws the ball 
        }   
    }
    /**
     * Responsible for checking collisions between a ball and line objects
     * @param lines Line Arraylist
     * @param app App instance
     */
    public void checkCollisionLine(ArrayList<Line> lines,App app) {
        
        
        float futureX = this.x +this.radius ; // minusing velocities so it collides closer
        float futureY = this.y +this.radius ;
        
        // for each line it collects its line segments and checks if the ball collides with it
        for (int i = lines.size() - 1; i >= 0; i--) { 
            Line item = lines.get(i);
            ArrayList<float[]> segments = item.getSeg();
            for (float[] segment : segments) {
                float[] P1 = {segment[0], segment[1]-App.TOPBAR};
                float[] P2 = {segment[2], segment[3]-App.TOPBAR};
                // gets normal vector of the points in the line segment
                float[] P1P2N1 = normal_v(new float[] {-(P2[1] - P1[1]), (P2[0] - P1[0])}); 
                float[] P1P2N2 = normal_v(new float[] {(P2[1] - P1[1]), -(P2[0] - P1[0])});
                if (checkCollisionWithLnSegment(P1, P2, futureX, futureY)) { // checks if there was a collision with line segment
            
                    Normal_closer(P1, P2, P1P2N1, P1P2N2,app); // changes the velocity of the ball accordingly
                
                    app.remove_line(item); // removes line if collision occured
                    collisionBuffer = 3; // reinstate collision buffer to 3 
                    
                }
                
            }
        }
       
    }
    
    /**
     * Checks if a ball has collided with a corner of a wall
     * @param P1 Corner of wall
     * @param app App instance
     * @return true if collision occured
     */
    public boolean checkPointCollision(float[] P1, App app) {
        float futureX = this.x  + this.radius + velocityX;
        float futureY = this.y  + this.radius  + velocityY ;
    
        double distance = App.dist(futureX,futureY, P1[0], P1[1]);
    
        
        if (distance <5) { // checks if the centre of the ball is in close proximity of corner
             // reverses the velocities to reflect ball
            this.velocityX = -velocityX;
            this.velocityY = -velocityY;
            return true; 
        }
        else{
            return false;
        }
    }
    
    /**
     * This is the main function that handles collisions between a ball and any wall object
     * @param item Object item
     * @param app App instance
     */
    public void checkCollision(Object item,App app) {
        float futureX = this.x  + this.radius ;
        float futureY = this.y  + this.radius  ;
        boolean colchange = false;
        // gets all the points of of a wall
        float[] P1 = {item.x * 32, item.y * 32-1};                
        float[] P2 = {item.x * 32 + 32, item.y * 32-1};    
        float[] P3 = {item.x * 32, item.y * 32 + 32+1};   
        float[] P4 = {item.x * 32 + 32, item.y * 32 + 32+1};
        // checks if the wall is a colour changing one
        if (item instanceof Wall){
            newsymb = item.get_symb();
            if (newsymb.equals("X")|| newsymb.equals(" ")|| newsymb.equals("F")){
                colchange = false;
            }
            else{
                colchange = true;
            }
        }
        // its its an instance of BWall checks if the colours match in order to break the wall
        if(item instanceof BWall){
            newsymb = item.get_symb();
            int ball_number = Integer.parseInt(this.symbol.substring(1));
            if (newsymb.equals("5")){
                breaking =true;
            }
            else if (Integer.parseInt(newsymb) >= 6 && Integer.parseInt(newsymb) <= 9) {
                
                if (ball_number == Integer.parseInt(newsymb) - 5) {
                    breaking = true;
                } else {
                    breaking = false;
                }
            } 
            else {
                breaking = false;
            }
            
        }
        else{
            breaking = false;
        }
        // gets all the normal vectors of all the line segments of a wall
        float[] P1P2N1 = normal_v(new float[] {-(P2[1] - P1[1]), (P2[0] - P1[0])}); 
        float[] P1P2N2 = normal_v(new float[] {(P2[1] - P1[1]), -(P2[0] - P1[0])}); 

        float[] P2P4N1 = normal_v(new float[] {-(P4[1] - P2[1]), (P4[0] - P2[0])}); 
        float[] P2P4N2 = normal_v(new float[] {(P4[1] - P2[1]), -(P4[0] - P2[0])});

        float[] P4P3N1 = normal_v(new float[] {-(P3[1] - P4[1]), (P3[0] - P4[0])}); 
        float[] P4P3N2 = normal_v(new float[] {(P3[1] - P4[1]), -(P3[0] - P4[0])}); 
        float[] P3P1N1 = normal_v(new float[] {-(P1[1] - P3[1]), (P1[0] - P3[0])}); 
        float[] P3P1N2 = normal_v(new float[] {(P1[1] - P3[1]), -(P1[0] - P3[0])});

        float[][] linePoints = {P1, P2, P4, P3};
        // checks if a collision has occured with any of the line segments of the wall
        // if so calls Normal_closer to change their velocity
        // if colchange is true or break is true deals with changing colours and breaking wall accordingly

        if (checkCollisionWithLnSegment(P1, P2, futureX, futureY)){
            this.y -=1.5;
            
            Normal_closer(P1, P2, P1P2N1, P1P2N2,app);
            if (colchange == true){
                this.change_col("B" +newsymb);
            }
            if (breaking){
                item.update_item(app);
            }
            return;
        }
        else if(checkCollisionWithLnSegment(P2, P4, futureX, futureY)){
            this.x+=1.5;
            
            Normal_closer(P2, P4, P2P4N1, P2P4N2,app);
            if (colchange == true){
                this.change_col("B" +newsymb);
            }
            if (breaking){
                item.update_item(app);
            }
            
            return;
        }
        else if(checkCollisionWithLnSegment(P4, P3, futureX, futureY)){
            this.y+=1.5;
            Normal_closer(P4, P3, P4P3N1, P4P3N2,app);
            if (colchange == true){
                this.change_col("B" +newsymb);
            }
            if (breaking){
                item.update_item(app);
            }
            
            return;
        }
        else if(checkCollisionWithLnSegment(P3, P1, futureX, futureY)){
            this.x-=1.5;
            Normal_closer(P3, P1, P3P1N1, P3P1N2,app);
            if (colchange == true){
                this.change_col("B" +newsymb);
            }
            if (breaking){
                item.update_item(app);
            }
            return;
        }
        // checks for point collisions and deals with colour change and breaking accordingly
        else{
            if (!(item instanceof Line))
            for (float[] point : linePoints) {
                if (point_collision(point, app,colchange,item)) {
                    return;  
                }
            }
        }
        return;
    }
    /**
     * Handles point collisions dealing with colour changing and breaking BWalls
     * @param P Point of wall
     * @param app App instance
     * @param colchange true if wall is a colour changing wall
     * @param item Object item
     * @return returns of collision occured
     */
    private boolean point_collision(float[] P, App app,boolean colchange,Object item) {
        if (checkPointCollision(P, app)) {
            if (colchange) {
                this.change_col("B" + newsymb);
            }
            if (breaking) {
                item.update_item(app);
            }
            return true;  
        }
        return false;
    }
    /**
     * retreives the normal vectors of a given vector
     * @param vec vector
     * @return normal vector
     */
    private float[] normal_v(float[] vec) {
        float magn = (float) Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1]);
        return new float[] { vec[0] / magn, vec[1] / magn };
    }
    /**
     * main function for handling velocity change after collision has been detected
     * @param P1 point one of line seg
     * @param P2 point two of line seg
     * @param P1P2N1 normal vector of 2 points
     * @param P1P2N2 normal vector of 2 points
     * @param app App instance
     */
    public void Normal_closer(float[] P1, float[] P2, float[]P1P2N1, float[] P1P2N2,App app){
        // midpoint of points
        float midx = (P1[0] + P2[0]) / 2.0f; 
        float midy = (P1[1] + P2[1]) / 2.0f;
        // Shift the midpoint by normal vectors
        float[] mid_1 = {midx + P1P2N1[0], midy + P1P2N1[1]};
        float[] mid_2 = {midx + P1P2N2[0], midy + P1P2N2[1]};
        float bx = this.x;
        float by = this.y;
        // Calculate distance from ball to each shifted midpoint
        float distanceM1 = (float) Math.sqrt(Math.pow(mid_1[0] - bx, 2) + Math.pow(mid_1[1] - by, 2));
        float distanceM2 =  (float) Math.sqrt(Math.pow(mid_2[0] - bx, 2) + Math.pow(mid_2[1] - by, 2));
         // Choose the closer normal vector
        float[] choseN;
        if (distanceM1 < distanceM2) {
            choseN= P1P2N1; 
        } else {
            choseN = P1P2N2; 
        }
        // Calculate the dot product of velocity and the chosen normal
        float dotPP = (this.velocityX * choseN[0]) + (this.velocityY * choseN[1]);
        // Reflect velocity across the chosen normal
        float newvX = this.velocityX - 2 * dotPP * choseN[0];
        float newvY = this.velocityY - 2 * dotPP * choseN[1];
        // Update velocity 
        if (Math.abs(newvX) <=0.2 || Math.abs(newvY)<=0.2){
            this.velocityX = -velocityX;
            this.velocityY = -velocityY;
        }
        else{
            this.velocityX = newvX;
            this.velocityY = newvY; 
        }
    }
    /**
     * method that checks if a ball has collided with a line segment
     * @param P1 first point of line seg
     * @param P2 second point of line seg
     * @param ballX current x coordinate
     * @param ballY current y coordinate
     * @return if collision occured
     */
    public boolean checkCollisionWithLnSegment(float[] P1, float[] P2, float ballX, float ballY) {
        // gets the distances of point1 to point 2
        // gets the distances of point 2 to ball and point 1 to ball
        double distanceP1Ball = App.dist(ballX+velocityX,ballY+velocityY,P1[0],P1[1]);
        double distanceP2Ball =App.dist(ballX+velocityX,ballY+velocityY,P2[0],P2[1]);
        double distanceP1P2 =App.dist(P2[0],P2[1],P1[0],P1[1]);

        // if the distance between p1 to ball and p2 to ball is less than the distance betweent the two point
        //  plus radius plus a tolerance of -6 then return true as the ball is colliding
        if (distanceP1Ball + distanceP2Ball < distanceP1P2 + this.radius-6) {
            
            return true;
        } else {
             
            return false;
        }
    }

    /**
     * sets the velocity of the ball
     * @param velocityX x velocity
     * @param velocityY y velocity
     */
    public void setVelocity(float velocityX, float velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public float getx() {
        return x;
    }

    public float gety() {
        return y;
    }
    public String getSymbol(){
        return this.symbol;
    }
    public float getRadius(){
        return this.radius;
    }
    
    /**
     * sets the radius of the ball
     * @param num float number
     */
    public void setSize(float num){
        this.radius = num;
    }
    /**
     * changes the colour of the ball by changing symbol
     * @param b String symbol
     */
    public void change_col(String b){
        this.symbol = b;

    }
    /**
     * sets pause to true 
     */
    public static void pause_balls(){
        paused = true; 
    }
    /**
     * unpauses the balls allowing them to move
     */
    public static void unpause(){
        paused = false; 
    }
    public float getVX(){
        return this.velocityX;
    }
    public float getVY(){
        return this.velocityY;
    }
    /**
     * adds velocity to current position
     */
    public void add_velocity(){
        this.x += velocityX;
        this.y += velocityY;

    }

    

    
}
