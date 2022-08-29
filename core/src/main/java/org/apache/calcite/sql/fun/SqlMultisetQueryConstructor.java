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
name|SqlSelect
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
name|SqlTypeTransform
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
name|SqlTypeTransforms
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|validate
operator|.
name|SqlValidator
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
name|validate
operator|.
name|SqlValidatorNamespace
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
name|validate
operator|.
name|SqlValidatorScope
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
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
specifier|final
name|SqlTypeTransform
name|typeTransform
decl_stmt|;
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
argument_list|,
name|SqlTypeTransforms
operator|.
name|TO_MULTISET_QUERY
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
parameter_list|,
name|SqlTypeTransform
name|typeTransform
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
operator|.
name|andThen
argument_list|(
name|typeTransform
argument_list|)
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|VARIADIC
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeTransform
operator|=
name|typeTransform
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
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
name|deriveType
argument_list|(
name|callBinding
argument_list|,
name|callBinding
operator|.
name|operands
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|componentType
init|=
name|callBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|leastRestrictive
argument_list|(
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
annotation|@
name|Override
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
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
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
specifier|final
name|SqlValidatorNamespace
name|ns
init|=
name|requireNonNull
argument_list|(
name|validator
operator|.
name|getNamespace
argument_list|(
name|subSelect
argument_list|)
argument_list|,
parameter_list|()
lambda|->
literal|"namespace is missing for "
operator|+
name|subSelect
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|requireNonNull
argument_list|(
name|ns
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|"rowType"
argument_list|)
decl_stmt|;
specifier|final
name|SqlCallBinding
name|opBinding
init|=
operator|new
name|SqlCallBinding
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|)
decl_stmt|;
return|return
name|typeTransform
operator|.
name|transformType
argument_list|(
name|opBinding
argument_list|,
name|rowType
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|call
operator|.
name|operandCount
argument_list|()
operator|==
literal|1
assert|;
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
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
annotation|@
name|Override
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

end_unit

