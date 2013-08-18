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

package ca.gobits.bnf.tokenizer;

public class BNFTokenizerParams {

    private boolean includeWhitespace;
    private boolean includeWhitespaceOther;
    private boolean includeWhitespaceNewlines;

    public BNFTokenizerParams() {
    }

    public boolean isIncludeWhitespace() {
        return this.includeWhitespace;
    }

    public void setIncludeWhitespace(boolean includeWhitespace) {
        this.includeWhitespace = includeWhitespace;
    }

    public boolean isIncludeWhitespaceOther() {
        return this.includeWhitespaceOther;
    }

    public void setIncludeWhitespaceOther(boolean includeWhitespaceOther) {
        this.includeWhitespaceOther = includeWhitespaceOther;
    }

    public boolean isIncludeWhitespaceNewlines() {
        return this.includeWhitespaceNewlines;
    }

    public void setIncludeWhitespaceNewlines(boolean includeWhitespaceNewlines) {
        this.includeWhitespaceNewlines = includeWhitespaceNewlines;
    }
}
