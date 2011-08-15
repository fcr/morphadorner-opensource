@echo off
set saveclasspath=%classpath%

set classpath=.
set classpath=%classpath%;bin
set classpath=%classpath%;lib\icu4j-charsets_3_6_1.jar
set classpath=%classpath%;lib\icu4j_3_6_1.jar

java -Xmx720m -Xss1m edu.northwestern.at.morphadorner.tools.countadornedwords.CountAdornedWords ^
	%1 %2 %3 %4 %5 %6 %7 %8 %9

set classpath=%saveclasspath%

