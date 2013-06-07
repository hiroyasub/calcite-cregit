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
name|rel
operator|.
name|convert
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
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
name|relopt
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * TraitMatchingRule adapts a converter rule, restricting it to fire only when  * its input already matches the expected output trait. This can be used with  * {@link org.eigenbase.relopt.hep.HepPlanner} in cases where alternate  * implementations are available and it is desirable to minimize converters.  */
end_comment

begin_class
specifier|public
class|class
name|TraitMatchingRule
extends|extends
name|RelOptRule
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|ConverterRule
name|converter
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a new TraitMatchingRule.      *      * @param converterRule rule to be restricted; rule must take a single      * operand expecting a single input      */
specifier|public
name|TraitMatchingRule
parameter_list|(
name|ConverterRule
name|converterRule
parameter_list|)
block|{
name|super
argument_list|(
name|some
argument_list|(
name|converterRule
operator|.
name|getOperand
argument_list|()
operator|.
name|getMatchedClass
argument_list|()
argument_list|,
name|any
argument_list|(
name|RelNode
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
literal|"TraitMatchingRule: "
operator|+
name|converterRule
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|converterRule
operator|.
name|getOperand
argument_list|()
operator|.
name|getChildOperands
argument_list|()
operator|==
literal|null
operator|)
assert|;
name|this
operator|.
name|converter
operator|=
name|converterRule
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelOptRule
specifier|public
name|Convention
name|getOutConvention
parameter_list|()
block|{
return|return
name|converter
operator|.
name|getOutConvention
argument_list|()
return|;
block|}
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|RelNode
name|input
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|input
operator|.
name|getTraitSet
argument_list|()
operator|.
name|contains
argument_list|(
name|converter
operator|.
name|getOutTrait
argument_list|()
argument_list|)
condition|)
block|{
name|converter
operator|.
name|onMatch
argument_list|(
name|call
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End TraitMatchingRule.java
end_comment

end_unit

