class ParserException(Exception):
    def __init__(self, message):
        self.message = message

class Parser:
    def __init__(self, line):
        self.line = line
        self.idx = 0

    def cgi(self):
        res = self.decode()
        print(res)
        return res

    def decode(self):
        res = ''
        while self.idx < len(self.line):
            if self.line[self.idx] == '+':
                self.idx += 1
                res += ' '
            elif self.line[self.idx] == '%':
                self.idx += 1
                digit_high = self.hex()
                digit_low = self.hex()
                val = digit_high * 16 + digit_low
                res += chr(val)
            else:
                res += self.line[self.idx]
                self.idx += 1
        return res

    def hex(self):
        if self.idx < len(self.line) and self.line[self.idx] == '0':
            self.idx += 1
            return 0
        elif self.idx < len(self.line) and self.line[self.idx] == '1':
            self.idx += 1
            return 1
        elif self.idx < len(self.line) and self.line[self.idx] == '2':
            self.idx += 1
            return 2
        elif self.idx < len(self.line) and self.line[self.idx] == '3':
            self.idx += 1
            return 3
        elif self.idx < len(self.line) and self.line[self.idx] == '4':
            self.idx += 1
            return 4
        elif self.idx < len(self.line) and self.line[self.idx] == '5':
            self.idx += 1
            return 5
        elif self.idx < len(self.line) and self.line[self.idx] == '6':
            self.idx += 1
            return 6
        elif self.idx < len(self.line) and self.line[self.idx] == '7':
            self.idx += 1
            return 7
        elif self.idx < len(self.line) and self.line[self.idx] == '8':
            self.idx += 1
            return 8
        elif self.idx < len(self.line) and self.line[self.idx] == '9':
            self.idx += 1
            return 9
        elif self.idx < len(self.line) and self.line[self.idx] == 'a':
            self.idx += 1
            return 10
        elif self.idx < len(self.line) and self.line[self.idx] == 'A':
            self.idx += 1
            return 10
        elif self.idx < len(self.line) and self.line[self.idx] == 'b':
            self.idx += 1
            return 11
        elif self.idx < len(self.line) and self.line[self.idx] == 'B':
            self.idx += 1
            return 11
        elif self.idx < len(self.line) and self.line[self.idx] == 'c':
            self.idx += 1
            return 12
        elif self.idx < len(self.line) and self.line[self.idx] == 'C':
            self.idx += 1
            return 12
        elif self.idx < len(self.line) and self.line[self.idx] == 'd':
            self.idx += 1
            return 13
        elif self.idx < len(self.line) and self.line[self.idx] == 'D':
            self.idx += 1
            return 13
        elif self.idx < len(self.line) and self.line[self.idx] == 'e':
            self.idx += 1
            return 14
        elif self.idx < len(self.line) and self.line[self.idx] == 'E':
            self.idx += 1
            return 14
        elif self.idx < len(self.line) and self.line[self.idx] == 'f':
            self.idx += 1
            return 15
        elif self.idx < len(self.line) and self.line[self.idx] == 'F':
            self.idx += 1
            return 15
        else: raise ParserException("Expected a Hex Value between 0 and f")

def main(args):
    p = Parser(args)
    return p.cgi()

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