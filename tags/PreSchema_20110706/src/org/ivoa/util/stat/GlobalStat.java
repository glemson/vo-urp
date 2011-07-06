package org.ivoa.util.stat;

import java.util.Date;

/**
 * Global Web service Statistics & monitoring
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public final class GlobalStat extends BaseStat {
	// members :
	/** responses counter */
	private int monitorResponse;

	/** responses FAILED counter */
	private int monitorResponseFAIL;

	/** responses OK counter */
	private int monitorResponseOK;

	/** concurrent call counter */
	private int monitorConcurrentCall;

	/** Max concurrent call counter */
	private int monitorMaxConcurrentCall;

	/**
	 * Creates a new GlobalStat object.
	 */
	public GlobalStat() {
		super();
	}

	/**
	 * Reset fields with synchronisation
	 */
  @Override
	public final void reset() {
		/* fine grain lock */
		synchronized (this.lock) {
			this.monitorResponse = 0;
			this.monitorResponseFAIL = 0;
			this.monitorResponseOK = 0;

			// concurrent flags :
			this.monitorConcurrentCall = 0;
			this.monitorMaxConcurrentCall = 0;

			super.reset();
		}
	}

	/**
	 * Monitor Call event
	 */
	public final void monitorCall() {
		/* fine grain lock */
		synchronized (this.lock) {
			this.monitorCalls++;
			this.monitorConcurrentCall++;
			if (this.monitorConcurrentCall > this.monitorMaxConcurrentCall) {
				this.monitorMaxConcurrentCall = this.monitorConcurrentCall;
			}
		}
	}

	/**
	 * Monitor Response OK event
	 */
	public final void monitorResponseOk() {
		/* fine grain lock */
		synchronized (this.lock) {
			this.monitorResponse++;
			this.monitorResponseOK++;
			this.monitorConcurrentCall--;
		}
	}

	/**
	 * Monitor Response FAIL event
	 */
	public final void monitorResponseFail() {
		/* fine grain lock */
		synchronized (this.lock) {
			this.monitorResponse++;
			this.monitorResponseFAIL++;
			this.monitorConcurrentCall--;
		}
	}

	/**
	 * Global User friendly dump :
	 * @param name name of this operation
	 * @param sb string buffer for output
	 * @param reset flag to reset statistics after the dump
	 */
	public final void dumpStats(final String name, final StringBuffer sb, final boolean reset) {
		if (getMonitorCall() > 0) {
			sb.append("Global Statistics for ").append(name);
			sb.append(" : ").append("\nSince : ");
			sb.append(format(new Date(getMonitorStartTime())));
			sb.append("\n");

			/* fine grain lock */
			synchronized (this.lock) {
				sb.append("calls | responses        : ").append(getMonitorCall());
				sb.append(" | ").append(getMonitorResponse());
				sb.append("\n");

				sb.append("OK    | FAIL responses   : ").append(getMonitorResponseOK());
				sb.append(" | ").append(getMonitorResponseFAIL());
				sb.append("\n");

				sb.append("maximum concurrent calls : ").append(getMonitorMaxConcurrentCall());
				sb.append("\n");
			}

			if (reset) {
				reset();
			}
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
	 * @return monitorConcurrentCall
	 */
	public int getMonitorConcurrentCall() {
		return this.monitorConcurrentCall;
	}

	/**
	 * @return monitorMaxConcurrentCall
	 */
	public int getMonitorMaxConcurrentCall() {
		return this.monitorMaxConcurrentCall;
	}
}
