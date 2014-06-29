/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import stock.Stock;
import stock.StockControl;

/**
 *
 * @author Jingchuan Chen
 */
public class UserControl {
    private static HashMap<String, Integer> stocks = new HashMap<String, Integer>();
    private static double balance = 1000;
    
    public static double getBalance(String uid) {
    	if(checkUserExistance(uid))
    	{
    	UserStorageAccess xmlAccessor = new UserStorageAccess();
        return xmlAccessor.getUserBalance(uid);
    	}else
    	{
    		return 0;
    	}
    }
    
    public static void setBalance(String uid, double bal) {
        balance = bal;
    }
    
    public static synchronized HashMap<String, Integer> getUserStock(String uid) {
    	UserStorageAccess xmlAccessor = new UserStorageAccess();
    	stocks = xmlAccessor.getUserStocks(uid);
    	//System.out.println("size of Hashmap : " + stocks.size());
    	//System.out.println(stocks.containsKey("CCR"));
        return stocks;
    }
    
    public static boolean checkUserExistance(String uid)
    {
    	UserStorageAccess xmlAccessor = new UserStorageAccess();
    	if(xmlAccessor.getUserNode(uid)!=null)
    	{
    	return true;
    	}
    	return false;
    }
    
    
    public static void addStock(String uid, String id, int amount) {
    	if(checkUserExistance(uid))
    	{
    	stocks = UserControl.getUserStock(uid);
        if (stocks.containsKey(id)) {
            stocks.put(id, stocks.get(id) + amount);
        } else {
            stocks.put(id, amount);
        }       
        try {
        	
        	Stock currentStock = StockControl.queryStock(id);
            System.out.println(currentStock);
        	UserStorageAccess xmlAccessor = new UserStorageAccess();
			xmlAccessor.removeStock(uid,currentStock .getPrice()*amount,stocks);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	}
    }
    
    public static boolean canSell(String uid, String id, int amount) {
    	if(checkUserExistance(uid))
    	{
    	if(!UserControl.getUserStock(uid).isEmpty())
    	{
        if (stocks.containsKey(id)) {
            if (stocks.get(id) >= amount) {
                return true;
            }
        } 
    	}
    	}
        return false;
    }
    
    public static void sellStock(String uid, String id, int amount) {
    	
    	/* May be add IF loop here with canSell to handle the condition here */
        int currAmount = stocks.get(id);
        UserStorageAccess xmlAccessor = new UserStorageAccess();
        
        Stock currentStock = StockControl.queryStock(id);
        System.out.println(currentStock);
        
        
        if (currAmount == amount) {
            stocks.remove(id);
        } else {
            stocks.put(id, currAmount - amount);
        }
        
        try {
			xmlAccessor.removeStock(uid,currentStock.getPrice()*amount,stocks);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void addUserIfNotExist(String uid) {
        UserStorageAccess usa = new UserStorageAccess();
        usa.addUserIfNotExist(uid);
    }
    
    
    
}
