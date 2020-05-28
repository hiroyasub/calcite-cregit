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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|SqlValidatorUtil
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|BiConsumer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Parse tree node that represents a PIVOT applied to a table reference  * (or sub-query).  *  *<p>Syntax:  *<blockquote>{@code  * SELECT *  * FROM query PIVOT (agg, ... FOR axis, ... IN (in, ...)) AS alias}  *</blockquote>  */
end_comment

begin_class
specifier|public
class|class
name|SqlPivot
extends|extends
name|SqlCall
block|{
specifier|public
name|SqlNode
name|query
decl_stmt|;
specifier|public
specifier|final
name|SqlNodeList
name|aggList
decl_stmt|;
specifier|public
specifier|final
name|SqlNodeList
name|axisList
decl_stmt|;
specifier|public
specifier|final
name|SqlNodeList
name|inList
decl_stmt|;
specifier|static
specifier|final
name|Operator
name|OPERATOR
init|=
operator|new
name|Operator
argument_list|(
name|SqlKind
operator|.
name|PIVOT
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlPivot
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
name|query
parameter_list|,
name|SqlNodeList
name|aggList
parameter_list|,
name|SqlNodeList
name|axisList
parameter_list|,
name|SqlNodeList
name|inList
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|this
operator|.
name|aggList
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|aggList
argument_list|)
expr_stmt|;
name|this
operator|.
name|axisList
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|axisList
argument_list|)
expr_stmt|;
name|this
operator|.
name|inList
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|inList
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|SqlOperator
name|getOperator
parameter_list|()
block|{
return|return
name|OPERATOR
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
name|query
argument_list|,
name|aggList
argument_list|,
name|axisList
argument_list|,
name|inList
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"nullness"
argument_list|)
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
comment|// Only 'query' is mutable. (It is required for validation.)
switch|switch
condition|(
name|i
condition|)
block|{
case|case
literal|0
case|:
name|query
operator|=
name|operand
expr_stmt|;
break|break;
default|default:
name|super
operator|.
name|setOperand
argument_list|(
name|i
argument_list|,
name|operand
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
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
name|query
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"PIVOT"
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
name|aggList
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
name|sep
argument_list|(
literal|"FOR"
argument_list|)
expr_stmt|;
comment|// force parentheses if there is more than one axis
specifier|final
name|int
name|leftPrec1
init|=
name|axisList
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|?
literal|1
else|:
literal|0
decl_stmt|;
name|axisList
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"IN"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|list
argument_list|(
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|PARENTHESES
argument_list|,
name|SqlWriter
operator|.
name|COMMA
argument_list|,
name|stripList
argument_list|(
name|inList
argument_list|)
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
specifier|private
specifier|static
name|SqlNodeList
name|stripList
parameter_list|(
name|SqlNodeList
name|list
parameter_list|)
block|{
return|return
name|list
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|SqlPivot
operator|::
name|strip
argument_list|)
operator|.
name|collect
argument_list|(
name|SqlNode
operator|.
name|toList
argument_list|(
name|list
operator|.
name|pos
argument_list|)
argument_list|)
return|;
block|}
comment|/** Converts a single-element SqlNodeList to its constituent node.    * For example, "(1)" becomes "1";    * "(2) as a" becomes "2 as a";    * "(3, 4)" remains "(3, 4)";    * "(5, 6) as b" remains "(5, 6) as b". */
specifier|private
specifier|static
name|SqlNode
name|strip
parameter_list|(
name|SqlNode
name|e
parameter_list|)
block|{
switch|switch
condition|(
name|e
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|AS
case|:
specifier|final
name|SqlCall
name|call
init|=
operator|(
name|SqlCall
operator|)
name|e
decl_stmt|;
specifier|final
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
return|return
name|SqlStdOperatorTable
operator|.
name|AS
operator|.
name|createCall
argument_list|(
name|e
operator|.
name|pos
argument_list|,
name|strip
argument_list|(
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
default|default:
if|if
condition|(
name|e
operator|instanceof
name|SqlNodeList
operator|&&
operator|(
operator|(
name|SqlNodeList
operator|)
name|e
operator|)
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
operator|(
operator|(
name|SqlNodeList
operator|)
name|e
operator|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
return|return
name|e
return|;
block|}
block|}
comment|/** Returns the aggregate list as (alias, call) pairs.    * If there is no 'AS', alias is null. */
specifier|public
name|void
name|forEachAgg
parameter_list|(
name|BiConsumer
argument_list|<
annotation|@
name|Nullable
name|String
argument_list|,
name|SqlNode
argument_list|>
name|consumer
parameter_list|)
block|{
for|for
control|(
name|SqlNode
name|agg
range|:
name|aggList
control|)
block|{
specifier|final
name|SqlNode
name|call
init|=
name|SqlUtil
operator|.
name|stripAs
argument_list|(
name|agg
argument_list|)
decl_stmt|;
specifier|final
name|String
name|alias
init|=
name|SqlValidatorUtil
operator|.
name|getAlias
argument_list|(
name|agg
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|alias
argument_list|,
name|call
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Returns the value list as (alias, node list) pairs. */
specifier|public
name|void
name|forEachNameValues
parameter_list|(
name|BiConsumer
argument_list|<
name|String
argument_list|,
name|SqlNodeList
argument_list|>
name|consumer
parameter_list|)
block|{
for|for
control|(
name|SqlNode
name|node
range|:
name|inList
control|)
block|{
name|String
name|alias
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|AS
condition|)
block|{
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operands
init|=
operator|(
operator|(
name|SqlCall
operator|)
name|node
operator|)
operator|.
name|getOperandList
argument_list|()
decl_stmt|;
name|alias
operator|=
operator|(
operator|(
name|SqlIdentifier
operator|)
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|getSimple
argument_list|()
expr_stmt|;
name|node
operator|=
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|alias
operator|=
name|pivotAlias
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|accept
argument_list|(
name|alias
argument_list|,
name|toNodes
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|String
name|pivotAlias
parameter_list|(
name|SqlNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|SqlNodeList
condition|)
block|{
return|return
operator|(
operator|(
name|SqlNodeList
operator|)
name|node
operator|)
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|SqlPivot
operator|::
name|pivotAlias
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|"_"
argument_list|)
argument_list|)
return|;
block|}
return|return
name|node
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Converts a SqlNodeList to a list, and other nodes to a singleton list. */
specifier|private
specifier|static
name|SqlNodeList
name|toNodes
parameter_list|(
name|SqlNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|SqlNodeList
condition|)
block|{
return|return
operator|(
name|SqlNodeList
operator|)
name|node
return|;
block|}
else|else
block|{
return|return
operator|new
name|SqlNodeList
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|node
argument_list|)
argument_list|,
name|node
operator|.
name|getParserPosition
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/** Returns the set of columns that are referenced as an argument to an    * aggregate function or in a column in the {@code FOR} clause. All columns    * that are not used will become "GROUP BY" columns. */
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|usedColumnNames
parameter_list|()
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|columnNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SqlVisitor
argument_list|<
name|Void
argument_list|>
name|nameCollector
init|=
operator|new
name|SqlBasicVisitor
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|visit
parameter_list|(
name|SqlIdentifier
name|id
parameter_list|)
block|{
name|columnNames
operator|.
name|add
argument_list|(
name|Util
operator|.
name|last
argument_list|(
name|id
operator|.
name|names
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|visit
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
decl_stmt|;
for|for
control|(
name|SqlNode
name|agg
range|:
name|aggList
control|)
block|{
specifier|final
name|SqlCall
name|call
init|=
operator|(
name|SqlCall
operator|)
name|SqlUtil
operator|.
name|stripAs
argument_list|(
name|agg
argument_list|)
decl_stmt|;
name|call
operator|.
name|accept
argument_list|(
name|nameCollector
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|SqlNode
name|axis
range|:
name|axisList
control|)
block|{
name|axis
operator|.
name|accept
argument_list|(
name|nameCollector
argument_list|)
expr_stmt|;
block|}
return|return
name|columnNames
return|;
block|}
comment|/** Pivot operator. */
specifier|static
class|class
name|Operator
extends|extends
name|SqlSpecialOperator
block|{
name|Operator
parameter_list|(
name|SqlKind
name|kind
parameter_list|)
block|{
name|super
argument_list|(
name|kind
operator|.
name|name
argument_list|()
argument_list|,
name|kind
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

