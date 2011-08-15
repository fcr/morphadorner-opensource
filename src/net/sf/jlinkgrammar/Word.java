package net.sf.jlinkgrammar;

/**
 * The basic word class
 * TODO - Create word comparator and internationalization.
 * That is: If the word is in French and we need to know if
 * it is a conjunction we need something like:
 * Jaque et Pierre == John and Peter
 *  Word.compare(english("and")); 
 *
 */
public class Word {
	  /**
     * Looks up the word s in the dictionary.  Returns null if it's not there.
     * If there, it builds the list of expressions for the word, and returns
     * a pointer to it.
     * @param s is a word as a String object
     * @return XNode
     * 
     */
    private void special_string(String s, Dictionary dict) {
        XNode e;
        if (dict.boolean_dictionary_lookup(s)) {
            this.x = build_word_expressions(s,dict);
            for (e = this.x; e != null; e = e.next) {
                e.string = this.string;
            }
        } else {
            throw new RuntimeException(
                "Can't build expressions. To process this sentence your dictionary needs the word \"" + s + "\"");
        }
    }
    public static XNode build_word_expressions(String s,Dictionary dict) {
        
        DictNode dn;
        XNode x, y;

        dn = dict.dictionary_lookup(s);

        x = null;
        while (dn != null) {
            y = new XNode();
            y.next = x;
            x = y;
            x.exp = Exp.copy_Exp(dn.exp);
            x.string = dn.string;
            dn = dn.right;
        }
        return x;
    }
    /**
     * Default constructor
     * @param s
     * @param dict
     */
	public Word(String s, Dictionary dict){
		this.string = s;
        if (dict.boolean_dictionary_lookup(s)) {
            this.x = build_word_expressions(s,dict);
        } else if (
            Character.isUpperCase(s.charAt(0)) && Dictionary.is_s_word(s) && dict.pl_capitalized_word_defined) {
            special_string(GlobalBean.PL_PROPER_WORD,dict);
        } else if (Character.isUpperCase(s.charAt(0)) && dict.capitalized_word_defined) {
            special_string(GlobalBean.PROPER_WORD,dict);
        } else if (Dictionary.is_number(s) && dict.number_word_defined) {
            /* we know it's a plural number, or 1 */
            /* if the string is 1, we'll only be here if 1's not in the dictionary */
            special_string(GlobalBean.NUMBER_WORD,dict);
        } else if (Dictionary.ishyphenated(s) && dict.hyphenated_word_defined) {
            /* singular hyphenated */
            special_string(GlobalBean.HYPHENATED_WORD,dict);
        } else if (Dictionary.is_ing_word(s) && dict.ing_word_defined) {
            guessed_string(s, GlobalBean.ING_WORD,dict);
        } else if (Dictionary.is_s_word(s) && dict.s_word_defined) {
            guessed_string(s, GlobalBean.S_WORD,dict);
        } else if (Dictionary.is_ed_word(s) && dict.ed_word_defined) {
            guessed_string(s, GlobalBean.ED_WORD,dict);
        } else if (Dictionary.is_ly_word(s) && dict.ly_word_defined) {
            guessed_string(s, GlobalBean.LY_WORD,dict);
        } else if (dict.unknown_word_defined && dict.use_unknown_word) {
            handle_unknown_word(s,dict);
        } else {
            /* the reason I can assert this is that the word
               should have been looked up already if we get here. */
            throw new RuntimeException("I should have found that word.");
        }	
	}
	private void guessed_string(String s, String type, Dictionary dict) {
        XNode e;
        int t;
        if (dict.boolean_dictionary_lookup(type)) {
            this.x = build_word_expressions(type,dict);
            e = this.x;
            if (Dictionary.is_s_word(s)) {
                for (; e != null; e = e.next) {
                    t = e.string.indexOf('.');
                    if (t >= 0) {
                        e.string = s + "[!]." + s.substring(t + 1);
                    } else {
                        e.string = s + "[!]";
                    }
                }
            } else {
                if (Dictionary.is_ed_word(s)) {
                    e.string = s + "[!].v";
                } else if (Dictionary.is_ing_word(s)) {
                    e.string = s + "[!].g";
                } else if (Dictionary.is_ly_word(s)) {
                    e.string = s + "[!].e";
                } else {
                    e.string = s + "[!]";
                }
            }
        } else {
            throw new RuntimeException(
                "Can't build expressions. To process this sentence your dictionary needs the word \"" + type + "\"");
        }
    }

    private void handle_unknown_word(String s, Dictionary dict) {
        /* puts into word[i].x the expression for the unknown word */
        /* the parameter s is the word that was not in the dictionary */
        /* it massages the names to have the corresponding subscripts */
        /* to those of the unknown words */
        /* so "grok" becomes "grok[?].v"  */
        int t;
        XNode d;

        this.x = build_word_expressions(GlobalBean.UNKNOWN_WORD,dict);

        for (d = this.x; d != null; d = d.next) {
            t = d.string.indexOf('.');
            StringBuffer str = new StringBuffer();
            if (t >= 0) {
                str.append(s + "[?]." + d.string.substring(t + 1));
            } else {
                str.append(s + "[?]");
            }
            d.string = str.toString();
        }
    }
    /**
     * this word as a String object
     */
    public String string;
    /**
     * a linked list of equivilent expressions also a sentence starts out with these
     * @see XNode
     */
    public XNode x;      
    /**
     * holds the disjunct of the word, eventually these get generated.
     * @see Disjunct
     */
    public Disjunct d;    
    /**
      * indicates that the first character is upper case
      * TODO - Remove English (Indo European) dependancy 
      */
    public boolean firstupper;
    /**
     * is conjunction or not
     */
    public boolean is_conjunction=false;
}
