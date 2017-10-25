
public class BinaryToOctal {
	
	/**
	 * Main method
	 * 
	 * @param args Command line arguments
	 */
	public static void main(String args[]) {
		int b = 2;
		ToOctal(b);
		
		
	}
	public static void ToOctal(int b) {
	
		convertBinaryToOctal(b);
	}
	/**
	 * This method converts a binary number to
	 * an octal number.
	 * 
	 * @param b The binary number
	 * @return The octal number
	 */
	public static int convertBinaryToOctal(int b) {
		int o = 0, r=0, j =1 ;
		while(b!=0)
		{
			r = b % 10;
        		o = o + r * j;
       			j = j * 2;
        		b = b / 10;
		}
		return o;
	}

}
