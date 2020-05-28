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
name|type
operator|.
name|ArraySqlType
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
name|MapSqlType
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
name|MultisetSqlType
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
name|SqlOperandCountRanges
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
name|SqlTypeName
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
name|Util
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
comment|/**  * The<code>UNNEST</code> operator.  */
end_comment

begin_class
specifier|public
class|class
name|SqlUnnestOperator
extends|extends
name|SqlFunctionalOperator
block|{
comment|/** Whether {@code WITH ORDINALITY} was specified.    *    *<p>If so, the returned records include a column {@code ORDINALITY}. */
specifier|public
specifier|final
name|boolean
name|withOrdinality
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ORDINALITY_COLUMN_NAME
init|=
literal|"ORDINALITY"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MAP_KEY_COLUMN_NAME
init|=
literal|"KEY"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MAP_VALUE_COLUMN_NAME
init|=
literal|"VALUE"
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlUnnestOperator
parameter_list|(
name|boolean
name|withOrdinality
parameter_list|)
block|{
name|super
argument_list|(
literal|"UNNEST"
argument_list|,
name|SqlKind
operator|.
name|UNNEST
argument_list|,
literal|200
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|repeat
argument_list|(
name|SqlOperandCountRanges
operator|.
name|from
argument_list|(
literal|1
argument_list|)
argument_list|,
name|OperandTypes
operator|.
name|SCALAR_OR_RECORD_COLLECTION_OR_MAP
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|withOrdinality
operator|=
name|withOrdinality
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
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
name|RelDataTypeFactory
operator|.
name|Builder
name|builder
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Integer
name|operand
range|:
name|Util
operator|.
name|range
argument_list|(
name|opBinding
operator|.
name|getOperandCount
argument_list|()
argument_list|)
control|)
block|{
name|RelDataType
name|type
init|=
name|opBinding
operator|.
name|getOperandType
argument_list|(
name|operand
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|ANY
condition|)
block|{
comment|// Unnest Operator in schema less systems returns one column as the output
comment|// $unnest is a place holder to specify that one column with type ANY is output.
return|return
name|builder
operator|.
name|add
argument_list|(
literal|"$unnest"
argument_list|,
name|SqlTypeName
operator|.
name|ANY
argument_list|)
operator|.
name|nullable
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
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
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
assert|assert
name|type
operator|instanceof
name|ArraySqlType
operator|||
name|type
operator|instanceof
name|MultisetSqlType
operator|||
name|type
operator|instanceof
name|MapSqlType
assert|;
if|if
condition|(
name|type
operator|instanceof
name|MapSqlType
condition|)
block|{
name|MapSqlType
name|mapType
init|=
operator|(
name|MapSqlType
operator|)
name|type
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|MAP_KEY_COLUMN_NAME
argument_list|,
name|mapType
operator|.
name|getKeyType
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|MAP_VALUE_COLUMN_NAME
argument_list|,
name|mapType
operator|.
name|getValueType
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|RelDataType
name|componentType
init|=
name|requireNonNull
argument_list|(
name|type
operator|.
name|getComponentType
argument_list|()
argument_list|,
literal|"componentType"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|allowAliasUnnestItems
argument_list|(
name|opBinding
argument_list|)
operator|&&
name|componentType
operator|.
name|isStruct
argument_list|()
condition|)
block|{
name|builder
operator|.
name|addAll
argument_list|(
name|componentType
operator|.
name|getFieldList
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|add
argument_list|(
name|SqlUtil
operator|.
name|deriveAliasFromOrdinal
argument_list|(
name|operand
argument_list|)
argument_list|,
name|componentType
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|withOrdinality
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|ORDINALITY_COLUMN_NAME
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
name|boolean
name|allowAliasUnnestItems
parameter_list|(
name|SqlOperatorBinding
name|operatorBinding
parameter_list|)
block|{
return|return
operator|(
name|operatorBinding
operator|instanceof
name|SqlCallBinding
operator|)
operator|&&
operator|(
operator|(
name|SqlCallBinding
operator|)
name|operatorBinding
operator|)
operator|.
name|getValidator
argument_list|()
operator|.
name|config
argument_list|()
operator|.
name|sqlConformance
argument_list|()
operator|.
name|allowAliasUnnestItems
argument_list|()
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
if|if
condition|(
name|call
operator|.
name|operandCount
argument_list|()
operator|==
literal|1
operator|&&
name|call
operator|.
name|getOperandList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|SELECT
condition|)
block|{
comment|// avoid double ( ) on unnesting a sub-query
name|writer
operator|.
name|keyword
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
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
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|call
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|withOrdinality
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"WITH ORDINALITY"
argument_list|)
expr_stmt|;
block|}
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

