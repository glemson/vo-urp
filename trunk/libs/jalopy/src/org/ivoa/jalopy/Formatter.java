package org.ivoa.jalopy;

import java.io.File;
import java.io.FileFilter;

import de.hunsicker.jalopy.Jalopy;
import de.hunsicker.jalopy.storage.History;
import de.hunsicker.jalopy.storage.Loggers;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;

/**
 * Custom formatter to avoid enum bugs
 */
public final class Formatter {

    public final static String JAXB_PACKAGE_INFO_JAVA = "package-info.java";
    public final static String EXT_JAVA = ".java";

    // constants :
    /** java file filter */
    private final static FileFilter filter = new FileFilter() {

        public boolean accept(final File file) {
            boolean res = false;
            final String name = file.getName();
            if (!JAXB_PACKAGE_INFO_JAVA.equals(name) && name.endsWith(EXT_JAVA)) {
                res = true;
            }
            return res;
        }
    };

    // members :
    /** Jalopy interface */
    private Jalopy jalopy;

    /**
     * Constructor with the given convetion
     * @param convention jalopy convention file
     */
    public Formatter(final String convention) throws Exception {
        super();
        jalopy = new Jalopy();

        jalopy.setEncoding("UTF-8");

        // Specifies whether all files should be formatted no matter what the state of a file is :
        jalopy.setForce(true);

        // Sets the number of backups to hold. A value of <code>0</code> means to hold no  backup at all :
        jalopy.setBackupLevel(0);
        jalopy.setHistoryPolicy(History.Policy.DISABLED);

        // Enables or disables the code inspector during formatting runs :
        jalopy.setInspect(true);

        // force logs :
/*
        Loggers.initialize(
        new ConsoleAppender(
        new PatternLayout("%d %p [%t] - %m%n"), "System.out"));
         */
        if (convention != null) {
            Jalopy.setConvention(new File(convention));
        }
    }

    private final void format(final File src) throws Exception {
        if (src.isFile() && filter.accept(src)) {
            jalopy.setInput(src);
            jalopy.setOutput(src);

            jalopy.format();

            if (jalopy.getState() == Jalopy.State.OK) {
                System.out.println("Formatted " + src.getAbsolutePath());
            } else if (jalopy.getState() == Jalopy.State.WARN) {
                System.out.println("Formatted " + src.getAbsolutePath() + " with warnings");
            } else if (jalopy.getState() == Jalopy.State.ERROR) {
                System.out.println("Failed formatting " + src.getAbsolutePath());
            }
        } else if (src.isDirectory()) {
            final File[] files = src.listFiles();
            for (File file : files) {
                format(file);
            }
        }

    }

    /**
     * Main : Formatter <dir> [<convention-path>]
     * 
     * @param args main arguments
     */
    public final static void main(final String[] args) {
        try {
            String convention = null;
            if (args.length < 1) {
                System.out.println("usage: Formatter <dir> [<convention-path>]");
                return;
            }
            final File src = new File(args[0]);
            if (args.length > 1) {
                convention = args[1];
                System.out.println("note: convention-path = " + convention);
            }

            final Formatter formatter = new Formatter(convention);

            System.out.println("start formatting sources located at : " + src);

            formatter.format(src);

            System.out.println("format done.");

        } catch (Throwable t) {
            System.err.println("Formatter failure : ");
            t.printStackTrace(System.err);
        }
    }
}
