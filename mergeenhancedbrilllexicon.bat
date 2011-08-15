set saveclasspath=%classpath%

set classpath=.
set classpath=%classpath%;bin

java -Xmx720m edu.northwestern.at.morphadorner.tools.mergeenhancedbrilllexicon.MergeEnhancedBrillLexicon ^
	%1 %2 %3

set classpath=%saveclasspath%
