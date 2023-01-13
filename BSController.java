package battleships;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Miguel
 */
public class BSController {
    private BSModel model;
    private BSView view;
    
    public BSController(BSModel model ){
        this.model = model;
    }
    
    public void setView(BSView view){
        this.view = view;
    }
    
    public void initialise(){
        model.initialise();
    }
    
    public void checkHit(int rowN, int colN) {
    model.checkHit(rowN, colN);
    }
    
    // add endgame ?????
}
