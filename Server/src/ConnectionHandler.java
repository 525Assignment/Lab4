
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import user.UserControl;
import stock.Stock;
import stock.StockControl;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Jingchuan Chen
 */
public class ConnectionHandler implements Runnable {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private String uid;
    private double balance;

    public ConnectionHandler(Socket sock) throws IOException {
        this.socket = sock;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void HandleLogin() throws IOException {
        String line;
        String[] parts;
        while (true) {
            line = in.readLine();
            parts = line.split(" ");
            if (parts[0].equals("USER")) {
                uid = parts[1];
                out.println("OK");
                return;
            } else {
                out.println("INV");
            }
        }
    }

    private boolean handleBuy(String[] parts) {
        if (parts.length < 3) {
            return false;
        }

        String id = parts[1];
        Stock s = StockControl.queryStock(id);
        if (s == null) {
            return false;
        }

        int amount = Integer.parseInt(parts[2]);
        int newInventory = s.getInventory() - amount;
        if (newInventory < 0) {
            return false;
        }

        double balance = UserControl.getBalance(uid);
        double newBal = balance - s.getPrice() * amount;
        if (newBal < 0) {
            return false;
        }

        StockControl.setInventory(id, newInventory);
        UserControl.addStock(uid, id, amount);
        UserControl.setBalance(uid, newBal);

        return true;
    }

    private boolean handleSell(String[] parts) {
        if (parts.length < 3) {
            return false;
        }

        String id = parts[1];
        Stock s = StockControl.queryStock(id);
        if (s == null) {
            return false;
        }
        
        int amount;
        try {
            amount = Integer.parseInt(parts[2]);
        } catch (IllegalArgumentException e) {
            return false;
        }
        
        if (UserControl.canSell(uid, id, amount)) {
            UserControl.sellStock(uid, id, amount);
            
            double balance = UserControl.getBalance(uid);
            balance += s.getPrice() * amount;
            UserControl.setBalance(uid, balance);
            
            int newInventory = s.getInventory() + amount;
            StockControl.setInventory(id, newInventory);
        }
        return true;
    }

    @Override
    public void run() {
        String[] parts;
        String str = "";
        String command = "";
        HashMap<String, Integer> userStocks;
        double balance;

        try {
            HandleLogin();

            while (!command.equals("QUIT")) {
                System.out.println("In while");
                parts = in.readLine().split(" ");
                command = parts[0];
                switch (command) {
                    case "CHECK": {
                        System.out.println("CHECK");
                        balance = UserControl.getBalance(uid);
                        userStocks = UserControl.getUserStock(uid);
                        str = String.valueOf(balance);
                        for (String key : userStocks.keySet()) {
                            str += " " + key + " " + userStocks.get(key);
                        }
                        break;
                    }
                    case "QUERY": {
                        System.out.println("query");
                        if (parts.length < 2) {
                            str = "INV";
                        } else {
                            Stock s = StockControl.queryStock(parts[1]);
                            if (s != null) {
                                str = "OK " + s.getIdentifier() + " "
                                        + String.valueOf(s.getPrice()) + " "
                                        + String.valueOf(s.getInventory());
                            } else {
                                str = "INV";
                            }
                        }
                        break;
                    }
                    case "BUY": {
                        System.out.println("BUY");
                        if (handleBuy(parts)) {
                            str = "OK";
                        } else {
                            str = "INV";
                        }
                        break;
                    }
                    case "SELL": {
                        System.out.println("SELL");
                        if (handleSell(parts)) {
                            str = "OK";
                        } else {
                            str = "INV";
                        }
                        break;
                    }
                    case "QUIT": {
                        System.out.println("returning");
                        socket.close();
                        return;
                    }
                    default: {
                        System.out.println("Default");
                        str = "INV";
                    }
                }
                out.println(str);
            }

        } catch (IOException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
