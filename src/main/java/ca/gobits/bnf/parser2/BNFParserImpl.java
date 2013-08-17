package ca.gobits.bnf.parser2;

import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import ca.gobits.bnf.parser.BNFParseResult;
import ca.gobits.bnf.parser.BNFParseResultImpl;
import ca.gobits.bnf.parser.BNFParser;
import ca.gobits.bnf.parser2.BNFParserState.BNFParserRepetition;
import ca.gobits.bnf.parser2.BNFParserState.HolderState;
import ca.gobits.bnf.parser2.BNFSymbol.BNFRepetition;
import ca.gobits.bnf.tokenizer.BNFToken;

public class BNFParserImpl implements BNFParser
{
    private Pattern numberPattern = Pattern.compile("^[\\d\\-\\.]+$");
    
    private Map<String, BNFSequences> stateDefinitions;
    private Stack<BNFParserState> stack = new Stack<BNFParserState>();
    
    public BNFParserImpl(Map<String, BNFSequences> stateDefinitions) 
    {
        this.stateDefinitions = stateDefinitions; 
    }
    
    @Override
    public BNFParseResult parse(BNFToken token)
    {        
        BNFSequences sd = stateDefinitions.get("@start");
        addPipeLine(sd, token, BNFParserRepetition.NONE, BNFRepetition.NONE);

        return parseSequences(token);
    }

    private BNFParseResultImpl parseSequences(BNFToken startToken)
    {
    	boolean success = false;
    	BNFToken maxMatchToken = startToken;
    	BNFToken errorToken = null;
    	
        BNFParseResultImpl result = new BNFParseResultImpl();
        result.setTop(startToken);

        while (!stack.isEmpty()) 
        {            
            BNFParserState holder = stack.peek();
                   
            if (holder.getState() == HolderState.EMPTY) {
            	
            	stack.pop();
            	BNFToken token = stack.peek().getCurrentToken();
            	if (!isEmpty(token)) {
//            		rewindToNextSequence();
            		rewindToNextSymbol();
            	} else {
            		success = true;
            		errorToken = null;
            		rewindToNextSequence();
            	}
            } 
            else if (holder.getState() == HolderState.NO_MATCH_WITH_ZERO_REPETITION)
            {
                processNoMatchWithZeroRepetition();
            }
            else if (holder.getState() == HolderState.MATCH_WITH_ZERO_REPETITION)
            {
                processMatchWithZeroRepetition();
            }
            else if (holder.getState() == HolderState.NO_MATCH_WITH_ZERO_REPETITION_LOOKING_FOR_FIRST_MATCH) {
            	maxMatchToken = processNoMatchWithZeroRepetitionLookingForFirstMatch();
            	errorToken = null;
                success = true;            	
            }
            else if (holder.getState() == HolderState.MATCH)
            {
            	maxMatchToken = processMatch();
            	errorToken = null;
                success = true;
            }
            else if (holder.getState() == HolderState.NO_MATCH)
            {   
                BNFToken eToken = processNoMatch();
                errorToken = updateErrorToken(errorToken, eToken);
                success = false;
            }
            else
            {
                processStack();
            }
        }
        
        result.setError(errorToken);
        result.setMaxMatchToken(maxMatchToken);
        result.setSuccess(success);
        
        return result;
    }   

    /**
     * Returns The BNFToken with the largest ID
     * @return BNFToken
     */
    private BNFToken updateErrorToken(BNFToken token1, BNFToken token2) {
    	return token1 != null && token1.getId() > token2.getId() ? token1 : token2;
    }
	/**
     * Rewind stack to the next sequence
     */
	private BNFToken processNoMatch() {
		
		debugPrintIndents();
		System.out.println ("-> no match, rewinding to next sequence");
		
		stack.pop();
		
		BNFToken token = stack.peek().getCurrentToken();

		rewindToNextSequence();
		
		if (!stack.isEmpty()) {
			BNFParserState holder = stack.peek();
			holder.resetToken();
		}
		
		return token;
	}

    private BNFToken processMatchWithZeroRepetition()
    {
        stack.pop();
        
        BNFToken token = stack.peek().getCurrentToken();

        debugPrintIndents();
        System.out.println ("-> matched token " + token.getStringValue() + " rewind to start of repetition");
        
        rewindToStartOfRepetition();
        
        if (!stack.isEmpty()) {
            BNFParserState holder = stack.peek();
            holder.advanceToken(token.getNextToken());
        }
        
        return token;
    }
    
    private BNFToken processNoMatchWithZeroRepetitionLookingForFirstMatch() {
    	
        stack.pop();
        
        BNFToken token = stack.peek().getCurrentToken();

		debugPrintIndents();
		System.out.println ("-> no match Zero Or More Looking for First Match token " + debug(token) + " rewind outside of Repetition");				
		
		rewindToOutsideOfRepetition();
		rewindToNextSymbol();
		
		if (!stack.isEmpty()) {
			BNFParserState holder = stack.peek();

			// TODO REMOVE?
//			if (holder.getRepetition() == BNFParserRepetition.ZERO_OR_MORE_LOOKING_FOR_FIRST_MATCH) {
//				holder.setRepetition(BNFParserRepetition.NONE);
//			}
//			
			holder.advanceToken(token);
		}
		
		return token;
	}

	/**
	 * Rewind stack to next symbol
	 */
	private BNFToken processMatch() {
		
        stack.pop();
	        
        BNFToken token = stack.peek().getCurrentToken();

		debugPrintIndents();
		System.out.println ("-> matched token " + token.getStringValue() + " rewind to next symbol");
				
		rewindToNextSymbolOrRepetition();
		
		if (!stack.isEmpty()) {
			BNFParserState holder = stack.peek(); 
			
			token = token.getNextToken();			
			holder.advanceToken(token);
		}
		
		return token;
	}

	private void processNoMatchWithZeroRepetition() {
		
		debugPrintIndents();
		System.out.println ("-> " + HolderState.NO_MATCH_WITH_ZERO_REPETITION + ", rewind to next symbol");
		
		stack.pop();
		
		BNFToken token = stack.peek().getCurrentToken();
		
//		rewindToNextSequence();
		rewindToNextSymbol(BNFParserRepetition.ZERO_OR_MORE);
		
		if (!stack.isEmpty()) {
			BNFParserState holder = stack.peek();
			holder.advanceToken(token);
		}
	}
    
    private void rewindToNextSymbol(BNFParserRepetition repetition)
    {
        while (!stack.isEmpty())
        {
            BNFParserState holder = stack.peek();
            if (holder.isSequence() && !holder.isComplete() && holder.getParserRepetition() != repetition) 
            {
                break;
            }

            stack.pop();
        }
    }
    
	private void rewindToOutsideOfRepetition() {
		
        while (!stack.isEmpty())
        {
            BNFParserState holder = stack.peek();
            
            if (holder.getParserRepetition() != BNFParserRepetition.NONE) 
            {
                stack.pop();
            } else {
                break;
            }
        }
	}
	
    private void rewindToStartOfRepetition()
    {
        BNFParserState startOfRepetition = null;
        
        while (!stack.isEmpty())
        {
            BNFParserState holder = stack.peek();
            
            if (holder.getParserRepetition() != BNFParserRepetition.NONE) 
            {
                startOfRepetition = holder;
                stack.pop();
            } else {
                break;
            }
        }
        
        if (startOfRepetition != null) {
            this.stack.push(startOfRepetition);
        }
    }
    
    /**
     * Rewinds to next incomplete sequence or to ZERO_OR_MORE repetition which ever one is first
     */
    private void rewindToNextSymbolOrRepetition()
    {
        while (!stack.isEmpty())
        {
            BNFParserState holder = stack.peek();
                        
            if (holder.getRepetition() == BNFRepetition.ZERO_OR_MORE && holder.isComplete()) {
            	holder.reset();
            	if (holder.getRepetition() != BNFRepetition.NONE) {
            		holder.setParserRepetition(BNFParserRepetition.ZERO_OR_MORE_LOOKING_FOR_FIRST_MATCH);
            	}
            	break;
            }
            else 
            	if (holder.isSequence() && !holder.isComplete()) 
            {
                	if (holder.getParserRepetition() == BNFParserRepetition.ZERO_OR_MORE_LOOKING_FOR_FIRST_MATCH) {
        				holder.setParserRepetition(BNFParserRepetition.NONE);
        			}

                break;
            }

            stack.pop();
        }
    }

    /**
     * Rewinds to next incomplete sequence or to ZERO_OR_MORE repetition which ever one is first
     */
    private void rewindToNextSymbol()
    {
        while (!stack.isEmpty())
        {
            BNFParserState holder = stack.peek();
                        
            if (holder.isSequence() && !holder.isComplete()) 
            {
                break;
            }

            stack.pop();
        }
    }

    private void rewindToNextSequence()
    {
        while (!stack.isEmpty())
        {
            BNFParserState holder = stack.peek();
            if (holder.isSequences()) 
            {
                break;
            }

            stack.pop();
        }
    }
    
    private void processStack()
    {
        BNFParserState holder = stack.peek();
        
        if (holder.isComplete())
        {
            stack.pop();
        }
        else
        {
            if (holder.isSequences())
            {
                if (holder.isComplete())
                {
                    stack.pop();
                    
                } else {
                    
                    BNFSequence pipeLine = holder.getNextSequence();
                    addPipeLine(pipeLine, holder.getCurrentToken(), holder.getParserRepetition(), BNFRepetition.NONE);
                }
            }
            else if (holder.isSequence())
            {
//                BNFSequence sequence = holder.getPipeLine();
                BNFSymbol pipe = holder.getNextSymbol();
                String nextPipe = pipe.getName();      
                BNFSequences sd = stateDefinitions.get(nextPipe);
             
                BNFParserRepetition repetition = getParserRepetition(holder, pipe);
                
                if (sd != null)
                {
                    addPipeLine(sd, holder.getCurrentToken(), repetition, pipe.getRepetition());
                }
                else
                {
                	if (nextPipe.equals("Empty")) {
                		addPipeLine(HolderState.EMPTY);
                	}
                	else if (isMatch(nextPipe, holder.getCurrentToken()))
                    {
//                	    if (repetition != BNFRepetition.NONE && holder.isComplete()) {
//                	        addPipeLine(HolderState.MATCH_WITH_ZERO_REPETITION);
//                	    } else {
//                        System.out.println("MATCHED COMPARING " + nextPipe + " WITH TOKEN " + holder.getCurrentToken().getStringValue());
                	        addPipeLine(HolderState.MATCH);
//                	    }
                    }
                	else if (repetition == BNFParserRepetition.ZERO_OR_MORE_LOOKING_FOR_FIRST_MATCH) {
                		
                		addPipeLine(HolderState.NO_MATCH_WITH_ZERO_REPETITION_LOOKING_FOR_FIRST_MATCH);
                		
                	} else if (repetition == BNFParserRepetition.ZERO_OR_MORE)
                    {
                        addPipeLine(HolderState.NO_MATCH_WITH_ZERO_REPETITION);
                    }
                    else
                    {
//                        System.out.println("NO MATCH COMPARING " + nextPipe + " WITH TOKEN " + holder.getCurrentToken().getStringValue());
                        addPipeLine(HolderState.NO_MATCH);
                    }
                    
                }
            }
        }
    }

    private boolean isMatch(String nextPipe, BNFToken token)
    {
    	boolean match = false;
    	
    	if (token != null) {
    		String s = isQuotedString(nextPipe) ? nextPipe.substring(1, nextPipe.length() - 1) : nextPipe;
    		match = s.equals(token.getStringValue()) || isQuotedString(nextPipe, token) || isNumber(nextPipe, token);
    	}
    	
    	return match;
    }
    
    private boolean isQuotedString(String value)
    {
        return (value.startsWith("\"") && value.endsWith("\"")) || value.startsWith("'") && value.endsWith("'");
    }
    
    private boolean isQuotedString(String nextPipe, BNFToken token)
    {
        String value = token.getStringValue();
        return nextPipe.equals("QuotedString") && isQuotedString(value);
    }
    
    private boolean isNumber(String nextPipe, BNFToken token)
    {
        boolean match = false;
        
        if (token != null && nextPipe.equals("Number")) {
            String value = token.getStringValue();
            match = numberPattern.matcher(value).matches();
        }
        
        return match;
    }
    
    private void addPipeLine(HolderState state)
    {
        stack.push(new BNFParserState(state));
    }
    
    private void addPipeLine(BNFSequences sd, BNFToken token, BNFParserRepetition parserRepetition, BNFRepetition repetition)
    {
        if (sd.getSequences().size() == 1)
        {
            addPipeLine(sd.getSequences().get(0), token, parserRepetition, repetition);
        }
        else
        {
            debug(sd, token, parserRepetition);
            stack.push(new BNFParserState(sd, token, parserRepetition, repetition));
        }
    }

    private void addPipeLine(BNFSequence pipeLine, BNFToken token, BNFParserRepetition parserRepetition, BNFRepetition repetition)
    {
        debug(pipeLine, token, parserRepetition);
        stack.push(new BNFParserState(pipeLine, token, parserRepetition, repetition));        
    }

    private BNFParserRepetition getParserRepetition(BNFParserState holder, BNFSymbol symbol) {
        
        BNFRepetition symbolRepetition = symbol.getRepetition();
        BNFParserRepetition holderRepetition = holder.getParserRepetition();
        
        if (symbolRepetition != BNFRepetition.NONE && holderRepetition == BNFParserRepetition.NONE)
        {
            holderRepetition = BNFParserRepetition.ZERO_OR_MORE_LOOKING_FOR_FIRST_MATCH;
        } else if (symbolRepetition != BNFRepetition.NONE && holderRepetition != BNFParserRepetition.NONE) {
            holderRepetition = BNFParserRepetition.ZERO_OR_MORE;
        }
        
//        if (repetition == BNFRepetition.NONE)
//        {
//            repetition = holder.getRepetition();
//        }
        
        return holderRepetition;
    }
    
    private boolean isEmpty(BNFToken token) {
        return token == null || token.getStringValue() == null || token.getStringValue().length() == 0;
    }
    
    private void debugPrintIndents()
    {
        int size = this.stack.size() - 1;
        for (int i = 0; i < size; i++)
        {
            System.out.print (" ");
        }
    }
    
    private String debug(BNFToken token) {
    	return token != null ? token.getStringValue() : null;
    }
    
    private void debug(BNFSequence pipeLine, BNFToken token, BNFParserRepetition repetition)
    {
        debugPrintIndents();        
        System.out.println ("-> procesing pipe line " + pipeLine + " for token " + debug(token) + " with repetition " + repetition);
    }

    private void debug(BNFSequences sd, BNFToken token, BNFParserRepetition repetition)
    {
        debugPrintIndents();        
        System.out.println ("-> adding pipe lines " + sd.getSequences() + " for token " + debug(token) + " with repetition " + repetition);
    }
}