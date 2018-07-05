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
name|ImmutableNullableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * SqlNode for MATCH_RECOGNIZE clause.  */
end_comment

begin_class
specifier|public
class|class
name|SqlMatchRecognize
extends|extends
name|SqlCall
block|{
specifier|public
specifier|static
specifier|final
name|int
name|OPERAND_TABLE_REF
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|OPERAND_PATTERN
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|OPERAND_STRICT_START
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|OPERAND_STRICT_END
init|=
literal|3
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|OPERAND_PATTERN_DEFINES
init|=
literal|4
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|OPERAND_MEASURES
init|=
literal|5
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|OPERAND_AFTER
init|=
literal|6
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|OPERAND_SUBSET
init|=
literal|7
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|OPERAND_ROWS_PER_MATCH
init|=
literal|8
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|OPERAND_PARTITION_BY
init|=
literal|9
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|OPERAND_ORDER_BY
init|=
literal|10
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|OPERAND_INTERVAL
init|=
literal|11
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlPrefixOperator
name|SKIP_TO_FIRST
init|=
operator|new
name|SqlPrefixOperator
argument_list|(
literal|"SKIP TO FIRST"
argument_list|,
name|SqlKind
operator|.
name|SKIP_TO_FIRST
argument_list|,
literal|20
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlPrefixOperator
name|SKIP_TO_LAST
init|=
operator|new
name|SqlPrefixOperator
argument_list|(
literal|"SKIP TO LAST"
argument_list|,
name|SqlKind
operator|.
name|SKIP_TO_LAST
argument_list|,
literal|20
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|//~ Instance fields -------------------------------------------
specifier|private
name|SqlNode
name|tableRef
decl_stmt|;
specifier|private
name|SqlNode
name|pattern
decl_stmt|;
specifier|private
name|SqlLiteral
name|strictStart
decl_stmt|;
specifier|private
name|SqlLiteral
name|strictEnd
decl_stmt|;
specifier|private
name|SqlNodeList
name|patternDefList
decl_stmt|;
specifier|private
name|SqlNodeList
name|measureList
decl_stmt|;
specifier|private
name|SqlNode
name|after
decl_stmt|;
specifier|private
name|SqlNodeList
name|subsetList
decl_stmt|;
specifier|private
name|SqlLiteral
name|rowsPerMatch
decl_stmt|;
specifier|private
name|SqlNodeList
name|partitionList
decl_stmt|;
specifier|private
name|SqlNodeList
name|orderList
decl_stmt|;
specifier|private
name|SqlLiteral
name|interval
decl_stmt|;
comment|/** Creates a SqlMatchRecognize. */
specifier|public
name|SqlMatchRecognize
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
name|tableRef
parameter_list|,
name|SqlNode
name|pattern
parameter_list|,
name|SqlLiteral
name|strictStart
parameter_list|,
name|SqlLiteral
name|strictEnd
parameter_list|,
name|SqlNodeList
name|patternDefList
parameter_list|,
name|SqlNodeList
name|measureList
parameter_list|,
name|SqlNode
name|after
parameter_list|,
name|SqlNodeList
name|subsetList
parameter_list|,
name|SqlLiteral
name|rowsPerMatch
parameter_list|,
name|SqlNodeList
name|partitionList
parameter_list|,
name|SqlNodeList
name|orderList
parameter_list|,
name|SqlLiteral
name|interval
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|pattern
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
name|this
operator|.
name|strictStart
operator|=
name|strictStart
expr_stmt|;
name|this
operator|.
name|strictEnd
operator|=
name|strictEnd
expr_stmt|;
name|this
operator|.
name|patternDefList
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|patternDefList
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|patternDefList
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|measureList
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|measureList
argument_list|)
expr_stmt|;
name|this
operator|.
name|after
operator|=
name|after
expr_stmt|;
name|this
operator|.
name|subsetList
operator|=
name|subsetList
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|rowsPerMatch
operator|==
literal|null
operator|||
name|rowsPerMatch
operator|.
name|value
operator|instanceof
name|RowsPerMatchOption
argument_list|)
expr_stmt|;
name|this
operator|.
name|rowsPerMatch
operator|=
name|rowsPerMatch
expr_stmt|;
name|this
operator|.
name|partitionList
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|partitionList
argument_list|)
expr_stmt|;
name|this
operator|.
name|orderList
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|orderList
argument_list|)
expr_stmt|;
name|this
operator|.
name|interval
operator|=
name|interval
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
name|SqlMatchRecognizeOperator
operator|.
name|INSTANCE
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|SqlKind
operator|.
name|MATCH_RECOGNIZE
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
name|pattern
argument_list|,
name|strictStart
argument_list|,
name|strictEnd
argument_list|,
name|patternDefList
argument_list|,
name|measureList
argument_list|,
name|after
argument_list|,
name|subsetList
argument_list|,
name|partitionList
argument_list|,
name|orderList
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
annotation|@
name|Override
specifier|public
name|void
name|validate
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
name|validator
operator|.
name|validateMatchRecognize
argument_list|(
name|this
argument_list|)
expr_stmt|;
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
argument_list|)
expr_stmt|;
break|break;
case|case
name|OPERAND_PATTERN
case|:
name|pattern
operator|=
name|operand
expr_stmt|;
break|break;
case|case
name|OPERAND_STRICT_START
case|:
name|strictStart
operator|=
operator|(
name|SqlLiteral
operator|)
name|operand
expr_stmt|;
break|break;
case|case
name|OPERAND_STRICT_END
case|:
name|strictEnd
operator|=
operator|(
name|SqlLiteral
operator|)
name|operand
expr_stmt|;
break|break;
case|case
name|OPERAND_PATTERN_DEFINES
case|:
name|patternDefList
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
operator|(
name|SqlNodeList
operator|)
name|operand
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|patternDefList
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|OPERAND_MEASURES
case|:
name|measureList
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
operator|(
name|SqlNodeList
operator|)
name|operand
argument_list|)
expr_stmt|;
break|break;
case|case
name|OPERAND_AFTER
case|:
name|after
operator|=
name|operand
expr_stmt|;
break|break;
case|case
name|OPERAND_SUBSET
case|:
name|subsetList
operator|=
operator|(
name|SqlNodeList
operator|)
name|operand
expr_stmt|;
break|break;
case|case
name|OPERAND_ROWS_PER_MATCH
case|:
name|rowsPerMatch
operator|=
operator|(
name|SqlLiteral
operator|)
name|operand
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|rowsPerMatch
operator|==
literal|null
operator|||
name|rowsPerMatch
operator|.
name|value
operator|instanceof
name|RowsPerMatchOption
argument_list|)
expr_stmt|;
break|break;
case|case
name|OPERAND_PARTITION_BY
case|:
name|partitionList
operator|=
operator|(
name|SqlNodeList
operator|)
name|operand
expr_stmt|;
break|break;
case|case
name|OPERAND_ORDER_BY
case|:
name|orderList
operator|=
operator|(
name|SqlNodeList
operator|)
name|operand
expr_stmt|;
break|break;
case|case
name|OPERAND_INTERVAL
case|:
name|interval
operator|=
operator|(
name|SqlLiteral
operator|)
name|operand
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
name|Nonnull
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
name|getPattern
parameter_list|()
block|{
return|return
name|pattern
return|;
block|}
specifier|public
name|SqlLiteral
name|getStrictStart
parameter_list|()
block|{
return|return
name|strictStart
return|;
block|}
specifier|public
name|SqlLiteral
name|getStrictEnd
parameter_list|()
block|{
return|return
name|strictEnd
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|SqlNodeList
name|getPatternDefList
parameter_list|()
block|{
return|return
name|patternDefList
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|SqlNodeList
name|getMeasureList
parameter_list|()
block|{
return|return
name|measureList
return|;
block|}
specifier|public
name|SqlNode
name|getAfter
parameter_list|()
block|{
return|return
name|after
return|;
block|}
specifier|public
name|SqlNodeList
name|getSubsetList
parameter_list|()
block|{
return|return
name|subsetList
return|;
block|}
specifier|public
name|SqlLiteral
name|getRowsPerMatch
parameter_list|()
block|{
return|return
name|rowsPerMatch
return|;
block|}
specifier|public
name|SqlNodeList
name|getPartitionList
parameter_list|()
block|{
return|return
name|partitionList
return|;
block|}
specifier|public
name|SqlNodeList
name|getOrderList
parameter_list|()
block|{
return|return
name|orderList
return|;
block|}
specifier|public
name|SqlLiteral
name|getInterval
parameter_list|()
block|{
return|return
name|interval
return|;
block|}
comment|/**    * Options for {@code ROWS PER MATCH}.    */
specifier|public
enum|enum
name|RowsPerMatchOption
block|{
name|ONE_ROW
argument_list|(
literal|"ONE ROW PER MATCH"
argument_list|)
block|,
name|ALL_ROWS
argument_list|(
literal|"ALL ROWS PER MATCH"
argument_list|)
block|;
specifier|private
specifier|final
name|String
name|sql
decl_stmt|;
name|RowsPerMatchOption
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|sql
return|;
block|}
specifier|public
name|SqlLiteral
name|symbol
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
name|SqlLiteral
operator|.
name|createSymbol
argument_list|(
name|this
argument_list|,
name|pos
argument_list|)
return|;
block|}
block|}
comment|/**    * Options for {@code AFTER MATCH} clause.    */
specifier|public
enum|enum
name|AfterOption
block|{
name|SKIP_TO_NEXT_ROW
argument_list|(
literal|"SKIP TO NEXT ROW"
argument_list|)
block|,
name|SKIP_PAST_LAST_ROW
argument_list|(
literal|"SKIP PAST LAST ROW"
argument_list|)
block|;
specifier|private
specifier|final
name|String
name|sql
decl_stmt|;
name|AfterOption
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|sql
return|;
block|}
comment|/**      * Creates a parse-tree node representing an occurrence of this symbol      * at a particular position in the parsed text.      */
specifier|public
name|SqlLiteral
name|symbol
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
name|SqlLiteral
operator|.
name|createSymbol
argument_list|(
name|this
argument_list|,
name|pos
argument_list|)
return|;
block|}
block|}
comment|/**    * An operator describing a MATCH_RECOGNIZE specification.    */
specifier|public
specifier|static
class|class
name|SqlMatchRecognizeOperator
extends|extends
name|SqlOperator
block|{
specifier|public
specifier|static
specifier|final
name|SqlMatchRecognizeOperator
name|INSTANCE
init|=
operator|new
name|SqlMatchRecognizeOperator
argument_list|()
decl_stmt|;
specifier|private
name|SqlMatchRecognizeOperator
parameter_list|()
block|{
name|super
argument_list|(
literal|"MATCH_RECOGNIZE"
argument_list|,
name|SqlKind
operator|.
name|MATCH_RECOGNIZE
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
name|functionQualifier
operator|==
literal|null
assert|;
assert|assert
name|operands
operator|.
name|length
operator|==
literal|12
assert|;
return|return
operator|new
name|SqlMatchRecognize
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
argument_list|,
operator|(
name|SqlLiteral
operator|)
name|operands
index|[
literal|2
index|]
argument_list|,
operator|(
name|SqlLiteral
operator|)
name|operands
index|[
literal|3
index|]
argument_list|,
operator|(
name|SqlNodeList
operator|)
name|operands
index|[
literal|4
index|]
argument_list|,
operator|(
name|SqlNodeList
operator|)
name|operands
index|[
literal|5
index|]
argument_list|,
name|operands
index|[
literal|6
index|]
argument_list|,
operator|(
name|SqlNodeList
operator|)
name|operands
index|[
literal|7
index|]
argument_list|,
operator|(
name|SqlLiteral
operator|)
name|operands
index|[
literal|8
index|]
argument_list|,
operator|(
name|SqlNodeList
operator|)
name|operands
index|[
literal|9
index|]
argument_list|,
operator|(
name|SqlNodeList
operator|)
name|operands
index|[
literal|10
index|]
argument_list|,
operator|(
name|SqlLiteral
operator|)
name|operands
index|[
literal|11
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
for|for
control|(
name|int
name|i
init|=
literal|0
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
name|SqlNode
name|operand
init|=
name|operands
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|operand
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
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
name|operand
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
name|onlyExpressions
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
name|validateCall
parameter_list|(
name|SqlCall
name|call
parameter_list|,
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlValidatorScope
name|operandScope
parameter_list|)
block|{
name|validator
operator|.
name|validateMatchRecognize
argument_list|(
name|call
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
specifier|final
name|SqlMatchRecognize
name|pattern
init|=
operator|(
name|SqlMatchRecognize
operator|)
name|call
decl_stmt|;
name|pattern
operator|.
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
specifier|final
name|SqlWriter
operator|.
name|Frame
name|mrFrame
init|=
name|writer
operator|.
name|startFunCall
argument_list|(
literal|"MATCH_RECOGNIZE"
argument_list|)
decl_stmt|;
if|if
condition|(
name|pattern
operator|.
name|partitionList
operator|!=
literal|null
operator|&&
name|pattern
operator|.
name|partitionList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"PARTITION BY"
argument_list|)
expr_stmt|;
specifier|final
name|SqlWriter
operator|.
name|Frame
name|partitionFrame
init|=
name|writer
operator|.
name|startList
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|pattern
operator|.
name|partitionList
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
name|endList
argument_list|(
name|partitionFrame
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pattern
operator|.
name|orderList
operator|!=
literal|null
operator|&&
name|pattern
operator|.
name|orderList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"ORDER BY"
argument_list|)
expr_stmt|;
specifier|final
name|SqlWriter
operator|.
name|Frame
name|orderFrame
init|=
name|writer
operator|.
name|startList
argument_list|(
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|ORDER_BY_LIST
argument_list|)
decl_stmt|;
name|unparseListClause
argument_list|(
name|writer
argument_list|,
name|pattern
operator|.
name|orderList
argument_list|)
expr_stmt|;
name|writer
operator|.
name|endList
argument_list|(
name|orderFrame
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pattern
operator|.
name|measureList
operator|!=
literal|null
operator|&&
name|pattern
operator|.
name|measureList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"MEASURES"
argument_list|)
expr_stmt|;
specifier|final
name|SqlWriter
operator|.
name|Frame
name|measureFrame
init|=
name|writer
operator|.
name|startList
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|pattern
operator|.
name|measureList
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
name|endList
argument_list|(
name|measureFrame
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pattern
operator|.
name|rowsPerMatch
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
name|pattern
operator|.
name|rowsPerMatch
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
if|if
condition|(
name|pattern
operator|.
name|after
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"AFTER MATCH"
argument_list|)
expr_stmt|;
name|pattern
operator|.
name|after
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
name|newlineAndIndent
argument_list|()
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"PATTERN"
argument_list|)
expr_stmt|;
name|SqlWriter
operator|.
name|Frame
name|patternFrame
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
if|if
condition|(
name|pattern
operator|.
name|strictStart
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|"^"
argument_list|)
expr_stmt|;
block|}
name|pattern
operator|.
name|pattern
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
if|if
condition|(
name|pattern
operator|.
name|strictEnd
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|"$"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|endList
argument_list|(
name|patternFrame
argument_list|)
expr_stmt|;
if|if
condition|(
name|pattern
operator|.
name|interval
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|"WITHIN"
argument_list|)
expr_stmt|;
name|pattern
operator|.
name|interval
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
if|if
condition|(
name|pattern
operator|.
name|subsetList
operator|!=
literal|null
operator|&&
name|pattern
operator|.
name|subsetList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"SUBSET"
argument_list|)
expr_stmt|;
name|SqlWriter
operator|.
name|Frame
name|subsetFrame
init|=
name|writer
operator|.
name|startList
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|pattern
operator|.
name|subsetList
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
name|endList
argument_list|(
name|subsetFrame
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"DEFINE"
argument_list|)
expr_stmt|;
specifier|final
name|SqlWriter
operator|.
name|Frame
name|patternDefFrame
init|=
name|writer
operator|.
name|startList
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|final
name|SqlNodeList
name|newDefineList
init|=
operator|new
name|SqlNodeList
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
decl_stmt|;
for|for
control|(
name|SqlNode
name|node
range|:
name|pattern
operator|.
name|getPatternDefList
argument_list|()
control|)
block|{
specifier|final
name|SqlCall
name|call2
init|=
operator|(
name|SqlCall
operator|)
name|node
decl_stmt|;
comment|// swap the position of alias position in AS operator
name|newDefineList
operator|.
name|add
argument_list|(
name|call2
operator|.
name|getOperator
argument_list|()
operator|.
name|createCall
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
name|call2
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
argument_list|,
name|call2
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|newDefineList
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
name|endList
argument_list|(
name|patternDefFrame
argument_list|)
expr_stmt|;
name|writer
operator|.
name|endList
argument_list|(
name|mrFrame
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlMatchRecognize.java
end_comment

end_unit

