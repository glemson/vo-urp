
echo removing old PDR site
rm -rf /srv/www/htdocs/PDR/*

echo copying subversion content
cp -r ./PDR/* /srv/www/htdocs/PDR/

echo done.
