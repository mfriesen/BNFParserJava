package ca.gobits.bnf.parser;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Map;

import org.junit.Test;

public class PropertyParserTest {

	private PropertyParser parser = new PropertyParser();
	
	@Test
	public void testJson() throws Exception {
		// given
		InputStream is = getClass().getResourceAsStream("/json.bnf");

		// when
		Map<String, String> map = parser.parse(is);
		
		// then
		assertEquals("propertyName colon value;", map.get("property"));
	}

}
