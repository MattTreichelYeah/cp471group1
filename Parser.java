import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class Parser {
	private Lexer lex = new Lexer();
	private Token lookahead = null;
	private Token token = null;
	private static Hashtable<String, List<String>> FIRST = new Hashtable<String, List<String>>();
	private static Hashtable<String, List<String>> FOLLOW = new Hashtable<String, List<String>>();

	private String currentName, currentFuncName, currentType, currentValue;
	
	private void initializeFIRST() {
        FIRST.put("program", Arrays.asList("def", "int", "double", "if", "while", "print", "return", "ID"));
        FIRST.put("fdecls", Arrays.asList("def", "EPSILON"));
        FIRST.put("fdec", Arrays.asList("def"));
        FIRST.put("fdec_r", Arrays.asList("def", "EPSILON"));
        FIRST.put("params", Arrays.asList("int", "double", "EPSILON"));
		FIRST.put("params_r", Arrays.asList(",", "EPSILON"));
		FIRST.put("fname", Arrays.asList("ID"));
		FIRST.put("declarations", Arrays.asList("int", "double", "EPSILON"));
        FIRST.put("decl", Arrays.asList("int", "double"));
        FIRST.put("decl_r", Arrays.asList("int", "double", "EPSILON"));
        FIRST.put("type", Arrays.asList("int", "double"));
		FIRST.put("varlist", Arrays.asList("ID"));
		FIRST.put("varlist_r", Arrays.asList(",", "EPSILON"));
		FIRST.put("statement_seq", Arrays.asList("if", "while", "print", "return", "ID", "EPSILON"));
        FIRST.put("statement", Arrays.asList("if","while","print","return","ID","EPSILON"));
        FIRST.put("statement_seq_r", Arrays.asList(";", "EPSILON"));
		FIRST.put("opt_else", Arrays.asList("else", "EPSILON"));
		FIRST.put("expr", Arrays.asList("ID", "NUMBER", "("));
		FIRST.put("term", Arrays.asList("ID", "NUMBER", "("));
		FIRST.put("term_r", Arrays.asList("+", "-", "EPSILON"));
		FIRST.put("var_r", Arrays.asList("[","EPSILON"));
        FIRST.put("var", Arrays.asList("ID"));
        FIRST.put("comp", Arrays.asList("<", ">", "==", "<=", ">=", "<>"));
        FIRST.put("bfactor_r_p", Arrays.asList("(", "not", "ID", "NUMBER", "EPSILON"));
        FIRST.put("bfactor", Arrays.asList("(", "not"));
        FIRST.put("bfactor_r", Arrays.asList("and", "EPSILON"));
        FIRST.put("bterm", Arrays.asList("(", "not"));
        FIRST.put("bterm_r", Arrays.asList("or", "EPSILON"));
        FIRST.put("bexpr", Arrays.asList("(", "not"));
        FIRST.put("exprseq_r", Arrays.asList(",", "EPSILON"));
        FIRST.put("exprseq", Arrays.asList("(", "ID", "NUMBER"));
        FIRST.put("factor", Arrays.asList("(", "ID", "NUMBER"));
        FIRST.put("factor_r", Arrays.asList("*", "/", "%", "EPSILON"));
        FIRST.put("factor_r_p", Arrays.asList("(","EPSILON"));
		
		// Missing grt_opt, less_opt, id, id_r, integer, integer_r, double, double_r, decimal, decimal_r, exponent, letter, digit
		// Might only be relevant for lexer. Will add in later if actually needed.
	}
	
	private void initializeFOLLOW() {
        FOLLOW.put("program", Arrays.asList("$"));
        FOLLOW.put("fdecls", Arrays.asList("int", "double", "if", "while", "print", "return", "ID"));
        FOLLOW.put("fdec", Arrays.asList(";"));
        FOLLOW.put("fdec_r", Arrays.asList(";"));
        FOLLOW.put("params", Arrays.asList(")"));
		FOLLOW.put("params_r", Arrays.asList(")"));
		FOLLOW.put("fname", Arrays.asList("("));
		FOLLOW.put("declarations", Arrays.asList("if","while","print","return","ID"));
        FOLLOW.put("decl", Arrays.asList(";"));
        FOLLOW.put("decl_r", Arrays.asList(";"));
        FOLLOW.put("type", Arrays.asList("ID"));
		FOLLOW.put("varlist", Arrays.asList(";",",",".","(",")","]","[","then","+","-","*","/","%","==","<>","<",">"));
		FOLLOW.put("varlist_r", Arrays.asList(";",",",".","(",")","]","[","then","+","-","*","/","%","==","<>","<",">"));
		FOLLOW.put("statement_seq", Arrays.asList(".","fed","fi","od","else"));
        FOLLOW.put("statement", Arrays.asList(".",";","fed","fi","od","else"));
        FOLLOW.put("statement_seq_r", Arrays.asList(".",";","fed","fi","od","else"));
		FOLLOW.put("opt_else", Arrays.asList("fi"));
		FOLLOW.put("expr", Arrays.asList(".",";","fed","fi","od","else",")","=",">","<","]"));
		FOLLOW.put("term", Arrays.asList(".",";","fed","fi","od","else",")","=",">","<","]","+","-","*","/"));
		FOLLOW.put("term_r", Arrays.asList(".",";","fed","fi","od","else",")","=",">","<","]","+","-","*","/"));
		FOLLOW.put("var_r", Arrays.asList(";",",",".","(",")","]","[","then","+","-","*","/","%","==","<>","<",">"));
        FOLLOW.put("var", Arrays.asList(";",",",".","(",")","]","[","then","+","-","*","/","%","==","<>","<",">"));
        FOLLOW.put("comp", Arrays.asList(""));
        FOLLOW.put("bfactor_r_p", Arrays.asList("then","do",")","or","and"));
        FOLLOW.put("bfactor", Arrays.asList("then","do",")","or","and"));
        FOLLOW.put("bfactor_r", Arrays.asList("then","do",")","or","and"));
        FOLLOW.put("bterm", Arrays.asList("then","do",")","or","and"));
        FOLLOW.put("bterm_r", Arrays.asList("then","do",")","or","and"));
        FOLLOW.put("bexpr", Arrays.asList("then","do",")","or"));
        FOLLOW.put("exprseq_r", Arrays.asList(")"));
        FOLLOW.put("exprseq", Arrays.asList(")"));
        FOLLOW.put("factor", Arrays.asList(".",";","fed","fi","od","else",")","=",">","<","]","+","-","*","/"));
        FOLLOW.put("factor_r", Arrays.asList(".",";","fed","fi","od","else",")","=",">","<","]","+","-","*","/"));
        FOLLOW.put("factor_r_p", Arrays.asList(".",";","fed","fi","od","else",")","=",">","<","]","+","-","*","/"));
	}
	
	// Calls Parser
	public static void main(String[] args) throws IOException {
		Parser parser = new Parser();
	}
	
	public Parser() throws IOException {
		initializeFIRST();
		initializeFOLLOW();
		boolean validParse = false;
		consumeToken(); consumeToken(); // Twice to initialize token & lookahead
		
		validParse = program();
		
		System.out.println("\nValid Parse: " + validParse);
		
		if (validParse)
		{
			System.out.println("");
			SymbolTableTree.getInstance().printSymbolTables();
		}
		else if (!validParse) System.out.println("Error on Line " + lex.getLineNum() + " at token " + lookahead.getRepresentation());
	}
	
	// RECURSIVE FUNCTIONS
	
	public boolean program() {
		String first = checkFIRST("program");
		if(first != null)
			return fdecls() && declarations() && statement_seq() && match('.');
		else
			return false;
	}
	
	public boolean fdecls() {
		String first = checkFIRST("fdecls");
		if(first != null)
			return fdec() && match(';') && fdec_r();
		else //Epsilon
			return true;
	}
	
	public boolean fdec() {
		String first = checkFIRST("fdec");
		if(first != null)
		{
			return match("def") && type() && fname() && match("(") && params() && match(")") && declarations() && statement_seq() && match("fed");
		}
		else
			return false;
	}
	
	public boolean fdec_r() {
		String first = checkFIRST("fdec_r");
		if(first != null)
			return fdec() && match(";") && fdec_r();
		else //Epsilon
			return true;
	}

	public boolean params() {
		String first = checkFIRST("params");
		if (first != null)
			return type() && var() && params_r();
		else
			return true;
	}
	
	public boolean params_r() {
		String first = checkFIRST("params_r");
		if (first != null)
			return match(",") && params();
		else //Epsilon
			return true;
	}
	
	public boolean fname() {
		String first = checkFIRST("fname");
		if (first != null)
		{
			currentName = lookahead.getRepresentation();
			currentFuncName = currentName;
			SymbolTableTree.getInstance().addEntry(new SymbolTableEntry(currentName, SymbolTableEntry.FUNCTION, currentType, null));
			return match(TokenType.ID);
		}
		else
			return false;
	}
	
	public boolean declarations() {
		String first = checkFIRST("declarations");
		if(first != null)
			return decl() && match(';') && decl_r();
		else //Epsilon
			return true;
	}
	
	public boolean decl() {
		String first = checkFIRST("decl");
		if(first != null)
			return type() && varlist();
		else
			return false;
	}
	
	public boolean decl_r() {
		String first = checkFIRST("decl_r");
		if(first != null)
			return decl() && match(";") && decl_r();
		else //Epsilon
			return true;
	}
	
	public boolean type() {
		String first = checkFIRST("type");
		
		switch(first) {
			case "int":
				currentType = SymbolTableEntry.INT;
				return match("int");
			case "double":
				currentType = SymbolTableEntry.DOUBLE;
				return match("double");
			default:
				return false;
		}
	}
	
	public boolean statement_seq() {
		String first = checkFIRST("statement_seq");
		if(first != null)
			return statement() && statement_seq_r();
		else //Epsilon
			return true;
	}
	
	public boolean varlist() {
		String first = checkFIRST("varlist");
		if (first != null)
			return var() && varlist_r();
		else
			return false;
	}
	
	public boolean varlist_r() {
		String first = checkFIRST("varlist_r");
		if (first != null)
			return match(",") && varlist();
		else //Epsilon
			return true;
	}
	
	public boolean statement() {
		String first = checkFIRST("statement");
		switch(first) {
			case "ID":
				return var() && match("=") && expr();
			case "if":
				return match("if") && bexpr() && match("then") && statement_seq() && opt_else() && match("fi"); 
			case "while":
				return match("while") && bexpr() && match("do") && statement_seq() && match("od");
			case "print":
				return match("print") && expr();
			case "return":
				return match("return") && expr();
			default: //Epsilon
				return true;
		}
	}
	
	public boolean statement_seq_r() {
		String first = checkFIRST("statement_seq_r");
		if(first != null)
			return match(";") && statement_seq();
		else //Epsilon
			return true;
	}

	public boolean opt_else() {
		String first = checkFIRST("opt_else");
		if (first != null)
			return match("else") && statement_seq();
		else //Epsilon
			return true;
	}
	
	public boolean expr()
	{
		String first = checkFIRST("expr");
		if (first != null)
			return term() && term_r();
		else
			return false;
	}
	
	public boolean term_r() {
		String first = checkFIRST("term_r");
		
		if (first != null) {
			if (first.equals("+"))
				return match("+") && term() && term_r();
			else if (first.equals("-"))
				return match("-") && term() && term_r();
			else
				return false;
		} else { //Epsilon
			return true;
		}
	}
	
	public boolean term() {
		String first = checkFIRST("term");
		
		if (first != null)
			return factor() && factor_r();
		else
			return false;
	}
	
	public boolean factor_r() {
		String first = checkFIRST("factor_r");
		if (first != null) {
			switch(first) {
				case "*":
					return match("*") && factor() && factor_r();
				case "/":
					return match("/") && factor() && factor_r();
				case "%":
					return match("%") && factor() && factor_r();
				default:
					return false;
			}
		} else { //Epsilon
			return true;
		}
	}
	
	// Careful
	 public boolean factor() {
		String first = checkFIRST("factor");
		if (first != null) {
			if (first.equals("ID"))
				return match(TokenType.ID) && factor_r_p();
			else if (first.equals("NUMBER"))
				return match(TokenType.INT) || match(TokenType.DOUBLE);
			else if (first.equals("("))
				return match("(") && expr() && match(")");
			else if (first.equals("ID"))
				return var();
			else
				return false;
		} else {
			return false;
		}
	}
 
	public boolean factor_r_p() {
		String first = checkFIRST("factor_r_p");
		if (first != null) {
			if (first.equals("("))
				return match("(") && exprseq() && match(")");
			else // EPSILON
				return true;
		} else {
			return true;
		}
	}
	
	public boolean exprseq() {
		String first = checkFIRST("exprseq");
		if (first != null)
			return expr() && exprseq_r();
		else //Epsilon
			return true;
	}
	
	public boolean exprseq_r() {
		String first = checkFIRST("exprseq_r");
		if (first != null)
			return match(",") && exprseq();
		else //Epsilon
			return true;
	}
	
	public boolean bexpr() {
		String first = checkFIRST("bexpr");
		if (first != null)
			return bterm() && bterm_r();
		else
			return false;
	}
	
	public boolean bterm_r() {
		String first = checkFIRST("bterm_r");
		if (first != null)
			return match("or") && bterm() && bterm_r();
		else //Epsilon
			return true;
	}
	
	public boolean bterm() {
		String first = checkFIRST("bterm");
		if (first != null)
			return bfactor() && bfactor_r();
		else
			return false;
	}
	
	public boolean bfactor_r() {
		String first = checkFIRST("bfactor_r");
		if (first != null)
			return match("and") && bfactor() && bfactor_r();
		else //Epsilon
			return true;
	}
	
	public boolean bfactor() {
		String first = checkFIRST("bfactor");
		switch (first) {
			case "(":
				return match("(") && bfactor_r_p() && match(")");
			case "not":
				return match("not") && bfactor();
			default:
				return false;
		}
	}
	
	// Careful
	public boolean bfactor_r_p() {
		String first = checkFIRST("bfactor_r_p");
		if (FIRST.get("bfactor_r_p").contains(first) && token.getType() == TokenType.COMP)
			return expr() && comp() && expr();
		else if (FIRST.get("bfactor_r_p").contains(first))
			return bexpr();
		else 
			return false;
	}
	
	public boolean comp() {
		String first = checkFIRST("comp");
		if (first != null)
			return match(TokenType.COMP);
		else
			return false;
	}
	
	public boolean var() {
		String first = checkFIRST("var");
		if (first != null)
		{
			currentName = lookahead.getRepresentation();
			
			if (currentFuncName != null)
				SymbolTableTree.getInstance().addEntry(new SymbolTableEntry(currentName, SymbolTableEntry.VARIABLE, currentType, null), currentFuncName);
			else
				SymbolTableTree.getInstance().addEntry(new SymbolTableEntry(currentName, SymbolTableEntry.VARIABLE, currentType, null));
			return match(TokenType.ID) && var_r();
		}
		else
			return false;
	}
	
	public boolean var_r() {
		String first = checkFIRST("var_r");
		if (first != null)
			return match("[") && expr() && match("]");
		else //Epsilon
			return true;
	}
	
	// UTILITY FUNCTIONS
	
	public void consumeToken() {
		lookahead = token;
		try {
			if (token == null || (token != null && token.getType() != TokenType.END)) {
				token = lex.getNextToken();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String checkFIRST(String nonterminal) {
		List<String> first = FIRST.get(nonterminal);
		if (first != null) {
			if (lookahead.getType() == TokenType.ID && first.contains("ID")) {
				return "ID";
			} else if ((lookahead.getType() == TokenType.INT || lookahead.getType() == TokenType.DOUBLE) && first.contains("NUMBER")) {
				return "NUMBER";
			} else if (first.contains(lookahead.getRepresentation())) {
				return lookahead.getRepresentation();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public boolean match() {
		consumeToken();
		return true;
	}
	
	public boolean match(char c) {
		boolean isMatch = lookahead.getRepresentation().equals(String.valueOf(c));
		if (isMatch) consumeToken();
		return isMatch;
	}
	
	public boolean match(String s) {
		boolean isMatch = lookahead.getRepresentation().equals(s);
		if (isMatch) 
		{
			if (s.equals("fed"))
				currentFuncName = null;
			
			consumeToken();
		}
		return isMatch;
	}
	
	public boolean match(TokenType type) {
		boolean isMatch = lookahead.getType() == type;
		if (isMatch) consumeToken();
		return isMatch;
	}
}