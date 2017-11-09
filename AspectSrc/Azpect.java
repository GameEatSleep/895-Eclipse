
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import java.lang.Runtime;
import java.lang.Thread;
import java.util.*;

public aspect Azpect {

	private int callDepth = 0;
	private int maxDepth = 0;
	private StringBuilder methodPath = new StringBuilder();
	List<String> allPaths = new ArrayList<String>();
	private String currentClassName ="";

	@Pointcut("execution(* *(..))")
	public void whatToMatch (){}

	@Pointcut("execution(* Azpect.*(..))")
	public void whatNotToMatch(){}


	@Pointcut ("execution(* *.main(..))")
	protected void startOfMainMethod() {}

	@Pointcut("whatToMatch() && ! whatNotToMatch() &&  !startOfMainMethod()")
	protected void loggingOperation()
	{
	}

	@Before("loggingOperation()")
	public synchronized void logJoinPoint(ProceedingJoinPoint joinPoint) {
		callDepth++;
		maxDepth = Math.max(maxDepth, callDepth);
		String methodArgs = Arrays.toString(joinPoint.getArgs());
		String tId = (Long.toString(Thread.currentThread().getId()));
		String methodName = joinPoint.getSignature().getName();
		String className = joinPoint.getSignature().getDeclaringTypeName();
		methodPath.append(tId).append(methodName).append(methodArgs).append("<").append(className).append(".class>").append("->");
	}

	private synchronized void checkClass(ProceedingJoinPoint joinPoint, String action) {
		String newClass = joinPoint.getSignature().getDeclaringTypeName();
		if (currentClassName.isEmpty()) {
			currentClassName = newClass;
		} else {
			//check if the classname changed
			if (currentClassName.equals(newClass)) {
				// If we are exiting, print the maxDepth, then reset it.
				if (action.equals("Exiting")) {
					if (methodPath.length() > 0) {
						String currentPath = methodPath.substring(0,methodPath.length() -2);
						allPaths.add(currentPath);
						currentPath = "";
					}
					resetCounters();
				}
			}
		}
	}

	private void resetCounters() {

		callDepth = 0;
		methodPath = new StringBuilder();
	}

	@After("loggingOperation()")
	public synchronized void logExitPoint(ProceedingJoinPoint joinPoint) {
		callDepth--;
		checkClass(joinPoint, "Exiting");		
	}	

	//Needed to know when we're leaving.
	@Before("startOfMainMethod()")
	public void logMainMethodStart()
	{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public synchronized void run() { 
				String output = "Max calldepth for " + currentClassName + ":" + maxDepth + ";" + allPaths;
				try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("log.txt"))))
				{
					writer.write(output);
				} 
				catch (IOException ex) {
					System.out.println("An Exception Occured:  " + ex);
				}  
				 
			}
		});
	}
}

