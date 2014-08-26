begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rex
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|SqlKind
import|;
end_import

begin_comment
comment|/**  * Row expression.  *  *<p>Every row-expression has a type. (Compare with {@link  * org.eigenbase.sql.SqlNode}, which is created before validation, and therefore  * types may not be available.)</p>  *  *<p>Some common row-expressions are: {@link RexLiteral} (constant value),  * {@link RexVariable} (variable), {@link RexCall} (call to operator with  * operands). Expressions are generally created using a {@link RexBuilder}  * factory.</p>  *  *<p>All sub-classes of RexNode are immutable.</p>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RexNode
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|// Effectively final. Set in each sub-class constructor, and never re-set.
specifier|protected
name|String
name|digest
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
specifier|public
specifier|abstract
name|RelDataType
name|getType
parameter_list|()
function_decl|;
comment|/**    * Returns whether this expression always returns true. (Such as if this    * expression is equal to the literal<code>TRUE</code>.)    */
specifier|public
name|boolean
name|isAlwaysTrue
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Returns whether this expression always returns false. (Such as if this    * expression is equal to the literal<code>FALSE</code>.)    */
specifier|public
name|boolean
name|isAlwaysFalse
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isA
parameter_list|(
name|SqlKind
name|kind
parameter_list|)
block|{
return|return
name|getKind
argument_list|()
operator|==
name|kind
return|;
block|}
specifier|public
name|boolean
name|isA
parameter_list|(
name|Collection
argument_list|<
name|SqlKind
argument_list|>
name|kinds
parameter_list|)
block|{
return|return
name|getKind
argument_list|()
operator|.
name|belongsTo
argument_list|(
name|kinds
argument_list|)
return|;
block|}
comment|/**    * Returns the kind of node this is.    *    * @return Node kind, never null    */
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|SqlKind
operator|.
name|OTHER
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|digest
return|;
block|}
comment|/**    * Accepts a visitor, dispatching to the right overloaded {@link    * RexVisitor#visitInputRef visitXxx} method.    *    *<p>Also see {@link RexProgram#apply(RexVisitor, java.util.List, RexNode)},    * which applies a visitor to several expressions simultaneously.    */
specifier|public
specifier|abstract
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|RexVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
function_decl|;
block|}
end_class

begin_comment
comment|// End RexNode.java
end_comment

end_unit

