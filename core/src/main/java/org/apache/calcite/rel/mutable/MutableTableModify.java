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
name|mutable
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
name|core
operator|.
name|TableModify
operator|.
name|Operation
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
name|rex
operator|.
name|RexNode
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/** Mutable equivalent of {@link org.apache.calcite.rel.core.TableModify}. */
end_comment

begin_class
specifier|public
class|class
name|MutableTableModify
extends|extends
name|MutableSingleRel
block|{
specifier|public
specifier|final
name|Prepare
operator|.
name|CatalogReader
name|catalogReader
decl_stmt|;
specifier|public
specifier|final
name|RelOptTable
name|table
decl_stmt|;
specifier|public
specifier|final
name|Operation
name|operation
decl_stmt|;
specifier|public
specifier|final
annotation|@
name|Nullable
name|List
argument_list|<
name|String
argument_list|>
name|updateColumnList
decl_stmt|;
specifier|public
specifier|final
annotation|@
name|Nullable
name|List
argument_list|<
name|RexNode
argument_list|>
name|sourceExpressionList
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|flattened
decl_stmt|;
specifier|private
name|MutableTableModify
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|MutableRel
name|input
parameter_list|,
name|RelOptTable
name|table
parameter_list|,
name|Prepare
operator|.
name|CatalogReader
name|catalogReader
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
name|MutableRelType
operator|.
name|TABLE_MODIFY
argument_list|,
name|rowType
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
name|this
operator|.
name|flattened
operator|=
name|flattened
expr_stmt|;
block|}
comment|/**    * Creates a MutableTableModify.    *    * @param rowType               Row type    * @param input                 Input relational expression    * @param table                 Target table to modify    * @param catalogReader         Accessor to the table metadata    * @param operation             Modify operation (INSERT, UPDATE, DELETE)    * @param updateColumnList      List of column identifiers to be updated    *                              (e.g. ident1, ident2); null if not UPDATE    * @param sourceExpressionList  List of value expressions to be set    *                              (e.g. exp1, exp2); null if not UPDATE    * @param flattened             Whether set flattens the input row type    */
specifier|public
specifier|static
name|MutableTableModify
name|of
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|MutableRel
name|input
parameter_list|,
name|RelOptTable
name|table
parameter_list|,
name|Prepare
operator|.
name|CatalogReader
name|catalogReader
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
return|return
operator|new
name|MutableTableModify
argument_list|(
name|rowType
argument_list|,
name|input
argument_list|,
name|table
argument_list|,
name|catalogReader
argument_list|,
name|operation
argument_list|,
name|updateColumnList
argument_list|,
name|sourceExpressionList
argument_list|,
name|flattened
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|MutableTableModify
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
name|MutableTableModify
operator|)
name|obj
operator|)
operator|.
name|table
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
operator|&&
name|operation
operator|==
operator|(
operator|(
name|MutableTableModify
operator|)
name|obj
operator|)
operator|.
name|operation
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|updateColumnList
argument_list|,
operator|(
operator|(
name|MutableTableModify
operator|)
name|obj
operator|)
operator|.
name|updateColumnList
argument_list|)
operator|&&
name|MutableRel
operator|.
name|PAIRWISE_STRING_EQUIVALENCE
operator|.
name|equivalent
argument_list|(
name|sourceExpressionList
argument_list|,
operator|(
operator|(
name|MutableTableModify
operator|)
name|obj
operator|)
operator|.
name|sourceExpressionList
argument_list|)
operator|&&
name|flattened
operator|==
operator|(
operator|(
name|MutableTableModify
operator|)
name|obj
operator|)
operator|.
name|flattened
operator|&&
name|input
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MutableTableModify
operator|)
name|obj
operator|)
operator|.
name|input
argument_list|)
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
name|Objects
operator|.
name|hash
argument_list|(
name|input
argument_list|,
name|table
operator|.
name|getQualifiedName
argument_list|()
argument_list|,
name|operation
argument_list|,
name|updateColumnList
argument_list|,
name|MutableRel
operator|.
name|PAIRWISE_STRING_EQUIVALENCE
operator|.
name|hash
argument_list|(
name|sourceExpressionList
argument_list|)
argument_list|,
name|flattened
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StringBuilder
name|digest
parameter_list|(
name|StringBuilder
name|buf
parameter_list|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"TableModify(table: "
argument_list|)
operator|.
name|append
argument_list|(
name|table
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", operation: "
argument_list|)
operator|.
name|append
argument_list|(
name|operation
argument_list|)
expr_stmt|;
if|if
condition|(
name|updateColumnList
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|", updateColumnList: "
argument_list|)
operator|.
name|append
argument_list|(
name|updateColumnList
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sourceExpressionList
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|", sourceExpressionList: "
argument_list|)
operator|.
name|append
argument_list|(
name|sourceExpressionList
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|append
argument_list|(
literal|", flattened: "
argument_list|)
operator|.
name|append
argument_list|(
name|flattened
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|MutableRel
name|clone
parameter_list|()
block|{
return|return
name|MutableTableModify
operator|.
name|of
argument_list|(
name|rowType
argument_list|,
name|input
operator|.
name|clone
argument_list|()
argument_list|,
name|table
argument_list|,
name|catalogReader
argument_list|,
name|operation
argument_list|,
name|updateColumnList
argument_list|,
name|sourceExpressionList
argument_list|,
name|flattened
argument_list|)
return|;
block|}
block|}
end_class

end_unit

