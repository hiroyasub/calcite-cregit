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
name|SqlNode
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
name|util
operator|.
name|Pair
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
name|util
operator|.
name|Util
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
name|List
import|;
end_import

begin_comment
comment|/**  * Abstract implementation of {@link SqlValidatorNamespace}.  */
end_comment

begin_class
specifier|abstract
class|class
name|AbstractNamespace
implements|implements
name|SqlValidatorNamespace
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|SqlValidatorImpl
name|validator
decl_stmt|;
comment|/**    * Whether this scope is currently being validated. Used to check for    * cycles.    */
specifier|private
name|SqlValidatorImpl
operator|.
name|Status
name|status
init|=
name|SqlValidatorImpl
operator|.
name|Status
operator|.
name|UNVALIDATED
decl_stmt|;
comment|/**    * Type of the output row, which comprises the name and type of each output    * column. Set on validate.    */
specifier|protected
name|RelDataType
name|rowType
decl_stmt|;
comment|/** As {@link #rowType}, but not necessarily a struct. */
specifier|protected
name|RelDataType
name|type
decl_stmt|;
specifier|private
name|boolean
name|forceNullable
decl_stmt|;
specifier|protected
specifier|final
name|SqlNode
name|enclosingNode
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an AbstractNamespace.    *    * @param validator     Validator    * @param enclosingNode Enclosing node    */
name|AbstractNamespace
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|,
name|SqlNode
name|enclosingNode
parameter_list|)
block|{
name|this
operator|.
name|validator
operator|=
name|validator
expr_stmt|;
name|this
operator|.
name|enclosingNode
operator|=
name|enclosingNode
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlValidator
name|getValidator
parameter_list|()
block|{
return|return
name|validator
return|;
block|}
specifier|public
specifier|final
name|void
name|validate
parameter_list|()
block|{
switch|switch
condition|(
name|status
condition|)
block|{
case|case
name|UNVALIDATED
case|:
try|try
block|{
name|status
operator|=
name|SqlValidatorImpl
operator|.
name|Status
operator|.
name|IN_PROGRESS
expr_stmt|;
name|Util
operator|.
name|permAssert
argument_list|(
name|rowType
operator|==
literal|null
argument_list|,
literal|"Namespace.rowType must be null before validate has been called"
argument_list|)
expr_stmt|;
name|RelDataType
name|type
init|=
name|validateImpl
argument_list|()
decl_stmt|;
name|Util
operator|.
name|permAssert
argument_list|(
name|type
operator|!=
literal|null
argument_list|,
literal|"validateImpl() returned null"
argument_list|)
expr_stmt|;
if|if
condition|(
name|forceNullable
condition|)
block|{
comment|// REVIEW jvs 10-Oct-2005: This may not be quite right
comment|// if it means that nullability will be forced in the
comment|// ON clause where it doesn't belong.
name|type
operator|=
name|validator
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createTypeWithNullability
argument_list|(
name|type
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|status
operator|=
name|SqlValidatorImpl
operator|.
name|Status
operator|.
name|VALID
expr_stmt|;
block|}
break|break;
case|case
name|IN_PROGRESS
case|:
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
literal|"todo: Cycle detected during type-checking"
argument_list|)
throw|;
case|case
name|VALID
case|:
break|break;
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|status
argument_list|)
throw|;
block|}
block|}
comment|/**    * Validates this scope and returns the type of the records it returns.    * External users should call {@link #validate}, which uses the    * {@link #status} field to protect against cycles.    *    * @return record data type, never null    */
specifier|protected
specifier|abstract
name|RelDataType
name|validateImpl
parameter_list|()
function_decl|;
specifier|public
name|RelDataType
name|getRowType
parameter_list|()
block|{
if|if
condition|(
name|rowType
operator|==
literal|null
condition|)
block|{
name|validator
operator|.
name|validateNamespace
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|Util
operator|.
name|permAssert
argument_list|(
name|rowType
operator|!=
literal|null
argument_list|,
literal|"validate must set rowType"
argument_list|)
expr_stmt|;
block|}
return|return
name|rowType
return|;
block|}
specifier|public
name|RelDataType
name|getRowTypeSansSystemColumns
parameter_list|()
block|{
return|return
name|getRowType
argument_list|()
return|;
block|}
specifier|public
name|RelDataType
name|getType
parameter_list|()
block|{
name|Util
operator|.
name|discard
argument_list|(
name|getRowType
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|type
return|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|convertToStruct
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlNode
name|getEnclosingNode
parameter_list|()
block|{
return|return
name|enclosingNode
return|;
block|}
specifier|public
name|SqlValidatorTable
name|getTable
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|SqlValidatorNamespace
name|lookupChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|validator
operator|.
name|lookupFieldNamespace
argument_list|(
name|getRowType
argument_list|()
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|fieldExists
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|RelDataType
name|rowType
init|=
name|getRowType
argument_list|()
decl_stmt|;
return|return
name|validator
operator|.
name|catalogReader
operator|.
name|field
argument_list|(
name|rowType
argument_list|,
name|name
argument_list|)
operator|!=
literal|null
return|;
block|}
specifier|public
name|List
argument_list|<
name|Pair
argument_list|<
name|SqlNode
argument_list|,
name|SqlMonotonicity
argument_list|>
argument_list|>
name|getMonotonicExprs
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|String
name|columnName
parameter_list|)
block|{
return|return
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
return|;
block|}
specifier|public
name|void
name|makeNullable
parameter_list|()
block|{
name|forceNullable
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|String
name|translate
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|name
return|;
block|}
specifier|public
name|SqlValidatorNamespace
name|resolve
parameter_list|()
block|{
return|return
name|this
return|;
block|}
specifier|public
name|boolean
name|supportsModality
parameter_list|(
name|SqlModality
name|modality
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isWrapperFor
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
name|clazz
operator|.
name|isInstance
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|protected
name|RelDataType
name|convertToStruct
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
comment|// "MULTISET [<expr>, ...]" needs to be wrapped in a record if
comment|//<expr> has a scalar type.
comment|// For example, "MULTISET [8, 9]" has type
comment|// "RECORD(INTEGER EXPR$0 NOT NULL) NOT NULL MULTISET NOT NULL".
specifier|final
name|RelDataType
name|componentType
init|=
name|type
operator|.
name|getComponentType
argument_list|()
decl_stmt|;
if|if
condition|(
name|componentType
operator|==
literal|null
operator|||
name|componentType
operator|.
name|isStruct
argument_list|()
condition|)
block|{
return|return
name|type
return|;
block|}
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|validator
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|structType
init|=
name|toStruct
argument_list|(
name|componentType
argument_list|,
name|getNode
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|collectionType
decl_stmt|;
switch|switch
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|ARRAY
case|:
name|collectionType
operator|=
name|typeFactory
operator|.
name|createArrayType
argument_list|(
name|structType
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
break|break;
case|case
name|MULTISET
case|:
name|collectionType
operator|=
name|typeFactory
operator|.
name|createMultisetType
argument_list|(
name|structType
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|type
argument_list|)
throw|;
block|}
return|return
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|collectionType
argument_list|,
name|type
operator|.
name|isNullable
argument_list|()
argument_list|)
return|;
block|}
comment|/** Converts a type to a struct if it is not already. */
specifier|protected
name|RelDataType
name|toStruct
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|SqlNode
name|unnest
parameter_list|)
block|{
if|if
condition|(
name|type
operator|.
name|isStruct
argument_list|()
condition|)
block|{
return|return
name|type
return|;
block|}
return|return
name|validator
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
name|validator
operator|.
name|deriveAlias
argument_list|(
name|unnest
argument_list|,
literal|0
argument_list|)
argument_list|,
name|type
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End AbstractNamespace.java
end_comment

end_unit

