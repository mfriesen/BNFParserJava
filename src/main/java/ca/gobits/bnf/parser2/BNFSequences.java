package ca.gobits.bnf.parser2;

import java.util.List;

public class BNFSequences
{
    private int currentPipeLine = -1;
    private List<BNFSequence> pipeLines;
    
    public BNFSequences(List<BNFSequence> pipeLines)
    {        
        this.pipeLines = pipeLines;
    }

    public BNFSequence getNextPipeLine()
    {
        BNFSequence state = null;
        int i = currentPipeLine + 1;
        
        if (i < pipeLines.size())
        {
            state = pipeLines.get(i);
            currentPipeLine = i;
        }
        
        return state;
    }

    public boolean isComplete()
    {
        return this.currentPipeLine >= this.pipeLines.size() - 1;
    }
    
    public List<BNFSequence> getPipeLines()
    {
        return pipeLines;
    }

    public void setPipeLines(List<BNFSequence> pipeLines)
    {
        this.pipeLines = pipeLines;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        for (BNFSequence lines : pipeLines)
        {
            sb.append(lines.toString());
        }
        
        return sb.toString();
    }
}
