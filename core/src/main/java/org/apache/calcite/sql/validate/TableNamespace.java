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
name|plan
operator|.
name|RelOptTable
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
name|runtime
operator|.
name|PredicateImpl
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
name|schema
operator|.
name|ExtensibleTable
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
name|schema
operator|.
name|Table
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
name|schema
operator|.
name|impl
operator|.
name|ModifiableViewTable
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
name|sql
operator|.
name|SqlNodeList
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
name|base
operator|.
name|Predicate
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
comment|/** Namespace based on a table from the catalog. */
end_comment

begin_class
class|class
name|TableNamespace
extends|extends
name|AbstractNamespace
block|{
specifier|private
specifier|final
name|SqlValidatorTable
name|table
decl_stmt|;
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|RelDataTypeField
argument_list|>
name|extendedFields
decl_stmt|;
comment|/** Creates a TableNamespace. */
specifier|private
name|TableNamespace
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|,
name|SqlValidatorTable
name|table
parameter_list|,
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
parameter_list|)
block|{
name|super
argument_list|(
name|validator
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|table
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|table
argument_list|)
expr_stmt|;
name|this
operator|.
name|extendedFields
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|fields
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TableNamespace
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|,
name|SqlValidatorTable
name|table
parameter_list|)
block|{
name|this
argument_list|(
name|validator
argument_list|,
name|table
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelDataTypeField
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|RelDataType
name|validateImpl
parameter_list|(
name|RelDataType
name|targetRowType
parameter_list|)
block|{
if|if
condition|(
name|extendedFields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|table
operator|.
name|getRowType
argument_list|()
return|;
block|}
specifier|final
name|RelDataTypeFactory
operator|.
name|Builder
name|builder
init|=
name|validator
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|addAll
argument_list|(
name|table
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAll
argument_list|(
name|extendedFields
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
comment|// This is the only kind of namespace not based on a node in the parse tree.
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlValidatorTable
name|getTable
parameter_list|()
block|{
return|return
name|table
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|String
name|columnName
parameter_list|)
block|{
specifier|final
name|SqlValidatorTable
name|table
init|=
name|getTable
argument_list|()
decl_stmt|;
return|return
name|table
operator|.
name|getMonotonicity
argument_list|(
name|columnName
argument_list|)
return|;
block|}
comment|/** Creates a TableNamespace based on the same table as this one, but with    * extended fields.    *    *<p>Extended fields are "hidden" or undeclared fields that may nevertheless    * be present if you ask for them. Phoenix uses them, for instance, to access    * rarely used fields in the underlying HBase table. */
specifier|public
name|TableNamespace
name|extend
parameter_list|(
name|SqlNodeList
name|extendList
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|identifierList
init|=
name|Util
operator|.
name|quotientList
argument_list|(
name|extendList
operator|.
name|getList
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|SqlValidatorUtil
operator|.
name|checkIdentifierListForDuplicates
argument_list|(
name|identifierList
argument_list|,
name|validator
operator|.
name|getValidationErrorFunction
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|RelDataTypeField
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|addAll
argument_list|(
name|this
operator|.
name|extendedFields
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAll
argument_list|(
name|SqlValidatorUtil
operator|.
name|getExtendedColumns
argument_list|(
name|validator
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|getTable
argument_list|()
argument_list|,
name|extendList
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|extendedFields
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Table
name|schemaTable
init|=
name|table
operator|.
name|unwrap
argument_list|(
name|Table
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|schemaTable
operator|!=
literal|null
operator|&&
name|table
operator|instanceof
name|RelOptTable
operator|&&
operator|(
name|schemaTable
operator|instanceof
name|ExtensibleTable
operator|||
name|schemaTable
operator|instanceof
name|ModifiableViewTable
operator|)
condition|)
block|{
name|checkExtendedColumnTypes
argument_list|(
name|extendList
argument_list|)
expr_stmt|;
specifier|final
name|RelOptTable
name|relOptTable
init|=
operator|(
operator|(
name|RelOptTable
operator|)
name|table
operator|)
operator|.
name|extend
argument_list|(
name|extendedFields
argument_list|)
decl_stmt|;
specifier|final
name|SqlValidatorTable
name|validatorTable
init|=
name|relOptTable
operator|.
name|unwrap
argument_list|(
name|SqlValidatorTable
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
operator|new
name|TableNamespace
argument_list|(
name|validator
argument_list|,
name|validatorTable
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelDataTypeField
operator|>
name|of
argument_list|()
argument_list|)
return|;
block|}
return|return
operator|new
name|TableNamespace
argument_list|(
name|validator
argument_list|,
name|table
argument_list|,
name|extendedFields
argument_list|)
return|;
block|}
comment|/**    * Gets the data-type of all columns in a table (for a view table: including    * columns of the underlying table)    */
specifier|private
name|RelDataType
name|getBaseRowType
parameter_list|()
block|{
specifier|final
name|Table
name|schemaTable
init|=
name|table
operator|.
name|unwrap
argument_list|(
name|Table
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|schemaTable
operator|instanceof
name|ModifiableViewTable
condition|)
block|{
specifier|final
name|Table
name|underlying
init|=
operator|(
operator|(
name|ModifiableViewTable
operator|)
name|schemaTable
operator|)
operator|.
name|unwrap
argument_list|(
name|Table
operator|.
name|class
argument_list|)
decl_stmt|;
assert|assert
name|underlying
operator|!=
literal|null
assert|;
return|return
name|underlying
operator|.
name|getRowType
argument_list|(
name|validator
operator|.
name|typeFactory
argument_list|)
return|;
block|}
return|return
name|schemaTable
operator|.
name|getRowType
argument_list|(
name|validator
operator|.
name|typeFactory
argument_list|)
return|;
block|}
comment|/**    * Ensures that extended columns that have the same name as a base column also    * have the same data-type.    */
specifier|private
name|void
name|checkExtendedColumnTypes
parameter_list|(
name|SqlNodeList
name|extendList
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|extendedFields
init|=
name|SqlValidatorUtil
operator|.
name|getExtendedColumns
argument_list|(
name|validator
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|table
argument_list|,
name|extendList
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|baseFields
init|=
name|getBaseRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|nameToIndex
init|=
name|SqlValidatorUtil
operator|.
name|mapNameToIndex
argument_list|(
name|baseFields
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|RelDataTypeField
name|extendedField
range|:
name|extendedFields
control|)
block|{
specifier|final
name|String
name|extFieldName
init|=
name|extendedField
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|nameToIndex
operator|.
name|containsKey
argument_list|(
name|extFieldName
argument_list|)
condition|)
block|{
specifier|final
name|Integer
name|baseIndex
init|=
name|nameToIndex
operator|.
name|get
argument_list|(
name|extFieldName
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|baseType
init|=
name|baseFields
operator|.
name|get
argument_list|(
name|baseIndex
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|extType
init|=
name|extendedField
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|extType
operator|.
name|equals
argument_list|(
name|baseType
argument_list|)
condition|)
block|{
comment|// Get the extended column node that failed validation.
specifier|final
name|Predicate
argument_list|<
name|SqlNode
argument_list|>
name|nameMatches
init|=
operator|new
name|PredicateImpl
argument_list|<
name|SqlNode
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|test
parameter_list|(
name|SqlNode
name|sqlNode
parameter_list|)
block|{
if|if
condition|(
name|sqlNode
operator|instanceof
name|SqlIdentifier
condition|)
block|{
specifier|final
name|SqlIdentifier
name|identifier
init|=
operator|(
name|SqlIdentifier
operator|)
name|sqlNode
decl_stmt|;
return|return
name|Util
operator|.
name|last
argument_list|(
name|identifier
operator|.
name|names
argument_list|)
operator|.
name|equals
argument_list|(
name|extendedField
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|SqlNode
name|extColNode
init|=
name|Iterables
operator|.
name|find
argument_list|(
name|extendList
operator|.
name|getList
argument_list|()
argument_list|,
name|nameMatches
argument_list|)
decl_stmt|;
throw|throw
name|validator
operator|.
name|getValidationErrorFunction
argument_list|()
operator|.
name|apply
argument_list|(
name|extColNode
argument_list|,
name|RESOURCE
operator|.
name|typeNotAssignable
argument_list|(
name|baseFields
operator|.
name|get
argument_list|(
name|baseIndex
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|,
name|baseType
operator|.
name|getFullTypeString
argument_list|()
argument_list|,
name|extendedField
operator|.
name|getName
argument_list|()
argument_list|,
name|extType
operator|.
name|getFullTypeString
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End TableNamespace.java
end_comment

end_unit

