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
name|SqlKind
import|;
end_import

begin_comment
comment|/**  * Dynamic parameter reference in a row-expression.  */
end_comment

begin_class
specifier|public
class|class
name|RexDynamicParam
extends|extends
name|RexVariable
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|int
name|index
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a dynamic parameter.    *    * @param type  inferred type of parameter    * @param index 0-based index of dynamic parameter in statement    */
specifier|public
name|RexDynamicParam
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|super
argument_list|(
literal|"?"
operator|+
name|index
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RexDynamicParam
name|clone
parameter_list|()
block|{
return|return
operator|new
name|RexDynamicParam
argument_list|(
name|type
argument_list|,
name|index
argument_list|)
return|;
block|}
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|SqlKind
operator|.
name|DYNAMIC_PARAM
return|;
block|}
specifier|public
name|int
name|getIndex
parameter_list|()
block|{
return|return
name|index
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
name|visitDynamicParam
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RexDynamicParam.java
end_comment

end_unit

