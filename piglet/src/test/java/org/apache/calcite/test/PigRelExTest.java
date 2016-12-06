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
name|util
operator|.
name|TestUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matcher
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
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|test
operator|.
name|Matchers
operator|.
name|inTree
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
name|containsString
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

begin_comment
comment|/**  * Tests for {@code PigRelExVisitor}.  */
end_comment

begin_class
specifier|public
class|class
name|PigRelExTest
extends|extends
name|PigRelTestBase
block|{
specifier|private
name|void
name|checkTranslation
parameter_list|(
name|String
name|pigExpr
parameter_list|,
name|Matcher
argument_list|<
name|RelNode
argument_list|>
name|relMatcher
parameter_list|)
block|{
name|String
name|pigScript
init|=
literal|""
operator|+
literal|"A = LOAD 'test' as (a:int, b:long, c:float, d:double,\n"
operator|+
literal|"    e:chararray, f:bytearray, g:boolean, h:datetime,\n"
operator|+
literal|"    i:biginteger, j:bigdecimal, k1:tuple(),\n"
operator|+
literal|"    k2:tuple(k21:int, k22:(k221:long, k222:chararray)), l1:bag{},\n"
operator|+
literal|"    l2:bag{(l21:int, l22:float)}, m1:map[], m2:map[int],\n"
operator|+
literal|"    m3:map[(m31:float)]);\n"
operator|+
literal|"B = FILTER A BY "
operator|+
name|pigExpr
operator|+
literal|";\n"
decl_stmt|;
try|try
block|{
specifier|final
name|RelNode
name|rel
init|=
name|converter
operator|.
name|pigQuery2Rel
argument_list|(
name|pigScript
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|rel
argument_list|,
name|relMatcher
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|TestUtil
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|checkType
parameter_list|(
name|String
name|pigExpr
parameter_list|,
name|Matcher
argument_list|<
name|String
argument_list|>
name|rowTypeMatcher
parameter_list|)
block|{
name|String
name|pigScript
init|=
literal|""
operator|+
literal|"A = LOAD 'test' as (a:int);\n"
operator|+
literal|"B = FOREACH A GENERATE a, "
operator|+
name|pigExpr
operator|+
literal|";\n"
decl_stmt|;
try|try
block|{
specifier|final
name|RelNode
name|rel
init|=
name|converter
operator|.
name|pigQuery2Rel
argument_list|(
name|pigScript
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|rowTypeMatcher
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|TestUtil
operator|.
name|rethrow
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConstantBoolean
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"g == false"
argument_list|,
name|inTree
argument_list|(
literal|"NOT($6)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConstantType
parameter_list|()
block|{
name|checkType
argument_list|(
literal|"0L as longCol"
argument_list|,
name|containsString
argument_list|(
literal|"BIGINT longCol"
argument_list|)
argument_list|)
expr_stmt|;
name|checkType
argument_list|(
literal|"0 as intCol"
argument_list|,
name|containsString
argument_list|(
literal|"INTEGER intCol"
argument_list|)
argument_list|)
expr_stmt|;
name|checkType
argument_list|(
literal|"0.0 as doubleCol"
argument_list|,
name|containsString
argument_list|(
literal|"DOUBLE doubleCol"
argument_list|)
argument_list|)
expr_stmt|;
name|checkType
argument_list|(
literal|"'0.0' as charCol"
argument_list|,
name|containsString
argument_list|(
literal|"CHAR(3) charCol"
argument_list|)
argument_list|)
expr_stmt|;
name|checkType
argument_list|(
literal|"true as boolCol"
argument_list|,
name|containsString
argument_list|(
literal|"BOOLEAN boolCol"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConstantFloat
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|".1E6 == -2.3"
argument_list|,
name|inTree
argument_list|(
literal|"=(1E5:DOUBLE, -2.3:DECIMAL(2, 1))"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConstantString
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"'test' == 'passed'"
argument_list|,
name|inTree
argument_list|(
literal|"=('test', 'passed')"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProjection
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"g"
argument_list|,
name|inTree
argument_list|(
literal|"=[$6]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNegation
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"-b == -6"
argument_list|,
name|inTree
argument_list|(
literal|"=(-($1), -6)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEqual
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"a == 10"
argument_list|,
name|inTree
argument_list|(
literal|"=($0, 10)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotEqual
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"b != 10"
argument_list|,
name|inTree
argument_list|(
literal|"<>($1, 10)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLessThan
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"b< 10"
argument_list|,
name|inTree
argument_list|(
literal|"<($1, 10)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLessThanEqual
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"b<= 10"
argument_list|,
name|inTree
argument_list|(
literal|"<=($1, 10)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGreaterThan
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"b> 10"
argument_list|,
name|inTree
argument_list|(
literal|">($1, 10)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGreaterThanEqual
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"b>= 10"
argument_list|,
name|inTree
argument_list|(
literal|">=($1, 10)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
specifier|public
name|void
name|testMatch
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"e matches 'A*BC.D'"
argument_list|,
name|inTree
argument_list|(
literal|"LIKE($4, 'A%BC_D')"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|// End PigRelExTest.java
specifier|public
name|void
name|testIsNull
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"e is null"
argument_list|,
name|inTree
argument_list|(
literal|"IS NULL($4)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsNotNull
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"c is not null"
argument_list|,
name|inTree
argument_list|(
literal|"IS NOT NULL($2)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNot
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"NOT(a is null)"
argument_list|,
name|inTree
argument_list|(
literal|"IS NOT NULL($0)"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"NOT(g)"
argument_list|,
name|inTree
argument_list|(
literal|"NOT($6)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAnd
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"a> 10 and g"
argument_list|,
name|inTree
argument_list|(
literal|"AND(>($0, 10), $6)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOr
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"a> 10 or g"
argument_list|,
name|inTree
argument_list|(
literal|"OR(>($0, 10), $6)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAdd
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"b + 3"
argument_list|,
name|inTree
argument_list|(
literal|"+($1, 3)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSubtract
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"b - 3"
argument_list|,
name|inTree
argument_list|(
literal|"-($1, 3)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultiply
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"b * 3"
argument_list|,
name|inTree
argument_list|(
literal|"*($1, 3)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMod
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"b % 3"
argument_list|,
name|inTree
argument_list|(
literal|"MOD($1, 3)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDivide
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"b / 3"
argument_list|,
name|inTree
argument_list|(
literal|"/($1, 3)"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"c / 3.1"
argument_list|,
name|inTree
argument_list|(
literal|"/($2, 3.1E0:DOUBLE)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBinCond
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"(b == 1 ? 2 : 3)"
argument_list|,
name|inTree
argument_list|(
literal|"CASE(=($1, 1), 2, 3)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTupleDereference
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"k2.k21"
argument_list|,
name|inTree
argument_list|(
literal|"[$11.k21]"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"k2.(k21, k22)"
argument_list|,
name|inTree
argument_list|(
literal|"[ROW($11.k21, $11.k22)]"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"k2.k22.(k221,k222)"
argument_list|,
name|inTree
argument_list|(
literal|"[ROW($11.k22.k221, $11.k22.k222)]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBagDereference
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"l2.l22"
argument_list|,
name|inTree
argument_list|(
literal|"[MULTISET_PROJECTION($13, 1)]"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"l2.(l21, l22)"
argument_list|,
name|inTree
argument_list|(
literal|"[MULTISET_PROJECTION($13, 0, 1)]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMapLookup
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"m2#'testKey'"
argument_list|,
name|inTree
argument_list|(
literal|"ITEM($15, 'testKey')"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCast
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"(int) b"
argument_list|,
name|inTree
argument_list|(
literal|"CAST($1):INTEGER"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"(long) a"
argument_list|,
name|inTree
argument_list|(
literal|"CAST($0):BIGINT"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"(float) b"
argument_list|,
name|inTree
argument_list|(
literal|"CAST($1):FLOAT"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"(double) b"
argument_list|,
name|inTree
argument_list|(
literal|"CAST($1):DOUBLE"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"(chararray) b"
argument_list|,
name|inTree
argument_list|(
literal|"CAST($1):VARCHAR"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"(bytearray) b"
argument_list|,
name|inTree
argument_list|(
literal|"CAST($1):BINARY"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"(boolean) c"
argument_list|,
name|inTree
argument_list|(
literal|"CAST($2):BOOLEAN"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"(biginteger) b"
argument_list|,
name|inTree
argument_list|(
literal|"CAST($1):DECIMAL(19, 0)"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"(bigdecimal) b"
argument_list|,
name|inTree
argument_list|(
literal|"CAST($1):DECIMAL(19, 0)"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"(tuple()) b"
argument_list|,
name|inTree
argument_list|(
literal|"CAST($1):(DynamicRecordRow[])"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"(tuple(int, float)) b"
argument_list|,
name|inTree
argument_list|(
literal|"CAST($1):RecordType(INTEGER $0, FLOAT $1)"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"(bag{}) b"
argument_list|,
name|inTree
argument_list|(
literal|"CAST($1):(DynamicRecordRow[]) NOT NULL MULTISET"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"(bag{tuple(int)}) b"
argument_list|,
name|inTree
argument_list|(
literal|"CAST($1):RecordType(INTEGER $0) MULTISET"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"(bag{tuple(int, float)}) b"
argument_list|,
name|inTree
argument_list|(
literal|"CAST($1):RecordType(INTEGER $0, FLOAT $1) MULTISET"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"(map[]) b"
argument_list|,
name|inTree
argument_list|(
literal|"CAST($1):(VARCHAR NOT NULL, BINARY(1) NOT NULL) MAP"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"(map[int]) b"
argument_list|,
name|inTree
argument_list|(
literal|"CAST($1):(VARCHAR NOT NULL, INTEGER"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"(map[tuple(int, float)]) b"
argument_list|,
name|inTree
argument_list|(
literal|"CAST($1):(VARCHAR NOT NULL, RecordType(INTEGER val_0, FLOAT val_1)) MAP"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPigBuiltinFunctions
parameter_list|()
block|{
name|checkTranslation
argument_list|(
literal|"ABS(-5)"
argument_list|,
name|inTree
argument_list|(
literal|"ABS(-5)"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"AddDuration(h, 'P1D')"
argument_list|,
name|inTree
argument_list|(
literal|"AddDuration(PIG_TUPLE($7, 'P1D'))"
argument_list|)
argument_list|)
expr_stmt|;
name|checkTranslation
argument_list|(
literal|"CEIL(1.2)"
argument_list|,
name|inTree
argument_list|(
literal|"CEIL(1.2E0:DOUBLE)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End PigRelExTest.java
end_comment

end_unit

