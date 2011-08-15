package edu.northwestern.at.utils.corpuslinguistics.stopwords;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.SetFactory;

/**	Stop word list by Chris Buckley And Gerald Salton at Cornell.
 */

public class BuckleyAndSaltonStopWords
	extends BaseStopWords
	implements StopWords
{
	/**	Stop words. */

	protected static Set<String> buckleyAndSaltonStopWordsSet	=
		SetFactory.createNewSet();

	/**	Create the stop word filter.
	 */

	public BuckleyAndSaltonStopWords()
	{
		stopWordsSet.addAll( buckleyAndSaltonStopWordsSet );
	}

	/**	Static initializer. */

	static
	{
		buckleyAndSaltonStopWordsSet.add( "a" );
		buckleyAndSaltonStopWordsSet.add( "a's" );
		buckleyAndSaltonStopWordsSet.add( "able" );
		buckleyAndSaltonStopWordsSet.add( "about" );
		buckleyAndSaltonStopWordsSet.add( "above" );
		buckleyAndSaltonStopWordsSet.add( "according" );
		buckleyAndSaltonStopWordsSet.add( "accordingly" );
		buckleyAndSaltonStopWordsSet.add( "across" );
		buckleyAndSaltonStopWordsSet.add( "actually" );
		buckleyAndSaltonStopWordsSet.add( "after" );
		buckleyAndSaltonStopWordsSet.add( "afterwards" );
		buckleyAndSaltonStopWordsSet.add( "again" );
		buckleyAndSaltonStopWordsSet.add( "against" );
		buckleyAndSaltonStopWordsSet.add( "ain't" );
		buckleyAndSaltonStopWordsSet.add( "all" );
		buckleyAndSaltonStopWordsSet.add( "allow" );
		buckleyAndSaltonStopWordsSet.add( "allows" );
		buckleyAndSaltonStopWordsSet.add( "almost" );
		buckleyAndSaltonStopWordsSet.add( "alone" );
		buckleyAndSaltonStopWordsSet.add( "along" );
		buckleyAndSaltonStopWordsSet.add( "already" );
		buckleyAndSaltonStopWordsSet.add( "also" );
		buckleyAndSaltonStopWordsSet.add( "although" );
		buckleyAndSaltonStopWordsSet.add( "always" );
		buckleyAndSaltonStopWordsSet.add( "am" );
		buckleyAndSaltonStopWordsSet.add( "among" );
		buckleyAndSaltonStopWordsSet.add( "amongst" );
		buckleyAndSaltonStopWordsSet.add( "an" );
		buckleyAndSaltonStopWordsSet.add( "and" );
		buckleyAndSaltonStopWordsSet.add( "another" );
		buckleyAndSaltonStopWordsSet.add( "any" );
		buckleyAndSaltonStopWordsSet.add( "anybody" );
		buckleyAndSaltonStopWordsSet.add( "anyhow" );
		buckleyAndSaltonStopWordsSet.add( "anyone" );
		buckleyAndSaltonStopWordsSet.add( "anything" );
		buckleyAndSaltonStopWordsSet.add( "anyway" );
		buckleyAndSaltonStopWordsSet.add( "anyways" );
		buckleyAndSaltonStopWordsSet.add( "anywhere" );
		buckleyAndSaltonStopWordsSet.add( "apart" );
		buckleyAndSaltonStopWordsSet.add( "appear" );
		buckleyAndSaltonStopWordsSet.add( "appreciate" );
		buckleyAndSaltonStopWordsSet.add( "appropriate" );
		buckleyAndSaltonStopWordsSet.add( "are" );
		buckleyAndSaltonStopWordsSet.add( "aren't" );
		buckleyAndSaltonStopWordsSet.add( "around" );
		buckleyAndSaltonStopWordsSet.add( "as" );
		buckleyAndSaltonStopWordsSet.add( "aside" );
		buckleyAndSaltonStopWordsSet.add( "ask" );
		buckleyAndSaltonStopWordsSet.add( "asking" );
		buckleyAndSaltonStopWordsSet.add( "associated" );
		buckleyAndSaltonStopWordsSet.add( "at" );
		buckleyAndSaltonStopWordsSet.add( "available" );
		buckleyAndSaltonStopWordsSet.add( "away" );
		buckleyAndSaltonStopWordsSet.add( "awfully" );
		buckleyAndSaltonStopWordsSet.add( "b" );
		buckleyAndSaltonStopWordsSet.add( "be" );
		buckleyAndSaltonStopWordsSet.add( "became" );
		buckleyAndSaltonStopWordsSet.add( "because" );
		buckleyAndSaltonStopWordsSet.add( "become" );
		buckleyAndSaltonStopWordsSet.add( "becomes" );
		buckleyAndSaltonStopWordsSet.add( "becoming" );
		buckleyAndSaltonStopWordsSet.add( "been" );
		buckleyAndSaltonStopWordsSet.add( "before" );
		buckleyAndSaltonStopWordsSet.add( "beforehand" );
		buckleyAndSaltonStopWordsSet.add( "behind" );
		buckleyAndSaltonStopWordsSet.add( "being" );
		buckleyAndSaltonStopWordsSet.add( "believe" );
		buckleyAndSaltonStopWordsSet.add( "below" );
		buckleyAndSaltonStopWordsSet.add( "beside" );
		buckleyAndSaltonStopWordsSet.add( "besides" );
		buckleyAndSaltonStopWordsSet.add( "best" );
		buckleyAndSaltonStopWordsSet.add( "better" );
		buckleyAndSaltonStopWordsSet.add( "between" );
		buckleyAndSaltonStopWordsSet.add( "beyond" );
		buckleyAndSaltonStopWordsSet.add( "both" );
		buckleyAndSaltonStopWordsSet.add( "brief" );
		buckleyAndSaltonStopWordsSet.add( "but" );
		buckleyAndSaltonStopWordsSet.add( "by" );
		buckleyAndSaltonStopWordsSet.add( "c" );
		buckleyAndSaltonStopWordsSet.add( "c'mon" );
		buckleyAndSaltonStopWordsSet.add( "c's" );
		buckleyAndSaltonStopWordsSet.add( "came" );
		buckleyAndSaltonStopWordsSet.add( "can" );
		buckleyAndSaltonStopWordsSet.add( "can't" );
		buckleyAndSaltonStopWordsSet.add( "cannot" );
		buckleyAndSaltonStopWordsSet.add( "cant" );
		buckleyAndSaltonStopWordsSet.add( "cause" );
		buckleyAndSaltonStopWordsSet.add( "causes" );
		buckleyAndSaltonStopWordsSet.add( "certain" );
		buckleyAndSaltonStopWordsSet.add( "certainly" );
		buckleyAndSaltonStopWordsSet.add( "changes" );
		buckleyAndSaltonStopWordsSet.add( "clearly" );
		buckleyAndSaltonStopWordsSet.add( "co" );
		buckleyAndSaltonStopWordsSet.add( "com" );
		buckleyAndSaltonStopWordsSet.add( "come" );
		buckleyAndSaltonStopWordsSet.add( "comes" );
		buckleyAndSaltonStopWordsSet.add( "concerning" );
		buckleyAndSaltonStopWordsSet.add( "consequently" );
		buckleyAndSaltonStopWordsSet.add( "consider" );
		buckleyAndSaltonStopWordsSet.add( "considering" );
		buckleyAndSaltonStopWordsSet.add( "contain" );
		buckleyAndSaltonStopWordsSet.add( "containing" );
		buckleyAndSaltonStopWordsSet.add( "contains" );
		buckleyAndSaltonStopWordsSet.add( "corresponding" );
		buckleyAndSaltonStopWordsSet.add( "could" );
		buckleyAndSaltonStopWordsSet.add( "couldn't" );
		buckleyAndSaltonStopWordsSet.add( "course" );
		buckleyAndSaltonStopWordsSet.add( "currently" );
		buckleyAndSaltonStopWordsSet.add( "d" );
		buckleyAndSaltonStopWordsSet.add( "definitely" );
		buckleyAndSaltonStopWordsSet.add( "described" );
		buckleyAndSaltonStopWordsSet.add( "despite" );
		buckleyAndSaltonStopWordsSet.add( "did" );
		buckleyAndSaltonStopWordsSet.add( "didn't" );
		buckleyAndSaltonStopWordsSet.add( "different" );
		buckleyAndSaltonStopWordsSet.add( "do" );
		buckleyAndSaltonStopWordsSet.add( "does" );
		buckleyAndSaltonStopWordsSet.add( "doesn't" );
		buckleyAndSaltonStopWordsSet.add( "doing" );
		buckleyAndSaltonStopWordsSet.add( "don't" );
		buckleyAndSaltonStopWordsSet.add( "done" );
		buckleyAndSaltonStopWordsSet.add( "down" );
		buckleyAndSaltonStopWordsSet.add( "downwards" );
		buckleyAndSaltonStopWordsSet.add( "during" );
		buckleyAndSaltonStopWordsSet.add( "e" );
		buckleyAndSaltonStopWordsSet.add( "each" );
		buckleyAndSaltonStopWordsSet.add( "edu" );
		buckleyAndSaltonStopWordsSet.add( "eg" );
		buckleyAndSaltonStopWordsSet.add( "eight" );
		buckleyAndSaltonStopWordsSet.add( "either" );
		buckleyAndSaltonStopWordsSet.add( "else" );
		buckleyAndSaltonStopWordsSet.add( "elsewhere" );
		buckleyAndSaltonStopWordsSet.add( "enough" );
		buckleyAndSaltonStopWordsSet.add( "entirely" );
		buckleyAndSaltonStopWordsSet.add( "especially" );
		buckleyAndSaltonStopWordsSet.add( "et" );
		buckleyAndSaltonStopWordsSet.add( "etc" );
		buckleyAndSaltonStopWordsSet.add( "even" );
		buckleyAndSaltonStopWordsSet.add( "ever" );
		buckleyAndSaltonStopWordsSet.add( "every" );
		buckleyAndSaltonStopWordsSet.add( "everybody" );
		buckleyAndSaltonStopWordsSet.add( "everyone" );
		buckleyAndSaltonStopWordsSet.add( "everything" );
		buckleyAndSaltonStopWordsSet.add( "everywhere" );
		buckleyAndSaltonStopWordsSet.add( "ex" );
		buckleyAndSaltonStopWordsSet.add( "exactly" );
		buckleyAndSaltonStopWordsSet.add( "example" );
		buckleyAndSaltonStopWordsSet.add( "except" );
		buckleyAndSaltonStopWordsSet.add( "f" );
		buckleyAndSaltonStopWordsSet.add( "far" );
		buckleyAndSaltonStopWordsSet.add( "few" );
		buckleyAndSaltonStopWordsSet.add( "fifth" );
		buckleyAndSaltonStopWordsSet.add( "first" );
		buckleyAndSaltonStopWordsSet.add( "five" );
		buckleyAndSaltonStopWordsSet.add( "followed" );
		buckleyAndSaltonStopWordsSet.add( "following" );
		buckleyAndSaltonStopWordsSet.add( "follows" );
		buckleyAndSaltonStopWordsSet.add( "for" );
		buckleyAndSaltonStopWordsSet.add( "former" );
		buckleyAndSaltonStopWordsSet.add( "formerly" );
		buckleyAndSaltonStopWordsSet.add( "forth" );
		buckleyAndSaltonStopWordsSet.add( "four" );
		buckleyAndSaltonStopWordsSet.add( "from" );
		buckleyAndSaltonStopWordsSet.add( "further" );
		buckleyAndSaltonStopWordsSet.add( "furthermore" );
		buckleyAndSaltonStopWordsSet.add( "g" );
		buckleyAndSaltonStopWordsSet.add( "get" );
		buckleyAndSaltonStopWordsSet.add( "gets" );
		buckleyAndSaltonStopWordsSet.add( "getting" );
		buckleyAndSaltonStopWordsSet.add( "given" );
		buckleyAndSaltonStopWordsSet.add( "gives" );
		buckleyAndSaltonStopWordsSet.add( "go" );
		buckleyAndSaltonStopWordsSet.add( "goes" );
		buckleyAndSaltonStopWordsSet.add( "going" );
		buckleyAndSaltonStopWordsSet.add( "gone" );
		buckleyAndSaltonStopWordsSet.add( "got" );
		buckleyAndSaltonStopWordsSet.add( "gotten" );
		buckleyAndSaltonStopWordsSet.add( "greetings" );
		buckleyAndSaltonStopWordsSet.add( "h" );
		buckleyAndSaltonStopWordsSet.add( "had" );
		buckleyAndSaltonStopWordsSet.add( "hadn't" );
		buckleyAndSaltonStopWordsSet.add( "happens" );
		buckleyAndSaltonStopWordsSet.add( "hardly" );
		buckleyAndSaltonStopWordsSet.add( "has" );
		buckleyAndSaltonStopWordsSet.add( "hasn't" );
		buckleyAndSaltonStopWordsSet.add( "have" );
		buckleyAndSaltonStopWordsSet.add( "haven't" );
		buckleyAndSaltonStopWordsSet.add( "having" );
		buckleyAndSaltonStopWordsSet.add( "he" );
		buckleyAndSaltonStopWordsSet.add( "he's" );
		buckleyAndSaltonStopWordsSet.add( "hello" );
		buckleyAndSaltonStopWordsSet.add( "help" );
		buckleyAndSaltonStopWordsSet.add( "hence" );
		buckleyAndSaltonStopWordsSet.add( "her" );
		buckleyAndSaltonStopWordsSet.add( "here" );
		buckleyAndSaltonStopWordsSet.add( "here's" );
		buckleyAndSaltonStopWordsSet.add( "hereafter" );
		buckleyAndSaltonStopWordsSet.add( "hereby" );
		buckleyAndSaltonStopWordsSet.add( "herein" );
		buckleyAndSaltonStopWordsSet.add( "hereupon" );
		buckleyAndSaltonStopWordsSet.add( "hers" );
		buckleyAndSaltonStopWordsSet.add( "herself" );
		buckleyAndSaltonStopWordsSet.add( "hi" );
		buckleyAndSaltonStopWordsSet.add( "him" );
		buckleyAndSaltonStopWordsSet.add( "himself" );
		buckleyAndSaltonStopWordsSet.add( "his" );
		buckleyAndSaltonStopWordsSet.add( "hither" );
		buckleyAndSaltonStopWordsSet.add( "hopefully" );
		buckleyAndSaltonStopWordsSet.add( "how" );
		buckleyAndSaltonStopWordsSet.add( "howbeit" );
		buckleyAndSaltonStopWordsSet.add( "however" );
		buckleyAndSaltonStopWordsSet.add( "i" );
		buckleyAndSaltonStopWordsSet.add( "i'd" );
		buckleyAndSaltonStopWordsSet.add( "i'll" );
		buckleyAndSaltonStopWordsSet.add( "i'm" );
		buckleyAndSaltonStopWordsSet.add( "i've" );
		buckleyAndSaltonStopWordsSet.add( "ie" );
		buckleyAndSaltonStopWordsSet.add( "if" );
		buckleyAndSaltonStopWordsSet.add( "ignored" );
		buckleyAndSaltonStopWordsSet.add( "immediate" );
		buckleyAndSaltonStopWordsSet.add( "in" );
		buckleyAndSaltonStopWordsSet.add( "inasmuch" );
		buckleyAndSaltonStopWordsSet.add( "inc" );
		buckleyAndSaltonStopWordsSet.add( "indeed" );
		buckleyAndSaltonStopWordsSet.add( "indicate" );
		buckleyAndSaltonStopWordsSet.add( "indicated" );
		buckleyAndSaltonStopWordsSet.add( "indicates" );
		buckleyAndSaltonStopWordsSet.add( "inner" );
		buckleyAndSaltonStopWordsSet.add( "insofar" );
		buckleyAndSaltonStopWordsSet.add( "instead" );
		buckleyAndSaltonStopWordsSet.add( "into" );
		buckleyAndSaltonStopWordsSet.add( "inward" );
		buckleyAndSaltonStopWordsSet.add( "is" );
		buckleyAndSaltonStopWordsSet.add( "isn't" );
		buckleyAndSaltonStopWordsSet.add( "it" );
		buckleyAndSaltonStopWordsSet.add( "it'd" );
		buckleyAndSaltonStopWordsSet.add( "it'll" );
		buckleyAndSaltonStopWordsSet.add( "it's" );
		buckleyAndSaltonStopWordsSet.add( "its" );
		buckleyAndSaltonStopWordsSet.add( "itself" );
		buckleyAndSaltonStopWordsSet.add( "j" );
		buckleyAndSaltonStopWordsSet.add( "just" );
		buckleyAndSaltonStopWordsSet.add( "k" );
		buckleyAndSaltonStopWordsSet.add( "keep" );
		buckleyAndSaltonStopWordsSet.add( "keeps" );
		buckleyAndSaltonStopWordsSet.add( "kept" );
		buckleyAndSaltonStopWordsSet.add( "know" );
		buckleyAndSaltonStopWordsSet.add( "knows" );
		buckleyAndSaltonStopWordsSet.add( "known" );
		buckleyAndSaltonStopWordsSet.add( "l" );
		buckleyAndSaltonStopWordsSet.add( "last" );
		buckleyAndSaltonStopWordsSet.add( "lately" );
		buckleyAndSaltonStopWordsSet.add( "later" );
		buckleyAndSaltonStopWordsSet.add( "latter" );
		buckleyAndSaltonStopWordsSet.add( "latterly" );
		buckleyAndSaltonStopWordsSet.add( "least" );
		buckleyAndSaltonStopWordsSet.add( "less" );
		buckleyAndSaltonStopWordsSet.add( "lest" );
		buckleyAndSaltonStopWordsSet.add( "let" );
		buckleyAndSaltonStopWordsSet.add( "let's" );
		buckleyAndSaltonStopWordsSet.add( "like" );
		buckleyAndSaltonStopWordsSet.add( "liked" );
		buckleyAndSaltonStopWordsSet.add( "likely" );
		buckleyAndSaltonStopWordsSet.add( "little" );
		buckleyAndSaltonStopWordsSet.add( "look" );
		buckleyAndSaltonStopWordsSet.add( "looking" );
		buckleyAndSaltonStopWordsSet.add( "looks" );
		buckleyAndSaltonStopWordsSet.add( "ltd" );
		buckleyAndSaltonStopWordsSet.add( "m" );
		buckleyAndSaltonStopWordsSet.add( "mainly" );
		buckleyAndSaltonStopWordsSet.add( "many" );
		buckleyAndSaltonStopWordsSet.add( "may" );
		buckleyAndSaltonStopWordsSet.add( "maybe" );
		buckleyAndSaltonStopWordsSet.add( "me" );
		buckleyAndSaltonStopWordsSet.add( "mean" );
		buckleyAndSaltonStopWordsSet.add( "meanwhile" );
		buckleyAndSaltonStopWordsSet.add( "merely" );
		buckleyAndSaltonStopWordsSet.add( "might" );
		buckleyAndSaltonStopWordsSet.add( "more" );
		buckleyAndSaltonStopWordsSet.add( "moreover" );
		buckleyAndSaltonStopWordsSet.add( "most" );
		buckleyAndSaltonStopWordsSet.add( "mostly" );
		buckleyAndSaltonStopWordsSet.add( "much" );
		buckleyAndSaltonStopWordsSet.add( "must" );
		buckleyAndSaltonStopWordsSet.add( "my" );
		buckleyAndSaltonStopWordsSet.add( "myself" );
		buckleyAndSaltonStopWordsSet.add( "n" );
		buckleyAndSaltonStopWordsSet.add( "name" );
		buckleyAndSaltonStopWordsSet.add( "namely" );
		buckleyAndSaltonStopWordsSet.add( "nd" );
		buckleyAndSaltonStopWordsSet.add( "near" );
		buckleyAndSaltonStopWordsSet.add( "nearly" );
		buckleyAndSaltonStopWordsSet.add( "necessary" );
		buckleyAndSaltonStopWordsSet.add( "need" );
		buckleyAndSaltonStopWordsSet.add( "needs" );
		buckleyAndSaltonStopWordsSet.add( "neither" );
		buckleyAndSaltonStopWordsSet.add( "never" );
		buckleyAndSaltonStopWordsSet.add( "nevertheless" );
		buckleyAndSaltonStopWordsSet.add( "new" );
		buckleyAndSaltonStopWordsSet.add( "next" );
		buckleyAndSaltonStopWordsSet.add( "nine" );
		buckleyAndSaltonStopWordsSet.add( "no" );
		buckleyAndSaltonStopWordsSet.add( "nobody" );
		buckleyAndSaltonStopWordsSet.add( "non" );
		buckleyAndSaltonStopWordsSet.add( "none" );
		buckleyAndSaltonStopWordsSet.add( "noone" );
		buckleyAndSaltonStopWordsSet.add( "nor" );
		buckleyAndSaltonStopWordsSet.add( "normally" );
		buckleyAndSaltonStopWordsSet.add( "not" );
		buckleyAndSaltonStopWordsSet.add( "nothing" );
		buckleyAndSaltonStopWordsSet.add( "novel" );
		buckleyAndSaltonStopWordsSet.add( "now" );
		buckleyAndSaltonStopWordsSet.add( "nowhere" );
		buckleyAndSaltonStopWordsSet.add( "o" );
		buckleyAndSaltonStopWordsSet.add( "obviously" );
		buckleyAndSaltonStopWordsSet.add( "of" );
		buckleyAndSaltonStopWordsSet.add( "off" );
		buckleyAndSaltonStopWordsSet.add( "often" );
		buckleyAndSaltonStopWordsSet.add( "oh" );
		buckleyAndSaltonStopWordsSet.add( "ok" );
		buckleyAndSaltonStopWordsSet.add( "okay" );
		buckleyAndSaltonStopWordsSet.add( "old" );
		buckleyAndSaltonStopWordsSet.add( "on" );
		buckleyAndSaltonStopWordsSet.add( "once" );
		buckleyAndSaltonStopWordsSet.add( "one" );
		buckleyAndSaltonStopWordsSet.add( "ones" );
		buckleyAndSaltonStopWordsSet.add( "only" );
		buckleyAndSaltonStopWordsSet.add( "onto" );
		buckleyAndSaltonStopWordsSet.add( "or" );
		buckleyAndSaltonStopWordsSet.add( "other" );
		buckleyAndSaltonStopWordsSet.add( "others" );
		buckleyAndSaltonStopWordsSet.add( "otherwise" );
		buckleyAndSaltonStopWordsSet.add( "ought" );
		buckleyAndSaltonStopWordsSet.add( "our" );
		buckleyAndSaltonStopWordsSet.add( "ours" );
		buckleyAndSaltonStopWordsSet.add( "ourselves" );
		buckleyAndSaltonStopWordsSet.add( "out" );
		buckleyAndSaltonStopWordsSet.add( "outside" );
		buckleyAndSaltonStopWordsSet.add( "over" );
		buckleyAndSaltonStopWordsSet.add( "overall" );
		buckleyAndSaltonStopWordsSet.add( "own" );
		buckleyAndSaltonStopWordsSet.add( "p" );
		buckleyAndSaltonStopWordsSet.add( "particular" );
		buckleyAndSaltonStopWordsSet.add( "particularly" );
		buckleyAndSaltonStopWordsSet.add( "per" );
		buckleyAndSaltonStopWordsSet.add( "perhaps" );
		buckleyAndSaltonStopWordsSet.add( "placed" );
		buckleyAndSaltonStopWordsSet.add( "please" );
		buckleyAndSaltonStopWordsSet.add( "plus" );
		buckleyAndSaltonStopWordsSet.add( "possible" );
		buckleyAndSaltonStopWordsSet.add( "presumably" );
		buckleyAndSaltonStopWordsSet.add( "probably" );
		buckleyAndSaltonStopWordsSet.add( "provides" );
		buckleyAndSaltonStopWordsSet.add( "q" );
		buckleyAndSaltonStopWordsSet.add( "que" );
		buckleyAndSaltonStopWordsSet.add( "quite" );
		buckleyAndSaltonStopWordsSet.add( "qv" );
		buckleyAndSaltonStopWordsSet.add( "r" );
		buckleyAndSaltonStopWordsSet.add( "rather" );
		buckleyAndSaltonStopWordsSet.add( "rd" );
		buckleyAndSaltonStopWordsSet.add( "re" );
		buckleyAndSaltonStopWordsSet.add( "really" );
		buckleyAndSaltonStopWordsSet.add( "reasonably" );
		buckleyAndSaltonStopWordsSet.add( "regarding" );
		buckleyAndSaltonStopWordsSet.add( "regardless" );
		buckleyAndSaltonStopWordsSet.add( "regards" );
		buckleyAndSaltonStopWordsSet.add( "relatively" );
		buckleyAndSaltonStopWordsSet.add( "respectively" );
		buckleyAndSaltonStopWordsSet.add( "right" );
		buckleyAndSaltonStopWordsSet.add( "s" );
		buckleyAndSaltonStopWordsSet.add( "said" );
		buckleyAndSaltonStopWordsSet.add( "same" );
		buckleyAndSaltonStopWordsSet.add( "saw" );
		buckleyAndSaltonStopWordsSet.add( "say" );
		buckleyAndSaltonStopWordsSet.add( "saying" );
		buckleyAndSaltonStopWordsSet.add( "says" );
		buckleyAndSaltonStopWordsSet.add( "second" );
		buckleyAndSaltonStopWordsSet.add( "secondly" );
		buckleyAndSaltonStopWordsSet.add( "see" );
		buckleyAndSaltonStopWordsSet.add( "seeing" );
		buckleyAndSaltonStopWordsSet.add( "seem" );
		buckleyAndSaltonStopWordsSet.add( "seemed" );
		buckleyAndSaltonStopWordsSet.add( "seeming" );
		buckleyAndSaltonStopWordsSet.add( "seems" );
		buckleyAndSaltonStopWordsSet.add( "seen" );
		buckleyAndSaltonStopWordsSet.add( "self" );
		buckleyAndSaltonStopWordsSet.add( "selves" );
		buckleyAndSaltonStopWordsSet.add( "sensible" );
		buckleyAndSaltonStopWordsSet.add( "sent" );
		buckleyAndSaltonStopWordsSet.add( "serious" );
		buckleyAndSaltonStopWordsSet.add( "seriously" );
		buckleyAndSaltonStopWordsSet.add( "seven" );
		buckleyAndSaltonStopWordsSet.add( "several" );
		buckleyAndSaltonStopWordsSet.add( "shall" );
		buckleyAndSaltonStopWordsSet.add( "she" );
		buckleyAndSaltonStopWordsSet.add( "should" );
		buckleyAndSaltonStopWordsSet.add( "shouldn't" );
		buckleyAndSaltonStopWordsSet.add( "since" );
		buckleyAndSaltonStopWordsSet.add( "six" );
		buckleyAndSaltonStopWordsSet.add( "so" );
		buckleyAndSaltonStopWordsSet.add( "some" );
		buckleyAndSaltonStopWordsSet.add( "somebody" );
		buckleyAndSaltonStopWordsSet.add( "somehow" );
		buckleyAndSaltonStopWordsSet.add( "someone" );
		buckleyAndSaltonStopWordsSet.add( "something" );
		buckleyAndSaltonStopWordsSet.add( "sometime" );
		buckleyAndSaltonStopWordsSet.add( "sometimes" );
		buckleyAndSaltonStopWordsSet.add( "somewhat" );
		buckleyAndSaltonStopWordsSet.add( "somewhere" );
		buckleyAndSaltonStopWordsSet.add( "soon" );
		buckleyAndSaltonStopWordsSet.add( "sorry" );
		buckleyAndSaltonStopWordsSet.add( "specified" );
		buckleyAndSaltonStopWordsSet.add( "specify" );
		buckleyAndSaltonStopWordsSet.add( "specifying" );
		buckleyAndSaltonStopWordsSet.add( "still" );
		buckleyAndSaltonStopWordsSet.add( "sub" );
		buckleyAndSaltonStopWordsSet.add( "such" );
		buckleyAndSaltonStopWordsSet.add( "sup" );
		buckleyAndSaltonStopWordsSet.add( "sure" );
		buckleyAndSaltonStopWordsSet.add( "t" );
		buckleyAndSaltonStopWordsSet.add( "t's" );
		buckleyAndSaltonStopWordsSet.add( "take" );
		buckleyAndSaltonStopWordsSet.add( "taken" );
		buckleyAndSaltonStopWordsSet.add( "tell" );
		buckleyAndSaltonStopWordsSet.add( "tends" );
		buckleyAndSaltonStopWordsSet.add( "th" );
		buckleyAndSaltonStopWordsSet.add( "than" );
		buckleyAndSaltonStopWordsSet.add( "thank" );
		buckleyAndSaltonStopWordsSet.add( "thanks" );
		buckleyAndSaltonStopWordsSet.add( "thanx" );
		buckleyAndSaltonStopWordsSet.add( "that" );
		buckleyAndSaltonStopWordsSet.add( "that's" );
		buckleyAndSaltonStopWordsSet.add( "thats" );
		buckleyAndSaltonStopWordsSet.add( "the" );
		buckleyAndSaltonStopWordsSet.add( "their" );
		buckleyAndSaltonStopWordsSet.add( "theirs" );
		buckleyAndSaltonStopWordsSet.add( "them" );
		buckleyAndSaltonStopWordsSet.add( "themselves" );
		buckleyAndSaltonStopWordsSet.add( "then" );
		buckleyAndSaltonStopWordsSet.add( "thence" );
		buckleyAndSaltonStopWordsSet.add( "there" );
		buckleyAndSaltonStopWordsSet.add( "there's" );
		buckleyAndSaltonStopWordsSet.add( "thereafter" );
		buckleyAndSaltonStopWordsSet.add( "thereby" );
		buckleyAndSaltonStopWordsSet.add( "therefore" );
		buckleyAndSaltonStopWordsSet.add( "therein" );
		buckleyAndSaltonStopWordsSet.add( "theres" );
		buckleyAndSaltonStopWordsSet.add( "thereupon" );
		buckleyAndSaltonStopWordsSet.add( "these" );
		buckleyAndSaltonStopWordsSet.add( "they" );
		buckleyAndSaltonStopWordsSet.add( "they'd" );
		buckleyAndSaltonStopWordsSet.add( "they'll" );
		buckleyAndSaltonStopWordsSet.add( "they're" );
		buckleyAndSaltonStopWordsSet.add( "they've" );
		buckleyAndSaltonStopWordsSet.add( "think" );
		buckleyAndSaltonStopWordsSet.add( "third" );
		buckleyAndSaltonStopWordsSet.add( "this" );
		buckleyAndSaltonStopWordsSet.add( "thorough" );
		buckleyAndSaltonStopWordsSet.add( "thoroughly" );
		buckleyAndSaltonStopWordsSet.add( "those" );
		buckleyAndSaltonStopWordsSet.add( "though" );
		buckleyAndSaltonStopWordsSet.add( "three" );
		buckleyAndSaltonStopWordsSet.add( "through" );
		buckleyAndSaltonStopWordsSet.add( "throughout" );
		buckleyAndSaltonStopWordsSet.add( "thru" );
		buckleyAndSaltonStopWordsSet.add( "thus" );
		buckleyAndSaltonStopWordsSet.add( "to" );
		buckleyAndSaltonStopWordsSet.add( "together" );
		buckleyAndSaltonStopWordsSet.add( "too" );
		buckleyAndSaltonStopWordsSet.add( "took" );
		buckleyAndSaltonStopWordsSet.add( "toward" );
		buckleyAndSaltonStopWordsSet.add( "towards" );
		buckleyAndSaltonStopWordsSet.add( "tried" );
		buckleyAndSaltonStopWordsSet.add( "tries" );
		buckleyAndSaltonStopWordsSet.add( "truly" );
		buckleyAndSaltonStopWordsSet.add( "try" );
		buckleyAndSaltonStopWordsSet.add( "trying" );
		buckleyAndSaltonStopWordsSet.add( "twice" );
		buckleyAndSaltonStopWordsSet.add( "two" );
		buckleyAndSaltonStopWordsSet.add( "u" );
		buckleyAndSaltonStopWordsSet.add( "un" );
		buckleyAndSaltonStopWordsSet.add( "under" );
		buckleyAndSaltonStopWordsSet.add( "unfortunately" );
		buckleyAndSaltonStopWordsSet.add( "unless" );
		buckleyAndSaltonStopWordsSet.add( "unlikely" );
		buckleyAndSaltonStopWordsSet.add( "until" );
		buckleyAndSaltonStopWordsSet.add( "unto" );
		buckleyAndSaltonStopWordsSet.add( "up" );
		buckleyAndSaltonStopWordsSet.add( "upon" );
		buckleyAndSaltonStopWordsSet.add( "us" );
		buckleyAndSaltonStopWordsSet.add( "use" );
		buckleyAndSaltonStopWordsSet.add( "used" );
		buckleyAndSaltonStopWordsSet.add( "useful" );
		buckleyAndSaltonStopWordsSet.add( "uses" );
		buckleyAndSaltonStopWordsSet.add( "using" );
		buckleyAndSaltonStopWordsSet.add( "usually" );
		buckleyAndSaltonStopWordsSet.add( "uucp" );
		buckleyAndSaltonStopWordsSet.add( "v" );
		buckleyAndSaltonStopWordsSet.add( "value" );
		buckleyAndSaltonStopWordsSet.add( "various" );
		buckleyAndSaltonStopWordsSet.add( "very" );
		buckleyAndSaltonStopWordsSet.add( "via" );
		buckleyAndSaltonStopWordsSet.add( "viz" );
		buckleyAndSaltonStopWordsSet.add( "vs" );
		buckleyAndSaltonStopWordsSet.add( "w" );
		buckleyAndSaltonStopWordsSet.add( "want" );
		buckleyAndSaltonStopWordsSet.add( "wants" );
		buckleyAndSaltonStopWordsSet.add( "was" );
		buckleyAndSaltonStopWordsSet.add( "wasn't" );
		buckleyAndSaltonStopWordsSet.add( "way" );
		buckleyAndSaltonStopWordsSet.add( "we" );
		buckleyAndSaltonStopWordsSet.add( "we'd" );
		buckleyAndSaltonStopWordsSet.add( "we'll" );
		buckleyAndSaltonStopWordsSet.add( "we're" );
		buckleyAndSaltonStopWordsSet.add( "we've" );
		buckleyAndSaltonStopWordsSet.add( "welcome" );
		buckleyAndSaltonStopWordsSet.add( "well" );
		buckleyAndSaltonStopWordsSet.add( "went" );
		buckleyAndSaltonStopWordsSet.add( "were" );
		buckleyAndSaltonStopWordsSet.add( "weren't" );
		buckleyAndSaltonStopWordsSet.add( "what" );
		buckleyAndSaltonStopWordsSet.add( "what's" );
		buckleyAndSaltonStopWordsSet.add( "whatever" );
		buckleyAndSaltonStopWordsSet.add( "when" );
		buckleyAndSaltonStopWordsSet.add( "whence" );
		buckleyAndSaltonStopWordsSet.add( "whenever" );
		buckleyAndSaltonStopWordsSet.add( "where" );
		buckleyAndSaltonStopWordsSet.add( "where's" );
		buckleyAndSaltonStopWordsSet.add( "whereafter" );
		buckleyAndSaltonStopWordsSet.add( "whereas" );
		buckleyAndSaltonStopWordsSet.add( "whereby" );
		buckleyAndSaltonStopWordsSet.add( "wherein" );
		buckleyAndSaltonStopWordsSet.add( "whereupon" );
		buckleyAndSaltonStopWordsSet.add( "wherever" );
		buckleyAndSaltonStopWordsSet.add( "whether" );
		buckleyAndSaltonStopWordsSet.add( "which" );
		buckleyAndSaltonStopWordsSet.add( "while" );
		buckleyAndSaltonStopWordsSet.add( "whither" );
		buckleyAndSaltonStopWordsSet.add( "who" );
		buckleyAndSaltonStopWordsSet.add( "who's" );
		buckleyAndSaltonStopWordsSet.add( "whoever" );
		buckleyAndSaltonStopWordsSet.add( "whole" );
		buckleyAndSaltonStopWordsSet.add( "whom" );
		buckleyAndSaltonStopWordsSet.add( "whose" );
		buckleyAndSaltonStopWordsSet.add( "why" );
		buckleyAndSaltonStopWordsSet.add( "will" );
		buckleyAndSaltonStopWordsSet.add( "willing" );
		buckleyAndSaltonStopWordsSet.add( "wish" );
		buckleyAndSaltonStopWordsSet.add( "with" );
		buckleyAndSaltonStopWordsSet.add( "within" );
		buckleyAndSaltonStopWordsSet.add( "without" );
		buckleyAndSaltonStopWordsSet.add( "won't" );
		buckleyAndSaltonStopWordsSet.add( "wonder" );
		buckleyAndSaltonStopWordsSet.add( "would" );
		buckleyAndSaltonStopWordsSet.add( "would" );
		buckleyAndSaltonStopWordsSet.add( "wouldn't" );
		buckleyAndSaltonStopWordsSet.add( "x" );
		buckleyAndSaltonStopWordsSet.add( "y" );
		buckleyAndSaltonStopWordsSet.add( "yes" );
		buckleyAndSaltonStopWordsSet.add( "yet" );
		buckleyAndSaltonStopWordsSet.add( "you" );
		buckleyAndSaltonStopWordsSet.add( "you'd" );
		buckleyAndSaltonStopWordsSet.add( "you'll" );
		buckleyAndSaltonStopWordsSet.add( "you're" );
		buckleyAndSaltonStopWordsSet.add( "you've" );
		buckleyAndSaltonStopWordsSet.add( "your" );
		buckleyAndSaltonStopWordsSet.add( "yours" );
		buckleyAndSaltonStopWordsSet.add( "yourself" );
		buckleyAndSaltonStopWordsSet.add( "yourselves" );
		buckleyAndSaltonStopWordsSet.add( "z" );
		buckleyAndSaltonStopWordsSet.add( "zero" );
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



