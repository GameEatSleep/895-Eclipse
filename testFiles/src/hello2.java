public class hello2 {
	public static void main(String[] args){
		supTool();
	}

	public static void supTool(){
		System.out.println("Running #2!");
		supTool2();
	}
	
	public static void supTool2(){
		System.out.println("sup again!");
	}
}
