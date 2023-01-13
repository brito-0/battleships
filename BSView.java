/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleships;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Observable;
import java.util.Observer;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;

/**
 *
 * @author Miguel
 */


//    A B C D E F G H I J             A B C D E F G H I J       M - miss
//  9 _ _ _ _ _ _ _ _ _ _           9 _ _ _ H H _ _ _ _ H       H - hit
//  8 _ _ _ _ _ _ _ _ _ _           8 _ _ _ _ _ _ _ _ _ H
//  7 _ _ _ _ _ _ _ _ _ _           7 _ _ _ M _ _ _ M _ H
//  6 _ _ _ _ _ _ _ _ _ _           6 _ _ _ M _ _ _ _ _ H
//  5 _ _ _ _ _ _ _ _ _ _           5 _ _ _ _ _ H _ _ _ H
//  4 _ _ _ _ _ _ _ _ _ _           4 _ H _ _ _ H _ _ _ _
//  3 _ _ _ _ _ _ _ _ _ _           3 _ H _ _ _ H _ _ _ _
//  2 _ _ _ _ _ _ _ _ _ _           2 _ _ _ _ H H H H _ _
//  1 _ _ _ _ _ _ _ _ _ _           1 _ M _ M _ _ _ _ M _
//  0 _ _ _ _ _ _ _ _ _ _           0 _ _ _ _ _ _ _ _ _ _

public class BSView extends Application implements Observer{
    private BSModel model;
    private BSController controller;
    private Canvas canvas;
    
    private boolean gameF = false;
    
    private Timer timer = new Timer();
    
    private Text t = new Text();
    
    private static final int WINDOW_WIDTH = 252;
    private static final int WINDOW_HEIGHT = 300;
    
    private List<Rectangle> rect = new ArrayList<>();
    
    private Rectangle testRect[][] = new Rectangle[10][10];
    
    //private GraphicsContext gc;
    
    
    @Override
    public void start(Stage primaryStage) {
        model = new BSModel();
        controller = new BSController(model);
        controller.setView(this);
        
        if (!model.getIsCLI()) {
        
//        StackPane root = new StackPane();
//        r

        Group root = new Group();
        canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        //gc = canvas.getGraphicsContext2D();
        //drawShapes(gc);
        
//        Rectangle rect = new Rectangle(1, 1, 25, 25);
//        rect.setFill(Color.WHITE);
//        rect.setStroke(Color.BLACK);
//        Rectangle rect1 = new Rectangle(125, 100, 25, 25);
//        rect1.setFill(Color.WHITE);
//        rect1.setStroke(Color.BLACK);
//        Rectangle rect2 = new Rectangle(150, 100, 25, 25);
//        rect2.setFill(Color.WHITE);
//        rect2.setStroke(Color.BLACK);
        
        //MakeGrid();
        MakeTestGridArray();
        
        t.setX(1);
        t.setY(280);
        
        root.getChildren().add(t);
        
        
////////////////////////////////////        for (Rectangle r : rect) {
////////////////////////////////////            r.setFill(Color.WHITE);
////////////////////////////////////            r.setStroke(Color.BLACK);
////////////////////////////////////            
////////////////////////////////////            //r.setOnMouseClicked(me -> r.setFill(Color.BLUE));
////////////////////////////////////            //r.onMouseClickedProperty(changeC(r, Color.BLUE));
////////////////////////////////////            
////////////////////////////////////            r.addEventFilter(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
////////////////////////////////////                if (e.getClickCount() > 0) {
////////////////////////////////////                    r.setFill(Color.BLUE);
////////////////////////////////////                    System.out.println(rect.indexOf(r));
////////////////////////////////////                    //r.removeEventFilter(MouseEvent.MOUSE_CLICKED, e);
////////////////////////////////////                }
////////////////////////////////////            });
////////////////////////////////////        }


        

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                //Rectangle abc = testRect[x][y];
                int tx = x;
                int ty = y;
                testRect[x][y].setFill(Color.WHITE);
                testRect[x][y].setStroke(Color.BLACK);
                
                testRect[x][y].addEventFilter(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
                    if (e.getClickCount() > 0) {
                        //abc.setFill(Color.BLUE);
                        //System.out.println("x: " + tx + " y: " + ty);
                        controller.checkHit(tx, ty);
                        //r.removeEventFilter(MouseEvent.MOUSE_CLICKED, e);
                }
            });
            }
        }



        
        // add 2 squares one blue and one red 
        // these squares will indicate to the player that blue square means miss and red square means hit
        
        
        // store each rectangle in a similar way to the gameBoard
        
//        EventHandler<MouseEvent> eventHandlerMouseC = new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent e) {
//                
//            }
//        }
        
        
//        for (int i = 0; i < rect.size(); i++) {
////            rect.get(i).addEventFilter(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
////                if (e.getClickCount() > 0) {
////                    rect.get(i).setFill(Color.BLUE);
////                    //hangeC(rect.get(i), Color.BLUE);
////                    
////                }
////            });
//        }
        
        //rect.addEventFilter(MouseEvent.MOUSE_CLICKED);
        
        
        
        
        //root.getChildren().addAll(rect);
        
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                root.getChildren().add(testRect[x][y]);
            }
        }
        

        primaryStage.setTitle("battleships");
        primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.show();

        model.addObserver(this);
        update(null, null);
        
        
        
        
//                model = new TLModel();
//
//        controller = new TLController(model);
//        controller.setView(this);
//        GridPane gridPane = makeLightsPane();
//
//        StackPane root = new StackPane();
//        root.getChildren().add(gridPane); 
//
//        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
//
//        primaryStage.setTitle("Traffic Lights");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//
//        model.addObserver(this);
//        update(null, null);
//
//        Platform.runLater(new Runnable(){
//            @Override
//            public void run() {
//                Stage stage = new TLGraphicalView (model);
//            }
//        });
        
        
        
//        model = new BSModel();
//        
//        Button btn = new Button();
//        btn.setText("Say 'Hello World'");
//        btn.setOnAction(new EventHandler<ActionEvent>() {
//            
//            @Override
//            public void handle(ActionEvent event) {
//                System.out.println("Hello World!");
//            }
//        });
//        
//        StackPane root = new StackPane();
//        root.getChildren().add(btn);
//        
//        Scene scene = new Scene(root, 300, 250);
//        
//        primaryStage.setTitle("Hello World!");
//        primaryStage.setScene(scene);
//        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                timer.cancel();
            }
        });
        
        }
    }
    
    // changes the colour displayed on each square depending on what is on the board
    private void changeBoardColour () {
        String[][] tempBoard = model.getBoard();
        
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                //int tx = x;
                //int ty = y;
                if (tempBoard[x][y] == "H") {
                    testRect[x][y].setFill(Color.RED);
                } else if (tempBoard[x][y] == "M") {
                    testRect[x][y].setFill(Color.BLUE);
                } else if (tempBoard[x][y] == "0") {
                    testRect[x][y].setFill(Color.GREEN);
                }
            }
        }
    }
    
    // creates the square grid, which is then displayed - old
    public void MakeGrid() {
        int posX = 1;
        int posY = 1;
        for(int x = 0; x < 10; x++) {
            for(int y = 0; y < 10; y++) {
                rect.add(new Rectangle(posX, posY, 25, 25));
                posX += 25;
            }
            posX = 1;
            posY += 25;
        }
    }
    
    // creates the square grid, which is then displayed
    public void MakeTestGridArray() {
        int posX = 1;
        int posY = 1;
        for(int x = 0; x < 10; x++) {
            for(int y = 0; y < 10; y++) {
                //rect.add(new Rectangle(posX, posY, 25, 25));
                testRect[x][y] = new Rectangle(posX, posY, 25, 25);
                posX += 25;
            }
            posX = 1;
            posY += 25;
        }
    }
    
    // displayes the number of tries that the player needed to finish the game
    public void gameEnd() {
        //System.out.println(model.getTries());
        t.setText("number of tries: " + String.valueOf(model.getTries()));
    }
    
    // displays a message when a ship is sunk
    private void shipSunk() {
        //System.out.println("ship sunk");
        
        t.setText("ship sunk");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                t.setText("");
            }
        }, 1000);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void update(Observable o, Object arg) {
            changeBoardColour();
            if (model.getEnd() && !gameF) { gameEnd(); gameF = true; }
            if (model.checkSunck() && !gameF) { shipSunk(); }
    }
    
}
