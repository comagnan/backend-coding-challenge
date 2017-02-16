package tests;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;
import com.univocity.parsers.tsv.*;
import backend.Location;
import backend.SuggestionService;
import javax.ws.rs.core.*;

public class ExplorationTests {
	private TsvParserSettings settings = new TsvParserSettings();
	private TsvParser parser = new TsvParser(settings);
	private SuggestionService testService = new SuggestionService();
	private List<String[]> allRows;

	@Before
	public void setUp() throws Exception {
		allRows = parser.parseAll(Location.class.getResourceAsStream("cities_canada-usa.tsv"));
	}
	
	@Test
	public void readTsvTest() throws Exception {
		for(int i = 1; i < allRows.size(); i++){
			String[] tmpRow = allRows.get(i);
			Location tmpLocation = new Location(Integer.parseInt(tmpRow[0]),
					tmpRow[1],
					tmpRow[2],
					tmpRow[3],
					tmpRow[8],
					tmpRow[10],
					Double.parseDouble(tmpRow[4]),
					Double.parseDouble(tmpRow[5]),
					Integer.parseInt(tmpRow[14]));
			tmpLocation.toString();
		}
	}
	
	@Test
	public void validMinimalSearchTest() throws Exception {
		Response testResponse = testService.getLocations("London", null, null, null);
		String stringResponse = (String) testResponse.getEntity();
		assertEquals(testResponse.getStatus(), 200);
		assertTrue(stringResponse.contains("London, Ontario, CA"));
		assertTrue(stringResponse.contains("New London, CT, US"));
	}
	
	@Test
	public void validIncompleteSearchTest() throws Exception {
		Response testResponse = testService.getLocations("Lond", null, null, null);
		String stringResponse = (String) testResponse.getEntity();
		assertEquals(testResponse.getStatus(), 200);
		assertTrue(stringResponse.contains("London, Ontario, CA"));
		assertTrue(stringResponse.contains("New London, CT, US"));
	}
	
	@Test
	public void validCompleteSearchTest() throws Exception {
		Response testResponse = testService.getLocations("London", "2.0", "3.0", "CA");
		String stringResponse = (String) testResponse.getEntity();
		assertEquals(testResponse.getStatus(), 200);
		assertTrue(stringResponse.contains("London, Ontario, CA"));
		assertFalse(stringResponse.contains("New London, CT, US"));
	}
	
	@Test
	public void missingCoordinatesSearchTest() throws Exception {
		Response testResponse = testService.getLocations("London", "2.0", null, "CA");
		String stringResponse = (String) testResponse.getEntity();
		assertEquals(testResponse.getStatus(), 200);
		assertTrue(stringResponse.contains("London, Ontario, CA"));
		assertFalse(stringResponse.contains("New London, CT, US"));
	}
	
	@Test
	public void invalidCoordinatesSearchTest() throws Exception {
		Response testResponse = testService.getLocations("London", "2.0", "invalid", null);
		String stringResponse = (String) testResponse.getEntity();
		assertEquals(testResponse.getStatus(), 200);
		assertTrue(stringResponse.contains("London, Ontario, CA"));
		assertTrue(stringResponse.contains("New London, CT, US"));
	}
	
	@Test
	public void emptySearchTest() throws Exception {
		Response testResponse = testService.getLocations(null, null, null, null);
		String stringResponse = (String) testResponse.getEntity();
		assertEquals(testResponse.getStatus(), 200);
		assertTrue(stringResponse.contains("[]"));
	}
}
