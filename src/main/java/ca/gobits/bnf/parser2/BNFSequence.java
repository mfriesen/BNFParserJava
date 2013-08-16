package ca.gobits.bnf.parser2;

import java.util.List;

public class BNFSequence
{
    private int currentPipe = -1;
    private List<BNFSymbol> pipes;
    
    public BNFSequence()
    {        
    }
    
    public BNFSequence(List<BNFSymbol> pipes)
    {        
        this.pipes = pipes;
    }
    
    public BNFSymbol getNextPipe()
    {
        BNFSymbol state = null;
        int i = currentPipe + 1;
        
        if (i < pipes.size())
        {
            state = pipes.get(i);
            currentPipe = i;
        }
        
        return state;
    }

    public boolean isComplete()
    {
        return this.currentPipe >= this.pipes.size() - 1;
    }

    public List<BNFSymbol> getPipes()
    {
        return pipes;
    }
    
    @Override
    public String toString()
    {
        return pipes.toString();
    }
}
