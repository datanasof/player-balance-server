package server_client;

import java.util.List;
import java.util.Random;

import dbconnector.DBhelper;

public class Randomizer {
	private Random randomGenerator;
    private List<String> playerNames;

    @SuppressWarnings("static-access")
	public Randomizer()
    { 
        try {
			playerNames = DBhelper.getInstance().selectPlayersNames();
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
