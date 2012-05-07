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
name|runtime
package|;
end_package

begin_comment
comment|/**  * A base class for implementations of {@link TupleIter}, with default  * implementations of some of its methods.  *  * @author Marc Berkowitz  * @version $Id$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractTupleIter
implements|implements
name|TupleIter
block|{
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|boolean
name|setTimeout
parameter_list|(
name|long
name|timeout
parameter_list|,
name|boolean
name|asUnderflow
parameter_list|)
block|{
return|return
literal|false
return|;
comment|// by default, don't provide a timeout
block|}
specifier|public
name|boolean
name|addListener
parameter_list|(
name|MoreDataListener
name|c
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|restart
parameter_list|()
block|{
block|}
specifier|public
name|StringBuilder
name|printStatus
parameter_list|(
name|StringBuilder
name|b
parameter_list|)
block|{
return|return
name|b
return|;
block|}
block|}
end_class

begin_comment
comment|// End AbstractTupleIter.java
end_comment

end_unit

