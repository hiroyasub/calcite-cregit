begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|advise
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|parser
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|validate
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|trace
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * An assistant which offers hints and corrections to a partially-formed SQL  * statement. It is used in the SQL editor user-interface.  */
end_comment

begin_class
specifier|public
class|class
name|SqlAdvisor
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|EigenbaseTrace
operator|.
name|parserTracer
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|// Flags indicating precision/scale combinations
specifier|private
specifier|final
name|SqlValidatorWithHints
name|validator
decl_stmt|;
specifier|private
specifier|final
name|String
name|hintToken
init|=
literal|"_suggest_"
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a SqlAdvisor with a validator instance    *    * @param validator Validator    */
specifier|public
name|SqlAdvisor
parameter_list|(
name|SqlValidatorWithHints
name|validator
parameter_list|)
block|{
name|this
operator|.
name|validator
operator|=
name|validator
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Gets completion hints for a partially completed or syntatically incorrect    * sql statement with cursor pointing to the position where completion hints    * are requested.    *    *<p>Writes into<code>replaced[0]</code> the string that is being    * replaced. Includes the cursor and the preceding identifier. For example,    * if<code>sql</code> is "select abc^de from t", sets<code>    * replaced[0]</code> to "abc". If the cursor is in the middle of    * whitespace, the replaced string is empty. The replaced string is never    * null.    *    * @param sql      A partial or syntatically incorrect sql statement for which    *                 to retrieve completion hints    * @param cursor   to indicate the 0-based cursor position in the query at    * @param replaced String which is being replaced (output)    * @return completion hints    */
specifier|public
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|getCompletionHints
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|cursor
parameter_list|,
name|String
index|[]
name|replaced
parameter_list|)
block|{
comment|// search backward starting from current position to find a "word"
name|int
name|wordStart
init|=
name|cursor
decl_stmt|;
name|boolean
name|quoted
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|(
name|wordStart
operator|>
literal|0
operator|)
operator|&&
name|Character
operator|.
name|isJavaIdentifierPart
argument_list|(
name|sql
operator|.
name|charAt
argument_list|(
name|wordStart
operator|-
literal|1
argument_list|)
argument_list|)
condition|)
block|{
operator|--
name|wordStart
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|wordStart
operator|>
literal|0
operator|)
operator|&&
operator|(
name|sql
operator|.
name|charAt
argument_list|(
name|wordStart
operator|-
literal|1
argument_list|)
operator|==
literal|'"'
operator|)
condition|)
block|{
name|quoted
operator|=
literal|true
expr_stmt|;
operator|--
name|wordStart
expr_stmt|;
block|}
if|if
condition|(
name|wordStart
operator|<
literal|0
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|// Search forwards to the end of the word we should remove. Eat up
comment|// trailing double-quote, if any
name|int
name|wordEnd
init|=
name|cursor
decl_stmt|;
while|while
condition|(
operator|(
name|wordEnd
operator|<
name|sql
operator|.
name|length
argument_list|()
operator|)
operator|&&
name|Character
operator|.
name|isJavaIdentifierPart
argument_list|(
name|sql
operator|.
name|charAt
argument_list|(
name|wordEnd
argument_list|)
argument_list|)
condition|)
block|{
operator|++
name|wordEnd
expr_stmt|;
block|}
if|if
condition|(
name|quoted
operator|&&
operator|(
name|wordEnd
operator|<
name|sql
operator|.
name|length
argument_list|()
operator|)
operator|&&
operator|(
name|sql
operator|.
name|charAt
argument_list|(
name|wordEnd
argument_list|)
operator|==
literal|'"'
operator|)
condition|)
block|{
operator|++
name|wordEnd
expr_stmt|;
block|}
comment|// remove the partially composed identifier from the
comment|// sql statement - otherwise we get a parser exception
name|String
name|word
init|=
name|replaced
index|[
literal|0
index|]
operator|=
name|sql
operator|.
name|substring
argument_list|(
name|wordStart
argument_list|,
name|cursor
argument_list|)
decl_stmt|;
if|if
condition|(
name|wordStart
operator|<
name|wordEnd
condition|)
block|{
name|sql
operator|=
name|sql
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|wordStart
argument_list|)
operator|+
name|sql
operator|.
name|substring
argument_list|(
name|wordEnd
argument_list|,
name|sql
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|completionHints
init|=
name|getCompletionHints0
argument_list|(
name|sql
argument_list|,
name|wordStart
argument_list|)
decl_stmt|;
comment|// If cursor was part of the way through a word, only include hints
comment|// which start with that word in the result.
specifier|final
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|result
decl_stmt|;
if|if
condition|(
name|word
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|result
operator|=
operator|new
name|ArrayList
argument_list|<
name|SqlMoniker
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|quoted
condition|)
block|{
comment|// Quoted identifier. Case-sensitive match.
name|word
operator|=
name|word
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|SqlMoniker
name|hint
range|:
name|completionHints
control|)
block|{
name|String
name|cname
init|=
name|hint
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|cname
operator|.
name|startsWith
argument_list|(
name|word
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|hint
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// Regular identifier. Case-insensitive match.
for|for
control|(
name|SqlMoniker
name|hint
range|:
name|completionHints
control|)
block|{
name|String
name|cname
init|=
name|hint
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|cname
operator|.
name|length
argument_list|()
operator|>=
name|word
operator|.
name|length
argument_list|()
operator|)
operator|&&
name|cname
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|word
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|equalsIgnoreCase
argument_list|(
name|word
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|hint
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|result
operator|=
name|completionHints
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|getCompletionHints0
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|cursor
parameter_list|)
block|{
name|String
name|simpleSql
init|=
name|simplifySql
argument_list|(
name|sql
argument_list|,
name|cursor
argument_list|)
decl_stmt|;
name|int
name|idx
init|=
name|simpleSql
operator|.
name|indexOf
argument_list|(
name|hintToken
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|SqlParserPos
name|pos
init|=
operator|new
name|SqlParserPos
argument_list|(
literal|1
argument_list|,
name|idx
operator|+
literal|1
argument_list|)
decl_stmt|;
return|return
name|getCompletionHints
argument_list|(
name|simpleSql
argument_list|,
name|pos
argument_list|)
return|;
block|}
comment|/**    * Gets completion hints for a syntatically correct sql statement with dummy    * SqlIdentifier    *    * @param sql A syntatically correct sql statement for which to retrieve    *            completion hints    * @param pos to indicate the line and column position in the query at which    *            completion hints need to be retrieved. For example, "select    *            a.ename, b.deptno from sales.emp a join sales.dept b "on    *            a.deptno=b.deptno where empno=1"; setting pos to 'Line 1, Column    *            17' returns all the possible column names that can be selected    *            from sales.dept table setting pos to 'Line 1, Column 31' returns    *            all the possible table names in 'sales' schema    * @return an array of hints ({@link SqlMoniker}) that can fill in at the    * indicated position    */
specifier|public
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|getCompletionHints
parameter_list|(
name|String
name|sql
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
comment|// First try the statement they gave us. If this fails, just return
comment|// the tokens which were expected at the failure point.
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|hintList
init|=
operator|new
name|ArrayList
argument_list|<
name|SqlMoniker
argument_list|>
argument_list|()
decl_stmt|;
name|SqlNode
name|sqlNode
init|=
name|tryParse
argument_list|(
name|sql
argument_list|,
name|hintList
argument_list|)
decl_stmt|;
if|if
condition|(
name|sqlNode
operator|==
literal|null
condition|)
block|{
return|return
name|hintList
return|;
block|}
comment|// Now construct a statement which is bound to fail. (Character 7 BEL
comment|// is not legal in any SQL statement.)
specifier|final
name|int
name|x
init|=
name|pos
operator|.
name|getColumnNum
argument_list|()
operator|-
literal|1
decl_stmt|;
name|sql
operator|=
name|sql
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|x
argument_list|)
operator|+
literal|" \07"
operator|+
name|sql
operator|.
name|substring
argument_list|(
name|x
argument_list|)
expr_stmt|;
name|tryParse
argument_list|(
name|sql
argument_list|,
name|hintList
argument_list|)
expr_stmt|;
comment|// Add the identifiers which are expected at the point of interest.
try|try
block|{
name|validator
operator|.
name|validate
argument_list|(
name|sqlNode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// mask any exception that is thrown during the validation, i.e.
comment|// try to continue even if the sql is invalid. we are doing a best
comment|// effort here to try to come up with the requested completion
comment|// hints
name|Util
operator|.
name|swallow
argument_list|(
name|e
argument_list|,
name|LOGGER
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|validatorHints
init|=
name|validator
operator|.
name|lookupHints
argument_list|(
name|sqlNode
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|hintList
operator|.
name|addAll
argument_list|(
name|validatorHints
argument_list|)
expr_stmt|;
return|return
name|hintList
return|;
block|}
comment|/**    * Tries to parse a SQL statement.    *    *<p>If succeeds, returns the parse tree node; if fails, populates the list    * of hints and returns null.    *    * @param sql      SQL statement    * @param hintList List of hints suggesting allowable tokens at the point of    *                 failure    * @return Parse tree if succeeded, null if parse failed    */
specifier|private
name|SqlNode
name|tryParse
parameter_list|(
name|String
name|sql
parameter_list|,
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|hintList
parameter_list|)
block|{
try|try
block|{
return|return
name|parseQuery
argument_list|(
name|sql
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
name|e
parameter_list|)
block|{
for|for
control|(
name|String
name|tokenName
range|:
name|e
operator|.
name|getExpectedTokenNames
argument_list|()
control|)
block|{
comment|// Only add tokens which are keywords, like '"BY"'; ignore
comment|// symbols such as '<Identifier>'.
if|if
condition|(
name|tokenName
operator|.
name|startsWith
argument_list|(
literal|"\""
argument_list|)
operator|&&
name|tokenName
operator|.
name|endsWith
argument_list|(
literal|"\""
argument_list|)
condition|)
block|{
name|hintList
operator|.
name|add
argument_list|(
operator|new
name|SqlMonikerImpl
argument_list|(
name|tokenName
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|tokenName
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|,
name|SqlMonikerType
operator|.
name|Keyword
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|EigenbaseException
name|e
parameter_list|)
block|{
name|Util
operator|.
name|swallow
argument_list|(
name|e
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Gets the fully qualified name for a {@link SqlIdentifier} at a given    * position of a sql statement.    *    * @param sql    A syntactically correct sql statement for which to retrieve a    *               fully qualified SQL identifier name    * @param cursor to indicate the 0-based cursor position in the query that    *               represents a SQL identifier for which its fully qualified    *               name is to be returned.    * @return a {@link SqlMoniker} that contains the fully qualified name of    * the specified SQL identifier, returns null if none is found or the SQL    * statement is invalid.    */
specifier|public
name|SqlMoniker
name|getQualifiedName
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|cursor
parameter_list|)
block|{
name|SqlNode
name|sqlNode
decl_stmt|;
try|try
block|{
name|sqlNode
operator|=
name|parseQuery
argument_list|(
name|sql
argument_list|)
expr_stmt|;
name|validator
operator|.
name|validate
argument_list|(
name|sqlNode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
name|SqlParserPos
name|pos
init|=
operator|new
name|SqlParserPos
argument_list|(
literal|1
argument_list|,
name|cursor
operator|+
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|validator
operator|.
name|lookupQualifiedName
argument_list|(
name|sqlNode
argument_list|,
name|pos
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EigenbaseContextException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|AssertionError
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Attempts to complete and validate a given partially completed sql    * statement, and returns whether it is valid.    *    * @param sql A partial or syntatically incorrect sql statement to validate    * @return whether SQL statement is valid    */
specifier|public
name|boolean
name|isValid
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|SqlSimpleParser
name|simpleParser
init|=
operator|new
name|SqlSimpleParser
argument_list|(
name|hintToken
argument_list|)
decl_stmt|;
name|String
name|simpleSql
init|=
name|simpleParser
operator|.
name|simplifySql
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|SqlNode
name|sqlNode
decl_stmt|;
try|try
block|{
name|sqlNode
operator|=
name|parseQuery
argument_list|(
name|simpleSql
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// if the sql can't be parsed we wont' be able to validate it
return|return
literal|false
return|;
block|}
try|try
block|{
name|validator
operator|.
name|validate
argument_list|(
name|sqlNode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Attempts to parse and validate a SQL statement. Throws the first    * exception encountered. The error message of this exception is to be    * displayed on the UI    *    * @param sql A user-input sql statement to be validated    * @return a List of ValidateErrorInfo (null if sql is valid)    */
specifier|public
name|List
argument_list|<
name|ValidateErrorInfo
argument_list|>
name|validate
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|SqlNode
name|sqlNode
decl_stmt|;
name|List
argument_list|<
name|ValidateErrorInfo
argument_list|>
name|errorList
init|=
operator|new
name|ArrayList
argument_list|<
name|ValidateErrorInfo
argument_list|>
argument_list|()
decl_stmt|;
name|sqlNode
operator|=
name|collectParserError
argument_list|(
name|sql
argument_list|,
name|errorList
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|errorList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|errorList
return|;
block|}
try|try
block|{
name|validator
operator|.
name|validate
argument_list|(
name|sqlNode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EigenbaseContextException
name|e
parameter_list|)
block|{
name|ValidateErrorInfo
name|errInfo
init|=
operator|new
name|ValidateErrorInfo
argument_list|(
name|e
argument_list|)
decl_stmt|;
comment|// validator only returns 1 exception now
name|errorList
operator|.
name|add
argument_list|(
name|errInfo
argument_list|)
expr_stmt|;
return|return
name|errorList
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|ValidateErrorInfo
name|errInfo
init|=
operator|new
name|ValidateErrorInfo
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|sql
operator|.
name|length
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
decl_stmt|;
comment|// parser only returns 1 exception now
name|errorList
operator|.
name|add
argument_list|(
name|errInfo
argument_list|)
expr_stmt|;
return|return
name|errorList
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Turns a partially completed or syntatically incorrect sql statement into    * a simplified, valid one that can be passed into getCompletionHints()    *    * @param sql    A partial or syntatically incorrect sql statement    * @param cursor to indicate column position in the query at which    *               completion hints need to be retrieved.    * @return a completed, valid (and possibly simplified SQL statement    */
specifier|public
name|String
name|simplifySql
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|cursor
parameter_list|)
block|{
name|SqlSimpleParser
name|parser
init|=
operator|new
name|SqlSimpleParser
argument_list|(
name|hintToken
argument_list|)
decl_stmt|;
return|return
name|parser
operator|.
name|simplifySql
argument_list|(
name|sql
argument_list|,
name|cursor
argument_list|)
return|;
block|}
comment|/**    * Return an array of SQL reserved and keywords    *    * @return an of SQL reserved and keywords    */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getReservedAndKeyWords
parameter_list|()
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|c
init|=
name|SqlAbstractParserImpl
operator|.
name|getSql92ReservedWords
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|l
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|getParserImpl
argument_list|()
operator|.
name|getMetadata
argument_list|()
operator|.
name|getJdbcKeywords
argument_list|()
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|al
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|al
operator|.
name|addAll
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|al
operator|.
name|addAll
argument_list|(
name|l
argument_list|)
expr_stmt|;
return|return
name|al
return|;
block|}
comment|/**    * Returns the underlying Parser implementation class.    *    *<p>To use a different parser (recognizing a different dialect of SQL),    * derived class should override.    *    * @return a {@link SqlAbstractParserImpl} instance    */
specifier|protected
name|SqlAbstractParserImpl
name|getParserImpl
parameter_list|()
block|{
name|SqlParser
name|parser
init|=
operator|new
name|SqlParser
argument_list|(
literal|""
argument_list|)
decl_stmt|;
return|return
name|parser
operator|.
name|getParserImpl
argument_list|()
return|;
block|}
comment|/**    * Wrapper function to parse a SQL query (SELECT or VALUES, but not INSERT,    * UPDATE, DELETE, CREATE, DROP etc.), throwing a {@link SqlParseException}    * if the statement is not syntactically valid.    *    * @param sql SQL statement    * @return parse tree    * @throws SqlParseException if not syntactically valid    */
specifier|protected
name|SqlNode
name|parseQuery
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SqlParseException
block|{
name|SqlParser
name|parser
init|=
operator|new
name|SqlParser
argument_list|(
name|sql
argument_list|)
decl_stmt|;
return|return
name|parser
operator|.
name|parseStmt
argument_list|()
return|;
block|}
comment|/**    * Attempts to parse a SQL statement and adds to the errorList if any syntax    * error is found. This implementation uses {@link SqlParser}. Subclass can    * re-implement this with a different parser implementation    *    * @param sql       A user-input sql statement to be parsed    * @param errorList A {@link List} of error to be added to    * @return {@link SqlNode } that is root of the parse tree, null if the sql    * is not valid    */
specifier|protected
name|SqlNode
name|collectParserError
parameter_list|(
name|String
name|sql
parameter_list|,
name|List
argument_list|<
name|ValidateErrorInfo
argument_list|>
name|errorList
parameter_list|)
block|{
try|try
block|{
return|return
name|parseQuery
argument_list|(
name|sql
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
name|e
parameter_list|)
block|{
name|ValidateErrorInfo
name|errInfo
init|=
operator|new
name|ValidateErrorInfo
argument_list|(
name|e
operator|.
name|getPos
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
decl_stmt|;
comment|// parser only returns 1 exception now
name|errorList
operator|.
name|add
argument_list|(
name|errInfo
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * An inner class that represents error message text and position info of a    * validator or parser exception    */
specifier|public
class|class
name|ValidateErrorInfo
block|{
specifier|private
name|int
name|startLineNum
decl_stmt|;
specifier|private
name|int
name|startColumnNum
decl_stmt|;
specifier|private
name|int
name|endLineNum
decl_stmt|;
specifier|private
name|int
name|endColumnNum
decl_stmt|;
specifier|private
name|String
name|errorMsg
decl_stmt|;
comment|/**      * Creates a new ValidateErrorInfo with the position coordinates and an      * error string.      *      * @param startLineNum   Start line number      * @param startColumnNum Start column number      * @param endLineNum     End line number      * @param endColumnNum   End column number      * @param errorMsg       Error message      */
specifier|public
name|ValidateErrorInfo
parameter_list|(
name|int
name|startLineNum
parameter_list|,
name|int
name|startColumnNum
parameter_list|,
name|int
name|endLineNum
parameter_list|,
name|int
name|endColumnNum
parameter_list|,
name|String
name|errorMsg
parameter_list|)
block|{
name|this
operator|.
name|startLineNum
operator|=
name|startLineNum
expr_stmt|;
name|this
operator|.
name|startColumnNum
operator|=
name|startColumnNum
expr_stmt|;
name|this
operator|.
name|endLineNum
operator|=
name|endLineNum
expr_stmt|;
name|this
operator|.
name|endColumnNum
operator|=
name|endColumnNum
expr_stmt|;
name|this
operator|.
name|errorMsg
operator|=
name|errorMsg
expr_stmt|;
block|}
comment|/**      * Creates a new ValidateErrorInfo with an EigenbaseContextException.      *      * @param e Exception      */
specifier|public
name|ValidateErrorInfo
parameter_list|(
name|EigenbaseContextException
name|e
parameter_list|)
block|{
name|this
operator|.
name|startLineNum
operator|=
name|e
operator|.
name|getPosLine
argument_list|()
expr_stmt|;
name|this
operator|.
name|startColumnNum
operator|=
name|e
operator|.
name|getPosColumn
argument_list|()
expr_stmt|;
name|this
operator|.
name|endLineNum
operator|=
name|e
operator|.
name|getEndPosLine
argument_list|()
expr_stmt|;
name|this
operator|.
name|endColumnNum
operator|=
name|e
operator|.
name|getEndPosColumn
argument_list|()
expr_stmt|;
name|this
operator|.
name|errorMsg
operator|=
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
comment|/**      * Creates a new ValidateErrorInfo with a SqlParserPos and an error      * string.      *      * @param pos      Error position      * @param errorMsg Error message      */
specifier|public
name|ValidateErrorInfo
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|String
name|errorMsg
parameter_list|)
block|{
name|this
operator|.
name|startLineNum
operator|=
name|pos
operator|.
name|getLineNum
argument_list|()
expr_stmt|;
name|this
operator|.
name|startColumnNum
operator|=
name|pos
operator|.
name|getColumnNum
argument_list|()
expr_stmt|;
name|this
operator|.
name|endLineNum
operator|=
name|pos
operator|.
name|getEndLineNum
argument_list|()
expr_stmt|;
name|this
operator|.
name|endColumnNum
operator|=
name|pos
operator|.
name|getEndColumnNum
argument_list|()
expr_stmt|;
name|this
operator|.
name|errorMsg
operator|=
name|errorMsg
expr_stmt|;
block|}
comment|/**      * @return 1-based starting line number      */
specifier|public
name|int
name|getStartLineNum
parameter_list|()
block|{
return|return
name|startLineNum
return|;
block|}
comment|/**      * @return 1-based starting column number      */
specifier|public
name|int
name|getStartColumnNum
parameter_list|()
block|{
return|return
name|startColumnNum
return|;
block|}
comment|/**      * @return 1-based end line number      */
specifier|public
name|int
name|getEndLineNum
parameter_list|()
block|{
return|return
name|endLineNum
return|;
block|}
comment|/**      * @return 1-based end column number      */
specifier|public
name|int
name|getEndColumnNum
parameter_list|()
block|{
return|return
name|endColumnNum
return|;
block|}
comment|/**      * @return error message      */
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|errorMsg
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlAdvisor.java
end_comment

end_unit

