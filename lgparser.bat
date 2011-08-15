set saveclasspath=%classpath%

set classpath=.
set classpath=%classpath%;bin
set classpath=%classpath%;lib\icu4j-charsets_3_6_1.jar
set classpath=%classpath%;lib\icu4j_3_6_1.jar

java edu.northwestern.at.morphadorner.tools.lgparser.LGParser data/lgparser %1

set classpath=%saveclasspath%

