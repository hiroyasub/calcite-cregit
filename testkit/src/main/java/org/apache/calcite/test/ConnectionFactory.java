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
name|avatica
operator|.
name|ConnectionProperty
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_comment
comment|/**  * Creates JDBC connections for tests.  *  *<p>The base class is abstract, and all of the {@code with} methods throw.  *  *<p>Avoid creating new sub-classes otherwise it would be hard to support  * {@code .with(property, value).with(...)} kind of chains.  *  *<p>If you want augment the connection, use  * {@link CalciteAssert.ConnectionPostProcessor}.  *  * @see ConnectionFactories  */
end_comment

begin_interface
specifier|public
interface|interface
name|ConnectionFactory
block|{
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|SQLException
function_decl|;
specifier|default
name|ConnectionFactory
name|with
parameter_list|(
name|String
name|property
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|default
name|ConnectionFactory
name|with
parameter_list|(
name|ConnectionProperty
name|property
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|default
name|ConnectionFactory
name|with
parameter_list|(
name|CalciteAssert
operator|.
name|ConnectionPostProcessor
name|postProcessor
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_interface

end_unit

