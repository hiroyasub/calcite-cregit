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
name|sql
operator|.
name|SqlAggFunction
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
name|SqlWindow
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
name|ControlFlowException
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
comment|/**  * Call to an aggregate function over a window.  */
end_comment

begin_class
specifier|public
class|class
name|RexOver
extends|extends
name|RexCall
block|{
specifier|private
specifier|static
specifier|final
name|Finder
name|FINDER
init|=
operator|new
name|Finder
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RexWindow
name|window
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|distinct
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a RexOver.    *    *<p>For example, "SUM(DISTINCT x) OVER (ROWS 3 PRECEDING)" is represented    * as:    *    *<ul>    *<li>type = Integer,    *<li>op = {@link org.apache.calcite.sql.fun.SqlStdOperatorTable#SUM},    *<li>operands = { {@link RexFieldAccess}("x") }    *<li>window = {@link SqlWindow}(ROWS 3 PRECEDING)    *</ul>    *    * @param type     Result type    * @param op       Aggregate operator    * @param operands Operands list    * @param window   Window specification    * @param distinct Aggregate operator is applied on distinct elements    */
name|RexOver
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|SqlAggFunction
name|op
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
parameter_list|,
name|RexWindow
name|window
parameter_list|,
name|boolean
name|distinct
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
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|op
operator|.
name|isAggregator
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|window
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|window
argument_list|)
expr_stmt|;
name|this
operator|.
name|distinct
operator|=
name|distinct
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the aggregate operator for this expression.    */
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
specifier|public
name|boolean
name|isDistinct
parameter_list|()
block|{
return|return
name|distinct
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|computeDigest
parameter_list|(
name|boolean
name|withType
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|op
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
if|if
condition|(
name|distinct
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"DISTINCT "
argument_list|)
expr_stmt|;
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
name|operands
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
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|RexNode
name|operand
init|=
name|operands
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|operand
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
if|if
condition|(
name|withType
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|type
operator|.
name|getFullTypeString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" OVER ("
argument_list|)
operator|.
name|append
argument_list|(
name|window
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
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
specifier|public
parameter_list|<
name|R
parameter_list|,
name|P
parameter_list|>
name|R
name|accept
parameter_list|(
name|RexBiVisitor
argument_list|<
name|R
argument_list|,
name|P
argument_list|>
name|visitor
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visitOver
argument_list|(
name|this
argument_list|,
name|arg
argument_list|)
return|;
block|}
comment|/**    * Returns whether an expression contains an OVER clause.    */
specifier|public
specifier|static
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
name|FINDER
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
comment|/**    * Returns whether a program contains an OVER clause.    */
specifier|public
specifier|static
name|boolean
name|containsOver
parameter_list|(
name|RexProgram
name|program
parameter_list|)
block|{
try|try
block|{
name|RexUtil
operator|.
name|apply
argument_list|(
name|FINDER
argument_list|,
name|program
operator|.
name|getExprList
argument_list|()
argument_list|,
literal|null
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
comment|/**    * Returns whether an expression list contains an OVER clause.    */
specifier|public
specifier|static
name|boolean
name|containsOver
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|exprs
parameter_list|,
name|RexNode
name|condition
parameter_list|)
block|{
try|try
block|{
name|RexUtil
operator|.
name|apply
argument_list|(
name|FINDER
argument_list|,
name|exprs
argument_list|,
name|condition
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
annotation|@
name|Override
specifier|public
name|RexCall
name|clone
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/** Exception thrown when an OVER is found. */
specifier|private
specifier|static
class|class
name|OverFound
extends|extends
name|ControlFlowException
block|{
specifier|public
specifier|static
specifier|final
name|OverFound
name|INSTANCE
init|=
operator|new
name|OverFound
argument_list|()
decl_stmt|;
block|}
comment|/**    * Visitor which detects a {@link RexOver} inside a {@link RexNode}    * expression.    *    *<p>It is re-entrant (two threads can use an instance at the same time)    * and it can be re-used for multiple visits.    */
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
name|INSTANCE
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RexOver.java
end_comment

end_unit

