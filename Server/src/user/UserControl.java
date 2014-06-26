/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package user;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Jingchuan Chen
 */
public class UserControl {
    private static HashMap<String, Integer> stocks = new HashMap<String, Integer>();
    private static double balance = 1000;
    
    public static double getBalance(String uid) {
        return balance;
    }
    
    public static void setBalance(String uid, double bal) {
        balance = bal;
    }
    
    public static HashMap<String, Integer> getUserStock(String uid) {
        return stocks;
    }
    
    
    public static void addStock(String uid, String id, int amount) {
        if (stocks.containsKey(id)) {
            stocks.put(id, stocks.get(id) + amount);
        } else {
            stocks.put(id, amount);
        }
    }
    
    public static boolean canSell(String uid, String id, int amount) {
        if (stocks.containsKey(id)) {
            if (stocks.get(id) >= amount) {
                return true;
            }
        } 
        
        return false;
    }
    
    public static void sellStock(String uid, String id, int amount) {
        int currAmount = stocks.get(id);
        if (currAmount == amount) {
            stocks.remove(id);
        } else {
            stocks.put(id, currAmount - amount);
        }
    }
    
    
    
}
