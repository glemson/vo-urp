Laurent Bourges - March 13, 2011

---------------------------------------
   Distributed librairies
---------------------------------------

This folder and sub folders contains java libraries used by ant build file and java source code :


* Saxon B : 
      - saxon9.jar
      - saxon9-s9api.jar
      (saxonb9-0-0-4j.zip)

    "The latest open-source implementation of XSLT 2.0 and XPath 2.0, and XQuery 1.0. 
    This provides the "basic" conformance level of these languages: in effect, this provides all the features 
    of the languages except schema-aware processing. This version reflects the syntax of the final XSLT 2.0, 
    XQuery 1.0, and XPath 2.0 Recommendations of 23 January 2007.

    There are three files available: one containing executable code for the Java platform, one containing 
    executable code for the .NET platform, and one containing documentation, source code, and sample applications 
    applicable to both platforms. (Documentation is also available online)
    ..."

    Usage : this library is only used by <xslt2> custom tasks in ant script to provide an XSLT 2.0 reference implementation.

    http://prdownloads.sourceforge.net/saxon/saxonb9-0-0-4j.zip




* Apache libraries : 
    License agreement : see ./apache_LICENSE-2.0.txt


    - Jakarta Commons Logging 1.1.1 :
      - commons-logging-1.1.1.jar

      "The Logging Component

      When writing a library it is very useful to log information. However there are many logging implementations out there, 
      and a library cannot impose the use of a particular one on the overall application that the library is a part of.

      The Logging package is an ultra-thin bridge between different logging implementations. A library that uses the 
      commons-logging API can be used with any logging implementation at runtime. Commons-logging comes with support for a 
      number of popular logging implementations, and writing adapters for others is a reasonably simple task. 
      ..."

      Usage : this library is used by the base java code to provide a logging system in the LogUtil class

      http://commons.apache.org/logging/


    - Log4j 1.2.16 :
      - log4j-1.2.16.jar

      "Welcome to Apache Logging Services

      The Apache Logging Services Project creates and maintains open-source software related to the logging of application 
      behavior and released at no charge to the public.

      The products of the Apache Logging Services Project included three logging frameworks: log4j for Java, log4cxx for C++
      and log4net for the Microsoft .NET framework and a log viewer and analysis tool: Chainsaw.
      ..."

      Usage : this library is used by the base java code to provide a logging system in the LogUtil class

      http://logging.apache.org/log4j/




* Eclipselink - 2.2 :
	Specification-Title: Eclipse Persistence Services
	Specification-Vendor: Eclipse.org - EclipseLink Project
	Specification-Version: 2.2.0
	Implementation-Title: org.eclipse.persistence
	Implementation-Vendor: Eclipse.org - EclipseLink Project
	Implementation-Version: 2.2.0.v20110202-r8913
	Release-Designation: EclipseLink 2.2.0

    License agreement : see eclipselink_license.html

      - eclipselink.jar (2.2.0)
      - javax.persistence_2.0.3.jar

     "The EclipseLink project's goal is to provide a complete persistence framework that is both comprehensive and universal. It will run in any Java environment and read and write objects to virtually any type of data source, including relational databases, and XML. EclipseLink will focus on providing leading edge support, including advanced feature extensions, for the dominant persistence standards for each target data source; Java Persistence API (JPA) for relational databases, Java API for XML Binding (JAXB) for XML, Service Data Objects (SDO), and Database Web services (DBWS)."

      Usage : this library is used as the persistence layer in Java code.

      http://www.eclipse.org/eclipselink/




* Java Architecture for XML Binding 2.2 = JAXB Reference Implementation 2.2.3
	Specification-Title: Java Architecture for XML Binding
	Specification-Version: 2.2.2
	Specification-Vendor: Oracle Corporation
	Implementation-Title: JAXB Reference Implementation 
	Implementation-Version: 2.2.3
	Implementation-Vendor: Oracle Corporation
	Implementation-Vendor-Id: com.sun
	Extension-Name: com.sun.xml.bind
	Build-Id: hudson-jaxb-ri-2.2.3-3

    License agreement : see JAXB-RI-License.txt and JAXB-RI-ThirdPartyLicenseReadme.txt

      - jaxb-api.jar              : JAXB 2.2 API
      - jaxb-impl.jar             : JAXB 2.2.3 RI Implementation

      - activation.jar            : JavaBeans(TM) Activation Framework Specification
      - jsr173_1.0_api.jar (StaX) : Streaming API for XML

      "The Java TM Architecture for XML Binding (JAXB) provides an API and tools that automate the mapping between XML documents
      and Java objects.

      The JAXB framework enables developers to perform the following operations:
        - Unmarshal XML content into a Java representation
        - Access and update the Java representation
        - Marshal the Java representation of the XML content into XML content

      JAXB gives Java developers an efficient and standard way of mapping between XML and Java code. Java developers using JAXB 
      are more productive because they can write less code themselves and do not have to be experts in XML. JAXB makes it easier 
      for developers to extend their applications with XML and Web Services technologies.
      ..."

      Usage : this library is used as the XML Marshaller / Unmarshaller for XML documents in Java code.

      https://jaxb.dev.java.net/




* jalopy-1.5b1.jar 
      Built from Jalopy source code obtained from http://sourceforge.net/projects/jalopy/
			


--- End of file ---
