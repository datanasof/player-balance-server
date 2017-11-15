package server_client;

import java.io.*;
import java.net.*;
 
public class Client {
    public static void main(String[] args) throws IOException {
         
        String hostName = "localhost"; 
        int portNumber = 5555;
        Randomizer randomizer = new Randomizer();
 
        try (Socket ClientSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(ClientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(ClientSocket.getInputStream()));
        ) {
            //BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;
 
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("Bye"))
                    break;
                String input = randomizer.getName()+","+randomizer.getTransactionNumber()+","+randomizer.getBalanceChange(); 
                fromUser = input;  //stdIn.readLine();
                if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
                try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {					
					e.printStackTrace();
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
}
