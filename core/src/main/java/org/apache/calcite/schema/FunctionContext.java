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
name|schema
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
name|linq4j
operator|.
name|function
operator|.
name|Experimental
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
name|RelDataTypeFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Information about a function call that is passed to the constructor of a  * function instance.  *  *<p>This may enable a function to perform work up front, during construction,  * rather than each time it is invoked. Here is an example of such a function:  *  *<blockquote><pre>  * class RegexMatchesFunction {  *   final java.util.regex.Pattern pattern;  *  *   public RegexMatchesFunction(FunctionContext cx) {  *     String pattern = cx.argumentValueAs(String.class);  *     this.compiledPattern = java.util.regex.Pattern.compile(pattern);  *   }  *  *   public boolean eval(String pattern, String s) {  *     return this.compiledPattern.matches(s);  *   }  * }  *</pre></blockquote>  *  *<p>Register it in the model as follows:  *  *<blockquote><pre>  *   functions: [  *     {  *       name: 'REGEX_MATCHES',  *       className: 'com.example.RegexMatchesFun'  *     }  *   ]  *</pre></blockquote>  *  *<p>and use it in a query:  *  *<blockquote><pre>  * SELECT empno, ename  * FROM Emp  * WHERE regex_matches('J.*ES', ename);  *  * +-------+--------+  * | EMPNO | ENAME  |  * +-------+--------+  * | 7900  | JAMES  |  * | 7566  | JONES  |  * +-------+--------+  *</pre></blockquote>  *  *<p>When executing the query, Calcite will create an instance of  * {@code RegexMatchesFunction} and call the {@code eval} method on that  * instance once per row.  *  *<p>If the {@code eval} method was static, or if the function's  * constructor had zero parameters, the {@code eval} method would have to call  * {@code java.util.regex.Pattern.compile(pattern)} to compile the pattern each  * time.  *  *<p>This interface is marked {@link Experimental}, which means that we may  * change or remove methods, or the entire interface, without notice. But  * probably we will add methods over time, which will just your UDFs more  * information to work with.  */
end_comment

begin_interface
annotation|@
name|Experimental
specifier|public
interface|interface
name|FunctionContext
block|{
comment|/** Returns the type factory. */
name|RelDataTypeFactory
name|getTypeFactory
parameter_list|()
function_decl|;
comment|/** Returns the number of parameters. */
name|int
name|getParameterCount
parameter_list|()
function_decl|;
comment|/** Returns whether the value of an argument is constant.    *    * @param ordinal Argument ordinal, starting from 0 */
name|boolean
name|isArgumentConstant
parameter_list|(
name|int
name|ordinal
parameter_list|)
function_decl|;
comment|/** Returns the value of an argument to this function,    * null if the argument is the NULL literal.    *    * @param ordinal Argument ordinal, starting from 0    * @param valueClass Type of value    *    * @throws ClassCastException if argument cannot be converted to    * {@code valueClass}    *    * @throws IllegalArgumentException if argument is not constant */
parameter_list|<
name|V
parameter_list|>
annotation|@
name|Nullable
name|V
name|getArgumentValueAs
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|Class
argument_list|<
name|V
argument_list|>
name|valueClass
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

