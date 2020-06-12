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
name|innodb
package|;
end_package

begin_comment
comment|/**  * Query type of a push down condition in InnoDB data source.  */
end_comment

begin_enum
specifier|public
enum|enum
name|QueryType
block|{
comment|/** Primary key point query. */
name|PK_POINT_QUERY
argument_list|(
literal|0
argument_list|)
block|,
comment|/** Secondary key point query. */
name|SK_POINT_QUERY
argument_list|(
literal|1
argument_list|)
block|,
comment|/** Primary key range query with lower and upper bound. */
name|PK_RANGE_QUERY
argument_list|(
literal|2
argument_list|)
block|,
comment|/** Secondary key range query with lower and upper bound. */
name|SK_RANGE_QUERY
argument_list|(
literal|3
argument_list|)
block|,
comment|/** Scanning table fully with primary key. */
name|PK_FULL_SCAN
argument_list|(
literal|4
argument_list|)
block|,
comment|/** Scanning table fully with secondary key. */
name|SK_FULL_SCAN
argument_list|(
literal|5
argument_list|)
block|;
specifier|private
specifier|final
name|int
name|priority
decl_stmt|;
specifier|static
name|QueryType
name|getPointQuery
parameter_list|(
name|boolean
name|isSk
parameter_list|)
block|{
return|return
name|isSk
condition|?
name|SK_POINT_QUERY
else|:
name|PK_POINT_QUERY
return|;
block|}
specifier|static
name|QueryType
name|getRangeQuery
parameter_list|(
name|boolean
name|isSk
parameter_list|)
block|{
return|return
name|isSk
condition|?
name|SK_RANGE_QUERY
else|:
name|PK_RANGE_QUERY
return|;
block|}
name|QueryType
parameter_list|(
name|int
name|priority
parameter_list|)
block|{
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
name|int
name|priority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
block|}
end_enum

end_unit

