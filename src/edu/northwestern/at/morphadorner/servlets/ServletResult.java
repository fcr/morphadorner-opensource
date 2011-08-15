package edu.northwestern.at.morphadorner.servlets;

/*	Please see the license information at the end of this file. */

/**	Stores results from a servlet. */

public class ServletResult
{
	/**	True if servlet results from a form response. */

	protected boolean fromForm;

	/**	Servlet results as a string. */

	protected String results;

	/**	Redirection URL. */

	protected String redirectionURL;

	/**	Session attribute name. */

	protected String sessionAttributeName;

	/**	Results title. */

	protected String title;

	/**	Create ServletResult.
	 *
	 */

	public ServletResult
	(
		boolean fromForm ,
		String results ,
		String title ,
		String redirectionURL ,
		String sessionAttributeName
	)
	{
		this.fromForm				= fromForm;
		this.results				= results;
		this.title					= title;
		this.redirectionURL			= redirectionURL;
		this.sessionAttributeName	= sessionAttributeName;
	}

	/**	Get from form flag.
	 *
	 *	@return		true if outputting results from form.
	 */

	public boolean getFromForm()
	{
		return fromForm;
	}

	/**	Get results output.
	 *
	 *	@return		Servlet results as HTML.
	 */

	public String getResults()
	{
		return results;
	}

	/**	Get results title.
	 *
	 *	@return		Servlet result title.
	 */

	public String getTitle()
	{
		return title;
	}

	/**	Get redirection URL.
	 *
	 *	@return		Redirection URL.
	 */

	public String getRedirectionURL()
	{
		return redirectionURL;
	}

	/**	Get session attribute name.
	 *
	 *	@return		Session attribute name for results.
	 */

	public String getSessionAttributeName()
	{
		return sessionAttributeName;
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



