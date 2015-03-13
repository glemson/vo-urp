REM wget --help > wget.txt

wget -v -S --spider --waitretry=3 --timeout=60 --no-cache --no-http-keep-alive --user=admin --password=llooo --input-file=inputURL-deploy.txt

REM  -O tmp\output-deploy.html

REM wget -v -S --no-cache --no-http-keep-alive --user=admin --password=llooo --input-file=inputURL-use.txt -O tmp\output-use.html

pause