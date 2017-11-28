# Tracer
# 895

## PRE-REQS
 
 - Need to have Java, Maven and AspectJ Compiler installed
 
 - Download proguard (https://sourceforge.net/projects/proguard/files/) and extract proguard.jar to the working directory
 
 - Download jShrink (http://www.e-t.com/jshrink.html) and extract jshrink.jar to the working directory
 
 - Download and install ApectJ Compiler (sudo apt install aspectj)
		
 - If you're running on a Unix machine, you also need to install openjfx (sudo apt-get install openjfx)
 
**************************************************************************************************************************************

Static Analysis:

* Clone Repository and Open Project "Tracer" in Eclipse 

* Download Maven Dependencies 

* Run as Java Application 

**************************************************************************************************************************************

Dynamic Analysis:

- Run tool.sh

**************************************************************************************************************************************




## Changes Needed to Run on different programs
 
 Static Analysis:

* Place Source files in testFiles/src directory 

************************************************************************************************************************************** 
 Dynamic Analysis:

* Update Pom Files in "jShrink","proguard","unobfuscated" folders with you jar dependency and Main Class

* Update "tool.sh" with your jar file and classpath dependencies for jShrink obfuscation

* Update "myconfig.pro" with your jar file and dependencies as libraryjars for proguard obfuscation

**************************************************************************************************************************************
