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
name|prepare
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
name|adapter
operator|.
name|java
operator|.
name|JavaTypeFactory
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
name|SqlOperatorTable
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
name|SqlConformance
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
name|SqlValidatorImpl
import|;
end_import

begin_comment
comment|/** Validator. */
end_comment

begin_class
class|class
name|CalciteSqlValidator
extends|extends
name|SqlValidatorImpl
block|{
specifier|public
name|CalciteSqlValidator
parameter_list|(
name|SqlOperatorTable
name|opTab
parameter_list|,
name|CalciteCatalogReader
name|catalogReader
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|SqlConformance
name|conformance
parameter_list|)
block|{
name|super
argument_list|(
name|opTab
argument_list|,
name|catalogReader
argument_list|,
name|typeFactory
argument_list|,
name|conformance
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|RelDataType
name|getLogicalSourceRowType
parameter_list|(
name|RelDataType
name|sourceRowType
parameter_list|,
name|SqlInsert
name|insert
parameter_list|)
block|{
specifier|final
name|RelDataType
name|superType
init|=
name|super
operator|.
name|getLogicalSourceRowType
argument_list|(
name|sourceRowType
argument_list|,
name|insert
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|JavaTypeFactory
operator|)
name|typeFactory
operator|)
operator|.
name|toSql
argument_list|(
name|superType
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|RelDataType
name|getLogicalTargetRowType
parameter_list|(
name|RelDataType
name|targetRowType
parameter_list|,
name|SqlInsert
name|insert
parameter_list|)
block|{
specifier|final
name|RelDataType
name|superType
init|=
name|super
operator|.
name|getLogicalTargetRowType
argument_list|(
name|targetRowType
argument_list|,
name|insert
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|JavaTypeFactory
operator|)
name|typeFactory
operator|)
operator|.
name|toSql
argument_list|(
name|superType
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End CalciteSqlValidator.java
end_comment

end_unit

