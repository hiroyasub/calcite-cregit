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
name|logical
operator|.
name|LogicalTableScan
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
name|schema
operator|.
name|QueryableTable
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
name|schema
operator|.
name|Table
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

begin_comment
comment|/** Planner rule that converts a {@link LogicalTableScan} to an {@link EnumerableTableScan}.  * You may provide a custom config to convert other nodes that extend {@link TableScan}.  *  * @see EnumerableRules#ENUMERABLE_TABLE_SCAN_RULE */
end_comment

begin_class
specifier|public
class|class
name|EnumerableTableScanRule
extends|extends
name|ConverterRule
block|{
comment|/** Default configuration. */
specifier|public
specifier|static
specifier|final
name|Config
name|DEFAULT_CONFIG
init|=
name|Config
operator|.
name|EMPTY
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|withConversion
argument_list|(
name|LogicalTableScan
operator|.
name|class
argument_list|,
name|r
lambda|->
name|EnumerableTableScan
operator|.
name|canHandle
argument_list|(
name|r
operator|.
name|getTable
argument_list|()
argument_list|)
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|,
literal|"EnumerableTableScanRule"
argument_list|)
operator|.
name|withRuleFactory
argument_list|(
name|EnumerableTableScanRule
operator|::
operator|new
argument_list|)
decl_stmt|;
specifier|protected
name|EnumerableTableScanRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
name|TableScan
name|scan
init|=
operator|(
name|TableScan
operator|)
name|rel
decl_stmt|;
specifier|final
name|RelOptTable
name|relOptTable
init|=
name|scan
operator|.
name|getTable
argument_list|()
decl_stmt|;
specifier|final
name|Table
name|table
init|=
name|relOptTable
operator|.
name|unwrap
argument_list|(
name|Table
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// The QueryableTable can only be implemented as ENUMERABLE convention,
comment|// but some test QueryableTables do not really implement the expressions,
comment|// just skips the QueryableTable#getExpression invocation and returns early.
if|if
condition|(
name|table
operator|instanceof
name|QueryableTable
operator|||
name|relOptTable
operator|.
name|getExpression
argument_list|(
name|Object
operator|.
name|class
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|EnumerableTableScan
operator|.
name|create
argument_list|(
name|scan
operator|.
name|getCluster
argument_list|()
argument_list|,
name|relOptTable
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

