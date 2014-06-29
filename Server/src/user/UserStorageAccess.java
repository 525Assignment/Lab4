package user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import stock.StockStorage;

public class UserStorageAccess {

    private static String fileName = "userstock.xml";
    private Document doc;

    public UserStorageAccess() {
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

    public double getUserBalance(String uid) {
        Node userNode = getUserNode(uid);
        if (userNode != null) {
            NamedNodeMap attr = userNode.getAttributes();
            Node nodeAttr = attr.getNamedItem("balance");
            return Double.parseDouble(nodeAttr.getTextContent());
        } else {
            return 0;
        }
    }

    public Node getUserNode(String uid) {
        NodeList userList = doc.getElementsByTagName("user");
        System.out.println(userList.getLength());

        for (int i = 0; i < userList.getLength(); i++) {
            Node node = userList.item(i);
            Element eElement = (Element) node;
            System.out.println(eElement.getAttribute("id") + eElement.getAttribute("balance"));
            if (eElement.getAttribute("id").equals(uid)) {
                return node;
            }
        }

        return null;
    }

    public HashMap<String, Integer> getUserStocks(String uid) {
        HashMap<String, Integer> userStocks = new HashMap<String, Integer>();;
        NodeList userList = doc.getElementsByTagName("user");
        System.out.println(userList.getLength());

        Node user = getUserNode(uid);
        NodeList userNode = user.getChildNodes();
        System.out.println("stock size : " + userNode.getLength());

        for (int temp = 0; temp < userNode.getLength(); temp++) {
            Node stockNode = userNode.item(temp);

            if (stockNode.getNodeType() == Node.ELEMENT_NODE) {
                Element stockElement = (Element) stockNode;
                System.out.println(stockElement.getAttribute("identifier") + stockElement.getAttribute("quantity"));
                userStocks.put(stockElement.getAttribute("identifier"), Integer.parseInt(stockElement.getAttribute("quantity")));
            }
        }

        /* XML code to get the elements and add to Hashmap */
        return userStocks;
    }

    public synchronized void removeStock(String uid, Float increment, HashMap<String, Integer> stocks) throws IOException {
        /* Code to remove the elements from the XML and rebuild the XML and increase the balance */
        try {
            Node userNode = getUserNode(uid);

            NamedNodeMap attr = userNode.getAttributes();
            Node nodeAttr = attr.getNamedItem("balance");

            Double newamount = increment + Double.parseDouble(nodeAttr.getTextContent());
            nodeAttr.setTextContent(newamount.toString());

            while (userNode.hasChildNodes()) {
                userNode.removeChild(userNode.getFirstChild());
            }

            Iterator it = stocks.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                System.out.println(pairs.getKey() + " = " + pairs.getValue());
                //it.remove(); // avoids a ConcurrentModificationException
                Element newStock = doc.createElement("stock");
                newStock.setAttribute("identifier", pairs.getKey().toString());
                newStock.setAttribute("quantity", pairs.getValue().toString());
                userNode.appendChild((Node) newStock);

            }

            commitFile();

        } catch (Exception e) {
            System.out.println("Error in updating");
        }
    }

    public synchronized void addStock(String uid, Float decrement, HashMap<String, Integer> stocks) throws IOException {
        try {
            Node userNode = getUserNode(uid);

            NamedNodeMap attr = userNode.getAttributes();
            Node nodeAttr = attr.getNamedItem("balance");

            Double newamount = Double.parseDouble(nodeAttr.getTextContent()) - decrement;
            nodeAttr.setTextContent(newamount.toString());

            while (userNode.hasChildNodes()) {
                userNode.removeChild(userNode.getFirstChild());
            }

            Iterator it = stocks.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                System.out.println(pairs.getKey() + " = " + pairs.getValue());
                //it.remove(); // avoids a ConcurrentModificationException
                Element newStock = doc.createElement("stock");
                newStock.setAttribute("identifier", pairs.getKey().toString());
                newStock.setAttribute("quantity", pairs.getValue().toString());
                userNode.appendChild((Node) newStock);
            }

            commitFile();

        } catch (Exception e) {
            System.out.println("Error in updating");
        }
    }

    public void addUserIfNotExist(String uid) {
        if (getUserNode(uid) == null) {
            Element newUser = doc.createElement("user");
            newUser.setAttribute("id", uid);
            newUser.setAttribute("balance", "1000");
            Element root = doc.getDocumentElement();
            root.appendChild((Node) newUser);
            
            commitFile();

        }
    }

    public void commitFile() {
        try {
            DOMImplementationLS domImplementationLS
                    = (DOMImplementationLS) doc.getImplementation().getFeature("LS", "3.0");
            LSOutput lsOutput = domImplementationLS.createLSOutput();
            FileOutputStream outputStream = new FileOutputStream("userstock.xml");
            lsOutput.setByteStream((OutputStream) outputStream);
            LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
            lsSerializer.write(doc, lsOutput);
            outputStream.close();
        } catch (Exception e) {
            System.out.println("Error in updating");
        }
    }

}
