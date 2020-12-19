rem Make sure to replace the jmods directory with the location of your javafx jmods.

JPACKAGE --verbose --name FXPad --app-version 0.1 --description "A clean, simple text editor." ^
--type msi --win-dir-chooser --win-per-user-install --win-shortcut ^
--module-path "out/artifacts/fxpad_jar/fxpad.jar;F:/Java-JDKs/javafx-jmods-15.0.1" ^
--module fxpad/fxpad.Main ^
--dest "executable" ^
--icon "metadata/icons/fxpad_icon.ico" ^
--license-file LICENSE ^
--file-associations "metadata/extensions/extension_txt.properties" ^
--file-associations "metadata/extensions/extension_text.properties" ^
