CHLASM 

This is a tool to compile/decompile CHL files used by the game Black & White
developed by Lionhead Studios and release in 2001.

This has been made for the original game, not the Creature Isle expansion.

This project has been developed by reverse engineering the CHL files with the
help of previous work made by other people.
Remarkable sources of information used in this project are:
- the openblack project on GitHub
- the bw2-modding project on GitHub
- the readme found in the old chasm tool developed by James "Anthem" Costlow
- the documentation of the official scripting tool for Creature Isle
- the original script files taken from B&W for Mac

-------------------

HOW TO USE THIS TOOL

The tool has a main class named it.ld.bw.chl.Main, it can be executed without arguments
to get the list of the available commands and their syntax. Currently these functions
are implemented:

- convert a CHL file to ASM, either in a single file or multiple files (project);
- convert an ASM file (or project) to CHL;
- compare two CHL files, ignoring source line numbers;
- print a summary of the instruction types used in a CHL file;
- print the list of the native functions.

The next step is to implement a compiler for the Challenge Language.

-------------------

NOTES

There is a package named it.ld.bw.chl.lexer which is capable of tokenize the CHL language,
but I will probably remove it in favor of a lexer+parser generated using ANTLR.

-------------------

CHLASM, Copyright (c) 2023 Daniele Lombardi / Daniels118. 
This software is distributed under GNU GENERAL PUBLIC LICENSE Version 3.
Read COPYING.TXT file for more details about licenses applicable to this software.
