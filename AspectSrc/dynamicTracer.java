import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.EnclosingStaticPart;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import java.io.*;
import java.lang.Thread;
import java.util.*;

@Aspect
public class dynamicTracer{

	private StringBuilder caller = new StringBuilder();
	public BufferedWriter bw = null;
	public boolean flag = false; // To check if we are parsing enclosingJoinPoint
	@Pointcut("call(public * *(..)) && !within(dynamicTracer)")
	public static void publicMethodCalls() {}
	@Pointcut ("execution(* *.main(..))")  
	protected void startOfMainMethod() {}

	@Before("publicMethodCalls()")
	public void myPointcut(
			JoinPoint thisJoinPoint,
			JoinPoint.EnclosingStaticPart ejp
			) 
	{	
		caller.append(ejp);	
		String tId = (Long.toString(Thread.currentThread().getId()));
		String callerString  = caller.toString();
		String calleeString  = sig(thisJoinPoint.getSignature().toLongString());
		flag = true;
		callerString  = callerString.replaceAll("execution", "");
		callerString  = callerString.replaceAll("call", "");
		callerString  = sig(callerString);
		flag = false;
		try {
			bw = new BufferedWriter(new FileWriter("log.csv",true));
			bw.write(tId + "  " + callerString + " ," + tId + "  "  + calleeString + '\n');
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}finally {                      
			if (bw != null) try {
				bw.close();
			} catch (IOException ioe2) {
			}
		}
		caller.setLength(0);
	}

	public String sig(String input){

		StringBuilder paramString = new StringBuilder();
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
		} else if (returnAndHeader.length >= 2){
			returnType = returnAndHeader[(returnAndHeader.length - 2)].trim();
			methodName = returnAndHeader[(returnAndHeader.length - 1)];
		}
		returnType=convert(returnType);
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
			parameters=convert(parameters);
			paramString.append(parameters);
		}
		String parameterString = paramString.toString();
		parameterString = methodName + ("(") + parameterString + (")") + returnType; 
		return parameterString;
	}

	public String convert(String input){

		boolean isArray = false;//is it an arrray (contains [])?
		boolean isClas = false;//is it a Class (contains /)?
		if (input.contains("[]")) {
			isArray= true;
			//remove those characters so we can continue without confusion
			input = input.replace("[]", "");
		}
		if (input.contains("/")) { //i.e. callerString fully qualified class
			isClas= true;
			input = input.replace("/", "");
		}
		input=input.replaceAll("\\.","/");
		input = input.replaceAll("\\bint\\b", "I");
		input = input.replaceAll("\\bfloat\\b", "F");
		input = input.replaceAll("\\bboolean\\b", "Z");
		input = input.replaceAll("\\bvoid\\b", "V");
		input = input.replaceAll("\\bchar\\b", "C");
		input = input.replaceAll("\\bshort\\b", "S");
		input = input.replaceAll("\\bbyte\\b", "B");
		input = input.replaceAll("\\bdouble\\b", "D");
		input = input.replaceAll("\\blong\\b", "J");
		input = input.replaceAll("\\bobject\\b", "L");
		if (flag) {
			input = input.replaceAll("\\bString\\b", "Ljava/lang/String");	
			input = input.replaceAll("\\bStringBuilder\\b", "Ljava/lang/StringBuilder");	
			input = input.replaceAll("\\bStringBuffer\\b", "Ljava/lang/StringBuffer");
			input = input.replaceAll("\\bBoolean\\b", "Ljava/lang/Boolean");
			input = input.replaceAll("\\bFloat\\b", "Ljava/lang/Float");
			input = input.replaceAll("\\bInteger\\b", "Ljava/lang/Integer");
			input = input.replaceAll("\\bCharacter\\b", "Ljava/lang/Character");
			input = input.replaceAll("\\bArrayList\\b","java/util/ArrayList");
			input = input.replaceAll("\\bHashSet\\b","java/util/HashSet");
			input = input.replaceAll("\\bHashMap\\b","java/util/HashMap");
			input = input.replaceAll("\\bObject\\b","java/lang/Object");
			input = input.replaceAll("\\bClass\\b","java/lang/Class");
		}
		if (isArray) {
			input = "[" + input;
		}
		if (isClas) {
			input = input + ";";
		}
		if (input.contains("/")) { //i.e. callerString fully qualified class
			input = input + ";";
		}
		return input;

	}
}