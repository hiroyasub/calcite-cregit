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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Internal util methods for ElasticSearch tests  */
end_comment

begin_class
specifier|public
class|class
name|ElasticChecker
block|{
specifier|private
name|ElasticChecker
parameter_list|()
block|{
block|}
comment|/** Returns a function that checks that a particular Elasticsearch pipeline is    * generated to implement a query. */
specifier|public
specifier|static
name|Function
argument_list|<
name|List
argument_list|,
name|Void
argument_list|>
name|elasticsearchChecker
parameter_list|(
specifier|final
name|String
modifier|...
name|strings
parameter_list|)
block|{
return|return
operator|new
name|Function
argument_list|<
name|List
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|Void
name|apply
parameter_list|(
annotation|@
name|Nullable
name|List
name|actual
parameter_list|)
block|{
name|Object
index|[]
name|actualArray
init|=
name|actual
operator|==
literal|null
operator|||
name|actual
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
operator|(
operator|(
name|List
operator|)
name|actual
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|CalciteAssert
operator|.
name|assertArrayEqual
argument_list|(
literal|"expected Elasticsearch query not found"
argument_list|,
name|strings
argument_list|,
name|actualArray
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End ElasticChecker.java
end_comment

end_unit

