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
name|adapter
operator|.
name|splunk
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
name|adapter
operator|.
name|splunk
operator|.
name|search
operator|.
name|SplunkConnection
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
name|schema
operator|.
name|Table
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
name|schema
operator|.
name|impl
operator|.
name|AbstractSchema
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
comment|/**  * Splunk schema.  */
end_comment

begin_class
specifier|public
class|class
name|SplunkSchema
extends|extends
name|AbstractSchema
block|{
comment|/** The name of the one and only table. */
specifier|public
specifier|static
specifier|final
name|String
name|SPLUNK_TABLE_NAME
init|=
literal|"splunk"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|TABLE_MAP
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|SPLUNK_TABLE_NAME
argument_list|,
name|SplunkTable
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
specifier|public
specifier|final
name|SplunkConnection
name|splunkConnection
decl_stmt|;
comment|/** Creates a SplunkSchema. */
specifier|public
name|SplunkSchema
parameter_list|(
name|SplunkConnection
name|splunkConnection
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|splunkConnection
operator|=
name|splunkConnection
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|getTableMap
parameter_list|()
block|{
return|return
name|TABLE_MAP
return|;
block|}
block|}
end_class

end_unit

