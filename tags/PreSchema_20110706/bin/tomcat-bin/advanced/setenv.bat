@echo off
REM script validated on Windows XP with Java 6.0.14 + apache tomcat 6.0.20

REM delete previous log files :
del /S /Q ..\logs\*

REM set your JRE here :
set JAVA_HOME=C:\Java\Jdk6_14

REM to enable JMX Monitoring, uncomment the following line : 
REM set CATALINA_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=7777"


REM this line defines the Java memory Heap, please adjust to your work load :
set JAVA_OPTS=-Xms256m -Xmx256m -XX:-UseAdaptiveSizePolicy -XX:NewRatio=2 -XX:SurvivorRatio=6 

REM this line defines the size of the Permanent Generation (space for loaded classes and static references) :
set JAVA_OPTS=%JAVA_OPTS% -XX:PermSize=128m -XX:MaxPermSize=128m

REM these options allow the Java 6 heap dump and visual VM to work
set JAVA_OPTS=%JAVA_OPTS% -XX:+HeapDumpOnOutOfMemoryError -XX:+PerfBypassFileSystemCheck -XX:+AggressiveOpts

REM these lines define the GC options (-XX:+CMSClassUnloadingEnabled -XX:+CMSPermGenPrecleaningEnabled are required to allow the JVM to unload classes)
set JAVA_OPTS=%JAVA_OPTS% -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+ClassUnloading -XX:+CMSClassUnloadingEnabled
set JAVA_OPTS=%JAVA_OPTS% -XX:+CMSPrecleaningEnabled -XX:+CMSPermGenSweepingEnabled -XX:+CMSPermGenPrecleaningEnabled -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses

set JAVA_OPTS=%JAVA_OPTS% -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing

REM this line enable the new experimental G1 collector (JDK 1.6.0_14 required)
REM set JAVA_OPTS=%JAVA_OPTS% -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC 


REM to enable GC details, uncomment the following line :
set JAVA_OPTS=%JAVA_OPTS% -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+TraceClassUnloading -XX:+PrintTenuringDistribution 

@echo "JAVA_HOME    : %JAVA_HOME%"
@echo "JAVA_OPTS    : %JAVA_OPTS%"
@echo "CATALINA_OPTS: %CATALINA_OPTS%"