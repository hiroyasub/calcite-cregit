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
name|framework
operator|.
name|qual
operator|.
name|Covariant
import|;
end_import

begin_comment
comment|/**  * Represents a collection of objects that have a common key.  *  * @param<K> Key type  * @param<V> Element type  */
end_comment

begin_interface
annotation|@
name|Covariant
argument_list|(
literal|0
argument_list|)
specifier|public
interface|interface
name|Grouping
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|Enumerable
argument_list|<
name|V
argument_list|>
block|{
comment|/**    * Gets the key of this Grouping.    */
name|K
name|getKey
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

