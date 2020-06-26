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
name|rel
operator|.
name|type
operator|.
name|StructKind
import|;
end_import

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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlWithItem
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
comment|/** Scope providing the objects that are available after evaluating an item  * in a WITH clause.  *  *<p>For example, in</p>  *  *<blockquote>{@code WITH t1 AS (q1) t2 AS (q2) q3}</blockquote>  *  *<p>{@code t1} provides a scope that is used to validate {@code q2}  * (and therefore {@code q2} may reference {@code t1}),  * and {@code t2} provides a scope that is used to validate {@code q3}  * (and therefore q3 may reference {@code t1} and {@code t2}).</p>  */
end_comment

begin_class
class|class
name|WithScope
extends|extends
name|ListScope
block|{
specifier|private
specifier|final
name|SqlWithItem
name|withItem
decl_stmt|;
comment|/** Creates a WithScope. */
name|WithScope
parameter_list|(
name|SqlValidatorScope
name|parent
parameter_list|,
name|SqlWithItem
name|withItem
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|withItem
operator|=
name|withItem
expr_stmt|;
block|}
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|withItem
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlValidatorNamespace
name|getTableNamespace
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
if|if
condition|(
name|names
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|names
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
name|withItem
operator|.
name|name
operator|.
name|getSimple
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|validator
operator|.
name|getNamespace
argument_list|(
name|withItem
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|getTableNamespace
argument_list|(
name|names
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|resolveTable
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|SqlNameMatcher
name|nameMatcher
parameter_list|,
name|Path
name|path
parameter_list|,
name|Resolved
name|resolved
parameter_list|)
block|{
if|if
condition|(
name|names
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|names
operator|.
name|equals
argument_list|(
name|withItem
operator|.
name|name
operator|.
name|names
argument_list|)
condition|)
block|{
specifier|final
name|SqlValidatorNamespace
name|ns
init|=
name|validator
operator|.
name|getNamespace
argument_list|(
name|withItem
argument_list|)
decl_stmt|;
specifier|final
name|Step
name|path2
init|=
name|path
operator|.
name|plus
argument_list|(
name|ns
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|0
argument_list|,
name|names
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|StructKind
operator|.
name|FULLY_QUALIFIED
argument_list|)
decl_stmt|;
name|resolved
operator|.
name|found
argument_list|(
name|ns
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|path2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return;
block|}
name|super
operator|.
name|resolveTable
argument_list|(
name|names
argument_list|,
name|nameMatcher
argument_list|,
name|path
argument_list|,
name|resolved
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

