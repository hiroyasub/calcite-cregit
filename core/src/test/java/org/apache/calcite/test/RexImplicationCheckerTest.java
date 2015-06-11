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
name|test
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
name|DataContext
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
name|RexImplicationChecker
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
name|RexExecutorImpl
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
name|SqlCollation
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
name|util
operator|.
name|NlsString
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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
name|assertTrue
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
import|import
name|java
operator|.
name|sql
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Time
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_comment
comment|/**  * Tests the RexImplication checker  */
end_comment

begin_class
specifier|public
class|class
name|RexImplicationCheckerTest
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|RexBuilder
name|rexBuilder
init|=
literal|null
decl_stmt|;
specifier|private
name|RexNode
name|bl
decl_stmt|;
specifier|private
name|RexNode
name|i
decl_stmt|;
specifier|private
name|RexNode
name|dec
decl_stmt|;
specifier|private
name|RexNode
name|lg
decl_stmt|;
specifier|private
name|RexNode
name|sh
decl_stmt|;
specifier|private
name|RexNode
name|by
decl_stmt|;
specifier|private
name|RexNode
name|fl
decl_stmt|;
specifier|private
name|RexNode
name|dt
decl_stmt|;
specifier|private
name|RexNode
name|ch
decl_stmt|;
specifier|private
name|RexNode
name|ts
decl_stmt|;
specifier|private
name|RexNode
name|t
decl_stmt|;
specifier|private
name|RelDataType
name|boolRelDataType
decl_stmt|;
specifier|private
name|RelDataType
name|intRelDataType
decl_stmt|;
specifier|private
name|RelDataType
name|decRelDataType
decl_stmt|;
specifier|private
name|RelDataType
name|longRelDataType
decl_stmt|;
specifier|private
name|RelDataType
name|shortDataType
decl_stmt|;
specifier|private
name|RelDataType
name|byteDataType
decl_stmt|;
specifier|private
name|RelDataType
name|floatDataType
decl_stmt|;
specifier|private
name|RelDataType
name|charDataType
decl_stmt|;
specifier|private
name|RelDataType
name|dateDataType
decl_stmt|;
specifier|private
name|RelDataType
name|timeStampDataType
decl_stmt|;
specifier|private
name|RelDataType
name|timeDataType
decl_stmt|;
specifier|private
name|RelDataTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
name|RexImplicationChecker
name|checker
decl_stmt|;
specifier|private
name|RelDataType
name|rowType
decl_stmt|;
specifier|private
name|RexExecutorImpl
name|executor
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|typeFactory
operator|=
operator|new
name|JavaTypeFactoryImpl
argument_list|(
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|rexBuilder
operator|=
operator|new
name|RexBuilder
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
name|boolRelDataType
operator|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
expr_stmt|;
name|intRelDataType
operator|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
expr_stmt|;
name|decRelDataType
operator|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|Double
operator|.
name|class
argument_list|)
expr_stmt|;
name|longRelDataType
operator|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|Long
operator|.
name|class
argument_list|)
expr_stmt|;
name|shortDataType
operator|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|Short
operator|.
name|class
argument_list|)
expr_stmt|;
name|byteDataType
operator|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|Byte
operator|.
name|class
argument_list|)
expr_stmt|;
name|floatDataType
operator|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|Float
operator|.
name|class
argument_list|)
expr_stmt|;
name|charDataType
operator|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|Character
operator|.
name|class
argument_list|)
expr_stmt|;
name|dateDataType
operator|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|Date
operator|.
name|class
argument_list|)
expr_stmt|;
name|timeStampDataType
operator|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|Timestamp
operator|.
name|class
argument_list|)
expr_stmt|;
name|timeDataType
operator|=
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|Time
operator|.
name|class
argument_list|)
expr_stmt|;
name|bl
operator|=
operator|new
name|RexInputRef
argument_list|(
literal|0
argument_list|,
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|boolRelDataType
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|=
operator|new
name|RexInputRef
argument_list|(
literal|1
argument_list|,
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|intRelDataType
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|dec
operator|=
operator|new
name|RexInputRef
argument_list|(
literal|2
argument_list|,
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|decRelDataType
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|lg
operator|=
operator|new
name|RexInputRef
argument_list|(
literal|3
argument_list|,
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|longRelDataType
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|sh
operator|=
operator|new
name|RexInputRef
argument_list|(
literal|4
argument_list|,
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|shortDataType
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|by
operator|=
operator|new
name|RexInputRef
argument_list|(
literal|5
argument_list|,
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|byteDataType
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|fl
operator|=
operator|new
name|RexInputRef
argument_list|(
literal|6
argument_list|,
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|floatDataType
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|ch
operator|=
operator|new
name|RexInputRef
argument_list|(
literal|7
argument_list|,
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|charDataType
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|dt
operator|=
operator|new
name|RexInputRef
argument_list|(
literal|8
argument_list|,
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|dateDataType
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|ts
operator|=
operator|new
name|RexInputRef
argument_list|(
literal|9
argument_list|,
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|timeStampDataType
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|=
operator|new
name|RexInputRef
argument_list|(
literal|10
argument_list|,
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|timeDataType
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|rowType
operator|=
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"bool"
argument_list|,
name|boolRelDataType
argument_list|)
operator|.
name|add
argument_list|(
literal|"int"
argument_list|,
name|intRelDataType
argument_list|)
operator|.
name|add
argument_list|(
literal|"dec"
argument_list|,
name|decRelDataType
argument_list|)
operator|.
name|add
argument_list|(
literal|"long"
argument_list|,
name|longRelDataType
argument_list|)
operator|.
name|add
argument_list|(
literal|"short"
argument_list|,
name|shortDataType
argument_list|)
operator|.
name|add
argument_list|(
literal|"byte"
argument_list|,
name|byteDataType
argument_list|)
operator|.
name|add
argument_list|(
literal|"float"
argument_list|,
name|floatDataType
argument_list|)
operator|.
name|add
argument_list|(
literal|"char"
argument_list|,
name|charDataType
argument_list|)
operator|.
name|add
argument_list|(
literal|"date"
argument_list|,
name|dateDataType
argument_list|)
operator|.
name|add
argument_list|(
literal|"timestamp"
argument_list|,
name|timeStampDataType
argument_list|)
operator|.
name|add
argument_list|(
literal|"time"
argument_list|,
name|timeDataType
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
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
argument_list|()
block|{
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
name|DataContext
name|dataContext
init|=
name|Schemas
operator|.
name|createDataContext
argument_list|(
name|statement
operator|.
name|getConnection
argument_list|()
argument_list|)
decl_stmt|;
name|executor
operator|=
operator|new
name|RexExecutorImpl
argument_list|(
name|dataContext
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|checker
operator|=
operator|new
name|RexImplicationChecker
argument_list|(
name|rexBuilder
argument_list|,
name|executor
argument_list|,
name|rowType
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkImplies
parameter_list|(
name|RexNode
name|node1
parameter_list|,
name|RexNode
name|node2
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|node1
operator|.
name|toString
argument_list|()
operator|+
literal|" doesnot imply "
operator|+
name|node2
operator|.
name|toString
argument_list|()
operator|+
literal|" when it should."
argument_list|,
name|checker
operator|.
name|implies
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkNotImplies
parameter_list|(
name|RexNode
name|node1
parameter_list|,
name|RexNode
name|node2
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|node1
operator|.
name|toString
argument_list|()
operator|+
literal|" implies "
operator|+
name|node2
operator|.
name|toString
argument_list|()
operator|+
literal|" when it should not"
argument_list|,
name|checker
operator|.
name|implies
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Simple Tests for Operators
annotation|@
name|Test
specifier|public
name|void
name|testSimpleGreaterCond
parameter_list|()
block|{
name|RexNode
name|node1
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
argument_list|,
name|i
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node2
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
argument_list|,
name|i
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|30
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node3
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN_OR_EQUAL
argument_list|,
name|i
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|30
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node4
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN_OR_EQUAL
argument_list|,
name|i
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node5
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|i
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|30
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node6
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_EQUALS
argument_list|,
name|i
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|checkImplies
argument_list|(
name|node2
argument_list|,
name|node1
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node1
argument_list|,
name|node3
argument_list|)
expr_stmt|;
name|checkImplies
argument_list|(
name|node3
argument_list|,
name|node1
argument_list|)
expr_stmt|;
name|checkImplies
argument_list|(
name|node5
argument_list|,
name|node1
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node1
argument_list|,
name|node5
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node1
argument_list|,
name|node6
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node4
argument_list|,
name|node6
argument_list|)
expr_stmt|;
comment|// TODO: Need to support Identity
comment|//checkImplies(node1, node1);
comment|//checkImplies(node3, node3);
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleLesserCond
parameter_list|()
block|{
name|RexNode
name|node1
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN
argument_list|,
name|i
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node2
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN
argument_list|,
name|i
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|30
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node3
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN_OR_EQUAL
argument_list|,
name|i
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|30
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node4
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN_OR_EQUAL
argument_list|,
name|i
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node5
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|i
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node6
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_EQUALS
argument_list|,
name|i
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|checkImplies
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node2
argument_list|,
name|node1
argument_list|)
expr_stmt|;
name|checkImplies
argument_list|(
name|node1
argument_list|,
name|node3
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node3
argument_list|,
name|node1
argument_list|)
expr_stmt|;
name|checkImplies
argument_list|(
name|node5
argument_list|,
name|node2
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node2
argument_list|,
name|node5
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node1
argument_list|,
name|node5
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node1
argument_list|,
name|node6
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node4
argument_list|,
name|node6
argument_list|)
expr_stmt|;
comment|// TODO: Need to support Identity
comment|//checkImplies(node1, node1);
comment|//checkImplies(node3, node3);
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleEq
parameter_list|()
block|{
name|RexNode
name|node1
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|i
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|30
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node2
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_EQUALS
argument_list|,
name|i
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|//Check Identity
name|checkImplies
argument_list|(
name|node1
argument_list|,
name|node1
argument_list|)
expr_stmt|;
comment|//TODO: Support Identity
comment|// checkImplies(node2, node2);
name|checkImplies
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node2
argument_list|,
name|node1
argument_list|)
expr_stmt|;
block|}
comment|// Simple Tests for DataTypes
annotation|@
name|Test
specifier|public
name|void
name|testSimpleDec
parameter_list|()
block|{
name|RexNode
name|node1
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN
argument_list|,
name|dec
argument_list|,
name|rexBuilder
operator|.
name|makeApproxLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|30.9
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node2
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN
argument_list|,
name|dec
argument_list|,
name|rexBuilder
operator|.
name|makeApproxLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|40.33
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|checkImplies
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node2
argument_list|,
name|node1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleBoolean
parameter_list|()
block|{
name|RexNode
name|node1
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|bl
argument_list|,
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node2
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|bl
argument_list|,
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
comment|//TODO: Need to support false => true
comment|//checkImplies(node2, node1);
name|checkNotImplies
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleLong
parameter_list|()
block|{
name|RexNode
name|node1
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN_OR_EQUAL
argument_list|,
name|lg
argument_list|,
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
operator|new
name|Long
argument_list|(
literal|324324L
argument_list|)
argument_list|,
name|longRelDataType
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node2
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
argument_list|,
name|lg
argument_list|,
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
operator|new
name|Long
argument_list|(
literal|324325L
argument_list|)
argument_list|,
name|longRelDataType
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|checkImplies
argument_list|(
name|node2
argument_list|,
name|node1
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleShort
parameter_list|()
block|{
name|RexNode
name|node1
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN_OR_EQUAL
argument_list|,
name|sh
argument_list|,
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
operator|new
name|Short
argument_list|(
operator|(
name|short
operator|)
literal|10
argument_list|)
argument_list|,
name|shortDataType
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node2
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN_OR_EQUAL
argument_list|,
name|sh
argument_list|,
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
operator|new
name|Short
argument_list|(
operator|(
name|short
operator|)
literal|11
argument_list|)
argument_list|,
name|shortDataType
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|checkImplies
argument_list|(
name|node2
argument_list|,
name|node1
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleChar
parameter_list|()
block|{
name|RexNode
name|node1
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN_OR_EQUAL
argument_list|,
name|ch
argument_list|,
name|rexBuilder
operator|.
name|makeCharLiteral
argument_list|(
operator|new
name|NlsString
argument_list|(
literal|"b"
argument_list|,
literal|null
argument_list|,
name|SqlCollation
operator|.
name|COERCIBLE
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node2
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN_OR_EQUAL
argument_list|,
name|ch
argument_list|,
name|rexBuilder
operator|.
name|makeCharLiteral
argument_list|(
operator|new
name|NlsString
argument_list|(
literal|"a"
argument_list|,
literal|null
argument_list|,
name|SqlCollation
operator|.
name|COERCIBLE
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|checkImplies
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node2
argument_list|,
name|node1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleDate
parameter_list|()
block|{
name|RexNode
name|node1
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN_OR_EQUAL
argument_list|,
name|dt
argument_list|,
name|rexBuilder
operator|.
name|makeDateLiteral
argument_list|(
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node2
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|dt
argument_list|,
name|rexBuilder
operator|.
name|makeDateLiteral
argument_list|(
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|checkImplies
argument_list|(
name|node2
argument_list|,
name|node1
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"work in progress"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testSimpleTimeStamp
parameter_list|()
block|{
name|RexNode
name|node1
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN_OR_EQUAL
argument_list|,
name|ts
argument_list|,
name|rexBuilder
operator|.
name|makeTimestampLiteral
argument_list|(
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|,
name|timeStampDataType
operator|.
name|getPrecision
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node2
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN_OR_EQUAL
argument_list|,
name|ts
argument_list|,
name|rexBuilder
operator|.
name|makeTimestampLiteral
argument_list|(
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|,
name|timeStampDataType
operator|.
name|getPrecision
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|checkImplies
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node2
argument_list|,
name|node1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"work in progress"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testSimpleTime
parameter_list|()
block|{
name|RexNode
name|node1
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN_OR_EQUAL
argument_list|,
name|t
argument_list|,
name|rexBuilder
operator|.
name|makeTimeLiteral
argument_list|(
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|,
name|timeDataType
operator|.
name|getPrecision
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|node2
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN_OR_EQUAL
argument_list|,
name|t
argument_list|,
name|rexBuilder
operator|.
name|makeTimestampLiteral
argument_list|(
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|,
name|timeDataType
operator|.
name|getPrecision
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|checkImplies
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|)
expr_stmt|;
name|checkNotImplies
argument_list|(
name|node2
argument_list|,
name|node1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

