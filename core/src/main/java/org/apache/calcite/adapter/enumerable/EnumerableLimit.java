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
name|enumerable
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
name|DataContext
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
name|linq4j
operator|.
name|tree
operator|.
name|BlockBuilder
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
name|linq4j
operator|.
name|tree
operator|.
name|Expression
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
name|linq4j
operator|.
name|tree
operator|.
name|Expressions
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
name|RelCollationTraitDef
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
name|RelDistributionTraitDef
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
name|RelWriter
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
name|SingleRel
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
name|RelMdCollation
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
name|RelMdDistribution
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
name|RexDynamicParam
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
name|util
operator|.
name|BuiltInMethod
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
comment|/** Relational expression that applies a limit and/or offset to its input. */
end_comment

begin_class
specifier|public
class|class
name|EnumerableLimit
extends|extends
name|SingleRel
implements|implements
name|EnumerableRel
block|{
specifier|public
specifier|final
name|RexNode
name|offset
decl_stmt|;
specifier|public
specifier|final
name|RexNode
name|fetch
decl_stmt|;
comment|/** Creates an EnumerableLimit.    *    *<p>Use {@link #create} unless you know what you're doing. */
specifier|public
name|EnumerableLimit
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
name|RexNode
name|offset
parameter_list|,
name|RexNode
name|fetch
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|fetch
operator|=
name|fetch
expr_stmt|;
assert|assert
name|getConvention
argument_list|()
operator|instanceof
name|EnumerableConvention
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
comment|/** Creates an EnumerableLimit. */
specifier|public
specifier|static
name|EnumerableLimit
name|create
parameter_list|(
specifier|final
name|RelNode
name|input
parameter_list|,
name|RexNode
name|offset
parameter_list|,
name|RexNode
name|fetch
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|input
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|cluster
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
operator|.
name|replaceIfs
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|,
parameter_list|()
lambda|->
name|RelMdCollation
operator|.
name|limit
argument_list|(
name|mq
argument_list|,
name|input
argument_list|)
argument_list|)
operator|.
name|replaceIf
argument_list|(
name|RelDistributionTraitDef
operator|.
name|INSTANCE
argument_list|,
parameter_list|()
lambda|->
name|RelMdDistribution
operator|.
name|limit
argument_list|(
name|mq
argument_list|,
name|input
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|EnumerableLimit
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|EnumerableLimit
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|newInputs
parameter_list|)
block|{
return|return
operator|new
name|EnumerableLimit
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|sole
argument_list|(
name|newInputs
argument_list|)
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
return|return
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
operator|.
name|itemIf
argument_list|(
literal|"offset"
argument_list|,
name|offset
argument_list|,
name|offset
operator|!=
literal|null
argument_list|)
operator|.
name|itemIf
argument_list|(
literal|"fetch"
argument_list|,
name|fetch
argument_list|,
name|fetch
operator|!=
literal|null
argument_list|)
return|;
block|}
specifier|public
name|Result
name|implement
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|,
name|Prefer
name|pref
parameter_list|)
block|{
specifier|final
name|BlockBuilder
name|builder
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|EnumerableRel
name|child
init|=
operator|(
name|EnumerableRel
operator|)
name|getInput
argument_list|()
decl_stmt|;
specifier|final
name|Result
name|result
init|=
name|implementor
operator|.
name|visitChild
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
name|child
argument_list|,
name|pref
argument_list|)
decl_stmt|;
specifier|final
name|PhysType
name|physType
init|=
name|PhysTypeImpl
operator|.
name|of
argument_list|(
name|implementor
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|getRowType
argument_list|()
argument_list|,
name|result
operator|.
name|format
argument_list|)
decl_stmt|;
name|Expression
name|v
init|=
name|builder
operator|.
name|append
argument_list|(
literal|"child"
argument_list|,
name|result
operator|.
name|block
argument_list|)
decl_stmt|;
if|if
condition|(
name|offset
operator|!=
literal|null
condition|)
block|{
name|v
operator|=
name|builder
operator|.
name|append
argument_list|(
literal|"offset"
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|v
argument_list|,
name|BuiltInMethod
operator|.
name|SKIP
operator|.
name|method
argument_list|,
name|getExpression
argument_list|(
name|offset
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fetch
operator|!=
literal|null
condition|)
block|{
name|v
operator|=
name|builder
operator|.
name|append
argument_list|(
literal|"fetch"
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|v
argument_list|,
name|BuiltInMethod
operator|.
name|TAKE
operator|.
name|method
argument_list|,
name|getExpression
argument_list|(
name|fetch
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|implementor
operator|.
name|result
argument_list|(
name|physType
argument_list|,
name|builder
operator|.
name|toBlock
argument_list|()
argument_list|)
return|;
block|}
specifier|static
name|Expression
name|getExpression
parameter_list|(
name|RexNode
name|rexNode
parameter_list|)
block|{
if|if
condition|(
name|rexNode
operator|instanceof
name|RexDynamicParam
condition|)
block|{
specifier|final
name|RexDynamicParam
name|param
init|=
operator|(
name|RexDynamicParam
operator|)
name|rexNode
decl_stmt|;
return|return
name|Expressions
operator|.
name|convert_
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|DataContext
operator|.
name|ROOT
argument_list|,
name|BuiltInMethod
operator|.
name|DATA_CONTEXT_GET
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|"?"
operator|+
name|param
operator|.
name|getIndex
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Expressions
operator|.
name|constant
argument_list|(
name|RexLiteral
operator|.
name|intValue
argument_list|(
name|rexNode
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

