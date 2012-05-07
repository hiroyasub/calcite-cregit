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
name|resource
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
name|parser
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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|validate
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Operator which aggregates sets of values into a result.  *  * @author jack  * @version $Id$  * @since Jun 3, 2005  */
end_comment

begin_class
specifier|public
class|class
name|SqlRankFunction
extends|extends
name|SqlAggFunction
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RelDataType
name|type
init|=
literal|null
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlRankFunction
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|SqlTypeStrategies
operator|.
name|rtiInteger
argument_list|,
literal|null
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcNiladic
argument_list|,
name|SqlFunctionCategory
operator|.
name|Numeric
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
block|{
return|return
name|SqlOperandCountRange
operator|.
name|Zero
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
name|boolean
name|isAggregator
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|validateCall
parameter_list|(
name|SqlCall
name|call
parameter_list|,
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlValidatorScope
name|operandScope
parameter_list|)
block|{
specifier|final
name|SqlParserPos
name|pos
init|=
name|call
operator|.
name|getParserPosition
argument_list|()
decl_stmt|;
throw|throw
name|SqlUtil
operator|.
name|newContextException
argument_list|(
name|pos
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|FunctionUndefined
operator|.
name|ex
argument_list|(
name|call
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
end_class

begin_comment
comment|// End SqlRankFunction.java
end_comment

end_unit

