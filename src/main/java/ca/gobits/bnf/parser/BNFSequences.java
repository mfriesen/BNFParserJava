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

public class BNFSequences
{
    private List<BNFSequence> sequences;
    
    public BNFSequences(List<BNFSequence> sequences)
    {        
        this.sequences = sequences;
    }
    
    public List<BNFSequence> getSequences()
    {
        return sequences;
    }

    public void setPipeLines(List<BNFSequence> sequences)
    {
        this.sequences = sequences;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        for (BNFSequence lines : sequences)
        {
            sb.append(lines.toString());
        }
        
        return sb.toString();
    }
}
