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
name|hint
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
name|core
operator|.
name|Aggregate
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
name|core
operator|.
name|Calc
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
name|core
operator|.
name|Correlate
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
name|core
operator|.
name|Join
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
name|core
operator|.
name|Project
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
name|core
operator|.
name|TableScan
import|;
end_import

begin_comment
comment|/**  * A hint predicate that specifies which kind of relational  * expression the hint can be applied to.  */
end_comment

begin_class
specifier|public
class|class
name|NodeTypeHintPredicate
implements|implements
name|HintPredicate
block|{
comment|/**    * Enumeration of the relational expression types that the hints    * may be propagated to.    */
enum|enum
name|NodeType
block|{
comment|/**      * The hint is used for the whole query, kind of like a query config.      * This kind of hints would never be propagated.      */
name|SET_VAR
argument_list|(
name|RelNode
operator|.
name|class
argument_list|)
block|,
comment|/**      * The hint would be propagated to the Join nodes.      */
name|JOIN
argument_list|(
name|Join
operator|.
name|class
argument_list|)
block|,
comment|/**      * The hint would be propagated to the TableScan nodes.      */
name|TABLE_SCAN
argument_list|(
name|TableScan
operator|.
name|class
argument_list|)
block|,
comment|/**      * The hint would be propagated to the Project nodes.      */
name|PROJECT
argument_list|(
name|Project
operator|.
name|class
argument_list|)
block|,
comment|/**      * The hint would be propagated to the Aggregate nodes.      */
name|AGGREGATE
argument_list|(
name|Aggregate
operator|.
name|class
argument_list|)
block|,
comment|/**      * The hint would be propagated to the Calc nodes.      */
name|CALC
argument_list|(
name|Calc
operator|.
name|class
argument_list|)
block|,
comment|/**      * The hint would be propagated to the Correlate nodes.      */
name|CORRELATE
argument_list|(
name|Correlate
operator|.
name|class
argument_list|)
block|;
comment|/** Relational expression clazz that the hint can apply to. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"ImmutableEnumChecker"
argument_list|)
specifier|private
name|Class
argument_list|<
name|?
argument_list|>
name|relClazz
decl_stmt|;
name|NodeType
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|relClazz
parameter_list|)
block|{
name|this
operator|.
name|relClazz
operator|=
name|relClazz
expr_stmt|;
block|}
block|}
specifier|private
name|NodeType
name|nodeType
decl_stmt|;
specifier|public
name|NodeTypeHintPredicate
parameter_list|(
name|NodeType
name|nodeType
parameter_list|)
block|{
name|this
operator|.
name|nodeType
operator|=
name|nodeType
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|RelHint
name|hint
parameter_list|,
name|RelNode
name|rel
parameter_list|)
block|{
switch|switch
condition|(
name|this
operator|.
name|nodeType
condition|)
block|{
comment|// Hints of SET_VAR type never propagate.
case|case
name|SET_VAR
case|:
return|return
literal|false
return|;
default|default:
return|return
name|this
operator|.
name|nodeType
operator|.
name|relClazz
operator|.
name|isInstance
argument_list|(
name|rel
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

