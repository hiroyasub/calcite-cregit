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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|type
operator|.
name|RelDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
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
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlInsert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlNode
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * Namespace for a table constructor<code>VALUES (expr, expr, ...)</code>.  */
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
comment|/**    * Creates a TableConstructorNamespace.    *    * @param validator     Validator    * @param values        VALUES parse tree node    * @param scope         Scope    * @param enclosingNode Enclosing node    */
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
comment|// First, validate the VALUES. If VALUES is inside INSERT, infers
comment|// the type of NULL values based on the types of target columns.
specifier|final
name|RelDataType
name|targetRowType
decl_stmt|;
if|if
condition|(
name|enclosingNode
operator|instanceof
name|SqlInsert
condition|)
block|{
name|SqlInsert
name|node
init|=
operator|(
name|SqlInsert
operator|)
name|enclosingNode
decl_stmt|;
name|targetRowType
operator|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|targetRowType
operator|=
name|validator
operator|.
name|getUnknownType
argument_list|()
expr_stmt|;
block|}
name|validator
operator|.
name|validateValues
argument_list|(
name|values
argument_list|,
name|targetRowType
argument_list|,
name|scope
argument_list|)
expr_stmt|;
specifier|final
name|RelDataType
name|tableConstructorRowType
init|=
name|validator
operator|.
name|getTableConstructorRowType
argument_list|(
name|values
argument_list|,
name|scope
argument_list|)
decl_stmt|;
if|if
condition|(
name|tableConstructorRowType
operator|==
literal|null
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|values
argument_list|,
name|RESOURCE
operator|.
name|incompatibleTypes
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|tableConstructorRowType
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

