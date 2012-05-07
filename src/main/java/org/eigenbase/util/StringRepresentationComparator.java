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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * StringRepresentationComparator compares two objects by comparing their {@link  * Object#toString()} representations.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|StringRepresentationComparator
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Comparator
argument_list|<
name|T
argument_list|>
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|// implement Comparator
specifier|public
name|int
name|compare
parameter_list|(
name|T
name|o1
parameter_list|,
name|T
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|toString
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|// implement Comparator
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End StringRepresentationComparator.java
end_comment

end_unit

