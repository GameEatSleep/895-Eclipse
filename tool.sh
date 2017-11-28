 #!/bin/bash
 # 895 automation script

<<<<<<< HEAD
 ajc -1.8 AspectSrc/dynamicTracer.java -outxml -outjar aspect.jar
=======
 ajc -1.8 AspectSrc/Azpect.java -outxml -outjar aspect.jar
>>>>>>> 9f91d7f5e6d2aaa7410a73d24a4cfa473f65066f
 
 cd jbox2d
 
 mvn clean install
 
 cd ..
 
 java -jar proguard.jar '@myconfig.pro'
 
 java -jar jshrink.jar jbox2d/jbox2d-testbed/target/jbox2d-testbed-2.3.1-SNAPSHOT.jar -overwrite -o snapshotj.jar -keep org.? -cp jbox2d/jbox2d-library/target/classes;jbox2d/jbox2d-serialization/target/classes
 
 cd unobfuscated
 
 mvn clean install
 
 mvn exec:java
 
 cd ../jShrink
 
 mvn clean install
 
 mvn exec:java
 
 cd ../proguard
 
 mvn clean install
 
 mvn exec:java
