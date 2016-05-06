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
name|SqlIdentifier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * A scope which contains nothing besides a few parameters. Like  * {@link EmptyScope} (which is its base class), it has no parent scope.  *  * @see ParameterNamespace  */
end_comment

begin_class
specifier|public
class|class
name|ParameterScope
extends|extends
name|EmptyScope
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * Map from the simple names of the parameters to types of the parameters    * ({@link RelDataType}).    */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
name|nameToTypeMap
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|ParameterScope
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
name|nameToTypeMap
parameter_list|)
block|{
name|super
argument_list|(
name|validator
argument_list|)
expr_stmt|;
name|this
operator|.
name|nameToTypeMap
operator|=
name|nameToTypeMap
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlQualified
name|fullyQualify
parameter_list|(
name|SqlIdentifier
name|identifier
parameter_list|)
block|{
return|return
name|SqlQualified
operator|.
name|create
argument_list|(
name|this
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|,
name|identifier
argument_list|)
return|;
block|}
specifier|public
name|SqlValidatorScope
name|getOperandScope
parameter_list|(
name|SqlCall
name|call
parameter_list|)
block|{
return|return
name|this
return|;
block|}
block|}
end_class

begin_comment
comment|// End ParameterScope.java
end_comment

end_unit

