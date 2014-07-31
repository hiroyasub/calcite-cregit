begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_comment
comment|/**  * Indicates that an operation timed out. This is not an error; you can  * retry the operation.  */
end_comment

begin_class
specifier|public
class|class
name|SqlTimeoutException
extends|extends
name|SQLException
block|{
name|SqlTimeoutException
parameter_list|()
block|{
comment|// SQLException(reason, SQLState, vendorCode)
comment|// REVIEW mb 19-Jul-05 Is there a standard SQLState?
name|super
argument_list|(
literal|"timeout"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlTimeoutException.java
end_comment

end_unit

