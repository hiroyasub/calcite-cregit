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
name|adapter
operator|.
name|druid
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
name|rex
operator|.
name|RexCall
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
name|rex
operator|.
name|RexLiteral
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
name|rex
operator|.
name|RexNode
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
name|SqlOperator
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
name|fun
operator|.
name|SqlStdOperatorTable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Converts Calcite SUBSTRING call to Druid Expression when possible  */
end_comment

begin_class
specifier|public
class|class
name|SubstringOperatorConversion
implements|implements
name|DruidSqlOperatorConverter
block|{
annotation|@
name|Override
specifier|public
name|SqlOperator
name|calciteOperator
parameter_list|()
block|{
return|return
name|SqlStdOperatorTable
operator|.
name|SUBSTRING
return|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|String
name|toDruidExpression
parameter_list|(
name|RexNode
name|rexNode
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|DruidQuery
name|query
parameter_list|)
block|{
specifier|final
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|rexNode
decl_stmt|;
specifier|final
name|String
name|arg
init|=
name|DruidExpressions
operator|.
name|toDruidExpression
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|rowType
argument_list|,
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|arg
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|String
name|startIndex
decl_stmt|;
specifier|final
name|String
name|length
decl_stmt|;
comment|// SQL is 1-indexed, Druid is 0-indexed.
if|if
condition|(
operator|!
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|LITERAL
argument_list|)
condition|)
block|{
specifier|final
name|String
name|arg1
init|=
name|DruidExpressions
operator|.
name|toDruidExpression
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|rowType
argument_list|,
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|arg1
operator|==
literal|null
condition|)
block|{
comment|// can not infer start index expression bailout.
return|return
literal|null
return|;
block|}
name|startIndex
operator|=
name|DruidQuery
operator|.
name|format
argument_list|(
literal|"(%s - 1)"
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|startIndex
operator|=
name|DruidExpressions
operator|.
name|numberLiteral
argument_list|(
name|RexLiteral
operator|.
name|intValue
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|2
condition|)
block|{
comment|//case substring from start index with length
if|if
condition|(
operator|!
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|LITERAL
argument_list|)
condition|)
block|{
comment|// case it is an expression try to parse it
name|length
operator|=
name|DruidExpressions
operator|.
name|toDruidExpression
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|rowType
argument_list|,
name|query
argument_list|)
expr_stmt|;
if|if
condition|(
name|length
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
comment|// case length is a constant
name|length
operator|=
name|DruidExpressions
operator|.
name|numberLiteral
argument_list|(
name|RexLiteral
operator|.
name|intValue
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//case substring from index to the end
name|length
operator|=
name|DruidExpressions
operator|.
name|numberLiteral
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|DruidQuery
operator|.
name|format
argument_list|(
literal|"substring(%s, %s, %s)"
argument_list|,
name|arg
argument_list|,
name|startIndex
argument_list|,
name|length
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SubstringOperatorConversion.java
end_comment

end_unit

