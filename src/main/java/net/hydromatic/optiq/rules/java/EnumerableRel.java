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
name|rules
operator|.
name|java
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
name|BlockExpression
import|;
end_import

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
name|Expression
import|;
end_import

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
name|ParameterExpression
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
name|util
operator|.
name|IntList
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

begin_comment
comment|/**  * A relational expression of the  * {@link JavaRules#CONVENTION} calling convention.  *  * @author jhyde  */
end_comment

begin_interface
specifier|public
interface|interface
name|EnumerableRel
extends|extends
name|RelNode
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Creates a plan for this expression according to a calling convention.      *      * @param implementor implementor      */
name|BlockExpression
name|implement
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|)
function_decl|;
comment|/**      * Describes the Java type returned by this relational expression, and the      * mapping between it and the fields of the logical row type.      */
name|PhysType
name|getPhysType
parameter_list|()
function_decl|;
comment|/** Physical type. Consists of a Java type and how it maps onto the row      * type. */
interface|interface
name|PhysType
block|{
name|Expression
name|generateAccessor
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|fields
parameter_list|)
function_decl|;
name|Expression
name|fieldReference
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|int
name|field
parameter_list|)
function_decl|;
name|Class
name|fieldClass
parameter_list|(
name|int
name|field
parameter_list|)
function_decl|;
name|PhysType
name|project
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|integers
parameter_list|)
function_decl|;
name|Expression
name|generateSelector
parameter_list|(
name|ParameterExpression
name|parameter
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|fields
parameter_list|)
function_decl|;
name|Class
name|getJavaRowClass
parameter_list|()
function_decl|;
comment|/** Returns a expression that yields a comparer, or null if this type          * is comparable. */
name|Expression
name|comparer
parameter_list|()
function_decl|;
block|}
block|}
end_interface

begin_comment
comment|// End EnumerableRel.java
end_comment

end_unit

