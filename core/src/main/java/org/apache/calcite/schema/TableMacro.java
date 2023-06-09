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
name|schema
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
name|Nullable
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

begin_comment
comment|/**  * Function that returns a {@link Table}.  *  *<p>As the name "macro" implies, this is invoked at "compile time", that is,  * during query preparation. Compile-time expansion of table expressions allows  * for some very powerful query-optimizations.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|TableMacro
extends|extends
name|Function
block|{
comment|/**    * Applies arguments to yield a table.    *    * @param arguments Arguments    * @return Table    */
name|TranslatableTable
name|apply
argument_list|(
name|List
operator|<
condition|?
then|extends @
name|Nullable
name|Object
operator|>
name|arguments
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

