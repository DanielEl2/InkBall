package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;


import java.io.*;
import java.util.*;
/**
 * Our central Class which extends PApplet and controls all basic funtionality,gameplay and rendering of our program.
 */
public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;
    public static final int BALLCELLSIZE = 24;
    public static final int BALLCELLHEIGHT = 24;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 576; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;

    private static Random random = new Random();
    private JSONObject config_file;
    public static int level_num = 2;
    public static int max_level;
    /**
     * ArrayList of config balls
     */
    private ArrayList<String> balls = new ArrayList<>();
    
    protected Object[][] our_board;
    public int board_time;
    private boolean disable_time;
    public int spawn_interval;
    private double score_increase_from_hole_capture_modifier;
    private double score_decrease_from_wrong_hole_modifier;
    public boolean paused = false;
    public boolean level_end = false;
    public boolean time_up = false;
    public boolean you_lose = false;
    private int new_game_score;
    public boolean stopdrawing = false;
    
    private int game_score = 0;
    public HashMap<String, Integer> scoreinc = new HashMap<>();
    public HashMap<String, Integer> scoredec = new HashMap<>();
    /**
     * how long ago ball timer decremented
     */
    private long lastBTime = 0;
    /**
     * how long ago Game timer decremented 
     */
    private long lastGTime = 0; 
    double ball_timer = spawn_interval;
    ArrayList<Wall> yellowtiles = new ArrayList<>();
    /**
     * Position of victory yellow tile 1
     */
    int [] P1 = {0,64};
    /**
     * Position of victory yellow tile 2
     */
    int [] P2 = {544,608};
    int frame = 0;
    private HashMap<String, PImage> game_images = new HashMap<String, PImage>();
    private ArrayList<Line> Line_list = new ArrayList<>();
    private ArrayList<String> Bll_top = new ArrayList<>();
    /**
     * ArrayList of config ball PImages 
     */
    ArrayList<PImage> bimage = new ArrayList<>();
    /**
     * Arraylist of Ball positions in Top bar
     */
    ArrayList<Integer> ballPos = new ArrayList<>();
    boolean originalpos = true;
    boolean shift = false;
    private ArrayList<float[]> tilestodraw = new ArrayList<>();
    private ArrayList<BWall> Bwall_list = new ArrayList<>();
    private ArrayList<Ball> Balls_List = new ArrayList<>();
    private ArrayList<Hole> holes_List = new ArrayList<>();
    private ArrayList<Spawner> Spawner_List = new ArrayList<>();
    private ArrayList<Object> item_list_array = new ArrayList<>();
    /**
     * used as a way to limit how many points are drawn
     */
    private int lastT = 0;  
    private int timeInt = 30;

    /**
     * This the the App class which is the main class that controls most of the things occuring in the game
     * It is responsible for rendering all of the objects and calling various other classes to implement 
     * the overall function of the game.
     */
    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }
    /**
     * this method clears all of the ArrayLists that are being used by the Application, 
     * it is mainly used when a player resets a level, or transitions to another level
     */
    public void clear_lists(){
        balls.clear();
        Balls_List.clear();
        ballPos.clear();
        bimage.clear();
        holes_List.clear();
        Spawner_List.clear();
        Line_list.clear();
        yellowtiles.clear();
        scoreinc.clear();
        scoredec.clear();
        tilestodraw.clear();
        Bll_top.clear();
        Bwall_list.clear();
        Hole.Balls_im.clear();
        Hole.Balls_to_add.clear();
    }
    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        
        clear_lists();
        frameRate(FPS);
		//See PApplet javadoc:
		//loadJSONObject(configPath)
		// the image is loaded from relative path: "src/main/resources/inkball/..."
		/*try {
            result = loadImage(URLDecoder.decode(this.getClass().getResource(filename+".png").getPath(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }*/
        Load imageLoader = new Load(this); 
        imageLoader.loadImages(); 
        game_images = imageLoader.getGameImages();
        //load the character representation of the game and put it in board
        char[][] board = load_items(level_num);
        // the dimensions of the boared are determined by the height and width of the game window,
        this.our_board = new Object[(HEIGHT-TOPBAR)/CELLSIZE][WIDTH/CELLSIZE];
        // for each symbol retreived we assign it accordingly and create/instantiate new objects such as spawner,Hole,ball with this specific coordinates
        // we also assign that specific position in the board to be a wall, this is useful in ensuring there is a tile behind the initial balls
        //
        for (int i = 0; i < this.our_board.length; i++) {
            for (int j = 0; j < this.our_board[i].length; j++) {
                char symbol = board[i][j];
                if (symbol == 'H') { 
                    String fullSymbol = "" + symbol + board[i][j + 1];
                    Hole newhole = new Hole(j,i,fullSymbol);
                    this.our_board[i][j] = new Wall(j, i, " ");
                    add_hole(newhole); 
                    board[i][j + 1] = 'F';  
                    board[i + 1][j + 1] = 'F';
                    board[i + 1][j] = 'F';
                }
                else if (symbol == 'B') { 
                    String fullSymbol = "" + symbol + board[i][j + 1];
                        Ball newBall = new Ball(j, i, 2, 2, fullSymbol); 
                        this.our_board[i][j] = new Wall(j, i, " "); 
                        add_ball(newBall); 
                        board[i][j + 1] = ' ';
                }
                else if (symbol == 'S'){
                    Spawner newS = new Spawner(j, i, ""+symbol);
                    this.our_board[i][j] = new Wall(j, i, " ");
                    add_Spawner(newS);

                }
                else if (symbol == '5' || symbol == '6' || symbol == '7' || symbol == '8' || symbol == '9'){
                    BWall newBwall = new BWall(j, i, ""+symbol, 0);
                    this.our_board[i][j] = new Wall(j, i, " ");
                    add_bwall(newBwall);

                }
                else {
                    this.our_board[i][j] = new Wall(j, i, "" + symbol);  
                }
                
            }
        }
        // here we just call methods in other classes which retreive images needed for drawing
        get_ball_im();
        Object.loadImages(this); 
    }
    /**
     * loads all items in the config file depending on which level we are currently in
     * 
     * @param level_n level number
     * @return returns 2d array of board in characters
     */
    public char[][] load_items(int level_n){
        config_file = loadJSONObject("config.json");
        JSONArray background_values = config_file.getJSONArray("levels");
        max_level = background_values.size()-1;
        
        // chooses which Json object depending on which level we are on
        JSONObject b_value = background_values.getJSONObject(level_n);
    
        String layout = b_value.getString("layout");
        try {
            
            board_time = b_value.getInt("time");
        
           
            if (board_time <= 0) {
                // check If time is invalid 0 or negative
                disable_time=true;  
            }
            else{
                disable_time=false;
            }
        } catch (Exception e) {
            // check If the value is missing or not an integer
            disable_time=true;  
        }
        spawn_interval = b_value.getInt("spawn_interval");
        ball_timer = spawn_interval;
        score_increase_from_hole_capture_modifier = b_value.getDouble("score_increase_from_hole_capture_modifier");
        score_decrease_from_wrong_hole_modifier = b_value.getDouble("score_decrease_from_wrong_hole_modifier");
        JSONArray ballsJ = b_value.getJSONArray("balls");
        // adds the list of balls that must be spawned to the Arraylist balls in order to use it later
        for(int i=0; i<ballsJ.size();i++){
            balls.add(ballsJ.getString(i));
        }
        // adds all the values of score increases from different balls into the scoreinc hashmap
        JSONObject score_increase = config_file.getJSONObject("score_increase_from_hole_capture");
        scoreinc.put("grey", score_increase.getInt("grey"));
        scoreinc.put("orange", score_increase.getInt("orange"));
        scoreinc.put("blue", score_increase.getInt("blue"));
        scoreinc.put("green", score_increase.getInt("green"));
        scoreinc.put("yellow", score_increase.getInt("yellow"));
        // adds all the values of score decreases from different balls into the scoredec hashmap
        JSONObject score_decrease = config_file.getJSONObject("score_decrease_from_wrong_hole");
        scoredec.put("grey", score_decrease.getInt("grey"));
        scoredec.put("orange", score_decrease.getInt("orange"));
        scoredec.put("blue", score_decrease.getInt("blue"));
        scoredec.put("green", score_decrease.getInt("green"));
        scoredec.put("yellow", score_decrease.getInt("yellow"));
        //this reads a text file line by line.
        //It counts the number of rows and columns in the level text file.
        //It stores the file's content in a StringBuilder.
        //It then populates a 2D array (board) based on the characters from the file, where each row corresponds to a line from the file, and each column contains a character from that line.
        StringBuilder filestuff = new StringBuilder();
        int num_Cols = 0;
        int num_Row = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(layout))) {
            String input_line;
            while ((input_line = reader.readLine()) != null) {
                if (num_Cols == 0) {
                    num_Cols = input_line.length();  
                }
                num_Row++;  
                filestuff.append(input_line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        char[][] board = new char[num_Row][num_Cols];
        int i = 0, j = 0;
        for (int index = 0; index < filestuff.length(); index++) {
            char currentChar = filestuff.charAt(index);
            if (currentChar == '\n') {
                
                i++;
                j = 0; 
            } else {
                
                board[i][j] = currentChar; 
                j++; 
            }
        }
        return board;
    }

    // returns the arraylist of config balls
    public ArrayList<String> get_config_balls(){
        return balls;
    }
    // gets and returns images for our hashmap of images
    public PImage get_images(String l){
        PImage figure = game_images.get(l);
        return figure;
    }
    /**
     * resets the game and ensures all of our boolean items are correctly set,then calls setup.
     */
    public void resetGame(){
        paused = false;
        level_end = false;
        time_up = false;
        you_lose = false;
        shift = false;
        originalpos=true;
        P1 = new int[]{0, 64};  
        P2 = new int[]{544, 608};
        setup();
    }
    /**
     * Receive key pressed signal from the keyboard.
     */
    // if Space bar is pressed pause the game, if "r" is pressed reset the level or game
	@Override
    public void keyPressed(KeyEvent event) {
        char pressedKey = event.getKey(); 
    
        if (!you_lose && !level_end ) {
            if (pressedKey == ' ') {
                paused = !paused; 
            }
        }
        if (pressedKey == 'r' || pressedKey == 'R') {
            if (level_num == max_level && level_end){
                level_num = 0;
                game_score = 0;
                new_game_score=0;
                resetGame();
            }
            else{
                game_score = new_game_score;
                resetGame();
            }
             
        }
    }

    /**
     * Receive key released signal from the keyboard.
     * 
     */
	@Override
    public void keyReleased(){
    }
    
    Line L = null;

    //returns a list of all the lines
    public ArrayList<Line> get_line_l(){
        return Line_list;
    }
    /**
     * removes a line from our line list
     * @param l a line object
     */
    public void remove_line(Line l){
        Line_list.remove(l);
    }
    public int get_game_score(){
        return game_score;
    }
    public void setGameScore(int score) {
        this.game_score = score;
    }
    @Override
    /**
     * receives any mouse pressed signals from our mouse
     */
    // checks if left mouse button has been clicked and is in bounds and creates a new Line object with the initial position being our X and Y of mouse
    public void mousePressed(MouseEvent e) {
        
        if (e.getButton() == LEFT) {
            float mouseX = e.getX();
            float mouseY = e.getY(); 
            if (mouseX >= 0 && mouseX < WIDTH && mouseY >= TOPBAR && mouseY < HEIGHT + TOPBAR) {
                if (L == null){
                    L = new Line(mouseX, mouseY);
                    Line_list.add(L);
                }
                
            
            }
        }
    }
    @Override
    /**
     * receives any mouse dragged signals from our mouse
     */
    // as the mouse is dragged new points are added to our line object every "timeInt" milliseconds
    // if the right mouse button is dragged over a line it will remove it
    public void mouseDragged(MouseEvent e) {
        if (L != null && e.getButton() == LEFT) {
            int currentTime = millis();
            if (currentTime - lastT >= timeInt) {
                float mouseX = e.getX();
                float mouseY = e.getY();
                L.add_points(mouseX, mouseY);
                lastT = currentTime;  
            }
        }
        if (e.getButton() == RIGHT) {
            float rmouseX = e.getX();
            float rmouseY = e.getY();
            Line removal_l = null;
            for (Line line : Line_list) {
                if (line.mouse_on_line(rmouseX, rmouseY)) { 
                    removal_l = line;
                    break;
                }
            }
            if (removal_l != null) {
                remove_line(removal_l);  
            }
        }
    }
        
    /**
     * receives any mouse released signals from our mouse
     */
    // when the mouse is released reset our L variable to null so it can be used again
    @Override
    public void mouseReleased(MouseEvent e) {
        if (L != null) {
            L = null; 
        }	
    }
    // goes through our board and items items exluding walls
    public void items_list() {
        item_list_array.clear();
    
        for (int i = 0; i < this.our_board.length; i++) {
            for (int j = 0; j < this.our_board[i].length; j++) {
                Object tile = (Object) this.our_board[i][j];
                String symbol = tile.symbol;
                
                
                if (symbol.startsWith("X") || symbol.startsWith("1") || symbol.startsWith("2") || symbol.startsWith("3") || symbol.startsWith("4") ) {
                    item_list_array.add(tile);
                }
                
                
            }
        }  
    }
    /**
     * responsible for changing the current score of the game.
     * @param num number to add or remove from score
     * @param s string of Positive or Negative
     */
    public void change_score(int num,String s){
        if (s=="Negative"){
            game_score+= -Math.round(num * score_decrease_from_wrong_hole_modifier);
        }
        else if(s=="Positive"){
            game_score+= Math.round(num * score_increase_from_hole_capture_modifier);
        }  
    }

    // all of these methods below just get add or remove items from Arraylists that are used
    /**
     * adds a new Bwall item to Bwall_list.
     * @param item Bwall object
     */
    public void add_bwall(BWall item){
        Bwall_list.add(item);
    }
    public ArrayList<BWall> get_BWalls(){
        return Bwall_list;
    }
    /**
     * removes a Bwall item from our Bwall_list Arraylist.
     * @param item Bwall object
     */
    public void remove_bwall(BWall item){
        Bwall_list.remove(item);

    }
    public ArrayList<Spawner> get_Spawners(){
        return Spawner_List;
    }

    public ArrayList<Object> get_items_list(){
        return item_list_array;
    }

    /**
     * adds a new Ball item to Balls_List.
     * @param item Ball object
     */
    public void add_ball(Ball item){
        Balls_List.add(item);

    }
    /**
     * retreives Balls_List which is the list of balls currently in play
     * @return returns Ball arraylist
     */
    public ArrayList<Ball> get_Balls(){
        return Balls_List;
    }
    /**
     * adds a new Hole item to the holes_List arraylist
     * @param item Hole Object
     */
    public void add_hole(Hole item){
        holes_List.add(item);

    }
    /**
     * returns the arraylist which contains all of the holes.
     * @return Hole Arraylist
     */
    public ArrayList<Hole> get_holes(){
        return holes_List;
    }
    /**
     * adds a new spawner to Spawner_list.
     * @param item Spawner object
     */
    public void add_Spawner(Spawner item){
        Spawner_List.add(item);

    }
    /**
     * removes a ball from the config balls list.
     * @param i which index to remove
     */
    public void remove_ball(int i){
        balls.remove(i);
    }
    /**
     * removes a ball from the current moving balls list.
     * @param ball Ball object
     */
    public void remove_balls(Ball ball){
        Balls_List.remove(ball);
    }
 
    /**
     * adds a new config ball
     * @param s ball colour string
     */
    public void add_config_balls(String s){
        balls.add(s);

    }
    /**
     * Checks if the topbar balls are currently shifting
     * @return  returns true if shifting
     */
    public boolean is_shifting(){
        return shift;
    }

    /**
     * retreives all of the config file balls and adds them to bimage arraylist as their corresponding image.
     * adds a position to the arraylist ballPos for every image added.
     */
    public void get_ball_im(){
        
        Bll_top = get_config_balls();
        for (String s:Bll_top){
            if (s.equals("grey")){
                bimage.add(get_images("ball0"));
                
            }
            else if (s.equals("orange")){
                bimage.add(get_images("ball1"));
                
            }
            else if (s.equals("blue")){
                bimage.add(get_images("ball2"));
                
            }
            else if (s.equals("green")){
                bimage.add(get_images("ball3"));
                
            }
            else if (s.equals("yellow")){
                bimage.add(get_images("ball4"));
                
            }

        }
        for (int i = 0; i < bimage.size(); i++) {
            ballPos.add(10 + (i * 30)); 
        }
        
        
    }
    
    public int get_ballpos_size(){
        return ballPos.size();
    }
    /**
     * Checks if there are any remaining balls anywhere in our applicatiop
     * @return True if there are no remaining balls
     */
    public boolean check_remaining_balls(){
        ArrayList<String> configb = get_config_balls();
        ArrayList<Ball> normalb = get_Balls();
        if (configb.isEmpty() && normalb.isEmpty() && bimage.isEmpty() && Hole.Balls_im.isEmpty() ){ // checks that all of the ball lists are empty including the ones that Hole class uses to process balls
            
            return true; 
        }
        return false;
    }
    /**
     * responsible for drawing the 2 yellow victory tiles
     * @param P1 first yellow tile coordinate
     * @param P2 second yellow tile coordinate
     */
    public void draw_yellow_tiles(int[] P1, int[]P2){
        PImage ball_image = get_images("wall4"); // gets the yellow wall image to draw
        image(ball_image,P1[0],P1[1]);
        image(ball_image,P2[0],P2[1]);
    }
    
    /**
    * Draws the current state of the game on the screen.
    * handles the rendering of various game components
    * Draws and updated balls, holes, lines, and the game board
    * Shifts ball positions and managing their display
    * Manages level end conditions.
    
    */
	@Override
    public void draw() {  
        if (stopdrawing){
            return;
        }
        
        background(200, 200, 200);
 
        if (board_time <=0){
            if (!disable_time){
                time_up =true;
            } 
        }
        if (check_remaining_balls()) {
            level_end = true;
        }
        
        // when the time runs out and the level has not ended, the game is lost and TIMES UP is showin in Top bar
        if (time_up && !level_end){
            you_lose = true;
            textSize(20);
            textAlign(CENTER,CENTER);
            fill(0);
            text("===TIME'S UP==="  ,320,30);
        }
        if (paused){
            textSize(20);
            textAlign(CENTER,CENTER);
            fill(0);
            text("***PAUSED***"  ,320,30);
        }
        textSize(20);
        textAlign(CENTER,CENTER);
        fill(0);
        text("Score: " ,490,20);

        textSize(20);
        textAlign(CENTER,CENTER);
        fill(0);
        text(game_score ,540,20);
        if(!disable_time){
            textSize(20);
            textAlign(CENTER,CENTER);
            fill(0);
            text("Time: " + board_time ,510,45);
        }
        
        if(shift){  
            if (!bimage.isEmpty() && !ballPos.isEmpty()) {
                
                for(int j = 1; j<ballPos.size();j++){
                    if (ballPos.get(j) == ballPos.get(j-1)){
                        ballPos.remove(0);
                        shift =false;
                        originalpos = true;
                        break;
                    }
                    ballPos.set(j,ballPos.get(j)-1);
                    if (j<6 && j - 1 < bimage.size() && j < ballPos.size()){
                        image(bimage.get(j - 1), ballPos.get(j), 25);
                    }
                }
            }
        
        }
        if (originalpos){
            for (int i = 0;i<5 && i< bimage.size();i++){
                image(bimage.get(i),ballPos.get(i),25);
            }
        }
        

        if (!paused && bimage.size()>0 && !you_lose && (ballPos.size() == bimage.size() )) {     
            if (millis() - lastBTime >=100) {
                ball_timer-=0.1;
                lastBTime = millis();
            }
            
            if (ball_timer <= 0 ){
                int randomIndex = random.nextInt(Spawner_List.size());
                Spawner Spawn = Spawner_List.get(randomIndex);
                Spawn.update_item(this);
                
                    
                    if (bimage.size() > 0) {
                        bimage.remove(0);
                        if (ballPos.size() == 1){
                            ballPos.remove(0);
                        }
                        originalpos = false;
                    }
                    shift = true;
                ball_timer = spawn_interval;
            }
        }
        
        if (bimage.size() == 0){
            shift = false;
            originalpos = true;   
        }
        for (int i = 0; i < this.our_board.length; i++) {
            for (int j = 0; j < this.our_board[i].length; j++) {
                this.our_board[i][j].update_item(this);
                this.our_board[i][j].draw(this);
            }
        }
        for (BWall B : Bwall_list){
            B.draw(this);
        }
        for (Hole H : holes_List){
            
            H.draw(this);
            H.update_item(this);
            H.warpball(this); 
        }
        if (bimage.size()>0) {
            textSize(18);
            fill(0);
            textAlign(CENTER, CENTER);
            String Cdown = String.format("%.1f", ball_timer);
            text(Cdown, 200, 35);
        }
        if(!paused && !time_up && !level_end && !disable_time){
            if (millis() - lastGTime >= 1000) {
                board_time -= 1;
                lastGTime = millis();    
            }
        }
        if (!Spawner_List.isEmpty()){
            for(Spawner S: Spawner_List){
                S.draw(this);
            }
        }
            
        if (!Balls_List.isEmpty()){
            for (Ball ball : Balls_List) {
                ball.update_item(this);
                ball.draw(this);
                if (paused || you_lose){
                    Ball.pause_balls();
                }
                else{
                    Ball.unpause();
                }  
            }
        }
        if (!you_lose && !level_end){
            for(Line l : Line_list){
                l.update_item(this);
                l.draw(this);
            }
        }
        if (level_end ) {
            draw_yellow_tiles(P1, P2);
            frame++;
            if (time_up || disable_time){
                if (level_num!=max_level){
                    level_num+=1;
                    new_game_score = game_score;
                    resetGame();
                }
                else{
                    textSize(20);
                    textAlign(CENTER,CENTER);
                    fill(0);
                    text("===ENDED==="  ,320,30);
                    
                }
            }
            if (frame >= 2) { 
                if (!time_up) {
                    board_time -=1;
                    game_score+=1;
                    if (P1[0] < WIDTH - 32 && P1[1] == 64) {
                        P1[0] += 32; 
                    } else if (P1[0] == WIDTH - 32 && P1[1] < HEIGHT - 32) {
                        P1[1] += 32; 
                    } else if (P1[0] > 0 && P1[1] == HEIGHT - 32) {
                        P1[0] -= 32; 
                    } else if (P1[0] == 0 && P1[1] > 64) {
                        P1[1] -= 32; 
                    }
        
                    if (P2[0] > 0 && P2[1] == HEIGHT - 32) {
                        P2[0] -= 32; 
                    } else if (P2[0] == 0 && P2[1] > 64) {
                        P2[1] -= 32; 
                    } else if (P2[0] < WIDTH - 32 && P2[1] == 64) {
                        P2[0] += 32; 
                    } else if (P2[0] == WIDTH - 32 && P2[1] < HEIGHT - 32) {
                        P2[1] += 32; 
                    }
                }
                frame = 0; 
            }
            
        }
        
    }
    
    public static void main(String[] args) {
        PApplet.main("inkball.App");
        
        
    }


}


// fix up vibration of balls when they hit a line
// check if we are rounding score


