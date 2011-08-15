set saveclasspath=%classpath%

set classpath=.
set classpath=%classpath%;bin
set classpath=%classpath%;gatelib\gate.jar
set classpath=%classpath%;gatelib\jasper-compiler-jdt.jar
set classpath=%classpath%;lib\jdom.jar
set classpath=%classpath%;gatelib\nekohtml-0.9.5.jar
set classpath=%classpath%;gatelib\ontotext.jar
set classpath=%classpath%;gatelib\PDFBox-0.7.2.jar
set classpath=%classpath%;gatelib\serializer.jar
set classpath=%classpath%;gatelib\stax-api-1.0.1.jar
set classpath=%classpath%;gatelib\xercesImpl.jar
set classpath=%classpath%;lib\icu4j-charsets_3_6_1.jar
set classpath=%classpath%;lib\icu4j_3_6_1.jar

java -Xmx720m -Xss1m edu.northwestern.at.morphadorner.tools.namedentities.AdornWithNamedEntities ^
     %1 %2 %3 %4 %5 %6 %7 %8 %9

set classpath=%saveclasspath%

