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
name|linq4j
operator|.
name|function
operator|.
name|Function1
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
name|Method
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
name|Collection
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|RandomAccess
import|;
end_import

begin_comment
comment|/**  * Utility and factory methods for Linq4j.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Linq4j
block|{
specifier|private
name|Linq4j
parameter_list|()
block|{
block|}
specifier|private
specifier|static
specifier|final
name|Object
name|DUMMY
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|Method
name|getMethod
parameter_list|(
name|String
name|className
parameter_list|,
name|String
name|methodName
parameter_list|,
name|Class
modifier|...
name|parameterTypes
parameter_list|)
block|{
try|try
block|{
return|return
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
operator|.
name|getMethod
argument_list|(
name|methodName
argument_list|,
name|parameterTypes
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Query provider that simply executes a {@link Queryable} by calling its    * enumerator method; does not attempt optimization.    */
specifier|public
specifier|static
specifier|final
name|QueryProvider
name|DEFAULT_PROVIDER
init|=
operator|new
name|QueryProviderImpl
argument_list|()
block|{
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Enumerator
argument_list|<
name|T
argument_list|>
name|executeQuery
parameter_list|(
name|Queryable
argument_list|<
name|T
argument_list|>
name|queryable
parameter_list|)
block|{
return|return
name|queryable
operator|.
name|enumerator
argument_list|()
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Enumerator
argument_list|<
name|Object
argument_list|>
name|EMPTY_ENUMERATOR
init|=
operator|new
name|Enumerator
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|current
parameter_list|()
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Enumerable
argument_list|<
name|?
argument_list|>
name|EMPTY_ENUMERABLE
init|=
operator|new
name|AbstractEnumerable
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerator
argument_list|<
name|Object
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
name|EMPTY_ENUMERATOR
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Adapter that converts an enumerator into an iterator.    *    *<p><b>WARNING</b>: The iterator returned by this method does not call    * {@link org.apache.calcite.linq4j.Enumerator#close()}, so it is not safe to    * use with an enumerator that allocates resources.</p>    *    * @param enumerator Enumerator    * @param<T> Element type    *    * @return Iterator    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Iterator
argument_list|<
name|T
argument_list|>
name|enumeratorIterator
parameter_list|(
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|)
block|{
return|return
operator|new
name|EnumeratorIterator
argument_list|<>
argument_list|(
name|enumerator
argument_list|)
return|;
block|}
comment|/**    * Adapter that converts an iterable into an enumerator.    *    * @param iterable Iterable    * @param<T> Element type    *    * @return enumerator    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Enumerator
argument_list|<
name|T
argument_list|>
name|iterableEnumerator
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|iterable
parameter_list|)
block|{
if|if
condition|(
name|iterable
operator|instanceof
name|Enumerable
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Enumerable
argument_list|<
name|T
argument_list|>
name|enumerable
init|=
operator|(
name|Enumerable
operator|)
name|iterable
decl_stmt|;
return|return
name|enumerable
operator|.
name|enumerator
argument_list|()
return|;
block|}
return|return
operator|new
name|IterableEnumerator
argument_list|<>
argument_list|(
name|iterable
argument_list|)
return|;
block|}
comment|/**    * Adapter that converts an {@link List} into an {@link Enumerable}.    *    * @param list List    * @param<T> Element type    *    * @return enumerable    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Enumerable
argument_list|<
name|T
argument_list|>
name|asEnumerable
parameter_list|(
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|)
block|{
return|return
operator|new
name|ListEnumerable
argument_list|<>
argument_list|(
name|list
argument_list|)
return|;
block|}
comment|/**    * Adapter that converts an {@link Collection} into an {@link Enumerable}.    *    *<p>It uses more efficient implementations if the iterable happens to    * be a {@link List}.</p>    *    * @param collection Collection    * @param<T> Element type    *    * @return enumerable    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Enumerable
argument_list|<
name|T
argument_list|>
name|asEnumerable
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|instanceof
name|List
condition|)
block|{
comment|//noinspection unchecked
return|return
name|asEnumerable
argument_list|(
operator|(
name|List
operator|)
name|collection
argument_list|)
return|;
block|}
return|return
operator|new
name|CollectionEnumerable
argument_list|<>
argument_list|(
name|collection
argument_list|)
return|;
block|}
comment|/**    * Adapter that converts an {@link Iterable} into an {@link Enumerable}.    *    *<p>It uses more efficient implementations if the iterable happens to    * be a {@link Collection} or a {@link List}.</p>    *    * @param iterable Iterable    * @param<T> Element type    *    * @return enumerable    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Enumerable
argument_list|<
name|T
argument_list|>
name|asEnumerable
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|T
argument_list|>
name|iterable
parameter_list|)
block|{
if|if
condition|(
name|iterable
operator|instanceof
name|Collection
condition|)
block|{
comment|//noinspection unchecked
return|return
name|asEnumerable
argument_list|(
operator|(
name|Collection
operator|)
name|iterable
argument_list|)
return|;
block|}
return|return
operator|new
name|IterableEnumerable
argument_list|<>
argument_list|(
name|iterable
argument_list|)
return|;
block|}
comment|/**    * Adapter that converts an array into an enumerable.    *    * @param ts Array    * @param<T> Element type    *    * @return enumerable    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Enumerable
argument_list|<
name|T
argument_list|>
name|asEnumerable
parameter_list|(
specifier|final
name|T
index|[]
name|ts
parameter_list|)
block|{
return|return
operator|new
name|ListEnumerable
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|ts
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Adapter that converts a collection into an enumerator.    *    * @param values Collection    * @param<V> Element type    *    * @return Enumerator over the collection    */
specifier|public
specifier|static
parameter_list|<
name|V
parameter_list|>
name|Enumerator
argument_list|<
name|V
argument_list|>
name|enumerator
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|V
argument_list|>
name|values
parameter_list|)
block|{
if|if
condition|(
name|values
operator|instanceof
name|List
operator|&&
name|values
operator|instanceof
name|RandomAccess
condition|)
block|{
comment|//noinspection unchecked
return|return
name|listEnumerator
argument_list|(
operator|(
name|List
operator|)
name|values
argument_list|)
return|;
block|}
return|return
name|iterableEnumerator
argument_list|(
name|values
argument_list|)
return|;
block|}
specifier|private
specifier|static
parameter_list|<
name|V
parameter_list|>
name|Enumerator
argument_list|<
name|V
argument_list|>
name|listEnumerator
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|V
argument_list|>
name|list
parameter_list|)
block|{
return|return
operator|new
name|ListEnumerator
argument_list|<>
argument_list|(
name|list
argument_list|)
return|;
block|}
comment|/** Applies a function to each element of an Enumerator.    *    * @param enumerator Backing enumerator    * @param func Transform function    * @param<F> Backing element type    * @param<E> Element type    * @return Enumerator    */
specifier|public
specifier|static
parameter_list|<
name|F
parameter_list|,
name|E
parameter_list|>
name|Enumerator
argument_list|<
name|E
argument_list|>
name|transform
parameter_list|(
name|Enumerator
argument_list|<
name|F
argument_list|>
name|enumerator
parameter_list|,
specifier|final
name|Function1
argument_list|<
name|F
argument_list|,
name|E
argument_list|>
name|func
parameter_list|)
block|{
return|return
operator|new
name|TransformedEnumerator
argument_list|<
name|F
argument_list|,
name|E
argument_list|>
argument_list|(
name|enumerator
argument_list|)
block|{
specifier|protected
name|E
name|transform
parameter_list|(
name|F
name|from
parameter_list|)
block|{
return|return
name|func
operator|.
name|apply
argument_list|(
name|from
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Converts the elements of a given Iterable to the specified type.    *    *<p>This method is implemented by using deferred execution. The immediate    * return value is an object that stores all the information that is    * required to perform the action. The query represented by this method is    * not executed until the object is enumerated either by calling its    * {@link Enumerable#enumerator} method directly or by using    * {@code for (... in ...)}.    *    *<p>Since standard Java {@link Collection} objects implement the    * {@link Iterable} interface, the {@code cast} method enables the standard    * query operators to be invoked on collections    * (including {@link java.util.List} and {@link java.util.Set}) by supplying    * the necessary type information. For example, {@link ArrayList} does not    * implement {@link Enumerable}&lt;F&gt;, but you can invoke    *    *<blockquote><code>Linq4j.cast(list, Integer.class)</code></blockquote>    *    *<p>to convert the list of an enumerable that can be queried using the    * standard query operators.    *    *<p>If an element cannot be cast to type&lt;TResult&gt;, this method will    * throw a {@link ClassCastException}. To obtain only those elements that    * can be cast to type TResult, use the {@link #ofType} method instead.    *    * @see Enumerable#cast(Class)    * @see #ofType    * @see #asEnumerable(Iterable)    */
specifier|public
specifier|static
parameter_list|<
name|TSource
parameter_list|,
name|TResult
parameter_list|>
name|Enumerable
argument_list|<
name|TResult
argument_list|>
name|cast
parameter_list|(
name|Iterable
argument_list|<
name|TSource
argument_list|>
name|source
parameter_list|,
name|Class
argument_list|<
name|TResult
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
name|asEnumerable
argument_list|(
name|source
argument_list|)
operator|.
name|cast
argument_list|(
name|clazz
argument_list|)
return|;
block|}
comment|/**    * Returns elements of a given {@link Iterable} that are of the specified    * type.    *    *<p>This method is implemented by using deferred execution. The immediate    * return value is an object that stores all the information that is    * required to perform the action. The query represented by this method is    * not executed until the object is enumerated either by calling its    * {@link Enumerable#enumerator} method directly or by using    * {@code for (... in ...)}.    *    *<p>The {@code ofType} method returns only those elements in source that    * can be cast to type TResult. To instead receive an exception if an    * element cannot be cast to type TResult, use    * {@link #cast(Iterable, Class)}.</p>    *    *<p>Since standard Java {@link Collection} objects implement the    * {@link Iterable} interface, the {@code cast} method enables the standard    * query operators to be invoked on collections    * (including {@link java.util.List} and {@link java.util.Set}) by supplying    * the necessary type information. For example, {@link ArrayList} does not    * implement {@link Enumerable}&lt;F&gt;, but you can invoke    *    *<blockquote><code>Linq4j.ofType(list, Integer.class)</code></blockquote>    *    *<p>to convert the list of an enumerable that can be queried using the    * standard query operators.    *    * @see Enumerable#cast(Class)    * @see #cast    */
specifier|public
specifier|static
parameter_list|<
name|TSource
parameter_list|,
name|TResult
parameter_list|>
name|Enumerable
argument_list|<
name|TResult
argument_list|>
name|ofType
parameter_list|(
name|Iterable
argument_list|<
name|TSource
argument_list|>
name|source
parameter_list|,
name|Class
argument_list|<
name|TResult
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
name|asEnumerable
argument_list|(
name|source
argument_list|)
operator|.
name|ofType
argument_list|(
name|clazz
argument_list|)
return|;
block|}
comment|/**    * Returns an {@link Enumerable} that has one element.    *    * @param<T> Element type    *    * @return Singleton enumerable    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Enumerable
argument_list|<
name|T
argument_list|>
name|singletonEnumerable
parameter_list|(
specifier|final
name|T
name|element
parameter_list|)
block|{
return|return
operator|new
name|AbstractEnumerable
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
name|singletonEnumerator
argument_list|(
name|element
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns an {@link Enumerator} that has one element.    *    * @param<T> Element type    *    * @return Singleton enumerator    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Enumerator
argument_list|<
name|T
argument_list|>
name|singletonEnumerator
parameter_list|(
name|T
name|element
parameter_list|)
block|{
return|return
operator|new
name|SingletonEnumerator
argument_list|<>
argument_list|(
name|element
argument_list|)
return|;
block|}
comment|/**    * Returns an {@link Enumerator} that has one null element.    *    * @param<T> Element type    *    * @return Singleton enumerator    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Enumerator
argument_list|<
name|T
argument_list|>
name|singletonNullEnumerator
parameter_list|()
block|{
return|return
operator|new
name|SingletonNullEnumerator
argument_list|<>
argument_list|()
return|;
block|}
comment|/**    * Returns an {@link Enumerable} that has no elements.    *    * @param<T> Element type    *    * @return Empty enumerable    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Enumerable
argument_list|<
name|T
argument_list|>
name|emptyEnumerable
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
operator|(
name|Enumerable
argument_list|<
name|T
argument_list|>
operator|)
name|EMPTY_ENUMERABLE
return|;
block|}
comment|/**    * Returns an {@link Enumerator} that has no elements.    *    * @param<T> Element type    *    * @return Empty enumerator    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Enumerator
argument_list|<
name|T
argument_list|>
name|emptyEnumerator
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
operator|(
name|Enumerator
argument_list|<
name|T
argument_list|>
operator|)
name|EMPTY_ENUMERATOR
return|;
block|}
comment|/**    * Concatenates two or more {@link Enumerable}s to form a composite    * enumerable that contains the union of their elements.    *    * @param enumerableList List of enumerable objects    * @param<E> Element type    *    * @return Composite enumerator    */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Enumerable
argument_list|<
name|E
argument_list|>
name|concat
parameter_list|(
specifier|final
name|List
argument_list|<
name|Enumerable
argument_list|<
name|E
argument_list|>
argument_list|>
name|enumerableList
parameter_list|)
block|{
return|return
operator|new
name|CompositeEnumerable
argument_list|<>
argument_list|(
name|enumerableList
argument_list|)
return|;
block|}
comment|/**    * Returns an enumerator that is the cartesian product of the given    * enumerators.    *    *<p>For example, given enumerator A that returns {"a", "b", "c"} and    * enumerator B that returns {"x", "y"}, product(List(A, B)) will return    * {List("a", "x"), List("a", "y"),    * List("b", "x"), List("b", "y"),    * List("c", "x"), List("c", "y")}.</p>    *    *<p>Notice that the cardinality of the result is the product of the    * cardinality of the inputs. The enumerators A and B have 3 and 2    * elements respectively, and the result has 3 * 2 = 6 elements.    * This is always the case. In    * particular, if any of the enumerators is empty, the result is empty.</p>    *    * @param enumerators List of enumerators    * @param<T> Element type    *    * @return Enumerator over the cartesian product    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Enumerator
argument_list|<
name|List
argument_list|<
name|T
argument_list|>
argument_list|>
name|product
parameter_list|(
name|List
argument_list|<
name|Enumerator
argument_list|<
name|T
argument_list|>
argument_list|>
name|enumerators
parameter_list|)
block|{
return|return
operator|new
name|CartesianProductListEnumerator
argument_list|<>
argument_list|(
name|enumerators
argument_list|)
return|;
block|}
comment|/** Returns the cartesian product of an iterable of iterables. */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Iterable
argument_list|<
name|List
argument_list|<
name|T
argument_list|>
argument_list|>
name|product
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|Iterable
argument_list|<
name|T
argument_list|>
argument_list|>
name|iterables
parameter_list|)
block|{
return|return
parameter_list|()
lambda|->
block|{
specifier|final
name|List
argument_list|<
name|Enumerator
argument_list|<
name|T
argument_list|>
argument_list|>
name|enumerators
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterable
argument_list|<
name|T
argument_list|>
name|iterable
range|:
name|iterables
control|)
block|{
name|enumerators
operator|.
name|add
argument_list|(
name|iterableEnumerator
argument_list|(
name|iterable
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|enumeratorIterator
argument_list|(
operator|new
name|CartesianProductListEnumerator
argument_list|<>
argument_list|(
name|enumerators
argument_list|)
argument_list|)
return|;
block|}
return|;
block|}
comment|/**    * Returns whether the arguments are equal to each other.    *    *<p>Equivalent to {@link java.util.Objects#equals} in JDK 1.7 and above.    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|boolean
name|equals
parameter_list|(
name|T
name|t0
parameter_list|,
name|T
name|t1
parameter_list|)
block|{
return|return
name|t0
operator|==
name|t1
operator|||
name|t0
operator|!=
literal|null
operator|&&
name|t0
operator|.
name|equals
argument_list|(
name|t1
argument_list|)
return|;
block|}
comment|/**    * Throws {@link NullPointerException} if argument is null, otherwise    * returns argument.    *    *<p>Equivalent to {@link java.util.Objects#requireNonNull} in JDK 1.7 and    * above.    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|requireNonNull
parameter_list|(
name|T
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
return|return
name|o
return|;
block|}
comment|/** Closes an iterator, if it can be closed. */
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|closeIterator
parameter_list|(
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|)
block|{
if|if
condition|(
name|iterator
operator|instanceof
name|AutoCloseable
condition|)
block|{
try|try
block|{
operator|(
operator|(
name|AutoCloseable
operator|)
name|iterator
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** Iterable enumerator.    *    * @param<T> element type */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|static
class|class
name|IterableEnumerator
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Enumerator
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|iterable
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|iterator
decl_stmt|;
name|T
name|current
decl_stmt|;
name|IterableEnumerator
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|iterable
parameter_list|)
block|{
name|this
operator|.
name|iterable
operator|=
name|iterable
expr_stmt|;
name|iterator
operator|=
name|iterable
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|current
operator|=
operator|(
name|T
operator|)
name|DUMMY
expr_stmt|;
block|}
specifier|public
name|T
name|current
parameter_list|()
block|{
if|if
condition|(
name|current
operator|==
name|DUMMY
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
return|return
name|current
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|current
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
name|current
operator|=
operator|(
name|T
operator|)
name|DUMMY
expr_stmt|;
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|iterator
operator|=
name|iterable
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|current
operator|=
operator|(
name|T
operator|)
name|DUMMY
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|iterator1
init|=
name|this
operator|.
name|iterator
decl_stmt|;
name|this
operator|.
name|iterator
operator|=
literal|null
expr_stmt|;
name|closeIterator
argument_list|(
name|iterator1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Composite enumerable.    *    * @param<E> element type */
specifier|static
class|class
name|CompositeEnumerable
parameter_list|<
name|E
parameter_list|>
extends|extends
name|AbstractEnumerable
argument_list|<
name|E
argument_list|>
block|{
specifier|private
specifier|final
name|Enumerator
argument_list|<
name|Enumerable
argument_list|<
name|E
argument_list|>
argument_list|>
name|enumerableEnumerator
decl_stmt|;
name|CompositeEnumerable
parameter_list|(
name|List
argument_list|<
name|Enumerable
argument_list|<
name|E
argument_list|>
argument_list|>
name|enumerableList
parameter_list|)
block|{
name|enumerableEnumerator
operator|=
name|iterableEnumerator
argument_list|(
name|enumerableList
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Enumerator
argument_list|<
name|E
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
operator|new
name|Enumerator
argument_list|<
name|E
argument_list|>
argument_list|()
block|{
comment|// Never null.
name|Enumerator
argument_list|<
name|E
argument_list|>
name|current
init|=
name|emptyEnumerator
argument_list|()
decl_stmt|;
specifier|public
name|E
name|current
parameter_list|()
block|{
return|return
name|current
operator|.
name|current
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|current
operator|.
name|moveNext
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|current
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|enumerableEnumerator
operator|.
name|moveNext
argument_list|()
condition|)
block|{
name|current
operator|=
name|emptyEnumerator
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
name|current
operator|=
name|enumerableEnumerator
operator|.
name|current
argument_list|()
operator|.
name|enumerator
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|enumerableEnumerator
operator|.
name|reset
argument_list|()
expr_stmt|;
name|current
operator|=
name|emptyEnumerator
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|current
operator|.
name|close
argument_list|()
expr_stmt|;
name|current
operator|=
name|emptyEnumerator
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
comment|/** Iterable enumerable.    *    * @param<T> element type */
specifier|static
class|class
name|IterableEnumerable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractEnumerable2
argument_list|<
name|T
argument_list|>
block|{
specifier|protected
specifier|final
name|Iterable
argument_list|<
name|T
argument_list|>
name|iterable
decl_stmt|;
name|IterableEnumerable
parameter_list|(
name|Iterable
argument_list|<
name|T
argument_list|>
name|iterable
parameter_list|)
block|{
name|this
operator|.
name|iterable
operator|=
name|iterable
expr_stmt|;
block|}
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|iterable
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|any
parameter_list|()
block|{
return|return
name|iterable
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
return|;
block|}
block|}
comment|/** Collection enumerable.    *    * @param<T> element type */
specifier|static
class|class
name|CollectionEnumerable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|IterableEnumerable
argument_list|<
name|T
argument_list|>
block|{
name|CollectionEnumerable
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|iterable
parameter_list|)
block|{
name|super
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Collection
argument_list|<
name|T
argument_list|>
name|getCollection
parameter_list|()
block|{
return|return
operator|(
name|Collection
argument_list|<
name|T
argument_list|>
operator|)
name|iterable
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|getCollection
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|longCount
parameter_list|()
block|{
return|return
name|getCollection
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|contains
parameter_list|(
name|T
name|element
parameter_list|)
block|{
return|return
name|getCollection
argument_list|()
operator|.
name|contains
argument_list|(
name|element
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|any
parameter_list|()
block|{
return|return
operator|!
name|getCollection
argument_list|()
operator|.
name|isEmpty
argument_list|()
return|;
block|}
block|}
comment|/** List enumerable.    *    * @param<T> element type */
specifier|static
class|class
name|ListEnumerable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|CollectionEnumerable
argument_list|<
name|T
argument_list|>
block|{
name|ListEnumerable
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|list
parameter_list|)
block|{
name|super
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
if|if
condition|(
name|iterable
operator|instanceof
name|RandomAccess
condition|)
block|{
comment|//noinspection unchecked
return|return
operator|new
name|ListEnumerator
argument_list|<>
argument_list|(
operator|(
name|List
operator|)
name|iterable
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|enumerator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|T
argument_list|>
name|toList
parameter_list|()
block|{
return|return
operator|(
name|List
argument_list|<
name|T
argument_list|>
operator|)
name|iterable
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumerable
argument_list|<
name|T
argument_list|>
name|skip
parameter_list|(
name|int
name|count
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|list
init|=
name|toList
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|>=
name|list
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
name|Linq4j
operator|.
name|emptyEnumerable
argument_list|()
return|;
block|}
return|return
operator|new
name|ListEnumerable
argument_list|<>
argument_list|(
name|list
operator|.
name|subList
argument_list|(
name|count
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumerable
argument_list|<
name|T
argument_list|>
name|take
parameter_list|(
name|int
name|count
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|list
init|=
name|toList
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|>=
name|list
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|ListEnumerable
argument_list|<>
argument_list|(
name|list
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|count
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|T
name|elementAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|toList
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
block|}
comment|/** Enumerator that returns one element.    *    * @param<E> element type */
specifier|private
specifier|static
class|class
name|SingletonEnumerator
parameter_list|<
name|E
parameter_list|>
implements|implements
name|Enumerator
argument_list|<
name|E
argument_list|>
block|{
specifier|final
name|E
name|e
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|SingletonEnumerator
parameter_list|(
name|E
name|e
parameter_list|)
block|{
name|this
operator|.
name|e
operator|=
name|e
expr_stmt|;
block|}
specifier|public
name|E
name|current
parameter_list|()
block|{
return|return
name|e
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
return|return
name|i
operator|++
operator|==
literal|0
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|i
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
block|}
block|}
comment|/** Enumerator that returns one null element.    *    * @param<E> element type */
specifier|private
specifier|static
class|class
name|SingletonNullEnumerator
parameter_list|<
name|E
parameter_list|>
implements|implements
name|Enumerator
argument_list|<
name|E
argument_list|>
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|public
name|E
name|current
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
return|return
name|i
operator|++
operator|==
literal|0
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|i
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
block|}
block|}
comment|/** Iterator that reads from an underlying {@link Enumerator}.    *    * @param<T> element type */
specifier|private
specifier|static
class|class
name|EnumeratorIterator
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Iterator
argument_list|<
name|T
argument_list|>
implements|,
name|AutoCloseable
block|{
specifier|private
specifier|final
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
decl_stmt|;
name|boolean
name|hasNext
decl_stmt|;
name|EnumeratorIterator
parameter_list|(
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|)
block|{
name|this
operator|.
name|enumerator
operator|=
name|enumerator
expr_stmt|;
name|hasNext
operator|=
name|enumerator
operator|.
name|moveNext
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|hasNext
return|;
block|}
specifier|public
name|T
name|next
parameter_list|()
block|{
name|T
name|t
init|=
name|enumerator
operator|.
name|current
argument_list|()
decl_stmt|;
name|hasNext
operator|=
name|enumerator
operator|.
name|moveNext
argument_list|()
expr_stmt|;
return|return
name|t
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
specifier|public
name|void
name|close
parameter_list|()
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Enumerator optimized for random-access list.    *    * @param<V> element type */
specifier|private
specifier|static
class|class
name|ListEnumerator
parameter_list|<
name|V
parameter_list|>
implements|implements
name|Enumerator
argument_list|<
name|V
argument_list|>
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|V
argument_list|>
name|list
decl_stmt|;
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
name|ListEnumerator
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|V
argument_list|>
name|list
parameter_list|)
block|{
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
block|}
specifier|public
name|V
name|current
parameter_list|()
block|{
return|return
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
return|return
operator|++
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|i
operator|=
operator|-
literal|1
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
block|}
block|}
comment|/** Enumerates over the cartesian product of the given lists, returning    * a list for each row.    *    * @param<E> element type */
specifier|private
specifier|static
class|class
name|CartesianProductListEnumerator
parameter_list|<
name|E
parameter_list|>
extends|extends
name|CartesianProductEnumerator
argument_list|<
name|E
argument_list|,
name|List
argument_list|<
name|E
argument_list|>
argument_list|>
block|{
name|CartesianProductListEnumerator
parameter_list|(
name|List
argument_list|<
name|Enumerator
argument_list|<
name|E
argument_list|>
argument_list|>
name|enumerators
parameter_list|)
block|{
name|super
argument_list|(
name|enumerators
argument_list|)
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|E
argument_list|>
name|current
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|elements
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End Linq4j.java
end_comment

end_unit

