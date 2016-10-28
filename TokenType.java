
public class TokenType {
	public String type;
	public TokenType(String t) { type = t; }
	public String toString() {
		return type;
	}
	
	public static final TokenType
		RESERVED = new TokenType("reserved"),
		ID = new TokenType("id"),
		INT = new TokenType("integer"),
		DOUBLE = new TokenType("double"),
		COMP = new TokenType("comparison"),
		TERM = new TokenType("terminal"),
		END = new TokenType("end"),
		ERR = new TokenType("error");
}
