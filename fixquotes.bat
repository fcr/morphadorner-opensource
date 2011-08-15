set saveclasspath=%classpath%

set classpath=.
set classpath=%classpath%;bin

java edu.northwestern.at.morphadorner.tools.fixquotes.FixQuotes %1 %2

set classpath=%saveclasspath%
