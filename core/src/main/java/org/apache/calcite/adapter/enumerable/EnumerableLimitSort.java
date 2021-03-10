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
name|RelCollation
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
import|import static
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
name|EnumerableLimit
operator|.
name|getExpression
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link org.apache.calcite.rel.core.Sort} in  * {@link org.apache.calcite.adapter.enumerable.EnumerableConvention enumerable calling convention}.  * It optimizes sorts that have a limit and an optional offset.  */
end_comment

begin_class
specifier|public
class|class
name|EnumerableLimitSort
extends|extends
name|Sort
implements|implements
name|EnumerableRel
block|{
comment|/**    * Creates an EnumerableLimitSort.    *    *<p>Use {@link #create} unless you know what you're doing.    */
specifier|public
name|EnumerableLimitSort
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
name|RelCollation
name|collation
parameter_list|,
annotation|@
name|Nullable
name|RexNode
name|offset
parameter_list|,
annotation|@
name|Nullable
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
argument_list|,
name|collation
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
expr_stmt|;
assert|assert
name|this
operator|.
name|getConvention
argument_list|()
operator|instanceof
name|EnumerableConvention
assert|;
assert|assert
name|this
operator|.
name|getConvention
argument_list|()
operator|==
name|input
operator|.
name|getConvention
argument_list|()
assert|;
block|}
comment|/** Creates an EnumerableLimitSort. */
specifier|public
specifier|static
name|EnumerableLimitSort
name|create
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|RelCollation
name|collation
parameter_list|,
annotation|@
name|Nullable
name|RexNode
name|offset
parameter_list|,
annotation|@
name|Nullable
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
name|replace
argument_list|(
name|collation
argument_list|)
decl_stmt|;
return|return
operator|new
name|EnumerableLimitSort
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|collation
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
name|EnumerableLimitSort
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|newInput
parameter_list|,
name|RelCollation
name|newCollation
parameter_list|,
annotation|@
name|Nullable
name|RexNode
name|offset
parameter_list|,
annotation|@
name|Nullable
name|RexNode
name|fetch
parameter_list|)
block|{
return|return
operator|new
name|EnumerableLimitSort
argument_list|(
name|this
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|newInput
argument_list|,
name|newCollation
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
name|this
operator|.
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
name|this
operator|.
name|getRowType
argument_list|()
argument_list|,
name|result
operator|.
name|format
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|childExp
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
specifier|final
name|PhysType
name|inputPhysType
init|=
name|result
operator|.
name|physType
decl_stmt|;
specifier|final
name|Pair
argument_list|<
name|Expression
argument_list|,
name|Expression
argument_list|>
name|pair
init|=
name|inputPhysType
operator|.
name|generateCollationKey
argument_list|(
name|this
operator|.
name|collation
operator|.
name|getFieldCollations
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|fetchVal
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|fetch
operator|==
literal|null
condition|)
block|{
name|fetchVal
operator|=
name|Expressions
operator|.
name|constant
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fetchVal
operator|=
name|getExpression
argument_list|(
name|this
operator|.
name|fetch
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Expression
name|offsetVal
init|=
name|this
operator|.
name|offset
operator|==
literal|null
condition|?
name|Expressions
operator|.
name|constant
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
argument_list|)
else|:
name|getExpression
argument_list|(
name|this
operator|.
name|offset
argument_list|)
decl_stmt|;
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
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|ORDER_BY_WITH_FETCH_AND_OFFSET
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|list
argument_list|(
name|childExp
argument_list|,
name|builder
operator|.
name|append
argument_list|(
literal|"keySelector"
argument_list|,
name|pair
operator|.
name|left
argument_list|)
argument_list|)
operator|.
name|appendIfNotNull
argument_list|(
name|builder
operator|.
name|appendIfNotNull
argument_list|(
literal|"comparator"
argument_list|,
name|pair
operator|.
name|right
argument_list|)
argument_list|)
operator|.
name|appendIfNotNull
argument_list|(
name|builder
operator|.
name|appendIfNotNull
argument_list|(
literal|"offset"
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|offsetVal
argument_list|)
argument_list|)
argument_list|)
operator|.
name|appendIfNotNull
argument_list|(
name|builder
operator|.
name|appendIfNotNull
argument_list|(
literal|"fetch"
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|fetchVal
argument_list|)
argument_list|)
argument_list|)
argument_list|)
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
block|}
end_class

end_unit

