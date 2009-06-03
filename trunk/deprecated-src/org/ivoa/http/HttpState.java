package org.ivoa.http;

/**
 * TODO : Class Description
 *
 * @author Laurent Bourges
 */
public final class HttpState {
  //~ Constants --------------------------------------------------------------------------------------------------------

  /** TODO : Field Description */
  public static final int OP_POST = 1;
  /** TODO : Field Description */
  public static final int OP_HEADER = 2;
  /** TODO : Field Description */
  public static final int OP_GET = 3;
  /** TODO : Field Description */
  public static final int OP_GET_BODY = 4;
  /** TODO : Field Description */
  public static final String[] OPS = new String[] { "UNKNOWN", "POST", "HEAD", "GET", "GET BODY" };

  //~ Members ----------------------------------------------------------------------------------------------------------

  /** TODO : Field Description */
  private final int id;
  /** TODO : Field Description */
  private final int op;
  /** TODO : Field Description */
  private final String msgIn;
  /** TODO : Field Description */
  private final long start;
  /** TODO : Field Description */
  private final long startNanos;
  /** TODO : Field Description */
  private int clen;
  /** TODO : Field Description */
  private String msgOut;
  /** elapsed */
  private long time;
  /** TODO : Field Description */
  private float ratio;

  //~ Constructors -----------------------------------------------------------------------------------------------------

/**
   * Creates a new HttpState object
   *
   * @param id
   * @param o
   * @param msg
   */
  public HttpState(final int id, final int o, final String msg) {
    this.id = id;
    this.op = o;
    this.msgIn = msg;
    this.start = System.currentTimeMillis();
    this.startNanos = System.nanoTime();
  }

  //~ Methods ----------------------------------------------------------------------------------------------------------

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getMsgIn() {
    return msgIn;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public String getMsgOut() {
    return msgOut;
  }

  /**
   * TODO : Method Description
   *
   * @param msgOut
   */
  public void setMsgOut(final String msgOut) {
    this.msgOut = msgOut;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public int getOp() {
    return op;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public final String getOperation() {
    return OPS[op];
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public int getId() {
    return id;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public long getTime() {
    return time;
  }

  /**
   * TODO : Method Description
   *
   * @param t
   */
  public void setTime(final long t) {
    time = t;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public long getStart() {
    return start;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public float getRatio() {
    return ratio;
  }

  /**
   * TODO : Method Description
   *
   * @param ratio
   */
  public void setRatio(final float ratio) {
    this.ratio = ratio;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public long getStartNanos() {
    return startNanos;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public int getClen() {
    return clen;
  }

  /**
   * TODO : Method Description
   *
   * @param clen
   */
  public void setClen(final int clen) {
    this.clen = clen;
  }

  /**
   * TODO : Method Description
   *
   * @return value TODO : Value Description
   */
  public float getSpeed() {
    return ((float) getClen()) / getTime();
  }
}
//~ End of file --------------------------------------------------------------------------------------------------------
