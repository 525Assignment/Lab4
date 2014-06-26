
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Jingchuan Chen
 */
public class Server {

    public static void main(String[] args) throws IOException {
        int portNo = 9090;

        try {
            if (args.length == 1) {
                portNo = Integer.parseInt(args[0]);
            }
        } catch (Exception e) {
            System.err.println("Invalid port number!");
            System.exit(1);
        }

        ServerSocket listener = new ServerSocket(9090);
        try {
            while (true) {
                Socket socket = listener.accept();
                Thread t = new Thread(new ConnectionHandler(socket));
                t.start();
            }
        } finally {
            listener.close();
        }
    }
}
