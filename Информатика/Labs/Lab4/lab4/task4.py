import re

global lvl
global recursion_counter
global parsed_string


def parse_xml(xml_string):
    main_pattern = re.compile(r'<(\w+)(.*?)>(.*?)</\1>', re.DOTALL)
    attr_pattern = re.compile(r'(\w+)="([^"]+)"')
    xml_string = xml_string.strip()

    node_match = main_pattern.match(xml_string)
    result = []
    if node_match:
        tag_name = node_match.group(1)
        attributes = dict(attr_pattern.findall(node_match.group(2)))
        text = ''
        children = parse_xml(node_match.group(3).strip())
        if len(children) == 0:
            text = node_match.group(3)
        result.append({'name': tag_name, 'attributes': attributes, 'text': text, 'children': children})
        cursor = node_match.span()[1]
        while cursor < len(xml_string):
            temp_count = len(xml_string[cursor::])
            node_match = main_pattern.match(xml_string[cursor::].lstrip())
            if node_match:
                tag_name = node_match.group(1)
                attributes = dict(attr_pattern.findall(node_match.group(2)))
                text = ''
                children = parse_xml(node_match.group(3).strip())
                if len(children) == 0:
                    text = node_match.group(3)
                result.append({'name': tag_name, 'attributes': attributes, 'text': text, 'children': children})
                cursor += node_match.span()[1] + temp_count - len(xml_string[cursor::].lstrip())
    return result


def parse_children(obj):
    global lvl
    global recursion_counter
    global parsed_string
    tab = '    '
    has_children = True
    after_attr = False

    for el in range(len(obj)):
        for key in obj[el]:

            if key == 'name':
                if obj[el][key] != '':
                    if obj[el]['text'] != '':
                        has_children = False
                        parsed_string += tab * lvl + obj[el][key] + ': ' + obj[el]['text'] + '\n'
                        if el == len(obj) - 1:
                            lvl -= 1
                        break
                    else:
                        parsed_string += tab * lvl + obj[el][key] + ': ' + '\n'
                else:
                    continue

            if key == 'attributes':
                if len(obj[el][key]) == 0:
                    continue
                else:
                    for attr in obj[el][key]:
                        el_attr = "'" + "@" + attr + "': " + obj[el][key][attr]
                        parsed_string += tab * lvl + el_attr + '\n'
                        after_attr = True

            if key == 'text':
                continue

            if key == 'children':
                if len(obj[el][key]) != 0:
                    if has_children:
                        if not after_attr:
                            lvl += 1
                    recursion_counter += 1
                    parse_children(obj[el][key])

    recursion_counter -= 1
    if recursion_counter == 1:
        lvl = 1


def to_string(obj):
    global lvl
    global recursion_counter
    global parsed_string
    lvl = 1
    recursion_counter = 0
    parsed_string = ''

    root_el = obj[0]['name'] + ':'
    parsed_string += root_el + '\n'

    root_tag = obj[0]['children']

    if len(root_tag) != 0:
        parse_children(root_tag)


def parse_to_yaml(s):
    s = s.replace('    ', '  ')
    lst_el_pattern = re.compile(r'((\s{2})+(\b\w+\b: ))(.+?\1)+', re.DOTALL)
    match = lst_el_pattern.findall(s)

    for el in range(len(match)):
        list_element = match[el][0]
        for char in range(len(list_element)-2):
            if list_element[char] == ' ' and list_element[char+1] == ' ' and list_element[char+2] != ' ':
                s = re.sub(list_element[char] + list_element[char+1] + match[el][0][match[el][0].find(match[el][0][char+2]):],
                           '-' + list_element[char+1] + match[el][0][match[el][0].find(match[el][0][char+2]):], s)
    return s


xmlFile = open('task4_schedule.xml', mode='r', encoding='utf-8')
yamlFile = open('task4_schedule.yaml', mode='a', encoding='utf-8')
data = xmlFile.read()[39:]

p_object = parse_xml(data)
to_string(p_object)
res = parse_to_yaml(parsed_string)
yamlFile.write(res)
