Laurent Bourges - May 12, 2008

---------------------------------------
   SaxonLiaison Ant Hack
---------------------------------------


This folder contains the SaxonLiaison class (source & compiled code).


This small class extends ant TraXLiaison class to use Saxon XSLT2 processor.


Moreover, it computes the lastModificaton date with the following algorithm :
  - xml source document : xml-date
      this date is deducted from the file content (<lastModifiedDate> node or lastModifiedDate attribute) if present; file date otherwise.

  - xsl document : xsl-date

  => lastModfied = max (xml-date, xsl-date)


Then the XSLT is processed and SaxonLiaison published the following parameters :
  - lastModified = UTC format : number of milliseconds since 1/1/1970

  - lastModifiedDate = string format [yyyyMMddHHmmss]
  - lastModifiedText = string format [yyyy-MM-dd HH:mm:ss]




--- End of file ---
