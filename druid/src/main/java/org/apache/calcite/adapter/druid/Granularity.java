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
name|druid
package|;
end_package

begin_comment
comment|/** Granularity of a Druid query. */
end_comment

begin_enum
specifier|public
enum|enum
name|Granularity
block|{
name|ALL
block|,
name|YEAR
block|,
name|QUARTER
block|,
name|MONTH
block|,
name|WEEK
block|,
name|DAY
block|,
name|HOUR
block|,
name|MINUTE
block|,
name|SECOND
block|,
name|NONE
block|;
comment|/** JSON attribute value in a Druid query. */
specifier|public
specifier|final
name|String
name|value
init|=
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
block|}
end_enum

begin_comment
comment|// End Granularity.java
end_comment

end_unit

