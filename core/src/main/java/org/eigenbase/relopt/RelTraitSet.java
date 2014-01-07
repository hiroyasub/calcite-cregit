begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|relopt
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Pair
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
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Constructs a RelTraitSet with the given set of RelTraits.      *      * @param cache Trait set cache (and indirectly cluster) that this set      *              belongs to      * @param traits Traits      */
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
comment|/**      * Creates an empty trait set.      *      *<p>It has a new cache, which will be shared by any trait set created from      * it. Thus each empty trait set is the start of a new ancestral line.      */
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
comment|/**      * Retrieves a RelTrait from the set.      *      * @param index 0-based index into ordered RelTraitSet      *      * @return the RelTrait      *      * @throws ArrayIndexOutOfBoundsException if index greater than or equal to      * {@link #size()} or less than 0.      */
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
comment|/**      * Retrieves a RelTrait of the given type from the set.      *      * @param traitDef the type of RelTrait to retrieve      *      * @return the RelTrait, or null if not found      */
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
comment|/**      * Replaces an existing RelTrait in the set.      * Returns a different trait set; does not modify this trait set.      *      * @param index 0-based index into ordered RelTraitSet      * @param trait the new RelTrait      *      * @return the old RelTrait at the index      */
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
comment|/**      * Returns a trait set consisting of the current set plus a new trait.      *      *<p>If the set does not contain a trait of the same {@link RelTraitDef},      * the trait is ignored, and this trait set is returned.      *      * @param trait the new trait      * @return New set      *      * @see #plus(RelTrait)      */
specifier|public
name|RelTraitSet
name|replace
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
block|{
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
comment|/**      * Returns the size of the RelTraitSet.      *      * @return the size of the RelTraitSet.      */
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
comment|/**      * Converts a trait to canonical form.      *      *<p>After canonization, t1.equals(t2) if and only if t1 == t2.      *      * @param trait Trait      *      * @return Trait in canonical form      */
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
comment|/**      * Compares two RelTraitSet objects for equality.      *      * @param obj another RelTraitSet      *      * @return true if traits are equal and in the same order, false otherwise      */
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
name|obj
operator|instanceof
name|RelTraitSet
condition|)
block|{
name|RelTraitSet
name|other
init|=
operator|(
name|RelTraitSet
operator|)
name|obj
decl_stmt|;
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|traits
argument_list|,
name|other
operator|.
name|traits
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Returns whether this trait set subsumes another trait set.      *      *<p>For that to happen, each trait subsumes the corresponding trait in the      * other set. In particular, each trait set subsumes itself, because each      * trait subsumes itself.</p>      *      *<p>Intuitively, if a relational expression is needed that has trait set      * S, and trait set S1 subsumes S, then a relational expression R in S1      * meets that need. For example, if we need a relational expression that has      * trait set S = {enumerable convention, sorted on [C1 asc]}, and R      * has {enumerable convention, sorted on [C1 asc, C2]}</p>      *      * @param that another RelTraitSet      *      * @return whether this trait set subsumes other trait set      */
specifier|public
name|boolean
name|subsumes
parameter_list|(
name|RelTraitSet
name|that
parameter_list|)
block|{
for|for
control|(
name|Pair
argument_list|<
name|RelTrait
argument_list|,
name|RelTrait
argument_list|>
name|pair
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|traits
argument_list|,
name|that
operator|.
name|traits
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|pair
operator|.
name|left
operator|.
name|subsumes
argument_list|(
name|pair
operator|.
name|right
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
comment|/**      * Compares two RelTraitSet objects to see if they match for the purposes of      * firing a rule. A null RelTrait within a RelTraitSet indicates a wildcard:      * any RelTrait in the other RelTraitSet will match. If one RelTraitSet is      * smaller than the other, comparison stops when the last RelTrait from the      * smaller set has been examined and the remaining RelTraits in the larger      * set are assumed to match.      *      * @param that another RelTraitSet      *      * @return true if the RelTraitSets match, false otherwise      */
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
comment|/**      * Returns whether this trait set contains a given trait.      *      * @param trait Sought trait      *      * @return Whether set contains given trait      */
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
comment|/**      * Returns whether this trait set contains the given trait, or whether the      * trait is not present because its {@link RelTraitDef} is not enabled.      * Returns false if another trait of the same {@code RelTraitDef} is      * present.      *      * @param trait Trait      * @return Whether trait is present, or is absent because disabled      */
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
comment|/**      * Returns whether this trait set comprises precisely the list of given      * traits.      *      * @param relTraits Traits      * @return Whether this trait set's traits are the same as the argument      */
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
comment|/**      * Outputs the traits of this set as a String. Traits are output in order,      * separated by periods.      */
specifier|public
name|String
name|toString
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
comment|/**      * Finds the index of a trait of a given type in this set.      *      * @param traitDef Sought trait definition      *      * @return index of trait, or -1 if not found      */
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
comment|/**      * Returns this trait set with a given trait added or overridden. Does not      * modify this trait set.      *      * @param trait Trait      * @return Trait set with given trait      */
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
name|RelTrait
index|[]
name|newTraits
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|traits
argument_list|,
name|traits
operator|.
name|length
operator|+
literal|1
argument_list|)
decl_stmt|;
name|newTraits
index|[
name|newTraits
operator|.
name|length
operator|-
literal|1
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
argument_list|<
name|RelTraitSet
argument_list|,
name|RelTraitSet
argument_list|>
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
name|traitSet
parameter_list|)
block|{
name|RelTraitSet
name|traitSet1
init|=
name|map
operator|.
name|get
argument_list|(
name|traitSet
argument_list|)
decl_stmt|;
if|if
condition|(
name|traitSet1
operator|!=
literal|null
condition|)
block|{
return|return
name|traitSet1
return|;
block|}
name|map
operator|.
name|put
argument_list|(
name|traitSet
argument_list|,
name|traitSet
argument_list|)
expr_stmt|;
return|return
name|traitSet
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RelTraitSet.java
end_comment

end_unit

