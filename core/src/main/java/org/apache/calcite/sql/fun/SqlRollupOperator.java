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
name|SqlInternalOperator
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
name|SqlNodeList
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

begin_comment
comment|/**  * Operator that appears in a {@code GROUP BY} clause: {@code CUBE},  * {@code ROLLUP}, {@code GROUPING SETS}.  */
end_comment

begin_class
class|class
name|SqlRollupOperator
extends|extends
name|SqlInternalOperator
block|{
name|SqlRollupOperator
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
literal|4
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
switch|switch
condition|(
name|kind
condition|)
block|{
case|case
name|ROLLUP
case|:
if|if
condition|(
operator|!
name|writer
operator|.
name|getDialect
argument_list|()
operator|.
name|supportsAggregateFunction
argument_list|(
name|kind
argument_list|)
operator|&&
name|writer
operator|.
name|getDialect
argument_list|()
operator|.
name|supportsGroupByWithRollup
argument_list|()
condition|)
block|{
comment|// MySQL version 5: generate "GROUP BY x, y WITH ROLLUP".
comment|// MySQL version 8 and higher is SQL-compliant,
comment|// so generate "GROUP BY ROLLUP(x, y)"
name|unparseKeyword
argument_list|(
name|writer
argument_list|,
name|call
argument_list|,
literal|"WITH ROLLUP"
argument_list|)
expr_stmt|;
return|return;
block|}
break|break;
case|case
name|CUBE
case|:
if|if
condition|(
operator|!
name|writer
operator|.
name|getDialect
argument_list|()
operator|.
name|supportsAggregateFunction
argument_list|(
name|kind
argument_list|)
operator|&&
name|writer
operator|.
name|getDialect
argument_list|()
operator|.
name|supportsGroupByWithCube
argument_list|()
condition|)
block|{
comment|// Spark SQL: generate "GROUP BY x, y WITH CUBE".
name|unparseKeyword
argument_list|(
name|writer
argument_list|,
name|call
argument_list|,
literal|"WITH CUBE"
argument_list|)
expr_stmt|;
return|return;
block|}
break|break;
case|case
name|GROUP_BY_DISTINCT
case|:
name|writer
operator|.
name|keyword
argument_list|(
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|SqlNodeList
name|groupBy
init|=
operator|new
name|SqlNodeList
argument_list|(
name|call
operator|.
name|getOperandList
argument_list|()
argument_list|,
name|call
operator|.
name|getParserPosition
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|list
argument_list|(
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|GROUP_BY_LIST
argument_list|,
name|SqlWriter
operator|.
name|COMMA
argument_list|,
name|groupBy
argument_list|)
expr_stmt|;
return|return;
default|default:
break|break;
block|}
name|unparseCube
argument_list|(
name|writer
argument_list|,
name|call
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|unparseKeyword
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|String
name|keyword
parameter_list|)
block|{
specifier|final
name|SqlWriter
operator|.
name|Frame
name|groupFrame
init|=
name|writer
operator|.
name|startList
argument_list|(
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|GROUP_BY_LIST
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
literal|2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|endList
argument_list|(
name|groupFrame
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
name|keyword
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|unparseCube
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
name|call
operator|.
name|getOperator
argument_list|()
operator|.
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
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|FUN_CALL
argument_list|,
literal|"("
argument_list|,
literal|")"
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
if|if
condition|(
name|operand
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|ROW
condition|)
block|{
specifier|final
name|SqlWriter
operator|.
name|Frame
name|frame2
init|=
name|writer
operator|.
name|startList
argument_list|(
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|SIMPLE
argument_list|,
literal|"("
argument_list|,
literal|")"
argument_list|)
decl_stmt|;
for|for
control|(
name|SqlNode
name|operand2
range|:
operator|(
operator|(
name|SqlCall
operator|)
name|operand
operator|)
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
name|operand2
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
name|writer
operator|.
name|endList
argument_list|(
name|frame2
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|operand
operator|instanceof
name|SqlNodeList
operator|&&
operator|(
operator|(
name|SqlNodeList
operator|)
name|operand
operator|)
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"()"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|operand
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

end_unit

