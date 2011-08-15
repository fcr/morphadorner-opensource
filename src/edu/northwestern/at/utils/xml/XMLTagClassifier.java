package edu.northwestern.at.utils.xml;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.net.*;

import edu.northwestern.at.utils.*;

/**	XML tag classifier.
 *
 *	<p>
 *	Allows you to classify an XML tag using an arbitrary label.
 *	Most of the time the labels represent categories such as
 *	"front" or "back" indicating a tag contains front matter or
 *	back matter, or "side" indicating a tag contains side matter.
 *	</p>
 *
 *	<p>
 *	Also classifies an XML tag as either a hard tag, a soft tag, or a
 *	jump tag.  A hard tag maintains the reading context but starts
 *	a new text section.  A jump tag interrupts the reading context
 *	as well as starting a new text section.  A soft tag neither interrupts
 *	the reading context nor starts a new text section.
 *	</p>
 *
 */

public class XMLTagClassifier
{
	/**	Set of jump tags. */

	protected Set<String> jumpTagsSet	= SetFactory.createNewSet();

	/**	Set of soft tags. */

	protected Set<String> softTagsSet	= SetFactory.createNewSet();

	/**	Class for tag. */

	protected Map<String , String> tagClasses	=
		MapFactory.createNewMap();

	/**	Create an XML tag class.
	 */

	public XMLTagClassifier()
	{
	}

	/**	Create an XML tag class.
	 *
	 *	@param	jumpTags	String containing jump tags separated by blanks.
	 *	@param	softTags	String containing soft tags separated by blanks.
	 *	@param	ignoreCase	True to store tag names in lower case.
	 */

	public XMLTagClassifier
	(
		String jumpTags ,
		String softTags ,
		boolean ignoreCase
	)
	{
		setJumpTags( jumpTags , ignoreCase );
		setSoftTags( softTags , ignoreCase );
	}

	/**	Set the jump tags.
	 *
	 *	@param	tags		String containing jump tags separated by blanks.
	 *	@param	ignoreCase	True to ignore the tag case.
	 *
	 *	<p>
	 *	When "ignoreCase" is true, the tag names are stored and
	 *	compared as lower case.
	 *	</p>
	 */

	public void setJumpTags( String tags , boolean ignoreCase )
	{
		setJumpTags( tags.split( "\\s" ) , ignoreCase );
	}

	/**	Set the jump tags.
	 *
	 *	@param	tags		String array containing jump tags.
	 *	@param	ignoreCase	True to ignore the tag case.
	 *
	 *	<p>
	 *	When "ignoreCase" is true, the tag names are stored and
	 *	compared as lower case.
	 *	</p>
	 */

	public void setJumpTags( String[] tags , boolean ignoreCase )
	{
		jumpTagsSet.clear();

		for ( int i = 0 ; i < tags.length ; i++ )
		{
			if ( tags[ i ].length() > 0 )
			{
				String tag	= tags[ i ];

				if ( ignoreCase )
				{
					tag	= tag.toLowerCase();
				}

				jumpTagsSet.add( tag );
			}
		}
	}

	/**	Set the soft tags.
	 *
	 *	@param	tags		String containing soft tags separated by blanks.
	 *	@param	ignoreCase	True to ignore the tag case.
	 *
	 *	<p>
	 *	When "ignoreCase" is true, the tag names are stored and
	 *	compared as lower case.
	 *	</p>
	 */

	public void setSoftTags( String tags , boolean ignoreCase )
	{
		setSoftTags( tags.split( "\\s" ) , ignoreCase );
	}

	/**	Set the soft tags.
	 *
	 *	@param	tags		String array containing soft tags.
	 *	@param	ignoreCase	True to ignore the tag case.
	 *
	 *	<p>
	 *	When "ignoreCase" is true, the tag names are stored and
	 *	compared as lower case.
	 *	</p>
	 */

	public void setSoftTags( String[] tags , boolean ignoreCase )
	{
		softTagsSet.clear();

		for ( int i = 0 ; i < tags.length ; i++ )
		{
			if ( tags[ i ].length() > 0 )
			{
				String tag	= tags[ i ];

				if ( ignoreCase )
				{
					tag	= tag.toLowerCase();
				}

				softTagsSet.add( tag );
			}
		}
	}

	/**	Set the tag class names.
	 *
	 *	@param	tagClass	The tag class.
	 *	@param	tags		String array containing tags.
	 *	@param	ignoreCase	True to ignore the tag case.
	 *
	 *	<p>
	 *	When "ignoreCase" is true, the tag names are stored and
	 *	compared as lower case.
	 *	</p>
	 */

	public void setTagClass
	(
		String tagClass ,
		String[] tags ,
		boolean ignoreCase
	)
	{
		for ( int i = 0 ; i < tags.length ; i++ )
		{
			if ( tags[ i ].length() > 0 )
			{
				String tag	= tags[ i ];

				if ( ignoreCase )
				{
					tag	= tag.toLowerCase();
				}

				tagClasses.put( tag , tagClass );
			}
		}
	}

	/**	Set the tag class names.
	 *
	 *	@param	tagClass	The tag class.
	 *	@param	tags		String containing tags,
	 *							separated by blanks.
	 *	@param	ignoreCase	True to ignore the tag case.
	 *
	 *	<p>
	 *	When "ignoreCase" is true, the tag names are stored and
	 *	compared as lower case.
	 *	</p>
	 */

	public void setTagClass
	(
		String tagClass ,
		String tags ,
		boolean ignoreCase
	)
	{
		setTagClass( tagClass , tags.split( "\\s" ) , ignoreCase );
	}

	/**	Get class for a tag.
	 *
	 *	@param	tag		The tag.
	 *
	 *	@return			The tag class, or null if tag is not
	 *						assigned a class.
	 */

	public String getTagClass( String tag )
	{
		String tagClass	= tagClasses.get( tag );

		if ( tagClass == null )
		{
			tagClass	= tagClasses.get( tag.toLowerCase() );
		}

		return tagClass;
	}

	/**	Is tag a soft tag?
	 *
	 *	@param	tag		The XML tag.
	 *
	 *	@return			true if tag is a soft tag.
	 */

	public boolean isSoftTag( String tag )
	{
		return
			softTagsSet.contains( tag ) ||
			softTagsSet.contains( tag.toLowerCase() );
	}

	/**	Is tag a jump tag?
	 *
	 *	@param	tag		The XML tag.
	 *
	 *	@return			true if tag is a jump tag.
	 */

	public boolean isJumpTag( String tag )
	{
		return
			jumpTagsSet.contains( tag ) ||
			jumpTagsSet.contains( tag.toLowerCase() );
	}

	/**	Is tag a hard tag?
	 *
	 *	@param	tag		The XML tag.
	 *
	 *	@return			true if tag is a hard tag.
	 */

	public boolean isHardTag( String tag )
	{
		return !( isSoftTag( tag ) || isJumpTag( tag ) );
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



