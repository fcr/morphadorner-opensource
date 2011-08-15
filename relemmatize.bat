@echo off
set saveclasspath=%classpath%

set classpath=.;
set classpath=%classpath%;bin
set classpath=%classpath%;lib\log4j-1.2.9.jar
set classpath=%classpath%;lib\jdom.jar
set classpath=%classpath%;lib\icu4j-charsets_3_6_1.jar
set classpath=%classpath%;lib\icu4j_3_6_1.jar
set classpath=%classpath%;lib\isorelax.jar
set classpath=%classpath%;lib\isorelax-jaxp-bridge-1.0.jar
set classpath=%classpath%;lib\msv.jar
set classpath=%classpath%;lib\relaxngDatatype.jar
set classpath=%classpath%;lib\xsdlib.jar

java -Xmx720m -Xss1m edu.northwestern.at.morphadorner.tools.relemmatize.Relemmatize  ^
	%1 %2 %3 %4 %5 %6 %7 %8 %9

set classpath=%saveclasspath%

