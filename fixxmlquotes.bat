set saveclasspath=%classpath%

set classpath=.
set classpath=%classpath%;bin

java edu.northwestern.at.morphadorner.tools.fixquotes.FixXMLQuotes ^
	data\softtags.txt data\jumptags.txt ^
	%1 %2 %3 %4 %5 %6 %7 %8 %9

set classpath=%saveclasspath%

