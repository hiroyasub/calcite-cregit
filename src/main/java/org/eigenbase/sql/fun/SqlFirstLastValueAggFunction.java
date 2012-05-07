begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|fun
package|;
end_package

begin_import
import|import
name|openjava
operator|.
name|mop
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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *<code>FIRST_VALUE</code> and<code>LAST_VALUE</code> aggregate functions  * return the first or the last value in a list of values that are input to the  * function.  */
end_comment

begin_class
specifier|public
class|class
name|SqlFirstLastValueAggFunction
extends|extends
name|SqlAggFunction
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|RelDataType
name|type
init|=
literal|null
decl_stmt|;
comment|// TODO:
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlFirstLastValueAggFunction
parameter_list|(
name|boolean
name|firstFlag
parameter_list|)
block|{
name|super
argument_list|(
operator|(
name|firstFlag
operator|)
condition|?
literal|"FIRST_VALUE"
else|:
literal|"LAST_VALUE"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|SqlTypeStrategies
operator|.
name|rtiFirstArgType
argument_list|,
literal|null
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcAny
argument_list|,
name|SqlFunctionCategory
operator|.
name|Numeric
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelDataType
index|[]
name|getParameterTypes
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
operator|new
name|RelDataType
index|[]
block|{
name|type
block|}
return|;
block|}
specifier|public
name|RelDataType
name|getReturnType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|type
return|;
block|}
specifier|public
name|OJClass
index|[]
name|getStartParameterTypes
parameter_list|()
block|{
return|return
operator|new
name|OJClass
index|[
literal|0
index|]
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlFirstLastValueAggFunction.java
end_comment

end_unit

