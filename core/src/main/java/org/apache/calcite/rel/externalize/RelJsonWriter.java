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
name|externalize
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
name|rel
operator|.
name|RelWriter
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
name|sql
operator|.
name|SqlExplainLevel
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
name|util
operator|.
name|JsonBuilder
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
name|util
operator|.
name|Pair
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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
comment|/**  * Callback for a relational expression to dump itself as JSON.  *  * @see RelJsonReader  */
end_comment

begin_class
specifier|public
class|class
name|RelJsonWriter
implements|implements
name|RelWriter
block|{
comment|//~ Instance fields ----------------------------------------------------------
specifier|private
specifier|final
name|JsonBuilder
name|jsonBuilder
decl_stmt|;
specifier|private
specifier|final
name|RelJson
name|relJson
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|RelNode
argument_list|,
name|String
argument_list|>
name|relIdMap
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|RelNode
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|relList
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|String
name|previousId
decl_stmt|;
comment|//~ Constructors -------------------------------------------------------------
specifier|public
name|RelJsonWriter
parameter_list|()
block|{
name|jsonBuilder
operator|=
operator|new
name|JsonBuilder
argument_list|()
expr_stmt|;
name|relList
operator|=
name|jsonBuilder
operator|.
name|list
argument_list|()
expr_stmt|;
name|relJson
operator|=
operator|new
name|RelJson
argument_list|(
name|jsonBuilder
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ------------------------------------------------------------------
specifier|protected
name|void
name|explain_
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|values
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|jsonBuilder
operator|.
name|map
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// ensure that id is the first attribute
name|map
operator|.
name|put
argument_list|(
literal|"relOp"
argument_list|,
name|relJson
operator|.
name|classToTypeName
argument_list|(
name|rel
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|value
operator|.
name|right
operator|instanceof
name|RelNode
condition|)
block|{
continue|continue;
block|}
name|put
argument_list|(
name|map
argument_list|,
name|value
operator|.
name|left
argument_list|,
name|value
operator|.
name|right
argument_list|)
expr_stmt|;
block|}
comment|// omit 'inputs: ["3"]' if "3" is the preceding rel
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|list
init|=
name|explainInputs
argument_list|(
name|rel
operator|.
name|getInputs
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|!=
literal|1
operator|||
operator|!
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
name|previousId
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
literal|"inputs"
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|id
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|relIdMap
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|relIdMap
operator|.
name|put
argument_list|(
name|rel
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|relList
operator|.
name|add
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|previousId
operator|=
name|id
expr_stmt|;
block|}
specifier|private
name|void
name|put
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|,
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|relJson
operator|.
name|toJson
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|Object
argument_list|>
name|explainInputs
parameter_list|(
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|list
init|=
name|jsonBuilder
operator|.
name|list
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|inputs
control|)
block|{
name|String
name|id
init|=
name|relIdMap
operator|.
name|get
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
name|input
operator|.
name|explain
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|id
operator|=
name|previousId
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
specifier|public
specifier|final
name|void
name|explain
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|valueList
parameter_list|)
block|{
name|explain_
argument_list|(
name|rel
argument_list|,
name|valueList
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlExplainLevel
name|getDetailLevel
parameter_list|()
block|{
return|return
name|SqlExplainLevel
operator|.
name|ALL_ATTRIBUTES
return|;
block|}
specifier|public
name|RelWriter
name|input
parameter_list|(
name|String
name|term
parameter_list|,
name|RelNode
name|input
parameter_list|)
block|{
return|return
name|this
return|;
block|}
specifier|public
name|RelWriter
name|item
parameter_list|(
name|String
name|term
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|values
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|term
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|private
name|List
argument_list|<
name|Object
argument_list|>
name|getList
parameter_list|(
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|values
parameter_list|,
name|String
name|tag
parameter_list|)
block|{
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|value
operator|.
name|left
operator|.
name|equals
argument_list|(
name|tag
argument_list|)
condition|)
block|{
comment|//noinspection unchecked
return|return
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|value
operator|.
name|right
return|;
block|}
block|}
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|tag
argument_list|,
operator|(
name|Object
operator|)
name|list
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|list
return|;
block|}
specifier|public
name|RelWriter
name|itemIf
parameter_list|(
name|String
name|term
parameter_list|,
name|Object
name|value
parameter_list|,
name|boolean
name|condition
parameter_list|)
block|{
if|if
condition|(
name|condition
condition|)
block|{
name|item
argument_list|(
name|term
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
specifier|public
name|RelWriter
name|done
parameter_list|(
name|RelNode
name|node
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|valuesCopy
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|explain_
argument_list|(
name|node
argument_list|,
name|valuesCopy
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|boolean
name|nest
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Returns a JSON string describing the relational expressions that were just    * explained.    */
specifier|public
name|String
name|asString
parameter_list|()
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|jsonBuilder
operator|.
name|map
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"rels"
argument_list|,
name|relList
argument_list|)
expr_stmt|;
return|return
name|jsonBuilder
operator|.
name|toJsonString
argument_list|(
name|map
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelJsonWriter.java
end_comment

end_unit

