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
name|schema
operator|.
name|impl
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
name|plan
operator|.
name|RelOptUtil
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeImpl
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
name|RelProtoDataType
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
name|rex
operator|.
name|RexBuilder
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
name|rex
operator|.
name|RexNode
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
name|ColumnStrategy
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
name|ModifiableView
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
name|Path
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
name|Wrapper
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
name|SqlFunction
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
name|sql2rel
operator|.
name|InitializerContext
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
name|sql2rel
operator|.
name|InitializerExpressionFactory
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
name|sql2rel
operator|.
name|NullInitializerExpressionFactory
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
name|ImmutableIntList
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
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|sql
operator|.
name|validate
operator|.
name|SqlValidatorUtil
operator|.
name|mapNameToIndex
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/** Extension to {@link ViewTable} that is modifiable. */
end_comment

begin_class
specifier|public
class|class
name|ModifiableViewTable
extends|extends
name|ViewTable
implements|implements
name|ModifiableView
implements|,
name|Wrapper
block|{
specifier|private
specifier|final
name|Table
name|table
decl_stmt|;
specifier|private
specifier|final
name|Path
name|tablePath
decl_stmt|;
specifier|private
specifier|final
name|RexNode
name|constraint
decl_stmt|;
specifier|private
specifier|final
name|ImmutableIntList
name|columnMapping
decl_stmt|;
specifier|private
specifier|final
name|InitializerExpressionFactory
name|initializerExpressionFactory
decl_stmt|;
comment|/** Creates a ModifiableViewTable. */
specifier|public
name|ModifiableViewTable
parameter_list|(
name|Type
name|elementType
parameter_list|,
name|RelProtoDataType
name|rowType
parameter_list|,
name|String
name|viewSql
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|,
annotation|@
name|Nullable
name|List
argument_list|<
name|String
argument_list|>
name|viewPath
parameter_list|,
name|Table
name|table
parameter_list|,
name|Path
name|tablePath
parameter_list|,
name|RexNode
name|constraint
parameter_list|,
name|ImmutableIntList
name|columnMapping
parameter_list|)
block|{
name|super
argument_list|(
name|elementType
argument_list|,
name|rowType
argument_list|,
name|viewSql
argument_list|,
name|schemaPath
argument_list|,
name|viewPath
argument_list|)
expr_stmt|;
name|this
operator|.
name|table
operator|=
name|table
expr_stmt|;
name|this
operator|.
name|tablePath
operator|=
name|tablePath
expr_stmt|;
name|this
operator|.
name|constraint
operator|=
name|constraint
expr_stmt|;
name|this
operator|.
name|columnMapping
operator|=
name|columnMapping
expr_stmt|;
name|this
operator|.
name|initializerExpressionFactory
operator|=
operator|new
name|ModifiableViewTableInitializerExpressionFactory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RexNode
name|getConstraint
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RelDataType
name|tableRowType
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|copy
argument_list|(
name|constraint
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ImmutableIntList
name|getColumnMapping
parameter_list|()
block|{
return|return
name|columnMapping
return|;
block|}
annotation|@
name|Override
specifier|public
name|Table
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
name|Path
name|getTablePath
parameter_list|()
block|{
return|return
name|tablePath
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|C
extends|extends
name|Object
parameter_list|>
annotation|@
name|Nullable
name|C
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|C
argument_list|>
name|aClass
parameter_list|)
block|{
if|if
condition|(
name|aClass
operator|.
name|isInstance
argument_list|(
name|initializerExpressionFactory
argument_list|)
condition|)
block|{
return|return
name|aClass
operator|.
name|cast
argument_list|(
name|initializerExpressionFactory
argument_list|)
return|;
block|}
if|else if
condition|(
name|aClass
operator|.
name|isInstance
argument_list|(
name|table
argument_list|)
condition|)
block|{
return|return
name|aClass
operator|.
name|cast
argument_list|(
name|table
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|unwrap
argument_list|(
name|aClass
argument_list|)
return|;
block|}
comment|/**    * Extends the underlying table and returns a new view with updated row-type    * and column-mapping.    *    *<p>The type factory is used to perform some scratch calculations, viz the    * type mapping, but the "real" row-type will be assigned later, when the    * table has been bound to the statement's type factory. The is important,    * because adding types to type factories that do not belong to a statement    * could potentially leak memory.    *    * @param extendedColumns Extended fields    * @param typeFactory Type factory    */
specifier|public
specifier|final
name|ModifiableViewTable
name|extend
parameter_list|(
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|extendedColumns
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
specifier|final
name|ExtensibleTable
name|underlying
init|=
name|unwrap
argument_list|(
name|ExtensibleTable
operator|.
name|class
argument_list|)
decl_stmt|;
assert|assert
name|underlying
operator|!=
literal|null
assert|;
specifier|final
name|RelDataTypeFactory
operator|.
name|Builder
name|builder
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|getRowType
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|column
range|:
name|rowType
operator|.
name|getFieldList
argument_list|()
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|column
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|RelDataTypeField
name|column
range|:
name|extendedColumns
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|column
argument_list|)
expr_stmt|;
block|}
comment|// The characteristics of the new view.
specifier|final
name|RelDataType
name|newRowType
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableIntList
name|newColumnMapping
init|=
name|getNewColumnMapping
argument_list|(
name|underlying
argument_list|,
name|getColumnMapping
argument_list|()
argument_list|,
name|extendedColumns
argument_list|,
name|typeFactory
argument_list|)
decl_stmt|;
comment|// Extend the underlying table with only the fields that
comment|// duplicate column names in neither the view nor the base table.
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|underlyingColumns
init|=
name|underlying
operator|.
name|getRowType
argument_list|(
name|typeFactory
argument_list|)
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|columnsOfExtendedBaseTable
init|=
name|RelOptUtil
operator|.
name|deduplicateColumns
argument_list|(
name|underlyingColumns
argument_list|,
name|extendedColumns
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|extendColumnsOfBaseTable
init|=
name|columnsOfExtendedBaseTable
operator|.
name|subList
argument_list|(
name|underlyingColumns
operator|.
name|size
argument_list|()
argument_list|,
name|columnsOfExtendedBaseTable
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Table
name|extendedTable
init|=
name|underlying
operator|.
name|extend
argument_list|(
name|extendColumnsOfBaseTable
argument_list|)
decl_stmt|;
return|return
name|extend
argument_list|(
name|extendedTable
argument_list|,
name|RelDataTypeImpl
operator|.
name|proto
argument_list|(
name|newRowType
argument_list|)
argument_list|,
name|newColumnMapping
argument_list|)
return|;
block|}
comment|/**    * Creates a mapping from the view index to the index in the underlying table.    */
specifier|private
specifier|static
name|ImmutableIntList
name|getNewColumnMapping
parameter_list|(
name|Table
name|underlying
parameter_list|,
name|ImmutableIntList
name|oldColumnMapping
parameter_list|,
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|extendedColumns
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|baseColumns
init|=
name|underlying
operator|.
name|getRowType
argument_list|(
name|typeFactory
argument_list|)
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
name|mapNameToIndex
argument_list|(
name|baseColumns
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|Integer
argument_list|>
name|newMapping
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
name|newMapping
operator|.
name|addAll
argument_list|(
name|oldColumnMapping
argument_list|)
expr_stmt|;
name|int
name|newMappedIndex
init|=
name|baseColumns
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|extendedColumn
range|:
name|extendedColumns
control|)
block|{
name|String
name|extendedColumnName
init|=
name|extendedColumn
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
name|extendedColumnName
argument_list|)
condition|)
block|{
comment|// The extended column duplicates a column in the underlying table.
comment|// Map to the index in the underlying table.
name|newMapping
operator|.
name|add
argument_list|(
name|nameToIndex
operator|.
name|get
argument_list|(
name|extendedColumnName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// The extended column is not in the underlying table.
name|newMapping
operator|.
name|add
argument_list|(
name|newMappedIndex
operator|++
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
name|newMapping
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|ModifiableViewTable
name|extend
parameter_list|(
name|Table
name|extendedTable
parameter_list|,
name|RelProtoDataType
name|protoRowType
parameter_list|,
name|ImmutableIntList
name|newColumnMapping
parameter_list|)
block|{
return|return
operator|new
name|ModifiableViewTable
argument_list|(
name|getElementType
argument_list|()
argument_list|,
name|protoRowType
argument_list|,
name|getViewSql
argument_list|()
argument_list|,
name|getSchemaPath
argument_list|()
argument_list|,
name|getViewPath
argument_list|()
argument_list|,
name|extendedTable
argument_list|,
name|getTablePath
argument_list|()
argument_list|,
name|constraint
argument_list|,
name|newColumnMapping
argument_list|)
return|;
block|}
comment|/**    * Initializes columns based on the view constraint.    */
specifier|private
class|class
name|ModifiableViewTableInitializerExpressionFactory
extends|extends
name|NullInitializerExpressionFactory
block|{
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|Integer
argument_list|,
name|RexNode
argument_list|>
name|projectMap
decl_stmt|;
specifier|private
name|ModifiableViewTableInitializerExpressionFactory
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|RexNode
argument_list|>
name|projectMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|filters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|RelOptUtil
operator|.
name|inferViewPredicates
argument_list|(
name|projectMap
argument_list|,
name|filters
argument_list|,
name|constraint
argument_list|)
expr_stmt|;
assert|assert
name|filters
operator|.
name|isEmpty
argument_list|()
assert|;
name|this
operator|.
name|projectMap
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|projectMap
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ColumnStrategy
name|generationStrategy
parameter_list|(
name|RelOptTable
name|table
parameter_list|,
name|int
name|iColumn
parameter_list|)
block|{
specifier|final
name|ModifiableViewTable
name|viewTable
init|=
name|requireNonNull
argument_list|(
name|table
operator|.
name|unwrap
argument_list|(
name|ModifiableViewTable
operator|.
name|class
argument_list|)
argument_list|,
parameter_list|()
lambda|->
literal|"unable to unwrap ModifiableViewTable from "
operator|+
name|table
argument_list|)
decl_stmt|;
assert|assert
name|iColumn
operator|<
name|viewTable
operator|.
name|columnMapping
operator|.
name|size
argument_list|()
assert|;
comment|// Use the view constraint to generate the default value if the column is
comment|// constrained.
specifier|final
name|int
name|mappedOrdinal
init|=
name|viewTable
operator|.
name|columnMapping
operator|.
name|get
argument_list|(
name|iColumn
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|viewConstraint
init|=
name|projectMap
operator|.
name|get
argument_list|(
name|mappedOrdinal
argument_list|)
decl_stmt|;
if|if
condition|(
name|viewConstraint
operator|!=
literal|null
condition|)
block|{
return|return
name|ColumnStrategy
operator|.
name|DEFAULT
return|;
block|}
comment|// Otherwise use the default value of the underlying table.
specifier|final
name|Table
name|schemaTable
init|=
name|viewTable
operator|.
name|getTable
argument_list|()
decl_stmt|;
if|if
condition|(
name|schemaTable
operator|instanceof
name|Wrapper
condition|)
block|{
specifier|final
name|InitializerExpressionFactory
name|initializerExpressionFactory
init|=
operator|(
operator|(
name|Wrapper
operator|)
name|schemaTable
operator|)
operator|.
name|unwrap
argument_list|(
name|InitializerExpressionFactory
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|initializerExpressionFactory
operator|!=
literal|null
condition|)
block|{
return|return
name|initializerExpressionFactory
operator|.
name|generationStrategy
argument_list|(
name|table
argument_list|,
name|iColumn
argument_list|)
return|;
block|}
block|}
return|return
name|super
operator|.
name|generationStrategy
argument_list|(
name|table
argument_list|,
name|iColumn
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RexNode
name|newColumnDefaultValue
parameter_list|(
name|RelOptTable
name|table
parameter_list|,
name|int
name|iColumn
parameter_list|,
name|InitializerContext
name|context
parameter_list|)
block|{
specifier|final
name|ModifiableViewTable
name|viewTable
init|=
name|requireNonNull
argument_list|(
name|table
operator|.
name|unwrap
argument_list|(
name|ModifiableViewTable
operator|.
name|class
argument_list|)
argument_list|,
parameter_list|()
lambda|->
literal|"unable to unwrap ModifiableViewTable from "
operator|+
name|table
argument_list|)
decl_stmt|;
assert|assert
name|iColumn
operator|<
name|viewTable
operator|.
name|columnMapping
operator|.
name|size
argument_list|()
assert|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|context
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|rexBuilder
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|viewType
init|=
name|viewTable
operator|.
name|getRowType
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|iType
init|=
name|viewType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|iColumn
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
comment|// Use the view constraint to generate the default value if the column is constrained.
specifier|final
name|int
name|mappedOrdinal
init|=
name|viewTable
operator|.
name|columnMapping
operator|.
name|get
argument_list|(
name|iColumn
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|viewConstraint
init|=
name|projectMap
operator|.
name|get
argument_list|(
name|mappedOrdinal
argument_list|)
decl_stmt|;
if|if
condition|(
name|viewConstraint
operator|!=
literal|null
condition|)
block|{
return|return
name|rexBuilder
operator|.
name|ensureType
argument_list|(
name|iType
argument_list|,
name|viewConstraint
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|// Otherwise use the default value of the underlying table.
specifier|final
name|Table
name|schemaTable
init|=
name|viewTable
operator|.
name|getTable
argument_list|()
decl_stmt|;
if|if
condition|(
name|schemaTable
operator|instanceof
name|Wrapper
condition|)
block|{
specifier|final
name|InitializerExpressionFactory
name|initializerExpressionFactory
init|=
operator|(
operator|(
name|Wrapper
operator|)
name|schemaTable
operator|)
operator|.
name|unwrap
argument_list|(
name|InitializerExpressionFactory
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|initializerExpressionFactory
operator|!=
literal|null
condition|)
block|{
specifier|final
name|RexNode
name|tableConstraint
init|=
name|initializerExpressionFactory
operator|.
name|newColumnDefaultValue
argument_list|(
name|table
argument_list|,
name|iColumn
argument_list|,
name|context
argument_list|)
decl_stmt|;
return|return
name|rexBuilder
operator|.
name|ensureType
argument_list|(
name|iType
argument_list|,
name|tableConstraint
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
comment|// Otherwise Sql type of NULL.
return|return
name|super
operator|.
name|newColumnDefaultValue
argument_list|(
name|table
argument_list|,
name|iColumn
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RexNode
name|newAttributeInitializer
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|SqlFunction
name|constructor
parameter_list|,
name|int
name|iAttribute
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|constructorArgs
parameter_list|,
name|InitializerContext
name|context
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not implemented - unknown requirements"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

