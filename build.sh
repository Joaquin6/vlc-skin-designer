#! /bin/sh

mkdir build
cd src/
`javac -d ../build vlcskineditor/Main.java`
cd ..
cp src/vlcskineditor/FreeSans.ttf build/vlcskineditor/
cp src/vlcskineditor/icons/* build/vlcskineditor/icons
echo "Main-Class: vlcskineditor.Main" > manifest_no_nb
jar cvfm VLCSkinEditor.jar manifest_no_nb -C build .
rm -f manifest_no_nb
