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
name|sql
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
comment|/**  * Call to an aggregate function over a window.  *  * @author jhyde  * @version $Id$  * @since Dec 6, 2004  */
end_comment

begin_class
specifier|public
class|class
name|RexOver
extends|extends
name|RexCall
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RexWindow
name|window
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a RexOver.      *      *<p>For example, "SUM(x) OVER (ROWS 3 PRECEDING)" is represented as:      *      *<ul>      *<li>type = Integer,      *<li>op = {@link org.eigenbase.sql.fun.SqlStdOperatorTable#sumOperator},      *<li>operands = { {@link RexFieldAccess}("x") }      *<li>window = {@link SqlWindow}(ROWS 3 PRECEDING)      *</ul>      *      * @param type Result type      * @param op Aggregate operator      * @param operands Operands list      * @param window Window specification      *      * @pre op.isAggregator()      * @pre window != null      * @pre window.getRefName() == null      */
name|RexOver
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|SqlAggFunction
name|op
parameter_list|,
name|RexNode
index|[]
name|operands
parameter_list|,
name|RexWindow
name|window
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|,
name|op
argument_list|,
name|operands
argument_list|)
expr_stmt|;
assert|assert
name|op
operator|.
name|isAggregator
argument_list|()
operator|:
literal|"precondition: op.isAggregator()"
assert|;
assert|assert
name|op
operator|instanceof
name|SqlAggFunction
assert|;
assert|assert
name|window
operator|!=
literal|null
operator|:
literal|"precondition: window != null"
assert|;
name|this
operator|.
name|window
operator|=
name|window
expr_stmt|;
name|this
operator|.
name|digest
operator|=
name|computeDigest
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns the aggregate operator for this expression.      */
specifier|public
name|SqlAggFunction
name|getAggOperator
parameter_list|()
block|{
return|return
operator|(
name|SqlAggFunction
operator|)
name|getOperator
argument_list|()
return|;
block|}
specifier|public
name|RexWindow
name|getWindow
parameter_list|()
block|{
return|return
name|window
return|;
block|}
specifier|protected
name|String
name|computeDigest
parameter_list|(
name|boolean
name|withType
parameter_list|)
block|{
return|return
name|super
operator|.
name|computeDigest
argument_list|(
name|withType
argument_list|)
operator|+
literal|" OVER ("
operator|+
name|window
operator|+
literal|")"
return|;
block|}
specifier|public
name|RexOver
name|clone
parameter_list|()
block|{
return|return
operator|new
name|RexOver
argument_list|(
name|getType
argument_list|()
argument_list|,
name|getAggOperator
argument_list|()
argument_list|,
name|operands
argument_list|,
name|window
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|RexVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visitOver
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * Returns whether an expression contains an OVER clause.      */
specifier|public
specifier|static
name|boolean
name|containsOver
parameter_list|(
name|RexNode
name|expr
parameter_list|)
block|{
return|return
name|Finder
operator|.
name|instance
operator|.
name|containsOver
argument_list|(
name|expr
argument_list|)
return|;
block|}
comment|/**      * Returns whether a program contains an OVER clause.      */
specifier|public
specifier|static
name|boolean
name|containsOver
parameter_list|(
name|RexProgram
name|program
parameter_list|)
block|{
for|for
control|(
name|RexNode
name|expr
range|:
name|program
operator|.
name|getExprList
argument_list|()
control|)
block|{
if|if
condition|(
name|Finder
operator|.
name|instance
operator|.
name|containsOver
argument_list|(
name|expr
argument_list|)
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
comment|/**      * Returns whether an expression list contains an OVER clause.      *      * @deprecated      */
specifier|public
specifier|static
name|boolean
name|containsOver
parameter_list|(
name|RexNode
index|[]
name|exprs
parameter_list|,
name|RexNode
name|expr
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|exprs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|Finder
operator|.
name|instance
operator|.
name|containsOver
argument_list|(
name|exprs
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
if|if
condition|(
operator|(
name|expr
operator|!=
literal|null
operator|)
operator|&&
name|Finder
operator|.
name|instance
operator|.
name|containsOver
argument_list|(
name|expr
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
specifier|private
specifier|static
class|class
name|OverFound
extends|extends
name|RuntimeException
block|{
specifier|public
specifier|static
specifier|final
name|OverFound
name|instance
init|=
operator|new
name|OverFound
argument_list|()
decl_stmt|;
block|}
comment|/**      * Visitor which detects a {@link RexOver} inside a {@link RexNode}      * expression.      *      *<p>It is re-entrant (two threads can use an instance at the same time)      * and it can be re-used for multiple visits.      */
specifier|private
specifier|static
class|class
name|Finder
extends|extends
name|RexVisitorImpl
argument_list|<
name|Void
argument_list|>
block|{
specifier|static
specifier|final
name|RexOver
operator|.
name|Finder
name|instance
init|=
operator|new
name|RexOver
operator|.
name|Finder
argument_list|()
decl_stmt|;
specifier|public
name|Finder
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Void
name|visitOver
parameter_list|(
name|RexOver
name|over
parameter_list|)
block|{
throw|throw
name|OverFound
operator|.
name|instance
throw|;
block|}
comment|/**          * Returns whether an expression contains an OVER clause.          */
name|boolean
name|containsOver
parameter_list|(
name|RexNode
name|expr
parameter_list|)
block|{
try|try
block|{
name|expr
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
name|OverFound
name|e
parameter_list|)
block|{
name|Util
operator|.
name|swallow
argument_list|(
name|e
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End RexOver.java
end_comment

end_unit

