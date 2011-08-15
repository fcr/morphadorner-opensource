package edu.northwestern.at.utils.net.mime;

/*	Please see the license information at the end of this file. */

import java.net.FileNameMap;
import java.util.*;

import edu.northwestern.at.utils.*;

/** Maps file extensions to mime types.
 */

public class MimeTypeMapper implements FileNameMap
{

	/** Array of mappings between mime types and file extensions.
	 *
	 *	<p>
	 *	The first entry is the mime type, the second is the
	 *	corresponding extension.  Some mime types are associated
	 *	with more then one extension.
	 *	</p>
	 */

	protected static final String[][] mimeDefs =
	{
		{ "application/mac-binhex40", "hqx" },
		{ "application/mac-compactpro", "cpt" },
		{ "application/msword", "doc" },
		{ "application/octet-stream", "bin" },
		{ "application/octet-stream", "dms" },
		{ "application/octet-stream", "lha" },
		{ "application/octet-stream", "lzh" },
		{ "application/octet-stream", "com" },
		{ "application/octet-stream", "exe" },
		{ "application/octet-stream", "class" },
		{ "application/oda", "oda" },
		{ "application/pdf", "pdf" },
		{ "application/postscript","ai" },
		{ "application/postscript","eps" },
		{ "application/postscript","ps" },
		{ "application/powerpoint","ppt" },
		{ "application/rtf", "rtf" },
		{ "application/x-ayeware-styledtext" , "stxt" },
		{ "application/x-bcpio", "bcpio" },
		{ "application/x-cdlink", "vcd" },
		{ "application/x-compress","Z" },
		{ "application/x-cpio", "cpio" },
		{ "application/x-csh", "csh" },
		{ "application/x-director","dcr" },
		{ "application/x-director","dir" },
		{ "application/x-director","dxr" },
		{ "application/x-dvi", "dvi" },
		{ "application/x-gtar", "gtar" },
		{ "application/x-gzip", "gz" },
		{ "application/x-hdf", "hdf" },
		{ "application/x-httpd-cgi", "cgi" },
		{ "application/x-koan", "skp" },
		{ "application/x-koan", "skd" },
		{ "application/x-koan", "skt" },
		{ "application/x-koan", "skm" },
		{ "application/x-latex", "latex" },
		{ "application/x-mif", "mif" },
		{ "application/x-netcdf", "nc" },
		{ "application/x-netcdf", "cdf" },
		{ "application/x-sh", "sh" },
		{ "application/x-shar", "shar" },
		{ "application/x-stuffit", "sit" },
		{ "application/x-sv4cpio", "sv4cpio" },
		{ "application/x-sv4crc", "sv4crc" },
		{ "application/x-tar", "tar" },
		{ "application/x-tcl", "tcl" },
		{ "application/x-tex", "tex" },
		{ "application/x-texinfo", "texinfo" },
		{ "application/x-texinfo", "texi" },
		{ "application/x-troff", "t" },
		{ "application/x-troff", "tr" },
		{ "application/x-troff", "roff" },
		{ "application/x-troff-man", "man" },
		{ "application/x-troff-me","me" },
		{ "application/x-troff-ms","ms" },
		{ "application/x-ustar", "ustar" },
		{ "application/x-wais-source", "src" },
		{ "application/zip", "zip" },
		{ "audio/basic", "au" },
		{ "audio/basic", "snd" },
		{ "audio/mpeg", "mpga" },
		{ "audio/mpeg", "mp2" },
		{ "audio/mpeg", "mp3" },
		{ "audio/x-aiff", "aif" },
		{ "audio/x-aiff", "aiff" },
		{ "audio/x-aiff", "aifc" },
		{ "audio/x-pn-realaudio", "ram" },
		{ "audio/x-pn-realaudio-plugin", "rpm" },
		{ "audio/x-realaudio", "ra"},
		{ "audio/x-wav", "wav" },
		{ "chemical/x-pdb", "pdb" },
		{ "chemical/x-pdb", "xyz" },
		{ "image/gif", "gif" },
		{ "image/ief", "ief" },
		{ "image/jpeg", "jpeg" },
		{ "image/jpeg", "jpg" },
		{ "image/jpeg", "jpe" },
		{ "image/png", "png" },
		{ "image/tiff", "tiff" },
		{ "image/tiff", "tif" },
		{ "image/x-cmu-raster", "ras" },
		{ "image/x-portable-anymap", "pnm" },
		{ "image/x-portable-bitmap", "pbm" },
		{ "image/x-portable-graymap", "pgm" },
		{ "image/x-portable-pixmap", "ppm" },
		{ "image/x-rgb", "rgb" },
		{ "image/x-xbitmap", "xbm" },
		{ "image/x-xpixmap", "xpm" },
		{ "image/x-xwindowdump", "xwd" },
		{ "text/html", "html" },
		{ "text/html", "htm" },
		{ "text/plain", "txt" },
		{ "text/richtext", "rtx" },
		{ "text/tab-separated-values", "tsv" },
		{ "text/x-setext", "etx" },
		{ "text/x-sgml", "sgml" },
		{ "text/x-sgml", "sgm" },
		{ "video/mpeg", "mpeg" },
		{ "video/mpeg", "mpg" },
		{ "video/mpeg", "mpe" },
		{ "video/quicktime", "qt" },
		{ "video/quicktime", "mov" },
		{ "video/x-msvideo", "avi" },
		{ "video/x-sgi-movie", "movie" },
		{ "x-conference/x-cooltalk", "ice" },
		{ "x-world/x-vrml", "wrl" },
		{ "x-world/x-vrml", "vrml" }
	};

	/** Map between mime types and file extensions. */

	protected static Map<String, String> mimeTypes =
		MapFactory.createNewMap( 256 );

	/** Null args constructor. */

	public MimeTypeMapper()
	{
	}

	/** Returns MIME type for a filename.
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

	public String getContentTypeFor( String fileName )
	{
								// Set default mime type.

		String result = "application/octet-stream";

								// Extract extension, if any, from
								// file name.
		String extension =
			FileNameUtils.getFileExtension( fileName , false ).toLowerCase();

								// Get mime type for extension.

		String mimeType = mimeTypes.get( extension );

								// If we found one, return it,
								// other return the default.

		if ( mimeType != null ) result = mimeType;

		return result;
	}

	/** Returns list of extensions matching main MIME type.
	 *
	 *	@param	mainMimeType	The main mime type, e.g., "audio".
	 *
	 *	@return					The list of matching file extensions.
	 */

	public static String[] getMatchingExtensions( String mainMimeType )
	{
		String[] result = new String[ 0 ];

		String mimeString =
			StringUtils.safeString( mainMimeType ).toLowerCase();

		if ( mimeString.length() > 0 )
		{
			List<String> extensions = ListFactory.createNewList();

			for ( int i = 0; i < mimeDefs.length; i++ )
			{
				String mimeType		= mimeDefs[ i ][ 0 ];
				String extension	= mimeDefs[ i ][ 1 ];

				if ( mimeType.startsWith( mimeString ) )
					extensions.add( '.' + extension );
			}

			int nExtensions = extensions.size();

			result = new String[ nExtensions ];

			for ( int i = 0; i < nExtensions; i++ )
			{
				result[ i ] = extensions.get( i );
			}
		}

		return result;
	}

	/** Static initializer for hash table. */

	static
	{
  		for ( int i = 0; i < mimeDefs.length; i++ )
  		{
			mimeTypes.put( mimeDefs[ i ][ 1 ] , mimeDefs[ i ][ 0 ] );
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



