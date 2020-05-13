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
name|rules
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
name|hep
operator|.
name|HepPlanner
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
name|volcano
operator|.
name|VolcanoPlanner
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
name|PhysicalNode
import|;
end_import

begin_comment
comment|/**  * Logical transformation rule, only logical operator can be rule operand,  * and only generate logical alternatives. It is only visible to  * {@link VolcanoPlanner}, {@link HepPlanner} will ignore this interface.  * That means, in {@link HepPlanner}, the rule that implements  * {@link TransformationRule} can still match with physical operator of  * {@link PhysicalNode} and generate physical alternatives.  *  *<p>But in {@link VolcanoPlanner}, {@link TransformationRule} doesn't match  * with physical operator that implements {@link PhysicalNode}. It is not  * allowed to generate physical operators in {@link TransformationRule},  * unless you are using it in {@link HepPlanner}.</p>  *  * @see VolcanoPlanner  * @see SubstitutionRule  */
end_comment

begin_interface
specifier|public
interface|interface
name|TransformationRule
block|{ }
end_interface

end_unit

