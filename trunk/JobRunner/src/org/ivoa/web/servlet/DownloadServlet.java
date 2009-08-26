package org.ivoa.web.servlet;

import org.ivoa.runner.FileManager;

import org.ivoa.util.JavaUtils;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Download a Pdr model as a zip file
 *
 * @author laurent bourges (voparis)
 */
public class DownloadServlet extends BaseServlet {
    /**
     * serial UID for Serializable interface
     */
    private static final long serialVersionUID = 1L;

    /**
     * Experiment Id parameter
     */
    public static final String INPUT_EXPERIMENT_ID = "EXPERIMENT_ID";

    /**
     * Constructor
     */
    public DownloadServlet() {
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return servlet info
     */
    @Override
    public String getServletInfo() {
        return "Download a Model";
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if JSP not found
     */
    @Override
    protected void processRequest(final HttpServletRequest request,
        final HttpServletResponse response) throws ServletException {
        final String pathInfo = request.getPathInfo();

        if (log.isInfoEnabled()) {
            log.info("DownloadServlet : pathInfo = " + pathInfo);
        }

        final int pos = pathInfo.indexOf("/",1);

        final String user = (pos > 0) ? pathInfo.substring(1, pos) : null;

        if ((user != null) && user.equals(request.getRemoteUser())) {
            File file = new File(FileManager.ARCHIVE + pathInfo);

            if (log.isInfoEnabled()) {
                log.info("DownloadServlet : file = " + file.getAbsolutePath());
            }

            if (file.isFile()) {
                downloadFile(request, response, file);
            } else {
                downloadDirectory(request, response, file);
            }
        }
    }

    private void downloadFile(final HttpServletRequest request,
        final HttpServletResponse response, final File file) {
        // set headers :
        //      response.setContentType("application/zip");
        response.setHeader("Content-disposition",
            "attachment;filename=" + file.getName());

        DataOutputStream out = null;

        try {
            out = new DataOutputStream(response.getOutputStream());

            addFileToStream(file, out);
        } catch (final IOException ioe) {
            log.error("IO failure :", ioe);
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (final IOException ioe) {
                    log.error("IO close error : ", ioe);
                }
            }
        }
    }

    private void downloadDirectory(final HttpServletRequest request,
        final HttpServletResponse response, final File dir) {
        final List<File> files = FileManager.getFileListForZip(dir);

        if (!JavaUtils.isEmpty(files)) {
            final String name = dir.getName() + ".zip";

            // set headers :
            response.setContentType("application/zip");
            response.setHeader("Content-disposition",
                "attachment;filename=" + name);

            ZipOutputStream zipOutputStream = null;

            try {
                zipOutputStream = new ZipOutputStream(response.getOutputStream());

                for (Iterator<File> it = files.iterator(); it.hasNext();) {
                    addFileToZip(it.next(), zipOutputStream);
                }
            } catch (final IOException ioe) {
                log.error("IO failure :", ioe);
            } finally {
                if (zipOutputStream != null) {
                    try {
                        zipOutputStream.flush();
                        zipOutputStream.close();
                    } catch (final IOException ioe) {
                        log.error("IO close error : ", ioe);
                    }
                }
            }
        }
    }

    /**
     * Add the file to the zip outputstream
     *
     * @param file file to add
     * @param zipOutputStream
     */
    private void addFileToZip(final File file,
        final ZipOutputStream zipOutputStream) {
        if (file.exists()) {
            InputStream in = null;

            try {
                in = new BufferedInputStream(new FileInputStream(file));

                final ZipEntry ze = new ZipEntry(File.separator +
                        file.getName());
                ze.setTime(file.lastModified());

                zipOutputStream.putNextEntry(ze);

                final byte[] buf = new byte[8192];
                int len;

                while ((len = in.read(buf)) > 0) {
                    zipOutputStream.write(buf, 0, len);
                }
            } catch (final IOException ioe) {
                log.error("addFileToZip : IO failure : ", ioe);
            } finally {
                try {
                    zipOutputStream.closeEntry();

                    if (in != null) {
                        in.close();
                    }
                } catch (final IOException ioe) {
                    log.error("addFileToZip : IO close error : ", ioe);
                }
            }
        }
    }

    /**
     * Add the file to the outputstream
     *
     * @param file file to add
     * @param outputStream
     */
    private void addFileToStream(final File file,
        final OutputStream outputStream) {
        if (file.exists()) {
            InputStream in = null;

            try {
                in = new BufferedInputStream(new FileInputStream(file));

                final byte[] buf = new byte[8192];
                int len;

                while ((len = in.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }
            } catch (final IOException ioe) {
                log.error("addFileToStream : IO failure : ", ioe);
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (final IOException ioe) {
                    log.error("addFileToStream : IO close error : ", ioe);
                }
            }
        }
    }
}
