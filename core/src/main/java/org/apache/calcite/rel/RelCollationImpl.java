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
name|RelTrait
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
name|RelTraitDef
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
name|base
operator|.
name|Function
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
comment|/**  * Simple implementation of {@link RelCollation}.  */
end_comment

begin_class
specifier|public
class|class
name|RelCollationImpl
implements|implements
name|RelCollation
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**    * A collation indicating that a relation is not sorted. Ordering by no    * columns.    */
specifier|public
specifier|static
specifier|final
name|RelCollation
name|EMPTY
init|=
name|RelCollationTraitDef
operator|.
name|INSTANCE
operator|.
name|canonize
argument_list|(
operator|new
name|RelCollationImpl
argument_list|(
name|ImmutableList
operator|.
expr|<
name|RelFieldCollation
operator|>
name|of
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * A collation that cannot be replicated by applying a sort. The only    * implementation choice is to apply operations that preserve order.    */
specifier|public
specifier|static
specifier|final
name|RelCollation
name|PRESERVE
init|=
name|RelCollationTraitDef
operator|.
name|INSTANCE
operator|.
name|canonize
argument_list|(
operator|new
name|RelCollationImpl
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|RelFieldCollation
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
block|{
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"PRESERVE"
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|RelFieldCollation
argument_list|>
name|fieldCollations
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|RelCollationImpl
parameter_list|(
name|ImmutableList
argument_list|<
name|RelFieldCollation
argument_list|>
name|fieldCollations
parameter_list|)
block|{
name|this
operator|.
name|fieldCollations
operator|=
name|fieldCollations
expr_stmt|;
block|}
specifier|public
specifier|static
name|RelCollation
name|of
parameter_list|(
name|RelFieldCollation
modifier|...
name|fieldCollations
parameter_list|)
block|{
return|return
operator|new
name|RelCollationImpl
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|fieldCollations
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RelCollation
name|of
parameter_list|(
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|fieldCollations
parameter_list|)
block|{
return|return
operator|new
name|RelCollationImpl
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|fieldCollations
argument_list|)
argument_list|)
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelTraitDef
name|getTraitDef
parameter_list|()
block|{
return|return
name|RelCollationTraitDef
operator|.
name|INSTANCE
return|;
block|}
specifier|public
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|getFieldCollations
parameter_list|()
block|{
return|return
name|fieldCollations
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|fieldCollations
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
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
name|obj
operator|instanceof
name|RelCollationImpl
condition|)
block|{
name|RelCollationImpl
name|that
init|=
operator|(
name|RelCollationImpl
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|fieldCollations
operator|.
name|equals
argument_list|(
name|that
operator|.
name|fieldCollations
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|register
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
block|}
specifier|public
name|boolean
name|subsumes
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
block|{
return|return
name|this
operator|==
name|trait
operator|||
name|trait
operator|instanceof
name|RelCollationImpl
operator|&&
name|Util
operator|.
name|startsWith
argument_list|(
name|fieldCollations
argument_list|,
operator|(
operator|(
name|RelCollationImpl
operator|)
name|trait
operator|)
operator|.
name|fieldCollations
argument_list|)
return|;
block|}
comment|/** Returns a string representation of this collation, suitably terse given    * that it will appear in plan traces. Examples:    * "[]", "[2]", "[0 DESC, 1]", "[0 DESC, 1 ASC NULLS LAST]". */
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|Iterator
argument_list|<
name|RelFieldCollation
argument_list|>
name|it
init|=
name|fieldCollations
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|"[]"
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|RelFieldCollation
name|e
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|e
operator|.
name|getFieldIndex
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|.
name|direction
operator|!=
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|ASCENDING
operator|||
name|e
operator|.
name|nullDirection
operator|!=
name|RelFieldCollation
operator|.
name|NullDirection
operator|.
name|UNSPECIFIED
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|e
operator|.
name|shortString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Creates a list containing one collation containing one field.    */
specifier|public
specifier|static
name|List
argument_list|<
name|RelCollation
argument_list|>
name|createSingleton
parameter_list|(
name|int
name|fieldIndex
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|of
argument_list|(
operator|new
name|RelFieldCollation
argument_list|(
name|fieldIndex
argument_list|,
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|ASCENDING
argument_list|,
name|RelFieldCollation
operator|.
name|NullDirection
operator|.
name|UNSPECIFIED
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Checks that a collection of collations is valid.    *    * @param rowType       Row type of the relational expression    * @param collationList List of collations    * @param fail          Whether to fail if invalid    * @return Whether valid    */
specifier|public
specifier|static
name|boolean
name|isValid
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList
parameter_list|,
name|boolean
name|fail
parameter_list|)
block|{
specifier|final
name|int
name|fieldCount
init|=
name|rowType
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
for|for
control|(
name|RelCollation
name|collation
range|:
name|collationList
control|)
block|{
for|for
control|(
name|RelFieldCollation
name|fieldCollation
range|:
name|collation
operator|.
name|getFieldCollations
argument_list|()
control|)
block|{
specifier|final
name|int
name|index
init|=
name|fieldCollation
operator|.
name|getFieldIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
operator|||
name|index
operator|>=
name|fieldCount
condition|)
block|{
assert|assert
operator|!
name|fail
assert|;
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|public
specifier|static
name|boolean
name|equal
parameter_list|(
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList1
parameter_list|,
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList2
parameter_list|)
block|{
return|return
name|collationList1
operator|.
name|equals
argument_list|(
name|collationList2
argument_list|)
return|;
block|}
comment|/** Returns the indexes of the field collations in a given collation. */
specifier|public
specifier|static
name|List
argument_list|<
name|Integer
argument_list|>
name|ordinals
parameter_list|(
name|RelCollation
name|collation
parameter_list|)
block|{
return|return
name|Lists
operator|.
name|transform
argument_list|(
name|collation
operator|.
name|getFieldCollations
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|RelFieldCollation
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|apply
parameter_list|(
name|RelFieldCollation
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getFieldIndex
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelCollationImpl.java
end_comment

end_unit

