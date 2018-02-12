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
name|Union
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
comment|/** Implementation of {@link org.apache.calcite.rel.core.Union} in  * {@link org.apache.calcite.adapter.enumerable.EnumerableConvention enumerable calling convention}. */
end_comment

begin_class
specifier|public
class|class
name|EnumerableUnion
extends|extends
name|Union
implements|implements
name|EnumerableRel
block|{
specifier|public
name|EnumerableUnion
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
block|}
specifier|public
name|EnumerableUnion
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
name|EnumerableUnion
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
name|Expression
name|unionExp
init|=
literal|null
decl_stmt|;
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
if|if
condition|(
name|unionExp
operator|==
literal|null
condition|)
block|{
name|unionExp
operator|=
name|childExp
expr_stmt|;
block|}
else|else
block|{
name|unionExp
operator|=
name|all
condition|?
name|Expressions
operator|.
name|call
argument_list|(
name|unionExp
argument_list|,
name|BuiltInMethod
operator|.
name|CONCAT
operator|.
name|method
argument_list|,
name|childExp
argument_list|)
else|:
name|Expressions
operator|.
name|call
argument_list|(
name|unionExp
argument_list|,
name|BuiltInMethod
operator|.
name|UNION
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|list
argument_list|(
name|childExp
argument_list|)
operator|.
name|appendIfNotNull
argument_list|(
name|result
operator|.
name|physType
operator|.
name|comparer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|add
argument_list|(
name|unionExp
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
comment|// End EnumerableUnion.java
end_comment

end_unit

