begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Management of materialized query results.  *  *<p>An actor ({@link org.apache.calcite.materialize.MaterializationActor})  * maintains the state of all  * materializations in the system and is wrapped in a service  * ({@link org.apache.calcite.materialize.MaterializationService})  * for access from other parts of the system.</p>  *  *<p>Optimizer rules allow Calcite to rewrite queries using materializations,  * if they are valid (that is, contain the same result as executing their  * defining query) and lower cost.  *  *<p>In future, the actor may manage the process of updating materializations,  * instantiating materializations from the intermediate results of queries, and  * recognize what materializations would be useful based on actual query load.  */
end_comment

begin_annotation
annotation|@
name|DefaultQualifier
argument_list|(
name|value
operator|=
name|NonNull
operator|.
name|class
argument_list|,
name|locations
operator|=
name|TypeUseLocation
operator|.
name|FIELD
argument_list|)
end_annotation

begin_annotation
annotation|@
name|DefaultQualifier
argument_list|(
name|value
operator|=
name|NonNull
operator|.
name|class
argument_list|,
name|locations
operator|=
name|TypeUseLocation
operator|.
name|PARAMETER
argument_list|)
end_annotation

begin_annotation
annotation|@
name|DefaultQualifier
argument_list|(
name|value
operator|=
name|NonNull
operator|.
name|class
argument_list|,
name|locations
operator|=
name|TypeUseLocation
operator|.
name|RETURN
argument_list|)
end_annotation

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|materialize
package|;
end_package

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|NonNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|framework
operator|.
name|qual
operator|.
name|DefaultQualifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|framework
operator|.
name|qual
operator|.
name|TypeUseLocation
import|;
end_import

end_unit

