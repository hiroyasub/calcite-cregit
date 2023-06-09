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
name|core
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
name|RelOptSchema
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
name|RelOptUtil
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
name|prepare
operator|.
name|Prepare
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
name|RelInput
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
name|SingleRel
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
name|externalize
operator|.
name|RelEnumTypes
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
name|sql
operator|.
name|SqlKind
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
name|type
operator|.
name|SqlTypeUtil
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
name|Preconditions
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
name|MonotonicNonNull
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Relational expression that modifies a table.  *  *<p>It is similar to {@link org.apache.calcite.rel.core.TableScan},  * but represents a request to modify a table rather than read from it.  * It takes one child which produces the modified rows. Those rows are:  *  *<ul>  *<li>For {@code INSERT}, those rows are the new values;  *<li>for {@code DELETE}, the old values;  *<li>for {@code UPDATE}, all old values plus updated new values.  *</ul>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TableModify
extends|extends
name|SingleRel
block|{
comment|//~ Enums ------------------------------------------------------------------
comment|/**    * Enumeration of supported modification operations.    */
specifier|public
enum|enum
name|Operation
block|{
name|INSERT
block|,
name|UPDATE
block|,
name|DELETE
block|,
name|MERGE
block|}
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * The connection to the optimizing session.    */
specifier|protected
name|Prepare
operator|.
name|CatalogReader
name|catalogReader
decl_stmt|;
comment|/**    * The table definition.    */
specifier|protected
specifier|final
name|RelOptTable
name|table
decl_stmt|;
specifier|private
specifier|final
name|Operation
name|operation
decl_stmt|;
specifier|private
specifier|final
annotation|@
name|Nullable
name|List
argument_list|<
name|String
argument_list|>
name|updateColumnList
decl_stmt|;
specifier|private
specifier|final
annotation|@
name|Nullable
name|List
argument_list|<
name|RexNode
argument_list|>
name|sourceExpressionList
decl_stmt|;
specifier|private
annotation|@
name|MonotonicNonNull
name|RelDataType
name|inputRowType
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|flattened
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a {@code TableModify}.    *    *<p>The UPDATE operation has format like this:    *<blockquote>    *<pre>UPDATE table SET iden1 = exp1, ident2 = exp2  WHERE condition</pre>    *</blockquote>    *    * @param cluster    Cluster this relational expression belongs to    * @param traitSet   Traits of this relational expression    * @param table      Target table to modify    * @param catalogReader accessor to the table metadata.    * @param input      Sub-query or filter condition    * @param operation  Modify operation (INSERT, UPDATE, DELETE)    * @param updateColumnList List of column identifiers to be updated    *           (e.g. ident1, ident2); null if not UPDATE    * @param sourceExpressionList List of value expressions to be set    *           (e.g. exp1, exp2); null if not UPDATE    * @param flattened Whether set flattens the input row type    */
specifier|protected
name|TableModify
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelOptTable
name|table
parameter_list|,
name|Prepare
operator|.
name|CatalogReader
name|catalogReader
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|Operation
name|operation
parameter_list|,
annotation|@
name|Nullable
name|List
argument_list|<
name|String
argument_list|>
name|updateColumnList
parameter_list|,
annotation|@
name|Nullable
name|List
argument_list|<
name|RexNode
argument_list|>
name|sourceExpressionList
parameter_list|,
name|boolean
name|flattened
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|table
operator|=
name|table
expr_stmt|;
name|this
operator|.
name|catalogReader
operator|=
name|catalogReader
expr_stmt|;
name|this
operator|.
name|operation
operator|=
name|operation
expr_stmt|;
name|this
operator|.
name|updateColumnList
operator|=
name|updateColumnList
expr_stmt|;
name|this
operator|.
name|sourceExpressionList
operator|=
name|sourceExpressionList
expr_stmt|;
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|UPDATE
condition|)
block|{
name|requireNonNull
argument_list|(
name|updateColumnList
argument_list|,
literal|"updateColumnList"
argument_list|)
expr_stmt|;
name|requireNonNull
argument_list|(
name|sourceExpressionList
argument_list|,
literal|"sourceExpressionList"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|sourceExpressionList
operator|.
name|size
argument_list|()
operator|==
name|updateColumnList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|operation
operator|==
name|Operation
operator|.
name|MERGE
condition|)
block|{
name|requireNonNull
argument_list|(
name|updateColumnList
argument_list|,
literal|"updateColumnList"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|updateColumnList
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|sourceExpressionList
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
name|RelOptSchema
name|relOptSchema
init|=
name|table
operator|.
name|getRelOptSchema
argument_list|()
decl_stmt|;
if|if
condition|(
name|relOptSchema
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|getPlanner
argument_list|()
operator|.
name|registerSchema
argument_list|(
name|relOptSchema
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|flattened
operator|=
name|flattened
expr_stmt|;
block|}
comment|/**    * Creates a TableModify by parsing serialized output.    */
specifier|protected
name|TableModify
parameter_list|(
name|RelInput
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
operator|.
name|getCluster
argument_list|()
argument_list|,
name|input
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|input
operator|.
name|getTable
argument_list|(
literal|"table"
argument_list|)
argument_list|,
operator|(
name|Prepare
operator|.
name|CatalogReader
operator|)
name|requireNonNull
argument_list|(
name|input
operator|.
name|getTable
argument_list|(
literal|"table"
argument_list|)
operator|.
name|getRelOptSchema
argument_list|()
argument_list|,
literal|"relOptSchema"
argument_list|)
argument_list|,
name|input
operator|.
name|getInput
argument_list|()
argument_list|,
name|requireNonNull
argument_list|(
name|input
operator|.
name|getEnum
argument_list|(
literal|"operation"
argument_list|,
name|Operation
operator|.
name|class
argument_list|)
argument_list|,
literal|"operation"
argument_list|)
argument_list|,
name|input
operator|.
name|getStringList
argument_list|(
literal|"updateColumnList"
argument_list|)
argument_list|,
name|input
operator|.
name|getExpressionList
argument_list|(
literal|"sourceExpressionList"
argument_list|)
argument_list|,
name|input
operator|.
name|getBoolean
argument_list|(
literal|"flattened"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Prepare
operator|.
name|CatalogReader
name|getCatalogReader
parameter_list|()
block|{
return|return
name|catalogReader
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelOptTable
name|getTable
parameter_list|()
block|{
return|return
name|table
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|List
argument_list|<
name|String
argument_list|>
name|getUpdateColumnList
parameter_list|()
block|{
return|return
name|updateColumnList
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|List
argument_list|<
name|RexNode
argument_list|>
name|getSourceExpressionList
parameter_list|()
block|{
return|return
name|sourceExpressionList
return|;
block|}
specifier|public
name|boolean
name|isFlattened
parameter_list|()
block|{
return|return
name|flattened
return|;
block|}
specifier|public
name|Operation
name|getOperation
parameter_list|()
block|{
return|return
name|operation
return|;
block|}
specifier|public
name|boolean
name|isInsert
parameter_list|()
block|{
return|return
name|operation
operator|==
name|Operation
operator|.
name|INSERT
return|;
block|}
specifier|public
name|boolean
name|isUpdate
parameter_list|()
block|{
return|return
name|operation
operator|==
name|Operation
operator|.
name|UPDATE
return|;
block|}
specifier|public
name|boolean
name|isDelete
parameter_list|()
block|{
return|return
name|operation
operator|==
name|Operation
operator|.
name|DELETE
return|;
block|}
specifier|public
name|boolean
name|isMerge
parameter_list|()
block|{
return|return
name|operation
operator|==
name|Operation
operator|.
name|MERGE
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|RelOptUtil
operator|.
name|createDmlRowType
argument_list|(
name|SqlKind
operator|.
name|INSERT
argument_list|,
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|getExpectedInputRowType
parameter_list|(
name|int
name|ordinalInParent
parameter_list|)
block|{
assert|assert
name|ordinalInParent
operator|==
literal|0
assert|;
if|if
condition|(
name|inputRowType
operator|!=
literal|null
condition|)
block|{
return|return
name|inputRowType
return|;
block|}
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|table
operator|.
name|getRowType
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|operation
condition|)
block|{
case|case
name|UPDATE
case|:
assert|assert
name|updateColumnList
operator|!=
literal|null
operator|:
literal|"updateColumnList must not be null for "
operator|+
name|operation
assert|;
name|inputRowType
operator|=
name|typeFactory
operator|.
name|createJoinType
argument_list|(
name|rowType
argument_list|,
name|getCatalogReader
argument_list|()
operator|.
name|createTypeFromProjection
argument_list|(
name|rowType
argument_list|,
name|updateColumnList
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|MERGE
case|:
assert|assert
name|updateColumnList
operator|!=
literal|null
operator|:
literal|"updateColumnList must not be null for "
operator|+
name|operation
assert|;
name|inputRowType
operator|=
name|typeFactory
operator|.
name|createJoinType
argument_list|(
name|typeFactory
operator|.
name|createJoinType
argument_list|(
name|rowType
argument_list|,
name|rowType
argument_list|)
argument_list|,
name|getCatalogReader
argument_list|()
operator|.
name|createTypeFromProjection
argument_list|(
name|rowType
argument_list|,
name|updateColumnList
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
name|inputRowType
operator|=
name|rowType
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|flattened
condition|)
block|{
name|inputRowType
operator|=
name|SqlTypeUtil
operator|.
name|flattenRecordType
argument_list|(
name|typeFactory
argument_list|,
name|inputRowType
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|inputRowType
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
literal|"table"
argument_list|,
name|table
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
operator|.
name|item
argument_list|(
literal|"operation"
argument_list|,
name|RelEnumTypes
operator|.
name|fromEnum
argument_list|(
name|getOperation
argument_list|()
argument_list|)
argument_list|)
operator|.
name|itemIf
argument_list|(
literal|"updateColumnList"
argument_list|,
name|updateColumnList
argument_list|,
name|updateColumnList
operator|!=
literal|null
argument_list|)
operator|.
name|itemIf
argument_list|(
literal|"sourceExpressionList"
argument_list|,
name|sourceExpressionList
argument_list|,
name|sourceExpressionList
operator|!=
literal|null
argument_list|)
operator|.
name|item
argument_list|(
literal|"flattened"
argument_list|,
name|flattened
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
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
comment|// REVIEW jvs 21-Apr-2006:  Just for now...
name|double
name|rowCount
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|this
argument_list|)
decl_stmt|;
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
name|rowCount
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
block|}
end_class

end_unit

