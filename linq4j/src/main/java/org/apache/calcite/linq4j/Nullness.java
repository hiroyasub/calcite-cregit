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
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|EnsuresNonNull
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
name|NonNull
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
name|org
operator|.
name|checkerframework
operator|.
name|dataflow
operator|.
name|qual
operator|.
name|Pure
import|;
end_import

begin_comment
comment|/**  * The methods in this class allow to cast nullable reference to a non-nullable one.  * This is an internal class, and it is not meant to be used as a public API.  *<p>The class enables to remove checker-qual runtime dependency, and helps IDEs to see  * the resulting types of {@code castNonNull} better</p>  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"cast.unsafe"
block|,
literal|"NullableProblems"
block|,
literal|"contracts.postcondition.not.satisfied"
block|}
argument_list|)
specifier|public
class|class
name|Nullness
block|{
specifier|private
name|Nullness
parameter_list|()
block|{
block|}
comment|/**    * Enables to threat nullable type as non-nullable with no assertions.    *    *<p>It is useful in the case you have a nullable lately-initialized field like the following:    * {@code class Wrapper<T> { @Nullable T value; }}.    * That signature allows to use {@code Wrapper} with both nullable or non-nullable types:    * {@code Wrapper<@Nullable Integer>} vs {@code Wrapper<Integer>}. Suppose you need to implement    * {@code T get() { return value; }} The issue is checkerframework does not permit that    * because {@code T} has unknown nullability, so the following needs to be used:    * {@code T get() { return sneakyNull(value); }}</p>    *    * @param<T>     the type of the reference    * @param ref     a reference of @Nullable type, that is non-null at run time    * @return the argument, casted to have the type qualifier @NonNull    */
annotation|@
name|Pure
specifier|public
specifier|static
expr|@
name|EnsuresNonNull
argument_list|(
literal|"#1"
argument_list|)
operator|<
name|T
expr|extends @
name|Nullable
name|Object
operator|>
expr|@
name|NonNull
name|T
name|castNonNull
argument_list|(
annotation|@
name|Nullable
name|T
name|ref
argument_list|)
block|{
comment|//noinspection ConstantConditions
return|return
operator|(
expr|@
name|NonNull
name|T
operator|)
name|ref
return|;
block|}
block|}
end_class

end_unit

