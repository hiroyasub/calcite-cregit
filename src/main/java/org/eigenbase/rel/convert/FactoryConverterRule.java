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

begin_comment
comment|/**  * Generic implementation of {@link ConverterRule} which lets a {@link  * ConverterFactory} do the work.  *  * @author jhyde  * @version $Id$  * @since Jun 18, 2003  */
end_comment

begin_class
specifier|public
class|class
name|FactoryConverterRule
extends|extends
name|ConverterRule
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|ConverterFactory
name|factory
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|FactoryConverterRule
parameter_list|(
name|ConverterFactory
name|factory
parameter_list|)
block|{
name|super
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|factory
operator|.
name|getInConvention
argument_list|()
argument_list|,
name|factory
operator|.
name|getConvention
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|boolean
name|isGuaranteed
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
name|factory
operator|.
name|convert
argument_list|(
name|rel
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End FactoryConverterRule.java
end_comment

end_unit

