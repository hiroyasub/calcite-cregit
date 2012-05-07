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
name|*
import|;
end_import

begin_comment
comment|/**  * Namespace for a table constructor<code>VALUES (expr, expr, ...)</code>.  *  * @author jhyde  * @version $Id$  * @since Mar 25, 2003  */
end_comment

begin_class
specifier|public
class|class
name|TableConstructorNamespace
extends|extends
name|AbstractNamespace
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlCall
name|values
decl_stmt|;
specifier|private
specifier|final
name|SqlValidatorScope
name|scope
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a TableConstructorNamespace.      *      * @param validator Validator      * @param values VALUES parse tree node      * @param scope Scope      * @param enclosingNode Enclosing node      */
name|TableConstructorNamespace
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|,
name|SqlCall
name|values
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlNode
name|enclosingNode
parameter_list|)
block|{
name|super
argument_list|(
name|validator
argument_list|,
name|enclosingNode
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|protected
name|RelDataType
name|validateImpl
parameter_list|()
block|{
return|return
name|validator
operator|.
name|getTableConstructorRowType
argument_list|(
name|values
argument_list|,
name|scope
argument_list|)
return|;
block|}
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|values
return|;
block|}
comment|/**      * Returns the scope.      *      * @return scope      */
specifier|public
name|SqlValidatorScope
name|getScope
parameter_list|()
block|{
return|return
name|scope
return|;
block|}
block|}
end_class

begin_comment
comment|// End TableConstructorNamespace.java
end_comment

end_unit

