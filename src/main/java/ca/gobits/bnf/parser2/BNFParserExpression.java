package ca.gobits.bnf.parser2;

import ca.gobits.bnf.parser2.BNFSymbol.BNFRepetition;
import ca.gobits.bnf.tokenizer.BNFToken;

public class BNFParserExpression
{
    public enum HolderState
    {
        NONE, MATCH, NO_MATCH, NO_MATCH_WITH_ZERO_REPETITION, EMPTY
    }
    
    private HolderState state;
    private BNFToken token;
    private BNFToken currentToken;
    private BNFSequences pipeLines;
    private BNFSequence pipeLine;
    private BNFRepetition repetition;
    
    public BNFParserExpression(HolderState state)
    {
        this.repetition = BNFRepetition.NONE;
        setState(state);
    }
    
    public BNFParserExpression(BNFSequences pipeLines, BNFToken token) 
    {
        this(HolderState.NONE);
        this.token = token;
        this.currentToken = this.token;
        this.pipeLines = pipeLines;
    }

    public BNFParserExpression(BNFSequence pipeLine, BNFToken token)
    {
        this(HolderState.NONE);
        this.pipeLine = pipeLine;
        this.token = token;
        this.currentToken = this.token;
    }

    public BNFParserExpression(BNFSequences sd, BNFToken token2, BNFRepetition repetition)
    {
        this(sd, token2);
        this.repetition = repetition;
    }

    public BNFParserExpression(BNFSequence pipeLine2, BNFToken token2, BNFRepetition repetition2)
    {
        this(pipeLine2, token2);
        this.repetition = repetition2;
    }

    public void advanceToken(BNFToken token2)
    {
        this.currentToken = token2;
    }
    
    public void resetToken()
    {
        this.currentToken = this.token;
    }
    
    public boolean isPipeLines()
    {
        return this.pipeLines != null;
    }
    
    public boolean isPipeLine()
    {
        return this.pipeLine != null;
    }
    
    public BNFToken getCurrentToken()
    {
        return currentToken;
    }

    public BNFSequences getPipeLines()
    {
        return pipeLines;
    }

    public void setPipeLines(BNFSequences pipeLines)
    {
        this.pipeLines = pipeLines;
    }

    public boolean isComplete()
    {
        return this.pipeLines != null && this.pipeLines.isComplete() || this.pipeLine != null && this.pipeLine.isComplete();
    }

    public BNFSequence getPipeLine()
    {
        return pipeLine;
    }

    public HolderState getState()
    {
        return state;
    }

    public void setState(HolderState state)
    {
        this.state = state;
    }
    
    @Override
    public String toString()
    {
        if (pipeLines != null) {
            return this.pipeLines.toString();
        }
        
        if (this.pipeLine != null) {
            return this.pipeLine.toString();
        }
           
        return "status " + this.state;
    }

    public BNFRepetition getRepetition()
    {
        return repetition;
    }
}
