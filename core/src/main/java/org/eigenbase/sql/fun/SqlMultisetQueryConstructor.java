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
name|fun
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
name|sql
operator|.
name|validate
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Definition of the SQL:2003 standard MULTISET query constructor,<code>  * MULTISET (&lt;query&gt;)</code>.  *  * @see SqlMultisetValueConstructor  */
end_comment

begin_class
specifier|public
class|class
name|SqlMultisetQueryConstructor
extends|extends
name|SqlSpecialOperator
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlMultisetQueryConstructor
parameter_list|()
block|{
name|this
argument_list|(
literal|"MULTISET"
argument_list|,
name|SqlKind
operator|.
name|MULTISET_QUERY_CONSTRUCTOR
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|SqlMultisetQueryConstructor
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
name|MDX_PRECEDENCE
argument_list|,
literal|false
argument_list|,
name|SqlTypeStrategies
operator|.
name|rtiFirstArgType
argument_list|,
literal|null
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcVariadic
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
name|RelDataType
name|type
init|=
name|getComponentType
argument_list|(
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|opBinding
operator|.
name|collectOperandTypes
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|type
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|SqlTypeUtil
operator|.
name|createMultisetType
argument_list|(
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|type
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|private
name|RelDataType
name|getComponentType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|List
argument_list|<
name|RelDataType
argument_list|>
name|argTypes
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|argTypes
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|checkOperandTypes
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|argTypes
init|=
name|SqlTypeUtil
operator|.
name|deriveAndCollectTypes
argument_list|(
name|callBinding
operator|.
name|getValidator
argument_list|()
argument_list|,
name|callBinding
operator|.
name|getScope
argument_list|()
argument_list|,
name|callBinding
operator|.
name|getCall
argument_list|()
operator|.
name|operands
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|componentType
init|=
name|getComponentType
argument_list|(
name|callBinding
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|argTypes
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|componentType
condition|)
block|{
if|if
condition|(
name|throwOnFailure
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newValidationError
argument_list|(
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|NeedSameTypeParameter
operator|.
name|ex
argument_list|()
argument_list|)
throw|;
block|}
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|RelDataType
name|deriveType
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
name|SqlSelect
name|subSelect
init|=
operator|(
name|SqlSelect
operator|)
name|call
operator|.
name|operands
index|[
literal|0
index|]
decl_stmt|;
name|subSelect
operator|.
name|validateExpr
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|)
expr_stmt|;
name|SqlValidatorNamespace
name|ns
init|=
name|validator
operator|.
name|getNamespace
argument_list|(
name|subSelect
argument_list|)
decl_stmt|;
assert|assert
literal|null
operator|!=
name|ns
operator|.
name|getRowType
argument_list|()
assert|;
return|return
name|SqlTypeUtil
operator|.
name|createMultisetType
argument_list|(
name|validator
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|ns
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|false
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
name|SqlNode
index|[]
name|operands
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"MULTISET"
argument_list|)
expr_stmt|;
specifier|final
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startList
argument_list|(
literal|"("
argument_list|,
literal|")"
argument_list|)
decl_stmt|;
assert|assert
name|operands
operator|.
name|length
operator|==
literal|1
assert|;
name|operands
index|[
literal|0
index|]
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|argumentMustBeScalar
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlMultisetQueryConstructor.java
end_comment

end_unit

