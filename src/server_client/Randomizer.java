package server_client;

import java.util.List;
import java.util.Random;

import dbconnector.Connector;

public class Randomizer {
	private Connector conn = new Connector();
	private Random randomGenerator;
    private List<String> playerNames;

    public Randomizer()
    { 
        try {
			playerNames = conn.selectPlayersNames();
		} catch (ClassNotFoundException e) {			
			e.printStackTrace();
		}
        randomGenerator = new Random();
    }
    
    public String getName() {
        int index = randomGenerator.nextInt(playerNames.size());
        String name = playerNames.get(index);
        return name;
    }

    public int getTransactionNumber(){
        int trn = randomGenerator.nextInt(100);        
        return trn;
    }
    
    public int getBalanceChange(){
        int bch = randomGenerator.nextInt(150);        
        return bch;
    }

}
