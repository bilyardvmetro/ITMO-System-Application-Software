import re

openTag = r'<([^/]\w+)>'
closeTag = r'</\w+>'
lstEl = r'        <class>'
tab = r'    '

scheduleXml = open('schedule.xml', mode='r', encoding='utf-8')
scheduleYaml = open('task3_schedule.yaml', mode='a', encoding='utf-8')
s = scheduleXml.read()[39:]

s = re.sub(lstEl, '  - class:', s)
s = re.sub(openTag, '\\1: ', s)
s = re.sub(closeTag, '', s)
s = re.sub(tab, '  ', s)

scheduleYaml.write(s)
scheduleXml.close()
scheduleYaml.close()
