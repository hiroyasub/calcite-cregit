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
name|sql
operator|.
name|type
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|ImmutableList
import|;
end_import

begin_comment
comment|/**  * Measure SQL type.  *  *<p>The type serves as a tag that the measure must be expanded  * into an expression before use.  */
end_comment

begin_class
specifier|public
class|class
name|MeasureSqlType
extends|extends
name|ApplySqlType
block|{
comment|/** Private constructor. */
specifier|private
name|MeasureSqlType
parameter_list|(
name|RelDataType
name|elementType
parameter_list|,
name|boolean
name|isNullable
parameter_list|)
block|{
name|super
argument_list|(
name|SqlTypeName
operator|.
name|MEASURE
argument_list|,
name|isNullable
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|elementType
argument_list|)
argument_list|)
expr_stmt|;
name|computeDigest
argument_list|()
expr_stmt|;
block|}
comment|/** Creates a MeasureSqlType. */
specifier|static
name|MeasureSqlType
name|create
parameter_list|(
name|RelDataType
name|elementType
parameter_list|)
block|{
return|return
operator|new
name|MeasureSqlType
argument_list|(
name|elementType
argument_list|,
name|elementType
operator|.
name|isNullable
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

