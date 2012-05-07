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
name|util
operator|.
name|property
package|;
end_package

begin_comment
comment|/**  * Basic implementation of a trigger, which doesn't do anything.  *  * @author Julian Hyde  * @version $Id$  * @since 5 July 2005  */
end_comment

begin_class
specifier|public
class|class
name|TriggerBase
implements|implements
name|Trigger
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|boolean
name|persistent
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|TriggerBase
parameter_list|(
name|boolean
name|persistent
parameter_list|)
block|{
name|this
operator|.
name|persistent
operator|=
name|persistent
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
name|persistent
return|;
block|}
specifier|public
name|int
name|phase
parameter_list|()
block|{
return|return
name|Trigger
operator|.
name|PRIMARY_PHASE
return|;
block|}
specifier|public
name|void
name|execute
parameter_list|(
name|Property
name|property
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|VetoRT
block|{
comment|// do nothing
block|}
block|}
end_class

begin_comment
comment|// End TriggerBase.java
end_comment

end_unit

