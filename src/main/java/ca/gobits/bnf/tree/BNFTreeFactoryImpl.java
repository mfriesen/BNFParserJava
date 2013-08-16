package ca.gobits.bnf.tree;

import ca.gobits.bnf.parser.BNFParseResult;

public class BNFTreeFactoryImpl implements BNFTreeFactory {

	@Override
	public BNFTree createTree(BNFParseResult result, String start, String end) {
		return null;
	}

	/*
- (BNFTree *)buildTree:(BNFToken *)token errorPosition:(NSInteger)errorPosition {
    
    BOOL haserror = NO;
    BNFTree *tree = [[[BNFTree alloc] init] autorelease];
    
    Stack *stack = [[Stack alloc] init];
    [stack push:tree];
        
    NSMutableString *ms = [[NSMutableString alloc] init];
    
    while (token) {
        
        BNFTreeNode *top = [stack peek];
        haserror = errorPosition > -1 && errorPosition <= [token identifier];
        NSString *s = [token stringValue];
        
        if ([s hasSuffix:@"["] || [s hasSuffix:@"{"]) {
  
            [ms appendString:s];
            BNFTreeNode *node = [self addTreeNode:top string:[NSString stringWithString:ms] haserror:haserror];
            [stack push:node];
            [ms setString:@""];
        
        } else if ([s isEqualToString:@":"]) {
            
            [ms appendString:@" : "];
            
        } else if ([s hasSuffix:@","]) {
            
            [ms appendString:s];
            [self addTreeNode:top string:[NSString stringWithString:ms] haserror:haserror];
            [ms setString:@""];
            
        } else if ([s hasSuffix:@"]"] || [s hasSuffix:@"}"]) {

            if ([ms length] > 0) {
                [self addTreeNode:top string:[NSString stringWithString:ms] haserror:haserror];
                [ms setString:@""];
            }
            
            [stack pop];
            
            [ms setString:s];
            
            if ([self isNextTokenComma:token]) {
                token = [token nextToken];
                [ms appendString:[token stringValue]];
            }

            [self addTreeNode:[stack peek] string:[NSString stringWithString:ms] haserror:haserror];
            [ms setString:@""];

        } else {
            
            [ms appendString:s];
        }
        
        token = [token nextToken];
    }
    
    if ([ms length] > 0) {
        [self addTreeNode:[stack peek] string:[NSString stringWithString:ms] haserror:haserror];
    }
    
    [stack release];
    [ms release];
    
    return tree;
}
	 */
}
