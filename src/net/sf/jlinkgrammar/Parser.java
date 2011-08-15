package net.sf.jlinkgrammar;
/*
 * Parser.java
 *
 * Created on October 20, 2006, 3:02 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * This class is meant to be a bean type interface to link grammar.  All the
 * options are preset and specific actions have to be taken to override them.
 * @author johnryan
 */
public class Parser {

       private static Dictionary dict;
       private static Sentence sent;
       private static String dictionary_file = null;
       private static String post_process_knowledge_file = null;
       private static String constituent_knowledge_file = null;
       private static String affix_file = null;
       private static boolean pp_on = true;
       private static boolean af_on = true;
       private static boolean cons_on = true;
       private static int num_linkages;
       private static StringBuffer input_string = new StringBuffer();
       private static int label = GlobalBean.NOT_LABEL;
       private static ParseOptions opts;
  /**
   * Creates a new instance of Parser
   */
   public Parser() {
       String[] args = new String[1];

       args[0] = new String("parseit");
       InitializeVars(args);
   }

   public static void InitializeVars(String arg[]) {

       int i = 0;
       if (arg.length > 1 && (arg[0].charAt(0) != '-')) {
           /* the dictionary is the first argument if it doesn't begin with "-" */
           dictionary_file = arg[0];
           i++;
       }
       opts = new ParseOptions();
       GlobalBean.opts = opts;
       // opts = new ParseOptions();

       opts.parse_options_set_max_sentence_length(70);
       opts.parse_options_set_linkage_limit(1000);
       opts.parse_options_set_short_length(10);

       for (; i < arg.length; i++) {
           if (arg[i].charAt(0) == '-') {
               if (arg[i].equals("-pp")) {
                   if ((post_process_knowledge_file != null) || (i + 1 == arg.length))
                       print_usage(arg[0]);
                   post_process_knowledge_file = arg[i + 1];
                   i++;
               } else if (arg[i].equals("-c")) {
                   if ((constituent_knowledge_file != null) || (i + 1 == arg.length))
                       print_usage(arg[0]);
                   constituent_knowledge_file = arg[i + 1];
                   i++;
               } else if (arg[i].equals("-a")) {
                   if ((affix_file != null) || (i + 1 == arg.length))
                       print_usage(arg[0]);
                   affix_file = arg[i + 1];
                   i++;
               } else if (arg[i].equals("-ppoff")) {
                   pp_on = false;
               } else if (arg[i].equals("-coff")) {
                   cons_on = false;
               } else if (arg[i].equals("-aoff")) {
                   af_on = false;
               } else if (arg[i].equals("-batch")) {
                   if ((opts.input != System.in) || (i + 1 == arg.length))
                       print_usage(arg[0]);
                       try {
                          opts.input = new FileInputStream(arg[i + 1]);
                       } catch (IOException ex ) {
                           // TODO - Do something
                       }
                   i++;

               } else if (arg[i].equals("-out")) {
                   if ((opts.out != System.out) || (i + 1 == arg.length))
                       print_usage(arg[0]);
                       try {
                           opts.out = new PrintStream(new FileOutputStream(arg[i + 1]));
                       } catch (IOException ex ) {
                           // TODO - Do something
                       }
                   i++;
               } else if (arg[i].charAt(1) == '!') {
               } else {
                   print_usage(arg[0]);
               }
           } else {
               // TODO - print_usage(arg[0]);
           }
       }

       if (!pp_on && post_process_knowledge_file != null)
           print_usage(arg[0]);

       if (dictionary_file == null) {
           // dictionary_file = defaultDataDir + "/link/4.0.dict";
           dictionary_file = "4.0.dict";
           // TODO: logging
           System.err.println("No dictionary file specified.  Using " + dictionary_file + ".");
       }

       if (af_on && affix_file == null) {
           // affix_file = defaultDataDir + "/link/4.0.affix";
           affix_file = "4.0.affix";
           // TODO: logging
           System.err.println("No affix file specified.  Using " + affix_file + ".");
       }

       if (pp_on && post_process_knowledge_file == null) {
           // post_process_knowledge_file = defaultDataDir + "/link/4.0.knowledge";
           post_process_knowledge_file = "4.0.knowledge";
           // TODO: logging
           System.err.println("No post process knowledge file specified.  Using " + post_process_knowledge_file + ".");
       }

       if (cons_on && constituent_knowledge_file == null) {
           // constituent_knowledge_file = defaultDataDir + "/link/4.0.constituent-knowledge";
           constituent_knowledge_file = "4.0.constituent-knowledge";
           // TODO: logging
           System.err.println("No constituent knowledge file specified.  Using " +
                   constituent_knowledge_file + ".");
       }

       try { dict =
           new Dictionary(opts, dictionary_file, post_process_knowledge_file,
               constituent_knowledge_file, affix_file);
       } catch (IOException ex ) {
                           // TODO - Do something
       }

       /* process the command line like commands */
       for (i = 1; i < arg.length; i++) {
           if (!arg[i].equals("-pp") && !arg[i].equals("-c") && !arg[i].equals("-a")) {
               i++;
           } else if (
               arg[i].charAt(0) == '-'
                   && !arg[i].equals("-ppoff")
                   && !arg[i].equals("-coff")
                   && !arg[i].equals("-aoff")) {
               opts.issue_special_command(arg[i].substring(1), dict);
           }
       }

   }

   public static void doIt(String arg[]) throws IOException {

       InitializeVars(arg);




       /* This section is a simple example of the API for those trying to figure out how to
        * incorporate it into their own program. Un-comment it to see the results
        */
       {
           String testString = "Which camera is small?"; // a simple test sentence

           int     rWordIndex;
           int     lWordIndex;
           String  leftWord;
           String  rightWord;
           String  linkLabel;

           // Set up a quick test
           sent = new Sentence(testString, dict, opts);
           // First parse with cost 0 or 1 and no null links
           opts.parse_options_set_disjunct_cost(2);
           opts.parse_options_set_min_null_count(0);
           opts.parse_options_set_max_null_count(0);
           opts.parse_options_reset_resources();
           num_linkages = sent.sentence_parse(opts);
           if ( num_linkages == 0) {
               // O.K. we have a null link (i.e. word without a link)
               // so allow one and try again
               opts.parse_options_set_min_null_count(1);
               opts.parse_options_set_max_null_count(sent.sentence_length());
               num_linkages = sent.sentence_parse(opts);
           }

           //This is an example of the API uncomment it to see it work.
           // Normally you loop over linkages, here we only choose the first
           Linkage myLinkage = new Linkage(0, sent, opts);
           // Normally you loop through sublinkages
           myLinkage.linkage_get_num_sublinkages();
           // Only choose the first sublinkage
           myLinkage.linkage_set_current_sublinkage(0);
           int numLinks = myLinkage.linkage_get_num_links();

           for (int linkIndex = 0; linkIndex < numLinks; linkIndex++) {
               rWordIndex = myLinkage.linkage_get_link_rword(linkIndex);
               lWordIndex = myLinkage.linkage_get_link_lword(linkIndex);
               rightWord = myLinkage.word.get(rWordIndex);
               leftWord = myLinkage.word.get(lWordIndex);
               linkLabel = myLinkage.linkage_get_link_label(linkIndex);
               opts.out.println(leftWord + "---" + linkLabel + "---" + rightWord);

           }
            
       }



       /*
        * This is the standard command line parser reading from the standard input and
        * displaying on the standard output.
        */
       while (GlobalBean.fget_input_string(input_string, opts.input, opts.out, opts)) {
           if (input_string.length() == 0) {
               continue;
           }
           if (input_string.equals("quit\n") || input_string.equals("exit\n"))
               break;
           if (GlobalBean.special_command(input_string, dict))
               continue;
           if (opts.parse_options_get_echo_on()) {
               opts.out.println(input_string);
           }

           if (opts.parse_options_get_batch_mode()) {
               label = GlobalBean.strip_off_label(input_string);
           }

           /**
            * Create the sentence object with the inputs "sentence string, dictionary to use, options"
            */
           sent = new Sentence(input_string.toString(), dict, opts);
           if (sent.sentence_length() > opts.parse_options_get_max_sentence_length()) {
               if (opts.verbosity > 0) {
                   opts.out.println(
                           "Sentence length ("
                           + sent.sentence_length()
                           + " words) exceeds maximum allowable ("
                           + opts.parse_options_get_max_sentence_length()
                           + " words)");
               }
               continue;
           } /* First parse with cost 0 or 1 and no null links */
           opts.parse_options_set_disjunct_cost(2);
           opts.parse_options_set_min_null_count(0);
           opts.parse_options_set_max_null_count(0);
           opts.parse_options_reset_resources();
           num_linkages = sent.sentence_parse(opts);
           /* Now parse with null links */
           if (num_linkages == 0 && !opts.parse_options_get_batch_mode()) {
               if (opts.verbosity > 0)
                   opts.out.println("No complete linkages found.");
               if (opts.parse_options_get_allow_null()) {
                   opts.parse_options_set_min_null_count(1);
                   opts.parse_options_set_max_null_count(sent.sentence_length());
                   num_linkages = sent.sentence_parse(opts);
               }
           }

           opts.print_total_time();
           if (opts.parse_options_get_batch_mode()) {
               GlobalBean.batch_process_some_linkages(label, sent, opts);
           } else {
               GlobalBean.process_some_linkages(sent, opts);
           }

       }

       if (opts.parse_options_get_batch_mode()) {
           opts.print_time("Total");
           opts.out.println("" + GlobalBean.batch_errors + " error" + ((GlobalBean.batch_errors == 1) ? "" : "s") + ".");
       }
   }


   /**
    * Instead of printing a link diagram print an XML tree
    *
    * @param sent  the sentence to print.
    */
   public void printWordsLabelsAndLinks(Sentence sent) {
       CNode root, current, next, previous;
       Linkage linkage;
       int numLinkages;
       int num_to_query;
       int i;

       if (sent.sentence_num_linkages_found() > 0) {
           // We have to walk all the linakges throwing away the bad ones.
           num_to_query = Math.min(sent.sentence_num_linkages_post_processed(), 1000);


           for (i = 0; i < num_to_query; ++i) {

               if ((sent.sentence_num_violations(i) > 0) && (!opts.parse_options_get_display_bad())) {
                   continue;
               }

               // O.K. we have our fisrt valid linkage.  Do we want to print them all? No just one.
               // TODO - optimize this somehow
               linkage = new Linkage(i, sent, opts);
               // linkage = new Linkage(0, sent, opts);
               int j, first_sublinkage;

               // In effect we are saying display sublinkages
               linkage.linkage_compute_union();
               numLinkages = linkage.linkage_get_num_sublinkages();
               first_sublinkage = numLinkages - 1;


               for (j = first_sublinkage; j < numLinkages; ++j) {
                   linkage.linkage_set_current_sublinkage(j);
                   root = linkage.linkage_constituent_tree();
                   // Now we can walk the linkage and print the structure
                   current = root;
                   int w = 0;
                   do {
                       opts.out.println(linkage.word.get(w++).toString());
                       displayCNode(current);
                   } while (current.next != null);
                   // string = linkage_print_diagram();
                   // opts.out.println(string);
               }

           }
       }
   }
   private static void print_usage(String arg){
	   System.out.println(arg);
   }
   private static void displayCNode(CNode current){
	   System.out.println(current.label);
   }
}
