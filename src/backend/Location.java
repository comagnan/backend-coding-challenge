package backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Hashtable;

public class Location { 
   private int id; 
   private String name; 
   private List<String> alt_names;
   private String country;
   private String state;
   private double latitude;
   private double longitude;
   private int population;
   private double score;
   
   // Hash table of Canadian provinces used with Location objects
   Hashtable<Integer, String> CA_PROVINCES = new Hashtable<Integer, String>() {{
	   put(1, "Alberta");
	   put(2, "British Columbia");
	   put(3, "Manitoba");
	   put(4, "New Brunswick");
	   put(5, "Newfoundland and Labrador");
	   put(7, "Nova Scotia");
	   put(8, "Ontario");
	   put(9, "Prince Edward Island");
	   put(10, "Québec");
	   put(11, "Saskatchewan");
	   put(12, "Yukon");
	   put(13, "Northwest Territories");
	   put(14, "Nunavut");
   }};
    
   /**
	 * Constructor for Location objects which hold data
	 * relevant to a search engine. For the sake of
	 * the challenge, Locations can only be cities.
	 *
	 * @param name         Primary name of the location
	 * @param ascii_name   Primary name of the location using ascii characters only
	 * @param alt_names    Alternative names given to the location
	 * @param country      The city's country. Can only be Canada or United States.
	 * @param state        The city's state or province. Must be converted to String.
	 * @param population   The city's population
	 */
   
   public Location(int id,
		   String name,
		   String ascii_name,
		   String alt_names,
		   String country,
		   String state,
		   double latitude,
		   double longitude,
		   int population){  
      this.id = id; 
      this.name = name;
      if(alt_names != null){
    	  this.alt_names = new ArrayList<String>(Arrays.asList(alt_names.split("\\s*,\\s*")));
      }
      else{
    	  this.alt_names = new ArrayList<String>();
      }
      // Make sure every possible spelling is included in alt_names
      this.alt_names.add(name);
      this.alt_names.add(ascii_name);
      this.country = country;
      this.population = population;
      this.latitude = latitude;
      this.longitude = longitude;

      if(country.equals("CA")){
    	  // Convert number to the proper province name
    	  this.state = CA_PROVINCES.get(Integer.parseInt(state));
      }
      else if(country.equals("US")){
    	  this.state = state;
      }
      else{
    	  // Other countries aren't supported in this challenge
    	  this.state = null;
      }
      this.score = 1;
   }
   
   /**
	 * Returns the Location's name, state or province and country as a String.
	 */
   
   public String getFormattedName() {
	   return String.format("%s, %s, %s", name, state, country);
   }
   
   /**
	 * Returns all the Location's available data as a String. Useful for debugging.
	 */
   
   public String toString(){
	   String locString = String.format("Location #%d \nName: %s, Alt Names: %s, Country: %s, %s, Coordinates: [%f, %f], Population %d",
			   id, name, alt_names, country, state, latitude, longitude, population);
	   return locString;
   }
   
   public int getId() { 
	   return id; 
   }
   
   public String getName() { 
	   return name; 
   }
   
   public String getState() {
	   return state;
   }
   
   public String getCountry() {
	   return country;
   }
   
   public List<String> getAltNames() {
	   return alt_names;
   }
   
   public double getLatitude(){
	   return latitude;
   }
   
   public double getLongitude(){
	   return longitude;
   }

   public int getPopulation(){
	   return population;
   }
   
   public double getScore(){
	   return score;
   }
   
   public void setScore(double score){
	   this.score = score;
   }
} 