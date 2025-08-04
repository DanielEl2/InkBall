package inkball;

import processing.core.PApplet;
import processing.core.PConstants;


import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import processing.event.KeyEvent;
import processing.event.MouseEvent;


public class InkballTests {
    
    

    // used to create a new App instance
    public static App setup() {
         App app = new App();
         app.stopdrawing = true;
        
        app.delay(500);
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(300);
        App.level_num = 0;
        app.setup();
        app.delay(500);
        return app;
        
        
    }
    App testing_app = setup();

    @Test
    public void simpleTest() {
        // Tests basic setup and loop initialization for the game.
        App newapp = new App();
        newapp.loop();
        PApplet.runSketch(new String[] { "App" }, newapp);
        newapp.setup();
        newapp.delay(500); 
    }

    @Test
    public void testFramerate() {
        // Verifies the frame rate is close to the expected FPS.
        testing_app.setup();
        assertTrue(
            App.FPS - 2 <= testing_app.frameRate && 
            App.FPS + 2 >= testing_app.frameRate
        );
    }


    @Test
    public void Test_ball_moving_position(){
        
        // Tests ball movement by checking if the ball's position updates correctly.
        Ball testball = new Ball(5, 5, 2, 2, "B1");
        assertNotNull(testing_app);
        testball.update_item(testing_app);
        
        assertEquals(162f, testball.x, 0.01f);
        assertEquals(162f, testball.y, 0.01f);
        
        
    }
    @Test
    public void testPauseFunctionality() {
        // Tests if the game pauses when the space key is pressed.
        testing_app.setup();
        testing_app.you_lose = false;
        testing_app.paused = false;
        KeyEvent spaceEvent = new KeyEvent(null, 50, KeyEvent.PRESS, 0, ' ', 32);
        testing_app.delay(100);
        testing_app.keyPressed(spaceEvent);
        testing_app.delay(100);
        assertTrue(testing_app.paused);
    }

    @Test
    public void testLevelResetFunctionality() {
         // Tests if the game level resets when the 'r' key is pressed.
        testing_app.level_end = true;
        
        
        KeyEvent rEvent = new KeyEvent(null, 20, KeyEvent.PRESS, 0, 'r', 'r');
        testing_app.delay(100);
        testing_app.keyPressed(rEvent);
        testing_app.delay(100);
        
        assertFalse(testing_app.level_end);

        testing_app.level_end = true;
        
        
        KeyEvent REvent = new KeyEvent(null, 20, KeyEvent.PRESS, 0, 'R', 'R');
        testing_app.delay(100);
        testing_app.keyPressed(REvent);
        testing_app.delay(100);
        
        assertFalse(testing_app.level_end);
        App.level_num=App.max_level;
        testing_app.level_end=true;
        testing_app.keyPressed(rEvent);
        assertTrue(App.level_num ==0);
        App.level_num = 0;
        testing_app.delay(300);
        
    }
    @Test
    public void test_changing_levels() {
        // Tests if the level increments after certain conditions are met.
        App t1_app = setup();
        App.level_num = 1;
        t1_app.stopdrawing = false;
        
        clear_items_and_images(t1_app);
        t1_app.get_Balls().clear();
        t1_app.get_Spawners().clear();

        
        t1_app.paused = true;
        t1_app.board_time = 0;
        t1_app.setGameScore(0);
        t1_app.level_end = true;
        t1_app.time_up = true;
        
        t1_app.frame = 2;  

        
        t1_app.draw();
        
        
        t1_app.delay(100);

        assertEquals(2, App.level_num);  

        t1_app.stopdrawing = true;
        t1_app.delay(100);
    }
    @Test
    public void testcreatingLineObject() {
        // Tests if a new line object is created and updated when mouse events occur
        // tests if points are added to this object when mouse is dragged
        // tests if L is null when mouse is released making it ready for a new line to be created-
        testing_app.get_line_l().clear();
        
        MouseEvent click = new MouseEvent(null,50,MouseEvent.PRESS,0,100,100,PConstants.LEFT,1);
        
        testing_app.delay(100);
        testing_app.mousePressed(click);
        testing_app.delay(100);
        
        assertTrue(testing_app.get_line_l().size()>0);
        assertTrue(testing_app.get_line_l().get(0).getx()==100);

        MouseEvent dragged = new MouseEvent(null,50,MouseEvent.DRAG,0,103,103,PConstants.LEFT,1);

        testing_app.delay(100);
        testing_app.mouseDragged(dragged);
        testing_app.delay(100);
        Line L = testing_app.get_line_l().get(0);
        L.draw(testing_app);
        float[] expectedPoint = {103, 103};
        float[] actualPoint = L.points.get(0);
        assertTrue(expectedPoint[0]==actualPoint[0]);
        MouseEvent released = new MouseEvent(null, 50, MouseEvent.RELEASE, 0, 103, 103, PConstants.LEFT, 1);
        testing_app.mouseReleased(released);
        testing_app.delay(100);
        assertTrue(testing_app.L == null);

        

        
        
    }
    
    @Test
    public void testLevelEndLogic() {
        // Tests level end logic, including time decrement and score increment.
        App test_end = setup();
        
        clear_items_and_images(test_end);
        test_end.paused=false;
        test_end.stopdrawing = false;
        test_end.level_end = true;
        test_end.board_time = 100;
        test_end.setGameScore(0);
        test_end.time_up = false;
        test_end.frame = 0;

        
        for (int i = 0; i < 2; i++) {
            test_end.draw();  
            
        }

        // time decreases while score increases
        assertEquals(99, test_end.board_time);  
        assertEquals(1, test_end.get_game_score());   
        test_end.stopdrawing = true;
    
    }
    
   
    
    @Test
    public void Test_ball_colliding_with_wall(){
        
        // Test ball velocity changes after colliding with a wall
        Ball testball = new Ball(5,12 , 2, 0, "B1");
        
        testing_app.get_Balls().add(testball);
        Wall testwall = new Wall(6, 12, "X");
        testball.add_velocity();
        testball.add_velocity(); // adding velocity simulating movement
        testball.add_velocity();
        testball.add_velocity();
        testball.add_velocity();
        
        
        testball.checkCollision(testwall, testing_app);
        testing_app.delay(200);
        // check if velocity changes after collision, since theres only x velocity it should reflect
        assertEquals(-2f, testball.getVX(),0.5f);
    }
    @Test
    public void Test_shifting_top_bar_balls(){
        // Test shifting top bar balls
        clear_items_and_images(testing_app);
        testing_app.stopdrawing = false;
        testing_app.paused =true;
        testing_app.level_end = false;
        testing_app.originalpos = false;
        testing_app.shift=true;
        testing_app.ballPos.add(40);  
        testing_app.ballPos.add(41);  
        
        testing_app.bimage.add(testing_app.get_images("ball2"));
        

        testing_app.delay(100);

        testing_app.draw();
       
        
        testing_app.delay(200);
        // only one ball remains after shifting 
        assertEquals(1, testing_app.ballPos.size());
        
        testing_app.stopdrawing = true;

    }
    
     
    @Test
    public void testTimeUp() {
        // tesing what occurs when the time has run out 
        testing_app.board_time = 0;
        testing_app.stopdrawing = false;
        testing_app.draw();
        testing_app.delay(400);
        assertTrue(testing_app.time_up);
        assertTrue(testing_app.you_lose);
        testing_app.stopdrawing=true;
    }
     
     
     @Test
     public void Testing_Hole_Capture(){
        // testin
         testing_app.get_holes().clear();
         testing_app.get_Balls().clear(); 
     
         Ball testball = new Ball(5, 6, 2, 0, "B1");
         testing_app.get_Balls().add(testball); 
     
         Hole testhole = new Hole(6, 5, "H1");
         testing_app.add_hole(testhole); 
     
         // Simulate ball movement and check if the ball is captured
         for (int i = 0; i < 25; i++) {
             testball.add_velocity();
             testing_app.delay(100);
             boolean yes = testhole.BallinHole(testball, testhole, testing_app);
             testing_app.delay(200); 
             if (yes){
                if (testhole.Ball_removal.size()>0){
                    break;
                }
                
             }
         }
     
         // Assert that the ball is removed after being captured, we call remove_balls as when a ball is captured it goes in
         // Ball_removal List.
         testhole.remove_balls(testing_app);
         testing_app.delay(300);
         assertTrue(testing_app.get_Balls().isEmpty());
     }

        


    
    @Test
    public void Testing_Ball_size_change_from_hole(){
        // Test ball size reduction after entering a hole
        App t4_app = setup();
        clear_items_and_images(t4_app);
        t4_app.get_Balls().clear();
        t4_app.get_holes().clear();
        
        Ball t1_ball = new Ball(5, 6, 2, 0, "B1");
        Hole testhole = new Hole(6, 5, "H1" );
        t4_app.get_Balls().add(t1_ball);
        t4_app.get_holes().add(testhole);
        assertTrue(t1_ball.getRadius() == 12);
        // Simulate ball movement into the hole
        for (int i = 0; i < 25; i++) {
            t1_ball.add_velocity();
            t4_app.delay(100);
            boolean yes = testhole.BallinHole(t1_ball, testhole, t4_app);
            t4_app.delay(200); 
            if (yes){
                break;
               
               
            }
        }
        
        // Assert the ball's radius decreases after entering the hole
        assertTrue(t1_ball.getRadius() < 12);
    }
    
    @Test
    public void check_remaining_balls_test(){
        // Test if the game detects that all balls are removed
        testing_app.get_Balls().clear();
        testing_app.bimage.clear();
        Hole.Balls_im.clear();
        testing_app.get_config_balls().clear();
        // Assert that there are no remaining balls
        boolean cleared = testing_app.check_remaining_balls();
        assertTrue(cleared);
    }
    @Test
    public void getting_ball_images(){
        // testing if our get_ball_im functions correctly
        clear_items_and_images(testing_app);
        // add config balls to balls ArrayList
        testing_app.add_config_balls("blue");
        testing_app.add_config_balls("orange");
        testing_app.add_config_balls("green");
        testing_app.add_config_balls("blue");
        testing_app.delay(200);
        //Get the config ball images
        testing_app.get_ball_im();
        testing_app.delay(200);
        // check if the amount mathces the ammount added
        assertTrue(testing_app.bimage.size() ==4 && testing_app.ballPos.size()==4);
    }
    @Test
    public void decrease_score_add_ball_back(){
        // testing that if a ball goes in the wrong hole we decrease score and add ball back to topbar
        App t6_app = setup();
        clear_items_and_images(t6_app);
        t6_app.get_Balls().clear();
        t6_app.get_holes().clear();
        t6_app.bimage.clear();
        Hole.Balls_im.clear();
        Hole.Balls_to_add.clear();
        // create ball and hole
        Ball testball = new Ball(5, 5, 2, 0, "B2");
        Hole testhole = new Hole(0, 0, "H1" );
        t6_app.setGameScore(0);
        // simulate scoring hole 
        testhole.score_hole(testball,testhole,t6_app);
        t6_app.delay(300);
        t6_app.shift =false;
        t6_app.originalpos = false;
        testhole.update_item(t6_app);
        // check that the score decreased and the ball is back in top bar ArrayList
        assertTrue(t6_app.get_game_score()<0);
        assertTrue(t6_app.get_config_balls().size() == 1);


    }
    @Test
    public void increase_score_dont_add_ball_back(){
        // Tests when a ball enters the correct hole it increases score and doesnt go back
        clear_items_and_images(testing_app);
        Hole.Balls_im.clear();
        Hole.Balls_to_add.clear();
        //create new ball and hole
        Ball testball = new Ball(5, 5, 2, 2, "B1");
        Hole testhole = new Hole(0, 0, "H1" );
        testing_app.setGameScore(0);
        // simulate scoring hole
        testhole.score_hole(testball,testhole,testing_app);
        testing_app.delay(600);
        //check that the score increased and top bar list in empty
        testhole.update_item(testing_app);
        testing_app.delay(1000);
        assertTrue(testing_app.get_game_score()>0);
        assertTrue(testing_app.bimage.isEmpty());
        assertTrue(testing_app.get_config_balls().isEmpty());


    }

    
    @Test
    public void test_spawner_spawns_ball(){
        // Test spawner creating and releasing a ball
        App t3_app = setup();
        Spawner testSpawner = new Spawner(5,13 , "S");
        t3_app.get_Spawners().clear();
        t3_app.get_Balls().clear();
        clear_items_and_images(t3_app);
        t3_app.add_config_balls("blue");
        t3_app.delay(300);
        testSpawner.update_item(t3_app);
        t3_app.delay(300);
         // Assert that a ball is added to the Balls_List after spawning
        assertTrue(t3_app.get_Balls().size() == 1);
        ArrayList <Ball> newballs = t3_app.get_Balls();
        Ball ball = newballs.get(0);
        // assert that it is correct symbol
        assertEquals("B2", ball.getSymbol());
        
    }
    @Test
    public void test_mouse_on_line(){
        // testing mouse_on_line method and checking whether a line is removed when right draggin over it 
        App t8_app = setup();
        clear_items_and_images(t8_app);
        t8_app.get_line_l().clear();
        t8_app.get_Balls().clear();
        Line testline = new Line(100, 100);
        testline.add_points(150, 150);
        testline.add_points(125, 125);
        testline.add_points(175, 125);
        testline.draw(t8_app);
        assertTrue(testline.mouse_on_line(130, 130));
        assertFalse(testline.mouse_on_line(150, 170));
        assertTrue(testline.mouse_on_line(140, 140));
        t8_app.L = testline;
        assertTrue(t8_app.get_line_l().size() == 0);
        t8_app.get_line_l().add(testline);

        MouseEvent rightdragged = new MouseEvent(null,100,MouseEvent.DRAG,0,130,130,PConstants.RIGHT,1);

        assertTrue(t8_app.get_line_l().size() == 1);

        t8_app.mouseDragged(rightdragged);

        t8_app.delay(300);
        assertTrue(t8_app.get_line_l().size() == 0);
    }
    @Test
    public void Ball_collision_with_Line(){
        // testing ball collision with a line object
        App t5_app = setup();
         clear_items_and_images(t5_app);
         t5_app.get_Balls().clear();
         t5_app.get_line_l().clear();
         t5_app.get_holes().clear();
         Ball tball = new Ball(5, 6, 2, 0, "B1");
         Line l = new Line(192, 245);
         t5_app.get_line_l().add(l);
         l.line_seg.add(new float[] {192,245,192,260});
         l.line_seg.add(new float[] {192,260,192,275});
         l.line_seg.add(new float[] {192,275,192,290});
         l.line_seg.add(new float[] {192,290,192,305});
         l.points.clear();
         l.add_points(1, 1);
         l.add_points(1, 1);
         l.update_item(t5_app); // after adding in points we run our update item which removes 1 of the points as they are way too close to each other.
         assertTrue(l.points.size() == 1);
         for (int i = 0; i < 20; i++) { // we are adding velocity to the ball to simulate it moving and then when the collision occurs we break out 
            tball.add_velocity();
            tball.checkCollisionLine(t5_app.get_line_l(), t5_app);
            if (tball.getVX()!=2){
                break;
            }
        }
        
        
        testing_app.delay(200);
        
        // Assert the expected change in X velocity after the collision
        assertEquals(-2f, tball.getVX(), 0.5f);


    }
    @Test
    public void Test_ball_colliding_with_Bwall_blue(){
        // tesing ball collision with a BWall object 
        App Test_app = setup();
        clear_items_and_images(Test_app);
        Test_app.stopdrawing = false;
        Test_app.get_Balls().clear();
        Test_app.get_BWalls().clear();

        Ball testball = new Ball(5,12 , 2, 0, "B2");
        
        Test_app.get_Balls().add(testball);
        BWall testBwall = new BWall(6, 12, "7",1);
        Test_app.add_bwall(testBwall);

        testball.add_velocity();
        testball.add_velocity(); // simulating ball movement
        testball.add_velocity();
        testball.add_velocity();
        testball.add_velocity();
        
        //checking if collision occured
        testball.checkCollision(testBwall, Test_app);
        Test_app.delay(200);
        // checking that the number of hits increased and that the ball is cracked
        assertTrue(testBwall.hits == 2);
        assertTrue(testBwall.cracked);
        Test_app.draw();
        Test_app.delay(100);
        Test_app.stopdrawing = true;
    }
    @Test
    public void test_out_of_bounds_line() {
        // checking that lines are not created when clicking out of bounds
        App t9_app = setup();
        t9_app.get_line_l().clear();
        
        MouseEvent click = new MouseEvent(null,50,MouseEvent.PRESS,0,0,0,PConstants.LEFT,1);
        MouseEvent click2 = new MouseEvent(null,50,MouseEvent.PRESS,0,-1,1000,PConstants.LEFT,1);
        MouseEvent click3 = new MouseEvent(null,50,MouseEvent.PRESS,0,1000,0,PConstants.RIGHT,1);
        t9_app.delay(100);
        t9_app.mousePressed(click);
        t9_app.delay(100);
        t9_app.mousePressed(click2);
        t9_app.delay(100);
        t9_app.mousePressed(click3);
        t9_app.delay(100);
        
        assertTrue(t9_app.get_line_l().size()==0);
    }
    @Test
    public void Test_Bwall_removal(){
        //testing if a BWall object will be removed after being hit a 3rd time
        App Test_app = setup();
        clear_items_and_images(Test_app);
        Test_app.get_Balls().clear();
        Test_app.get_BWalls().clear();
        // creating a ball and Bwall object
        Ball testball = new Ball(5,12 , 2, 0, "B2");
        
        Test_app.get_Balls().add(testball);
        BWall testBwall = new BWall(6, 12, "7",2);
        Test_app.add_bwall(testBwall);
        testball.add_velocity();
        testball.add_velocity(); // simulating ball movement
        testball.add_velocity();
        testball.add_velocity();
        testball.add_velocity();
        
        //checking that ball collided and the Bwall was removed
        testball.checkCollision(testBwall, Test_app);
        assertTrue(testBwall.hits == 3);
        Test_app.delay(100);
        testBwall.update_item(Test_app);
        testball.update_item(Test_app);
        Test_app.delay(100);
        assertTrue(Test_app.get_BWalls().size() == 0);
        
        
        
    }
    
    
    @Test
    public void Test_victory_tiles(){
        // testing if the victory yellow tiles are moving correctly
        App new_tester = setup();
        new_tester.stopdrawing = false;
        new_tester.level_end = true;  
        new_tester.time_up = false;   
        new_tester.frame = 2;             
                  
        new_tester.board_time = 10;       
        new_tester.setGameScore(0);        
        new_tester.P1[0] = 0;
        new_tester.P1[1] = 64;   
        new_tester.P2[0] = 544;
        new_tester.P2[1] = 608;

        // Move P1 right
        
        new_tester.draw();
        assertEquals(32, new_tester.P1[0]); 
        assertEquals(64, new_tester.P1[1]); 
        
        new_tester.level_end = true;  
        new_tester.time_up = false;   
        new_tester.frame = 2;             
                  
        new_tester.board_time = 10;       
        new_tester.setGameScore(0); 
        // move P1 down
        new_tester.P1[0] = App.WIDTH - 32; 
        new_tester.P1[1] = 64; 
        new_tester.draw();
        assertEquals(App.WIDTH - 32, new_tester.P1[0]); 
        assertEquals(64+32, new_tester.P1[1]); 
        
        new_tester.level_end = true;  
        new_tester.time_up = false;   
        new_tester.frame = 2;             
                  
        new_tester.board_time = 10;       
        new_tester.setGameScore(0); 
        //  move P1 left
        new_tester.P1[0] =App.WIDTH - 32; 
        new_tester.P1[1] = App.HEIGHT - 32; 
        new_tester.draw();
        assertEquals(App.WIDTH - 64, new_tester.P1[0]); 
        assertEquals(App.HEIGHT - 32, new_tester.P1[1]); 
        
        new_tester.level_end = true;  
        new_tester.time_up = false;   
        new_tester.frame = 2;             
                  
        new_tester.board_time = 10;       
        new_tester.setGameScore(0); 
        //move P1 up
        new_tester.P1[0] = 0; 
        new_tester.P1[1] = 96; 
        new_tester.draw();
        assertEquals(0, new_tester.P1[0]);
        assertEquals(64, new_tester.P1[1]); 
        
        new_tester.level_end = true;  
        new_tester.time_up = false;   
        new_tester.frame = 2;             
                  
        new_tester.board_time = 10;       
        new_tester.setGameScore(0); 
        //  move P2 left
        new_tester.P2[0] = 544; 
        new_tester.P2[1] = 608; 
        new_tester.draw();
        assertEquals(544-32, new_tester.P2[0]); 
        assertEquals(608, new_tester.P2[1]); 
        
        new_tester.level_end = true;  
        new_tester.time_up = false;   
        new_tester.frame = 2;             
                  
        new_tester.board_time = 10;       
        new_tester.setGameScore(0); 
        // move P2 up
        new_tester.P2[0] = 0; 
        new_tester.P2[1] = 96; 
        new_tester.draw();
        assertEquals(0, new_tester.P2[0]); 
        assertEquals(64, new_tester.P2[1]); 
        
        new_tester.level_end = true;  
        new_tester.time_up = false;   
        new_tester.frame = 2;             
                  
        new_tester.board_time = 10;       
        new_tester.setGameScore(0); 
        // move P2 right
        new_tester.P2[0] = 0; 
        new_tester.P2[1] = 64; 
        new_tester.draw();
        assertEquals(32, new_tester.P2[0]); 
        assertEquals(64, new_tester.P2[1]); 
        
        new_tester.level_end = true;  
        new_tester.time_up = false;   
        new_tester.frame = 2;             
                  
        new_tester.board_time = 10;       
        new_tester.setGameScore(0); 
        // move P2 down
        new_tester.P2[0] = App.WIDTH - 32; 
        new_tester.P2[1] = 64; 
        new_tester.draw();
        assertEquals(App.WIDTH - 32, new_tester.P2[0]); 
        assertEquals(96, new_tester.P2[1]); 

        
    
    }


    public void clear_items_and_images(App app){
        app.get_config_balls().clear();
        
        app.bimage.clear();
        app.ballPos.clear();
        
        

    }



    










    
}


// gradle run						Run the program
// gradle test						Run the testcases

// Please ensure you leave comments in your testcases explaining what the testcase is testing.
// Your mark will be based off the average of branches and instructions code coverage.
// To run the testcases and generate the jacoco code coverage report: 
// gradle test jacocoTestReport
