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
name|model
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonCreator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonProperty
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * An aggregate function applied to a column (or columns) of a lattice.  *  *<p>Occurs in a {@link org.apache.calcite.model.JsonTile},  * and there is a default list in  * {@link org.apache.calcite.model.JsonLattice}.  *  * @see JsonRoot Description of schema elements  */
end_comment

begin_class
specifier|public
class|class
name|JsonMeasure
block|{
comment|/** The name of an aggregate function.    *    *<p>Required. Usually {@code count}, {@code sum},    * {@code min}, {@code max}.    */
specifier|public
specifier|final
name|String
name|agg
decl_stmt|;
comment|/** Arguments to the measure.    *    *<p>Valid values are:    *<ul>    *<li>Not specified: no arguments</li>    *<li>null: no arguments</li>    *<li>Empty list: no arguments</li>    *<li>String: single argument, the name of a lattice column</li>    *<li>List: multiple arguments, each a column name</li>    *</ul>    *    *<p>Unlike lattice dimensions, measures can not be specified in qualified    * format, {@code ["table", "column"]}. When you define a lattice, make sure    * that each column you intend to use as a measure has a unique name within    * the lattice (using "{@code AS alias}" if necessary).    */
specifier|public
specifier|final
annotation|@
name|Nullable
name|Object
name|args
decl_stmt|;
annotation|@
name|JsonCreator
specifier|public
name|JsonMeasure
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
name|value
operator|=
literal|"agg"
argument_list|,
name|required
operator|=
literal|true
argument_list|)
name|String
name|agg
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"args"
argument_list|)
annotation|@
name|Nullable
name|Object
name|args
parameter_list|)
block|{
name|this
operator|.
name|agg
operator|=
name|requireNonNull
argument_list|(
name|agg
argument_list|,
literal|"agg"
argument_list|)
expr_stmt|;
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ModelHandler
name|modelHandler
parameter_list|)
block|{
name|modelHandler
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

