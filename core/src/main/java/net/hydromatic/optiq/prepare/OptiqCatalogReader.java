begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|prepare
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|Table
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|java
operator|.
name|JavaTypeFactory
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
operator|.
name|OptiqSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|RelOptPlanner
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
name|validate
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
name|util
operator|.
name|Pair
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link net.hydromatic.optiq.prepare.Prepare.CatalogReader}  * and also {@link org.eigenbase.sql.SqlOperatorTable} based on tables and  * functions defined schemas.  */
end_comment

begin_class
specifier|public
class|class
name|OptiqCatalogReader
implements|implements
name|Prepare
operator|.
name|CatalogReader
implements|,
name|SqlOperatorTable
block|{
specifier|final
name|OptiqSchema
name|rootSchema
decl_stmt|;
specifier|final
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|defaultSchema
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|caseSensitive
decl_stmt|;
specifier|public
name|OptiqCatalogReader
parameter_list|(
name|OptiqSchema
name|rootSchema
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|defaultSchema
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
assert|assert
name|rootSchema
operator|!=
name|defaultSchema
assert|;
name|this
operator|.
name|rootSchema
operator|=
name|rootSchema
expr_stmt|;
name|this
operator|.
name|caseSensitive
operator|=
name|caseSensitive
expr_stmt|;
name|this
operator|.
name|defaultSchema
operator|=
name|defaultSchema
expr_stmt|;
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
block|}
specifier|public
name|OptiqCatalogReader
name|withSchemaPath
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|)
block|{
return|return
operator|new
name|OptiqCatalogReader
argument_list|(
name|rootSchema
argument_list|,
name|caseSensitive
argument_list|,
name|schemaPath
argument_list|,
name|typeFactory
argument_list|)
return|;
block|}
specifier|public
name|RelOptTableImpl
name|getTable
parameter_list|(
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
comment|// First look in the default schema, if any.
if|if
condition|(
name|defaultSchema
operator|!=
literal|null
condition|)
block|{
name|RelOptTableImpl
name|table
init|=
name|getTableFrom
argument_list|(
name|names
argument_list|,
name|defaultSchema
argument_list|)
decl_stmt|;
if|if
condition|(
name|table
operator|!=
literal|null
condition|)
block|{
return|return
name|table
return|;
block|}
block|}
comment|// If not found, look in the root schema
return|return
name|getTableFrom
argument_list|(
name|names
argument_list|,
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|RelOptTableImpl
name|getTableFrom
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|schemaNames
parameter_list|)
block|{
name|OptiqSchema
name|schema
init|=
name|getSchema
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|schemaNames
argument_list|,
name|Util
operator|.
name|skipLast
argument_list|(
name|names
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|schema
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|String
name|name
init|=
name|Util
operator|.
name|last
argument_list|(
name|names
argument_list|)
decl_stmt|;
name|Pair
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|pair
init|=
name|schema
operator|.
name|getTable
argument_list|(
name|name
argument_list|,
name|caseSensitive
argument_list|)
decl_stmt|;
if|if
condition|(
name|pair
operator|==
literal|null
condition|)
block|{
name|pair
operator|=
name|schema
operator|.
name|getTableBasedOnNullaryFunction
argument_list|(
name|name
argument_list|,
name|caseSensitive
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pair
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Table
name|table
init|=
name|pair
operator|.
name|getValue
argument_list|()
decl_stmt|;
specifier|final
name|String
name|name2
init|=
name|pair
operator|.
name|getKey
argument_list|()
decl_stmt|;
return|return
name|RelOptTableImpl
operator|.
name|create
argument_list|(
name|this
argument_list|,
name|table
operator|.
name|getRowType
argument_list|(
name|typeFactory
argument_list|)
argument_list|,
name|schema
operator|.
name|add
argument_list|(
name|name2
argument_list|,
name|table
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|Collection
argument_list|<
name|Function
argument_list|>
name|getFunctionsFrom
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Function
argument_list|>
name|functions2
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|schemaNameList
decl_stmt|;
if|if
condition|(
name|names
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
comment|// If name is qualified, ignore path.
name|schemaNameList
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|OptiqSchema
name|schema
init|=
name|getSchema
argument_list|(
name|defaultSchema
argument_list|)
decl_stmt|;
if|if
condition|(
name|schema
operator|==
literal|null
condition|)
block|{
name|schemaNameList
operator|=
name|ImmutableList
operator|.
name|of
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|schemaNameList
operator|=
name|schema
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|List
argument_list|<
name|String
argument_list|>
name|schemaNames
range|:
name|schemaNameList
control|)
block|{
name|OptiqSchema
name|schema
init|=
name|getSchema
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|schemaNames
argument_list|,
name|Util
operator|.
name|skipLast
argument_list|(
name|names
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|schema
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|name
init|=
name|Util
operator|.
name|last
argument_list|(
name|names
argument_list|)
decl_stmt|;
name|functions2
operator|.
name|addAll
argument_list|(
name|schema
operator|.
name|getFunctions
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|functions2
return|;
block|}
specifier|private
name|OptiqSchema
name|getSchema
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|schemaNames
parameter_list|)
block|{
name|OptiqSchema
name|schema
init|=
name|rootSchema
decl_stmt|;
for|for
control|(
name|String
name|schemaName
range|:
name|schemaNames
control|)
block|{
name|schema
operator|=
name|schema
operator|.
name|getSubSchema
argument_list|(
name|schemaName
argument_list|,
name|caseSensitive
argument_list|)
expr_stmt|;
if|if
condition|(
name|schema
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|schema
return|;
block|}
specifier|public
name|RelDataType
name|getNamedType
parameter_list|(
name|SqlIdentifier
name|typeName
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|getAllSchemaObjectNames
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
specifier|final
name|OptiqSchema
name|schema
init|=
name|getSchema
argument_list|(
name|names
argument_list|)
decl_stmt|;
if|if
condition|(
name|schema
operator|==
literal|null
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
specifier|final
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|SqlMoniker
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|OptiqSchema
argument_list|>
name|schemaMap
init|=
name|schema
operator|.
name|getSubSchemaMap
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|subSchema
range|:
name|schemaMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|SqlMonikerImpl
argument_list|(
name|schema
operator|.
name|path
argument_list|(
name|subSchema
argument_list|)
argument_list|,
name|SqlMonikerType
operator|.
name|SCHEMA
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|table
range|:
name|schema
operator|.
name|getTableNames
argument_list|()
control|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|SqlMonikerImpl
argument_list|(
name|schema
operator|.
name|path
argument_list|(
name|table
argument_list|)
argument_list|,
name|SqlMonikerType
operator|.
name|TABLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|NavigableSet
argument_list|<
name|String
argument_list|>
name|functions
init|=
name|schema
operator|.
name|getFunctionNames
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|function
range|:
name|functions
control|)
block|{
comment|// views are here as well
name|result
operator|.
name|add
argument_list|(
operator|new
name|SqlMonikerImpl
argument_list|(
name|schema
operator|.
name|path
argument_list|(
name|function
argument_list|)
argument_list|,
name|SqlMonikerType
operator|.
name|FUNCTION
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getSchemaName
parameter_list|()
block|{
return|return
name|defaultSchema
return|;
block|}
specifier|public
name|RelOptTableImpl
name|getTableForMember
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
return|return
name|getTable
argument_list|(
name|names
argument_list|)
return|;
block|}
specifier|public
name|RelDataTypeField
name|field
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|String
name|alias
parameter_list|)
block|{
return|return
name|SqlValidatorUtil
operator|.
name|lookupField
argument_list|(
name|caseSensitive
argument_list|,
name|rowType
argument_list|,
name|alias
argument_list|)
return|;
block|}
specifier|public
name|int
name|fieldOrdinal
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|String
name|alias
parameter_list|)
block|{
name|RelDataTypeField
name|field
init|=
name|field
argument_list|(
name|rowType
argument_list|,
name|alias
argument_list|)
decl_stmt|;
return|return
name|field
operator|!=
literal|null
condition|?
name|field
operator|.
name|getIndex
argument_list|()
else|:
operator|-
literal|1
return|;
block|}
specifier|public
name|int
name|match
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|strings
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|Util
operator|.
name|match2
argument_list|(
name|strings
argument_list|,
name|name
argument_list|,
name|caseSensitive
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|createTypeFromProjection
parameter_list|(
specifier|final
name|RelDataType
name|type
parameter_list|,
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|columnNameList
parameter_list|)
block|{
return|return
name|SqlValidatorUtil
operator|.
name|createTypeFromProjection
argument_list|(
name|type
argument_list|,
name|columnNameList
argument_list|,
name|typeFactory
argument_list|,
name|caseSensitive
argument_list|)
return|;
block|}
specifier|public
name|void
name|lookupOperatorOverloads
parameter_list|(
specifier|final
name|SqlIdentifier
name|opName
parameter_list|,
name|SqlFunctionCategory
name|category
parameter_list|,
name|SqlSyntax
name|syntax
parameter_list|,
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|operatorList
parameter_list|)
block|{
if|if
condition|(
name|syntax
operator|!=
name|SqlSyntax
operator|.
name|FUNCTION
condition|)
block|{
return|return;
block|}
specifier|final
name|Collection
argument_list|<
name|Function
argument_list|>
name|functions
init|=
name|getFunctionsFrom
argument_list|(
name|opName
operator|.
name|names
argument_list|)
decl_stmt|;
if|if
condition|(
name|functions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|operatorList
operator|.
name|addAll
argument_list|(
name|Collections2
operator|.
name|transform
argument_list|(
name|functions
argument_list|,
operator|new
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
argument_list|<
name|Function
argument_list|,
name|SqlOperator
argument_list|>
argument_list|()
block|{
specifier|public
name|SqlOperator
name|apply
parameter_list|(
name|Function
name|function
parameter_list|)
block|{
return|return
name|toOp
argument_list|(
name|opName
argument_list|,
name|function
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|SqlOperator
name|toOp
parameter_list|(
name|SqlIdentifier
name|name
parameter_list|,
name|Function
name|function
parameter_list|)
block|{
name|List
argument_list|<
name|RelDataType
argument_list|>
name|argTypes
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
name|SqlTypeFamily
argument_list|>
name|typeFamilies
init|=
operator|new
name|ArrayList
argument_list|<
name|SqlTypeFamily
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FunctionParameter
name|o
range|:
name|function
operator|.
name|getParameters
argument_list|()
control|)
block|{
specifier|final
name|RelDataType
name|type
init|=
name|o
operator|.
name|getType
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
name|argTypes
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|typeFamilies
operator|.
name|add
argument_list|(
name|Util
operator|.
name|first
argument_list|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|.
name|getFamily
argument_list|()
argument_list|,
name|SqlTypeFamily
operator|.
name|ANY
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelDataType
name|returnType
decl_stmt|;
if|if
condition|(
name|function
operator|instanceof
name|ScalarFunction
condition|)
block|{
return|return
operator|new
name|SqlUserDefinedFunction
argument_list|(
name|name
argument_list|,
name|ReturnTypes
operator|.
name|explicit
argument_list|(
name|Schemas
operator|.
name|proto
argument_list|(
operator|(
name|ScalarFunction
operator|)
name|function
argument_list|)
argument_list|)
argument_list|,
name|InferTypes
operator|.
name|explicit
argument_list|(
name|argTypes
argument_list|)
argument_list|,
name|OperandTypes
operator|.
name|family
argument_list|(
name|typeFamilies
argument_list|)
argument_list|,
name|toSql
argument_list|(
name|argTypes
argument_list|)
argument_list|,
name|function
argument_list|)
return|;
block|}
if|else if
condition|(
name|function
operator|instanceof
name|AggregateFunction
condition|)
block|{
name|returnType
operator|=
operator|(
operator|(
name|AggregateFunction
operator|)
name|function
operator|)
operator|.
name|getReturnType
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
return|return
operator|new
name|SqlUserDefinedAggFunction
argument_list|(
name|name
argument_list|,
name|ReturnTypes
operator|.
name|explicit
argument_list|(
name|returnType
argument_list|)
argument_list|,
name|InferTypes
operator|.
name|explicit
argument_list|(
name|argTypes
argument_list|)
argument_list|,
name|OperandTypes
operator|.
name|family
argument_list|(
name|typeFamilies
argument_list|)
argument_list|,
operator|(
name|AggregateFunction
operator|)
name|function
argument_list|)
return|;
block|}
if|else if
condition|(
name|function
operator|instanceof
name|TableMacro
condition|)
block|{
return|return
operator|new
name|SqlUserDefinedTableMacro
argument_list|(
name|name
argument_list|,
name|ReturnTypes
operator|.
name|CURSOR
argument_list|,
name|InferTypes
operator|.
name|explicit
argument_list|(
name|argTypes
argument_list|)
argument_list|,
name|OperandTypes
operator|.
name|family
argument_list|(
name|typeFamilies
argument_list|)
argument_list|,
operator|(
name|TableMacro
operator|)
name|function
argument_list|)
return|;
block|}
if|else if
condition|(
name|function
operator|instanceof
name|TableFunction
condition|)
block|{
return|return
operator|new
name|SqlUserDefinedTableFunction
argument_list|(
name|name
argument_list|,
name|ReturnTypes
operator|.
name|CURSOR
argument_list|,
name|InferTypes
operator|.
name|explicit
argument_list|(
name|argTypes
argument_list|)
argument_list|,
name|OperandTypes
operator|.
name|family
argument_list|(
name|typeFamilies
argument_list|)
argument_list|,
name|toSql
argument_list|(
name|argTypes
argument_list|)
argument_list|,
operator|(
name|TableFunction
operator|)
name|function
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unknown function type "
operator|+
name|function
argument_list|)
throw|;
block|}
block|}
specifier|private
name|List
argument_list|<
name|RelDataType
argument_list|>
name|toSql
parameter_list|(
name|List
argument_list|<
name|RelDataType
argument_list|>
name|types
parameter_list|)
block|{
return|return
name|Lists
operator|.
name|transform
argument_list|(
name|types
argument_list|,
operator|new
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
argument_list|<
name|RelDataType
argument_list|,
name|RelDataType
argument_list|>
argument_list|()
block|{
specifier|public
name|RelDataType
name|apply
parameter_list|(
name|RelDataType
name|input
parameter_list|)
block|{
if|if
condition|(
name|input
operator|instanceof
name|RelDataTypeFactoryImpl
operator|.
name|JavaType
operator|&&
operator|(
operator|(
name|RelDataTypeFactoryImpl
operator|.
name|JavaType
operator|)
name|input
operator|)
operator|.
name|getJavaClass
argument_list|()
operator|==
name|Object
operator|.
name|class
condition|)
block|{
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
return|;
block|}
return|return
name|typeFactory
operator|.
name|toSql
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|getOperatorList
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|RelDataTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
name|typeFactory
return|;
block|}
specifier|public
name|void
name|registerRules
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
throws|throws
name|Exception
block|{
block|}
block|}
end_class

begin_comment
comment|// End OptiqCatalogReader.java
end_comment

end_unit

