Private Sub Worksheet_Change(ByVal Target As Range)
    If Target.Row = 1 Then
        Select Case Target.Column
            Case 1
                If Target.Value = "Play" Then playsong
            Case 6
                FillGraphic
            Case 11
                FillGraphic
        End Select
    End If
End Sub


Private Sub Worksheet_SelectionChange(ByVal Target As Range)
    If Target.Count = 1 And Target.Row > 3 And Target.Column > 2 Then
        If Target.Value = "" Then
            Target.Value = "1"
        ElseIf Target.Value = "1" Then
            Target.Value = "2"
        Else
            Target.Value = ""
        End If
        Target.Offset(0, 1 - Target.Column).Select
    End If
End Sub

Sub FillGraphic()
    Dim i As Integer, j As Integer, k As Integer, R As Range
    
    Application.Cursor = xlWait
    Application.ScreenUpdating = False
    
    Set R = Ëèñò1.Range("C3:EI50")
    R.Delete xlShiftToLeft
    
    j = 0
    For i = 0 To Val(Ëèñò1.Cells(1, 6)) - 1
        With Ëèñò1.Cells(3, 3).Offset(0, i)
        .Value = j + 1
        j = (j + 1) Mod Val(Ëèñò1.Cells(1, 11))
            If (i \ Val(Ëèñò1.Cells(1, 11)) Mod 2) = 0 Then
                .Interior.Color = RGB(255, 255, 0)
            Else
                .Interior.Color = RGB(255, 252, 204)
            End If
        End With
    Next
    k = 0
    For j = 0 To 46
    For i = 0 To Val(Ëèñò1.Cells(1, 6)) - 1
    
        With Ëèñò1.Cells(4, 3).Offset(j, i)
        
            If (i \ Val(Ëèñò1.Cells(1, 11)) Mod 2) = 0 Then
                .Interior.Color = RGB(255, 255, 0)
            Else
                .Interior.Color = RGB(255, 240, 240)
            End If
        End With
    Next: Next
    
    Application.Cursor = xlDefault
    Application.ScreenUpdating = True
    
End Sub
