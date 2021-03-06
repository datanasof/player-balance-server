package dbconnector;

public class SQLstatement {
	
	public static String urlDB = "jdbc:sqlite:player_history.db";
	public static float defaultBalanceLimit = 100;
	
	public static String createPlayers = "CREATE table players("
            + "id integer primary key autoincrement,"
            + "username text not null,"
            + "balance_version int not null,"
            + "balance float not null"
            + ")";
	
	public static String createTransactions = "CREATE table transactions("
            + "id integer primary key autoincrement,"           
            + "transactionid int not null,"
            + "errorcode int not null,"
            + "balanceversion int not null,"
            + "balancechange float not null,"
            + "balanceafter float not null"
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
	
	public static String selectPlayerNames = "SELECT username "
			+ "FROM players";
	
	public static String selectPlayerInfo = "SELECT balancelimit, blacklisted "
			+ "FROM player_info "
			+ "WHERE id=";
	
	public static String selectTransactions = "SELECT "
			+ "transactionid,"
            + "errorcode,"
            + "balanceversion,"
            + "balancechange,"
            + "balanceafter "
			+ "FROM "
			+ "(SELECT * FROM transactions ORDER BY id DESC LIMIT 1000) "
			+ "ORDER BY id ASC";
		
	public static String addTransaction = "INSERT INTO transactions "
			+ "SELECT "
			+ "? AS id, "			
			+ "? AS transactionid,"
			+ "? AS errorcode,"
			+ "? AS balanceversion,"
			+ "? AS balancechange,"
			+ "? AS balanceafter ";
	
	public static String addAdditionalTransaction = "UNION ALL SELECT "
			+ "?,?,?,?,?,? ";
		
	public static String updatePlayers = "UPDATE players "
			+ "SET "			
			+ "balance_version = ?, "
			+ "balance = ? "
			+ "WHERE players.id = ?";
			
	public static String updatePlayerInfo = "UPDATE player_info "
			+ "SET "	
			+ "balancelimit = ?, "
			+ "blacklisted = ? "
			+ "WHERE player_info.id = ?";
	
	public static String addPlayer = "INSERT INTO players "
			+ "SELECT "
			+ "? AS id, "
			+ "? AS username, "
			+ "? AS balance_version, "
			+ "? AS balance";
	
	public static String addPlayerInfo = "INSERT INTO player_info "
			+ "SELECT "
			+ "? AS id, "	
			+ "? AS balancelimit, "
			+ "? AS blacklisted";
	
}
