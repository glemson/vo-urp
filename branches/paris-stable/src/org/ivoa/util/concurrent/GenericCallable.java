package org.ivoa.util.concurrent;

import java.util.concurrent.Callable;

import org.ivoa.bean.LogSupport;

/**
 * This abstract class extends Callable to add the logging system
 * 
 * @param <V> the result type of method <tt>call</tt>
 * @see org.ivoa.bean.LogSupport
 *
 * @author Laurent Bourges (voparis) / Gerard Lemson (mpe)
 */
public abstract class GenericCallable<V> extends LogSupport implements Callable<V> {

  // ~ End of file
  // --------------------------------------------------------------------------------------------------------
}