# Microsoft Developer Studio Project File - Name="Quests" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Console Application" 0x0103

CFG=Quests - Win32 DebugGame
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "Quests.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "Quests.mak" CFG="Quests - Win32 DebugGame"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "Quests - Win32 Release" (based on "Win32 (x86) Console Application")
!MESSAGE "Quests - Win32 Debug" (based on "Win32 (x86) Console Application")
!MESSAGE "Quests - Win32 DebugGame" (based on "Win32 (x86) Console Application")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""$/Black/Scripts/Quests", NQOAAAAA"
# PROP Scc_LocalPath "."
CPP=cl.exe
RSC=rc.exe

!IF  "$(CFG)" == "Quests - Win32 Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Release"
# PROP Intermediate_Dir "Release"
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /c
# ADD CPP /nologo /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /c
# ADD BASE RSC /l 0x409 /d "NDEBUG"
# ADD RSC /l 0x409 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:console /machine:I386
# ADD LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:console /machine:I386

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug"
# PROP Intermediate_Dir "Debug"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /GZ /c
# ADD CPP /nologo /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /GZ /c
# ADD BASE RSC /l 0x409 /d "_DEBUG"
# ADD RSC /l 0x409 /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:console /debug /machine:I386 /pdbtype:sept
# ADD LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:console /debug /machine:I386 /pdbtype:sept

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Quests___Win32_DebugGame"
# PROP BASE Intermediate_Dir "Quests___Win32_DebugGame"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "DebugGame"
# PROP Intermediate_Dir "DebugGame"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /GZ /c
# ADD CPP /nologo /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /YX /FD /GZ /c
# ADD BASE RSC /l 0x409 /d "_DEBUG"
# ADD RSC /l 0x409 /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:console /debug /machine:I386 /pdbtype:sept
# ADD LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:console /debug /machine:I386 /pdbtype:sept

!ENDIF 

# Begin Target

# Name "Quests - Win32 Release"
# Name "Quests - Win32 Debug"
# Name "Quests - Win32 DebugGame"
# Begin Group "Source Files"

# PROP Default_Filter ""
# Begin Group "Control"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\LandControl1.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\LandControl1.txt
InputName=LandControl1

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LandControl1.txt
InputName=LandControl1

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\LandControl2.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\LandControl2.txt
InputName=LandControl2

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LandControl2.txt
InputName=LandControl2

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\LandControl3.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\LandControl3.txt
InputName=LandControl3

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LandControl3.txt
InputName=LandControl3

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\LandControl4.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\LandControl4.txt
InputName=LandControl4

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LandControl4.txt
InputName=LandControl4

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\LandControl5.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\LandControl5.txt
InputName=LandControl5

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LandControl5.txt
InputName=LandControl5

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\LandControlAll.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\LandControlAll.txt
InputName=LandControlAll

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LandControlAll.txt
InputName=LandControlAll

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\LandControlT.txt
# End Source File
# End Group
# Begin Group "Land 1"

# PROP Default_Filter "*.txt"
# Begin Source File

SOURCE=.\ChooseYourCreature.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\ChooseYourCreature.txt
InputName=ChooseYourCreature

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\ChooseYourCreature.txt
InputName=ChooseYourCreature

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\ChooseYourCreature.txt
InputName=ChooseYourCreature

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\CitadelGuide.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\CitadelGuide.txt
InputName=CitadelGuide

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\CreatureDevelopment.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\CreatureDevelopment.txt
InputName=CreatureDevelopment

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\CreatureGuardian.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\CreatureGuardian.txt
InputName=CreatureGuardian

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\CreatureGuardian.txt
InputName=CreatureGuardian

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\CreatureGuide.txt
# End Source File
# Begin Source File

SOURCE=.\CreatureSavingPeople.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\CreatureSavingPeople.txt
InputName=CreatureSavingPeople

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\CreatureSavingPeople.txt
InputName=CreatureSavingPeople

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\CreatureSavingPeople.txt
InputName=CreatureSavingPeople

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\FollowUs.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\FollowUs.txt
InputName=FollowUs

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\FollowUs.txt
InputName=FollowUs

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\FollowUs.txt
InputName=FollowUs

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\LeaveThroughVortexL1.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\LeaveThroughVortexL1.txt
InputName=LeaveThroughVortexL1

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LeaveThroughVortexL1.txt
InputName=LeaveThroughVortexL1

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\LostBrother.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LostBrother.txt
InputName=LostBrother

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# PROP Ignore_Default_Tool 1
# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LostBrother.txt
InputName=LostBrother

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP BASE Ignore_Default_Tool 1
# PROP Ignore_Default_Tool 1
# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LostBrother.txt
InputName=LostBrother

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\MagicMushroom.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\MagicMushroom.txt
InputName=MagicMushroom

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\PiedPiper.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\PiedPiper.txt
InputName=PiedPiper

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# PROP Ignore_Default_Tool 1
# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\PiedPiper.txt
InputName=PiedPiper

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP BASE Ignore_Default_Tool 1
# PROP Ignore_Default_Tool 1
# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\PiedPiper.txt
InputName=PiedPiper

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\ProtectGateKeys.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\ProtectGateKeys.txt
InputName=ProtectGateKeys

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\SetupLand1.txt
# End Source File
# Begin Source File

SOURCE=.\SingingStoneCircle.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\SingingStoneCircle.txt
InputName=SingingStoneCircle

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt c:\dev\black\scripts\quests\challengenotify.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\TakeOverVillagesL1.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\TakeOverVillagesL1.txt
InputName=TakeOverVillagesL1

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TakeOverVillagesL1.txt
InputName=TakeOverVillagesL1

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\TheHermit.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TheHermit.txt
InputName=TheHermit

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TheHermit.txt
InputName=TheHermit

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TheHermit.txt
InputName=TheHermit

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\TheLostFlock.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TheLostFlock.txt
InputName=TheLostFlock

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# PROP Ignore_Default_Tool 1
# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TheLostFlock.txt
InputName=TheLostFlock

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP BASE Ignore_Default_Tool 1
# PROP Ignore_Default_Tool 1
# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TheLostFlock.txt
InputName=TheLostFlock

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\TheMissionaries.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TheMissionaries.txt
InputName=TheMissionaries

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TheMissionaries.txt
InputName=TheMissionaries

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TheMissionaries.txt
InputName=TheMissionaries

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\TheSculptor.txt
# End Source File
# Begin Source File

SOURCE=.\ThrowingStones.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\ThrowingStones.txt
InputName=ThrowingStones

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\ThrowingStones.txt
InputName=ThrowingStones

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\ThrowingStones.txt
InputName=ThrowingStones

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# End Group
# Begin Group "Land 2"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\Baywatch.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Baywatch.txt
InputName=Baywatch

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Baywatch.txt
InputName=Baywatch

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Baywatch.txt
InputName=Baywatch

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\BeginLand2.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\BeginLand2.txt
InputName=BeginLand2

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\quests\VortexEntry.txt c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\BeginLand2.txt
InputName=BeginLand2

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\BeginLand2.txt
InputName=BeginLand2

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\FinalGoldScroll.txt
# End Source File
# Begin Source File

SOURCE=.\GreedyFarmer.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\GreedyFarmer.txt
InputName=GreedyFarmer

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# PROP Ignore_Default_Tool 1
# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\GreedyFarmer.txt
InputName=GreedyFarmer

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP BASE Ignore_Default_Tool 1
# PROP Ignore_Default_Tool 1
# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\GreedyFarmer.txt
InputName=GreedyFarmer

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\HanoiFlood.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\HanoiFlood.txt
InputName=HanoiFlood

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\IdolPyre.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\IdolPyre.txt
InputName=IdolPyre

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\IdolPyre.txt
InputName=IdolPyre

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\IdolPyre.txt
InputName=IdolPyre

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\KillKhazar.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\KillKhazar.txt
InputName=KillKhazar

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\KillKhazar.txt
InputName=KillKhazar

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\Land2ComputerAI.txt
# End Source File
# Begin Source File

SOURCE=.\Land2FireballChallenge.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Land2FireballChallenge.txt
InputName=Land2FireballChallenge

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\Land2ShieldChallenge.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Land2ShieldChallenge.txt
InputName=Land2ShieldChallenge

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\LearnGestures.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LearnGestures.txt
InputName=LearnGestures

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\LearnInfluence.txt
# End Source File
# Begin Source File

SOURCE=.\LearnWorshipping.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LearnWorshipping.txt
InputName=LearnWorshipping

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\LeaveThroughVortexL2.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\LeaveThroughVortexL2.txt
InputName=LeaveThroughVortexL2

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LeaveThroughVortexL2.txt
InputName=LeaveThroughVortexL2

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\LethysVortex.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\LethysVortex.txt
InputName=LethysVortex

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LethysVortex.txt
InputName=LethysVortex

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\LostTreasure.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LostTreasure.txt
InputName=LostTreasure

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LostTreasure.txt
InputName=LostTreasure

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LostTreasure.txt
InputName=LostTreasure

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt c:\dev\black\scripts\quests\swapcreatures.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\Plague.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Plague.txt
InputName=Plague

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Plague.txt
InputName=Plague

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Plague.txt
InputName=Plague

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\Sacrifice.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Sacrifice.txt
InputName=Sacrifice

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Sacrifice.txt
InputName=Sacrifice

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Sacrifice.txt
InputName=Sacrifice

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\SetupLand2.txt
# End Source File
# Begin Source File

SOURCE=.\SingingStonesSongs.txt
# End Source File
# Begin Source File

SOURCE=.\SpiritualHealer.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\SpiritualHealer.txt
InputName=SpiritualHealer

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\SpiritualHealer.txt
InputName=SpiritualHealer

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\SpiritualHealer.txt
InputName=SpiritualHealer

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\TheSlavers.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TheSlavers.txt
InputName=TheSlavers

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TheSlavers.txt
InputName=TheSlavers

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TheSlavers.txt
InputName=TheSlavers

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\TheWorkshop.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TheWorkshop.txt
InputName=TheWorkshop

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TheWorkshop.txt
InputName=TheWorkshop

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TheWorkshop.txt
InputName=TheWorkshop

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\TreePuzzleOne.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TreePuzzleOne.txt
InputName=TreePuzzleOne

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# End Group
# Begin Group "Land 3"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\FreeTheCreature.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\FreeTheCreature.txt
InputName=FreeTheCreature

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\GetThroughVortexL2.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\GetThroughVortexL2.txt
InputName=GetThroughVortexL2

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\GetThroughVortexL2.txt
InputName=GetThroughVortexL2

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\LeaveThroughVortexL3.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\LeaveThroughVortexL3.txt
InputName=LeaveThroughVortexL3

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LeaveThroughVortexL3.txt
InputName=LeaveThroughVortexL3

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\SetupLand3.txt
# End Source File
# Begin Source File

SOURCE=.\Shaolin.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Shaolin.txt
InputName=Shaolin

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Shaolin.txt
InputName=Shaolin

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Shaolin.txt
InputName=Shaolin

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\SwapToApe.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\SwapToApe.txt
InputName=SwapToApe

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\SwapToApe.txt
InputName=SwapToApe

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt c:\dev\black\scripts\quests\swapcreatures.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\ThrowBloke.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\ThrowBloke.txt
InputName=ThrowBloke

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\TreePuzzleTwo.txt
# End Source File
# End Group
# Begin Group "Land 4"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\BeginLand4.txt
# End Source File
# Begin Source File

SOURCE=.\BlindWoman.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\BlindWoman.txt
InputName=BlindWoman

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\FishPuzzle.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\FishPuzzle.txt
InputName=FishPuzzle

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\Land4Meteorites.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Land4Meteorites.txt
InputName=Land4Meteorites

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Land4Meteorites.txt
InputName=Land4Meteorites

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Land4Meteorites.txt
InputName=Land4Meteorites

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\Land4Ogre.txt
# End Source File
# Begin Source File

SOURCE=.\LeaveThroughVortexL4.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\LeaveThroughVortexL4.txt
InputName=LeaveThroughVortexL4

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LeaveThroughVortexL4.txt
InputName=LeaveThroughVortexL4

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\SetupLand4.txt
# End Source File
# Begin Source File

SOURCE=.\ThesiusPuzzleLand4.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\ThesiusPuzzleLand4.txt
InputName=ThesiusPuzzleLand4

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# End Group
# Begin Group "Land 5"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\BeginLand5.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\BeginLand5.txt
InputName=BeginLand5

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt c:\dev\black\scripts\quests\vortexentry.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\ControlNemesisBattleStrategy.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\ControlNemesisBattleStrategy.txt
InputName=ControlNemesisBattleStrategy

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\CreatureCurse.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\CreatureCurse.txt
InputName=CreatureCurse

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\Crusaders.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Crusaders.txt
InputName=Crusaders

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\FinalCreatureSequence.txt
# End Source File
# Begin Source File

SOURCE=.\FireOnHigh.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\FireOnHigh.txt
InputName=FireOnHigh

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\FireOnHigh.txt
InputName=FireOnHigh

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\JapaneseTraitor.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\JapaneseTraitor.txt
InputName=JapaneseTraitor

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\LionPuzzle.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\LionPuzzle.txt
InputName=LionPuzzle

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\MissionariesReturned.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\MissionariesReturned.txt
InputName=MissionariesReturned

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\MissionariesReturned.txt
InputName=MissionariesReturned

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\SetupLand5.txt
# End Source File
# Begin Source File

SOURCE=.\SwapToBrownBear.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\SwapToBrownBear.txt
InputName=SwapToBrownBear

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\SwapToBrownBear.txt
InputName=SwapToBrownBear

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\TakeOverNemesisVillageL5.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TakeOverNemesisVillageL5.txt
InputName=TakeOverNemesisVillageL5

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\TakeOverNeutralVillage.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\TakeOverNeutralVillage.txt
InputName=TakeOverNeutralVillage

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TakeOverNeutralVillage.txt
InputName=TakeOverNeutralVillage

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\TakeOverVillagesL5.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\TakeOverVillagesL5.txt
InputName=TakeOverVillagesL5

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TakeOverVillagesL5.txt
InputName=TakeOverVillagesL5

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\TheBigFight.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\TheBigFight.txt
InputName=TheBigFight

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TheBigFight.txt
InputName=TheBigFight

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\ThrowThroughShield.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\ThrowThroughShield.txt
InputName=ThrowThroughShield

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\ThrowThroughShield.txt
InputName=ThrowThroughShield

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\Volcano.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\Volcano.txt
InputName=Volcano

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Volcano.txt
InputName=Volcano

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# End Group
# Begin Group "RunControl"

# PROP Default_Filter "*.txt"
# Begin Source File

SOURCE=.\RunLandControl1.txt
# End Source File
# Begin Source File

SOURCE=.\RunLandControl2.txt
# End Source File
# Begin Source File

SOURCE=.\RunLandControl3.txt
# End Source File
# Begin Source File

SOURCE=.\RunLandControl4.txt
# End Source File
# Begin Source File

SOURCE=.\RunLandControl5.txt
# End Source File
# End Group
# Begin Group "RunLand1"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\RunChooseYourCreature.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunChooseYourCreature.txt
InputName=RunChooseYourCreature

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunCitadelGuide.txt
# End Source File
# Begin Source File

SOURCE=.\RunCreatureBreeder.txt
# End Source File
# Begin Source File

SOURCE=.\RunCreatureDevelopment.txt
# End Source File
# Begin Source File

SOURCE=.\RunCreatureGuardian.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunCreatureGuardian.txt
InputName=RunCreatureGuardian

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunCreatureGuide.txt
# End Source File
# Begin Source File

SOURCE=.\RunCreatureSavingPeople.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunCreatureSavingPeople.txt
InputName=RunCreatureSavingPeople

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunFollowUs.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunFollowUs.txt
InputName=RunFollowUs

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunHandDemos.txt
# End Source File
# Begin Source File

SOURCE=.\RunLeaveThroughVortexL1.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunLeaveThroughVortexL1.txt
InputName=RunLeaveThroughVortexL1

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunLostBrother.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunLostBrother.txt
InputName=RunLostBrother

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunMagicMushroom.txt
# End Source File
# Begin Source File

SOURCE=.\RunPiedPiper.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunPiedPiper.txt
InputName=RunPiedPiper

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunSingingStoneCircle.txt
# End Source File
# Begin Source File

SOURCE=.\RunTheHermit.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunTheHermit.txt
InputName=RunTheHermit

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunTheLostFlock.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunTheLostFlock.txt
InputName=RunTheLostFlock

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunTheMissionaries.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunTheMissionaries.txt
InputName=RunTheMissionaries

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunTheSculptor.txt
# End Source File
# Begin Source File

SOURCE=.\RunTheStorm.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunTheStorm.txt
InputName=RunTheStorm

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunThrowingStones.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunThrowingStones.txt
InputName=RunThrowingStones

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# End Group
# Begin Group "RunLand2"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\RunAllySpeaks.txt
# End Source File
# Begin Source File

SOURCE=.\RunBaywatch.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunBaywatch.txt
InputName=RunBaywatch

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunBeginLand2.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunBeginLand2.txt
InputName=RunBeginLand2

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunCreatureBreederLand2.txt
# End Source File
# Begin Source File

SOURCE=.\RunFinalGoldScroll.txt
# End Source File
# Begin Source File

SOURCE=.\RunGreedyFarmer.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunGreedyFarmer.txt
InputName=RunGreedyFarmer

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunHanoiFlood.txt
# End Source File
# Begin Source File

SOURCE=.\RunIdolPyre.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunIdolPyre.txt
InputName=RunIdolPyre

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunKillKhazar.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunKillKhazar.txt
InputName=RunKillKhazar

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunLearnGestures.txt
# End Source File
# Begin Source File

SOURCE=.\RunLearnInfluence.txt
# End Source File
# Begin Source File

SOURCE=.\RunLearnWorshipping.txt
# End Source File
# Begin Source File

SOURCE=.\RunLeaveThroughVortexL2.txt
# End Source File
# Begin Source File

SOURCE=.\RunLethysAI.txt
# End Source File
# Begin Source File

SOURCE=.\RunLethysVortex.txt
# End Source File
# Begin Source File

SOURCE=.\RunLostTreasure.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunLostTreasure.txt
InputName=RunLostTreasure

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunMarauder.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunMarauder.txt
InputName=RunMarauder

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunMoreSingingStones.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunMoreSingingStones.txt
InputName=RunMoreSingingStones

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunPlague.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunPlague.txt
InputName=RunPlague

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunPuzzleMaster.txt
# End Source File
# Begin Source File

SOURCE=.\RunSacrifice.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunSacrifice.txt
InputName=RunSacrifice

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunSpiritualHealer.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunSpiritualHealer.txt
InputName=RunSpiritualHealer

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunSwapToLeopard.txt
# End Source File
# Begin Source File

SOURCE=.\RunTheSlavers.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunTheSlavers.txt
InputName=RunTheSlavers

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunTheWorkshop.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunTheWorkshop.txt
InputName=RunTheWorkshop

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunTreePuzzleOne.txt
# End Source File
# End Group
# Begin Group "RunLand3"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\RunActuallyCreatureFreed.txt
# End Source File
# Begin Source File

SOURCE=.\RunFanaticAttack.txt
# End Source File
# Begin Source File

SOURCE=.\RunGetThroughVortexL2.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunGetThroughVortexL2.txt
InputName=RunGetThroughVortexL2

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunLeaveThroughVortexL3.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunLeaveThroughVortexL3.txt
InputName=RunLeaveThroughVortexL3

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunLethysSpeaks.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunLethysSpeaks.txt
InputName=RunLethysSpeaks

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunShaolin.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunShaolin.txt
InputName=RunShaolin

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunSwapToApe.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunSwapToApe.txt
InputName=RunSwapToApe

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunThrowBloke.txt
# End Source File
# Begin Source File

SOURCE=.\RunTreePuzzleTwo.txt
# End Source File
# End Group
# Begin Group "RunLand4"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\RunBeginLand4.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunBeginLand4.txt
InputName=RunBeginLand4

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunBlindWoman.txt
# End Source File
# Begin Source File

SOURCE=.\RunCreatureBreederLand4.txt
# End Source File
# Begin Source File

SOURCE=.\RunCreatureGetsCreed.txt
# End Source File
# Begin Source File

SOURCE=.\RunExplainsHistory.txt
# End Source File
# Begin Source File

SOURCE=.\RunFishPuzzle.txt
# End Source File
# Begin Source File

SOURCE=.\RunLand4Meteorites.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunLand4Meteorites.txt
InputName=RunLand4Meteorites

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunLand4Nomad.txt
# End Source File
# Begin Source File

SOURCE=.\RunLand4Ogre.txt
# End Source File
# Begin Source File

SOURCE=.\RunLeaveThroughVortexL4.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunLeaveThroughVortexL4.txt
InputName=RunLeaveThroughVortexL4

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunSimonSays.txt
# End Source File
# Begin Source File

SOURCE=.\RunSwapToCow.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunSwapToCow.txt
InputName=RunSwapToCow

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunSwapToHorse.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunSwapToHorse.txt
InputName=RunSwapToHorse

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunSwapToTurtle.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunSwapToTurtle.txt
InputName=RunSwapToTurtle

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunTakeOverAztecVillage.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunTakeOverAztecVillage.txt
InputName=RunTakeOverAztecVillage

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunTakeOverVillagesL4.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunTakeOverVillagesL4.txt
InputName=RunTakeOverVillagesL4

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunThesiusPuzzleLand4.txt
# End Source File
# Begin Source File

SOURCE=.\RunUndeadVillage.txt
# End Source File
# Begin Source File

SOURCE=..\..\Script.cpp
# End Source File
# End Group
# Begin Group "RunLand5"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\RunBeginLand5.txt
# End Source File
# Begin Source File

SOURCE=.\RunChimpPosse.txt
# End Source File
# Begin Source File

SOURCE=.\RunCreatureBreederLand5.txt
# End Source File
# Begin Source File

SOURCE=.\RunCreatureMirrorFight.txt
# End Source File
# Begin Source File

SOURCE=.\RunCreatureRunsAmok.txt
# End Source File
# Begin Source File

SOURCE=.\RunCrusaders.txt
# End Source File
# Begin Source File

SOURCE=.\RunFinalCreatureSequence.txt
# End Source File
# Begin Source File

SOURCE=.\RunFireOnHigh.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunFireOnHigh.txt
InputName=RunFireOnHigh

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunGameOver.txt
# End Source File
# Begin Source File

SOURCE=.\RunJapaneseTraitor.txt
# End Source File
# Begin Source File

SOURCE=.\RunLionPuzzle.txt
# End Source File
# Begin Source File

SOURCE=.\RunMissionariesReturned.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunMissionariesReturned.txt
InputName=RunMissionariesReturned

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunNemesisSpeaks1.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunNemesisSpeaks1.txt
InputName=RunNemesisSpeaks1

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunNemesisSpeaks2.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunNemesisSpeaks2.txt
InputName=RunNemesisSpeaks2

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunRebuildVillage.txt
# End Source File
# Begin Source File

SOURCE=.\RunSingingStonesC.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunSingingStonesC.txt
InputName=RunSingingStonesC

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunSwapToBrownBear.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunSwapToBrownBear.txt
InputName=RunSwapToBrownBear

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunSwapToWolf.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunSwapToWolf.txt
InputName=RunSwapToWolf

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunTakeOverNemesisVillageL5.txt
# End Source File
# Begin Source File

SOURCE=.\RunTakeOverNeutralVillage.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunTakeOverNeutralVillage.txt
InputName=RunTakeOverNeutralVillage

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunTakeOverVillagesL5.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunTakeOverVillagesL5.txt
InputName=RunTakeOverVillagesL5

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunTheBigFight.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunTheBigFight.txt
InputName=RunTheBigFight

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunThrowThroughShield.txt
# End Source File
# Begin Source File

SOURCE=.\RunVillagerCatch.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunVillagerCatch.txt
InputName=RunVillagerCatch

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunVillagerWavingSequence.txt
# End Source File
# Begin Source File

SOURCE=.\RunVolcano.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\RunVolcano.txt
InputName=RunVolcano

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunWhackAVillager.txt
# End Source File
# End Group
# Begin Group "Tools Scripts"

# PROP Default_Filter "*.txt"
# Begin Source File

SOURCE=.\ChallengeNotify.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\ChallengeNotify.txt
InputName=ChallengeNotify

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\CreatureBreeder.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\CreatureBreeder.txt
InputName=CreatureBreeder

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\DidYouKnow.txt
# End Source File
# Begin Source File

SOURCE=.\GameOver.txt
# End Source File
# Begin Source File

SOURCE=.\HandDemos.txt
# End Source File
# Begin Source File

SOURCE=.\Reward.txt
# End Source File
# Begin Source File

SOURCE=.\SetupComputerCreatures.txt
# End Source File
# Begin Source File

SOURCE=.\SpittingTotem.txt
# End Source File
# Begin Source File

SOURCE=.\StandardReminder.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\StandardReminder.txt
InputName=StandardReminder

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\SwapCreatures.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\SwapCreatures.txt
InputName=SwapCreatures

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\VortexEntry.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build
InputPath=.\VortexEntry.txt
InputName=VortexEntry

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\VortexEntry.txt
InputName=VortexEntry

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# End Group
# Begin Group "Help"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\CitadelHelp.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\CitadelHelp.txt
InputName=CitadelHelp

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\CitadelHelp.txt
InputName=CitadelHelp

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\CitadelHelp.txt
InputName=CitadelHelp

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\CreatureHelp.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\CreatureHelp.txt
InputName=CreatureHelp

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\CreatureHelp.txt
InputName=CreatureHelp

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\CreatureHelp.txt
InputName=CreatureHelp

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\HelpSystem.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\HelpSystem.txt
InputName=HelpSystem

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# End Group
# Begin Group "BuildChallengeFiles"

# PROP Default_Filter "*.bat"
# Begin Source File

SOURCE=.\AllLandsChallenge.bat
# End Source File
# Begin Source File

SOURCE=.\Challenge.bat
# End Source File
# Begin Source File

SOURCE=.\FileChallenge.bat
# End Source File
# Begin Source File

SOURCE=.\FileLand2Challenge.bat
# End Source File
# Begin Source File

SOURCE=.\GlobalChallenges.txt
# End Source File
# Begin Source File

SOURCE=.\Land1Challenges.txt
# End Source File
# Begin Source File

SOURCE=.\Land2Challenges.txt
# End Source File
# Begin Source File

SOURCE=.\Land3Challenges.txt
# End Source File
# Begin Source File

SOURCE=.\Land4Challenges.txt
# End Source File
# Begin Source File

SOURCE=.\Land5Challenges.txt
# End Source File
# Begin Source File

SOURCE=.\XmenuExtraChallenges.txt
# End Source File
# End Group
# End Group
# Begin Group "Header files"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\Headers.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# PROP Exclude_From_Build 1

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP BASE Exclude_From_Build 1
# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\QuestList.lst
# End Source File
# End Group
# Begin Group "Misc"

# PROP Default_Filter ""
# Begin Group "Test Scripts"

# PROP Default_Filter ""
# Begin Group "TestPuzzle"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\PuzzleFishes1.txt
# End Source File
# Begin Source File

SOURCE=.\PuzzleHanoi.txt
# End Source File
# Begin Source File

SOURCE=.\PuzzleImmersion1.txt
# End Source File
# Begin Source File

SOURCE=.\PuzzleImmersion2.txt
# End Source File
# Begin Source File

SOURCE=.\PuzzleLionsheep.txt
# End Source File
# Begin Source File

SOURCE=.\PuzzleThesius1.txt
# End Source File
# Begin Source File

SOURCE=.\PuzzleThesius2.txt
# End Source File
# Begin Source File

SOURCE=.\PuzzleTilt1.txt
# End Source File
# Begin Source File

SOURCE=.\PuzzleTilt2.txt
# End Source File
# Begin Source File

SOURCE=.\PuzzleTotem1.txt
# End Source File
# Begin Source File

SOURCE=.\PuzzleTotem2.txt
# End Source File
# Begin Source File

SOURCE=.\PuzzleTotem3.txt
# End Source File
# Begin Source File

SOURCE=.\PuzzleTotem4.txt
# End Source File
# Begin Source File

SOURCE=.\PuzzleTrees1.txt
# End Source File
# Begin Source File

SOURCE=.\PuzzleTrees2.txt
# End Source File
# End Group
# Begin Source File

SOURCE=.\BlackCloudTest.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\BlackCloudTest.txt
InputName=BlackCloudTest

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\Bowling.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\Bowling.txt
InputName=Bowling

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\CowBowling.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\CowBowling.txt
InputName=CowBowling

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\CowBowling.txt
InputName=CowBowling

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\CowBowling.txt
InputName=CowBowling

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\CreatureLadder.txt
# End Source File
# Begin Source File

SOURCE=.\FollowUsKM.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\FollowUsKM.txt
InputName=FollowUsKM

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\FoodStoreTest.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\FoodStoreTest.txt
InputName=FoodStoreTest

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\MakeBall.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\MakeBall.txt
InputName=MakeBall

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RunSpittingTotem.txt
# End Source File
# Begin Source File

SOURCE=.\RunVortexEntry.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\RunVortexEntry.txt
InputName=RunVortexEntry

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# Begin Source File

SOURCE=.\RussYourCreature.txt
# End Source File
# Begin Source File

SOURCE=.\TestDidYouKnow.txt
# End Source File
# Begin Source File

SOURCE=.\TestScrap.txt

!IF  "$(CFG)" == "Quests - Win32 Release"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TestScrap.txt
InputName=TestScrap

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TestScrap.txt
InputName=TestScrap

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# Begin Custom Build - Compiling Script $(InputPath) With Headers and Creature Help
InputPath=.\TestScrap.txt
InputName=TestScrap

"c:\dev\black\scripts\quests\$(InputName).chl" : $(SOURCE) "$(INTDIR)" "$(OUTDIR)"
	"Script Compiler" c:\dev\black\scripts\quests\headers.txt c:\dev\black\scripts\quests\creaturehelp.txt $(InputPath) -o$(InputName).chl 
	copy c:\dev\black\scripts\quests\$(InputName).chl c:\dev\black\scripts\quests\challenge.chl 
	
# End Custom Build

!ENDIF 

# End Source File
# End Group
# Begin Group "GotoLand"

# PROP Default_Filter "goto*.txt"
# Begin Source File

SOURCE=.\GotoLand1.txt
# End Source File
# Begin Source File

SOURCE=.\GotoLand2.txt
# End Source File
# Begin Source File

SOURCE=.\GotoLand3.txt
# End Source File
# Begin Source File

SOURCE=.\GotoLand4.txt
# End Source File
# Begin Source File

SOURCE=.\GotoLand5.txt
# End Source File
# End Group
# End Group
# Begin Group "Position Data"

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\..\Data\camera.edt
# End Source File
# Begin Source File

SOURCE=..\..\CameraPosEnum.h
# End Source File
# Begin Source File

SOURCE=..\..\scriptcam.txt
# End Source File
# End Group
# Begin Group "Enumerations"

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\..\AllMeshes.h
# End Source File
# Begin Source File

SOURCE=..\..\AudioMusic.h
# End Source File
# Begin Source File

SOURCE=..\..\AudioSFX.h
# End Source File
# Begin Source File

SOURCE=..\..\CreatureEnum.h
# End Source File
# Begin Source File

SOURCE=..\..\CreatureSpec.h
# End Source File
# Begin Source File

SOURCE=..\..\Enum.h
# End Source File
# Begin Source File

SOURCE=..\..\GStates.h
# End Source File
# Begin Source File

SOURCE=..\..\HelpDudeAnim.h
# End Source File
# Begin Source File

SOURCE=..\..\HelpProfile.h
# End Source File
# Begin Source File

SOURCE=..\..\HitRegions.h
# End Source File
# Begin Source File

SOURCE=..\..\info.h
# End Source File
# Begin Source File

SOURCE=..\..\LHSample.h
# End Source File
# Begin Source File

SOURCE=.\PlayerComputerStrings.txt
# End Source File
# Begin Source File

SOURCE=..\..\ScriptChallengeEnums.h
# End Source File
# Begin Source File

SOURCE=..\..\ScriptEnums.h
# End Source File
# Begin Source File

SOURCE=..\..\Audio\SFX\Script\ScriptSfxEnum.h
# End Source File
# End Group
# Begin Group "Scripting data"

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\..\Audio2ScriptData.h
# End Source File
# Begin Source File

SOURCE=..\..\HelpTextEnums.h
# End Source File
# Begin Source File

SOURCE=..\InfoScript1.txt
# End Source File
# Begin Source File

SOURCE=..\InfoScript2.txt
# End Source File
# End Group
# Begin Group "Output"

# PROP Default_Filter ""
# Begin Source File

SOURCE=.\challenge.chl

!IF  "$(CFG)" == "Quests - Win32 Release"

!ELSEIF  "$(CFG)" == "Quests - Win32 Debug"

# PROP Exclude_From_Build 1

!ELSEIF  "$(CFG)" == "Quests - Win32 DebugGame"

# PROP BASE Exclude_From_Build 1
# PROP Exclude_From_Build 1

!ENDIF 

# End Source File
# End Group
# Begin Group "Zones"

# PROP Default_Filter ".exc"
# Begin Source File

SOURCE=..\..\Data\Zones\land1begin.exc
# End Source File
# Begin Source File

SOURCE=..\..\Data\Zones\Land1Building.exc
# End Source File
# Begin Source File

SOURCE=..\..\Data\Zones\Land1Choose.exc
# End Source File
# Begin Source File

SOURCE=..\..\Data\Zones\Land1TemplePath.exc
# End Source File
# Begin Source File

SOURCE=..\..\Data\Zones\Land1Zone1.exc
# End Source File
# Begin Source File

SOURCE=..\..\Data\Zones\Land1Zone2.exc
# End Source File
# Begin Source File

SOURCE=..\..\Data\Zones\Land1Zone3.exc
# End Source File
# Begin Source File

SOURCE=..\..\Data\Zones\Land1Zone4.exc
# End Source File
# Begin Source File

SOURCE=..\..\Data\Zones\Land1Zone5.exc
# End Source File
# End Group
# Begin Source File

SOURCE=..\map.txt
# End Source File
# End Target
# End Project
