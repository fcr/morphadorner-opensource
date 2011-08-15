package edu.northwestern.at.utils.corpuslinguistics.inflector.pluralizer;

/*	Please see the license information at the end of this file. */

import static edu.northwestern.at.utils.corpuslinguistics.inflector.wordrule.AbstractRegexReplacementRule.disjunction;
import static edu.northwestern.at.utils.corpuslinguistics.inflector.wordrule.IrregularMappingRule.toMap;

import java.util.*;
import java.util.regex.*;

import edu.northwestern.at.utils.corpuslinguistics.inflector.*;
import edu.northwestern.at.utils.corpuslinguistics.inflector.wordrule.*;

/** English language pluralizer.
 *
 *	<p>
 *	Based upon the paper "An Algorithmic Approach to English Pluralization"
 *	by Damian Conway at
 *	<a href="http://www.csse.monash.edu.au/~damian/papers/HTML/Plurals.html">
 *	http://www.csse.monash.edu.au/~damian/papers/HTML/Plurals.html
 *	<a>.
 *	</p>
 *
 *	<p>
 *	Original code written by Tom White under the Apache v2 license.
 *	Modified by Philip R. Burns for integration into MorphAdorner.
 *	</p>
 */

public class EnglishPluralizer
	extends RuleBasedPluralizer
{
	protected static final String POSTFIX_ADJECTIVE_REGEX =
		"(" + "(?!major|lieutenant|brigadier|adjutant)\\S+(?=(?:-|\\s+)general)|" +
		"court(?=(?:-|\\s+)martial)" + ")(.*)";

	protected static final String[] PREPOSITIONS =
	{
		"about" ,
		"above" ,
		"across" ,
		"after" ,
		"among" ,
		"around" ,
		"at" ,
		"athwart" ,
		"before" ,
		"behind" ,
		"below" ,
		"beneath" ,
		"beside" ,
		"besides" ,
		"between" ,
		"betwixt" ,
		"beyond" ,
		"but" ,
		"by" ,
		"during" ,
		"except" ,
		"for" ,
		"from" ,
		"in" ,
		"into" ,
		"near" ,
		"of" ,
		"off" ,
		"on" ,
		"onto" ,
		"out" ,
		"over" ,
		"since" ,
		"till" ,
		"to" ,
		"under" ,
		"until" ,
		"unto" ,
		"upon" ,
		"with" ,
	};

	protected static final Map<String , String> NOMINATIVE_PRONOUNS =
		toMap
		(
			new String[][]
			{
								// nominative			reflexive

				{ "i"			, "we" } ,
				{ "myself"		, "ourselves" } ,
				{ "you"			, "you" } ,
				{ "yourself"	, "yourselves" } ,
				{ "she"			, "they" } ,
				{ "herself"		, "themselves" } ,
				{ "he"			, "they" } ,
				{ "himself"		, "themselves" } ,
				{ "it"			, "they" } ,
				{ "itself"		, "themselves" } ,
				{ "they"		, "they" } ,
				{ "themself"	, "themselves" } ,

								// possessive

				{ "mine"	, "ours" } ,
				{ "yours"	, "yours" } ,
				{ "hers"	, "theirs" } ,
				{ "his"		, "theirs" } ,
				{ "its"		, "theirs" } ,
				{ "theirs"	, "theirs" } ,
				}
			);

	protected static final Map<String , String> ACCUSATIVE_PRONOUNS =
		toMap
		(
			new String[][]
			{
				{ "me"			, "us" } ,
				{ "myself"		, "ourselves" } ,
				{ "you"			, "you" } ,
				{ "yourself"	, "yourselves" } ,
				{ "her"			, "them" } ,
				{ "herself"		, "themselves" } ,
				{ "him"			, "them" } ,
				{ "himself"		, "themselves" } ,
				{ "it"			, "them" } ,
				{ "itself"		, "themselves" } ,
				{ "them"		, "them" } ,
				{ "themself"	, "themselves" } ,
			}
		);

	protected static final Map<String , String> IRREGULAR_NOUNS =
		toMap
		(
			new String[][]
			{
				{ "child"		, "children" } ,
				{ "brother"		, "brothers" } , // irregular classical form
				{ "loaf"		, "loaves" } ,
				{ "hoof"		, "hoofs" } , // irregular classical form
				{ "beef"		, "beefs" } , // irregular classical form
				{ "money"		, "monies" } ,
				{ "mongoose"	, "mongooses" } ,
				{ "ox"			, "oxen" } ,
				{ "cow"			, "cows" } , // irregular classical form
				{ "soliloquy"	, "soliloquies" } ,
				{ "graffito"	, "graffiti" } ,
				{ "prima donna"	, "prima donnas" } , // irregular classical form
				{ "octopus"		, "octopuses" } , // irregular classical form
//				{ "octopus"		, "octopodes" } , // irregular classical form
				{ "genie"		, "genies" } , // irregular classical form
				{ "ganglion"	, "ganglions" } , // irregular classical form
				{ "trilby"		, "trilbys" } ,
				{ "turf"		, "turfs" } , // irregular classical form
				{ "numen"		, "numina" } ,
				{ "atman"		, "atmas" } ,
				{ "occiput"		, "occiputs" } , // irregular classical form

										// Words ending in -s

				{ "corpus"	, "corpuses" } , // irregular classical form
				{ "opus"	, "opuses" } , // irregular classical form
				{ "genus"	, "genera" } ,
				{ "mythos"	, "mythoi" } ,
				{ "penis"	, "penises" } , // irregular classical form
				{ "testis"	, "testes" } ,
				{ "atlas"	, "atlases" } , // irregular classical form
			}
		);

	protected static final String[] CATEGORY_UNINFLECTED_NOUNS =
	{
								// Fish and herd animals
		".*fish" ,
		"tuna" ,
		"salmon" ,
		"mackerel" ,
		"trout" ,
		"bream" ,
		"sea[- ]bass" ,
		"carp" ,
		"cod" ,
		"flounder" ,
		"whiting" ,
		".*deer" ,
		".*sheep" ,
		"wildebeest" ,
		"swine" ,
		"eland" ,
		"bison" ,
		"buffalo" ,
		"elk" ,
		"moose" ,
		"rhinoceros" ,
									// Nationals ending in -ese
		"Amoyese" ,
		"Beninese" ,
		"Bhutanese" ,
		"Borghese" ,
		"Burmese" ,
		"Chinese" ,
		"Congoese" ,
		"Congolese" ,
		"Faroese" ,
		"Foochowese" ,
		"Gabonese" ,
		"Genevese" ,
		"Genoese" ,
		"Gilbertese" ,
		"Guyanese" ,
		"Hottentotese" ,
		"Japanese" ,
		"Kiplingese" ,
		"Kongoese" ,
		"Lebanese" ,
		"Lucchese" ,
		"Maltese" ,
		"Marshallese" ,
		"Nankingese" ,
		"Nepalese" ,
		"Niasese" ,
		"Pekingese" ,
		"Piedmontese" ,
		"Pistoiese" ,
		"Portuguese" ,
		"Sammarinese" ,
		"Sarawakese" ,
		"Senegalese" ,
		"Shavese" ,
		"Sudanese" ,
		"Togolese" ,
		"Vermontese" ,
		"Vietnamese" ,
		"Wenchowese" ,
		"Yengeese" ,
		".*[nrlm]ese" ,
								//	Other nationalities.
		"British" ,
		"Burkinabe" ,
		"French" ,
		"I-Kiribati" ,
		"Irish" ,
		"Mahorais" ,
		"Malagasy" ,
		"Ni-Vanuatu" ,
		"Seychellois" ,
		"Spanish" ,
		"Swiss" ,
		"Taiwan" ,
		"Thai" ,
								//	Diseases
		".*pox" ,
								//	Other oddities
		"graffiti" ,
		"djinn" ,
								//	Words ending in -s
								//	Pairs or groups subsumed to a singular
		"breeches" ,
		"britches" ,
		"clippers" ,
		"gallows" ,
		"hijinks" ,
		"headquarters" ,
		"pliers" ,
		"scissors" ,
		"testes" ,
		"herpes" ,
		"pincers" ,
		"shears" ,
		"proceedings" ,
		"trousers" ,
								// Unassimilated Latin 4th declension
		"cantus" ,
		"coitus" ,
		"nexus" ,
								// Recent imports
		"contretemps" ,
		"corps" ,
		"debris" ,
		".*ois" ,
		"siemens" ,
								// Diseases
		".*measles" ,
		"mumps" ,
								// Others
		"diabetes" ,
		"jackanapes" ,
		"series" ,
		"species" ,
		"rabies" ,
		"chassis" ,
		"innings" ,
		"news" ,
		"mews" ,
	};

	protected static final String[] CATEGORY_MAN_MANS_RULE =
	{
		"human" ,
		"Alabaman" ,
		"Bahaman" ,
		"Burman" ,
		"German" ,
		"Hiroshiman" ,
		"Liman" ,
		"Nakayaman" ,
		"Oklahoman" ,
		"Panaman" ,
		"Selman" ,
		"Sonaman" ,
		"Tacoman" ,
		"Yakiman" ,
		"Yokohaman" ,
		"Yuman" ,
	};

	protected static final String[] CATEGORY_EX_ICES_RULE =
	{
		"codex" ,
		"index" ,
		"murex" ,
		"silex" ,
		"vertex"
	};

	protected static final String[] CATEGORY_IX_ICES_RULE =
	{
		"appendix" ,
		"radix" ,
		"helix" ,
		"matrix"
	};

	protected static final String[] CATEGORY_UM_A_RULE =
	{
		"bacterium" ,
		"agendum" ,
		"desideratum" ,
		"erratum" ,
		"stratum" ,
		"datum" ,
		"ovum" ,
		"extremum" ,
		"candelabrum" ,
	};

	protected static final String[] CLASSICAL_UM_A	=
	{
		"maximum",
		"minimum",
		"momentum",
		"optimum",
		"quantum",
		"cranium",
		"curriculum",
		"dictum",
		"phylum",
		"aquarium",
		"compendium",
		"emporium",
		"enconium",
		"gymnasium",
		"honorarium",
		"interregnum",
		"lustrum",
		"memorandum",
		"millennium",
		"rostrum",
		"spectrum",
		"speculum",
		"stadium",
		"trapezium",
		"ultimatum",
		"medium",
		"vacuum",
		"velum",
		"consortium",
	};

	protected static final String[] CATEGORY_US_I_RULE =
	{
		"alumnus" ,
		"alveolus" ,
		"bacillus" ,
		"bronchus" ,
		"locus" ,
		"nucleus" ,
		"stimulus" ,
		"meniscus" ,
	};

	protected static final String[] CLASSICAL_US_I_RULE =
	{
		"focus",
		"radius",
		"genius",
		"incubus",
		"succubus",
		"nimbus",
		"fungus",
		"nucleolus",
		"stylus",
		"torus",
		"umbilicus",
		"uterus",
		"hippopotamus",
	};

	protected static final String[] CLASSICAL_US_US_RULE =
	{
		"status",
		"apparatus",
		"prospectus",
		"sinus",
		"hiatus",
		"impetus",
		"plexus",
	};

	protected static final String[] CATEGORY_ON_A_RULE =
	{
		"criterion" ,
		"perihelion" ,
		"aphelion" ,
		"phenomenon" ,
		"prolegomenon" ,
		"noumenon" ,
		"organon" ,
		"asyndeton" ,
		"hyperbaton" ,
	};

	protected static final String[] CLASSICAL_ON_A_RULE =
	{
		"oxymoron",
	};

	protected static final String[] CLASSICAL_A_ATA_RULE	=
	{
		"anathema",
		"bema",
		"carcinoma",
		"charisma",
		"diploma",
		"dogma",
		"drama",
		"edema",
		"enema",
		"enigma",
		"lemma",
		"lymphoma",
		"magma",
		"melisma",
		"miasma",
		"oedema",
		"sarcoma",
		"schema",
		"soma",
		"stigma",
		"stoma",
		"trauma",
		"gumma",
		"pragma",
	};

	protected static final String[] CATEGORY_A_AE_RULE =
	{
		"alumna" ,
		"alga" ,
		"vertebra" ,
		"persona"
	};

	protected static final String[] CLASSICAL_A_AE_RULE	=
	{
		"amoeba",
		"antenna",
		"formula",
		"hyperbola",
		"medusa",
		"nebula",
		"parabola",
		"abscissa",
		"hydra",
		"nova",
		"lacuna",
		"aurora",
		".*umbra",
		"flora",
		"fauna",
	};

    protected static final String[] CLASSICAL_EN_INA_RULE	=
    {
		"stamen",
		"foramen",
		"lumen"
	};

    protected static final String[] CLASSICAL_O_I_RULE	=
    {
		"solo",
		"soprano",
		"basso",
		"alto",
		"contralto",
		"tempo",
		"piano",
		"virtuoso",
	};

	protected static final String[] CATEGORY_O_OS_RULE =
	{
		"albino" ,
		"archipelago" ,
		"armadillo" ,
		"commando" ,
		"crescendo" ,
		"fiasco" ,
		"ditto" ,
		"dynamo" ,
		"embryo" ,
		"ghetto" ,
		"guano" ,
		"inferno" ,
		"jumbo" ,
		"lumbago" ,
		"magneto" ,
		"manifesto" ,
		"medico" ,
		"octavo" ,
		"photo" ,
		"pro" ,
		"quarto" ,
		"canto" ,
		"lingo" ,
		"generalissimo" ,
		"stylo" ,
		"rhino" ,
		"casino" ,
		"auto" ,
		"macro" ,
		"zero" ,
		"solo" ,
		"soprano" ,
		"basso" ,
		"alto" ,
		"contralto" ,
		"tempo" ,
		"piano" ,
		"virtuoso" ,
	};

	protected static final String[] CATEGORY_SINGULAR_S_RULE =
	{
		".*ss" ,
		"acropolis" ,
		"aegis" ,
		"alias" ,
		"asbestos" ,
		"bathos" ,
		"bias" ,
		"bronchitis" ,
		"bursitis" ,
		"caddis" ,
		"cannabis" ,
		"canvas" ,
		"chaos" ,
		"cosmos" ,
		"dais" ,
		"digitalis" ,
		"epidermis" ,
		"ethos" ,
		"eyas" ,
		"gas" ,
		"glottis" ,
		"hubris" ,
		"ibis" ,
		"lens" ,
		"mantis" ,
		"marquis" ,
		"metropolis" ,
		"pathos" ,
		"pelvis" ,
		"polis" ,
		"rhinoceros" ,
		"sassafras" ,
		"trellis" ,
		".*us" ,
		"[A-Z].*es" ,

		"ephemeris" ,
		"iris" ,
		"clitoris" ,
		"chrysalis" ,
		"epididymis" ,
								 // Inflamations
		".*itis" ,
	};

	protected static final String[] CLASSICAL_EIX_ICES_RULE =
	{
		"vortex",
		"vertex",
		"cortex",
		"latex",
		"pontifex",
		"apex",
		"index",
		"simplex",
	};

	// References to Steps are to those in Conway's paper

	protected final List<WordRule> rules =
		Arrays.asList
		(
			new WordRule[]
			{
								//	Blank word

				new RegexReplacementRule( "^(\\s)$" , "$1" ) ,

								//	Nouns that do not inflect in the plural
								//	(such as "fish") [Step 2]

				new CategoryInflectionRule
				(
					CATEGORY_UNINFLECTED_NOUNS , "-" , "-"
				) ,
								//	Compounds [Step 12]

				new AbstractRegexReplacementRule
				(
					"(?i)^(?:" + POSTFIX_ADJECTIVE_REGEX + ")$"
				)
				{
					@Override public String replace( Matcher m )
					{
						return
							EnglishPluralizer.this.pluralize( m.group( 1 ) ) +
							m.group( 2 );
					}
				} ,

				new AbstractRegexReplacementRule
				(
					"(?i)(.*?)((?:-|\\s+)(?:" + disjunction( PREPOSITIONS ) +
					"|d[eu])(?:-|\\s+))a(?:-|\\s+)(.*)"
				)
				{
					@Override public String replace( Matcher m )
					{
						return
							EnglishPluralizer.this.pluralize( m.group( 1 ) ) +
							m.group( 2 ) +
							EnglishPluralizer.this.pluralize( m.group( 3 ) );
					}
				} ,

				new AbstractRegexReplacementRule
				(
					"(?i)(.*?)((-|\\s+)(" + disjunction( PREPOSITIONS ) +
					"|d[eu])((-|\\s+)(.*))?)"
				)
				{
					@Override public String replace( Matcher m )
					{
						return
							EnglishPluralizer.this.pluralize( m.group( 1 ) ) +
							m.group( 2 );
					}
				} ,
								//	Pronouns [Step 3]

				new IrregularMappingRule
				(
					NOMINATIVE_PRONOUNS ,
					"(?i)" + disjunction( NOMINATIVE_PRONOUNS.keySet() )
				) ,
				new IrregularMappingRule
				(
					ACCUSATIVE_PRONOUNS ,
					"(?i)" + disjunction( ACCUSATIVE_PRONOUNS.keySet() )
				) ,
				new IrregularMappingRule
				(
					ACCUSATIVE_PRONOUNS ,
					"(?i)(" + disjunction( PREPOSITIONS ) + "\\s)" + "(" +
					disjunction( ACCUSATIVE_PRONOUNS.keySet() ) + ")"
				)
				{
					@Override public String replace( Matcher m )
					{
						return m.group( 1 ) +
							mappings.get( m.group( 2 ).toLowerCase() );
					}
				} ,

								//	Standard irregular plurals
								//	(such as "children") [Step 4]

				new IrregularMappingRule
				(
					IRREGULAR_NOUNS ,
					"(?i)(.*)\\b" +
						disjunction( IRREGULAR_NOUNS.keySet() ) + "$"
				) ,
				new CategoryInflectionRule
				(
					CATEGORY_MAN_MANS_RULE , "-man" , "-mans"
				) ,
				new RegexReplacementRule
				(
					"(?i)(\\S*)(person)$" ,
					"$1people"
				) ,
								//	Families of irregular plurals for common \
								//	suffixes (such as "-men") [Step 5]

				new SuffixInflectionRule( "-man" , "-man" , "-men" ) ,
				new SuffixInflectionRule( "-[lm]ouse" , "-ouse" , "-ice" ) ,
				new SuffixInflectionRule( "-tooth" , "-tooth" , "-teeth" ) ,
				new SuffixInflectionRule( "-goose" , "-goose" , "-geese" ) ,
				new SuffixInflectionRule( "-foot" , "-foot" , "-feet" ) ,

								//	Assimilated irregular plurals [Step 6]

				new SuffixInflectionRule( "-ceps" , "-" , "-" ) ,
				new SuffixInflectionRule( "-zoon" , "-zoon" , "-zoa" ) ,
				new SuffixInflectionRule( "-[csx]is" , "-is" , "-es" ) ,
				new CategoryInflectionRule
				(
					CATEGORY_EX_ICES_RULE , "-ex" , "-ices"
				) ,
				new CategoryInflectionRule
				(
					CATEGORY_IX_ICES_RULE ,
					"-ix" ,
					"-ices"
				) ,
				new CategoryInflectionRule
				(
					CATEGORY_UM_A_RULE ,
					"-um" ,
					"-a"
				) ,
				new CategoryInflectionRule
				(
					CATEGORY_US_I_RULE ,
					"-us" ,
					"-i"
				) ,
				new CategoryInflectionRule
				(
					CATEGORY_ON_A_RULE ,
					"-on" ,
					"-a"
				) ,
				new CategoryInflectionRule
				(
					CATEGORY_A_AE_RULE ,
					"-a" ,
					"-ae"
				) ,

								//	Classical irregular plurals [Step 7]
								//	Classical plurals have not been
								//	active.
/*
				new CategoryInflectionRule
				(
					CLASSICAL_A_ATA_RULE ,
					"-a" ,
					"-ata"
				) ,

				new CategoryInflectionRule
				(
					CLASSICAL_A_AE_RULE ,
					"-a" ,
					"-ae"
				) ,

				new CategoryInflectionRule
				(
					CLASSICAL_EN_INA_RULE ,
					"-en" ,
					"-ina"
				) ,

				new CategoryInflectionRule
				(
					CLASSICAL_UM_A_RULE ,
					"-um" ,
					"-a"
				) ,

				new CategoryInflectionRule
				(
					CLASSICAL_US_I_RULE ,
					"-us" ,
					"-i"
				) ,

				new CategoryInflectionRule
				(
					CLASSICAL_US_US_RULE ,
					"-us" ,
					"-us"
				) ,

				new CategoryInflectionRule
				(
					CLASSICAL_ON_A_RULE ,
					"-on" ,
					"-a"
				) ,

				new CategoryInflectionRule
				(
					CLASSICAL_O_I_RULE ,
					"-o" ,
					"-i"
				) ,

				new CategoryInflectionRule
				(
					CLASSICAL_EIX_ICES_RULE ,
					"-[ei]x" ,
					"-ices"
				) ,
*/
								//	Nouns ending in sibilants
								//	(such as "churches") [Step 8]

				new CategoryInflectionRule
				(
					CATEGORY_SINGULAR_S_RULE ,
					"-s" ,
					"-ses"
				) ,
				new RegexReplacementRule( "^([A-Z].*s)$" , "$1es" ) ,
				new SuffixInflectionRule( "-[cs]h" , "-h" , "-hes" ) ,
				new SuffixInflectionRule( "-x" , "-x" , "-xes" ) ,
				new SuffixInflectionRule( "-z" , "-z" , "-zes" ) ,

								//	Nouns ending with "-f" or "-fe"
								//	take "-ves" in the plural
								//	(such as "halves") [Step 9]

				new SuffixInflectionRule( "-[aeo]lf" , "-f" , "-ves" ) ,
				new SuffixInflectionRule( "-[^d]eaf" , "-f" , "-ves" ) ,
				new SuffixInflectionRule( "-arf" , "-f" , "-ves" ) ,
				new SuffixInflectionRule( "-[nlw]ife" , "-fe" , "-ves" ) ,

								//	Nouns ending with "-y" [Step 10]

				new SuffixInflectionRule( "-[aeiou]y" , "-y" , "-ys" ) ,
				new RegexReplacementRule( "^([A-Z].*y)$" , "$1s" ) ,
				new SuffixInflectionRule( "-y" , "-y" , "-ies" ) ,

								//	Nouns ending with "-o" [Step 11]

				new CategoryInflectionRule
				(
					CATEGORY_O_OS_RULE ,
					"-o" ,
					"-os"
				) ,
				new SuffixInflectionRule( "-[aeiou]o" , "-o" , "-os" ) ,
				new SuffixInflectionRule( "-o" , "-o" , "-oes" ) ,

								//	Default rule: add "s" [Step 13]

				new SuffixInflectionRule( "-" , "-s" ) ,
			}
		);

	/** Create Engish pluralizer.
	 */

	public EnglishPluralizer()
	{
		setRules( rules );
		setLocale( Locale.ENGLISH );
	}

    /**	Fix case of pluralized word.
     *
     *	@param	trimmedWord		The input word, with leading and trailing
     *							whitespace removed.
     *	@param	pluralizedWord	The pluralized word.
     *
     *	@return					The <code>pluralizedWord</code> after
     *							processing.
     *
     *	<p>
     *	If <code>trimmedWord</code> is all uppercase, then
     *	<code>pluralizedWord</code> is uppercased.
     *	If <code>trimmedWord</code> is titlecase, then
     *	<code>pluralizedWord</code> is titlecased.
     *	</p>
     */

	@Override
	protected String postProcess
	(
		String trimmedWord ,
		String pluralizedWord
	)
	{
		if ( trimmedWord.matches( "^I$" ) )
		{
			return pluralizedWord;
		}

		return super.postProcess( trimmedWord , pluralizedWord );
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



