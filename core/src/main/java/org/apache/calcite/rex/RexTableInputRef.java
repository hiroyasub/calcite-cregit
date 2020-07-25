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
name|rex
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
name|sql
operator|.
name|SqlKind
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
comment|/**  * Variable which references a column of a table occurrence in a relational plan.  *  *<p>This object is used by  * {@link org.apache.calcite.rel.metadata.BuiltInMetadata.ExpressionLineage}  * and {@link org.apache.calcite.rel.metadata.BuiltInMetadata.AllPredicates}.  *  *<p>Given a relational expression, its purpose is to be able to reference uniquely  * the provenance of a given expression. For that, it uses a unique table reference  * (contained in a {@link RelTableRef}) and an column index within the table.  *  *<p>For example, {@code A.#0.$3 + 2} column {@code $3} in the {@code 0}  * occurrence of table {@code A} in the plan.  *  *<p>Note that this kind of {@link RexNode} is an auxiliary data structure with  * a very specific purpose and should not be used in relational expressions.  */
end_comment

begin_class
specifier|public
class|class
name|RexTableInputRef
extends|extends
name|RexInputRef
block|{
specifier|private
specifier|final
name|RelTableRef
name|tableRef
decl_stmt|;
specifier|private
name|RexTableInputRef
parameter_list|(
name|RelTableRef
name|tableRef
parameter_list|,
name|int
name|index
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|tableRef
operator|=
name|tableRef
expr_stmt|;
name|this
operator|.
name|digest
operator|=
name|tableRef
operator|.
name|toString
argument_list|()
operator|+
literal|".$"
operator|+
name|index
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|this
operator|==
name|obj
operator|||
name|obj
operator|instanceof
name|RexTableInputRef
operator|&&
name|tableRef
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|RexTableInputRef
operator|)
name|obj
operator|)
operator|.
name|tableRef
argument_list|)
operator|&&
name|index
operator|==
operator|(
operator|(
name|RexTableInputRef
operator|)
name|obj
operator|)
operator|.
name|index
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|digest
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|public
name|RelTableRef
name|getTableRef
parameter_list|()
block|{
return|return
name|tableRef
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getQualifiedName
parameter_list|()
block|{
return|return
name|tableRef
operator|.
name|getQualifiedName
argument_list|()
return|;
block|}
specifier|public
name|int
name|getIdentifier
parameter_list|()
block|{
return|return
name|tableRef
operator|.
name|getEntityNumber
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|RexTableInputRef
name|of
parameter_list|(
name|RelTableRef
name|tableRef
parameter_list|,
name|int
name|index
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
return|return
operator|new
name|RexTableInputRef
argument_list|(
name|tableRef
argument_list|,
name|index
argument_list|,
name|type
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RexTableInputRef
name|of
parameter_list|(
name|RelTableRef
name|tableRef
parameter_list|,
name|RexInputRef
name|ref
parameter_list|)
block|{
return|return
operator|new
name|RexTableInputRef
argument_list|(
name|tableRef
argument_list|,
name|ref
operator|.
name|getIndex
argument_list|()
argument_list|,
name|ref
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|RexVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visitTableInputRef
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|R
parameter_list|,
name|P
parameter_list|>
name|R
name|accept
parameter_list|(
name|RexBiVisitor
argument_list|<
name|R
argument_list|,
name|P
argument_list|>
name|visitor
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visitTableInputRef
argument_list|(
name|this
argument_list|,
name|arg
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|SqlKind
operator|.
name|TABLE_INPUT_REF
return|;
block|}
comment|/** Identifies uniquely a table by its qualified name and its entity number    * (occurrence). */
specifier|public
specifier|static
class|class
name|RelTableRef
implements|implements
name|Comparable
argument_list|<
name|RelTableRef
argument_list|>
block|{
specifier|private
specifier|final
name|RelOptTable
name|table
decl_stmt|;
specifier|private
specifier|final
name|int
name|entityNumber
decl_stmt|;
specifier|private
specifier|final
name|String
name|digest
decl_stmt|;
specifier|private
name|RelTableRef
parameter_list|(
name|RelOptTable
name|table
parameter_list|,
name|int
name|entityNumber
parameter_list|)
block|{
name|this
operator|.
name|table
operator|=
name|table
expr_stmt|;
name|this
operator|.
name|entityNumber
operator|=
name|entityNumber
expr_stmt|;
name|this
operator|.
name|digest
operator|=
name|table
operator|.
name|getQualifiedName
argument_list|()
operator|+
literal|".#"
operator|+
name|entityNumber
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|this
operator|==
name|obj
operator|||
name|obj
operator|instanceof
name|RelTableRef
operator|&&
name|table
operator|.
name|getQualifiedName
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|RelTableRef
operator|)
name|obj
operator|)
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
operator|&&
name|entityNumber
operator|==
operator|(
operator|(
name|RelTableRef
operator|)
name|obj
operator|)
operator|.
name|entityNumber
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|digest
operator|.
name|hashCode
argument_list|()
return|;
block|}
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
name|List
argument_list|<
name|String
argument_list|>
name|getQualifiedName
parameter_list|()
block|{
return|return
name|table
operator|.
name|getQualifiedName
argument_list|()
return|;
block|}
specifier|public
name|int
name|getEntityNumber
parameter_list|()
block|{
return|return
name|entityNumber
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|digest
return|;
block|}
specifier|public
specifier|static
name|RelTableRef
name|of
parameter_list|(
name|RelOptTable
name|table
parameter_list|,
name|int
name|entityNumber
parameter_list|)
block|{
return|return
operator|new
name|RelTableRef
argument_list|(
name|table
argument_list|,
name|entityNumber
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|RelTableRef
name|o
parameter_list|)
block|{
return|return
name|digest
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|digest
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

