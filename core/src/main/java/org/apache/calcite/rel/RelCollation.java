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
name|rel
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
name|plan
operator|.
name|RelMultipleTrait
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
name|util
operator|.
name|ImmutableIntList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Description of the physical ordering of a relational expression.  *  *<p>An ordering consists of a list of one or more column ordinals and the  * direction of the ordering.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelCollation
extends|extends
name|RelMultipleTrait
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the ordinals and directions of the columns in this ordering.    */
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|getFieldCollations
parameter_list|()
function_decl|;
comment|/**    * Returns the ordinals of the key columns.    */
specifier|default
name|ImmutableIntList
name|getKeys
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|collations
init|=
name|getFieldCollations
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|collations
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|keys
init|=
operator|new
name|int
index|[
name|size
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|keys
index|[
name|i
index|]
operator|=
name|collations
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getFieldIndex
argument_list|()
expr_stmt|;
block|}
return|return
name|ImmutableIntList
operator|.
name|of
argument_list|(
name|keys
argument_list|)
return|;
block|}
block|}
end_interface

end_unit

