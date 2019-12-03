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
name|adapter
operator|.
name|spark
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
name|adapter
operator|.
name|enumerable
operator|.
name|JavaRelImplementor
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
name|adapter
operator|.
name|enumerable
operator|.
name|PhysType
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
name|linq4j
operator|.
name|tree
operator|.
name|BlockStatement
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
name|rex
operator|.
name|RexBuilder
import|;
end_import

begin_comment
comment|/**  * Relational expression that uses Spark calling convention.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SparkRel
extends|extends
name|RelNode
block|{
name|Result
name|implementSpark
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
function_decl|;
comment|/** Calling convention for relational operations that occur in Spark. */
name|Convention
name|CONVENTION
init|=
operator|new
name|Convention
operator|.
name|Impl
argument_list|(
literal|"SPARK"
argument_list|,
name|SparkRel
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Extension to {@link JavaRelImplementor} that can handle Spark relational    * expressions. */
specifier|abstract
class|class
name|Implementor
extends|extends
name|JavaRelImplementor
block|{
specifier|public
name|Implementor
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
name|super
argument_list|(
name|rexBuilder
argument_list|)
expr_stmt|;
block|}
specifier|abstract
name|Result
name|result
parameter_list|(
name|PhysType
name|physType
parameter_list|,
name|BlockStatement
name|blockStatement
parameter_list|)
function_decl|;
specifier|abstract
name|Result
name|visitInput
parameter_list|(
name|SparkRel
name|parent
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|SparkRel
name|input
parameter_list|)
function_decl|;
block|}
comment|/** Result of generating Java code to implement a Spark relational    * expression. */
class|class
name|Result
block|{
specifier|public
specifier|final
name|BlockStatement
name|block
decl_stmt|;
specifier|public
specifier|final
name|PhysType
name|physType
decl_stmt|;
specifier|public
name|Result
parameter_list|(
name|PhysType
name|physType
parameter_list|,
name|BlockStatement
name|block
parameter_list|)
block|{
name|this
operator|.
name|physType
operator|=
name|physType
expr_stmt|;
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
block|}
block|}
block|}
end_interface

end_unit

