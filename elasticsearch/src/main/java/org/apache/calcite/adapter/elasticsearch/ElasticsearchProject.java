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
name|RelOptCost
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
name|RelOptPlanner
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
name|Project
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
name|metadata
operator|.
name|RelMetadataQuery
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
name|ImmutableList
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
comment|/**  * Implementation of {@link org.apache.calcite.rel.core.Project}  * relational expression in Elasticsearch.  */
end_comment

begin_class
specifier|public
class|class
name|ElasticsearchProject
extends|extends
name|Project
implements|implements
name|ElasticsearchRel
block|{
name|ElasticsearchProject
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|projects
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|input
argument_list|,
name|projects
argument_list|,
name|rowType
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|ElasticsearchRel
operator|.
name|CONVENTION
assert|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|input
operator|.
name|getConvention
argument_list|()
assert|;
block|}
annotation|@
name|Override
specifier|public
name|Project
name|copy
parameter_list|(
name|RelTraitSet
name|relTraitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|projects
parameter_list|,
name|RelDataType
name|relDataType
parameter_list|)
block|{
return|return
operator|new
name|ElasticsearchProject
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|projects
argument_list|,
name|relDataType
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|super
operator|.
name|computeSelfCost
argument_list|(
name|planner
argument_list|,
name|mq
argument_list|)
operator|.
name|multiplyBy
argument_list|(
literal|0.1
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|implement
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
block|{
name|implementor
operator|.
name|visitChild
argument_list|(
literal|0
argument_list|,
name|getInput
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|inFields
init|=
name|ElasticsearchRules
operator|.
name|elasticsearchFieldNames
argument_list|(
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ElasticsearchRules
operator|.
name|RexToElasticsearchTranslator
name|translator
init|=
operator|new
name|ElasticsearchRules
operator|.
name|RexToElasticsearchTranslator
argument_list|(
operator|(
name|JavaTypeFactory
operator|)
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|inFields
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|scriptFields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// registers wherever "select *" is present
name|boolean
name|hasSelectStar
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
name|pair
range|:
name|getNamedProjects
argument_list|()
control|)
block|{
specifier|final
name|String
name|name
init|=
name|pair
operator|.
name|right
decl_stmt|;
specifier|final
name|String
name|expr
init|=
name|pair
operator|.
name|left
operator|.
name|accept
argument_list|(
name|translator
argument_list|)
decl_stmt|;
comment|// "select *" present?
name|hasSelectStar
operator||=
name|ElasticsearchConstants
operator|.
name|isSelectAll
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|ElasticsearchRules
operator|.
name|isItem
argument_list|(
name|pair
operator|.
name|left
argument_list|)
condition|)
block|{
name|implementor
operator|.
name|addExpressionItemMapping
argument_list|(
name|name
argument_list|,
name|expr
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|expr
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|expr
operator|.
name|matches
argument_list|(
literal|"\"literal\":.+"
argument_list|)
condition|)
block|{
name|scriptFields
operator|.
name|add
argument_list|(
name|ElasticsearchRules
operator|.
name|quote
argument_list|(
name|name
argument_list|)
operator|+
literal|":{\"script\": "
operator|+
name|expr
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
index|[
literal|1
index|]
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|scriptFields
operator|.
name|add
argument_list|(
name|ElasticsearchRules
operator|.
name|quote
argument_list|(
name|name
argument_list|)
operator|+
literal|":{\"script\":"
comment|// _source (ES2) vs params._source (ES5)
operator|+
literal|"\""
operator|+
name|implementor
operator|.
name|elasticsearchTable
operator|.
name|scriptedFieldPrefix
argument_list|()
operator|+
literal|"."
operator|+
name|expr
operator|.
name|replace
argument_list|(
literal|"\""
argument_list|,
literal|""
argument_list|)
operator|+
literal|"\"}"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|hasSelectStar
condition|)
block|{
comment|// means select * from elastic
comment|// this does not yet cover select *, _MAP['foo'], _MAP['bar'][0] from elastic
return|return;
block|}
specifier|final
name|StringBuilder
name|query
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|scriptFields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|newList
init|=
name|fields
operator|.
name|stream
argument_list|()
comment|// _id field is available implicitly
operator|.
name|filter
argument_list|(
name|f
lambda|->
operator|!
name|ElasticsearchConstants
operator|.
name|ID
operator|.
name|equals
argument_list|(
name|f
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|ElasticsearchRules
operator|::
name|quote
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|findString
init|=
name|String
operator|.
name|join
argument_list|(
literal|", "
argument_list|,
name|newList
argument_list|)
decl_stmt|;
name|query
operator|.
name|append
argument_list|(
literal|"\"_source\" : ["
argument_list|)
operator|.
name|append
argument_list|(
name|findString
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// if scripted fields are present, ES ignores _source attribute
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|scriptFields
operator|.
name|add
argument_list|(
name|ElasticsearchRules
operator|.
name|quote
argument_list|(
name|field
argument_list|)
operator|+
literal|":{\"script\": "
comment|// _source (ES2) vs params._source (ES5)
operator|+
literal|"\""
operator|+
name|implementor
operator|.
name|elasticsearchTable
operator|.
name|scriptedFieldPrefix
argument_list|()
operator|+
literal|"."
operator|+
name|field
operator|+
literal|"\"}"
argument_list|)
expr_stmt|;
block|}
name|query
operator|.
name|append
argument_list|(
literal|"\"script_fields\": {"
operator|+
name|String
operator|.
name|join
argument_list|(
literal|", "
argument_list|,
name|scriptFields
argument_list|)
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
name|implementor
operator|.
name|list
operator|.
name|removeIf
argument_list|(
name|l
lambda|->
name|l
operator|.
name|startsWith
argument_list|(
literal|"\"_source\""
argument_list|)
argument_list|)
expr_stmt|;
name|implementor
operator|.
name|add
argument_list|(
literal|"{"
operator|+
name|query
operator|.
name|toString
argument_list|()
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

