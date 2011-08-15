package edu.northwestern.at.utils.xml;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.net.*;

import javax.xml.parsers.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;
import javax.xml.XMLConstants;

import org.xml.sax.*;
import org.w3c.dom.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import edu.northwestern.at.utils.*;

/**	XML DOM utilities.
 */

public class DOMUtils
{
	/**	Parses an XML file.
	 *
	 *	@param	file		File.
	 *
	 *	@return				DOM document.
	 *
	 *	@throws	Exception
	 */

	public static Document parse (File file)
		throws IOException, ParserConfigurationException, SAXException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(file);
	}

	/**	Parses an XML file.
	 *
	 *	@param	path		File path.
	 *
	 *	@return				DOM document.
	 *
	 *	@throws	Exception
	 */

	public static Document parse (String path)
		throws IOException, ParserConfigurationException, SAXException
	{
		return parse(new File(path));
	}

	/**	Parses XML document from URL.
	 *
	 *	@param	url		URL.
	 *
	 *	@return				DOM document.
	 *
	 *	@throws	Exception
	 */

	public static Document parse (URL url)
		throws IOException, ParserConfigurationException, SAXException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(url.openStream());
	}

	/**	Parses XML document from a string.
	 *
	 *	@param	text	Document text string.
	 *
	 *	@return			DOM document.
	 *
	 *	@throws	Exception
	 */

	public static Document parseText (String text)
		throws IOException, ParserConfigurationException, SAXException
	{
		DocumentBuilderFactory factory =
			DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(text)));
	}

	/**	Gets a child element of a node by name.
	 *
	 *	@param	node	Node.
	 *
	 *	@param	name	Name.
	 *
	 *	@return			First child element with given tag name, or
	 *					null if none found.
	 */

	public static Element getChild (Node node, String name) {
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) continue;
			if (child.getNodeName().equals(name)) return (Element)child;
		}
		return null;
	}

	/**	Find child node name matching regular expression.
	 *
	 *	@param	parent		Node whose child we want.
	 *	@param	namePat		Regular expression for child name.
	 *
	 *	@return				The first child whose name matches
	 *						the specified namePat, or null if none matches.
	 */

	public static Element findChild( Node parent , String namePat )
	{
		Element result	= null;

		if ( parent != null )
		{
			NodeList children	= parent.getChildNodes();

			for ( int i = 0 ; i < children.getLength() ; i++ )
			{
				Node child	= (Node)children.item( i );

				if ( child.getNodeType() != Node.ELEMENT_NODE ) continue;

				if ( child.getNodeName().matches( namePat ) )
				{
					result	= (Element)child;
					break;
				}
			}
		}

		return result;
	}

	/**	Gets the last child element of a node by name.
	 *
	 *	@param	node	Node.
	 *
	 *	@param	name	Name.
	 *
	 *	@return			Last child element with given tag name, or
	 *					null if none found.
	 */

	public static Element getLastChild (Node node, String name) {
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		for (int i = numChildren-1; i >= 0; i--) {
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) continue;
			if (child.getNodeName().equals(name)) return (Element)child;
		}
		return null;
	}

	/**	Gets a child element of a node by name and attribute value.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	name		Name.
	 *
	 *	@param	attrName	Attribute name.
	 *
	 *	@param	attrValue	Attribute value.
	 *
	 *	@return				First child element with given tag name and
	 *						given attribute value, or null if none found.
	 */

	public static Element getChild (Node node, String name,
		String attrName, String attrValue)
	{
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) continue;
			if (child.getNodeName().equals(name)) {
				Element el = (Element)child;
				if (attrValue.equals(el.getAttribute(attrName))) return el;
			}
		}
		return null;
	}

	/**	Gets text for a node.
	 *
	 *	@param	node		Node.
	 *
	 *	@return				Value of first child text node, or the empty
	 *						string if none found, with leading and trailing
	 *						white space trimmed.
	 */

	public static String getText (Node node) {
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE)
				return child.getNodeValue().trim();
		}
		return "";
	}

	/**	Sets text for a node.
	 *
	 *	<p>Sets the value of the first child text node, if any.
	 *	Creates new child text node if none found.</p>
	 *
	 *	@param	node		Node.
	 *
	 *	@param	text		New text for the node.
	 */

	public static void setText (Node node, String text) {
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				child.setNodeValue(text);
				return;
			}
		}
		Text child	= node.getOwnerDocument().createTextNode(text);
		node.appendChild(child);
	}

	/**	Gets all the text for a node.
	 *
	 *	@param	node		Node.
	 *
	 *	@return				Values of each child text node concatenated
	 *						together in order, with leading and trailing
	 *						white space trimmed.
	 */

	public static String getAllText (Node node) {
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		String result = "";
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE)
				result = result + child.getNodeValue();
		}
		return result.trim();
	}

	/**	Gets the child elements of a node by name.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	name		Name.
	 *
	 *	@return				All of the child elements of the node with
	 *						the given tag name, in order.
	 */

	public static List<Node> getChildren (Node node, String name) {
		List<Node> result = ListFactory.createNewList();
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) continue;
			if (child.getNodeName().equals(name))
				result.add(child);
		}
		return result;
	}

	/**	Gets the child elements of a node by name pattern.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	name		Name as a regular expression.
	 *
	 *	@return				All of the child elements of the node with
	 *						the given matching tag name, in order.
	 */

	public static List<Node> findChildren (Node node, String name) {
		List<Node> result = ListFactory.createNewList();
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) continue;
			if (child.getNodeName().matches(name))
				result.add(child);
		}
		return result;
	}

	/**	Gets the child elements of a node by name and attribute value.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	name		Name.
	 *
	 *	@param	attrName	Attribute name.
	 *
	 *	@param	attrValue	Attribute value.
	 *
	 *	@return				All of the child elements of the node with
	 *						the given tag name which have the given
	 *						attribute value, in order.
	 */

	public static List<Element> getChildren (Node node, String name,
		String attrName, String attrValue)
	{
		List<Node> children = getChildren(node, name);
		List<Element> result = ListFactory.createNewList();
		for (Iterator it = children.iterator(); it.hasNext(); ) {
			Element el = (Element)it.next();
			if (el.getAttribute(attrName).equals(attrValue))
				result.add(el);
		}
		return result;
	}

	/**	Gets a descendant element of a node.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	path		Path to descendant, using tag names of child
	 *							elements separated by "/".
	 *
	 *	@return				Descendant element, or null if none found.
	 */

	public static Element getDescendant (Node node, String path) {
		StringTokenizer tok = new StringTokenizer(path, "/");
		while (tok.hasMoreTokens()) {
			node = getChild(node, tok.nextToken());
			if (node == null) return null;
		}
		return (Element)node;
	}

	/**	Gets descendant elements in a document by tag name.
	 *
	 *	@param	doc			Document.
	 *
	 *	@param	tagName		Tag name.
	 *
	 *	@return				List of descendant nodes with specified tag.
	 *						List may be empty.
	 */

	public static List<Node> getDescendants (Document doc, String tagName) {
		List<Node> result = ListFactory.createNewList();
		NodeList nodeList = doc.getElementsByTagName(tagName);
		for (int i=0; i<nodeList.getLength(); i++) {
			result.add(nodeList.item(i));
		}
		return result;
	}

	/**	Adds descendant elements of a node to a list.
	 *
	 *	@param	node			Node.
	 *
	 *	@param	descendants		List of descendants.
	 */

	public static void addDescendants (Node node, List<Node> descendants) {
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				descendants.add(child);
				addDescendants(child, descendants);
			}
		}
	}

	/**	Gets descendant elements of a node.
	 *
	 *	@param	node		Node.
	 *
	 *	@return				List of descendant elemen nodes in no
	 *						particular order.
	 *						List may be empty.
	 */

	public static List<Node> getDescendants (Node node) {
		List<Node> result = ListFactory.createNewList();
		addDescendants(node, result);
		return result;
	}

	/**	Creates a new empty DOM document.
	 *
	 *	@return		New empty DOM document.
	 *
	 *	@throws	ParserConfigurationException
	 */

	public static Document newDocument ()
		throws ParserConfigurationException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.newDocument();
	}

    /**	Parse a string containing XML and return a DocumentFragment.
     *
     *	@param	doc			The document,
     *	@param	fragment	The XML text fragment.
     *
     *	@return				The document fragment.
     */

	public static DocumentFragment parseXML (Document doc, String fragment)
	{
								//	Wrap fragment in an arbitrary element.

		fragment = "<fragment>" + fragment + "</fragment>";

		try	{
								// Create DOM builder and parse fragment.

			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();

			Document d = factory.newDocumentBuilder().parse(
				new InputSource(new StringReader(fragment)));

								// Import nodes of new document into
								// doc so they will be compatible with doc

			Node node = doc.importNode(d.getDocumentElement(), true);

								// Create document fragment node
								// to hold the new nodes

			DocumentFragment docfrag = doc.createDocumentFragment();

								// Move nodes into fragment

			while (node.hasChildNodes()) {
				docfrag.appendChild(node.removeChild(node.getFirstChild()));
			}

			// Return the fragment

			return docfrag;

		} catch (SAXException e) {

								// Parsing error occurred --
								// XML input is not valid

		} catch (ParserConfigurationException e) {
		} catch (IOException e) {
		}

		return null;
	}

	/**	Saves a DOM document to an XML file in utf-8.
	 *
	 *	@param	document	DOM document.
	 *	@param	path		Output file path.
	 *
	 *	@throws	TransformerException, IOException
	 */

	public static void save (Document document, String path)
		throws TransformerException, IOException
	{
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		PrintWriter pw = new PrintWriter(new FileOutputStream(path));
		StreamResult destination = new StreamResult(pw);
		transformer.transform(source, destination);
		pw.close();
	}

	/**	Saves a DOM document to an XML file in utf-8.
	 *
	 *	@param	document	DOM document.
	 *	@param	dtdName		The DTD name.
	 *	@param	path		Output file path.
	 *
	 *	@throws	TransformerException, IOException
	 */

	public static void save (Document document, String dtdName, String path)
		throws TransformerException, IOException
	{
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtdName);
		PrintWriter pw = new PrintWriter(new FileOutputStream(path));
		StreamResult destination = new StreamResult(pw);
		transformer.transform(source, destination);
		pw.close();
	}

	/**	Saves a DOM document to a String.
	 *
	 *	@param	document	DOM document.
	 *  @return             XML version of DOM document as a string.
	 *
	 *	@throws	TransformerException, IOException
	 */

	public static String saveToString (Document document)
		throws TransformerException, IOException
	{
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		StringWriter sw = new StringWriter();
		StreamResult destination = new StreamResult(sw);
		transformer.transform(source, destination);
		return sw.toString();
	}

	/**	Saves a DOM document to a String with a specified DTD name.
	 *
	 *	@param	document	DOM document.
	 *	@param	dtdName		The DTD name.
	 * 	@return		XML version of DOM document as a string.
	 *
	 *	@throws	TransformerException, IOException
	 */

	public static String saveToString (Document document, String dtdName)
		throws TransformerException, IOException
	{
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtdName);
		StringWriter sw = new StringWriter();
		StreamResult destination = new StreamResult(sw);
		transformer.transform(source, destination);
		return sw.toString();
	}

	/**	Checks to see if a node has a descendant node of some name, other
	 *	than those in children of some other name.
	 *
	 *	@param	node		Node
	 *
	 *	@param	names1		Array of names to include.
	 *
	 *	@param	names2		Array of names to exclude.
	 *
	 *	@return				True if the node contains a descendant in names1,
	 *						but not in children in names2.
	 */

	public static boolean nodeHasDescendant (Node node, String[] names1,
		String[] names2)
	{
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String childName = child.getNodeName();
			for (int j = 0; j < names1.length; j++)
				if (childName.equals(names1[j])) return true;
			boolean isInNames2 = false;
			for (int j = 0; j < names2.length; j++) {
				if (childName.equals(names2[j])) {
					isInNames2 = true;
					break;
				}
			}
			if (isInNames2) continue;
			if (nodeHasDescendant(child, names1, names2)) return true;
		}
		return false;
	}

	/**	Validate a DOM document against a schema.
	 *
	 *	@param	document	The parsed DOM document to validate.
	 *	@param	schemaURI	The schema URI.
	 *
	 *	@throws				MalformedURLException if the schema URI is invalid.
	 *	@throws				URISyntaxException if the schema URI is invalid.
	 *	@throws				ClassNotFoundException if the validation fails.
	 *	@throws				IllegalAccessException if the validation fails.
	 *	@throws				InstantiationException if the validation fails.
	 *	@throws				IOException if the validation fails.
	 *	@throws				SAXException if the validation fails.
	 *
	 *	<p>
	 *	Simply returns without error if validation succeeds.
	 *	</p>
	 */
/*
	public static void validateDocument
	(
		Document document ,
		String schemaURI
	)
		throws SAXException ,
			URISyntaxException ,
			MalformedURLException ,
			IOException ,
			IllegalAccessException ,
			InstantiationException ,
			ClassNotFoundException
	{
								//	No schema?  Assume validation
								//	succeeds.

		if ( ( schemaURI != null ) && ( schemaURI.length() > 0 ) )
		{
								//	Get a schema factory for the schema.

			SchemaFactory schemaFactory	=
				SchemaUtils.getSchemaFactory( schemaURI );

								//	Compile the schema.

			Schema schema	=
				schemaFactory.newSchema( new URI( schemaURI ).toURL() );

			validateDocument( document , schema );
		}
	}
*/
	/**	Validate a DOM document against a schema.
	 *
	 *	@param	document	The parsed DOM document to validate.
	 *	@param	schema		A parsed schema.
	 *
	 *	@throws				IOException if the validation fails.
	 *	@throws				SAXException if the validation fails.
	 *
	 *	<p>
	 *	Simply returns without error if validation succeeds.
	 *	</p>
	 */

	public static void validateDocument
	(
		Document document ,
		Schema schema
	)
		throws SAXException , IOException
	{
								//	Get a validator from the schema.

		Validator validator	= schema.newValidator();

								//	Get SAX event source for the
								//	document so we can validate it.

		Source source	= new DOMSource( document );

								//	Check the document against the
								//	schema.

		validator.validate( source );
	}

	/**	Hides the default no-arg constructor.
	 */

	private DOMUtils () {
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


