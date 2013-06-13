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
name|math
operator|.
name|BigDecimal
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
name|rex
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|SqlTypeName
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
name|JavaTypeFactoryImpl
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
name|Test
import|;
end_import

begin_comment
comment|/**  * Unit tests for {@link RexProgram} and  * {@link org.eigenbase.rex.RexProgramBuilder}.  */
end_comment

begin_class
specifier|public
class|class
name|RexProgramTest
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
name|RexBuilder
name|rexBuilder
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Creates a RexProgramTest.      */
specifier|public
name|RexProgramTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
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
argument_list|()
expr_stmt|;
name|rexBuilder
operator|=
operator|new
name|RexBuilder
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests construction of a RexProgram.      */
annotation|@
name|Test
specifier|public
name|void
name|testBuildProgram
parameter_list|()
block|{
specifier|final
name|RexProgramBuilder
name|builder
init|=
name|createProg
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RexProgram
name|program
init|=
name|builder
operator|.
name|getProgram
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|String
name|programString
init|=
name|program
operator|.
name|toString
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|assertEqualsVerbose
argument_list|(
literal|"(expr#0..1=[{inputs}], expr#2=[+($0, 1)], expr#3=[77], "
operator|+
literal|"expr#4=[+($0, $1)], expr#5=[+($0, $0)], expr#6=[+($t4, $t2)], "
operator|+
literal|"a=[$t6], b=[$t5])"
argument_list|,
name|programString
argument_list|)
expr_stmt|;
comment|// Normalize the program using the RexProgramBuilder.normalize API.
comment|// Note that unused expression '77' is eliminated, input refs (e.g. $0)
comment|// become local refs (e.g. $t0), and constants are assigned to locals.
specifier|final
name|RexProgram
name|normalizedProgram
init|=
name|RexProgramBuilder
operator|.
name|normalize
argument_list|(
name|rexBuilder
argument_list|,
name|program
argument_list|)
decl_stmt|;
specifier|final
name|String
name|normalizedProgramString
init|=
name|normalizedProgram
operator|.
name|toString
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|assertEqualsVerbose
argument_list|(
literal|"(expr#0..1=[{inputs}], expr#2=[+($t0, $t1)], expr#3=[1], "
operator|+
literal|"expr#4=[+($t0, $t3)], expr#5=[+($t2, $t4)], "
operator|+
literal|"expr#6=[+($t0, $t0)], a=[$t5], b=[$t6])"
argument_list|,
name|normalizedProgramString
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests construction and normalization of a RexProgram.      */
annotation|@
name|Test
specifier|public
name|void
name|testNormalize
parameter_list|()
block|{
specifier|final
name|RexProgramBuilder
name|builder
init|=
name|createProg
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|String
name|program
init|=
name|builder
operator|.
name|getProgram
argument_list|(
literal|true
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|assertEqualsVerbose
argument_list|(
literal|"(expr#0..1=[{inputs}], expr#2=[+($t0, $t1)], expr#3=[1], "
operator|+
literal|"expr#4=[+($t0, $t3)], expr#5=[+($t2, $t4)], "
operator|+
literal|"expr#6=[+($t0, $t0)], a=[$t5], b=[$t6])"
argument_list|,
name|program
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests construction and normalization of a RexProgram.      */
annotation|@
name|Test
specifier|public
name|void
name|testElimDups
parameter_list|()
block|{
specifier|final
name|RexProgramBuilder
name|builder
init|=
name|createProg
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|String
name|unnormalizedProgram
init|=
name|builder
operator|.
name|getProgram
argument_list|(
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|assertEqualsVerbose
argument_list|(
literal|"(expr#0..1=[{inputs}], expr#2=[+($0, 1)], expr#3=[77], "
operator|+
literal|"expr#4=[+($0, $1)], expr#5=[+($0, 1)], expr#6=[+($0, $t5)], "
operator|+
literal|"expr#7=[+($t4, $t2)], a=[$t7], b=[$t6])"
argument_list|,
name|unnormalizedProgram
argument_list|)
expr_stmt|;
comment|// normalize eliminates dups (specifically "+($0, $1)")
specifier|final
name|RexProgramBuilder
name|builder2
init|=
name|createProg
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|String
name|program2
init|=
name|builder2
operator|.
name|getProgram
argument_list|(
literal|true
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|assertEqualsVerbose
argument_list|(
literal|"(expr#0..1=[{inputs}], expr#2=[+($t0, $t1)], expr#3=[1], "
operator|+
literal|"expr#4=[+($t0, $t3)], expr#5=[+($t2, $t4)], "
operator|+
literal|"expr#6=[+($t0, $t4)], a=[$t5], b=[$t6])"
argument_list|,
name|program2
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests that AND(x, x) is translated to x.      */
annotation|@
name|Test
specifier|public
name|void
name|testDuplicateAnd
parameter_list|()
block|{
specifier|final
name|RexProgramBuilder
name|builder
init|=
name|createProg
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|String
name|program
init|=
name|builder
operator|.
name|getProgram
argument_list|(
literal|true
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|assertEqualsVerbose
argument_list|(
literal|"(expr#0..1=[{inputs}], expr#2=[+($t0, $t1)], expr#3=[1], "
operator|+
literal|"expr#4=[+($t0, $t3)], expr#5=[+($t2, $t4)], "
operator|+
literal|"expr#6=[+($t0, $t0)], expr#7=[>($t2, $t0)], "
operator|+
literal|"a=[$t5], b=[$t6], $condition=[$t7])"
argument_list|,
name|program
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a program, depending on variant:      *<ol>      *<li><code>select (x + y) + (x + 1) as a, (x + x) as b from t(x, y)</code>      *<li><code>select (x + y) + (x + 1) as a, (x + (x + 1)) as b      *     from t(x, y)</code>      *<li><code>select (x + y) + (x + 1) as a, (x + x) as b from t(x, y)      *     where ((x + y)> 1) and ((x + y)> 1)</code>      *</ul>      */
specifier|private
name|RexProgramBuilder
name|createProg
parameter_list|(
name|int
name|variant
parameter_list|)
block|{
assert|assert
name|variant
operator|==
literal|0
operator|||
name|variant
operator|==
literal|1
operator|||
name|variant
operator|==
literal|2
assert|;
name|List
argument_list|<
name|RelDataType
argument_list|>
name|types
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
argument_list|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"x"
argument_list|,
literal|"y"
argument_list|)
decl_stmt|;
name|RelDataType
name|inputRowType
init|=
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|types
argument_list|,
name|names
argument_list|)
decl_stmt|;
specifier|final
name|RexProgramBuilder
name|builder
init|=
operator|new
name|RexProgramBuilder
argument_list|(
name|inputRowType
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
comment|// $t0 = x
comment|// $t1 = y
comment|// $t2 = $t0 + 1 (i.e. x + 1)
specifier|final
name|RexNode
name|i0
init|=
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|types
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RexLiteral
name|c1
init|=
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
decl_stmt|;
name|RexLocalRef
name|t2
init|=
name|builder
operator|.
name|addExpr
argument_list|(
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|plusOperator
argument_list|,
name|i0
argument_list|,
name|c1
argument_list|)
argument_list|)
decl_stmt|;
comment|// $t3 = 77 (not used)
specifier|final
name|RexLiteral
name|c77
init|=
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|77
argument_list|)
argument_list|)
decl_stmt|;
name|RexLocalRef
name|t3
init|=
name|builder
operator|.
name|addExpr
argument_list|(
name|c77
argument_list|)
decl_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|t3
argument_list|)
expr_stmt|;
comment|// $t4 = $t0 + $t1 (i.e. x + y)
specifier|final
name|RexNode
name|i1
init|=
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|types
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|RexLocalRef
name|t4
init|=
name|builder
operator|.
name|addExpr
argument_list|(
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|plusOperator
argument_list|,
name|i0
argument_list|,
name|i1
argument_list|)
argument_list|)
decl_stmt|;
name|RexLocalRef
name|t5
decl_stmt|;
switch|switch
condition|(
name|variant
condition|)
block|{
case|case
literal|0
case|:
case|case
literal|2
case|:
comment|// $t5 = $t0 + $t0 (i.e. x + x)
name|t5
operator|=
name|builder
operator|.
name|addExpr
argument_list|(
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|plusOperator
argument_list|,
name|i0
argument_list|,
name|i0
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
comment|// $tx = $t0 + 1
name|RexLocalRef
name|tx
init|=
name|builder
operator|.
name|addExpr
argument_list|(
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|plusOperator
argument_list|,
name|i0
argument_list|,
name|c1
argument_list|)
argument_list|)
decl_stmt|;
comment|// $t5 = $t0 + $tx (i.e. x + (x + 1))
name|t5
operator|=
name|builder
operator|.
name|addExpr
argument_list|(
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|plusOperator
argument_list|,
name|i0
argument_list|,
name|tx
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
literal|"unexpected variant "
operator|+
name|variant
argument_list|)
throw|;
block|}
comment|// $t6 = $t4 + $t2 (i.e. (x + y) + (x + 1))
name|RexLocalRef
name|t6
init|=
name|builder
operator|.
name|addExpr
argument_list|(
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|plusOperator
argument_list|,
name|t4
argument_list|,
name|t2
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addProject
argument_list|(
name|t6
operator|.
name|getIndex
argument_list|()
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addProject
argument_list|(
name|t5
operator|.
name|getIndex
argument_list|()
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
if|if
condition|(
name|variant
operator|==
literal|2
condition|)
block|{
comment|// $t7 = $t4> $i0 (i.e. (x + y)> 0)
name|RexLocalRef
name|t7
init|=
name|builder
operator|.
name|addExpr
argument_list|(
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|greaterThanOperator
argument_list|,
name|t4
argument_list|,
name|i0
argument_list|)
argument_list|)
decl_stmt|;
comment|// $t8 = $t7 AND $t7
name|RexLocalRef
name|t8
init|=
name|builder
operator|.
name|addExpr
argument_list|(
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|andOperator
argument_list|,
name|t7
argument_list|,
name|t7
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addCondition
argument_list|(
name|t8
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addCondition
argument_list|(
name|t7
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

begin_comment
comment|// End RexProgramTest.java
end_comment

end_unit

