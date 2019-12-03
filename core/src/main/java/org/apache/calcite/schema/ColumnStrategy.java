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
name|schema
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
name|RelOptTable
import|;
end_import

begin_comment
comment|/** Describes how a column gets populated.  *  * @see org.apache.calcite.sql2rel.InitializerExpressionFactory#generationStrategy  * @see RelOptTable#getColumnStrategies()  */
end_comment

begin_enum
specifier|public
enum|enum
name|ColumnStrategy
block|{
comment|/** Column does not have a default value, but does allow null values.    * If you don't specify it in an INSERT, it will get a NULL value. */
name|NULLABLE
block|,
comment|/** Column does not have a default value, and does not allow nulls.    * You must specify it in an INSERT. */
name|NOT_NULLABLE
block|,
comment|/** Column has a default value.    * If you don't specify it in an INSERT, it will get a NULL value. */
name|DEFAULT
block|,
comment|/** Column is computed and stored. You cannot insert into it. */
name|STORED
block|,
comment|/** Column is computed and not stored. You cannot insert into it. */
name|VIRTUAL
block|;
comment|/**    * Returns whether you can insert into the column.    *    * @return true if this column can be inserted    */
specifier|public
name|boolean
name|canInsertInto
parameter_list|()
block|{
return|return
name|this
operator|!=
name|STORED
operator|&&
name|this
operator|!=
name|VIRTUAL
return|;
block|}
block|}
end_enum

end_unit

