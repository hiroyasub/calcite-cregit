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
name|rex
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
name|RelOptPredicateList
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
name|SqlKind
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
name|hamcrest
operator|.
name|MatcherAssert
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
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertEquals
import|;
end_import

begin_class
class|class
name|RexProgramTestBase
extends|extends
name|RexProgramBuilderBase
block|{
specifier|protected
name|void
name|checkDigest
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|,
parameter_list|()
lambda|->
literal|"Digest of "
operator|+
name|node
operator|.
name|toStringRaw
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|checkRaw
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|node
operator|.
name|toStringRaw
argument_list|()
argument_list|,
parameter_list|()
lambda|->
literal|"Raw representation of node with digest "
operator|+
name|node
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|checkCnf
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"RexUtil.toCnf(rexBuilder, "
operator|+
name|node
operator|+
literal|")"
argument_list|,
name|RexUtil
operator|.
name|toCnf
argument_list|(
name|rexBuilder
argument_list|,
name|node
argument_list|)
operator|.
name|toStringRaw
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|checkThresholdCnf
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|int
name|threshold
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"RexUtil.toCnf(rexBuilder, threshold="
operator|+
name|threshold
operator|+
literal|" , "
operator|+
name|node
operator|+
literal|")"
argument_list|,
name|RexUtil
operator|.
name|toCnf
argument_list|(
name|rexBuilder
argument_list|,
name|threshold
argument_list|,
name|node
argument_list|)
operator|.
name|toStringRaw
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|checkPullFactorsUnchanged
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
name|checkPullFactors
argument_list|(
name|node
argument_list|,
name|node
operator|.
name|toStringRaw
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|checkPullFactors
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"RexUtil.pullFactors(rexBuilder, "
operator|+
name|node
operator|+
literal|")"
argument_list|,
name|RexUtil
operator|.
name|pullFactors
argument_list|(
name|rexBuilder
argument_list|,
name|node
argument_list|)
operator|.
name|toStringRaw
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Asserts that given node has expected string representation with account of node type    * @param message extra message that clarifies where the node came from    * @param expected expected string representation of the node    * @param node node to check    */
specifier|protected
name|void
name|assertNode
parameter_list|(
name|String
name|message
parameter_list|,
name|String
name|expected
parameter_list|,
name|RexNode
name|node
parameter_list|)
block|{
name|String
name|actual
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|CAST
argument_list|)
operator|||
name|node
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|NEW_SPECIFICATION
argument_list|)
condition|)
block|{
comment|// toString contains type (see RexCall.toString)
name|actual
operator|=
name|node
operator|.
name|toStringRaw
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|actual
operator|=
name|node
operator|+
literal|":"
operator|+
name|node
operator|.
name|getType
argument_list|()
operator|+
operator|(
name|node
operator|.
name|getType
argument_list|()
operator|.
name|isNullable
argument_list|()
condition|?
literal|""
else|:
literal|" NOT NULL"
operator|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
comment|/** Simplifies an expression and checks that the result is as expected. */
specifier|protected
name|void
name|checkSimplify
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
specifier|final
name|String
name|nodeString
init|=
name|node
operator|.
name|toStringRaw
argument_list|()
decl_stmt|;
name|checkSimplify3_
argument_list|(
name|node
argument_list|,
name|expected
argument_list|,
name|expected
argument_list|,
name|expected
argument_list|)
expr_stmt|;
if|if
condition|(
name|expected
operator|.
name|equals
argument_list|(
name|nodeString
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"expected == node.toStringRaw(); "
operator|+
literal|"use checkSimplifyUnchanged"
argument_list|)
throw|;
block|}
block|}
comment|/** Simplifies an expression and checks that the result is unchanged. */
specifier|protected
name|void
name|checkSimplifyUnchanged
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
specifier|final
name|String
name|expected
init|=
name|node
operator|.
name|toStringRaw
argument_list|()
decl_stmt|;
name|checkSimplify3_
argument_list|(
name|node
argument_list|,
name|expected
argument_list|,
name|expected
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Simplifies an expression and checks the result if unknowns remain    * unknown, or if unknown becomes false. If the result is the same, use    * {@link #checkSimplify(RexNode, String)}.    *    * @param node Expression to simplify    * @param expected Expected simplification    * @param expectedFalse Expected simplification, if unknown is to be treated    *     as false    */
specifier|protected
name|void
name|checkSimplify2
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|String
name|expected
parameter_list|,
name|String
name|expectedFalse
parameter_list|)
block|{
name|checkSimplify3_
argument_list|(
name|node
argument_list|,
name|expected
argument_list|,
name|expectedFalse
argument_list|,
name|expected
argument_list|)
expr_stmt|;
if|if
condition|(
name|expected
operator|.
name|equals
argument_list|(
name|expectedFalse
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"expected == expectedFalse; use checkSimplify"
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|void
name|checkSimplify3
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|String
name|expected
parameter_list|,
name|String
name|expectedFalse
parameter_list|,
name|String
name|expectedTrue
parameter_list|)
block|{
name|checkSimplify3_
argument_list|(
name|node
argument_list|,
name|expected
argument_list|,
name|expectedFalse
argument_list|,
name|expectedTrue
argument_list|)
expr_stmt|;
if|if
condition|(
name|expected
operator|.
name|equals
argument_list|(
name|expectedFalse
argument_list|)
operator|&&
name|expected
operator|.
name|equals
argument_list|(
name|expectedTrue
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"expected == expectedFalse == expectedTrue; "
operator|+
literal|"use checkSimplify"
argument_list|)
throw|;
block|}
if|if
condition|(
name|expected
operator|.
name|equals
argument_list|(
name|expectedTrue
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"expected == expectedTrue; use checkSimplify2"
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|void
name|checkSimplify3_
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|String
name|expected
parameter_list|,
name|String
name|expectedFalse
parameter_list|,
name|String
name|expectedTrue
parameter_list|)
block|{
specifier|final
name|RexNode
name|simplified
init|=
name|simplify
operator|.
name|simplifyUnknownAs
argument_list|(
name|node
argument_list|,
name|RexUnknownAs
operator|.
name|UNKNOWN
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"simplify(unknown as unknown): "
operator|+
name|node
argument_list|,
name|simplified
operator|.
name|toStringRaw
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|BOOLEAN
condition|)
block|{
specifier|final
name|RexNode
name|simplified2
init|=
name|simplify
operator|.
name|simplifyUnknownAs
argument_list|(
name|node
argument_list|,
name|RexUnknownAs
operator|.
name|FALSE
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"simplify(unknown as false): "
operator|+
name|node
argument_list|,
name|simplified2
operator|.
name|toStringRaw
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedFalse
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RexNode
name|simplified3
init|=
name|simplify
operator|.
name|simplifyUnknownAs
argument_list|(
name|node
argument_list|,
name|RexUnknownAs
operator|.
name|TRUE
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"simplify(unknown as true): "
operator|+
name|node
argument_list|,
name|simplified3
operator|.
name|toStringRaw
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedTrue
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
literal|"node type is not BOOLEAN, so<<expectedFalse>> should match<<expected>>"
argument_list|,
name|expectedFalse
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"node type is not BOOLEAN, so<<expectedTrue>> should match<<expected>>"
argument_list|,
name|expectedTrue
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|checkSimplifyFilter
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
specifier|final
name|RexNode
name|simplified
init|=
name|this
operator|.
name|simplify
operator|.
name|simplifyUnknownAs
argument_list|(
name|node
argument_list|,
name|RexUnknownAs
operator|.
name|FALSE
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|simplified
operator|.
name|toStringRaw
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|checkSimplifyFilter
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|RelOptPredicateList
name|predicates
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
specifier|final
name|RexNode
name|simplified
init|=
name|simplify
operator|.
name|withPredicates
argument_list|(
name|predicates
argument_list|)
operator|.
name|simplifyUnknownAs
argument_list|(
name|node
argument_list|,
name|RexUnknownAs
operator|.
name|FALSE
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|simplified
operator|.
name|toStringRaw
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Checks that {@link RexNode#isAlwaysTrue()},    * {@link RexNode#isAlwaysTrue()} and {@link RexSimplify} agree that    * an expression reduces to true or false. */
specifier|protected
name|void
name|checkIs
parameter_list|(
name|RexNode
name|e
parameter_list|,
name|boolean
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"isAlwaysTrue() of expression: "
operator|+
name|e
operator|.
name|toStringRaw
argument_list|()
argument_list|,
name|e
operator|.
name|isAlwaysTrue
argument_list|()
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"isAlwaysFalse() of expression: "
operator|+
name|e
operator|.
name|toStringRaw
argument_list|()
argument_list|,
name|e
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|,
name|is
argument_list|(
operator|!
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"Simplification is not using isAlwaysX informations"
argument_list|,
name|simplify
argument_list|(
name|e
argument_list|)
operator|.
name|toStringRaw
argument_list|()
argument_list|,
name|is
argument_list|(
name|expected
condition|?
literal|"true"
else|:
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Comparable
name|eval
parameter_list|(
name|RexNode
name|e
parameter_list|)
block|{
return|return
name|RexInterpreter
operator|.
name|evaluate
argument_list|(
name|e
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|simplify
parameter_list|(
name|RexNode
name|e
parameter_list|)
block|{
specifier|final
name|RexSimplify
name|simplify
init|=
operator|new
name|RexSimplify
argument_list|(
name|rexBuilder
argument_list|,
name|RelOptPredicateList
operator|.
name|EMPTY
argument_list|,
name|RexUtil
operator|.
name|EXECUTOR
argument_list|)
operator|.
name|withParanoid
argument_list|(
literal|true
argument_list|)
decl_stmt|;
return|return
name|simplify
operator|.
name|simplifyUnknownAs
argument_list|(
name|e
argument_list|,
name|RexUnknownAs
operator|.
name|UNKNOWN
argument_list|)
return|;
block|}
block|}
end_class

end_unit

