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

begin_comment
comment|/**  * Abstract base for character and binary string literals.  */
end_comment

begin_class
specifier|abstract
class|class
name|SqlAbstractStringLiteral
extends|extends
name|SqlLiteral
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|SqlAbstractStringLiteral
parameter_list|(
name|Object
name|value
parameter_list|,
name|SqlTypeName
name|typeName
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|value
argument_list|,
name|typeName
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Helper routine for {@link SqlUtil#concatenateLiterals}.    *    * @param literals homogeneous StringLiteral args    * @return StringLiteral with concatenated value. this == lits[0], used only    * for method dispatch.    */
specifier|protected
specifier|abstract
name|SqlAbstractStringLiteral
name|concat1
parameter_list|(
name|List
argument_list|<
name|SqlLiteral
argument_list|>
name|literals
parameter_list|)
function_decl|;
block|}
end_class

begin_comment
comment|// End SqlAbstractStringLiteral.java
end_comment

end_unit

