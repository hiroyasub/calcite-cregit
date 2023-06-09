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
name|Filter
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
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonGenerator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UncheckedIOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
comment|/**  * Implementation of a {@link org.apache.calcite.rel.core.Filter}  * relational expression in Elasticsearch.  */
end_comment

begin_class
specifier|public
class|class
name|ElasticsearchFilter
extends|extends
name|Filter
implements|implements
name|ElasticsearchRel
block|{
name|ElasticsearchFilter
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RexNode
name|condition
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|child
argument_list|,
name|condition
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
name|child
operator|.
name|getConvention
argument_list|()
assert|;
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
name|Filter
name|copy
parameter_list|(
name|RelTraitSet
name|relTraitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|RexNode
name|condition
parameter_list|)
block|{
return|return
operator|new
name|ElasticsearchFilter
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|relTraitSet
argument_list|,
name|input
argument_list|,
name|condition
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
name|ObjectMapper
name|mapper
init|=
name|implementor
operator|.
name|elasticsearchTable
operator|.
name|mapper
decl_stmt|;
name|PredicateAnalyzerTranslator
name|translator
init|=
operator|new
name|PredicateAnalyzerTranslator
argument_list|(
name|mapper
argument_list|)
decl_stmt|;
try|try
block|{
name|implementor
operator|.
name|add
argument_list|(
name|translator
operator|.
name|translateMatch
argument_list|(
name|condition
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UncheckedIOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PredicateAnalyzer
operator|.
name|ExpressionNotAnalyzableException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * New version of translator which uses visitor pattern    * and allow to process more complex (boolean) predicates.    */
specifier|static
class|class
name|PredicateAnalyzerTranslator
block|{
specifier|private
specifier|final
name|ObjectMapper
name|mapper
decl_stmt|;
name|PredicateAnalyzerTranslator
parameter_list|(
specifier|final
name|ObjectMapper
name|mapper
parameter_list|)
block|{
name|this
operator|.
name|mapper
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|mapper
argument_list|,
literal|"mapper"
argument_list|)
expr_stmt|;
block|}
name|String
name|translateMatch
parameter_list|(
name|RexNode
name|condition
parameter_list|)
throws|throws
name|IOException
throws|,
name|PredicateAnalyzer
operator|.
name|ExpressionNotAnalyzableException
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|JsonGenerator
name|generator
init|=
name|mapper
operator|.
name|getFactory
argument_list|()
operator|.
name|createGenerator
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|boolean
name|disMax
init|=
name|condition
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|OR
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|RexNode
argument_list|>
name|operands
init|=
operator|(
operator|(
name|RexCall
operator|)
name|condition
operator|)
operator|.
name|getOperands
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|operands
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|disMax
condition|)
block|{
if|if
condition|(
name|operands
operator|.
name|next
argument_list|()
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|OR
argument_list|)
condition|)
block|{
name|disMax
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|disMax
condition|)
block|{
name|QueryBuilders
operator|.
name|disMaxQueryBuilder
argument_list|(
name|PredicateAnalyzer
operator|.
name|analyze
argument_list|(
name|condition
argument_list|)
argument_list|)
operator|.
name|writeJson
argument_list|(
name|generator
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|QueryBuilders
operator|.
name|constantScoreQuery
argument_list|(
name|PredicateAnalyzer
operator|.
name|analyze
argument_list|(
name|condition
argument_list|)
argument_list|)
operator|.
name|writeJson
argument_list|(
name|generator
argument_list|)
expr_stmt|;
block|}
name|generator
operator|.
name|flush
argument_list|()
expr_stmt|;
name|generator
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
literal|"{\"query\" : "
operator|+
name|writer
operator|.
name|toString
argument_list|()
operator|+
literal|"}"
return|;
block|}
block|}
block|}
end_class

end_unit

