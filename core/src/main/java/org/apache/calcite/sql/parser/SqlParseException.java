begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|parser
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|CalciteParserException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_comment
comment|/**  * SqlParseException defines a checked exception corresponding to  * {@link SqlParser}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlParseException
extends|extends
name|Exception
implements|implements
name|CalciteParserException
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlParserPos
name|pos
decl_stmt|;
specifier|private
specifier|final
name|int
index|[]
index|[]
name|expectedTokenSequences
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|tokenImages
decl_stmt|;
comment|/**    * The original exception thrown by the generated parser. Unfortunately,    * each generated parser throws exceptions of a different class. So, we keep    * the exception for forensic purposes, but don't print it publicly.    *    *<p>Also, make it transient, because it is a ParseException generated by    * JavaCC and contains a non-serializable Token.    */
specifier|private
specifier|final
specifier|transient
name|Throwable
name|parserException
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a SqlParseException.    *    * @param message                Message    * @param pos                    Position    * @param expectedTokenSequences Token sequences    * @param tokenImages            Token images    * @param parserException        Parser exception    */
specifier|public
name|SqlParseException
parameter_list|(
name|String
name|message
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
name|int
index|[]
index|[]
name|expectedTokenSequences
parameter_list|,
name|String
index|[]
name|tokenImages
parameter_list|,
name|Throwable
name|parserException
parameter_list|)
block|{
comment|// Cause must be null because the exception generated by JavaCC
comment|// contains a Token and is therefore not serializable (even though it
comment|// implements the Serializable interface). This is serious: one
comment|// non-serializable object poisons the entire chain, so the stack
comment|// cannot be transmitted over Java RMI.
name|super
argument_list|(
name|message
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|pos
operator|=
name|pos
expr_stmt|;
name|this
operator|.
name|expectedTokenSequences
operator|=
name|expectedTokenSequences
expr_stmt|;
name|this
operator|.
name|tokenImages
operator|=
name|tokenImages
expr_stmt|;
name|this
operator|.
name|parserException
operator|=
name|parserException
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the position where this error occurred.    *    * @return parser position    */
specifier|public
name|SqlParserPos
name|getPos
parameter_list|()
block|{
return|return
name|pos
return|;
block|}
comment|/**    * Returns a list of the token names which could have legally occurred at    * this point.    *    *<p>If some of the alternatives contain multiple tokens, returns the last    * token of only these longest sequences. (This occurs when the parser is    * maintaining more than the usual lookup.) For instance, if the possible    * tokens are    *    *<blockquote>    *<pre>    * {"IN"}    * {"BETWEEN"}    * {"LIKE"}    * {"=", "&lt;IDENTIFIER&gt;"}    * {"=", "USER"}    *</pre>    *</blockquote>    *    *<p>returns    *    *<blockquote>    *<pre>    * "&lt;IDENTIFIER&gt;"    * "USER"    *</pre>    *</blockquote>    *    * @return list of token names which could have occurred at this point    */
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getExpectedTokenNames
parameter_list|()
block|{
if|if
condition|(
name|expectedTokenSequences
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|int
name|maxLength
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
index|[]
name|expectedTokenSequence
range|:
name|expectedTokenSequences
control|)
block|{
name|maxLength
operator|=
name|Math
operator|.
name|max
argument_list|(
name|expectedTokenSequence
operator|.
name|length
argument_list|,
name|maxLength
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
index|[]
name|expectedTokenSequence
range|:
name|expectedTokenSequences
control|)
block|{
if|if
condition|(
name|expectedTokenSequence
operator|.
name|length
operator|==
name|maxLength
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|tokenImages
index|[
name|expectedTokenSequence
index|[
name|expectedTokenSequence
operator|.
name|length
operator|-
literal|1
index|]
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|set
return|;
block|}
comment|/**    * Returns the token images.    *    * @return token images    */
specifier|public
name|String
index|[]
name|getTokenImages
parameter_list|()
block|{
return|return
name|tokenImages
return|;
block|}
comment|/**    * Returns the expected token sequences.    *    * @return expected token sequences    */
specifier|public
name|int
index|[]
index|[]
name|getExpectedTokenSequences
parameter_list|()
block|{
return|return
name|expectedTokenSequences
return|;
block|}
comment|// override Exception
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Throwable
name|getCause
parameter_list|()
block|{
return|return
name|parserException
return|;
block|}
comment|/**    * Per {@link java.io.Serializable} API, provides a replacement object to be    * written during serialization.    *    *<p>SqlParseException is serializable but is not available on the client.    * This implementation converts this SqlParseException into a vanilla    * {@link RuntimeException} with the same message.    */
specifier|private
name|Object
name|writeReplace
parameter_list|()
block|{
return|return
operator|new
name|RuntimeException
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|getMessage
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

