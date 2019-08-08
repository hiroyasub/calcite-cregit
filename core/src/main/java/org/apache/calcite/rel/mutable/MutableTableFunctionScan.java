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
name|metadata
operator|.
name|RelColumnMapping
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
name|rex
operator|.
name|RexNode
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/** Mutable equivalent of  * {@link org.apache.calcite.rel.core.TableFunctionScan}. */
end_comment

begin_class
specifier|public
class|class
name|MutableTableFunctionScan
extends|extends
name|MutableMultiRel
block|{
specifier|public
specifier|final
name|RexNode
name|rexCall
decl_stmt|;
specifier|public
specifier|final
name|Type
name|elementType
decl_stmt|;
specifier|public
specifier|final
name|Set
argument_list|<
name|RelColumnMapping
argument_list|>
name|columnMappings
decl_stmt|;
specifier|private
name|MutableTableFunctionScan
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
name|RexNode
name|rexCall
parameter_list|,
name|Type
name|elementType
parameter_list|,
name|Set
argument_list|<
name|RelColumnMapping
argument_list|>
name|columnMappings
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
name|TABLE_FUNCTION_SCAN
argument_list|,
name|inputs
argument_list|)
expr_stmt|;
name|this
operator|.
name|rexCall
operator|=
name|rexCall
expr_stmt|;
name|this
operator|.
name|elementType
operator|=
name|elementType
expr_stmt|;
name|this
operator|.
name|columnMappings
operator|=
name|columnMappings
expr_stmt|;
block|}
comment|/**    * Creates a MutableTableFunctionScan.    *    * @param cluster         Cluster that this relational expression belongs to    * @param rowType         Row type    * @param inputs          Input relational expressions    * @param rexCall         Function invocation expression    * @param elementType     Element type of the collection that will implement    *                        this table    * @param columnMappings  Column mappings associated with this function    */
specifier|public
specifier|static
name|MutableTableFunctionScan
name|of
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
name|RexNode
name|rexCall
parameter_list|,
name|Type
name|elementType
parameter_list|,
name|Set
argument_list|<
name|RelColumnMapping
argument_list|>
name|columnMappings
parameter_list|)
block|{
return|return
operator|new
name|MutableTableFunctionScan
argument_list|(
name|cluster
argument_list|,
name|rowType
argument_list|,
name|inputs
argument_list|,
name|rexCall
argument_list|,
name|elementType
argument_list|,
name|columnMappings
argument_list|)
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
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|MutableTableFunctionScan
operator|&&
name|STRING_EQUIVALENCE
operator|.
name|equivalent
argument_list|(
name|rexCall
argument_list|,
operator|(
operator|(
name|MutableTableFunctionScan
operator|)
name|obj
operator|)
operator|.
name|rexCall
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|elementType
argument_list|,
operator|(
operator|(
name|MutableTableFunctionScan
operator|)
name|obj
operator|)
operator|.
name|elementType
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|columnMappings
argument_list|,
operator|(
operator|(
name|MutableTableFunctionScan
operator|)
name|obj
operator|)
operator|.
name|columnMappings
argument_list|)
operator|&&
name|inputs
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MutableTableFunctionScan
operator|)
name|obj
operator|)
operator|.
name|getInputs
argument_list|()
argument_list|)
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
name|inputs
argument_list|,
name|STRING_EQUIVALENCE
operator|.
name|hash
argument_list|(
name|rexCall
argument_list|)
argument_list|,
name|elementType
argument_list|,
name|columnMappings
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
name|buf
operator|.
name|append
argument_list|(
literal|"TableFunctionScan(rexCall: "
argument_list|)
operator|.
name|append
argument_list|(
name|rexCall
argument_list|)
expr_stmt|;
if|if
condition|(
name|elementType
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|", elementType: "
argument_list|)
operator|.
name|append
argument_list|(
name|elementType
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
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
comment|// TODO Auto-generated method stub
return|return
name|MutableTableFunctionScan
operator|.
name|of
argument_list|(
name|cluster
argument_list|,
name|rowType
argument_list|,
name|cloneChildren
argument_list|()
argument_list|,
name|rexCall
argument_list|,
name|elementType
argument_list|,
name|columnMappings
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End MutableTableFunctionScan.java
end_comment

end_unit

