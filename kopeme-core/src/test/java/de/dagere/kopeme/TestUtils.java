package de.dagere.kopeme;

import java.io.File;
import java.nio.file.Paths;

import de.dagere.kopeme.datastorage.FolderProvider;

public class TestUtils {

	public static final String KOPEME_DEFAULT_FOLDER = "target/test-classes/.KoPeMe";
	public static final String TEST_ADDITION = "testAddition";

	public static void deleteRecursively(File file) {
		if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			for (File subfile : file.listFiles()) {
				deleteRecursively(subfile);
			}
			file.delete();
		}
	}

	public static void cleanAndSetKoPeMeOutputFolder() {
		FolderProvider.getInstance().setKopemeDefaultFolder(KOPEME_DEFAULT_FOLDER);
		deleteRecursively(Paths.get(KOPEME_DEFAULT_FOLDER).toFile());
	}

	/**
	 * Returns the File for the test with the given canonical test class name and the given name of the test method.
	 * 
	 * @param canonicalName name of the testclass
	 * @param testCaseName name of the test method
	 * @return File containing the test result
	 */
	public static File xmlFileForKoPeMeTest(final String canonicalName, final String testCaseName) {
		return Paths.get(KOPEME_DEFAULT_FOLDER, canonicalName, canonicalName + "." + testCaseName + ".xml").toFile();
	}

}