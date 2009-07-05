package org.ivoa.util.stat;

import java.util.Date;

/**
 * Web service Statistics & monitoring
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class CommandStat extends BaseStat {
	/** method Name */
	private final String method;

	/** responses counter */
	private int monitorResponse;

	/** responses FAILED counter */
	private int monitorResponseFAIL;

	/** responses OK counter */
	private int monitorResponseOK;

	/** statistics for elapsed time for OK responses only */
	private final StatLong monitorTimeOK;

	/** statistics for elapsed time for all kind of FAIL responses */
	private final StatLong monitorTimeFAIL;

	/**
	 * Creates a new CommandStat object.
	 * @param method operation name
	 */
	public CommandStat(final String method) {
		super();
		this.method = method;
		this.monitorTimeOK = new StatLong();
		this.monitorTimeFAIL = new StatLong();
	}

	/**
	 * Reset the statistics
	 */
  @Override
	public final void reset() {
		/* fine grain lock */
		synchronized (this.lock) {
			this.monitorResponse = 0;
			this.monitorResponseFAIL = 0;
			this.monitorResponseOK = 0;

			if (this.monitorTimeOK != null) {
				this.monitorTimeOK.reset();
			}
			if (this.monitorTimeFAIL != null) {
				this.monitorTimeFAIL.reset();
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
		/* fine grain lock */
		synchronized (this.lock) {
			this.monitorCalls++;
		}
		return System.currentTimeMillis();
	}

	/**
	 * Monitor Response OK event
	 *
	 * @param start time given by @see #monitorEnter()
	 */
	public final void monitorResponseOk(final long start) {
		final long elapsed = System.currentTimeMillis() - start;
		/* fine grain lock */
		synchronized (this.lock) {
			this.monitorResponse++;
			this.monitorResponseOK++;
			if (start != 0L) {
				this.monitorTimeOK.add(elapsed);
			}
		}
	}

	/**
	 * Monitor Response FAIL event
	 *
	 * @param start time given by @see #monitorEnter()
	 */
	public final void monitorResponseFail(final long start) {
		final long elapsed = System.currentTimeMillis() - start;
		/* fine grain lock */
		synchronized (this.lock) {
			this.monitorResponse++;
			this.monitorResponseFAIL++;
			if (start != 0L) {
				this.monitorTimeFAIL.add(elapsed);
			}
		}
	}

	/**
	 * Tabular format dump (XL) for Header :
	 * @param sb string buffer for output
	 */
	public final static void dumpStatsHeader(final StringBuffer sb) {
		sb.append("Service").append("\t");
		sb.append("Date").append("\t");
		sb.append("Requests").append("\t");
		sb.append("Responses").append("\t");
		sb.append("Responses OK").append("\t");
		sb.append("Responses FAIL").append("\t");

		sb.append("Time OK (ms)").append("\t");
		sb.append("EcartType OK (ms)").append("\t");
		sb.append("Time FAIL (ms)").append("\t");
		sb.append("EcartType FAIL (ms)");
	}

	/**
	 * Tabular format dump (XL) for Stats :
	 * @param name name of this operation
	 * @param sb string buffer for output
	 */
  @Override
	public final void dumpStatsLine(final String name, final StringBuffer sb) {
		if (getMonitorCall() > 0) {
			sb.append(name).append(".");
			sb.append(method).append("\t");
			sb.append(format(new Date(getMonitorStartTime()))).append("\t");

			/* fine grain lock */
			synchronized (this.lock) {
				sb.append(getMonitorCall()).append("\t");
				sb.append(getMonitorResponse()).append("\t");
				sb.append(getMonitorResponseOK()).append("\t");
				sb.append(getMonitorResponseFAIL()).append("\t");

				sb.append(format(getMonitorTimeOKAverage())).append("\t");
				sb.append(format(getMonitorTimeOKSigma())).append("\t");
				sb.append(format(getMonitorTimeFAILAverage())).append("\t");
				sb.append(format(getMonitorTimeFAILSigma())).append("\n");
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
			sb.append("Statistics for : ");
			if (name != null) {
				sb.append(name).append(".");
			}
			sb.append(method);
			sb.append(endLine);
			sb.append("Since : ").append(format(new Date(getMonitorStartTime())));
			sb.append(endLine);

			/* fine grain lock */
			synchronized (this.lock) {
				sb.append("calls | responses       : ").append(getMonitorCall());
				sb.append(" | ").append(getMonitorResponse());
				sb.append(endLine);

				sb.append("OK    | FAIL responses  : ").append(getMonitorResponseOK());
				sb.append(" | ").append(getMonitorResponseFAIL());
				sb.append(endLine);

				sb.append("OK    | sigma time (ms) : ").append(format(getMonitorTimeOKAverage()));
				sb.append(" | ").append(format(getMonitorTimeOKSigma()));
				sb.append(endLine);

				sb.append("FAIL  | sigma time (ms) : ").append(format(getMonitorTimeFAILAverage()));
				sb.append(" | ").append(format(getMonitorTimeFAILSigma()));
				sb.append(endLine);
			}
		}

		if (reset) {
			reset();
		}
	}

	/**
	 * @return monitorResponse
	 */
	public int getMonitorResponse() {
		return this.monitorResponse;
	}

	/**
	 * @return monitorResponseFAIL
	 */
	public int getMonitorResponseFAIL() {
		return this.monitorResponseFAIL;
	}

	/**
	 * @return monitorResponseOK
	 */
	public int getMonitorResponseOK() {
		return this.monitorResponseOK;
	}

	/**
	 * @return monitorTimeOK.getAverage()
	 */
	public final double getMonitorTimeOKAverage() {
		return this.monitorTimeOK.getAverage();
	}

	/**
	 * @return monitorTimeOK.getSigma()
	 */
	public final double getMonitorTimeOKSigma() {
		return this.monitorTimeOK.getStdDev();
	}

	/**
	 * @return monitorTimeFAIL.getAverage()
	 */
	public final double getMonitorTimeFAILAverage() {
		return this.monitorTimeFAIL.getAverage();
	}

	/**
	 * @return monitorTimeFAIL.getSigma()
	 */
	public final double getMonitorTimeFAILSigma() {
		return this.monitorTimeFAIL.getStdDev();
	}
}
