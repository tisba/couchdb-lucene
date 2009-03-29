package org.apache.lucene.search;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;

public final class TimeoutIndexSearcher extends IndexSearcher {

	private final long timeoutMillis;

	public TimeoutIndexSearcher(final Directory directory, final long timeoutMillis) throws CorruptIndexException,
			IOException {
		super(directory);
		this.timeoutMillis = timeoutMillis;
	}

	public TimeoutIndexSearcher(final IndexReader r, final long timeoutMillis) {
		super(r);
		this.timeoutMillis = timeoutMillis;
	}

	public TimeoutIndexSearcher(final String path, final long timeoutMillis) throws CorruptIndexException, IOException {
		super(path);
		this.timeoutMillis = timeoutMillis;
	}

	@Override
	public TopDocs search(Weight weight, Filter filter, final int nDocs) throws IOException {
		if (nDocs <= 0) // null might be returned from hq.top() below.
			throw new IllegalArgumentException("nDocs must be > 0");

		TopDocCollector collector = new TopDocCollector(nDocs);
		search(weight, filter, new TimeoutHitCollector(collector, timeoutMillis));
		return collector.topDocs();
	}

	@Override
	public TopFieldDocs search(Weight weight, Filter filter, final int nDocs, Sort sort) throws IOException {
		TopFieldDocCollector collector = new TopFieldDocCollector(reader, sort, nDocs);
		search(weight, filter, new TimeoutHitCollector(collector, timeoutMillis));
		return (TopFieldDocs) collector.topDocs();
	}

}
