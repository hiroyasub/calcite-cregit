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
name|linq4j
operator|.
name|tree
operator|.
name|Primitive
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
name|Correlate
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
name|CorrelationId
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
name|JoinRelType
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
name|ImmutableBitSet
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_comment
comment|/** Implementation of {@link org.apache.calcite.rel.core.Correlate} in  * {@link org.apache.calcite.adapter.enumerable.EnumerableConvention enumerable calling convention}. */
end_comment

begin_class
specifier|public
class|class
name|EnumerableCorrelate
extends|extends
name|Correlate
implements|implements
name|EnumerableRel
block|{
specifier|public
name|EnumerableCorrelate
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|CorrelationId
name|correlationId
parameter_list|,
name|ImmutableBitSet
name|requiredColumns
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|correlationId
argument_list|,
name|requiredColumns
argument_list|,
name|joinType
argument_list|)
expr_stmt|;
block|}
comment|/** Creates an EnumerableCorrelate. */
specifier|public
specifier|static
name|EnumerableCorrelate
name|create
parameter_list|(
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|CorrelationId
name|correlationId
parameter_list|,
name|ImmutableBitSet
name|requiredColumns
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|left
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
name|enumerableCorrelate
argument_list|(
name|mq
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|joinType
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|EnumerableCorrelate
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|correlationId
argument_list|,
name|requiredColumns
argument_list|,
name|joinType
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|EnumerableCorrelate
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|CorrelationId
name|correlationId
parameter_list|,
name|ImmutableBitSet
name|requiredColumns
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|)
block|{
return|return
operator|new
name|EnumerableCorrelate
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|correlationId
argument_list|,
name|requiredColumns
argument_list|,
name|joinType
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
name|Result
name|leftResult
init|=
name|implementor
operator|.
name|visitChild
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
operator|(
name|EnumerableRel
operator|)
name|left
argument_list|,
name|pref
argument_list|)
decl_stmt|;
name|Expression
name|leftExpression
init|=
name|builder
operator|.
name|append
argument_list|(
literal|"left"
argument_list|,
name|leftResult
operator|.
name|block
argument_list|)
decl_stmt|;
specifier|final
name|BlockBuilder
name|corrBlock
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
name|Type
name|corrVarType
init|=
name|leftResult
operator|.
name|physType
operator|.
name|getJavaRowType
argument_list|()
decl_stmt|;
name|ParameterExpression
name|corrRef
decl_stmt|;
comment|// correlate to be used in inner loop
name|ParameterExpression
name|corrArg
decl_stmt|;
comment|// argument to correlate lambda (must be boxed)
if|if
condition|(
operator|!
name|Primitive
operator|.
name|is
argument_list|(
name|corrVarType
argument_list|)
condition|)
block|{
name|corrArg
operator|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Modifier
operator|.
name|FINAL
argument_list|,
name|corrVarType
argument_list|,
name|getCorrelVariable
argument_list|()
argument_list|)
expr_stmt|;
name|corrRef
operator|=
name|corrArg
expr_stmt|;
block|}
else|else
block|{
name|corrArg
operator|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Modifier
operator|.
name|FINAL
argument_list|,
name|Primitive
operator|.
name|box
argument_list|(
name|corrVarType
argument_list|)
argument_list|,
literal|"$box"
operator|+
name|getCorrelVariable
argument_list|()
argument_list|)
expr_stmt|;
name|corrRef
operator|=
operator|(
name|ParameterExpression
operator|)
name|corrBlock
operator|.
name|append
argument_list|(
name|getCorrelVariable
argument_list|()
argument_list|,
name|Expressions
operator|.
name|unbox
argument_list|(
name|corrArg
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|implementor
operator|.
name|registerCorrelVariable
argument_list|(
name|getCorrelVariable
argument_list|()
argument_list|,
name|corrRef
argument_list|,
name|corrBlock
argument_list|,
name|leftResult
operator|.
name|physType
argument_list|)
expr_stmt|;
specifier|final
name|Result
name|rightResult
init|=
name|implementor
operator|.
name|visitChild
argument_list|(
name|this
argument_list|,
literal|1
argument_list|,
operator|(
name|EnumerableRel
operator|)
name|right
argument_list|,
name|pref
argument_list|)
decl_stmt|;
name|implementor
operator|.
name|clearCorrelVariable
argument_list|(
name|getCorrelVariable
argument_list|()
argument_list|)
expr_stmt|;
name|corrBlock
operator|.
name|add
argument_list|(
name|rightResult
operator|.
name|block
argument_list|)
expr_stmt|;
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
name|Expression
name|selector
init|=
name|EnumUtils
operator|.
name|joinSelector
argument_list|(
name|joinType
argument_list|,
name|physType
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|leftResult
operator|.
name|physType
argument_list|,
name|rightResult
operator|.
name|physType
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|leftExpression
argument_list|,
name|BuiltInMethod
operator|.
name|CORRELATE_JOIN
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|EnumUtils
operator|.
name|toLinq4jJoinType
argument_list|(
name|joinType
argument_list|)
argument_list|)
argument_list|,
name|Expressions
operator|.
name|lambda
argument_list|(
name|corrBlock
operator|.
name|toBlock
argument_list|()
argument_list|,
name|corrArg
argument_list|)
argument_list|,
name|selector
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

begin_comment
comment|// End EnumerableCorrelate.java
end_comment

end_unit

