public class SymbolTableEntry<ValueType>
{
	public static final String INT = "int";
	public static final String DOUBLE = "double";
	public static final String VARIABLE = "var";
	public static final String FUNCTION = "function";
	public static final String CONSTANT = "constant";
	public static final String RESERVED_WORD = "reserved";
	
	/*
		name: Variable or function name
		idType: Is this a variable or a function?
		dataType: Is the variable or function return type an int, a double?
	*/
	
	private String name, idType, dataType;
	private ValueType value;
	
	public SymbolTableEntry(String n, String i, String d, ValueType v)
	{
		name = n;
		idType = i;
		dataType = d;
		
		if (v == null)
			value = (ValueType)"";
		else
			value = v;
	}
	
	public String getName() {return name;}
	public String getIdType() {return idType;}
	public String getDataType() {return dataType;}
	public ValueType getValue() {return value;}
	
	public boolean equals(Object otherObject)
	{
		if (otherObject != null && otherObject instanceof SymbolTableEntry)
		{
			SymbolTableEntry rhs = (SymbolTableEntry) otherObject;
			return ((this.name.equals(rhs.name)) && (this.idType.equals(rhs.idType)) && (this.dataType.equals(rhs.dataType)) && (this.value.equals(rhs.value)));
		}
		
		return false;
	}
	
	public String toString()
	{
		String output = "";
		
		if (name != null && !name.equals(""))
		{
			output += name + " ";
		}
		
		if (idType != null && !idType.equals(""))
		{
			output += idType + " ";
		}
		
		if (dataType != null && !dataType.equals(""))
		{
			output += dataType + " ";
		}
		
		if (value != null)
		{
			output += value;
		}
		
		return output;
	}
}