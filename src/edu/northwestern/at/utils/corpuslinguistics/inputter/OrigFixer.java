package edu.northwestern.at.utils.corpuslinguistics.inputter;

/*	Please see the license information at the end of this file. */

import java.text.*;
import java.util.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.filter.*;
import org.jdom.output.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

/**	Fixes &lt;orig&gt; element and surrounding elements.
 *
 *	<p>
 *	&lt;orig&gt; tags are used in Wright archive documents to
 *	mark words split across a page break.  A typical page break
 *	appears as follows:
 *	</p>
 *
 *	<pre>
 *	&lt;orig reg="larboard" TEIform="orig"&gt;lar-&lt;/orig&gt;
 *    &lt;pb TEIform="pb"/&gt; board hand,
 *	</pre>
 *
 *	<p>
 *	The "reg=" attribute of the &lt;orig&gt; tag provides the
 *	original unsplit spelling of the word which is split across the
 *	page boundary.  The whitespace following the &lt;/orig&gt; and
 *	preceding the &lt;pb&gt;, as well as the whitespace following
 *	the &lt;/pb&gt;, causes MorphAdorner to process the split word
 *	incorrectly as multiple words instead of a single word.
 *	</p>
 *
 *	<p>
 *	OrigFixer modifies the XML for &lt;orig&gt; tags as follows.
 *	</p>
 *
 *	<ul>
 *	<li>OrigFixer removes the whitespace following the closing &lt;/orig&gt;
 *	    tag up to the start of the subsequent &lt;pb&gt; .
 *	    </li>
 *	<li>OrigFixer removes the leading whitespace from the text
 *	    following the closing &lt;/pb&gt; which follows the &lt;/orig&gt;.
 *	    </li>
 *	<li>OrigFixer replaces the trailing "-" in the word enclosed by
 *	    the &lt;orig&gt; tag with a special user-defined Unicode
 *		character (currently 0xE502) when there is no matching "-" in the
 *		"reg=" attribute of the &lt;orig&gt; tag.  This allows the
 *		MorphAdorner word tokenizers to distinguish the overloaded
 *		use of the trailing hyphen as a word continuation character
 *		from its use as both a continuation character and a character
 *		in the original spelling.
 *		</li>
 *	</ul>
 *
 *	<p>
 *	For example, OrigFixer modifies the sample text above to read:
 *	</p>
 *
 *	<pre>
 *	&lt;orig reg="larboard" TEIform="orig"&gt;lar\ue502&lt;/orig&gt;&lt;pb TEIform="pb"/&gt;board hand,
 *	</pre>
 *
 *	<p>
 *	These modifications allow MorphAdorner to process the split word
 *	correctly.  The MorphAdorner tokenizers recognize the special substitute
 *	hyphen character, which is restored to a plain hyphen character by the
 *	XML output writers.
 *	</p>
 */

public class OrigFixer
{
	/**	Fix <orig> elements in a DOM document.
	 *
	 *	@param	document			Document containing <orig> elements to fix.
	 */

	public static void fixOrigs( Document document )
	{
								//	Create filter for <orig> elements.

		Filter filter				= new OrigFilter();

								//	Create processor for <orig> elements.

		ElementProcessor processor	= new OrigProcessor();

								//	Process <orig> elements.

		JDOMUtils.applyElementFilter( document , filter , processor );
	}

	/**	Allow overrides but no instantiation.
	 */

	protected OrigFixer()
	{
	}

	/**	JDOM element filter which selects <orig> elements. */

	public static class OrigFilter implements Filter
	{
		public boolean matches( java.lang.Object obj )
		{
			return
				( obj instanceof Element ) &&
				( ((Element)obj).getName().equalsIgnoreCase( "orig" ) );
		}
	}

	/**	JDOM element processor which fixes <orig> elements. */

	public static class OrigProcessor implements ElementProcessor
	{
		/**	Fix <orig> elements in JDOM element.
		 *
	 	 *	@param	document	The document being processed.
	 	 *	@param	element		The JDOM element to process.
	 	 */

		public void processElement( Document document , Element element )
		{
			String reg	= element.getAttributeValue( "reg" );

			String text	= element.getText();

			if ( text.endsWith( "-" ) )
			{
				if ( !reg.startsWith( text ) )
				{
					text	= text.substring( 0 , text.length() - 1 );
					text	= text + CharUtils.CHAR_FAKE_SOFT_HYPHEN_STRING;

					element.setText( text );
				}
			}

			Parent parent	= element.getParent();

			if ( parent != null )
			{
				int index	= parent.indexOf( element );

				Content sibling	= parent.getContent( index + 1 );

				if ( sibling != null )
				{
					if ( sibling instanceof Text )
					{
						Text textSibling	= (Text)sibling;
						text				= textSibling.getText();

						if ( text.length() > 0 )
						{
							text	= text.trim();
							textSibling.setText( text );
						}
					}
				}

				sibling	= parent.getContent( index + 2 );

				if ( sibling != null )
				{
					if ( sibling instanceof Element )
					{
						Element pbSibling	= (Element)sibling;

						if ( pbSibling.getName().equalsIgnoreCase( "pb" ) )
						{
							sibling	= parent.getContent( index + 3 );

							if ( sibling != null )
							{
								if ( sibling instanceof Text )
								{
									Text textSibling	= (Text)sibling;
									text				= textSibling.getText();

									if ( text.length() > 0 )
									{
										text	= text.substring( 1 );
										textSibling.setText( text );
									}
								}
							}
						}
					}
				}
			}
		}
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



