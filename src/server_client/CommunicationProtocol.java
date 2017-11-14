package server_client;

import player_gameplay.Gameplay;

public class CommunicationProtocol {
	
	Gameplay gameplay = new Gameplay();
   

    public String processInput(String theInput) {
        String theOutput = null;
        try{
        	 String[] toBeProcessed = theInput.split(",");
             String username = toBeProcessed[0];
             int transactionID = Integer.parseInt(toBeProcessed[1]);
             float balanceChange = Float.parseFloat(toBeProcessed[2]);             
             theOutput = gameplay.makeTransaction(username, transactionID, balanceChange).printTransaction();
        
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }	        
                return theOutput;
    }
}