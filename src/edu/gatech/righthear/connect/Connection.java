package edu.gatech.righthear.connect;

/**
 * Singleton class for managing the connection to the bluetooth device.
 * 
 * @author Taylor Wrobel
 *
 */
public class Connection {
	
	private static Connection instance;
	
	// Instantiate singleton instance at class loading
	static{
		instance = new Connection();
	}
	
	public static Connection getInstance(){
		return instance;
	}
	
	private Connection(){
		
	}

}
