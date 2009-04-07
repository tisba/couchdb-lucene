package com.github.rnewson.couchdb.lucene;

import java.io.IOException;

import org.apache.lucene.search.Searchable;

/**
 * Add methods to allow complete, remote management.
 * 
 * @author rnewson
 *
 */
interface ExtendedSearchable extends Searchable {

    void refresh() throws IOException;
    
}
