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
operator|.
name|schemata
operator|.
name|lingual
package|;
end_package

begin_comment
comment|/**  * Lingual schema.  */
end_comment

begin_class
specifier|public
class|class
name|LingualSchema
block|{
specifier|public
specifier|final
name|LingualEmp
index|[]
name|EMPS
init|=
block|{
operator|new
name|LingualEmp
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
block|,
operator|new
name|LingualEmp
argument_list|(
literal|2
argument_list|,
literal|30
argument_list|)
block|}
decl_stmt|;
block|}
end_class

end_unit

