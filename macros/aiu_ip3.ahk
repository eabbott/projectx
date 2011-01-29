;
; AutoHotkey Version: 1.x
; Language:       English
; Platform:       Win9x/NT
; Author:         A.N.Other <myemail@nowhere.com>
;
; Script Function:
;	Template script (you can customize this template by editing "ShellNew\Template.ahk" in your Windows folder)
;

#NoEnv  ; Recommended for performance and compatibility with future AutoHotkey releases.
SendMode Input  ; Recommended for new scripts due to its superior speed and reliability.
SetWorkingDir %A_ScriptDir%  ; Ensures a consistent starting directory.
score:=125
F1::
Send There was no APA reference page (-5 points).
score:=score-5
return
F2::
Send There were no speaker notes (-10 points).
score:=score-10
return
F4::
Send Presentation was to be 10-15 slides in length (-5 points).
score:=score-5
return
F5::
Send There are many misspellings or grammatical errors. (-10 points).
score:=score-10
return
F6::
Send Content was too brief; speaker note content did not fully explain/support bullet points (-10 points).
score:=score-10
return
F7::
Send Prohibited sites were used, therefore no credit is given.
score:=score-125
return
F8::
Send Paper was submitted late (-5 points).
score:=score-5
return
F9::
Send There was no direct comparison of neighboring economies (-10 points).
score:=score-10
return
F10::
Send Otherwise, the content of your third individual assignment was interesting to read.
score:=score-0
return
F11::
Send Your third individual assignment was well done and I enjoyed reading it.
score:=score-0
return
F12::
Send %score%
score:=125
return