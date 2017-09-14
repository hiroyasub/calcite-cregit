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
name|tools
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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumerableConvention
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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumerableTableScan
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
name|RelOptAbstractTable
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
name|RelOptCluster
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
name|plan
operator|.
name|RelOptSchema
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
name|plan
operator|.
name|RelTraitSet
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
name|RelNode
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
name|logical
operator|.
name|LogicalFilter
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeSystemImpl
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
name|RexLiteral
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
name|SchemaPlus
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
name|Schemas
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
name|AbstractTable
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
name|server
operator|.
name|CalciteServerStatement
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
name|SqlExplainFormat
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
name|SqlExplainLevel
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
name|dialect
operator|.
name|AnsiSqlDialect
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|test
operator|.
name|CalciteAssert
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Unit tests for methods in {@link Frameworks}.  */
end_comment

begin_class
specifier|public
class|class
name|FrameworksTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testOptimize
parameter_list|()
block|{
name|RelNode
name|x
init|=
name|Frameworks
operator|.
name|withPlanner
argument_list|(
operator|new
name|Frameworks
operator|.
name|PlannerAction
argument_list|<
name|RelNode
argument_list|>
argument_list|()
block|{
specifier|public
name|RelNode
name|apply
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptSchema
name|relOptSchema
parameter_list|,
name|SchemaPlus
name|rootSchema
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|cluster
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|Table
name|table
init|=
operator|new
name|AbstractTable
argument_list|()
block|{
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
specifier|final
name|RelDataType
name|stringType
init|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|integerType
init|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"s"
argument_list|,
name|stringType
argument_list|)
operator|.
name|add
argument_list|(
literal|"i"
argument_list|,
name|integerType
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|// "SELECT * FROM myTable"
specifier|final
name|RelOptAbstractTable
name|relOptTable
init|=
operator|new
name|RelOptAbstractTable
argument_list|(
name|relOptSchema
argument_list|,
literal|"myTable"
argument_list|,
name|table
operator|.
name|getRowType
argument_list|(
name|typeFactory
argument_list|)
argument_list|)
block|{             }
decl_stmt|;
specifier|final
name|EnumerableTableScan
name|tableRel
init|=
name|EnumerableTableScan
operator|.
name|create
argument_list|(
name|cluster
argument_list|,
name|relOptTable
argument_list|)
decl_stmt|;
comment|// "WHERE i> 1"
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|cluster
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RexNode
name|condition
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
argument_list|,
name|rexBuilder
operator|.
name|makeFieldAccess
argument_list|(
name|rexBuilder
operator|.
name|makeRangeReference
argument_list|(
name|tableRel
argument_list|)
argument_list|,
literal|"i"
argument_list|,
literal|true
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|LogicalFilter
name|filter
init|=
name|LogicalFilter
operator|.
name|create
argument_list|(
name|tableRel
argument_list|,
name|condition
argument_list|)
decl_stmt|;
comment|// Specify that the result should be in Enumerable convention.
specifier|final
name|RelNode
name|rootRel
init|=
name|filter
decl_stmt|;
specifier|final
name|RelOptPlanner
name|planner
init|=
name|cluster
operator|.
name|getPlanner
argument_list|()
decl_stmt|;
name|RelTraitSet
name|desiredTraits
init|=
name|cluster
operator|.
name|traitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|rootRel2
init|=
name|planner
operator|.
name|changeTraits
argument_list|(
name|rootRel
argument_list|,
name|desiredTraits
argument_list|)
decl_stmt|;
name|planner
operator|.
name|setRoot
argument_list|(
name|rootRel2
argument_list|)
expr_stmt|;
comment|// Now, plan.
return|return
name|planner
operator|.
name|findBestExp
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|RelOptUtil
operator|.
name|dumpPlan
argument_list|(
literal|""
argument_list|,
name|x
argument_list|,
name|SqlExplainFormat
operator|.
name|TEXT
argument_list|,
name|SqlExplainLevel
operator|.
name|DIGEST_ATTRIBUTES
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|Util
operator|.
name|toLinux
argument_list|(
name|s
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"EnumerableFilter(condition=[>($1, 1)])\n"
operator|+
literal|"  EnumerableTableScan(table=[[myTable]])\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Unit test to test create root schema which has no "metadata" schema. */
annotation|@
name|Test
specifier|public
name|void
name|testCreateRootSchemaWithNoMetadataSchema
parameter_list|()
block|{
name|SchemaPlus
name|rootSchema
init|=
name|Frameworks
operator|.
name|createRootSchema
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|rootSchema
operator|.
name|getSubSchemaNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that validation (specifically, inferring the result of adding    * two DECIMAL(19, 0) values together) happens differently with a type system    * that allows a larger maximum precision for decimals.    *    *<p>Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-413">[CALCITE-413]    * Add RelDataTypeSystem plugin, allowing different max precision of a    * DECIMAL</a>.    *    *<p>Also tests the plugin system, by specifying implementations of a    * plugin interface with public and private constructors. */
annotation|@
name|Test
specifier|public
name|void
name|testTypeSystem
parameter_list|()
block|{
name|checkTypeSystem
argument_list|(
literal|19
argument_list|,
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|checkTypeSystem
argument_list|(
literal|25
argument_list|,
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|typeSystem
argument_list|(
name|HiveLikeTypeSystem
operator|.
name|INSTANCE
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|checkTypeSystem
argument_list|(
literal|31
argument_list|,
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|typeSystem
argument_list|(
operator|new
name|HiveLikeTypeSystem2
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkTypeSystem
parameter_list|(
specifier|final
name|int
name|expected
parameter_list|,
name|FrameworkConfig
name|config
parameter_list|)
block|{
name|Frameworks
operator|.
name|withPrepare
argument_list|(
operator|new
name|Frameworks
operator|.
name|PrepareAction
argument_list|<
name|Void
argument_list|>
argument_list|(
name|config
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Void
name|apply
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptSchema
name|relOptSchema
parameter_list|,
name|SchemaPlus
name|rootSchema
parameter_list|,
name|CalciteServerStatement
name|statement
parameter_list|)
block|{
specifier|final
name|RelDataType
name|type
init|=
name|cluster
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
literal|30
argument_list|,
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|RexLiteral
name|literal
init|=
name|cluster
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|,
name|type
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|call
init|=
name|cluster
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|,
name|literal
argument_list|,
name|literal
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|call
operator|.
name|getType
argument_list|()
operator|.
name|getPrecision
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that the validator expands identifiers by default.    *    *<p>Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-593">[CALCITE-593]    * Validator in Frameworks should expand identifiers</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testFrameworksValidatorWithIdentifierExpansion
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|Frameworks
operator|.
name|createRootSchema
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|FrameworkConfig
name|config
init|=
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|defaultSchema
argument_list|(
name|CalciteAssert
operator|.
name|addSchema
argument_list|(
name|rootSchema
argument_list|,
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|HR
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Planner
name|planner
init|=
name|Frameworks
operator|.
name|getPlanner
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select * from \"emps\" "
argument_list|)
decl_stmt|;
name|SqlNode
name|val
init|=
name|planner
operator|.
name|validate
argument_list|(
name|parse
argument_list|)
decl_stmt|;
name|String
name|valStr
init|=
name|val
operator|.
name|toSqlString
argument_list|(
name|AnsiSqlDialect
operator|.
name|DEFAULT
argument_list|,
literal|false
argument_list|)
operator|.
name|getSql
argument_list|()
decl_stmt|;
name|String
name|expandedStr
init|=
literal|"SELECT `emps`.`empid`, `emps`.`deptno`, `emps`.`name`, `emps`.`salary`, `emps`.`commission`\n"
operator|+
literal|"FROM `hr`.`emps` AS `emps`"
decl_stmt|;
name|assertThat
argument_list|(
name|Util
operator|.
name|toLinux
argument_list|(
name|valStr
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expandedStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test for {@link Path}. */
annotation|@
name|Test
specifier|public
name|void
name|testSchemaPath
parameter_list|()
block|{
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|Frameworks
operator|.
name|createRootSchema
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|FrameworkConfig
name|config
init|=
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|defaultSchema
argument_list|(
name|CalciteAssert
operator|.
name|addSchema
argument_list|(
name|rootSchema
argument_list|,
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|HR
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|path
init|=
name|Schemas
operator|.
name|path
argument_list|(
name|config
operator|.
name|getDefaultSchema
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|path
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|path
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|left
argument_list|,
name|is
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|path
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|left
argument_list|,
name|is
argument_list|(
literal|"hr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|path
operator|.
name|names
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|path
operator|.
name|names
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"hr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|path
operator|.
name|schemas
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|parent
init|=
name|path
operator|.
name|parent
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|parent
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parent
operator|.
name|names
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|grandparent
init|=
name|parent
operator|.
name|parent
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|grandparent
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|Object
name|o
init|=
name|grandparent
operator|.
name|parent
argument_list|()
decl_stmt|;
name|fail
argument_list|(
literal|"expected exception, got "
operator|+
name|o
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// ok
block|}
block|}
comment|/** Dummy type system, similar to Hive's, accessed via an INSTANCE member. */
specifier|public
specifier|static
class|class
name|HiveLikeTypeSystem
extends|extends
name|RelDataTypeSystemImpl
block|{
specifier|public
specifier|static
specifier|final
name|RelDataTypeSystem
name|INSTANCE
init|=
operator|new
name|HiveLikeTypeSystem
argument_list|()
decl_stmt|;
specifier|private
name|HiveLikeTypeSystem
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMaxNumericPrecision
parameter_list|()
block|{
assert|assert
name|super
operator|.
name|getMaxNumericPrecision
argument_list|()
operator|==
literal|19
assert|;
return|return
literal|25
return|;
block|}
block|}
comment|/** Dummy type system, similar to Hive's, accessed via a public default    * constructor. */
specifier|public
specifier|static
class|class
name|HiveLikeTypeSystem2
extends|extends
name|RelDataTypeSystemImpl
block|{
specifier|public
name|HiveLikeTypeSystem2
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMaxNumericPrecision
parameter_list|()
block|{
assert|assert
name|super
operator|.
name|getMaxNumericPrecision
argument_list|()
operator|==
literal|19
assert|;
return|return
literal|38
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End FrameworksTest.java
end_comment

end_unit

