/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jingchuan Chen
 */
public class StockControl {

    private static class Updater implements Runnable {
        private int time = 120000;
        
        public Updater(int time) {
            this.time = time;
        }

        public void run() {
            while (true) {
                try {
                    StockControl.doUpdate();
                    Thread.sleep(time);
                } catch (InterruptedException ex) {
                    Logger.getLogger(StockControl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public synchronized static Stock queryStock(String identifier) {
        StockStorage store = StockStorage.getInstance();
        Stock s = store.query(identifier);
        if (s == null) {
            s = StockQuery.QueryOne(identifier);
            store.addStock(s);
            store.save();
        }
        s = store.query(identifier);
        
        return s;
    }

    public synchronized static boolean setInventory(String identifier, int newInventory) {
        StockStorage store = StockStorage.getInstance();
        return store.setInventory(identifier, newInventory);
    }
    
    public static void startUpdater(int time) {
        Thread t = new Thread(new Updater(time));
        t.start();
    }

    private synchronized static void doUpdate() {
        System.out.println("updating");
        StockStorage store = StockStorage.getInstance();
        ArrayList<String> identifierList;
        ArrayList<Stock> stockList;
        HashMap<String, Float> priceMap;
        
        identifierList = store.getAllIdentifier();   
        stockList = StockQuery.QueryMultiple(identifierList);
        store.setPriceForMultiple(stockList);
    }

}
