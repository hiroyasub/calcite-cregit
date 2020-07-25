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
name|sql
operator|.
name|validate
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
name|sql
operator|.
name|SqlIdentifier
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Ordering
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
name|List
import|;
end_import

begin_comment
comment|/**  * An interface of an object identifier that represents a SqlIdentifier.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlMoniker
block|{
name|Comparator
argument_list|<
name|SqlMoniker
argument_list|>
name|COMPARATOR
init|=
operator|new
name|Comparator
argument_list|<
name|SqlMoniker
argument_list|>
argument_list|()
block|{
specifier|final
name|Ordering
argument_list|<
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|listOrdering
init|=
name|Ordering
operator|.
expr|<
name|String
operator|>
name|natural
argument_list|()
operator|.
name|lexicographical
argument_list|()
decl_stmt|;
specifier|public
name|int
name|compare
parameter_list|(
name|SqlMoniker
name|o1
parameter_list|,
name|SqlMoniker
name|o2
parameter_list|)
block|{
name|int
name|c
init|=
name|o1
operator|.
name|getType
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
name|c
operator|=
name|listOrdering
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getFullyQualifiedNames
argument_list|()
argument_list|,
name|o2
operator|.
name|getFullyQualifiedNames
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
block|}
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the type of object referred to by this moniker. Never null.    */
name|SqlMonikerType
name|getType
parameter_list|()
function_decl|;
comment|/**    * Returns the array of component names.    */
name|List
argument_list|<
name|String
argument_list|>
name|getFullyQualifiedNames
parameter_list|()
function_decl|;
comment|/**    * Creates a {@link SqlIdentifier} containing the fully-qualified name.    */
name|SqlIdentifier
name|toIdentifier
parameter_list|()
function_decl|;
name|String
name|id
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

