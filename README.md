# RescueBulletin

## Installation
This program requires Java 11. You can check version by typing "java --version" into the command prompt. Make sure your java installation is listed in your system's PATH variable.<br />
https://www.java.com/en/download/ <br />
https://www.java.com/en/download/help/path.html
## Features
* Ability to edit and randomize PMD: Red Rescue Team (US/Aus) <br />
Note: Some randomizer settings require an extra patch. <br />
https://www.pokecommunity.com/threads/patch-for-crash-when-using-a-modified-player-partner-in-pmd-rrt.493301/

## Goals
These are in order of importance
* Achieve data shiftability by completing and integrating SbinFile, SiroFile, SiroSegment, SiroFactory, and ConfigHandler
* Ensure that data currently viewable is accurate and savable. (This is best done second in case the first step breaks something.)
* Import and convert code from precursor script viewing tool. For now, it will be diaplayed as raw data annotated with current understanding to assist in verification and further reverse engineering.

## Contributing
Pull requests are welcome. The currently most desired assistance is the script system and the comment beginning on line 29 of minerd.relic.util.CompressionHandler.
