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
name|type
operator|.
name|SqlOperandTypeChecker
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
name|type
operator|.
name|SqlOperandTypeInference
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
name|type
operator|.
name|SqlReturnTypeInference
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
name|validate
operator|.
name|SqlMonotonicity
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
name|function
operator|.
name|Function
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
comment|/**  * Concrete implementation of {@link SqlFunction}.  *  *<p>The class is final, and instances are immutable.  *  *<p>Instances are created only by {@link SqlBasicFunction#create} and are  * "modified" by "wither" methods such as {@link #withName} to create a new  * instance with one property changed. Since the class is final, you can modify  * behavior only by providing strategy objects, not by overriding methods in a  * subclass.  */
end_comment

begin_class
specifier|public
class|class
name|SqlBasicFunction
extends|extends
name|SqlFunction
block|{
specifier|private
specifier|final
name|SqlSyntax
name|syntax
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|deterministic
decl_stmt|;
specifier|private
specifier|final
name|Function
argument_list|<
name|SqlOperatorBinding
argument_list|,
name|SqlMonotonicity
argument_list|>
name|monotonicityInference
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a new SqlFunction for a call to a built-in function.    *    * @param name Name of built-in function    * @param kind Kind of operator implemented by function    * @param syntax Syntax    * @param deterministic Whether the function is deterministic    * @param returnTypeInference Strategy to use for return type inference    * @param operandTypeInference Strategy to use for parameter type inference    * @param operandTypeChecker Strategy to use for parameter type checking    * @param category Categorization for function    * @param monotonicityInference Strategy to infer monotonicity of a call    */
specifier|private
name|SqlBasicFunction
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|SqlSyntax
name|syntax
parameter_list|,
name|boolean
name|deterministic
parameter_list|,
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
annotation|@
name|Nullable
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|,
name|SqlFunctionCategory
name|category
parameter_list|,
name|Function
argument_list|<
name|SqlOperatorBinding
argument_list|,
name|SqlMonotonicity
argument_list|>
name|monotonicityInference
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
name|requireNonNull
argument_list|(
name|returnTypeInference
argument_list|,
literal|"returnTypeInference"
argument_list|)
argument_list|,
name|operandTypeInference
argument_list|,
name|requireNonNull
argument_list|(
name|operandTypeChecker
argument_list|,
literal|"operandTypeChecker"
argument_list|)
argument_list|,
name|category
argument_list|)
expr_stmt|;
name|this
operator|.
name|syntax
operator|=
name|requireNonNull
argument_list|(
name|syntax
argument_list|,
literal|"syntax"
argument_list|)
expr_stmt|;
name|this
operator|.
name|deterministic
operator|=
name|deterministic
expr_stmt|;
name|this
operator|.
name|monotonicityInference
operator|=
name|requireNonNull
argument_list|(
name|monotonicityInference
argument_list|,
literal|"monotonicityInference"
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a {@code SqlBasicFunction} whose name is the same as its kind    * and whose category {@link SqlFunctionCategory#SYSTEM}. */
specifier|public
specifier|static
name|SqlBasicFunction
name|create
parameter_list|(
name|SqlKind
name|kind
parameter_list|,
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|)
block|{
return|return
operator|new
name|SqlBasicFunction
argument_list|(
name|kind
operator|.
name|name
argument_list|()
argument_list|,
name|kind
argument_list|,
name|SqlSyntax
operator|.
name|FUNCTION
argument_list|,
literal|true
argument_list|,
name|returnTypeInference
argument_list|,
literal|null
argument_list|,
name|operandTypeChecker
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
argument_list|,
name|call
lambda|->
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
argument_list|)
return|;
block|}
comment|/** Creates a {@code SqlBasicFunction}    *  with kind {@link SqlKind#OTHER_FUNCTION}    *  and category {@link SqlFunctionCategory#NUMERIC}. */
specifier|public
specifier|static
name|SqlBasicFunction
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|)
block|{
return|return
operator|new
name|SqlBasicFunction
argument_list|(
name|name
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|SqlSyntax
operator|.
name|FUNCTION
argument_list|,
literal|true
argument_list|,
name|returnTypeInference
argument_list|,
literal|null
argument_list|,
name|operandTypeChecker
argument_list|,
name|SqlFunctionCategory
operator|.
name|NUMERIC
argument_list|,
name|call
lambda|->
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
argument_list|)
return|;
block|}
comment|/** Creates a {@code SqlBasicFunction}    *  with kind {@link SqlKind#OTHER_FUNCTION}. */
specifier|public
specifier|static
name|SqlBasicFunction
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|,
name|SqlFunctionCategory
name|category
parameter_list|)
block|{
return|return
operator|new
name|SqlBasicFunction
argument_list|(
name|name
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|SqlSyntax
operator|.
name|FUNCTION
argument_list|,
literal|true
argument_list|,
name|returnTypeInference
argument_list|,
literal|null
argument_list|,
name|operandTypeChecker
argument_list|,
name|category
argument_list|,
name|call
lambda|->
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
argument_list|)
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|SqlReturnTypeInference
name|getReturnTypeInference
parameter_list|()
block|{
return|return
name|requireNonNull
argument_list|(
name|super
operator|.
name|getReturnTypeInference
argument_list|()
argument_list|,
literal|"returnTypeInference"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlOperandTypeChecker
name|getOperandTypeChecker
parameter_list|()
block|{
return|return
name|requireNonNull
argument_list|(
name|super
operator|.
name|getOperandTypeChecker
argument_list|()
argument_list|,
literal|"operandTypeChecker"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlSyntax
name|getSyntax
parameter_list|()
block|{
return|return
name|syntax
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDeterministic
parameter_list|()
block|{
return|return
name|deterministic
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|SqlOperatorBinding
name|call
parameter_list|)
block|{
return|return
name|monotonicityInference
operator|.
name|apply
argument_list|(
name|call
argument_list|)
return|;
block|}
comment|/** Returns a copy of this function with a given name. */
specifier|public
name|SqlBasicFunction
name|withName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|SqlBasicFunction
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
name|syntax
argument_list|,
name|deterministic
argument_list|,
name|getReturnTypeInference
argument_list|()
argument_list|,
name|getOperandTypeInference
argument_list|()
argument_list|,
name|getOperandTypeChecker
argument_list|()
argument_list|,
name|getFunctionType
argument_list|()
argument_list|,
name|monotonicityInference
argument_list|)
return|;
block|}
comment|/** Returns a copy of this function with a given kind. */
specifier|public
name|SqlBasicFunction
name|withKind
parameter_list|(
name|SqlKind
name|kind
parameter_list|)
block|{
return|return
operator|new
name|SqlBasicFunction
argument_list|(
name|getName
argument_list|()
argument_list|,
name|kind
argument_list|,
name|syntax
argument_list|,
name|deterministic
argument_list|,
name|getReturnTypeInference
argument_list|()
argument_list|,
name|getOperandTypeInference
argument_list|()
argument_list|,
name|getOperandTypeChecker
argument_list|()
argument_list|,
name|getFunctionType
argument_list|()
argument_list|,
name|monotonicityInference
argument_list|)
return|;
block|}
comment|/** Returns a copy of this function with a given category. */
specifier|public
name|SqlBasicFunction
name|withFunctionType
parameter_list|(
name|SqlFunctionCategory
name|category
parameter_list|)
block|{
return|return
operator|new
name|SqlBasicFunction
argument_list|(
name|getName
argument_list|()
argument_list|,
name|kind
argument_list|,
name|syntax
argument_list|,
name|deterministic
argument_list|,
name|getReturnTypeInference
argument_list|()
argument_list|,
name|getOperandTypeInference
argument_list|()
argument_list|,
name|getOperandTypeChecker
argument_list|()
argument_list|,
name|category
argument_list|,
name|monotonicityInference
argument_list|)
return|;
block|}
comment|/** Returns a copy of this function with a given syntax. */
specifier|public
name|SqlBasicFunction
name|withSyntax
parameter_list|(
name|SqlSyntax
name|syntax
parameter_list|)
block|{
return|return
operator|new
name|SqlBasicFunction
argument_list|(
name|getName
argument_list|()
argument_list|,
name|kind
argument_list|,
name|syntax
argument_list|,
name|deterministic
argument_list|,
name|getReturnTypeInference
argument_list|()
argument_list|,
name|getOperandTypeInference
argument_list|()
argument_list|,
name|getOperandTypeChecker
argument_list|()
argument_list|,
name|getFunctionType
argument_list|()
argument_list|,
name|monotonicityInference
argument_list|)
return|;
block|}
comment|/** Returns a copy of this function with a given strategy for inferring    * the types of its operands. */
specifier|public
name|SqlBasicFunction
name|withOperandTypeInference
parameter_list|(
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|)
block|{
return|return
operator|new
name|SqlBasicFunction
argument_list|(
name|getName
argument_list|()
argument_list|,
name|kind
argument_list|,
name|syntax
argument_list|,
name|deterministic
argument_list|,
name|getReturnTypeInference
argument_list|()
argument_list|,
name|operandTypeInference
argument_list|,
name|getOperandTypeChecker
argument_list|()
argument_list|,
name|getFunctionType
argument_list|()
argument_list|,
name|monotonicityInference
argument_list|)
return|;
block|}
comment|/** Returns a copy of this function with a given determinism. */
specifier|public
name|SqlBasicFunction
name|withDeterministic
parameter_list|(
name|boolean
name|deterministic
parameter_list|)
block|{
return|return
operator|new
name|SqlBasicFunction
argument_list|(
name|getName
argument_list|()
argument_list|,
name|kind
argument_list|,
name|syntax
argument_list|,
name|deterministic
argument_list|,
name|getReturnTypeInference
argument_list|()
argument_list|,
name|getOperandTypeInference
argument_list|()
argument_list|,
name|getOperandTypeChecker
argument_list|()
argument_list|,
name|getFunctionType
argument_list|()
argument_list|,
name|monotonicityInference
argument_list|)
return|;
block|}
comment|/** Returns a copy of this function with a given strategy for inferring    * whether a call is monotonic. */
specifier|public
name|SqlBasicFunction
name|withMonotonicityInference
parameter_list|(
name|Function
argument_list|<
name|SqlOperatorBinding
argument_list|,
name|SqlMonotonicity
argument_list|>
name|monotonicityInference
parameter_list|)
block|{
return|return
operator|new
name|SqlBasicFunction
argument_list|(
name|getName
argument_list|()
argument_list|,
name|kind
argument_list|,
name|syntax
argument_list|,
name|deterministic
argument_list|,
name|getReturnTypeInference
argument_list|()
argument_list|,
name|getOperandTypeInference
argument_list|()
argument_list|,
name|getOperandTypeChecker
argument_list|()
argument_list|,
name|getFunctionType
argument_list|()
argument_list|,
name|monotonicityInference
argument_list|)
return|;
block|}
block|}
end_class

end_unit
