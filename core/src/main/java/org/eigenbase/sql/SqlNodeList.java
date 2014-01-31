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
name|sql
operator|.
name|fun
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
name|sql
operator|.
name|validate
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A<code>SqlNodeList</code> is a list of {@link SqlNode}s. It is also a {@link  * SqlNode}, so may appear in a parse tree.  */
end_comment

begin_class
specifier|public
class|class
name|SqlNodeList
extends|extends
name|SqlNode
implements|implements
name|Iterable
argument_list|<
name|SqlNode
argument_list|>
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**    * An immutable, empty SqlNodeList.    */
specifier|public
specifier|static
specifier|final
name|SqlNodeList
name|EMPTY
init|=
operator|new
name|SqlNodeList
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
block|{
specifier|public
name|void
name|add
parameter_list|(
name|SqlNode
name|node
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|list
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an empty<code>SqlNodeList</code>.    */
specifier|public
name|SqlNodeList
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|list
operator|=
operator|new
name|ArrayList
argument_list|<
name|SqlNode
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates a<code>SqlNodeList</code> containing the nodes in<code>    * list</code>. The list is copied, but the nodes in it are not.    */
specifier|public
name|SqlNodeList
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|list
operator|=
operator|new
name|ArrayList
argument_list|<
name|SqlNode
argument_list|>
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement Iterable<SqlNode>
specifier|public
name|Iterator
argument_list|<
name|SqlNode
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|list
operator|.
name|iterator
argument_list|()
return|;
block|}
specifier|public
name|List
argument_list|<
name|SqlNode
argument_list|>
name|getList
parameter_list|()
block|{
return|return
name|list
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|SqlNode
name|node
parameter_list|)
block|{
name|list
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlNode
name|clone
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
operator|new
name|SqlNodeList
argument_list|(
name|list
argument_list|,
name|pos
argument_list|)
return|;
block|}
specifier|public
name|SqlNode
name|get
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
name|list
operator|.
name|get
argument_list|(
name|n
argument_list|)
return|;
block|}
specifier|public
name|SqlNode
name|set
parameter_list|(
name|int
name|n
parameter_list|,
name|SqlNode
name|node
parameter_list|)
block|{
return|return
name|list
operator|.
name|set
argument_list|(
name|n
argument_list|,
name|node
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|list
operator|.
name|size
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
operator|(
operator|(
name|leftPrec
operator|>
literal|0
operator|)
operator|||
operator|(
name|rightPrec
operator|>
literal|0
operator|)
operator|)
condition|?
name|writer
operator|.
name|startList
argument_list|(
literal|"("
argument_list|,
literal|")"
argument_list|)
else|:
name|writer
operator|.
name|startList
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|commaList
argument_list|(
name|writer
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
name|void
name|commaList
parameter_list|(
name|SqlWriter
name|writer
parameter_list|)
block|{
comment|// The precedence of the comma operator if low but not zero. For
comment|// instance, this ensures parentheses in
comment|//    select x, (select * from foo order by z), y from t
for|for
control|(
name|SqlNode
name|node
range|:
name|list
control|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|node
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|andOrList
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlKind
name|sepKind
parameter_list|)
block|{
name|SqlBinaryOperator
name|sepOp
init|=
name|sepKind
operator|==
name|SqlKind
operator|.
name|AND
condition|?
name|SqlStdOperatorTable
operator|.
name|AND
else|:
name|SqlStdOperatorTable
operator|.
name|OR
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SqlNode
name|node
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|writer
operator|.
name|sep
argument_list|(
name|sepKind
operator|.
name|name
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// The precedence pulling on the LHS of a node is the
comment|// right-precedence of the separator operator, except at the start
comment|// of the list; similarly for the RHS of a node. If the operator
comment|// has left precedence 4 and right precedence 5, the precedences
comment|// in a 3-node list will look as follows:
comment|//   0<- node1 -> 4  5<- node2 -> 4  5<- node3 -> 0
name|int
name|lprec
init|=
operator|(
name|i
operator|==
literal|0
operator|)
condition|?
literal|0
else|:
name|sepOp
operator|.
name|getRightPrec
argument_list|()
decl_stmt|;
name|int
name|rprec
init|=
operator|(
name|i
operator|==
operator|(
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|)
operator|)
condition|?
literal|0
else|:
name|sepOp
operator|.
name|getLeftPrec
argument_list|()
decl_stmt|;
name|node
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|lprec
argument_list|,
name|rprec
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|validate
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
for|for
control|(
name|SqlNode
name|child
range|:
name|list
control|)
block|{
name|child
operator|.
name|validate
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|SqlVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
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
name|SqlNodeList
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
name|SqlNodeList
name|that
init|=
operator|(
name|SqlNodeList
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|size
argument_list|()
operator|!=
name|that
operator|.
name|size
argument_list|()
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SqlNode
name|thisChild
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|thatChild
init|=
name|that
operator|.
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|thisChild
operator|.
name|equalsDeep
argument_list|(
name|thatChild
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
name|SqlNode
index|[]
name|toArray
parameter_list|()
block|{
return|return
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|SqlNode
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isEmptyList
parameter_list|(
specifier|final
name|SqlNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|SqlNodeList
condition|)
block|{
if|if
condition|(
literal|0
operator|==
operator|(
operator|(
name|SqlNodeList
operator|)
name|node
operator|)
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|validateExpr
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
comment|// While a SqlNodeList is not always a valid expression, this
comment|// implementation makes that assumption. It just validates the members
comment|// of the list.
comment|//
comment|// One example where this is valid is the IN operator. The expression
comment|//
comment|//    empno IN (10, 20)
comment|//
comment|// results in a call with operands
comment|//
comment|//    {  SqlIdentifier({"empno"}),
comment|//       SqlNodeList(SqlLiteral(10), SqlLiteral(20))  }
for|for
control|(
name|SqlNode
name|node
range|:
name|list
control|)
block|{
name|node
operator|.
name|validateExpr
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlNodeList.java
end_comment

end_unit

