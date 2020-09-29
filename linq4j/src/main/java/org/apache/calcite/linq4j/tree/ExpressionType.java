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
name|linq4j
operator|.
name|tree
package|;
end_package

begin_comment
comment|/**  *<p>Analogous to LINQ's System.Linq.Expressions.ExpressionType.</p>  */
end_comment

begin_enum
specifier|public
enum|enum
name|ExpressionType
block|{
comment|// Operator precedence and associativity is as follows.
comment|//
comment|//  Priority Operators  Operation
comment|//  ======== ========== ========================================
comment|//  1 left   [ ]        array index
comment|//           ()         method call
comment|//           .          member access
comment|//  2 right  ++         pre- or postfix increment
comment|//           --         pre- or postfix decrement
comment|//           + -        unary plus, minus
comment|//           ~          bitwise NOT
comment|//           !          boolean (logical) NOT
comment|//           (type)     type cast
comment|//           new        object creation
comment|//  3 left   * / %      multiplication, division, remainder
comment|//  4 left   + -        addition, subtraction
comment|//           +          string concatenation
comment|//  5 left<<         signed bit shift left
comment|//>>         signed bit shift right
comment|//>>>        unsigned bit shift right
comment|//  6 left<<=       less than, less than or equal to
comment|//>>=       greater than, greater than or equal to
comment|//           instanceof reference test
comment|//  7 left   ==         equal to
comment|//           !=         not equal to
comment|//  8 left&          bitwise AND
comment|//&          boolean (logical) AND
comment|//  9 left   ^          bitwise XOR
comment|//           ^          boolean (logical) XOR
comment|//  10 left  |          bitwise OR
comment|//           |          boolean (logical) OR
comment|//  11 left&&         boolean (logical) AND
comment|//  12 left  ||         boolean (logical) OR
comment|//  13 right ? :        conditional right
comment|//  14 right =          assignment
comment|//           *= /= += -= %=
comment|//<<=>>=>>>=
comment|//&= ^= |=   combined assignment
comment|/**    * An addition operation, such as a + b, without overflow    * checking, for numeric operands.    */
name|Add
argument_list|(
literal|" + "
argument_list|,
literal|false
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * An addition operation, such as (a + b), with overflow    * checking, for numeric operands.    */
name|AddChecked
argument_list|(
literal|" + "
argument_list|,
literal|false
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * A bitwise or logical AND operation, such as {@code a& b} in Java.    */
name|And
argument_list|(
literal|"& "
argument_list|,
literal|false
argument_list|,
literal|8
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * A conditional AND operation that evaluates the second operand    * only if the first operand evaluates to true. It corresponds to    * {@code a&& b} in Java.    */
name|AndAlso
argument_list|(
literal|"&& "
argument_list|,
literal|false
argument_list|,
literal|11
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * An operation that obtains the length of a one-dimensional    * array, such as array.Length.    */
name|ArrayLength
block|,
comment|/**    * An indexing operation in a one-dimensional array, such as    * {@code array[index]} in Java.    */
name|ArrayIndex
block|,
comment|/**    * A method call, such as in the {@code obj.sampleMethod()}    * expression.    */
name|Call
argument_list|(
literal|"."
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * A node that represents a null coalescing operation, such    * as (a ?? b) in C# or If(a, b) in Visual Basic.    */
name|Coalesce
block|,
comment|/**    * A conditional operation, such as {@code a> b ? a : b} in Java.    */
name|Conditional
argument_list|(
literal|" ? "
argument_list|,
literal|" : "
argument_list|,
literal|false
argument_list|,
literal|13
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A constant value.    */
name|Constant
block|,
comment|/**    * A cast or conversion operation, such as {@code (SampleType) obj} in    * Java. For a numeric    * conversion, if the converted value is too large for the    * destination type, no exception is thrown.    */
name|Convert
argument_list|(
literal|null
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A cast or conversion operation, such as {@code (SampleType) obj} in    * Java. For a numeric    * conversion, if the converted value does not fit the    * destination type, an exception is thrown.    */
name|ConvertChecked
block|,
comment|/**    * A division operation, such as (a / b), for numeric    * operands.    */
name|Divide
argument_list|(
literal|" / "
argument_list|,
literal|false
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * A percent remainder operation, such as (a % b), for numeric    * operands.    */
name|Mod
argument_list|(
literal|" % "
argument_list|,
literal|false
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * A node that represents an equality comparison, such as {@code a == b} in    * Java.    */
name|Equal
argument_list|(
literal|" == "
argument_list|,
literal|false
argument_list|,
literal|7
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * A bitwise or logical XOR operation, such as {@code a ^ b} in Java.    */
name|ExclusiveOr
argument_list|(
literal|" ^ "
argument_list|,
literal|false
argument_list|,
literal|9
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * A "greater than" comparison, such as (a&gt; b).    */
name|GreaterThan
argument_list|(
literal|"> "
argument_list|,
literal|false
argument_list|,
literal|6
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * A "greater than or equal to" comparison, such as (a&gt;=    * b).    */
name|GreaterThanOrEqual
argument_list|(
literal|">= "
argument_list|,
literal|false
argument_list|,
literal|6
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * An operation that invokes a delegate or lambda expression,    * such as sampleDelegate.Invoke().    */
name|Invoke
block|,
comment|/**    * A lambda expression, such as {@code a -> a + a} in Java.    */
name|Lambda
block|,
comment|/**    * A bitwise left-shift operation, such as {@code a<< b} in Java.    */
name|LeftShift
argument_list|(
literal|"<< "
argument_list|,
literal|false
argument_list|,
literal|5
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * A "less than" comparison, such as (a&lt; b).    */
name|LessThan
argument_list|(
literal|"< "
argument_list|,
literal|false
argument_list|,
literal|6
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * A "less than or equal to" comparison, such as (a&lt;= b).    */
name|LessThanOrEqual
argument_list|(
literal|"<= "
argument_list|,
literal|false
argument_list|,
literal|6
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * An operation that creates a new IEnumerable object and    * initializes it from a list of elements, such as new    * List&lt;SampleType&gt;(){ a, b, c } in C# or Dim sampleList = {    * a, b, c } in Visual Basic.    */
name|ListInit
block|,
comment|/**    * An operation that reads from a field or property, such as    * obj.SampleProperty.    */
name|MemberAccess
argument_list|(
literal|"."
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * An operation that creates a new object and initializes one    * or more of its members, such as new Point { X = 1, Y = 2 }    * in C# or New Point With {.X = 1, .Y = 2} in Visual    * Basic.    */
name|MemberInit
block|,
comment|/**    * An arithmetic remainder operation, such as (a % b) in C#    * or (a Mod b) in Visual Basic.    */
name|Modulo
argument_list|(
literal|" % "
argument_list|,
literal|false
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * A multiplication operation, such as (a * b), without    * overflow checking, for numeric operands.    */
name|Multiply
argument_list|(
literal|" * "
argument_list|,
literal|false
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * An multiplication operation, such as (a * b), that has    * overflow checking, for numeric operands.    */
name|MultiplyChecked
argument_list|(
literal|" * "
argument_list|,
literal|false
argument_list|,
literal|3
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * An arithmetic negation operation, such as (-a). The object    * a should not be modified in place.    */
name|Negate
argument_list|(
literal|"- "
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A unary plus operation, such as (+a). The result of a    * predefined unary plus operation is the value of the    * operand, but user-defined implementations might have    * unusual results.    */
name|UnaryPlus
argument_list|(
literal|"+ "
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * An arithmetic negation operation, such as (-a), that has    * overflow checking. The object a should not be modified in    * place.    */
name|NegateChecked
argument_list|(
literal|"-"
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * An operation that calls a constructor to create a new    * object, such as new SampleType().    */
name|New
block|,
comment|/**    * An operation that creates a new one-dimensional array and    * initializes it from a list of elements, such as new    * SampleType[]{a, b, c} in C# or New SampleType(){a, b, c} in    * Visual Basic.    */
name|NewArrayInit
block|,
comment|/**    * An operation that creates a new array, in which the bounds    * for each dimension are specified, such as new    * SampleType[dim1, dim2] in C# or New SampleType(dim1, dim2)    * in Visual Basic.    */
name|NewArrayBounds
block|,
comment|/**    * A bitwise complement or logical negation operation. In C#,    * it is equivalent to (~a) for integral types and to (!a) for    * Boolean values. In Visual Basic, it is equivalent to (Not    * a). The object a should not be modified in place.    */
name|Not
argument_list|(
literal|"!"
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * An inequality comparison, such as (a != b) in C# or (a&lt;&gt;    * b) in Visual Basic.    */
name|NotEqual
argument_list|(
literal|" != "
argument_list|,
literal|false
argument_list|,
literal|7
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * A bitwise or logical OR operation, such as (a | b) in C#    * or (a Or b) in Visual Basic.    */
name|Or
argument_list|(
literal|" | "
argument_list|,
literal|false
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * A short-circuiting conditional OR operation, such as (a ||    * b) in C# or (a OrElse b) in Visual Basic.    */
name|OrElse
argument_list|(
literal|" || "
argument_list|,
literal|false
argument_list|,
literal|12
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * A reference to a parameter or variable that is defined in    * the context of the expression. For more information, see    * ParameterExpression.    */
name|Parameter
block|,
comment|/**    * A mathematical operation that raises a number to a power,    * such as (a ^ b) in Visual Basic.    */
name|Power
block|,
comment|/**    * An expression that has a constant value of type    * Expression. A Quote node can contain references to    * parameters that are defined in the context of the    * expression it represents.    */
name|Quote
block|,
comment|/**    * A bitwise right-shift operation, such as (a&gt;*gt; b).    */
name|RightShift
argument_list|(
literal|">> "
argument_list|,
literal|false
argument_list|,
literal|5
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * A subtraction operation, such as (a - b), without overflow    * checking, for numeric operands.    */
name|Subtract
argument_list|(
literal|" - "
argument_list|,
literal|false
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * An arithmetic subtraction operation, such as (a - b), that    * has overflow checking, for numeric operands.    */
name|SubtractChecked
argument_list|(
literal|" - "
argument_list|,
literal|false
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * An explicit reference or boxing conversion in which null    * is supplied if the conversion fails, such as (obj as    * SampleType) in C# or TryCast(obj, SampleType) in Visual    * Basic.    */
name|TypeAs
block|,
comment|/**    * A type test, such as obj is SampleType in C# or TypeOf obj    * is SampleType in Visual Basic.    */
name|TypeIs
argument_list|(
literal|" instanceof "
argument_list|,
literal|false
argument_list|,
literal|6
argument_list|,
literal|false
argument_list|)
block|,
comment|/**    * An assignment operation, such as (a = b).    */
name|Assign
argument_list|(
literal|" = "
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|14
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A block of expressions.    */
name|Block
block|,
comment|/**    * Debugging information.    */
name|DebugInfo
block|,
comment|/**    * A unary decrement operation, such as (a - 1) in C# and    * Visual Basic. The object a should not be modified in    * place.    */
name|Decrement
block|,
comment|/**    * A dynamic operation.    */
name|Dynamic
block|,
comment|/**    * A default value.    */
name|Default
block|,
comment|/**    * An extension expression.    */
name|Extension
block|,
comment|/**    * A "go to" expression, such as goto Label in C# or GoTo    * Label in Visual Basic.    */
name|Goto
block|,
comment|/**    * A unary increment operation, such as (a + 1) in C# and    * Visual Basic. The object a should not be modified in    * place.    */
name|Increment
block|,
comment|/**    * An index operation or an operation that accesses a    * property that takes arguments.    */
name|Index
block|,
comment|/**    * A label.    */
name|Label
block|,
comment|/**    * A list of run-time variables. For more information, see    * RuntimeVariablesExpression.    */
name|RuntimeVariables
block|,
comment|/**    * A loop, such as for or while.    */
name|Loop
block|,
comment|/**    * A switch operation, such as switch in C# or Select Case in    * Visual Basic.    */
name|Switch
block|,
comment|/**    * An operation that throws an exception, such as throw new    * Exception().    */
name|Throw
block|,
comment|/**    * A try-catch expression.    */
name|Try
block|,
comment|/**    * An unbox value type operation, such as unbox and unbox.any    * instructions in MSIL.    */
name|Unbox
block|,
comment|/**    * An addition compound assignment operation, such as (a +=    * b), without overflow checking, for numeric operands.    */
name|AddAssign
argument_list|(
literal|" += "
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|14
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A bitwise or logical AND compound assignment operation,    * such as (a&amp;= b) in C#.    */
name|AndAssign
argument_list|(
literal|"&= "
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|14
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * An division compound assignment operation, such as (a /=    * b), for numeric operands.    */
name|DivideAssign
argument_list|(
literal|" /= "
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|14
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A bitwise or logical XOR compound assignment operation,    * such as (a ^= b) in C#.    */
name|ExclusiveOrAssign
argument_list|(
literal|" ^= "
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|14
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A bitwise left-shift compound assignment, such as (a&lt;&lt;=    * b).    */
name|LeftShiftAssign
argument_list|(
literal|"<<= "
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|14
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * An arithmetic remainder compound assignment operation,    * such as (a %= b) in C#.    */
name|ModuloAssign
argument_list|(
literal|" %= "
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|14
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A multiplication compound assignment operation, such as (a    * *= b), without overflow checking, for numeric operands.    */
name|MultiplyAssign
argument_list|(
literal|" *= "
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|14
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A bitwise or logical OR compound assignment, such as (a |=    * b) in C#.    */
name|OrAssign
argument_list|(
literal|" |= "
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|14
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A compound assignment operation that raises a number to a    * power, such as (a ^= b) in Visual Basic.    */
name|PowerAssign
block|,
comment|/**    * A bitwise right-shift compound assignment operation, such    * as (a&gt;&gt;= b).    */
name|RightShiftAssign
argument_list|(
literal|">>= "
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|14
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A subtraction compound assignment operation, such as (a -=    * b), without overflow checking, for numeric operands.    */
name|SubtractAssign
argument_list|(
literal|" -= "
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|14
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * An addition compound assignment operation, such as (a +=    * b), with overflow checking, for numeric operands.    */
name|AddAssignChecked
argument_list|(
literal|" += "
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|14
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A multiplication compound assignment operation, such as (a    * *= b), that has overflow checking, for numeric operands.    */
name|MultiplyAssignChecked
argument_list|(
literal|" *= "
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|14
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A subtraction compound assignment operation, such as (a -=    * b), that has overflow checking, for numeric operands.    */
name|SubtractAssignChecked
argument_list|(
literal|" -= "
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|14
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A unary prefix increment, such as (++a). The object a    * should be modified in place.    */
name|PreIncrementAssign
argument_list|(
literal|"++"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A unary prefix decrement, such as (--a). The object a    * should be modified in place.    */
name|PreDecrementAssign
argument_list|(
literal|"--"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A unary postfix increment, such as (a++). The object a    * should be modified in place.    */
name|PostIncrementAssign
argument_list|(
literal|"++"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A unary postfix decrement, such as (a--). The object a    * should be modified in place.    */
name|PostDecrementAssign
argument_list|(
literal|"--"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * An exact type test.    */
name|TypeEqual
block|,
comment|/**    * A ones complement operation, such as (~a) in C#.    */
name|OnesComplement
argument_list|(
literal|"~"
argument_list|,
literal|false
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
block|,
comment|/**    * A true condition value.    */
name|IsTrue
block|,
comment|/**    * A false condition value.    */
name|IsFalse
block|,
comment|/**    * Declaration of a variable.    */
name|Declaration
block|,
comment|/**    * For loop.    */
name|For
block|,
comment|/** For-each loop, "for (Type i : expression) body". */
name|ForEach
block|,
comment|/**    * While loop.    */
name|While
block|;
specifier|final
name|String
name|op
decl_stmt|;
specifier|final
name|String
name|op2
decl_stmt|;
specifier|final
name|boolean
name|postfix
decl_stmt|;
specifier|final
name|int
name|lprec
decl_stmt|;
specifier|final
name|int
name|rprec
decl_stmt|;
specifier|final
name|boolean
name|modifiesLvalue
decl_stmt|;
name|ExpressionType
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|ExpressionType
parameter_list|(
name|String
name|op
parameter_list|,
name|boolean
name|postfix
parameter_list|,
name|int
name|prec
parameter_list|,
name|boolean
name|right
parameter_list|)
block|{
name|this
argument_list|(
name|op
argument_list|,
literal|null
argument_list|,
name|postfix
argument_list|,
name|prec
argument_list|,
name|right
argument_list|)
expr_stmt|;
block|}
name|ExpressionType
parameter_list|(
name|String
name|op
parameter_list|,
name|String
name|op2
parameter_list|,
name|boolean
name|postfix
parameter_list|,
name|int
name|prec
parameter_list|,
name|boolean
name|right
parameter_list|)
block|{
name|this
argument_list|(
name|op
argument_list|,
name|op2
argument_list|,
name|postfix
argument_list|,
name|prec
argument_list|,
name|right
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|ExpressionType
parameter_list|(
name|String
name|op
parameter_list|,
name|String
name|op2
parameter_list|,
name|boolean
name|postfix
parameter_list|,
name|int
name|prec
parameter_list|,
name|boolean
name|right
parameter_list|,
name|boolean
name|modifiesLvalue
parameter_list|)
block|{
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
name|this
operator|.
name|op2
operator|=
name|op2
expr_stmt|;
name|this
operator|.
name|postfix
operator|=
name|postfix
expr_stmt|;
name|this
operator|.
name|modifiesLvalue
operator|=
name|modifiesLvalue
expr_stmt|;
name|this
operator|.
name|lprec
operator|=
operator|(
literal|20
operator|-
name|prec
operator|)
operator|*
literal|2
operator|+
operator|(
name|right
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
name|this
operator|.
name|rprec
operator|=
operator|(
literal|20
operator|-
name|prec
operator|)
operator|*
literal|2
operator|+
operator|(
name|right
condition|?
literal|0
else|:
literal|1
operator|)
expr_stmt|;
block|}
block|}
end_enum

end_unit

