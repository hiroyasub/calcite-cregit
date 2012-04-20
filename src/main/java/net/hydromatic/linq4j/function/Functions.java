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

begin_comment
comment|/**  * Utilities relating to functions.  */
end_comment

begin_class
specifier|public
class|class
name|Functions
block|{
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
block|}
end_class

begin_comment
comment|// End Functions.java
end_comment

end_unit

