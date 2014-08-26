begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|tpcds
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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|SchemaFactory
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
name|SchemaPlus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Factory that creates a {@link TpcdsSchema}.  *  *<p>Allows a custom schema to be included in a model.json file.</p>  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
class|class
name|TpcdsSchemaFactory
implements|implements
name|SchemaFactory
block|{
comment|// public constructor, per factory contract
specifier|public
name|TpcdsSchemaFactory
parameter_list|()
block|{
block|}
specifier|public
name|Schema
name|create
parameter_list|(
name|SchemaPlus
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
parameter_list|)
block|{
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|operand
decl_stmt|;
name|double
name|scale
init|=
name|Util
operator|.
name|first
argument_list|(
operator|(
name|Double
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"scale"
argument_list|)
argument_list|,
literal|1D
argument_list|)
decl_stmt|;
name|int
name|part
init|=
name|Util
operator|.
name|first
argument_list|(
operator|(
name|Integer
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"part"
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|int
name|partCount
init|=
name|Util
operator|.
name|first
argument_list|(
operator|(
name|Integer
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"partCount"
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|boolean
name|columnPrefix
init|=
name|Util
operator|.
name|first
argument_list|(
operator|(
name|Boolean
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"columnPrefix"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|TpcdsSchema
argument_list|(
name|scale
argument_list|,
name|part
argument_list|,
name|partCount
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End TpcdsSchemaFactory.java
end_comment

end_unit

