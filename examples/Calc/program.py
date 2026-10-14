class ParserException(Exception):
    def __init__(self, message):
        self.message = message

class Calc:
    def __init__(self, line):
        self.line = line
        self.idx = 0

    def calc(self):
        res_cal = self.expr()
        print(res_cal)
        return res_cal

    def expr(self):
        res_expr = []
        if self.idx < len(self.line) and self.line[self.idx] >= '0' and self.line[self.idx] <= '9':
            num = self.num()
            res_expr.append(num)
        elif self.idx < len(self.line) and self.line[self.idx] == '(':
            par = self.paren()
            res_expr.append(par)
        else: raise ParserException("Expected either a number between 0 and 9 or an opening bracket")

        while self.idx < len(self.line) and (self.line[self.idx] in ['+', '-', '*', '/']):
            if self.line[self.idx] == '+':
                res_expr.append('+')
                self.idx += 1
            elif self.line[self.idx] == '-':
                res_expr.append('-')
                self.idx += 1
            elif self.line[self.idx] == '*':
                res_expr.append('*')
                self.idx += 1
            elif self.line[self.idx] == '/':
                res_expr.append('/')
                self.idx += 1
            else: raise ParserException("Expected one of the following operators: + - * /")

            if self.idx < len(self.line) and self.line[self.idx] >= '0' and self.line[self.idx] <= '9':
                num = self.num()
                res_expr.append(num)
            elif self.idx < len(self.line) and self.line[self.idx] == '(':
                par = self.paren()
                res_expr.append(par)
            else: raise ParserException("Expected either a number of an opening parenthesis")

        return res_expr

    def num(self):
        num = ''

        num += self.digit()

        while self.idx < len(self.line) and self.line[self.idx] >= '0' and self.line[self.idx] <= '9':
            num += self.digit()

        return num

    def paren(self):
        if self.idx < len(self.line) and self.line[self.idx] == '(':
            self.idx += 1
        else: raise ParserException("Expected an opening parenthesis")

        res_paren = self.expr()

        if self.idx < len(self.line) and self.line[self.idx] == ')':
            self.idx += 1
        else: raise ParserException("Expected a closing parenthesis")

        return res_paren

    def digit(self):
        if self.idx < len(self.line) and self.line[self.idx] >= '0' and self.line[self.idx] <= '9':
            digit = self.line[self.idx] # we do not use normal assignments as coping yet, should we do that?
            self.idx += 1
            return digit
        else: raise ParserException("Expected a number between 0 and 9")

def main(args):
    parser = Calc(args)
    res_main = parser.calc()
    return res_main

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
