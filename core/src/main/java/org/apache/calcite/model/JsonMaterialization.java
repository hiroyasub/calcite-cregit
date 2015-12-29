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
comment|/**  * Element that describes how a table is a materialization of a query.  *  *<p>Occurs within {@link JsonSchema#materializations}.  *  * @see JsonRoot Description of schema elements  */
end_comment

begin_class
specifier|public
class|class
name|JsonMaterialization
block|{
specifier|public
name|String
name|view
decl_stmt|;
specifier|public
name|String
name|table
decl_stmt|;
comment|/** SQL query that defines the materialization.    *    *<p>Must be a string or a list of strings (which are concatenated into a    * multi-line SQL string, separated by newlines).    */
specifier|public
name|Object
name|sql
decl_stmt|;
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|viewSchemaPath
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
literal|"JsonMaterialization(table="
operator|+
name|table
operator|+
literal|", view="
operator|+
name|view
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
comment|// End JsonMaterialization.java
end_comment

end_unit

