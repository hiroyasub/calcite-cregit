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
name|Collect
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

begin_comment
comment|/** Implementation of {@link org.apache.calcite.rel.core.Collect} in  * {@link org.apache.calcite.adapter.enumerable.EnumerableConvention enumerable calling convention}. */
end_comment

begin_class
specifier|public
class|class
name|EnumerableCollect
extends|extends
name|Collect
implements|implements
name|EnumerableRel
block|{
specifier|public
name|EnumerableCollect
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
name|String
name|fieldName
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
name|fieldName
argument_list|)
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
name|child
operator|.
name|getConvention
argument_list|()
assert|;
block|}
annotation|@
name|Override
specifier|public
name|EnumerableCollect
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|newInput
parameter_list|)
block|{
return|return
operator|new
name|EnumerableCollect
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|newInput
argument_list|,
name|fieldName
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
name|getInput
argument_list|()
decl_stmt|;
comment|// REVIEW zabetak January 7, 2019: Even if we ask the implementor to provide a result
comment|// where records are represented as arrays (Prefer.ARRAY) this may not be respected.
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
name|Prefer
operator|.
name|ARRAY
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
name|JavaRowFormat
operator|.
name|LIST
argument_list|)
decl_stmt|;
comment|// final Enumerable child =<<child adapter>>;
comment|// final Enumerable<Object[]> converted = child.select(<<conversion code>>);
comment|// final List<Object[]> list = converted.toList();
name|Expression
name|child_
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
comment|// In the internal representation of multisets , every element must be a record. In case the
comment|// result above is a scalar type we have to wrap it around a physical type capable of
comment|// representing records. For this reason the following conversion is necessary.
comment|// REVIEW zabetak January 7, 2019: If we can ensure that the input to this operator
comment|// has the correct physical type (e.g., respecting the Prefer.ARRAY above) then this conversion
comment|// can be removed.
name|Expression
name|conv_
init|=
name|builder
operator|.
name|append
argument_list|(
literal|"converted"
argument_list|,
name|result
operator|.
name|physType
operator|.
name|convertTo
argument_list|(
name|child_
argument_list|,
name|JavaRowFormat
operator|.
name|ARRAY
argument_list|)
argument_list|)
decl_stmt|;
name|Expression
name|list_
init|=
name|builder
operator|.
name|append
argument_list|(
literal|"list"
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|conv_
argument_list|,
name|BuiltInMethod
operator|.
name|ENUMERABLE_TO_LIST
operator|.
name|method
argument_list|)
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
name|SINGLETON_ENUMERABLE
operator|.
name|method
argument_list|,
name|list_
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

