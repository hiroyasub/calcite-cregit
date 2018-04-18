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
name|validate
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
name|SqlIdentifier
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
name|SqlUnnestOperator
import|;
end_import

begin_comment
comment|/**  * Namespace for UNNEST.  */
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
operator|instanceof
name|SqlUnnestOperator
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
annotation|@
name|Override
specifier|public
name|SqlValidatorTable
name|getTable
parameter_list|()
block|{
specifier|final
name|SqlNode
name|toUnnest
init|=
name|unnest
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|toUnnest
operator|instanceof
name|SqlIdentifier
condition|)
block|{
comment|// When operand of SqlIdentifier type does not have struct, fake a table
comment|// for UnnestNamespace
specifier|final
name|SqlIdentifier
name|id
init|=
operator|(
name|SqlIdentifier
operator|)
name|toUnnest
decl_stmt|;
specifier|final
name|SqlQualified
name|qualified
init|=
name|this
operator|.
name|scope
operator|.
name|fullyQualify
argument_list|(
name|id
argument_list|)
decl_stmt|;
return|return
name|qualified
operator|.
name|namespace
operator|.
name|getTable
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|RelDataType
name|validateImpl
parameter_list|(
name|RelDataType
name|targetRowType
parameter_list|)
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
return|return
name|toStruct
argument_list|(
name|type
argument_list|,
name|unnest
argument_list|)
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

