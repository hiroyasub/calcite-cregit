begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
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
name|*
import|;
end_import

begin_comment
comment|/**  * Utilities relating to functions.  */
end_comment

begin_class
specifier|public
class|class
name|Functions
block|{
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
name|Function1
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
name|EqualityComparer
argument_list|<
name|Object
argument_list|>
name|IDENTITY_COMPARER
init|=
operator|new
name|EqualityComparer
argument_list|<
name|Object
argument_list|>
argument_list|()
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
name|EqualityComparer
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
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
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
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
argument_list|<
name|V
argument_list|,
name|K
argument_list|>
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
comment|/**      * A predicate with one parameter that always returns {@code true}.      *      * @param<T> First parameter type      * @return Predicate that always returns true      */
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
comment|/**      * A predicate with one parameter that always returns {@code true}.      *      * @param<T> First parameter type      * @return Predicate that always returns true      */
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
comment|/**      * A predicate with two parameters that always returns {@code true}.      *      * @param<T1> First parameter type      * @param<T2> Second parameter type      * @return Predicate that always returns true      */
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
comment|/**      * A predicate with two parameters that always returns {@code false}.      *      * @param<T1> First parameter type      * @param<T2> Second parameter type      * @return Predicate that always returns false      */
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
comment|/**      * Creates a predicate that returns whether an object is an instance of a      * particular type or is null.      *      * @param clazz Desired type      * @param<T> Type of objects to test      * @param<T2> Desired type      * @return Predicate that tests for desired type      */
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
comment|/**      * Converts a 2-parameter function to a predicate.      */
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
comment|/**      * Converts a 1-parameter function to a predicate.      */
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
comment|/**      * Returns the appropriate interface for a lambda function with      * 1 argument and the given return type.      *      *<p>For example:</p>      * functionClass(Integer.TYPE) returns IntegerFunction1.class      * functionClass(String.class) returns Function1.class      *      * @param aClass Return type      * @return Function class      */
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
name|Class
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
comment|/** Adapts an {@link IntegerFunction1} (that returns an {@code int}) to      * an {@link Function1} returning an {@link Integer}. */
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
comment|/** Adapts a {@link DoubleFunction1} (that returns a {@code double}) to      * an {@link Function1} returning a {@link Double}. */
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
comment|/** Adapts a {@link LongFunction1} (that returns a {@code long}) to      * an {@link Function1} returning a {@link Long}. */
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
comment|/** Adapts a {@link FloatFunction1} (that returns a {@code float}) to      * an {@link Function1} returning a {@link Float}. */
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
comment|/** Returns an {@link EqualityComparer} that uses object identity and hash      * code. */
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
comment|/** Returns an {@link EqualityComparer} that works on arrays of objects. */
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
block|}
end_class

begin_comment
comment|// End Functions.java
end_comment

end_unit

