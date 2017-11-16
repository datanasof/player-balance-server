package server_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import player_gameplay.BackupWorker;

public class Server {
    public static void main(String[] args) throws IOException {
        
        int portNumber = 5555;

        try ( 
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        ) {
        
            String inputLine, outputLine;
            
            // Initiate conversation with client
            CommunicationProtocol comp = new CommunicationProtocol();
            outputLine = comp.processInput(null);
            out.println(outputLine);
            
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            ScheduledFuture<?> future = executor.scheduleAtFixedRate(new BackupWorker(comp.getPlayersList(), comp.getTransactionsList()), 
                 0, 30, TimeUnit.SECONDS); 

            while ((inputLine = in.readLine()) != null) {
                outputLine = comp.processInput(inputLine);
                out.println(outputLine);
                if (outputLine.equals("Bye"))
                    break;
            }
            
            future.cancel(false);
            executor.shutdown();
            
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
    
    
    
}
