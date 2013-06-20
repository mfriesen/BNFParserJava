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
			
			BNFState rewindState = rewindStackToNextStateOrStateDefinitionWithNextSequence();
			pushToStack(rewindState, token);
			
		} else if (state.match(token)) {

			System.out.println ("FOUND MATCH " + state.getName() + " " + token.getValue());

			token = token.getNextToken();
			result.setMaxMatchToken(token);
			
			BNFState rewindState = rewindStackToNextStateOrRepetition();
			
			if (rewindState.getRepetition() != BNFRepetition.NONE) {
				//!!!
				rewindStackToStateAboveStateDefinition();
				
				BNFStateDefinition sd = stateDefinitions.get(rewindState.getName());
				BNFPathStateDefinition path = pushToStack(token, sd);
				path.setRewind(true);
				
			} else {				
				pushToStack(rewindState, token);
			}
							
		} else {
			
			rewindStackToNextStateDefinition();
			
			if (stack.peek().isRewind()) {
				BNFState rewindState = rewindStackToNextStateOrRepetition();
				pushToStack(rewindState, token);
			}
		}		
	}

	private void rewindStackToStateAboveStateDefinition() {

		while (!stack.isEmpty()) {
			
			BNFPath sp = stack.pop();
			System.out.println ("REWIND6 " + sp.toString());
			if (sp.isStateDefinition()) {
				break;
			}
		}		
	}

	private void rewindStackToNextStateDefinition() {
		while (!stack.isEmpty()) {
			BNFPath sp = stack.peek();
			
			if (!sp.isStateDefinition()) {
				sp = stack.pop();
				System.out.println ("REWIND1 " + sp);
			} else {
				break;
			}
		}
	}

//	private void rewindStackToNextRepetition(BNFRepetition repetition) {
//		
//		while (!stack.isEmpty()) {
//			
//			BNFPath sp = stack.peek();
//			
//			if (repetition == sp.getRepetition()) {
//				stack.pop();
//				System.out.println ("REWIND3 " + sp.toString());
//			} else {
//				break;
//			}			
//		}
//	}
	
	private BNFState rewindStackToNextStateOrRepetition() {
		
		BNFState nextState = null;
		
		while (!stack.isEmpty()) {
			
			BNFPath sp = stack.peek();	
			
			if (!sp.isStateDefinition()) {
				
				BNFPathState bps = (BNFPathState) stack.pop();
				BNFState state = bps.getState();
				nextState = state.getNextState();			
			
				System.out.println ("REWIND2 " + state.getName());
				if (nextState != null) {
					break;
				}
				
//				if (state.getRepetition() != BNFRepetition.NONE) {
//					nextState = state;
//					break;
//				}
				
			} else {
				System.out.println ("REWIND2 " + sp.toString());
				stack.pop();
			}
		}
		
		return nextState;
	}
	
//	@Deprecated
//	private BNFState rewindStackToNextStateOrStateDefinition() {
//		
//		BNFState nextState = null;
//		
//		while (!stack.isEmpty()) {
//			
//			BNFPath sp = stack.peek();
//			
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
//		}
//		
//		return nextState;
//	}

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
