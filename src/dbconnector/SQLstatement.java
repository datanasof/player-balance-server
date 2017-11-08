package dbconnector;

public class SQLstatement {
	
	public static String urlDB = "jdbc:sqlite:player_history.db";
	public static float defaultBalanceLimit = 1000;
	
	public static String createPlayers = "CREATE table players("
            + "id integer primary key autoincrement,"
            + "username text not null,"
            + "balance_version int not null,"
            + "balance float not null"
            + ")";
	
	public static String createPlayerInfo = "CREATE table player_info("
            + "id integer,"
            + "balancelimit float,"
            + "blacklisted boolean,"    
            + "FOREIGN KEY(id) REFERENCES players(id)"
            + ")";
	
	public static String selectPlayer = "SELECT id, balance_version, balance "
			+ "FROM players "
			+ "WHERE username LIKE ";
	
	public static String selectPlayerInfo = "SELECT balancelimit, blacklisted "
			+ "FROM player_info "
			+ "WHERE id=";
	
	
	

}