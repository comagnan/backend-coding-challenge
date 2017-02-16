package backend;

import java.util.ArrayList; 
import java.util.List;  
import com.univocity.parsers.tsv.*;

public class LocationFinder {
	private TsvParser parser;
	private List<String[]> allRows;
	
	/**
	 * Object that contains information about CA/USA cities read from a tsv file.
	 * It's possible to search its private data using the getMatchingLocations
	 * method.
	 */
	
	public LocationFinder(){
		TsvParserSettings settings = new TsvParserSettings();
		this.parser = new TsvParser(settings);

		// Read bundled tsv file
		try {
			this.allRows = parser.parseAll(Location.class.getResourceAsStream("cities_canada-usa.tsv"));
		}
		
		catch (NullPointerException e){
			e.printStackTrace();
		}
		
		catch (IndexOutOfBoundsException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns locations that match a certain query after searching
	 * in cities_canada-usa.tsv. If positional info is provided,
	 * relevance can be affected and entries can be left out.
	 * 
	 * This method always returns, whether or not the 
	 * query matches known cities. 
	 *
	 * @param query a String that potentially matches city names
	 * @param latitude   Optional latitude used for score calculation
	 * @param longitude   Optional longitude used for calculation
	 * @return  List of matching Location objects.
	 */
	
	public List<Location> getMatchingLocations(String query, String latitude, String longitude, String country){
		List<Location> resultList = new ArrayList<Location>(); 
		for(int i = 1; i < this.allRows.size(); i++)
		{
			String[] currentRow = this.allRows.get(i);
			String name = currentRow[1] + ", " + currentRow[2];
			String alt_names = currentRow[3];
			// Query at least partially contained in names
			if(name.toLowerCase().contains(query.toLowerCase()) || (alt_names != null && alt_names.toLowerCase().contains(query.toLowerCase()))){
				// If country is specified, skip table entries for other countries
				if(country != null && !country.toLowerCase().equals(currentRow[8].toLowerCase())){
					continue;
				}
				// Create new location object
				Location newLocation = new Location(Integer.parseInt(currentRow[0]),
						currentRow[1],
						currentRow[2],
						currentRow[3],
						currentRow[8],
						currentRow[10],
						Double.parseDouble(currentRow[4]),
						Double.parseDouble(currentRow[5]),
						Integer.parseInt(currentRow[14]));
				boolean completeMatch = false;
				for(String str: newLocation.getAltNames()){
					if(str.trim().toLowerCase().equals(query.trim().toLowerCase())){
						completeMatch = true;
						break;
					}
				}
				if(!completeMatch){
					// The longer the query, the more confidence in the final result, up to a maximum multiplier of 0.75.
					double multiplier = Math.min(query.length()/5.0, 0.75);
					newLocation.setScore(newLocation.getScore() * multiplier);
				}
				// If latitude and longitude are given, tailor results according to distance
				if(latitude != null && longitude != null){
					try{
						double queryLat = Double.parseDouble(latitude);
						double queryLon = Double.parseDouble(longitude);
						double distance = getDistanceFromCoordinates(queryLat,
								queryLon,
								newLocation.getLatitude(),
								newLocation.getLongitude());
						// Penalize relevance according to distance to user
						double scoreMultiplier = 1.0 - (Math.sqrt(distance)/Math.sqrt(2.0 * Math.PI * 6371.0));
						newLocation.setScore(newLocation.getScore() * scoreMultiplier);
					}
					catch(NumberFormatException e){
						latitude = null;
						longitude = null;
					}
				}
				resultList.add(newLocation);
			}
		}
		return resultList; 
	}
	
	/**
	 * Calculates an approximate distance between coordinates in kilometers, using
	 * 6371km as the earth's radius. This is used to compare proximity between cities.
	 *
	 * @param lonA   First latitude
	 * @param latA   First longitude
	 * @param lonB   Second latitude
	 * @param latB   Second longitude
	 * @return       Approximated distance between the coordinates, in kilometers.
	 */
	private double getDistanceFromCoordinates(double latA, double lonA, double latB, double lonB){
		double radToDeg = Math.PI/180;
		double arcA = 0.5 - Math.cos((latB - latA) * radToDeg) + 
				Math.cos(latA * radToDeg) * Math.cos(latB * radToDeg) *
				(1 - Math.cos((lonB - lonA) * radToDeg))/2;
		
		return 2.0 * 6371.0 * Math.asin(Math.sqrt(arcA));
	}
}
