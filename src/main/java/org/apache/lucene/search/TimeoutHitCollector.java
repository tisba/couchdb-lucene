package org.apache.lucene.search;

public final class TimeoutHitCollector extends HitCollector {

	private final HitCollector delegate;

	private final long timeout;

	public TimeoutHitCollector(final HitCollector delegate, final long timeoutMillis) {
		this.delegate = delegate;
		this.timeout = System.currentTimeMillis() + timeoutMillis;
	}
	
	@Override
	public void collect(final int doc, final float score) {
		if (System.currentTimeMillis() > timeout)
			throw new RuntimeException("timeout.");
		delegate.collect(doc, score);
	}

}
