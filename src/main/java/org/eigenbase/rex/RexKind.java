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
name|rex
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

begin_comment
comment|/**  * Enumeration of some important types of row-expression.  *  *<p>The values are immutable, canonical constants, so you can use Kinds to  * find particular types of expressions quickly. To identity a call to a common  * operator such as '=', use {@link RexNode#isA}:  *  *<blockquote>  *<pre>exp.{@link RexNode#isA isA}({@link RexKind#Equals RexKind.Equals})</pre>  *</blockquote>  *  * To identify a category of expressions, you can use {@link RexNode#isA} with  * an aggregate RexKind. The following expression will return<code>true</code>  * for calls to '=' and '&gt;=', but<code>false</code> for the constant '5', or  * a call to '+':  *  *<blockquote>  *<pre>exp.{@link RexNode#isA isA}({@link RexKind#Comparison RexKind.Comparison})</pre>  *</blockquote>  *  * To quickly choose between a number of options, use a switch statement:  *  *<blockquote>  *<pre>switch (exp.getKind()) {  * case {@link #Equals}:  *     ...;  * case {@link #NotEquals}:  *     ...;  * default:  *     throw {@link org.eigenbase.util.Util#unexpected Util.unexpected}(exp.getKind());  * }</pre>  *</blockquote>  *</p>  *  * @author jhyde  * @version $Id$  * @since Nov 24, 2003  */
end_comment

begin_enum
specifier|public
enum|enum
name|RexKind
block|{
comment|/**      * No operator in particular. This is the default kind.      */
name|Other
block|,
comment|/**      * The equals operator, "=".      */
name|Equals
block|,
comment|/**      * The not-equals operator, "&#33;=" or "&lt;&gt;".      */
name|NotEquals
block|,
comment|/**      * The greater-than operator, "&gt;".      */
name|GreaterThan
block|,
comment|/**      * The greater-than-or-equal operator, "&gt;=".      */
name|GreaterThanOrEqual
block|,
comment|/**      * The less-than operator, "&lt;".      */
name|LessThan
block|,
comment|/**      * The less-than-or-equal operator, "&lt;=".      */
name|LessThanOrEqual
block|,
comment|/**      * A comparison operator ({@link #Equals}, {@link #GreaterThan}, etc.).      * Comparisons are always a {@link RexCall} with 2 arguments.      */
name|Comparison
argument_list|(
operator|new
name|RexKind
index|[]
block|{
name|Equals
block|,
name|NotEquals
block|,
name|GreaterThan
block|,
name|GreaterThanOrEqual
block|,
name|LessThan
block|,
name|LessThanOrEqual
block|}
argument_list|)
block|,
comment|/**      * The logical "AND" operator.      */
name|And
block|,
comment|/**      * The logical "OR" operator.      */
name|Or
block|,
comment|/**      * The logical "NOT" operator.      */
name|Not
block|,
comment|/**      * A logical operator ({@link #And}, {@link #Or}, {@link #Not}).      */
name|Logical
argument_list|(
operator|new
name|RexKind
index|[]
block|{
name|And
block|,
name|Or
block|,
name|Not
block|}
argument_list|)
block|,
comment|/**      * The arithmetic division operator, "/".      */
name|Divide
block|,
comment|/**      * The arithmetic minus operator, "-".      *      * @see #MinusPrefix      */
name|Minus
block|,
comment|/**      * The arithmetic plus operator, "+".      */
name|Plus
block|,
comment|/**      * The unary minus operator, as in "-1".      *      * @see #Minus      */
name|MinusPrefix
block|,
comment|/**      * The arithmetic multiplication operator, "*".      */
name|Times
block|,
comment|/**      * An arithmetic operator ({@link #Divide}, {@link #Minus}, {@link      * #MinusPrefix}, {@link #Plus}, {@link #Times}).      */
name|Arithmetic
argument_list|(
operator|new
name|RexKind
index|[]
block|{
name|Divide
block|,
name|Minus
block|,
name|MinusPrefix
block|,
name|Plus
block|,
name|Times
block|}
argument_list|)
block|,
comment|/**      * The field access operator, ".".      */
name|FieldAccess
block|,
comment|/**      * The string concatenation operator, "||".      */
name|Concat
block|,
comment|/**      * The substring function.      */
comment|// REVIEW (jhyde, 2004/1/26) We should obsolete Substr. RexKind values are
comment|// so that the validator and optimizer can quickly recognize special
comment|// syntactic cetegories, and there's nothing particularly special about
comment|// Substr. For the mapping of sql->rex, and rex->calc, just use its name or
comment|// signature.
name|Substr
block|,
comment|/**      * The row constructor operator.      */
name|Row
block|,
comment|/**      * The IS NULL operator.      */
name|IsNull
block|,
comment|/**      * An identifier.      */
name|Identifier
block|,
comment|/**      * A literal.      */
name|Literal
block|,
comment|/**      * The VALUES operator.      */
name|Values
block|,
comment|/**      * The IS TRUE operator.      */
name|IsTrue
block|,
comment|/**      * The IS FALSE operator.      */
name|IsFalse
block|,
comment|/**      * A dynamic parameter.      */
name|DynamicParam
block|,
name|Cast
block|,
name|Trim
block|,
comment|/**      * The LIKE operator.      */
name|Like
block|,
comment|/**      * The SIMILAR operator.      */
name|Similar
block|,
comment|/**      * The MULTISET Query Constructor      */
name|MultisetQueryConstructor
block|,
comment|/**      * NEW invocation      */
name|NewSpecification
block|,
comment|/**      * The internal REINTERPRET operator      */
name|Reinterpret
block|;
specifier|private
specifier|final
name|Set
argument_list|<
name|RexKind
argument_list|>
name|otherKinds
decl_stmt|;
comment|/**      * Creates a kind.      */
name|RexKind
parameter_list|()
block|{
name|otherKinds
operator|=
name|Collections
operator|.
name|emptySet
argument_list|()
expr_stmt|;
block|}
comment|/**      * Creates a kind which includes other kinds.      */
name|RexKind
parameter_list|(
name|RexKind
index|[]
name|others
parameter_list|)
block|{
name|otherKinds
operator|=
operator|new
name|HashSet
argument_list|<
name|RexKind
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|others
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|RexKind
name|other
init|=
name|others
index|[
name|i
index|]
decl_stmt|;
name|otherKinds
operator|.
name|add
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|otherKinds
operator|.
name|addAll
argument_list|(
name|other
operator|.
name|otherKinds
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|includes
parameter_list|(
name|RexKind
name|kind
parameter_list|)
block|{
return|return
operator|(
name|kind
operator|==
name|this
operator|)
operator|||
name|otherKinds
operator|.
name|contains
argument_list|(
name|kind
argument_list|)
return|;
block|}
block|}
end_enum

begin_comment
comment|// End RexKind.java
end_comment

end_unit

