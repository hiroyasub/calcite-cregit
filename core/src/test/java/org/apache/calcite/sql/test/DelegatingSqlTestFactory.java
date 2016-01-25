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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
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
name|apache
operator|.
name|calcite
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
name|apache
operator|.
name|calcite
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
name|apache
operator|.
name|calcite
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
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|validate
operator|.
name|SqlValidatorWithHints
import|;
end_import

begin_comment
comment|/** * Implementation of {@link SqlTestFactory} that delegates  * everything to an underlying factory.  *  *<p>Generally a chain starts with a  * {@link org.apache.calcite.sql.test.DefaultSqlTestFactory}, and continues with  * a succession of objects that derive from {@code DelegatingSqlTestFactory} and  * override one method.</p>  *  *<p>Methods such as  * {@link org.apache.calcite.sql.test.SqlTester#withConformance} help create  * such chains.</p> */
end_comment

begin_class
specifier|public
class|class
name|DelegatingSqlTestFactory
implements|implements
name|SqlTestFactory
block|{
specifier|private
specifier|final
name|SqlTestFactory
name|factory
decl_stmt|;
specifier|public
name|DelegatingSqlTestFactory
parameter_list|(
name|SqlTestFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
specifier|public
name|Object
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|factory
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|SqlOperatorTable
name|createOperatorTable
parameter_list|(
name|SqlTestFactory
name|factory
parameter_list|)
block|{
return|return
name|this
operator|.
name|factory
operator|.
name|createOperatorTable
argument_list|(
name|factory
argument_list|)
return|;
block|}
specifier|public
name|SqlAdvisor
name|createAdvisor
parameter_list|(
name|SqlValidatorWithHints
name|validator
parameter_list|)
block|{
return|return
name|factory
operator|.
name|createAdvisor
argument_list|(
name|validator
argument_list|)
return|;
block|}
specifier|public
name|SqlValidator
name|getValidator
parameter_list|(
name|SqlTestFactory
name|factory
parameter_list|)
block|{
return|return
name|this
operator|.
name|factory
operator|.
name|getValidator
argument_list|(
name|factory
argument_list|)
return|;
block|}
specifier|public
name|SqlParser
name|createParser
parameter_list|(
name|SqlTestFactory
name|factory
parameter_list|,
name|String
name|sql
parameter_list|)
block|{
return|return
name|this
operator|.
name|factory
operator|.
name|createParser
argument_list|(
name|factory
argument_list|,
name|sql
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End DelegatingSqlTestFactory.java
end_comment

end_unit

