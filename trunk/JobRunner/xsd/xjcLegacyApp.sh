# This shell script uses JAXB 2.1 XJC compiler to produce Java code for the intermediate model xml schema

# The goal is to have the intermediate model document available as Java Beans.

#DIR=`pwd`
DIR='e:/workspaces/eclipse_runner/JobRunner'
DIR_GEN=$DIR/src/

# Add your JAXB library path here :
#export PATH=/java/jaxb-ri-20070917/bin:$PATH
export PATH=C:/software/java/jaxb/jaxb-ri-20071219/bin:$PATH




# Creates and displays output directory

#rm -rf $DIR_GEN
#mkdir $DIR_GEN

echo
echo Generation output : $DIR_GEN
echo




# Checks jaxb 2.1 & options

C:/software/java/jaxb/jaxb-ri-20071219/bin/xjc.sh -version 2> xjc-version.txt
C:/software/java/jaxb/jaxb-ri-20071219/bin/xjc.sh -help > xjc-help.txt

JAXB=`cat xjc-version.txt`

echo
echo JAXB Version : 
echo   $JAXB
echo




# Starts jaxb 2.1
xjc.sh -d $DIR_GEN -p org.vourp.runner.model -no-header -readOnly -verbose ./LegacyApp.xsd

if [ "$?" -ne "0" ]; then
  echo "Sorry, cannot Generate code with xjc (jaxb 2.1 needed). Fix previous errors and try again ..."
  exit 1
fi




# End :
echo
echo Generation done in $DIR_GEN
echo

