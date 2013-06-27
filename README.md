## BNFParserJava

BNFParserJava is a [Backus-Naur Form](https://en.wikipedia.org/wiki/Backus%E2%80%93Naur_Form) Framework written by Mike Friesen in Java and released under the Apache 2 Open Source License.

BNFParserJava was inspired by the framework [ParseKit](http://parsekit.com/) by Todd Ditchendorf.

The BNFParserJava Framework offers 3 basic services of general interest to developers:

1. String Tokenization via the BNFTokenizerFactory and BNFToken classes

2. Property Key/Value mapper via PropertyParser

3. Text Parsing via Grammars via BNFParser [see grammar syntax](http://parsekit.com/grammars.html)

### PropertyParser

Uses the string tokenizer to parse a string and create key/value mapping based on the '=' symbol.

Example
---------
sample key = sample value

returns a key/value mapping where "sample key" is the key and "sample value" is the value.

Usage
-----
String text = "sample key = sample value";

PropertyParser parser = new PropertyParser();

Map<String, String> keyValueMap = parser.parse(text);

Assert.assertNotNull(keyValueMap.get("sample key"));

### Text Parsing via Gramars

BNFParserJava currently only ships with a JSON grammar so the example are based on that.

Example Valid JSON
-------------------

// Create String Tokens

String text = "{ \"key\":\"value\"}";

BNFTokenizerFactory tokenizerFactory = new BNFTokenizerFactoryImpl();

BNFToken token = tokenizerFactory.tokens(text);

// Create Backus-Naur Form State Definitions

BNFStateDefinitionFactoryImpl sdf = new BNFStateDefinitionFactoryImpl();

Map<String, BNFStateDefinition> map = sdf.json();

// Run Tokens through Parser

BNFParser parser = new BNFParserImpl(map);

BNFParseResult result = parser.parse(token);

// Verify results
Assert.assertTrue(result.isSuccess()); // verify text passes grammar

Assert.assertNotNull(result.getTop()); // the "first" token, same as the token returned from the tokenizer factory

Assert.assertNull(result.getError());  // the "first" error token, this token and any afterwards are considered to not have passed the grammar