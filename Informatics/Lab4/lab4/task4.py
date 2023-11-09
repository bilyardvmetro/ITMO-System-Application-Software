import re


def parse_xml(xml_string):
    root_pattern = re.compile(r'<(\w+)(.*?)>(.*?)</\1>', re.DOTALL)
    attr_pattern = re.compile(r'(\w+)="([^"]+)"')
    xml_string = xml_string.strip()

    node_match = root_pattern.match(xml_string)
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
            node_match = root_pattern.match(xml_string[cursor::].lstrip())
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


xmlFile = open('schedule.xml', mode='r', encoding='utf-8')
yamlFile = open('task3_schedule.yaml', mode='a', encoding='utf-8')
data = xmlFile.read()[39:]

print(parse_xml(data))
# xmlFile.close()
# yamlFile.close()
