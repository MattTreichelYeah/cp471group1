
public class Id extends Token {
	public int offset;
	public Id(String n, TokenType t, int o) { super(n, t); offset = o; };
	public String toString() {
		return "< " + type + ", " + offset + " >";
	}
}
