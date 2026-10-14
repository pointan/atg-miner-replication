class ParserException(Exception):
    def __init__(self, message):
        self.message = message

class Parser:
    def __init__(self, line):
        self.line = line
        self.idx = 0

    def xmltree(self):
        opid = self.xml_open_tag()
        inner = self.inner_xml_tree()
        clid = self.xml_close_tag()

        balanced = opid == clid
        balanced = balanced and inner
        return balanced

    def xml_open_tag(self):
        if self.idx < len(self.line) and self.line[self.idx] == '<':
            self.idx += 1
        else:
            raise ParserException("Expected a opening <")

        opid = self.id()

        if self.idx < len(self.line) and self.line[self.idx] == '>':
            self.idx += 1
        else:
            raise ParserException("Expected a closing >")

        return opid

    def inner_xml_tree(self):
        balanced = True
        if self.idx < len(self.line) and self.line[self.idx] in 'abcdef':
            text = self.text()
        elif self.idx < len(self.line) and self.line[self.idx] == '<' and self.idx + 1 < len(self.line) and not self.line[self.idx + 1] == '/':
            inner = self.xmltree()
            balanced = balanced and inner
        else:
            raise ParserException('Expected either a text or a xml tree')

        if self.idx < len(self.line) and self.line[self.idx] in 'abcdef<' and self.idx + 1 < len(self.line) and not self.line[self.idx + 1] == '/':
            inner = self.inner_xml_tree()
            balanced = balanced and inner

        return balanced

    def xml_close_tag(self):
        if self.idx < len(self.line) and self.line[self.idx: self.idx + 2] == '</':
            self.idx += 2
        else:
            raise ParserException('Expected a closing </')

        clid = self.id()

        if self.idx < len(self.line) and self.line[self.idx] == '>':
            self.idx += 1
        else:
            raise ParserException('Expected a closing >')

        return clid

    def text(self):
        text = ''

        if self.idx < len(self.line) and self.line[self.idx] in 'abcdef':
            text = text + self.text_ch()

        while self.idx < len(self.line) and self.line[self.idx] in 'abcdef':
            text = text + self.text_ch()

        return text

    def text_ch(self):
        if self.idx < len(self.line) and self.line[self.idx] >= 'a' and self.line[self.idx] <= 'f':
            character = self.line[self.idx]
            self.idx += 1
            return character
        else:
            raise ParserException("Expected a character between a and f")

    def id(self):
        identifier = ''

        if self.idx < len(self.line) and self.line[self.idx] in 'ABCDEF':
            identifier = identifier + self.id_ch()

        while self.idx < len(self.line) and self.line[self.idx] in 'ABCDEF':
            identifier = identifier + self.id_ch()

        return identifier

    def id_ch(self):
        if self.idx < len(self.line) and self.line[self.idx] >= 'A' and self.line[self.idx] <= 'F':
            character = self.line[self.idx]
            self.idx += 1
            return character
        else: raise ParserException("Expected a character between A and F")

def main(args):
    parser = Parser(args)
    res_main = parser.xmltree()
    print("XML is balanced: " + str(res_main))
    return res_main

def result():
    return main(input())

def test():
    main(input())
    return 0

# if __name__ == "__main__":
#     # main('<AB>cc<BD>dd</BD></AB>')
#     main('<EAB><C>c</A><D><D>ba<F>f</B></F><A>e</A>a</E><B><B>d</A>cdc</FC><F>f</B>b</D>')

# if __name__ == '__main__':
#     with open('manual_inputs.txt', 'r') as file:
#         lines = [line.strip() for line in file.readlines()]
#         for line in lines:
#             try:
#                 main(line)
#             except Exception as e:
#                 print(line + " lead to error " + str(e))