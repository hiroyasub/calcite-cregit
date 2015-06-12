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
name|pretty
operator|.
name|SqlPrettyWriter
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
name|SqlString
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
name|SqlVisitor
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
name|validate
operator|.
name|SqlMoniker
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
name|validate
operator|.
name|SqlMonotonicity
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
name|validate
operator|.
name|SqlValidator
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
name|validate
operator|.
name|SqlValidatorScope
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
name|Collection
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * A<code>SqlNode</code> is a SQL parse tree.  *  *<p>It may be an  * {@link SqlOperator operator}, {@link SqlLiteral literal},  * {@link SqlIdentifier identifier}, and so forth.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlNode
implements|implements
name|Cloneable
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|SqlNode
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|SqlNode
index|[
literal|0
index|]
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|SqlParserPos
name|pos
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a node.    *    * @param pos Parser position, must not be null.    */
name|SqlNode
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|Util
operator|.
name|pre
argument_list|(
name|pos
operator|!=
literal|null
argument_list|,
literal|"pos != null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|pos
operator|=
name|pos
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Object
name|clone
parameter_list|()
block|{
return|return
name|clone
argument_list|(
name|getParserPosition
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Clones a SqlNode with a different position.    */
specifier|public
name|SqlNode
name|clone
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
comment|// REVIEW jvs 26-July-2006:  shouldn't pos be used here?  Or are
comment|// subclasses always supposed to override, in which case this
comment|// method should probably be abstract?
try|try
block|{
return|return
operator|(
name|SqlNode
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|e
argument_list|,
literal|"error while cloning "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns the type of node this is, or    * {@link org.apache.calcite.sql.SqlKind#OTHER} if it's nothing special.    *    * @return a {@link SqlKind} value, never null    * @see #isA    */
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|SqlKind
operator|.
name|OTHER
return|;
block|}
comment|/**    * Returns whether this node is a member of an aggregate category.    *    *<p>For example, {@code node.isA(SqlKind.QUERY)} returns {@code true}    * if the node is a SELECT, INSERT, UPDATE etc.    *    *<p>This method is shorthand: {@code node.isA(category)} is always    * equivalent to {@code node.getKind().belongsTo(category)}.    *    * @param category Category    * @return Whether this node belongs to the given category.    */
specifier|public
specifier|final
name|boolean
name|isA
parameter_list|(
name|Set
argument_list|<
name|SqlKind
argument_list|>
name|category
parameter_list|)
block|{
return|return
name|getKind
argument_list|()
operator|.
name|belongsTo
argument_list|(
name|category
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|SqlNode
index|[]
name|cloneArray
parameter_list|(
name|SqlNode
index|[]
name|nodes
parameter_list|)
block|{
name|SqlNode
index|[]
name|clones
init|=
name|nodes
operator|.
name|clone
argument_list|()
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
name|clones
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SqlNode
name|node
init|=
name|clones
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|clones
index|[
name|i
index|]
operator|=
operator|(
name|SqlNode
operator|)
name|node
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|clones
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toSqlString
argument_list|(
literal|null
argument_list|)
operator|.
name|getSql
argument_list|()
return|;
block|}
comment|/**    * Returns the SQL text of the tree of which this<code>SqlNode</code> is    * the root.    *    * @param dialect     Dialect    * @param forceParens wraps all expressions in parentheses; good for parse    *                    test, but false by default.    *    *<p>Typical return values are:</p>    *<ul>    *<li>'It''s a bird!'</li>    *<li>NULL</li>    *<li>12.3</li>    *<li>DATE '1969-04-29'</li>    *</ul>    */
specifier|public
name|SqlString
name|toSqlString
parameter_list|(
name|SqlDialect
name|dialect
parameter_list|,
name|boolean
name|forceParens
parameter_list|)
block|{
if|if
condition|(
name|dialect
operator|==
literal|null
condition|)
block|{
name|dialect
operator|=
name|SqlDialect
operator|.
name|DUMMY
expr_stmt|;
block|}
name|SqlPrettyWriter
name|writer
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|dialect
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setAlwaysUseParentheses
argument_list|(
name|forceParens
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setSelectListItemsOnSeparateLines
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setIndentation
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|unparse
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
operator|new
name|SqlString
argument_list|(
name|dialect
argument_list|,
name|sql
argument_list|)
return|;
block|}
specifier|public
name|SqlString
name|toSqlString
parameter_list|(
name|SqlDialect
name|dialect
parameter_list|)
block|{
return|return
name|toSqlString
argument_list|(
name|dialect
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Writes a SQL representation of this node to a writer.    *    *<p>The<code>leftPrec</code> and<code>rightPrec</code> parameters give    * us enough context to decide whether we need to enclose the expression in    * parentheses. For example, we need parentheses around "2 + 3" if preceded    * by "5 *". This is because the precedence of the "*" operator is greater    * than the precedence of the "+" operator.    *    *<p>The algorithm handles left- and right-associative operators by giving    * them slightly different left- and right-precedence.    *    *<p>If {@link SqlWriter#isAlwaysUseParentheses()} is true, we use    * parentheses even when they are not required by the precedence rules.    *    *<p>For the details of this algorithm, see {@link SqlCall#unparse}.    *    * @param writer    Target writer    * @param leftPrec  The precedence of the {@link SqlNode} immediately    *                  preceding this node in a depth-first scan of the parse    *                  tree    * @param rightPrec The precedence of the {@link SqlNode} immediately    */
specifier|public
specifier|abstract
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
function_decl|;
specifier|public
name|SqlParserPos
name|getParserPosition
parameter_list|()
block|{
return|return
name|pos
return|;
block|}
comment|/**    * Validates this node.    *    *<p>The typical implementation of this method will make a callback to the    * validator appropriate to the node type and context. The validator has    * methods such as {@link SqlValidator#validateLiteral} for these purposes.    *    * @param scope Validator    */
specifier|public
specifier|abstract
name|void
name|validate
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
function_decl|;
comment|/**    * Lists all the valid alternatives for this node if the parse position of    * the node matches that of pos. Only implemented now for SqlCall and    * SqlOperator.    *    * @param validator Validator    * @param scope     Validation scope    * @param pos       SqlParserPos indicating the cursor position at which    *                  completion hints are requested for    * @param hintList  list of valid options    */
specifier|public
name|void
name|findValidOptions
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
name|Collection
argument_list|<
name|SqlMoniker
argument_list|>
name|hintList
parameter_list|)
block|{
comment|// no valid options
block|}
comment|/**    * Validates this node in an expression context.    *    *<p>Usually, this method does much the same as {@link #validate}, but a    * {@link SqlIdentifier} can occur in expression and non-expression    * contexts.    */
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
name|validate
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|)
expr_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|validator
operator|.
name|deriveType
argument_list|(
name|scope
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Accepts a generic visitor.    *    *<p>Implementations of this method in subtypes simply call the appropriate    *<code>visit</code> method on the    * {@link org.apache.calcite.sql.util.SqlVisitor visitor object}.    *    *<p>The type parameter<code>R</code> must be consistent with the type    * parameter of the visitor.    */
specifier|public
specifier|abstract
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
function_decl|;
comment|/**    * Returns whether this node is structurally equivalent to another node.    * Some examples:    *    *<ul>    *<li>1 + 2 is structurally equivalent to 1 + 2</li>    *<li>1 + 2 + 3 is structurally equivalent to (1 + 2) + 3, but not to 1 +    * (2 + 3), because the '+' operator is left-associative</li>    *</ul>    */
specifier|public
specifier|abstract
name|boolean
name|equalsDeep
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|boolean
name|fail
parameter_list|)
function_decl|;
comment|/**    * Returns whether two nodes are equal (using    * {@link #equalsDeep(SqlNode, boolean)}) or are both null.    *    * @param node1 First expression    * @param node2 Second expression    * @param fail  Whether to throw {@link AssertionError} if expressions are    *              not equal    */
specifier|public
specifier|static
name|boolean
name|equalDeep
parameter_list|(
name|SqlNode
name|node1
parameter_list|,
name|SqlNode
name|node2
parameter_list|,
name|boolean
name|fail
parameter_list|)
block|{
if|if
condition|(
name|node1
operator|==
literal|null
condition|)
block|{
return|return
name|node2
operator|==
literal|null
return|;
block|}
if|else if
condition|(
name|node2
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|node1
operator|.
name|equalsDeep
argument_list|(
name|node2
argument_list|,
name|fail
argument_list|)
return|;
block|}
block|}
comment|/**    * Returns whether expression is always ascending, descending or constant.    * This property is useful because it allows to safely aggregate infinite    * streams of values.    *    *<p>The default implementation returns    * {@link SqlMonotonicity#NOT_MONOTONIC}.    *    * @param scope Scope    */
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
return|return
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
return|;
block|}
comment|/** Returns whether two lists of operands are equal. */
specifier|public
specifier|static
name|boolean
name|equalDeep
parameter_list|(
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operands0
parameter_list|,
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operands1
parameter_list|,
name|boolean
name|fail
parameter_list|)
block|{
if|if
condition|(
name|operands0
operator|.
name|size
argument_list|()
operator|!=
name|operands1
operator|.
name|size
argument_list|()
condition|)
block|{
assert|assert
operator|!
name|fail
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
name|operands0
operator|.
name|size
argument_list|()
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
name|operands0
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|operands1
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
block|}
end_class

begin_comment
comment|// End SqlNode.java
end_comment

end_unit

