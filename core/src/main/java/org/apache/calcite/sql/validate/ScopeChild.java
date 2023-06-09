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

begin_comment
comment|/** One of the inputs of a {@link SqlValidatorScope}.  *  *<p>Most commonly, it is an item in a FROM clause, and consists of a namespace  * (the columns it provides), and optional name (table alias), and ordinal  * within the FROM clause. */
end_comment

begin_class
class|class
name|ScopeChild
block|{
specifier|final
name|int
name|ordinal
decl_stmt|;
specifier|final
name|String
name|name
decl_stmt|;
specifier|final
name|SqlValidatorNamespace
name|namespace
decl_stmt|;
specifier|final
name|boolean
name|nullable
decl_stmt|;
comment|/** Creates a ScopeChild.    *    * @param ordinal Ordinal of child within parent scope    * @param name Table alias    * @param namespace Namespace of child    * @param nullable Whether fields of the child are nullable when seen from the    *   parent, due to outer joins    */
name|ScopeChild
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|String
name|name
parameter_list|,
name|SqlValidatorNamespace
name|namespace
parameter_list|,
name|boolean
name|nullable
parameter_list|)
block|{
name|this
operator|.
name|ordinal
operator|=
name|ordinal
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|namespace
operator|=
name|namespace
expr_stmt|;
name|this
operator|.
name|nullable
operator|=
name|nullable
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|ordinal
operator|+
literal|": "
operator|+
name|name
operator|+
literal|": "
operator|+
name|namespace
operator|+
operator|(
name|nullable
condition|?
literal|" (nullable)"
else|:
literal|""
operator|)
return|;
block|}
block|}
end_class

end_unit

