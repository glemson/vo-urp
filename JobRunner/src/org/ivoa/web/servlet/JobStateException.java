package org.ivoa.web.servlet;
/**
 * An exception representing a problem while performing an action pertaining to jobs and tasks.
 * @author Gerard Lemson
 *
 */
public class JobStateException extends Exception {

	public JobStateException(String message)
	{
		super(message);
	}
	public JobStateException(String message, Throwable e)
	{
		super(message, e);
	}
	public JobStateException(Throwable e)
	{
		super(e);
	}
}
