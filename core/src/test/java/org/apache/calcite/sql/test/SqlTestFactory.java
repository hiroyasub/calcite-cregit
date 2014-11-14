begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|test
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|SqlOperatorTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|advise
operator|.
name|SqlAdvisor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|parser
operator|.
name|SqlParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|validate
operator|.
name|SqlValidator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|validate
operator|.
name|SqlValidatorWithHints
import|;
end_import

begin_comment
comment|/** * Creates the objects needed to run a SQL parsing or validation test.  *  * @see org.eigenbase.sql.test.SqlTester */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlTestFactory
block|{
name|SqlOperatorTable
name|createOperatorTable
parameter_list|()
function_decl|;
name|SqlParser
name|createParser
parameter_list|(
name|SqlTestFactory
name|factory
parameter_list|,
name|String
name|sql
parameter_list|)
function_decl|;
name|SqlValidator
name|getValidator
parameter_list|(
name|SqlTestFactory
name|factory
parameter_list|)
function_decl|;
name|SqlAdvisor
name|createAdvisor
parameter_list|(
name|SqlValidatorWithHints
name|validator
parameter_list|)
function_decl|;
name|Object
name|get
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlTestFactory.java
end_comment

end_unit

