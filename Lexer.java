// Matt Treichel - 120585470
// CP471 - Intro to Compiling

import java.io.*;
import java.util.*;

public class Lexer {
	private static final List<String> RESERVED =
		Arrays.asList("if","then","else","fi","while","do","od","def","fed","int","double","print","return","or","and","not");
	private static final List<Character> TERMINALS =
		Arrays.asList(';',',','(',')','[',']','+','-','*','/','%','.','=','>','<');
	private static final List<Character> WHITESPACE = 
		Arrays.asList(' ','\t','\n');
	private char end = '.';
	// Right now symbolTable doesn't need to hold values
	// private Hashtable symbolTable = new Hashtable();
	private static List<String> symbolTable = new ArrayList<String>();
	private int lineNum = 1;
	private char next = ' ';
	
	// Constructor
	public Lexer() { 
		reserve();
	}
	
	// Put Key Words into Symbol Table
	void reserve() { 
		for(int i = 0; i < RESERVED.size(); i++) {
			symbolTable.add(RESERVED.get(i));
		}
	}
	
	// Read Input 1 character at a time
	void readchar() throws IOException {
		next = (char)System.in.read();
		System.out.print(next);
	}
	
	// Helps identify 2 character terminals
	boolean readchar(char c) throws IOException {
		readchar();
		if (next != c) return false;
		else readchar(); return true;
	}
	
	// Grammar only allows a-z as letters
	public static boolean isLetter(char c) {
	    return (c >= 97 && c <= 122);  // a - z
	}
	
	// Grammar only allows 0-9 as digits
	public static boolean isDigit(char c) {
	    return (c >= 48 && c <= 57);  // 0 - 9
	}
	
	// Recover bad ID token
	private String recover(String token) throws IOException {
		while(isLetter(next) || isDigit(next)) {
			token += next;
			readchar();
		}
		return token;
	}
	
	public int getLineNum() {
		return lineNum;
	}
	
	public Token getNextToken() throws IOException {

		// Skip Whitespace & Print it to HTML
		while (WHITESPACE.contains(next)) {
			if (next == '\n') lineNum += 1;
			readchar();
		}
		
		// Check for 2 character terminal symbols
		switch (next) {
			case '=':
				if (readchar('=')) return new Token("==", TokenType.COMP);
				else return new Token('=', TokenType.TERM);
			case '<':
				if (readchar('=')) return new Token("<=", TokenType.COMP);
				else if (next == '>') { readchar(); return new Token("<>", TokenType.COMP); }
				else return new Token('<', TokenType.COMP);
			case '>':
				if (readchar('=')) return new Token(">=", TokenType.COMP);
				else return new Token('>', TokenType.COMP);
		}
		
		// Start Processing Number Token
		if (isDigit(next)) {
			// Integer Part
			String num = "";
			do {
				num += next;
				readchar();
			} while (isDigit(next));
			
			// Invalid Integer
			if (isLetter(next) && next != 'e' && next != 'E')  { 
				return new Token(recover(num), TokenType.ERR);
			// Valid Integer
			} else if (next != '.') {
				SymbolTableTree.getInstance().addEntry(new SymbolTableEntry<Integer>("", SymbolTableEntry.CONSTANT, SymbolTableEntry.INT, Integer.parseInt(num)));
				return new Token(num, TokenType.INT);
			// Double Part
			} else {
				num += next;
				readchar();
				while (isDigit(next)) {			
					num += next;
					readchar();
				}
				// Scientific Notation
				if (next == 'e' || next == 'E') {
					num += next;
					readchar();
					if (next == '-' || next == '+') {
						num += next;
						readchar();
					}
					if (isDigit(next)) {
						while (isDigit(next)) {			
							num += next;
							readchar();
						}
					// Invalid Double
					} else {
						return new Token(recover(num), TokenType.ERR);
					}
				// Invalid Double
				} else if (isLetter(next)) {
					return new Token(recover(num), TokenType.ERR);
				}
				// Valid Double
				SymbolTableTree.getInstance().addEntry(new SymbolTableEntry<Double>("", SymbolTableEntry.CONSTANT, SymbolTableEntry.DOUBLE, Double.parseDouble(num)));
				return new Token(num, TokenType.DOUBLE);
			}
		}
		
		
		// Start Processing ID (or Reserved) Token
		else if (isLetter(next)) {
			
			// Build ID token
			StringBuffer buffer = new StringBuffer();
			do {
				buffer.append(next);
				readchar();
			} while(isLetter(next) || isDigit(next));
			String id = buffer.toString();
			
			// Add to Symbol Table if ID not already defined
			if (!symbolTable.contains(id)) {
				symbolTable.add(id);
				return new Id(id, TokenType.ID, symbolTable.size() - 1);
			} else {
				int index = symbolTable.indexOf(id);
				
				// Return depending on if within Reserved word range
				if (index < RESERVED.size()) {
					return new Reserved(id, TokenType.RESERVED, index);
				} else {
					return new Id(id, TokenType.ID, index);
				}
			}
		}
		
		// Process terminal symbols within grammar
		else if (TERMINALS.contains(next)) {
			if (next == end) {
				return new Token(next, TokenType.END);
			} else {
				Token token = new Token(next, TokenType.TERM);
				readchar();
				return token;
			}
		} 
		
		// Anything else isn't grammar
		else {
			Token token = new Token(next, TokenType.ERR);
			readchar();
			return token;
		}
	}
}