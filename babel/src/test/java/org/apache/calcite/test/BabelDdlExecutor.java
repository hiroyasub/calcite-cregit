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
name|server
operator|.
name|DdlExecutor
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
name|SqlAbstractParserImpl
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
name|SqlParserImplFactory
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
name|babel
operator|.
name|SqlBabelParserImpl
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/** Executes the few DDL commands supported by  * {@link SqlBabelParserImpl}. */
end_comment

begin_class
specifier|public
class|class
name|BabelDdlExecutor
extends|extends
name|MockDdlExecutor
block|{
specifier|static
specifier|final
name|BabelDdlExecutor
name|INSTANCE
init|=
operator|new
name|BabelDdlExecutor
argument_list|()
decl_stmt|;
comment|/** Parser factory. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
comment|// used via reflection
specifier|public
specifier|static
specifier|final
name|SqlParserImplFactory
name|PARSER_FACTORY
init|=
operator|new
name|SqlParserImplFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SqlAbstractParserImpl
name|getParser
parameter_list|(
name|Reader
name|stream
parameter_list|)
block|{
return|return
name|SqlBabelParserImpl
operator|.
name|FACTORY
operator|.
name|getParser
argument_list|(
name|stream
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|DdlExecutor
name|getDdlExecutor
parameter_list|()
block|{
return|return
name|BabelDdlExecutor
operator|.
name|INSTANCE
return|;
block|}
block|}
decl_stmt|;
block|}
end_class

end_unit

