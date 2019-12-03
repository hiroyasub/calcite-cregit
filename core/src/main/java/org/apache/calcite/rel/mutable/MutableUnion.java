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
name|mutable
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
name|RelOptCluster
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/** Mutable equivalent of {@link org.apache.calcite.rel.core.Union}. */
end_comment

begin_class
specifier|public
class|class
name|MutableUnion
extends|extends
name|MutableSetOp
block|{
specifier|private
name|MutableUnion
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|MutableRel
argument_list|>
name|inputs
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|rowType
argument_list|,
name|MutableRelType
operator|.
name|UNION
argument_list|,
name|inputs
argument_list|,
name|all
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a MutableUnion.    *    * @param rowType Row type    * @param inputs  Input relational expressions    * @param all     Whether the union result should include all rows or    *                eliminate duplicates from input relational expressions    */
specifier|public
specifier|static
name|MutableUnion
name|of
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|MutableRel
argument_list|>
name|inputs
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
assert|assert
name|inputs
operator|.
name|size
argument_list|()
operator|>=
literal|2
assert|;
specifier|final
name|MutableRel
name|input0
init|=
name|inputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
operator|new
name|MutableUnion
argument_list|(
name|input0
operator|.
name|cluster
argument_list|,
name|rowType
argument_list|,
name|inputs
argument_list|,
name|all
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StringBuilder
name|digest
parameter_list|(
name|StringBuilder
name|buf
parameter_list|)
block|{
return|return
name|buf
operator|.
name|append
argument_list|(
literal|"Union(all: "
argument_list|)
operator|.
name|append
argument_list|(
name|all
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|MutableRel
name|clone
parameter_list|()
block|{
return|return
name|MutableUnion
operator|.
name|of
argument_list|(
name|rowType
argument_list|,
name|cloneChildren
argument_list|()
argument_list|,
name|all
argument_list|)
return|;
block|}
block|}
end_class

end_unit

