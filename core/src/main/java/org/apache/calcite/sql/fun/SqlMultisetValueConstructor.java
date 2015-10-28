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
name|fun
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlCall
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlCallBinding
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlKind
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlOperatorBinding
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlSpecialOperator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|type
operator|.
name|OperandTypes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|type
operator|.
name|ReturnTypes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|type
operator|.
name|SqlTypeUtil
import|;
end_import

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
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * Definition of the SQL:2003 standard MULTISET constructor,<code>MULTISET  * [&lt;expr&gt;, ...]</code>.  *  *<p>Derived classes construct other kinds of collections.</p>  *  * @see SqlMultisetQueryConstructor  */
end_comment

begin_class
specifier|public
class|class
name|SqlMultisetValueConstructor
extends|extends
name|SqlSpecialOperator
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlMultisetValueConstructor
parameter_list|()
block|{
name|this
argument_list|(
literal|"MULTISET"
argument_list|,
name|SqlKind
operator|.
name|MULTISET_VALUE_CONSTRUCTOR
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|SqlMultisetValueConstructor
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
name|ReturnTypes
operator|.
name|ARG0
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|VARIADIC
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
specifier|protected
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
name|operands
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|argTypes
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newValidationError
argument_list|(
name|RESOURCE
operator|.
name|requireAtLeastOneArg
argument_list|()
argument_list|)
throw|;
block|}
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
name|RESOURCE
operator|.
name|needSameTypeParameter
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
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
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
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// "MULTISET" or "ARRAY"
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
literal|"["
argument_list|,
literal|"]"
argument_list|)
decl_stmt|;
for|for
control|(
name|SqlNode
name|operand
range|:
name|call
operator|.
name|getOperandList
argument_list|()
control|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|operand
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
block|}
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlMultisetValueConstructor.java
end_comment

end_unit

