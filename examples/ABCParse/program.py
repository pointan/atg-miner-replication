class ParserException(Exception):
    def __init__(self, message):
        self.message = message

class SParse:
    def __init__(self, line):
        self.line = line
        self.idx = 0

    def S(self):
        cnt_a = self.A()
        cnt_b = self.B()
        cnt_c = self.C()

        return cnt_a == cnt_b and cnt_b == cnt_c

    def A(self):
        cnt = 0
        if self.idx < len(self.line) and self.line[self.idx] == 'a':
            self.idx += 1
            cnt = 1
        else:
            raise ParserException("Unexpected character, expected: 'a'")
        if self.idx < len(self.line) and self.line[self.idx] == 'a':
            cnt = cnt + self.A()
        return cnt

    def B(self):
        cnt = 0
        if self.idx < len(self.line) and self.line[self.idx] == 'b':
            self.idx += 1
            cnt = 1
        else:
            raise ParserException("Unexpected character, expected: 'b'")
        if self.idx < len(self.line) and self.line[self.idx] == 'b':
            cnt = cnt + self.B()
        return cnt

    def C(self):
        cnt = 0
        if self.idx < len(self.line) and self.line[self.idx] == 'c':
            self.idx += 1
            cnt = 1
        else:
            raise ParserException("Unexpected character, expected: 'c'")
        if self.idx < len(self.line) and self.line[self.idx] == 'c':
            cnt = cnt + self.C()
        return cnt


def main(arg):
    parse = SParse(arg)
    return parse.S()

def result():
    return main(input())

def test():
    return main(input())

# if __name__ == '__main__':
#     with open('manual_inputs.txt', 'r') as file:
#         lines = [line.strip() for line in file.readlines()]
#         for line in lines:
#             try:
#                 print(main(line))
#             except ParserException as e:
#                 print(line + " lead to error " + str(e))