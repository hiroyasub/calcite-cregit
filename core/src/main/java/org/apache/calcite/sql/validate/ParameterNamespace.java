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
name|SqlNode
import|;
end_import

begin_comment
comment|/**  * Namespace representing the type of a dynamic parameter.  *  * @see ParameterScope  */
end_comment

begin_class
class|class
name|ParameterNamespace
extends|extends
name|AbstractNamespace
block|{
comment|//~ Instance fields --------------------------------------------------------
annotation|@
name|SuppressWarnings
argument_list|(
literal|"HidingField"
argument_list|)
specifier|private
specifier|final
name|RelDataType
name|type
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|ParameterNamespace
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|validator
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
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
return|return
name|type
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|getRowType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
block|}
end_class

end_unit

