package org.ivoa.util.stat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Base class for Statistics & monitoring
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public class BaseStat {
	/** number formatter */
	public final static DecimalFormat NB_FORMAT = new DecimalFormat("0.00");

	/** date formatter */
	public final static SimpleDateFormat STAT_FORMAT = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");

	// members :
	/** fine grain lock for synchroniz'd block per instance */
	protected final Object lock;

	/** start time in ms */
	private long monitorStartTime;

	/** calls counter */
	protected int monitorCalls;

	/**
	 * Creates a new GlobalStat object.
	 */
	public BaseStat() {
		this.lock = new Object();
		reset();
	}

	/**
	 * Reset fields without any synchronisation : must be done in child implementations
	 */
	public void reset() {
		this.monitorCalls = 0;
		this.monitorStartTime = System.currentTimeMillis();
	}

	/**
	 * @return monitorCall
	 */
	public final int getMonitorCall() {
		return this.monitorCalls;
	}

	/**
	 * @return monitorStartTime
	 */
	public final long getMonitorStartTime() {
		return this.monitorStartTime;
	}

	// Formatters :

	/**
	 * Format a number
	 * @param val number to format
	 * @return formatted number
	 */
	public final static String format(final double val) {
		/* fine grain lock */
		synchronized (NB_FORMAT) {
			return NB_FORMAT.format(val);
		}
	}

	/**
	 * Format a date
	 * @param val date to format
	 * @return formatted date
	 */
	public final static String format(final Date val) {
		/* fine grain lock */
		synchronized (STAT_FORMAT) {
			return STAT_FORMAT.format(val);
		}
	}

	/**
	 * Tabular format dump (XL) for Stats :
	 * @param name name of this operation
	 * @param sb string buffer for output
	 */
	public void dumpStatsLine(final String name, final StringBuffer sb) {
	// Empty implementation : must be overriden
	}

	/**
	 * User friendly dump :
	 *
	 * @param name name of this operation
	 * @param sb string buffer for output
	 * @param reset flag to reset statistics after the dump
	 * @param endLine end line char
	 */
	public void dumpStats(final String name, final StringBuffer sb, final boolean reset, final String endLine) {
	// Empty block : must be overriden
	}
}
