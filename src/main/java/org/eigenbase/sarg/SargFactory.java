begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sarg
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
name|rex
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

begin_comment
comment|/**  * SargFactory creates new instances of various sarg-related objects.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|SargFactory
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RexBuilder
name|rexBuilder
decl_stmt|;
specifier|private
specifier|final
name|RexNode
name|rexNull
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a new SargFactory.      *      * @param rexBuilder factory for instances of {@link RexNode}, needed      * internally in the sarg representation, and also for recomposing sargs      * into equivalent rex trees      */
specifier|public
name|SargFactory
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
name|this
operator|.
name|rexBuilder
operator|=
name|rexBuilder
expr_stmt|;
name|rexNull
operator|=
name|rexBuilder
operator|.
name|constantNull
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Creates a new endpoint. Initially, the endpoint represents a lower bound      * of negative infinity.      *      * @param dataType datatype for domain      *      * @return new endpoint      */
specifier|public
name|SargMutableEndpoint
name|newEndpoint
parameter_list|(
name|RelDataType
name|dataType
parameter_list|)
block|{
return|return
operator|new
name|SargMutableEndpoint
argument_list|(
name|this
argument_list|,
name|dataType
argument_list|)
return|;
block|}
comment|/**      * Creates a new interval expression. The interval starts out as unbounded      * (meaning it includes every non-null value of the datatype), with      * SqlNullSemantics.NULL_MATCHES_NOTHING.      *      * @param dataType datatype for domain      */
specifier|public
name|SargIntervalExpr
name|newIntervalExpr
parameter_list|(
name|RelDataType
name|dataType
parameter_list|)
block|{
return|return
name|newIntervalExpr
argument_list|(
name|dataType
argument_list|,
name|SqlNullSemantics
operator|.
name|NULL_MATCHES_NOTHING
argument_list|)
return|;
block|}
comment|/**      * Creates a new unbounded interval expression with non-default null      * semantics.      *      * @param dataType datatype for domain      * @param nullSemantics null semantics governing searches on this interval      */
specifier|public
name|SargIntervalExpr
name|newIntervalExpr
parameter_list|(
name|RelDataType
name|dataType
parameter_list|,
name|SqlNullSemantics
name|nullSemantics
parameter_list|)
block|{
return|return
operator|new
name|SargIntervalExpr
argument_list|(
name|this
argument_list|,
name|dataType
argument_list|,
name|nullSemantics
argument_list|)
return|;
block|}
comment|/**      * Creates a new set expression, initially with no children.      *      * @param dataType datatype for domain      * @param setOp set operator      */
specifier|public
name|SargSetExpr
name|newSetExpr
parameter_list|(
name|RelDataType
name|dataType
parameter_list|,
name|SargSetOperator
name|setOp
parameter_list|)
block|{
return|return
operator|new
name|SargSetExpr
argument_list|(
name|this
argument_list|,
name|dataType
argument_list|,
name|setOp
argument_list|)
return|;
block|}
comment|/**      * @return new analyzer for rex expressions      */
specifier|public
name|SargRexAnalyzer
name|newRexAnalyzer
parameter_list|()
block|{
return|return
operator|new
name|SargRexAnalyzer
argument_list|(
name|this
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * @param simpleMode if true, the analyzer restricts the types of predicates      * it allows; the following are disallowed - conjuntions on the same      * RexInputRef, more than one range predicate, and all disjunctions      *      * @return new analyzer for rex expressions      */
specifier|public
name|SargRexAnalyzer
name|newRexAnalyzer
parameter_list|(
name|boolean
name|simpleMode
parameter_list|)
block|{
return|return
operator|new
name|SargRexAnalyzer
argument_list|(
name|this
argument_list|,
name|simpleMode
argument_list|)
return|;
block|}
comment|/**      * @param lowerRexInputIdx if>= 0, treat RexInputRefs whose index is within      * the range [lowerRexInputIdx, upperRexInputIdx) as coordinates in      * expressions      * @param upperRexInputIdx if>= 0, treat RexInputRefs whose index is within      * the range [lowerRexInputIdx, upperRexInputIdx) as coordinates in      * expressions      *      * @return new analyzer for rex expressions      */
specifier|public
name|SargRexAnalyzer
name|newRexAnalyzer
parameter_list|(
name|int
name|lowerRexInputIdx
parameter_list|,
name|int
name|upperRexInputIdx
parameter_list|)
block|{
return|return
operator|new
name|SargRexAnalyzer
argument_list|(
name|this
argument_list|,
literal|true
argument_list|,
name|lowerRexInputIdx
argument_list|,
name|upperRexInputIdx
argument_list|)
return|;
block|}
comment|/**      * @return the null literal, which can be used to represent a range matching      * the null value      */
specifier|public
name|RexNode
name|newNullLiteral
parameter_list|()
block|{
return|return
name|rexNull
return|;
block|}
comment|/**      * @return RexBuilder used by this factory      */
specifier|public
name|RexBuilder
name|getRexBuilder
parameter_list|()
block|{
return|return
name|rexBuilder
return|;
block|}
block|}
end_class

begin_comment
comment|// End SargFactory.java
end_comment

end_unit

