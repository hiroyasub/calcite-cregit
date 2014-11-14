begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Function2
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

begin_comment
comment|/**  * Represents a collection of keys each mapped to one or more values.  *  * @param<K> Key type  * @param<V> Value type  */
end_comment

begin_interface
specifier|public
interface|interface
name|Lookup
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|Map
argument_list|<
name|K
argument_list|,
name|Enumerable
argument_list|<
name|V
argument_list|>
argument_list|>
extends|,
name|Enumerable
argument_list|<
name|Grouping
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
block|{
comment|/**    * Applies a transform function to each key and its associated values and    * returns the results.    *    * @param resultSelector Result selector    * @param<TResult> Result type    *    * @return Enumerable over results    */
parameter_list|<
name|TResult
parameter_list|>
name|Enumerable
argument_list|<
name|TResult
argument_list|>
name|applyResultSelector
parameter_list|(
name|Function2
argument_list|<
name|K
argument_list|,
name|Enumerable
argument_list|<
name|V
argument_list|>
argument_list|,
name|TResult
argument_list|>
name|resultSelector
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End Lookup.java
end_comment

end_unit

