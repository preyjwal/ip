# Steph project template

This is a project template for a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Steph.java` file, right-click it, and choose `Run Steph.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    ____  _             _     
   / ___|| |_ ___ _ __ | |__  
   \___ \| __/ _ \ '_ \| '_ \ 
    ___) | ||  __/ |_) | | | |
   |____/ \__\___| .__/|_| |_|
                 |_|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running Steph

* **GUI (JavaFX):** run `./gradlew run`. This launches `steph.Launcher`, which
  opens the chat window built from `src/main/resources/view/*.fxml` and styled by
  `src/main/resources/css/*.css`. JavaFX is supplied by the `25.0.3.fx-zulu`
  JDK, so no extra dependency is needed.
* **Console:** run `steph.Steph.main()` directly from the IDE (or
  `java -cp build/classes/java/main steph.Steph`). Both front ends share the
  same `./data/steph.txt` save file.
