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
name|rel
operator|.
name|metadata
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
name|AggregateCall
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
name|Calc
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
name|Exchange
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
name|Snapshot
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
name|TableModify
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
name|rex
operator|.
name|RexInputRef
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
name|RexLocalRef
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
name|RexShuttle
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
name|RexVisitor
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
name|RexVisitorImpl
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
name|PolyNull
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
name|HashSet
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
comment|/**  * RelMdColumnOrigins supplies a default implementation of  * {@link RelMetadataQuery#getColumnOrigins} for the standard logical algebra.  */
end_comment

begin_class
specifier|public
class|class
name|RelMdColumnOrigins
implements|implements
name|MetadataHandler
argument_list|<
name|BuiltInMetadata
operator|.
name|ColumnOrigin
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
name|RelMetadataProvider
name|SOURCE
init|=
name|ReflectiveRelMetadataProvider
operator|.
name|reflectiveSource
argument_list|(
operator|new
name|RelMdColumnOrigins
argument_list|()
argument_list|,
name|BuiltInMetadata
operator|.
name|ColumnOrigin
operator|.
name|Handler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|RelMdColumnOrigins
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|MetadataDef
argument_list|<
name|BuiltInMetadata
operator|.
name|ColumnOrigin
argument_list|>
name|getDef
parameter_list|()
block|{
return|return
name|BuiltInMetadata
operator|.
name|ColumnOrigin
operator|.
name|DEF
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|getColumnOrigins
parameter_list|(
name|Aggregate
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|int
name|iOutputColumn
parameter_list|)
block|{
if|if
condition|(
name|iOutputColumn
operator|<
name|rel
operator|.
name|getGroupCount
argument_list|()
condition|)
block|{
comment|// get actual index of Group columns.
return|return
name|mq
operator|.
name|getColumnOrigins
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|rel
operator|.
name|getGroupSet
argument_list|()
operator|.
name|asList
argument_list|()
operator|.
name|get
argument_list|(
name|iOutputColumn
argument_list|)
argument_list|)
return|;
block|}
comment|// Aggregate columns are derived from input columns
name|AggregateCall
name|call
init|=
name|rel
operator|.
name|getAggCallList
argument_list|()
operator|.
name|get
argument_list|(
name|iOutputColumn
operator|-
name|rel
operator|.
name|getGroupCount
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Integer
name|iInput
range|:
name|call
operator|.
name|getArgList
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|inputSet
init|=
name|mq
operator|.
name|getColumnOrigins
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|iInput
argument_list|)
decl_stmt|;
name|inputSet
operator|=
name|createDerivedColumnOrigins
argument_list|(
name|inputSet
argument_list|)
expr_stmt|;
if|if
condition|(
name|inputSet
operator|!=
literal|null
condition|)
block|{
name|set
operator|.
name|addAll
argument_list|(
name|inputSet
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|set
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|getColumnOrigins
parameter_list|(
name|Join
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|int
name|iOutputColumn
parameter_list|)
block|{
name|int
name|nLeftColumns
init|=
name|rel
operator|.
name|getLeft
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
decl_stmt|;
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|set
decl_stmt|;
name|boolean
name|derived
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|iOutputColumn
operator|<
name|nLeftColumns
condition|)
block|{
name|set
operator|=
name|mq
operator|.
name|getColumnOrigins
argument_list|(
name|rel
operator|.
name|getLeft
argument_list|()
argument_list|,
name|iOutputColumn
argument_list|)
expr_stmt|;
if|if
condition|(
name|rel
operator|.
name|getJoinType
argument_list|()
operator|.
name|generatesNullsOnLeft
argument_list|()
condition|)
block|{
name|derived
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
name|set
operator|=
name|mq
operator|.
name|getColumnOrigins
argument_list|(
name|rel
operator|.
name|getRight
argument_list|()
argument_list|,
name|iOutputColumn
operator|-
name|nLeftColumns
argument_list|)
expr_stmt|;
if|if
condition|(
name|rel
operator|.
name|getJoinType
argument_list|()
operator|.
name|generatesNullsOnRight
argument_list|()
condition|)
block|{
name|derived
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|derived
condition|)
block|{
comment|// nulls are generated due to outer join; that counts
comment|// as derivation
name|set
operator|=
name|createDerivedColumnOrigins
argument_list|(
name|set
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|getColumnOrigins
parameter_list|(
name|SetOp
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|int
name|iOutputColumn
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|rel
operator|.
name|getInputs
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|inputSet
init|=
name|mq
operator|.
name|getColumnOrigins
argument_list|(
name|input
argument_list|,
name|iOutputColumn
argument_list|)
decl_stmt|;
if|if
condition|(
name|inputSet
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|set
operator|.
name|addAll
argument_list|(
name|inputSet
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|getColumnOrigins
parameter_list|(
name|Project
name|rel
parameter_list|,
specifier|final
name|RelMetadataQuery
name|mq
parameter_list|,
name|int
name|iOutputColumn
parameter_list|)
block|{
specifier|final
name|RelNode
name|input
init|=
name|rel
operator|.
name|getInput
argument_list|()
decl_stmt|;
name|RexNode
name|rexNode
init|=
name|rel
operator|.
name|getProjects
argument_list|()
operator|.
name|get
argument_list|(
name|iOutputColumn
argument_list|)
decl_stmt|;
if|if
condition|(
name|rexNode
operator|instanceof
name|RexInputRef
condition|)
block|{
comment|// Direct reference:  no derivation added.
name|RexInputRef
name|inputRef
init|=
operator|(
name|RexInputRef
operator|)
name|rexNode
decl_stmt|;
return|return
name|mq
operator|.
name|getColumnOrigins
argument_list|(
name|input
argument_list|,
name|inputRef
operator|.
name|getIndex
argument_list|()
argument_list|)
return|;
block|}
comment|// Anything else is a derivation, possibly from multiple columns.
specifier|final
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|set
init|=
name|getMultipleColumns
argument_list|(
name|rexNode
argument_list|,
name|input
argument_list|,
name|mq
argument_list|)
decl_stmt|;
return|return
name|createDerivedColumnOrigins
argument_list|(
name|set
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|getColumnOrigins
parameter_list|(
name|Calc
name|rel
parameter_list|,
specifier|final
name|RelMetadataQuery
name|mq
parameter_list|,
name|int
name|iOutputColumn
parameter_list|)
block|{
specifier|final
name|RelNode
name|input
init|=
name|rel
operator|.
name|getInput
argument_list|()
decl_stmt|;
specifier|final
name|RexShuttle
name|rexShuttle
init|=
operator|new
name|RexShuttle
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RexNode
name|visitLocalRef
parameter_list|(
name|RexLocalRef
name|localRef
parameter_list|)
block|{
return|return
name|rel
operator|.
name|getProgram
argument_list|()
operator|.
name|expandLocalRef
argument_list|(
name|localRef
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|projects
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|rex
range|:
name|rexShuttle
operator|.
name|apply
argument_list|(
name|rel
operator|.
name|getProgram
argument_list|()
operator|.
name|getProjectList
argument_list|()
argument_list|)
control|)
block|{
name|projects
operator|.
name|add
argument_list|(
name|rex
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RexNode
name|rexNode
init|=
name|projects
operator|.
name|get
argument_list|(
name|iOutputColumn
argument_list|)
decl_stmt|;
if|if
condition|(
name|rexNode
operator|instanceof
name|RexInputRef
condition|)
block|{
comment|// Direct reference:  no derivation added.
name|RexInputRef
name|inputRef
init|=
operator|(
name|RexInputRef
operator|)
name|rexNode
decl_stmt|;
return|return
name|mq
operator|.
name|getColumnOrigins
argument_list|(
name|input
argument_list|,
name|inputRef
operator|.
name|getIndex
argument_list|()
argument_list|)
return|;
block|}
comment|// Anything else is a derivation, possibly from multiple columns.
specifier|final
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|set
init|=
name|getMultipleColumns
argument_list|(
name|rexNode
argument_list|,
name|input
argument_list|,
name|mq
argument_list|)
decl_stmt|;
return|return
name|createDerivedColumnOrigins
argument_list|(
name|set
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|getColumnOrigins
parameter_list|(
name|TableScan
name|scan
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|int
name|iOutputColumn
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|ColumnOrigin
operator|.
name|Handler
name|handler
init|=
name|scan
operator|.
name|getTable
argument_list|()
operator|.
name|unwrap
argument_list|(
name|BuiltInMetadata
operator|.
name|ColumnOrigin
operator|.
name|Handler
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|!=
literal|null
condition|)
block|{
return|return
name|handler
operator|.
name|getColumnOrigins
argument_list|(
name|scan
argument_list|,
name|mq
argument_list|,
name|iOutputColumn
argument_list|)
return|;
block|}
return|return
name|getColumnOrigins
argument_list|(
operator|(
name|RelNode
operator|)
name|scan
argument_list|,
name|mq
argument_list|,
name|iOutputColumn
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|getColumnOrigins
parameter_list|(
name|Filter
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|int
name|iOutputColumn
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getColumnOrigins
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|iOutputColumn
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|getColumnOrigins
parameter_list|(
name|Sort
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|int
name|iOutputColumn
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getColumnOrigins
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|iOutputColumn
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|getColumnOrigins
parameter_list|(
name|TableModify
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|int
name|iOutputColumn
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getColumnOrigins
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|iOutputColumn
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|getColumnOrigins
parameter_list|(
name|Exchange
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|int
name|iOutputColumn
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getColumnOrigins
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|iOutputColumn
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|getColumnOrigins
parameter_list|(
name|Snapshot
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|int
name|iOutputColumn
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getColumnOrigins
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|iOutputColumn
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|getColumnOrigins
parameter_list|(
name|TableFunctionScan
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|int
name|iOutputColumn
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|RelColumnMapping
argument_list|>
name|mappings
init|=
name|rel
operator|.
name|getColumnMappings
argument_list|()
decl_stmt|;
if|if
condition|(
name|mappings
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|rel
operator|.
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// This is a non-leaf transformation:  say we don't
comment|// know about origins, because there are probably
comment|// columns below.
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// This is a leaf transformation: say there are fer sure no
comment|// column origins.
return|return
name|set
return|;
block|}
block|}
for|for
control|(
name|RelColumnMapping
name|mapping
range|:
name|mappings
control|)
block|{
if|if
condition|(
name|mapping
operator|.
name|iOutputColumn
operator|!=
name|iOutputColumn
condition|)
block|{
continue|continue;
block|}
specifier|final
name|RelNode
name|input
init|=
name|rel
operator|.
name|getInputs
argument_list|()
operator|.
name|get
argument_list|(
name|mapping
operator|.
name|iInputRel
argument_list|)
decl_stmt|;
specifier|final
name|int
name|column
init|=
name|mapping
operator|.
name|iInputColumn
decl_stmt|;
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|origins
init|=
name|mq
operator|.
name|getColumnOrigins
argument_list|(
name|input
argument_list|,
name|column
argument_list|)
decl_stmt|;
if|if
condition|(
name|origins
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|mapping
operator|.
name|derived
condition|)
block|{
name|origins
operator|=
name|createDerivedColumnOrigins
argument_list|(
name|origins
argument_list|)
expr_stmt|;
block|}
name|set
operator|.
name|addAll
argument_list|(
name|origins
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
comment|// Catch-all rule when none of the others apply.
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|getColumnOrigins
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|int
name|iOutputColumn
parameter_list|)
block|{
comment|// NOTE jvs 28-Mar-2006: We may get this wrong for a physical table
comment|// expression which supports projections.  In that case,
comment|// it's up to the plugin writer to override with the
comment|// correct information.
if|if
condition|(
name|rel
operator|.
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// No generic logic available for non-leaf rels.
return|return
literal|null
return|;
block|}
specifier|final
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|RelOptTable
name|table
init|=
name|rel
operator|.
name|getTable
argument_list|()
decl_stmt|;
if|if
condition|(
name|table
operator|==
literal|null
condition|)
block|{
comment|// Somebody is making column values up out of thin air, like a
comment|// VALUES clause, so we return an empty set.
return|return
name|set
return|;
block|}
comment|// Detect the case where a physical table expression is performing
comment|// projection, and say we don't know instead of making any assumptions.
comment|// (Theoretically we could try to map the projection using column
comment|// names.)  This detection assumes the table expression doesn't handle
comment|// rename as well.
if|if
condition|(
name|table
operator|.
name|getRowType
argument_list|()
operator|!=
name|rel
operator|.
name|getRowType
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|set
operator|.
name|add
argument_list|(
operator|new
name|RelColumnOrigin
argument_list|(
name|table
argument_list|,
name|iOutputColumn
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|set
return|;
block|}
specifier|private
specifier|static
annotation|@
name|PolyNull
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|createDerivedColumnOrigins
parameter_list|(
annotation|@
name|PolyNull
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|inputSet
parameter_list|)
block|{
if|if
condition|(
name|inputSet
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelColumnOrigin
name|rco
range|:
name|inputSet
control|)
block|{
name|RelColumnOrigin
name|derived
init|=
operator|new
name|RelColumnOrigin
argument_list|(
name|rco
operator|.
name|getOriginTable
argument_list|()
argument_list|,
name|rco
operator|.
name|getOriginColumnOrdinal
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
name|derived
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|getMultipleColumns
parameter_list|(
name|RexNode
name|rexNode
parameter_list|,
name|RelNode
name|input
parameter_list|,
specifier|final
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|RexVisitor
argument_list|<
name|Void
argument_list|>
name|visitor
init|=
operator|new
name|RexVisitorImpl
argument_list|<
name|Void
argument_list|>
argument_list|(
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Void
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|)
block|{
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|inputSet
init|=
name|mq
operator|.
name|getColumnOrigins
argument_list|(
name|input
argument_list|,
name|inputRef
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|inputSet
operator|!=
literal|null
condition|)
block|{
name|set
operator|.
name|addAll
argument_list|(
name|inputSet
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
name|rexNode
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
return|return
name|set
return|;
block|}
block|}
end_class

end_unit

