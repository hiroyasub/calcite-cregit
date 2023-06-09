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
name|util
operator|.
name|SqlBasicVisitor
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
name|util
operator|.
name|SqlVisitor
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
name|ImmutableNullableList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Parse tree node for "{@code FOR SYSTEM_TIME AS OF}" temporal clause.  */
end_comment

begin_class
specifier|public
class|class
name|SqlSnapshot
extends|extends
name|SqlCall
block|{
specifier|private
specifier|static
specifier|final
name|int
name|OPERAND_TABLE_REF
init|=
literal|0
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|OPERAND_PERIOD
init|=
literal|1
decl_stmt|;
comment|//~ Instance fields -------------------------------------------
specifier|private
name|SqlNode
name|tableRef
decl_stmt|;
specifier|private
name|SqlNode
name|period
decl_stmt|;
comment|/** Creates a SqlSnapshot. */
specifier|public
name|SqlSnapshot
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
name|tableRef
parameter_list|,
name|SqlNode
name|period
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|tableRef
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|tableRef
argument_list|,
literal|"tableRef"
argument_list|)
expr_stmt|;
name|this
operator|.
name|period
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|period
argument_list|,
literal|"period"
argument_list|)
expr_stmt|;
block|}
comment|// ~ Methods
annotation|@
name|Override
specifier|public
name|SqlOperator
name|getOperator
parameter_list|()
block|{
return|return
name|SqlSnapshotOperator
operator|.
name|INSTANCE
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|SqlNode
argument_list|>
name|getOperandList
parameter_list|()
block|{
return|return
name|ImmutableNullableList
operator|.
name|of
argument_list|(
name|tableRef
argument_list|,
name|period
argument_list|)
return|;
block|}
specifier|public
name|SqlNode
name|getTableRef
parameter_list|()
block|{
return|return
name|tableRef
return|;
block|}
specifier|public
name|SqlNode
name|getPeriod
parameter_list|()
block|{
return|return
name|period
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOperand
parameter_list|(
name|int
name|i
parameter_list|,
annotation|@
name|Nullable
name|SqlNode
name|operand
parameter_list|)
block|{
switch|switch
condition|(
name|i
condition|)
block|{
case|case
name|OPERAND_TABLE_REF
case|:
name|tableRef
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|operand
argument_list|,
literal|"operand"
argument_list|)
expr_stmt|;
break|break;
case|case
name|OPERAND_PERIOD
case|:
name|period
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|operand
argument_list|,
literal|"operand"
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|i
argument_list|)
throw|;
block|}
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
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
name|getOperator
argument_list|()
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|this
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * An operator describing a FOR SYSTEM_TIME specification.    */
specifier|public
specifier|static
class|class
name|SqlSnapshotOperator
extends|extends
name|SqlOperator
block|{
specifier|public
specifier|static
specifier|final
name|SqlSnapshotOperator
name|INSTANCE
init|=
operator|new
name|SqlSnapshotOperator
argument_list|()
decl_stmt|;
specifier|private
name|SqlSnapshotOperator
parameter_list|()
block|{
name|super
argument_list|(
literal|"SNAPSHOT"
argument_list|,
name|SqlKind
operator|.
name|SNAPSHOT
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SqlSyntax
name|getSyntax
parameter_list|()
block|{
return|return
name|SqlSyntax
operator|.
name|SPECIAL
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"argument.type.incompatible"
argument_list|)
annotation|@
name|Override
specifier|public
name|SqlCall
name|createCall
parameter_list|(
annotation|@
name|Nullable
name|SqlLiteral
name|functionQualifier
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
annotation|@
name|Nullable
name|SqlNode
modifier|...
name|operands
parameter_list|)
block|{
assert|assert
name|functionQualifier
operator|==
literal|null
assert|;
assert|assert
name|operands
operator|.
name|length
operator|==
literal|2
assert|;
return|return
operator|new
name|SqlSnapshot
argument_list|(
name|pos
argument_list|,
name|operands
index|[
literal|0
index|]
argument_list|,
name|operands
index|[
literal|1
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|R
parameter_list|>
name|void
name|acceptCall
parameter_list|(
name|SqlVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|boolean
name|onlyExpressions
parameter_list|,
name|SqlBasicVisitor
operator|.
name|ArgHandler
argument_list|<
name|R
argument_list|>
name|argHandler
parameter_list|)
block|{
if|if
condition|(
name|onlyExpressions
condition|)
block|{
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operands
init|=
name|call
operator|.
name|getOperandList
argument_list|()
decl_stmt|;
comment|// skip the first operand
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|operands
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|argHandler
operator|.
name|visitChild
argument_list|(
name|visitor
argument_list|,
name|call
argument_list|,
name|i
argument_list|,
name|operands
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|super
operator|.
name|acceptCall
argument_list|(
name|visitor
argument_list|,
name|call
argument_list|,
literal|false
argument_list|,
name|argHandler
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|SqlSnapshot
name|snapshot
init|=
operator|(
name|SqlSnapshot
operator|)
name|call
decl_stmt|;
name|SqlNode
name|tableRef
init|=
name|snapshot
operator|.
name|tableRef
decl_stmt|;
if|if
condition|(
name|tableRef
operator|instanceof
name|SqlBasicCall
operator|&&
operator|(
operator|(
name|SqlBasicCall
operator|)
name|tableRef
operator|)
operator|.
name|getOperator
argument_list|()
operator|instanceof
name|SqlAsOperator
condition|)
block|{
name|SqlBasicCall
name|basicCall
init|=
operator|(
name|SqlBasicCall
operator|)
name|tableRef
decl_stmt|;
name|basicCall
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
name|writer
operator|.
name|setNeedWhitespace
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|writeForSystemTimeAsOf
argument_list|(
name|writer
argument_list|,
name|snapshot
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"AS"
argument_list|)
expr_stmt|;
name|basicCall
operator|.
name|operand
argument_list|(
literal|1
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
name|tableRef
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
name|writeForSystemTimeAsOf
argument_list|(
name|writer
argument_list|,
name|snapshot
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|writeForSystemTimeAsOf
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlSnapshot
name|snapshot
parameter_list|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"FOR SYSTEM_TIME AS OF"
argument_list|)
expr_stmt|;
name|snapshot
operator|.
name|period
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
block|}
end_class

end_unit

