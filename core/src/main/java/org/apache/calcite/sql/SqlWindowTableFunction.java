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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeField
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
name|SqlOperandCountRanges
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
name|SqlOperandMetadata
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
name|type
operator|.
name|SqlTypeUtil
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
comment|/** The data source which the table function computes with. */
specifier|protected
specifier|static
specifier|final
name|String
name|PARAM_DATA
init|=
literal|"DATA"
decl_stmt|;
comment|/** The time attribute column. Also known as the event time. */
specifier|protected
specifier|static
specifier|final
name|String
name|PARAM_TIMECOL
init|=
literal|"TIMECOL"
decl_stmt|;
comment|/** The window duration INTERVAL. */
specifier|protected
specifier|static
specifier|final
name|String
name|PARAM_SIZE
init|=
literal|"SIZE"
decl_stmt|;
comment|/** The optional align offset for each window. */
specifier|protected
specifier|static
specifier|final
name|String
name|PARAM_OFFSET
init|=
literal|"OFFSET"
decl_stmt|;
comment|/** The session key(s), only used for SESSION window. */
specifier|protected
specifier|static
specifier|final
name|String
name|PARAM_KEY
init|=
literal|"KEY"
decl_stmt|;
comment|/** The slide interval, only used for HOP window. */
specifier|protected
specifier|static
specifier|final
name|String
name|PARAM_SLIDE
init|=
literal|"SLIDE"
decl_stmt|;
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
parameter_list|,
name|SqlOperandMetadata
name|operandMetadata
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
name|operandMetadata
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
name|SqlOperandMetadata
name|getOperandTypeChecker
parameter_list|()
block|{
return|return
operator|(
name|SqlOperandMetadata
operator|)
name|super
operator|.
name|getOperandTypeChecker
argument_list|()
return|;
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
comment|/** Partial implementation of operand type checker. */
specifier|protected
specifier|abstract
specifier|static
class|class
name|AbstractOperandMetadata
implements|implements
name|SqlOperandMetadata
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|paramNames
decl_stmt|;
specifier|final
name|int
name|mandatoryParamCount
decl_stmt|;
name|AbstractOperandMetadata
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|paramNames
parameter_list|,
name|int
name|mandatoryParamCount
parameter_list|)
block|{
name|this
operator|.
name|paramNames
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|paramNames
argument_list|)
expr_stmt|;
name|this
operator|.
name|mandatoryParamCount
operator|=
name|mandatoryParamCount
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|mandatoryParamCount
operator|>=
literal|0
operator|&&
name|mandatoryParamCount
operator|<=
name|paramNames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
block|{
return|return
name|SqlOperandCountRanges
operator|.
name|between
argument_list|(
name|mandatoryParamCount
argument_list|,
name|paramNames
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|RelDataType
argument_list|>
name|paramTypes
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|nCopies
argument_list|(
name|paramNames
operator|.
name|size
argument_list|()
argument_list|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|paramNames
parameter_list|()
block|{
return|return
name|paramNames
return|;
block|}
annotation|@
name|Override
specifier|public
name|Consistency
name|getConsistency
parameter_list|()
block|{
return|return
name|Consistency
operator|.
name|NONE
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isOptional
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|i
operator|>
name|getOperandCountRange
argument_list|()
operator|.
name|getMin
argument_list|()
operator|&&
name|i
operator|<=
name|getOperandCountRange
argument_list|()
operator|.
name|getMax
argument_list|()
return|;
block|}
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
comment|/**      * Checks whether the heading operands are in the form      * {@code (ROW, DESCRIPTOR, DESCRIPTOR ..., other params)},      * returning whether successful, and throwing if any columns are not found.      *      * @param callBinding The call binding      * @param descriptorCount The number of descriptors following the first      * operand (e.g. the table)      *      * @return true if validation passes; throws if any columns are not found      */
name|boolean
name|checkTableAndDescriptorOperands
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|int
name|descriptorCount
parameter_list|)
block|{
specifier|final
name|SqlNode
name|operand0
init|=
name|callBinding
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|SqlValidator
name|validator
init|=
name|callBinding
operator|.
name|getValidator
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|operand0
argument_list|)
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
name|ROW
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|descriptorCount
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|SqlNode
name|operand
init|=
name|callBinding
operator|.
name|operand
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|operand
operator|.
name|getKind
argument_list|()
operator|!=
name|SqlKind
operator|.
name|DESCRIPTOR
condition|)
block|{
return|return
literal|false
return|;
block|}
name|validateColumnNames
argument_list|(
name|validator
argument_list|,
name|type
operator|.
name|getFieldNames
argument_list|()
argument_list|,
operator|(
operator|(
name|SqlCall
operator|)
name|operand
operator|)
operator|.
name|getOperandList
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Checks whether the type that the operand of time col descriptor refers to is valid.      *      * @param callBinding The call binding      * @param pos The position of the descriptor at the operands of the call      * @return true if validation passes, false otherwise      */
name|boolean
name|checkTimeColumnDescriptorOperand
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|int
name|pos
parameter_list|)
block|{
name|SqlValidator
name|validator
init|=
name|callBinding
operator|.
name|getValidator
argument_list|()
decl_stmt|;
name|SqlNode
name|operand0
init|=
name|callBinding
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RelDataType
name|type
init|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|operand0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operands
init|=
operator|(
operator|(
name|SqlCall
operator|)
name|callBinding
operator|.
name|operand
argument_list|(
name|pos
argument_list|)
operator|)
operator|.
name|getOperandList
argument_list|()
decl_stmt|;
name|SqlIdentifier
name|identifier
init|=
operator|(
name|SqlIdentifier
operator|)
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|columnName
init|=
name|identifier
operator|.
name|getSimple
argument_list|()
decl_stmt|;
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
name|RelDataTypeField
name|field
range|:
name|type
operator|.
name|getFieldList
argument_list|()
control|)
block|{
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|columnName
argument_list|)
condition|)
block|{
return|return
name|SqlTypeUtil
operator|.
name|isTimestamp
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Checks whether the operands starting from position {@code startPos} are      * all of type {@code INTERVAL}, returning whether successful.      *      * @param callBinding The call binding      * @param startPos    The start position to validate (starting index is 0)      *      * @return true if validation passes      */
name|boolean
name|checkIntervalOperands
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|int
name|startPos
parameter_list|)
block|{
specifier|final
name|SqlValidator
name|validator
init|=
name|callBinding
operator|.
name|getValidator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|startPos
init|;
name|i
operator|<
name|callBinding
operator|.
name|getOperandCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|RelDataType
name|type
init|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|callBinding
operator|.
name|operand
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|isInterval
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
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
block|}
block|}
end_class

end_unit

