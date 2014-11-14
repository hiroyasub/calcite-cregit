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
name|Values
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeField
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
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
comment|/** Implementation of {@link org.apache.calcite.rel.core.Values} in  * {@link org.apache.calcite.adapter.enumerable.EnumerableConvention enumerable calling convention}. */
end_comment

begin_class
specifier|public
class|class
name|EnumerableValues
extends|extends
name|Values
implements|implements
name|EnumerableRel
block|{
name|EnumerableValues
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|List
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|tuples
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|rowType
argument_list|,
name|tuples
argument_list|,
name|traitSet
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
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
parameter_list|)
block|{
assert|assert
name|inputs
operator|.
name|isEmpty
argument_list|()
assert|;
return|return
operator|new
name|EnumerableValues
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|rowType
argument_list|,
name|tuples
argument_list|,
name|traitSet
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
comment|/*           return Linq4j.asEnumerable(               new Object[][] {                   new Object[] {1, 2},                   new Object[] {3, 4}               }); */
specifier|final
name|JavaTypeFactory
name|typeFactory
init|=
operator|(
name|JavaTypeFactory
operator|)
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|BlockBuilder
name|builder
init|=
operator|new
name|BlockBuilder
argument_list|()
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
name|pref
operator|.
name|preferCustom
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Type
name|rowClass
init|=
name|physType
operator|.
name|getJavaRowType
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|rowType
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|tuple
range|:
name|tuples
control|)
block|{
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|literals
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|RelDataTypeField
argument_list|,
name|RexLiteral
argument_list|>
name|pair
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|fields
argument_list|,
name|tuple
argument_list|)
control|)
block|{
name|literals
operator|.
name|add
argument_list|(
name|RexToLixTranslator
operator|.
name|translateLiteral
argument_list|(
name|pair
operator|.
name|right
argument_list|,
name|pair
operator|.
name|left
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
name|NULL
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|expressions
operator|.
name|add
argument_list|(
name|physType
operator|.
name|record
argument_list|(
name|literals
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
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|AS_ENUMERABLE
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|newArrayInit
argument_list|(
name|Primitive
operator|.
name|box
argument_list|(
name|rowClass
argument_list|)
argument_list|,
name|expressions
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

begin_comment
comment|// End EnumerableValues.java
end_comment

end_unit

