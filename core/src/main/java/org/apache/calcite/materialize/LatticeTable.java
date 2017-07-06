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
name|RelOptTable
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
name|RelDataTypeField
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
name|Util
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/** Table registered in the graph. */
end_comment

begin_class
specifier|public
class|class
name|LatticeTable
block|{
annotation|@
name|Nonnull
specifier|public
specifier|final
name|RelOptTable
name|t
decl_stmt|;
annotation|@
name|Nonnull
specifier|public
specifier|final
name|String
name|alias
decl_stmt|;
name|LatticeTable
parameter_list|(
name|RelOptTable
name|table
parameter_list|)
block|{
name|t
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|table
argument_list|)
expr_stmt|;
name|alias
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|Util
operator|.
name|last
argument_list|(
name|table
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|t
operator|.
name|getQualifiedName
argument_list|()
operator|.
name|hashCode
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
name|obj
parameter_list|)
block|{
return|return
name|this
operator|==
name|obj
operator|||
name|obj
operator|instanceof
name|LatticeTable
operator|&&
name|t
operator|.
name|getQualifiedName
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|LatticeTable
operator|)
name|obj
operator|)
operator|.
name|t
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|t
operator|.
name|getQualifiedName
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
name|RelDataTypeField
name|field
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|t
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End LatticeTable.java
end_comment

end_unit

