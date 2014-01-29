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
name|type
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
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
name|rel
operator|.
name|metadata
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
name|*
import|;
end_import

begin_comment
comment|/**  * TableFunctionReturnTypeInference implements rules for deriving table function  * output row types by expanding references to cursor parameters.  */
end_comment

begin_class
specifier|public
class|class
name|TableFunctionReturnTypeInference
extends|extends
name|ExplicitReturnTypeInference
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|paramNames
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|RelColumnMapping
argument_list|>
name|columnMappings
decl_stmt|;
comment|// not re-entrant!
specifier|private
specifier|final
name|boolean
name|isPassthrough
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|TableFunctionReturnTypeInference
parameter_list|(
name|RelProtoDataType
name|unexpandedOutputType
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|paramNames
parameter_list|,
name|boolean
name|isPassthrough
parameter_list|)
block|{
name|super
argument_list|(
name|unexpandedOutputType
argument_list|)
expr_stmt|;
name|this
operator|.
name|paramNames
operator|=
name|paramNames
expr_stmt|;
name|this
operator|.
name|isPassthrough
operator|=
name|isPassthrough
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Set
argument_list|<
name|RelColumnMapping
argument_list|>
name|getColumnMappings
parameter_list|()
block|{
return|return
name|columnMappings
return|;
block|}
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
name|columnMappings
operator|=
operator|new
name|HashSet
argument_list|<
name|RelColumnMapping
argument_list|>
argument_list|()
expr_stmt|;
name|RelDataType
name|unexpandedOutputType
init|=
name|protoType
operator|.
name|apply
argument_list|(
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RelDataType
argument_list|>
name|expandedOutputTypes
init|=
operator|new
name|ArrayList
argument_list|<
name|RelDataType
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expandedFieldNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|unexpandedOutputType
operator|.
name|getFieldList
argument_list|()
control|)
block|{
name|RelDataType
name|fieldType
init|=
name|field
operator|.
name|getType
argument_list|()
decl_stmt|;
name|String
name|fieldName
init|=
name|field
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldType
operator|.
name|getSqlTypeName
argument_list|()
operator|!=
name|SqlTypeName
operator|.
name|CURSOR
condition|)
block|{
name|expandedOutputTypes
operator|.
name|add
argument_list|(
name|fieldType
argument_list|)
expr_stmt|;
name|expandedFieldNames
operator|.
name|add
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// Look up position of cursor parameter with same name as output
comment|// field, also counting how many cursors appear before it
comment|// (need this for correspondence with RelNode child position).
name|int
name|paramOrdinal
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|iCursor
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|paramNames
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|paramNames
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|paramOrdinal
operator|=
name|i
expr_stmt|;
break|break;
block|}
name|RelDataType
name|cursorType
init|=
name|opBinding
operator|.
name|getCursorOperand
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|cursorType
operator|!=
literal|null
condition|)
block|{
operator|++
name|iCursor
expr_stmt|;
block|}
block|}
assert|assert
name|paramOrdinal
operator|!=
operator|-
literal|1
assert|;
comment|// Translate to actual argument type.
name|boolean
name|isRowOp
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|columnNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|RelDataType
name|cursorType
init|=
name|opBinding
operator|.
name|getCursorOperand
argument_list|(
name|paramOrdinal
argument_list|)
decl_stmt|;
if|if
condition|(
name|cursorType
operator|==
literal|null
condition|)
block|{
name|isRowOp
operator|=
literal|true
expr_stmt|;
name|String
name|parentCursorName
init|=
name|opBinding
operator|.
name|getColumnListParamInfo
argument_list|(
name|paramOrdinal
argument_list|,
name|fieldName
argument_list|,
name|columnNames
argument_list|)
decl_stmt|;
assert|assert
name|parentCursorName
operator|!=
literal|null
assert|;
name|paramOrdinal
operator|=
operator|-
literal|1
expr_stmt|;
name|iCursor
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|paramNames
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|paramNames
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|parentCursorName
argument_list|)
condition|)
block|{
name|paramOrdinal
operator|=
name|i
expr_stmt|;
break|break;
block|}
name|cursorType
operator|=
name|opBinding
operator|.
name|getCursorOperand
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|cursorType
operator|!=
literal|null
condition|)
block|{
operator|++
name|iCursor
expr_stmt|;
block|}
block|}
name|cursorType
operator|=
name|opBinding
operator|.
name|getCursorOperand
argument_list|(
name|paramOrdinal
argument_list|)
expr_stmt|;
assert|assert
name|cursorType
operator|!=
literal|null
assert|;
block|}
comment|// And expand. Function output is always nullable... except system
comment|// fields.
name|int
name|iInputColumn
decl_stmt|;
if|if
condition|(
name|isRowOp
condition|)
block|{
for|for
control|(
name|String
name|columnName
range|:
name|columnNames
control|)
block|{
name|iInputColumn
operator|=
operator|-
literal|1
expr_stmt|;
name|RelDataTypeField
name|cursorField
init|=
literal|null
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|cField
range|:
name|cursorType
operator|.
name|getFieldList
argument_list|()
control|)
block|{
operator|++
name|iInputColumn
expr_stmt|;
if|if
condition|(
name|cField
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|columnName
argument_list|)
condition|)
block|{
name|cursorField
operator|=
name|cField
expr_stmt|;
break|break;
block|}
block|}
name|addOutputColumn
argument_list|(
name|expandedFieldNames
argument_list|,
name|expandedOutputTypes
argument_list|,
name|iInputColumn
argument_list|,
name|iCursor
argument_list|,
name|opBinding
argument_list|,
name|cursorField
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|iInputColumn
operator|=
operator|-
literal|1
expr_stmt|;
for|for
control|(
name|RelDataTypeField
name|cursorField
range|:
name|cursorType
operator|.
name|getFieldList
argument_list|()
control|)
block|{
operator|++
name|iInputColumn
expr_stmt|;
name|addOutputColumn
argument_list|(
name|expandedFieldNames
argument_list|,
name|expandedOutputTypes
argument_list|,
name|iInputColumn
argument_list|,
name|iCursor
argument_list|,
name|opBinding
argument_list|,
name|cursorField
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createStructType
argument_list|(
name|expandedOutputTypes
argument_list|,
name|expandedFieldNames
argument_list|)
return|;
block|}
specifier|private
name|void
name|addOutputColumn
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|expandedFieldNames
parameter_list|,
name|List
argument_list|<
name|RelDataType
argument_list|>
name|expandedOutputTypes
parameter_list|,
name|int
name|iInputColumn
parameter_list|,
name|int
name|iCursor
parameter_list|,
name|SqlOperatorBinding
name|opBinding
parameter_list|,
name|RelDataTypeField
name|cursorField
parameter_list|)
block|{
name|columnMappings
operator|.
name|add
argument_list|(
operator|new
name|RelColumnMapping
argument_list|(
name|expandedFieldNames
operator|.
name|size
argument_list|()
argument_list|,
name|iCursor
argument_list|,
name|iInputColumn
argument_list|,
operator|!
name|isPassthrough
argument_list|)
argument_list|)
expr_stmt|;
comment|// As a special case, system fields are implicitly NOT NULL.
comment|// A badly behaved UDX can still provide NULL values, so the
comment|// system must ensure that each generated system field has a
comment|// reasonable value.
name|boolean
name|nullable
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|opBinding
operator|instanceof
name|SqlCallBinding
condition|)
block|{
name|SqlCallBinding
name|sqlCallBinding
init|=
operator|(
name|SqlCallBinding
operator|)
name|opBinding
decl_stmt|;
if|if
condition|(
name|sqlCallBinding
operator|.
name|getValidator
argument_list|()
operator|.
name|isSystemField
argument_list|(
name|cursorField
argument_list|)
condition|)
block|{
name|nullable
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|RelDataType
name|nullableType
init|=
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createTypeWithNullability
argument_list|(
name|cursorField
operator|.
name|getType
argument_list|()
argument_list|,
name|nullable
argument_list|)
decl_stmt|;
comment|// Make sure there are no duplicates in the output column names
for|for
control|(
name|String
name|fieldName
range|:
name|expandedFieldNames
control|)
block|{
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
name|cursorField
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
name|opBinding
operator|.
name|newError
argument_list|(
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|DuplicateColumnName
operator|.
name|ex
argument_list|(
name|cursorField
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
name|expandedOutputTypes
operator|.
name|add
argument_list|(
name|nullableType
argument_list|)
expr_stmt|;
name|expandedFieldNames
operator|.
name|add
argument_list|(
name|cursorField
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End TableFunctionReturnTypeInference.java
end_comment

end_unit

