set saveclasspath=%classpath%

set classpath=.;.\bin;

java -Xmx720m -Xss1m edu.northwestern.at.morphadorner.tools.addcharacteroffsets.AddCharacterOffsets ^
	 %1 %2 %3

set classpath=%saveclasspath%
