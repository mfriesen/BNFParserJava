package ca.gobits.bnf.parser;

import java.util.Map;

public interface BNFStateDefinitionFactory {
	Map<String, BNFStateDefinition> json();
}
