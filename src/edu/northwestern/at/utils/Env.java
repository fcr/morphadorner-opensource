package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

/**	Environment information.
 */

public class Env {

	/** The operating system. */

	public static final String OSNAME =
		System.getProperty("os.name");

	/**	True if running on Mac OS X. */

	public static final boolean MACOSX =
		System.getProperty("os.name").equals("Mac OS X");

	/**	True if running on some version of MS Windows. */

	public static final boolean WINDOWSOS =
		System.getProperty("os.name").toLowerCase().startsWith("windows");

	/** True if running Java 2 level 1.3 or later. */

	public static final boolean IS_JAVA_13_OR_LATER =
		System.getProperty("java.version").compareTo("1.3") >= 0;

	/** True if running Java 2 level 1.4 or later. */

	public static final boolean IS_JAVA_14_OR_LATER =
		System.getProperty("java.version").compareTo("1.4") >= 0;

	/** True if running Java 2 level 1.4.2 or later. */

	public static final boolean IS_JAVA_142_OR_LATER =
		System.getProperty("java.version").compareTo("1.4.2") >= 0;

	/** True if running Java 2 level 1.5 or later. */

	public static final boolean IS_JAVA_15_OR_LATER =
		System.getProperty("java.version").compareTo("1.5") >= 0;

	/** True if running Java 2 level 1.6 or later. */

	public static final boolean IS_JAVA_16_OR_LATER =
		System.getProperty("java.version").compareTo("1.6") >= 0;

	/** True if running Java 2 level 1.7 or later. */

	public static final boolean IS_JAVA_17_OR_LATER =
		System.getProperty("java.version").compareTo("1.7") >= 0;

	/**	Menu shortcut key mask. */

	public static int MENU_SHORTCUT_KEY_MASK;

	/**	Menu shortcut key mask with shift key. */

	public static int MENU_SHORTCUT_SHIFT_KEY_MASK;

	/** Line separator. */

	public static final String LINE_SEPARATOR =
		System.getProperty("line.separator");

	static
	{
								//	Do not initialize keyboard settings
								//	if we are running headless.

		try
		{
			GraphicsEnvironment ge	=
				GraphicsEnvironment.getLocalGraphicsEnvironment();

			if ( !ge.isHeadless() )
			{
				MENU_SHORTCUT_KEY_MASK =
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

				MENU_SHORTCUT_SHIFT_KEY_MASK =
					MENU_SHORTCUT_KEY_MASK + InputEvent.SHIFT_MASK;
			}
			else
			{
				MENU_SHORTCUT_KEY_MASK			=	0;
				MENU_SHORTCUT_SHIFT_KEY_MASK	=	0;
			}
		}
		catch ( Exception e )
		{
		}
	}

	/** Don't allow instantiation, do allow overrides. */

	protected Env()
	{
	}
}

/*
Copyright (c) 2008, 2009 by Northwestern University.
All rights reserved.

Developed by:
   Academic and Research Technologies
   Northwestern University
   http://www.it.northwestern.edu/about/departments/at/

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal with the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimers.

    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimers in the documentation and/or other materials provided
      with the distribution.

    * Neither the names of Academic and Research Technologies,
      Northwestern University, nor the names of its contributors may be
      used to endorse or promote products derived from this Software
      without specific prior written permission.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE CONTRIBUTORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE SOFTWARE.
*/


