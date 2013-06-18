//
//  Copyright (c) 2013 Mike Friesen
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package ca.gobits.bnf.parser;

import java.util.Map;
import java.util.Stack;

import ca.gobits.bnf.tokenizer.BNFToken;

public class BNFParser {
	
	private Map<String, BNFStateDefinition> stateDefinitions;
	private Stack<BNFPath> stack = new Stack<BNFPath>();
	
	public BNFParser(Map<String, BNFStateDefinition> stateDefinitions) {
		this.stateDefinitions = stateDefinitions; 
	}
	
	public BNFParserResult parser(BNFToken token) {
		
		stack.clear();
		
		BNFParserResult result = new BNFParserResult();
		result.setTop(token);
		result.setMaxMatchToken(token);
		
		BNFStateDefinition sd = stateDefinitions.get("@start");
		pushToStack(token, sd);
	
		while (!stack.isEmpty()) {		
			BNFPath sp = stack.peek();
			
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
		stack.push(new BNFPath(state, token));
	}
	
	private void pushToStack(BNFToken token, BNFStateDefinition sd) {		
		
		int i = sd.getStates().size();
		for (BNFState state : sd.getStates()) {

			BNFPath statePath = new BNFPath(state, token);
			statePath.setPathCount(sd.getStates().size());
			statePath.setPathPosition(i);
			System.out.println ("pushing " + state.getName() + " " + token.getValue() + " PATH: " + statePath.getPathPosition() + " " + statePath.getPathCount());
			stack.push(statePath);
			i--;
		}
	}
	
	private void parse(BNFParserResult result) {
		
		BNFPath sp = stack.peek();
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
			BNFPath sp = stack.peek();
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
			BNFPath sp = stack.pop();
			nextState = sp.getState().getNextState();			
			
			System.out.println ("REWIND2 " + sp.getState().getName());
			if (nextState != null) {
				break;
			}		
		}
		
		return nextState;
	}
}
