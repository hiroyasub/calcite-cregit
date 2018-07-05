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
name|type
operator|.
name|SqlOperandTypeInference
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
name|util
operator|.
name|PrecedenceClimbingParser
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
comment|/**  * Generic operator for nodes with special syntax.  */
end_comment

begin_class
specifier|public
class|class
name|SqlSpecialOperator
extends|extends
name|SqlOperator
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlSpecialOperator
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlSpecialOperator
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|int
name|prec
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
name|prec
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlSpecialOperator
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|int
name|prec
parameter_list|,
name|boolean
name|leftAssoc
parameter_list|,
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
name|prec
argument_list|,
name|leftAssoc
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandTypeChecker
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlSyntax
name|getSyntax
parameter_list|()
block|{
return|return
name|SqlSyntax
operator|.
name|SPECIAL
return|;
block|}
comment|/**    * Reduces a list of operators and arguments according to the rules of    * precedence and associativity. Returns the ordinal of the node which    * replaced the expression.    *    *<p>The default implementation throws    * {@link UnsupportedOperationException}.    *    * @param ordinal indicating the ordinal of the current operator in the list    *                on which a possible reduction can be made    * @param list    List of alternating    *     {@link org.apache.calcite.sql.parser.SqlParserUtil.ToTreeListItem} and    *     {@link SqlNode}    * @return ordinal of the node which replaced the expression    */
specifier|public
name|ReduceResult
name|reduceExpr
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|TokenSequence
name|list
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|needToImplement
argument_list|(
name|this
argument_list|)
throw|;
block|}
comment|/** List of tokens: the input to a parser. Every token is either an operator    * ({@link SqlOperator}) or an expression ({@link SqlNode}), and every token    * has a position. */
specifier|public
interface|interface
name|TokenSequence
block|{
name|int
name|size
parameter_list|()
function_decl|;
name|SqlOperator
name|op
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
name|SqlParserPos
name|pos
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
name|boolean
name|isOp
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
name|SqlNode
name|node
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
name|void
name|replaceSublist
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|SqlNode
name|e
parameter_list|)
function_decl|;
comment|/** Creates a parser whose token sequence is a copy of a subset of this      * token sequence. */
name|PrecedenceClimbingParser
name|parser
parameter_list|(
name|int
name|start
parameter_list|,
name|Predicate
argument_list|<
name|PrecedenceClimbingParser
operator|.
name|Token
argument_list|>
name|predicate
parameter_list|)
function_decl|;
block|}
comment|/** Result of applying    * {@link org.apache.calcite.util.PrecedenceClimbingParser.Special#apply}.    * Tells the caller which range of tokens to replace, and with what. */
specifier|public
class|class
name|ReduceResult
block|{
specifier|public
specifier|final
name|int
name|startOrdinal
decl_stmt|;
specifier|public
specifier|final
name|int
name|endOrdinal
decl_stmt|;
specifier|public
specifier|final
name|SqlNode
name|node
decl_stmt|;
specifier|public
name|ReduceResult
parameter_list|(
name|int
name|startOrdinal
parameter_list|,
name|int
name|endOrdinal
parameter_list|,
name|SqlNode
name|node
parameter_list|)
block|{
name|this
operator|.
name|startOrdinal
operator|=
name|startOrdinal
expr_stmt|;
name|this
operator|.
name|endOrdinal
operator|=
name|endOrdinal
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlSpecialOperator.java
end_comment

end_unit

