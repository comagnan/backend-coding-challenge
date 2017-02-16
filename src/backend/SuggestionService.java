package backend;

import java.util.List; 
import javax.ws.rs.GET; 
import javax.ws.rs.Path; 
import javax.ws.rs.Produces; 
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;  
import javax.ws.rs.core.Response;
import net.sf.json.*;
import backend.LocationFinder;
import backend.Location;

// The suggestions address is the root of this project
@Path("/suggestions")


public class SuggestionService { 
	// Initiate a new location finder object that will read the tsv file.
	private LocationFinder locFinder = new LocationFinder();  
	private String HTTPresponse;
	private List<Location> searchResult;
	
	// Upon receiving a GET request, return suggestion
	@GET
	@Produces(MediaType.APPLICATION_JSON) 
	
	/**
	 * Returns suggestions with scores for city names based on
	 * a partial, or complete, query. The web query can also 
	 * include optional location data, which are used if available
	 * when calculating the score.
	 * 
	 * This class' method always returns, whether or not the 
	 * query matches known cities. If matching cities are found, they
	 * are returned in order of relevance
	 *
	 * @param query a String that potentially matches city names
	 * @param lat   Optional latitude used for comparisons
	 * @param lon   Optional longitude used for comparisons
	 * @return      JSON-formatted suggestions
	 */
	
	public Response getLocations(
			@QueryParam("q") String query,
			@QueryParam("latitude") String lat,
			@QueryParam("longitude") String lon,
			@QueryParam("country") String country){
		// Create JSON containers
		JSONObject containerJSON = new JSONObject();
		JSONArray suggestionsJSON = new JSONArray();

		// Providing suggestions for non-empty queries
		if(query != null && query.length() > 0){
			searchResult = locFinder.getMatchingLocations(query, lat, lon, country);
			
			if(searchResult.size() > 0){
				// Sort by descending population
				searchResult.sort((o1, o2) -> Integer.compare(o2.getPopulation(), o1.getPopulation()));
				int maxPop = searchResult.get(0).getPopulation();
				for(int i = 1; i < searchResult.size(); i++){
					double previousScore = searchResult.get(i).getScore();
					// Larger population means more relevance
					double scoreMultiplier = Math.sqrt(searchResult.get(i).getPopulation())/Math.sqrt(maxPop); 
					searchResult.get(i).setScore(previousScore * scoreMultiplier);
				}
				
				// Sort by descending score
				searchResult.sort((o1, o2) -> Double.compare(o2.getScore(), o1.getScore()));
				
				// Create JSON objects for the six more relevant results
				for(int i = 0; i < Math.min(searchResult.size(), 6); i++){
					JSONObject newEntry = new JSONObject();
					newEntry.put("name", searchResult.get(i).getFormattedName());
					newEntry.put("latitude", searchResult.get(i).getLatitude());
					newEntry.put("longitude", searchResult.get(i).getLongitude());
					newEntry.put("score", Math.round(searchResult.get(i).getScore()*100.0)/100.0);
					suggestionsJSON.add(newEntry);
				}
			}
		}
		
		// Return the suggestion list
		containerJSON.put("suggestions", suggestionsJSON);
		HTTPresponse = containerJSON.toString();
		return Response.status(200).entity(HTTPresponse).build();
   }  
}
