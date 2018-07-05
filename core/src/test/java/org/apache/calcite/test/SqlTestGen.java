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
name|SqlCollation
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
name|test
operator|.
name|DefaultSqlTestFactory
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
name|test
operator|.
name|DelegatingSqlTestFactory
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
name|test
operator|.
name|SqlTestFactory
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
name|test
operator|.
name|SqlTester
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
name|test
operator|.
name|SqlTesterImpl
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
name|util
operator|.
name|BarfingInvocationHandler
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
name|util
operator|.
name|Util
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Proxy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Utility to generate a SQL script from validator test.  */
end_comment

begin_class
specifier|public
class|class
name|SqlTestGen
block|{
specifier|private
name|SqlTestGen
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
operator|new
name|SqlTestGen
argument_list|()
operator|.
name|genValidatorTest
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|genValidatorTest
parameter_list|()
block|{
specifier|final
name|File
name|file
init|=
operator|new
name|File
argument_list|(
literal|"validatorTest.sql"
argument_list|)
decl_stmt|;
try|try
init|(
name|PrintWriter
name|pw
init|=
name|Util
operator|.
name|printWriter
argument_list|(
name|file
argument_list|)
init|)
block|{
name|Method
index|[]
name|methods
init|=
name|getJunitMethods
argument_list|(
name|SqlValidatorSpooler
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|Method
name|method
range|:
name|methods
control|)
block|{
specifier|final
name|SqlValidatorSpooler
name|test
init|=
operator|new
name|SqlValidatorSpooler
argument_list|(
name|pw
argument_list|)
decl_stmt|;
specifier|final
name|Object
name|result
init|=
name|method
operator|.
name|invoke
argument_list|(
name|test
argument_list|)
decl_stmt|;
assert|assert
name|result
operator|==
literal|null
assert|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|IllegalAccessException
decl||
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns a list of all of the Junit methods in a given class.    */
specifier|private
specifier|static
name|Method
index|[]
name|getJunitMethods
parameter_list|(
name|Class
argument_list|<
name|SqlValidatorSpooler
argument_list|>
name|clazz
parameter_list|)
block|{
name|List
argument_list|<
name|Method
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Method
name|method
range|:
name|clazz
operator|.
name|getMethods
argument_list|()
control|)
block|{
if|if
condition|(
name|method
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"test"
argument_list|)
operator|&&
name|Modifier
operator|.
name|isPublic
argument_list|(
name|method
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|&&
operator|!
name|Modifier
operator|.
name|isStatic
argument_list|(
name|method
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|&&
operator|(
name|method
operator|.
name|getParameterTypes
argument_list|()
operator|.
name|length
operator|==
literal|0
operator|)
operator|&&
operator|(
name|method
operator|.
name|getReturnType
argument_list|()
operator|==
name|Void
operator|.
name|TYPE
operator|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|Method
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Subversive subclass, which spools results to a writer rather than running    * tests.    */
specifier|private
specifier|static
class|class
name|SqlValidatorSpooler
extends|extends
name|SqlValidatorTest
block|{
specifier|private
specifier|final
name|PrintWriter
name|pw
decl_stmt|;
specifier|private
name|SqlValidatorSpooler
parameter_list|(
name|PrintWriter
name|pw
parameter_list|)
block|{
name|this
operator|.
name|pw
operator|=
name|pw
expr_stmt|;
block|}
specifier|public
name|SqlTester
name|getTester
parameter_list|()
block|{
specifier|final
name|SqlTestFactory
name|factory
init|=
operator|new
name|DelegatingSqlTestFactory
argument_list|(
name|DefaultSqlTestFactory
operator|.
name|INSTANCE
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SqlValidator
name|getValidator
parameter_list|(
name|SqlTestFactory
name|factory
parameter_list|)
block|{
return|return
operator|(
name|SqlValidator
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|SqlValidatorSpooler
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|SqlValidator
operator|.
name|class
block|}
argument_list|,
operator|new
name|MyInvocationHandler
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|SqlTesterImpl
argument_list|(
name|factory
argument_list|)
block|{
specifier|public
name|void
name|assertExceptionIsThrown
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
block|{
if|if
condition|(
name|expectedMsgPattern
operator|==
literal|null
condition|)
block|{
comment|// This SQL statement is supposed to succeed.
comment|// Generate it to the file, so we can see what
comment|// output it produces.
name|pw
operator|.
name|println
argument_list|(
literal|"-- "
comment|/* + getName() */
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
name|sql
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Do nothing. We know that this fails the validator
comment|// test, so we don't learn anything by having it fail
comment|// from SQL.
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkColumnType
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkResultType
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
block|}
specifier|public
name|void
name|checkType
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
comment|// We could generate the SQL -- or maybe describe -- but
comment|// ignore it for now.
block|}
specifier|public
name|void
name|checkCollation
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|expectedCollationName
parameter_list|,
name|SqlCollation
operator|.
name|Coercibility
name|expectedCoercibility
parameter_list|)
block|{
comment|// We could generate the SQL -- or maybe describe -- but
comment|// ignore it for now.
block|}
specifier|public
name|void
name|checkCharset
parameter_list|(
name|String
name|expression
parameter_list|,
name|Charset
name|expectedCharset
parameter_list|)
block|{
comment|// We could generate the SQL -- or maybe describe -- but
comment|// ignore it for now.
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkIntervalConv
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkRewrite
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|String
name|query
parameter_list|,
name|String
name|expectedRewrite
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkFieldOrigin
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|fieldOriginList
parameter_list|)
block|{
block|}
block|}
return|;
block|}
comment|/**      * Handles the methods in      * {@link org.apache.calcite.sql.validate.SqlValidator} that are called      * from validator tests.      */
specifier|public
specifier|static
class|class
name|MyInvocationHandler
extends|extends
name|BarfingInvocationHandler
block|{
specifier|public
name|void
name|setIdentifierExpansion
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setColumnReferenceExpansion
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setCallRewrite
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
block|}
specifier|public
name|boolean
name|shouldExpandIdentifiers
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlTestGen.java
end_comment

end_unit

