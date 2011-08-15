package net.sf.jlinkgrammar;

/**
 * TODO add javadoc
 *
 */
public class Link {
    public int l, r;
    public Connector lc, rc;
    public String name; /* spelling of full link name */

    void replace_link_name(String s) {
        name = s;
    }

}
