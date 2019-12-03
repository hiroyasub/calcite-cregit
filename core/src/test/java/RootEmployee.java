begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** Equivalent to  * {@link org.apache.calcite.examples.foodmart.java.JdbcExample.Employee}, but  * belongs to the unnamed (root) package. */
end_comment

begin_class
specifier|public
class|class
name|RootEmployee
block|{
specifier|public
specifier|final
name|int
name|empid
decl_stmt|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
comment|/** Creates a RootEmployee. */
specifier|public
name|RootEmployee
parameter_list|(
name|int
name|empid
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|empid
operator|=
name|empid
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
block|}
end_class

end_unit

