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
name|*
import|;
end_import

begin_comment
comment|/**  * Abstract base class for implementing {@link Table}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractTable
implements|implements
name|Table
block|{
specifier|protected
name|AbstractTable
parameter_list|()
block|{
block|}
comment|// Default implementation. Override if you have statistics.
specifier|public
name|Statistic
name|getStatistic
parameter_list|()
block|{
return|return
name|Statistics
operator|.
name|UNKNOWN
return|;
block|}
specifier|public
name|Schema
operator|.
name|TableType
name|getJdbcTableType
parameter_list|()
block|{
return|return
name|Schema
operator|.
name|TableType
operator|.
name|TABLE
return|;
block|}
block|}
end_class

begin_comment
comment|// End AbstractTable.java
end_comment

end_unit

