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
name|sql
operator|.
name|validate
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * The name-resolution context for expression inside a JOIN clause. The objects  * visible are the joined table expressions, and those inherited from the parent  * scope.  *  *<p>Consider "SELECT * FROM (A JOIN B ON {exp1}) JOIN C ON {exp2}". {exp1} is  * resolved in the join scope for "A JOIN B", which contains A and B but not  * C.</p>  *  * @author jhyde  * @version $Id$  * @since Mar 25, 2003  */
end_comment

begin_class
specifier|public
class|class
name|JoinScope
extends|extends
name|ListScope
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlValidatorScope
name|usingScope
decl_stmt|;
specifier|private
specifier|final
name|SqlJoin
name|join
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a<code>JoinScope</code>.      *      * @param parent Parent scope      * @param usingScope Scope for resolving USING clause      * @param join Call to JOIN operator      */
name|JoinScope
parameter_list|(
name|SqlValidatorScope
name|parent
parameter_list|,
name|SqlValidatorScope
name|usingScope
parameter_list|,
name|SqlJoin
name|join
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|usingScope
operator|=
name|usingScope
expr_stmt|;
name|this
operator|.
name|join
operator|=
name|join
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|join
return|;
block|}
specifier|public
name|void
name|addChild
parameter_list|(
name|SqlValidatorNamespace
name|ns
parameter_list|,
name|String
name|alias
parameter_list|)
block|{
name|super
operator|.
name|addChild
argument_list|(
name|ns
argument_list|,
name|alias
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|usingScope
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|usingScope
operator|!=
name|parent
operator|)
condition|)
block|{
comment|// We're looking at a join within a join. Recursively add this
comment|// child to its parent scope too. Example:
comment|//
comment|//   select *
comment|//   from (a join b on expr1)
comment|//   join c on expr2
comment|//   where expr3
comment|//
comment|// 'a' is a child namespace of 'a join b' and also of
comment|// 'a join b join c'.
name|usingScope
operator|.
name|addChild
argument_list|(
name|ns
argument_list|,
name|alias
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|SqlWindow
name|lookupWindow
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// Lookup window in enclosing select.
if|if
condition|(
name|usingScope
operator|!=
literal|null
condition|)
block|{
return|return
name|usingScope
operator|.
name|lookupWindow
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Returns the scope which is used for resolving USING clause.      */
specifier|public
name|SqlValidatorScope
name|getUsingScope
parameter_list|()
block|{
return|return
name|usingScope
return|;
block|}
block|}
end_class

begin_comment
comment|// End JoinScope.java
end_comment

end_unit

