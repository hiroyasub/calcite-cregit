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
name|sql
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|resource
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
name|*
import|;
end_import

begin_comment
comment|/**  * SQL window specifcation.  *  *<p>For example, the query  *  *<blockquote>  *<pre>SELECT sum(a) OVER (w ROWS 3 PRECEDING)  * FROM t  * WINDOW w AS (PARTITION BY x, y ORDER BY z),  *     w1 AS (w ROWS 5 PRECEDING UNBOUNDED FOLLOWING)</pre>  *</blockquote>  *  * declares windows w and w1, and uses a window in an OVER clause. It thus  * contains 3 {@link SqlWindow} objects.</p>  */
end_comment

begin_class
specifier|public
class|class
name|SqlWindow
extends|extends
name|SqlCall
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**    * Ordinal of the operand which holds the name of the window being declared.    */
specifier|public
specifier|static
specifier|final
name|int
name|DeclName_OPERAND
init|=
literal|0
decl_stmt|;
comment|/**    * Ordinal of operand which holds the name of the window being referenced,    * or null.    */
specifier|public
specifier|static
specifier|final
name|int
name|RefName_OPERAND
init|=
literal|1
decl_stmt|;
comment|/**    * Ordinal of the operand which holds the list of partitioning columns.    */
specifier|public
specifier|static
specifier|final
name|int
name|PartitionList_OPERAND
init|=
literal|2
decl_stmt|;
comment|/**    * Ordinal of the operand which holds the list of ordering columns.    */
specifier|public
specifier|static
specifier|final
name|int
name|OrderList_OPERAND
init|=
literal|3
decl_stmt|;
comment|/**    * Ordinal of the operand which declares whether it is a physical (rows) or    * logical (values) range.    */
specifier|public
specifier|static
specifier|final
name|int
name|IsRows_OPERAND
init|=
literal|4
decl_stmt|;
comment|/**    * Ordinal of the operand which holds the lower bound of the window.    */
specifier|public
specifier|static
specifier|final
name|int
name|LowerBound_OPERAND
init|=
literal|5
decl_stmt|;
comment|/**    * Ordinal of the operand which holds the upper bound of the window.    */
specifier|public
specifier|static
specifier|final
name|int
name|UpperBound_OPERAND
init|=
literal|6
decl_stmt|;
comment|/**    * Ordinal of the operand which declares whether to allow partial results.    * It may be null.    */
specifier|public
specifier|static
specifier|final
name|int
name|AllowPartial_OPERAND
init|=
literal|7
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|OperandCount
init|=
literal|8
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|SqlCall
name|windowCall
init|=
literal|null
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a window.    *    * @pre operands[DeclName_OPERAND] == null ||    * operands[DeclName_OPERAND].isSimple()    * @pre operands[OrderList_OPERAND] != null    * @pre operands[PartitionList_OPERAND] != null    */
specifier|public
name|SqlWindow
parameter_list|(
name|SqlWindowOperator
name|operator
parameter_list|,
name|SqlNode
index|[]
name|operands
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|operator
argument_list|,
name|operands
argument_list|,
name|pos
argument_list|)
expr_stmt|;
assert|assert
name|operands
operator|.
name|length
operator|==
name|OperandCount
assert|;
specifier|final
name|SqlIdentifier
name|declId
init|=
operator|(
name|SqlIdentifier
operator|)
name|operands
index|[
name|DeclName_OPERAND
index|]
decl_stmt|;
assert|assert
operator|(
name|declId
operator|==
literal|null
operator|)
operator|||
name|declId
operator|.
name|isSimple
argument_list|()
operator|:
name|declId
assert|;
assert|assert
name|operands
index|[
name|PartitionList_OPERAND
index|]
operator|!=
literal|null
assert|;
assert|assert
name|operands
index|[
name|OrderList_OPERAND
index|]
operator|!=
literal|null
assert|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
name|SqlIdentifier
name|declName
init|=
operator|(
name|SqlIdentifier
operator|)
name|operands
index|[
name|DeclName_OPERAND
index|]
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|declName
condition|)
block|{
name|declName
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"AS"
argument_list|)
expr_stmt|;
block|}
comment|// Override, so we don't print extra parentheses.
name|getOperator
argument_list|()
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|operands
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlIdentifier
name|getDeclName
parameter_list|()
block|{
return|return
operator|(
name|SqlIdentifier
operator|)
name|operands
index|[
name|DeclName_OPERAND
index|]
return|;
block|}
specifier|public
name|void
name|setDeclName
parameter_list|(
name|SqlIdentifier
name|name
parameter_list|)
block|{
name|Util
operator|.
name|pre
argument_list|(
name|name
operator|.
name|isSimple
argument_list|()
argument_list|,
literal|"name.isSimple()"
argument_list|)
expr_stmt|;
name|operands
index|[
name|DeclName_OPERAND
index|]
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|SqlNode
name|getLowerBound
parameter_list|()
block|{
return|return
name|operands
index|[
name|LowerBound_OPERAND
index|]
return|;
block|}
specifier|public
name|void
name|setLowerBound
parameter_list|(
name|SqlNode
name|bound
parameter_list|)
block|{
name|operands
index|[
name|LowerBound_OPERAND
index|]
operator|=
name|bound
expr_stmt|;
block|}
specifier|public
name|SqlNode
name|getUpperBound
parameter_list|()
block|{
return|return
name|operands
index|[
name|UpperBound_OPERAND
index|]
return|;
block|}
specifier|public
name|void
name|setUpperBound
parameter_list|(
name|SqlNode
name|bound
parameter_list|)
block|{
name|operands
index|[
name|UpperBound_OPERAND
index|]
operator|=
name|bound
expr_stmt|;
block|}
specifier|public
name|boolean
name|isRows
parameter_list|()
block|{
return|return
name|SqlLiteral
operator|.
name|booleanValue
argument_list|(
name|operands
index|[
name|IsRows_OPERAND
index|]
argument_list|)
return|;
block|}
specifier|public
name|SqlNodeList
name|getOrderList
parameter_list|()
block|{
return|return
operator|(
name|SqlNodeList
operator|)
name|operands
index|[
name|OrderList_OPERAND
index|]
return|;
block|}
specifier|public
name|SqlNodeList
name|getPartitionList
parameter_list|()
block|{
return|return
operator|(
name|SqlNodeList
operator|)
name|operands
index|[
name|PartitionList_OPERAND
index|]
return|;
block|}
specifier|public
name|SqlIdentifier
name|getRefName
parameter_list|()
block|{
return|return
operator|(
name|SqlIdentifier
operator|)
name|operands
index|[
name|RefName_OPERAND
index|]
return|;
block|}
specifier|public
name|void
name|setWindowCall
parameter_list|(
name|SqlCall
name|windowCall
parameter_list|)
block|{
name|this
operator|.
name|windowCall
operator|=
name|windowCall
expr_stmt|;
block|}
specifier|public
name|SqlCall
name|getWindowCall
parameter_list|()
block|{
return|return
name|windowCall
return|;
block|}
comment|/**    * Creates a new window by combining this one with another.    *    *<p>For example,    *    *<pre>WINDOW (w PARTITION BY x ORDER BY y)    *   overlay    *   WINDOW w AS (PARTITION BY z)</pre>    *    * yields    *    *<pre>WINDOW (PARTITION BY z ORDER BY y)</pre>    *    *<p>Does not alter this or the other window.    *    * @return A new window    */
specifier|public
name|SqlWindow
name|overlay
parameter_list|(
name|SqlWindow
name|that
parameter_list|,
name|SqlValidator
name|validator
parameter_list|)
block|{
comment|// check 7.11 rule 10c
specifier|final
name|SqlNodeList
name|partitions
init|=
name|getPartitionList
argument_list|()
decl_stmt|;
if|if
condition|(
literal|0
operator|!=
name|partitions
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|partitions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|PartitionNotAllowed
operator|.
name|ex
argument_list|()
argument_list|)
throw|;
block|}
comment|// 7.11 rule 10d
specifier|final
name|SqlNodeList
name|baseOrder
init|=
name|getOrderList
argument_list|()
decl_stmt|;
specifier|final
name|SqlNodeList
name|refOrder
init|=
name|that
operator|.
name|getOrderList
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
literal|0
operator|!=
name|baseOrder
operator|.
name|size
argument_list|()
operator|)
operator|&&
operator|(
literal|0
operator|!=
name|refOrder
operator|.
name|size
argument_list|()
operator|)
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|baseOrder
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|OrderByOverlap
operator|.
name|ex
argument_list|()
argument_list|)
throw|;
block|}
comment|// 711 rule 10e
specifier|final
name|SqlNode
name|lowerBound
init|=
name|that
operator|.
name|getLowerBound
argument_list|()
decl_stmt|,
name|upperBound
init|=
name|that
operator|.
name|getUpperBound
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
literal|null
operator|!=
name|lowerBound
operator|)
operator|||
operator|(
literal|null
operator|!=
name|upperBound
operator|)
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|that
operator|.
name|operands
index|[
name|IsRows_OPERAND
index|]
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|RefWindowWithFrame
operator|.
name|ex
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|SqlNode
index|[]
name|newOperands
init|=
operator|(
name|SqlNode
index|[]
operator|)
name|operands
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// Clear the reference window, because the reference is now resolved.
comment|// The overlaying window may have its own reference, of course.
name|newOperands
index|[
name|RefName_OPERAND
index|]
operator|=
literal|null
expr_stmt|;
comment|// Overlay other parameters.
name|setOperand
argument_list|(
name|newOperands
argument_list|,
name|that
operator|.
name|operands
argument_list|,
name|PartitionList_OPERAND
argument_list|,
name|validator
argument_list|)
expr_stmt|;
name|setOperand
argument_list|(
name|newOperands
argument_list|,
name|that
operator|.
name|operands
argument_list|,
name|OrderList_OPERAND
argument_list|,
name|validator
argument_list|)
expr_stmt|;
name|setOperand
argument_list|(
name|newOperands
argument_list|,
name|that
operator|.
name|operands
argument_list|,
name|LowerBound_OPERAND
argument_list|,
name|validator
argument_list|)
expr_stmt|;
name|setOperand
argument_list|(
name|newOperands
argument_list|,
name|that
operator|.
name|operands
argument_list|,
name|UpperBound_OPERAND
argument_list|,
name|validator
argument_list|)
expr_stmt|;
return|return
operator|new
name|SqlWindow
argument_list|(
operator|(
name|SqlWindowOperator
operator|)
name|getOperator
argument_list|()
argument_list|,
name|newOperands
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|setOperand
parameter_list|(
specifier|final
name|SqlNode
index|[]
name|destOperands
parameter_list|,
name|SqlNode
index|[]
name|srcOperands
parameter_list|,
name|int
name|i
parameter_list|,
name|SqlValidator
name|validator
parameter_list|)
block|{
specifier|final
name|SqlNode
name|thatOperand
init|=
name|srcOperands
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|thatOperand
operator|!=
literal|null
operator|)
operator|&&
operator|!
name|SqlNodeList
operator|.
name|isEmptyList
argument_list|(
name|thatOperand
argument_list|)
condition|)
block|{
specifier|final
name|SqlNode
name|clonedOperand
init|=
name|destOperands
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|clonedOperand
operator|==
literal|null
operator|)
operator|||
name|SqlNodeList
operator|.
name|isEmptyList
argument_list|(
name|clonedOperand
argument_list|)
condition|)
block|{
name|destOperands
index|[
name|i
index|]
operator|=
name|thatOperand
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|clonedOperand
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|CannotOverrideWindowAttribute
operator|.
name|ex
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Overridden method to specfically check only the right subtree of a window    * definition    *    * @param node The SqlWindow to compare to "this" window    * @param fail Whether to throw if not equal    * @return boolean true if all nodes in the subtree are equal    */
specifier|public
name|boolean
name|equalsDeep
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|boolean
name|fail
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|node
operator|instanceof
name|SqlWindow
operator|)
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
name|this
operator|+
literal|"!="
operator|+
name|node
assert|;
return|return
literal|false
return|;
block|}
name|SqlCall
name|that
init|=
operator|(
name|SqlCall
operator|)
name|node
decl_stmt|;
comment|// Compare operators by name, not identity, because they may not
comment|// have been resolved yet.
if|if
condition|(
operator|!
name|this
operator|.
name|getOperator
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|that
operator|.
name|getOperator
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
name|this
operator|+
literal|"!="
operator|+
name|node
assert|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|this
operator|.
name|operands
operator|.
name|length
operator|!=
name|that
operator|.
name|operands
operator|.
name|length
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
name|this
operator|+
literal|"!="
operator|+
name|node
assert|;
return|return
literal|false
return|;
block|}
comment|// This is the difference over super.equalsDeep.  It skips
comment|// operands[0] the declared name fo this window.  We only want
comment|// to check the window components.
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|this
operator|.
name|operands
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|SqlNode
operator|.
name|equalDeep
argument_list|(
name|this
operator|.
name|operands
index|[
name|i
index|]
argument_list|,
name|that
operator|.
name|operands
index|[
name|i
index|]
argument_list|,
name|fail
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|SqlWindowOperator
operator|.
name|OffsetRange
name|getOffsetAndRange
parameter_list|()
block|{
return|return
name|SqlWindowOperator
operator|.
name|getOffsetAndRange
argument_list|(
name|getLowerBound
argument_list|()
argument_list|,
name|getUpperBound
argument_list|()
argument_list|,
name|isRows
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns whether partial windows are allowed. If false, a partial window    * (for example, a window of size 1 hour which has only 45 minutes of data    * in it) will appear to windowed aggregate functions to be empty.    */
specifier|public
name|boolean
name|isAllowPartial
parameter_list|()
block|{
comment|// Default (and standard behavior) is to allow partial windows.
return|return
operator|(
name|operands
index|[
name|AllowPartial_OPERAND
index|]
operator|==
literal|null
operator|)
operator|||
name|SqlLiteral
operator|.
name|booleanValue
argument_list|(
name|operands
index|[
name|AllowPartial_OPERAND
index|]
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlWindow.java
end_comment

end_unit

