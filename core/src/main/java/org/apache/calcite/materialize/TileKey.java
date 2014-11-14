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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
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
name|BitSet
import|;
end_import

begin_comment
comment|/** Definition of a particular combination of dimensions and measures of a  * lattice that is the basis of a materialization.  *  *<p>Holds similar information to a  * {@link org.apache.calcite.materialize.Lattice.Tile} but a lattice is  * immutable and tiles are not added after their creation. */
end_comment

begin_class
specifier|public
class|class
name|TileKey
block|{
specifier|public
specifier|final
name|Lattice
name|lattice
decl_stmt|;
specifier|public
specifier|final
name|BitSet
name|dimensions
decl_stmt|;
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|Lattice
operator|.
name|Measure
argument_list|>
name|measures
decl_stmt|;
comment|/** Creates a TileKey. */
specifier|public
name|TileKey
parameter_list|(
name|Lattice
name|lattice
parameter_list|,
name|BitSet
name|dimensions
parameter_list|,
name|ImmutableList
argument_list|<
name|Lattice
operator|.
name|Measure
argument_list|>
name|measures
parameter_list|)
block|{
name|this
operator|.
name|lattice
operator|=
name|lattice
expr_stmt|;
name|this
operator|.
name|dimensions
operator|=
name|dimensions
expr_stmt|;
name|this
operator|.
name|measures
operator|=
name|measures
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
name|Objects
operator|.
name|hashCode
argument_list|(
name|lattice
argument_list|,
name|dimensions
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
name|TileKey
operator|&&
name|lattice
operator|==
operator|(
operator|(
name|TileKey
operator|)
name|obj
operator|)
operator|.
name|lattice
operator|&&
name|dimensions
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|TileKey
operator|)
name|obj
operator|)
operator|.
name|dimensions
argument_list|)
operator|&&
name|measures
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|TileKey
operator|)
name|obj
operator|)
operator|.
name|measures
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End TileKey.java
end_comment

end_unit

