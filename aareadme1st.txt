Downloading MorphAdorner
------------------------

The file

    morphadorner-2009-04-29.zip

contains the MorphAdorner source code, data, and libraries.

Current version: 1.0
Last update: April 29, 2009

File Layout of Morphadorner Release
-----------------------------------

File or Directory          Contents
=========================  ================================================
aareadme1st.txt            Printable copy of this file in Windows text
                           format (lines terminated by Ascii cr/lf).
bin/                       Binaries for MorphAdorner.
build.xml                  Apache Ant build file used to compile
                           MorphAdorner.
data/                      Data files used by MorphAdorner.
documentation/             MorphAdorner documentation.
gatelib/                   Java libraries used by Gate.
javadoc/                   Javadoc (internal documentation).
jetty/                     Web server for running sample servlets.
lib/                       Java library files.
misc/                      Miscellaneous files for compiling MorphAdorner.
src/                       MorphAdorner source code.

Installing and Building MorphAdorner
------------------------------------

Extract the files from morphadorner-2009-04-29.zip, retaining the
directory structure, to an empty directory. The zip file contains
precompiled (with Java 1.6) versions of all of the code as well as the
javadoc.  You do not need to rebuild the code unless you want to make
changes. If you do want to rebuild the code, make sure you have
installed recent working copies of Sun's Java Development Kit and
Apache Ant on your system. Move to the directory in which you extracted
morphadorner-2009-04-29.zip, and type:

    ant

This should build MorphAdorner successfully.

Type

    ant doc

to generate the javadoc (internal documentation).

Type

    ant clean

to remove the effects of compilation.

Documentation
-------------

Printable documentation, in Adobe Acrobat PDF format, appears in the
documentation/morphadorner.pdf file in the MorphAdorner release.
MorphAdorner documentation is also available online. The online version
will generally be more up-to-date than the printable version included in
the release materials. The javadoc (internal documentation) is also
available online as well as in the release materials in the javadoc/
directory. The online MorphAdorner modification history describes what has
changed from one release of MorphAdorner to the next.

Running MorphAdorner
--------------------

MorphAdorner has run successfully on Windows, Mac OS X, and various flavors
of Linux.

The sample batch file adornncf.bat and the corresponding Linux script
adornncf shows how to run MorphAdorner to adorn TEI Analytics format XML
files for 19th century and later works in which quote marks are not
distinguished from apostrophes. Use the sample batch file adornncfa.bat and
the script adornncfa for works in which quote marks are distinguished from
apostrophes. Use the batch file adornwright.bat and script adornwright for
Wright archive texts. The sample batch file adorneme.bat and the
corresponding Linux script adorneme shows how to run MorphAdorner to adorn
TEI Analytics versions of XML files generated from the early modern English
eebo/tcp collection. Please see Adorning a Text for more information on
these and other sample batch files and scripts in the MorphAdorner release.

Don't forget to mark the Unix script files as executable before using them.
On most Unix/Linux systems you can use the chmod command to do this, e.g.:

    chmod 755 adornncfa

The MorphAdorner release contains a script makescriptsexecutable which
applies chmod to each of the scripts in the release. On most Unix-like
systems you can execute makescriptsexecutable by moving to the MorphAdorner
installation directory and entering

    chmod 755 makescriptsexecutable ./makescriptsexecutable

or

    /bin/sh <makescriptsexecutable

There are presumably lots of warts, misfeatures, bugs, missing items, and
whatnot. Use MorphAdorner with caution.

