/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jingchuan Chen
 */
public class StockQuery {

    private static Stock parseStock(String line) {
        String[] parts = line.split(",");
        if (parts[2].equals("0.00")) {
            System.err.println("Stock " + parts[1] + " does not exist!");
            return null;
        }
        Stock s = new Stock(parts[1].replace("\"", ""),
                parts[0].replace("\"", ""),
                0, Float.parseFloat(parts[2]));

        return s;
    }

    public static Stock QueryOne(String code) {
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;
        Stock s = null;

        String urlStr = "http://finance.yahoo.com/d/quotes.csv?s=" + code + "&f=nsl1";

        try {
            url = new URL(urlStr);
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            line = br.readLine();
            s = parseStock(line);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
            }
        }
        return s;
    }

    public static ArrayList<Stock> QueryMultiple(ArrayList<String> codes) {
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;
        Stock tmp;
        ArrayList<Stock> sList = new ArrayList<>();

        String urlStr = "";
        for (int i = 0; i < codes.size() - 1; i++) {
            urlStr += codes.get(i) + ",";
        }
        urlStr += codes.get(codes.size() - 1);
        urlStr = "http://finance.yahoo.com/d/quotes.csv?s=" + urlStr + "&f=nsl1";

        try {
            url = new URL(urlStr);
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                tmp = parseStock(line);
                if (tmp != null) {
                    sList.add(tmp);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
            }
        }
        return sList;
    }

}
