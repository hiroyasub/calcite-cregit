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
name|metadata
package|;
end_package

begin_comment
comment|/**  * Mapping from an input column of a {@link org.apache.calcite.rel.RelNode} to  * one of its output columns.  */
end_comment

begin_class
specifier|public
class|class
name|RelColumnMapping
block|{
specifier|public
name|RelColumnMapping
parameter_list|(
name|int
name|iOutputColumn
parameter_list|,
name|int
name|iInputRel
parameter_list|,
name|int
name|iInputColumn
parameter_list|,
name|boolean
name|derived
parameter_list|)
block|{
name|this
operator|.
name|iOutputColumn
operator|=
name|iOutputColumn
expr_stmt|;
name|this
operator|.
name|iInputRel
operator|=
name|iInputRel
expr_stmt|;
name|this
operator|.
name|iInputColumn
operator|=
name|iInputColumn
expr_stmt|;
name|this
operator|.
name|derived
operator|=
name|derived
expr_stmt|;
block|}
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * 0-based ordinal of mapped output column.    */
specifier|public
specifier|final
name|int
name|iOutputColumn
decl_stmt|;
comment|/**    * 0-based ordinal of mapped input rel.    */
specifier|public
specifier|final
name|int
name|iInputRel
decl_stmt|;
comment|/**    * 0-based ordinal of mapped column within input rel.    */
specifier|public
specifier|final
name|int
name|iInputColumn
decl_stmt|;
comment|/**    * Whether the column mapping transforms the input.    */
specifier|public
specifier|final
name|boolean
name|derived
decl_stmt|;
block|}
end_class

end_unit

