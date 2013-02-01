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
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|function
operator|.
name|*
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
name|*
import|;
end_import

begin_comment
comment|/**  * Contains what, in LINQ.NET, would be extension methods.  *  *<h3>Notes on mapping from LINQ.NET to Java</h3>  *  *<p>We have preserved most of the API. But we've changed a few things, so that  * the API is more typical Java API:</p>  *  *<ul>  *  *<li>Java method names start with a lower-case letter.</li>  *  *<li>A few methods became keywords when their first letter was converted  * to lower case; hence  * {@link net.hydromatic.linq4j.expressions.Expressions#break_}</li>  *  *<li>We created a Java interface {@link Enumerable}, similar to LINQ.NET's  * IEnumerable. IEnumerable is built into C#, and that gives it  * advantages: the standard collections implement it, and you can use  * any IEnumerable in a foreach loop. We made the Java  * {@code Enumerable} extend {@link Iterable},  * so that it can be used in for-each loops. But the standard  * collections still don't implement it. A few methods that take an  * IEnumerable in LINQ.NET take an Iterable in LINQ4J.</li>  *  *<li>LINQ.NET's Dictionary interface maps to Map in Java;  * hence, the LINQ.NET {@code ToDictionary} methods become  * {@code toMap}.</li>  *  *<li>LINQ.NET's decimal type changes to BigDecimal. (A little bit unnatural,  * since decimal is primitive and BigDecimal is not.)</li>  *  *<li>There is no Nullable in Java. Therefore we distinguish between methods  * that return, say, Long (which may be null) and long. See for example  * {@link NullableLongFunction1} and {@link LongFunction1}, and the  * variants of {@link Enumerable#sum} that call them.  *  *<li>Java erases type parameters from argument types before resolving  * overloading. Therefore similar methods have the same erasure. Methods  * {@link ExtendedQueryable#averageDouble averageDouble},  * {@link ExtendedQueryable#averageInteger averageInteger},  * {@link ExtendedQueryable#groupByK groupByK},  * {@link ExtendedQueryable#selectN selectN},  * {@link ExtendedQueryable#selectManyN selectManyN},  * {@link ExtendedQueryable#skipWhileN skipWhileN},  * {@link ExtendedQueryable#sumBigDecimal sumBigDecimal},  * {@link ExtendedQueryable#sumNullableBigDecimal sumNullableBigDecimal},  * {@link ExtendedQueryable#whereN whereN}  * have been renamed from {@code average}, {@code groupBy}, {@code max},  * {@code min}, {@code select}, {@code selectMany}, {@code skipWhile} and  * {@code where} to prevent ambiguity.</li>  *  *<li>.NET allows<i>extension methods</i>&mdash; static methods that then  * become, via compiler magic, a method of any object whose type is the  * same as the first parameter of the extension method. In LINQ.NET, the  * {@code IQueryable} and {@code IEnumerable} interfaces have many such methods.  * In Java, those methods need to be explicitly added to the interface, and will  * need to be implemented by every class that implements that interface.  * We can help by implementing the methods as static methods, and by  * providing an abstract base class that implements the extension methods  * in the interface. Hence {@link AbstractEnumerable} and  * {@link AbstractQueryable} call methods in {@link Extensions}.</li>  *  *<li>.NET Func becomes {@link net.hydromatic.linq4j.function.Function0},  * {@link net.hydromatic.linq4j.function.Function1},  * {@link net.hydromatic.linq4j.function.Function2}, depending  * on the number of arguments to the function, because Java types cannot be  * overloaded based on the number of type parameters.</li>  *  *<li>Types map as follows:  * {@code Int32} => {@code int} or {@link Integer},  * {@code Int64} => {@code long} or {@link Long},  * {@code bool} => {@code boolean} or {@link Boolean},  * {@code Dictionary} => {@link Map},  * {@code Lookup} => {@link Map} whose value type is an {@link Iterable},  *</li>  *  *<li>Function types that accept primitive types in LINQ.NET have become  * boxed types in LINQ4J. For example, a predicate function  * {@code Func&lt;T, bool&gt;} becomes {@code Func1&lt;T, Boolean&gt;}.  * It would be wrong to infer that the function is allowed to return null.</li>  *  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|Extensions
block|{
specifier|static
specifier|final
name|Function2
argument_list|<
name|BigDecimal
argument_list|,
name|BigDecimal
argument_list|,
name|BigDecimal
argument_list|>
name|BIG_DECIMAL_SUM
init|=
operator|new
name|Function2
argument_list|<
name|BigDecimal
argument_list|,
name|BigDecimal
argument_list|,
name|BigDecimal
argument_list|>
argument_list|()
block|{
specifier|public
name|BigDecimal
name|apply
parameter_list|(
name|BigDecimal
name|v1
parameter_list|,
name|BigDecimal
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|.
name|add
argument_list|(
name|v2
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|static
specifier|final
name|Function2
argument_list|<
name|Float
argument_list|,
name|Float
argument_list|,
name|Float
argument_list|>
name|FLOAT_SUM
init|=
operator|new
name|Function2
argument_list|<
name|Float
argument_list|,
name|Float
argument_list|,
name|Float
argument_list|>
argument_list|()
block|{
specifier|public
name|Float
name|apply
parameter_list|(
name|Float
name|v1
parameter_list|,
name|Float
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|+
name|v2
return|;
block|}
block|}
decl_stmt|;
specifier|static
specifier|final
name|Function2
argument_list|<
name|Double
argument_list|,
name|Double
argument_list|,
name|Double
argument_list|>
name|DOUBLE_SUM
init|=
operator|new
name|Function2
argument_list|<
name|Double
argument_list|,
name|Double
argument_list|,
name|Double
argument_list|>
argument_list|()
block|{
specifier|public
name|Double
name|apply
parameter_list|(
name|Double
name|v1
parameter_list|,
name|Double
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|+
name|v2
return|;
block|}
block|}
decl_stmt|;
specifier|static
specifier|final
name|Function2
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|,
name|Integer
argument_list|>
name|INTEGER_SUM
init|=
operator|new
name|Function2
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|apply
parameter_list|(
name|Integer
name|v1
parameter_list|,
name|Integer
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|+
name|v2
return|;
block|}
block|}
decl_stmt|;
specifier|static
specifier|final
name|Function2
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|,
name|Long
argument_list|>
name|LONG_SUM
init|=
operator|new
name|Function2
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|,
name|Long
argument_list|>
argument_list|()
block|{
specifier|public
name|Long
name|apply
parameter_list|(
name|Long
name|v1
parameter_list|,
name|Long
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|+
name|v2
return|;
block|}
block|}
decl_stmt|;
specifier|static
specifier|final
name|Function2
name|COMPARABLE_MIN
init|=
operator|new
name|Function2
argument_list|<
name|Comparable
argument_list|,
name|Comparable
argument_list|,
name|Comparable
argument_list|>
argument_list|()
block|{
specifier|public
name|Comparable
name|apply
parameter_list|(
name|Comparable
name|v1
parameter_list|,
name|Comparable
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|==
literal|null
operator|||
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|>
literal|0
condition|?
name|v2
else|:
name|v1
return|;
block|}
block|}
decl_stmt|;
specifier|static
specifier|final
name|Function2
name|COMPARABLE_MAX
init|=
operator|new
name|Function2
argument_list|<
name|Comparable
argument_list|,
name|Comparable
argument_list|,
name|Comparable
argument_list|>
argument_list|()
block|{
specifier|public
name|Comparable
name|apply
parameter_list|(
name|Comparable
name|v1
parameter_list|,
name|Comparable
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|==
literal|null
operator|||
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|<
literal|0
condition|?
name|v2
else|:
name|v1
return|;
block|}
block|}
decl_stmt|;
specifier|static
specifier|final
name|Function2
argument_list|<
name|Float
argument_list|,
name|Float
argument_list|,
name|Float
argument_list|>
name|FLOAT_MIN
init|=
operator|new
name|Function2
argument_list|<
name|Float
argument_list|,
name|Float
argument_list|,
name|Float
argument_list|>
argument_list|()
block|{
specifier|public
name|Float
name|apply
parameter_list|(
name|Float
name|v1
parameter_list|,
name|Float
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|==
literal|null
operator|||
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|>
literal|0
condition|?
name|v2
else|:
name|v1
return|;
block|}
block|}
decl_stmt|;
specifier|static
specifier|final
name|Function2
argument_list|<
name|Float
argument_list|,
name|Float
argument_list|,
name|Float
argument_list|>
name|FLOAT_MAX
init|=
operator|new
name|Function2
argument_list|<
name|Float
argument_list|,
name|Float
argument_list|,
name|Float
argument_list|>
argument_list|()
block|{
specifier|public
name|Float
name|apply
parameter_list|(
name|Float
name|v1
parameter_list|,
name|Float
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|==
literal|null
operator|||
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|<
literal|0
condition|?
name|v2
else|:
name|v1
return|;
block|}
block|}
decl_stmt|;
specifier|static
specifier|final
name|Function2
argument_list|<
name|Double
argument_list|,
name|Double
argument_list|,
name|Double
argument_list|>
name|DOUBLE_MIN
init|=
operator|new
name|Function2
argument_list|<
name|Double
argument_list|,
name|Double
argument_list|,
name|Double
argument_list|>
argument_list|()
block|{
specifier|public
name|Double
name|apply
parameter_list|(
name|Double
name|v1
parameter_list|,
name|Double
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|==
literal|null
operator|||
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|>
literal|0
condition|?
name|v2
else|:
name|v1
return|;
block|}
block|}
decl_stmt|;
specifier|static
specifier|final
name|Function2
argument_list|<
name|Double
argument_list|,
name|Double
argument_list|,
name|Double
argument_list|>
name|DOUBLE_MAX
init|=
operator|new
name|Function2
argument_list|<
name|Double
argument_list|,
name|Double
argument_list|,
name|Double
argument_list|>
argument_list|()
block|{
specifier|public
name|Double
name|apply
parameter_list|(
name|Double
name|v1
parameter_list|,
name|Double
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|==
literal|null
operator|||
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|<
literal|0
condition|?
name|v2
else|:
name|v1
return|;
block|}
block|}
decl_stmt|;
specifier|static
specifier|final
name|Function2
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|,
name|Integer
argument_list|>
name|INTEGER_MIN
init|=
operator|new
name|Function2
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|apply
parameter_list|(
name|Integer
name|v1
parameter_list|,
name|Integer
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|==
literal|null
operator|||
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|>
literal|0
condition|?
name|v2
else|:
name|v1
return|;
block|}
block|}
decl_stmt|;
specifier|static
specifier|final
name|Function2
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|,
name|Integer
argument_list|>
name|INTEGER_MAX
init|=
operator|new
name|Function2
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|apply
parameter_list|(
name|Integer
name|v1
parameter_list|,
name|Integer
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|==
literal|null
operator|||
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|<
literal|0
condition|?
name|v2
else|:
name|v1
return|;
block|}
block|}
decl_stmt|;
specifier|static
specifier|final
name|Function2
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|,
name|Long
argument_list|>
name|LONG_MIN
init|=
operator|new
name|Function2
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|,
name|Long
argument_list|>
argument_list|()
block|{
specifier|public
name|Long
name|apply
parameter_list|(
name|Long
name|v1
parameter_list|,
name|Long
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|==
literal|null
operator|||
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|>
literal|0
condition|?
name|v2
else|:
name|v1
return|;
block|}
block|}
decl_stmt|;
specifier|static
specifier|final
name|Function2
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|,
name|Long
argument_list|>
name|LONG_MAX
init|=
operator|new
name|Function2
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|,
name|Long
argument_list|>
argument_list|()
block|{
specifier|public
name|Long
name|apply
parameter_list|(
name|Long
name|v1
parameter_list|,
name|Long
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|==
literal|null
operator|||
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
operator|<
literal|0
condition|?
name|v2
else|:
name|v1
return|;
block|}
block|}
decl_stmt|;
comment|// flags a piece of code we're yet to implement
specifier|public
specifier|static
name|RuntimeException
name|todo
parameter_list|()
block|{
return|return
operator|new
name|RuntimeException
argument_list|()
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Queryable
argument_list|<
name|T
argument_list|>
name|asQueryable
parameter_list|(
name|DefaultEnumerable
argument_list|<
name|T
argument_list|>
name|source
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|source
operator|instanceof
name|Queryable
condition|?
operator|(
operator|(
name|Queryable
argument_list|<
name|T
argument_list|>
operator|)
name|source
operator|)
else|:
operator|new
name|EnumerableQueryable
argument_list|<
name|T
argument_list|>
argument_list|(
name|Linq4j
operator|.
name|DEFAULT_PROVIDER
argument_list|,
operator|(
name|Class
operator|)
name|Object
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|source
argument_list|)
return|;
block|}
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|Comparable
argument_list|>
name|COMPARABLE_COMPARATOR
init|=
operator|new
name|Comparator
argument_list|<
name|Comparable
argument_list|>
argument_list|()
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
decl_stmt|;
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
name|comparableComparator
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
operator|(
name|Comparator
argument_list|<
name|T
argument_list|>
operator|)
operator|(
name|Comparator
operator|)
name|COMPARABLE_COMPARATOR
return|;
block|}
block|}
end_class

begin_comment
comment|// End Extensions.java
end_comment

end_unit

