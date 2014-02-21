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
name|RelDataType
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
name|SqlCall
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

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|eigenbase
operator|.
name|sql
operator|.
name|SqlWithOperator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Util
import|;
end_import

begin_comment
comment|/**  * Namespace for<code>WITH</code> clause.  */
end_comment

begin_class
specifier|public
class|class
name|WithNamespace
extends|extends
name|AbstractNamespace
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlCall
name|with
decl_stmt|;
specifier|private
specifier|final
name|SqlValidatorScope
name|scope
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a TableConstructorNamespace.    *    * @param validator     Validator    * @param with          WITH clause    * @param scope         Scope    * @param enclosingNode Enclosing node    */
name|WithNamespace
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|,
name|SqlCall
name|with
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
name|with
operator|=
name|with
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
assert|assert
name|with
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|WITH
assert|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|protected
name|RelDataType
name|validateImpl
parameter_list|()
block|{
specifier|final
name|SqlWithOperator
operator|.
name|Call
name|call
init|=
name|SqlWithOperator
operator|.
name|Call
operator|.
name|of
argument_list|(
name|with
argument_list|)
decl_stmt|;
for|for
control|(
name|SqlNode
name|with
range|:
name|call
operator|.
name|withList
control|)
block|{
name|validator
operator|.
name|validateWithItem
argument_list|(
operator|(
name|SqlCall
operator|)
name|with
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SqlValidatorScope
name|scope2
init|=
name|validator
operator|.
name|getWithScope
argument_list|(
name|Util
operator|.
name|last
argument_list|(
name|call
operator|.
name|withList
operator|.
name|getList
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|validator
operator|.
name|validateQuery
argument_list|(
name|call
operator|.
name|body
argument_list|,
name|scope2
argument_list|)
expr_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|call
operator|.
name|body
argument_list|)
decl_stmt|;
name|validator
operator|.
name|setValidatedNodeType
argument_list|(
name|with
argument_list|,
name|rowType
argument_list|)
expr_stmt|;
return|return
name|rowType
return|;
block|}
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|with
return|;
block|}
comment|/**    * Returns the scope.    *    * @return scope    */
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

