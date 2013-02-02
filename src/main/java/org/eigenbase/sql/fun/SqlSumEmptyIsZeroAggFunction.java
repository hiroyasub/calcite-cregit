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
name|fun
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
comment|/**  *<code>Sum0</code> is an aggregator which returns the sum of the values which  * go into it like<code>Sum</code>. It differs in that when no non null values  * are applied zero is returned instead of null. Can be used along with<code>  * Count</code> to implement<code>Sum</code>.  *  * @author jhahn  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|SqlSumEmptyIsZeroAggFunction
extends|extends
name|SqlAggFunction
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RelDataType
name|type
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlSumEmptyIsZeroAggFunction
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
name|super
argument_list|(
literal|"$SUM0"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|SqlTypeStrategies
operator|.
name|rtiFirstArgTypeForceNullable
argument_list|,
literal|null
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcNumeric
argument_list|,
name|SqlFunctionCategory
operator|.
name|Numeric
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
name|getType
parameter_list|()
block|{
return|return
name|type
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
block|}
end_class

begin_comment
comment|// End SqlSumEmptyIsZeroAggFunction.java
end_comment

end_unit

