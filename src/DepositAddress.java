import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Random;

import org.json.JSONObject;

public class DepositAddress {
	
	public String run(Connection conn) throws Exception{
	
	Statement stm = conn.createStatement();
	String newAddress = generateString();
	ResultSet rs = stm.executeQuery("SELECT address FROM accounts WHERE address = '"+newAddress+"';");
	
	while(rs.next()) {
		newAddress = generateString();
		rs = stm.executeQuery("SELECT address FROM accounts WHERE address = '"+newAddress+"';");
	}
	stm.executeUpdate("INSERT INTO accounts VALUES ( '" + newAddress + "' , '" + "0" + "')");
	System.out.println(newAddress);
	return newAddress;
	
	}

static private String generateString() {
    String alphabet= "abcdefghijklmnopqrstuvwxyz";
    String s = "";
    Random random = new Random();
    int randomLen = 1+random.nextInt(9);
    for (int i = 0; i < randomLen; i++) {
        char c = alphabet.charAt(random.nextInt(26));
        s+=c;
    }
    return s;
}

}