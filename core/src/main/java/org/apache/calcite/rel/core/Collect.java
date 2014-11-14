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
name|plan
operator|.
name|Convention
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
name|plan
operator|.
name|RelTraitSet
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
name|RelInput
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
name|RelWriter
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
name|SingleRel
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
name|type
operator|.
name|SqlTypeUtil
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
comment|/**  * A relational expression that collapses multiple rows into one.  *  *<p>Rules:</p>  *  *<ul>  *<li>{@code net.sf.farrago.fennel.rel.FarragoMultisetSplitterRule}  * creates a Collect from a call to  * {@link org.apache.calcite.sql.fun.SqlMultisetValueConstructor} or to  * {@link org.apache.calcite.sql.fun.SqlMultisetQueryConstructor}.</li>  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|Collect
extends|extends
name|SingleRel
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|String
name|fieldName
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a Collect.    *    * @param cluster   Cluster    * @param child     Child relational expression    * @param fieldName Name of the sole output field    */
specifier|public
name|Collect
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|child
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
block|}
comment|/**    * Creates a Collect by parsing serialized output.    */
specifier|public
name|Collect
parameter_list|(
name|RelInput
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
operator|.
name|getCluster
argument_list|()
argument_list|,
name|input
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|input
operator|.
name|getInput
argument_list|()
argument_list|,
name|input
operator|.
name|getString
argument_list|(
literal|"field"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
specifier|final
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
return|return
name|copy
argument_list|(
name|traitSet
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|)
block|{
assert|assert
name|traitSet
operator|.
name|containsIfApplicable
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
assert|;
return|return
operator|new
name|Collect
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
return|return
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
operator|.
name|item
argument_list|(
literal|"field"
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
comment|/**    * Returns the name of the sole output field.    *    * @return name of the sole output field    */
specifier|public
name|String
name|getFieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
annotation|@
name|Override
specifier|protected
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|deriveCollectRowType
argument_list|(
name|this
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
comment|/**    * Derives the output type of a collect relational expression.    *    * @param rel       relational expression    * @param fieldName name of sole output field    * @return output type of a collect relational expression    */
specifier|public
specifier|static
name|RelDataType
name|deriveCollectRowType
parameter_list|(
name|SingleRel
name|rel
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|RelDataType
name|childType
init|=
name|rel
operator|.
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
decl_stmt|;
assert|assert
name|childType
operator|.
name|isStruct
argument_list|()
assert|;
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|RelDataType
name|ret
init|=
name|SqlTypeUtil
operator|.
name|createMultisetType
argument_list|(
name|typeFactory
argument_list|,
name|childType
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|ret
operator|=
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
name|fieldName
argument_list|,
name|ret
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|ret
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End Collect.java
end_comment

end_unit

