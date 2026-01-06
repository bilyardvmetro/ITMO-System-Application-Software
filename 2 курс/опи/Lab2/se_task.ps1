$other = "E:\OPILab2\README.md", "E:\OPILab2\.gitignore", "E:\OPILab2\5fzBaMZaiL.C7d", "E:\OPILab2\7d5fzBaMZa.YpC",
"E:\OPILab2\flkY5grVVO.Rao", "E:\OPILab2\PWT510a227.UGZ", "E:\OPILab2\vALRaoflkY.GeX"

if (Test-Path .git){
		Remove-Item -Recurse -Force .git -verbose
	}
	else {
		Write-Host ".git dir doesn't exists"
	}

Remove-Item *.java -verbose

foreach($file in $other){
		if (Test-Path $file){
		Remove-Item $file -verbose
	}
	else {
		Write-Host "File $file doesn't exists"
	}
}

Write-Output "#smth" >> README.md

git init

git config user.name "red"
git config user.name "red@example.com"
git config merge.tool "notepad.exe"


# initial commit
git add .
git status
git commit -m "initial commit"

Write-Output "README.md" >> .gitignore
Write-Output "*.ps1" >> .gitignore
Write-Output ".idea" >> .gitignore
Write-Output "*.zip" >> .gitignore
Write-Output "*.png" >> .gitignore
Write-Output "*.C7d" >> .gitignore
Write-Output "*.YpC" >> .gitignore
Write-Output "*.Rao" >> .gitignore
Write-Output "*.UGZ" >> .gitignore
Write-Output "*.Gex" >> .gitignore
Get-Content .gitignore

# .gitignore commit
git rm -r --cached .
git add .
git status
git commit -m "add .gitignore"

# =================================================================================================================

# commit 0
Expand-Archive commit0.zip -Force -DestinationPath .
git add .
git commit -m "r0 (red)"

# commit 1
Expand-Archive commit1.zip -Force -DestinationPath .
git add .
git commit -m "r1 (red)"

# new branch
git checkout -b branch2

# commit 2
Expand-Archive commit2.zip -Force -DestinationPath .
git add .
git commit --author="blue <blue@example.com>" -m "r2 (blue)"

# new branch
git checkout -b branch3

# commit 3
Expand-Archive commit3.zip -Force -DestinationPath .
git add .
git commit -m "r3 (red)"

# goto branch2
git checkout branch2

# commit 4
Expand-Archive commit4.zip -Force -DestinationPath .
git add .
git commit --author="blue <blue@example.com>" -m "r4 (blue)"

# commit 5
Expand-Archive commit5.zip -Force -DestinationPath .
git add .
git commit --author="blue <blue@example.com>" -m "r5 (blue)"

# goto branch3
git checkout branch3

# commit 6
Expand-Archive commit6.zip -Force -DestinationPath .
git add .
git commit -m "r6 (red)"

# goto branch2
git checkout branch2

# commit 7
Expand-Archive commit7.zip -Force -DestinationPath .
git add .
git commit --author="blue <blue@example.com>" -m "r7 (blue)"

# goto master
git checkout master

# commit 8
Expand-Archive commit8.zip -Force -DestinationPath .
git add .
git commit -m "r8 (red)"

# goto branch3
git checkout branch3

# commit 9
Expand-Archive commit9.zip -Force -DestinationPath .
git add .
git commit -m "r9 (red)"

# commit 10
Expand-Archive commit10.zip -Force -DestinationPath .
git add .
git commit -m "r10 (red)"

# goto branch2
git checkout branch2

# commit 11
Expand-Archive commit11.zip -Force -DestinationPath .
git add .
git commit --author="blue <blue@example.com>" -m "r11 (blue)"

git checkout branch3

# merge
# -Xtheirs = get updates from branch2
git merge branch2 --no-commit -Xtheirs

# commit 12
Expand-Archive commit12.zip -Force -DestinationPath .
git add .
git commit -m "r12 (red)"

# commit 13
Expand-Archive commit13.zip -Force -DestinationPath .
git add .
git commit -m "r13 (red)"

git checkout master

# merge
# -Xours = get updates from master
git merge branch3 --commit -Xours

# commit 14
Expand-Archive commit14.zip -Force -DestinationPath .
git add .
git commit -m "r14 (red)"


