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
name|file
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
name|enumerable
operator|.
name|EnumerableConvention
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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumerableRel
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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumerableRelImplementor
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
name|adapter
operator|.
name|enumerable
operator|.
name|PhysType
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
name|adapter
operator|.
name|enumerable
operator|.
name|PhysTypeImpl
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
name|Blocks
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
name|RelOptTable
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
name|core
operator|.
name|TableScan
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
name|RelDataTypeFactory
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
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Relational expression representing a scan of a CSV file.  *  *<p>Like any table scan, it serves as a leaf node of a query tree.  */
end_comment

begin_class
specifier|public
class|class
name|CsvTableScan
extends|extends
name|TableScan
implements|implements
name|EnumerableRel
block|{
specifier|final
name|CsvTranslatableTable
name|csvTable
decl_stmt|;
specifier|private
specifier|final
name|int
index|[]
name|fields
decl_stmt|;
specifier|protected
name|CsvTableScan
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptTable
name|table
parameter_list|,
name|CsvTranslatableTable
name|csvTable
parameter_list|,
name|int
index|[]
name|fields
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|table
argument_list|)
expr_stmt|;
name|this
operator|.
name|csvTable
operator|=
name|csvTable
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
assert|assert
name|csvTable
operator|!=
literal|null
assert|;
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
name|CsvTableScan
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|table
argument_list|,
name|csvTable
argument_list|,
name|fields
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
name|item
argument_list|(
literal|"fields"
argument_list|,
name|Primitive
operator|.
name|asList
argument_list|(
name|fields
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fieldList
init|=
name|table
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
specifier|final
name|RelDataTypeFactory
operator|.
name|Builder
name|builder
init|=
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|field
range|:
name|fields
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|fieldList
operator|.
name|get
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|register
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
name|planner
operator|.
name|addRule
argument_list|(
name|FileRules
operator|.
name|PROJECT_SCAN
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
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
comment|// Multiply the cost by a factor that makes a scan more attractive if it
comment|// has significantly fewer fields than the original scan.
comment|//
comment|// The "+ 2D" on top and bottom keeps the function fairly smooth.
comment|//
comment|// For example, if table has 3 fields, project has 1 field,
comment|// then factor = (1 + 2) / (3 + 2) = 0.6
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
operator|(
operator|(
name|double
operator|)
name|fields
operator|.
name|length
operator|+
literal|2D
operator|)
operator|/
operator|(
operator|(
name|double
operator|)
name|table
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
operator|+
literal|2D
operator|)
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
name|preferArray
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|table
operator|instanceof
name|JsonTable
condition|)
block|{
return|return
name|implementor
operator|.
name|result
argument_list|(
name|physType
argument_list|,
name|Blocks
operator|.
name|toBlock
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|table
operator|.
name|getExpression
argument_list|(
name|JsonTable
operator|.
name|class
argument_list|)
argument_list|,
literal|"enumerable"
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
return|return
name|implementor
operator|.
name|result
argument_list|(
name|physType
argument_list|,
name|Blocks
operator|.
name|toBlock
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|table
operator|.
name|getExpression
argument_list|(
name|CsvTranslatableTable
operator|.
name|class
argument_list|)
argument_list|,
literal|"project"
argument_list|,
name|implementor
operator|.
name|getRootExpression
argument_list|()
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|fields
argument_list|)
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

