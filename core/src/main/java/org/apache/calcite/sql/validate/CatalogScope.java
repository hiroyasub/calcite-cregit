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
name|SqlNode
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
name|ImmutableList
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
comment|/**  * Implementation of {@link SqlValidatorScope} that can see all schemas in the  * current catalog.  *  *<p>Occurs near the root of the scope stack; its parent is typically  * {@link EmptyScope}.  *  *<p>Helps resolve {@code schema.table.column} column references, such as  *  *<blockquote><pre>select sales.emp.empno from sales.emp</pre></blockquote>  */
end_comment

begin_class
class|class
name|CatalogScope
extends|extends
name|DelegatingScope
block|{
comment|/** Fully-qualified name of the catalog. Typically empty or ["CATALOG"]. */
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|names
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|CatalogScope
parameter_list|(
name|SqlValidatorScope
name|parent
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|names
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|names
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

