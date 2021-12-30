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
name|SqlUtil
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
name|InferTypes
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
name|util
operator|.
name|Pair
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * SqlRowOperator represents the special ROW constructor.  *  *<p>TODO: describe usage for row-value construction and row-type construction  * (SQL supports both).  */
end_comment

begin_class
specifier|public
class|class
name|SqlRowOperator
extends|extends
name|SqlSpecialOperator
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlRowOperator
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|SqlKind
operator|.
name|ROW
argument_list|,
name|MDX_PRECEDENCE
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|InferTypes
operator|.
name|RETURN_TYPE
argument_list|,
name|OperandTypes
operator|.
name|VARIADIC
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
specifier|final
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
comment|// The type of a ROW(e1,e2) expression is a record with the types
comment|// {e1type,e2type}.  According to the standard, field names are
comment|// implementation-defined.
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|recordType
init|=
name|typeFactory
operator|.
name|createStructType
argument_list|(
operator|new
name|AbstractList
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|Pair
operator|.
name|of
argument_list|(
name|SqlUtil
operator|.
name|deriveAliasFromOrdinal
argument_list|(
name|index
argument_list|)
argument_list|,
name|opBinding
operator|.
name|getOperandType
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|opBinding
operator|.
name|getOperandCount
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// The value of ROW(e1,e2) is considered null if and only all of its
comment|// fields (i.e., e1, e2) are null. Otherwise ROW can not be null.
specifier|final
name|boolean
name|nullable
init|=
name|recordType
operator|.
name|getFieldList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|allMatch
argument_list|(
name|f
lambda|->
name|f
operator|.
name|getType
argument_list|()
operator|.
name|isNullable
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|recordType
argument_list|,
name|nullable
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
name|SqlUtil
operator|.
name|unparseFunctionSyntax
argument_list|(
name|this
argument_list|,
name|writer
argument_list|,
name|call
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// override SqlOperator
annotation|@
name|Override
specifier|public
name|boolean
name|requiresDecimalExpansion
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

