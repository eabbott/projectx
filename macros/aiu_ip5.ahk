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
score:=200
F1::
Send There was no APA cover page -5 points.
score:=score-5
return
F2::
Send There was no APA reference page -5 points.
score:=score-5
return
F3::
Send References were not in proper APA format, ie, alphabetized -5 points.
score:=score-5
return
F4::
Send You did not contribute to this group project, thus no credit is given.
score:=score-200
return
F5::
Send Paper did not meet 5-8 pages guidelines (not including cover or reference pages -5 points.
score:=score-5
return
F6::
Send There are many words that are not spelled correctly -10 points.
score:=score-10
return
F7::
Send Content was far too brief -5 points.
score:=score-5
return
F8::
Send Prohibited sites were used, therefore no credit is given.
score:=score-200
return
F9::
Send There was no detailed mention of work teams -5 points.
score:=score-5
return
F10::
Send Otherwise, your final group project shows a cohesive team effort. Thank you for your full submission.
score:=score-0
return
F11::
Send Your final group project shows a cohesive effort. Thank you for your final submission. 
score:=score-0
return
F12::
Send %score%
score:=200
return