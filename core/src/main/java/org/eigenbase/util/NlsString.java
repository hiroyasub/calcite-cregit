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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
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
name|resource
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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|runtime
operator|.
name|SqlFunctions
import|;
end_import

begin_comment
comment|/**  * A string, optionally with {@link Charset character set} and {@link  * SqlCollation}. It is immutable.  */
end_comment

begin_class
specifier|public
class|class
name|NlsString
implements|implements
name|Comparable
argument_list|<
name|NlsString
argument_list|>
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|String
name|charsetName
decl_stmt|;
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
specifier|private
specifier|final
name|Charset
name|charset
decl_stmt|;
specifier|private
specifier|final
name|SqlCollation
name|collation
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a string in a specfied character set.    *    * @param value       String constant, must not be null    * @param charsetName Name of the character set, may be null    * @param collation   Collation, may be null    * @throws IllegalCharsetNameException If the given charset name is illegal    * @throws UnsupportedCharsetException If no support for the named charset    *                                     is available in this instance of the    *                                     Java virtual machine    * @throws RuntimeException            If the given value cannot be represented in the    *                                     given charset    * @pre theString != null    */
specifier|public
name|NlsString
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|charsetName
parameter_list|,
name|SqlCollation
name|collation
parameter_list|)
throws|throws
name|IllegalCharsetNameException
throws|,
name|UnsupportedCharsetException
block|{
name|Util
operator|.
name|pre
argument_list|(
name|value
operator|!=
literal|null
argument_list|,
literal|"theString != null"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|charsetName
condition|)
block|{
name|charsetName
operator|=
name|charsetName
operator|.
name|toUpperCase
argument_list|()
expr_stmt|;
name|this
operator|.
name|charsetName
operator|=
name|charsetName
expr_stmt|;
name|String
name|javaCharsetName
init|=
name|SqlUtil
operator|.
name|translateCharacterSetName
argument_list|(
name|charsetName
argument_list|)
decl_stmt|;
if|if
condition|(
name|javaCharsetName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedCharsetException
argument_list|(
name|charsetName
argument_list|)
throw|;
block|}
name|this
operator|.
name|charset
operator|=
name|Charset
operator|.
name|forName
argument_list|(
name|javaCharsetName
argument_list|)
expr_stmt|;
name|CharsetEncoder
name|encoder
init|=
name|charset
operator|.
name|newEncoder
argument_list|()
decl_stmt|;
comment|// dry run to see if encoding hits any problems
try|try
block|{
name|encoder
operator|.
name|encode
argument_list|(
name|CharBuffer
operator|.
name|wrap
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CharacterCodingException
name|ex
parameter_list|)
block|{
throw|throw
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|CharsetEncoding
operator|.
name|ex
argument_list|(
name|value
argument_list|,
name|javaCharsetName
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|charsetName
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|charset
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|collation
operator|=
name|collation
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Object
name|clone
parameter_list|()
block|{
return|return
operator|new
name|NlsString
argument_list|(
name|value
argument_list|,
name|charsetName
argument_list|,
name|collation
argument_list|)
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|value
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|=
name|Util
operator|.
name|hash
argument_list|(
name|h
argument_list|,
name|charsetName
argument_list|)
expr_stmt|;
name|h
operator|=
name|Util
operator|.
name|hash
argument_list|(
name|h
argument_list|,
name|collation
argument_list|)
expr_stmt|;
return|return
name|h
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|NlsString
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NlsString
name|that
init|=
operator|(
name|NlsString
operator|)
name|obj
decl_stmt|;
return|return
name|Util
operator|.
name|equal
argument_list|(
name|value
argument_list|,
name|that
operator|.
name|value
argument_list|)
operator|&&
name|Util
operator|.
name|equal
argument_list|(
name|charsetName
argument_list|,
name|that
operator|.
name|charsetName
argument_list|)
operator|&&
name|Util
operator|.
name|equal
argument_list|(
name|collation
argument_list|,
name|that
operator|.
name|collation
argument_list|)
return|;
block|}
comment|// implement Comparable
specifier|public
name|int
name|compareTo
parameter_list|(
name|NlsString
name|other
parameter_list|)
block|{
comment|// TODO jvs 18-Jan-2006:  Actual collation support.  This just uses
comment|// the default collation.
return|return
name|value
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|value
argument_list|)
return|;
block|}
specifier|public
name|String
name|getCharsetName
parameter_list|()
block|{
return|return
name|charsetName
return|;
block|}
specifier|public
name|Charset
name|getCharset
parameter_list|()
block|{
return|return
name|charset
return|;
block|}
specifier|public
name|SqlCollation
name|getCollation
parameter_list|()
block|{
return|return
name|collation
return|;
block|}
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**    * Returns a string the same as this but with spaces trimmed from the    * right.    */
specifier|public
name|NlsString
name|rtrim
parameter_list|()
block|{
name|String
name|trimmed
init|=
name|SqlFunctions
operator|.
name|rtrim
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|trimmed
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
operator|new
name|NlsString
argument_list|(
name|trimmed
argument_list|,
name|charsetName
argument_list|,
name|collation
argument_list|)
return|;
block|}
return|return
name|this
return|;
block|}
comment|/**    * Returns the string quoted for SQL, for example<code>_ISO-8859-1'is it a    * plane? no it''s superman!'</code>.    *    * @param prefix if true, prefix the character set name    * @param suffix if true, suffix the collation clause    * @return the quoted string    */
specifier|public
name|String
name|asSql
parameter_list|(
name|boolean
name|prefix
parameter_list|,
name|boolean
name|suffix
parameter_list|)
block|{
name|StringBuilder
name|ret
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|prefix
operator|&&
operator|(
literal|null
operator|!=
name|charsetName
operator|)
condition|)
block|{
name|ret
operator|.
name|append
argument_list|(
literal|"_"
argument_list|)
expr_stmt|;
name|ret
operator|.
name|append
argument_list|(
name|charsetName
argument_list|)
expr_stmt|;
block|}
name|ret
operator|.
name|append
argument_list|(
literal|"'"
argument_list|)
expr_stmt|;
name|ret
operator|.
name|append
argument_list|(
name|Util
operator|.
name|replace
argument_list|(
name|value
argument_list|,
literal|"'"
argument_list|,
literal|"''"
argument_list|)
argument_list|)
expr_stmt|;
name|ret
operator|.
name|append
argument_list|(
literal|"'"
argument_list|)
expr_stmt|;
comment|// NOTE jvs 3-Feb-2005:  see FRG-78 for why this should go away
if|if
condition|(
literal|false
condition|)
block|{
if|if
condition|(
name|suffix
operator|&&
operator|(
literal|null
operator|!=
name|collation
operator|)
condition|)
block|{
name|ret
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|ret
operator|.
name|append
argument_list|(
name|collation
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Returns the string quoted for SQL, for example<code>_ISO-8859-1'is it a    * plane? no it''s superman!'</code>.    */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|asSql
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Concatenates some {@link NlsString} objects. The result has the charset    * and collation of the first element. The other elements must have matching    * (or null) charset and collation. Concatenates all at once, not pairwise,    * to avoid string copies.    *    * @param args array of {@link NlsString} to be concatenated    */
specifier|public
specifier|static
name|NlsString
name|concat
parameter_list|(
name|NlsString
index|[]
name|args
parameter_list|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|2
condition|)
block|{
return|return
name|args
index|[
literal|0
index|]
return|;
block|}
name|String
name|charSetName
init|=
name|args
index|[
literal|0
index|]
operator|.
name|charsetName
decl_stmt|;
name|SqlCollation
name|collation
init|=
name|args
index|[
literal|0
index|]
operator|.
name|collation
decl_stmt|;
name|int
name|length
init|=
name|args
index|[
literal|0
index|]
operator|.
name|value
operator|.
name|length
argument_list|()
decl_stmt|;
comment|// sum string lengths and validate
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|length
operator|+=
name|args
index|[
name|i
index|]
operator|.
name|value
operator|.
name|length
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
operator|(
name|args
index|[
name|i
index|]
operator|.
name|charsetName
operator|==
literal|null
operator|)
operator|||
name|args
index|[
name|i
index|]
operator|.
name|charsetName
operator|.
name|equals
argument_list|(
name|charSetName
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"mismatched charsets"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
operator|(
name|args
index|[
name|i
index|]
operator|.
name|collation
operator|==
literal|null
operator|)
operator|||
name|args
index|[
name|i
index|]
operator|.
name|collation
operator|.
name|equals
argument_list|(
name|collation
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"mismatched collations"
argument_list|)
throw|;
block|}
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|args
index|[
name|i
index|]
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|NlsString
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|charSetName
argument_list|,
name|collation
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End NlsString.java
end_comment

end_unit

