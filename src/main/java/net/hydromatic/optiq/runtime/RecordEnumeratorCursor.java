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

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link net.hydromatic.optiq.runtime.Cursor} on top of an  * {@link net.hydromatic.linq4j.Enumerator} that  * returns a record for each row. The record is a synthetic class whose fields  * are all public.  *  * @author jhyde  */
end_comment

begin_class
specifier|public
class|class
name|RecordEnumeratorCursor
parameter_list|<
name|E
parameter_list|>
extends|extends
name|AbstractCursor
block|{
specifier|private
specifier|final
name|Enumerator
argument_list|<
name|E
argument_list|>
name|enumerator
decl_stmt|;
specifier|private
specifier|final
name|Class
argument_list|<
name|E
argument_list|>
name|clazz
decl_stmt|;
comment|/**      * Creates a RecordEnumeratorCursor.      *      * @param enumerator Enumerator      * @param clazz Element type      */
specifier|public
name|RecordEnumeratorCursor
parameter_list|(
name|Enumerator
argument_list|<
name|E
argument_list|>
name|enumerator
parameter_list|,
name|Class
argument_list|<
name|E
argument_list|>
name|clazz
parameter_list|)
block|{
name|this
operator|.
name|enumerator
operator|=
name|enumerator
expr_stmt|;
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
block|}
annotation|@
name|Override
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
name|RecordEnumeratorGetter
argument_list|(
name|clazz
operator|.
name|getFields
argument_list|()
index|[
name|ordinal
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
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
class|class
name|RecordEnumeratorGetter
implements|implements
name|Getter
block|{
specifier|protected
specifier|final
name|Field
name|field
decl_stmt|;
specifier|public
name|RecordEnumeratorGetter
parameter_list|(
name|Field
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
specifier|public
name|Object
name|getObject
parameter_list|()
block|{
name|Object
name|o
decl_stmt|;
try|try
block|{
name|o
operator|=
name|field
operator|.
name|get
argument_list|(
name|enumerator
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
comment|// End RecordEnumeratorCursor.java
end_comment

end_unit

