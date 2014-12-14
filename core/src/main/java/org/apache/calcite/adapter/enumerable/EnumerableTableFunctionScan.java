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
name|TableFunctionScan
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
name|RelColumnMapping
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/** Implementation of {@link org.apache.calcite.rel.core.TableFunctionScan} in  * {@link org.apache.calcite.adapter.enumerable.EnumerableConvention enumerable calling convention}. */
end_comment

begin_class
specifier|public
class|class
name|EnumerableTableFunctionScan
extends|extends
name|TableFunctionScan
implements|implements
name|EnumerableRel
block|{
specifier|public
name|EnumerableTableFunctionScan
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|,
name|Type
name|elementType
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|RexNode
name|call
parameter_list|,
name|Set
argument_list|<
name|RelColumnMapping
argument_list|>
name|columnMappings
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|inputs
argument_list|,
name|call
argument_list|,
name|elementType
argument_list|,
name|rowType
argument_list|,
name|columnMappings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|EnumerableTableFunctionScan
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
name|RexNode
name|rexCall
parameter_list|,
name|Type
name|elementType
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|Set
argument_list|<
name|RelColumnMapping
argument_list|>
name|columnMappings
parameter_list|)
block|{
return|return
operator|new
name|EnumerableTableFunctionScan
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|inputs
argument_list|,
name|elementType
argument_list|,
name|rowType
argument_list|,
name|rexCall
argument_list|,
name|columnMappings
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
name|BlockBuilder
name|bb
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
comment|// Non-array user-specified types are not supported yet
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
name|getElementType
argument_list|()
operator|==
literal|null
comment|/* e.g. not known */
operator|||
operator|(
name|getElementType
argument_list|()
operator|instanceof
name|Class
operator|&&
name|Object
index|[]
operator|.
expr|class
operator|.
name|isAssignableFrom
argument_list|(
operator|(
name|Class
operator|)
name|getElementType
argument_list|()
argument_list|)
operator|)
condition|?
name|JavaRowFormat
operator|.
name|ARRAY
else|:
name|JavaRowFormat
operator|.
name|CUSTOM
argument_list|)
decl_stmt|;
name|RexToLixTranslator
name|t
init|=
name|RexToLixTranslator
operator|.
name|forAggregation
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
name|bb
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|t
operator|=
name|t
operator|.
name|setCorrelates
argument_list|(
name|implementor
operator|.
name|allCorrelateVariables
argument_list|)
expr_stmt|;
specifier|final
name|Expression
name|translated
init|=
name|t
operator|.
name|translate
argument_list|(
name|getCall
argument_list|()
argument_list|)
decl_stmt|;
name|bb
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|translated
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
name|bb
operator|.
name|toBlock
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End EnumerableTableFunctionScan.java
end_comment

end_unit

