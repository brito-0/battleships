package battleships;

import java.util.Observable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Miguel
 */
public class BSModel extends Observable{
    private String gameBoard[][] = new String[10][10];
    private String testBoard[][] = new String[10][10];
    private Ship ships[] = new Ship[5];
    private int tries;
    
    private boolean isEnd = false;
    private boolean isSunk = false;
    
    private boolean cli = false;
    
    private int prevX[] = new int[16];
    private int prevY[] = new int[16];
    
      private List<Integer> pX = new ArrayList<Integer>();
      private List<Integer> pY = new ArrayList<Integer>();
    
    private Random random = new Random();
    
    public BSModel() {
        initialise();
    }
    
    /**
     * 
     * postcondition: 5 ships are created
     */
    public void initialise() {
        
        // set to true to read configuration file
        boolean shipFile = false;
        // set to false to use random ships
        
        // set to true for cli version
        cli = true;
        // set to false for gui version
        
        tries = 0;
        if (shipFile) { setShips(); } else { randShips(); }
        
        // uncomment to get the position of the ships at the biginning of the game
        DisplayMapWithShips();
        
        if (cli) { cliOut(); }
    }
    
    // returns the number of tries the player took
    /**
     * 
     * postcondition: returns tries
     * @return tries
     */
    public int getTries() {
        return tries;
    }
    
    // returns the game board
    /**
     * 
     * postcondition: returns gameboard
     * @return gameboard
     */
    public String[][] getBoard() {
        return gameBoard;
    }
    
    // checks if a ship has been sunk
    /**
     * 
     * postcondition: returns isSunk
     * @return isSink
     */
    public boolean checkSunck() {
        return isSunk;
    }
    
    // runs the cli version of the game
    /**
     * 
     * postcondition: map is displayed and player can play the cli version
     */
    private void cliOut() {
        // sets all of the gameboard's positions to -
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                gameBoard[x][y] = "-";
            }
        }
        int sSize;
        int iX = 0;
        int iY = 0;
        while (!isEnd) {
            sSize = 5;
            // prints the gameboard
            for (int x = 0; x < 10; x++) {
                for (int y = 0; y < 10; y++) {
                    System.out.print(gameBoard[x][y] + " ");
                }
                System.out.print("\n");
            }
            
            // get input from the user on which position thet want to try
            Scanner scan = new Scanner(System.in);
            while (2 > sSize || sSize > 3) {
            System.out.println("position to hit: ");
            String input = scan.next();
            //System.out.println(input);
            sSize = input.length();
            
            // takes the user's input and transforms it into positions
            iX = input.charAt(0) - 65;
            input = input.substring(1);
            iY = Integer.parseInt(input) - 1;
            
            
            if ((iX < 0 || iX > 9 ) || (iY < 0 || iY > 10)) { sSize = 5; }
            }
            // prints the positions
            System.out.println("x: " + iX);
            System.out.println("y: " + iY);
            // checks whether the positions the player input hit a ship
            checkHit(iY, iX);
            // prints ship sunk if a ship is destroyed
            if (isSunk) { System.out.println("#### ship sunk ####"); }
        }
        
        for (int x = 0; x < 10; x++) {
                for (int y = 0; y < 10; y++) {
                    System.out.print(gameBoard[x][y] + " ");
                }
                System.out.print("\n");
            }
        System.out.println ("\nnumber of tries: " + tries + "\n");
        System.exit(0);
    }
    
    // checks if a certain positions is a hit
    // anc changes the board accordingly
    /**
     * 
     * @param rowN x position to check
     * @param colN y position to check
     * precondition: rowN and colN are values from 0 to 9
     * postcondition: returns hit
     * @return hit
     */
    public boolean checkHit(int rowN, int colN) {
        tries++;
        isSunk = false;
        
        boolean hit = false;
        outer: for (Ship s : ships) {
            for (int i = 0; i < s.getSize(); i++) {
                if (rowN == s.getPosX()[i] && colN == s.getPosY()[i]) {
                    hit = true; 
                    s.incTHits(); 
                    if (s.getDestroyed()) { isSunk = true; } 
                    break outer;
                }
            }
        }
        if (hit) {
            gameBoard[rowN][colN] = "H";
        } else {
            gameBoard[rowN][colN] = "M";
        }
        
        endGame();
        
        setChanged();
        notifyObservers();
        return hit;
    }
    
    
    // format:  size | dir | starting pos
    // check if format is correct if it is try to place ships 
    // if there are errors in the file give an error message
    
    // 0 - right
    // 1 - left
    // 2 - bellow
    // 3 - above
    
    // compare the ship sizes to an ordered list to see if the correct sizes were inserted
    /**
     * 
     * postcondition: ships are created based on the file
     * @exception  FileNotFoundException
     */
    private void setShips(){
        int linesFile = 0;
        boolean cSizes = false;
        
        //remove
        //System.out.println("reading file...");
        
        try {
            // file that is going to be read
            File textFile = new File("ShipConfiguration.txt");
            Scanner scan = new Scanner(textFile);
            
            // ship sizes to compare to the ones inside the file
            List<Integer> expectedShipSizes = new ArrayList<Integer>();
            expectedShipSizes.add(5); expectedShipSizes.add(4); expectedShipSizes.add(3); expectedShipSizes.add(2); expectedShipSizes.add(2);
            // list of ship sizes inside the file
            List<Integer> shipSizes = new ArrayList<Integer>();
            while (scan.hasNextLine()) {
                shipSizes.add(scan.nextInt());
                scan.nextLine();
                linesFile++;
                assert linesFile <= 5 : "incorrect ship sizes";
                //if (linesFile > 5) { cSizes = true; System.out.println("wrong file format"); break; }
            }
            
            if (shipSizes.containsAll(expectedShipSizes) && !cSizes) {
                // collision checking
                
                scan = new Scanner(textFile);

                for (int i = 0; i < 5; i++) {
                    
                    if(i > 0) { scan.nextLine(); }

                    boolean collision = true;
                    
                    int size = scan.nextInt();
                    int dir = scan.nextInt();
                    int x = scan.nextInt();
                    int y = scan.nextInt();

                    int[] posX = new int[size];
                    int[] posY = new int[size];
                    
                    assert dir >= 0 && dir < 4 : "invalid direction value";
                    
//                    System.out.print(size);
//                    System.out.print(" "+dir);
//                    System.out.print(" "+x);
//                    System.out.print(" "+y);
                    // runs for the first ship, this avoids collision checks on the first one
                    if (i == 0) {
                        // checks that the ship is inside the board
                        switch(dir) {
                        case 0:
                            if (y + (size - 1) < 10) { collision = false;}
                            break;
                        case 1:
                            if (y - (size - 1) >= 0) { collision = false;}
                            break;
                        case 2:
                            if (x + (size - 1) < 10) { collision = false;}
                            break;
                        case 3:
                            if (x - (size - 1) >= 0) { collision = false;}
                            break;
                        }

                        assert !collision : "ship out of bounds";

                        posX[0] = x;
                        posY[0] = y;
                        // gives the ships its position based on the direction set in the file
                        switch(dir) {
                            case 0:
                                for (int t = 1; t < size; t++) {
                                    posX[t] = x;
                                    posY[t] = y + t;
                                }
                                break;
                            case 1:
                                for (int t = 1; t < size; t++) {
                                    posX[t] = x;
                                    posY[t] = y - t;
                                }
                                break;
                            case 2:
                                for (int t = 1; t < size; t++) {
                                    posX[t] = x + t;
                                    posY[t] = y;
                                }
                                break;
                            case 3:
                                for (int t = 1; t < size; t++) {
                                    posX[t] = x - t;
                                    posY[t] = y;
                                }
                                break;
                        }
                    } else {
                        // runs for the following ships, contains collision checks
                        // checks for collisions in the direction set inside the file
                        switch(dir) {
                            case 0:
                                if (y + (size - 1) < 10) { collision = false;}
                                if (!collision) {
                                    outer: for (int t = 0; t < i; t++) { // ships
                                        for (int h = 0; h < ships[t].getSize(); h++) { // size of the ships
                                            for (int u = 0; u < size; u++) { // size of current ship
                                                if (x == ships[t].getPosX()[h] && y + u == ships[t].getPosY()[h]) { collision = true; break outer; } // checks if there is a collision with prior ships
                                            }
                                        }
                                    }
                                }
                                break;
                            case 1:
                                if (y - (size - 1) >= 0) { collision = false;}
                                if (!collision) {
                                    outer: for (int t = 0; t < i; t++) {
                                        for (int h = 0; h < ships[t].getSize(); h++) {
                                            for (int u = 0; u < size; u++) {
                                                if (x == ships[t].getPosX()[h] && y - u == ships[t].getPosY()[h]) { collision = true; break outer; }
                                            }
                                        }
                                    }
                                }
                                break;
                            case 2:
                                if (x + (size - 1) < 10) { collision = false;}
                                if (!collision) {
                                    outer: for (int t = 0; t < i; t++) {
                                        for (int h = 0; h < ships[t].getSize(); h++) {
                                            for (int u = 0; u < size; u++) {
                                                if (x + u == ships[t].getPosX()[h] && y == ships[t].getPosY()[h]) { collision = true; break outer; }
                                            }
                                        }
                                    }
                                }
                                break;
                            case 3:
                                if (x - (size - 1) >= 0) { collision = false;}
                                if (!collision) {
                                    outer: for (int t = 0; t < i; t++) {
                                        for (int h = 0; h < ships[t].getSize(); h++) {
                                            for (int u = 0; u < size; u++) {
                                                if (x - u == ships[t].getPosX()[h] && y == ships[t].getPosY()[h]) { collision = true; break outer; }
                                            }
                                        }
                                    }
                                }
                                break;
                        }
                        
                        assert !collision : "a collision happened or ship out of bounds";
                        
                        posX[0] = x;
                        posY[0] = y;
                        // gives the ships its position based on the direction set in the file    
                        switch(dir) {
                            case 0:
                                for (int t = 1; t < size; t++) {
                                    posX[t] = x;
                                    posY[t] = y + t;
                                }
                                break;
                            case 1:
                                for (int t = 1; t < size; t++) {
                                    posX[t] = x;
                                    posY[t] = y - t;
                                }
                                break;
                            case 2:
                                for (int t = 1; t < size; t++) {
                                    posX[t] = x + t;
                                    posY[t] = y;
                                }
                                break;
                            case 3:
                                for (int t = 1; t < size; t++) {
                                    posX[t] = x - t;
                                    posY[t] = y;
                                }
                                break;
                        }
                        
                    }
                    // creates the ship
                    ships[i] = new Ship(size, posX, posY);
                }
                
            }
            
            // add an exception to check if collision is false
            // if collision is true stop the program and throw an error
            
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("text file error");
            e.printStackTrace();
        }
        
//        if (txtFile != null) {
//            
//        } else { randShips(); }
    }
    
    // 1 - 5
    // 1 - 4
    // 1 - 3
    // 2 - 2
    /**
     * 
     * postcondition: ships are created randomly
     */
    private void randShips() {
        int size = 5;
        boolean coll = true;
        //boolean sColl = true;
        int counter = 0;
        int invS = 0;
        //int invCounter = 0;
        for (int i = 0; i < 5; i++) {
            int[] posX = new int[size];
            int[] posY = new int[size];
            
            int x;
            int y;
            // creates the first ship without collision checking
            if (i == 0) {
                coll = true;
                counter = 0;
                int dir = random.nextInt(4);
                
                x = random.nextInt(10);
                y = random.nextInt(10);
                
                // check if it will be whithin the bounds of the board, cosidering the size and direction of the ship
                while(coll) {
                    switch(dir) {
                    case 0:
                        if (y + (size - 1) < 10) { coll = false;}
                        break;
                    case 1:
                        if (y - (size - 1) >= 0) { coll = false;}
                        break;
                    case 2:
                        if (x + (size - 1) < 10) { coll = false;}
                        break;
                    case 3:
                        if (x - (size - 1) >= 0) { coll = false;}
                        break;
                    }
                    // if there is a collision new values are given to the ship, and it is checked again
                    // the first ship will always get completly new values
                    if (coll) {
                        if (dir < 3) { dir++; } else { dir = 0; }
                        counter++;
                        if (counter > 0) {
                            dir = random.nextInt(4);
                            x = random.nextInt(10);
                            y = random.nextInt(10);
                            counter = 0;
                        }
                    }
                }
                
                posX[0] = x;
                posY[0] = y;
                
                // 0 - right
                // 1 - left
                // 2 - bellow
                // 3 - above
                // gives the ships its position based on the direction set in the file
                switch(dir) {
                    case 0:
                        for (int t = 1; t < size; t++) {
                            posX[t] = x;
                            posY[t] = y + t;
                        }
                        break;
                    case 1:
                        for (int t = 1; t < size; t++) {
                            posX[t] = x;
                            posY[t] = y - t;
                        }
                        break;
                    case 2:
                        for (int t = 1; t < size; t++) {
                            posX[t] = x + t;
                            posY[t] = y;
                        }
                        break;
                    case 3:
                        for (int t = 1; t < size; t++) {
                            posX[t] = x - t;
                            posY[t] = y;
                        }
                        break;
                }
                
                // create a random starting pos x,y
                // create a switch depending on the direction
                // add more x,y depending on the size of the ship
                
                // for the remaining ships collision cheking happens
            } else {
                coll = true;
                counter = 0;
                int dir = random.nextInt(4);
                
                x = random.nextInt(10);
                y = random.nextInt(10);
                
                // check if it will be whithin the bounds of the board
                // check for collision with previous ships
                while(coll) {
                    //invS = i - 1;
                    //invCounter = i - 1;
                    switch(dir) {
                    case 0:
                        if (y + (size - 1) < 10) { coll = false;}
                        if (!coll) {
                            //int c = 0;
                            outer: for (int t = 0; t < i; t++) { // ships
                                for (int h = 0; h < ships[t].getSize(); h++) { // size of the ships
                                    for (int u = 0; u < size; u++) { // size of current ship
                                        if (x == ships[t].getPosX()[h] && y + u == ships[t].getPosY()[h]) { coll = true; break outer; } // checks if there is a collision with prior ships
                                    }
                                }
                            }
                            
                            //
                        }
                        break;
                    case 1:
                        if (y - (size - 1) >= 0) { coll = false;}
                        if (!coll) {
                            //int c = 0;
                            outer: for (int t = 0; t < i; t++) {
                                for (int h = 0; h < ships[t].getSize(); h++) {
                                    for (int u = 0; u < size; u++) {
                                        if (x == ships[t].getPosX()[h] && y - u == ships[t].getPosY()[h]) { coll = true; break outer; }
                                    }
                                }
                            }
                            
//                            for (int u = 0; u < size; u++) {
//                                if (prevX.contains(y - u) || prevY.contains(y - u)) { coll = true; break; }
//                            }
                        }
                        break;
                    case 2:
                        if (x + (size - 1) < 10) { coll = false;}
                        if (!coll) {
                            //int c = 0;
                            outer: for (int t = 0; t < i; t++) {
                                for (int h = 0; h < ships[t].getSize(); h++) {
                                    for (int u = 0; u < size; u++) {
                                        if (x + u == ships[t].getPosX()[h] && y == ships[t].getPosY()[h]) { coll = true; break outer; }
                                    }
                                }
                            }
                            
//                            for (int u = 0; u < size; u++) {
//                                if (prevX.contains(x + u) || prevY.contains(x + u)) { coll = true; break; }
//                            }
                        }
                        break;
                    case 3:
                        if (x - (size - 1) >= 0) { coll = false;}
                        if (!coll) {
                            //int c = 0;
                            outer: for (int t = 0; t < i; t++) {
                                for (int h = 0; h < ships[t].getSize(); h++) {
                                    for (int u = 0; u < size; u++) {
                                        if (x - u == ships[t].getPosX()[h] && y == ships[t].getPosY()[h]) { coll = true; break outer; }
                                    }
                                }
                            }

//                            for (int u = 0; u < size; u++) {
//                                if (prevX.contains(x - u) || prevY.contains(x - u)) { coll = true; break; }
//                            }
                        }
                        break;
                    }
                    // if there is a collision new values are given to the ship, and it is checked again
                    // the ships following the first get their direction increased by 1, up to 3 times
                    // once the counter is bigger than 3 the ship gets completly new values
                    if (coll) {
                        if (dir < 3) { dir++; } else { dir = 0; }
                        counter++;
                        if (counter > 3) {
                            dir = random.nextInt(4);
                            x = random.nextInt(10);
                            y = random.nextInt(10);
                            counter = 0;
                        }
                    }
                }
                
                posX[0] = x;
                posY[0] = y;
                
                // 0 - above
                // 1 - bellow
                // 2 - right
                // 3 - left
                
                // gives the ships its position based on the direction set in the file
                switch(dir) {
                    case 0:
                        for (int t = 1; t < size; t++) {
                            posX[t] = x;
                            posY[t] = y + t;
                        }
                        break;
                    case 1:
                        for (int t = 1; t < size; t++) {
                            posX[t] = x;
                            posY[t] = y - t;
                        }
                        break;
                    case 2:
                        for (int t = 1; t < size; t++) {
                            posX[t] = x + t;
                            posY[t] = y;
                        }
                        break;
                    case 3:
                        for (int t = 1; t < size; t++) {
                            posX[t] = x - t;
                            posY[t] = y;
                        }
                        break;
                }
                
                
                // same as above but check for collision after every position is generated
                // in case of a collision increase or decrease dir by 1 and try again
                // if every dir has been tested try a different starting pos
            }
            
//            for (int m = 0; m < size; m++) {
//                    prevX.add(posX[m]);
//                    prevY.add(posY[m]);
//            }
            // creates the ship
            ships[i] = new Ship(size, posX, posY);
            // decreases the size of the next ship, unless the size is equal to 2
            if (size != 2 ) { size--; }
        }
    }
    
    // returns the ships array
    /**
     * 
     * postcondition: returns ships
     * @return ships
     */
    public Ship[] getShips() { 
        return ships; 
    }
    
    // returns whether the game has ended or not
    /**
     * 
     * postcondition: returns isEnd
     * @return isEnd
     */
    public boolean getEnd() {
        return isEnd;
    }
    
    // returns whether the game is running on the cli version
    /**
     * 
     * postcondition: returns cli
     * @return cli
     */
    public boolean getIsCLI() { 
        return cli;
    }
    
    // checks how many ships have been destroyed
    // if the number is equal to the total number of ships
    // isEnd is set to true and the whole board is changed to 0
    /**
     * 
     * postcondition: checks if the game is over, and if it is chnages the gameboard
     */
    public void endGame() {
        boolean end = false;
        int desCounter = 0;
        for (Ship s : ships) {
            if (s.getDestroyed()) { desCounter++; }
        }
        if (desCounter == ships.length) { end = true; } 
        if (end) {
            isEnd = true;
            for (int x = 0; x < 10; x++) {
                for (int y = 0; y < 10; y++) {
                    gameBoard[x][y] = "0";
                }
            }
        }
//        setChanged();
//        notifyObservers();
    }
    
    // prints the positions of the ships
    /**
     * 
     * postcondition: prints the ships positions
     */
    private void DisplayShipPos() {
        for(Ship s : ships) {
            System.out.print("\n");
            for (int i = 0; i < s.getSize(); i++) {
                System.out.print("x: " + s.getPosX()[i] + "\t");
                System.out.print("y: " + s.getPosY()[i]);
                System.out.print("\n");
            }
        }
        System.out.print("\n");
    }
    
    // displays the positions of the ship in a map similar to the cli version
    /**
     * 
     * postcondition: prints the gameboard with the ships
     */
    private void DisplayMapWithShips() {
        System.out.println("0000000000000000000");
        
        for (Ship s : ships) {
            for (int i = 0; i < s.getSize(); i++) {
                pX.add(s.getPosX()[i]);
                pY.add(s.getPosY()[i]);
            }
        }
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                testBoard[x][y] = "-";
            }
        }
        for (int o = 0; o < pX.size(); o++) {
            testBoard[pX.get(o)][pY.get(o)] = "0";
        }
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                System.out.print(testBoard[x][y] + " ");
            }
            System.out.print("\n");
        }
        System.out.println("0000000000000000000");
    }
}

class Ship {
    private int size;
    private int posX[] = new int[size]; 
    private int posY[] = new int[size];
    private boolean isHit[] = new boolean[size]; 
    private int tHits;
    private boolean destroyed = false;
    
    public Ship(int size, int posX[], int posY[]) {
        this.size = size;
        this.posX = posX;
        this.posY = posY;
        destroyed = false;
        tHits = 0;
        for(int i = 0; i < isHit.length; i++) { isHit[i] = false; }
    }
    
    // returns the size of the ship
    /**
     * 
     * postcondition: returns size
     * @return size
     */
    public int getSize() {
        return size;
    }
    
    // returns the x positions of the ship
    /**
     * 
     * postcondition: returns posX
     * @return posX
     */
    public int[] getPosX() {
        return posX;
    }
    
    // returns the y positions of the ship
    /**
     * 
     * postcondition: returns posY
     * @return posY
     */
    public int[] getPosY() {
        return posY;
    }
    
    // returns the position where the ship has been hit
    /**
     * 
     * postcondition: returns isHit
     * @return isHit
     */
    public boolean[] getIsHit() {
        return isHit;
    }
    
    // increments the total hits on the ship
    // also sets destroyed to true if tHits is larger or equal to the size of the ship
    /**
     * 
     * postcondition: increments tHits, and sets the ship as destroyed if isHit is equal or larger than size
     */
    public void incTHits() {
        tHits++;
        if (tHits >= size) { destroyed = true; }
    }
    
    // sets a position of the ship as hit
    /**
     * 
     * @param i ship position to hit
     * @param b Boolean value to set
     * precondition: i is set to a value smaller than the length of isHit, and b is set to either true or false
     * postcondition: value of isHit is changed for the index i
     */
    public void setIsHit(int i, boolean b) {
        if (i < isHit.length) {
            //isHit[i] = true;
            isHit[i] = b;
        }
    }
    
    // returns whether the ship is destroyed or not
    /**
     * 
     * postcondition: returns destroyed
     * @return destroyed
     */
    public boolean getDestroyed() {
        return destroyed;
    }
}
