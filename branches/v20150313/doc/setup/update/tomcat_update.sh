export PATH=$PATH:/opt/ant/apache-ant-1.7.0/bin

cd PDRDB_Query/
ant makeAll 
cd ..

echo copying tomcat web app 
cp -r ./PDRDB_Query/dist/*.war /usr/share/tomcat5/webapps/ 

echo done.

tail -f /usr/share/tomcat5/logs/catalina.out
