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
comment|/**  * A set of classes for holding primitive objects.  *  * @author jhyde  * @version $Id$  * @since 8 February, 2002  */
end_comment

begin_class
specifier|public
class|class
name|Holder
block|{
comment|//~ Inner Classes ----------------------------------------------------------
specifier|public
specifier|static
specifier|final
class|class
name|int_Holder
block|{
specifier|public
name|int
name|value
decl_stmt|;
specifier|public
name|int_Holder
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
name|void
name|setGreater
parameter_list|(
name|int
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|>
name|value
condition|)
block|{
name|value
operator|=
name|other
expr_stmt|;
block|}
block|}
name|void
name|setLesser
parameter_list|(
name|int
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|<
name|value
condition|)
block|{
name|value
operator|=
name|other
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End Holder.java
end_comment

end_unit

