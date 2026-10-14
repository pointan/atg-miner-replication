package at.fhooe.se.atgminer.atg.coco;

import at.fhooe.se.atgminer.atg.ATGrammar;
import at.fhooe.se.atgminer.atg.ATElement;
import at.fhooe.se.atgminer.atg.ATNonTerminal;
import at.fhooe.se.atgminer.atg.ATRule;



public class Parser {
	public static final int _EOF = 0;
	public static final int _ident = 1;
	public static final int _number = 2;
	public static final int _string = 3;
	public static final int _badString = 4;
	public static final int _char = 5;
	public static final int maxT = 44;
	public static final int _ddtSym = 45;
	public static final int _optionSym = 46;

	static final boolean _T = true;
	static final boolean _x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;

	public Scanner scanner;
	public Errors errors;

	static final int id = 0;
	static final int str = 1;

    final ATGrammar grammar = new ATGrammar();
    private final CharacterSet factory = new CharacterSet(grammar);

	boolean genScanner;
	String tokenString;         // used in declarations of literal tokens
	String noString = "-none-"; // used in declarations of literal tokens

	private void ignore(Object obj) {
	}

/*-------------------------------------------------------------------------*/



	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}

	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			if (la.kind == 45) {
			}
			if (la.kind == 46) {
			}
			la = t;
		}
	}

	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}

	boolean StartOf (int s) {
		return set[s][la.kind];
	}

	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}

	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}

	private String strip(String val) {
		if (val.length() > 2) {
			return val.substring(1, val.length() - 1);
		}
		else {
			return val;
		}
	}


	void Coco() {
		if (StartOf(1)) {
			Get();
			while (StartOf(1)) {
				Get();
			}
		}
		Expect(6);
		Expect(1);
		grammar.setStartSymbol(grammar.createNonTerminal(t.val));
		while (StartOf(2)) {
			Get();
		}
		if (la.kind == 7) {
			Get();
		}
		if (la.kind == 8) {
			Get();
			while (la.kind == 1) {
				ATRule s = SetDecl();
				grammar.addRule(s);
			}
		}
		if (la.kind == 9) {
			Get();
			while (la.kind == 1 || la.kind == 3 || la.kind == 5) {
				ATRule t = TokenDecl();
				grammar.addRule(t);
			}
		}
		if (la.kind == 10) {
			Get();
			while (la.kind == 1 || la.kind == 3 || la.kind == 5) {
				ATRule t = TokenDecl();
				ignore(t);
			}
		}
		while (la.kind == 11) {
			Get();
			Expect(12);
			ATElement e1 = TokenExpr();
			ignore(e1);
			Expect(13);
			ATElement e2 = TokenExpr();
			ignore(e2);
			if (la.kind == 14) {
				Get();
			}
		}
		while (la.kind == 15) {
			Get();
			ATElement s = Set();
			ignore(s);
		}
		while (!(la.kind == 0 || la.kind == 16)) {SynErr(45); Get();}
		Expect(16);
		while (la.kind == 1) {
			Get();
			String semStr = ""; var key = grammar.createNonTerminal(t.val);
			if (la.kind == 24 || la.kind == 29) {
				AttrDecl();
			}
			if (la.kind == 42) {
				semStr = SemText();
			}
			ExpectWeak(17, 3);
			ATElement body = Expression();
			grammar.addRule(grammar.createRule(key, body, semStr));
			ExpectWeak(18, 4);
		}
		Expect(19);
		Expect(1);
		Expect(18);
	}

	ATRule  SetDecl() {
		ATRule  s;
		Expect(1);
		var key = grammar.createNonTerminal(t.val);
		Expect(17);
		ATElement body = Set();
		s = grammar.createRule(key, body);
		Expect(18);
		return s;
	}

	ATRule  TokenDecl() {
		ATRule  t;
		t = null;
		ATElement s = Sym();
		var key = (ATNonTerminal)s;
		while (!(StartOf(5))) {SynErr(46); Get();}
		if (la.kind == 17) {
			Get();
			ATElement body = TokenExpr();
			Expect(18);
			t = grammar.createRule(key, body);
		} else if (StartOf(6)) {
		} else SynErr(47);
		if (la.kind == 42) {
			String semStr = SemText();
		}
		return t;
	}

	ATElement  TokenExpr() {
		ATElement  e;
		e = null;
		var l = new ATElementList();
		ATElement t1 = TokenTerm();
		l.add(t1);
		while (WeakSeparator(33,7,8) ) {
			ATElement t2 = TokenTerm();
			l.add(t2);
		}
		if (l.size() == 1) e = l.first();
		else e = grammar.createChoice(l.all());
		return e;
	}

	ATElement  Set() {
		ATElement  s;
		s = SimSet();
		while (la.kind == 20 || la.kind == 21) {
			if (la.kind == 20) {
				Get();
				ATElement s1 = SimSet();
				s = factory.union(s, s1);
			} else {
				Get();
				ATElement s2 = SimSet();
				s = factory.diff(s, s2);
			}
		}
		return s;
	}

	void AttrDecl() {
		if (la.kind == 24) {
			Get();
			if (la.kind == 25 || la.kind == 26) {
				if (la.kind == 25) {
					Get();
				} else {
					Get();
				}
				TypeName();
				Expect(1);
				if (la.kind == 27) {
					Get();
				} else if (la.kind == 28) {
					Get();
					while (StartOf(9)) {
						Get();
					}
					Expect(27);
				} else SynErr(48);
			} else if (StartOf(10)) {
				if (StartOf(11)) {
					Get();
					while (StartOf(9)) {
						Get();
					}
				}
				Expect(27);
			} else SynErr(49);
		} else if (la.kind == 29) {
			Get();
			if (la.kind == 25 || la.kind == 26) {
				if (la.kind == 25) {
					Get();
				} else {
					Get();
				}
				TypeName();
				Expect(1);
				if (la.kind == 30) {
					Get();
				} else if (la.kind == 28) {
					Get();
					while (StartOf(12)) {
						Get();
					}
					Expect(30);
				} else SynErr(50);
			} else if (StartOf(10)) {
				if (StartOf(13)) {
					Get();
					while (StartOf(12)) {
						Get();
					}
				}
				Expect(30);
			} else SynErr(51);
		} else SynErr(52);
	}

	String  SemText() {
		String  s;
		s = "";
		Expect(42);
		while (StartOf(14)) {
			if (StartOf(15)) {
				Get();
				s += t.val;
			} else if (la.kind == 4) {
				Get();
			} else {
				Get();
			}
		}
		Expect(43);
		return s;
	}

	ATElement  Expression() {
		ATElement  e;
		var l = new ATElementList();
		ATElement t1 = Term();
		l.add(t1);
		while (WeakSeparator(33,16,17) ) {
			ATElement t2 = Term();
			l.add(t2);
		}
		if (l.size() == 1) e = l.first();
		else e = grammar.createChoice(l.all());
		return e;
	}

	ATElement  SimSet() {
		ATElement  s;
		s = null;
		if (la.kind == 1) {
			Get();
			s = grammar.createNonTerminal(t.val);
		} else if (la.kind == 3) {
			Get();
			s = factory.createFromString(strip(t.val));
		} else if (la.kind == 5) {
			String c1 = Char();
			s = factory.createFromChar(c1);
			if (la.kind == 22) {
				Get();
				String c2 = Char();
				s = factory.createFromRange(c1, c2);
			}
		} else if (la.kind == 23) {
			Get();
			s = factory.createAny();
		} else SynErr(53);
		return s;
	}

	String  Char() {
		String  c;
		Expect(5);
		c = strip(t.val);
		return c;
	}

	ATElement  Sym() {
		ATElement  s;
		s = null;
		if (la.kind == 1) {
			Get();
			s = grammar.createNonTerminal(t.val);
		} else if (la.kind == 3 || la.kind == 5) {
			if (la.kind == 3) {
				Get();
				s = grammar.createTerminal(strip(t.val));
			} else {
				Get();
				s = grammar.createTerminal(strip(t.val));
			}
		} else SynErr(54);
		return s;
	}

	void TypeName() {
		Expect(1);
		while (la.kind == 18 || la.kind == 24 || la.kind == 31) {
			if (la.kind == 18) {
				Get();
				Expect(1);
			} else if (la.kind == 31) {
				Get();
				Expect(32);
			} else {
				Get();
				TypeName();
				while (la.kind == 28) {
					Get();
					TypeName();
				}
				Expect(27);
			}
		}
	}

	ATElement  Term() {
		ATElement  t;
		var l = new ATElementList();
		t = null;
		if (StartOf(18)) {
			if (la.kind == 40) {
				Resolver();
			}
			ATElement f1 = Factor();
			l.add(f1);
			while (StartOf(19)) {
				ATElement f2 = Factor();
				l.add(f2);
			}
			if (l.size() == 1) t = l.first();
			else t = grammar.createSequence(l.all());
		} else if (StartOf(20)) {
		} else SynErr(55);
		return t;
	}

	void Resolver() {
		Expect(40);
		Expect(35);
		Condition();
	}

	ATElement  Factor() {
		ATElement  f;
		f = null; String semStr = "";
		switch (la.kind) {
		case 1: case 3: case 5: case 34: {
			if (la.kind == 34) {
				Get();
			}
			ATElement s = Sym();
			f = s;
			if (la.kind == 24 || la.kind == 29) {
				Attribs();
			}
			break;
		}
		case 35: {
			Get();
			ATElement e = Expression();
			Expect(36);
			f = grammar.createSequence(e);
			break;
		}
		case 31: {
			Get();
			ATElement e = Expression();
			Expect(32);
			f = grammar.createOption(e);
			break;
		}
		case 37: {
			Get();
			ATElement e = Expression();
			Expect(38);
			f = grammar.createRepetition(e);
			break;
		}
		case 42: {
			semStr = SemText();
			break;
		}
		case 23: {
			Get();
			f = factory.createAny();
			break;
		}
		case 39: {
			Get();
			break;
		}
		default: SynErr(56); break;
		}
		return f;
	}

	void Attribs() {
		if (la.kind == 24) {
			Get();
			if (la.kind == 25 || la.kind == 26) {
				if (la.kind == 25) {
					Get();
				} else {
					Get();
				}
				while (StartOf(21)) {
					if (StartOf(22)) {
						Get();
					} else if (la.kind == 31 || la.kind == 35) {
						Bracketed();
					} else {
						Get();
					}
				}
				if (la.kind == 27) {
					Get();
				} else if (la.kind == 28) {
					Get();
					while (StartOf(9)) {
						if (StartOf(23)) {
							Get();
						} else {
							Get();
						}
					}
					Expect(27);
				} else SynErr(57);
			} else if (StartOf(10)) {
				if (StartOf(11)) {
					if (StartOf(24)) {
						Get();
					} else {
						Get();
					}
					while (StartOf(9)) {
						if (StartOf(23)) {
							Get();
						} else {
							Get();
						}
					}
				}
				Expect(27);
			} else SynErr(58);
		} else if (la.kind == 29) {
			Get();
			if (la.kind == 25 || la.kind == 26) {
				if (la.kind == 25) {
					Get();
				} else {
					Get();
				}
				while (StartOf(25)) {
					if (StartOf(26)) {
						Get();
					} else if (la.kind == 31 || la.kind == 35) {
						Bracketed();
					} else {
						Get();
					}
				}
				if (la.kind == 30) {
					Get();
				} else if (la.kind == 28) {
					Get();
					while (StartOf(12)) {
						if (StartOf(27)) {
							Get();
						} else {
							Get();
						}
					}
					Expect(30);
				} else SynErr(59);
			} else if (StartOf(10)) {
				if (StartOf(13)) {
					if (StartOf(28)) {
						Get();
					} else {
						Get();
					}
					while (StartOf(12)) {
						if (StartOf(27)) {
							Get();
						} else {
							Get();
						}
					}
				}
				Expect(30);
			} else SynErr(60);
		} else SynErr(61);
	}

	void Condition() {
		while (StartOf(29)) {
			if (la.kind == 35) {
				Get();
				Condition();
			} else {
				Get();
			}
		}
		Expect(36);
	}

	ATElement  TokenTerm() {
		ATElement  t;
		t = null;
		var l = new ATElementList();
		ATElement f1 = TokenFactor();
		l.add(f1);
		while (StartOf(7)) {
			ATElement f2 = TokenFactor();
			l.add(f2);
		}
		if (l.size() == 1) t = l.first();
		else t = grammar.createSequence(l.all());
		if (la.kind == 41) {
			Get();
			Expect(35);
			ATElement e = TokenExpr();
			ignore(e);
			Expect(36);
		}
		return t;
	}

	ATElement  TokenFactor() {
		ATElement  f;
		f = null;
		if (la.kind == 1 || la.kind == 3 || la.kind == 5) {
			ATElement s = Sym();
			f = s;
		} else if (la.kind == 35) {
			Get();
			ATElement e = TokenExpr();
			Expect(36);
			f = grammar.createSequence(e);
		} else if (la.kind == 31) {
			Get();
			ATElement e = TokenExpr();
			Expect(32);
			f = grammar.createOption(e);
		} else if (la.kind == 37) {
			Get();
			ATElement e = TokenExpr();
			Expect(38);
			f = grammar.createRepetition(e);
		} else SynErr(62);
		return f;
	}

	void Bracketed() {
		if (la.kind == 35) {
			Get();
			while (StartOf(29)) {
				if (la.kind == 31 || la.kind == 35) {
					Bracketed();
				} else {
					Get();
				}
			}
			Expect(36);
		} else if (la.kind == 31) {
			Get();
			while (StartOf(30)) {
				if (la.kind == 31 || la.kind == 35) {
					Bracketed();
				} else {
					Get();
				}
			}
			Expect(32);
		} else SynErr(63);
	}



	public void Parse() {
		la = new Token();
		la.val = "";
		Get();
		Coco();
		Expect(0);

	}

	private static final boolean[][] set = {
		{_T,_T,_x,_T, _x,_T,_x,_x, _x,_x,_T,_T, _x,_x,_x,_T, _T,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_T,_x, _x,_x},
		{_x,_T,_T,_T, _T,_T,_x,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_x,_T,_T,_T, _T,_T,_T,_x, _x,_x,_x,_x, _T,_T,_T,_x, _x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_T,_T,_x,_T, _x,_T,_x,_x, _x,_x,_T,_T, _x,_x,_x,_T, _T,_T,_T,_x, _x,_x,_x,_T, _x,_x,_x,_x, _x,_x,_x,_T, _x,_T,_T,_T, _x,_T,_x,_T, _T,_x,_T,_x, _x,_x},
		{_T,_T,_x,_T, _x,_T,_x,_x, _x,_x,_T,_T, _x,_x,_x,_T, _T,_T,_x,_T, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_T,_x, _x,_x},
		{_T,_T,_x,_T, _x,_T,_x,_x, _x,_x,_T,_T, _x,_x,_x,_T, _T,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_T,_x, _x,_x},
		{_x,_T,_x,_T, _x,_T,_x,_x, _x,_x,_T,_T, _x,_x,_x,_T, _T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_T,_x, _x,_x},
		{_x,_T,_x,_T, _x,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_T, _x,_x,_x,_T, _x,_T,_x,_x, _x,_x,_x,_x, _x,_x},
		{_x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_T, _x,_T,_T,_T, _T,_x,_T,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _T,_x,_x,_x, _T,_x,_T,_x, _x,_x,_x,_x, _x,_x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_x, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x,_x,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x,_x,_x, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_x,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x,_x,_T, _T,_T,_x,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_x, _T,_x},
		{_x,_T,_T,_T, _x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_x,_x, _T,_x},
		{_x,_T,_x,_T, _x,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_T,_x, _x,_x,_x,_T, _x,_x,_x,_x, _x,_x,_x,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x,_T,_x, _x,_x},
		{_x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_T,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _T,_x,_x,_x, _T,_x,_T,_x, _x,_x,_x,_x, _x,_x},
		{_x,_T,_x,_T, _x,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_T, _x,_x,_x,_x, _x,_x,_x,_T, _x,_x,_T,_T, _x,_T,_x,_T, _T,_x,_T,_x, _x,_x},
		{_x,_T,_x,_T, _x,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_T, _x,_x,_x,_x, _x,_x,_x,_T, _x,_x,_T,_T, _x,_T,_x,_T, _x,_x,_T,_x, _x,_x},
		{_x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_T,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _T,_T,_x,_x, _T,_x,_T,_x, _x,_x,_x,_x, _x,_x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_x, _x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_x,_T,_T,_T, _x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_x, _x,_T,_T,_x, _T,_T,_T,_x, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_x,_T,_T,_T, _x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_x, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_x,_T,_T,_T, _x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x,_x,_x, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _x,_T,_x,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_x,_T,_T,_T, _x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _x,_T,_x,_x, _T,_T,_T,_x, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_x,_T,_T,_T, _x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_x,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_x,_T,_T,_T, _x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x,_x,_T, _T,_T,_x,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _x,_T,_T,_T, _T,_T,_T,_T, _T,_x},
		{_x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _x,_T,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x}

	};
} // end Parser


class Errors {
	public int count = 0;                                    // number of errors detected
	public java.io.PrintStream errorStream = System.out;     // error messages go to this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text

	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
		pos = b.indexOf("{1}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
		pos = b.indexOf("{2}");
		if (pos >= 0) b.replace(pos, pos+3, msg);
		errorStream.println(b.toString());
	}

	public void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "ident expected"; break;
			case 2: s = "number expected"; break;
			case 3: s = "string expected"; break;
			case 4: s = "badString expected"; break;
			case 5: s = "char expected"; break;
			case 6: s = "\"COMPILER\" expected"; break;
			case 7: s = "\"IGNORECASE\" expected"; break;
			case 8: s = "\"CHARACTERS\" expected"; break;
			case 9: s = "\"TOKENS\" expected"; break;
			case 10: s = "\"PRAGMAS\" expected"; break;
			case 11: s = "\"COMMENTS\" expected"; break;
			case 12: s = "\"FROM\" expected"; break;
			case 13: s = "\"TO\" expected"; break;
			case 14: s = "\"NESTED\" expected"; break;
			case 15: s = "\"IGNORE\" expected"; break;
			case 16: s = "\"PRODUCTIONS\" expected"; break;
			case 17: s = "\"=\" expected"; break;
			case 18: s = "\".\" expected"; break;
			case 19: s = "\"END\" expected"; break;
			case 20: s = "\"+\" expected"; break;
			case 21: s = "\"-\" expected"; break;
			case 22: s = "\"..\" expected"; break;
			case 23: s = "\"ANY\" expected"; break;
			case 24: s = "\"<\" expected"; break;
			case 25: s = "\"^\" expected"; break;
			case 26: s = "\"out\" expected"; break;
			case 27: s = "\">\" expected"; break;
			case 28: s = "\",\" expected"; break;
			case 29: s = "\"<.\" expected"; break;
			case 30: s = "\".>\" expected"; break;
			case 31: s = "\"[\" expected"; break;
			case 32: s = "\"]\" expected"; break;
			case 33: s = "\"|\" expected"; break;
			case 34: s = "\"WEAK\" expected"; break;
			case 35: s = "\"(\" expected"; break;
			case 36: s = "\")\" expected"; break;
			case 37: s = "\"{\" expected"; break;
			case 38: s = "\"}\" expected"; break;
			case 39: s = "\"SYNC\" expected"; break;
			case 40: s = "\"IF\" expected"; break;
			case 41: s = "\"CONTEXT\" expected"; break;
			case 42: s = "\"(.\" expected"; break;
			case 43: s = "\".)\" expected"; break;
			case 44: s = "??? expected"; break;
			case 45: s = "this symbol not expected in Coco"; break;
			case 46: s = "this symbol not expected in TokenDecl"; break;
			case 47: s = "invalid TokenDecl"; break;
			case 48: s = "invalid AttrDecl"; break;
			case 49: s = "invalid AttrDecl"; break;
			case 50: s = "invalid AttrDecl"; break;
			case 51: s = "invalid AttrDecl"; break;
			case 52: s = "invalid AttrDecl"; break;
			case 53: s = "invalid SimSet"; break;
			case 54: s = "invalid Sym"; break;
			case 55: s = "invalid Term"; break;
			case 56: s = "invalid Factor"; break;
			case 57: s = "invalid Attribs"; break;
			case 58: s = "invalid Attribs"; break;
			case 59: s = "invalid Attribs"; break;
			case 60: s = "invalid Attribs"; break;
			case 61: s = "invalid Attribs"; break;
			case 62: s = "invalid TokenFactor"; break;
			case 63: s = "invalid Bracketed"; break;
			default: s = "error " + n; break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr (int line, int col, String s) {
		printMsg(line, col, s);
		count++;
	}

	public void SemErr (String s) {
		errorStream.println(s);
		count++;
	}

	public void Warning (int line, int col, String s) {
		printMsg(line, col, s);
	}

	public void Warning (String s) {
		errorStream.println(s);
	}
} // Errors


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}
