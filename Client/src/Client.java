
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    private static BufferedReader inFromSock;
    private static BufferedReader inFromUser;
    private static PrintWriter outToSock;

    public static void main(String[] args) {
        String input;
        try {
            Socket s = new Socket("localhost", 9090);
            inFromSock = new BufferedReader(new InputStreamReader(s.getInputStream()));
            inFromUser = new BufferedReader(new InputStreamReader(System.in));
            outToSock = new PrintWriter(s.getOutputStream(), true);

            while (true) {
                System.out.print("Input:");
                input = inFromUser.readLine();
                
                
                outToSock.println(input);
                System.out.println("OUT:" + inFromSock.readLine());
                if (input.equals("QUIT")) {
                    break;
                }
                
            }
            
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
