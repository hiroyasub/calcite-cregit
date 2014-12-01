begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|runtime
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|Enumerator
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link org.apache.calcite.avatica.util.Cursor} on top of an  * {@link org.apache.calcite.linq4j.Enumerator} that  * returns a record for each row. The record is a synthetic class whose fields  * are all public.  *  * @param<E> Element type  */
end_comment

begin_class
specifier|public
class|class
name|RecordEnumeratorCursor
parameter_list|<
name|E
parameter_list|>
extends|extends
name|EnumeratorCursor
argument_list|<
name|E
argument_list|>
block|{
specifier|private
specifier|final
name|Class
argument_list|<
name|E
argument_list|>
name|clazz
decl_stmt|;
comment|/**    * Creates a RecordEnumeratorCursor.    *    * @param enumerator Enumerator    * @param clazz Element type    */
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
name|super
argument_list|(
name|enumerator
argument_list|)
expr_stmt|;
name|this
operator|.
name|clazz
operator|=
name|clazz
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
name|FieldGetter
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
block|}
end_class

begin_comment
comment|// End RecordEnumeratorCursor.java
end_comment

end_unit

