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
name|SqlCallBinding
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
name|SqlOperator
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
name|SqlTableFunction
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
name|type
operator|.
name|SqlTypeName
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
comment|/**  * Namespace whose contents are defined by the result of a call to a  * user-defined procedure.  */
end_comment

begin_class
specifier|public
class|class
name|ProcedureNamespace
extends|extends
name|AbstractNamespace
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlValidatorScope
name|scope
decl_stmt|;
specifier|private
specifier|final
name|SqlCall
name|call
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|ProcedureNamespace
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlCall
name|call
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
name|scope
operator|=
name|scope
expr_stmt|;
name|this
operator|.
name|call
operator|=
name|call
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|RelDataType
name|validateImpl
parameter_list|(
name|RelDataType
name|targetRowType
parameter_list|)
block|{
name|validator
operator|.
name|inferUnknownTypes
argument_list|(
name|validator
operator|.
name|unknownType
argument_list|,
name|scope
argument_list|,
name|call
argument_list|)
expr_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|validator
operator|.
name|deriveTypeImpl
argument_list|(
name|scope
argument_list|,
name|call
argument_list|)
decl_stmt|;
specifier|final
name|SqlOperator
name|operator
init|=
name|call
operator|.
name|getOperator
argument_list|()
decl_stmt|;
specifier|final
name|SqlCallBinding
name|callBinding
init|=
operator|new
name|SqlCallBinding
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|operator
operator|instanceof
name|SqlTableFunction
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Argument must be a table function: "
operator|+
name|operator
operator|.
name|getNameAsId
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|SqlTableFunction
name|tableFunction
init|=
operator|(
name|SqlTableFunction
operator|)
name|operator
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|!=
name|SqlTypeName
operator|.
name|CURSOR
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Table function should have CURSOR "
operator|+
literal|"type, not "
operator|+
name|type
argument_list|)
throw|;
block|}
specifier|final
name|SqlReturnTypeInference
name|rowTypeInference
init|=
name|tableFunction
operator|.
name|getRowTypeInference
argument_list|()
decl_stmt|;
return|return
name|requireNonNull
argument_list|(
name|rowTypeInference
operator|.
name|inferReturnType
argument_list|(
name|callBinding
argument_list|)
argument_list|,
parameter_list|()
lambda|->
literal|"got null from inferReturnType for call "
operator|+
name|callBinding
operator|.
name|getCall
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|call
return|;
block|}
block|}
end_class

end_unit

