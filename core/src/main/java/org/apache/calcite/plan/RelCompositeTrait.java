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
name|Ordering
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
comment|/**  * A trait that consists of a list of traits, all of the same type.  *  *<p>It exists so that multiple traits of the same type  * ({@link org.apache.calcite.plan.RelTraitDef}) can be stored in the same  * {@link org.apache.calcite.plan.RelTraitSet}.  *  * @param<T> Member trait  */
end_comment

begin_class
class|class
name|RelCompositeTrait
parameter_list|<
name|T
extends|extends
name|RelMultipleTrait
parameter_list|>
implements|implements
name|RelTrait
block|{
specifier|private
specifier|final
name|RelTraitDef
name|traitDef
decl_stmt|;
specifier|private
specifier|final
name|T
index|[]
name|traits
decl_stmt|;
comment|/** Creates a RelCompositeTrait. */
comment|// Must remain private. Does not copy the array.
specifier|private
name|RelCompositeTrait
parameter_list|(
name|RelTraitDef
name|traitDef
parameter_list|,
name|T
index|[]
name|traits
parameter_list|)
block|{
name|this
operator|.
name|traitDef
operator|=
name|traitDef
expr_stmt|;
name|this
operator|.
name|traits
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|traits
argument_list|)
expr_stmt|;
comment|//noinspection unchecked
assert|assert
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|isStrictlyOrdered
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|(
name|Comparable
index|[]
operator|)
name|traits
argument_list|)
argument_list|)
operator|:
name|Arrays
operator|.
name|toString
argument_list|(
name|traits
argument_list|)
assert|;
for|for
control|(
name|T
name|trait
range|:
name|traits
control|)
block|{
assert|assert
name|trait
operator|.
name|getTraitDef
argument_list|()
operator|==
name|this
operator|.
name|traitDef
assert|;
block|}
block|}
comment|/** Creates a RelCompositeTrait. The constituent traits are canonized. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|static
parameter_list|<
name|T
extends|extends
name|RelMultipleTrait
parameter_list|>
name|RelTrait
name|of
parameter_list|(
name|RelTraitDef
name|def
parameter_list|,
name|List
argument_list|<
name|T
argument_list|>
name|traitList
parameter_list|)
block|{
specifier|final
name|RelCompositeTrait
argument_list|<
name|T
argument_list|>
name|compositeTrait
decl_stmt|;
if|if
condition|(
name|traitList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|def
operator|.
name|getDefault
argument_list|()
return|;
block|}
if|else if
condition|(
name|traitList
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|def
operator|.
name|canonize
argument_list|(
name|traitList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|RelMultipleTrait
index|[]
name|traits
init|=
name|traitList
operator|.
name|toArray
argument_list|(
operator|new
name|RelMultipleTrait
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|traits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|traits
index|[
name|i
index|]
operator|=
operator|(
name|T
operator|)
name|def
operator|.
name|canonize
argument_list|(
name|traits
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|compositeTrait
operator|=
operator|new
name|RelCompositeTrait
argument_list|<>
argument_list|(
name|def
argument_list|,
operator|(
name|T
index|[]
operator|)
name|traits
argument_list|)
expr_stmt|;
block|}
return|return
name|def
operator|.
name|canonize
argument_list|(
name|compositeTrait
argument_list|)
return|;
block|}
specifier|public
name|RelTraitDef
name|getTraitDef
parameter_list|()
block|{
return|return
name|traitDef
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
name|Arrays
operator|.
name|hashCode
argument_list|(
name|traits
argument_list|)
return|;
block|}
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
name|RelCompositeTrait
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|traits
argument_list|,
operator|(
operator|(
name|RelCompositeTrait
operator|)
name|obj
operator|)
operator|.
name|traits
argument_list|)
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
name|Arrays
operator|.
name|toString
argument_list|(
name|traits
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|satisfies
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
block|{
for|for
control|(
name|T
name|t
range|:
name|traits
control|)
block|{
if|if
condition|(
name|t
operator|.
name|satisfies
argument_list|(
name|trait
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
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
comment|/** Returns an immutable list of the traits in this composite trait. */
specifier|public
name|List
argument_list|<
name|T
argument_list|>
name|traitList
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|traits
argument_list|)
return|;
block|}
comment|/** Returns the {@code i}th trait. */
specifier|public
name|T
name|trait
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|traits
index|[
name|i
index|]
return|;
block|}
comment|/** Returns the number of traits. */
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|traits
operator|.
name|length
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelCompositeTrait.java
end_comment

end_unit

