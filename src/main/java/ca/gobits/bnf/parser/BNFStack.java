//
// Copyright 2013 Mike Friesen
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package ca.gobits.bnf.parser;

import java.util.ArrayDeque;

import ca.gobits.bnf.parser.states.BNFState;
import ca.gobits.bnf.parser.states.BNFState.BNFRepetition;

public class BNFStack extends ArrayDeque<BNFPath> {

	private static final long serialVersionUID = -4499604601446944271L;

	public void push(BNFPathState path) {
		super.push(path);
	}
	
	/**
	 * Rewinds to the next Token with next sequence
	 */
	public void rewindToNextTokenAndNextSequence() {
		
		while (!isEmpty()) {
			
			BNFPath sp = peek();
			
			if (sp.isStateDefinition()) {
				
				BNFPathStateDefinition spd = (BNFPathStateDefinition) sp;
				
				if (sp.getToken() != null && spd.hasNextSequence()) {
					break;
				} else {
					pop();
				}
				
			} else {
				pop();
			}
		}
	}
	
	/**
	 * Rewinds to next state or next Repetition
	 */
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
				
				if (nextState != null) {
					break;
				}
								
			} else {
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
				
				if (foundRepetition && state.getNextState() != null) {
					nextState = state.getNextState();
					break;
				}
				
			} else {
				
				BNFPathStateDefinition spd = (BNFPathStateDefinition) sp;
				if (foundRepetition) {
				    sp = pop();
				} else if (spd.hasNextSequence()) {
					break;
				} else {
					pop();
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
				nextState = sd.getNextState();
				
			} else {
				
				BNFPathState bps = (BNFPathState) pop();
				nextState = bps.getState().getNextState();						
			}
			
			if (nextState != null) {
				break;
			}
		}
		
		return nextState;
	}
}
