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
name|avatica
operator|.
name|util
operator|.
name|PositionedCursor
import|;
end_import

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
comment|/**  * Implementation of {@link org.apache.calcite.avatica.util.Cursor} on top of an  * {@link org.apache.calcite.linq4j.Enumerator} that  * returns an {@link Object} for each row.  */
end_comment

begin_class
specifier|public
class|class
name|ObjectEnumeratorCursor
extends|extends
name|PositionedCursor
argument_list|<
name|Object
argument_list|>
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
name|ObjectGetter
argument_list|(
name|ordinal
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Object
name|current
parameter_list|()
block|{
return|return
name|enumerator
operator|.
name|current
argument_list|()
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
annotation|@
name|Override
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
block|}
end_class

end_unit

