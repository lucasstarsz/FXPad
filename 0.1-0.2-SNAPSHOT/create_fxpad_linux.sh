# Make sure to replace the jmods directory with the location of your javafx jmods.
# You may also need to add executable permissions to this file. (chmod +x create_fxpad_linux)
#
"/opt/jdks/jdk-15/bin/jpackage.exe" --verbose --name FXPad --app-version 0.1.1 \
	--type deb --linux-shortcut \
	--module-path "out/artifacts/fxpad_jar/fxpad.jar:../javafx-jmods-15.0.1" \
	--module fxpad/fxpad.Main \
	--dest "executable" \
	--icon "metadata/icons/fxpad_icon.ico" \
	--license-file LICENSE \
	--file-associations "metadata/extensions/extension_txt.properties" \
	--file-associations "metadata/extensions/extension_text.properties" \
