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
name|plan
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
name|rex
operator|.
name|RexInputRef
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
name|parser
operator|.
name|SqlParser
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
name|tools
operator|.
name|Frameworks
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
name|tools
operator|.
name|RelBuilder
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
name|TestUtil
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link RelOptUtil} and other classes in this package.  */
end_comment

begin_class
specifier|public
class|class
name|RelOptUtilTest
block|{
comment|/** Creates a config based on the "scott" schema. */
specifier|private
specifier|static
name|Frameworks
operator|.
name|ConfigBuilder
name|config
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
return|return
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|parserConfig
argument_list|(
name|SqlParser
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|)
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
name|SCOTT
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
specifier|final
name|RelBuilder
name|REL_BUILDER
init|=
name|RelBuilder
operator|.
name|create
argument_list|(
name|config
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|RelNode
name|EMP_SCAN
init|=
name|REL_BUILDER
operator|.
name|scan
argument_list|(
literal|"EMP"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|RelNode
name|DEPT_SCAN
init|=
name|REL_BUILDER
operator|.
name|scan
argument_list|(
literal|"DEPT"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|RelDataType
name|EMP_ROW
init|=
name|EMP_SCAN
operator|.
name|getRowType
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|RelDataType
name|DEPT_ROW
init|=
name|DEPT_SCAN
operator|.
name|getRowType
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|EMP_DEPT_JOIN_REL_FIELDS
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|EMP_ROW
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|DEPT_ROW
operator|.
name|getFieldList
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RelOptUtilTest
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Test
specifier|public
name|void
name|testTypeDump
parameter_list|()
block|{
name|RelDataTypeFactory
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
name|RelDataType
name|t1
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"f0"
argument_list|,
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
literal|5
argument_list|,
literal|2
argument_list|)
operator|.
name|add
argument_list|(
literal|"f1"
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
literal|10
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|assertEqualsVerbose
argument_list|(
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"f0 DECIMAL(5, 2) NOT NULL,"
argument_list|,
literal|"f1 VARCHAR(10) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\" NOT NULL"
argument_list|)
argument_list|,
name|Util
operator|.
name|toLinux
argument_list|(
name|RelOptUtil
operator|.
name|dumpType
argument_list|(
name|t1
argument_list|)
operator|+
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
name|RelDataType
name|t2
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"f0"
argument_list|,
name|t1
argument_list|)
operator|.
name|add
argument_list|(
literal|"f1"
argument_list|,
name|typeFactory
operator|.
name|createMultisetType
argument_list|(
name|t1
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|assertEqualsVerbose
argument_list|(
name|TestUtil
operator|.
name|fold
argument_list|(
literal|"f0 RECORD ("
argument_list|,
literal|"  f0 DECIMAL(5, 2) NOT NULL,"
argument_list|,
literal|"  f1 VARCHAR(10) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\" NOT NULL) NOT NULL,"
argument_list|,
literal|"f1 RECORD ("
argument_list|,
literal|"  f0 DECIMAL(5, 2) NOT NULL,"
argument_list|,
literal|"  f1 VARCHAR(10) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\" NOT NULL) NOT NULL MULTISET NOT NULL"
argument_list|)
argument_list|,
name|Util
operator|.
name|toLinux
argument_list|(
name|RelOptUtil
operator|.
name|dumpType
argument_list|(
name|t2
argument_list|)
operator|+
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests the rules for how we name rules.    */
annotation|@
name|Test
specifier|public
name|void
name|testRuleGuessDescription
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"Bar"
argument_list|,
name|RelOptRule
operator|.
name|guessDescription
argument_list|(
literal|"com.foo.Bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Baz"
argument_list|,
name|RelOptRule
operator|.
name|guessDescription
argument_list|(
literal|"com.flatten.Bar$Baz"
argument_list|)
argument_list|)
expr_stmt|;
comment|// yields "1" (which as an integer is an invalid
try|try
block|{
name|Util
operator|.
name|discard
argument_list|(
name|RelOptRule
operator|.
name|guessDescription
argument_list|(
literal|"com.foo.Bar$1"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Derived description of rule class com.foo.Bar$1 is an "
operator|+
literal|"integer, not valid. Supply a description manually."
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test {@link RelOptUtil#splitJoinCondition(RelNode, RelNode, RexNode, List, List, List)}    * where the join condition contains just one which is a EQUAL operator.    */
annotation|@
name|Test
specifier|public
name|void
name|testSplitJoinConditionEquals
parameter_list|()
block|{
name|int
name|leftJoinIndex
init|=
name|EMP_SCAN
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"DEPTNO"
argument_list|)
decl_stmt|;
name|int
name|rightJoinIndex
init|=
name|DEPT_ROW
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"DEPTNO"
argument_list|)
decl_stmt|;
name|RexNode
name|joinCond
init|=
name|REL_BUILDER
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|RexInputRef
operator|.
name|of
argument_list|(
name|leftJoinIndex
argument_list|,
name|EMP_DEPT_JOIN_REL_FIELDS
argument_list|)
argument_list|,
name|RexInputRef
operator|.
name|of
argument_list|(
name|EMP_ROW
operator|.
name|getFieldCount
argument_list|()
operator|+
name|rightJoinIndex
argument_list|,
name|EMP_DEPT_JOIN_REL_FIELDS
argument_list|)
argument_list|)
decl_stmt|;
name|splitJoinConditionHelper
argument_list|(
name|joinCond
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|leftJoinIndex
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|rightJoinIndex
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|true
argument_list|)
argument_list|,
name|REL_BUILDER
operator|.
name|literal
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test {@link RelOptUtil#splitJoinCondition(RelNode, RelNode, RexNode, List, List, List)}    * where the join condition contains just one which is a IS NOT DISTINCT operator.    */
annotation|@
name|Test
specifier|public
name|void
name|testSplitJoinConditionIsNotDistinctFrom
parameter_list|()
block|{
name|int
name|leftJoinIndex
init|=
name|EMP_SCAN
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"DEPTNO"
argument_list|)
decl_stmt|;
name|int
name|rightJoinIndex
init|=
name|DEPT_ROW
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"DEPTNO"
argument_list|)
decl_stmt|;
name|RexNode
name|joinCond
init|=
name|REL_BUILDER
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_DISTINCT_FROM
argument_list|,
name|RexInputRef
operator|.
name|of
argument_list|(
name|leftJoinIndex
argument_list|,
name|EMP_DEPT_JOIN_REL_FIELDS
argument_list|)
argument_list|,
name|RexInputRef
operator|.
name|of
argument_list|(
name|EMP_ROW
operator|.
name|getFieldCount
argument_list|()
operator|+
name|rightJoinIndex
argument_list|,
name|EMP_DEPT_JOIN_REL_FIELDS
argument_list|)
argument_list|)
decl_stmt|;
name|splitJoinConditionHelper
argument_list|(
name|joinCond
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|leftJoinIndex
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|rightJoinIndex
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|false
argument_list|)
argument_list|,
name|REL_BUILDER
operator|.
name|literal
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test {@link RelOptUtil#splitJoinCondition(RelNode, RelNode, RexNode, List, List, List)}    * where the join condition contains an expanded version of IS NOT DISTINCT    */
annotation|@
name|Test
specifier|public
name|void
name|testSplitJoinConditionExpandedIsNotDistinctFrom
parameter_list|()
block|{
name|int
name|leftJoinIndex
init|=
name|EMP_SCAN
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"DEPTNO"
argument_list|)
decl_stmt|;
name|int
name|rightJoinIndex
init|=
name|DEPT_ROW
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"DEPTNO"
argument_list|)
decl_stmt|;
name|RexInputRef
name|leftKeyInputRef
init|=
name|RexInputRef
operator|.
name|of
argument_list|(
name|leftJoinIndex
argument_list|,
name|EMP_DEPT_JOIN_REL_FIELDS
argument_list|)
decl_stmt|;
name|RexInputRef
name|rightKeyInputRef
init|=
name|RexInputRef
operator|.
name|of
argument_list|(
name|EMP_ROW
operator|.
name|getFieldCount
argument_list|()
operator|+
name|rightJoinIndex
argument_list|,
name|EMP_DEPT_JOIN_REL_FIELDS
argument_list|)
decl_stmt|;
name|RexNode
name|joinCond
init|=
name|REL_BUILDER
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OR
argument_list|,
name|REL_BUILDER
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|leftKeyInputRef
argument_list|,
name|rightKeyInputRef
argument_list|)
argument_list|,
name|REL_BUILDER
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|REL_BUILDER
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NULL
argument_list|,
name|leftKeyInputRef
argument_list|)
argument_list|,
name|REL_BUILDER
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NULL
argument_list|,
name|rightKeyInputRef
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|splitJoinConditionHelper
argument_list|(
name|joinCond
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|leftJoinIndex
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|rightJoinIndex
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|false
argument_list|)
argument_list|,
name|REL_BUILDER
operator|.
name|literal
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|splitJoinConditionHelper
parameter_list|(
name|RexNode
name|joinCond
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|expLeftKeys
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|expRightKeys
parameter_list|,
name|List
argument_list|<
name|Boolean
argument_list|>
name|expFilterNulls
parameter_list|,
name|RexNode
name|expRemaining
parameter_list|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|actLeftKeys
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|actRightKeys
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Boolean
argument_list|>
name|actFilterNulls
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|RexNode
name|actRemaining
init|=
name|RelOptUtil
operator|.
name|splitJoinCondition
argument_list|(
name|EMP_SCAN
argument_list|,
name|DEPT_SCAN
argument_list|,
name|joinCond
argument_list|,
name|actLeftKeys
argument_list|,
name|actRightKeys
argument_list|,
name|actFilterNulls
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expRemaining
operator|.
name|toString
argument_list|()
argument_list|,
name|actRemaining
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expFilterNulls
argument_list|,
name|actFilterNulls
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expLeftKeys
argument_list|,
name|actLeftKeys
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expRightKeys
argument_list|,
name|actRightKeys
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End RelOptUtilTest.java
end_comment

end_unit

