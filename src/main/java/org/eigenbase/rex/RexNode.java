begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Row expression.  *  *<p>Every row-expression has a type. (Compare with {@link  * org.eigenbase.sql.SqlNode}, which is created before validation, and therefore  * types may not be available.)</p>  *  *<p>Some common row-expressions are: {@link RexLiteral} (constant value),  * {@link RexVariable} (variable), {@link RexCall} (call to operator with  * operands). Expressions are generally created using a {@link RexBuilder}  * factory.</p>  *  * @author jhyde  * @version $Id$  * @since Nov 22, 2003  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RexNode
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|RexNode
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|RexNode
index|[
literal|0
index|]
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
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
specifier|public
specifier|abstract
name|RexNode
name|clone
parameter_list|()
function_decl|;
comment|/**      * Returns whether this expression always returns true. (Such as if this      * expression is equal to the literal<code>TRUE</code>.)      */
specifier|public
name|boolean
name|isAlwaysTrue
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
name|RexKind
name|kind
parameter_list|)
block|{
return|return
operator|(
name|getKind
argument_list|()
operator|==
name|kind
operator|)
operator|||
name|kind
operator|.
name|includes
argument_list|(
name|getKind
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns the kind of node this is.      *      * @return A {@link RexKind} value, never null      *      * @post return != null      */
specifier|public
name|RexKind
name|getKind
parameter_list|()
block|{
return|return
name|RexKind
operator|.
name|Other
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
comment|/**      * Accepts a visitor, dispatching to the right overloaded {@link      * RexVisitor#visitInputRef visitXxx} method.      *      *<p>Also see {@link RexProgram#apply(RexVisitor, RexNode[], RexNode)},      * which applies a visitor to several expressions simultaneously.      */
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

