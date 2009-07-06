package org.ivoa.util.stat;

import java.util.Date;

/**
 * Cache Statistics & monitoring
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class CacheStat extends BaseStat {
	/** cache key */
	private final String key;

	/** cache counter */
	private int monitorCache;

	/** miss counter */
	private int monitorMiss;

	/** statistics for elapsed time for miss only */
	private final StatLong monitorTime;

	/**
	 * Creates a new CommandStat object.
	 * @param pKey cache key
	 */
	public CacheStat(final String pKey) {
		super();
		this.key = pKey;
		this.monitorTime = new StatLong();
	}

	/**
	 * Reset the statistics
	 */
  @Override
	public final void reset() {
		/* fine grain lock */
		synchronized (this.lock) {
			this.monitorCache = 0;
			this.monitorMiss = 0;

			if (this.monitorTime != null) {
				this.monitorTime.reset();
			}

			super.reset();
		}
	}

	/**
	 * Monitor Enter event
	 *
	 * @return current time
	 */
	public final long monitorEnter() {
		return System.currentTimeMillis();
	}

	/**
	 * Monitor Cache event
	 */
	public final void monitorCache() {
		/* fine grain lock */
		synchronized (this.lock) {
			this.monitorCalls++;
			this.monitorCache++;
		}
	}

	/**
	 * Monitor Miss event
	 *
	 * @param start time given by @see #monitorEnter()
	 */
	public final void monitorMiss(final long start) {
		final long elapsed;
		if (start != 0L) {
			elapsed = System.currentTimeMillis() - start;
		} else {
			elapsed = 0L;
		}

		/* fine grain lock */
		synchronized (this.lock) {
			this.monitorCalls++;
			this.monitorMiss++;
			if (start != 0L) {
				this.monitorTime.add(elapsed);
			}
		}
	}

	/**
	 * User friendly dump :
	 *
	 * @param name name of this operation
	 * @param sb string buffer for output
	 * @param reset flag to reset statistics after the dump
	 * @param endLine end line char
	 */
  @Override
	public final void dumpStats(final String name, final StringBuffer sb, final boolean reset, final String endLine) {
		if (getMonitorCall() > 0) {
			sb.append("Statistics for : ").append(key);
			sb.append(endLine);
			sb.append("Since : ").append(format(new Date(getMonitorStartTime())));
			sb.append(endLine);

			/* fine grain lock */
			synchronized (this.lock) {
				sb.append("calls | Cache Hit ratio : ").append(getMonitorCall());
				sb.append(" | ").append(getMonitorCacheHitRatio());
				sb.append(" %").append(endLine);

				sb.append("Cache | Miss responses  : ").append(getMonitorCache());
				sb.append(" | ").append(getMonitorMiss());
				sb.append(endLine);

				if (this.monitorTime.getAccumulator() > 0d) {
					sb.append("Miss  | sigma time (ms) : ").append(format(getMonitorTimeAverage()));
					sb.append(" | ").append(format(getMonitorTimeSigma()));
					sb.append(endLine);
				}
			}
		}

		if (reset) {
			reset();
		}
	}

	/**
	 * @return monitorCache
	 */
	public final int getMonitorCache() {
		return this.monitorCache;
	}

	/**
	 * @return monitorMiss
	 */
	public final int getMonitorMiss() {
		return this.monitorMiss;
	}

	/**
	 * @return monitorTime.getAverage()
	 */
	public final double getMonitorTimeAverage() {
		return this.monitorTime.getAverage();
	}

	/**
	 * @return monitorTime.getSigma()
	 */
	public final double getMonitorTimeSigma() {
		return this.monitorTime.getStdDev();
	}

	/**
	 * Computes Cache hit ratio
	 * @return cache hit
	 */
	public final double getMonitorCacheHitRatio() {
		final int calls = getMonitorCall();
		int val = 0;
		if (calls > 0) {
			val = (1000 * this.monitorCache) / calls;
		}
		return val / 10.D;
	}
}
