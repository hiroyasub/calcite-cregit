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
name|test
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Mock operator table for testing purposes. Contains the standard SQL operator  * table, plus a list of operators.  */
end_comment

begin_class
specifier|public
class|class
name|MockSqlOperatorTable
extends|extends
name|ChainedSqlOperatorTable
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|ListSqlOperatorTable
name|listOpTab
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|MockSqlOperatorTable
parameter_list|(
name|SqlOperatorTable
name|parentTable
parameter_list|)
block|{
name|super
argument_list|(
name|Arrays
operator|.
expr|<
name|SqlOperatorTable
operator|>
name|asList
argument_list|(
name|parentTable
argument_list|,
operator|new
name|ListSqlOperatorTable
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|listOpTab
operator|=
operator|(
name|ListSqlOperatorTable
operator|)
name|tableList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Adds an operator to this table.      */
specifier|public
name|void
name|addOperator
parameter_list|(
name|SqlOperator
name|op
parameter_list|)
block|{
name|listOpTab
operator|.
name|add
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|addRamp
parameter_list|(
name|MockSqlOperatorTable
name|opTab
parameter_list|)
block|{
name|opTab
operator|.
name|addOperator
argument_list|(
operator|new
name|SqlFunction
argument_list|(
literal|"RAMP"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcNumeric
argument_list|,
name|SqlFunctionCategory
operator|.
name|UserDefinedFunction
argument_list|)
block|{
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
index|[]
name|types
init|=
block|{
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
block|}
decl_stmt|;
specifier|final
name|String
index|[]
name|fieldNames
init|=
operator|new
name|String
index|[]
block|{
literal|"I"
block|}
decl_stmt|;
return|return
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|types
argument_list|,
name|fieldNames
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|opTab
operator|.
name|addOperator
argument_list|(
operator|new
name|SqlFunction
argument_list|(
literal|"DEDUP"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcVariadic
argument_list|,
name|SqlFunctionCategory
operator|.
name|UserDefinedFunction
argument_list|)
block|{
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
index|[]
name|types
init|=
block|{
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
literal|1024
argument_list|)
block|}
decl_stmt|;
specifier|final
name|String
index|[]
name|fieldNames
init|=
operator|new
name|String
index|[]
block|{
literal|"NAME"
block|}
decl_stmt|;
return|return
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|types
argument_list|,
name|fieldNames
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End MockSqlOperatorTable.java
end_comment

end_unit

