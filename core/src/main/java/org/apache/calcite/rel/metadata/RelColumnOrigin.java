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
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * RelColumnOrigin is a data structure describing one of the origins of an  * output column produced by a relational expression.  */
end_comment

begin_class
specifier|public
class|class
name|RelColumnOrigin
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RelOptTable
name|originTable
decl_stmt|;
specifier|private
specifier|final
name|int
name|iOriginColumn
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isDerived
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RelColumnOrigin
parameter_list|(
name|RelOptTable
name|originTable
parameter_list|,
name|int
name|iOriginColumn
parameter_list|,
name|boolean
name|isDerived
parameter_list|)
block|{
name|this
operator|.
name|originTable
operator|=
name|originTable
expr_stmt|;
name|this
operator|.
name|iOriginColumn
operator|=
name|iOriginColumn
expr_stmt|;
name|this
operator|.
name|isDerived
operator|=
name|isDerived
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/** Returns table of origin. */
specifier|public
name|RelOptTable
name|getOriginTable
parameter_list|()
block|{
return|return
name|originTable
return|;
block|}
comment|/** Returns the 0-based index of column in origin table; whether this ordinal    * is flattened or unflattened depends on whether UDT flattening has already    * been performed on the relational expression which produced this    * description. */
specifier|public
name|int
name|getOriginColumnOrdinal
parameter_list|()
block|{
return|return
name|iOriginColumn
return|;
block|}
comment|/**    * Consider the query<code>select a+b as c, d as e from t</code>. The    * output column c has two origins (a and b), both of them derived. The    * output column d as one origin (c), which is not derived.    *    * @return false if value taken directly from column in origin table; true    * otherwise    */
specifier|public
name|boolean
name|isDerived
parameter_list|()
block|{
return|return
name|isDerived
return|;
block|}
comment|// override Object
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|RelColumnOrigin
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RelColumnOrigin
name|other
init|=
operator|(
name|RelColumnOrigin
operator|)
name|obj
decl_stmt|;
return|return
name|originTable
operator|.
name|getQualifiedName
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|originTable
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
operator|&&
operator|(
name|iOriginColumn
operator|==
name|other
operator|.
name|iOriginColumn
operator|)
operator|&&
operator|(
name|isDerived
operator|==
name|other
operator|.
name|isDerived
operator|)
return|;
block|}
comment|// override Object
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|originTable
operator|.
name|getQualifiedName
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|+
name|iOriginColumn
operator|+
operator|(
name|isDerived
condition|?
literal|313
else|:
literal|0
operator|)
return|;
block|}
block|}
end_class

end_unit

