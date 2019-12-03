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
name|plan
operator|.
name|Convention
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
name|convert
operator|.
name|ConverterRule
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
name|RelFactories
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
name|logical
operator|.
name|LogicalTableFunctionScan
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
name|tools
operator|.
name|RelBuilderFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
import|;
end_import

begin_comment
comment|/** Planner rule that converts a  * {@link org.apache.calcite.rel.logical.LogicalTableFunctionScan}  * relational expression  * {@link org.apache.calcite.adapter.enumerable.EnumerableConvention enumerable calling convention}. */
end_comment

begin_class
specifier|public
class|class
name|EnumerableTableFunctionScanRule
extends|extends
name|ConverterRule
block|{
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|EnumerableTableFunctionScanRule
parameter_list|()
block|{
name|this
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an EnumerableTableFunctionScanRule.    *    * @param relBuilderFactory Builder for relational expressions    */
specifier|public
name|EnumerableTableFunctionScanRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|LogicalTableFunctionScan
operator|.
name|class
argument_list|,
operator|(
name|Predicate
argument_list|<
name|RelNode
argument_list|>
operator|)
name|r
lambda|->
literal|true
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|,
name|relBuilderFactory
argument_list|,
literal|"EnumerableTableFunctionScanRule"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|LogicalTableFunctionScan
name|tbl
init|=
operator|(
name|LogicalTableFunctionScan
operator|)
name|rel
decl_stmt|;
return|return
operator|new
name|EnumerableTableFunctionScan
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|tbl
operator|.
name|getInputs
argument_list|()
argument_list|,
name|tbl
operator|.
name|getElementType
argument_list|()
argument_list|,
name|tbl
operator|.
name|getRowType
argument_list|()
argument_list|,
name|tbl
operator|.
name|getCall
argument_list|()
argument_list|,
name|tbl
operator|.
name|getColumnMappings
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

