package routePlanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class RoutePlanner {
	
	public String getKey(String filename) {
		BufferedReader reader;
		String line;
		try {
			reader = new BufferedReader(new FileReader(filename));
			line = reader.readLine();
			reader.close();
			return line;
		} catch (IOException e) {
			return null;
		}
	}
	
	public ArrayList<String> routePlanner(ArrayList<double[]> list, String units){
		ArrayList<String> weatherResults = new ArrayList<String>();
		
		String API_KEY = getKey("OPENWEATHER_KEY.txt");
		if(API_KEY == null) {
			System.out.println("Error: \"OPENWEATHER_KEY.txt\" was not read correctly.");
			return null;
		}
		
		
		String UNITS = units;
		String urlString;
		
		StringBuilder result;
		URL url;
		URLConnection conn;
		BufferedReader rd;
		String line;
		
		String cityName;
		String weather;
		String weatherDesc;
		String temperature;
		
		String fOrC = "";
		if(UNITS.equals("imperial")) {
			fOrC = "F";
		} else if(UNITS.equals("metric")) {
			fOrC = "C";
		}
		
		for(int i = 0; i < list.size(); i++) {
			
			urlString = "https://api.openweathermap.org/data/2.5/forecast?"
			+ "lat=" +  list.get(i)[0]
			+ "&lon=" + list.get(i)[1]
			+ "&appid=" + API_KEY + "&units=" + UNITS
			+ "&cnt=" + Integer.valueOf(i+1).toString();
			//System.out.println(urlString);
			
			try {
				result = new StringBuilder();
				url = new URL(urlString);
				conn = url.openConnection();
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while((line = rd.readLine()) != null) {
					result.append(line);
				}
				rd.close();
				//System.out.println(result);
				
				cityName    = 	result.substring( result.indexOf("\"name\"")+8, 
					(result.substring(result.indexOf("\"name\"")+8)).indexOf("\"") 
					+ result.indexOf("\"name\"")+8 );
			
				weather     = 	result.substring( result.lastIndexOf("\"weather\"")+29, 
					(result.substring(result.lastIndexOf("\"weather\"")+29)).indexOf("\"") 
					+ result.lastIndexOf("\"weather\"")+29 );
			
				weatherDesc = 	result.substring( result.lastIndexOf("\"description\"")+15, 
					(result.substring(result.lastIndexOf("\"description\"")+15)).indexOf("\"") 
					+ result.lastIndexOf("\"description\"")+15 );
				
				temperature = 	result.substring( result.lastIndexOf("\"temp\"")+7, 
					(result.substring(result.lastIndexOf("\"temp\"")+7)).indexOf(",") 
					+ result.lastIndexOf("\"temp\"")+7 );
				
				
				
				weatherResults.add( "In " + i*3 + " hours...\n\t"
									+ "City: " + cityName + "\n\t"
									+ "Temperature: " + temperature + "\u00B0" + fOrC + "\n\t"
									+ "Weather: " + weather + " (" + weatherDesc + ")\n\n");
				
			} catch (IOException e) {
				weatherResults.clear();
				System.out.println("Error: Could not establish input stream from URLConnection.");
				return null;
			}
			
		}
		return weatherResults;
	}
}
