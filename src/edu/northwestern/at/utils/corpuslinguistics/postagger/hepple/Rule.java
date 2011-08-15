package edu.northwestern.at.utils.corpuslinguistics.postagger.hepple;

/*	Please see the license information in the header below. */

import java.util.*;

/**
 *  Rule.java
 *
 *  Copyright (c) 2001-2005, The University of Sheffield.
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  HepTag was originally written by Mark Hepple, this version contains
 *  modifications by Valentin Tablan and Niraj Aswani.
 *
 *  $Id: Rule.java,v 1.1 2005/09/30 14:48:12 ian_roberts Exp $
 */

/**
 * Title:        HepTag
 * Description:  Mark Hepple's POS tagger
 * Copyright:    Copyright (c) 2001
 * Company:      University of Sheffield
 * @author Mark Hepple
 * @version 1.0
 */

abstract public class Rule
{
	protected boolean debug	= false;

	protected String from;
	protected String to;
	protected String ruleId;
	protected String[] context;

	public void initialise( List ruleParts )
	{
		from	= (String)ruleParts.get( 0 );
		to		= (String)ruleParts.get( 1 );
		ruleId	= (String)ruleParts.get( 2 );

		int contextSize	= ruleParts.size() - 3;

		context			= new String[ contextSize ];

		for ( int i = 0 ; i < contextSize ; i++ )
		{
			context[ i ]	= (String)ruleParts.get( i + 3 );
		}
	}

	abstract public boolean checkContext( HeppleTagger tagger );

	public boolean hasToTag( HeppleTagger tagger )
	{
		boolean result	= false;

		for ( int i = 0 ; i < tagger.lexBuff[ 3 ].length ; i++ )
		{
			if ( to.equals( tagger.lexBuff[ 3 ][ i ] ) )
			{
				result	= true;
				break;
			}
		}

		return result;
	}

	public boolean apply( HeppleTagger tagger )
	{
		if ( hasToTag( tagger ) && checkContext( tagger ) )
		{
			tagger.tagBuff[ 3 ]	= to;
			tagger.incrementRuleCorrections();

			return true;
		}
		else return false;
	}
}

