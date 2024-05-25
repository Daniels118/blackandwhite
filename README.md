# Black & White modding tools

This is a project under development which aims to build a set of tools to write new challenges (stories) for the game Black & White developed by Lionhead Studios and released in 2001.

The final intent is to have an IDE capable of editing scripts with the help of landscape visualization to pick or show coordinates.

If your are looking for the tools for Black & White: Creature Isle, check the repo [blackandwhite_ci](https://github.com/Daniels118/blackandwhite_ci).

## Current status

A tool capable of compiling CHL sources to CHL binary, and to disassemble/assemble CHL has been made and works perfectly.

## Setup

You need to install Java 11 or later to run this tool. Download the latest release and unpack it to your favorite location.

Optionally add the extracted folder to the PATH environment variable.

## Usage

The syntax differ from the original scripting tool from Lionhead. To compile, you have to prepare a project file where you declare the C headers to be included, and the list of source files to compile. The release zip contains a sample project which should be self explanatory. Then open a command prompt and run the command:
```
chlasm -compile -p _project.txt -o _challenge.chl
```
Be aware that the output file will be overwritten without prompting, so be sure to backup your original CHL or work in another directory.

To see all the available commands and options run the `chlasm` program without any argument.

## Next steps

TBD.

## License

GPL 3

## Author

Daniels118
