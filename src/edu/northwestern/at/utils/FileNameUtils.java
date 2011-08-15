package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import java.text.*;

import edu.northwestern.at.utils.net.mime.*;

/**	File name utilities.
 *
 *	<p>
 *	This static class provides various utility methods for manipulating
 *	file and directory names.
 *	</p>
 */

public class FileNameUtils
{
	/** Static mime type mapper. */

	public static MimeTypeMapper mimeTypeMapper = new MimeTypeMapper();

	/** File separator. */

	public static final String FILE_SEPARATOR =
		System.getProperty( "file.separator" );

	/**	Strips path from a file name.
	 *
	 *	@param	fileName	File name with possible path.
	 *
	 *	@return				File name stripped of path.
	 */

	public static String stripPathName( String fileName )
	{
		 return new File( fileName ).getName();
	}

	/**	Change extension of file name.
	 *
	 *	@param	fileName		The file name whose extension is to be changed.
	 *	@param	newExtension	The new file name extension.
	 *
	 *	@return					The file name with the extension
	 *								changed to new extension.
	 *
	 *	<p>
	 *	Null returned if file name or new extension null.
	 *	</p>
	 */

	public static String changeFileExtension
	(
		String fileName ,
		String newExtension
	)
	{
		String result	= null;

		if ( ( fileName != null ) && ( newExtension != null ) )
		{
			String extension	= newExtension;

			if ( extension.length() > 0 )
			{
				if ( extension.charAt( 0 ) != '.' )
				{
					extension	= '.' + extension;
				}
			}

			int periodPos = fileName.lastIndexOf( '.' );

			if ( periodPos >= 0 )
			{
				result	=
					fileName.substring( 0 , periodPos ) + extension;
			}
			else
			{
				result	= fileName + extension;
			}
		}

		return result;
	}

	/** Get file extension from file name.
	 *
	 *	@param	fileName		The file name whose extension is wanted.
	 *	@param	keepPeriod	Keep the period in the extension.
	 *
	 *	@return				The extension, if any, with the optional
	 *							leading period.
	 */

	public static String getFileExtension
	(
		String fileName ,
		boolean keepPeriod
	)
	{
								// Strip path from file name.

		String name = stripPathName( StringUtils.safeString( fileName ) );

								// Find trailing period.
								// This marks start of extension.
		String extension;

		int periodPos = name.lastIndexOf( '.' );

		if ( periodPos != -1 )
		{
								// If period found,
								// we have an extension.
								// Keep or delete the leading
								// period as requested.
			if( keepPeriod )
				extension = name.substring( periodPos );
			else
				extension = name.substring( periodPos + 1 );
		}
								// No extension.
		else
		{
			extension = "";
		}

		return extension;
	}

	/** Get MIME type for a filename.
	 *
	 *	@param	fileName	Name of file for which mime type is desired.
	 *
	 *	@return				The mime type, e.g., "text/plain".
	 *
	 *	<p>
	 *	When the file name's extension is not found in the mime types
	 *	hash table, a mime type of "application/octet-stream" is returned.
	 *	</p>
	 */

	public static String getContentTypeFor( String fileName )
	{
		return mimeTypeMapper.getContentTypeFor( fileName );
	}

	/** Checks if a file exists.
	 *
	 *	@param	fileName	The file name to check for existence.
	 *
	 *	@return				True if file exists.
	 */

    public static boolean fileExists( String fileName )
    {
		return ( new File( fileName ) ).exists();
    }

	/**	Expand file name wildcard.
	 *
	 *	@param	fileSpec	File spec possibly containing "*" wildcard.
	 *
	 *	@return				String array of expanded file names.
	 *						Only file names for existing files are
	 *						returned, so result may be zero length
	 *						string array.
	 */

	public static String[] expandWildcards( String fileSpec )
	{
		String[] result	= new String[ 0 ];

								//	See if the file spec contains
								//	any "*" wildcard characters.

		String fileName	= fileSpec;

		int wildIndex	= fileName.indexOf( "*" );

		if ( wildIndex == -1 )
		{
								//	File spec does not contain
								//	wild cards, so it is a plain
								//	file name.  If the file it names exists,
								//	return that file name.

			if ( new File( fileName ).exists() )
			{
	    			result	= new String[]{ fileName };
			}
		}
		else
		{
								//	File spec contains one or more
								//	wildcard characters.
								//
								//	Remove duplicate "*" wildcard characters.

			fileName	= StringUtils.replaceAll( fileName , "**" , "*" );

								//	Get position of first "*".

			wildIndex	= fileName.indexOf( "*" );

								//	Find last occurrence of file
								//	separator string.  If none,
								//	there is no directory specification.

			int index	= fileName.lastIndexOf( FILE_SEPARATOR );
			String path	= "";

								//	Extract the directory path
								//	from the file name if present.
			if ( index > 0 )
			{
				path	= fileName.substring( 0 , index );
			}
								//	Otherwise use current directory path.
			else
			{
				path	= FileUtils.getCurrentDirectory();
			}

			File pathDir	= new File( path );

								//	Check that directory path exists,

			if ( pathDir.exists() )
			{
				final String prefix	=
					fileName.substring( index + 1 , wildIndex );

				String suff			= "";

				if ( ( wildIndex + 1 ) < fileName.length() )
				{
					suff	=
						fileName.substring(
							wildIndex + 1 , fileName.length() );
				}

				final String suffix = suff;

				String listFiles[]	=
					pathDir.list
					(
						new FilenameFilter()
						{
							public boolean accept
							(
								File cwd ,
								String name
							)
							{
								return
									(	name.startsWith( prefix ) &&
										name.endsWith( suffix )
									);
							};
						}
					);

				for ( int i = 0 ; i < listFiles.length ; i++ )
				{
					listFiles[ i ]	=
						path.concat( FILE_SEPARATOR + listFiles[ i ] );
				}

				if ( listFiles.length > 0 )
				{
					Arrays.sort( listFiles );

					result	= listFiles;
				}
			}
		}

    	return result;
	}

	/**	Fix file separators.
	 *
	 *	@param	fileName	File name to fix.
	 *
	 *	@return				File name with file separators fixed.
	 *
	 *	<p>
	 *	Escapes the file separators if they are "\" characters.
	 *	</p>
	 */

	public static String fixFileSeparators( String fileName )
	{
		String fixedFileName	= "";

		if ( FILE_SEPARATOR.equals( "\\" ) )
		{
			fixedFileName	=
				StringUtils.replaceAll(
					fileName , "\\" , "<backslash>" );

			fixedFileName	=
				StringUtils.replaceAll(
					fixedFileName , "<backslash>" , "\\\\" );
		}
		else
		{
			fixedFileName	= fileName;
		}

		return fixedFileName;
	}

	/**	Expand the file name wildcards.
	 *
	 *	@param	wildCardNames	File names with possible wildcards.
	 *
	 *	@return					String array of expanded file names.
	 *
	 *	<p>
	 *	File names expressed as URLs are left untouched and
	 *	any wildcard characters they contains are left as-is.
	 *	</p>
	 */

	public static String[] expandFileNameWildcards
	(
		String[] wildCardNames
	)
	{
		List<String> fileNames	= ListFactory.createNewList();

		for ( int i = 0 ; i < wildCardNames.length ; i++ )
		{
			if ( URLUtils.isURL( wildCardNames[ i ] ) )
			{
				fileNames.add( wildCardNames[ i ] );
			}
			else
			{
				String[] expandedFileNames	=
					expandWildcards( wildCardNames[ i ] );

				fileNames.addAll( Arrays.asList( expandedFileNames ) );
			}
		}

		String[] result	=
			(String[])fileNames.toArray( new String[ fileNames.size() ] );

		Arrays.sort( result );

		return result;
	}

	/**	Create versioned file name.
	 *
	 *	@param	fileName	Candidate file name.
	 *
	 *	@return				Possibly revised file name with
	 *							version number added if
	 *							candidate file name already exists.
	 */

	public static String createVersionedFileName( String fileName )
	{
		String result	= fileName;

		if ( new File( fileName ).exists() )
		{
			result	= createVersionedFileName( fileName , 1 , "%03d" );
		}

		return result;
	}

	/**	Create versioned file name.
	 *
	 *	@param	fileName			Candidate file name.
	 *	@param	versionNumber	Candidate version number .
	 *	@param	versionFormat		PrintF format for version number.
	 *
	 *	@return				Possibly revised file name with
	 *							version number added if
	 *							candidate file name already exists.
	 *							The version number is adjusted
	 *							as necessary.
	 */

	public static String createVersionedFileName
	(
		String fileName ,
		int versionNumber ,
		String versionFormat
	)
	{
		int n	= fileName.lastIndexOf( '.' );

		StringBuilder sb		= new StringBuilder();

		new Formatter( sb ).format
		(
			versionFormat ,
			versionNumber
		);

		String sVersionNumber	= sb.toString();

		String versionedFileName	= fileName + "-" + sVersionNumber;

		if ( n >= 0 )
		{
			versionedFileName	=
				fileName.substring( 0 , n ) + "-" + sVersionNumber +
				fileName.substring( n , fileName.length() );
		}

		if ( new File( versionedFileName ).exists() )
		{
			versionedFileName	=
				createVersionedFileName
				(
					fileName ,
					++versionNumber ,
					versionFormat
				);
		}

		return versionedFileName;
	}

	/** Hide default no-args constructor. */

	private FileNameUtils()
	{
		throw new UnsupportedOperationException();
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


