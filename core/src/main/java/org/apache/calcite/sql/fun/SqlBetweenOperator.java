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
name|sql
operator|.
name|fun
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
name|RelDataTypeComparability
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
name|ExplicitOperatorBinding
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
name|SqlCall
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
name|SqlInfixOperator
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
name|SqlOperatorBinding
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
name|SqlUtil
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
name|SqlWriter
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
name|parser
operator|.
name|SqlParserUtil
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
name|ComparableOperandTypeChecker
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
name|SqlOperandTypeChecker
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
name|SqlBasicVisitor
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
name|Litmus
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
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * Defines the BETWEEN operator.  *  *<p>Syntax:  *  *<blockquote><code>X [NOT] BETWEEN [ASYMMETRIC | SYMMETRIC] Y AND  * Z</code></blockquote>  *  *<p>If the asymmetric/symmetric keywords are left out ASYMMETRIC is default.  *  *<p>This operator is always expanded (into something like<code>Y&lt;= X AND  * X&lt;= Z</code>) before being converted into Rex nodes.  */
end_comment

begin_class
specifier|public
class|class
name|SqlBetweenOperator
extends|extends
name|SqlInfixOperator
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|BETWEEN_NAMES
init|=
block|{
literal|"BETWEEN"
block|,
literal|"AND"
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|NOT_BETWEEN_NAMES
init|=
block|{
literal|"NOT BETWEEN"
block|,
literal|"AND"
block|}
decl_stmt|;
comment|/**    * Ordinal of the 'value' operand.    */
specifier|public
specifier|static
specifier|final
name|int
name|VALUE_OPERAND
init|=
literal|0
decl_stmt|;
comment|/**    * Ordinal of the 'lower' operand.    */
specifier|public
specifier|static
specifier|final
name|int
name|LOWER_OPERAND
init|=
literal|1
decl_stmt|;
comment|/**    * Ordinal of the 'upper' operand.    */
specifier|public
specifier|static
specifier|final
name|int
name|UPPER_OPERAND
init|=
literal|2
decl_stmt|;
comment|/**    * Custom operand-type checking strategy.    */
specifier|private
specifier|static
specifier|final
name|SqlOperandTypeChecker
name|OTC_CUSTOM
init|=
operator|new
name|ComparableOperandTypeChecker
argument_list|(
literal|3
argument_list|,
name|RelDataTypeComparability
operator|.
name|ALL
argument_list|,
name|SqlOperandTypeChecker
operator|.
name|Consistency
operator|.
name|COMPARE
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlWriter
operator|.
name|FrameType
name|FRAME_TYPE
init|=
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|create
argument_list|(
literal|"BETWEEN"
argument_list|)
decl_stmt|;
comment|//~ Enums ------------------------------------------------------------------
comment|/**    * Defines the "SYMMETRIC" and "ASYMMETRIC" keywords.    */
specifier|public
enum|enum
name|Flag
block|{
name|ASYMMETRIC
block|,
name|SYMMETRIC
block|}
comment|//~ Instance fields --------------------------------------------------------
specifier|public
specifier|final
name|Flag
name|flag
decl_stmt|;
comment|/**    * If true the call represents 'NOT BETWEEN'.    */
specifier|private
specifier|final
name|boolean
name|negated
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlBetweenOperator
parameter_list|(
name|Flag
name|flag
parameter_list|,
name|boolean
name|negated
parameter_list|)
block|{
name|super
argument_list|(
name|negated
condition|?
name|NOT_BETWEEN_NAMES
else|:
name|BETWEEN_NAMES
argument_list|,
name|SqlKind
operator|.
name|BETWEEN
argument_list|,
literal|32
argument_list|,
literal|null
argument_list|,
name|InferTypes
operator|.
name|FIRST_KNOWN
argument_list|,
name|OTC_CUSTOM
argument_list|)
expr_stmt|;
name|this
operator|.
name|flag
operator|=
name|flag
expr_stmt|;
name|this
operator|.
name|negated
operator|=
name|negated
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|boolean
name|validRexOperands
parameter_list|(
name|int
name|count
parameter_list|,
name|Litmus
name|litmus
parameter_list|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"not a rex operator"
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isNegated
parameter_list|()
block|{
return|return
name|negated
return|;
block|}
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
name|ExplicitOperatorBinding
name|newOpBinding
init|=
operator|new
name|ExplicitOperatorBinding
argument_list|(
name|opBinding
argument_list|,
name|opBinding
operator|.
name|collectOperandTypes
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ReturnTypes
operator|.
name|BOOLEAN_NULLABLE
operator|.
name|inferReturnType
argument_list|(
name|newOpBinding
argument_list|)
return|;
block|}
specifier|public
name|String
name|getSignatureTemplate
parameter_list|(
specifier|final
name|int
name|operandsCount
parameter_list|)
block|{
name|Util
operator|.
name|discard
argument_list|(
name|operandsCount
argument_list|)
expr_stmt|;
return|return
literal|"{1} {0} {2} AND {3}"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|super
operator|.
name|getName
argument_list|()
operator|+
literal|" "
operator|+
name|flag
operator|.
name|name
argument_list|()
return|;
block|}
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
specifier|final
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startList
argument_list|(
name|FRAME_TYPE
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|call
operator|.
name|operand
argument_list|(
name|VALUE_OPERAND
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|getLeftPrec
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
name|super
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
name|flag
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
comment|// If the expression for the lower bound contains a call to an AND
comment|// operator, we need to wrap the expression in parentheses to prevent
comment|// the AND from associating with BETWEEN. For example, we should
comment|// unparse
comment|//    a BETWEEN b OR (c AND d) OR e AND f
comment|// as
comment|//    a BETWEEN (b OR c AND d) OR e) AND f
comment|// If it were unparsed as
comment|//    a BETWEEN b OR c AND d OR e AND f
comment|// then it would be interpreted as
comment|//    (a BETWEEN (b OR c) AND d) OR (e AND f)
comment|// which would be wrong.
specifier|final
name|SqlNode
name|lower
init|=
name|call
operator|.
name|operand
argument_list|(
name|LOWER_OPERAND
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|upper
init|=
name|call
operator|.
name|operand
argument_list|(
name|UPPER_OPERAND
argument_list|)
decl_stmt|;
name|int
name|lowerPrec
init|=
operator|new
name|AndFinder
argument_list|()
operator|.
name|containsAnd
argument_list|(
name|lower
argument_list|)
condition|?
literal|100
else|:
literal|0
decl_stmt|;
name|lower
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|lowerPrec
argument_list|,
name|lowerPrec
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"AND"
argument_list|)
expr_stmt|;
name|upper
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
name|getRightPrec
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ReduceResult
name|reduceExpr
parameter_list|(
name|int
name|opOrdinal
parameter_list|,
name|TokenSequence
name|list
parameter_list|)
block|{
name|SqlOperator
name|op
init|=
name|list
operator|.
name|op
argument_list|(
name|opOrdinal
argument_list|)
decl_stmt|;
assert|assert
name|op
operator|==
name|this
assert|;
comment|// Break the expression up into expressions. For example, a simple
comment|// expression breaks down as follows:
comment|//
comment|//            opOrdinal   endExp1
comment|//            |           |
comment|//     a + b BETWEEN c + d AND e + f
comment|//    |_____|       |_____|   |_____|
comment|//     exp0          exp1      exp2
comment|// Create the expression between 'BETWEEN' and 'AND'.
name|SqlNode
name|exp1
init|=
name|SqlParserUtil
operator|.
name|toTreeEx
argument_list|(
name|list
argument_list|,
name|opOrdinal
operator|+
literal|1
argument_list|,
literal|0
argument_list|,
name|SqlKind
operator|.
name|AND
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|opOrdinal
operator|+
literal|2
operator|)
operator|>=
name|list
operator|.
name|size
argument_list|()
condition|)
block|{
name|SqlParserPos
name|lastPos
init|=
name|list
operator|.
name|pos
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|line
init|=
name|lastPos
operator|.
name|getEndLineNum
argument_list|()
decl_stmt|;
specifier|final
name|int
name|col
init|=
name|lastPos
operator|.
name|getEndColumnNum
argument_list|()
operator|+
literal|1
decl_stmt|;
name|SqlParserPos
name|errPos
init|=
operator|new
name|SqlParserPos
argument_list|(
name|line
argument_list|,
name|col
argument_list|,
name|line
argument_list|,
name|col
argument_list|)
decl_stmt|;
throw|throw
name|SqlUtil
operator|.
name|newContextException
argument_list|(
name|errPos
argument_list|,
name|RESOURCE
operator|.
name|betweenWithoutAnd
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|list
operator|.
name|isOp
argument_list|(
name|opOrdinal
operator|+
literal|2
argument_list|)
operator|||
name|list
operator|.
name|op
argument_list|(
name|opOrdinal
operator|+
literal|2
argument_list|)
operator|.
name|getKind
argument_list|()
operator|!=
name|SqlKind
operator|.
name|AND
condition|)
block|{
name|SqlParserPos
name|errPos
init|=
name|list
operator|.
name|pos
argument_list|(
name|opOrdinal
operator|+
literal|2
argument_list|)
decl_stmt|;
throw|throw
name|SqlUtil
operator|.
name|newContextException
argument_list|(
name|errPos
argument_list|,
name|RESOURCE
operator|.
name|betweenWithoutAnd
argument_list|()
argument_list|)
throw|;
block|}
comment|// Create the expression after 'AND', but stopping if we encounter an
comment|// operator of lower precedence.
comment|//
comment|// For example,
comment|//   a BETWEEN b AND c + d OR e
comment|// becomes
comment|//   (a BETWEEN b AND c + d) OR e
comment|// because OR has lower precedence than BETWEEN.
name|SqlNode
name|exp2
init|=
name|SqlParserUtil
operator|.
name|toTreeEx
argument_list|(
name|list
argument_list|,
name|opOrdinal
operator|+
literal|3
argument_list|,
name|getRightPrec
argument_list|()
argument_list|,
name|SqlKind
operator|.
name|OTHER
argument_list|)
decl_stmt|;
comment|// Create the call.
name|SqlNode
name|exp0
init|=
name|list
operator|.
name|node
argument_list|(
name|opOrdinal
operator|-
literal|1
argument_list|)
decl_stmt|;
name|SqlCall
name|newExp
init|=
name|createCall
argument_list|(
name|list
operator|.
name|pos
argument_list|(
name|opOrdinal
argument_list|)
argument_list|,
name|exp0
argument_list|,
name|exp1
argument_list|,
name|exp2
argument_list|)
decl_stmt|;
comment|// Replace all of the matched nodes with the single reduced node.
return|return
operator|new
name|ReduceResult
argument_list|(
name|opOrdinal
operator|-
literal|1
argument_list|,
name|opOrdinal
operator|+
literal|4
argument_list|,
name|newExp
argument_list|)
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Finds an AND operator in an expression.    */
specifier|private
specifier|static
class|class
name|AndFinder
extends|extends
name|SqlBasicVisitor
argument_list|<
name|Void
argument_list|>
block|{
specifier|public
name|Void
name|visit
parameter_list|(
name|SqlCall
name|call
parameter_list|)
block|{
specifier|final
name|SqlOperator
name|operator
init|=
name|call
operator|.
name|getOperator
argument_list|()
decl_stmt|;
if|if
condition|(
name|operator
operator|==
name|SqlStdOperatorTable
operator|.
name|AND
condition|)
block|{
throw|throw
name|Util
operator|.
name|FoundOne
operator|.
name|NULL
throw|;
block|}
return|return
name|super
operator|.
name|visit
argument_list|(
name|call
argument_list|)
return|;
block|}
name|boolean
name|containsAnd
parameter_list|(
name|SqlNode
name|node
parameter_list|)
block|{
try|try
block|{
name|node
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|Util
operator|.
name|FoundOne
name|e
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

