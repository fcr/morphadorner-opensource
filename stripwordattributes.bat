set saveclasspath=%classpath%

set classpath=.;
set classpath=%classpath%;bin;

java -Xmx720m -Xss1m edu.northwestern.at.morphadorner.tools.stripwordattributes.StripWordAttributes %1 %2 %3 %4 %5

set classpath=%saveclasspath%
