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
operator|.
name|test
package|;
end_package

begin_comment
comment|/**  * Implementation of {@link SqlTester} that can parse and validate SQL,  * and convert it to relational algebra.  *  *<p>This tester is therefore suitable for many general-purpose tests,  * including SQL parsing, validation, and SQL-to-Rel conversion.  */
end_comment

begin_class
specifier|public
class|class
name|SqlValidatorTester
extends|extends
name|AbstractSqlTester
block|{
comment|/** Default instance of this tester. */
specifier|public
specifier|static
specifier|final
name|SqlValidatorTester
name|DEFAULT
init|=
operator|new
name|SqlValidatorTester
argument_list|()
decl_stmt|;
block|}
end_class

end_unit

