package player_gameplay;

public class BackupWorker implements Runnable {
	PlayersList players;
	TransactionsList transactions;
	
	public BackupWorker(PlayersList players,TransactionsList transactions){
		this.players = players;
		this.transactions = transactions;
	}
	
	@Override
	public void run() {
		for(Player p: players){
			players.updatePlayerToDB(p);	
			
		}
		transactions.updateToDB();
		System.out.println("Server: updating Database..");
	}
	
	

}
