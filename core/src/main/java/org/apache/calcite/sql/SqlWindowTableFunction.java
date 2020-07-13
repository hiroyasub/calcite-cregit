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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
name|ReturnTypes
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
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|validate
operator|.
name|SqlNameMatcher
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
name|SqlValidator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * Base class for a table-valued function that computes windows. Examples  * include {@code TUMBLE}, {@code HOP} and {@code SESSION}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlWindowTableFunction
extends|extends
name|SqlFunction
implements|implements
name|SqlTableFunction
block|{
comment|/**    * Type-inference strategy whereby the row type of a table function call is a    * ROW, which is combined from the row type of operand #0 (which is a TABLE)    * and two additional fields. The fields are as follows:    *    *<ol>    *<li>{@code window_start}: TIMESTAMP type to indicate a window's start    *<li>{@code window_end}: TIMESTAMP type to indicate a window's end    *</ol>    */
specifier|public
specifier|static
specifier|final
name|SqlReturnTypeInference
name|ARG0_TABLE_FUNCTION_WINDOWING
init|=
name|SqlWindowTableFunction
operator|::
name|inferRowType
decl_stmt|;
comment|/** Creates a window table function with a given name. */
specifier|public
name|SqlWindowTableFunction
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
name|ReturnTypes
operator|.
name|CURSOR
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|SqlFunctionCategory
operator|.
name|SYSTEM
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SqlReturnTypeInference
name|getRowTypeInference
parameter_list|()
block|{
return|return
name|ARG0_TABLE_FUNCTION_WINDOWING
return|;
block|}
specifier|protected
name|boolean
name|throwValidationSignatureErrorOrReturnFalse
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
if|if
condition|(
name|throwOnFailure
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newValidationSignatureError
argument_list|()
throw|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|protected
name|void
name|validateColumnNames
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|,
name|List
argument_list|<
name|SqlNode
argument_list|>
name|columnNames
parameter_list|)
block|{
specifier|final
name|SqlNameMatcher
name|matcher
init|=
name|validator
operator|.
name|getCatalogReader
argument_list|()
operator|.
name|nameMatcher
argument_list|()
decl_stmt|;
for|for
control|(
name|SqlNode
name|columnName
range|:
name|columnNames
control|)
block|{
specifier|final
name|String
name|name
init|=
operator|(
operator|(
name|SqlIdentifier
operator|)
name|columnName
operator|)
operator|.
name|getSimple
argument_list|()
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|indexOf
argument_list|(
name|fieldNames
argument_list|,
name|name
argument_list|)
operator|<
literal|0
condition|)
block|{
throw|throw
name|SqlUtil
operator|.
name|newContextException
argument_list|(
name|columnName
operator|.
name|getParserPosition
argument_list|()
argument_list|,
name|RESOURCE
operator|.
name|unknownIdentifier
argument_list|(
name|name
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * {@inheritDoc}    *    *<p>Overrides because the first parameter of    * table-value function windowing is an explicit TABLE parameter,    * which is not scalar.    */
annotation|@
name|Override
specifier|public
name|boolean
name|argumentMustBeScalar
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
name|ordinal
operator|!=
literal|0
return|;
block|}
comment|/** Helper for {@link #ARG0_TABLE_FUNCTION_WINDOWING}. */
specifier|private
specifier|static
name|RelDataType
name|inferRowType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
specifier|final
name|RelDataType
name|inputRowType
init|=
name|opBinding
operator|.
name|getOperandType
argument_list|(
literal|0
argument_list|)
decl_stmt|;
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
name|timestampType
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
decl_stmt|;
return|return
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|kind
argument_list|(
name|inputRowType
operator|.
name|getStructKind
argument_list|()
argument_list|)
operator|.
name|addAll
argument_list|(
name|inputRowType
operator|.
name|getFieldList
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"window_start"
argument_list|,
name|timestampType
argument_list|)
operator|.
name|add
argument_list|(
literal|"window_end"
argument_list|,
name|timestampType
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

