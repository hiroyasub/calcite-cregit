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
name|interpreter
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
name|rel
operator|.
name|core
operator|.
name|Aggregate
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
name|rel
operator|.
name|core
operator|.
name|Filter
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
name|Join
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
name|Match
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
name|Project
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
name|SetOp
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
name|Sort
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
name|core
operator|.
name|Uncollect
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
name|core
operator|.
name|Window
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
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|initialization
operator|.
name|qual
operator|.
name|UnknownInitialization
import|;
end_import

begin_comment
comment|/**  * Helper methods for {@link Node} and implementations for core relational  * expressions.  */
end_comment

begin_class
specifier|public
class|class
name|Nodes
block|{
comment|/** Extension to    * {@link Interpreter.CompilerImpl}    * that knows how to handle the core logical    * {@link org.apache.calcite.rel.RelNode}s. */
specifier|public
specifier|static
class|class
name|CoreCompiler
extends|extends
name|Interpreter
operator|.
name|CompilerImpl
block|{
name|CoreCompiler
parameter_list|(
annotation|@
name|UnknownInitialization
name|Interpreter
name|interpreter
parameter_list|,
name|RelOptCluster
name|cluster
parameter_list|)
block|{
name|super
argument_list|(
name|interpreter
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|Aggregate
name|agg
parameter_list|)
block|{
name|node
operator|=
operator|new
name|AggregateNode
argument_list|(
name|this
argument_list|,
name|agg
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|node
operator|=
operator|new
name|FilterNode
argument_list|(
name|this
argument_list|,
name|filter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|Project
name|project
parameter_list|)
block|{
name|node
operator|=
operator|new
name|ProjectNode
argument_list|(
name|this
argument_list|,
name|project
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|Values
name|value
parameter_list|)
block|{
name|node
operator|=
operator|new
name|ValuesNode
argument_list|(
name|this
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|TableScan
name|scan
parameter_list|)
block|{
specifier|final
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|filters
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
name|node
operator|=
name|TableScanNode
operator|.
name|create
argument_list|(
name|this
argument_list|,
name|scan
argument_list|,
name|filters
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|Bindables
operator|.
name|BindableTableScan
name|scan
parameter_list|)
block|{
name|node
operator|=
name|TableScanNode
operator|.
name|create
argument_list|(
name|this
argument_list|,
name|scan
argument_list|,
name|scan
operator|.
name|filters
argument_list|,
name|scan
operator|.
name|projects
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|TableFunctionScan
name|functionScan
parameter_list|)
block|{
name|node
operator|=
name|TableFunctionScanNode
operator|.
name|create
argument_list|(
name|this
argument_list|,
name|functionScan
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|Sort
name|sort
parameter_list|)
block|{
name|node
operator|=
operator|new
name|SortNode
argument_list|(
name|this
argument_list|,
name|sort
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|SetOp
name|setOp
parameter_list|)
block|{
name|node
operator|=
operator|new
name|SetOpNode
argument_list|(
name|this
argument_list|,
name|setOp
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|Join
name|join
parameter_list|)
block|{
name|node
operator|=
operator|new
name|JoinNode
argument_list|(
name|this
argument_list|,
name|join
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|Window
name|window
parameter_list|)
block|{
name|node
operator|=
operator|new
name|WindowNode
argument_list|(
name|this
argument_list|,
name|window
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|Match
name|match
parameter_list|)
block|{
name|node
operator|=
operator|new
name|MatchNode
argument_list|(
name|this
argument_list|,
name|match
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|Collect
name|collect
parameter_list|)
block|{
name|node
operator|=
operator|new
name|CollectNode
argument_list|(
name|this
argument_list|,
name|collect
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|Uncollect
name|uncollect
parameter_list|)
block|{
name|node
operator|=
operator|new
name|UncollectNode
argument_list|(
name|this
argument_list|,
name|uncollect
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

