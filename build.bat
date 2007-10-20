rd /S /Q build
md build
cd src
javac -d ../build vlcskineditor/Main.java
cd ..
md build\vlcskineditor\icons
copy src\vlcskineditor\icons build\vlcskineditor\icons\
echo Main-Class: vlcskineditor.Main>manifest_no_nb
jar cvfm VLCSkinEditor.jar manifest_no_nb -C build .
del manifest_no_nb
