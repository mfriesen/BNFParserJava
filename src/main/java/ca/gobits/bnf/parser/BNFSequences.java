package ca.gobits.bnf.parser;

import java.util.List;

public class BNFSequences
{
    private List<BNFSequence> sequences;
    
    public BNFSequences(List<BNFSequence> sequences)
    {        
        this.sequences = sequences;
    }
    
    public List<BNFSequence> getSequences()
    {
        return sequences;
    }

    public void setPipeLines(List<BNFSequence> sequences)
    {
        this.sequences = sequences;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        for (BNFSequence lines : sequences)
        {
            sb.append(lines.toString());
        }
        
        return sb.toString();
    }
}
