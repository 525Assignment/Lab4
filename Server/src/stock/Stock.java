/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package stock;

/**
 *
 * @author Jingchuan Chen
 */
public class Stock {
    
    private String identifier;
    private String name;
    private int inventory;
    private float price;
    
    public Stock(String identifier, String name, int inventory, float price) {
        this.identifier = identifier;
        this.name = name;
        this.inventory = inventory;
        this.price = price;
    }
    
    @Override
    public String toString(){
        String str = this.name + "\n";
        str += "\tIdentifier: " + this.identifier + "\n";
        str += "\tPrice: " + this.price + "\n";
        str += "\tInventory: " + this.inventory + "\n";
        return str;
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the inventory
     */
    public int getInventory() {
        return inventory;
    }

    /**
     * @param inventory the inventory to set
     */
    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    /**
     * @return the price
     */
    public float getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(float price) {
        this.price = price;
    }
    
}
