"""вариант 116 паттерн: ;<P"""
import re


def find_smiles(text):
    smiles_count = re.findall(';<P', text)
    print('Количество смайликов', len(smiles_count))


find_smiles('nothing is here')
find_smiles('Реали;<Pзуйте програ;<Pммный продукт на ;<P языке Python')
find_smiles(';<P;);<P^_^;<P\_(*_*)_/;<P;<PxD;<P')
find_smiles(';<P__004==gkg;<Pjs;fYU;<PVT#(U_O;<P"')
find_smiles('заходят как-то в бар русский, немец и еврей')
