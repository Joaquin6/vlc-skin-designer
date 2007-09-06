md build
cd src
javac -d ../build vlcskineditor/Main.java
cd ..
copy src\vlcskineditor\FreeSans.ttf build\vlcskineditor\FreeSans.ttf
copy src\vlcskineditor\icons build\vlcskineditor\icons
echo Main-Class: vlcskineditor.Main>manifest_no_nb
jar cvfm VLCSkinEditor.jar manifest_no_nb -C build .
del manifest_no_nb
