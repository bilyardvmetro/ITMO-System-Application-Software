import re

s = '<day id="1" name="Понедельник" hui="fdfdfdf">\n<class id="1">'
lines = re.split('\n', s)
print(lines, '\n')

match = re.search(r'<([^/]\w+(\s+\w+="\w+")+)>', lines[0])
print(match[1].split())
