# FXPad

A clean, simple text editor.

FXPad offers smart tabs, a refreshing color palate, and a simple interface to work with.

## How To Run

#### Executables

- Installer options for Linux & Windows are found [here.](https://github.com/lucasstarsz/FXPad/releases)
- Mac users will need to build the application for themselves as of now, or otherwise run the app through the project (mentioned below).

#### Project

- Download the repository, either by
  - Pressing the green `Code` button, or 
  - Opening a terminal, and running `git clone https://github.com/lucasstarsz/FXPad.git` in the desired directory.
- Open this project with [IntelliJ (2020.3 or later preferred)](https://www.jetbrains.com/idea/download/)

- Run -> Run... -> Edit Configurations...
- Select Application -> Main
- Set the VM options to `--module-path "path/to/javafx-sdk-15/lib":out/production`
- Apply, then Run.
