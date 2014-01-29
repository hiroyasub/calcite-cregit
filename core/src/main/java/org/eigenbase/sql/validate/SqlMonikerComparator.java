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
name|sql
operator|.
name|validate
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
comment|/**  * A general-purpose implementation of {@link Comparator} to compare {@link  * SqlMoniker} values.  */
end_comment

begin_class
specifier|public
class|class
name|SqlMonikerComparator
implements|implements
name|Comparator
argument_list|<
name|SqlMoniker
argument_list|>
block|{
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|int
name|compare
parameter_list|(
name|SqlMoniker
name|m1
parameter_list|,
name|SqlMoniker
name|m2
parameter_list|)
block|{
if|if
condition|(
name|m1
operator|.
name|getType
argument_list|()
operator|.
name|ordinal
argument_list|()
operator|>
name|m2
operator|.
name|getType
argument_list|()
operator|.
name|ordinal
argument_list|()
condition|)
block|{
return|return
literal|1
return|;
block|}
if|else if
condition|(
name|m1
operator|.
name|getType
argument_list|()
operator|.
name|ordinal
argument_list|()
operator|<
name|m2
operator|.
name|getType
argument_list|()
operator|.
name|ordinal
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
name|m1
operator|.
name|toString
argument_list|()
operator|.
name|compareTo
argument_list|(
name|m2
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlMonikerComparator.java
end_comment

end_unit

