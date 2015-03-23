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
name|linq4j
operator|.
name|function
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * Utilities relating to functions.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Functions
block|{
specifier|private
name|Functions
parameter_list|()
block|{
block|}
specifier|public
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Function
argument_list|>
argument_list|,
name|Class
argument_list|>
name|FUNCTION_RESULT_TYPES
init|=
name|Collections
operator|.
expr|<
name|Class
argument_list|<
name|?
extends|extends
name|Function
argument_list|>
decl_stmt|,
name|Class
decl|>
name|unmodifiableMap
argument_list|(
name|map
argument_list|(
name|Function0
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|,
name|Function1
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|,
name|Function2
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|,
name|BigDecimalFunction1
operator|.
name|class
argument_list|,
name|BigDecimal
operator|.
name|class
argument_list|,
name|DoubleFunction1
operator|.
name|class
argument_list|,
name|Double
operator|.
name|TYPE
argument_list|,
name|FloatFunction1
operator|.
name|class
argument_list|,
name|Float
operator|.
name|TYPE
argument_list|,
name|IntegerFunction1
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|TYPE
argument_list|,
name|LongFunction1
operator|.
name|class
argument_list|,
name|Long
operator|.
name|TYPE
argument_list|,
name|NullableBigDecimalFunction1
operator|.
name|class
argument_list|,
name|BigDecimal
operator|.
name|class
argument_list|,
name|NullableDoubleFunction1
operator|.
name|class
argument_list|,
name|Double
operator|.
name|class
argument_list|,
name|NullableFloatFunction1
operator|.
name|class
argument_list|,
name|Float
operator|.
name|class
argument_list|,
name|NullableIntegerFunction1
operator|.
name|class
argument_list|,
name|Integer
operator|.
name|class
argument_list|,
name|NullableLongFunction1
operator|.
name|class
argument_list|,
name|Long
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Function
argument_list|>
argument_list|>
name|FUNCTION1_CLASSES
init|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|inverse
argument_list|(
name|FUNCTION_RESULT_TYPES
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Comparator
name|NULLS_FIRST_COMPARATOR
init|=
operator|new
name|NullsFirstComparator
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Comparator
name|NULLS_LAST_COMPARATOR
init|=
operator|new
name|NullsLastComparator
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Comparator
name|NULLS_LAST_REVERSE_COMPARATOR
init|=
operator|new
name|NullsLastReverseComparator
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Comparator
name|NULLS_FIRST_REVERSE_COMPARATOR
init|=
operator|new
name|NullsFirstReverseComparator
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|EqualityComparer
argument_list|<
name|Object
argument_list|>
name|IDENTITY_COMPARER
init|=
operator|new
name|IdentityEqualityComparer
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|EqualityComparer
argument_list|<
name|Object
index|[]
argument_list|>
name|ARRAY_COMPARER
init|=
operator|new
name|ArrayEqualityComparer
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Function1
name|CONSTANT_NULL_FUNCTION1
init|=
operator|new
name|Function1
argument_list|()
block|{
specifier|public
name|Object
name|apply
parameter_list|(
name|Object
name|s
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Function1
name|TO_STRING_FUNCTION1
init|=
operator|new
name|Function1
argument_list|<
name|Object
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|Object
name|a0
parameter_list|)
block|{
return|return
name|a0
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
parameter_list|(
name|K
name|k
parameter_list|,
name|V
name|v
parameter_list|,
name|Object
modifier|...
name|rest
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|v
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
name|rest
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|map
operator|.
name|put
argument_list|(
operator|(
name|K
operator|)
name|rest
index|[
name|i
operator|++
index|]
argument_list|,
operator|(
name|V
operator|)
name|rest
index|[
name|i
operator|++
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
specifier|private
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|Map
argument_list|<
name|V
argument_list|,
name|K
argument_list|>
name|inverse
parameter_list|(
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
parameter_list|)
block|{
name|HashMap
argument_list|<
name|V
argument_list|,
name|K
argument_list|>
name|inverseMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|inverseMap
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|inverseMap
return|;
block|}
comment|/** Returns a 1-parameter function that always returns the same value. */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|,
name|R
parameter_list|>
name|Function1
argument_list|<
name|T
argument_list|,
name|R
argument_list|>
name|constant
parameter_list|(
specifier|final
name|R
name|r
parameter_list|)
block|{
return|return
operator|new
name|Function1
argument_list|<
name|T
argument_list|,
name|R
argument_list|>
argument_list|()
block|{
specifier|public
name|R
name|apply
parameter_list|(
name|T
name|s
parameter_list|)
block|{
return|return
name|r
return|;
block|}
block|}
return|;
block|}
comment|/** Returns a 1-parameter function that always returns null. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|,
name|R
parameter_list|>
name|Function1
argument_list|<
name|T
argument_list|,
name|R
argument_list|>
name|constantNull
parameter_list|()
block|{
return|return
name|CONSTANT_NULL_FUNCTION1
return|;
block|}
comment|/**    * A predicate with one parameter that always returns {@code true}.    *    * @param<T> First parameter type    *    * @return Predicate that always returns true    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Predicate1
argument_list|<
name|T
argument_list|>
name|truePredicate1
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
operator|(
name|Predicate1
argument_list|<
name|T
argument_list|>
operator|)
name|Predicate1
operator|.
name|TRUE
return|;
block|}
comment|/**    * A predicate with one parameter that always returns {@code true}.    *    * @param<T> First parameter type    *    * @return Predicate that always returns true    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Predicate1
argument_list|<
name|T
argument_list|>
name|falsePredicate1
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
operator|(
name|Predicate1
argument_list|<
name|T
argument_list|>
operator|)
name|Predicate1
operator|.
name|FALSE
return|;
block|}
comment|/**    * A predicate with two parameters that always returns {@code true}.    *    * @param<T1> First parameter type    * @param<T2> Second parameter type    *    * @return Predicate that always returns true    */
specifier|public
specifier|static
parameter_list|<
name|T1
parameter_list|,
name|T2
parameter_list|>
name|Predicate2
argument_list|<
name|T1
argument_list|,
name|T2
argument_list|>
name|truePredicate2
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
operator|(
name|Predicate2
argument_list|<
name|T1
argument_list|,
name|T2
argument_list|>
operator|)
name|Predicate2
operator|.
name|TRUE
return|;
block|}
comment|/**    * A predicate with two parameters that always returns {@code false}.    *    * @param<T1> First parameter type    * @param<T2> Second parameter type    *    * @return Predicate that always returns false    */
specifier|public
specifier|static
parameter_list|<
name|T1
parameter_list|,
name|T2
parameter_list|>
name|Predicate2
argument_list|<
name|T1
argument_list|,
name|T2
argument_list|>
name|falsePredicate2
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
operator|(
name|Predicate2
argument_list|<
name|T1
argument_list|,
name|T2
argument_list|>
operator|)
name|Predicate2
operator|.
name|FALSE
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|TSource
parameter_list|>
name|Function1
argument_list|<
name|TSource
argument_list|,
name|TSource
argument_list|>
name|identitySelector
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
operator|(
name|Function1
operator|)
name|Function1
operator|.
name|IDENTITY
return|;
block|}
comment|/** Returns a selector that calls the {@link Object#toString()} method on    * each element. */
specifier|public
specifier|static
parameter_list|<
name|TSource
parameter_list|>
name|Function1
argument_list|<
name|TSource
argument_list|,
name|String
argument_list|>
name|toStringSelector
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
name|TO_STRING_FUNCTION1
return|;
block|}
comment|/**    * Creates a predicate that returns whether an object is an instance of a    * particular type or is null.    *    * @param clazz Desired type    * @param<T> Type of objects to test    * @param<T2> Desired type    *    * @return Predicate that tests for desired type    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|,
name|T2
parameter_list|>
name|Predicate1
argument_list|<
name|T
argument_list|>
name|ofTypePredicate
parameter_list|(
specifier|final
name|Class
argument_list|<
name|T2
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
operator|new
name|Predicate1
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|T
name|v1
parameter_list|)
block|{
return|return
name|v1
operator|==
literal|null
operator|||
name|clazz
operator|.
name|isInstance
argument_list|(
name|v1
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T1
parameter_list|,
name|T2
parameter_list|>
name|Predicate2
argument_list|<
name|T1
argument_list|,
name|T2
argument_list|>
name|toPredicate2
parameter_list|(
specifier|final
name|Predicate1
argument_list|<
name|T1
argument_list|>
name|p1
parameter_list|)
block|{
return|return
operator|new
name|Predicate2
argument_list|<
name|T1
argument_list|,
name|T2
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|T1
name|v1
parameter_list|,
name|T2
name|v2
parameter_list|)
block|{
return|return
name|p1
operator|.
name|apply
argument_list|(
name|v1
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Converts a 2-parameter function to a predicate.    */
specifier|public
specifier|static
parameter_list|<
name|T1
parameter_list|,
name|T2
parameter_list|>
name|Predicate2
argument_list|<
name|T1
argument_list|,
name|T2
argument_list|>
name|toPredicate
parameter_list|(
specifier|final
name|Function2
argument_list|<
name|T1
argument_list|,
name|T2
argument_list|,
name|Boolean
argument_list|>
name|function
parameter_list|)
block|{
return|return
operator|new
name|Predicate2
argument_list|<
name|T1
argument_list|,
name|T2
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|T1
name|v1
parameter_list|,
name|T2
name|v2
parameter_list|)
block|{
return|return
name|function
operator|.
name|apply
argument_list|(
name|v1
argument_list|,
name|v2
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Converts a 1-parameter function to a predicate.    */
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Predicate1
argument_list|<
name|T
argument_list|>
name|toPredicate
parameter_list|(
specifier|final
name|Function1
argument_list|<
name|T
argument_list|,
name|Boolean
argument_list|>
name|function
parameter_list|)
block|{
return|return
operator|new
name|Predicate1
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|T
name|v1
parameter_list|)
block|{
return|return
name|function
operator|.
name|apply
argument_list|(
name|v1
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns the appropriate interface for a lambda function with    * 1 argument and the given return type.    *    *<p>For example:</p>    * functionClass(Integer.TYPE) returns IntegerFunction1.class    * functionClass(String.class) returns Function1.class    *    * @param aClass Return type    *    * @return Function class    */
specifier|public
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|Function
argument_list|>
name|functionClass
parameter_list|(
name|Type
name|aClass
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|Function
argument_list|>
name|c
init|=
name|FUNCTION1_CLASSES
operator|.
name|get
argument_list|(
name|aClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
return|return
name|c
return|;
block|}
return|return
name|Function1
operator|.
name|class
return|;
block|}
comment|/**    * Adapts an {@link IntegerFunction1} (that returns an {@code int}) to    * an {@link Function1} returning an {@link Integer}.    */
specifier|public
specifier|static
parameter_list|<
name|T1
parameter_list|>
name|Function1
argument_list|<
name|T1
argument_list|,
name|Integer
argument_list|>
name|adapt
parameter_list|(
specifier|final
name|IntegerFunction1
argument_list|<
name|T1
argument_list|>
name|f
parameter_list|)
block|{
return|return
operator|new
name|Function1
argument_list|<
name|T1
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|apply
parameter_list|(
name|T1
name|a0
parameter_list|)
block|{
return|return
name|f
operator|.
name|apply
argument_list|(
name|a0
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Adapts a {@link DoubleFunction1} (that returns a {@code double}) to    * an {@link Function1} returning a {@link Double}.    */
specifier|public
specifier|static
parameter_list|<
name|T1
parameter_list|>
name|Function1
argument_list|<
name|T1
argument_list|,
name|Double
argument_list|>
name|adapt
parameter_list|(
specifier|final
name|DoubleFunction1
argument_list|<
name|T1
argument_list|>
name|f
parameter_list|)
block|{
return|return
operator|new
name|Function1
argument_list|<
name|T1
argument_list|,
name|Double
argument_list|>
argument_list|()
block|{
specifier|public
name|Double
name|apply
parameter_list|(
name|T1
name|a0
parameter_list|)
block|{
return|return
name|f
operator|.
name|apply
argument_list|(
name|a0
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Adapts a {@link LongFunction1} (that returns a {@code long}) to    * an {@link Function1} returning a {@link Long}.    */
specifier|public
specifier|static
parameter_list|<
name|T1
parameter_list|>
name|Function1
argument_list|<
name|T1
argument_list|,
name|Long
argument_list|>
name|adapt
parameter_list|(
specifier|final
name|LongFunction1
argument_list|<
name|T1
argument_list|>
name|f
parameter_list|)
block|{
return|return
operator|new
name|Function1
argument_list|<
name|T1
argument_list|,
name|Long
argument_list|>
argument_list|()
block|{
specifier|public
name|Long
name|apply
parameter_list|(
name|T1
name|a0
parameter_list|)
block|{
return|return
name|f
operator|.
name|apply
argument_list|(
name|a0
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Adapts a {@link FloatFunction1} (that returns a {@code float}) to    * an {@link Function1} returning a {@link Float}.    */
specifier|public
specifier|static
parameter_list|<
name|T1
parameter_list|>
name|Function1
argument_list|<
name|T1
argument_list|,
name|Float
argument_list|>
name|adapt
parameter_list|(
specifier|final
name|FloatFunction1
argument_list|<
name|T1
argument_list|>
name|f
parameter_list|)
block|{
return|return
operator|new
name|Function1
argument_list|<
name|T1
argument_list|,
name|Float
argument_list|>
argument_list|()
block|{
specifier|public
name|Float
name|apply
parameter_list|(
name|T1
name|a0
parameter_list|)
block|{
return|return
name|f
operator|.
name|apply
argument_list|(
name|a0
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Creates a view of a list that applies a function to each element.    *    * @deprecated Use {@link com.google.common.collect.Lists#transform}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
parameter_list|<
name|T1
parameter_list|,
name|R
parameter_list|>
name|List
argument_list|<
name|R
argument_list|>
name|adapt
parameter_list|(
specifier|final
name|List
argument_list|<
name|T1
argument_list|>
name|list
parameter_list|,
specifier|final
name|Function1
argument_list|<
name|T1
argument_list|,
name|R
argument_list|>
name|f
parameter_list|)
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|R
argument_list|>
argument_list|()
block|{
specifier|public
name|R
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|f
operator|.
name|apply
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|list
operator|.
name|size
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/**    * Creates a view of an array that applies a function to each element.    *    * @deprecated Use {@link com.google.common.collect.Lists#transform}    * and {@link Arrays#asList(Object[])}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|,
name|R
parameter_list|>
name|List
argument_list|<
name|R
argument_list|>
name|adapt
parameter_list|(
specifier|final
name|T
index|[]
name|ts
parameter_list|,
specifier|final
name|Function1
argument_list|<
name|T
argument_list|,
name|R
argument_list|>
name|f
parameter_list|)
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|R
argument_list|>
argument_list|()
block|{
specifier|public
name|R
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|f
operator|.
name|apply
argument_list|(
name|ts
index|[
name|index
index|]
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|ts
operator|.
name|length
return|;
block|}
block|}
return|;
block|}
comment|/**    * Creates a copy of a list, applying a function to each element.    */
specifier|public
specifier|static
parameter_list|<
name|T1
parameter_list|,
name|R
parameter_list|>
name|List
argument_list|<
name|R
argument_list|>
name|apply
parameter_list|(
specifier|final
name|List
argument_list|<
name|T1
argument_list|>
name|list
parameter_list|,
specifier|final
name|Function1
argument_list|<
name|T1
argument_list|,
name|R
argument_list|>
name|f
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|R
argument_list|>
name|list2
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|T1
name|t
range|:
name|list
control|)
block|{
name|list2
operator|.
name|add
argument_list|(
name|f
operator|.
name|apply
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list2
return|;
block|}
comment|/** Returns a list that contains only elements of {@code list} that match    * {@code predicate}. Avoids allocating a list if all elements match or no    * elements match. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
argument_list|<
name|E
argument_list|>
name|filter
parameter_list|(
name|List
argument_list|<
name|E
argument_list|>
name|list
parameter_list|,
name|Predicate1
argument_list|<
name|E
argument_list|>
name|predicate
parameter_list|)
block|{
name|sniff
label|:
block|{
name|int
name|hitCount
init|=
literal|0
decl_stmt|;
name|int
name|missCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|E
name|e
range|:
name|list
control|)
block|{
if|if
condition|(
name|predicate
operator|.
name|apply
argument_list|(
name|e
argument_list|)
condition|)
block|{
if|if
condition|(
name|missCount
operator|>
literal|0
condition|)
block|{
break|break
name|sniff
break|;
block|}
operator|++
name|hitCount
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|hitCount
operator|>
literal|0
condition|)
block|{
break|break
name|sniff
break|;
block|}
operator|++
name|missCount
expr_stmt|;
block|}
block|}
if|if
condition|(
name|hitCount
operator|==
literal|0
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
if|if
condition|(
name|missCount
operator|==
literal|0
condition|)
block|{
return|return
name|list
return|;
block|}
block|}
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|list2
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|E
name|e
range|:
name|list
control|)
block|{
if|if
condition|(
name|predicate
operator|.
name|apply
argument_list|(
name|e
argument_list|)
condition|)
block|{
name|list2
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list2
return|;
block|}
comment|/** Returns whether there is an element in {@code list} for which    * {@code predicate} is true. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|boolean
name|exists
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|E
argument_list|>
name|list
parameter_list|,
name|Predicate1
argument_list|<
name|E
argument_list|>
name|predicate
parameter_list|)
block|{
for|for
control|(
name|E
name|e
range|:
name|list
control|)
block|{
if|if
condition|(
name|predicate
operator|.
name|apply
argument_list|(
name|e
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
comment|/** Returns whether {@code predicate} is true for all elements of    * {@code list}. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|boolean
name|all
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|E
argument_list|>
name|list
parameter_list|,
name|Predicate1
argument_list|<
name|E
argument_list|>
name|predicate
parameter_list|)
block|{
for|for
control|(
name|E
name|e
range|:
name|list
control|)
block|{
if|if
condition|(
operator|!
name|predicate
operator|.
name|apply
argument_list|(
name|e
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
comment|/** Returns a list generated by applying a function to each index between    * 0 and {@code size} - 1. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
argument_list|<
name|E
argument_list|>
name|generate
parameter_list|(
specifier|final
name|int
name|size
parameter_list|,
specifier|final
name|Function1
argument_list|<
name|Integer
argument_list|,
name|E
argument_list|>
name|fn
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
return|return
operator|new
name|AbstractList
argument_list|<
name|E
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
specifier|public
name|E
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|fn
operator|.
name|apply
argument_list|(
name|index
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns a function of arity 0 that does nothing.    *    * @param<R> Return type    * @return Function that does nothing.    */
specifier|public
specifier|static
parameter_list|<
name|R
parameter_list|>
name|Function0
argument_list|<
name|R
argument_list|>
name|ignore0
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
name|Ignore
operator|.
name|INSTANCE
return|;
block|}
comment|/**    * Returns a function of arity 1 that does nothing.    *    * @param<R> Return type    * @param<T0> Type of parameter 0    * @return Function that does nothing.    */
specifier|public
specifier|static
parameter_list|<
name|R
parameter_list|,
name|T0
parameter_list|>
name|Function1
argument_list|<
name|R
argument_list|,
name|T0
argument_list|>
name|ignore1
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
name|Ignore
operator|.
name|INSTANCE
return|;
block|}
comment|/**    * Returns a function of arity 2 that does nothing.    *    * @param<R> Return type    * @param<T0> Type of parameter 0    * @param<T1> Type of parameter 1    * @return Function that does nothing.    */
specifier|public
specifier|static
parameter_list|<
name|R
parameter_list|,
name|T0
parameter_list|,
name|T1
parameter_list|>
name|Function2
argument_list|<
name|R
argument_list|,
name|T0
argument_list|,
name|T1
argument_list|>
name|ignore2
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
name|Ignore
operator|.
name|INSTANCE
return|;
block|}
comment|/**    * Returns a {@link Comparator} that handles null values.    *    * @param nullsFirst Whether nulls come before all other values    * @param reverse Whether to reverse the usual order of {@link Comparable}s    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|T
argument_list|>
parameter_list|>
name|Comparator
argument_list|<
name|T
argument_list|>
name|nullsComparator
parameter_list|(
name|boolean
name|nullsFirst
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
return|return
operator|(
name|Comparator
argument_list|<
name|T
argument_list|>
operator|)
operator|(
name|reverse
condition|?
operator|(
name|nullsFirst
condition|?
name|NULLS_FIRST_REVERSE_COMPARATOR
else|:
name|NULLS_LAST_REVERSE_COMPARATOR
operator|)
else|:
operator|(
name|nullsFirst
condition|?
name|NULLS_FIRST_COMPARATOR
else|:
name|NULLS_LAST_COMPARATOR
operator|)
operator|)
return|;
block|}
comment|/**    * Returns an {@link EqualityComparer} that uses object identity and hash    * code.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|EqualityComparer
argument_list|<
name|T
argument_list|>
name|identityComparer
parameter_list|()
block|{
return|return
operator|(
name|EqualityComparer
operator|)
name|IDENTITY_COMPARER
return|;
block|}
comment|/**    * Returns an {@link EqualityComparer} that works on arrays of objects.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|EqualityComparer
argument_list|<
name|T
index|[]
argument_list|>
name|arrayComparer
parameter_list|()
block|{
return|return
operator|(
name|EqualityComparer
operator|)
name|ARRAY_COMPARER
return|;
block|}
comment|/**    * Returns an {@link EqualityComparer} that uses a selector function.    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|,
name|T2
parameter_list|>
name|EqualityComparer
argument_list|<
name|T
argument_list|>
name|selectorComparer
parameter_list|(
name|Function1
argument_list|<
name|T
argument_list|,
name|T2
argument_list|>
name|selector
parameter_list|)
block|{
return|return
operator|new
name|SelectorEqualityComparer
argument_list|<>
argument_list|(
name|selector
argument_list|)
return|;
block|}
comment|/** Array equality comparer. */
specifier|private
specifier|static
class|class
name|ArrayEqualityComparer
implements|implements
name|EqualityComparer
argument_list|<
name|Object
index|[]
argument_list|>
block|{
specifier|public
name|boolean
name|equal
parameter_list|(
name|Object
index|[]
name|v1
parameter_list|,
name|Object
index|[]
name|v2
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|v1
argument_list|,
name|v2
argument_list|)
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|(
name|Object
index|[]
name|t
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|hashCode
argument_list|(
name|t
argument_list|)
return|;
block|}
block|}
comment|/** Identity equality comparer. */
specifier|private
specifier|static
class|class
name|IdentityEqualityComparer
implements|implements
name|EqualityComparer
argument_list|<
name|Object
argument_list|>
block|{
specifier|public
name|boolean
name|equal
parameter_list|(
name|Object
name|v1
parameter_list|,
name|Object
name|v2
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|v1
argument_list|,
name|v2
argument_list|)
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|(
name|Object
name|t
parameter_list|)
block|{
return|return
name|t
operator|==
literal|null
condition|?
literal|0x789d
else|:
name|t
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
comment|/** Selector equality comparer. */
specifier|private
specifier|static
specifier|final
class|class
name|SelectorEqualityComparer
parameter_list|<
name|T
parameter_list|,
name|T2
parameter_list|>
implements|implements
name|EqualityComparer
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|Function1
argument_list|<
name|T
argument_list|,
name|T2
argument_list|>
name|selector
decl_stmt|;
name|SelectorEqualityComparer
parameter_list|(
name|Function1
argument_list|<
name|T
argument_list|,
name|T2
argument_list|>
name|selector
parameter_list|)
block|{
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
block|}
specifier|public
name|boolean
name|equal
parameter_list|(
name|T
name|v1
parameter_list|,
name|T
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|==
name|v2
operator|||
name|v1
operator|!=
literal|null
operator|&&
name|v2
operator|!=
literal|null
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|selector
operator|.
name|apply
argument_list|(
name|v1
argument_list|)
argument_list|,
name|selector
operator|.
name|apply
argument_list|(
name|v2
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|(
name|T
name|t
parameter_list|)
block|{
return|return
name|t
operator|==
literal|null
condition|?
literal|0x789d
else|:
name|selector
operator|.
name|apply
argument_list|(
name|t
argument_list|)
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
comment|/** Nulls first comparator. */
specifier|private
specifier|static
class|class
name|NullsFirstComparator
implements|implements
name|Comparator
argument_list|<
name|Comparable
argument_list|>
implements|,
name|Serializable
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Comparable
name|o1
parameter_list|,
name|Comparable
name|o2
parameter_list|)
block|{
if|if
condition|(
name|o1
operator|==
name|o2
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|o1
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|o2
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
comment|//noinspection unchecked
return|return
name|o1
operator|.
name|compareTo
argument_list|(
name|o2
argument_list|)
return|;
block|}
block|}
comment|/** Nulls last comparator. */
specifier|private
specifier|static
class|class
name|NullsLastComparator
implements|implements
name|Comparator
argument_list|<
name|Comparable
argument_list|>
implements|,
name|Serializable
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Comparable
name|o1
parameter_list|,
name|Comparable
name|o2
parameter_list|)
block|{
if|if
condition|(
name|o1
operator|==
name|o2
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|o1
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|o2
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|//noinspection unchecked
return|return
name|o1
operator|.
name|compareTo
argument_list|(
name|o2
argument_list|)
return|;
block|}
block|}
comment|/** Nulls first reverse comparator. */
specifier|private
specifier|static
class|class
name|NullsFirstReverseComparator
implements|implements
name|Comparator
argument_list|<
name|Comparable
argument_list|>
implements|,
name|Serializable
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Comparable
name|o1
parameter_list|,
name|Comparable
name|o2
parameter_list|)
block|{
if|if
condition|(
name|o1
operator|==
name|o2
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|o1
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|o2
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
comment|//noinspection unchecked
return|return
operator|-
name|o1
operator|.
name|compareTo
argument_list|(
name|o2
argument_list|)
return|;
block|}
block|}
comment|/** Nulls last reverse comparator. */
specifier|private
specifier|static
class|class
name|NullsLastReverseComparator
implements|implements
name|Comparator
argument_list|<
name|Comparable
argument_list|>
implements|,
name|Serializable
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Comparable
name|o1
parameter_list|,
name|Comparable
name|o2
parameter_list|)
block|{
if|if
condition|(
name|o1
operator|==
name|o2
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|o1
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|o2
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|//noinspection unchecked
return|return
operator|-
name|o1
operator|.
name|compareTo
argument_list|(
name|o2
argument_list|)
return|;
block|}
block|}
comment|/** Ignore. */
specifier|private
specifier|static
specifier|final
class|class
name|Ignore
parameter_list|<
name|R
parameter_list|,
name|T0
parameter_list|,
name|T1
parameter_list|>
implements|implements
name|Function0
argument_list|<
name|R
argument_list|>
implements|,
name|Function1
argument_list|<
name|T0
argument_list|,
name|R
argument_list|>
implements|,
name|Function2
argument_list|<
name|T0
argument_list|,
name|T1
argument_list|,
name|R
argument_list|>
block|{
specifier|public
name|R
name|apply
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|R
name|apply
parameter_list|(
name|T0
name|p0
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|R
name|apply
parameter_list|(
name|T0
name|p0
parameter_list|,
name|T1
name|p1
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|static
specifier|final
name|Ignore
name|INSTANCE
init|=
operator|new
name|Ignore
argument_list|()
decl_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End Functions.java
end_comment

end_unit

