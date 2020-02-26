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
name|splunk
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
name|adapter
operator|.
name|splunk
operator|.
name|util
operator|.
name|StringUtils
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
name|plan
operator|.
name|RelOptCluster
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
name|plan
operator|.
name|RelOptRule
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
name|plan
operator|.
name|RelOptRuleCall
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
name|plan
operator|.
name|RelOptRuleOperand
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
name|RelNode
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
name|core
operator|.
name|RelFactories
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
name|logical
operator|.
name|LogicalFilter
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
name|logical
operator|.
name|LogicalProject
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
name|RelDataTypeField
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
name|RexInputRef
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
name|rex
operator|.
name|RexSlot
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
name|SqlBinaryOperator
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
name|tools
operator|.
name|RelBuilderFactory
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
name|NlsString
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Planner rule to push filters and projections to Splunk.  */
end_comment

begin_class
specifier|public
class|class
name|SplunkPushDownRule
extends|extends
name|RelOptRule
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|StringUtils
operator|.
name|getClassTracer
argument_list|(
name|SplunkPushDownRule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|SqlKind
argument_list|>
name|SUPPORTED_OPS
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|SqlKind
operator|.
name|CAST
argument_list|,
name|SqlKind
operator|.
name|EQUALS
argument_list|,
name|SqlKind
operator|.
name|LESS_THAN
argument_list|,
name|SqlKind
operator|.
name|LESS_THAN_OR_EQUAL
argument_list|,
name|SqlKind
operator|.
name|GREATER_THAN
argument_list|,
name|SqlKind
operator|.
name|GREATER_THAN_OR_EQUAL
argument_list|,
name|SqlKind
operator|.
name|NOT_EQUALS
argument_list|,
name|SqlKind
operator|.
name|LIKE
argument_list|,
name|SqlKind
operator|.
name|AND
argument_list|,
name|SqlKind
operator|.
name|OR
argument_list|,
name|SqlKind
operator|.
name|NOT
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SplunkPushDownRule
name|PROJECT_ON_FILTER
init|=
operator|new
name|SplunkPushDownRule
argument_list|(
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|LogicalFilter
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|SplunkTableScan
operator|.
name|class
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"proj on filter on proj"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SplunkPushDownRule
name|FILTER_ON_PROJECT
init|=
operator|new
name|SplunkPushDownRule
argument_list|(
name|operand
argument_list|(
name|LogicalFilter
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|SplunkTableScan
operator|.
name|class
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"filter on proj"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SplunkPushDownRule
name|FILTER
init|=
operator|new
name|SplunkPushDownRule
argument_list|(
name|operand
argument_list|(
name|LogicalFilter
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|SplunkTableScan
operator|.
name|class
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"filter"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SplunkPushDownRule
name|PROJECT
init|=
operator|new
name|SplunkPushDownRule
argument_list|(
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|SplunkTableScan
operator|.
name|class
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"proj"
argument_list|)
decl_stmt|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
name|SplunkPushDownRule
parameter_list|(
name|RelOptRuleOperand
name|rule
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|this
argument_list|(
name|rule
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a SplunkPushDownRule. */
specifier|protected
name|SplunkPushDownRule
parameter_list|(
name|RelOptRuleOperand
name|rule
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|rule
argument_list|,
name|relBuilderFactory
argument_list|,
literal|"SplunkPushDownRule: "
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
comment|// ~ Methods --------------------------------------------------------------
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
name|description
argument_list|)
expr_stmt|;
name|int
name|relLength
init|=
name|call
operator|.
name|rels
operator|.
name|length
decl_stmt|;
name|SplunkTableScan
name|splunkRel
init|=
operator|(
name|SplunkTableScan
operator|)
name|call
operator|.
name|rels
index|[
name|relLength
operator|-
literal|1
index|]
decl_stmt|;
name|LogicalFilter
name|filter
decl_stmt|;
name|LogicalProject
name|topProj
init|=
literal|null
decl_stmt|;
name|LogicalProject
name|bottomProj
init|=
literal|null
decl_stmt|;
name|RelDataType
name|topRow
init|=
name|splunkRel
operator|.
name|getRowType
argument_list|()
decl_stmt|;
name|int
name|filterIdx
init|=
literal|2
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|rels
index|[
name|relLength
operator|-
literal|2
index|]
operator|instanceof
name|LogicalProject
condition|)
block|{
name|bottomProj
operator|=
operator|(
name|LogicalProject
operator|)
name|call
operator|.
name|rels
index|[
name|relLength
operator|-
literal|2
index|]
expr_stmt|;
name|filterIdx
operator|=
literal|3
expr_stmt|;
comment|// bottom projection will change the field count/order
name|topRow
operator|=
name|bottomProj
operator|.
name|getRowType
argument_list|()
expr_stmt|;
block|}
name|String
name|filterString
decl_stmt|;
if|if
condition|(
name|filterIdx
operator|<=
name|relLength
operator|&&
name|call
operator|.
name|rels
index|[
name|relLength
operator|-
name|filterIdx
index|]
operator|instanceof
name|LogicalFilter
condition|)
block|{
name|filter
operator|=
operator|(
name|LogicalFilter
operator|)
name|call
operator|.
name|rels
index|[
name|relLength
operator|-
name|filterIdx
index|]
expr_stmt|;
name|int
name|topProjIdx
init|=
name|filterIdx
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|topProjIdx
operator|<=
name|relLength
operator|&&
name|call
operator|.
name|rels
index|[
name|relLength
operator|-
name|topProjIdx
index|]
operator|instanceof
name|LogicalProject
condition|)
block|{
name|topProj
operator|=
operator|(
name|LogicalProject
operator|)
name|call
operator|.
name|rels
index|[
name|relLength
operator|-
name|topProjIdx
index|]
expr_stmt|;
block|}
name|RexCall
name|filterCall
init|=
operator|(
name|RexCall
operator|)
name|filter
operator|.
name|getCondition
argument_list|()
decl_stmt|;
name|SqlOperator
name|op
init|=
name|filterCall
operator|.
name|getOperator
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
init|=
name|filterCall
operator|.
name|getOperands
argument_list|()
decl_stmt|;
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"fieldNames: {}"
argument_list|,
name|getFieldsString
argument_list|(
name|topRow
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|getFilter
argument_list|(
name|op
argument_list|,
name|operands
argument_list|,
name|buf
argument_list|,
name|topRow
operator|.
name|getFieldNames
argument_list|()
argument_list|)
condition|)
block|{
name|filterString
operator|=
name|buf
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return;
comment|// can't handle
block|}
block|}
else|else
block|{
name|filterString
operator|=
literal|""
expr_stmt|;
block|}
comment|// top projection will change the field count/order
if|if
condition|(
name|topProj
operator|!=
literal|null
condition|)
block|{
name|topRow
operator|=
name|topProj
operator|.
name|getRowType
argument_list|()
expr_stmt|;
block|}
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"pre transformTo fieldNames: {}"
argument_list|,
name|getFieldsString
argument_list|(
name|topRow
argument_list|)
argument_list|)
expr_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|appendSearchString
argument_list|(
name|filterString
argument_list|,
name|splunkRel
argument_list|,
name|topProj
argument_list|,
name|bottomProj
argument_list|,
name|topRow
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Appends a search string.    *    * @param toAppend Search string to append    * @param splunkRel Relational expression    * @param topProj Top projection    * @param bottomProj Bottom projection    */
specifier|protected
name|RelNode
name|appendSearchString
parameter_list|(
name|String
name|toAppend
parameter_list|,
name|SplunkTableScan
name|splunkRel
parameter_list|,
name|LogicalProject
name|topProj
parameter_list|,
name|LogicalProject
name|bottomProj
parameter_list|,
name|RelDataType
name|topRow
parameter_list|,
name|RelDataType
name|bottomRow
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|splunkRel
operator|.
name|getCluster
argument_list|()
decl_stmt|;
name|StringBuilder
name|updateSearchStr
init|=
operator|new
name|StringBuilder
argument_list|(
name|splunkRel
operator|.
name|search
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|toAppend
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|updateSearchStr
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|toAppend
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|bottomFields
init|=
name|bottomRow
operator|==
literal|null
condition|?
literal|null
else|:
name|bottomRow
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|topFields
init|=
name|topRow
operator|==
literal|null
condition|?
literal|null
else|:
name|topRow
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
if|if
condition|(
name|bottomFields
operator|==
literal|null
condition|)
block|{
name|bottomFields
operator|=
name|splunkRel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
expr_stmt|;
block|}
comment|// handle bottom projection (ie choose a subset of the table fields)
if|if
condition|(
name|bottomProj
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|tmp
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|dRow
init|=
name|bottomProj
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|rn
range|:
name|bottomProj
operator|.
name|getProjects
argument_list|()
control|)
block|{
name|RelDataTypeField
name|rdtf
decl_stmt|;
if|if
condition|(
name|rn
operator|instanceof
name|RexSlot
condition|)
block|{
name|RexSlot
name|rs
init|=
operator|(
name|RexSlot
operator|)
name|rn
decl_stmt|;
name|rdtf
operator|=
name|bottomFields
operator|.
name|get
argument_list|(
name|rs
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rdtf
operator|=
name|dRow
operator|.
name|get
argument_list|(
name|tmp
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|tmp
operator|.
name|add
argument_list|(
name|rdtf
argument_list|)
expr_stmt|;
block|}
name|bottomFields
operator|=
name|tmp
expr_stmt|;
block|}
comment|// field renaming: to -> from
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|renames
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// handle top projection (ie reordering and renaming)
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|newFields
init|=
name|bottomFields
decl_stmt|;
if|if
condition|(
name|topProj
operator|!=
literal|null
condition|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"topProj: {}"
argument_list|,
name|topProj
operator|.
name|getPermutation
argument_list|()
argument_list|)
expr_stmt|;
name|newFields
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RexNode
name|rn
range|:
name|topProj
operator|.
name|getProjects
argument_list|()
control|)
block|{
name|RexInputRef
name|rif
init|=
operator|(
name|RexInputRef
operator|)
name|rn
decl_stmt|;
name|RelDataTypeField
name|field
init|=
name|bottomFields
operator|.
name|get
argument_list|(
name|rif
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|bottomFields
operator|.
name|get
argument_list|(
name|rif
operator|.
name|getIndex
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|topFields
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|renames
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|bottomFields
operator|.
name|get
argument_list|(
name|rif
operator|.
name|getIndex
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|,
name|topFields
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|field
operator|=
name|topFields
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|newFields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|renames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|updateSearchStr
operator|.
name|append
argument_list|(
literal|"| rename "
argument_list|)
expr_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|p
range|:
name|renames
control|)
block|{
name|updateSearchStr
operator|.
name|append
argument_list|(
name|p
operator|.
name|left
argument_list|)
operator|.
name|append
argument_list|(
literal|" AS "
argument_list|)
operator|.
name|append
argument_list|(
name|p
operator|.
name|right
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
name|RelDataType
name|resultType
init|=
name|cluster
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createStructType
argument_list|(
name|newFields
argument_list|)
decl_stmt|;
name|String
name|searchWithFilter
init|=
name|updateSearchStr
operator|.
name|toString
argument_list|()
decl_stmt|;
name|RelNode
name|rel
init|=
operator|new
name|SplunkTableScan
argument_list|(
name|cluster
argument_list|,
name|splunkRel
operator|.
name|getTable
argument_list|()
argument_list|,
name|splunkRel
operator|.
name|splunkTable
argument_list|,
name|searchWithFilter
argument_list|,
name|splunkRel
operator|.
name|earliest
argument_list|,
name|splunkRel
operator|.
name|latest
argument_list|,
name|resultType
operator|.
name|getFieldNames
argument_list|()
argument_list|)
decl_stmt|;
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"end of appendSearchString fieldNames: {}"
argument_list|,
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rel
return|;
block|}
comment|// ~ Private Methods ------------------------------------------------------
specifier|private
specifier|static
name|RelNode
name|addProjectionRule
parameter_list|(
name|LogicalProject
name|proj
parameter_list|,
name|RelNode
name|rel
parameter_list|)
block|{
if|if
condition|(
name|proj
operator|==
literal|null
condition|)
block|{
return|return
name|rel
return|;
block|}
return|return
name|LogicalProject
operator|.
name|create
argument_list|(
name|rel
argument_list|,
name|proj
operator|.
name|getHints
argument_list|()
argument_list|,
name|proj
operator|.
name|getProjects
argument_list|()
argument_list|,
name|proj
operator|.
name|getRowType
argument_list|()
argument_list|)
return|;
block|}
comment|// TODO: use StringBuilder instead of String
comment|// TODO: refactor this to use more tree like parsing, need to also
comment|//      make sure we use parens properly - currently precedence
comment|//      rules are simply left to right
specifier|private
name|boolean
name|getFilter
parameter_list|(
name|SqlOperator
name|op
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
parameter_list|,
name|StringBuilder
name|s
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|)
block|{
if|if
condition|(
operator|!
name|valid
argument_list|(
name|op
operator|.
name|getKind
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|like
init|=
literal|false
decl_stmt|;
switch|switch
condition|(
name|op
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|NOT
case|:
comment|// NOT op pre-pended
name|s
operator|=
name|s
operator|.
name|append
argument_list|(
literal|" NOT "
argument_list|)
expr_stmt|;
break|break;
case|case
name|CAST
case|:
return|return
name|asd
argument_list|(
literal|false
argument_list|,
name|operands
argument_list|,
name|s
argument_list|,
name|fieldNames
argument_list|,
literal|0
argument_list|)
return|;
case|case
name|LIKE
case|:
name|like
operator|=
literal|true
expr_stmt|;
break|break;
block|}
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
if|if
condition|(
operator|!
name|asd
argument_list|(
name|like
argument_list|,
name|operands
argument_list|,
name|s
argument_list|,
name|fieldNames
argument_list|,
name|i
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|op
operator|instanceof
name|SqlBinaryOperator
operator|&&
name|i
operator|==
literal|0
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|op
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|boolean
name|asd
parameter_list|(
name|boolean
name|like
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
parameter_list|,
name|StringBuilder
name|s
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|,
name|int
name|i
parameter_list|)
block|{
name|RexNode
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
operator|instanceof
name|RexCall
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
specifier|final
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|operand
decl_stmt|;
name|boolean
name|b
init|=
name|getFilter
argument_list|(
name|call
operator|.
name|getOperator
argument_list|()
argument_list|,
name|call
operator|.
name|getOperands
argument_list|()
argument_list|,
name|s
argument_list|,
name|fieldNames
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|b
condition|)
block|{
return|return
literal|false
return|;
block|}
name|s
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|operand
operator|instanceof
name|RexInputRef
condition|)
block|{
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|fieldIndex
init|=
operator|(
operator|(
name|RexInputRef
operator|)
name|operand
operator|)
operator|.
name|getIndex
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|fieldNames
operator|.
name|get
argument_list|(
name|fieldIndex
argument_list|)
decl_stmt|;
name|s
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// RexLiteral
name|String
name|tmp
init|=
name|toString
argument_list|(
name|like
argument_list|,
operator|(
name|RexLiteral
operator|)
name|operand
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmp
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|s
operator|.
name|append
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|boolean
name|valid
parameter_list|(
name|SqlKind
name|kind
parameter_list|)
block|{
return|return
name|SUPPORTED_OPS
operator|.
name|contains
argument_list|(
name|kind
argument_list|)
return|;
block|}
specifier|private
name|String
name|toString
parameter_list|(
name|SqlOperator
name|op
parameter_list|)
block|{
if|if
condition|(
name|op
operator|.
name|equals
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LIKE
argument_list|)
condition|)
block|{
return|return
name|SqlStdOperatorTable
operator|.
name|EQUALS
operator|.
name|toString
argument_list|()
return|;
block|}
if|else if
condition|(
name|op
operator|.
name|equals
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_EQUALS
argument_list|)
condition|)
block|{
return|return
literal|"!="
return|;
block|}
return|return
name|op
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|String
name|searchEscape
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|str
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|"\"\""
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|str
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|quote
init|=
literal|false
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
name|str
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'"'
operator|||
name|c
operator|==
literal|'\\'
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|quote
operator||=
operator|!
operator|(
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|c
argument_list|)
operator|||
name|c
operator|==
literal|'_'
operator|)
expr_stmt|;
block|}
if|if
condition|(
name|quote
operator|||
name|sb
operator|.
name|length
argument_list|()
operator|!=
name|str
operator|.
name|length
argument_list|()
condition|)
block|{
name|sb
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
literal|'"'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
name|str
return|;
block|}
specifier|private
name|String
name|toString
parameter_list|(
name|boolean
name|like
parameter_list|,
name|RexLiteral
name|literal
parameter_list|)
block|{
name|String
name|value
init|=
literal|null
decl_stmt|;
name|SqlTypeName
name|litSqlType
init|=
name|literal
operator|.
name|getTypeName
argument_list|()
decl_stmt|;
if|if
condition|(
name|SqlTypeName
operator|.
name|NUMERIC_TYPES
operator|.
name|contains
argument_list|(
name|litSqlType
argument_list|)
condition|)
block|{
name|value
operator|=
name|literal
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|litSqlType
operator|==
name|SqlTypeName
operator|.
name|CHAR
condition|)
block|{
name|value
operator|=
operator|(
operator|(
name|NlsString
operator|)
name|literal
operator|.
name|getValue
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|like
condition|)
block|{
name|value
operator|=
name|value
operator|.
name|replace
argument_list|(
literal|"%"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
block|}
name|value
operator|=
name|searchEscape
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
comment|// transform the call from SplunkUdxRel to FarragoJavaUdxRel
comment|// usually used to stop the optimizer from calling us
specifier|protected
name|void
name|transformToFarragoUdxRel
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|,
name|SplunkTableScan
name|splunkRel
parameter_list|,
name|LogicalFilter
name|filter
parameter_list|,
name|LogicalProject
name|topProj
parameter_list|,
name|LogicalProject
name|bottomProj
parameter_list|)
block|{
assert|assert
literal|false
assert|;
comment|/*     RelNode rel =         new EnumerableTableScan(             udxRel.getCluster(),             udxRel.getTable(),             udxRel.getRowType(),             udxRel.getServerMofId());      rel = RelOptUtil.createCastRel(rel, udxRel.getRowType(), true);      rel = addProjectionRule(bottomProj, rel);      if (filter != null) {       rel =           new LogicalFilter(filter.getCluster(), rel, filter.getCondition());     }      rel = addProjectionRule(topProj, rel);      call.transformTo(rel); */
block|}
specifier|public
specifier|static
name|String
name|getFieldsString
parameter_list|(
name|RelDataType
name|row
parameter_list|)
block|{
return|return
name|row
operator|.
name|getFieldNames
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

