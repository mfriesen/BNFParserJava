package ca.gobits.bnf.parser;

import java.util.Map;
import java.util.Stack;

import ca.gobits.bnf.parser.states.BNFState;
import ca.gobits.bnf.parser.states.BNFStateEnd;
import ca.gobits.bnf.tokenizer.BNFToken;

public class BNFParserImpl implements BNFParser {

	private Map<String, BNFStateDefinition> stateDefinitions;
	private Stack<BNFParsePath> stack = new Stack<BNFParsePath>();
	
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
		pushToStack(token, sd);
	
		while (!stack.isEmpty()) {		
			BNFParsePath sp = stack.peek();
			
			if (sp.getState().getClass().equals(BNFStateEnd.class)) {
				
				sp = stack.pop();
				
				if (sp.getToken() == null) {
					result.setSuccess(true);
					break;
				}
			}
			
			if (!stack.isEmpty()) {
				parse(result);
			}
		}
		
		result.complete();
		
		return result;
	}
	
	private void pushToStack(BNFState state, BNFToken token) {
		System.out.println ("ADDING NEXT STATE " + state.getName() + " setting token " + token);
		stack.push(new BNFParsePath(state, token));
	}
	
	private void pushToStack(BNFToken token, BNFStateDefinition sd) {		
		
		int i = sd.getStates().size();
		for (BNFState state : sd.getStates()) {

			BNFParsePath statePath = new BNFParsePath(state, token);
			statePath.setPathCount(sd.getStates().size());
			statePath.setPathPosition(i);
			System.out.println ("pushing " + state.getName() + " " + token.getValue() + " PATH: " + statePath.getPathPosition() + " " + statePath.getPathCount());
			stack.push(statePath);
			i--;
		}
	}
	
	private void parse(BNFParseResultImpl result) {
		
		BNFParsePath sp = stack.peek();
		BNFState state = sp.getState();
		BNFToken token = sp.getToken();

		if (state.match(token)) {

			System.out.println ("FOUND MATCH " + state.getName() + " " + token.getValue());

			if (state.matchAdvancedToNextToken(token)) {
				token = token.getNextToken();
				result.setMaxMatchToken(token);
			}
			
			BNFState rewindState = rewindStackToNextState();

			pushToStack(rewindState, token);

		} else if (stateDefinitions.containsKey(state.getName())) {
			
			BNFStateDefinition sd = stateDefinitions.get(state.getName());
			pushToStack(token, sd);
			
		} else {
			
			rewindStackToNextPath();
		}		
	}

	private void rewindStackToNextPath() {
		while (!stack.isEmpty()) {
			BNFParsePath sp = stack.peek();
			if (sp.getPathPosition() < sp.getPathCount()) {
				sp = stack.pop();
				break;
			} else if (sp.getPathPosition() == sp.getPathCount()) {
				sp = stack.pop();
				System.out.println ("REWIND1 " + sp.getState().getName());
			} else {
				break;
			}
		}
	}

	private BNFState rewindStackToNextState() {
		
		BNFState nextState = null;
		
		while (!stack.isEmpty()) {
			BNFParsePath sp = stack.pop();
			nextState = sp.getState().getNextState();			
			
			System.out.println ("REWIND2 " + sp.getState().getName());
			if (nextState != null) {
				break;
			}		
		}
		
		return nextState;
	}
}
