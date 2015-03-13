c:
cd \tmp

del /Q leak

jmap -dump:format=b,file=leak %1

jhat -J-Xmx512m leak  

cd \dev