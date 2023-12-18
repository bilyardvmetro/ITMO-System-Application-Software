Option Explicit

#If Win64 Then
    Private Declare PtrSafe Function midiOutOpen Lib "winmm.dll" (lphMidiOut As LongPtr, ByVal uDeviceID As Long, ByVal dwCallback As LongPtr, ByVal dwInstance As LongPtr, ByVal dwflags As Long) As LongPtr
    Private Declare PtrSafe Function midiOutClose Lib "winmm.dll" (ByVal hMidiOut As LongPtr) As Long
    Private Declare PtrSafe Function midiOutShortMsg Lib "winmm.dll" (ByVal hMidiOut As LongPtr, ByVal dwMsg As Long) As Long
    Private Declare PtrSafe Function GetTickCount Lib "kernel32" () As Long
#Else
    Private Declare PtrSafe Function midiOutOpen Lib "winmm.dll" (lphMidiOut As Long, ByVal uDeviceID As Long, ByVal dwCallback As Long, ByVal dwInstance As Long, ByVal dwflags As Long) As Long
    Private Declare PtrSafe Function midiOutClose Lib "winmm.dll" (ByVal hMidiOut As Long) As Long
    Private Declare PtrSafe Function midiOutShortMsg Lib "winmm.dll" (ByVal hMidiOut As Long, ByVal dwMsg As Long) As Long
    Private Declare PtrSafe Function GetTickCount Lib "kernel32" () As Long
#End If

#If Win64 Then
    Private hMidiOut1 As LongLong
#Else
    Private hMidiOut1 As Long
#End If

Public LastTick As Long


Private Sub MidiOpen()
    MidiClose
    midiOutOpen hMidiOut1, 0, 0, 0, 0
End Sub


Private Sub MidiClose()
    midiOutClose hMidiOut1
    hMidiOut1 = 0
End Sub


Private Sub HitDrum(ByVal Note As Integer, ByVal Volume As Integer)
    If hMidiOut1 = 0 Then MidiOpen
    midiOutShortMsg hMidiOut1, RGB(153, Note, Volume)
End Sub


Private Function ticker(x As Long) As Boolean
    Dim i As Long, j As Long
    j = GetTickCount
    i = j - LastTick
    
    If (i >= x) Then
        LastTick = j
        ticker = True
    Else
        ticker = False
    End If
    
End Function


Sub playsong()
    Dim tempo As Long
    Dim marker As Long
    Dim rnge As Range
    Dim TPB As Long
    Dim Ticks As Long
    Dim i As Integer
    Set rnge = Лист1.Cells(1, 1)
    TPB = rnge.Offset(0, 10)
    Ticks = rnge.Offset(0, 5)
    tempo = 60000 / rnge.Offset(0, 15) / TPB
    MidiOpen
    marker = 0
    Do While (rnge.Offset(0, 0) <> "Pause")
        If ticker(tempo) Then
            For i = 0 To 46
                If Val(rnge.Offset(3 + i, 2 + marker)) > 0 Then
                    HitDrum Val(rnge.Offset(3 + i, 0)), Val(rnge.Offset(3 + i, 2 + marker)) * 31 + 64
                End If
                DoEvents
            Next
            marker = (marker + 1) Mod Ticks
        End If
        DoEvents
    Loop
    MidiClose
End Sub

