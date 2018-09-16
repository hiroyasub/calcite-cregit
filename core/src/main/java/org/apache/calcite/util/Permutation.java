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
name|util
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
name|util
operator|.
name|mapping
operator|.
name|IntPair
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
name|Mapping
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
name|MappingType
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
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Represents a mapping which reorders elements in an array.  */
end_comment

begin_class
specifier|public
class|class
name|Permutation
implements|implements
name|Mapping
implements|,
name|Mappings
operator|.
name|TargetMapping
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|int
index|[]
name|targets
decl_stmt|;
specifier|private
name|int
index|[]
name|sources
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a permutation of a given size.    *    *<p>It is initialized to the identity permutation, such as "[0, 1, 2, 3]".    *    * @param size Number of elements in the permutation    */
specifier|public
name|Permutation
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|targets
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
name|sources
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
comment|// Initialize to identity.
name|identity
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates a permutation from an array.    *    * @param targets Array of targets    * @throws IllegalArgumentException       if elements of array are not unique    * @throws ArrayIndexOutOfBoundsException if elements of array are not    *                                        between 0 through targets.length - 1    *                                        inclusive    */
specifier|public
name|Permutation
parameter_list|(
name|int
index|[]
name|targets
parameter_list|)
block|{
name|this
operator|.
name|targets
operator|=
name|targets
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|sources
operator|=
operator|new
name|int
index|[
name|targets
operator|.
name|length
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|sources
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|targets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|target
init|=
name|targets
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|target
operator|<
literal|0
operator|||
name|target
operator|>=
name|sources
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"target out of range"
argument_list|)
throw|;
block|}
if|if
condition|(
name|sources
index|[
name|target
index|]
operator|!=
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"more than one permutation element maps to position "
operator|+
name|target
argument_list|)
throw|;
block|}
name|sources
index|[
name|target
index|]
operator|=
name|i
expr_stmt|;
block|}
assert|assert
name|isValid
argument_list|(
literal|true
argument_list|)
assert|;
block|}
comment|/**    * Creates a permutation. Arrays are not copied, and are assumed to be valid    * permutations.    */
specifier|private
name|Permutation
parameter_list|(
name|int
index|[]
name|targets
parameter_list|,
name|int
index|[]
name|sources
parameter_list|)
block|{
name|this
operator|.
name|targets
operator|=
name|targets
expr_stmt|;
name|this
operator|.
name|sources
operator|=
name|sources
expr_stmt|;
assert|assert
name|isValid
argument_list|(
literal|true
argument_list|)
assert|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Object
name|clone
parameter_list|()
block|{
return|return
operator|new
name|Permutation
argument_list|(
name|targets
operator|.
name|clone
argument_list|()
argument_list|,
name|sources
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Initializes this permutation to the identity permutation.    */
specifier|public
name|void
name|identity
parameter_list|()
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
name|targets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|targets
index|[
name|i
index|]
operator|=
name|sources
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
block|}
comment|/**    * Returns the number of elements in this permutation.    */
specifier|public
specifier|final
name|int
name|size
parameter_list|()
block|{
return|return
name|targets
operator|.
name|length
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot clear: permutation must always contain one mapping per element"
argument_list|)
throw|;
block|}
comment|/**    * Returns a string representation of this permutation.    *    *<p>For example, the mapping    *    *<table>    *<caption>Example mapping</caption>    *<tr>    *<th>source</th>    *<th>target</th>    *</tr>    *<tr>    *<td>0</td>    *<td>2</td>    *</tr>    *<tr>    *<td>1</td>    *<td>0</td>    *</tr>    *<tr>    *<td>2</td>    *<td>1</td>    *</tr>    *<tr>    *<td>3</td>    *<td>3</td>    *</tr>    *</table>    *    *<p>is represented by the string "[2, 0, 1, 3]".    */
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|targets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|targets
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Maps source position to target position.    *    *<p>To preserve the 1:1 nature of the permutation, the previous target of    * source becomes the new target of the previous source.    *    *<p>For example, given the permutation    *    *<blockquote><pre>[3, 2, 0, 1]</pre></blockquote>    *    *<p>suppose we map position 2 to target 1. Position 2 currently has target    * 0, and the source of position 1 is position 3. We preserve the permutation    * property by mapping the previous source 3 to the previous target 0. The new    * permutation is    *    *<blockquote><pre>[3, 2, 1, 0].</pre></blockquote>    *    *<p>Another example. Again starting from    *    *<blockquote><pre>[3, 2, 0, 1]</pre></blockquote>    *    *<p>suppose we map position 2 to target 3. We map the previous source 0 to    * the previous target 0, which gives    *    *<blockquote><pre>[0, 2, 3, 1].</pre></blockquote>    *    * @param source Source position    * @param target Target position    * @throws ArrayIndexOutOfBoundsException if source or target is negative or    *                                        greater than or equal to the size of    *                                        the permuation    */
specifier|public
name|void
name|set
parameter_list|(
name|int
name|source
parameter_list|,
name|int
name|target
parameter_list|)
block|{
name|set
argument_list|(
name|source
argument_list|,
name|target
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Maps source position to target position, automatically resizing if source    * or target is out of bounds.    *    *<p>To preserve the 1:1 nature of the permutation, the previous target of    * source becomes the new target of the previous source.    *    *<p>For example, given the permutation    *    *<blockquote><pre>[3, 2, 0, 1]</pre></blockquote>    *    *<p>suppose we map position 2 to target 1. Position 2 currently has target    * 0, and the source of position 1 is position 3. We preserve the permutation    * property by mapping the previous source 3 to the previous target 0. The new    * permutation is    *    *<blockquote><pre>[3, 2, 1, 0].</pre></blockquote>    *    *<p>Another example. Again starting from    *    *<blockquote><pre>[3, 2, 0, 1]</pre></blockquote>    *    *<p>suppose we map position 2 to target 3. We map the previous source 0 to    * the previous target 0, which gives    *    *<blockquote><pre>[0, 2, 3, 1].</pre></blockquote>    *    * @param source      Source position    * @param target      Target position    * @param allowResize Whether to resize the permutation if the source or    *                    target is greater than the current capacity    * @throws ArrayIndexOutOfBoundsException if source or target is negative,    *                                        or greater than or equal to the size    *                                        of the permutation, and    *<code>allowResize</code> is false    */
specifier|public
name|void
name|set
parameter_list|(
name|int
name|source
parameter_list|,
name|int
name|target
parameter_list|,
name|boolean
name|allowResize
parameter_list|)
block|{
specifier|final
name|int
name|maxSourceTarget
init|=
name|Math
operator|.
name|max
argument_list|(
name|source
argument_list|,
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxSourceTarget
operator|>=
name|sources
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|allowResize
condition|)
block|{
name|resize
argument_list|(
name|maxSourceTarget
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
name|maxSourceTarget
argument_list|)
throw|;
block|}
block|}
name|int
name|prevTarget
init|=
name|targets
index|[
name|source
index|]
decl_stmt|;
assert|assert
name|sources
index|[
name|prevTarget
index|]
operator|==
name|source
assert|;
name|int
name|prevSource
init|=
name|sources
index|[
name|target
index|]
decl_stmt|;
assert|assert
name|targets
index|[
name|prevSource
index|]
operator|==
name|target
assert|;
name|setInternal
argument_list|(
name|source
argument_list|,
name|target
argument_list|)
expr_stmt|;
comment|// To balance things up, make the previous source reference the
comment|// previous target. This ensures that each ordinal occurs precisely
comment|// once in the sources array and the targets array.
name|setInternal
argument_list|(
name|prevSource
argument_list|,
name|prevTarget
argument_list|)
expr_stmt|;
comment|// For example:
comment|// Before: [2, 1, 0, 3]
comment|// Now we set(source=1, target=0)
comment|//  previous target of source (1) was 1, is now 0
comment|//  previous source of target (0) was 2, is now 1
comment|//  something now has to have target 1 -- use previous source
comment|// After:  [2, 0, 1, 3]
block|}
comment|/**    * Inserts into the targets.    *    *<p>For example, consider the permutation</p>    *    *<table border="1">    *<caption>Example permutation</caption>    *<tr>    *<td>source</td>    *<td>0</td>    *<td>1</td>    *<td>2</td>    *<td>3</td>    *<td>4</td>    *</tr>    *<tr>    *<td>target</td>    *<td>3</td>    *<td>0</td>    *<td>4</td>    *<td>2</td>    *<td>1</td>    *</tr>    *</table>    *    *<p>After applying<code>insertTarget(2)</code> every target 2 or higher is    * shifted up one.</p>    *    *<table border="1">    *<caption>Mapping after applying insertTarget(2)</caption>    *<tr>    *<td>source</td>    *<td>0</td>    *<td>1</td>    *<td>2</td>    *<td>3</td>    *<td>4</td>    *<td>5</td>    *</tr>    *<tr>    *<td>target</td>    *<td>4</td>    *<td>0</td>    *<td>5</td>    *<td>3</td>    *<td>1</td>    *<td>2</td>    *</tr>    *</table>    *    *<p>Note that the array has been extended to accommodate the new target, and    * the previously unmapped source 5 is mapped to the unused target slot 2.</p>    *    * @param x Ordinal of position to add to target    */
specifier|public
name|void
name|insertTarget
parameter_list|(
name|int
name|x
parameter_list|)
block|{
assert|assert
name|isValid
argument_list|(
literal|true
argument_list|)
assert|;
name|resize
argument_list|(
name|sources
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// Shuffle sources up.
name|shuffleUp
argument_list|(
name|sources
argument_list|,
name|x
argument_list|)
expr_stmt|;
comment|// Shuffle targets.
name|increment
argument_list|(
name|x
argument_list|,
name|targets
argument_list|)
expr_stmt|;
assert|assert
name|isValid
argument_list|(
literal|true
argument_list|)
assert|;
block|}
comment|/**    * Inserts into the sources.    *    *<p>Behavior is analogous to {@link #insertTarget(int)}.</p>    *    * @param x Ordinal of position to add to source    */
specifier|public
name|void
name|insertSource
parameter_list|(
name|int
name|x
parameter_list|)
block|{
assert|assert
name|isValid
argument_list|(
literal|true
argument_list|)
assert|;
name|resize
argument_list|(
name|targets
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// Shuffle targets up.
name|shuffleUp
argument_list|(
name|targets
argument_list|,
name|x
argument_list|)
expr_stmt|;
comment|// Increment sources.
name|increment
argument_list|(
name|x
argument_list|,
name|sources
argument_list|)
expr_stmt|;
assert|assert
name|isValid
argument_list|(
literal|true
argument_list|)
assert|;
block|}
specifier|private
name|void
name|increment
parameter_list|(
name|int
name|x
parameter_list|,
name|int
index|[]
name|zzz
parameter_list|)
block|{
specifier|final
name|int
name|size
init|=
name|zzz
operator|.
name|length
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
name|size
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|targets
index|[
name|i
index|]
operator|==
operator|(
name|size
operator|-
literal|1
operator|)
condition|)
block|{
name|targets
index|[
name|i
index|]
operator|=
name|x
expr_stmt|;
block|}
if|else if
condition|(
name|targets
index|[
name|i
index|]
operator|>=
name|x
condition|)
block|{
operator|++
name|targets
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|shuffleUp
parameter_list|(
specifier|final
name|int
index|[]
name|zz
parameter_list|,
name|int
name|x
parameter_list|)
block|{
specifier|final
name|int
name|size
init|=
name|zz
operator|.
name|length
decl_stmt|;
name|int
name|t
init|=
name|zz
index|[
name|size
operator|-
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|zz
argument_list|,
name|x
argument_list|,
name|zz
argument_list|,
name|x
operator|+
literal|1
argument_list|,
name|size
operator|-
literal|1
operator|-
name|x
argument_list|)
expr_stmt|;
name|zz
index|[
name|x
index|]
operator|=
name|t
expr_stmt|;
block|}
specifier|private
name|void
name|resize
parameter_list|(
name|int
name|newSize
parameter_list|)
block|{
assert|assert
name|isValid
argument_list|(
literal|true
argument_list|)
assert|;
specifier|final
name|int
name|size
init|=
name|targets
operator|.
name|length
decl_stmt|;
name|int
index|[]
name|newTargets
init|=
operator|new
name|int
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|targets
argument_list|,
literal|0
argument_list|,
name|newTargets
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|int
index|[]
name|newSources
init|=
operator|new
name|int
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|sources
argument_list|,
literal|0
argument_list|,
name|newSources
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
comment|// Initialize the new elements to the identity mapping.
for|for
control|(
name|int
name|i
init|=
name|size
init|;
name|i
operator|<
name|newSize
condition|;
name|i
operator|++
control|)
block|{
name|newSources
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
name|newTargets
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
name|targets
operator|=
name|newTargets
expr_stmt|;
name|sources
operator|=
name|newSources
expr_stmt|;
assert|assert
name|isValid
argument_list|(
literal|true
argument_list|)
assert|;
block|}
specifier|private
name|void
name|setInternal
parameter_list|(
name|int
name|source
parameter_list|,
name|int
name|target
parameter_list|)
block|{
name|targets
index|[
name|source
index|]
operator|=
name|target
expr_stmt|;
name|sources
index|[
name|target
index|]
operator|=
name|source
expr_stmt|;
block|}
comment|/**    * Returns the inverse permutation.    */
specifier|public
name|Permutation
name|inverse
parameter_list|()
block|{
return|return
operator|new
name|Permutation
argument_list|(
name|sources
operator|.
name|clone
argument_list|()
argument_list|,
name|targets
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns whether this is the identity permutation.    */
specifier|public
name|boolean
name|isIdentity
parameter_list|()
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
name|targets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|targets
index|[
name|i
index|]
operator|!=
name|i
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
comment|/**    * Returns the position that<code>source</code> is mapped to.    */
specifier|public
name|int
name|getTarget
parameter_list|(
name|int
name|source
parameter_list|)
block|{
try|try
block|{
return|return
name|targets
index|[
name|source
index|]
return|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Mappings
operator|.
name|NoElementException
argument_list|(
literal|"invalid source "
operator|+
name|source
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns the position which maps to<code>target</code>.    */
specifier|public
name|int
name|getSource
parameter_list|(
name|int
name|target
parameter_list|)
block|{
try|try
block|{
return|return
name|sources
index|[
name|target
index|]
return|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Mappings
operator|.
name|NoElementException
argument_list|(
literal|"invalid target "
operator|+
name|target
argument_list|)
throw|;
block|}
block|}
comment|/**    * Checks whether this permutation is valid.    *    *    *    * @param fail Whether to assert if invalid    * @return Whether valid    */
specifier|private
name|boolean
name|isValid
parameter_list|(
name|boolean
name|fail
parameter_list|)
block|{
specifier|final
name|int
name|size
init|=
name|targets
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|sources
operator|.
name|length
operator|!=
name|size
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
literal|"different lengths"
assert|;
return|return
literal|false
return|;
block|}
comment|// Every element in sources has corresponding element in targets.
name|int
index|[]
name|occurCount
init|=
operator|new
name|int
index|[
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|int
name|target
init|=
name|targets
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|sources
index|[
name|target
index|]
operator|!=
name|i
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
literal|"source["
operator|+
name|target
operator|+
literal|"] = "
operator|+
name|sources
index|[
name|target
index|]
operator|+
literal|", should be "
operator|+
name|i
assert|;
return|return
literal|false
return|;
block|}
name|int
name|source
init|=
name|sources
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|targets
index|[
name|source
index|]
operator|!=
name|i
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
literal|"target["
operator|+
name|source
operator|+
literal|"] = "
operator|+
name|targets
index|[
name|source
index|]
operator|+
literal|", should be "
operator|+
name|i
assert|;
return|return
literal|false
return|;
block|}
comment|// Every member should occur once.
if|if
condition|(
name|occurCount
index|[
name|target
index|]
operator|!=
literal|0
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
literal|"target "
operator|+
name|target
operator|+
literal|" occurs more than once"
assert|;
return|return
literal|false
return|;
block|}
name|occurCount
index|[
name|target
index|]
operator|++
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// not very efficient
return|return
name|toString
argument_list|()
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
comment|// not very efficient
return|return
operator|(
name|obj
operator|instanceof
name|Permutation
operator|)
operator|&&
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|obj
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|// implement Mapping
specifier|public
name|Iterator
argument_list|<
name|IntPair
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|IntPair
argument_list|>
argument_list|()
block|{
specifier|private
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|i
operator|<
name|targets
operator|.
name|length
return|;
block|}
specifier|public
name|IntPair
name|next
parameter_list|()
block|{
specifier|final
name|IntPair
name|pair
init|=
operator|new
name|IntPair
argument_list|(
name|i
argument_list|,
name|targets
index|[
name|i
index|]
argument_list|)
decl_stmt|;
operator|++
name|i
expr_stmt|;
return|return
name|pair
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
specifier|public
name|int
name|getSourceCount
parameter_list|()
block|{
return|return
name|targets
operator|.
name|length
return|;
block|}
specifier|public
name|int
name|getTargetCount
parameter_list|()
block|{
return|return
name|targets
operator|.
name|length
return|;
block|}
specifier|public
name|MappingType
name|getMappingType
parameter_list|()
block|{
return|return
name|MappingType
operator|.
name|BIJECTION
return|;
block|}
specifier|public
name|int
name|getTargetOpt
parameter_list|(
name|int
name|source
parameter_list|)
block|{
return|return
name|getTarget
argument_list|(
name|source
argument_list|)
return|;
block|}
specifier|public
name|int
name|getSourceOpt
parameter_list|(
name|int
name|target
parameter_list|)
block|{
return|return
name|getSource
argument_list|(
name|target
argument_list|)
return|;
block|}
specifier|public
name|void
name|setAll
parameter_list|(
name|Mapping
name|mapping
parameter_list|)
block|{
for|for
control|(
name|IntPair
name|pair
range|:
name|mapping
control|)
block|{
name|set
argument_list|(
name|pair
operator|.
name|source
argument_list|,
name|pair
operator|.
name|target
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the product of this Permutation with a given Permutation. Does    * not modify this Permutation or<code>permutation</code>.    *    *<p>For example, perm.product(perm.inverse()) yields the identity.    */
specifier|public
name|Permutation
name|product
parameter_list|(
name|Permutation
name|permutation
parameter_list|)
block|{
name|Permutation
name|product
init|=
operator|new
name|Permutation
argument_list|(
name|sources
operator|.
name|length
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
name|targets
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|product
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|permutation
operator|.
name|getTarget
argument_list|(
name|targets
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|product
return|;
block|}
block|}
end_class

begin_comment
comment|// End Permutation.java
end_comment

end_unit

