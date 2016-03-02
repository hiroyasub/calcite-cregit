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
name|avatica
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Tests for the UnsynchronizedBuffer.  */
end_comment

begin_class
specifier|public
class|class
name|UnsynchronizedBufferTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testArrayResizing
parameter_list|()
block|{
name|int
name|size
init|=
literal|64
decl_stmt|;
name|int
name|expected
init|=
literal|128
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
comment|// We keep being one byte short to contain this message
name|int
name|next
init|=
name|UnsynchronizedBuffer
operator|.
name|nextArraySize
argument_list|(
name|size
operator|+
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|next
argument_list|)
expr_stmt|;
name|size
operator|=
name|next
expr_stmt|;
name|expected
operator|*=
literal|2
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End UnsynchronizedBufferTest.java
end_comment

end_unit

