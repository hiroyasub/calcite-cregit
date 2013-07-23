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
name|materialize
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|Schema
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
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Actor that manages the state of materializations in the system.  */
end_comment

begin_class
class|class
name|MaterializationActor
block|{
comment|// Not an actor yet -- TODO make members private and add request/response
comment|// queues
specifier|final
name|Map
argument_list|<
name|MaterializationKey
argument_list|,
name|Materialization
argument_list|>
name|keyMap
init|=
operator|new
name|HashMap
argument_list|<
name|MaterializationKey
argument_list|,
name|Materialization
argument_list|>
argument_list|()
decl_stmt|;
specifier|static
class|class
name|Materialization
block|{
specifier|final
name|MaterializationKey
name|key
decl_stmt|;
specifier|final
name|Schema
name|rootSchema
decl_stmt|;
name|Schema
operator|.
name|TableInSchema
name|materializedTable
decl_stmt|;
specifier|final
name|String
name|sql
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
comment|/** Creates a materialization.      *      * @param key  Unique identifier of this materialization      * @param materializedTable Table that currently materializes the query.      *                          That is, executing "select * from table" will      *                          give the same results as executing the query.      *                          May be null when the materialization is created;      *                          materialization service will change the value as      * @param sql  Query that is materialized      * @param rowType Row type      */
name|Materialization
parameter_list|(
name|MaterializationKey
name|key
parameter_list|,
name|Schema
name|rootSchema
parameter_list|,
name|Schema
operator|.
name|TableInSchema
name|materializedTable
parameter_list|,
name|String
name|sql
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|rootSchema
operator|=
name|rootSchema
expr_stmt|;
name|this
operator|.
name|materializedTable
operator|=
name|materializedTable
expr_stmt|;
comment|// may be null
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End MaterializationActor.java
end_comment

end_unit

