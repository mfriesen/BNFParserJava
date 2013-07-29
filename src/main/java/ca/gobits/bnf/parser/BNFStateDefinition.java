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

import java.util.List;

import ca.gobits.bnf.parser.states.BNFState;


public class BNFStateDefinition {

	private String name;
	private List<BNFState> states;
	
	public BNFStateDefinition() {		
	}
	
	public BNFStateDefinition(String name, List<BNFState> states) {
		this.name = name;
		this.states = states;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<BNFState> getStates() {
		return states;
	}

	public void setStates(List<BNFState> states) {
		this.states = states;
	}
	
	public boolean hasSequences() {
		return states.size() > 1;
	}
	
	public BNFState getFirstState() {
		
		BNFState state = null;
		
		if (!states.isEmpty()) {
			state = states.get(0);
		}
		
		return state;
	}
}
