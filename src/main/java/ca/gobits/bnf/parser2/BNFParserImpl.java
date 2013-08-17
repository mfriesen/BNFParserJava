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
        addPipeLine(sd, token, BNFParserRepetition.NONE);

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
            else if (holder.getState() == HolderState.MATCH || holder.getState() == HolderState.MATCH_NO_TOKEN_ADVANCE)
            {
            	maxMatchToken = processMatch(holder.getState());
            	errorToken = null;
                success = true;
            }
            else if (holder.getState() == HolderState.NO_MATCH)
            {   
                errorToken = processNoMatch();
//                System.out.println ("ERROR " + errorToken);
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
    
	/**
	 * Rewind stack to next symbol
	 */
	private BNFToken processMatch(HolderState state) {
		
        stack.pop();
	        
        BNFToken token = stack.peek().getCurrentToken();

		debugPrintIndents();
		System.out.println ("-> matched token " + token.getStringValue() + " rewind to next symbol");
				
		rewindToNextSymbol();
		
		if (!stack.isEmpty()) {
			BNFParserState holder = stack.peek();
			
			if (holder.getRepetition() == BNFParserRepetition.ZERO_OR_MORE_LOOKING_FOR_FIRST_MATCH) {
				holder.setRepetition(BNFParserRepetition.ZERO_OR_MORE);
			}
			
			if (state == HolderState.MATCH) {
				token = token.getNextToken();
			}
			
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
            if (holder.isSequence() && !holder.isComplete() && holder.getRepetition() != repetition) 
            {
                break;
            }

            stack.pop();
        }
    }
    
    private void rewindToStartOfRepetition()
    {
        BNFParserState startOfRepetition = null;
        
        while (!stack.isEmpty())
        {
            BNFParserState holder = stack.peek();
            
            if (holder.getRepetition() != BNFParserRepetition.NONE) 
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
                    addPipeLine(pipeLine, holder.getCurrentToken(), holder.getRepetition());
                }
            }
            else if (holder.isSequence())
            {
//                BNFSequence sequence = holder.getPipeLine();
                BNFSymbol pipe = holder.getNextSymbol();
                String nextPipe = pipe.getName();      
                BNFSequences sd = stateDefinitions.get(nextPipe);
             
                BNFParserRepetition repetition = getRepetition(holder, pipe);
                
                if (sd != null)
                {
                    addPipeLine(sd, holder.getCurrentToken(), repetition);
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
                		
                		addPipeLine(HolderState.MATCH_NO_TOKEN_ADVANCE);
                		
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
        String s = isQuotedString(nextPipe) ? nextPipe.substring(1, nextPipe.length() - 1) : nextPipe;
        return s.equals(token.getStringValue()) || isQuotedString(nextPipe, token) || isNumber(nextPipe, token);
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
    
    private void addPipeLine(BNFSequences sd, BNFToken token, BNFParserRepetition repetition)
    {
        if (sd.getSequences().size() == 1)
        {
            addPipeLine(sd.getSequences().get(0), token, repetition);
        }
        else
        {
            debug(sd, token, repetition);
            stack.push(new BNFParserState(sd, token, repetition));
        }
    }

    private void addPipeLine(BNFSequence pipeLine, BNFToken token, BNFParserRepetition repetition)
    {
        debug(pipeLine, token, repetition);
        stack.push(new BNFParserState(pipeLine, token, repetition));        
    }

    private BNFParserRepetition getRepetition(BNFParserState holder, BNFSymbol symbol) {
        
        BNFRepetition symbolRepetition = symbol.getRepetition();
        BNFParserRepetition holderRepetition = holder.getRepetition();
        
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
    
    private void debug(BNFSequence pipeLine, BNFToken token, BNFParserRepetition repetition)
    {
        debugPrintIndents();        
        System.out.println ("-> procesing pipe line " + pipeLine + " for token " + token.getStringValue() + " with repetition " + repetition);
    }

    private void debug(BNFSequences sd, BNFToken token, BNFParserRepetition repetition)
    {
        debugPrintIndents();        
        System.out.println ("-> adding pipe lines " + sd.getSequences() + " for token " + token.getStringValue() + " with repetition " + repetition);
    }
}



/*
@start        = Empty | array | object;

object        = openCurly objectContent closeCurly;
objectContent = Empty | actualObject;
actualObject  = property commaProperty*;
property      = propertyName colon value;
commaProperty = comma property;
propertyName  = QuotedString;

array         = openBracket arrayContent closeBracket;
arrayContent  = Empty | actualArray;
actualArray   = value commaValue*;
commaValue    = comma value;

value         = null | true | false | array | object | number | string;

string        = QuotedString;
number        = Number;
null          = 'null';
true          = 'true';
false         = 'false';

openCurly     = '{';
closeCurly    = '}';
openBracket   = '[';
closeBracket  = ']';
comma         = ',';
colon         = ':';
*/