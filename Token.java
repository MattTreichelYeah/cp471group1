
public class Token {
	public TokenType type;
	public String name;
	public Token(String n, TokenType t) { name = n; type = t; }
	public Token(char n, TokenType t) { name = String.valueOf(n); type = t; }
	public String toString() {
		return "< " + name + " >";
	}
	public TokenType getType() {
		return type;
	}
	public String getRepresentation() {
		return name;
	}
}
