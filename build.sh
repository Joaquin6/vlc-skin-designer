#! /bin/sh

mkdir -p build/vlcskineditor/icons
cd src/
`javac -O -d ../build vlcskineditor/Main.java`
cd ..
cp src/vlcskineditor/icons/* build/vlcskineditor/icons
echo "Main-Class: vlcskineditor.Main" > manifest_no_nb
jar cvfm VLCSkinEditor.jar manifest_no_nb -C build .
rm -f manifest_no_nb

