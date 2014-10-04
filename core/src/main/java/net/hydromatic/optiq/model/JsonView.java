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
name|optiq
operator|.
name|model
package|;
end_package

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
comment|/**  * View schema element.  *  * @see JsonRoot Description of schema elements  */
end_comment

begin_class
specifier|public
class|class
name|JsonView
extends|extends
name|JsonTable
block|{
comment|/** SQL query that is the definition of the view. */
specifier|public
name|Object
name|sql
decl_stmt|;
comment|/** Schema name(s) to use when resolving query. If not specified, defaults    * to current schema. */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|path
decl_stmt|;
specifier|public
name|void
name|accept
parameter_list|(
name|ModelHandler
name|handler
parameter_list|)
block|{
name|handler
operator|.
name|visit
argument_list|(
name|this
argument_list|)
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
literal|"JsonView(name="
operator|+
name|name
operator|+
literal|")"
return|;
block|}
comment|/** Returns the SQL query as a string, concatenating a list of lines if    * necessary. */
specifier|public
name|String
name|getSql
parameter_list|()
block|{
return|return
name|JsonLattice
operator|.
name|toString
argument_list|(
name|sql
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End JsonView.java
end_comment

end_unit

