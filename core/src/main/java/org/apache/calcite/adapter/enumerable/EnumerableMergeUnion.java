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
name|Ord
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
name|linq4j
operator|.
name|tree
operator|.
name|ParameterExpression
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
comment|/** Implementation of {@link org.apache.calcite.rel.core.Union} in  * {@link org.apache.calcite.adapter.enumerable.EnumerableConvention enumerable calling convention}.  *  *<p>Performs a union (or union all) of all its inputs (which must be already  * sorted), respecting the order. */
end_comment

begin_class
specifier|public
class|class
name|EnumerableMergeUnion
extends|extends
name|EnumerableUnion
block|{
specifier|protected
name|EnumerableMergeUnion
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|inputs
argument_list|,
name|all
argument_list|)
expr_stmt|;
specifier|final
name|RelCollation
name|collation
init|=
name|traitSet
operator|.
name|getCollation
argument_list|()
decl_stmt|;
if|if
condition|(
name|collation
operator|==
literal|null
operator|||
name|collation
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"EnumerableMergeUnion with no collation"
argument_list|)
throw|;
block|}
for|for
control|(
name|RelNode
name|input
range|:
name|inputs
control|)
block|{
specifier|final
name|RelCollation
name|inputCollation
init|=
name|input
operator|.
name|getTraitSet
argument_list|()
operator|.
name|getCollation
argument_list|()
decl_stmt|;
if|if
condition|(
name|inputCollation
operator|==
literal|null
operator|||
operator|!
name|inputCollation
operator|.
name|satisfies
argument_list|(
name|collation
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"EnumerableMergeUnion input does "
operator|+
literal|"not satisfy collation. EnumerableMergeUnion collation: "
operator|+
name|collation
operator|+
literal|". Input collation: "
operator|+
name|inputCollation
operator|+
literal|". Input: "
operator|+
name|input
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
specifier|static
name|EnumerableMergeUnion
name|create
parameter_list|(
name|RelCollation
name|collation
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|inputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
name|EnumerableMergeUnion
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|inputs
argument_list|,
name|all
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|EnumerableMergeUnion
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
return|return
operator|new
name|EnumerableMergeUnion
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|inputs
argument_list|,
name|all
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
name|ParameterExpression
name|inputListExp
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|List
operator|.
name|class
argument_list|,
name|builder
operator|.
name|newName
argument_list|(
literal|"mergeUnionInputs"
operator|+
name|Integer
operator|.
name|toUnsignedString
argument_list|(
name|this
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|declare
argument_list|(
literal|0
argument_list|,
name|inputListExp
argument_list|,
name|Expressions
operator|.
name|new_
argument_list|(
name|ArrayList
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|RelNode
argument_list|>
name|ord
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|inputs
argument_list|)
control|)
block|{
specifier|final
name|EnumerableRel
name|input
init|=
operator|(
name|EnumerableRel
operator|)
name|ord
operator|.
name|e
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
name|ord
operator|.
name|i
argument_list|,
name|input
argument_list|,
name|pref
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
operator|+
name|ord
operator|.
name|i
argument_list|,
name|result
operator|.
name|block
argument_list|)
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|statement
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|inputListExp
argument_list|,
name|BuiltInMethod
operator|.
name|COLLECTION_ADD
operator|.
name|method
argument_list|,
name|childExp
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|pref
operator|.
name|prefer
argument_list|(
name|JavaRowFormat
operator|.
name|CUSTOM
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RelCollation
name|collation
init|=
name|getTraitSet
argument_list|()
operator|.
name|getCollation
argument_list|()
decl_stmt|;
if|if
condition|(
name|collation
operator|==
literal|null
operator|||
name|collation
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// should not happen
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"EnumerableMergeUnion with no collation"
argument_list|)
throw|;
block|}
specifier|final
name|Pair
argument_list|<
name|Expression
argument_list|,
name|Expression
argument_list|>
name|pair
init|=
name|physType
operator|.
name|generateCollationKey
argument_list|(
name|collation
operator|.
name|getFieldCollations
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|sortKeySelector
init|=
name|pair
operator|.
name|left
decl_stmt|;
specifier|final
name|Expression
name|sortComparator
init|=
name|pair
operator|.
name|right
decl_stmt|;
specifier|final
name|Expression
name|equalityComparator
init|=
name|Util
operator|.
name|first
argument_list|(
name|physType
operator|.
name|comparer
argument_list|()
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|IDENTITY_COMPARER
operator|.
name|method
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|unionExp
init|=
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|MERGE_UNION
operator|.
name|method
argument_list|,
name|inputListExp
argument_list|,
name|sortKeySelector
argument_list|,
name|sortComparator
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|all
argument_list|,
name|boolean
operator|.
name|class
argument_list|)
argument_list|,
name|equalityComparator
argument_list|)
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|unionExp
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

