class ParserException(Exception):
    def __init__(self, message):
        self.message = message

class NumberParser:

    def __init__(self, line):
        self.line = line
        self.idx = 0

    def number(self):
        sign = 1
        value = 0
        if self.line[self.idx] == '-':
            sign = -1
            self.idx += 1

        if self.idx < len(self.line) and self.line[self.idx] in '0123456789':
            value = self.digit()
            while self.idx < len(self.line) and self.line[self.idx] in '0123456789':
                value = 10 * value + self.digit()
        else:
            raise ParserException("Expected a number")

        if self.idx < len(self.line) and self.line[self.idx] == '.':
            self.idx += 1
            value = value + self.digit() / 10

        return sign * value

    def digit(self):
        if self.idx < len(self.line) and self.line[self.idx] in '0123456789':
            res = int(self.line[self.idx], 10)
            self.idx += 1
            return res
        else:
            raise ParserException("Expected a digit")

def main(arg):
    parse = NumberParser(arg)
    return parse.number()

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