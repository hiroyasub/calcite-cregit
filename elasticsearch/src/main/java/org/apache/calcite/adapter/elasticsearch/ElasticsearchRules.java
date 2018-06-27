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
name|RelTrait
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
name|ArrayList
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
block|}
decl_stmt|;
specifier|private
name|ElasticsearchRules
parameter_list|()
block|{
block|}
comment|/**    * Returns 'string' if it is a call to item['string'], null otherwise.    * @param call current relational expression    * @return literal value    */
specifier|static
name|String
name|isItem
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
literal|"\"literal\":\""
operator|+
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
operator|+
literal|"\""
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
name|isItem
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
literal|"\""
operator|+
name|name
operator|+
literal|"\""
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
name|strings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"$"
argument_list|)
condition|?
name|strings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
else|:
name|strings
operator|.
name|get
argument_list|(
literal|0
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
operator|.
name|toString
argument_list|()
operator|+
literal|"is not supported by ElasticsearchProject"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|visitList
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|list
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|node
range|:
name|list
control|)
block|{
name|strings
operator|.
name|add
argument_list|(
name|node
operator|.
name|accept
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|strings
return|;
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
specifier|final
name|Convention
name|out
decl_stmt|;
name|ElasticsearchConverterRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|clazz
parameter_list|,
name|RelTrait
name|in
parameter_list|,
name|Convention
name|out
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|clazz
argument_list|,
name|in
argument_list|,
name|out
argument_list|,
name|description
argument_list|)
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|out
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
operator|new
name|ElasticsearchSortRule
argument_list|()
decl_stmt|;
specifier|private
name|ElasticsearchSortRule
parameter_list|()
block|{
name|super
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
operator|new
name|ElasticsearchFilterRule
argument_list|()
decl_stmt|;
specifier|private
name|ElasticsearchFilterRule
parameter_list|()
block|{
name|super
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
operator|new
name|ElasticsearchProjectRule
argument_list|()
decl_stmt|;
specifier|private
name|ElasticsearchProjectRule
parameter_list|()
block|{
name|super
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

begin_comment
comment|// End ElasticsearchRules.java
end_comment

end_unit

