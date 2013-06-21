package ca.gobits.bnf.parser;

import java.util.Map;

import ca.gobits.bnf.parser.states.BNFState;
import ca.gobits.bnf.parser.states.BNFStateEmpty;
import ca.gobits.bnf.tokenizer.BNFToken;

public class BNFParserImpl implements BNFParser {

	private Map<String, BNFStateDefinition> stateDefinitions;
	private BNFStack stack = new BNFStack();
	
	public BNFParserImpl(Map<String, BNFStateDefinition> stateDefinitions) {
		this.stateDefinitions = stateDefinitions; 
	}
	
	@Override
	public BNFParseResult parse(BNFToken token) {
		
		stack.clear();
		
		BNFParseResultImpl result = new BNFParseResultImpl();
		result.setTop(token);
		result.setMaxMatchToken(token);
		
		BNFStateDefinition sd = stateDefinitions.get("@start");
		pushToStackOrFirstState(token, sd);
	
		while (!stack.isEmpty()) {
			
			BNFPath sp = stack.peek();
			
			if (sp.isStateEnd()) {
				
				sp = stack.pop();
				
				if (isEmpty(sp.getToken())) {
					result.setSuccess(true);
					break;
				}
			} else if (sp.isStateDefinition()) {
				
				boolean added = parseStateDefinition(sp.getToken());
				if (!added) {
					result.setSuccess(false);
					break;
				}
				
			} else {
				parseState(result);
			}
		}
		
		result.complete();
		
		return result;
	}
	
	private boolean isEmpty(BNFToken token) {
		return token == null || token.getValue() == null || token.getValue().trim().length() == 0;
	}
	
	private boolean parseStateDefinition(BNFToken token) {
		boolean success = false;
		BNFPathStateDefinition sd = (BNFPathStateDefinition) stack.peek();
		BNFState state = sd.getNextSequence();
		if (state != null) {
			success = true;
			pushToStack(state, token);
		}
		
		return success;
	}

	private void pushToStack(BNFState state, BNFToken token) {
		
		if (state != null) {
			System.out.println ("ADDING NEXT STATE " + state.getName() + " setting token " + token);
			BNFPathState path = new BNFPathState(state, token);
			stack.push(path);
		}
	}
	
	private BNFPathStateDefinition pushToStack(BNFToken token, BNFStateDefinition sd) {
		BNFPathStateDefinition path = new BNFPathStateDefinition();
		path.setToken(token);
		path.setStateDefinition(sd);
		stack.push(path);
		
		return path;
	}
	
	private void pushToStackOrFirstState(BNFToken token, BNFStateDefinition sd) {		
		
		if (sd.hasSequences()) {
			pushToStack(token, sd);
		} else {
			pushToStack(sd.getFirstState(), token);
		}
	}
	
	private void parseState(BNFParseResultImpl result) {
		
		BNFPathState sp = (BNFPathState) stack.peek();
		BNFState state = sp.getState();
		BNFToken token = sp.getToken();

		if (!state.isTerminal()) {
			
			BNFStateDefinition sd = stateDefinitions.get(state.getName());
			
			if (sd == null) {
				throw new RuntimeException("unknown state " + state.getName());
			}
			
			pushToStackOrFirstState(token, sd);
			
		} else if (state.getClass().equals(BNFStateEmpty.class)) {
			
			BNFState rewindState = stack.rewindStackEmptyState();
			pushToStack(rewindState, token);
			
		} else if (state.match(token)) {

			System.out.println ("FOUND MATCH " + state.getName() + " " + token.getValue());

			token = token.getNextToken();
			result.setMaxMatchToken(token);
			
			BNFState rewindState = stack.rewindStackMatchedToken();
			pushToStack(rewindState, token);
							
		} else {
			
			BNFState nextState = stack.rewindStackUnmatchedToken();
			pushToStack(nextState, token);
		}		
	}
}