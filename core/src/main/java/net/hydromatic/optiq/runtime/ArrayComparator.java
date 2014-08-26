begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Ordering
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/**  * Compares arrays.  */
end_comment

begin_class
specifier|public
class|class
name|ArrayComparator
implements|implements
name|Comparator
argument_list|<
name|Object
index|[]
argument_list|>
block|{
specifier|private
specifier|final
name|Comparator
index|[]
name|comparators
decl_stmt|;
specifier|public
name|ArrayComparator
parameter_list|(
name|Comparator
modifier|...
name|comparators
parameter_list|)
block|{
name|this
operator|.
name|comparators
operator|=
name|comparators
expr_stmt|;
block|}
specifier|public
name|ArrayComparator
parameter_list|(
name|boolean
modifier|...
name|descendings
parameter_list|)
block|{
name|this
operator|.
name|comparators
operator|=
name|comparators
argument_list|(
name|descendings
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Comparator
index|[]
name|comparators
parameter_list|(
name|boolean
index|[]
name|descendings
parameter_list|)
block|{
name|Comparator
index|[]
name|comparators
init|=
operator|new
name|Comparator
index|[
name|descendings
operator|.
name|length
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
name|descendings
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|descending
init|=
name|descendings
index|[
name|i
index|]
decl_stmt|;
name|comparators
index|[
name|i
index|]
operator|=
name|descending
condition|?
name|Collections
operator|.
name|reverseOrder
argument_list|()
else|:
name|Ordering
operator|.
name|natural
argument_list|()
expr_stmt|;
block|}
return|return
name|comparators
return|;
block|}
specifier|public
name|int
name|compare
parameter_list|(
name|Object
index|[]
name|o1
parameter_list|,
name|Object
index|[]
name|o2
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|comparators
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Comparator
name|comparator
init|=
name|comparators
index|[
name|i
index|]
decl_stmt|;
name|int
name|c
init|=
name|comparator
operator|.
name|compare
argument_list|(
name|o1
index|[
name|i
index|]
argument_list|,
name|o2
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
block|}
end_class

begin_comment
comment|// End ArrayComparator.java
end_comment

end_unit

