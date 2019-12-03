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
name|SqlAggFunction
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
name|SqlFunctionCategory
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
name|SqlJsonConstructorNullClause
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
name|SqlLiteral
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
name|parser
operator|.
name|SqlParserPos
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
name|SqlTypeFamily
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
name|SqlValidatorImpl
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Optionality
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * The<code>JSON_OBJECTAGG</code> aggregate function.  */
end_comment

begin_class
specifier|public
class|class
name|SqlJsonArrayAggAggFunction
extends|extends
name|SqlAggFunction
block|{
specifier|private
specifier|final
name|SqlJsonConstructorNullClause
name|nullClause
decl_stmt|;
specifier|public
name|SqlJsonArrayAggAggFunction
parameter_list|(
name|SqlKind
name|kind
parameter_list|,
name|SqlJsonConstructorNullClause
name|nullClause
parameter_list|)
block|{
name|super
argument_list|(
name|kind
operator|+
literal|"_"
operator|+
name|nullClause
operator|.
name|name
argument_list|()
argument_list|,
literal|null
argument_list|,
name|kind
argument_list|,
name|ReturnTypes
operator|.
name|VARCHAR_2000
argument_list|,
name|InferTypes
operator|.
name|ANY_NULLABLE
argument_list|,
name|OperandTypes
operator|.
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|ANY
argument_list|)
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|Optionality
operator|.
name|OPTIONAL
argument_list|)
expr_stmt|;
name|this
operator|.
name|nullClause
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|nullClause
argument_list|)
expr_stmt|;
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
assert|assert
name|call
operator|.
name|operandCount
argument_list|()
operator|==
literal|1
assert|;
specifier|final
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startFunCall
argument_list|(
literal|"JSON_ARRAYAGG"
argument_list|)
decl_stmt|;
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
name|keyword
argument_list|(
name|nullClause
operator|.
name|sql
argument_list|)
expr_stmt|;
name|writer
operator|.
name|endFunCall
argument_list|(
name|frame
argument_list|)
expr_stmt|;
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
comment|// To prevent operator rewriting by SqlFunction#deriveType.
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
name|RelDataType
name|nodeType
init|=
name|validator
operator|.
name|deriveType
argument_list|(
name|scope
argument_list|,
name|operand
argument_list|)
decl_stmt|;
operator|(
operator|(
name|SqlValidatorImpl
operator|)
name|validator
operator|)
operator|.
name|setValidatedNodeType
argument_list|(
name|operand
argument_list|,
name|nodeType
argument_list|)
expr_stmt|;
block|}
return|return
name|validateOperands
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlCall
name|createCall
parameter_list|(
name|SqlLiteral
name|functionQualifier
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
modifier|...
name|operands
parameter_list|)
block|{
assert|assert
name|operands
operator|.
name|length
operator|==
literal|1
operator|||
name|operands
operator|.
name|length
operator|==
literal|2
assert|;
specifier|final
name|SqlNode
name|valueExpr
init|=
name|operands
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|operands
operator|.
name|length
operator|==
literal|2
condition|)
block|{
specifier|final
name|SqlNode
name|orderList
init|=
name|operands
index|[
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|orderList
operator|!=
literal|null
condition|)
block|{
comment|// call has an order by clause, e.g. json_arrayagg(col_1 order by col_1)
return|return
name|SqlStdOperatorTable
operator|.
name|WITHIN_GROUP
operator|.
name|createCall
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
name|createCall_
argument_list|(
name|functionQualifier
argument_list|,
name|pos
argument_list|,
name|valueExpr
argument_list|)
argument_list|,
name|orderList
argument_list|)
return|;
block|}
block|}
return|return
name|createCall_
argument_list|(
name|functionQualifier
argument_list|,
name|pos
argument_list|,
name|valueExpr
argument_list|)
return|;
block|}
specifier|private
name|SqlCall
name|createCall_
parameter_list|(
name|SqlLiteral
name|functionQualifier
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
name|valueExpr
parameter_list|)
block|{
return|return
name|super
operator|.
name|createCall
argument_list|(
name|functionQualifier
argument_list|,
name|pos
argument_list|,
name|valueExpr
argument_list|)
return|;
block|}
specifier|public
name|SqlJsonArrayAggAggFunction
name|with
parameter_list|(
name|SqlJsonConstructorNullClause
name|nullClause
parameter_list|)
block|{
return|return
name|this
operator|.
name|nullClause
operator|==
name|nullClause
condition|?
name|this
else|:
operator|new
name|SqlJsonArrayAggAggFunction
argument_list|(
name|getKind
argument_list|()
argument_list|,
name|nullClause
argument_list|)
return|;
block|}
specifier|public
name|SqlJsonConstructorNullClause
name|getNullClause
parameter_list|()
block|{
return|return
name|nullClause
return|;
block|}
block|}
end_class

end_unit

