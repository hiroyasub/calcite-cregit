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
name|test
package|;
end_package

begin_comment
comment|/** Information necessary to create a JDBC connection.  *  *<p>Specify one to run tests against a different database. */
end_comment

begin_class
specifier|public
class|class
name|ConnectionSpec
block|{
specifier|public
specifier|final
name|String
name|url
decl_stmt|;
specifier|public
specifier|final
name|String
name|username
decl_stmt|;
specifier|public
specifier|final
name|String
name|password
decl_stmt|;
specifier|public
specifier|final
name|String
name|driver
decl_stmt|;
specifier|public
name|ConnectionSpec
parameter_list|(
name|String
name|url
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|driver
parameter_list|)
block|{
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
name|this
operator|.
name|username
operator|=
name|username
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
name|this
operator|.
name|driver
operator|=
name|driver
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End ConnectionSpec.java
end_comment

end_unit

