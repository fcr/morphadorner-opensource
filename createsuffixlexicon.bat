@echo off
set saveclasspath=%classpath%

set classpath=.
set classpath=%classpath%;bin

java -Xmx720m -Xss1m edu.northwestern.at.morphadorner.tools.createsuffixlexicon.CreateSuffixLexicon ^
	%1 %2 %3 %4 %5 %6 %7 %8 %9

set classpath=%saveclasspath%

