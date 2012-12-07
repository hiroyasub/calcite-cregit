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
name|Queryable
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
name|*
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
name|RelImplementorImpl
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
name|RelImplementor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
operator|.
name|RexBuilder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Subclass of {@link RelImplementor} for relational operators  * of {@link JavaRules#CONVENTION} calling  * convention.  *  * @author jhyde  */
end_comment

begin_class
specifier|public
class|class
name|EnumerableRelImplementor
extends|extends
name|RelImplementorImpl
block|{
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Queryable
argument_list|>
name|map
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Queryable
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|EnumerableRelImplementor
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
specifier|public
name|BlockExpression
name|visitChild
parameter_list|(
name|EnumerableRel
name|parent
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|EnumerableRel
name|child
parameter_list|)
block|{
return|return
operator|(
name|BlockExpression
operator|)
name|super
operator|.
name|visitChild
argument_list|(
name|parent
argument_list|,
name|ordinal
argument_list|,
name|child
argument_list|)
return|;
block|}
specifier|public
name|BlockExpression
name|visitChildInternal
parameter_list|(
name|RelNode
name|child
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
return|return
operator|(
operator|(
name|EnumerableRel
operator|)
name|child
operator|)
operator|.
name|implement
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|BlockExpression
name|implementRoot
parameter_list|(
name|EnumerableRel
name|rootRel
parameter_list|)
block|{
return|return
name|rootRel
operator|.
name|implement
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|register
parameter_list|(
name|Queryable
name|queryable
parameter_list|)
block|{
name|String
name|name
init|=
literal|"v"
operator|+
name|map
operator|.
name|size
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|queryable
argument_list|)
expr_stmt|;
return|return
name|Expressions
operator|.
name|variable
argument_list|(
name|queryable
operator|.
name|getClass
argument_list|()
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End EnumerableRelImplementor.java
end_comment

end_unit

