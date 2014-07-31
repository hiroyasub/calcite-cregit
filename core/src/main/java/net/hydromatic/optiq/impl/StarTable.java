begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|Table
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|RelOptUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataTypeFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|validate
operator|.
name|SqlValidatorUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|ImmutableIntList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Pair
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_comment
comment|/**  * Virtual table that is composed of two or more tables joined together.  *  *<p>Star tables do not occur in end-user queries. They are introduced by the  * optimizer to help matching queries to materializations, and used only  * during the planning process.</p>  *  *<p>When a materialization is defined, if it involves a join, it is converted  * to a query on top of a star table. Queries that are candidates to map onto  * the materialization are mapped onto the same star table.</p>  */
end_comment

begin_class
specifier|public
class|class
name|StarTable
extends|extends
name|AbstractTable
block|{
comment|// TODO: we'll also need a list of join conditions between tables. For now
comment|//  we assume that join conditions match
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|Table
argument_list|>
name|tables
decl_stmt|;
comment|/** Number of fields in each table's row type. */
specifier|public
name|ImmutableIntList
name|fieldCounts
decl_stmt|;
comment|/** Creates a StarTable. */
specifier|public
name|StarTable
parameter_list|(
name|List
argument_list|<
name|Table
argument_list|>
name|tables
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|tables
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|tables
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a StarTable and registers it in a schema. */
specifier|public
specifier|static
name|StarTable
name|of
parameter_list|(
name|List
argument_list|<
name|Table
argument_list|>
name|tables
parameter_list|)
block|{
return|return
operator|new
name|StarTable
argument_list|(
name|tables
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|typeList
init|=
operator|new
name|ArrayList
argument_list|<
name|RelDataType
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|nameList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|fieldCounts
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Table
name|table
range|:
name|tables
control|)
block|{
specifier|final
name|RelDataType
name|rowType
init|=
name|table
operator|.
name|getRowType
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
name|typeList
operator|.
name|addAll
argument_list|(
name|RelOptUtil
operator|.
name|getFieldTypeList
argument_list|(
name|rowType
argument_list|)
argument_list|)
expr_stmt|;
name|nameList
operator|.
name|addAll
argument_list|(
name|rowType
operator|.
name|getFieldNames
argument_list|()
argument_list|)
expr_stmt|;
name|fieldCounts
operator|.
name|add
argument_list|(
name|rowType
operator|.
name|getFieldCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Compute fieldCounts the first time this method is called. Safe to assume
comment|// that the field counts will be the same whichever type factory is used.
if|if
condition|(
name|this
operator|.
name|fieldCounts
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|fieldCounts
operator|=
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
name|fieldCounts
argument_list|)
expr_stmt|;
block|}
return|return
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|typeList
argument_list|,
name|SqlValidatorUtil
operator|.
name|uniquify
argument_list|(
name|nameList
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|StarTable
name|add
parameter_list|(
name|Table
name|table
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Table
argument_list|>
name|tables1
init|=
operator|new
name|ArrayList
argument_list|<
name|Table
argument_list|>
argument_list|(
name|tables
argument_list|)
decl_stmt|;
name|tables1
operator|.
name|add
argument_list|(
name|table
argument_list|)
expr_stmt|;
return|return
name|of
argument_list|(
name|tables1
argument_list|)
return|;
block|}
comment|/** Returns the column offset of the first column of {@code table} in this    * star table's output row type.    *    * @param table Table    * @return Column offset    * @throws IllegalArgumentException if table is not in this star    */
specifier|public
name|int
name|columnOffset
parameter_list|(
name|Table
name|table
parameter_list|)
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|Table
argument_list|,
name|Integer
argument_list|>
name|pair
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|tables
argument_list|,
name|fieldCounts
argument_list|)
control|)
block|{
if|if
condition|(
name|pair
operator|.
name|left
operator|==
name|table
condition|)
block|{
return|return
name|n
return|;
block|}
name|n
operator|+=
name|pair
operator|.
name|right
expr_stmt|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"star table "
operator|+
name|this
operator|+
literal|" does not contain table "
operator|+
name|table
argument_list|)
throw|;
block|}
block|}
end_class

begin_comment
comment|// End StarTable.java
end_comment

end_unit

