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
name|convert
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
name|RelTraitDef
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
name|RelNode
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
comment|/**  * A relational expression implements the interface<code>Converter</code> to  * indicate that it converts a physical attribute, or  * {@link org.apache.calcite.plan.RelTrait trait}, of a relational expression  * from one value to another.  *  *<p>Sometimes this conversion is expensive; for example, to convert a  * non-distinct to a distinct object stream, we have to clone every object in  * the input.</p>  *  *<p>A converter does not change the logical expression being evaluated; after  * conversion, the number of rows and the values of those rows will still be the  * same. By declaring itself to be a converter, a relational expression is  * telling the planner about this equivalence, and the planner groups  * expressions which are logically equivalent but have different physical traits  * into groups called<code>RelSet</code>s.  *  *<p>In principle one could devise converters which change multiple traits  * simultaneously (say change the sort-order and the physical location of a  * relational expression). In which case, the method {@link #getInputTraits()}  * would return a {@link org.apache.calcite.plan.RelTraitSet}. But for  * simplicity, this class only allows one trait to be converted at a  * time; all other traits are assumed to be preserved.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Converter
extends|extends
name|RelNode
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the trait of the input relational expression.    *    * @return input trait    */
name|RelTraitSet
name|getInputTraits
parameter_list|()
function_decl|;
comment|/**    * Returns the definition of trait which this converter works on.    *    *<p>The input relational expression (matched by the rule) must possess    * this trait and have the value given by {@link #getInputTraits()}, and the    * traits of the output of this converter given by {@link #getTraitSet()} will    * have one trait altered and the other orthogonal traits will be the same.    *    * @return trait which this converter modifies    */
annotation|@
name|Nullable
name|RelTraitDef
name|getTraitDef
parameter_list|()
function_decl|;
comment|/**    * Returns the sole input relational expression.    *    * @return child relational expression    */
name|RelNode
name|getInput
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

