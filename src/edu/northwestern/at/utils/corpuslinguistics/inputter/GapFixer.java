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

/**	Adds displayable content for certain gap elements in XML file. */

public class GapFixer
{
	/**	Letter gap marker is Unicode lozenge = \u25CA. */

	protected static final char letterGapChar		=
		CharUtils.CHAR_GAP_MARKER;

	protected static final String letterGapString	= letterGapChar + "";

	/**	Word gap marker is Unicode lozenge = \u25CA. */

	protected static final char wordGapChar		= '\u25CA';
	protected static final String wordGapString	= wordGapChar + "";

	/**	Span gap marker is Unicode three dots = \u2026. */

	protected static final char spanGapChar		= '\u2026';
	protected static final String spanGapString	= spanGapChar + "";

	/**	Fix some gap elements in a DOM document.
	 *
	 *	@param	document	Document containing gaps to fix.
	 */

	public static void fixGaps( Document document )
	{
								//	Create filter for <gap> elements.

		Filter filter				= new GapFilter();

								//	Create processor for <gap> elements.

		ElementProcessor processor	= new GapProcessor();

								//	Process <gap> elements.

		JDOMUtils.applyElementFilter( document , filter , processor );
	}

	/**	Allow overrides but no instantiation.
	 */

	protected GapFixer()
	{
	}

	/**	JDOM element filter which selects <gap> elements. */

	public static class GapFilter implements Filter
	{
		public boolean matches( java.lang.Object obj )
		{
			return
				( obj instanceof Element ) &&
				( ((Element)obj).getName().equalsIgnoreCase( "gap" ) );
		}
	}

	/**	JDOM element processor which fixes <gap> elements. */

	public static class GapProcessor implements ElementProcessor
	{
		public void processElement( Document document , Element gap )
		{
								//	Collect information about this gap
								//	to see if we want to add a displayable
								//	element.

			boolean isWords			= false;
			boolean isPage			= false;
			boolean isSpan			= false;
			boolean isParagraph		= false;
			boolean isMissing		= false;
			boolean isLetter		= false;
			int count				= 0;

			String desc	=
				JDOMUtils.getAttributeValueIgnoreCase( gap , "desc" );

			if ( desc == null ) desc = "";

			String extent	=
				JDOMUtils.getAttributeValueIgnoreCase( gap , "extent" );

			if ( extent == null )
			{
				extent = "";
			}
			else
			{
				String[] extentTokens	= extent.split( " " );

				isWords	=
					( StringUtils.indexOfIgnoreCase(
						extentTokens[ 1 ] , "word" ) >= 0 );

				isPage	=
					( StringUtils.indexOfIgnoreCase(
						extentTokens[ 1 ] , "page" ) >= 0 );

				isSpan	=
					( StringUtils.indexOfIgnoreCase(
						extentTokens[ 1 ] , "span" ) >= 0 );

				isParagraph	=
					( StringUtils.indexOfIgnoreCase(
						extentTokens[ 1 ] , "paragraph" ) >= 0 );

				isMissing	=
					( StringUtils.indexOfIgnoreCase(
						extentTokens[ 1 ] , "missing" ) >= 0 );

				isLetter	=
					( StringUtils.indexOfIgnoreCase(
						extentTokens[ 1 ] , "letter" ) >= 0 );

				extentTokens[ 0 ]	=
					StringUtils.replaceAll( extentTokens[ 0 ] , "+" , "" );

				count = Integer.parseInt( extentTokens[ 0 ] );
			}

			String disp	=
				JDOMUtils.getAttributeValueIgnoreCase( gap , "disp" );

			if ( disp == null ) disp = "";

			if ( isWords ) disp = wordGapString;
			if ( isSpan ) disp	= spanGapString;
			if ( isPage || isMissing ||  isParagraph ) disp = "";

			if ( isLetter && ( disp.length() == 0 ) )
			{
				disp = StringUtils.dupl( letterGapString , count );
			}

			disp	= StringUtils.replaceAll( disp , "\u3008" , "" );
			disp	= StringUtils.replaceAll( disp , "\u3009" , "" );
			disp	= StringUtils.replaceAll( disp , "\uFEFF" , "" );

								//	Add displayable text to gap element.

			if ( disp.length() > 0 )
			{
				gap.setText( disp );
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



