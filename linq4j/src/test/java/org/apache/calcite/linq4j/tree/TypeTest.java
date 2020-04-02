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
name|linq4j
operator|.
name|tree
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
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
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Test for {@link Types#gcd}.  */
end_comment

begin_class
class|class
name|TypeTest
block|{
annotation|@
name|Test
name|void
name|testGcd
parameter_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|char
name|c
init|=
literal|0
decl_stmt|;
name|byte
name|b
init|=
literal|0
decl_stmt|;
name|short
name|s
init|=
literal|0
decl_stmt|;
name|int
name|l
init|=
literal|0
decl_stmt|;
comment|// int to long
name|l
operator|=
name|i
expr_stmt|;
name|assertEquals
argument_list|(
name|long
operator|.
name|class
argument_list|,
name|Types
operator|.
name|gcd
argument_list|(
name|int
operator|.
name|class
argument_list|,
name|long
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// reverse args
name|assertEquals
argument_list|(
name|long
operator|.
name|class
argument_list|,
name|Types
operator|.
name|gcd
argument_list|(
name|long
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// char to int
name|i
operator|=
name|c
expr_stmt|;
name|assertEquals
argument_list|(
name|int
operator|.
name|class
argument_list|,
name|Types
operator|.
name|gcd
argument_list|(
name|char
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// can assign byte to short
name|assertEquals
argument_list|(
name|short
operator|.
name|class
argument_list|,
name|Types
operator|.
name|gcd
argument_list|(
name|byte
operator|.
name|class
argument_list|,
name|short
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|s
operator|=
name|b
expr_stmt|;
comment|// cannot assign byte to char
comment|// cannot assign char to short
comment|// can assign byte and char to int
comment|// fails: c = b;
comment|// fails: s = c;
name|i
operator|=
name|b
expr_stmt|;
name|i
operator|=
name|c
expr_stmt|;
name|assertEquals
argument_list|(
name|int
operator|.
name|class
argument_list|,
name|Types
operator|.
name|gcd
argument_list|(
name|char
operator|.
name|class
argument_list|,
name|byte
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|int
operator|.
name|class
argument_list|,
name|Types
operator|.
name|gcd
argument_list|(
name|byte
operator|.
name|class
argument_list|,
name|char
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// mix a primitive with an object
comment|// (correct answer is java.io.Serializable)
name|assertEquals
argument_list|(
name|Object
operator|.
name|class
argument_list|,
name|Types
operator|.
name|gcd
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|java
operator|.
name|io
operator|.
name|Serializable
name|o
init|=
literal|true
condition|?
literal|"x"
else|:
literal|1
decl_stmt|;
block|}
block|}
end_class

end_unit

