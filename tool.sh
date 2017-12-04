 #!/bin/bash
 # 895 automation script

 ajc -1.8 AspectSrc/dynamicTracer.java -outjar aspect.jar
 
 cd jbox2d
 
 mvn clean install
 
 cd ..
 
 java -jar proguard.jar '@myconfig.pro'
 
 java -jar jshrink.jar jbox2d/jbox2d-testbed/target/jbox2d-testbed-2.3.1-SNAPSHOT.jar -overwrite -o snapshotj.jar -keep org.? -classpath "<java.home>/lib/rt.jar;jbox2d/jbox2d-library/target/classes;jbox2d/jbox2d-serialization/target/classes"
 
 cd unobfuscated
 
 mvn clean install exec:java
 
 cd ../jShrink
 
 mvn clean install exec:java
 
 cd ../proguard
 
 mvn clean install exec:java
