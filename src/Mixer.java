
import java.sql.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.*;
import javax.net.ssl.HttpsURLConnection;
import java.text.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


class Mixer {

	public static void main(String[] args) throws Exception{

		DepositAddress d = new DepositAddress();
		Connection connection = getDbCon();
		String newAddress = d.run(connection);
		
		Statement stm = connection.createStatement();
	
		ArrayList<Wallet> accounts = new ArrayList<Wallet>();
		int tableSize = 0;
		ResultSet rs = stm.executeQuery("SELECT address FROM accounts;");
		
		while(!mixerContains(newAddress)) {
			TimeUnit.SECONDS.sleep(3);
		}
		
		
		while(rs.next()) {
			//query from db
			String arg = rs.getString("address");
			String amount = getBalance(arg); //reduce api calls
			if((Double) round(Double.parseDouble(amount),1) <= 0) continue;
			accounts.add(new Wallet(getBalance(arg), arg));
			sendCoin(arg, "service", amount);
			tableSize++;
		}
		
		Random rand = new Random();
		int offset;
		while(!accounts.isEmpty() && Double.parseDouble(getBalance("service")) > 0) {
			offset = rand.nextInt(tableSize);
			if(accounts.get(offset).getCurrent().compareTo(accounts.get(offset).getTotal()) == 0) {
				tableSize--;
				accounts.remove(offset);
			} else {
				if(accounts.get(offset).getTotal().subtract(accounts.get(offset).getCurrent()).doubleValue() < 0.1) {
					String toIncrement = accounts.get(offset).getTotal().subtract(accounts.get(offset).getCurrent()).toString();
					sendCoin("service", accounts.get(offset).getAddress(), toIncrement);
					accounts.get(offset).incrementBy(toIncrement);
				}else {
					sendCoin("service", accounts.get(offset).getAddress(), "0.1");
					accounts.get(offset).incrementByDime();
				}
			}
			
		}
		
	}
	
	static private Boolean mixerContains(String address) throws IOException, NumberFormatException, JSONException {
		String url = "http://jobcoin.gemini.com/steadfast/api/addresses/"+address;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		JSONObject jasone = new JSONObject(response.toString());
		double balance = Double.parseDouble(jasone.getString("balance"));
		if(balance == 0)
			return false;
		else
			return true;
	}

		
	static private Connection getDbCon() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return null;
		}
		Connection connection = null;
		try {
			connection = DriverManager
			.getConnection("jdbc:mysql://localhost:3306/jobcoin","username", "password");

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;
		}

		if (connection == null) {
			System.out.println("Failed to make connection!");
		}
		return connection;
	}
		
	private static double round (double number, int place) {
			double result = number / place;
			   result = Math.floor(result);
			   result *= place;
			   return result;
		
		}
		
	static private String getBalance(String address) throws Exception{
			String url = "http://jobcoin.gemini.com/steadfast/api/addresses/"+address;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			JSONObject jason = new JSONObject(response.toString());

			return jason.getString("balance");
		}
		
	static private void sendCoin(String sender, String reciever, String amount) throws IOException {
			String url = "http://jobcoin.gemini.com/steadfast/api/transactions";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			String urlParameters = "fromAddress="+sender+"&toAddress="+reciever+"&amount="+amount;

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			
			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

		}

}

	
