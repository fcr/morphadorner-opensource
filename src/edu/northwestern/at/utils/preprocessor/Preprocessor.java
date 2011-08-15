package edu.northwestern.at.utils.preprocessor;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;

/**	Simple preprocessor.
 */

public class Preprocessor
{
	/**	Defined symbols. */

	protected Map<String, String>definedSymbols	=
		new TreeMap<String, String>();

	/**	true if skipping lines. */

	protected boolean skipState				= false;

	/**	Line skip nesting state. */

	protected Stack<Boolean> skipStates		= new Stack<Boolean>();

	/**	Count of lines processed. */

	protected int lineCount					= 0;

	/**	Writer to which to output preprocessed text. */

	protected BufferedWriter outputWriter	= null;

	/**	Name of source file to preprocess. */

	protected String sourceFileName			= null;

	/**	Name of sestination file to receive proprocessed text. */

	protected String destFileName			= null;

	/**	Preprocessor directive lead-in string. */

	protected String directiveLeadIn		= "//#";

	/**	--- Directives --- */

	protected String defineDirective	= directiveLeadIn + "define";
	protected String undefineDirective	= directiveLeadIn + "undefine";
	protected String includeDirective	= directiveLeadIn + "include";
	protected String ifdefDirective		= directiveLeadIn + "ifdef";
	protected String ifndefDirective	= directiveLeadIn + "ifndef";
	protected String endifDirective		= directiveLeadIn + "endif";

	/**	Create preprocessor.
	 */

	public Preprocessor()
	{
	}

	/**	Create preprocessor with specified directive lead-in string.
	 */

	public Preprocessor( String leadIn )
	{
		directiveLeadIn		= leadIn;

		defineDirective		= directiveLeadIn + "define";
		undefineDirective	= directiveLeadIn + "undefine";
		includeDirective		= directiveLeadIn + "include";
		ifdefDirective		= directiveLeadIn + "ifdef";
		ifndefDirective		= directiveLeadIn + "ifndef";
		endifDirective		= directiveLeadIn + "endif";
	}

	/**	Define a symbol.
	 *
	 *	@param	symbol		The symbol to define.
	 *	@param	value		Symbol value.
	 */

	protected void defineSymbol( String symbol , String value )
	{
		definedSymbols.put( symbol , value );
	}

	/**	Undefine a symbol.
	 *
	 *	@param	symbol	Symbol to undefine.
	 */

	protected void undefineSymbol( String symbol )
	{
		definedSymbols.remove( symbol );
	}

	/**	Check if symbol defined.
	 *
	 *	@param	symbol	Symbol name to check if defined.
	 *
	 *	@return			true if symbol defined (not null),
	 *					false otherwise.
	 */

	protected boolean symbolDefined( String symbol )
	{
		return ( definedSymbols.get( symbol ) != null );
	}

	/**	Replace defined symbols in string.
	 *
	 *	@param	s	The string containing symbols to replace.
	 *
	 *	@return		The string with defined symbols replaced by
	 *				their values.
	 */

	protected String resolve( String s )
	{
		for ( String symbolName : definedSymbols.keySet() )
		{
			if ( s.indexOf( symbolName ) >= 0 )
			{
				String symbolValue	= definedSymbols.get( symbolName );

				s	= StringUtils.replaceAll( s , symbolName , symbolValue );
			}
		}

		return s;
	}

	/**	Process one file.
	 *
	 *	@param	sourceFileName	File to preprocess.
	 *	@param	outputWriter	Output writer.  Must be open.
	 *	@param	quiet			True to suppress progress messages.
	 *
	 *	@throws	Exception		If something goes wrong.
	 */

	protected void processFile
	(
		String sourceFileName ,
		BufferedWriter outputWriter ,
		boolean quiet
	)
		throws Exception
	{
								//	Say we're preprocessing file.
		if ( !quiet )
		{
			System.out.println( "   Processing " + sourceFileName );
		}

								//	Open file to preprocess.

		BufferedReader inputReader	=
			new BufferedReader
			(
				new UnicodeReader
				(
					new FileInputStream( sourceFileName ) , "8859_1"
				)
			);
								//	Read first line of file to preprocess.

		String sourceLine	= inputReader.readLine();

								//	Loop over lines of text in file.

		while ( sourceLine != null )
		{
								//	Increment line count.
			lineCount++;

								//	Line not processed yet.

			boolean processed			= false;

								//	Create string tokenizer over line.

			StringTokenizer tokenizer	=
				new StringTokenizer( sourceLine );

								//	If line not empty, look for
								//	preprocessor commands.

			if ( tokenizer.hasMoreTokens() )
			{
								//	Get first token in line.
								//	May be a preprocessor directive.

				String firstToken		= tokenizer.nextToken();
				boolean hasMoreTokens	= tokenizer.hasMoreTokens();

								//	#include to include text from
								//	another file?

				if	( firstToken.equals( includeDirective ) )
				{
					if ( hasMoreTokens )
					{
						String includeFileName	= tokenizer.nextToken();

						includeFileName			=
							includeFileName.replaceAll( "\"" , "" );

						includeFileName			=
							new File( includeFileName ).getAbsolutePath();

						processFile(
							includeFileName , outputWriter , quiet );
					}

					processed	= true;
				}
								//	#define to define a symbol?

				else if ( firstToken.equals( defineDirective ) )
				{
					if ( hasMoreTokens )
					{
						String symbolName	= tokenizer.nextToken();
						String value			= tokenizer.nextToken( "" );

						defineSymbol( symbolName , value );
					}

					processed			= true;
				}
								//	#undefine to undefine a symbol?

				else if ( firstToken.equals( undefineDirective ) )
				{
					if ( hasMoreTokens )
					{
						undefineSymbol( tokenizer.nextToken() );
					}

					processed			= true;
				}
								//	#ifdef to check if a symbol is defined?

				else if ( firstToken.equals( ifdefDirective ) )
				{
					if ( hasMoreTokens )
					{
						String symbolName	= tokenizer.nextToken();

						skipStates.push( new Boolean( skipState ) );

						skipState			= !symbolDefined( symbolName );
					}

					processed			= true;
				}
								//	#ifndef to check if a symbol is undefined?

				else if ( firstToken.equals( ifndefDirective ) )
				{
					if ( hasMoreTokens )
					{
						String symbolName	= tokenizer.nextToken();

						skipStates.push( new Boolean( skipState ) );

						skipState			= symbolDefined( symbolName );
					}

					processed			= true;
				}
								//	#endif to end a #ifdef oir #ifndef?

				else if ( firstToken.equals( endifDirective ) )
				{
					try
					{
						skipState	=
							((Boolean)skipStates.pop()).booleanValue();
					}
					catch ( EmptyStackException e )
					{
						throw new Exception(
							"Mismatched endif at line " + lineCount );
					}

					processed			= true;
				}
			}

			if ( !( processed || skipState ) )
			{
				outputWriter.write( resolve( sourceLine ) );
				outputWriter.newLine();
			}

			sourceLine	= inputReader.readLine();
		}
								//	Close input file.
       	try
       	{
       		if ( inputReader != null ) inputReader.close();
       	}
       	catch ( Exception e )
       	{
       	}
								//	Report if ifdef/ifndef/endif
								//	not matched up.

		if ( !skipStates.empty() )
			throw new Exception( "Mismatched endif at line " + lineCount );
	}

	/**	Preprocess one file.
	 *
	 *	@param	sourceFileName	The source file to preprocess.
	 *	@param	destFileName	The destination file.
	 *	@param	quiet			true to suppress progress messages.
	 *
	 *	@return					true if file preprocessed successfully.
	 */

	public boolean preprocess
	(
		String sourceFileName ,
		String destFileName ,
		boolean quiet
	)
	{
								//	Assume preprocessing fails.

		boolean result	= false;

		if ( !quiet )
		{
			System.out.println(
				"Preprocessing " + sourceFileName + " to " + destFileName );
		}

		skipState		= false;
		skipStates		= new Stack<Boolean>();
		lineCount		= 0;
		definedSymbols	= MapFactory.createNewMap();

		long sourceFileTime	= 0;

		try
		{
								//	Get modification time for source file.

			File sourceFile	= new File( sourceFileName );
			sourceFileTime	= sourceFile.lastModified();

			outputWriter	=
				new BufferedWriter
				(
					new OutputStreamWriter
					(
						new FileOutputStream( destFileName ) , "8859_1"
					)
				);

			processFile( sourceFileName , outputWriter , quiet );

								//	Say preprocessing went OK.
			result	= true;
		}
		catch( Exception e )
		{
			if ( !quiet ) System.out.println( e.getMessage() );
		}
		finally
		{
			try
			{
				if ( outputWriter != null ) outputWriter.close();
			}
			catch ( Exception e )
			{
			}
								//	Set modification time for preprocessed
								//	file to that of source file.
			if ( result )
			{
				File destFile		= new File( destFileName );

				if ( destFile != null )
				{
					destFile.setLastModified( sourceFileTime );
				}
			}
		}

		return result;
	}

	/**	Preprocess one file.
	 *
	 *	@param	sourceFileName	The source file to preprocess.
	 *	@param	destFileName	The destination file.
	 *
	 *	@return					true if file preprocessed successfully.
	 */

	public boolean preprocess
	(
		String sourceFileName ,
		String destFileName
	)
	{
		return preprocess( sourceFileName , destFileName , false );
	}

	/**	Get the number of lines preprocessed.
	 *
	 *	@return		The number of lines preprocessed.
	 */

	public int getLineCount()
	{
		return lineCount;
	}

	/**	Main program.
	 *
	 *	@param	args	Command line arguments.
	 */

	public static void main( String args[] )
	{
		Preprocessor preproc	= new Preprocessor();

		boolean result			=
			preproc.preprocess( args[ 0 ] , args[ 1 ] );

		System.out.println( preproc.getLineCount() + " lines processed." );

		if ( !result ) System.exit( 1 );
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


