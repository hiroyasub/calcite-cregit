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
name|runtime
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Enumerator
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link net.hydromatic.avatica.Cursor} on top of an  * {@link net.hydromatic.linq4j.Enumerator} that  * returns a record for each row. The returned record is cached to avoid  * multiple computations of current row.  * For instance,  * {@link net.hydromatic.optiq.rules.java.JavaRules.EnumerableCalcRel}  * computes result just in {@code current()} method, thus it makes sense to  * cache the result and make it available for all the accesors.  *  * @param<T> Element type  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|EnumeratorCursor
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractCursor
block|{
specifier|private
specifier|final
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
decl_stmt|;
specifier|private
name|T
name|current
decl_stmt|;
comment|/**    * Creates a {@code EnumeratorCursor}    * @param enumerator input enumerator    */
specifier|protected
name|EnumeratorCursor
parameter_list|(
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|)
block|{
name|this
operator|.
name|enumerator
operator|=
name|enumerator
expr_stmt|;
block|}
specifier|public
name|boolean
name|next
parameter_list|()
block|{
if|if
condition|(
name|enumerator
operator|.
name|moveNext
argument_list|()
condition|)
block|{
name|current
operator|=
name|enumerator
operator|.
name|current
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
name|current
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|current
operator|=
literal|null
expr_stmt|;
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns current row.    * @return current row    */
specifier|protected
name|T
name|current
parameter_list|()
block|{
return|return
name|current
return|;
block|}
block|}
end_class

begin_comment
comment|// End EnumeratorCursor.java
end_comment

end_unit

