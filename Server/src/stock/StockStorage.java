/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stock;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Jingchuan Chen
 */
public class StockStorage {

    private static String fileName = "stock.xml";
    private static StockStorage theOne = null;
    private Document doc;

    private StockStorage() {
        try {
            File fXmlFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
        } catch (Exception ex) {
            Logger.getLogger(StockStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Element getStockByIdentifier(String identifier) {
        NodeList nl = doc.getDocumentElement().getElementsByTagName("stock");
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            if (e.getAttribute("identifier").equals(identifier)) {
                return e;
            }
        }
        return null;
    }

    public static StockStorage getInstance() {
        if (theOne == null) {
            theOne = new StockStorage();
        }
        return theOne;
    }

    public Stock query(String identifier) {
        Element e = getStockByIdentifier(identifier);
        if (e == null) {
            return null;
        }
        return new Stock(identifier, e.getAttribute("name"),
                Integer.parseInt(e.getAttribute("inventory")),
                Float.parseFloat(e.getAttribute("price")));
    }
    
    public ArrayList<String> getAllIdentifier() {
        ArrayList<String> result = new ArrayList<>();
        NodeList nl = doc.getDocumentElement().getElementsByTagName("stock");
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            result.add(e.getAttribute("identifier"));
        }
        return result;
    }
    
    public boolean addStock(Stock s) {
        if (s == null) {
            return false;
        }
        
        Element e = getStockByIdentifier(s.getIdentifier());
        if (e != null) {
            System.err.println("Stock " + s.getIdentifier() + " already exist!");
            return false;
        }
        Element newStock = doc.createElement("stock");
        newStock.setAttribute("identifier", s.getIdentifier());
        newStock.setAttribute("name", s.getName());
        newStock.setAttribute("inventory", "1000");
        newStock.setAttribute("price", String.valueOf(s.getPrice()));
        
        Element root = doc.getDocumentElement();
        root.appendChild((Node)newStock);
        save();
        return true;
    }
    
    public void setPriceForMultiple(ArrayList<Stock> sList) {
        HashMap<String, Float> priceMap = new HashMap<>();
        for (Stock s : sList) {
            priceMap.put(s.getIdentifier(), s.getPrice());
        }
        
        NodeList nl = doc.getDocumentElement().getElementsByTagName("stock");
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            String id = e.getAttribute("identifier");
            if (priceMap.containsKey(id)) {
                System.out.println("Seeting " + id + " to " + priceMap.get(id));
                e.setAttribute("price", String.valueOf(priceMap.get(id)));
            }
        }
        save();
    }
    
    public boolean setPrice(String Identifier, float newPrice) {
        if (newPrice < 0) {
            System.err.println("Price cannot be less than 0!");
            return false;
        }
        
        Element e = getStockByIdentifier(Identifier);
        if (e == null) {
            System.err.println("Stock " + Identifier + " does not exist!");
            return false;
        }
        e.setAttribute("price", String.valueOf(newPrice));
        save();
        return true;
    }
    
    public boolean setPrice(Stock s) {
        return setPrice(s.getIdentifier(), s.getPrice());
    }
    
    public boolean setInventory(String identifier, int inventory) {
        if (inventory > 1000 || inventory < 0) {
            System.err.println("Invalid inventory!");
            return false;
        }
        
        Element e = getStockByIdentifier(identifier);
        if (e == null) {
            System.err.println("Stock " + identifier + " does not exist!");
            return false;
        }
        e.setAttribute("inventory", String.valueOf(inventory));
        save();
        return true;
    }
    
    public boolean setInventory(Stock s) {
        return setInventory(s.getIdentifier(), s.getInventory());
    }

    public void save() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileName));
            transformer.transform(source, result);
        } catch (Exception ex) {
            Logger.getLogger(StockStorage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
