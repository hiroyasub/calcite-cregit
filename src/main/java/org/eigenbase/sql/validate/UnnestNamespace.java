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
name|validate
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
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
name|fun
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
comment|/**  * Namespace for UNNEST.  *  * @author wael  * @version $Id$  * @since Mar 25, 2003  */
end_comment

begin_class
class|class
name|UnnestNamespace
extends|extends
name|AbstractNamespace
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlCall
name|unnest
decl_stmt|;
specifier|private
specifier|final
name|SqlValidatorScope
name|scope
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|UnnestNamespace
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|,
name|SqlCall
name|unnest
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlNode
name|enclosingNode
parameter_list|)
block|{
name|super
argument_list|(
name|validator
argument_list|,
name|enclosingNode
argument_list|)
expr_stmt|;
assert|assert
name|scope
operator|!=
literal|null
assert|;
assert|assert
name|unnest
operator|.
name|getOperator
argument_list|()
operator|==
name|SqlStdOperatorTable
operator|.
name|unnestOperator
assert|;
name|this
operator|.
name|unnest
operator|=
name|unnest
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|protected
name|RelDataType
name|validateImpl
parameter_list|()
block|{
comment|// Validate the call and its arguments, and infer the return type.
name|validator
operator|.
name|validateCall
argument_list|(
name|unnest
argument_list|,
name|scope
argument_list|)
expr_stmt|;
name|RelDataType
name|type
init|=
name|unnest
operator|.
name|getOperator
argument_list|()
operator|.
name|validateOperands
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|unnest
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|isStruct
argument_list|()
condition|)
block|{
return|return
name|type
return|;
block|}
return|return
name|validator
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createStructType
argument_list|(
operator|new
name|RelDataType
index|[]
block|{
name|type
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
name|validator
operator|.
name|deriveAlias
argument_list|(
name|unnest
argument_list|,
literal|0
argument_list|)
block|}
argument_list|)
return|;
block|}
comment|/**      * Returns the type of the argument to UNNEST.      */
specifier|private
name|RelDataType
name|inferReturnType
parameter_list|()
block|{
specifier|final
name|SqlNode
name|operand
init|=
name|unnest
operator|.
name|getOperands
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|RelDataType
name|type
init|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|operand
argument_list|)
decl_stmt|;
comment|// If sub-query, pick out first column.
comment|// TODO: Handle this using usual sub-select validation.
if|if
condition|(
name|type
operator|.
name|isStruct
argument_list|()
condition|)
block|{
name|type
operator|=
name|type
operator|.
name|getFields
argument_list|()
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
name|MultisetSqlType
name|t
init|=
operator|(
name|MultisetSqlType
operator|)
name|type
decl_stmt|;
return|return
name|t
operator|.
name|getComponentType
argument_list|()
return|;
block|}
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|unnest
return|;
block|}
block|}
end_class

begin_comment
comment|// End UnnestNamespace.java
end_comment

end_unit

