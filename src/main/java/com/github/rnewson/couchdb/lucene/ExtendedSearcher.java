package com.github.rnewson.couchdb.lucene;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.Weight;
import org.apache.lucene.store.Directory;

/**
 * A managable extension to {@link Searcher}.
 * 
 * @author rnewson
 * 
 */
final class ExtendedSearcher extends Searcher implements ExtendedSearchable {

    private final Directory dir;

    private IndexSearcher searcher;

    private final ReadWriteLock rwl = new ReentrantReadWriteLock();

    private final Lock rl = rwl.readLock();

    private final Lock wl = rwl.writeLock();

    public ExtendedSearcher(final Directory dir) throws IOException {
        this.dir = dir;
        this.searcher = new IndexSearcher(dir);
    }

    @Override
    public void close() throws IOException {
        rl.lock();
        try {
            searcher.close();
        } finally {
            rl.unlock();
        }
    }

    @Override
    public Document doc(final int i) throws CorruptIndexException, IOException {
        rl.lock();
        try {
            return searcher.doc(i);
        } finally {
            rl.unlock();
        }
    }

    public Document doc(final int i, final FieldSelector fieldSelector) throws CorruptIndexException, IOException {
        rl.lock();
        try {
            return searcher.doc(i, fieldSelector);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public int docFreq(final Term term) throws IOException {
        rl.lock();
        try {
            return searcher.docFreq(term);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public int[] docFreqs(final Term[] terms) throws IOException {
        rl.lock();
        try {
            return searcher.docFreqs(terms);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public boolean equals(final Object obj) {
        rl.lock();
        try {
            return searcher.equals(obj);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public Explanation explain(final Query query, final int doc) throws IOException {
        rl.lock();
        try {
            return searcher.explain(query, doc);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public Explanation explain(final Weight weight, final int doc) throws IOException {
        rl.lock();
        try {
            return searcher.explain(weight, doc);
        } finally {
            rl.unlock();
        }
    }

    public IndexReader getIndexReader() {
        rl.lock();
        try {
            return searcher.getIndexReader();
        } finally {
            rl.unlock();
        }
    }

    @Override
    public Similarity getSimilarity() {
        rl.lock();
        try {
            return searcher.getSimilarity();
        } finally {
            rl.unlock();
        }
    }

    @Override
    public int hashCode() {
        rl.lock();
        try {
            return searcher.hashCode();
        } finally {
            rl.unlock();
        }
    }

    @Override
    public int maxDoc() throws IOException {
        rl.lock();
        try {
            return searcher.maxDoc();
        } finally {
            rl.unlock();
        }
    }

    @Override
    public Query rewrite(final Query original) throws IOException {
        rl.lock();
        try {
            return searcher.rewrite(original);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public Hits search(final Query query, final Filter filter) throws IOException {
        rl.lock();
        try {
            return searcher.search(query, filter);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public void search(final Query query, final Filter filter, final HitCollector results) throws IOException {
        rl.lock();
        try {
            searcher.search(query, filter, results);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public TopDocs search(final Query query, final Filter filter, final int n) throws IOException {
        rl.lock();
        try {
            return searcher.search(query, filter, n);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public TopFieldDocs search(final Query query, final Filter filter, final int n, final Sort sort) throws IOException {
        rl.lock();
        try {
            return searcher.search(query, filter, n, sort);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public Hits search(final Query query, final Filter filter, final Sort sort) throws IOException {
        rl.lock();
        try {
            return searcher.search(query, filter, sort);
        } finally {
            rl.unlock();
        }

    }

    @Override
    public void search(final Query query, final HitCollector results) throws IOException {
        rl.lock();
        try {
            searcher.search(query, results);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public TopDocs search(final Query query, final int n) throws IOException {
        rl.lock();
        try {
            return searcher.search(query, n);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public Hits search(final Query query, final Sort sort) throws IOException {
        rl.lock();
        try {
            return searcher.search(query, sort);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public void search(final Weight weight, final Filter filter, final HitCollector results) throws IOException {
        rl.lock();
        try {
            searcher.search(weight, filter, results);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public TopDocs search(final Weight weight, final Filter filter, final int docs) throws IOException {
        rl.lock();
        try {
            return searcher.search(weight, filter, docs);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public TopFieldDocs search(final Weight weight, final Filter filter, final int docs, final Sort sort)
            throws IOException {
        rl.lock();
        try {
            return searcher.search(weight, filter, docs, sort);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public void setSimilarity(final Similarity similarity) {
        rl.lock();
        try {
            searcher.setSimilarity(similarity);
        } finally {
            rl.unlock();
        }
    }

    @Override
    public String toString() {
        rl.lock();
        try {
            return searcher.toString();
        } finally {
            rl.unlock();
        }
    }

    public void refresh() throws IOException {
        wl.lock();
        try {
            final IndexReader currentReader = searcher.getIndexReader();
            final IndexReader newReader = searcher.getIndexReader().reopen();
            if (currentReader != newReader) {
                final Similarity similarity = searcher.getSimilarity();
                searcher = new IndexSearcher(newReader);
                searcher.setSimilarity(similarity);
                currentReader.close();
            }
        } finally {
            wl.unlock();
        }
    }

}
