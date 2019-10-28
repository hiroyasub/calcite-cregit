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
name|hep
operator|.
name|HepRelVertex
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
name|volcano
operator|.
name|RelSubset
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
name|Sample
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
name|RexTableInputRef
operator|.
name|RelTableRef
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
name|Util
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
name|HashMultimap
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
name|ImmutableSet
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
name|Multimap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
comment|/**  * Default implementation of {@link RelMetadataQuery#getTableReferences} for the  * standard logical algebra.  *  *<p>The goal of this provider is to return all tables used by a given  * expression identified uniquely by a {@link RelTableRef}.  *  *<p>Each unique identifier {@link RelTableRef} of a table will equal to the  * identifier obtained running {@link RelMdExpressionLineage} over the same plan  * node for an expression that refers to the same table.  *  *<p>If tables cannot be obtained, we return null.  */
end_comment

begin_class
specifier|public
class|class
name|RelMdTableReferences
implements|implements
name|MetadataHandler
argument_list|<
name|BuiltInMetadata
operator|.
name|TableReferences
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
name|BuiltInMethod
operator|.
name|TABLE_REFERENCES
operator|.
name|method
argument_list|,
operator|new
name|RelMdTableReferences
argument_list|()
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|RelMdTableReferences
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|MetadataDef
argument_list|<
name|BuiltInMetadata
operator|.
name|TableReferences
argument_list|>
name|getDef
parameter_list|()
block|{
return|return
name|BuiltInMetadata
operator|.
name|TableReferences
operator|.
name|DEF
return|;
block|}
comment|// Catch-all rule when none of the others apply.
specifier|public
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|getTableReferences
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|getTableReferences
parameter_list|(
name|HepRelVertex
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getTableReferences
argument_list|(
name|rel
operator|.
name|getCurrentRel
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|getTableReferences
parameter_list|(
name|RelSubset
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getTableReferences
argument_list|(
name|Util
operator|.
name|first
argument_list|(
name|rel
operator|.
name|getBest
argument_list|()
argument_list|,
name|rel
operator|.
name|getOriginal
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * TableScan table reference.    */
specifier|public
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|getTableReferences
parameter_list|(
name|TableScan
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|RelTableRef
operator|.
name|of
argument_list|(
name|rel
operator|.
name|getTable
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Table references from Aggregate.    */
specifier|public
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|getTableReferences
parameter_list|(
name|Aggregate
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getTableReferences
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Table references from Join.    */
specifier|public
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|getTableReferences
parameter_list|(
name|Join
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
specifier|final
name|RelNode
name|leftInput
init|=
name|rel
operator|.
name|getLeft
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|rightInput
init|=
name|rel
operator|.
name|getRight
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Gather table references, left input references remain unchanged
specifier|final
name|Multimap
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|RelTableRef
argument_list|>
name|leftQualifiedNamesToRefs
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|leftTableRefs
init|=
name|mq
operator|.
name|getTableReferences
argument_list|(
name|leftInput
argument_list|)
decl_stmt|;
if|if
condition|(
name|leftTableRefs
operator|==
literal|null
condition|)
block|{
comment|// We could not infer the table refs from left input
return|return
literal|null
return|;
block|}
for|for
control|(
name|RelTableRef
name|leftRef
range|:
name|leftTableRefs
control|)
block|{
assert|assert
operator|!
name|result
operator|.
name|contains
argument_list|(
name|leftRef
argument_list|)
assert|;
name|result
operator|.
name|add
argument_list|(
name|leftRef
argument_list|)
expr_stmt|;
name|leftQualifiedNamesToRefs
operator|.
name|put
argument_list|(
name|leftRef
operator|.
name|getQualifiedName
argument_list|()
argument_list|,
name|leftRef
argument_list|)
expr_stmt|;
block|}
comment|// Gather table references, right input references might need to be
comment|// updated if there are table names clashes with left input
specifier|final
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|rightTableRefs
init|=
name|mq
operator|.
name|getTableReferences
argument_list|(
name|rightInput
argument_list|)
decl_stmt|;
if|if
condition|(
name|rightTableRefs
operator|==
literal|null
condition|)
block|{
comment|// We could not infer the table refs from right input
return|return
literal|null
return|;
block|}
for|for
control|(
name|RelTableRef
name|rightRef
range|:
name|rightTableRefs
control|)
block|{
name|int
name|shift
init|=
literal|0
decl_stmt|;
name|Collection
argument_list|<
name|RelTableRef
argument_list|>
name|lRefs
init|=
name|leftQualifiedNamesToRefs
operator|.
name|get
argument_list|(
name|rightRef
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|lRefs
operator|!=
literal|null
condition|)
block|{
name|shift
operator|=
name|lRefs
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|RelTableRef
name|shiftTableRef
init|=
name|RelTableRef
operator|.
name|of
argument_list|(
name|rightRef
operator|.
name|getTable
argument_list|()
argument_list|,
name|shift
operator|+
name|rightRef
operator|.
name|getEntityNumber
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
operator|!
name|result
operator|.
name|contains
argument_list|(
name|shiftTableRef
argument_list|)
assert|;
name|result
operator|.
name|add
argument_list|(
name|shiftTableRef
argument_list|)
expr_stmt|;
block|}
comment|// Return result
return|return
name|result
return|;
block|}
comment|/**    * Table references from Union, Intersect, Minus.    *    *<p>For Union operator, we might be able to extract multiple table    * references.    */
specifier|public
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|getTableReferences
parameter_list|(
name|SetOp
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Infer column origin expressions for given references
specifier|final
name|Multimap
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|RelTableRef
argument_list|>
name|qualifiedNamesToRefs
init|=
name|HashMultimap
operator|.
name|create
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
specifier|final
name|Map
argument_list|<
name|RelTableRef
argument_list|,
name|RelTableRef
argument_list|>
name|currentTablesMapping
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|inputTableRefs
init|=
name|mq
operator|.
name|getTableReferences
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|inputTableRefs
operator|==
literal|null
condition|)
block|{
comment|// We could not infer the table refs from input
return|return
literal|null
return|;
block|}
for|for
control|(
name|RelTableRef
name|tableRef
range|:
name|inputTableRefs
control|)
block|{
name|int
name|shift
init|=
literal|0
decl_stmt|;
name|Collection
argument_list|<
name|RelTableRef
argument_list|>
name|lRefs
init|=
name|qualifiedNamesToRefs
operator|.
name|get
argument_list|(
name|tableRef
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|lRefs
operator|!=
literal|null
condition|)
block|{
name|shift
operator|=
name|lRefs
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|RelTableRef
name|shiftTableRef
init|=
name|RelTableRef
operator|.
name|of
argument_list|(
name|tableRef
operator|.
name|getTable
argument_list|()
argument_list|,
name|shift
operator|+
name|tableRef
operator|.
name|getEntityNumber
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
operator|!
name|result
operator|.
name|contains
argument_list|(
name|shiftTableRef
argument_list|)
assert|;
name|result
operator|.
name|add
argument_list|(
name|shiftTableRef
argument_list|)
expr_stmt|;
name|currentTablesMapping
operator|.
name|put
argument_list|(
name|tableRef
argument_list|,
name|shiftTableRef
argument_list|)
expr_stmt|;
block|}
comment|// Add to existing qualified names
for|for
control|(
name|RelTableRef
name|newRef
range|:
name|currentTablesMapping
operator|.
name|values
argument_list|()
control|)
block|{
name|qualifiedNamesToRefs
operator|.
name|put
argument_list|(
name|newRef
operator|.
name|getQualifiedName
argument_list|()
argument_list|,
name|newRef
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Return result
return|return
name|result
return|;
block|}
comment|/**    * Table references from Project.    */
specifier|public
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|getTableReferences
parameter_list|(
name|Project
name|rel
parameter_list|,
specifier|final
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getTableReferences
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Table references from Filter.    */
specifier|public
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|getTableReferences
parameter_list|(
name|Filter
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getTableReferences
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Table references from Sort.    */
specifier|public
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|getTableReferences
parameter_list|(
name|Sort
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getTableReferences
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Table references from TableModify.    */
specifier|public
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|getTableReferences
parameter_list|(
name|TableModify
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getTableReferences
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Table references from Exchange.    */
specifier|public
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|getTableReferences
parameter_list|(
name|Exchange
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getTableReferences
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Table references from Window.    */
specifier|public
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|getTableReferences
parameter_list|(
name|Window
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getTableReferences
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Table references from Sample.    */
specifier|public
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|getTableReferences
parameter_list|(
name|Sample
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getTableReferences
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelMdTableReferences.java
end_comment

end_unit

