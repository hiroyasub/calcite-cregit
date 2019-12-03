begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** Equivalent to  * {@link org.apache.calcite.examples.foodmart.java.JdbcExample.Hr}, but  * belongs to the unnamed (root) package. */
end_comment

begin_class
specifier|public
class|class
name|RootHr
block|{
specifier|public
specifier|final
name|RootEmployee
index|[]
name|emps
init|=
block|{
operator|new
name|RootEmployee
argument_list|(
literal|100
argument_list|,
literal|"Bill"
argument_list|)
block|,
operator|new
name|RootEmployee
argument_list|(
literal|200
argument_list|,
literal|"Eric"
argument_list|)
block|,
operator|new
name|RootEmployee
argument_list|(
literal|150
argument_list|,
literal|"Sebastian"
argument_list|)
block|,   }
decl_stmt|;
block|}
end_class

end_unit

