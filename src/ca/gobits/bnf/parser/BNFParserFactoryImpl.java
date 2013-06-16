package ca.gobits.bnf.parser;

import java.util.Map;

public class BNFParserFactoryImpl implements BNFParserFactory {

	private BNFStateDefinitionFactory df = new BNFStateDefinitionFactoryImpl();
	
	@Override
	public BNFParser json() {
		
		Map<String, BNFStateDefinition> map = df.json();
		BNFParser parser = new BNFParser(map);
		
		return parser;
	}

}
