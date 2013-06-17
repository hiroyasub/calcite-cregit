begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
comment|/**  * Implementation of {@link Cursor} on top of an  * {@link net.hydromatic.linq4j.Enumerator} that  * returns an {@link Object} for each row.  */
end_comment

begin_class
specifier|public
class|class
name|ObjectEnumeratorCursor
extends|extends
name|AbstractCursor
block|{
specifier|private
specifier|final
name|Enumerator
argument_list|<
name|Object
argument_list|>
name|enumerator
decl_stmt|;
comment|/**    * Creates an ObjectEnumeratorCursor.    *    * @param enumerator Enumerator    */
specifier|public
name|ObjectEnumeratorCursor
parameter_list|(
name|Enumerator
argument_list|<
name|Object
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
specifier|protected
name|Getter
name|createGetter
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
operator|new
name|ObjectEnumeratorGetter
argument_list|(
name|ordinal
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|next
parameter_list|()
block|{
return|return
name|enumerator
operator|.
name|moveNext
argument_list|()
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
class|class
name|ObjectEnumeratorGetter
implements|implements
name|Getter
block|{
specifier|public
name|ObjectEnumeratorGetter
parameter_list|(
name|int
name|field
parameter_list|)
block|{
assert|assert
name|field
operator|==
literal|0
assert|;
block|}
specifier|public
name|Object
name|getObject
parameter_list|()
block|{
name|Object
name|o
init|=
name|enumerator
operator|.
name|current
argument_list|()
decl_stmt|;
name|wasNull
index|[
literal|0
index|]
operator|=
operator|(
name|o
operator|==
literal|null
operator|)
expr_stmt|;
return|return
name|o
return|;
block|}
specifier|public
name|boolean
name|wasNull
parameter_list|()
block|{
return|return
name|wasNull
index|[
literal|0
index|]
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End ObjectEnumeratorCursor.java
end_comment

end_unit

