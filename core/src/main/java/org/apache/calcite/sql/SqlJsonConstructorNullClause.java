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
name|sql
package|;
end_package

begin_comment
comment|/**  * Indicating that how do Json constructors handle null  */
end_comment

begin_enum
specifier|public
enum|enum
name|SqlJsonConstructorNullClause
block|{
name|NULL_ON_NULL
argument_list|(
literal|"NULL ON NULL"
argument_list|)
block|,
name|ABSENT_ON_NULL
argument_list|(
literal|"ABSENT ON NULL"
argument_list|)
block|;
specifier|public
specifier|final
name|String
name|sql
decl_stmt|;
name|SqlJsonConstructorNullClause
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
block|}
block|}
end_enum

begin_comment
comment|// End SqlJsonConstructorNullClause.java
end_comment

end_unit

