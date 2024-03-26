# RescueBulletin

## Installation
This program requires Java 11. Download the latest JDK or a JRE that supports Java 11. Run the following command in the command prompt to launch, changing parts in parentheses to match your installation.

java --module-path "C:\Program Files\Java\javafx-sdk-(version)\lib" --add-modules javafx.controls,javafx.fxml -jar C:\(path)\RescueBulletin.jar

## Features
* Ability to edit and randomize PMD: Red Rescue Team (US/Aus)

## Goals
These are in order of importance
* Achieve data shiftability by completing and integrating SbinFile, SiroFile, SiroSegment, SiroFactory, and ConfigHandler
* Ensure that data currently viewable is accurate and savable. (This is best done second in case the first step breaks something.)
* Import and convert code from precursor script viewing tool. For now, it will be diaplayed as raw data annotated with current understanding to assist in verification and further reverse engineering.

## Contributing
Pull requests are welcome.
