"""
Вариант 5
XML ---> YAML
день недели: Понедельник
"""

scheduleXml = open('schedule.xml', mode='r', encoding='utf-8')
scheduleYaml = open('task1_schedule.yaml', mode='a', encoding='utf-8')
schedule = [line.rstrip() for line in scheduleXml]

for line in range(1, len(schedule)):
    s = schedule[line]
    for symbol in range(0, len(schedule[line])-1):
        if s[symbol] == '<' and s[symbol + 1] == '/':
            s = s[:symbol]
            break
        if s[symbol] == '<':
            s = s.replace(s[symbol], '*', 1)
        if s[symbol] != '/' and s[symbol+1] == '>':
            s = s.replace(s[symbol+1], '&', 1)

    s = s.replace('*', '').replace('&', ': ')
    if s == '        class: ':
        s = '        - class: '

    scheduleYaml.write(s + '\n')
scheduleXml.close()
scheduleYaml.close()
