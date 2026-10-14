class ParserException(Exception):
    def __init__(self, message):
        self.message = message

class Parser:
    def __init__(self, line):
        self.line = line
        self.idx = 0

    def microjson(self):
        return self.json()

    def json(self):
        res = self.raw()
        print(res)
        return res

    def raw(self):
        if self.idx < len(self.line) and self.line[self.idx] == '"':
            return self.string()
        elif self.idx < len(self.line) and self.line[self.idx] == '{':
            return self.dict()
        elif self.idx < len(self.line) and self.line[self.idx] == '[':
            return self.list()
        elif self.idx < len(self.line) and self.line[self.idx] in 'tfn':
            return self.fixed()
        elif self.idx < len(self.line) and self.line[self.idx] in '0123456789-eE.':
            return self.number()
        else: raise ParserException("Expected a raw element")

    def string(self):
        res = ''
        if self.idx < len(self.line) and self.line[self.idx] == '"':
            self.idx += 1
        else:
            raise ParserException("Expected a quote")

        while self.idx < len(self.line) and not self.line[self.idx] == '"':
            if self.line[self.idx] == '\\':
                decode = self.decode_escape()
                res += decode
            else:
                res += self.line[self.idx]
                self.idx += 1

        if self.idx < len(self.line) and self.line[self.idx] == '"':
            self.idx += 1
        else:
            raise ParserException("Expected a quote")

        return res

    def dict(self):
        res = {}
        if self.idx < len(self.line) and self.line[self.idx] == '{':
            self.idx += 1
        else:
            raise ParserException("Expected a open bracket")

        if self.idx < len(self.line) and self.line[self.idx] in ',"':
            while self.idx < len(self.line) and self.line[self.idx] == ',':
                self.idx += 1
            key = self.string()
            if self.line[self.idx] == ':':
                self.idx += 1
            else:
                raise ParserException("Expected a colon")
            value = self.raw()
            res[key] = value
            while self.idx < len(self.line) and self.line[self.idx] == ',':
                self.idx += 1
                while self.idx < len(self.line) and self.line[self.idx] == ',':
                    self.idx += 1
                key = self.string()
                if self.line[self.idx] == ':':
                    self.idx += 1
                else:
                    raise ParserException("Expected a colon")
                value = self.raw()
                res[key] = value

        if self.idx < len(self.line) and self.line[self.idx] == '}':
            self.idx += 1
        else:
            raise ParserException("Expected a closed bracket")

        return res

    def list(self):
        res = []
        if self.idx < len(self.line) and self.line[self.idx] == '[':
            self.idx += 1
        else:
            raise ParserException("Expected an opening square bracket")

        if self.idx < len(self.line) and self.line[self.idx] == ",":
            self.idx += 1

        if self.idx < len(self.line) and self.line[self.idx] in '"{[tnf0123456789-eE.':
            val = self.raw()
            res.append(val)
            while self.idx < len(self.line) and self.line[self.idx] == ',':
                self.idx += 1
                val = self.raw()
                res.append(val)

        if self.idx < len(self.line) and self.line[self.idx] == ']':
            self.idx += 1
        else:
            raise ParserException("Expected a closing square bracket")

        return res

    def fixed(self):
        if self.idx < len(self.line) and self.line[self.idx:self.idx + 4] == 'true':
            self.idx += 4
            return True
        elif self.idx < len(self.line) and self.line[self.idx:self.idx + 5] == 'false':
            self.idx += 5
            return False
        elif self.idx < len(self.line) and self.line[self.idx:self.idx + 4] == 'null':
            self.idx += 4
            return None
        else:
            raise ParserException("Expected on of the keywords: true, false, null")

    def number(self):
        res = ''

        if self.idx < len(self.line) and self.line[self.idx] == '-':
            self.idx += 1
            res += '-'

        while self.idx < len(self.line) and self.line[self.idx] in '0123456789':
            digit = self.digit()
            res += digit

        if self.idx < len(self.line) and self.line[self.idx] == '.':
            self.idx += 1
            res += "."
            while self.idx < len(self.line) and self.line[self.idx] in '0123456789':
                digit = self.digit()
                res += digit

        if self.idx < len(self.line) and self.line[self.idx] in 'eE':
            if self.line[self.idx] == 'e':
                res += 'e'
                self.idx += 1
            elif self.line[self.idx] == 'E':
                res += 'E'
                self.idx += 1

            if self.idx < len(self.line) and self.line[self.idx] in '+-':
                if self.line[self.idx] == '-':
                    res += '-'
                    self.idx += 1
                elif self.line[self.idx] == '+':
                    res += '+'
                    self.idx += 1

            digit = self.digit()
            res += digit

            while self.idx < len(self.line) and self.line[self.idx] in '0123456789':
                digit = self.digit()
                res += digit

        return float(res)

    def decode_escape(self):
        if self.idx < len(self.line) and self.line[self.idx] == '\\':
            self.idx += 1
        else:
            raise ParserException("Expected a backslash")

        if self.idx < len(self.line) and self.line[self.idx] == 'n':
            self.idx += 1
            return "\n"
        elif self.idx < len(self.line) and self.line[self.idx] == 't':
            self.idx += 1
            return "\t"
        elif self.idx < len(self.line) and self.line[self.idx] == 'r':
            self.idx += 1
            return "\r"
        elif self.idx < len(self.line) and self.line[self.idx] == 'b':
            self.idx += 1
            return "\b"
        elif self.idx < len(self.line) and self.line[self.idx] == 'f':
            self.idx += 1
            return "\f"
        elif self.idx < len(self.line) and self.line[self.idx] == 'u':
            self.idx += 1
            sv = 12
            r = 0
            hex = self.hex()
            r |= int(hex, 16) << sv
            hex = self.hex()
            r |= int(hex, 16) << sv
            hex = self.hex()
            r |= int(hex, 16) << sv
            hex = self.hex()
            r |= int(hex, 16) << sv
            return chr(r)
        else: raise ParserException("Expected on of the following escape characters: n, t, r, b, f, u")

    def digit(self):
        if self.idx < len(self.line) and self.line[self.idx] == '0':
            self.idx += 1
            return '0'
        elif self.idx < len(self.line) and self.line[self.idx] == '1':
            self.idx += 1
            return '1'
        elif self.idx < len(self.line) and self.line[self.idx] == '2':
            self.idx += 1
            return '2'
        elif self.idx < len(self.line) and self.line[self.idx] == '3':
            self.idx += 1
            return '3'
        elif self.idx < len(self.line) and self.line[self.idx] == '4':
            self.idx += 1
            return '4'
        elif self.idx < len(self.line) and self.line[self.idx] == '5':
            self.idx += 1
            return '5'
        elif self.idx < len(self.line) and self.line[self.idx] == '6':
            self.idx += 1
            return '6'
        elif self.idx < len(self.line) and self.line[self.idx] == '7':
            self.idx += 1
            return '7'
        elif self.idx < len(self.line) and self.line[self.idx] == '8':
            self.idx += 1
            return '8'
        elif self.idx < len(self.line) and self.line[self.idx] == '9':
            self.idx += 1
            return '9'
        else: raise ParserException("Expected a number between 0 and 9")

    def hex(self):
        if self.idx < len(self.line) and self.line[self.idx] == '0':
            self.idx += 1
            return '0'
        elif self.idx < len(self.line) and self.line[self.idx] == '1':
            self.idx += 1
            return '1'
        elif self.idx < len(self.line) and self.line[self.idx] == '2':
            self.idx += 1
            return '2'
        elif self.idx < len(self.line) and self.line[self.idx] == '3':
            self.idx += 1
            return '3'
        elif self.idx < len(self.line) and self.line[self.idx] == '4':
            self.idx += 1
            return '4'
        elif self.idx < len(self.line) and self.line[self.idx] == '5':
            self.idx += 1
            return '5'
        elif self.idx < len(self.line) and self.line[self.idx] == '6':
            self.idx += 1
            return '6'
        elif self.idx < len(self.line) and self.line[self.idx] == '7':
            self.idx += 1
            return '7'
        elif self.idx < len(self.line) and self.line[self.idx] == '8':
            self.idx += 1
            return '8'
        elif self.idx < len(self.line) and self.line[self.idx] == '9':
            self.idx += 1
            return '9'
        elif self.idx < len(self.line) and self.line[self.idx] == 'a':
            self.idx += 1
            return 'a'
        elif self.idx < len(self.line) and self.line[self.idx] == 'A':
            self.idx += 1
            return 'A'
        elif self.idx < len(self.line) and self.line[self.idx] == 'b':
            self.idx += 1
            return 'b'
        elif self.idx < len(self.line) and self.line[self.idx] == 'B':
            self.idx += 1
            return 'B'
        elif self.idx < len(self.line) and self.line[self.idx] == 'c':
            self.idx += 1
            return 'c'
        elif self.idx < len(self.line) and self.line[self.idx] == 'C':
            self.idx += 1
            return 'C'
        elif self.idx < len(self.line) and self.line[self.idx] == 'd':
            self.idx += 1
            return 'd'
        elif self.idx < len(self.line) and self.line[self.idx] == 'D':
            self.idx += 1
            return 'D'
        elif self.idx < len(self.line) and self.line[self.idx] == 'e':
            self.idx += 1
            return 'e'
        elif self.idx < len(self.line) and self.line[self.idx] == 'E':
            self.idx += 1
            return 'E'
        elif self.idx < len(self.line) and self.line[self.idx] == 'f':
            self.idx += 1
            return 'f'
        elif self.idx < len(self.line) and self.line[self.idx] == 'F':
            self.idx += 1
            return 'F'
        else: raise ParserException("Expected a hex value between 0 and f")

def main(arg):
    p = Parser(arg)
    return p.microjson()

def result():
    return main(input())

def test():
    main(input())
    return 0

# if __name__ == '__main__':
#     with open('manual_inputs.txt', 'r') as file:
#         lines = [line.strip() for line in file.readlines()]
#         for line in lines:
#             try:
#                 main(line)
#             except ParserException as e:
#                 print(line + " lead to error " + str(e))