![Universal G-Code Sender](https://github.com/winder/Universal-G-Code-Sender/raw/master/pictures/ugs_header.png "UGS Splash Image")


[![Last commit](https://img.shields.io/github/last-commit/winder/Universal-G-Code-Sender.svg?maxAge=1800)](https://github.com/winder/Universal-G-Code-Sender/commits/master)
[![Build Status](https://img.shields.io/github/actions/workflow/status/winder/Universal-G-code-sender/nightly.yaml?branch=master)](https://github.com/winder/Universal-G-Code-Sender/actions/workflows/nightly.yaml)
[![Codebeat badge](https://codebeat.co/badges/48cc1265-2f6b-4163-8a8a-964acc073100)](https://codebeat.co/projects/github-com-winder-universal-g-code-sender-master)
[![Crowdin](https://badges.crowdin.net/universalgcodesender/localized.svg)](https://translate.universalgcodesender.com/project/universalgcodesender)
[![Downloads](https://img.shields.io/github/downloads/winder/universal-g-code-sender/total?label=downloads)](https://github.com/winder/Universal-G-Code-Sender#downloads)
[![Releases](https://img.shields.io/github/v/release/winder/Universal-G-Code-Sender)](https://github.com/winder/Universal-G-Code-Sender/releases)
[![Discord](https://img.shields.io/discord/1257702590137897072?label=discord%20chat)](https://discord.com/invite/4DYywtyGYK)

Universal G-Code Sender is a Java based, cross platform G-Code sender, compatible with [GRBL](https://github.com/gnea/grbl/), [TinyG](https://github.com/synthetos/TinyG), [g2core](https://github.com/synthetos/g2) and [Smoothieware](http://smoothieware.org/).

Webpage: https://universalgcodesender.com/ <br/>
Discussion forum: https://github.com/winder/Universal-G-Code-Sender/discussions <br/>
Discord chat: https://discord.com/invite/4DYywtyGYK <br/>
Translations: https://translate.universalgcodesender.com/ <br/>

Technical details:

* [JSSC](https://github.com/scream3r/java-simple-serial-connector) or [JSerialComm](https://github.com/Fazecast/jSerialComm) for serial communication
* [JogAmp](https://jogamp.org/) for OpenGL
* [Netbeans Platform](https://netbeans.org/features/platform/)
* [JTS](https://github.com/locationtech/jts) for geometric transformations
* [Batik](https://xmlgraphics.apache.org/batik/) for reading SVG

## Downloads
Below you will find the latest release of UGS.<br/> For older releases please visit the [releases page](https://github.com/winder/Universal-G-Code-Sender/releases).

**UGS Platform**<br>
The next generation, feature packed variant based on the Netbeans Platform.<br>
Unpack and start the program ```bin/ugsplatform```


| Latest release (v2.1.15)                                                                                                                                                       | Nightly build                                                                                                                                                                         |
|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [![Windows 64-bit](pictures/os_windows.png) Windows 64-bit](https://github.com/winder/Universal-G-Code-Sender/releases/download/v2.1.15/win64-ugs-platform-app-2.1.15.zip)      | [![Windows 64-bit](pictures/os_windows.png) Windows 64-bit](https://github.com/winder/Universal-G-Code-Sender/releases/download/nightly/win64-ugs-platform-app-2.0-SNAPSHOT.zip)      |
| [![Mac OSX](pictures/os_mac.png) Mac OSX](https://github.com/winder/Universal-G-Code-Sender/releases/download/v2.1.15/macosx-x64-ugs-platform-app-2.1.15.dmg)                   | [![Mac OSX](pictures/os_mac.png) Mac OSX](https://github.com/winder/Universal-G-Code-Sender/releases/download/nightly/macosx-x64-ugs-platform-app-2.0-SNAPSHOT.dmg)                   |
| [![Mac OSX](pictures/os_mac.png) Mac OSX ARM64](https://github.com/winder/Universal-G-Code-Sender/releases/download/v2.1.15/macosx-aarch64-ugs-platform-app-2.1.15.dmg)         | [![Mac OSX ARM64](pictures/os_mac.png) Mac OSX ARM64](https://github.com/winder/Universal-G-Code-Sender/releases/download/nightly/macosx-aarch64-ugs-platform-app-2.0-SNAPSHOT.dmg)   | 
| [![Linux x64](pictures/os_linux.png) Linux 64-bit](https://github.com/winder/Universal-G-Code-Sender/releases/download/v2.1.15/linux-x64-ugs-platform-app-2.1.15.tar.gz)        | [![Linux x64](pictures/os_linux.png) Linux 64-bit](https://github.com/winder/Universal-G-Code-Sender/releases/download/nightly/linux-x64-ugs-platform-app-2.0-SNAPSHOT.tar.gz)        |
| [![Linux ARM](pictures/os_linux_arm.png) Linux ARM](https://github.com/winder/Universal-G-Code-Sender/releases/download/v2.1.15/linux-arm-ugs-platform-app-2.1.15.tar.gz)       | [![Linux ARM](pictures/os_linux_arm.png) Linux ARM](https://github.com/winder/Universal-G-Code-Sender/releases/download/nightly/linux-arm-ugs-platform-app-2.0-SNAPSHOT.tar.gz)       |
| [![Linux ARM](pictures/os_linux_arm.png) Linux ARM64](https://github.com/winder/Universal-G-Code-Sender/releases/download/v2.1.15/linux-aarch64-ugs-platform-app-2.1.15.tar.gz) | [![Linux ARM](pictures/os_linux_arm.png) Linux ARM64](https://github.com/winder/Universal-G-Code-Sender/releases/download/nightly/linux-aarch64-ugs-platform-app-2.0-SNAPSHOT.tar.gz) | 
| [![Zip](pictures/zip.png) All platforms](https://github.com/winder/Universal-G-Code-Sender/releases/download/v2.1.15/ugs-platform-app-2.1.15.zip)                               | [![Zip](pictures/zip.png) All platforms](https://github.com/winder/Universal-G-Code-Sender/releases/download/nightly/ugs-platform-app-2.0-SNAPSHOT.zip)                               |

**UGS Classic**<br>
A clean and lightweight variant of UGS (requires [Java 17](https://adoptium.net/temurin/releases/?package=jre&version=17)). <br>
Unpack and start the program by double clicking the jar file. On some platforms you may need to run the included start script. <br>

| Latest release (v2.1.15)                                                                                                                       | Nightly build                                                                                                                                  |
|:----------------------------------------------------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------|
| [![Zip](pictures/zip.png) All platforms](https://github.com/winder/Universal-G-Code-Sender/releases/download/v2.1.15/UniversalGcodeSender.zip) | [![Zip](pictures/zip.png) All platforms](https://github.com/winder/Universal-G-Code-Sender/releases/download/nightly/UniversalGcodeSender.zip) |

## Screenshots

### UGS Platform

UGS Platform main window

![UGS Platform](https://github.com/winder/Universal-G-Code-Sender/raw/master/pictures/2.0_platform_ugs_platform.png "UGS Platform")

Customizable panel layout

![Customizable panel layout](https://github.com/winder/Universal-G-Code-Sender/raw/master/pictures/2.0_platform_customizable_panels.png "Customizable panel layout")

Menu actions with customizable keybindings

![Actions](https://github.com/winder/Universal-G-Code-Sender/raw/master/pictures/2.0_platform_actions_menu.png "Actions")

Menu with plugins

![Plugins](https://github.com/winder/Universal-G-Code-Sender/raw/master/pictures/2.0_platform_plugins_menu.png "Plugins")

One of many plugins

![Dowel Maker](https://github.com/winder/Universal-G-Code-Sender/raw/master/pictures/2.0_platform_dowel_maker_plugin.png "Dowel maker plugin")

Basic gcode editor

![Basic gcode editor](https://github.com/winder/Universal-G-Code-Sender/raw/master/pictures/2.0_platform_editor.png "Basic gcode editor")

Vector graphics designer for generating GCode toolpaths

![Designer](https://github.com/winder/Universal-G-Code-Sender/raw/master/pictures/2.0_platform_designer.png "Basic gcode editor")


### UGS Classic

UGS Classic main window

![Classic main window](https://winder.github.io/ugs_website/img/screenshots/finished.png)

UGS Classic with visualizer

![Classic visualizer](https://winder.github.io/ugs_website/img/screenshots/visualizer.png)


## Development
<details><summary>Show details on how to compile the software</summary>
<p>

For development we use [Maven](http://maven.apache.org) and [Java 17](https://adoptium.net/) for compiling.

#### Compiling and starting the application

UGS Classic: 
```bash
mvn install
mvn exec:java -Dexec.mainClass="com.willwinder.universalgcodesender.MainWindow" -pl ugs-core
```

UGS Platform: 
```bash
mvn install
mvn nbm:run-platform -pl ugs-platform/application
```


#### Execute all tests

```bash
mvn test
```


#### Building the self-executing JAR

```bash
mvn install
mvn package -pl ugs-classic
```


#### Build a UniversalGcodeSender.zip release file

```bash
mvn package -pl ugs-classic assembly:assembly
```

#### Develop via IntelliJ

If you are more used to IntelliJ, you can also build, run and debug it there.

- Run  `mvn nbm:run-platform -pl ugs-platform/application` once via terminal to build everything
- Import the Source, `File` -> `New` -> `Project from existing Sources`
- Setup a new "Run Configuration", `Java Application`, with following settings:
  - Main Class: `org.netbeans.Main`
  - VM Options:
```
-Dpolyglot.engine.WarnInterpreterOnly=false
-Dnetbeans.user=$ProjectFileDir$/ugs-platform/application/target/userdir
-Dnetbeans.home=$ProjectFileDir$/ugs-platform/application/target/ugsplatform/platform
-Dnetbeans.logger.console=true
-Dnetbeans.indexing.noFileRefresh=true
-Dnetbeans.moduleitem.dontverifyclassloader=true
-Dnetbeans.dirs=$ProjectFileDir$/ugs-platform/application/target/ugsplatform/ugsplatform:$ProjectFileDir$/ugs-platform/application/target/ugsplatform/platform:$ProjectFileDir$/ugs-platform/application/target/ugsplatform/ide:$ProjectFileDir$/ugs-platform/application/target/ugsplatform/extra:$ProjectFileDir$/ugs-platform/application/target/ugsplatform/java
--add-opens=java.base/java.net=ALL-UNNAMED
--add-opens=java.base/java.lang.ref=ALL-UNNAMED
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.security=ALL-UNNAMED
--add-opens=java.base/java.util=ALL-UNNAMED
--add-opens=java.base/java.nio=ALL-UNNAMED
--add-exports=java.base/sun.reflect.annotation=ALL-UNNAMED
--add-opens=java.prefs/java.util.prefs=ALL-UNNAMED
--add-opens=java.desktop/javax.swing.plaf.basic=ALL-UNNAMED
--add-opens=java.desktop/javax.swing.text=ALL-UNNAMED
--add-opens=java.desktop/javax.swing=ALL-UNNAMED
--add-opens=java.desktop/java.awt=ALL-UNNAMED
--add-opens=java.desktop/java.awt.event=ALL-UNNAMED
--add-opens=java.desktop/sun.awt.X11=ALL-UNNAMED
--add-opens=java.desktop/javax.swing.plaf.synth=ALL-UNNAMED
--add-opens=java.desktop/com.sun.java.swing.plaf.gtk=ALL-UNNAMED
--add-opens=java.desktop/sun.awt.shell=ALL-UNNAMED
--add-opens=java.desktop/sun.awt.im=ALL-UNNAMED
--add-exports=java.desktop/sun.awt=ALL-UNNAMED
--add-exports=java.desktop/java.awt.peer=ALL-UNNAMED
--add-exports=java.desktop/com.sun.beans.editors=ALL-UNNAMED
--add-exports=java.desktop/sun.swing=ALL-UNNAMED
--add-exports=java.desktop/sun.awt.im=ALL-UNNAMED
--add-exports=java.desktop/com.sun.java.swing.plaf.motif=ALL-UNNAMED
``` 
  - Program arguments: `--branding ugsplatform`
  - Working dir: `$ProjectFileDir$`
  - Use classpath of module: `ugs-platform-app` 
- There is a [runConfiguration](.idea/runConfigurations/UGS_Platform.xml) in the repository, which should be available after importing the project

</p>
</details>

## Supported by
[![JetBrains logo.](https://resources.jetbrains.com/storage/products/company/brand/logos/jetbrains.svg)](https://jb.gg/OpenSourceSupport)
