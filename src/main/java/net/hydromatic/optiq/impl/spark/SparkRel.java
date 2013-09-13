begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|spark
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|BlockStatement
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|java
operator|.
name|JavaTypeFactory
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|rules
operator|.
name|java
operator|.
name|PhysType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|eigenbase
operator|.
name|relopt
operator|.
name|Convention
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
specifier|final
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
specifier|public
interface|interface
name|Implementor
block|{
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
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
function_decl|;
block|}
specifier|public
class|class
name|Result
block|{   }
block|}
end_interface

begin_comment
comment|// End SparkRel.java
end_comment

end_unit

