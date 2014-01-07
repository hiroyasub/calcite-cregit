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
package|;
end_package

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
name|type
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
comment|/**  * A character string literal.  *  *<p>Its {@link #value} field is an {@link NlsString} and {@link #typeName} is  * {@link SqlTypeName#CHAR}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlCharStringLiteral
extends|extends
name|SqlAbstractStringLiteral
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|SqlCharStringLiteral
parameter_list|(
name|NlsString
name|val
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|val
argument_list|,
name|SqlTypeName
operator|.
name|CHAR
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * @return the underlying NlsString      */
specifier|public
name|NlsString
name|getNlsString
parameter_list|()
block|{
return|return
operator|(
name|NlsString
operator|)
name|value
return|;
block|}
comment|/**      * @return the collation      */
specifier|public
name|SqlCollation
name|getCollation
parameter_list|()
block|{
return|return
name|getNlsString
argument_list|()
operator|.
name|getCollation
argument_list|()
return|;
block|}
specifier|public
name|SqlNode
name|clone
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
operator|new
name|SqlCharStringLiteral
argument_list|(
operator|(
name|NlsString
operator|)
name|value
argument_list|,
name|pos
argument_list|)
return|;
block|}
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
if|if
condition|(
literal|false
condition|)
block|{
name|Util
operator|.
name|discard
argument_list|(
name|Bug
operator|.
name|Frg78Fixed
argument_list|)
expr_stmt|;
name|String
name|stringValue
init|=
operator|(
operator|(
name|NlsString
operator|)
name|value
operator|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|writer
operator|.
name|literal
argument_list|(
name|writer
operator|.
name|getDialect
argument_list|()
operator|.
name|quoteStringLiteral
argument_list|(
name|stringValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
assert|assert
name|value
operator|instanceof
name|NlsString
assert|;
name|writer
operator|.
name|literal
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|SqlAbstractStringLiteral
name|concat1
parameter_list|(
name|SqlLiteral
index|[]
name|lits
parameter_list|)
block|{
name|NlsString
index|[]
name|args
init|=
operator|new
name|NlsString
index|[
name|lits
operator|.
name|length
index|]
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
name|lits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|args
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|SqlCharStringLiteral
operator|)
name|lits
index|[
name|i
index|]
operator|)
operator|.
name|getNlsString
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|SqlCharStringLiteral
argument_list|(
name|NlsString
operator|.
name|concat
argument_list|(
name|args
argument_list|)
argument_list|,
name|lits
index|[
literal|0
index|]
operator|.
name|getParserPosition
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlCharStringLiteral.java
end_comment

end_unit

