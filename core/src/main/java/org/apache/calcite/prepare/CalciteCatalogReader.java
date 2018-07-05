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
name|prepare
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
name|config
operator|.
name|CalciteConnectionConfig
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
name|jdbc
operator|.
name|CalciteSchema
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
name|jdbc
operator|.
name|JavaTypeFactoryImpl
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
name|model
operator|.
name|ModelHandler
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
name|RelOptPlanner
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
name|RelDataTypeFactoryImpl
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
name|RelDataTypeSystem
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
name|AggregateFunction
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
name|Function
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
name|FunctionParameter
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
name|ScalarFunction
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
name|TableFunction
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
name|TableMacro
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
name|schema
operator|.
name|impl
operator|.
name|ScalarFunctionImpl
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
name|SqlFunctionCategory
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
name|SqlOperator
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
name|SqlOperatorTable
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
name|SqlSyntax
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
name|parser
operator|.
name|SqlParserPos
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
name|FamilyOperandTypeChecker
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
name|InferTypes
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
name|OperandTypes
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
name|SqlTypeFactoryImpl
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
name|SqlTypeFamily
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
name|util
operator|.
name|ListSqlOperatorTable
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
name|SqlMoniker
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
name|SqlMonikerImpl
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
name|SqlMonikerType
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
name|SqlNameMatchers
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
name|SqlUserDefinedAggFunction
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
name|SqlUserDefinedFunction
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
name|SqlUserDefinedTableFunction
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
name|SqlUserDefinedTableMacro
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
name|SqlValidatorUtil
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
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
import|import
name|java
operator|.
name|util
operator|.
name|NavigableSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link org.apache.calcite.prepare.Prepare.CatalogReader}  * and also {@link org.apache.calcite.sql.SqlOperatorTable} based on tables and  * functions defined schemas.  */
end_comment

begin_class
specifier|public
class|class
name|CalciteCatalogReader
implements|implements
name|Prepare
operator|.
name|CatalogReader
block|{
specifier|protected
specifier|final
name|CalciteSchema
name|rootSchema
decl_stmt|;
specifier|protected
specifier|final
name|RelDataTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|schemaPaths
decl_stmt|;
specifier|protected
specifier|final
name|SqlNameMatcher
name|nameMatcher
decl_stmt|;
specifier|protected
specifier|final
name|CalciteConnectionConfig
name|config
decl_stmt|;
specifier|public
name|CalciteCatalogReader
parameter_list|(
name|CalciteSchema
name|rootSchema
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|defaultSchema
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|CalciteConnectionConfig
name|config
parameter_list|)
block|{
name|this
argument_list|(
name|rootSchema
argument_list|,
name|SqlNameMatchers
operator|.
name|withCaseSensitive
argument_list|(
name|config
operator|!=
literal|null
operator|&&
name|config
operator|.
name|caseSensitive
argument_list|()
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|defaultSchema
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
argument_list|,
name|typeFactory
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|CalciteCatalogReader
parameter_list|(
name|CalciteSchema
name|rootSchema
parameter_list|,
name|SqlNameMatcher
name|nameMatcher
parameter_list|,
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|schemaPaths
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|CalciteConnectionConfig
name|config
parameter_list|)
block|{
name|this
operator|.
name|rootSchema
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|rootSchema
argument_list|)
expr_stmt|;
name|this
operator|.
name|nameMatcher
operator|=
name|nameMatcher
expr_stmt|;
name|this
operator|.
name|schemaPaths
operator|=
name|Util
operator|.
name|immutableCopy
argument_list|(
name|Util
operator|.
name|isDistinct
argument_list|(
name|schemaPaths
argument_list|)
condition|?
name|schemaPaths
else|:
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|(
name|schemaPaths
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
specifier|public
name|CalciteCatalogReader
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
name|CalciteCatalogReader
argument_list|(
name|rootSchema
argument_list|,
name|nameMatcher
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|schemaPath
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
argument_list|,
name|typeFactory
argument_list|,
name|config
argument_list|)
return|;
block|}
specifier|public
name|Prepare
operator|.
name|PreparingTable
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
comment|// If not found, look in the root schema.
name|CalciteSchema
operator|.
name|TableEntry
name|entry
init|=
name|SqlValidatorUtil
operator|.
name|getTableEntry
argument_list|(
name|this
argument_list|,
name|names
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Table
name|table
init|=
name|entry
operator|.
name|getTable
argument_list|()
decl_stmt|;
if|if
condition|(
name|table
operator|instanceof
name|Wrapper
condition|)
block|{
specifier|final
name|Prepare
operator|.
name|PreparingTable
name|relOptTable
init|=
operator|(
operator|(
name|Wrapper
operator|)
name|table
operator|)
operator|.
name|unwrap
argument_list|(
name|Prepare
operator|.
name|PreparingTable
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|relOptTable
operator|!=
literal|null
condition|)
block|{
return|return
name|relOptTable
return|;
block|}
block|}
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
name|entry
argument_list|,
literal|null
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|CalciteConnectionConfig
name|getConfig
parameter_list|()
block|{
return|return
name|config
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
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|schemaNameList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
comment|// Name qualified: ignore path. But we do look in "/catalog" and "/",
comment|// the last 2 items in the path.
if|if
condition|(
name|schemaPaths
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|schemaNameList
operator|.
name|addAll
argument_list|(
name|Util
operator|.
name|skip
argument_list|(
name|schemaPaths
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|schemaNameList
operator|.
name|addAll
argument_list|(
name|schemaPaths
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
range|:
name|schemaPaths
control|)
block|{
name|CalciteSchema
name|schema
init|=
name|SqlValidatorUtil
operator|.
name|getSchema
argument_list|(
name|rootSchema
argument_list|,
name|schemaPath
argument_list|,
name|nameMatcher
argument_list|)
decl_stmt|;
if|if
condition|(
name|schema
operator|!=
literal|null
condition|)
block|{
name|schemaNameList
operator|.
name|addAll
argument_list|(
name|schema
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|CalciteSchema
name|schema
init|=
name|SqlValidatorUtil
operator|.
name|getSchema
argument_list|(
name|rootSchema
argument_list|,
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
argument_list|,
name|nameMatcher
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
specifier|public
name|RelDataType
name|getNamedType
parameter_list|(
name|SqlIdentifier
name|typeName
parameter_list|)
block|{
name|CalciteSchema
operator|.
name|TypeEntry
name|typeEntry
init|=
name|SqlValidatorUtil
operator|.
name|getTypeEntry
argument_list|(
name|getRootSchema
argument_list|()
argument_list|,
name|typeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|typeEntry
operator|!=
literal|null
condition|)
block|{
return|return
name|typeEntry
operator|.
name|getType
argument_list|()
operator|.
name|apply
argument_list|(
name|typeFactory
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
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
name|CalciteSchema
name|schema
init|=
name|SqlValidatorUtil
operator|.
name|getSchema
argument_list|(
name|rootSchema
argument_list|,
name|names
argument_list|,
name|nameMatcher
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
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Add root schema if not anonymous
if|if
condition|(
operator|!
name|schema
operator|.
name|name
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|moniker
argument_list|(
name|schema
argument_list|,
literal|null
argument_list|,
name|SqlMonikerType
operator|.
name|SCHEMA
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CalciteSchema
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
name|moniker
argument_list|(
name|schema
argument_list|,
name|subSchema
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
name|moniker
argument_list|(
name|schema
argument_list|,
name|table
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
name|moniker
argument_list|(
name|schema
argument_list|,
name|function
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
specifier|private
name|SqlMonikerImpl
name|moniker
parameter_list|(
name|CalciteSchema
name|schema
parameter_list|,
name|String
name|name
parameter_list|,
name|SqlMonikerType
name|type
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|path
init|=
name|schema
operator|.
name|path
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
operator|!
name|schema
operator|.
name|root
argument_list|()
operator|.
name|name
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|&&
name|type
operator|==
name|SqlMonikerType
operator|.
name|SCHEMA
condition|)
block|{
name|type
operator|=
name|SqlMonikerType
operator|.
name|CATALOG
expr_stmt|;
block|}
return|return
operator|new
name|SqlMonikerImpl
argument_list|(
name|path
argument_list|,
name|type
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getSchemaPaths
parameter_list|()
block|{
return|return
name|schemaPaths
return|;
block|}
specifier|public
name|Prepare
operator|.
name|PreparingTable
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
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
name|nameMatcher
operator|.
name|field
argument_list|(
name|rowType
argument_list|,
name|alias
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|string
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|nameMatcher
operator|.
name|matches
argument_list|(
name|string
argument_list|,
name|name
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
name|nameMatcher
operator|.
name|isCaseSensitive
argument_list|()
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
name|Predicate
argument_list|<
name|Function
argument_list|>
name|predicate
decl_stmt|;
if|if
condition|(
name|category
operator|==
literal|null
condition|)
block|{
name|predicate
operator|=
name|function
lambda|->
literal|true
expr_stmt|;
block|}
if|else if
condition|(
name|category
operator|.
name|isTableFunction
argument_list|()
condition|)
block|{
name|predicate
operator|=
name|function
lambda|->
name|function
operator|instanceof
name|TableMacro
operator|||
name|function
operator|instanceof
name|TableFunction
expr_stmt|;
block|}
else|else
block|{
name|predicate
operator|=
name|function
lambda|->
operator|!
operator|(
name|function
operator|instanceof
name|TableMacro
operator|||
name|function
operator|instanceof
name|TableFunction
operator|)
expr_stmt|;
block|}
name|getFunctionsFrom
argument_list|(
name|opName
operator|.
name|names
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|predicate
argument_list|)
operator|.
name|map
argument_list|(
name|function
lambda|->
name|toOp
argument_list|(
name|opName
argument_list|,
name|function
argument_list|)
argument_list|)
operator|.
name|forEachOrdered
argument_list|(
name|operatorList
operator|::
name|add
argument_list|)
expr_stmt|;
block|}
comment|/** Creates an operator table that contains functions in the given class.    *    * @see ModelHandler#addFunctions */
specifier|public
specifier|static
name|SqlOperatorTable
name|operatorTable
parameter_list|(
name|String
name|className
parameter_list|)
block|{
comment|// Dummy schema to collect the functions
specifier|final
name|CalciteSchema
name|schema
init|=
name|CalciteSchema
operator|.
name|createRootSchema
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|ModelHandler
operator|.
name|addFunctions
argument_list|(
name|schema
operator|.
name|plus
argument_list|()
argument_list|,
literal|null
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|className
argument_list|,
literal|"*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// The following is technical debt; see [CALCITE-2082] Remove
comment|// RelDataTypeFactory argument from SqlUserDefinedAggFunction constructor
specifier|final
name|SqlTypeFactoryImpl
name|typeFactory
init|=
operator|new
name|SqlTypeFactoryImpl
argument_list|(
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|ListSqlOperatorTable
name|table
init|=
operator|new
name|ListSqlOperatorTable
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|schema
operator|.
name|getFunctionNames
argument_list|()
control|)
block|{
for|for
control|(
name|Function
name|function
range|:
name|schema
operator|.
name|getFunctions
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
control|)
block|{
specifier|final
name|SqlIdentifier
name|id
init|=
operator|new
name|SqlIdentifier
argument_list|(
name|name
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
decl_stmt|;
name|table
operator|.
name|add
argument_list|(
name|toOp
argument_list|(
name|typeFactory
argument_list|,
name|id
argument_list|,
name|function
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|table
return|;
block|}
specifier|private
name|SqlOperator
name|toOp
parameter_list|(
name|SqlIdentifier
name|name
parameter_list|,
specifier|final
name|Function
name|function
parameter_list|)
block|{
return|return
name|toOp
argument_list|(
name|typeFactory
argument_list|,
name|name
argument_list|,
name|function
argument_list|)
return|;
block|}
comment|/** Converts a function to a {@link org.apache.calcite.sql.SqlOperator}.    *    *<p>The {@code typeFactory} argument is technical debt; see [CALCITE-2082]    * Remove RelDataTypeFactory argument from SqlUserDefinedAggFunction    * constructor. */
specifier|private
specifier|static
name|SqlOperator
name|toOp
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|SqlIdentifier
name|name
parameter_list|,
specifier|final
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
argument_list|<>
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
argument_list|<>
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
name|FamilyOperandTypeChecker
name|typeChecker
init|=
name|OperandTypes
operator|.
name|family
argument_list|(
name|typeFamilies
argument_list|,
name|i
lambda|->
name|function
operator|.
name|getParameters
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|isOptional
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|paramTypes
init|=
name|toSql
argument_list|(
name|typeFactory
argument_list|,
name|argTypes
argument_list|)
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
name|infer
argument_list|(
operator|(
name|ScalarFunction
operator|)
name|function
argument_list|)
argument_list|,
name|InferTypes
operator|.
name|explicit
argument_list|(
name|argTypes
argument_list|)
argument_list|,
name|typeChecker
argument_list|,
name|paramTypes
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
return|return
operator|new
name|SqlUserDefinedAggFunction
argument_list|(
name|name
argument_list|,
name|infer
argument_list|(
operator|(
name|AggregateFunction
operator|)
name|function
argument_list|)
argument_list|,
name|InferTypes
operator|.
name|explicit
argument_list|(
name|argTypes
argument_list|)
argument_list|,
name|typeChecker
argument_list|,
operator|(
name|AggregateFunction
operator|)
name|function
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|typeFactory
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
name|typeChecker
argument_list|,
name|paramTypes
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
name|typeChecker
argument_list|,
name|paramTypes
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
specifier|static
name|SqlReturnTypeInference
name|infer
parameter_list|(
specifier|final
name|ScalarFunction
name|function
parameter_list|)
block|{
return|return
name|opBinding
lambda|->
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
name|type
decl_stmt|;
if|if
condition|(
name|function
operator|instanceof
name|ScalarFunctionImpl
condition|)
block|{
name|type
operator|=
operator|(
operator|(
name|ScalarFunctionImpl
operator|)
name|function
operator|)
operator|.
name|getReturnType
argument_list|(
name|typeFactory
argument_list|,
name|opBinding
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
name|function
operator|.
name|getReturnType
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
block|}
return|return
name|toSql
argument_list|(
name|typeFactory
argument_list|,
name|type
argument_list|)
return|;
block|}
return|;
block|}
specifier|private
specifier|static
name|SqlReturnTypeInference
name|infer
parameter_list|(
specifier|final
name|AggregateFunction
name|function
parameter_list|)
block|{
return|return
name|opBinding
lambda|->
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
name|type
init|=
name|function
operator|.
name|getReturnType
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
return|return
name|toSql
argument_list|(
name|typeFactory
argument_list|,
name|type
argument_list|)
return|;
block|}
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|RelDataType
argument_list|>
name|toSql
parameter_list|(
specifier|final
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
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
name|type
lambda|->
name|toSql
argument_list|(
name|typeFactory
argument_list|,
name|type
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|RelDataType
name|toSql
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
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
name|type
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
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
return|return
name|JavaTypeFactoryImpl
operator|.
name|toSql
argument_list|(
name|typeFactory
argument_list|,
name|type
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
name|CalciteSchema
name|getRootSchema
parameter_list|()
block|{
return|return
name|rootSchema
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
specifier|public
name|boolean
name|isCaseSensitive
parameter_list|()
block|{
return|return
name|nameMatcher
operator|.
name|isCaseSensitive
argument_list|()
return|;
block|}
specifier|public
name|SqlNameMatcher
name|nameMatcher
parameter_list|()
block|{
return|return
name|nameMatcher
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|C
parameter_list|>
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
name|this
argument_list|)
condition|)
block|{
return|return
name|aClass
operator|.
name|cast
argument_list|(
name|this
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

begin_comment
comment|// End CalciteCatalogReader.java
end_comment

end_unit

