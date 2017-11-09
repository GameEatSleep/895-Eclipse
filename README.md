# Tracer
895

Static Analysis:

* Clone Repository and Open Project "Tracer" in Eclipse 

* Download Maven Dependencies 

* Run as Java Application 

Dynamic Analysis:

- Run the following commands in the repository directory:

* cd jbox2d

* mvn clean install 

* cd ../original

* mvn clean install 

* mvn exec:java > originalLog.txt

* cd ../proguard

* mvn clean install 

* mvn exec:java > proguardLog.txt

* cd ../jShrink

* mvn clean install 

* mvn exec:java > jsLog.txt
