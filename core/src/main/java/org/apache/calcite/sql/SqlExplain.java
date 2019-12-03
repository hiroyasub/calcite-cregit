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
name|util
operator|.
name|ImmutableNullableList
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

begin_comment
comment|/**  * A<code>SqlExplain</code> is a node of a parse tree which represents an  * EXPLAIN PLAN statement.  */
end_comment

begin_class
specifier|public
class|class
name|SqlExplain
extends|extends
name|SqlCall
block|{
specifier|public
specifier|static
specifier|final
name|SqlSpecialOperator
name|OPERATOR
init|=
operator|new
name|SqlSpecialOperator
argument_list|(
literal|"EXPLAIN"
argument_list|,
name|SqlKind
operator|.
name|EXPLAIN
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SqlCall
name|createCall
parameter_list|(
name|SqlLiteral
name|functionQualifier
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
modifier|...
name|operands
parameter_list|)
block|{
return|return
operator|new
name|SqlExplain
argument_list|(
name|pos
argument_list|,
name|operands
index|[
literal|0
index|]
argument_list|,
operator|(
name|SqlLiteral
operator|)
name|operands
index|[
literal|1
index|]
argument_list|,
operator|(
name|SqlLiteral
operator|)
name|operands
index|[
literal|2
index|]
argument_list|,
operator|(
name|SqlLiteral
operator|)
name|operands
index|[
literal|3
index|]
argument_list|,
literal|0
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|//~ Enums ------------------------------------------------------------------
comment|/**    * The level of abstraction with which to display the plan.    */
specifier|public
enum|enum
name|Depth
block|{
name|TYPE
block|,
name|LOGICAL
block|,
name|PHYSICAL
block|;
comment|/**      * Creates a parse-tree node representing an occurrence of this symbol      * at a particular position in the parsed text.      */
specifier|public
name|SqlLiteral
name|symbol
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
name|SqlLiteral
operator|.
name|createSymbol
argument_list|(
name|this
argument_list|,
name|pos
argument_list|)
return|;
block|}
block|}
comment|//~ Instance fields --------------------------------------------------------
name|SqlNode
name|explicandum
decl_stmt|;
name|SqlLiteral
name|detailLevel
decl_stmt|;
name|SqlLiteral
name|depth
decl_stmt|;
name|SqlLiteral
name|format
decl_stmt|;
specifier|private
specifier|final
name|int
name|dynamicParameterCount
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlExplain
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
name|explicandum
parameter_list|,
name|SqlLiteral
name|detailLevel
parameter_list|,
name|SqlLiteral
name|depth
parameter_list|,
name|SqlLiteral
name|format
parameter_list|,
name|int
name|dynamicParameterCount
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|explicandum
operator|=
name|explicandum
expr_stmt|;
name|this
operator|.
name|detailLevel
operator|=
name|detailLevel
expr_stmt|;
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
name|this
operator|.
name|dynamicParameterCount
operator|=
name|dynamicParameterCount
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|SqlKind
operator|.
name|EXPLAIN
return|;
block|}
specifier|public
name|SqlOperator
name|getOperator
parameter_list|()
block|{
return|return
name|OPERATOR
return|;
block|}
specifier|public
name|List
argument_list|<
name|SqlNode
argument_list|>
name|getOperandList
parameter_list|()
block|{
return|return
name|ImmutableNullableList
operator|.
name|of
argument_list|(
name|explicandum
argument_list|,
name|detailLevel
argument_list|,
name|depth
argument_list|,
name|format
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOperand
parameter_list|(
name|int
name|i
parameter_list|,
name|SqlNode
name|operand
parameter_list|)
block|{
switch|switch
condition|(
name|i
condition|)
block|{
case|case
literal|0
case|:
name|explicandum
operator|=
name|operand
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|detailLevel
operator|=
operator|(
name|SqlLiteral
operator|)
name|operand
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|depth
operator|=
operator|(
name|SqlLiteral
operator|)
name|operand
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|format
operator|=
operator|(
name|SqlLiteral
operator|)
name|operand
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|i
argument_list|)
throw|;
block|}
block|}
comment|/**    * @return the underlying SQL statement to be explained    */
specifier|public
name|SqlNode
name|getExplicandum
parameter_list|()
block|{
return|return
name|explicandum
return|;
block|}
comment|/**    * @return detail level to be generated    */
specifier|public
name|SqlExplainLevel
name|getDetailLevel
parameter_list|()
block|{
return|return
name|detailLevel
operator|.
name|symbolValue
argument_list|(
name|SqlExplainLevel
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**    * Returns the level of abstraction at which this plan should be displayed.    */
specifier|public
name|Depth
name|getDepth
parameter_list|()
block|{
return|return
name|depth
operator|.
name|symbolValue
argument_list|(
name|Depth
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**    * @return the number of dynamic parameters in the statement    */
specifier|public
name|int
name|getDynamicParamCount
parameter_list|()
block|{
return|return
name|dynamicParameterCount
return|;
block|}
comment|/**    * @return whether physical plan implementation should be returned    */
specifier|public
name|boolean
name|withImplementation
parameter_list|()
block|{
return|return
name|getDepth
argument_list|()
operator|==
name|Depth
operator|.
name|PHYSICAL
return|;
block|}
comment|/**    * @return whether type should be returned    */
specifier|public
name|boolean
name|withType
parameter_list|()
block|{
return|return
name|getDepth
argument_list|()
operator|==
name|Depth
operator|.
name|TYPE
return|;
block|}
comment|/**    * Returns the desired output format.    */
specifier|public
name|SqlExplainFormat
name|getFormat
parameter_list|()
block|{
return|return
name|format
operator|.
name|symbolValue
argument_list|(
name|SqlExplainFormat
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**    * Returns whether result is to be in XML format.    *    * @deprecated Use {@link #getFormat()}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|boolean
name|isXml
parameter_list|()
block|{
return|return
name|getFormat
argument_list|()
operator|==
name|SqlExplainFormat
operator|.
name|XML
return|;
block|}
comment|/**    * Returns whether result is to be in JSON format.    */
specifier|public
name|boolean
name|isJson
parameter_list|()
block|{
return|return
name|getFormat
argument_list|()
operator|==
name|SqlExplainFormat
operator|.
name|JSON
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"EXPLAIN PLAN"
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|getDetailLevel
argument_list|()
condition|)
block|{
case|case
name|NO_ATTRIBUTES
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"EXCLUDING ATTRIBUTES"
argument_list|)
expr_stmt|;
break|break;
case|case
name|EXPPLAN_ATTRIBUTES
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"INCLUDING ATTRIBUTES"
argument_list|)
expr_stmt|;
break|break;
case|case
name|ALL_ATTRIBUTES
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"INCLUDING ALL ATTRIBUTES"
argument_list|)
expr_stmt|;
break|break;
block|}
switch|switch
condition|(
name|getDepth
argument_list|()
condition|)
block|{
case|case
name|TYPE
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"WITH TYPE"
argument_list|)
expr_stmt|;
break|break;
case|case
name|LOGICAL
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"WITHOUT IMPLEMENTATION"
argument_list|)
expr_stmt|;
break|break;
case|case
name|PHYSICAL
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"WITH IMPLEMENTATION"
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
switch|switch
condition|(
name|getFormat
argument_list|()
condition|)
block|{
case|case
name|XML
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"AS XML"
argument_list|)
expr_stmt|;
break|break;
case|case
name|JSON
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"AS JSON"
argument_list|)
expr_stmt|;
break|break;
default|default:
block|}
name|writer
operator|.
name|keyword
argument_list|(
literal|"FOR"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
name|explicandum
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|getOperator
argument_list|()
operator|.
name|getLeftPrec
argument_list|()
argument_list|,
name|getOperator
argument_list|()
operator|.
name|getRightPrec
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

