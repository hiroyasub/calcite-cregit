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
name|runtime
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
name|Predicate
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Abstract implementation of {@link com.google.common.base.Predicate}.  *  *<p>Derived class needs to implement the {@link #test} method.  *  *<p>Helps with the transition to {@code java.util.function.Predicate},  * which was introduced in JDK 1.8, and is required in Guava 21.0 and higher,  * but still works on JDK 1.7.  *  * @param<T> the type of the input to the predicate  *  * @deprecated Now Calcite is Java 8 and higher, we recommend that you  * implement {@link java.util.function.Predicate} directly.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|PredicateImpl
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Predicate
argument_list|<
name|T
argument_list|>
block|{
specifier|public
specifier|final
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|T
name|input
parameter_list|)
block|{
return|return
name|test
argument_list|(
name|input
argument_list|)
return|;
block|}
comment|/** Overrides {@code java.util.function.Predicate#test} in JDK8 and higher. */
specifier|public
specifier|abstract
name|boolean
name|test
parameter_list|(
annotation|@
name|Nullable
name|T
name|t
parameter_list|)
function_decl|;
block|}
end_class

end_unit

