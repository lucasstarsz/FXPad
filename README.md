<div align="center">
    <img src="https://raw.githubusercontent.com/lucasstarsz/FXPad/main/metadata/icons/fxpad_png.png" width="30%" height="30%" alt="FXPad image"/>
    <h1>FXPad</h1>
    <p>
        A clean, simple text editor.
    </p>
</div>

FXPad offers smart tabs, a refreshing color palate, and a simple interface to work with.

## How To Run

#### Executables

- **Linux** & **Windows** installers are found [here.](https://github.com/lucasstarsz/FXPad/releases)
- **Mac** users will need to build the application for themselves (I recommend using one of the batch scripts as a base), or otherwise run the app through the project (mentioned below).

#### Project

1. Download the repository, either by

   - [Pressing the green "Code" button](https://i.imgur.com/pw92PNf.png), and downloading to your workspace (.zip, or downloading to GitHub Desktop), or

   - Opening a terminal, and running

     ```bash
     git clone https://github.com/lucasstarsz/FXPad.git
     ```

     in the desired directory.

2. Open this project with [IntelliJ (2020.3 or later preferred)](https://www.jetbrains.com/idea/download/)

3. Run -> Run... -> Edit Configurations...

4. Select Application -> Main

5. Set the VM options to `--module-path "path/to/javafx-sdk-15/lib":out/production`

6. Apply, then Run.



#### [Feature Requests Welcome]()