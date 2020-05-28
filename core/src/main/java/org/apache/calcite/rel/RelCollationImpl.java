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
name|RelMultipleTrait
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
name|runtime
operator|.
name|Utilities
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|UnmodifiableIterator
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
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
specifier|final
name|RelCollation
name|EMPTY
init|=
name|RelCollations
operator|.
name|EMPTY
decl_stmt|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
specifier|final
name|RelCollation
name|PRESERVE
init|=
name|RelCollations
operator|.
name|PRESERVE
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
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|Util
operator|.
name|isDistinct
argument_list|(
name|RelCollations
operator|.
name|ordinals
argument_list|(
name|fieldCollations
argument_list|)
argument_list|)
argument_list|,
literal|"fields must be distinct"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
name|RelCollations
operator|.
name|of
argument_list|(
name|fieldCollations
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
name|RelCollations
operator|.
name|of
argument_list|(
name|fieldCollations
argument_list|)
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|boolean
name|isTop
parameter_list|()
block|{
return|return
name|fieldCollations
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|RelMultipleTrait
name|o
parameter_list|)
block|{
specifier|final
name|RelCollationImpl
name|that
init|=
operator|(
name|RelCollationImpl
operator|)
name|o
decl_stmt|;
specifier|final
name|UnmodifiableIterator
argument_list|<
name|RelFieldCollation
argument_list|>
name|iterator
init|=
name|that
operator|.
name|fieldCollations
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|RelFieldCollation
name|f
range|:
name|fieldCollations
control|)
block|{
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|1
return|;
block|}
specifier|final
name|RelFieldCollation
name|f2
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|c
init|=
name|Utilities
operator|.
name|compare
argument_list|(
name|f
operator|.
name|getFieldIndex
argument_list|()
argument_list|,
name|f2
operator|.
name|getFieldIndex
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
block|}
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
condition|?
operator|-
literal|1
else|:
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|register
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
block|}
comment|/**    * Applies mapping to a given collation.    *    * If mapping destroys the collation prefix, this method returns an empty collation.    * Examples of applying mappings to collation [0, 1]:    *<ul>    *<li>mapping(0, 1) =&gt; [0, 1]</li>    *<li>mapping(1, 0) =&gt; [1, 0]</li>    *<li>mapping(0) =&gt; [0]</li>    *<li>mapping(1) =&gt; []</li>    *<li>mapping(2, 0) =&gt; [1]</li>    *<li>mapping(2, 1, 0) =&gt; [2, 1]</li>    *<li>mapping(2, 1) =&gt; []</li>    *</ul>    *    * @param mapping   Mapping    * @return Collation with applied mapping.    */
annotation|@
name|Override
specifier|public
name|RelCollationImpl
name|apply
parameter_list|(
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|mapping
parameter_list|)
block|{
return|return
operator|(
name|RelCollationImpl
operator|)
name|RexUtil
operator|.
name|apply
argument_list|(
name|mapping
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|satisfies
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
annotation|@
name|Override
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
name|e
operator|.
name|direction
operator|.
name|defaultNullDirection
argument_list|()
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
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
name|RelCollations
operator|.
name|createSingleton
argument_list|(
name|fieldIndex
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
return|return
name|RelCollations
operator|.
name|isValid
argument_list|(
name|rowType
argument_list|,
name|collationList
argument_list|,
name|fail
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
name|RelCollations
operator|.
name|equal
argument_list|(
name|collationList1
argument_list|,
name|collationList2
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
name|RelCollations
operator|.
name|ordinals
argument_list|(
name|collation
argument_list|)
return|;
block|}
block|}
end_class

end_unit

