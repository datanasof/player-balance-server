package dbconnector;

public class DBhelper extends Connector { 

	  private static DBhelper sInstance;

	  public static synchronized DBhelper getInstance() {
	     
	  
	    if (sInstance == null) {
	      sInstance = new DBhelper();
	    }
	    return sInstance;
	  }
	    
	  
	  private DBhelper() {
	    super();
	  }
	}