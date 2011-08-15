package edu.northwestern.at.morphadorner.xgtagger;

/*	Please see the license information in the header below. */

/** XGTagger
 *
 *	Copyright Ecole Nationale Superieure des Mines de Saint-Etienne
 *
 *	Original authors: Aude Garnier and Xavier Tannier.
 *
 *	Modifications by Philip R. "Pib" Burns at Northwestern University
 *	for integration into MorphAdorner.
 *
 *	Please DO NOT address questions about this modified version to the
 *	original authors.
 *
 *	This software is a computer program whose purpose is to provide
 *	a generic interface to deal with and analyse any XML textual content.
 *
 *	This software is governed by the CeCILL  license under French law and
 *	abiding by the rules of distribution of free software.	You can  use,
 *	modify and/ or redistribute the software under the terms of the CeCILL
 *	license as circulated by CEA, CNRS and INRIA at the following URL
 *	"http://www.cecill.info".
 *
 *	As a counterpart to the access to the source code and  rights to copy,
 *	modify and redistribute granted by the license, users are provided only
 *	with a limited warranty  and the software's author,  the holder of the
 *	economic rights,  and the successive licensors	have only  limited
 *	liability.
 *
 *	In this respect, the user's attention is drawn to the risks associated
 *	with loading,  using,  modifying and/or developing or reproducing the
 *	software by the user in light of its specific status of free software,
 *	that may mean  that it is complicated to manipulate,  and  that  also
 *	therefore means  that it is reserved for developers  and  experienced
 *	professionals having in-depth computer knowledge. Users are therefore
 *	encouraged to load and test the software's suitability as regards their
 *	requirements in conditions enabling the security of their systems and/or
 *	data to be ensured and,  more generally, to use and operate it in the
 *	same conditions as regards security.
 *
 *	The fact that you are presently reading this means that you have had
 *	knowledge of the CeCILL license and that you accept its terms.
 */

import java.util.Vector;
import java.util.Properties;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;

import java.lang.Process;

import edu.northwestern.at.morphadorner.*;
import edu.northwestern.at.utils.*;

public class XGOptions
{
	Vector<String> vectorSoftTags;
	Vector<String> vectorJumpTags;
	String strFieldDelimiters;
	String strWordDelimiters;
	String strSpecialSeparator;
	String strSurroundMarker;
	int intPath;
	boolean boolLog;
	boolean boolIds;
	boolean boolVerbose;
	boolean boolMergeEntities;
	boolean boolTreatAllEntities;
	boolean boolEntityIgnoreFiles;
	boolean boolRepeatAttributes;
	String strId;
	String strTagsPath;
	String strWordPath;
	int intWordField;
	String strRelativeURIBase;
	String strWordTagName;
	Properties properties;
	boolean ignoreTagCase;

	public XGOptions()
	{
		this.strRelativeURIBase			= null;
		this.vectorSoftTags			= new Vector<String>();
		this.vectorJumpTags			= new Vector<String>();
		this.strFieldDelimiters			= " \t";
		this.strWordDelimiters			= "\n";
		this.intPath					= 0;
		this.boolIds					= false;
		this.boolVerbose				= false;
		this.boolMergeEntities			= true;
		this.boolTreatAllEntities		= false;
		this.boolEntityIgnoreFiles		= false;
		this.boolRepeatAttributes		= true;
		this.strSurroundMarker		=
			CharUtils.CHAR_END_OF_TEXT_SECTION_STRING;
		this.ignoreTagCase			= true;
	}

	/**
	 * Writes a text in the appriopriate stream, according to LOG property
	 * and to boolError.
	 * @param strText the text to print.
	 * @param intType specifies the type of log:
	 *			 <ul><li><code>-1</code> for an error message;
	 *				 <li><code>0</code> for a message only if a log has been asked;
	 *				 <li><code>1</code> for a message in any case.</ul>
	 */

	protected void log( String strText , int intType )
		throws IOException
	{
		switch ( intType )
		{
			case -1:
				MorphAdornerLogger.logError( strText );
				break;

			case 0:
				MorphAdornerLogger.logDebug( strText );
				break;

			default:
				MorphAdornerLogger.logInfo( strText );
				break;
		}
	}

	public void setProperties( Properties p )
	{
		this.properties = p;
	}

	public Object setProperty( String s1 , String s2 )
	{
		return this.properties.setProperty( s1 , s2 );
	}

	public void setWordTagName( String s )
	{
		this.strWordTagName = s;
	}

	public void setWordField( int i )
	{
		this.intWordField = i;
	}

	protected void setJumpTags( Vector<String> v )
	{
		this.vectorJumpTags = v;
	}

	protected void setSoftTags( Vector<String> v )
	{
		this.vectorSoftTags = v;
	}

	public void setJumpTags( String tags )
	{
		String[] splitTags	= tags.split( "\\s" );

		vectorJumpTags.clear();

		for ( int i = 0 ; i < splitTags.length ; i++ )
		{
			if ( splitTags[ i ].length() > 0 )
			{
				if ( ignoreTagCase )
				{
					splitTags[ i ]	= splitTags[ i ].toLowerCase();
				}

				this.vectorJumpTags.add( splitTags[ i ] );
			}
		}
	}

	public void setSoftTags( String tags )
	{
		String[] splitTags	= tags.split( "\\s" );

		vectorSoftTags.clear();

		for ( int i = 0 ; i < splitTags.length ; i++ )
		{
			if ( splitTags[ i ].length() > 0 )
			{
				if ( ignoreTagCase )
				{
					splitTags[ i ]	= splitTags[ i ].toLowerCase();
				}

				this.vectorSoftTags.add( splitTags[ i ] );
			}
		}
	}

	public void setFieldDelimiters( String s )
	{
		this.strFieldDelimiters = s;
	}

	public void setWordDelimiters( String s )
	{
		this.strWordDelimiters = s;
	}

	public void setSpecialSeparator( String s )
	{
		this.strSpecialSeparator = s;
	}

	public void setIdArgumentName( String s )
	{
		this.strId = s;
	}

	public void setSurroundMarker( String s )
	{
		this.strSurroundMarker = s;
	}

	public void setTagsPathArgumentName( String s )
	{
		this.strTagsPath = s;
	}

	public void setWordPathArgumentName( String s )
	{
		this.strWordPath = s;
	}

	public void setWritePath( int i )
	{
//		this.intPath += i;
		this.intPath |= i;
	}

	public void setWriteLog( boolean b )
	{
		this.boolLog = b;
	}

	public void setVerbose( boolean b )
	{
		this.boolVerbose = b;
	}

	/** Asks application to repeat attributes
	 * while creating new nodes for multiple word terms
	 * (term containing one or several special separators) (or not)
	 * @param b - <code>true</code> if attributes should be repeated in any case,
	 *	 <code>false</code> if only the first word should contain the attributes.
	 */
	public void repeatAttributes( boolean b )
	{
		this.boolRepeatAttributes = b;
	}

	/** Sets the base for relative URIs.
	 * @param str - the String representation of the base for relative URIs
	 */
	public void setRelativeURIBase( String str )
	{
		this.strRelativeURIBase = str;
	}

	/** Ask application to merge
	 * all entity references into a single output file (or not)
	 * @param b - <code>true</code> if asking for merging, <code>false</code> otherwise
	 * @see #getEntityMerging
	 */
	public void setEntityMerging( boolean b )
	{
		this.boolMergeEntities = b;
	}

	/** Ask application to treat all entity references (or not)
	 * @param b - <code>true</code> if asking for treating all entity references,
	 * <code>false</code> otherwise
	 * @see #getEntityTreatAll
	 */
	public void setEntityTreatAll( boolean b )
	{
		this.boolTreatAllEntities = b;
	}

	/** Ask application to ignore external file entity references (or not)
	 * @param b - <code>true</code> if asking for ignoring external file entity references.
	 * <code>false</code> otherwise
	 * @see #getEntityIgnoreFiles
	 */
	public void setEntityIgnoreFiles( boolean b )
	{
		this.boolEntityIgnoreFiles = b;
	}

	public void setWriteIds( boolean b )
	{
		this.boolIds = b;
	}

	/**	Set ignore tag case flag.
	  *
	  *	@param ignoreTagCase		true to ignore xml tag case.
	  */

	public void setIgnoreTagCase( boolean ignoreTagCase )
	{
		this.ignoreTagCase	= ignoreTagCase;
	}

	public String getProperty( String str )
	{
		return this.properties.getProperty( str );
	}

	public Vector<String> getJumpTags()
	{
		return this.vectorJumpTags;
	}

	public Vector<String> getSoftTags()
	{
		return this.vectorSoftTags;
	}

	public boolean isSoftTag( String strTagName )
	{
		boolean result;

		if ( ignoreTagCase )
		{
			result	=
				this.vectorSoftTags.contains( strTagName.toLowerCase() );
		}
		else
		{
			result	= this.vectorSoftTags.contains( strTagName );
		}

		return result;
	}

	public boolean isJumpTag( String strTagName )
	{
		boolean result;

		if ( ignoreTagCase )
		{
			result	=
				this.vectorJumpTags.contains( strTagName.toLowerCase() );
		}
		else
		{
			result	= this.vectorJumpTags.contains( strTagName );
		}

		return result;
	}

	public String getSurroundMarker()
	{
		return this.strSurroundMarker;
	}

	public String getFieldDelimiters()
	{
		return this.strFieldDelimiters;
	}

	public String getWordDelimiters()
	{
		return this.strWordDelimiters;
	}

	public String getSpecialSeparator()
	{
		return this.strSpecialSeparator;
	}

	public int getWritePath()
	{
		return this.intPath;
	}

	public boolean getWriteLog()
	{
		return this.boolLog;
	}

	public boolean getWriteIds()
	{
		return this.boolIds;
	}

	public boolean isVerbose()
	{
		return this.boolVerbose;
	}

	/** Tests whether attributes should be repeated
	 * while creating new nodes for multiple word terms
	 * (term containing one or several special separators)
	 * @return <code>true</code> if attributes should be repeated in any case,
	 *	 <code>false</code> if only the first word should contain the attributes.
	 */
	public boolean repeatAttributes()
	{
		return this.boolRepeatAttributes;
	}

	/** Tests whether the application has been asked to merge
	 * all entity references into a single output file.
	 * @return <code>true</code> if entity references should be merged
	 *	 into a single file, <code>false</code> otherwise.
	 * @see #setEntityMerging
	 */
	public boolean getEntityMerging()
	{
		return this.boolMergeEntities;
	}

	/** Tests whether the application has been asked to treat
	 * all entity references.
	 * @return <code>true</code> if entity references should be treated
	 *	 <code>false</code> otherwise.
	 * @see #setEntityTreatAll
	 */
	public boolean getEntityTreatAll()
	{
		return this.boolTreatAllEntities;
	}

	/** Tests whether the application has been asked not to treat
	 * external file entity references.
	 * @return <code>true</code> if external file entity references should be ignored
	 *	 <code>false</code> otherwise.
	 * @see #setEntityIgnoreFiles
	 */
	public boolean getEntityIgnoreFiles()
	{
		return this.boolEntityIgnoreFiles;
	}

	/** Returns the base for relative URIs.
	 * @return the base for relative URIs
	 */
	public String getRelativeURIBase()
	{
		return this.strRelativeURIBase;
	}

	public String getWordTagName()
	{
		return this.strWordTagName;
	}

	public int getWordField()
	{
		return this.intWordField;
	}

	public String getIdArgumentName()
	{
		return this.strId;
	}

	public String getTagsPathArgumentName()
	{
		return this.strTagsPath;
	}

	public String getWordPathArgumentName()
	{
		return this.strWordPath;
	}

	public boolean getIgnoreTagCase()
	{
		return ignoreTagCase;
	}

	/** Tests whether the specified output file name is a directory.
	 *
	 *	@return	<code>true</code> if the specified output file name is
	 *				a directory, <code>false</code> otherwise.
	 *
	 *	<p>
	 *	Always returns false in MorphAdorner.
	 *	</p>
	 */

	public boolean isOutputDirectory()
	{
//		return this.boolOutputDirectory;
		return false;
	}
}

