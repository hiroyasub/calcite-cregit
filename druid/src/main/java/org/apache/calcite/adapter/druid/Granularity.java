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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * A strategy by which Druid rolls up rows into sub-totals based on their  * timestamp values.  *  *<p>Typical granularities are based upon time units (e.g. 1 day or  * 15 minutes). A special granularity, all, combines all rows into a single  * total.  *  *<p>A Granularity instance is immutable, and generates a JSON string as  * part of a Druid query.  *  * @see Granularities  */
end_comment

begin_interface
specifier|public
interface|interface
name|Granularity
extends|extends
name|DruidJson
block|{
comment|/** Type of supported periods for granularity. */
enum|enum
name|Type
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
block|;
comment|/** Lower-case name, e.g. "all", "minute". */
specifier|public
specifier|final
name|String
name|lowerName
init|=
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
block|}
name|Type
name|getType
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

