"""
использую библиотеку xmlplain 1.6.0
"""
import xmlplain

with open('schedule.xml', encoding='utf-8') as inf:
    xmlSchedule = xmlplain.xml_to_obj(inf, strip_space=True, fold_dict=True)

with open('task2_schedule.yaml', 'a', encoding='utf-8') as outf:
    xmlplain.obj_to_yaml(xmlSchedule, outf)
