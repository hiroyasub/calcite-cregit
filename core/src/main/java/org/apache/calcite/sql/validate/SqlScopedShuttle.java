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
name|SqlNode
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
name|util
operator|.
name|SqlShuttle
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
name|util
operator|.
name|SqlVisitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Refinement to {@link SqlShuttle} which maintains a stack of scopes.  *  *<p>Derived class should override {@link #visitScoped(SqlCall)} rather than  * {@link SqlVisitor#visit(SqlCall)}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlScopedShuttle
extends|extends
name|SqlShuttle
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Deque
argument_list|<
name|SqlValidatorScope
argument_list|>
name|scopes
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|SqlScopedShuttle
parameter_list|(
name|SqlValidatorScope
name|initialScope
parameter_list|)
block|{
name|scopes
operator|.
name|push
argument_list|(
name|initialScope
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
specifier|final
annotation|@
name|Nullable
name|SqlNode
name|visit
parameter_list|(
name|SqlCall
name|call
parameter_list|)
block|{
name|SqlValidatorScope
name|oldScope
init|=
name|getScope
argument_list|()
decl_stmt|;
name|SqlValidatorScope
name|newScope
init|=
name|oldScope
operator|.
name|getOperandScope
argument_list|(
name|call
argument_list|)
decl_stmt|;
name|scopes
operator|.
name|push
argument_list|(
name|newScope
argument_list|)
expr_stmt|;
name|SqlNode
name|result
init|=
name|visitScoped
argument_list|(
name|call
argument_list|)
decl_stmt|;
name|scopes
operator|.
name|pop
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**    * Visits an operator call. If the call has entered a new scope, the base    * class will have already modified the scope.    */
specifier|protected
annotation|@
name|Nullable
name|SqlNode
name|visitScoped
parameter_list|(
name|SqlCall
name|call
parameter_list|)
block|{
return|return
name|super
operator|.
name|visit
argument_list|(
name|call
argument_list|)
return|;
block|}
comment|/**    * Returns the current scope.    */
specifier|protected
name|SqlValidatorScope
name|getScope
parameter_list|()
block|{
return|return
name|requireNonNull
argument_list|(
name|scopes
operator|.
name|peek
argument_list|()
argument_list|,
literal|"scopes.peek()"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

