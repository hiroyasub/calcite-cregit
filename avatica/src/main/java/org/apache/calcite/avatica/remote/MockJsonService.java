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
name|avatica
operator|.
name|remote
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
name|AvaticaConnection
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Mock implementation of {@link Service}  * that encodes its requests and responses as JSON  * and looks up responses from a pre-defined map.  */
end_comment

begin_class
specifier|public
class|class
name|MockJsonService
extends|extends
name|JsonService
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
decl_stmt|;
specifier|public
name|MockJsonService
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|request
parameter_list|)
block|{
specifier|final
name|String
name|response
init|=
name|map
operator|.
name|get
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No response for "
operator|+
name|request
argument_list|)
throw|;
block|}
return|return
name|response
return|;
block|}
comment|/** Factory that creates a {@code MockJsonService}. */
specifier|public
specifier|static
class|class
name|Factory
implements|implements
name|Service
operator|.
name|Factory
block|{
specifier|public
name|Service
name|create
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map1
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|map1
operator|.
name|put
argument_list|(
literal|"{\"request\":\"getSchemas\",\"catalog\":null,\"schemaPattern\":{\"s\":null}}"
argument_list|,
literal|"{\"response\":\"resultSet\", firstFrame: {offset: 0, done: true, rows: []}}"
argument_list|)
expr_stmt|;
name|map1
operator|.
name|put
argument_list|(
name|JsonService
operator|.
name|encode
argument_list|(
operator|new
name|SchemasRequest
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|,
literal|"{\"response\":\"resultSet\", firstFrame: {offset: 0, done: true, rows: []}}"
argument_list|)
expr_stmt|;
name|map1
operator|.
name|put
argument_list|(
name|JsonService
operator|.
name|encode
argument_list|(
operator|new
name|TablesRequest
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|Arrays
operator|.
expr|<
name|String
operator|>
name|asList
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|"{\"response\":\"resultSet\", firstFrame: {offset: 0, done: true, rows: []}}"
argument_list|)
expr_stmt|;
name|map1
operator|.
name|put
argument_list|(
literal|"{\"request\":\"createStatement\",\"connectionId\":0}"
argument_list|,
literal|"{\"response\":\"createStatement\",\"id\":0}"
argument_list|)
expr_stmt|;
name|map1
operator|.
name|put
argument_list|(
literal|"{\"request\":\"prepareAndExecute\",\"statementId\":0,"
operator|+
literal|"\"sql\":\"select * from (\\n  values (1, 'a'), (null, 'b'), (3, 'c')) as t (c1, c2)\",\"maxRowCount\":-1}"
argument_list|,
literal|"{\"response\":\"resultSet\",\"signature\": {\n"
operator|+
literal|" \"columns\": [\n"
operator|+
literal|"   {\"columnName\": \"C1\", \"type\": {type: \"scalar\", id: 4, rep: \"INTEGER\"}},\n"
operator|+
literal|"   {\"columnName\": \"C2\", \"type\": {type: \"scalar\", id: 12, rep: \"STRING\"}}\n"
operator|+
literal|" ], \"cursorFactory\": {\"style\": \"ARRAY\"}\n"
operator|+
literal|"}, \"rows\": [[1, \"a\"], [null, \"b\"], [3, \"c\"]]}"
argument_list|)
expr_stmt|;
name|map1
operator|.
name|put
argument_list|(
literal|"{\"request\":\"prepare\",\"statementId\":0,"
operator|+
literal|"\"sql\":\"select * from (\\n  values (1, 'a'), (null, 'b'), (3, 'c')) as t (c1, c2)\",\"maxRowCount\":-1}"
argument_list|,
literal|"{\"response\":\"prepare\",\"signature\": {\n"
operator|+
literal|" \"columns\": [\n"
operator|+
literal|"   {\"columnName\": \"C1\", \"type\": {type: \"scalar\", id: 4, rep: \"INTEGER\"}},\n"
operator|+
literal|"   {\"columnName\": \"C2\", \"type\": {type: \"scalar\", id: 12, rep: \"STRING\"}}\n"
operator|+
literal|" ],\n"
operator|+
literal|" \"parameters\": [],\n"
operator|+
literal|" \"cursorFactory\": {\"style\": \"ARRAY\"}\n"
operator|+
literal|"}}"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
return|return
operator|new
name|MockJsonService
argument_list|(
name|map1
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End MockJsonService.java
end_comment

end_unit

