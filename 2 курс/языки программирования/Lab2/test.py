import unittest
import subprocess

errors = ["К сожалению такого ключа не существует", "Превышен размера буфера. Введите ключ длиной меньше 255 символов"]
welcome_string = "Введите ключ для поиска: "

class TestDictionary(unittest.TestCase):

    def run_program(self, input_data):
        process = subprocess.run(["./main"], input=input_data, text=True, capture_output=True)
        return process.stdout, process.stderr

    def test_chipinkos(self):
        input = "song"
        output = "chipinkos"

        out, err = self.run_program(input)

        self.assertEqual(out, welcome_string + "\n" + output + "\n")
        self.assertEqual(err, "")

    def test_kasta(self):
        input = "ringtone"
        output = "kasta na s*ka plug flip"

        out, err = self.run_program(input)

        self.assertEqual(out, welcome_string + "\n" + output + "\n")
        self.assertEqual(err, "")

    def test_blank_key(self):
        input = " "
        output = "32423444"

        out, err = self.run_program(input)

        self.assertEqual(out, welcome_string + "\n" + output + "\n")
        self.assertEqual(err, "")

    def test_carti(self):
        input = "playboi carti lyrics"
        output = "wegabadirodogifabono"

        out, err = self.run_program(input)

        self.assertEqual(out, welcome_string + "\n" + output + "\n")
        self.assertEqual(err, "")

    def test_simple_key(self):
        input = "a"
        output = "не придумал что здесь написать"

        out, err = self.run_program(input)

        self.assertEqual(out, welcome_string + "\n" + output + "\n")
        self.assertEqual(err, "")

    def test_key_255(self):
        input = "f"*255
        output = "wassup"

        out, err = self.run_program(input)

        self.assertEqual(out, welcome_string + "\n" + output + "\n")
        self.assertEqual(err, "")

    def test_empty_key(self):
        input = ""
        output = "dev/null"

        out, err = self.run_program(input)

        self.assertEqual(out, welcome_string + "\n" + output + "\n")
        self.assertEqual(err, "")

    def test_non_existing_key(self):
        input = "lol"
        output = ""

        out, err = self.run_program(input)

        self.assertEqual(out, welcome_string + "\n" + output + "\n")
        self.assertEqual(err, errors[0])

    def test_BOF(self):
        input = "*wokeuplikethis" * 100
        output = ""

        out, err = self.run_program(input)

        self.assertEqual(out, welcome_string + output + "\n")
        self.assertEqual(err, errors[1])


if __name__ == '__main__':
    unittest.main()
