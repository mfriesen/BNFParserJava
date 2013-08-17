package ca.gobits.bnf.parser2;

import ca.gobits.bnf.tokenizer.BNFToken;

public class BNFParserState
{
    public enum BNFParserRepetition
    {
        NONE, ZERO_OR_MORE, ZERO_OR_MORE_LOOKING_FOR_FIRST_MATCH
    }
    
    public enum HolderState
    {
        NONE, MATCH, MATCH_NO_TOKEN_ADVANCE, NO_MATCH, MATCH_WITH_ZERO_REPETITION, NO_MATCH_WITH_ZERO_REPETITION, EMPTY
    }
    
    private int currentSequence = -1;
    private int currentSymbol = -1;
    private HolderState state;
    private BNFToken token;
    private BNFToken currentToken;
    private BNFSequences sequences;
    private BNFSequence sequence;
    private BNFParserRepetition repetition;
    
    public BNFParserState(HolderState state)
    {
        this.repetition = BNFParserRepetition.NONE;
        setState(state);
    }
    
    public BNFParserState(BNFSequences sequences, BNFToken token) 
    {
        this(HolderState.NONE);
        this.token = token;
        this.currentToken = this.token;
        this.sequences = sequences;
    }

    public BNFParserState(BNFSequence sequence, BNFToken token)
    {
        this(HolderState.NONE);
        this.sequence = sequence;
        this.token = token;
        this.currentToken = this.token;
    }

    public BNFParserState(BNFSequences sd, BNFToken token, BNFParserRepetition repetition)
    {
        this(sd, token);
        this.repetition = repetition;
    }

    public BNFParserState(BNFSequence sequence, BNFToken token, BNFParserRepetition repetition)
    {
        this(sequence, token);
        this.repetition = repetition;
    }

    public void advanceToken(BNFToken token2)
    {
        this.currentToken = token2;
    }
    
    public void resetToken()
    {
        this.currentToken = this.token;
    }
    
    public boolean isSequences()
    {
        return this.sequences != null;
    }
    
    public boolean isSequence()
    {
        return this.sequence != null;
    }
    
    public BNFToken getCurrentToken()
    {
        return currentToken;
    }

    public BNFSequences getPipeLines()
    {
        return sequences;
    }

    public void setPipeLines(BNFSequences pipeLines)
    {
        this.sequences = pipeLines;
    }

    public boolean isComplete()
    {
        return this.isCompleteSequence() || isCompleteSymbol();
    }

    public BNFSequence getPipeLine()
    {
        return sequence;
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
        if (sequences != null) {
            return this.sequences.toString();
        }
        
        if (this.sequence != null) {
            return this.sequence.toString();
        }
           
        return "status " + this.state;
    }

    public BNFParserRepetition getRepetition()
    {
        return repetition;
    }
    
	public void setRepetition(BNFParserRepetition repetition) {
		this.repetition = repetition;	
	}

    public BNFSequence getNextSequence()
    {
        BNFSequence seq = null;
        int i = currentSequence + 1;
        
        if (i < this.sequences.getSequences().size())
        {
        	seq = this.sequences.getSequences().get(i);
            currentSequence = i;
        }
        
        return seq;
    }
    
    public boolean isCompleteSequence()
    {
        return this.sequences != null && this.currentSequence >= this.sequences.getSequences().size() - 1;
    }
    
	public BNFSymbol getNextSymbol() {
		
		BNFSymbol symbol = null;
		int i = this.currentSymbol + 1;

		if (i < this.sequence.getSymbols().size()) {
			symbol = this.sequence.getSymbols().get(i);
			this.currentSymbol = i;
		}

		return symbol;
	}
	
	public boolean isCompleteSymbol() {
		return this.sequence != null && this.currentSymbol >= this.sequence.getSymbols().size() - 1;
	}
}
