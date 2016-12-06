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
operator|.
name|tree
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  *<p>Analogous to LINQ's System.Linq.Expression.</p>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Expression
extends|extends
name|AbstractNode
block|{
comment|/**    * Creates an Expression.    *    *<p>The type of the expression may, at the caller's discretion, be a    * regular class (because {@link Class} implements {@link Type}) or it may    * be a different implementation that retains information about type    * parameters.</p>    *    * @param nodeType Node type    * @param type Type of the expression    */
specifier|public
name|Expression
parameter_list|(
name|ExpressionType
name|nodeType
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|nodeType
argument_list|,
name|type
argument_list|)
expr_stmt|;
assert|assert
name|nodeType
operator|!=
literal|null
assert|;
assert|assert
name|type
operator|!=
literal|null
assert|;
block|}
annotation|@
name|Override
comment|// More specific return type.
specifier|public
specifier|abstract
name|Expression
name|accept
parameter_list|(
name|Shuttle
name|shuttle
parameter_list|)
function_decl|;
comment|/**    * Indicates that the node can be reduced to a simpler node. If this    * returns true, Reduce() can be called to produce the reduced form.    */
specifier|public
name|boolean
name|canReduce
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

begin_comment
comment|// End Expression.java
end_comment

end_unit

