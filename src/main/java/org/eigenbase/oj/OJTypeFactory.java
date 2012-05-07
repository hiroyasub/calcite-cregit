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
name|oj
package|;
end_package

begin_import
import|import
name|openjava
operator|.
name|mop
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
name|reltype
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Extended {@link RelDataTypeFactory} which can convert to and from {@link  * openjava.mop.OJClass}.  *  * @author jhyde  * @version $Id$  * @since Jun 1, 2003  */
end_comment

begin_interface
specifier|public
interface|interface
name|OJTypeFactory
extends|extends
name|RelDataTypeFactory
block|{
comment|//~ Methods ----------------------------------------------------------------
name|OJClass
name|toOJClass
parameter_list|(
name|OJClass
name|declarer
parameter_list|,
name|RelDataType
name|type
parameter_list|)
function_decl|;
name|RelDataType
name|toType
parameter_list|(
name|OJClass
name|ojClass
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End OJTypeFactory.java
end_comment

end_unit

