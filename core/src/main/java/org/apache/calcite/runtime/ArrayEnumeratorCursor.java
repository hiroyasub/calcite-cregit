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
comment|/**  * Implementation of {@link org.apache.calcite.avatica.Cursor} on top of an  * {@link org.apache.calcite.linq4j.Enumerator} that  * returns an array of {@link Object} for each row.  */
end_comment

begin_class
specifier|public
class|class
name|ArrayEnumeratorCursor
extends|extends
name|EnumeratorCursor
argument_list|<
name|Object
index|[]
argument_list|>
block|{
comment|/**    * Creates an ArrayEnumeratorCursor.    *    * @param enumerator Enumerator    */
specifier|public
name|ArrayEnumeratorCursor
parameter_list|(
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerator
parameter_list|)
block|{
name|super
argument_list|(
name|enumerator
argument_list|)
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
name|ArrayEnumeratorGetter
argument_list|(
name|ordinal
argument_list|)
return|;
block|}
comment|/** Implementation of {@link Getter} that reads from records that are    * arrays. */
class|class
name|ArrayEnumeratorGetter
extends|extends
name|AbstractGetter
block|{
specifier|protected
specifier|final
name|int
name|field
decl_stmt|;
specifier|public
name|ArrayEnumeratorGetter
parameter_list|(
name|int
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
init|=
name|current
argument_list|()
index|[
name|field
index|]
decl_stmt|;
name|wasNull
index|[
literal|0
index|]
operator|=
name|o
operator|==
literal|null
expr_stmt|;
return|return
name|o
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End ArrayEnumeratorCursor.java
end_comment

end_unit

