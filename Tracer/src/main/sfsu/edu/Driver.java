package main.sfsu.edu;

import java.io.File;
import java.net.URISyntaxException;

public class Driver {

	/**
	 * Main entry point. Used to get the base "root" folder for all incoming files, and to initialize the configuration (path to jar files, etc).
	 * @param args
	 * @throws URISyntaxException
	 */
	public static void main(String[] args) throws URISyntaxException {
		//initConfiguration();

		try {
			String input = System.getProperty("user.dir")+File.separator+".."+File.separator+ "testFiles"+File.separator+ "src";
			File f = new File(input);
			if (f.exists() && f.isDirectory()) {
				System.out.println("-----------------------\n");

				System.out.println("*******Analyzing*********");
				System.out.println("-----------------------\n");

				ObfuscationAnalysis oa = new ObfuscationAnalysis(input);
				oa.analyze();

				System.out.println("*******Done*********");
			}
			else {
				System.out.println("No testFiles folder found! - exiting");
				System.exit(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Initializes the configuration for this comparison tool. The main job here is to track down the lib folder, set them to AGENT_PATH and start the 
	 * AspectJ loadtime-weaving components.
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private static void initConfiguration() throws URISyntaxException {
		//AspectJUtils.isAspectJAgentLoaded();
	}

	/**
	 * Finds out where the present jar is running from. If the accompanying lib folder is present, we can continue.
	 * @return the folder where we are running this jar.
	 * 
	 * Now that we are running this from eclipse, and not an executable JAR file, this is no longer necessary
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private static File getJarFileDetails() throws URISyntaxException {
		File dir = new File(Driver.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		String jar = System.getProperty("java.class.path");
		File jarFile = new File(dir, jar);
		return jarFile;
	}
}
