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
name|rel
operator|.
name|rules
operator|.
name|materialize
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
name|plan
operator|.
name|RelOptRule
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
name|rel
operator|.
name|core
operator|.
name|Aggregate
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
name|rel
operator|.
name|core
operator|.
name|Filter
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
name|rel
operator|.
name|core
operator|.
name|Join
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
name|rel
operator|.
name|core
operator|.
name|Project
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
name|rel
operator|.
name|core
operator|.
name|TableScan
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
name|rel
operator|.
name|rules
operator|.
name|MaterializedViewFilterScanRule
import|;
end_import

begin_comment
comment|/**  * Collection of rules pertaining to materialized views.  *  *<p>Also may contain utilities for {@link MaterializedViewRule}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MaterializedViewRules
block|{
specifier|private
name|MaterializedViewRules
parameter_list|()
block|{
block|}
comment|/** Rule that matches {@link Project} on {@link Aggregate}. */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|PROJECT_AGGREGATE
init|=
name|MaterializedViewProjectAggregateRule
operator|.
name|INSTANCE
decl_stmt|;
comment|/** Rule that matches {@link Aggregate}. */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|AGGREGATE
init|=
name|MaterializedViewOnlyAggregateRule
operator|.
name|INSTANCE
decl_stmt|;
comment|/** Rule that matches {@link Filter}. */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|FILTER
init|=
name|MaterializedViewOnlyFilterRule
operator|.
name|INSTANCE
decl_stmt|;
comment|/** Rule that matches {@link Join}. */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|JOIN
init|=
name|MaterializedViewOnlyJoinRule
operator|.
name|INSTANCE
decl_stmt|;
comment|/** Rule that matches {@link Project} on {@link Filter}. */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|PROJECT_FILTER
init|=
name|MaterializedViewProjectFilterRule
operator|.
name|INSTANCE
decl_stmt|;
comment|/** Rule that matches {@link Project} on {@link Join}. */
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|PROJECT_JOIN
init|=
name|MaterializedViewProjectJoinRule
operator|.
name|INSTANCE
decl_stmt|;
comment|/** Rule that converts a {@link Filter} on a {@link TableScan}    * to a {@link Filter} on a Materialized View. */
specifier|public
specifier|static
specifier|final
name|MaterializedViewFilterScanRule
name|FILTER_SCAN
init|=
name|MaterializedViewFilterScanRule
operator|.
name|INSTANCE
decl_stmt|;
block|}
end_class

end_unit

