begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  *<p>Statement.</p>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Statement
extends|extends
name|AbstractNode
block|{
specifier|protected
name|Statement
parameter_list|(
name|ExpressionType
name|nodeType
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|nodeType
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|final
name|void
name|accept
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|,
name|int
name|lprec
parameter_list|,
name|int
name|rprec
parameter_list|)
block|{
assert|assert
name|lprec
operator|==
literal|0
assert|;
assert|assert
name|rprec
operator|==
literal|0
assert|;
name|accept0
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
comment|// Make return type more specific. A statement can only become a different
comment|// kind of statement; it can't become an expression.
specifier|public
specifier|abstract
name|Statement
name|accept
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
function_decl|;
block|}
end_class

begin_comment
comment|// End Statement.java
end_comment

end_unit

