begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|mongodb
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|Expression
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|TableInSchemaImpl
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|java
operator|.
name|MapSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataTypeFactory
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
name|type
operator|.
name|SqlTypeName
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DB
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoClient
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Schema mapped onto a directory of MONGO files. Each table in the schema  * is a MONGO file in that directory.  */
end_comment

begin_class
specifier|public
class|class
name|MongoSchema
extends|extends
name|MapSchema
block|{
specifier|final
name|DB
name|mongoDb
decl_stmt|;
comment|/**    * Creates a MONGO schema.    *    * @param parentSchema Parent schema    * @param host Mongo host, e.g. "localhost"    * @param database Mongo database name, e.g. "foodmart"    */
specifier|public
name|MongoSchema
parameter_list|(
name|Schema
name|parentSchema
parameter_list|,
name|String
name|host
parameter_list|,
name|String
name|database
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
name|super
argument_list|(
name|parentSchema
argument_list|,
name|expression
argument_list|)
expr_stmt|;
try|try
block|{
name|MongoClient
name|mongo
init|=
operator|new
name|MongoClient
argument_list|(
name|host
argument_list|)
decl_stmt|;
name|this
operator|.
name|mongoDb
operator|=
name|mongo
operator|.
name|getDB
argument_list|(
name|database
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
block|}
annotation|@
name|Override
specifier|protected
name|Collection
argument_list|<
name|TableInSchema
argument_list|>
name|initialTables
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|TableInSchema
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|TableInSchema
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|mapType
init|=
name|typeFactory
operator|.
name|createMapType
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
argument_list|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|typeFactory
operator|.
name|createStructType
argument_list|(
operator|new
name|RelDataTypeFactory
operator|.
name|FieldInfoBuilder
argument_list|()
operator|.
name|add
argument_list|(
literal|"_MAP"
argument_list|,
name|mapType
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|collection
range|:
name|mongoDb
operator|.
name|getCollectionNames
argument_list|()
control|)
block|{
specifier|final
name|MongoTable
name|table
init|=
operator|new
name|MongoTable
argument_list|(
name|this
argument_list|,
name|collection
argument_list|,
name|rowType
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
operator|new
name|TableInSchemaImpl
argument_list|(
name|this
argument_list|,
name|collection
argument_list|,
name|TableType
operator|.
name|TABLE
argument_list|,
name|table
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
block|}
end_class

begin_comment
comment|// End MongoSchema.java
end_comment

end_unit

