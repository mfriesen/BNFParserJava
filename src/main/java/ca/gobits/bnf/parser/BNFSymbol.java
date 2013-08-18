package ca.gobits.bnf.parser;

public class BNFSymbol
{
    public enum BNFRepetition { NONE, ZERO_OR_MORE }
    
    private String name;
    private BNFRepetition repetition;
    
    public BNFSymbol()
    {        
        this.repetition = BNFRepetition.NONE;
    }
    
    public BNFSymbol(String name)
    {
        this();
        this.name = name;
    }
    
    public BNFSymbol(String name, BNFRepetition repetition)
    {
        this(name);
        this.repetition = repetition;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public BNFRepetition getRepetition()
    {
        return repetition;
    }

    public void setRepetition(BNFRepetition repetition)
    {
        this.repetition = repetition;
    }
        
    @Override
    public String toString()
    {
        return name;
    }
}
