class ParserException(Exception):
    def __init__(self, message):
        self.message = message

def zero_one_parse(s):
    idx = 0
    cnt = 0

    while idx < len(s):
        if s[idx] == '0':
           cnt = cnt + 1
        elif s[idx] == '1':
            cnt = cnt - 1
        else:
            raise ParserException("Unknown character '%s'" % s[idx])
        idx = idx + 1

    return cnt == 0

def result():
    return zero_one_parse(input())

def test():
    zero_one_parse(input())
    return 0

# if __name__ == '__main__':
#     with open('manual_inputs.txt', 'r') as file:
#         lines = [line.strip() for line in file.readlines()]
#         for line in lines:
#             try:
#                 zero_one_parse(line)
#             except Exception as e:
#                 print(line + " lead to error " + str(e))