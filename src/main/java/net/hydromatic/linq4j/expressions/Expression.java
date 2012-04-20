begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
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
name|Extensions
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
block|{
specifier|public
specifier|final
name|ExpressionType
name|nodeType
decl_stmt|;
comment|/**      * Creates an Expression.      *      * @param nodeType Node type      */
specifier|public
name|Expression
parameter_list|(
name|ExpressionType
name|nodeType
parameter_list|)
block|{
name|this
operator|.
name|nodeType
operator|=
name|nodeType
expr_stmt|;
block|}
comment|/** Indicates that the node can be reduced to a simpler node. If this      * returns true, Reduce() can be called to produce the reduced form. */
specifier|public
name|boolean
name|canReduce
parameter_list|()
block|{
throw|throw
name|Extensions
operator|.
name|todo
argument_list|()
throw|;
block|}
comment|/** Gets the node type of this Expression. */
specifier|public
name|ExpressionType
name|getNodeType
parameter_list|()
block|{
return|return
name|nodeType
return|;
block|}
comment|/** Gets the static type of the expression that this Expression represents. */
specifier|public
name|Class
name|getType
parameter_list|()
block|{
throw|throw
name|Extensions
operator|.
name|todo
argument_list|()
throw|;
block|}
block|}
end_class

begin_comment
comment|// End Expression.java
end_comment

end_unit

