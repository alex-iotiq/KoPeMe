package de.dagere.kopeme.datastorage;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

public class FolderProviderTest {

	private static final String FOLDERPROVIDER = "folderprovider";

	private FolderProvider testable;

	private final String testClasses = "target/test-classes";
	
	@Before
	public void setup(){
		testable = FolderProvider.getInstance();
		testable.setKopemeDefaultFolder(testClasses);
	}
	
	@Test
	public void testDefaultFolderName(){
		assertEquals(System.getenv("HOME") + File.separator + ".KoPeMe" + File.separator, FolderProvider.KOPEME_DEFAULT_FOLDER);
	}
	
	@Test
	public void testFolderForTesting(){
		String filename = "dataprovider";
		assertEquals(testClasses +  File.separator + filename + File.separator, testable.getFolderFor(filename));
	}
	
	@Test
	public void testGetAllPerformanceResults(){
		Collection<File> result = testable.getPerformanceResultFolders(FOLDERPROVIDER + File.separator + "de.test.ExampleTest1");
		assertEquals(2, result.size());
	}
	
	@Test
	public void testGetLatestPerformanceResult(){
		File result = testable.getLastPerformanceResultFolder(FOLDERPROVIDER + File.separator + "de.test.ExampleTest1");
		String expected = testClasses + File.separator + FOLDERPROVIDER + File.separator + "de.test.ExampleTest1" + File.separator + "1432060232";
		assertEquals(expected, result.getPath());
	}
}