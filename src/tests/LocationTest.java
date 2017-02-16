package tests;

import backend.Location;
import static org.junit.Assert.*;

import org.junit.Test;

public class LocationTest {

	@Test
	public void validCreationCanada() {
		Location tester = new Location(5881791,
				   "Abbotsford",
				   "Abbotsford",
				   "Abbotsford,YXX,ÐÐ±Ð±Ð¾Ñ‚ÑÑ„Ð¾Ñ€Ð´",
				   "CA",
				   "1",
				   1.00,
				   2.00,
				   128256);
		assertEquals("Name must be valid", "Abbotsford", tester.getName());
		assertTrue("Alt names must be valid", tester.getAltNames().contains("YXX"));
		assertEquals("Country must be valid", "CA", tester.getCountry());
		assertEquals("State must be valid", "Alberta", tester.getState());
		assertEquals("Population must be valid", 128256, tester.getPopulation());
	}
	
	@Test
	public void validCreationUSA() {
		Location tester = new Location(4046255,
				   "Bay Minette",
				   "Bay Minette",
				   "Bay Minette",
				   "US",
				   "AL",
				   1.00,
				   2.00,
				   8044);
		assertEquals("Name must be valid", "Bay Minette", tester.getName());
		assertTrue("Alt names must be valid", tester.getAltNames().contains("Bay Minette"));
		assertEquals("Country must be valid", "US", tester.getCountry());
		assertEquals("State must be valid", "AL", tester.getState());
		assertEquals("Population must be valid", 8044, tester.getPopulation());
	}

	@Test
	public void unexpectedCountry() {
		Location tester = new Location(4046255,
				   "Testville",
				   "Testville",
				   "Testville",
				   "Test",
				   "Test",
				   2.00,
				   3.00,
				   8044);
		assertEquals("Name must be valid", "Testville", tester.getName());
		assertTrue("Alt names must be valid", tester.getAltNames().contains("Testville"));
		assertEquals("Country must be copied", "Test", tester.getCountry());
		assertNull("State must be invalid", tester.getState());
		assertEquals("Population must be valid", 8044, tester.getPopulation());
	}
	
	@Test
	public void invalidProvinceCanada() {
		Location tester = new Location(5881791,
				   "Abbotsford",
				   "Abbotsford",
				   "Abbotsford,YXX,ÐÐ±Ð±Ð¾Ñ‚ÑÑ„Ð¾Ñ€Ð´",
				   "CA",
				   "52",
				   1.00,
				   2.00,
				   128256);
		assertNotNull("Object must be created", tester);
		assertEquals("Name must be valid", "Abbotsford", tester.getName());
		assertNull("State must be invalid", tester.getState());
	}
	
}
