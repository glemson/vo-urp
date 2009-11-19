package org.gavo.sam;

import java.util.Map;

import org.ivoa.util.runner.RootContext;
import org.ivoa.util.runner.RunContext;

public interface ILegacyApp {

	public void init(String configFile);
	public String getName();
	public String inputPage();
	public String detailsPage();
	public String prepareRun(Map<String, String> params);
	
    public void performJobEvent(final RootContext rootCtx);
	public boolean performTaskDone(final RootContext rootCtx, final RunContext runCtx);
	public boolean performTaskEvent(final RootContext rootCtx, final RunContext runCtx);

}
