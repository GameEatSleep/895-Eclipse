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
		initConfiguration();

		try {

			String input = System.getProperty("user.dir")+File.separator+ "testFiles";
//			String input = "C:/Users/maanni/Desktop/testFiles";
			File f = new File(input);
			if (f.exists() && f.isDirectory()) {
				System.out.println("-----------------------\n");

				System.out.println("*******Analyzing*********");
				System.out.println("-----------------------\n");

				ObfuscationAnalysis oa = new ObfuscationAnalysis(input);
				oa.analyze();

				System.out.println("*******Done*********");
				System.exit(0);
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
	private static void initConfiguration() throws URISyntaxException {
		File jarFile = getJarFileDetails();
		String absolutePath = jarFile.getPath();
		String pathToJar = absolutePath.
				substring(0,absolutePath.lastIndexOf(File.separator));
		String folderOfJar = pathToJar.
				substring(0,pathToJar.lastIndexOf(File.separator));
		String jarFileName = jarFile.getName();
		jarFileName  = jarFileName.substring(0,jarFileName.length()-4);
		String folderOfLibFiles = jarFileName+"_lib";
//		String folderOfLibFiles = "C:/Users/maanni/Desktop/c_lib";
		String agent_path = folderOfJar + File.separator + folderOfLibFiles;
//		String agent_path = folderOfLibFiles;
		//verify that this folder exists. If it is not present, we cannot continue.
		File f = new File(agent_path);
		if (f.exists() && f.isDirectory()) {
			System.setProperty( "AGENT_PATH", agent_path);
			
			boolean ready = AspectJUtils.isAspectJAgentLoaded();
			if (!ready) {
				System.out.println("Problem loading the AspectJ agent - exiting");
				System.exit(0);
			}
		} else {
			System.out.println("lib folder was not detected. Please ensure that the library files are present alongside this application");
			System.exit(0);
		}


	}

	/**
	 * Finds out where the present jar is running from. If the accompanying lib folder is present, we can continue.
	 * @return the folder where we are running this jar
	 * @throws URISyntaxException
	 */
	private static File getJarFileDetails() throws URISyntaxException {
		File dir = new File(Driver.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		String jar = System.getProperty("java.class.path");
		File jarFile = new File(dir, jar);
		return jarFile;
	}
}
