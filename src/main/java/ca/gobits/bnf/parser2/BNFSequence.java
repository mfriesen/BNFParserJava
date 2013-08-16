package ca.gobits.bnf.parser2;

import java.util.List;

public class BNFSequence
{
    private List<BNFSymbol> symbols;
    
    public BNFSequence()
    {        
    }
    
    public BNFSequence(List<BNFSymbol> symbols)
    {        
        this.symbols = symbols;
    }
    
    public List<BNFSymbol> getSymbols()
    {
        return symbols;
    }
    
    @Override
    public String toString()
    {
        return symbols.toString();
    }
}
