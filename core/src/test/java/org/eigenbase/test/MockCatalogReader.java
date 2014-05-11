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
name|*
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
name|relopt
operator|.
name|RelOptSchema
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
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|Expression
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
name|prepare
operator|.
name|Prepare
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
name|Ordering
import|;
end_import

begin_comment
comment|/**  * Mock implementation of {@link SqlValidatorCatalogReader} which returns tables  * "EMP", "DEPT", "BONUS", "SALGRADE" (same as Oracle's SCOTT schema).  */
end_comment

begin_class
specifier|public
class|class
name|MockCatalogReader
implements|implements
name|Prepare
operator|.
name|CatalogReader
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|protected
specifier|static
specifier|final
name|String
name|DEFAULT_CATALOG
init|=
literal|"CATALOG"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|DEFAULT_SCHEMA
init|=
literal|"SALES"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Ordering
argument_list|<
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|CASE_INSENSITIVE_LIST_COMPARATOR
init|=
name|Ordering
operator|.
expr|<
name|String
operator|>
name|from
argument_list|(
name|String
operator|.
name|CASE_INSENSITIVE_ORDER
argument_list|)
operator|.
name|lexicographical
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|RelDataTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|caseSensitive
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|MockTable
argument_list|>
name|tables
decl_stmt|;
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|MockSchema
argument_list|>
name|schemas
decl_stmt|;
specifier|private
name|RelDataType
name|addressType
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a MockCatalogReader.    *    * @param typeFactory Type factory    */
specifier|public
name|MockCatalogReader
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
block|{
name|this
argument_list|(
name|typeFactory
argument_list|,
name|caseSensitive
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates a MockCatalogReader but does not initialize.    *    *<p>Protected constructor for use by subclasses, which must call    * {@link #init} at the end of their public constructor.    *    * @param typeFactory Type factory    * @param dummy       Dummy parameter to distinguish from public constructor    */
specifier|protected
name|MockCatalogReader
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|,
name|boolean
name|dummy
parameter_list|)
block|{
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
name|this
operator|.
name|caseSensitive
operator|=
name|caseSensitive
expr_stmt|;
assert|assert
operator|!
name|dummy
assert|;
if|if
condition|(
name|caseSensitive
condition|)
block|{
name|tables
operator|=
operator|new
name|HashMap
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|MockTable
argument_list|>
argument_list|()
expr_stmt|;
name|schemas
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|MockSchema
argument_list|>
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|tables
operator|=
operator|new
name|TreeMap
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|MockTable
argument_list|>
argument_list|(
name|CASE_INSENSITIVE_LIST_COMPARATOR
argument_list|)
expr_stmt|;
name|schemas
operator|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|MockSchema
argument_list|>
argument_list|(
name|String
operator|.
name|CASE_INSENSITIVE_ORDER
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Initializes this catalog reader.    */
specifier|protected
name|void
name|init
parameter_list|()
block|{
specifier|final
name|RelDataType
name|intType
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|intTypeNull
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|intType
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|varchar10Type
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|varchar20Type
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
literal|20
argument_list|)
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
specifier|final
name|RelDataType
name|booleanType
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|rectilinearCoordType
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"X"
argument_list|,
name|intType
argument_list|)
operator|.
name|add
argument_list|(
literal|"Y"
argument_list|,
name|intType
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// TODO jvs 12-Feb-2005: register this canonical instance with type
comment|// factory
name|addressType
operator|=
operator|new
name|ObjectSqlType
argument_list|(
name|SqlTypeName
operator|.
name|STRUCTURED
argument_list|,
operator|new
name|SqlIdentifier
argument_list|(
literal|"ADDRESS"
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|,
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|RelDataTypeFieldImpl
argument_list|(
literal|"STREET"
argument_list|,
literal|0
argument_list|,
name|varchar20Type
argument_list|)
argument_list|,
operator|new
name|RelDataTypeFieldImpl
argument_list|(
literal|"CITY"
argument_list|,
literal|1
argument_list|,
name|varchar20Type
argument_list|)
argument_list|,
operator|new
name|RelDataTypeFieldImpl
argument_list|(
literal|"ZIP"
argument_list|,
literal|1
argument_list|,
name|intType
argument_list|)
argument_list|,
operator|new
name|RelDataTypeFieldImpl
argument_list|(
literal|"STATE"
argument_list|,
literal|1
argument_list|,
name|varchar20Type
argument_list|)
argument_list|)
argument_list|,
name|RelDataTypeComparability
operator|.
name|NONE
argument_list|)
expr_stmt|;
comment|// Register "SALES" schema.
name|MockSchema
name|salesSchema
init|=
operator|new
name|MockSchema
argument_list|(
literal|"SALES"
argument_list|)
decl_stmt|;
name|registerSchema
argument_list|(
name|salesSchema
argument_list|)
expr_stmt|;
comment|// Register "EMP" table.
name|MockTable
name|empTable
init|=
operator|new
name|MockTable
argument_list|(
name|this
argument_list|,
name|salesSchema
argument_list|,
literal|"EMP"
argument_list|)
decl_stmt|;
name|empTable
operator|.
name|addColumn
argument_list|(
literal|"EMPNO"
argument_list|,
name|intType
argument_list|)
expr_stmt|;
name|empTable
operator|.
name|addColumn
argument_list|(
literal|"ENAME"
argument_list|,
name|varchar20Type
argument_list|)
expr_stmt|;
name|empTable
operator|.
name|addColumn
argument_list|(
literal|"JOB"
argument_list|,
name|varchar10Type
argument_list|)
expr_stmt|;
name|empTable
operator|.
name|addColumn
argument_list|(
literal|"MGR"
argument_list|,
name|intTypeNull
argument_list|)
expr_stmt|;
name|empTable
operator|.
name|addColumn
argument_list|(
literal|"HIREDATE"
argument_list|,
name|timestampType
argument_list|)
expr_stmt|;
name|empTable
operator|.
name|addColumn
argument_list|(
literal|"SAL"
argument_list|,
name|intType
argument_list|)
expr_stmt|;
name|empTable
operator|.
name|addColumn
argument_list|(
literal|"COMM"
argument_list|,
name|intType
argument_list|)
expr_stmt|;
name|empTable
operator|.
name|addColumn
argument_list|(
literal|"DEPTNO"
argument_list|,
name|intType
argument_list|)
expr_stmt|;
name|empTable
operator|.
name|addColumn
argument_list|(
literal|"SLACKER"
argument_list|,
name|booleanType
argument_list|)
expr_stmt|;
name|registerTable
argument_list|(
name|empTable
argument_list|)
expr_stmt|;
comment|// Register "DEPT" table.
name|MockTable
name|deptTable
init|=
operator|new
name|MockTable
argument_list|(
name|this
argument_list|,
name|salesSchema
argument_list|,
literal|"DEPT"
argument_list|)
decl_stmt|;
name|deptTable
operator|.
name|addColumn
argument_list|(
literal|"DEPTNO"
argument_list|,
name|intType
argument_list|)
expr_stmt|;
name|deptTable
operator|.
name|addColumn
argument_list|(
literal|"NAME"
argument_list|,
name|varchar10Type
argument_list|)
expr_stmt|;
name|registerTable
argument_list|(
name|deptTable
argument_list|)
expr_stmt|;
comment|// Register "BONUS" table.
name|MockTable
name|bonusTable
init|=
operator|new
name|MockTable
argument_list|(
name|this
argument_list|,
name|salesSchema
argument_list|,
literal|"BONUS"
argument_list|)
decl_stmt|;
name|bonusTable
operator|.
name|addColumn
argument_list|(
literal|"ENAME"
argument_list|,
name|varchar20Type
argument_list|)
expr_stmt|;
name|bonusTable
operator|.
name|addColumn
argument_list|(
literal|"JOB"
argument_list|,
name|varchar10Type
argument_list|)
expr_stmt|;
name|bonusTable
operator|.
name|addColumn
argument_list|(
literal|"SAL"
argument_list|,
name|intType
argument_list|)
expr_stmt|;
name|bonusTable
operator|.
name|addColumn
argument_list|(
literal|"COMM"
argument_list|,
name|intType
argument_list|)
expr_stmt|;
name|registerTable
argument_list|(
name|bonusTable
argument_list|)
expr_stmt|;
comment|// Register "SALGRADE" table.
name|MockTable
name|salgradeTable
init|=
operator|new
name|MockTable
argument_list|(
name|this
argument_list|,
name|salesSchema
argument_list|,
literal|"SALGRADE"
argument_list|)
decl_stmt|;
name|salgradeTable
operator|.
name|addColumn
argument_list|(
literal|"GRADE"
argument_list|,
name|intType
argument_list|)
expr_stmt|;
name|salgradeTable
operator|.
name|addColumn
argument_list|(
literal|"LOSAL"
argument_list|,
name|intType
argument_list|)
expr_stmt|;
name|salgradeTable
operator|.
name|addColumn
argument_list|(
literal|"HISAL"
argument_list|,
name|intType
argument_list|)
expr_stmt|;
name|registerTable
argument_list|(
name|salgradeTable
argument_list|)
expr_stmt|;
comment|// Register "EMP_ADDRESS" table
name|MockTable
name|contactAddressTable
init|=
operator|new
name|MockTable
argument_list|(
name|this
argument_list|,
name|salesSchema
argument_list|,
literal|"EMP_ADDRESS"
argument_list|)
decl_stmt|;
name|contactAddressTable
operator|.
name|addColumn
argument_list|(
literal|"EMPNO"
argument_list|,
name|intType
argument_list|)
expr_stmt|;
name|contactAddressTable
operator|.
name|addColumn
argument_list|(
literal|"HOME_ADDRESS"
argument_list|,
name|addressType
argument_list|)
expr_stmt|;
name|contactAddressTable
operator|.
name|addColumn
argument_list|(
literal|"MAILING_ADDRESS"
argument_list|,
name|addressType
argument_list|)
expr_stmt|;
name|registerTable
argument_list|(
name|contactAddressTable
argument_list|)
expr_stmt|;
comment|// Register "CUSTOMER" schema.
name|MockSchema
name|customerSchema
init|=
operator|new
name|MockSchema
argument_list|(
literal|"CUSTOMER"
argument_list|)
decl_stmt|;
name|registerSchema
argument_list|(
name|customerSchema
argument_list|)
expr_stmt|;
comment|// Register "CONTACT" table.
name|MockTable
name|contactTable
init|=
operator|new
name|MockTable
argument_list|(
name|this
argument_list|,
name|customerSchema
argument_list|,
literal|"CONTACT"
argument_list|)
decl_stmt|;
name|contactTable
operator|.
name|addColumn
argument_list|(
literal|"CONTACTNO"
argument_list|,
name|intType
argument_list|)
expr_stmt|;
name|contactTable
operator|.
name|addColumn
argument_list|(
literal|"FNAME"
argument_list|,
name|varchar10Type
argument_list|)
expr_stmt|;
name|contactTable
operator|.
name|addColumn
argument_list|(
literal|"LNAME"
argument_list|,
name|varchar10Type
argument_list|)
expr_stmt|;
name|contactTable
operator|.
name|addColumn
argument_list|(
literal|"EMAIL"
argument_list|,
name|varchar20Type
argument_list|)
expr_stmt|;
name|contactTable
operator|.
name|addColumn
argument_list|(
literal|"COORD"
argument_list|,
name|rectilinearCoordType
argument_list|)
expr_stmt|;
name|registerTable
argument_list|(
name|contactTable
argument_list|)
expr_stmt|;
comment|// Register "ACCOUNT" table.
name|MockTable
name|accountTable
init|=
operator|new
name|MockTable
argument_list|(
name|this
argument_list|,
name|customerSchema
argument_list|,
literal|"ACCOUNT"
argument_list|)
decl_stmt|;
name|accountTable
operator|.
name|addColumn
argument_list|(
literal|"ACCTNO"
argument_list|,
name|intType
argument_list|)
expr_stmt|;
name|accountTable
operator|.
name|addColumn
argument_list|(
literal|"TYPE"
argument_list|,
name|varchar20Type
argument_list|)
expr_stmt|;
name|accountTable
operator|.
name|addColumn
argument_list|(
literal|"BALANCE"
argument_list|,
name|intType
argument_list|)
expr_stmt|;
name|registerTable
argument_list|(
name|accountTable
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Prepare
operator|.
name|CatalogReader
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
name|this
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
block|{
block|}
specifier|protected
name|void
name|registerTable
parameter_list|(
name|MockTable
name|table
parameter_list|)
block|{
name|table
operator|.
name|onRegister
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
name|tables
operator|.
name|put
argument_list|(
name|table
operator|.
name|getQualifiedName
argument_list|()
argument_list|,
name|table
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|registerSchema
parameter_list|(
name|MockSchema
name|schema
parameter_list|)
block|{
name|schemas
operator|.
name|put
argument_list|(
name|schema
operator|.
name|name
argument_list|,
name|schema
argument_list|)
expr_stmt|;
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
switch|switch
condition|(
name|names
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|1
case|:
comment|// assume table in SALES schema (the original default)
comment|// if it's not supplied, because SqlValidatorTest is effectively
comment|// using SALES as its default schema.
return|return
name|tables
operator|.
name|get
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|DEFAULT_CATALOG
argument_list|,
name|DEFAULT_SCHEMA
argument_list|,
name|names
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
return|;
case|case
literal|2
case|:
return|return
name|tables
operator|.
name|get
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|DEFAULT_CATALOG
argument_list|,
name|names
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|names
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
return|;
case|case
literal|3
case|:
return|return
name|tables
operator|.
name|get
argument_list|(
name|names
argument_list|)
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|RelDataType
name|getNamedType
parameter_list|(
name|SqlIdentifier
name|typeName
parameter_list|)
block|{
if|if
condition|(
name|typeName
operator|.
name|equalsDeep
argument_list|(
name|addressType
operator|.
name|getSqlIdentifier
argument_list|()
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return
name|addressType
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
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|result
decl_stmt|;
switch|switch
condition|(
name|names
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
comment|// looking for schema names
name|result
operator|=
operator|new
name|ArrayList
argument_list|<
name|SqlMoniker
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|MockSchema
name|schema
range|:
name|schemas
operator|.
name|values
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
name|name
argument_list|,
name|SqlMonikerType
operator|.
name|SCHEMA
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
case|case
literal|1
case|:
comment|// looking for table names in the given schema
name|MockSchema
name|schema
init|=
name|schemas
operator|.
name|get
argument_list|(
name|names
operator|.
name|get
argument_list|(
literal|0
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
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|result
operator|=
operator|new
name|ArrayList
argument_list|<
name|SqlMoniker
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|tableName
range|:
name|schema
operator|.
name|tableNames
control|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|SqlMonikerImpl
argument_list|(
name|tableName
argument_list|,
name|SqlMonikerType
operator|.
name|TABLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
default|default:
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
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
name|Collections
operator|.
name|singletonList
argument_list|(
name|DEFAULT_SCHEMA
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
specifier|final
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
specifier|private
specifier|static
name|List
argument_list|<
name|RelCollation
argument_list|>
name|deduceMonotonicity
parameter_list|(
name|Prepare
operator|.
name|PreparingTable
name|table
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList
init|=
operator|new
name|ArrayList
argument_list|<
name|RelCollation
argument_list|>
argument_list|()
decl_stmt|;
comment|// Deduce which fields the table is sorted on.
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|table
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
control|)
block|{
operator|++
name|i
expr_stmt|;
specifier|final
name|SqlMonotonicity
name|monotonicity
init|=
name|table
operator|.
name|getMonotonicity
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|monotonicity
operator|!=
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
condition|)
block|{
specifier|final
name|RelFieldCollation
operator|.
name|Direction
name|direction
init|=
name|monotonicity
operator|.
name|isDecreasing
argument_list|()
condition|?
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|DESCENDING
else|:
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|ASCENDING
decl_stmt|;
name|collationList
operator|.
name|add
argument_list|(
name|RelCollationImpl
operator|.
name|of
argument_list|(
operator|new
name|RelFieldCollation
argument_list|(
name|i
argument_list|,
name|direction
argument_list|,
name|RelFieldCollation
operator|.
name|NullDirection
operator|.
name|UNSPECIFIED
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|collationList
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
specifier|public
specifier|static
class|class
name|MockSchema
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|tableNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|public
name|MockSchema
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|void
name|addTable
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|tableNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getCatalogName
parameter_list|()
block|{
return|return
name|DEFAULT_CATALOG
return|;
block|}
block|}
comment|/**    * Mock implementation of    * {@link net.hydromatic.optiq.prepare.Prepare.PreparingTable}.    */
specifier|public
specifier|static
class|class
name|MockTable
implements|implements
name|Prepare
operator|.
name|PreparingTable
block|{
specifier|private
specifier|final
name|MockCatalogReader
name|catalogReader
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
argument_list|>
name|columnList
init|=
operator|new
name|ArrayList
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|RelDataType
name|rowType
decl_stmt|;
specifier|private
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|names
decl_stmt|;
specifier|public
name|MockTable
parameter_list|(
name|MockCatalogReader
name|catalogReader
parameter_list|,
name|MockSchema
name|schema
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|catalogReader
operator|=
name|catalogReader
expr_stmt|;
name|this
operator|.
name|names
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|schema
operator|.
name|getCatalogName
argument_list|()
argument_list|,
name|schema
operator|.
name|name
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|schema
operator|.
name|addTable
argument_list|(
name|name
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|this
argument_list|)
condition|)
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
return|return
literal|null
return|;
block|}
specifier|public
name|double
name|getRowCount
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|RelOptSchema
name|getRelOptSchema
parameter_list|()
block|{
return|return
name|catalogReader
return|;
block|}
specifier|public
name|RelNode
name|toRel
parameter_list|(
name|ToRelContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|TableAccessRel
argument_list|(
name|context
operator|.
name|getCluster
argument_list|()
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollationList
parameter_list|()
block|{
return|return
name|collationList
return|;
block|}
specifier|public
name|boolean
name|isKey
parameter_list|(
name|BitSet
name|columns
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|()
block|{
return|return
name|rowType
return|;
block|}
specifier|public
name|void
name|onRegister
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|rowType
operator|=
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|columnList
argument_list|)
expr_stmt|;
name|collationList
operator|=
name|deduceMonotonicity
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getQualifiedName
parameter_list|()
block|{
return|return
name|names
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
name|SqlAccessType
name|getAllowedAccess
parameter_list|()
block|{
return|return
name|SqlAccessType
operator|.
name|ALL
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|void
name|addColumn
parameter_list|(
name|int
name|index
parameter_list|,
name|String
name|name
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
name|columnList
operator|.
name|add
argument_list|(
name|index
argument_list|,
name|Pair
operator|.
name|of
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addColumn
parameter_list|(
name|String
name|name
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
name|columnList
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End MockCatalogReader.java
end_comment

end_unit

