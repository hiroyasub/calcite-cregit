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
name|interpreter
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
name|core
operator|.
name|Uncollect
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
name|Locale
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
comment|/**  * Interpreter node that implements a  * {@link org.apache.calcite.rel.core.Uncollect}.  */
end_comment

begin_class
specifier|public
class|class
name|UncollectNode
extends|extends
name|AbstractSingleNode
argument_list|<
name|Uncollect
argument_list|>
block|{
specifier|public
name|UncollectNode
parameter_list|(
name|Compiler
name|compiler
parameter_list|,
name|Uncollect
name|uncollect
parameter_list|)
block|{
name|super
argument_list|(
name|compiler
argument_list|,
name|uncollect
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Row
name|row
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|row
operator|=
name|source
operator|.
name|receive
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Object
name|value
range|:
name|row
operator|.
name|getValues
argument_list|()
control|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"NULL value for unnest."
argument_list|)
throw|;
block|}
name|int
name|i
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|List
condition|)
block|{
name|List
name|list
init|=
operator|(
name|List
operator|)
name|value
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|list
control|)
block|{
if|if
condition|(
name|rel
operator|.
name|withOrdinality
condition|)
block|{
name|sink
operator|.
name|send
argument_list|(
name|Row
operator|.
name|of
argument_list|(
name|o
argument_list|,
name|i
operator|++
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sink
operator|.
name|send
argument_list|(
name|Row
operator|.
name|of
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|else if
condition|(
name|value
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|value
decl_stmt|;
for|for
control|(
name|Object
name|key
range|:
name|map
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|rel
operator|.
name|withOrdinality
condition|)
block|{
name|sink
operator|.
name|send
argument_list|(
name|Row
operator|.
name|of
argument_list|(
name|key
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|,
name|i
operator|++
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sink
operator|.
name|send
argument_list|(
name|Row
operator|.
name|of
argument_list|(
name|key
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Invalid type: %s for unnest."
argument_list|,
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

