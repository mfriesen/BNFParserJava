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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ca.gobits.bnf.parser.states.BNFState;
import ca.gobits.bnf.parser.states.BNFState.BNFRepetition;
import ca.gobits.bnf.parser.states.BNFStateEmpty;
import ca.gobits.bnf.parser.states.BNFStateEnd;
import ca.gobits.bnf.parser.states.BNFStateNumber;
import ca.gobits.bnf.parser.states.BNFStateQuotedString;
import ca.gobits.bnf.parser.states.BNFStateTerminal;

public class BNFStateDefinitionFactoryImpl implements BNFStateDefinitionFactory {

	@Override
	public Map<String, BNFStateDefinition> json() {
		
		Map<String, BNFStateDefinition> map = new HashMap<String, BNFStateDefinition>();
		
		Properties prop = jsonProperties();
		for (Map.Entry<Object, Object> e : prop.entrySet()) {
			String name = e.getKey().toString();
			
			String value = e.getValue().toString();

			String[] values = value.split("[|]");

			List<BNFState> states = createStates(name, values);
			sort(states);
			
			map.put(name, new BNFStateDefinition(name, states));
		}
	
		return map;
	}	
	
	private void sort(List<BNFState> states) {
		Collections.sort(states, new Comparator<BNFState>() {
			@Override
			public int compare(BNFState o1, BNFState o2) {
				if (o1.getClass().equals(BNFStateEmpty.class)) {
					return 1;
				} else if (o2.getClass().equals(BNFStateEmpty.class)) {
					return -1;
				}
				return 0;
			}
		});
	}

	private Properties jsonProperties() {
		InputStream in = getClass().getResourceAsStream("/ca/gobits/bnf/parser/json.bnf");
		Properties p = new Properties();
		try {
			p.load(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return p;
	}
	
	private List<BNFState> createStates(String name, String[] states)
	{
		List<BNFState> c = new ArrayList<BNFState>(states.length);
		
		for (String s : states) {

			int pos = 0;
			BNFState firstState = null;
			BNFState previousState = null;
			String[] split = s.trim().split(" ");

			for (String ss : split) {

				BNFState state = createState(ss);
				state.setPosition(pos);
				
				if (firstState == null) {
					firstState = state;
				} 
				
				if (previousState != null) {
					previousState.setNextState(state);
				}
				
				previousState = state;
				pos++;
			}

			if (previousState != null && name.equals("@start")) {
				previousState.setNextState(new BNFStateEnd());
			}
			
			c.add(firstState);
		}
		
		return c;
	}
	
	private BNFState createState(String ss) {
		boolean isTerminal = isTerminal(ss);
		String name = fixQuotedString(ss);
		BNFRepetition repetition = BNFRepetition.NONE;
		
		if (name.endsWith("*")) {
			repetition = BNFRepetition.ZERO_OR_MORE;
			name = name.substring(0, name.length() - 1);
		}
		
		BNFState state = createStateInstance(name, isTerminal);
		state.setName(name);
		state.setRepetition(repetition);
		
		return state;
	}

	private BNFState createStateInstance(String ss, boolean terminal) {
		BNFState state = null;

		if (terminal) {
			state = new BNFStateTerminal();
		} else if (ss.equals("Number")) {
			state = new BNFStateNumber();
		} else if (ss.equals("QuotedString")) {
			state = new BNFStateQuotedString();
		} else if (ss.equals("Empty")) {
			state = new BNFStateEmpty();
		} else {
			state = new BNFState();
		}
		
		return state;
	}

	private boolean isTerminal(String ss) {
		return ss.startsWith("'") || ss.startsWith("\"");
	}
	
	private String fixQuotedString(String ss) {

		int len = ss.length();
		int start = ss.startsWith("'") ? 1 : 0;
		int end = ss.endsWith(";") ? len - 1 : len;
		end = ss.endsWith("';") ? len - 2 : end;
		
		if (start > 0 || end < len) {
			ss = ss.substring(start, end);
		}
		
		return ss;
	}
}
