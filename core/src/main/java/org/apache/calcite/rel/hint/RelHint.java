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
name|hint
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
name|ImmutableMap
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
name|ArrayList
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
name|LinkedHashMap
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * Hint attached to a relation expression.  *  *<p>A hint can be used to:  *  *<ul>  *<li>Enforce planner: there's no perfect planner, so it makes sense to implement hints to  *   allow user better control the execution. For instance, "never merge this subquery with others",  *   "treat those tables as leading ones" in the join ordering, etc.</li>  *<li>Append meta data/statistics: Some statistics like âtable index for scanâ and  *   âskew info of some shuffle keysâ are somewhat dynamic for the query, it would be very  *   convenient to config them with hints because our planning metadata from the planner is very  *   often not that accurate.</li>  *<li>Operator resource constraints: For many cases, we would give a default resource  *   configuration for the execution operators, i.e. min parallelism or  *   managed memory (resource consuming UDF) or special resource requirement (GPU or SSD disk)  *   and so on, it would be very flexible to profile the resource with hints per query  *   (instead of the Job).</li>  *</ul>  *  *<p>In order to support hint override, each hint has a {@code inheritPath} (integers list) to  * record its propagate path from the root node, number `0` represents the hint was propagated  * along the first(left) child, number `1` represents the hint was propagated along the  * second(right) child. Given a relational expression tree with initial attached hints:  *  *<blockquote><pre>  *            Filter (Hint1)  *                |  *               Join  *              /    \  *            Scan  Project (Hint2)  *                     |  *                    Scan2  *</pre></blockquote>  *  *<p>The plan would have hints path as follows (assumes each hint can be propagated to all  * child nodes):  *  *<blockquote><ul>  *<li>Filter&#8594; {Hint1[]}</li>  *<li>Join&#8594; {Hint1[0]}</li>  *<li>Scan&#8594; {Hint1[0, 0]}</li>  *<li>Project&#8594; {Hint1[0,1], Hint2[]}</li>  *<li>Scan2&#8594; {[Hint1[0, 1, 0], Hint2[0]}</li>  *</ul></blockquote>  *  *<p>{@code listOptions} and {@code kvOptions} are supposed to contain the same information,  * they are mutually exclusive, that means, they can not both be non-empty.  *  *<p>RelHint is immutable.  */
end_comment

begin_class
specifier|public
class|class
name|RelHint
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|Integer
argument_list|>
name|inheritPath
decl_stmt|;
specifier|public
specifier|final
name|String
name|hintName
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|listOptions
decl_stmt|;
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|kvOptions
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a {@code RelHint}.    *    * @param inheritPath Hint inherit path    * @param hintName    Hint name    * @param listOption  Hint options as string list    * @param kvOptions   Hint options as string key value pair    */
specifier|private
name|RelHint
parameter_list|(
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|inheritPath
parameter_list|,
name|String
name|hintName
parameter_list|,
annotation|@
name|Nullable
name|List
argument_list|<
name|String
argument_list|>
name|listOption
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|kvOptions
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|inheritPath
argument_list|,
literal|"inheritPath"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|hintName
argument_list|,
literal|"hintName"
argument_list|)
expr_stmt|;
name|this
operator|.
name|inheritPath
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|inheritPath
argument_list|)
expr_stmt|;
name|this
operator|.
name|hintName
operator|=
name|hintName
expr_stmt|;
name|this
operator|.
name|listOptions
operator|=
name|listOption
operator|==
literal|null
condition|?
name|ImmutableList
operator|.
name|of
argument_list|()
else|:
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|listOption
argument_list|)
expr_stmt|;
name|this
operator|.
name|kvOptions
operator|=
name|kvOptions
operator|==
literal|null
condition|?
name|ImmutableMap
operator|.
name|of
argument_list|()
else|:
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|kvOptions
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/** Creates a hint builder with specified hint name. */
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|(
name|String
name|hintName
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|hintName
argument_list|)
return|;
block|}
comment|/**    * Returns a copy of this hint with specified inherit path.    *    * @param inheritPath Hint path    * @return the new {@code RelHint}    */
specifier|public
name|RelHint
name|copy
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|inheritPath
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|inheritPath
argument_list|,
literal|"inheritPath"
argument_list|)
expr_stmt|;
return|return
operator|new
name|RelHint
argument_list|(
name|inheritPath
argument_list|,
name|hintName
argument_list|,
name|listOptions
argument_list|,
name|kvOptions
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RelHint
name|hint
init|=
operator|(
name|RelHint
operator|)
name|o
decl_stmt|;
return|return
name|inheritPath
operator|.
name|equals
argument_list|(
name|hint
operator|.
name|inheritPath
argument_list|)
operator|&&
name|hintName
operator|.
name|equals
argument_list|(
name|hint
operator|.
name|hintName
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|listOptions
argument_list|,
name|hint
operator|.
name|listOptions
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|kvOptions
argument_list|,
name|hint
operator|.
name|kvOptions
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
name|this
operator|.
name|hintName
argument_list|,
name|this
operator|.
name|inheritPath
argument_list|,
name|this
operator|.
name|listOptions
argument_list|,
name|this
operator|.
name|kvOptions
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
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|hintName
argument_list|)
operator|.
name|append
argument_list|(
literal|" inheritPath:"
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|inheritPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|listOptions
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|||
name|this
operator|.
name|kvOptions
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" options:"
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|listOptions
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|this
operator|.
name|listOptions
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|this
operator|.
name|kvOptions
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//~ Inner Class ------------------------------------------------------------
comment|/** Builder for {@link RelHint}. */
specifier|public
specifier|static
class|class
name|Builder
block|{
specifier|private
name|String
name|hintName
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|inheritPath
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|listOptions
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|kvOptions
decl_stmt|;
specifier|private
name|Builder
parameter_list|(
name|String
name|hintName
parameter_list|)
block|{
name|this
operator|.
name|listOptions
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|kvOptions
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|hintName
operator|=
name|hintName
expr_stmt|;
name|this
operator|.
name|inheritPath
operator|=
name|ImmutableList
operator|.
name|of
argument_list|()
expr_stmt|;
block|}
comment|/** Sets up the inherit path with given integer list. */
specifier|public
name|Builder
name|inheritPath
parameter_list|(
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|inheritPath
parameter_list|)
block|{
name|this
operator|.
name|inheritPath
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|inheritPath
argument_list|,
literal|"inheritPath"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Sets up the inherit path with given integer array. */
specifier|public
name|Builder
name|inheritPath
parameter_list|(
name|Integer
modifier|...
name|inheritPath
parameter_list|)
block|{
name|this
operator|.
name|inheritPath
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|inheritPath
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Add a hint option as string. */
specifier|public
name|Builder
name|hintOption
parameter_list|(
name|String
name|hintOption
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|hintOption
argument_list|,
literal|"hintOption"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|this
operator|.
name|kvOptions
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|,
literal|"List options and key value options can not be mixed in"
argument_list|)
expr_stmt|;
name|this
operator|.
name|listOptions
operator|.
name|add
argument_list|(
name|hintOption
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Add multiple string hint options. */
specifier|public
name|Builder
name|hintOptions
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|hintOptions
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|hintOptions
argument_list|,
literal|"hintOptions"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|this
operator|.
name|kvOptions
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|,
literal|"List options and key value options can not be mixed in"
argument_list|)
expr_stmt|;
name|this
operator|.
name|listOptions
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|hintOptions
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Add a hint option as string key-value pair. */
specifier|public
name|Builder
name|hintOption
parameter_list|(
name|String
name|optionKey
parameter_list|,
name|String
name|optionValue
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|optionKey
argument_list|,
literal|"optionKey"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|optionValue
argument_list|,
literal|"optionValue"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|this
operator|.
name|listOptions
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|,
literal|"List options and key value options can not be mixed in"
argument_list|)
expr_stmt|;
name|this
operator|.
name|kvOptions
operator|.
name|put
argument_list|(
name|optionKey
argument_list|,
name|optionValue
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Add multiple string key-value pair hint options. */
specifier|public
name|Builder
name|hintOptions
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|kvOptions
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|kvOptions
argument_list|,
literal|"kvOptions"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|this
operator|.
name|listOptions
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|,
literal|"List options and key value options can not be mixed in"
argument_list|)
expr_stmt|;
name|this
operator|.
name|kvOptions
operator|=
name|kvOptions
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|RelHint
name|build
parameter_list|()
block|{
return|return
operator|new
name|RelHint
argument_list|(
name|this
operator|.
name|inheritPath
argument_list|,
name|this
operator|.
name|hintName
argument_list|,
name|this
operator|.
name|listOptions
argument_list|,
name|this
operator|.
name|kvOptions
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

