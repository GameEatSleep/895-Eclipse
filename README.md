# Tracer
895

Static Analysis:

* Clone Repository and Open Project "Tracer" in Eclipse 

* Download Maven Dependencies 

* Run as Java Application 

 *****************************************************PRE-REQS**********************************************************************
 
 - Need to have Java and Maven installed
 
 - Download proguard (https://sourceforge.net/projects/proguard/files/) and extract proguard.jar to the working directory
 
 - Download jShrink (http://www.e-t.com/jshrink.html) and extract jshrink.jar to the working directory
 
 - Download ApectJ and extract to the working directory
		- set ASPECTJ_HOME='AspectJ directory'
		- Run the command "%JAVA_HOME%\bin\java" -classpath "%ASPECTJ_HOME%\lib\aspectjtools.jar;%JAVA_HOME%\lib\tools.jar;%CLASSPATH%" -Xmx64M org.aspectj.tools.ajc.Main %1 %2 %3 %4 %5 %6 %7 %8 %9
		- Add aspectjweaver.jar and aspectjrt.jar to the classpath
		
 - If you're running on a Unix machine, you need to install openjfx (sudo apt-get install openjfx)
 
 - Need to set library jar in 'myconfig.pro' pointing to Java_Home\jdk1.8.x_xxx\jre\lib\rt.jar
 
**************************************************************************************************************************************
 

Dynamic Analysis:

- Run the following commands in the repository directory:

* cd jbox2d

* mvn clean install

* cd ..

* ajc -1.8 Azpect.java -outxml -outjar aspect.jar
 
 * java -jar proguard.jar '@myconfig.pro'
 
 * java -jar jshrink.jar jbox2d\jbox2d-testbed\target\jbox2d-testbed-2.3.1-SNAPSHOT.jar -overwrite -o snapshotj.jar -keep org.? -cp jbox2d\jbox2d-library\target\classes;jbox2d\jbox2d-serialization\target\classes
 
* cd original

* mvn clean install 

* mvn exec:java

* cd ../proguard

* mvn clean install 

* mvn exec:java

* cd ../jShrink

* mvn clean install 

* mvn exec:java
