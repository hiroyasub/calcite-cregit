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
comment|/**  *<code>NoneConverter</code> converts a plan from<code>inConvention</code> to  * {@link org.eigenbase.relopt.CallingConvention#NONE_ORDINAL}.  *  * @author jhyde  * @version $Id$  * @since 15 February, 2002  */
end_comment

begin_class
specifier|public
class|class
name|NoneConverterRel
extends|extends
name|ConverterRelImpl
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|NoneConverterRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|child
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|CallingConventionTraitDef
operator|.
name|instance
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|CallingConvention
operator|.
name|NONE
argument_list|)
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
assert|assert
name|traitSet
operator|.
name|comprises
argument_list|(
name|CallingConvention
operator|.
name|NONE
argument_list|)
assert|;
return|return
operator|new
name|NoneConverterRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|init
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
comment|// we can't convert from any conventions, therefore no rules to register
name|Util
operator|.
name|discard
argument_list|(
name|planner
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End NoneConverterRel.java
end_comment

end_unit

