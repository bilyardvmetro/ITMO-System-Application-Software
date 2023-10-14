print('Введите сообщение из 7 битов')

message = input()

if len(message) != 7:
    print('Броу, давай без приколов')
    exit(1)

s1 = int(message[0]) ^ int(message[2]) ^ int(message[4]) ^ int(message[6])
s2 = int(message[1]) ^ int(message[2]) ^ int(message[5]) ^ int(message[6])
s3 = int(message[3]) ^ int(message[4]) ^ int(message[5]) ^ int(message[6])

syndrome = str(s1) + str(s2) + str(s3)
print(syndrome)

match syndrome:
    case '000':
        print('Ошибок нет. Всё кул ^_^')

    case '001':
        print('Ошибка в четвертом бите')
        if message[3] == '0':
            message = message[:3] + '1' + message[4:]
        else:
            message = message[:3] + '0' + message[4:]

        print('Верное сообщение: ', message)

    case '010':
        print('Ошибка во втором бите')
        if message[1] == '0':
            message = message[1] + '1' + message[2:]
        else:
            message = message[1] + '0' + message[2:]

        print('Верное сообщение: ', message)

    case '011':
        print('Ошибка в шестом бите')
        if message[5] == '0':
            message = message[:5] + '1' + message[6:]
        else:
            message = message[:5] + '0' + message[6:]

        print('Верное сообщение: ', message)

    case '100':
        print('Ошибка в первом бите')
        if message[0] == '0':
            message = '1' + message[2:]
        else:
            message = '0' + message[2:]

        print('Верное сообщение: ', message)

    case '101':
        print('Ошибка в пятом бите')
        if message[4] == '0':
            message = message[:4] + '1' + message[5:]
        else:
            message = message[:4] + '0' + message[5:]

        print('Верное сообщение: ', message)

    case '110':
        print('Ошибка в третьем бите')
        if message[2] == '0':
            message = message[:2] + '1' + message[3:]
        else:
            message = message[:2] + '0' + message[3:]

        print('Верное сообщение: ', message)

    case '111':
        print('Ошибка в седьмом бите')
        if message[1] == '0':
            message = message[:7] + '1'
        else:
            message = message[:7] + '0'

        print('Верное сообщение: ', message)
