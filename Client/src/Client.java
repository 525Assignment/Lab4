import java.io.*;
import java.net.*;

public class StockClient {
    public static void main(String[] args) throws IOException {
        
        if (args.length != 2) {
            System.err.println(
                "Usage: java StockClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
            Socket clientSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        ) {
            BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));

            String fromServer;
            String fromUser = null;
            boolean userValid = false;

            String greeting_msg = "Please indicate your identity by giving your user name";
            String rule = "Please indicate your operation:\n"+"\"A\": Ask for Stock Price\n"+"\"B\": Buy Stock\n"
            +"\"C\": Check Your Account\n"+"\"Q\": Quit\n"+"\"S\": Sell Stock\n";

            while (!userValid) {
                System.out.println(greeting_msg);
                fromUser = stdIn.readLine();
                String [] splitedUser = fromUser.split("\\s+");
                if (splitedUser.length == 1){
                    fromUser = "USER "+fromUser;
                    out.println(fromUser);
                    fromServer = in.readLine();
                    if (fromServer.equals("OK")) {
                        System.out.println("Login Succeeds.\n");
                        userValid = true;
                    }
                } else {
                    System.out.println("A valid username cannot have space in between. Try again.\n");
                }
            }

            while (!fromUser.equalsIgnoreCase("Q")) {
                System.out.println(rule);
                fromUser = stdIn.readLine();
                while (fromUser.length() != 1) {
                    System.out.println("Invalid Input, please try again.");
                    System.out.println(rule);
                    fromUser = stdIn.readLine();
                }

                switch(fromUser.charAt(0)) {
                    case 'a':
                    case 'A': 
                        String msg_query = "Please query the price of the stock by giving the tickername. Quit by typing \"Q\"";
                        boolean queryValid = false;
                        while (!queryValid) {
                            System.out.println(msg_query);
                            fromUser = stdIn.readLine();
                            if (fromUser.equalsIgnoreCase("Q")){
                                fromUser = "X";
                                break;
                            }
                            String [] splitedQuery = fromUser.split("\\s+");
                            if (splitedQuery.length == 1){
                                fromUser = "QUERY "+fromUser;
                                out.println(fromUser);
                                fromServer = in.readLine();
                                String [] splitedResult = fromServer.split("\\s+");
                                if (splitedResult[0].equals("OK")){
                                    System.out.printf("%-15s%-10s%s\n","TICKERNAME","PRICE","Av. SHARE");
                                    System.out.printf("%-15s%-10s%s\n\n",splitedResult[1],splitedResult[2],splitedResult[3]);
                                    queryValid = true;
                                } else {
                                    System.out.println("The TICKERNAME doesn't exist. Try again.");
                                }
                                // System.out.println(fromServer);
                            } else {
                                System.out.println("A valid tickername cannot have space in between. Try again.\n");
                            } 
                        }
                        break;
                    case 'b':
                    case 'B':
                        String msg_buy = "Please buy the stock by giving the tickername and share volume. Quit by typing \"Q\"";
                        boolean buyValid = false;
                        while (!buyValid) {
                            System.out.println(msg_buy);
                            fromUser = stdIn.readLine();
                            if (fromUser.equalsIgnoreCase("Q")){
                                fromUser = "X";
                                break;
                            }
                            String [] splitedBuy = fromUser.split("\\s+");
                            if (splitedBuy.length == 2){
                                if(isPositiveInteger(splitedBuy[1])){
                                    fromUser = "BUY "+splitedBuy[0]+" "+splitedBuy[1];
                                    out.println(fromUser);
                                    fromServer = in.readLine();
                                    if (fromServer.equals("OK")) {
                                        System.out.println("Transaction Approved.\n");
                                        buyValid = true;
                                    } else {    
                                        System.out.println("Transaction Denied.\n");    
                                    }
                                } else {
                                    System.out.println("Share volume should be a positive integer and the maximum is 1000. Try again.\n");
                                }
                            } else {
                                System.out.println("Invalid input. Try again.\n");
                            }
                        } 
                        break;
                    case 'c':
                    case 'C':
                        out.println("CHECK");
                        String msg = "Your account information is shown below:";
                        System.out.println(msg);
                        fromServer = in.readLine();
                        String [] splitedBalance = fromServer.split("\\s+");
                        int i = 0;
                        int count = 1;
                        double balance = Double.parseDouble(splitedBalance[i++]);
                        //balance = round(balance,2);
                        System.out.printf("Balance: $");
                        System.out.printf("%.2f\n", balance);
                        if (splitedBalance.length == 1){
                            System.out.println("You don't have any stock at the moment.\n");
                        }
                        else {
                            System.out.println("Below are the stocks you have:");
                            System.out.printf("%-2s %-14s %s\n","NO.","TICKERNAME","SHARE");
                            for (int j=1; j<splitedBalance.length-1; j++){
                                System.out.printf("%2d. ", count);
                                System.out.printf("%-15s",splitedBalance[j]);
                                j++;
                                System.out.printf("%s\n",splitedBalance[j]);
                                count++;
                            }
                            System.out.println();
                        }
                        // System.out.println(fromServer);
                        break;
                    case 'q':
                    case 'Q': 
                        out.println("QUIT");
                        System.out.println("Bye. Have a good day!");
                        break;
                    case 's':
                    case 'S': 
                        String msg_sell = "Please sell the stock by giving the tickername and share volume. Quit by typing \"Q\"";
                        boolean sellValid = false;
                        while (!sellValid) {
                            System.out.println(msg_sell);
                            fromUser = stdIn.readLine();
                            if (fromUser.equalsIgnoreCase("Q")){
                                fromUser = "X";
                                break;
                            }
                            String [] splitedSell = fromUser.split("\\s+");
                            if (splitedSell.length == 2){
                                if(isPositiveInteger(splitedSell[1])){
                                    fromUser = "SELL "+splitedSell[0]+" "+splitedSell[1];
                                    out.println(fromUser);
                                    fromServer = in.readLine();
                                    if (fromServer.equals("OK")) {
                                        System.out.println("Transaction Approved.\n");
                                        sellValid = true;
                                    } else {    
                                        System.out.println("Transaction Denied.\n");    
                                    }
                                } else {
                                    System.out.println("Share volume should be a positive integer and the maximum is 1000. Try again.\n");
                                }
                            } else {
                                System.out.println("Invalid input. Try again.\n");
                            } 
                        }
                        break;
                    default:
                        System.out.println("Invalid Input, please try again.");
                        break;
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
    }

    public static boolean isPositiveInteger(String s){
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }
        int var = Integer.parseInt(s);
        if (var > 0 && var <= 1000) {
            return true;
        } else {
            return false;
        }
    }

    /*
    private static void queryStock(BufferedReader stdIn, PrintWriter out){
        String msg = "Please query the price of the stock by giving \"QUERY <TICKERNAME>\"";
        boolean queryValid = false;
        while (!queryValid) {
            System.out.println(msg);
            String fromUser = stdIn.readLine();
            String [] splitedUser = fromUser.split("\\s+");
            if (splitedUser[0].equals("QUERY") && splitedUser.length == 2){
                queryValid = true;
                out.println(fromUser);
            } 
        }
    }
    */
    /*
    private static void buyStock(BufferedReader stdIn, PrintWriter out){
        String msg = "Please buy the stock by giving \"BUY <TICKERNAME> <NUM STOCKS>\"";
        boolean buyValid = false;
        while (!buyValid) {
            System.out.println(msg);
            String fromUser = stdIn.readLine();
            String [] splitedUser = fromUser.split("\\s+");
            if (splitedUser[0].equals("BUY") && splitedUser.length == 3){
                buyValid = true;
                out.println(fromUser);
            } 
        }
    }
    */
    /*
    private static void checkBalance(){
        String msg = "Your Balance is shown below";
            System.out.println(msg);
    }
    */
    /*
    private static void sellStock(BufferedReader stdIn, PrintWriter out){
        String msg = "Please sell the stock by giving \"SELL <TICKERNAME> <NUM STOCKS>\"";
        boolean sellValid = false;
        while (!sellValid) {
            System.out.println(msg);
            String fromUser = stdIn.readLine();
            String [] splitedUser = fromUser.split("\\s+");
            if (splitedUser[0].equals("SELL") && splitedUser.length == 3){
                sellValid = true;
                out.println(fromUser);
            } 
        }
    }
    */
}
