package routePlanner;

import java.util.ArrayList;

public class RoutePlannerTester {

	public static void main(String[] args) {
		ArrayList<double[]> test = new ArrayList<>();
		test.add(new double[] {40.86815, -73.42621}); // Huntington, NY
		test.add(new double[] {39.29208, -76.61173}); // Baltimore, MD
		test.add(new double[] {37.55380, -77.46030}); // Richmond, VA
		test.add(new double[] {35.22679, -80.84997}); // Charlotte, NC
		RoutePlanner rp = new RoutePlanner();
		ArrayList<String> result = rp.routePlanner(test, "imperial");
		
		System.out.println(result);
	}

}
