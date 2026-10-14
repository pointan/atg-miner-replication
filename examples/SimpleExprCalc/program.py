class SimpleExprCalc:
    def __init__(self, line):
        self.line = line
        self.idx = 0

    def digit(self):
        if self.line[self.idx] == '0': d = 0
        elif self.line[self.idx] == '1': d = 1
        elif self.line[self.idx] == '2': d = 2
        elif self.line[self.idx] == '3': d = 3
        elif self.line[self.idx] == '4': d = 4
        elif self.line[self.idx] == '5': d = 5
        elif self.line[self.idx] == '6': d = 6
        elif self.line[self.idx] == '7': d = 7
        elif self.line[self.idx] == '8': d = 8
        elif self.line[self.idx] == '9': d = 9
        else: raise Exception("")

        self.idx += 1

        return d

    def s(self):
        e = self.expr()
        print(e)
        return e

    def expr(self):
        t1 = self.term()
        res = t1
        if self.idx < len(self.line) and (self.line[self.idx] == '+' or self.line[self.idx] == '-'):
            if self.line[self.idx] == '+':
                self.idx += 1
                t2 = self.term()
                res = t1 + t2
            elif self.line[self.idx] == '-':
                self.idx += 1
                t2 = self.term()
                res = t1 - t2
        return res

    def term(self):
        f1 = self.fact()
        res = f1
        if self.idx < len(self.line) and (self.line[self.idx] == '*' or self.line[self.idx] == '/'):
            if self.line[self.idx] == '*':
                self.idx += 1
                f2 = self.fact()
                res = f1 * f2
            elif self.line[self.idx] == '/':
                self.idx += 1
                f2 = self.fact()
                if f2 == 0:
                    raise Exception("Division by zero is not allowed!")
                else:
                    res = f1 / f2
        return res

    def fact(self):
        if self.idx < len(self.line) and self.line[self.idx] >= '0' and self.line[self.idx] <= '9':
            f = self.digit()
            return f
        elif self.idx < len(self.line) and self.line[self.idx] == '(':
            self.idx += 1
            f = self.expr()
            if not self.line[self.idx] == ')':
                raise Exception("")
            self.idx += 1
            return f
        else:
            raise Exception("")

def main(line):
    calc = SimpleExprCalc(line)
    return calc.s()

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
#             except Exception as e:
#                 print(line + " lead to error " + str(e))