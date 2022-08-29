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
name|SqlOperator
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
name|fun
operator|.
name|SqlQuantifyOperator
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|SqlTypeName
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
comment|/**  * Scalar expression that represents an IN, EXISTS or scalar sub-query.  */
end_comment

begin_class
specifier|public
class|class
name|RexSubQuery
extends|extends
name|RexCall
block|{
specifier|public
specifier|final
name|RelNode
name|rel
decl_stmt|;
specifier|private
name|RexSubQuery
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|SqlOperator
name|op
parameter_list|,
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|operands
parameter_list|,
name|RelNode
name|rel
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|,
name|op
argument_list|,
name|operands
argument_list|)
expr_stmt|;
name|this
operator|.
name|rel
operator|=
name|rel
expr_stmt|;
block|}
comment|/** Creates an IN sub-query. */
specifier|public
specifier|static
name|RexSubQuery
name|in
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|nodes
parameter_list|)
block|{
specifier|final
name|RelDataType
name|type
init|=
name|type
argument_list|(
name|rel
argument_list|,
name|nodes
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexSubQuery
argument_list|(
name|type
argument_list|,
name|SqlStdOperatorTable
operator|.
name|IN
argument_list|,
name|nodes
argument_list|,
name|rel
argument_list|)
return|;
block|}
comment|/** Creates a SOME sub-query.    *    *<p>There is no ALL. For {@code x comparison ALL (sub-query)} use instead    * {@code NOT (x inverse-comparison SOME (sub-query))}.    * If {@code comparison} is {@code>}    * then {@code negated-comparison} is {@code<=}, and so forth.    *    *<p>Also =SOME is rewritten into IN</p> */
specifier|public
specifier|static
name|RexSubQuery
name|some
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|nodes
parameter_list|,
name|SqlQuantifyOperator
name|op
parameter_list|)
block|{
assert|assert
name|op
operator|.
name|kind
operator|==
name|SqlKind
operator|.
name|SOME
assert|;
if|if
condition|(
name|op
operator|==
name|SqlStdOperatorTable
operator|.
name|SOME_EQ
condition|)
block|{
return|return
name|RexSubQuery
operator|.
name|in
argument_list|(
name|rel
argument_list|,
name|nodes
argument_list|)
return|;
block|}
specifier|final
name|RelDataType
name|type
init|=
name|type
argument_list|(
name|rel
argument_list|,
name|nodes
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexSubQuery
argument_list|(
name|type
argument_list|,
name|op
argument_list|,
name|nodes
argument_list|,
name|rel
argument_list|)
return|;
block|}
specifier|static
name|RelDataType
name|type
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|nodes
parameter_list|)
block|{
assert|assert
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
operator|==
name|nodes
operator|.
name|size
argument_list|()
assert|;
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|boolean
name|nullable
init|=
literal|false
decl_stmt|;
for|for
control|(
name|RexNode
name|node
range|:
name|nodes
control|)
block|{
if|if
condition|(
name|node
operator|.
name|getType
argument_list|()
operator|.
name|isNullable
argument_list|()
condition|)
block|{
name|nullable
operator|=
literal|true
expr_stmt|;
block|}
block|}
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
control|)
block|{
if|if
condition|(
name|field
operator|.
name|getType
argument_list|()
operator|.
name|isNullable
argument_list|()
condition|)
block|{
name|nullable
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
argument_list|,
name|nullable
argument_list|)
return|;
block|}
comment|/** Creates an EXISTS sub-query. */
specifier|public
specifier|static
name|RexSubQuery
name|exists
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexSubQuery
argument_list|(
name|type
argument_list|,
name|SqlStdOperatorTable
operator|.
name|EXISTS
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|rel
argument_list|)
return|;
block|}
comment|/** Creates an UNIQUE sub-query. */
specifier|public
specifier|static
name|RexSubQuery
name|unique
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexSubQuery
argument_list|(
name|type
argument_list|,
name|SqlStdOperatorTable
operator|.
name|UNIQUE
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|rel
argument_list|)
return|;
block|}
comment|/** Creates a scalar sub-query. */
specifier|public
specifier|static
name|RexSubQuery
name|scalar
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fieldList
init|=
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldList
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|fieldList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexSubQuery
argument_list|(
name|type
argument_list|,
name|SqlStdOperatorTable
operator|.
name|SCALAR_QUERY
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|rel
argument_list|)
return|;
block|}
comment|/** Creates an ARRAY sub-query. */
specifier|public
specifier|static
name|RexSubQuery
name|array
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|typeFactory
operator|.
name|createArrayType
argument_list|(
name|SqlTypeUtil
operator|.
name|deriveCollectionQueryComponentType
argument_list|(
name|SqlTypeName
operator|.
name|ARRAY
argument_list|,
name|rel
operator|.
name|getRowType
argument_list|()
argument_list|)
argument_list|,
operator|-
literal|1L
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexSubQuery
argument_list|(
name|type
argument_list|,
name|SqlStdOperatorTable
operator|.
name|ARRAY_QUERY
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|rel
argument_list|)
return|;
block|}
comment|/** Creates a MULTISET sub-query. */
specifier|public
specifier|static
name|RexSubQuery
name|multiset
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|typeFactory
operator|.
name|createMultisetType
argument_list|(
name|SqlTypeUtil
operator|.
name|deriveCollectionQueryComponentType
argument_list|(
name|SqlTypeName
operator|.
name|MULTISET
argument_list|,
name|rel
operator|.
name|getRowType
argument_list|()
argument_list|)
argument_list|,
operator|-
literal|1L
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexSubQuery
argument_list|(
name|type
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MULTISET_QUERY
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|rel
argument_list|)
return|;
block|}
comment|/** Creates a MAP sub-query. */
specifier|public
specifier|static
name|RexSubQuery
name|map
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|rel
operator|.
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
name|rel
operator|.
name|getRowType
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|rowType
operator|.
name|getFieldCount
argument_list|()
operator|==
literal|2
argument_list|,
literal|"MAP requires exactly two fields, got %s; row type %s"
argument_list|,
name|rowType
operator|.
name|getFieldCount
argument_list|()
argument_list|,
name|rowType
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fieldList
init|=
name|rowType
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|typeFactory
operator|.
name|createMapType
argument_list|(
name|fieldList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|,
name|fieldList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexSubQuery
argument_list|(
name|type
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MAP_QUERY
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|rel
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
name|visitSubQuery
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
name|visitSubQuery
argument_list|(
name|this
argument_list|,
name|arg
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|computeDigest
parameter_list|(
name|boolean
name|withType
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|op
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
for|for
control|(
name|RexNode
name|operand
range|:
name|operands
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|operand
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"{\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|rel
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"})"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|RexSubQuery
name|clone
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
parameter_list|)
block|{
return|return
operator|new
name|RexSubQuery
argument_list|(
name|type
argument_list|,
name|getOperator
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|operands
argument_list|)
argument_list|,
name|rel
argument_list|)
return|;
block|}
specifier|public
name|RexSubQuery
name|clone
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
operator|new
name|RexSubQuery
argument_list|(
name|type
argument_list|,
name|getOperator
argument_list|()
argument_list|,
name|operands
argument_list|,
name|rel
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
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|RexSubQuery
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RexSubQuery
name|sq
init|=
operator|(
name|RexSubQuery
operator|)
name|obj
decl_stmt|;
return|return
name|op
operator|.
name|equals
argument_list|(
name|sq
operator|.
name|op
argument_list|)
operator|&&
name|operands
operator|.
name|equals
argument_list|(
name|sq
operator|.
name|operands
argument_list|)
operator|&&
name|rel
operator|.
name|deepEquals
argument_list|(
name|sq
operator|.
name|rel
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
if|if
condition|(
name|hash
operator|==
literal|0
condition|)
block|{
name|hash
operator|=
name|Objects
operator|.
name|hash
argument_list|(
name|op
argument_list|,
name|operands
argument_list|,
name|rel
operator|.
name|deepHashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
block|}
end_class

end_unit

