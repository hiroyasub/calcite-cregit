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
name|pig
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
name|EnumerableRules
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
name|jdbc
operator|.
name|CalciteSchema
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
name|RelOptRule
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
name|rules
operator|.
name|AggregateExpandDistinctAggregatesRule
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
name|pig
operator|.
name|data
operator|.
name|DataType
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
name|base
operator|.
name|Joiner
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
comment|/** Implementation of {@link org.apache.calcite.rel.core.TableScan} in  * {@link PigRel#CONVENTION Pig calling convention}. */
end_comment

begin_class
specifier|public
class|class
name|PigTableScan
extends|extends
name|TableScan
implements|implements
name|PigRel
block|{
comment|/** Creates a PigTableScan. */
specifier|public
name|PigTableScan
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelOptTable
name|table
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|table
argument_list|)
expr_stmt|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|PigRel
operator|.
name|CONVENTION
assert|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|implement
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
block|{
specifier|final
name|PigTable
name|pigTable
init|=
name|getPigTable
argument_list|(
name|implementor
operator|.
name|getTableName
argument_list|(
name|this
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|alias
init|=
name|implementor
operator|.
name|getPigRelationAlias
argument_list|(
name|this
argument_list|)
decl_stmt|;
specifier|final
name|String
name|schema
init|=
literal|'('
operator|+
name|getSchemaForPigStatement
argument_list|(
name|implementor
argument_list|)
operator|+
literal|')'
decl_stmt|;
specifier|final
name|String
name|statement
init|=
name|alias
operator|+
literal|" = LOAD '"
operator|+
name|pigTable
operator|.
name|getFilePath
argument_list|()
operator|+
literal|"' USING PigStorage() AS "
operator|+
name|schema
operator|+
literal|';'
decl_stmt|;
name|implementor
operator|.
name|addStatement
argument_list|(
name|statement
argument_list|)
expr_stmt|;
block|}
specifier|private
name|PigTable
name|getPigTable
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|CalciteSchema
name|schema
init|=
name|getTable
argument_list|()
operator|.
name|unwrap
argument_list|(
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|jdbc
operator|.
name|CalciteSchema
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
operator|(
name|PigTable
operator|)
name|schema
operator|.
name|getTable
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
operator|.
name|getTable
argument_list|()
return|;
block|}
specifier|private
name|String
name|getSchemaForPigStatement
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|fieldNamesAndTypes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|getTable
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|f
range|:
name|getTable
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
control|)
block|{
name|fieldNamesAndTypes
operator|.
name|add
argument_list|(
name|getConcatenatedFieldNameAndTypeForPigSchema
argument_list|(
name|implementor
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Joiner
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|join
argument_list|(
name|fieldNamesAndTypes
argument_list|)
return|;
block|}
specifier|private
name|String
name|getConcatenatedFieldNameAndTypeForPigSchema
parameter_list|(
name|Implementor
name|implementor
parameter_list|,
name|RelDataTypeField
name|field
parameter_list|)
block|{
specifier|final
name|PigDataType
name|pigDataType
init|=
name|PigDataType
operator|.
name|valueOf
argument_list|(
name|field
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|fieldName
init|=
name|implementor
operator|.
name|getFieldName
argument_list|(
name|this
argument_list|,
name|field
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|fieldName
operator|+
literal|':'
operator|+
name|DataType
operator|.
name|findTypeName
argument_list|(
name|pigDataType
operator|.
name|getPigType
argument_list|()
argument_list|)
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
name|PigToEnumerableConverterRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
for|for
control|(
name|RelOptRule
name|rule
range|:
name|PigRules
operator|.
name|ALL_PIG_OPT_RULES
control|)
block|{
name|planner
operator|.
name|addRule
argument_list|(
name|rule
argument_list|)
expr_stmt|;
block|}
comment|// Don't move Aggregates around, otherwise PigAggregate.implement() won't
comment|// know how to correctly procuce Pig Latin
name|planner
operator|.
name|removeRule
argument_list|(
name|AggregateExpandDistinctAggregatesRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
comment|// Make sure planner picks PigJoin over EnumerableJoin. Should there be
comment|// a rule for this instead for removing ENUMERABLE_JOIN_RULE here?
name|planner
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End PigTableScan.java
end_comment

end_unit

