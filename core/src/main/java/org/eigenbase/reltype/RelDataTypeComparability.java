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
name|reltype
package|;
end_package

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
comment|/**  * RelDataTypeComparability is an enumeration of the categories of comparison  * operators which types may support.  *  *<p>NOTE jvs 17-Mar-2005: the order of values of this enumeration is  * significant (from least inclusive to most inclusive) and should not be  * changed.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_enum
specifier|public
enum|enum
name|RelDataTypeComparability
block|{
name|None
argument_list|(
literal|"No comparisons allowed"
argument_list|)
block|,
name|Unordered
argument_list|(
literal|"Only equals/not-equals allowed"
argument_list|)
block|,
name|All
argument_list|(
literal|"All comparisons allowed"
argument_list|)
block|;
name|RelDataTypeComparability
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|Util
operator|.
name|discard
argument_list|(
name|description
argument_list|)
expr_stmt|;
block|}
block|}
end_enum

begin_comment
comment|// End RelDataTypeComparability.java
end_comment

end_unit

