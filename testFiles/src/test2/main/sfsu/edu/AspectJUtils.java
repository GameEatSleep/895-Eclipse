package main.sfsu.edu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.aspectj.weaver.loadtime.Agent;

import com.sun.tools.attach.VirtualMachine;

/**
 * 
 * AspectJUtils.java
 * 
 * A series of helper methods used to initialize and configure the aspectJ
 * library and other tools.
 * 
 * @author anaqvi
 *
 */

public class AspectJUtils {

	/**
	 * Ensures that the loadtime weaving agent is loaded and ready
	 * 
	 * @return true if the weaver agent is ready to go - and false if something
	 *         went wrong
	 */
	public static boolean isAspectJAgentLoaded() {
		try {
			Agent.getInstrumentation();
		} catch (NoClassDefFoundError e) {
			System.out.println("Error loading weaver: " + e);
			e.printStackTrace();
			return false;
		} catch (UnsupportedOperationException e) {
			return dynamicallyLoadAspectJAgent();
		}
		return true;
	}

	/**
	 * Attempts to set up the Virtual Machine with a custom aspectJWeaver
	 * classloader - this is needed to perform load-time weaving.
	 * 
	 * @return true if the agent can be loaded.
	 */
	public static boolean dynamicallyLoadAspectJAgent() {
		String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
		int p = nameOfRunningVM.indexOf('@');
		String pid = nameOfRunningVM.substring(0, p);
		try {
			VirtualMachine vm = VirtualMachine.attach(pid);
			String jarFilePath = System.getProperty("AGENT_PATH").concat("/aspectjweaver.jar");
			vm.loadAgent(jarFilePath);
			vm.detach();
		} catch (Exception e) {
			System.out.println("Failed to dynamically load weaving agent" + e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Performs the actual analysis of the given class via the aspectj weaver,
	 * in order to get the call depth of that class
	 * 
	 * @param klass
	 *            the class to analyze
	 * @param filePath
	 *            the path to that class under analysis
	 * @return the calldepth for the given class under analysis
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static CallDepthAnalysis exec(Class klass, String filePath) throws IOException, InterruptedException {

		String currentDir = System.getProperty("AGENT_PATH");
		int callDepth = 0;
		String callFlow = "";
		// First, check if there is a package declaration
		/**
		 * If the package is not null, we need to add a classpath parameter and
		 * ensure that when we provide the classname, it's prefixed with the
		 * fully qualified packagename. For example, if the input class is
		 * Hello.class, and the package is com.test, we need to adjust the
		 * classpath and change the filename to be run to 'com.test.Hello'
		 */
		String aspectJarPath = currentDir.concat("/aspect.jar");
		String aspectRTPath = currentDir.concat("/aspectjrt.jar");
		String aspectWeaverPath = currentDir.concat("/aspectjweaver.jar");
		ArrayList<String> paramsExecute = new ArrayList<String>();
		paramsExecute.add("java");
		paramsExecute.add("-cp");
		paramsExecute.add(filePath + File.pathSeparator + aspectRTPath + File.pathSeparator + aspectJarPath);
		paramsExecute.add("-javaagent:" + aspectWeaverPath);
		paramsExecute.add(klass.getName());
		ProcessBuilder builderExecute = new ProcessBuilder(paramsExecute);
		Process process = builderExecute.start();

		// get output from the process
		InputStreamReader isr = new InputStreamReader(process.getInputStream());
		BufferedReader in = new BufferedReader(isr);
		String line;
		boolean found = false;
		while ((line = in.readLine()) != null) {
			if (line.contains("Max calldepth for")) {
				found = true;
				// get the number after the colon
				String output = line.substring(line.indexOf(":") + 1).trim();
				// parse that string - break it up on the semicolon
				String[] returnedValues = output.split(";");
				callDepth = Integer.parseInt(returnedValues[0]);
				if (returnedValues.length>1) {
					callFlow = returnedValues[1];
				} else {
					callFlow = "";
				}
				
			}
		}
		if (!found) {
			callDepth = 1;
		}

		process.waitFor();

		in.close();
		isr.close();
		CallDepthAnalysis ao = new CallDepthAnalysis(callDepth, callFlow);
		return ao;
	}

	/**
	 * Given a file, kicks off loadtime weaving and collects the calldepth
	 * 
	 * @param f1
	 *            the file under analysis
	 * @return the calldepth, an integer
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	static CallDepthAnalysis getCallDepth(File f1)
			throws MalformedURLException, ClassNotFoundException, IOException, InterruptedException {
		String filePath = f1.getAbsolutePath().substring(0, f1.getAbsolutePath().lastIndexOf(File.separator));
		String packageName = "";
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f1);
			/* Parse the class */
			ClassParser parser = new ClassParser(fis, "test.class");
			JavaClass javaClass = parser.parse();
			packageName = javaClass.getPackageName();
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			fis.close();
		}
		String pathToBasePackage = FileUtils.getBasePackagePath(filePath, packageName);
		File ammar = new File(pathToBasePackage);
		@SuppressWarnings("deprecation")
		URL url = ammar.toURL();
		URL[] cp = new URL[] { url };
		URLClassLoader urlcl = new URLClassLoader(cp);
		String fileName = FileUtils.getFileNameWithoutExtension(f1);
		if (packageName.length() > 0) {
			// we have to append it to the filename since that means we had a
			// package declaration
			fileName = packageName.concat(".").concat(fileName);
		}
		Class clazz = urlcl.loadClass(fileName);
		urlcl.close();
		return AspectJUtils.exec(clazz, pathToBasePackage);
	}

}
