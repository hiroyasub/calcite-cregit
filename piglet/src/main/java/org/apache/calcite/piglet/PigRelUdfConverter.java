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
name|piglet
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
name|schema
operator|.
name|impl
operator|.
name|ScalarFunctionImpl
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
name|SqlAggFunction
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
name|SqlOperator
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|validate
operator|.
name|SqlUserDefinedFunction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pig
operator|.
name|Accumulator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pig
operator|.
name|FuncSpec
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pig
operator|.
name|impl
operator|.
name|logicalLayer
operator|.
name|FrontendException
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|Method
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
comment|/**  * This class maps a Pig UDF to a corresponding SQL built-in function/operator.  * If such mapping is not available, it creates a wrapper to allow SQL engines  * call Pig UDFs directly.  *  */
end_comment

begin_class
class|class
name|PigRelUdfConverter
block|{
specifier|private
name|PigRelUdfConverter
parameter_list|()
block|{
block|}
specifier|private
specifier|static
specifier|final
name|PigUdfFinder
name|PIG_UDF_FINDER
init|=
operator|new
name|PigUdfFinder
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SqlOperator
argument_list|>
name|BUILTIN_FUNC
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|SqlOperator
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.ABS"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|ABS
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.BigDecimalAbs"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|ABS
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.BigIntegerAbs"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|ABS
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.DoubleAbs"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|ABS
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.FloatAbs"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|ABS
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.IntAbs"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|ABS
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.LongAbs"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|ABS
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.CEIL"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|CEIL
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.CONCAT"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|CONCAT
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.StringConcat"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|CONCAT
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.EXP"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|EXP
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.FLOOR"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|FLOOR
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.LOG"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|LN
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.LOG10"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|LOG10
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.LOWER"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|LOWER
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.RANDOM"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|RAND
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.SQRT"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|SQRT
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.StringSize"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|CHAR_LENGTH
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.SUBSTRING"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|SUBSTRING
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.TOTUPLE"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|ROW
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.UPPER"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|UPPER
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SqlAggFunction
argument_list|>
name|BUILTIN_AGG_FUNC
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|SqlAggFunction
decl|>
name|builder
argument_list|()
comment|// AVG()
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.AVG"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|AVG
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.BigDecimalAvg"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|AVG
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.BigIntegerAvg"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|AVG
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.DoubleAvg"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|AVG
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.FloatAvg"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|AVG
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.IntAvg"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|AVG
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.LongAvg"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|AVG
argument_list|)
comment|// COUNT()
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.COUNT"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|COUNT
argument_list|)
comment|// MAX()
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.MAX"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MAX
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.BigDecimalMax"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MAX
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.BigIntegerMax"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MAX
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.DateTimeMax"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MAX
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.DoubleMax"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MAX
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.FloatMax"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MAX
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.IntMax"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MAX
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.LongMax"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MAX
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.StringMax"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MAX
argument_list|)
comment|// MIN()
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.MIN"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MIN
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.BigDecimalMin"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MIN
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.BigIntegerMin"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MIN
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.DateTimeMin"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MIN
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.DoubleMin"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MIN
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.FloatMin"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MIN
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.IntMin"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MIN
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.LongMin"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MIN
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.StringMin"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MIN
argument_list|)
comment|// SUM()
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.BigDecimalSum"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|SUM
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.BigIntegerSum"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|SUM
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.DoubleSum"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|SUM
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.FloatSum"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|SUM
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.IntSum"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|SUM
argument_list|)
decl|.
name|put
argument_list|(
literal|"org.apache.pig.builtin.LongSum"
argument_list|,
name|SqlStdOperatorTable
operator|.
name|SUM
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
comment|/**    * Converts a Pig UDF, given its {@link FuncSpec} and a list of relational    * operands (function arguments). To call this function, the arguments of    * Pig functions need to be converted into the relational types before.    *    * @param builder The relational builder    * @param pigFunc Pig function description    * @param operands Relational operands for the function    * @param returnType Function return data type    * @return The SQL calls equivalent to the Pig function    */
specifier|static
name|RexNode
name|convertPigFunction
parameter_list|(
name|PigRelBuilder
name|builder
parameter_list|,
name|FuncSpec
name|pigFunc
parameter_list|,
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|operands
parameter_list|,
name|RelDataType
name|returnType
parameter_list|)
throws|throws
name|FrontendException
block|{
comment|// First, check the map for the direct mapping SQL builtin
specifier|final
name|SqlOperator
name|operator
init|=
name|BUILTIN_FUNC
operator|.
name|get
argument_list|(
name|pigFunc
operator|.
name|getClassName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|operator
operator|!=
literal|null
condition|)
block|{
return|return
name|builder
operator|.
name|call
argument_list|(
name|operator
argument_list|,
name|operands
argument_list|)
return|;
block|}
comment|// If no mapping found, build the argument wrapper to convert the relation operands
comment|// into a Pig tuple so that the Pig function can consume it.
try|try
block|{
comment|// Find the implementation method for the Pig function from
comment|// the class defining the UDF.
specifier|final
name|Class
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|pigFunc
operator|.
name|getClassName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Method
name|method
init|=
name|PIG_UDF_FINDER
operator|.
name|findPigUdfImplementationMethod
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
comment|// Now create the argument wrapper. Depend on the type of the UDF, the
comment|// relational operands are converted into a Pig Tuple or Pig DataBag
comment|// with the appropriate wrapper.
specifier|final
name|SqlUserDefinedFunction
name|convertOp
init|=
name|Accumulator
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|?
name|PigRelSqlUdfs
operator|.
name|createPigBagUDF
argument_list|(
name|operands
argument_list|)
else|:
name|PigRelSqlUdfs
operator|.
name|createPigTupleUDF
argument_list|(
name|operands
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|rexTuple
init|=
name|builder
operator|.
name|call
argument_list|(
name|convertOp
argument_list|,
name|operands
argument_list|)
decl_stmt|;
comment|// Then convert the Pig function into a @SqlUserDefinedFunction.
name|SqlUserDefinedFunction
name|userFuncOp
init|=
name|PigRelSqlUdfs
operator|.
name|createGeneralPigUdf
argument_list|(
name|clazz
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|method
argument_list|,
name|pigFunc
argument_list|,
name|rexTuple
operator|.
name|getType
argument_list|()
argument_list|,
name|returnType
argument_list|)
decl_stmt|;
comment|// Ready to return SqlCall after having SqlUDF and operand
return|return
name|builder
operator|.
name|call
argument_list|(
name|userFuncOp
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|rexTuple
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|FrontendException
argument_list|(
literal|"Cannot find the implementation for Pig UDF class: "
operator|+
name|pigFunc
operator|.
name|getClassName
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Gets the {@link SqlAggFunction} for the corresponding Pig aggregate    * UDF call; returns null for invalid rex call.    *    * @param call Pig aggregate UDF call    */
specifier|static
name|SqlAggFunction
name|getSqlAggFuncForPigUdf
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|call
operator|.
name|getOperator
argument_list|()
operator|instanceof
name|PigUserDefinedFunction
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|PigUserDefinedFunction
name|pigUdf
init|=
operator|(
name|PigUserDefinedFunction
operator|)
name|call
operator|.
name|getOperator
argument_list|()
decl_stmt|;
if|if
condition|(
name|pigUdf
operator|.
name|funcSpec
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|pigUdfClassName
init|=
name|pigUdf
operator|.
name|funcSpec
operator|.
name|getClassName
argument_list|()
decl_stmt|;
specifier|final
name|SqlAggFunction
name|sqlAggFunction
init|=
name|BUILTIN_AGG_FUNC
operator|.
name|get
argument_list|(
name|pigUdfClassName
argument_list|)
decl_stmt|;
if|if
condition|(
name|sqlAggFunction
operator|==
literal|null
condition|)
block|{
specifier|final
name|Class
name|udfClass
init|=
operator|(
operator|(
name|ScalarFunctionImpl
operator|)
name|pigUdf
operator|.
name|getFunction
argument_list|()
operator|)
operator|.
name|method
operator|.
name|getDeclaringClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|Accumulator
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|udfClass
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot find corresponding SqlAgg func for Pig aggegate "
operator|+
name|pigUdfClassName
argument_list|)
throw|;
block|}
block|}
return|return
name|sqlAggFunction
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

