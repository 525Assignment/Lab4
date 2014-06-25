/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package stock;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jingchuan Chen
 */
public class TestMain {
    public static void main(String[] args) {
        
        StockControl.startUpdater();
        
        Stock yahoo = StockControl.queryStock("YHOO");
        System.out.println(yahoo);
        Stock apple = StockControl.queryStock("AAPL");
        System.out.println(apple);
        
        StockControl.setInventory("YHOO", 700);
        yahoo = StockControl.queryStock("YHOO");
        
        
        System.out.println(yahoo);
        
        Stock goog = StockControl.queryStock("GOOG");
        System.out.println(goog);
        
        try {
            Thread.sleep(500000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
