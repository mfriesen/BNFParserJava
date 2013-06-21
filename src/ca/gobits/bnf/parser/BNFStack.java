package ca.gobits.bnf.parser;

import java.util.ArrayDeque;

import ca.gobits.bnf.parser.states.BNFState;
import ca.gobits.bnf.parser.states.BNFState.BNFRepetition;

public class BNFStack extends ArrayDeque<BNFPath> {

	private static final long serialVersionUID = -4499604601446944271L;

	public void push(BNFPathState path) {
		super.push(path);
	}
		
	public BNFState rewindStackMatchedToken() {
		
		BNFState nextState = null;
		
		while (!isEmpty()) {
			
			BNFPath sp = peek();	
			
			if (!sp.isStateDefinition()) {
				
				BNFPathState bps = (BNFPathState) pop();
				BNFState state = bps.getState();
				nextState = state.getNextState();			
			
				if (state.getRepetition() != BNFRepetition.NONE) {
					nextState = state;
				}
				
				System.out.println ("REWIND2 " + state.getName());
				if (nextState != null) {
					break;
				}
								
			} else {
				System.out.println ("REWIND2 " + sp.toString());
				pop();
			}
		}
		
		return nextState;
	}
	
	/**
	 * Rewinds stack to the next sequence, unless we find a repetition, then we'll find to the next state
	 * @return BNFState - null or next state to put on stack
	 */
	public BNFState rewindStackUnmatchedToken() {
		
		BNFState nextState = null;
		boolean foundRepetition = false;
		
		while (!isEmpty()) {
			
			BNFPath sp = peek();
			
			if (!sp.isStateDefinition()) {
				
				BNFPathState ps = (BNFPathState) sp;
				BNFState state = ps.getState();
				
				if (state.getRepetition() != BNFRepetition.NONE) {
					foundRepetition = true;
				}
				
				sp = pop();
				System.out.println ("REWIND1 " + sp);
				
				if (foundRepetition && state.getNextState() != null) {
					nextState = state.getNextState();
					break;
				}
				
			} else {
				
				if (foundRepetition) {
				    sp = pop();
				    System.out.println ("REWIND1 " + sp);
				} else {			
					break;
				}
			}
		}
		
		return nextState;
	}

	public BNFState rewindStackEmptyState() {
		
		BNFState nextState = null;
		
		while (!isEmpty()) {
			
			BNFPath sp = peek();
			
			if (sp.isStateDefinition()) {
				
				BNFPathStateDefinition sd = (BNFPathStateDefinition) pop();
				System.out.println ("NEXT SEQ " + sd.toString());
				nextState = sd.getNextState();
				
			} else {
				
				BNFPathState bps = (BNFPathState) pop();
				nextState = bps.getState().getNextState();			
			
				System.out.println ("REWIND4 " + bps.getState().getName());
			}
			
			if (nextState != null) {
				break;
			}
		}
		
		return nextState;
	}

}
