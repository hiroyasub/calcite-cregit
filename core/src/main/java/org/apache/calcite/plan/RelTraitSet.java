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
name|rel
operator|.
name|RelCollation
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
name|RelCollationTraitDef
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
name|RelDistribution
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
name|RelDistributionTraitDef
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
name|AbstractList
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
name|HashMap
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
name|function
operator|.
name|Supplier
import|;
end_import

begin_comment
comment|/**  * RelTraitSet represents an ordered set of {@link RelTrait}s.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|RelTraitSet
extends|extends
name|AbstractList
argument_list|<
name|RelTrait
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|RelTrait
index|[]
name|EMPTY_TRAITS
init|=
operator|new
name|RelTrait
index|[
literal|0
index|]
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Cache
name|cache
decl_stmt|;
specifier|private
specifier|final
name|RelTrait
index|[]
name|traits
decl_stmt|;
specifier|private
name|String
name|string
decl_stmt|;
comment|/** Caches the hash code for the traits. */
specifier|private
name|int
name|hash
decl_stmt|;
comment|// Default to 0
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Constructs a RelTraitSet with the given set of RelTraits.    *    * @param cache  Trait set cache (and indirectly cluster) that this set    *               belongs to    * @param traits Traits    */
specifier|private
name|RelTraitSet
parameter_list|(
name|Cache
name|cache
parameter_list|,
name|RelTrait
index|[]
name|traits
parameter_list|)
block|{
comment|// NOTE: We do not copy the array. It is important that the array is not
comment|//   shared. However, since this constructor is private, we assume that
comment|//   the caller has made a copy.
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
name|this
operator|.
name|traits
operator|=
name|traits
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Creates an empty trait set.    *    *<p>It has a new cache, which will be shared by any trait set created from    * it. Thus each empty trait set is the start of a new ancestral line.    */
specifier|public
specifier|static
name|RelTraitSet
name|createEmpty
parameter_list|()
block|{
return|return
operator|new
name|RelTraitSet
argument_list|(
operator|new
name|Cache
argument_list|()
argument_list|,
name|EMPTY_TRAITS
argument_list|)
return|;
block|}
comment|/**    * Retrieves a RelTrait from the set.    *    * @param index 0-based index into ordered RelTraitSet    * @return the RelTrait    * @throws ArrayIndexOutOfBoundsException if index greater than or equal to    *                                        {@link #size()} or less than 0.    */
specifier|public
name|RelTrait
name|getTrait
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|traits
index|[
name|index
index|]
return|;
block|}
comment|/**    * Retrieves a list of traits from the set.    *    * @param index 0-based index into ordered RelTraitSet    * @return the RelTrait    * @throws ArrayIndexOutOfBoundsException if index greater than or equal to    *                                        {@link #size()} or less than 0.    */
specifier|public
parameter_list|<
name|E
extends|extends
name|RelMultipleTrait
parameter_list|>
name|List
argument_list|<
name|E
argument_list|>
name|getTraits
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|RelTrait
name|trait
init|=
name|traits
index|[
name|index
index|]
decl_stmt|;
if|if
condition|(
name|trait
operator|instanceof
name|RelCompositeTrait
condition|)
block|{
comment|//noinspection unchecked
return|return
operator|(
operator|(
name|RelCompositeTrait
argument_list|<
name|E
argument_list|>
operator|)
name|trait
operator|)
operator|.
name|traitList
argument_list|()
return|;
block|}
else|else
block|{
comment|//noinspection unchecked
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
operator|(
name|E
operator|)
name|trait
argument_list|)
return|;
block|}
block|}
specifier|public
name|RelTrait
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|getTrait
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**    * Returns whether a given kind of trait is enabled.    */
specifier|public
parameter_list|<
name|T
extends|extends
name|RelTrait
parameter_list|>
name|boolean
name|isEnabled
parameter_list|(
name|RelTraitDef
argument_list|<
name|T
argument_list|>
name|traitDef
parameter_list|)
block|{
return|return
name|getTrait
argument_list|(
name|traitDef
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**    * Retrieves a RelTrait of the given type from the set.    *    * @param traitDef the type of RelTrait to retrieve    * @return the RelTrait, or null if not found    */
specifier|public
parameter_list|<
name|T
extends|extends
name|RelTrait
parameter_list|>
name|T
name|getTrait
parameter_list|(
name|RelTraitDef
argument_list|<
name|T
argument_list|>
name|traitDef
parameter_list|)
block|{
name|int
name|index
init|=
name|findIndex
argument_list|(
name|traitDef
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|>=
literal|0
condition|)
block|{
comment|//noinspection unchecked
return|return
operator|(
name|T
operator|)
name|getTrait
argument_list|(
name|index
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Retrieves a list of traits of the given type from the set.    *    *<p>Only valid for traits that support multiple entries. (E.g. collation.)    *    * @param traitDef the type of RelTrait to retrieve    * @return the RelTrait, or null if not found    */
specifier|public
parameter_list|<
name|T
extends|extends
name|RelMultipleTrait
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|getTraits
parameter_list|(
name|RelTraitDef
argument_list|<
name|T
argument_list|>
name|traitDef
parameter_list|)
block|{
name|int
name|index
init|=
name|findIndex
argument_list|(
name|traitDef
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|>=
literal|0
condition|)
block|{
comment|//noinspection unchecked
return|return
operator|(
name|List
argument_list|<
name|T
argument_list|>
operator|)
name|getTraits
argument_list|(
name|index
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Replaces an existing RelTrait in the set.    * Returns a different trait set; does not modify this trait set.    *    * @param index 0-based index into ordered RelTraitSet    * @param trait the new RelTrait    * @return the old RelTrait at the index    */
specifier|public
name|RelTraitSet
name|replace
parameter_list|(
name|int
name|index
parameter_list|,
name|RelTrait
name|trait
parameter_list|)
block|{
assert|assert
name|traits
index|[
name|index
index|]
operator|.
name|getTraitDef
argument_list|()
operator|==
name|trait
operator|.
name|getTraitDef
argument_list|()
operator|:
literal|"RelTrait has different RelTraitDef than replacement"
assert|;
name|RelTrait
name|canonizedTrait
init|=
name|canonize
argument_list|(
name|trait
argument_list|)
decl_stmt|;
if|if
condition|(
name|traits
index|[
name|index
index|]
operator|==
name|canonizedTrait
condition|)
block|{
return|return
name|this
return|;
block|}
name|RelTrait
index|[]
name|newTraits
init|=
name|traits
operator|.
name|clone
argument_list|()
decl_stmt|;
name|newTraits
index|[
name|index
index|]
operator|=
name|canonizedTrait
expr_stmt|;
return|return
name|cache
operator|.
name|getOrAdd
argument_list|(
operator|new
name|RelTraitSet
argument_list|(
name|cache
argument_list|,
name|newTraits
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns a trait set consisting of the current set plus a new trait.    *    *<p>If the set does not contain a trait of the same {@link RelTraitDef},    * the trait is ignored, and this trait set is returned.    *    * @param trait the new trait    * @return New set    * @see #plus(RelTrait)    */
specifier|public
name|RelTraitSet
name|replace
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
block|{
comment|// Quick check for common case
if|if
condition|(
name|containsShallow
argument_list|(
name|traits
argument_list|,
name|trait
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
specifier|final
name|RelTraitDef
name|traitDef
init|=
name|trait
operator|.
name|getTraitDef
argument_list|()
decl_stmt|;
name|int
name|index
init|=
name|findIndex
argument_list|(
name|traitDef
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
comment|// Trait is not present. Ignore it.
return|return
name|this
return|;
block|}
return|return
name|replace
argument_list|(
name|index
argument_list|,
name|trait
argument_list|)
return|;
block|}
comment|/** Returns whether an element occurs within an array.    *    *<p>Uses {@code ==}, not {@link #equals}. Nulls are allowed. */
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|boolean
name|containsShallow
parameter_list|(
name|T
index|[]
name|ts
parameter_list|,
name|RelTrait
name|seek
parameter_list|)
block|{
for|for
control|(
name|T
name|t
range|:
name|ts
control|)
block|{
if|if
condition|(
name|t
operator|==
name|seek
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
comment|/** Replaces the trait(s) of a given type with a list of traits of the same    * type.    *    *<p>The list must not be empty, and all traits must be of the same type.    */
specifier|public
parameter_list|<
name|T
extends|extends
name|RelMultipleTrait
parameter_list|>
name|RelTraitSet
name|replace
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|traits
parameter_list|)
block|{
assert|assert
operator|!
name|traits
operator|.
name|isEmpty
argument_list|()
assert|;
specifier|final
name|RelTraitDef
name|def
init|=
name|traits
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTraitDef
argument_list|()
decl_stmt|;
return|return
name|replace
argument_list|(
name|RelCompositeTrait
operator|.
name|of
argument_list|(
name|def
argument_list|,
name|traits
argument_list|)
argument_list|)
return|;
block|}
comment|/** Replaces the trait(s) of a given type with a list of traits of the same    * type.    *    *<p>The list must not be empty, and all traits must be of the same type.    */
specifier|public
parameter_list|<
name|T
extends|extends
name|RelMultipleTrait
parameter_list|>
name|RelTraitSet
name|replace
parameter_list|(
name|RelTraitDef
argument_list|<
name|T
argument_list|>
name|def
parameter_list|,
name|List
argument_list|<
name|T
argument_list|>
name|traits
parameter_list|)
block|{
return|return
name|replace
argument_list|(
name|RelCompositeTrait
operator|.
name|of
argument_list|(
name|def
argument_list|,
name|traits
argument_list|)
argument_list|)
return|;
block|}
comment|/** If a given multiple trait is enabled, replaces it by calling the given    * function. */
specifier|public
parameter_list|<
name|T
extends|extends
name|RelMultipleTrait
parameter_list|>
name|RelTraitSet
name|replaceIfs
parameter_list|(
name|RelTraitDef
argument_list|<
name|T
argument_list|>
name|def
parameter_list|,
name|Supplier
argument_list|<
name|List
argument_list|<
name|T
argument_list|>
argument_list|>
name|traitSupplier
parameter_list|)
block|{
name|int
name|index
init|=
name|findIndex
argument_list|(
name|def
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
return|return
name|this
return|;
comment|// trait is not enabled; ignore it
block|}
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|traitList
init|=
name|traitSupplier
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|traitList
operator|==
literal|null
condition|)
block|{
return|return
name|replace
argument_list|(
name|index
argument_list|,
name|def
operator|.
name|getDefault
argument_list|()
argument_list|)
return|;
block|}
return|return
name|replace
argument_list|(
name|index
argument_list|,
name|RelCompositeTrait
operator|.
name|of
argument_list|(
name|def
argument_list|,
name|traitList
argument_list|)
argument_list|)
return|;
block|}
comment|/** If a given trait is enabled, replaces it by calling the given function. */
specifier|public
parameter_list|<
name|T
extends|extends
name|RelTrait
parameter_list|>
name|RelTraitSet
name|replaceIf
parameter_list|(
name|RelTraitDef
argument_list|<
name|T
argument_list|>
name|def
parameter_list|,
name|Supplier
argument_list|<
name|T
argument_list|>
name|traitSupplier
parameter_list|)
block|{
name|int
name|index
init|=
name|findIndex
argument_list|(
name|def
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
return|return
name|this
return|;
comment|// trait is not enabled; ignore it
block|}
name|T
name|traitList
init|=
name|traitSupplier
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|traitList
operator|==
literal|null
condition|)
block|{
name|traitList
operator|=
name|def
operator|.
name|getDefault
argument_list|()
expr_stmt|;
block|}
return|return
name|replace
argument_list|(
name|index
argument_list|,
name|traitList
argument_list|)
return|;
block|}
comment|/**    * Applies a mapping to this traitSet.    *    * @param mapping   Mapping    * @return traitSet with mapping applied    */
specifier|public
name|RelTraitSet
name|apply
parameter_list|(
name|Mappings
operator|.
name|TargetMapping
name|mapping
parameter_list|)
block|{
name|RelTrait
index|[]
name|newTraits
init|=
operator|new
name|RelTrait
index|[
name|traits
operator|.
name|length
index|]
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
name|newTraits
index|[
name|i
index|]
operator|=
name|traits
index|[
name|i
index|]
operator|.
name|apply
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
block|}
return|return
name|cache
operator|.
name|getOrAdd
argument_list|(
operator|new
name|RelTraitSet
argument_list|(
name|cache
argument_list|,
name|newTraits
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns whether all the traits are default trait value.    */
specifier|public
name|boolean
name|isDefault
parameter_list|()
block|{
for|for
control|(
specifier|final
name|RelTrait
name|trait
range|:
name|traits
control|)
block|{
if|if
condition|(
name|trait
operator|!=
name|trait
operator|.
name|getTraitDef
argument_list|()
operator|.
name|getDefault
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Returns whether all the traits except {@link Convention}    * are default trait value.    */
specifier|public
name|boolean
name|isDefaultSansConvention
parameter_list|()
block|{
for|for
control|(
specifier|final
name|RelTrait
name|trait
range|:
name|traits
control|)
block|{
if|if
condition|(
name|trait
operator|.
name|getTraitDef
argument_list|()
operator|==
name|ConventionTraitDef
operator|.
name|INSTANCE
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|trait
operator|!=
name|trait
operator|.
name|getTraitDef
argument_list|()
operator|.
name|getDefault
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Returns whether all the traits except {@link Convention}    * equals with traits in {@code other} traitSet.    */
specifier|public
name|boolean
name|equalsSansConvention
parameter_list|(
name|RelTraitSet
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|this
operator|.
name|size
argument_list|()
operator|!=
name|other
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
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
if|if
condition|(
name|traits
index|[
name|i
index|]
operator|.
name|getTraitDef
argument_list|()
operator|==
name|ConventionTraitDef
operator|.
name|INSTANCE
condition|)
block|{
continue|continue;
block|}
comment|// each trait should be canonized already
if|if
condition|(
name|traits
index|[
name|i
index|]
operator|!=
name|other
operator|.
name|traits
index|[
name|i
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Returns a new traitSet with same traitDefs with    * current traitSet, but each trait is the default    * trait value.    */
specifier|public
name|RelTraitSet
name|getDefault
parameter_list|()
block|{
name|RelTrait
index|[]
name|newTraits
init|=
operator|new
name|RelTrait
index|[
name|traits
operator|.
name|length
index|]
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
name|newTraits
index|[
name|i
index|]
operator|=
name|traits
index|[
name|i
index|]
operator|.
name|getTraitDef
argument_list|()
operator|.
name|getDefault
argument_list|()
expr_stmt|;
block|}
return|return
name|cache
operator|.
name|getOrAdd
argument_list|(
operator|new
name|RelTraitSet
argument_list|(
name|cache
argument_list|,
name|newTraits
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns a new traitSet with same traitDefs with    * current traitSet, but each trait except {@link Convention}    * is the default trait value. {@link Convention} trait    * remains the same with current traitSet.    */
specifier|public
name|RelTraitSet
name|getDefaultSansConvention
parameter_list|()
block|{
name|RelTrait
index|[]
name|newTraits
init|=
operator|new
name|RelTrait
index|[
name|traits
operator|.
name|length
index|]
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
if|if
condition|(
name|traits
index|[
name|i
index|]
operator|.
name|getTraitDef
argument_list|()
operator|==
name|ConventionTraitDef
operator|.
name|INSTANCE
condition|)
block|{
name|newTraits
index|[
name|i
index|]
operator|=
name|traits
index|[
name|i
index|]
expr_stmt|;
block|}
else|else
block|{
name|newTraits
index|[
name|i
index|]
operator|=
name|traits
index|[
name|i
index|]
operator|.
name|getTraitDef
argument_list|()
operator|.
name|getDefault
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|cache
operator|.
name|getOrAdd
argument_list|(
operator|new
name|RelTraitSet
argument_list|(
name|cache
argument_list|,
name|newTraits
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns {@link Convention} trait defined by    * {@link ConventionTraitDef#INSTANCE}, or null if the    * {@link ConventionTraitDef#INSTANCE} is not registered    * in this traitSet.    */
specifier|public
name|Convention
name|getConvention
parameter_list|()
block|{
return|return
name|getTrait
argument_list|(
name|ConventionTraitDef
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
comment|/**    * Returns {@link RelDistribution} trait defined by    * {@link RelDistributionTraitDef#INSTANCE}, or null if the    * {@link RelDistributionTraitDef#INSTANCE} is not registered    * in this traitSet.    */
specifier|public
parameter_list|<
name|T
extends|extends
name|RelDistribution
parameter_list|>
name|T
name|getDistribution
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
operator|(
name|T
operator|)
name|getTrait
argument_list|(
name|RelDistributionTraitDef
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
comment|/**    * Returns {@link RelCollation} trait defined by    * {@link RelCollationTraitDef#INSTANCE}, or null if the    * {@link RelCollationTraitDef#INSTANCE} is not registered    * in this traitSet.    */
specifier|public
parameter_list|<
name|T
extends|extends
name|RelCollation
parameter_list|>
name|T
name|getCollation
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
operator|(
name|T
operator|)
name|getTrait
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
comment|/**    * Returns the size of the RelTraitSet.    *    * @return the size of the RelTraitSet.    */
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
comment|/**    * Converts a trait to canonical form.    *    *<p>After canonization, t1.equals(t2) if and only if t1 == t2.    *    * @param trait Trait    * @return Trait in canonical form    */
specifier|public
parameter_list|<
name|T
extends|extends
name|RelTrait
parameter_list|>
name|T
name|canonize
parameter_list|(
name|T
name|trait
parameter_list|)
block|{
if|if
condition|(
name|trait
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
name|trait
operator|instanceof
name|RelCompositeTrait
condition|)
block|{
comment|// Composite traits are canonized on creation
comment|//noinspection unchecked
return|return
name|trait
return|;
block|}
comment|//noinspection unchecked
return|return
operator|(
name|T
operator|)
name|trait
operator|.
name|getTraitDef
argument_list|()
operator|.
name|canonize
argument_list|(
name|trait
argument_list|)
return|;
block|}
comment|/**    * Compares two RelTraitSet objects for equality.    *    * @param obj another RelTraitSet    * @return true if traits are equal and in the same order, false otherwise    */
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
name|RelTraitSet
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RelTraitSet
name|that
init|=
operator|(
name|RelTraitSet
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|hash
operator|!=
literal|0
operator|&&
name|that
operator|.
name|hash
operator|!=
literal|0
operator|&&
name|this
operator|.
name|hash
operator|!=
name|that
operator|.
name|hash
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|traits
operator|.
name|length
operator|!=
name|that
operator|.
name|traits
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
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
if|if
condition|(
name|traits
index|[
name|i
index|]
operator|!=
name|that
operator|.
name|traits
index|[
name|i
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
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
name|Arrays
operator|.
name|hashCode
argument_list|(
name|traits
argument_list|)
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
comment|/**    * Returns whether this trait set satisfies another trait set.    *    *<p>For that to happen, each trait satisfies the corresponding trait in the    * other set. In particular, each trait set satisfies itself, because each    * trait subsumes itself.    *    *<p>Intuitively, if a relational expression is needed that has trait set    * S (A, B), and trait set S1 (A1, B1) subsumes S, then any relational    * expression R in S1 meets that need.    *    *<p>For example, if we need a relational expression that has    * trait set S = {enumerable convention, sorted on [C1 asc]}, and R    * has {enumerable convention, sorted on [C3], [C1, C2]}. R has two    * sort keys, but one them [C1, C2] satisfies S [C1], and that is enough.    *    * @param that another RelTraitSet    * @return whether this trait set satisfies other trait set    *    * @see org.apache.calcite.plan.RelTrait#satisfies(RelTrait)    */
specifier|public
name|boolean
name|satisfies
parameter_list|(
name|RelTraitSet
name|that
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|this
operator|.
name|size
argument_list|()
argument_list|,
name|that
operator|.
name|size
argument_list|()
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|RelTrait
name|thisTrait
init|=
name|this
operator|.
name|traits
index|[
name|i
index|]
decl_stmt|;
name|RelTrait
name|thatTrait
init|=
name|that
operator|.
name|traits
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|thisTrait
operator|.
name|satisfies
argument_list|(
name|thatTrait
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Compares two RelTraitSet objects to see if they match for the purposes of    * firing a rule. A null RelTrait within a RelTraitSet indicates a wildcard:    * any RelTrait in the other RelTraitSet will match. If one RelTraitSet is    * smaller than the other, comparison stops when the last RelTrait from the    * smaller set has been examined and the remaining RelTraits in the larger    * set are assumed to match.    *    * @param that another RelTraitSet    * @return true if the RelTraitSets match, false otherwise    */
specifier|public
name|boolean
name|matches
parameter_list|(
name|RelTraitSet
name|that
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|this
operator|.
name|size
argument_list|()
argument_list|,
name|that
operator|.
name|size
argument_list|()
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|RelTrait
name|thisTrait
init|=
name|this
operator|.
name|traits
index|[
name|i
index|]
decl_stmt|;
name|RelTrait
name|thatTrait
init|=
name|that
operator|.
name|traits
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|thisTrait
operator|==
literal|null
operator|)
operator|||
operator|(
name|thatTrait
operator|==
literal|null
operator|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|thisTrait
operator|!=
name|thatTrait
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Returns whether this trait set contains a given trait.    *    * @param trait Sought trait    * @return Whether set contains given trait    */
specifier|public
name|boolean
name|contains
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
block|{
for|for
control|(
name|RelTrait
name|relTrait
range|:
name|traits
control|)
block|{
if|if
condition|(
name|trait
operator|==
name|relTrait
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
comment|/**    * Returns whether this trait set contains the given trait, or whether the    * trait is not present because its {@link RelTraitDef} is not enabled.    * Returns false if another trait of the same {@code RelTraitDef} is    * present.    *    * @param trait Trait    * @return Whether trait is present, or is absent because disabled    */
specifier|public
name|boolean
name|containsIfApplicable
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
block|{
comment|// Note that '==' is sufficient, because trait should be canonized.
specifier|final
name|RelTrait
name|trait1
init|=
name|getTrait
argument_list|(
name|trait
operator|.
name|getTraitDef
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|trait1
operator|==
literal|null
operator|||
name|trait1
operator|==
name|trait
return|;
block|}
comment|/**    * Returns whether this trait set comprises precisely the list of given    * traits.    *    * @param relTraits Traits    * @return Whether this trait set's traits are the same as the argument    */
specifier|public
name|boolean
name|comprises
parameter_list|(
name|RelTrait
modifier|...
name|relTraits
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|traits
argument_list|,
name|relTraits
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
if|if
condition|(
name|string
operator|==
literal|null
condition|)
block|{
name|string
operator|=
name|computeString
argument_list|()
expr_stmt|;
block|}
return|return
name|string
return|;
block|}
comment|/**    * Outputs the traits of this set as a String. Traits are output in order,    * separated by periods.    */
specifier|protected
name|String
name|computeString
parameter_list|()
block|{
name|StringBuilder
name|s
init|=
operator|new
name|StringBuilder
argument_list|()
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
specifier|final
name|RelTrait
name|trait
init|=
name|traits
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|trait
operator|==
literal|null
operator|)
operator|&&
operator|(
name|traits
operator|.
name|length
operator|==
literal|1
operator|)
condition|)
block|{
comment|// Special format for a list containing a single null trait;
comment|// otherwise its string appears as "null", which is the same
comment|// as if the whole trait set were null, and so confusing.
name|s
operator|.
name|append
argument_list|(
literal|"{null}"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|s
operator|.
name|append
argument_list|(
name|trait
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|s
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Finds the index of a trait of a given type in this set.    *    * @param traitDef Sought trait definition    * @return index of trait, or -1 if not found    */
specifier|private
name|int
name|findIndex
parameter_list|(
name|RelTraitDef
name|traitDef
parameter_list|)
block|{
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
name|RelTrait
name|trait
init|=
name|traits
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|trait
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|trait
operator|.
name|getTraitDef
argument_list|()
operator|==
name|traitDef
operator|)
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * Returns this trait set with a given trait added or overridden. Does not    * modify this trait set.    *    * @param trait Trait    * @return Trait set with given trait    */
specifier|public
name|RelTraitSet
name|plus
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
block|{
if|if
condition|(
name|contains
argument_list|(
name|trait
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
name|int
name|i
init|=
name|findIndex
argument_list|(
name|trait
operator|.
name|getTraitDef
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
return|return
name|replace
argument_list|(
name|i
argument_list|,
name|trait
argument_list|)
return|;
block|}
specifier|final
name|RelTrait
name|canonizedTrait
init|=
name|canonize
argument_list|(
name|trait
argument_list|)
decl_stmt|;
assert|assert
name|canonizedTrait
operator|!=
literal|null
assert|;
name|RelTrait
index|[]
name|newTraits
init|=
operator|new
name|RelTrait
index|[
name|traits
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|traits
argument_list|,
literal|0
argument_list|,
name|newTraits
argument_list|,
literal|0
argument_list|,
name|traits
operator|.
name|length
argument_list|)
expr_stmt|;
name|newTraits
index|[
name|traits
operator|.
name|length
index|]
operator|=
name|canonizedTrait
expr_stmt|;
return|return
name|cache
operator|.
name|getOrAdd
argument_list|(
operator|new
name|RelTraitSet
argument_list|(
name|cache
argument_list|,
name|newTraits
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelTraitSet
name|plusAll
parameter_list|(
name|RelTrait
index|[]
name|traits
parameter_list|)
block|{
name|RelTraitSet
name|t
init|=
name|this
decl_stmt|;
for|for
control|(
name|RelTrait
name|trait
range|:
name|traits
control|)
block|{
name|t
operator|=
name|t
operator|.
name|plus
argument_list|(
name|trait
argument_list|)
expr_stmt|;
block|}
return|return
name|t
return|;
block|}
specifier|public
name|RelTraitSet
name|merge
parameter_list|(
name|RelTraitSet
name|additionalTraits
parameter_list|)
block|{
return|return
name|plusAll
argument_list|(
name|additionalTraits
operator|.
name|traits
argument_list|)
return|;
block|}
comment|/** Returns a list of traits that are in {@code traitSet} but not in this    * RelTraitSet. */
specifier|public
name|ImmutableList
argument_list|<
name|RelTrait
argument_list|>
name|difference
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|)
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|RelTrait
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|this
operator|.
name|size
argument_list|()
argument_list|,
name|traitSet
operator|.
name|size
argument_list|()
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|RelTrait
name|thisTrait
init|=
name|this
operator|.
name|traits
index|[
name|i
index|]
decl_stmt|;
name|RelTrait
name|thatTrait
init|=
name|traitSet
operator|.
name|traits
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|thisTrait
operator|!=
name|thatTrait
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|thatTrait
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/** Returns whether there are any composite traits in this set. */
specifier|public
name|boolean
name|allSimple
parameter_list|()
block|{
for|for
control|(
name|RelTrait
name|trait
range|:
name|traits
control|)
block|{
if|if
condition|(
name|trait
operator|instanceof
name|RelCompositeTrait
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/** Returns a trait set similar to this one but with all composite traits    * flattened. */
specifier|public
name|RelTraitSet
name|simplify
parameter_list|()
block|{
name|RelTraitSet
name|x
init|=
name|this
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
specifier|final
name|RelTrait
name|trait
init|=
name|traits
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|trait
operator|instanceof
name|RelCompositeTrait
condition|)
block|{
name|x
operator|=
name|x
operator|.
name|replace
argument_list|(
name|i
argument_list|,
operator|(
operator|(
name|RelCompositeTrait
operator|)
name|trait
operator|)
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
operator|(
operator|(
name|RelCompositeTrait
operator|)
name|trait
operator|)
operator|.
name|trait
argument_list|(
literal|0
argument_list|)
else|:
name|trait
operator|.
name|getTraitDef
argument_list|()
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|x
return|;
block|}
comment|/** Cache of trait sets. */
specifier|private
specifier|static
class|class
name|Cache
block|{
specifier|final
name|Map
argument_list|<
name|RelTraitSet
argument_list|,
name|RelTraitSet
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Cache
parameter_list|()
block|{
block|}
name|RelTraitSet
name|getOrAdd
parameter_list|(
name|RelTraitSet
name|t
parameter_list|)
block|{
name|RelTraitSet
name|exist
init|=
name|map
operator|.
name|putIfAbsent
argument_list|(
name|t
argument_list|,
name|t
argument_list|)
decl_stmt|;
return|return
name|exist
operator|==
literal|null
condition|?
name|t
else|:
name|exist
return|;
block|}
block|}
block|}
end_class

end_unit

