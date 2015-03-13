@echo off
REM script validated on Windows XP with Java 6.0.14 + apache tomcat 6.0.20

REM delete previous log files :
del /S /Q ..\logs\*

REM set your JRE here :
set JAVA_HOME=C:\Java\Jdk6_14

REM to enable JMX Monitoring, uncomment the following line : 
REM set CATALINA_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=7777"


REM this line defines the Java memory Heap, please adjust to your work load :
set JAVA_OPTS=-Xms128m -Xmx128m -XX:PermSize=32m -XX:MaxPermSize=32m 

REM this line defines the GC options (-XX:+CMSClassUnloadingEnabled -XX:+CMSPermGenPrecleaningEnabled are required to allow the JVM to unload classes)
set JAVA_OPTS=%JAVA_OPTS% -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSClassUnloadingEnabled -XX:+CMSPermGenPrecleaningEnabled -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses

REM to enable GC details, uncomment the following line :
REM set JAVA_OPTS=%JAVA_OPTS% -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+TraceClassUnloading 


@echo "JAVA_HOME    : %JAVA_HOME%"
@echo "JAVA_OPTS    : %JAVA_OPTS%"
@echo "CATALINA_OPTS: %CATALINA_OPTS%"