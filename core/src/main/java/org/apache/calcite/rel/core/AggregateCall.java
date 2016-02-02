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
name|core
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
name|RelNode
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
name|type
operator|.
name|RelDataType
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
name|type
operator|.
name|RelDataTypeFactory
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
name|sql
operator|.
name|SqlAggFunction
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
name|sql
operator|.
name|type
operator|.
name|SqlTypeUtil
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
name|util
operator|.
name|mapping
operator|.
name|Mapping
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
name|util
operator|.
name|mapping
operator|.
name|Mappings
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
name|base
operator|.
name|Preconditions
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Call to an aggFunction function within an  * {@link org.apache.calcite.rel.logical.LogicalAggregate}.  */
end_comment

begin_class
specifier|public
class|class
name|AggregateCall
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlAggFunction
name|aggFunction
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|distinct
decl_stmt|;
specifier|public
specifier|final
name|RelDataType
name|type
decl_stmt|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
comment|// We considered using ImmutableIntList but we would not save much memory:
comment|// since all values are small, ImmutableList uses cached Integer values.
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|Integer
argument_list|>
name|argList
decl_stmt|;
specifier|public
specifier|final
name|int
name|filterArg
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an AggregateCall.    *    * @param aggFunction Aggregate function    * @param distinct    Whether distinct    * @param argList     List of ordinals of arguments    * @param type        Result type    * @param name        Name (may be null)    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|AggregateCall
parameter_list|(
name|SqlAggFunction
name|aggFunction
parameter_list|,
name|boolean
name|distinct
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|argList
parameter_list|,
name|RelDataType
name|type
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|aggFunction
argument_list|,
name|distinct
argument_list|,
name|argList
argument_list|,
operator|-
literal|1
argument_list|,
name|type
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an AggregateCall.    *    * @param aggFunction Aggregate function    * @param distinct    Whether distinct    * @param argList     List of ordinals of arguments    * @param filterArg   Ordinal of filter argument, or -1    * @param type        Result type    * @param name        Name (may be null)    */
specifier|private
name|AggregateCall
parameter_list|(
name|SqlAggFunction
name|aggFunction
parameter_list|,
name|boolean
name|distinct
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|argList
parameter_list|,
name|int
name|filterArg
parameter_list|,
name|RelDataType
name|type
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|aggFunction
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|aggFunction
argument_list|)
expr_stmt|;
name|this
operator|.
name|argList
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|argList
argument_list|)
expr_stmt|;
name|this
operator|.
name|filterArg
operator|=
name|filterArg
expr_stmt|;
name|this
operator|.
name|distinct
operator|=
name|distinct
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/** Creates an AggregateCall, inferring its type if {@code type} is null. */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|AggregateCall
name|create
parameter_list|(
name|SqlAggFunction
name|aggFunction
parameter_list|,
name|boolean
name|distinct
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|argList
parameter_list|,
name|int
name|groupCount
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|RelDataType
name|type
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|create
argument_list|(
name|aggFunction
argument_list|,
name|distinct
argument_list|,
name|argList
argument_list|,
operator|-
literal|1
argument_list|,
name|groupCount
argument_list|,
name|input
argument_list|,
name|type
argument_list|,
name|name
argument_list|)
return|;
block|}
comment|/** Creates an AggregateCall, inferring its type if {@code type} is null. */
specifier|public
specifier|static
name|AggregateCall
name|create
parameter_list|(
name|SqlAggFunction
name|aggFunction
parameter_list|,
name|boolean
name|distinct
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|argList
parameter_list|,
name|int
name|filterArg
parameter_list|,
name|int
name|groupCount
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|RelDataType
name|type
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|input
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|types
init|=
name|SqlTypeUtil
operator|.
name|projectTypes
argument_list|(
name|input
operator|.
name|getRowType
argument_list|()
argument_list|,
name|argList
argument_list|)
decl_stmt|;
specifier|final
name|Aggregate
operator|.
name|AggCallBinding
name|callBinding
init|=
operator|new
name|Aggregate
operator|.
name|AggCallBinding
argument_list|(
name|typeFactory
argument_list|,
name|aggFunction
argument_list|,
name|types
argument_list|,
name|groupCount
argument_list|,
name|filterArg
operator|>=
literal|0
argument_list|)
decl_stmt|;
name|type
operator|=
name|aggFunction
operator|.
name|inferReturnType
argument_list|(
name|callBinding
argument_list|)
expr_stmt|;
block|}
return|return
name|create
argument_list|(
name|aggFunction
argument_list|,
name|distinct
argument_list|,
name|argList
argument_list|,
name|filterArg
argument_list|,
name|type
argument_list|,
name|name
argument_list|)
return|;
block|}
comment|/** Creates an AggregateCall. */
specifier|public
specifier|static
name|AggregateCall
name|create
parameter_list|(
name|SqlAggFunction
name|aggFunction
parameter_list|,
name|boolean
name|distinct
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|argList
parameter_list|,
name|int
name|filterArg
parameter_list|,
name|RelDataType
name|type
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|AggregateCall
argument_list|(
name|aggFunction
argument_list|,
name|distinct
argument_list|,
name|argList
argument_list|,
name|filterArg
argument_list|,
name|type
argument_list|,
name|name
argument_list|)
return|;
block|}
comment|/**    * Returns whether this AggregateCall is distinct, as in<code>    * COUNT(DISTINCT empno)</code>.    *    * @return whether distinct    */
specifier|public
specifier|final
name|boolean
name|isDistinct
parameter_list|()
block|{
return|return
name|distinct
return|;
block|}
comment|/**    * Returns the aggregate function.    *    * @return aggregate function    */
specifier|public
specifier|final
name|SqlAggFunction
name|getAggregation
parameter_list|()
block|{
return|return
name|aggFunction
return|;
block|}
comment|/**    * Returns the ordinals of the arguments to this call.    *    *<p>The list is immutable.    *    * @return list of argument ordinals    */
specifier|public
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|getArgList
parameter_list|()
block|{
return|return
name|argList
return|;
block|}
comment|/**    * Returns the result type.    *    * @return result type    */
specifier|public
specifier|final
name|RelDataType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**    * Returns the name.    *    * @return name    */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * Creates an equivalent AggregateCall that has a new name.    *    * @param name New name (may be null)    */
specifier|public
name|AggregateCall
name|rename
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|name
argument_list|,
name|name
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|AggregateCall
argument_list|(
name|aggFunction
argument_list|,
name|distinct
argument_list|,
name|argList
argument_list|,
name|filterArg
argument_list|,
name|type
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
name|aggFunction
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
if|if
condition|(
name|distinct
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
operator|(
name|argList
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
condition|?
literal|"DISTINCT"
else|:
literal|"DISTINCT "
argument_list|)
expr_stmt|;
block|}
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|Integer
name|arg
range|:
name|argList
control|)
block|{
if|if
condition|(
operator|++
name|i
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"$"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
if|if
condition|(
name|filterArg
operator|>=
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" FILTER $"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|filterArg
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|AggregateCall
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|AggregateCall
name|other
init|=
operator|(
name|AggregateCall
operator|)
name|o
decl_stmt|;
return|return
name|aggFunction
operator|.
name|equals
argument_list|(
name|other
operator|.
name|aggFunction
argument_list|)
operator|&&
operator|(
name|distinct
operator|==
name|other
operator|.
name|distinct
operator|)
operator|&&
name|argList
operator|.
name|equals
argument_list|(
name|other
operator|.
name|argList
argument_list|)
operator|&&
name|filterArg
operator|==
name|other
operator|.
name|filterArg
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|aggFunction
argument_list|,
name|distinct
argument_list|,
name|argList
argument_list|,
name|filterArg
argument_list|)
return|;
block|}
comment|/**    * Creates a binding of this call in the context of an    * {@link org.apache.calcite.rel.logical.LogicalAggregate},    * which can then be used to infer the return type.    */
specifier|public
name|Aggregate
operator|.
name|AggCallBinding
name|createBinding
parameter_list|(
name|Aggregate
name|aggregateRelBase
parameter_list|)
block|{
specifier|final
name|RelDataType
name|rowType
init|=
name|aggregateRelBase
operator|.
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
decl_stmt|;
return|return
operator|new
name|Aggregate
operator|.
name|AggCallBinding
argument_list|(
name|aggregateRelBase
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|aggFunction
argument_list|,
name|SqlTypeUtil
operator|.
name|projectTypes
argument_list|(
name|rowType
argument_list|,
name|argList
argument_list|)
argument_list|,
name|aggregateRelBase
operator|.
name|getGroupCount
argument_list|()
argument_list|,
name|filterArg
operator|>=
literal|0
argument_list|)
return|;
block|}
comment|/**    * Creates an equivalent AggregateCall with new argument ordinals.    *    * @param args Arguments    * @return AggregateCall that suits new inputs and GROUP BY columns    */
specifier|public
name|AggregateCall
name|copy
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|args
parameter_list|,
name|int
name|filterArg
parameter_list|)
block|{
return|return
operator|new
name|AggregateCall
argument_list|(
name|aggFunction
argument_list|,
name|distinct
argument_list|,
name|args
argument_list|,
name|filterArg
argument_list|,
name|type
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|AggregateCall
name|copy
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|args
parameter_list|)
block|{
return|return
name|copy
argument_list|(
name|args
argument_list|,
name|filterArg
argument_list|)
return|;
block|}
comment|/**    * Creates equivalent AggregateCall that is adapted to a new input types    * and/or number of columns in GROUP BY.    *    * @param input relation that will be used as a child of aggregate    * @param argList argument indices of the new call in the input    * @param filterArg Index of the filter, or -1    * @param oldGroupKeyCount number of columns in GROUP BY of old aggregate    * @param newGroupKeyCount number of columns in GROUP BY of new aggregate    * @return AggregateCall that suits new inputs and GROUP BY columns    */
specifier|public
name|AggregateCall
name|adaptTo
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|argList
parameter_list|,
name|int
name|filterArg
parameter_list|,
name|int
name|oldGroupKeyCount
parameter_list|,
name|int
name|newGroupKeyCount
parameter_list|)
block|{
comment|// The return type of aggregate call need to be recomputed.
comment|// Since it might depend on the number of columns in GROUP BY.
specifier|final
name|RelDataType
name|newType
init|=
name|oldGroupKeyCount
operator|==
name|newGroupKeyCount
operator|&&
name|argList
operator|.
name|equals
argument_list|(
name|this
operator|.
name|argList
argument_list|)
operator|&&
name|filterArg
operator|==
name|this
operator|.
name|filterArg
condition|?
name|type
else|:
literal|null
decl_stmt|;
return|return
name|create
argument_list|(
name|aggFunction
argument_list|,
name|distinct
argument_list|,
name|argList
argument_list|,
name|filterArg
argument_list|,
name|newGroupKeyCount
argument_list|,
name|input
argument_list|,
name|newType
argument_list|,
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/** Creates a copy of this aggregate call, applying a mapping to its    * arguments. */
specifier|public
name|AggregateCall
name|transform
parameter_list|(
name|Mappings
operator|.
name|TargetMapping
name|mapping
parameter_list|)
block|{
return|return
name|copy
argument_list|(
name|Mappings
operator|.
name|apply2
argument_list|(
operator|(
name|Mapping
operator|)
name|mapping
argument_list|,
name|argList
argument_list|)
argument_list|,
name|filterArg
operator|<
literal|0
condition|?
operator|-
literal|1
else|:
name|Mappings
operator|.
name|apply
argument_list|(
name|mapping
argument_list|,
name|filterArg
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End AggregateCall.java
end_comment

end_unit

