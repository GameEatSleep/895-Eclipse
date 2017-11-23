import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import java.lang.Runtime;
import java.lang.Thread;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import org.aspectj.lang.Signature;
@Aspect
public class Azpect {


	private StringBuilder sb = new StringBuilder();
	private StringBuilder caller = new StringBuilder();
	private StringBuilder callee = new StringBuilder();
	@Pointcut("call(public * *(..)) && !within(Azpect)")
	public static void publicMethodCalls() {}
	@Pointcut ("execution(* *.main(..))")  
	protected void startOfMainMethod() {}

	@Before("publicMethodCalls()")
	public void myPointcut(
			JoinPoint thisJoinPoint,
			JoinPoint.EnclosingStaticPart thisEnclosingJoinPointStaticPart
			) 
	{	
		caller.append(thisEnclosingJoinPointStaticPart);
		callee.append(thisJoinPoint);
		String tId = (Long.toString(Thread.currentThread().getId()));
		String callerString  = caller.toString();
		String calleeString  = callee.toString();

		callerString  = callerString.replaceAll("execution", "");
		callerString  = callerString.replaceAll("call", "");
		callerString  = sig(callerString);
		calleeString  = calleeString.replaceAll("execution", "");
		calleeString  = calleeString.replaceAll("call", "");
		calleeString  = sig(calleeString);
		sb.append(tId + "  " + callerString + " ," + tId + "  "  + calleeString + '\n');
		caller.setLength(0);
		callee.setLength(0);


	}

	public String sig(String input){

		if (input == null || input.length() ==0) {
		return null;
		}
		if (input.startsWith("(")) {
			input = input.substring(1);
		}
		if (input.endsWith(")")) {
			input = input.substring(0,input.length() - 1);
		}

		//Get the parameters (bit within brackets)
		int positionOfParamStart = input.indexOf("(");
		int positionOfParamEnd = input.indexOf(")");
		String firstPart = input.substring(0, positionOfParamStart);
		String params = input.substring(positionOfParamStart);
		// break the first part (return type + header) on spaces
		String[] returnAndHeader = firstPart.split(" ");
		String returnType="";
		String methodName="";
		if (returnAndHeader.length == 1) {
			//special case - if there is only one value in the header, then make returnType void
			returnType = "void";
			methodName = returnAndHeader[0].trim();
		} else {
			returnType = returnAndHeader[0].trim();
			methodName = returnAndHeader[1];
		}
		StringBuilder paramString = new StringBuilder();
		boolean isArra = false;
		boolean isClass = false;
		if (returnType.contains("[]")) {
			isArra= true;
			//remove those characters so we can continue without confusion
			returnType = returnType.replace("[]", "");
			}
		if (returnType.contains("/")) { //i.e. callerString fully qualified class
			isClass= true;
			returnType = returnType.replace("/", "");
			}
		returnType=returnType.replaceAll("\\.","/");
		returnType  = returnType.replaceAll("\\bint\\b", "I");
		returnType  = returnType.replaceAll("\\bfloat\\b", "F");
		returnType  = returnType.replaceAll("\\bboolean\\b", "Z");
		returnType  = returnType.replaceAll("\\bvoid\\b", "V");
		returnType  = returnType.replaceAll("\\bchar\\b", "C");
		returnType  = returnType.replaceAll("\\bshort\\b", "returnType");
		returnType  = returnType.replaceAll("\\bbyte\\b", "B");
		returnType  = returnType.replaceAll("\\bdouble\\b", "D");
		returnType  = returnType.replaceAll("\\blong\\b", "J");
		returnType  = returnType.replaceAll("\\bobject\\b", "L");
		returnType  = returnType.replaceAll("\\bString\\b", "Ljava/lang/String");
		returnType  = returnType.replaceAll("\\bBoolean\\b", "Ljava/lang/Boolean");
		returnType  = returnType.replaceAll("\\bFloat\\b", "Ljava/lang/Float");
		returnType  = returnType.replaceAll("\\bInteger\\b", "Ljava/lang/Integer");
		returnType  = returnType.replaceAll("\\bCharacter\\b", "Ljava/lang/Character");
		returnType = returnType.replaceAll("\\bArrayList\\b","java/util/ArrayList");
		returnType = returnType.replaceAll("\\bHashSet\\b","java/util/HashSet");
		returnType = returnType.replaceAll("\\bHashMap\\b","java/util/HashMap");
		returnType = returnType.replaceAll("\\bObject\\b","java/lang/Object");
		returnType = returnType.replaceAll("\\bClass\\b","java/lang/Class");
		if (isArra) {
			returnType = "[" + returnType;
		}
		if (isClass) {
			returnType = returnType + ";";
		}
		
			if (returnType.contains("/")) { //i.e. callerString fully qualified class
				returnType = returnType + ";";
			}
		if (params.startsWith("(")) {
			params = params.substring(1);
		}
		if (params.endsWith(")")) {
			params = params.substring(0,params.length() - 1);
		}

		//now split the params into another array, on space+comma
		String[] splitParams = params.split(",");
		for (String parameters:splitParams) {
			parameters=parameters.trim();
			//is it an arrray (contains [])?
			boolean isArray = false;
			boolean isClas = false;
			if (parameters.contains("[]")) {
				isArray= true;
				//remove those characters so we can continue without confusion
				parameters = parameters.replace("[]", "");
			}
			if (parameters.contains("/")) { //i.e. callerString fully qualified class
				isClas= true;
				parameters = parameters.replace("/", "");
				}
			parameters=parameters.replaceAll("\\.","/");
			parameters  = parameters.replaceAll("\\bint\\b", "I");
			parameters  = parameters.replaceAll("\\bfloat\\b", "F");
			parameters  = parameters.replaceAll("\\bboolean\\b", "Z");
			parameters  = parameters.replaceAll("\\bvoid\\b", "V");
			parameters  = parameters.replaceAll("\\bchar\\b", "C");
			parameters  = parameters.replaceAll("\\bshort\\b", "S");
			parameters  = parameters.replaceAll("\\bbyte\\b", "B");
			parameters  = parameters.replaceAll("\\bdouble\\b", "D");
			parameters  = parameters.replaceAll("\\blong\\b", "J");
			parameters  = parameters.replaceAll("\\bobject\\b", "L");
			parameters  = parameters.replaceAll("\\bString\\b", "Ljava/lang/String");
			parameters  = parameters.replaceAll("\\bBoolean\\b", "Ljava/lang/Boolean");
			parameters  = parameters.replaceAll("\\bFloat\\b", "Ljava/lang/Float");
			parameters  = parameters.replaceAll("\\bInteger\\b", "Ljava/lang/Integer");
			parameters  = parameters.replaceAll("\\bCharacter\\b", "Ljava/lang/Character");
			parameters = parameters.replaceAll("\\bArrayList\\b","java/util/ArrayList");
			parameters = parameters.replaceAll("\\bHashSet\\b","java/util/HashSet");
			parameters = parameters.replaceAll("\\bHashMap\\b","java/util/HashMap");
			parameters = parameters.replaceAll("\\bObject\\b","java/lang/Object");
			parameters = parameters.replaceAll("\\bClass\\b","java/lang/Class");
			if (isArray) {
				parameters = "[" + parameters;
			}
			if (isClas) {
				parameters = parameters + ";";
			}
			if (parameters.contains("/")) { //i.e. callerString fully qualified class
				parameters = parameters + ";";
			}
			paramString.append(parameters);
		}
		String parameterString = paramString.toString();
		parameterString = methodName + ("(") + parameterString + (")") + returnType; 
		return parameterString;

	} 
	//Needed to know when we're leaving.
	@Before("startOfMainMethod()")
	public void logMainMethodStart()
	{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public synchronized void run() { 
				String replacedtext  = sb.toString();


				try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("log.csv"))))
				{
					writer.write(replacedtext);
				} 
				catch (IOException ex) {
					System.out.println("An Exception Occured:  " + ex);
				}  
			}
		});
	}
}