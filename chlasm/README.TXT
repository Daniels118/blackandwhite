CHLASM for Black & White 1

This is a tool to compile/decompile CHL files used by the game Black & White
developed by Lionhead Studios and released in 2001.

This has been made for the original game, not the Creature Isle expansion.

_______________________

HOW TO USE THIS TOOL

The tool has a main class named it.ld.bw.chl.Main, it can be executed without arguments
to get the list of the available commands and their syntax. Currently these functions
are implemented:

- compile CHL source files to CHL binary file;
- convert a CHL binary file to ASM, either in a single file or multiple files (project);
- convert an ASM file (or project) to CHL binary file;
- functionally compare two CHL files;
- various informative functions.

_______________________

REQUIREMENTS

You need to install Java 11 or later in order to run this tool.

_______________________

FAQ

- Where can I get the original source files to edit the original story?

  Files are not included here due to licensing problems. You may search the web to learn
  how to obtain those files.

- I have all the original source files, how can I compile them as CHL?

  You can find the instructions in samples/bw1_story/_project.txt

_______________________

ABOUT

This project has been developed by reverse engineering the CHL files with the
help of previous work made by other people.
Remarkable sources of information used in this project are:
- the openblack project on GitHub
- the bw2-modding project on GitHub
- the readme found in the old chasm tool developed by James "Anthem" Costlow
- the documentation of the official scripting tool for Creature Isle
- the original script files taken from B&W for Mac

_______________________

CHANGELOG

# Version 0.5
- Added //@noyield annotation to speed up execution of short-living scripts

# Version 0.4
- Fixed camera block not allowing dialog statements
- Implemented full support for "say" instructions
_______________________

CHLASM, Copyright (c) 2023-2024 Daniele Lombardi / Daniels118. 
This software is distributed under GNU GENERAL PUBLIC LICENSE Version 3.
Read LICENSE.TXT file for more details about licenses applicable to this software.
