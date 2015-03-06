package tests;

import server.Log;

public class TestLog {

	public static void main(String[] args) {
		
		Log.init(TestLog.class.getName());
		
		Log.write(Log.WARNING, "ASDASDSDAD");
		Log.write(Log.INFO, "info info info");
		
	}
	
}
