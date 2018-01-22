import java.io.*;
import java.util.*;
import java.lang.Thread;
import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.JoinPoint.EnclosingStaticPart;

@Aspect
public class dynamicTracer{

	private static final Map<Object, String> JNI_TYPE = new HashMap<>();
	static
	{
		JNI_TYPE.put(int.class, "I");
		JNI_TYPE.put(long.class, "J");
		JNI_TYPE.put(short.class, "S");
		JNI_TYPE.put(double.class, "D");
		JNI_TYPE.put(float.class, "F");
		JNI_TYPE.put(boolean.class, "Z");
		JNI_TYPE.put(byte.class, "B");
		JNI_TYPE.put(char.class, "C");
		JNI_TYPE.put(void.class, "V");
	}

	public BufferedWriter bw = null;

	//Pointcut for Method Calls Excluding this class(dynamicTracer)
	//to avoid infinite loop.
	@Pointcut("call(* *(..)) && !within(dynamicTracer)")
	public static void MethodCalls() {}

	@Before("MethodCalls()")
	public void logger(	JoinPoint thisJoinPoint, JoinPoint.EnclosingStaticPart ejp ) 
	{	
		if(((JoinPoint.StaticPart)ejp).getSignature() instanceof MethodSignature){
			MethodSignature calleeSignature = (MethodSignature) thisJoinPoint.getSignature();
			Method calleeMethod = calleeSignature.getMethod();
			MethodSignature callerSignature = null;
			callerSignature = (MethodSignature) ((JoinPoint.StaticPart)ejp).getSignature();
			Method callerMethod = callerSignature.getMethod();
			String callerString = getJNITypeSig(callerMethod);
			String tId = (Long.toString(Thread.currentThread().getId()));
			String calleeString  = getJNITypeSig(calleeMethod);
			try {
				bw = new BufferedWriter(new FileWriter("log.csv",true));
				bw.write(tId + "  " + callerString + "  " + callerMethod.getName() + " ," + tId + "  "  + calleeString + "  " + calleeMethod.getName() + '\n');
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}finally {                      
				if (bw != null) try {
					bw.close();
				} catch (IOException ioe2) {
				}
			}
		}
	}

	//Builds JNI signature for a method
	public static final String getJNITypeSig(Method m)
	{
		final StringBuilder sb = new StringBuilder("(");
		for(final Class<?> C : m.getParameterTypes())
		{
			sb.append(convJNIClassType(C));
		}
		sb.append(')').append(convJNIClassType(m.getReturnType()));
		return sb.toString();
	}

	//Convert Class Types to JNI Format
	static String convJNIClassType(Class<?> c)
	{
		if(c.isArray())
		{
			final Class<?> compType = c.getComponentType();
			return '[' + convJNIClassType(compType);
		}
		else if(c.isPrimitive())
		{
			return JNI_TYPE.get(c);
		}
		else
		{
			return 'L' + c.getName().replace('.', '/') + ';';
		}        
	}
}