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
name|plan
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
name|DataContext
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
name|java
operator|.
name|JavaTypeFactory
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
name|QueryProvider
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
name|rel
operator|.
name|logical
operator|.
name|LogicalFilter
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
name|type
operator|.
name|RelDataType
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
name|RexCall
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
name|RexInputRef
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
name|RexLiteral
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
name|RexNode
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
name|RexUtil
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
name|schema
operator|.
name|SchemaPlus
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
name|NlsString
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|trace
operator|.
name|CalciteLogger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
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
comment|/**  * DataContext for evaluating a RexExpression.  */
end_comment

begin_class
specifier|public
class|class
name|VisitorDataContext
implements|implements
name|DataContext
block|{
specifier|private
specifier|static
specifier|final
name|CalciteLogger
name|LOGGER
init|=
operator|new
name|CalciteLogger
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|VisitorDataContext
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Object
index|[]
name|values
decl_stmt|;
specifier|public
name|VisitorDataContext
parameter_list|(
name|Object
index|[]
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SchemaPlus
name|getRootSchema
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unsupported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unsupported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|QueryProvider
name|getQueryProvider
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unsupported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"inputRecord"
argument_list|)
condition|)
block|{
return|return
name|values
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
specifier|static
name|DataContext
name|of
parameter_list|(
name|RelNode
name|targetRel
parameter_list|,
name|LogicalFilter
name|queryRel
parameter_list|)
block|{
return|return
name|of
argument_list|(
name|targetRel
operator|.
name|getRowType
argument_list|()
argument_list|,
name|queryRel
operator|.
name|getCondition
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|DataContext
name|of
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|RexNode
name|rex
parameter_list|)
block|{
specifier|final
name|int
name|size
init|=
name|rowType
operator|.
name|getFieldList
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|Object
index|[]
name|values
init|=
operator|new
name|Object
index|[
name|size
index|]
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
init|=
operator|(
operator|(
name|RexCall
operator|)
name|rex
operator|)
operator|.
name|getOperands
argument_list|()
decl_stmt|;
specifier|final
name|RexNode
name|firstOperand
init|=
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|secondOperand
init|=
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Pair
argument_list|<
name|Integer
argument_list|,
name|?
argument_list|>
name|value
init|=
name|getValue
argument_list|(
name|firstOperand
argument_list|,
name|secondOperand
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|int
name|index
init|=
name|value
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|values
index|[
name|index
index|]
operator|=
name|value
operator|.
name|getValue
argument_list|()
expr_stmt|;
return|return
operator|new
name|VisitorDataContext
argument_list|(
name|values
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
specifier|static
name|DataContext
name|of
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|RexInputRef
argument_list|,
name|RexNode
argument_list|>
argument_list|>
name|usageList
parameter_list|)
block|{
specifier|final
name|int
name|size
init|=
name|rowType
operator|.
name|getFieldList
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|Object
index|[]
name|values
init|=
operator|new
name|Object
index|[
name|size
index|]
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|RexInputRef
argument_list|,
name|RexNode
argument_list|>
name|elem
range|:
name|usageList
control|)
block|{
name|Pair
argument_list|<
name|Integer
argument_list|,
name|?
argument_list|>
name|value
init|=
name|getValue
argument_list|(
name|elem
operator|.
name|getKey
argument_list|()
argument_list|,
name|elem
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|LOGGER
operator|.
name|warn
argument_list|(
literal|"{} is not handled for {} for checking implication"
argument_list|,
name|elem
operator|.
name|getKey
argument_list|()
argument_list|,
name|elem
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|int
name|index
init|=
name|value
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|values
index|[
name|index
index|]
operator|=
name|value
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|VisitorDataContext
argument_list|(
name|values
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Pair
argument_list|<
name|Integer
argument_list|,
name|?
argument_list|>
name|getValue
parameter_list|(
name|RexNode
name|inputRef
parameter_list|,
name|RexNode
name|literal
parameter_list|)
block|{
name|inputRef
operator|=
name|inputRef
operator|==
literal|null
condition|?
literal|null
else|:
name|RexUtil
operator|.
name|removeCast
argument_list|(
name|inputRef
argument_list|)
expr_stmt|;
name|literal
operator|=
name|literal
operator|==
literal|null
condition|?
literal|null
else|:
name|RexUtil
operator|.
name|removeCast
argument_list|(
name|literal
argument_list|)
expr_stmt|;
if|if
condition|(
name|inputRef
operator|instanceof
name|RexInputRef
operator|&&
name|literal
operator|instanceof
name|RexLiteral
condition|)
block|{
specifier|final
name|int
name|index
init|=
operator|(
operator|(
name|RexInputRef
operator|)
name|inputRef
operator|)
operator|.
name|getIndex
argument_list|()
decl_stmt|;
specifier|final
name|RexLiteral
name|rexLiteral
init|=
operator|(
name|RexLiteral
operator|)
name|literal
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|inputRef
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|==
literal|null
condition|)
block|{
name|LOGGER
operator|.
name|warn
argument_list|(
literal|"{} returned null SqlTypeName"
argument_list|,
name|inputRef
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
switch|switch
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|INTEGER
case|:
return|return
name|Pair
operator|.
name|of
argument_list|(
name|index
argument_list|,
name|rexLiteral
operator|.
name|getValueAs
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
argument_list|)
return|;
case|case
name|DOUBLE
case|:
return|return
name|Pair
operator|.
name|of
argument_list|(
name|index
argument_list|,
name|rexLiteral
operator|.
name|getValueAs
argument_list|(
name|Double
operator|.
name|class
argument_list|)
argument_list|)
return|;
case|case
name|REAL
case|:
return|return
name|Pair
operator|.
name|of
argument_list|(
name|index
argument_list|,
name|rexLiteral
operator|.
name|getValueAs
argument_list|(
name|Float
operator|.
name|class
argument_list|)
argument_list|)
return|;
case|case
name|BIGINT
case|:
return|return
name|Pair
operator|.
name|of
argument_list|(
name|index
argument_list|,
name|rexLiteral
operator|.
name|getValueAs
argument_list|(
name|Long
operator|.
name|class
argument_list|)
argument_list|)
return|;
case|case
name|SMALLINT
case|:
return|return
name|Pair
operator|.
name|of
argument_list|(
name|index
argument_list|,
name|rexLiteral
operator|.
name|getValueAs
argument_list|(
name|Short
operator|.
name|class
argument_list|)
argument_list|)
return|;
case|case
name|TINYINT
case|:
return|return
name|Pair
operator|.
name|of
argument_list|(
name|index
argument_list|,
name|rexLiteral
operator|.
name|getValueAs
argument_list|(
name|Byte
operator|.
name|class
argument_list|)
argument_list|)
return|;
case|case
name|DECIMAL
case|:
return|return
name|Pair
operator|.
name|of
argument_list|(
name|index
argument_list|,
name|rexLiteral
operator|.
name|getValueAs
argument_list|(
name|BigDecimal
operator|.
name|class
argument_list|)
argument_list|)
return|;
case|case
name|DATE
case|:
case|case
name|TIME
case|:
return|return
name|Pair
operator|.
name|of
argument_list|(
name|index
argument_list|,
name|rexLiteral
operator|.
name|getValueAs
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
argument_list|)
return|;
case|case
name|TIMESTAMP
case|:
return|return
name|Pair
operator|.
name|of
argument_list|(
name|index
argument_list|,
name|rexLiteral
operator|.
name|getValueAs
argument_list|(
name|Long
operator|.
name|class
argument_list|)
argument_list|)
return|;
case|case
name|CHAR
case|:
return|return
name|Pair
operator|.
name|of
argument_list|(
name|index
argument_list|,
name|rexLiteral
operator|.
name|getValueAs
argument_list|(
name|Character
operator|.
name|class
argument_list|)
argument_list|)
return|;
case|case
name|VARCHAR
case|:
return|return
name|Pair
operator|.
name|of
argument_list|(
name|index
argument_list|,
name|rexLiteral
operator|.
name|getValueAs
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
return|;
default|default:
comment|// TODO: Support few more supported cases
name|LOGGER
operator|.
name|warn
argument_list|(
literal|"{} for value of class {} is being handled in default way"
argument_list|,
name|type
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|rexLiteral
operator|.
name|getValue
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|rexLiteral
operator|.
name|getValue
argument_list|()
operator|instanceof
name|NlsString
condition|)
block|{
return|return
name|Pair
operator|.
name|of
argument_list|(
name|index
argument_list|,
operator|(
operator|(
name|NlsString
operator|)
name|rexLiteral
operator|.
name|getValue
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Pair
operator|.
name|of
argument_list|(
name|index
argument_list|,
name|rexLiteral
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
comment|// Unsupported Arguments
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

