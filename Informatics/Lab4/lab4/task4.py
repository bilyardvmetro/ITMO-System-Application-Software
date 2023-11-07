import re

openTag = r'<([^/]\w+)>'
closeTag = r'</\w+>'
lstEl = r'<([^/]\w+(\s+\w+="\w+")+)>'
tab = r'    '

xmlFile = open('schedule.xml', mode='r', encoding='utf-8')
yamlFile = open('task3_schedule.yaml', mode='a', encoding='utf-8')
data = xmlFile.read()[39:]

# data = re.sub(lstEl, '-\\1:', data)
# data = re.sub(openTag, '\\1: ', data)
# data = re.sub(closeTag, '', data)
# data = re.sub(tab, '  ', data)
#
# yamlFile.write(data)
# xmlFile.close()
# yamlFile.close()
