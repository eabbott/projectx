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
Send There was no APA cover page (-5 points).
score:=score-5
return
F2::
Send There was no APA reference page (-5 points).
score:=score-5
return
F3::
Send References were not in proper APA format, ie, alphabetized (-5 points).
score:=score-5
return
F4::
Send Please provide analysis of benefits and disadvantages in greater depth. (-5 points).
score:=score-5
return
F5::
Send Paper did not meet 3-5 pages guidelines (not including cover or reference pages (-5 points).
score:=score-5
return
F6::
Send There are many words that are not spelled correctly (-10 points).
score:=score-10
return
F7::
Send Content was far too brief (-5 points).
score:=score-5
return
F8::
Send Prohibited sites were used, therefore no credit is given.
score:=score-125
return
F9::
Send Paper was submitted lates (-5 points).
score:=score-5
return
F10::
Send That said, the content was enjoyable to read.
score:=score-0
return
F11::
Send Your fourth individual project shows your comprehension of the material at hand...good work!
score:=score-0
return
F12::
Send %score%
score:=125
return