package ca.gobits.bnf.parser;

import java.util.Map;
import java.util.Stack;

import ca.gobits.bnf.parser.states.BNFState;
import ca.gobits.bnf.parser.states.BNFStateEmpty;
import ca.gobits.bnf.parser.states.BNFState.BNFRepetition;
import ca.gobits.bnf.tokenizer.BNFToken;

public class BNFParserImpl implements BNFParser {

	private Map<String, BNFStateDefinition> stateDefinitions;
	private Stack<BNFPath> stack = new Stack<BNFPath>();
	
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
		pushToStack(token, sd, BNFRepetition.NONE);
	
		while (!stack.isEmpty()) {
			
			BNFPath sp = stack.peek();
			
			if (sp.isStateEnd()) {
				
				sp = stack.pop();
				
				if (isEmpty(sp.getToken())) {
					result.setSuccess(true);
					break;
				}
			} else if (sp.isStateDefinition()) {
				
				if (!parseStateDefinition(sp.getToken())) {
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
			pushToStack(state, token, state.getRepetition());
		}
		
		return success;
	}

	private void pushToStack(BNFState state, BNFToken token, BNFRepetition repetition) {
		
		if (state != null) {
			System.out.println ("ADDING NEXT STATE " + state.getName() + " setting token " + token + " repetition " + repetition);
			BNFPathState path = new BNFPathState(state, token);
			path.setRepetition(repetition != null ? repetition : state.getRepetition());
			stack.push(path);
		}
	}
	
	private void pushToStack(BNFToken token, BNFStateDefinition sd, BNFRepetition repetition) {		
		
		if (sd.hasSequences()) {
			BNFPathStateDefinition path = new BNFPathStateDefinition();
			path.setToken(token);
			path.setStateDefinition(sd);
			path.setRepetition(repetition);
			stack.push(path);
		} else {
			pushToStack(sd.getFirstState(), token, repetition);
		}
	}
	
	private void parseState(BNFParseResultImpl result) {
		
		BNFPathState sp = (BNFPathState) stack.peek();
		BNFState state = sp.getState();
		BNFToken token = sp.getToken();
		BNFRepetition repetition = sp.getRepetition();

		if (!state.isTerminal()) {
			
			BNFStateDefinition sd = stateDefinitions.get(state.getName());
			
			if (sd == null) {
				throw new RuntimeException("unknown state " + state.getName());
			}
			
			pushToStack(token, sd, repetition);
			
		} else if (state.getClass().equals(BNFStateEmpty.class)) {
			
//			BNFState rewindState = rewindStackToNextState();
			BNFState rewindState = rewindStackToNextStateOrStateDefinitionWithNextSequence();
//			if (rewindState.isEnd()) {
//				stack.pop();
//			}
			
			pushToStack(rewindState, token, null);
			
		} else if (state.match(token)) {

			System.out.println ("FOUND MATCH " + state.getName() + " " + token.getValue());

			if (state.matchAdvancedToNextToken(token)) {
				token = token.getNextToken();
				result.setMaxMatchToken(token);
				
				BNFState rewindState = rewindStackToNextState();
				pushToStack(rewindState, token, null);
				
			} else {
				
				BNFState rewindState = rewindStackToNextStateOrStateDefinition();
				pushToStack(rewindState, token, null);
			}
			

		} else if (repetition == BNFRepetition.ZERO_OR_MORE) {
			
			rewindStackToNextRepetition(repetition);			
			
			BNFState rewindState = rewindStackToNextState();
			pushToStack(rewindState, token, null);
			
		} else {
			
			rewindStackToNextPath();
		}		
	}

	private void rewindStackToNextPath() {
		while (!stack.isEmpty()) {
			BNFPath sp = stack.peek();
			
			if (!sp.isStateDefinition()) {
				BNFPathState spState = (BNFPathState) stack.pop();
				System.out.println ("REWIND1 " + spState.getState().getName());
			} else {
				break;
			}
		}
	}

	private void rewindStackToNextRepetition(BNFRepetition repetition) {
		
		while (!stack.isEmpty()) {
			
			BNFPath sp = stack.peek();
			
			if (repetition == sp.getRepetition()) {
				stack.pop();
				System.out.println ("REWIND3 " + sp.toString());
			} else {
				break;
			}			
		}
	}
	
	private BNFState rewindStackToNextState() {
		
		BNFState nextState = null;
		
		while (!stack.isEmpty()) {
			
			BNFPath sp = stack.peek();	
			
			if (!sp.isStateDefinition()) {
				
				BNFPathState bps = (BNFPathState) stack.pop();
				nextState = bps.getState().getNextState();			
			
				System.out.println ("REWIND2 " + bps.getState().getName());
				if (nextState != null) {
					break;
				}
			} else {
				System.out.println ("REWIND2 " + sp.toString());
				stack.pop();
			}
		}
		
		return nextState;
	}
	
	@Deprecated
	private BNFState rewindStackToNextStateOrStateDefinition() {
		
		BNFState nextState = null;
		
		while (!stack.isEmpty()) {
			
			BNFPath sp = stack.peek();
			
			if (!sp.isStateDefinition()) {
				
				BNFPathState bps = (BNFPathState) stack.pop();
				nextState = bps.getState().getNextState();			
			
				System.out.println ("REWIND2 " + bps.getState().getName());
				if (nextState != null) {
					break;
				}
			} else {
				break;
			}
		}
		
		return nextState;
	}

	private BNFState rewindStackToNextStateOrStateDefinitionWithNextSequence() {
		
		BNFState nextState = null;
		
		while (!stack.isEmpty()) {
			
			BNFPath sp = stack.peek();
			
			if (sp.isStateDefinition()) {
				
				BNFPathStateDefinition sd = (BNFPathStateDefinition) stack.pop();
				System.out.println ("NEXT SEQ " + sd.toString());
//				nextState = sd.getNextSequence();
				nextState = sd.getNextState();
				
			} else {
				
				BNFPathState bps = (BNFPathState) stack.pop();
				nextState = bps.getState().getNextState();			
			
				System.out.println ("REWIND4 " + bps.getState().getName());
			}
			
			if (nextState != null) {
				break;
			}

//			nextState = sp.getNextState();
//			
//			if (nextState == null) {
//				sp = stack.pop();
//				System.out.println ("REWIND2 " + sp.toString());
//			} else {
//				break;
//			}
			
//			if (!sp.isStateDefinition()) {
//				
//				BNFPathState bps = (BNFPathState) stack.pop();
//				nextState = bps.getState().getNextState();			
//			
//				System.out.println ("REWIND2 " + bps.getState().getName());
//				if (nextState != null) {
//					break;
//				}
//			} else {
//				break;
//			}
		}
		
		return nextState;
	}
}
