# This shell script uses JAXB 2.1 XJC compiler to produce Java code for the intermediate model xml schema

# The goal is to have the intermediate model document available as Java Beans.

DIR=C:/workspaces/eclipse3.2.2/SimDB
DIR_GEN=$DIR/src/

# Add your JAXB library path here :
export PATH=/java/jaxb-ri-20070917/bin:$PATH
export PATH=C:/software/Java/JAXB/jaxb-ri/bin:$PATH


# Creates and displays output directory

#rm -rf $DIR_GEN
#mkdir $DIR_GEN

echo
echo Generation output : $DIR_GEN
echo




# Checks jaxb 2.1 & options

C:/software/Java/JAXB/jaxb-ri/bin/xjc.sh -version 2> xjc-version.txt
C:/software/Java/JAXB/jaxb-ri/bin/xjc.sh -help > xjc-help.txt

JAXB=`cat xjc-version.txt`

echo
echo JAXB Version : 
echo   $JAXB
echo




# Starts jaxb 2.1
C:/software/Java/JAXB/jaxb-ri/bin/xjc.sh -d $DIR_GEN -p org.ivoa.metamodel -no-header -readOnly -verbose ./input/intermediateModel.xsd
xjc.sh -d $DIR_GEN -p org.ivoa.metamodel -no-header -readOnly -verbose ./input/intermediateModel.xsd
### attempts to get VOTable into JAXB fails on latest 1.2-beta version due to multiple INFO elements at same level under a common container.
### UNLESS one follows suggestion in http://kingsfleet.blogspot.com/2008/07/working-round-xsdchoice-binding-issue.html
xjc.sh -d $DIR_GEN -b votable_xjcConfig.xml -p org.ivoa.votable -no-header -readOnly -verbose -extension http://www.ivoa.net/internal/IVOA/IvoaVOTable/VOTable-beta.xsd 
### next two are preliminary (AstroGrid) versions of VODataService for use in TAP. Not used yet. 
### Should use "official" version
#xjc.sh -d $DIR_GEN -p org.ivoa.tap -no-header -readOnly -verbose http://software.astrogrid.org/schema/vo-resource-types/VODataService/v1.0/VODataService.xsd
#xjc.sh -d $DIR_GEN -p org.ivoa.tap -no-header -readOnly -verbose http://wfaudata.roe.ac.uk/ukidssWorld-dsa/schema/Tables.xsd


if [ "$?" -ne "0" ]; then
  echo "Sorry, cannot Generate code with xjc (jaxb 2.1 needed). Fix previous errors and try again ..."
  exit 1
fi




# End :
echo
echo Generation done in $DIR_GEN
echo

