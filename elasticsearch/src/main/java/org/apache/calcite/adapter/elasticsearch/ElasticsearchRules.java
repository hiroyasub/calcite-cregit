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
name|elasticsearch
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
name|enumerable
operator|.
name|RexImpTable
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
name|adapter
operator|.
name|enumerable
operator|.
name|RexToLixTranslator
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
name|adapter
operator|.
name|java
operator|.
name|JavaTypeFactory
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
name|Convention
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
name|RelTraitSet
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
name|InvalidRelException
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
name|RelCollations
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
name|convert
operator|.
name|ConverterRule
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
name|Sort
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
name|LogicalAggregate
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
name|RexVisitorImpl
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
name|sql
operator|.
name|validate
operator|.
name|SqlValidatorUtil
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
name|List
import|;
end_import

begin_comment
comment|/**  * Rules and relational operators for  * {@link ElasticsearchRel#CONVENTION ELASTICSEARCH}  * calling convention.  */
end_comment

begin_class
class|class
name|ElasticsearchRules
block|{
specifier|static
specifier|final
name|RelOptRule
index|[]
name|RULES
init|=
block|{
name|ElasticsearchSortRule
operator|.
name|INSTANCE
block|,
name|ElasticsearchFilterRule
operator|.
name|INSTANCE
block|,
name|ElasticsearchProjectRule
operator|.
name|INSTANCE
block|,
name|ElasticsearchAggregateRule
operator|.
name|INSTANCE
block|}
decl_stmt|;
specifier|private
name|ElasticsearchRules
parameter_list|()
block|{
block|}
comment|/**    * Returns 'string' if it is a call to item['string'], null otherwise.    * @param call current relational expression    * @return literal value    */
specifier|private
specifier|static
name|String
name|isItemCall
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
if|if
condition|(
name|call
operator|.
name|getOperator
argument_list|()
operator|!=
name|SqlStdOperatorTable
operator|.
name|ITEM
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|RexNode
name|op0
init|=
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|op1
init|=
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|op0
operator|instanceof
name|RexInputRef
operator|&&
operator|(
operator|(
name|RexInputRef
operator|)
name|op0
operator|)
operator|.
name|getIndex
argument_list|()
operator|==
literal|0
operator|&&
name|op1
operator|instanceof
name|RexLiteral
operator|&&
operator|(
operator|(
name|RexLiteral
operator|)
name|op1
operator|)
operator|.
name|getValue2
argument_list|()
operator|instanceof
name|String
condition|)
block|{
return|return
operator|(
name|String
operator|)
operator|(
operator|(
name|RexLiteral
operator|)
name|op1
operator|)
operator|.
name|getValue2
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Checks if current node represents item access as in {@code _MAP['foo']} or    * {@code cast(_MAP['foo'] as integer)}.    *    * @return whether expression is item    */
specifier|static
name|boolean
name|isItem
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
specifier|final
name|Boolean
name|result
init|=
name|node
operator|.
name|accept
argument_list|(
operator|new
name|RexVisitorImpl
argument_list|<
name|Boolean
argument_list|>
argument_list|(
literal|false
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|visitCall
parameter_list|(
specifier|final
name|RexCall
name|call
parameter_list|)
block|{
return|return
name|isItemCall
argument_list|(
name|uncast
argument_list|(
name|call
argument_list|)
argument_list|)
operator|!=
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|result
argument_list|)
return|;
block|}
comment|/**    * Unwraps cast expressions from current call. {@code cast(cast(expr))} becomes {@code expr}.    */
specifier|private
specifier|static
name|RexCall
name|uncast
parameter_list|(
name|RexCall
name|maybeCast
parameter_list|)
block|{
if|if
condition|(
name|maybeCast
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|CAST
operator|&&
name|maybeCast
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|RexCall
condition|)
block|{
return|return
name|uncast
argument_list|(
operator|(
name|RexCall
operator|)
name|maybeCast
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
comment|// not a cast
return|return
name|maybeCast
return|;
block|}
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|elasticsearchFieldNames
parameter_list|(
specifier|final
name|RelDataType
name|rowType
parameter_list|)
block|{
return|return
name|SqlValidatorUtil
operator|.
name|uniquify
argument_list|(
operator|new
name|AbstractList
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|String
name|name
init|=
name|rowType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|name
operator|.
name|startsWith
argument_list|(
literal|"$"
argument_list|)
condition|?
literal|"_"
operator|+
name|name
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
else|:
name|name
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
name|rowType
operator|.
name|getFieldCount
argument_list|()
return|;
block|}
block|}
argument_list|,
name|SqlValidatorUtil
operator|.
name|EXPR_SUGGESTER
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|static
name|String
name|quote
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
literal|"\""
operator|+
name|s
operator|+
literal|"\""
return|;
block|}
specifier|static
name|String
name|stripQuotes
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|length
argument_list|()
operator|>
literal|1
operator|&&
name|s
operator|.
name|startsWith
argument_list|(
literal|"\""
argument_list|)
operator|&&
name|s
operator|.
name|endsWith
argument_list|(
literal|"\""
argument_list|)
condition|?
name|s
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
else|:
name|s
return|;
block|}
specifier|private
specifier|static
name|String
name|escapeSpecialSymbols
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|replace
argument_list|(
literal|"\\"
argument_list|,
literal|"\\\\"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"\""
argument_list|,
literal|"\\\""
argument_list|)
return|;
block|}
comment|/**    * Translator from {@link RexNode} to strings in Elasticsearch's expression    * language.    */
specifier|static
class|class
name|RexToElasticsearchTranslator
extends|extends
name|RexVisitorImpl
argument_list|<
name|String
argument_list|>
block|{
specifier|private
specifier|final
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|inFields
decl_stmt|;
name|RexToElasticsearchTranslator
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|inFields
parameter_list|)
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
name|this
operator|.
name|inFields
operator|=
name|inFields
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|visitLiteral
parameter_list|(
name|RexLiteral
name|literal
parameter_list|)
block|{
if|if
condition|(
name|literal
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|"null"
return|;
block|}
return|return
literal|"\"literal\":"
operator|+
name|quote
argument_list|(
name|escapeSpecialSymbols
argument_list|(
name|RexToLixTranslator
operator|.
name|translateLiteral
argument_list|(
name|literal
argument_list|,
name|literal
operator|.
name|getType
argument_list|()
argument_list|,
name|typeFactory
argument_list|,
name|RexImpTable
operator|.
name|NullAs
operator|.
name|NOT_POSSIBLE
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|)
block|{
return|return
name|quote
argument_list|(
name|inFields
operator|.
name|get
argument_list|(
name|inputRef
operator|.
name|getIndex
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|visitCall
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
specifier|final
name|String
name|name
init|=
name|isItemCall
argument_list|(
name|call
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
return|return
name|name
return|;
block|}
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
name|visitList
argument_list|(
name|call
operator|.
name|operands
argument_list|)
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|CAST
condition|)
block|{
return|return
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
if|if
condition|(
name|call
operator|.
name|getOperator
argument_list|()
operator|==
name|SqlStdOperatorTable
operator|.
name|ITEM
condition|)
block|{
specifier|final
name|RexNode
name|op1
init|=
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|op1
operator|instanceof
name|RexLiteral
operator|&&
name|op1
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|INTEGER
condition|)
block|{
return|return
name|stripQuotes
argument_list|(
name|strings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|+
literal|"["
operator|+
operator|(
operator|(
name|RexLiteral
operator|)
name|op1
operator|)
operator|.
name|getValue2
argument_list|()
operator|+
literal|"]"
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Translation of "
operator|+
name|call
operator|+
literal|" is not supported by ElasticsearchProject"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Base class for planner rules that convert a relational expression to    * Elasticsearch calling convention.    */
specifier|abstract
specifier|static
class|class
name|ElasticsearchConverterRule
extends|extends
name|ConverterRule
block|{
specifier|protected
name|ElasticsearchConverterRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Rule to convert a {@link org.apache.calcite.rel.core.Sort} to an    * {@link ElasticsearchSort}.    */
specifier|private
specifier|static
class|class
name|ElasticsearchSortRule
extends|extends
name|ElasticsearchConverterRule
block|{
specifier|private
specifier|static
specifier|final
name|ElasticsearchSortRule
name|INSTANCE
init|=
name|Config
operator|.
name|INSTANCE
operator|.
name|withConversion
argument_list|(
name|Sort
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|ElasticsearchRel
operator|.
name|CONVENTION
argument_list|,
literal|"ElasticsearchSortRule"
argument_list|)
operator|.
name|withRuleFactory
argument_list|(
name|ElasticsearchSortRule
operator|::
operator|new
argument_list|)
operator|.
name|toRule
argument_list|(
name|ElasticsearchSortRule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|ElasticsearchSortRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|relNode
parameter_list|)
block|{
specifier|final
name|Sort
name|sort
init|=
operator|(
name|Sort
operator|)
name|relNode
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|sort
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
operator|.
name|replace
argument_list|(
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|ElasticsearchSort
argument_list|(
name|relNode
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|sort
operator|.
name|getInput
argument_list|()
argument_list|,
name|traitSet
operator|.
name|replace
argument_list|(
name|RelCollations
operator|.
name|EMPTY
argument_list|)
argument_list|)
argument_list|,
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|,
name|sort
operator|.
name|offset
argument_list|,
name|sort
operator|.
name|fetch
argument_list|)
return|;
block|}
block|}
comment|/**    * Rule to convert a {@link org.apache.calcite.rel.logical.LogicalFilter} to an    * {@link ElasticsearchFilter}.    */
specifier|private
specifier|static
class|class
name|ElasticsearchFilterRule
extends|extends
name|ElasticsearchConverterRule
block|{
specifier|private
specifier|static
specifier|final
name|ElasticsearchFilterRule
name|INSTANCE
init|=
name|Config
operator|.
name|INSTANCE
operator|.
name|withConversion
argument_list|(
name|LogicalFilter
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|ElasticsearchRel
operator|.
name|CONVENTION
argument_list|,
literal|"ElasticsearchFilterRule"
argument_list|)
operator|.
name|withRuleFactory
argument_list|(
name|ElasticsearchFilterRule
operator|::
operator|new
argument_list|)
operator|.
name|toRule
argument_list|(
name|ElasticsearchFilterRule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|ElasticsearchFilterRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|relNode
parameter_list|)
block|{
specifier|final
name|LogicalFilter
name|filter
init|=
operator|(
name|LogicalFilter
operator|)
name|relNode
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|filter
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
decl_stmt|;
return|return
operator|new
name|ElasticsearchFilter
argument_list|(
name|relNode
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|filter
operator|.
name|getInput
argument_list|()
argument_list|,
name|out
argument_list|)
argument_list|,
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Rule to convert an {@link org.apache.calcite.rel.logical.LogicalAggregate}    * to an {@link ElasticsearchAggregate}.    */
specifier|private
specifier|static
class|class
name|ElasticsearchAggregateRule
extends|extends
name|ElasticsearchConverterRule
block|{
specifier|private
specifier|static
specifier|final
name|RelOptRule
name|INSTANCE
init|=
name|Config
operator|.
name|INSTANCE
operator|.
name|withConversion
argument_list|(
name|LogicalAggregate
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|ElasticsearchRel
operator|.
name|CONVENTION
argument_list|,
literal|"ElasticsearchAggregateRule"
argument_list|)
operator|.
name|withRuleFactory
argument_list|(
name|ElasticsearchAggregateRule
operator|::
operator|new
argument_list|)
operator|.
name|toRule
argument_list|(
name|ElasticsearchAggregateRule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|ElasticsearchAggregateRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|LogicalAggregate
name|agg
init|=
operator|(
name|LogicalAggregate
operator|)
name|rel
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|agg
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|new
name|ElasticsearchAggregate
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|agg
operator|.
name|getInput
argument_list|()
argument_list|,
name|traitSet
operator|.
name|simplify
argument_list|()
argument_list|)
argument_list|,
name|agg
operator|.
name|getGroupSet
argument_list|()
argument_list|,
name|agg
operator|.
name|getGroupSets
argument_list|()
argument_list|,
name|agg
operator|.
name|getAggCallList
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidRelException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
comment|/**    * Rule to convert a {@link org.apache.calcite.rel.logical.LogicalProject}    * to an {@link ElasticsearchProject}.    */
specifier|private
specifier|static
class|class
name|ElasticsearchProjectRule
extends|extends
name|ElasticsearchConverterRule
block|{
specifier|private
specifier|static
specifier|final
name|ElasticsearchProjectRule
name|INSTANCE
init|=
name|Config
operator|.
name|INSTANCE
operator|.
name|withConversion
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|ElasticsearchRel
operator|.
name|CONVENTION
argument_list|,
literal|"ElasticsearchProjectRule"
argument_list|)
operator|.
name|withRuleFactory
argument_list|(
name|ElasticsearchProjectRule
operator|::
operator|new
argument_list|)
operator|.
name|toRule
argument_list|(
name|ElasticsearchProjectRule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|ElasticsearchProjectRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|LogicalProject
name|project
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|project
operator|.
name|getVariablesSet
argument_list|()
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|relNode
parameter_list|)
block|{
specifier|final
name|LogicalProject
name|project
init|=
operator|(
name|LogicalProject
operator|)
name|relNode
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|project
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
decl_stmt|;
return|return
operator|new
name|ElasticsearchProject
argument_list|(
name|project
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|project
operator|.
name|getInput
argument_list|()
argument_list|,
name|out
argument_list|)
argument_list|,
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

