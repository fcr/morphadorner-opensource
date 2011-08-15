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

import java.io.*;

import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;

import org.w3c.dom.*;
import org.w3c.dom.ls.*;

import java.util.regex.*;

public class XGMisc
{
	/**	Allow use of LS 3.0 features for output. */

	protected static boolean allowLS30	= true;

	/**	Print a <code>Node</code> into a file.
	 *
	 *	@param	node		The <code>Node</code> to print.
	 *	@param	outputName	The name out the output file.
	 *
	 *	@return <code>-1</code> if the operation failed,
	 *			<code>1</code> otherwise.
	 */

	public static int printNodeToFile( Node node , String outputName )
		throws IOException , FileNotFoundException
	{
		int result	= -1;

		try
		{
								//	Create builder DOM.

			DocumentBuilder builder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();

			DOMImplementation domImpl = builder.getDOMImplementation();

			if ( allowLS30 && domImpl.hasFeature( "LS" , "3.0" ) )
			{
								//	Create implementation.

				DOMImplementationLS domImplLS =
					( (DOMImplementationLS)domImpl );

								//	Output.

				LSOutput lsOutput = domImplLS.createLSOutput();

								//	Serializer and output.

				LSSerializer lsSerializer = domImplLS.createLSSerializer();

				lsSerializer.getDomConfig().setParameter(
					"xml-declaration" , false );

//				lsSerializer.getDomConfig().setParameter(
//					"format-pretty-print" , true );

				FileOutputStream fos	=
					new FileOutputStream( outputName );

				lsOutput.setByteStream( fos );
				lsSerializer.write( node , lsOutput );
				fos.close();

				result	= 1;
			}
			else
			{
				Transformer identityTransformer =
					TransformerFactory.newInstance().newTransformer();

				Properties outputProps = new Properties();

				outputProps.setProperty( "encoding" , "utf-8" );
				outputProps.setProperty( "indent" , "yes" );
				outputProps.setProperty( "omit-xml-declaration" , "yes" );

				identityTransformer.setOutputProperties( outputProps );

				DOMSource source			= new DOMSource( node );

				FileOutputStream fos		=
					new FileOutputStream( outputName );

				StreamResult streamResult 	= new StreamResult( fos );

				identityTransformer.transform( source , streamResult );

				fos.close();

				result	= 1;
			}
		}
		catch ( javax.xml.parsers.ParserConfigurationException e )
		{
		}
		catch ( javax.xml.transform.TransformerConfigurationException e )
		{
		}
		catch ( javax.xml.transform.TransformerException e )
		{
		}

		return result;
	}

	/** Print a <code>Node</code> into a string.
	 *
	 *	@param node the <code>Node</code> to print
	 *	@return XML string if operation succeeded, null otherwise.
	 */

	public static String printNodeToString( Node node )
		throws IOException , FileNotFoundException
	{
		String result	= null;

		try
		{
								//	Create builder DOM.

			DocumentBuilder builder =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();

			DOMImplementation domImpl = builder.getDOMImplementation();

			if ( allowLS30 && domImpl.hasFeature( "LS" , "3.0" ) )
			{
								//	Create implementation.

				DOMImplementationLS domImplLS =
					( (DOMImplementationLS)domImpl );

								//	Output.

				LSOutput lsOutput = domImplLS.createLSOutput();

								//	Serializer and output.

				LSSerializer lsSerializer = domImplLS.createLSSerializer();

				lsSerializer.getDomConfig().setParameter(
					"xml-declaration" , false );

//				lsSerializer.getDomConfig().setParameter(
//					"format-pretty-print" , true );

				ByteArrayOutputStream bos	= new ByteArrayOutputStream();

				lsOutput.setByteStream( bos );
				lsSerializer.write( node , lsOutput );
				bos.close();

				result	= bos.toString( "utf-8" );
			}
			else
			{
				Transformer identityTransformer =
					TransformerFactory.newInstance().newTransformer();

				Properties outputProps = new Properties();

				outputProps.setProperty( "encoding" , "utf-8" );
				outputProps.setProperty( "indent" , "yes" );
				outputProps.setProperty( "omit-xml-declaration" , "yes" );

				identityTransformer.setOutputProperties( outputProps );

				DOMSource source			= new DOMSource( node );

				ByteArrayOutputStream bos	= new ByteArrayOutputStream();

				StreamResult streamResult 	= new StreamResult( bos );

				identityTransformer.transform( source , streamResult );

				bos.close();

				result	= bos.toString( "utf-8" );
			}
		}
		catch ( javax.xml.parsers.ParserConfigurationException e )
		{
		}
		catch ( javax.xml.transform.TransformerConfigurationException e )
		{
		}
		catch ( javax.xml.transform.TransformerException e )
		{
		}

		return result;
	}

	/**	Returns a file from its name. Expands environment variables and
	 *	get canonical path.
	 *
	 *	@param strFileName the initial file name
	 *	@return File
	 */

	public static File getFile( String strFileName )
		throws IOException
	{
		Pattern p		= Pattern.compile( "\\$([a-zA-Z_]+)" );
		Matcher m		= p.matcher( strFileName );

		StringBuffer sb	= new StringBuffer();

		while ( m.find() )
		{
			if ( System.getenv( m.group( 1 ) ) != null )
			{
				m.appendReplacement( sb , System.getenv( m.group( 1 ) ) );
			}
			else
			{
				m.appendReplacement( sb , "" );
			}
		}

		m.appendTail( sb );

		return new File( sb.toString() );
	}

	/**	Writes a text into a file.
	 *
	 *	@param	strText			The text to write.
	 *	@param	strFileName	The URI of the file.
	 *
	 *	@throws	IOException		If file cannot be written.
	 */

	public static void write( String strText , String strFileName )
		throws IOException
	{
		OutputStreamWriter myFile =
			new OutputStreamWriter(
				new FileOutputStream( strFileName ) , "utf-8" );

		BufferedWriter myBuffer = new BufferedWriter( myFile );

		myBuffer.write( strText , 0 , strText.length() );
		myBuffer.flush();
		myBuffer.close();
		myFile.close();
	}
}

