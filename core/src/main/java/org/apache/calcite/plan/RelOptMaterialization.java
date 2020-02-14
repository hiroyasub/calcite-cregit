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
name|plan
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
name|config
operator|.
name|CalciteSystemProperty
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
name|RelShuttleImpl
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
name|LogicalJoin
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
name|DefaultRelMetadataProvider
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
name|AggregateFilterTransposeRule
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
name|AggregateProjectMergeRule
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
name|FilterJoinRule
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
name|JoinProjectTransposeRule
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
name|ProjectFilterTransposeRule
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
name|ProjectMergeRule
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
name|ProjectRemoveRule
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rex
operator|.
name|RexUtil
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
name|apache
operator|.
name|calcite
operator|.
name|schema
operator|.
name|impl
operator|.
name|StarTable
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
name|sql
operator|.
name|SqlExplainFormat
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
name|sql
operator|.
name|SqlExplainLevel
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
name|Program
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
name|Programs
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
name|Util
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
name|mapping
operator|.
name|Mappings
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Records that a particular query is materialized by a particular table.  */
end_comment

begin_class
specifier|public
class|class
name|RelOptMaterialization
block|{
specifier|public
specifier|final
name|RelNode
name|tableRel
decl_stmt|;
specifier|public
specifier|final
name|RelOptTable
name|starRelOptTable
decl_stmt|;
specifier|public
specifier|final
name|StarTable
name|starTable
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|qualifiedTableName
decl_stmt|;
specifier|public
specifier|final
name|RelNode
name|queryRel
decl_stmt|;
comment|/**    * Creates a RelOptMaterialization.    */
specifier|public
name|RelOptMaterialization
parameter_list|(
name|RelNode
name|tableRel
parameter_list|,
name|RelNode
name|queryRel
parameter_list|,
name|RelOptTable
name|starRelOptTable
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|qualifiedTableName
parameter_list|)
block|{
name|this
operator|.
name|tableRel
operator|=
name|RelOptUtil
operator|.
name|createCastRel
argument_list|(
name|tableRel
argument_list|,
name|queryRel
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|starRelOptTable
operator|=
name|starRelOptTable
expr_stmt|;
if|if
condition|(
name|starRelOptTable
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|starTable
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|starTable
operator|=
name|starRelOptTable
operator|.
name|unwrap
argument_list|(
name|StarTable
operator|.
name|class
argument_list|)
expr_stmt|;
assert|assert
name|starTable
operator|!=
literal|null
assert|;
block|}
name|this
operator|.
name|qualifiedTableName
operator|=
name|qualifiedTableName
expr_stmt|;
name|this
operator|.
name|queryRel
operator|=
name|queryRel
expr_stmt|;
block|}
comment|/**    * Converts a relational expression to one that uses a    * {@link org.apache.calcite.schema.impl.StarTable}.    *    *<p>The relational expression is already in leaf-join-form, per    * {@link #toLeafJoinForm(org.apache.calcite.rel.RelNode)}.    *    * @return Rewritten expression, or null if expression cannot be rewritten    * to use the star    */
specifier|public
specifier|static
name|RelNode
name|tryUseStar
parameter_list|(
name|RelNode
name|rel
parameter_list|,
specifier|final
name|RelOptTable
name|starRelOptTable
parameter_list|)
block|{
specifier|final
name|StarTable
name|starTable
init|=
name|starRelOptTable
operator|.
name|unwrap
argument_list|(
name|StarTable
operator|.
name|class
argument_list|)
decl_stmt|;
assert|assert
name|starTable
operator|!=
literal|null
assert|;
name|RelNode
name|rel2
init|=
name|rel
operator|.
name|accept
argument_list|(
operator|new
name|RelShuttleImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|TableScan
name|scan
parameter_list|)
block|{
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
if|if
condition|(
name|table
operator|.
name|equals
argument_list|(
name|starTable
operator|.
name|tables
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
name|Mappings
operator|.
name|TargetMapping
name|mapping
init|=
name|Mappings
operator|.
name|createShiftMapping
argument_list|(
name|starRelOptTable
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|relOptTable
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RelOptCluster
name|cluster
init|=
name|scan
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|scan2
init|=
name|starRelOptTable
operator|.
name|toRel
argument_list|(
name|ViewExpanders
operator|.
name|simpleContext
argument_list|(
name|cluster
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|RelOptUtil
operator|.
name|createProject
argument_list|(
name|scan2
argument_list|,
name|Mappings
operator|.
name|asList
argument_list|(
name|mapping
operator|.
name|inverse
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
return|return
name|scan
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|LogicalJoin
name|join
parameter_list|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|RelNode
name|rel
init|=
name|super
operator|.
name|visit
argument_list|(
name|join
argument_list|)
decl_stmt|;
if|if
condition|(
name|rel
operator|==
name|join
operator|||
operator|!
operator|(
name|rel
operator|instanceof
name|LogicalJoin
operator|)
condition|)
block|{
return|return
name|rel
return|;
block|}
name|join
operator|=
operator|(
name|LogicalJoin
operator|)
name|rel
expr_stmt|;
specifier|final
name|ProjectFilterTable
name|left
init|=
name|ProjectFilterTable
operator|.
name|of
argument_list|(
name|join
operator|.
name|getLeft
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|left
operator|!=
literal|null
condition|)
block|{
specifier|final
name|ProjectFilterTable
name|right
init|=
name|ProjectFilterTable
operator|.
name|of
argument_list|(
name|join
operator|.
name|getRight
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|right
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|match
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|join
operator|.
name|getCluster
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Util
operator|.
name|FoundOne
name|e
parameter_list|)
block|{
return|return
operator|(
name|RelNode
operator|)
name|e
operator|.
name|getNode
argument_list|()
return|;
block|}
block|}
block|}
block|}
block|}
comment|/** Throws a {@link org.apache.calcite.util.Util.FoundOne} containing            * a {@link org.apache.calcite.rel.logical.LogicalTableScan} on            * success.  (Yes, an exception for normal operation.) */
specifier|private
name|void
name|match
parameter_list|(
name|ProjectFilterTable
name|left
parameter_list|,
name|ProjectFilterTable
name|right
parameter_list|,
name|RelOptCluster
name|cluster
parameter_list|)
block|{
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|leftMapping
init|=
name|left
operator|.
name|mapping
argument_list|()
decl_stmt|;
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|rightMapping
init|=
name|right
operator|.
name|mapping
argument_list|()
decl_stmt|;
specifier|final
name|RelOptTable
name|leftRelOptTable
init|=
name|left
operator|.
name|getTable
argument_list|()
decl_stmt|;
specifier|final
name|Table
name|leftTable
init|=
name|leftRelOptTable
operator|.
name|unwrap
argument_list|(
name|Table
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|int
name|leftCount
init|=
name|leftRelOptTable
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|RelOptTable
name|rightRelOptTable
init|=
name|right
operator|.
name|getTable
argument_list|()
decl_stmt|;
specifier|final
name|Table
name|rightTable
init|=
name|rightRelOptTable
operator|.
name|unwrap
argument_list|(
name|Table
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|leftTable
operator|instanceof
name|StarTable
operator|&&
operator|(
operator|(
name|StarTable
operator|)
name|leftTable
operator|)
operator|.
name|tables
operator|.
name|contains
argument_list|(
name|rightTable
argument_list|)
condition|)
block|{
specifier|final
name|int
name|offset
init|=
operator|(
operator|(
name|StarTable
operator|)
name|leftTable
operator|)
operator|.
name|columnOffset
argument_list|(
name|rightTable
argument_list|)
decl_stmt|;
name|Mappings
operator|.
name|TargetMapping
name|mapping
init|=
name|Mappings
operator|.
name|merge
argument_list|(
name|leftMapping
argument_list|,
name|Mappings
operator|.
name|offsetTarget
argument_list|(
name|Mappings
operator|.
name|offsetSource
argument_list|(
name|rightMapping
argument_list|,
name|offset
argument_list|)
argument_list|,
name|leftMapping
operator|.
name|getTargetCount
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|project
init|=
name|RelOptUtil
operator|.
name|createProject
argument_list|(
name|leftRelOptTable
operator|.
name|toRel
argument_list|(
name|ViewExpanders
operator|.
name|simpleContext
argument_list|(
name|cluster
argument_list|)
argument_list|)
argument_list|,
name|Mappings
operator|.
name|asList
argument_list|(
name|mapping
operator|.
name|inverse
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|conditions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|left
operator|.
name|condition
operator|!=
literal|null
condition|)
block|{
name|conditions
operator|.
name|add
argument_list|(
name|left
operator|.
name|condition
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|right
operator|.
name|condition
operator|!=
literal|null
condition|)
block|{
name|conditions
operator|.
name|add
argument_list|(
name|RexUtil
operator|.
name|apply
argument_list|(
name|mapping
argument_list|,
name|RexUtil
operator|.
name|shift
argument_list|(
name|right
operator|.
name|condition
argument_list|,
name|offset
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelNode
name|filter
init|=
name|RelOptUtil
operator|.
name|createFilter
argument_list|(
name|project
argument_list|,
name|conditions
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|Util
operator|.
name|FoundOne
argument_list|(
name|filter
argument_list|)
throw|;
block|}
if|if
condition|(
name|rightTable
operator|instanceof
name|StarTable
operator|&&
operator|(
operator|(
name|StarTable
operator|)
name|rightTable
operator|)
operator|.
name|tables
operator|.
name|contains
argument_list|(
name|leftTable
argument_list|)
condition|)
block|{
specifier|final
name|int
name|offset
init|=
operator|(
operator|(
name|StarTable
operator|)
name|rightTable
operator|)
operator|.
name|columnOffset
argument_list|(
name|leftTable
argument_list|)
decl_stmt|;
name|Mappings
operator|.
name|TargetMapping
name|mapping
init|=
name|Mappings
operator|.
name|merge
argument_list|(
name|Mappings
operator|.
name|offsetSource
argument_list|(
name|leftMapping
argument_list|,
name|offset
argument_list|)
argument_list|,
name|Mappings
operator|.
name|offsetTarget
argument_list|(
name|rightMapping
argument_list|,
name|leftCount
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|project
init|=
name|RelOptUtil
operator|.
name|createProject
argument_list|(
name|rightRelOptTable
operator|.
name|toRel
argument_list|(
name|ViewExpanders
operator|.
name|simpleContext
argument_list|(
name|cluster
argument_list|)
argument_list|)
argument_list|,
name|Mappings
operator|.
name|asList
argument_list|(
name|mapping
operator|.
name|inverse
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|conditions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|left
operator|.
name|condition
operator|!=
literal|null
condition|)
block|{
name|conditions
operator|.
name|add
argument_list|(
name|RexUtil
operator|.
name|apply
argument_list|(
name|mapping
argument_list|,
name|RexUtil
operator|.
name|shift
argument_list|(
name|left
operator|.
name|condition
argument_list|,
name|offset
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|right
operator|.
name|condition
operator|!=
literal|null
condition|)
block|{
name|conditions
operator|.
name|add
argument_list|(
name|RexUtil
operator|.
name|apply
argument_list|(
name|mapping
argument_list|,
name|right
operator|.
name|condition
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelNode
name|filter
init|=
name|RelOptUtil
operator|.
name|createFilter
argument_list|(
name|project
argument_list|,
name|conditions
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|Util
operator|.
name|FoundOne
argument_list|(
name|filter
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|rel2
operator|==
name|rel
condition|)
block|{
comment|// No rewrite happened.
return|return
literal|null
return|;
block|}
specifier|final
name|Program
name|program
init|=
name|Programs
operator|.
name|hep
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|ProjectFilterTransposeRule
operator|.
name|INSTANCE
argument_list|,
name|AggregateProjectMergeRule
operator|.
name|INSTANCE
argument_list|,
name|AggregateFilterTransposeRule
operator|.
name|INSTANCE
argument_list|)
argument_list|,
literal|false
argument_list|,
name|DefaultRelMetadataProvider
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
return|return
name|program
operator|.
name|run
argument_list|(
literal|null
argument_list|,
name|rel2
argument_list|,
literal|null
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
return|;
block|}
comment|/** A table scan and optional project mapping and filter condition. */
specifier|private
specifier|static
class|class
name|ProjectFilterTable
block|{
specifier|final
name|RexNode
name|condition
decl_stmt|;
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|mapping
decl_stmt|;
specifier|final
name|TableScan
name|scan
decl_stmt|;
specifier|private
name|ProjectFilterTable
parameter_list|(
name|RexNode
name|condition
parameter_list|,
name|Mappings
operator|.
name|TargetMapping
name|mapping
parameter_list|,
name|TableScan
name|scan
parameter_list|)
block|{
name|this
operator|.
name|condition
operator|=
name|condition
expr_stmt|;
name|this
operator|.
name|mapping
operator|=
name|mapping
expr_stmt|;
name|this
operator|.
name|scan
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|scan
argument_list|)
expr_stmt|;
block|}
specifier|static
name|ProjectFilterTable
name|of
parameter_list|(
name|RelNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Filter
condition|)
block|{
specifier|final
name|Filter
name|filter
init|=
operator|(
name|Filter
operator|)
name|node
decl_stmt|;
return|return
name|of2
argument_list|(
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|,
name|filter
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|of2
argument_list|(
literal|null
argument_list|,
name|node
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|ProjectFilterTable
name|of2
parameter_list|(
name|RexNode
name|condition
parameter_list|,
name|RelNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Project
condition|)
block|{
specifier|final
name|Project
name|project
init|=
operator|(
name|Project
operator|)
name|node
decl_stmt|;
return|return
name|of3
argument_list|(
name|condition
argument_list|,
name|project
operator|.
name|getMapping
argument_list|()
argument_list|,
name|project
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|of3
argument_list|(
name|condition
argument_list|,
literal|null
argument_list|,
name|node
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|ProjectFilterTable
name|of3
parameter_list|(
name|RexNode
name|condition
parameter_list|,
name|Mappings
operator|.
name|TargetMapping
name|mapping
parameter_list|,
name|RelNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|TableScan
condition|)
block|{
return|return
operator|new
name|ProjectFilterTable
argument_list|(
name|condition
argument_list|,
name|mapping
argument_list|,
operator|(
name|TableScan
operator|)
name|node
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|Mappings
operator|.
name|TargetMapping
name|mapping
parameter_list|()
block|{
return|return
name|mapping
operator|!=
literal|null
condition|?
name|mapping
else|:
name|Mappings
operator|.
name|createIdentity
argument_list|(
name|scan
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RelOptTable
name|getTable
parameter_list|()
block|{
return|return
name|scan
operator|.
name|getTable
argument_list|()
return|;
block|}
block|}
comment|/**    * Converts a relational expression to a form where    * {@link org.apache.calcite.rel.logical.LogicalJoin}s are    * as close to leaves as possible.    */
specifier|public
specifier|static
name|RelNode
name|toLeafJoinForm
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|Program
name|program
init|=
name|Programs
operator|.
name|hep
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|JoinProjectTransposeRule
operator|.
name|RIGHT_PROJECT
argument_list|,
name|JoinProjectTransposeRule
operator|.
name|LEFT_PROJECT
argument_list|,
name|FilterJoinRule
operator|.
name|FilterIntoJoinRule
operator|.
name|FILTER_ON_JOIN
argument_list|,
name|ProjectRemoveRule
operator|.
name|INSTANCE
argument_list|,
name|ProjectMergeRule
operator|.
name|INSTANCE
argument_list|)
argument_list|,
literal|false
argument_list|,
name|DefaultRelMetadataProvider
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
if|if
condition|(
name|CalciteSystemProperty
operator|.
name|DEBUG
operator|.
name|value
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|RelOptUtil
operator|.
name|dumpPlan
argument_list|(
literal|"before"
argument_list|,
name|rel
argument_list|,
name|SqlExplainFormat
operator|.
name|TEXT
argument_list|,
name|SqlExplainLevel
operator|.
name|DIGEST_ATTRIBUTES
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelNode
name|rel2
init|=
name|program
operator|.
name|run
argument_list|(
literal|null
argument_list|,
name|rel
argument_list|,
literal|null
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|CalciteSystemProperty
operator|.
name|DEBUG
operator|.
name|value
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|RelOptUtil
operator|.
name|dumpPlan
argument_list|(
literal|"after"
argument_list|,
name|rel2
argument_list|,
name|SqlExplainFormat
operator|.
name|TEXT
argument_list|,
name|SqlExplainLevel
operator|.
name|DIGEST_ATTRIBUTES
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|rel2
return|;
block|}
block|}
end_class

end_unit

