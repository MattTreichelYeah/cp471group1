import java.util.*;

public class SymbolTableTree
{
	private int size = 0;
	
	private class SymbolTableNode
	{
		public static final String GLOBAL_SCOPE = "GLOBAL";
		
		public SymbolTableNode parent;
		public ArrayList<SymbolTableNode> children;
		public ArrayList<SymbolTableEntry> symbolTable;
		public String symbolTableName;
		
		// Use to create global symbol table
		public SymbolTableNode()
		{
			symbolTable = new ArrayList<SymbolTableEntry>();
			children = new ArrayList<SymbolTableNode>();
			parent = null;
			symbolTableName = GLOBAL_SCOPE;
		}
		
		// Use to create a function sub-table
		public SymbolTableNode(SymbolTableNode parentTable, String functionName)
		{
			parent = parentTable;
			symbolTable = new ArrayList<SymbolTableEntry>();
			children = new ArrayList<SymbolTableNode>();
			symbolTableName = functionName;
		}
		
		public SymbolTableNode clone()
		{
			SymbolTableNode nodeToReturn = new SymbolTableNode();
			nodeToReturn.children = (ArrayList<SymbolTableNode>) this.children.clone();
			nodeToReturn.symbolTable = (ArrayList<SymbolTableEntry>) this.symbolTable.clone();
			nodeToReturn.parent = this.parent;
			nodeToReturn.symbolTableName = this.symbolTableName;
			
			return nodeToReturn;
		}
		
		public void printTable()
		{
			System.out.println(symbolTableName + "\n" + "--------------------------");
			
			for (int i = 0; i < symbolTable.size(); i++)
			{
				System.out.println(symbolTable.get(i));
			}
		}
		
		public void add(SymbolTableEntry entryToAdd)
		{
			if (!symbolTable.contains(entryToAdd))
			{
				symbolTable.add(entryToAdd);
				size++;
			}
		}
		
		// Search symbol tables by name
		public SymbolTableEntry get(String name)
		{
			SymbolTableEntry locatedEntry = null;
			
			// Search local symbol table first
			for (int currentEntry = 0; currentEntry < symbolTable.size(); currentEntry++)
			{
				if (symbolTable.get(currentEntry).getName().equals(name))
					locatedEntry = symbolTable.get(currentEntry);
			}
			
			if (locatedEntry != null)
			{
				return locatedEntry;
			}
			else
			{
				// ID wasn't found in local symbol table, must search child tables
				for (int i = 0; i < children.size(); i++)
				{
					for (int j = 0; j < children.get(i).symbolTable.size(); j++)
					{
						if (children.get(i).symbolTable.get(j).getName().equals(name))
						{
							locatedEntry = children.get(i).symbolTable.get(j);
							return locatedEntry;
						}
					}
				}
				
				// ID not found in child symbol tables, must search parents
				SymbolTableNode currentTable = parent;
				
				while (currentTable != null)
				{
					for (int i = 0; i < currentTable.symbolTable.size(); i++)
					{
						if (currentTable.symbolTable.get(i).getName().equals(name))
						{
							locatedEntry = symbolTable.get(i);
							break;
						}
					}
					
					if (locatedEntry != null) break;
					
					// ID still not found, must go another level higher.
					currentTable = currentTable.parent;
				}
				
				return locatedEntry;
			}
		}
	}
	
	private static SymbolTableNode globalTable = null;
	private static SymbolTableTree instance = null;
	
	private SymbolTableTree(){} // Does nothing, exists only to block instantiation
	
	private SymbolTableNode locateFunctionTable(String functionName) // Used to return a SymbolTableNode corresponding to a specified function's symbolTable
	{
		SymbolTableNode currentNode = globalTable.clone();
		
		for (int i = 0; i < currentNode.children.size(); i++)
		{
			if (currentNode.children.get(i).symbolTableName.equals(functionName))
				return currentNode.children.get(i);
		}
		
		return null;
	}
	
	public void addEntry(SymbolTableEntry entryToAdd) // Use this to add entries to the global symbol table
	{
		if (globalTable == null)
			globalTable = new SymbolTableNode(); // Initialize symbol table by creating global scope table.
		
		if (entryToAdd.getIdType().equals(SymbolTableEntry.FUNCTION))
		{
			globalTable.add(entryToAdd);
			
			SymbolTableNode functionChildTable = new SymbolTableNode(globalTable, entryToAdd.getName());
			globalTable.children.add(functionChildTable);
		}
		else if (entryToAdd.getIdType().equals(SymbolTableEntry.VARIABLE) || entryToAdd.getIdType().equals(SymbolTableEntry.CONSTANT))
		{
			globalTable.add(entryToAdd);
		}
	}
	
	public void addEntry(SymbolTableEntry entryToAdd, String atFunction) // Use this to add entires to the symbol table for the specified function name.
	{
		if (globalTable == null)
			globalTable = new SymbolTableNode(); // Initialize symbol table by creating global scope table.
		
		SymbolTableNode nodeToModify = locateFunctionTable(atFunction);
		
		if (entryToAdd.getIdType().equals(SymbolTableEntry.FUNCTION)) // This is extra fluff that would allow for adding a function within a function. I've left it just in case we get the opportunity to expand the grammar to support that for extra marks some day? Probably not?
		{
			nodeToModify.add(entryToAdd);
			
			SymbolTableNode functionChildTable = new SymbolTableNode(nodeToModify, entryToAdd.getName());
			nodeToModify.children.add(functionChildTable);
		}
		else if (entryToAdd.getIdType().equals(SymbolTableEntry.VARIABLE) || entryToAdd.getIdType().equals(SymbolTableEntry.CONSTANT))
		{
			nodeToModify.add(entryToAdd);
		}
	}
	
	public SymbolTableEntry getEntry(String nameToFind) // Use this to search for entries starting at the global table
	{
		return globalTable.get(nameToFind);
	}
	
	public SymbolTableEntry getEntry(String nameToFind, String atFunction) // Use this to search for entries starting at a specified function name.
	{
		return locateFunctionTable(atFunction).get(nameToFind);
	}
	
	public boolean contains(String nameToFind)
	{
		if (instance == null)
			return false;
		
		return globalTable.get(nameToFind) != null;
	}
	
	public boolean contains(String nameToFind, String atFunction)
	{
		if (instance == null)
			return false;
		
		return locateFunctionTable(atFunction).get(nameToFind) != null;
	}
	
	public void printSymbolTables()
	{
		globalTable.printTable();
		
		for (int i = 0; i < globalTable.children.size(); i++)
		{
			System.out.println("");
			globalTable.children.get (i).printTable();
		}
	}
	
	public int getSize()
	{
		return size;
	}
	
	public static SymbolTableTree getInstance()
	{
		if (instance == null)
			instance = new SymbolTableTree();
		
		return instance;
	}
	
	public static void main(String[] args)
	{
		// Various test cases in order to ensure the symbol table tree works as intended.
		
		SymbolTableTree.getInstance().addEntry(new SymbolTableEntry("butts", SymbolTableEntry.VARIABLE, SymbolTableEntry.INT, "123456"));
		SymbolTableTree.getInstance().addEntry(new SymbolTableEntry("ButtFunc", SymbolTableEntry.FUNCTION, null, null));
		SymbolTableTree.getInstance().addEntry(new SymbolTableEntry("ButtFuncInnerVar", SymbolTableEntry.VARIABLE, SymbolTableEntry.INT, "456789"), "ButtFunc");
		
		System.out.println(SymbolTableTree.getInstance().getEntry("butts").getName());
		System.out.println(SymbolTableTree.getInstance().getEntry("ButtFuncInnerVar", "ButtFunc").getName());
		System.out.println(SymbolTableTree.getInstance().contains("butts"));
	}
}