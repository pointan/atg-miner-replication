import math

class ParserException(Exception):
    def __init__(self, message):
        self.message = message

class Parser:
    def __init__(self, line):
        self.line = line
        self.idx = 0

    def math(self):
        value = self.expression()
        print(value)
        return value

    def expression(self):
        return self.addition()

    def addition(self):
        # values = [self.multiplication()]
        values = self.multiplication()

        while self.idx < len(self.line) and (self.line[self.idx] == '+' or self.line[self.idx] == '-'):
            if self.line[self.idx] == '+':
                self.idx += 1
                # values.append(self.multiplication())
                values += self.multiplication()
            elif self.line[self.idx] == '-':
                self.idx += 1
                # values.append(self.multiplication())
                values -= self.multiplication()
            else: raise ParserException("Expected either the operator + or -")

        # return sum(values)
        return values

    def multiplication(self):
        values = self.parenthesis()

        while self.idx < len(self.line) and (self.line[self.idx] == '*' or self.line[self.idx] == '/'):
            if self.line[self.idx] == '*':
                self.idx += 1
                values *= self.parenthesis()
            elif self.line[self.idx] == '/':
                self.idx += 1
                divisor = self.parenthesis()
                if divisor == 0:
                    raise ParserException("Division by 0 is not allowed")
                else:
                    values /= divisor
            else: raise ParserException("Expected either the operator * or /")

        return values

    def parenthesis(self):
        if self.idx < len(self.line) and self.line[self.idx] == '(':
            self.idx += 1
            expr = self.expression()
            if self.idx < len(self.line) and self.line[self.idx] == ')':
                self.idx += 1
                return expr
            else: raise ParserException("Expected a closed parenthesis")
        elif self.idx < len(self.line) and self.line[self.idx] in '-0123456789abcdefghijklnopqrstuvwxyz':
            return self.negative()
        else:
            raise ParserException("Expected an opening parenthesis or a number, variable or function")

    def negative(self):
        if self.idx < len(self.line) and self.line[self.idx] == '-':
            self.idx += 1
            return -self.parenthesis()
        elif self.idx < len(self.line) and self.line[self.idx] in '0123456789abcdefghijklnopqrstuvwxyz':
            return self.value()
        else:
            raise ParserException("Expected a number or a variable/function or -")

    def value(self):
        if self.idx < len(self.line) and self.line[self.idx] in '0123456789':
            return self.number()
        elif self.idx < len(self.line) and self.line[self.idx] in 'abcdefghijklnopqrstuvwxyz':
            return self.variable()
        else: raise ParserException("Expected a number or a variable/function")

    def number(self):
        str_val = self.digit()

        while self.idx < len(self.line) and self.line[self.idx] in '0123456789':
            str_val += self.digit()

        if self.idx < len(self.line) and self.line[self.idx] == '.':
            self.idx += 1
            str_val += "."
            while self.idx < len(self.line) and self.line[self.idx] in '0123456789':
                str_val += self.digit()

        return float(str_val)

    def variable(self):
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('abs')] == 'abs':
            self.idx += len("abs")
            return abs(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('acos')] == 'acos':
            self.idx += len("acos")
            return math.acos(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('asin')] == 'asin':
            self.idx += len("asin")
            return math.asin(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('atan2')] == 'atan2':
            self.idx += len("atan2")
            return math.atan2(*self.arguments2())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('atan')] == 'atan':
            self.idx += len("atan")
            return math.atan(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('cosh')] == 'cosh':
            self.idx += len("cosh")
            return math.cosh(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('cos')] == 'cos':
            self.idx += len("cos")
            return math.cos(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('degrees')] == 'degrees':
            self.idx += len("degrees")
            return math.degrees(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('exp')] == 'exp':
            self.idx += len("exp")
            return math.exp(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('fabs')] == 'fabs':
            self.idx += len("fabs")
            return math.fabs(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('floor')] == 'floor':
            self.idx += len("floor")
            return math.floor(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('fmod')] == 'fmod':
            self.idx += len("fmod")
            return math.fmod(*self.arguments2())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('hypot')] == 'hypot':
            self.idx += len("hypot")
            return math.hypot(*self.arguments2())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('ldexp')] == 'ldexp':
            self.idx += len("ldexp")
            return math.ldexp(*self.arguments2())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('log10')] == 'log10':
            self.idx += len("log10")
            return math.log10(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('log')] == 'log':
            self.idx += len("log")
            return math.log(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('pow')] == 'pow':
            self.idx += len("pow")
            return math.pow(*self.arguments2())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('radians')] == 'radians':
            self.idx += len("radians")
            return math.radians(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('sinh')] == 'sinh':
            self.idx += len("sinh")
            return math.sinh(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('sin')] == 'sin':
            self.idx += len("sin")
            return math.sin(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('sqrt')] == 'sqrt':
            self.idx += len("sqrt")
            return math.sqrt(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('tanh')] == 'tanh':
            self.idx += len("tanh")
            return math.tanh(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('tan')] == 'tan':
            self.idx += len("tan")
            return math.tan(self.arguments1())
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('pi')] == 'pi':
            self.idx += len("pi")
            return math.pi
        if self.idx < len(self.line) and self.line[self.idx : self.idx + len('e')] == 'e':
            self.idx += len("e")
            return math.e
        # if self.idx < len(self.line) and self.line[self.idx : self.idx + len('phi')] == 'phi':
        #     self.idx += len("phi")
        #     return (1 + 5 ** .5) / 2

        raise ParserException("Expected a valid function or variable")

    def arguments1(self):
        if self.idx < len(self.line) and self.line[self.idx] == '(':
            self.idx += 1
        else: raise ParserException("Expected an opening parenthesis")
        arg = self.expression()
        if self.idx < len(self.line) and self.line[self.idx] == ')':
            self.idx += 1
        else: raise ParserException("Expected a closing parenthesis")
        return arg

    def arguments2(self):
        if self.idx < len(self.line) and self.line[self.idx] == '(':
            self.idx += 1
        else: raise ParserException("Expected an opening parenthesis")
        arg1 = self.expression()
        if self.idx < len(self.line) and self.line[self.idx] == ',':
            self.idx += 1
        else: raise ParserException("Expected a comma (,)")
        arg2 = self.expression()
        if self.idx < len(self.line) and self.line[self.idx] == ')':
            self.idx += 1
        else: raise ParserException("Expected a closing parenthesis")
        return arg1, arg2

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

def main(arg):
    p = Parser(arg)
    return p.math()

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