This is release 1.0 of MorphAdorner.

Main changes since v0.9.1.

(1)	The list of languages recognized by the default language
	recognizer has been changed to:

	dutch, english, french, german, italian, latin, scots, spanish,
	welsh

	The base language recognizer has been modified to allow
	for setting the list of recognized languages.

(2)	Minimal documentation on setting up the sample MorphAdorner
	servlets has been added to the online and printable documentation.
	The servlets are intended mainly for demonstration purposes,
	not production use.

(3)	LGParser had problems under Unix/Linux.  Those problems should be corrected.

Main changes since v0.9.

(1)	The Unicode non-breaking hyphen (\u2011) is no longer
	treated as a separate token.

Main changes since v0.8.2.

(1)	Added a facility to handle selected split words.
	The initial implementation handles reflexive pronouns
	in Early Modern English texts, e.g., "them selves" for
	"themselves".

(2)	Added a facility to remove adornments from children of
	specified tags (e.g., figdesc) when writing the final
	adorned text.

(3)	Corrected a lemmatization problem in which some words
	lemmatized to an empty string.  The spelling is returned
	for such cases.

(4)	Added "UnicodeReader" class to allow correct reading
	of files with initial BOM markers.  The standard Java
	I/O libraries does not handle BOM markers correctly
	when a Reader is used.

(5)	Added an ObjectStore interface to allow using lightweight
	object databases for storing data temporarily during
	MorphAdorner processing.  The new VirtualHashMap and VirtualTreeMap
	classes use an implementation of the ObjectStore interface
	based upon the open source NeoDatis library.

(6)	Periods before the file extension in input xml file names are
	now mapped to underlines when generating word IDs.

(7)	Removed the following portions of GPL licensed code:
	--	Lovins stemmer
	--	gnu.getopt (replaced with jargs)

(8)	Made MergeEnhancedBrillLexicon and CreateSuffixLexicon
	utilities public.

(9)	Ensure selected xml tags (e.g., figdesc, sic) do not contain
	<w> and <c> children since the TEI spec now disallows that.

(10)	Ensure vertical bars are treated as symbols when they appear
	in source text.

Main changes since v0.8.1.

(1)	A simple XML outputter for adorned words is available as
	SimpleXMLAdornedWordOutputter.

(2)	Added a simple EEBO name standardizer to regularize a few
	names.

(3)	Changed the lemmatization of possessive pronouns to map to
	themselves, e.g., my --> my, his --> his, etc.

(4)	Corrected a bug in the part of speech for words cache that
	caused some lexicon tags to be ignored for cached words.

(5)	Combining macrons with invalid leading spaces are now joined
	to the preceding word in Early Modern English texts.

Main changes since v0.8.

(1)	URLs are now accepted for input file names on the MorphAdorner
	command line.  URLs may not contain wildcard characters.

(2)	The Early Modern English data files have been updated.
	There are new abbreviations files and Latin word files.

(3)	The tokenizer for Early Modern English now handles combining
	macrons.  There is a new map for mapping words with combining
	macrons to their demacronized form, as well as a list of rules.

(4)	Some bad memory leaks have been plugged.

(5)	The English Lemmatizer handles a wider variety of unusual contractions.

(6)	Old style Roman numerals of the form .romannumeral. are now treated
	as a single token rather then a period followed by the romannumeral
	followed by a period.

(7)	Archaic printer's marks encoded using a sup tag (e.g., y<sup>t</sup> for "that")
	are now handled correctly.

(8)	The XML validator now works again.

(9)	Merging adornments into XML is now faster.

(10)	The default license text has changed from the GPL to an NCSA (BSD-like)
	variant.

Main changes since v0.7.1.

(1)	Added a simple thesaurus interface and a default implementation
	using Brett Spell's Java interface to WordNet.  Added the
	jaws-bin-1.1 jar to the classpath and added a data/wordnet
	directory tree.  The WordNet data is from v3.0 of the Princeton
	release.

(2)	Changed the XML path ID options to add the current
	file name base as the first part of the generated ID.

(3)	Added an interface class for part of speech tag mapping.
	There are no concrete implementations included yet.

(4)	Added the boolean configuration parameter
	xmlhandler.output_pseudo_page_boundaries to add
	page milestone elements at word internals specified by
	xmlhandler.pseudo_page_size (default value 300).
	The xmlhandler.pseudo_page_container_div_types lists the
	<div> tag types which should force the end of a pseudo-page.
	Default list is volume, sermon, chapter.

	A standalone utility AddPseudoPages is also available.

(5)	Corrected sentence and word numbering for XML files when
	adorner.output.word_number and/or adorner.output.sentence_number
	are set true.  Adding word and sentence numbers requires
	two passes for output generation instead of one, which slows
	processing significantly.

(6)	Added support for RelaxNG (and W3C) schema definitions.

	The configuration parameter xmlhandler.xml_schema
	provides the name of a schema to use for XML input parsing.
	Default is:

	http://ariadne.northwestern.edu/monk/dtds/TEIAnalytics.rng .

	The ValidateXMLFiles tool has been modified to accept a relax NG
	or W3C schema as the first program argument.  This schema is assumed
	to apply to all the subsequently listed XML files.

(7)	Corrected numerous errors in the Early Modern English spelling map.
	There are a number of problems left.

(8)	Externalized the word class dependent spelling map as
	spellingsbywordclass.txt .  Currently the same map is used
	for Early Modern English and 19th century fiction.  The "-w"
	configuration parameter specifies the name of the word class
	dependent spelling map.

(9)	Configurations now operate as overrides of the default configuration
	file morphadorner.properties .  Only configuration parameters
	which different from the base settings now appear in
	ncfa.properties, eme.properties, etc.

(10)	Moved some jump tags to the hard tags list.  This results in
	smoother word numbering.

(11)	Fixed a problem with empty soft tags generating incorrectly
	joined words.

(12)	Corrected a number of problems with the lemmatization of
	capitalized words.  Also added two batch file and scripts
	for the Relemmatize tool: relemmatizencf[.bat] relemmatizes
	texts using the 19th century fiction data files, while
	relemmatizeeme[.bat] relemmatizes texts using the Early
	Modern English data files.

(13)	Improved the indented adorned output to ensure closing tags
	get placed on a line of their own when they should be.

(14)	The adornwithne[.bat] tool now works properly on Linux systems
	with Java 1.5 .

(15)	Headless exceptions should no longer appear on Linux systems.

(16)	Already adorned files can be readorned by reprocessing them
	with MorphAdorner.  Previously adorned files are automatically
	detected.  The tokenization is left unchanged, so
	the word IDs remain the same.  Retaggers than can change
	the tokenization are disabled during readornment.

(17)	Corrected a problem in which parts of two splits words occurring
	in sequence were sometimes treated as the same word.

(18)	Removed the configuration option to generate sentence milestones.
	These cannot be generated reliably because of intrusive jump tags.
	Sentence number attributes are reliable and handle jump tags
	properly, so we may want to consider adding these as defaults.

Main changes since v0.7.

(1)	Updated the English irregular forms list for the English
	lemmatizer.

(2)	Added TabWordFileReader and TabWordInfo classes for working with
	XMLToTab output.

(3)	Corrected some weaknesses in the CSV package.

Main changes since v0.6.1.

(1)	Updated the lexicons and associated data for both 19th Century
	Fiction and Early Modern English.

(2)	Added a TextSegmenter package.  Includes modified versions
	of Choi's C99 linear text segmenter and Choi's Java version
	of Hearst's TextTiler.

(3)	Fixed the Sentence Melder to not output a blank after a leading
	quote in a sentence.

(4)	Added a TextSummarizer package. The sample summarizer is
	useless for literature, but serves as a simple example.

(5)	Added a StopWords package to consolidate the different
	stopwords lists.

(6)	Removed the type AdornedWordList and used generics instead.

(7)	Added a WordCounts package.  Very incomplete.

(8)	Added xmlhandler.fix_gap_tags and xmlhandler.fix_orig_tags
	boolean configuration elements to allow enabling/disabling
	special processing of <gap> and <orig> tags, respectively.
	Both currently enabled by default.

Main changes since v0.6.

(1)	Improved the sentence splitting heuristics for initials.

(2)	Modified the name recognizer to include methods for getting
	and setting a part of speech tagger.  Also improved the
	default name recognizer to use part of speech tags to extract
	names based upon proper noun phrases.

Main changes since v0.5.1.

(1)	Corrected a bug in the part of speech guesser which resulted in
	the emission of a "*" for the part of speech in some cases.

(2)	Added a verb inflector for English.

(3)	Added a noun pluralizer for English.

(4)	Added a simple relemmatizer for updating lemmata
	in a MorphAdorned file.

(5)	Added a syllable counter for English.

(6)	Made the English lemmatizer a little less conservative, to
	better match the lemmata in the training data.

Main changes since v0.5.0.

(1)	Added an implementation of the Lovins stemmer.

(2)	Fixed an error in the Lancaster stemmer which occurred when
	a word ended in a letter outside the range "a" through "z".

Main changes since v0.4.0 .

(1)	The bad XML output caused by certain erroneous XML input should
	no longer occur.

(2)	Fewer extraneous whitespace elements are added to adorned
	XML output.  In particular, whitespace elements are not emitted
	before an ending hard or jump tag.

(3)	The default categorization of XML tags into hard/soft/jump and
	main/side have been adjusted slightly.  In particular the
	<l> tag is now treated as a soft tag instead of a hard tag.

(4)	Nearly all of the code has been modified to use Java 1.5
	generics.  This significantly improves the clarity of the code
	as regards to types used in maps, lists, and sets, among other
	things.

(5)	A new "compare strings" tool produces Dunning's log-likelihood
	given two files containing count maps.

(6)	A new AdornedWordFilter interface provides for defining
	filters for adorned words.  A sample stop word filter based
	upon Martin Porter's stop word list is included as
	PorterStopWordFilter.

(7)	Some obsolete classes have been removed, including the
	old "printf" class (replace by the standard Java 1.5 class)
	and the old pull-like Sax wrappers.

(8)	The training data for nineteenth century fiction has been
	updated to include American fiction as well as British fiction.

(9)	The standardized spellings can be adjusted to produce "British"
	or "American" spellings, depending upon the corpus.

(10)	The new "adornwright" script and batch file may be used to
	adorn American nineteenth century fiction, principally the
	Wright archive.

Known bugs and shortcomings in snapshot v0.5.0 .

(1)	The NUPos tag set remains in flux.

(2)	The Hepple tagger remains broken.

(3)	Not all of the MorphAdorner tools have batch files/script files
	yet.

