package mapsProgram;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeSet;

import routePlanner.RoutePlanner;

public class MapsProgram {


	static HashSet<double[]> pairs = new HashSet<double[]>(); 
	static double[] START = new double[2];
	static double[] END = new double[2];
	static double[] Last = new double[2];
	static TreeSet<double[]> ordered;
	static ArrayList<double[]> oList;
	static boolean DEBUGGING = false;
	
	static ArrayList<double[]> toCalc = new ArrayList<double[]>();
	
	public static void main(String[] args) {
		
		Scanner keyb = new Scanner(System.in);  // Create a Scanner object
		
	    System.out.print("Enter starting location: ");
	    String location1 = keyb.nextLine();  // Read user input
	    System.out.println();
	    System.out.print("Enter destination: ");
	    String location2 = keyb.nextLine();  // Read user input
	    System.out.println();
	    System.out.print("Specify units (\"C\" for Celsius or \"F\" for Fahrenheit): ");
	    String units = keyb.nextLine();  // Read user input
	    System.out.println();
	    
	    if(units.toLowerCase().equals("c")) {
	    	units = "metric";
	    } else if(units.toLowerCase().equals("f")){
	    	units = "imperial";
	    } else {
	    	units = "standard";
	    }
	    
		location1 = location1.replace(' ', '+');
		location2 = location2.replace(' ', '+');
		
		
		
		
		try {
			RoutePlanner rp = new RoutePlanner();
			String API_KEY = rp.getKey("MAPS_KEY.txt");
			
			String toBeUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=" + location1 + "&destination=" + location2 + "&key=" + API_KEY;
			URL url = new URL(toBeUrl);
			
			 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("GET");
	            conn.connect();
	          
	            	
	                StringBuilder informationString = new StringBuilder();
	                Scanner scanner = new Scanner(url.openStream());
	                
	                while (scanner.hasNext()) {
	                	String s = scanner.nextLine();
	                    informationString.append(s);
	                    //System.out.println(s);
	                    
	                    
	                    if( s.contains("start_location") || s.contains("start_address") || s.contains("end_address"))
	                    {
	                    	boolean start = false;
	                    	boolean end = false;
	                    	if(s.contains("start_address")){
	                    		start = true;
	                    		scanner.nextLine();
	                    	}
	                    	if(s.contains("end_address")){
	                    		end = true;
	                    		scanner.nextLine();
	                    	}
	                    
	                    	String s1 = scanner.nextLine().trim();
	                    	String s2 = scanner.nextLine().trim();
	                    	
	                    	StringTokenizer st = new StringTokenizer(s1, " : ");
	                    	st.nextToken();
	                    	String toTrim = st.nextToken();
	                    	double lat = Double.parseDouble(toTrim.substring(0, toTrim.length()-1));
	                    	//System.out.println("Lat = " + lat);
	                    	
	                    	StringTokenizer st2 = new StringTokenizer(s2, " : ");
	                    	st2.nextToken();
	                    	String toTrim2 = st2.nextToken();
	                    	double lng = Double.parseDouble(toTrim2.substring(0, toTrim2.length()-1));
	                    	//System.out.println("Lng = " + lng);
	                    	
	                    	double[] cords = new double[] {lat, lng};
	                    	pairs.add(cords);
	                    	if(start)
	                    	{
	                    		START[0] = lat;
	                    		START[1] = lng;
	                    	}if(end)
	                    	{
	                    		END[0] = lat;
	                    		END[1] = lng;
	                    	}
	                    }
	                }
	                //Close the scanner
	                scanner.close();
	             
	                
	         if(DEBUGGING)
	         {
	                System.out.println("START = " + START[0] + " : " + START[1]);
	                System.out.println("END = " + END[0] + " : " + END[1]);
	         }
	                
	                		
	                //Math Calculations
	                
	                minimizePairs();
	                
	                
	                ArrayList<String> result = rp.routePlanner(toCalc, units);
	                
	                for(String x : result)
	                {
	                	System.out.println(x);
	                }
	              
	      

	               

	            
	        } catch (Exception e) {
	            System.out.println(e.toString());;
	        }
		keyb.close(); // closed scanner
	}

	private static void minimizePairs() 
	{
		
		if(START[0] > END[0])
		{
			ordered = new TreeSet<double[]>(new sorterDEC());
		} else { ordered = new TreeSet<double[]>(new sorterINC()); }
		for(double[] x : pairs)
		{
			ordered.add(x);
		}
		
		addMidPoints();
		
		
		for(double[] x : ordered)
		{
			//System.out.println(x[0] + " : " + x[1]);
			oList.add(x);
		}
		
		
		/* System.out.println("\nOrdered");
		for(double[] x : ordered)
		{
			System.out.println(x[0] + " : " + x[1]);
		}
		*/
		
		int totalCities = (int)Math.round(getDistBetween(START, END) / 3);
		
		toCalc = new ArrayList<double[]>();
		toCalc.add(START);
		Last[0] = START[0];
		Last[1] = START[1];

		
		int pointer = 0;
		int lastPointer = 0;
		double error = .3;
		for(int i = 1; i < totalCities; i++)
		{
			error = .3;
		
			while(true)
			{
				int DistanceNeeded = 3;
				double[] temp = oList.get(pointer);
				double dist;
				if((dist = (Math.abs(getDistFromLast(temp) - DistanceNeeded))) < error)
				{
					if(DEBUGGING)
					{
						System.out.println("adding {" + temp[0] + " : " + temp[1] + "} with a dist=" + dist);
					}
					
					toCalc.add(temp);
					Last[0] = temp[0];
					Last[1] = temp[1];
					lastPointer = pointer;
					pointer++;
					break;
				} else 
				{ 
					pointer++;
					if(pointer >= oList.size())
					{
						pointer = lastPointer + 1;
						
						error+=.3;
					}
				}
			}
			
		}
		
		if(getDistanceFromEnd(toCalc.get(toCalc.size()-1)) > 2.5)
		{
			toCalc.add(END);
		}
		
		if(DEBUGGING)
		{
			System.out.println("\n\nFinal List");
			for(double[] x : toCalc)
			{
				System.out.println(x[0] + " : " + x[1]);
			}
		}
		
	}
	
	private static void addMidPoints() {
		oList = new ArrayList<double[]>();
		boolean NotFinished = true;
		while(NotFinished)
		{
			oList.clear();
			for(double[] x : ordered)
			{
				oList.add(x);
			}
			
				NotFinished = false;
				for(int i = 0; i < oList.size() -1; i++)
				{
					double[] temp1 = oList.get(i);
					double[] temp2 = oList.get(i+1);
					if(getDistBetween(temp1, temp2) > .7)
					{
						double[] midPoint = new double[2];
						midPoint[0] = (temp1[0] + temp2[0]) /2;
						midPoint[1] =  (temp1[1] + temp2[1]) / 2;
						ordered.add(midPoint);
						NotFinished = true;
						break;
					}
				}
				
				
		}
		oList.clear();			
	}

	public static double getDistFromLast(double[] x)
	{
		return (Math.sqrt( (Math.pow(Last[0] - x[0], 2)) + Math.pow(Last[1] - x[1], 2)));
	}
	
	public static double getDistBetween(double[] x, double[] y)
	{
		return (Math.sqrt( (Math.pow(y[0] - x[0], 2)) + Math.pow(y[1] - x[1], 2)));
	}
	
	public static double getDistanceFromEnd(double[] x)
	{
		return (Math.sqrt( (Math.pow(END[0] - x[0], 2)) + Math.pow(END[1] - x[1], 2)));
	}
	
	public static double getDistanceFromStart(double[] x)
	{
		return (Math.sqrt( (Math.pow(START[0] - x[0], 2)) + Math.pow(START[1] - x[1], 2)));
	}
}

class sorterINC implements Comparator<double[]>
{
	public static double getDistBetween(double[] x, double[] y)
	{
		return (Math.sqrt( (Math.pow(y[0] - x[0], 2)) + Math.pow(y[1] - x[1], 2)));
	}
	
	@Override
	public int compare(double[] o1, double[] o2) {
		if(o1[0] > o2[0])
			return 1;
		return -1;
				
		
	}
	
}

class sorterDEC implements Comparator<double[]>
{
	public static double getDistBetween(double[] x, double[] y)
	{
		return (Math.sqrt( (Math.pow(y[0] - x[0], 2)) + Math.pow(y[1] - x[1], 2)));
	}
	
	@Override
	public int compare(double[] o1, double[] o2) {
		if(o1[0] > o2[0])
			return -1;
		return 1;
				
		
	}
	
}
