set saveclasspath=%classpath%

set classpath=.;
set classpath=%classpath%;bin;
set classpath=%classpath%;lib\jdom.jar
set classpath=%classpath%;lib\icu4j-charsets_3_6_1.jar
set classpath=%classpath%;lib\icu4j_3_6_1.jar

java -Xmx720m -Xss1m edu.northwestern.at.morphadorner.tools.xmltotab.XMLToTab %1 %2

set classpath=%saveclasspath%

