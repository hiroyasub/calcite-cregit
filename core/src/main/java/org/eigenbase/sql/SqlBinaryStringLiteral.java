begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|List
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_comment
comment|/**  * A binary (or hexadecimal) string literal.  *  *<p>The {@link #value} field is a {@link BitString} and {@link #typeName} is  * {@link SqlTypeName#BINARY}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlBinaryStringLiteral
extends|extends
name|SqlAbstractStringLiteral
block|{
specifier|private
specifier|static
specifier|final
name|Function
argument_list|<
name|SqlLiteral
argument_list|,
name|BitString
argument_list|>
name|F
init|=
operator|new
name|Function
argument_list|<
name|SqlLiteral
argument_list|,
name|BitString
argument_list|>
argument_list|()
block|{
specifier|public
name|BitString
name|apply
parameter_list|(
name|SqlLiteral
name|literal
parameter_list|)
block|{
return|return
operator|(
operator|(
name|SqlBinaryStringLiteral
operator|)
name|literal
operator|)
operator|.
name|getBitString
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|SqlBinaryStringLiteral
parameter_list|(
name|BitString
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
name|BINARY
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * @return the underlying BitString    */
specifier|public
name|BitString
name|getBitString
parameter_list|()
block|{
return|return
operator|(
name|BitString
operator|)
name|value
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
name|SqlBinaryStringLiteral
argument_list|(
operator|(
name|BitString
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
assert|assert
name|value
operator|instanceof
name|BitString
assert|;
name|writer
operator|.
name|literal
argument_list|(
literal|"X'"
operator|+
operator|(
operator|(
name|BitString
operator|)
name|value
operator|)
operator|.
name|toHexString
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|SqlAbstractStringLiteral
name|concat1
parameter_list|(
name|List
argument_list|<
name|SqlLiteral
argument_list|>
name|literals
parameter_list|)
block|{
return|return
operator|new
name|SqlBinaryStringLiteral
argument_list|(
name|BitString
operator|.
name|concat
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|literals
argument_list|,
name|F
argument_list|)
argument_list|)
argument_list|,
name|literals
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getParserPosition
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlBinaryStringLiteral.java
end_comment

end_unit

