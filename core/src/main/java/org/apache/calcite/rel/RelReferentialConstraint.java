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
name|util
operator|.
name|mapping
operator|.
name|IntPair
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
comment|/**  * Interface for a referential constraint, i.e., Foreign-Key - Unique-Key relationship,  * between two tables.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelReferentialConstraint
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/** Returns the number of columns in the keys.    *    * @deprecated Use {@code getColumnPairs().size()} */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|default
name|int
name|getNumColumns
parameter_list|()
block|{
return|return
name|getColumnPairs
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**The qualified name of the referencing table, e.g. DEPT. */
name|List
argument_list|<
name|String
argument_list|>
name|getSourceQualifiedName
parameter_list|()
function_decl|;
comment|/** The qualified name of the referenced table, e.g. EMP. */
name|List
argument_list|<
name|String
argument_list|>
name|getTargetQualifiedName
parameter_list|()
function_decl|;
comment|/** The (source, target) column ordinals. */
name|List
argument_list|<
name|IntPair
argument_list|>
name|getColumnPairs
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

