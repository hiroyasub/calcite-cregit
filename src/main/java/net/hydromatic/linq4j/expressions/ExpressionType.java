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
name|linq4j
operator|.
name|expressions
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
comment|/** An addition operation, such as a + b, without overflow      * checking, for numeric operands. */
name|Add
block|,
comment|/** An addition operation, such as (a + b), with overflow      * checking, for numeric operands. */
name|AddChecked
block|,
comment|/** A bitwise or logical AND operation, such as (a& b) in C# and      * (a And b) in Visual Basic. */
name|And
block|,
comment|/** A conditional AND operation that evaluates the second operand      * only if the first operand evaluates to true. It corresponds to      * (a&& b) in C# and (a AndAlso b) in Visual Basic. */
name|AndAlso
block|,
comment|/** An operation that obtains the length of a one-dimensional      * array, such as array.Length. */
name|ArrayLength
block|,
comment|/** An indexing operation in a one-dimensional array, such as      * array[index] in C# or array(index) in Visual Basic. */
name|ArrayIndex
block|,
comment|/** A method call, such as in the obj.sampleMethod()      * expression. */
name|Call
block|,
comment|/** A node that represents a null coalescing operation, such      * as (a ?? b) in C# or If(a, b) in Visual Basic. */
name|Coalesce
block|,
comment|/** A conditional operation, such as a> b ? a : b in C# or      * If(a> b, a, b) in Visual Basic. */
name|Conditional
block|,
comment|/** A constant value. */
name|Constant
block|,
comment|/** A cast or conversion operation, such as (SampleType)obj in      * C#or CType(obj, SampleType) in Visual Basic. For a numeric      * conversion, if the converted value is too large for the      * destination type, no exception is thrown. */
name|Convert
block|,
comment|/** A cast or conversion operation, such as (SampleType)obj in      * C#or CType(obj, SampleType) in Visual Basic. For a numeric      * conversion, if the converted value does not fit the      * destination type, an exception is thrown. */
name|ConvertChecked
block|,
comment|/** A division operation, such as (a / b), for numeric      * operands. */
name|Divide
block|,
comment|/** A node that represents an equality comparison, such as (a      * == b) in C# or (a = b) in Visual Basic. */
name|Equal
block|,
comment|/** A bitwise or logical XOR operation, such as (a ^ b) in C#      * or (a Xor b) in Visual Basic. */
name|ExclusiveOr
block|,
comment|/** A "greater than" comparison, such as (a> b). */
name|GreaterThan
block|,
comment|/** A "greater than or equal to" comparison, such as (a>=      * b). */
name|GreaterThanOrEqual
block|,
comment|/** An operation that invokes a delegate or lambda expression,      * such as sampleDelegate.Invoke(). */
name|Invoke
block|,
comment|/** A lambda expression, such as a => a + a in C# or      * Function(a) a + a in Visual Basic. */
name|Lambda
block|,
comment|/** A bitwise left-shift operation, such as (a<< b). */
name|LeftShift
block|,
comment|/** A "less than" comparison, such as (a< b). */
name|LessThan
block|,
comment|/** A "less than or equal to" comparison, such as (a<= b). */
name|LessThanOrEqual
block|,
comment|/** An operation that creates a new IEnumerable object and      * initializes it from a list of elements, such as new      * List<SampleType>(){ a, b, c } in C# or Dim sampleList = {      * a, b, c } in Visual Basic. */
name|ListInit
block|,
comment|/** An operation that reads from a field or property, such as      * obj.SampleProperty. */
name|MemberAccess
block|,
comment|/** An operation that creates a new object and initializes one      * or more of its members, such as new Point { X = 1, Y = 2 }      * in C# or New Point With {.X = 1, .Y = 2} in Visual      * Basic. */
name|MemberInit
block|,
comment|/** An arithmetic remainder operation, such as (a % b) in C#      * or (a Mod b) in Visual Basic. */
name|Modulo
block|,
comment|/** A multiplication operation, such as (a * b), without      * overflow checking, for numeric operands. */
name|Multiply
block|,
comment|/** An multiplication operation, such as (a * b), that has      * overflow checking, for numeric operands. */
name|MultiplyChecked
block|,
comment|/** An arithmetic negation operation, such as (-a). The object      * a should not be modified in place. */
name|Negate
block|,
comment|/** A unary plus operation, such as (+a). The result of a      * predefined unary plus operation is the value of the      * operand, but user-defined implementations might have      * unusual results. */
name|UnaryPlus
block|,
comment|/** An arithmetic negation operation, such as (-a), that has      * overflow checking. The object a should not be modified in      * place. */
name|NegateChecked
block|,
comment|/** An operation that calls a constructor to create a new      * object, such as new SampleType(). */
name|New
block|,
comment|/** An operation that creates a new one-dimensional array and      * initializes it from a list of elements, such as new      * SampleType[]{a, b, c} in C# or New SampleType(){a, b, c} in      * Visual Basic. */
name|NewArrayInit
block|,
comment|/** An operation that creates a new array, in which the bounds      * for each dimension are specified, such as new      * SampleType[dim1, dim2] in C# or New SampleType(dim1, dim2)      * in Visual Basic. */
name|NewArrayBounds
block|,
comment|/** A bitwise complement or logical negation operation. In C#,      * it is equivalent to (~a) for integral types and to (!a) for      * Boolean values. In Visual Basic, it is equivalent to (Not      * a). The object a should not be modified in place. */
name|Not
block|,
comment|/** An inequality comparison, such as (a != b) in C# or (a<>      * b) in Visual Basic. */
name|NotEqual
block|,
comment|/** A bitwise or logical OR operation, such as (a | b) in C#      * or (a Or b) in Visual Basic. */
name|Or
block|,
comment|/** A short-circuiting conditional OR operation, such as (a ||      * b) in C# or (a OrElse b) in Visual Basic. */
name|OrElse
block|,
comment|/** A reference to a parameter or variable that is defined in      * the context of the expression. For more information, see      * ParameterExpression. */
name|Parameter
block|,
comment|/** A mathematical operation that raises a number to a power,      * such as (a ^ b) in Visual Basic. */
name|Power
block|,
comment|/** An expression that has a constant value of type      * Expression. A Quote node can contain references to      * parameters that are defined in the context of the      * expression it represents. */
name|Quote
block|,
comment|/** A bitwise right-shift operation, such as (a>> b). */
name|RightShift
block|,
comment|/** A subtraction operation, such as (a - b), without overflow      * checking, for numeric operands. */
name|Subtract
block|,
comment|/** An arithmetic subtraction operation, such as (a - b), that      * has overflow checking, for numeric operands. */
name|SubtractChecked
block|,
comment|/** An explicit reference or boxing conversion in which null      * is supplied if the conversion fails, such as (obj as      * SampleType) in C# or TryCast(obj, SampleType) in Visual      * Basic. */
name|TypeAs
block|,
comment|/** A type test, such as obj is SampleType in C# or TypeOf obj      * is SampleType in Visual Basic. */
name|TypeIs
block|,
comment|/** An assignment operation, such as (a = b). */
name|Assign
block|,
comment|/** A block of expressions. */
name|Block
block|,
comment|/** Debugging information. */
name|DebugInfo
block|,
comment|/** A unary decrement operation, such as (a - 1) in C# and      * Visual Basic. The object a should not be modified in      * place. */
name|Decrement
block|,
comment|/** A dynamic operation. */
name|Dynamic
block|,
comment|/** A default value. */
name|Default
block|,
comment|/** An extension expression. */
name|Extension
block|,
comment|/** A "go to" expression, such as goto Label in C# or GoTo      * Label in Visual Basic. */
name|Goto
block|,
comment|/** A unary increment operation, such as (a + 1) in C# and      * Visual Basic. The object a should not be modified in      * place. */
name|Increment
block|,
comment|/** An index operation or an operation that accesses a      * property that takes arguments. */
name|Index
block|,
comment|/** A label. */
name|Label
block|,
comment|/** A list of run-time variables. For more information, see      * RuntimeVariablesExpression. */
name|RuntimeVariables
block|,
comment|/** A loop, such as for or while. */
name|Loop
block|,
comment|/** A switch operation, such as switch in C# or Select Case in      * Visual Basic. */
name|Switch
block|,
comment|/** An operation that throws an exception, such as throw new      * Exception(). */
name|Throw
block|,
comment|/** A try-catch expression. */
name|Try
block|,
comment|/** An unbox value type operation, such as unbox and unbox.any      * instructions in MSIL. */
name|Unbox
block|,
comment|/** An addition compound assignment operation, such as (a +=      * b), without overflow checking, for numeric operands. */
name|AddAssign
block|,
comment|/** A bitwise or logical AND compound assignment operation,      * such as (a&= b) in C#. */
name|AndAssign
block|,
comment|/** An division compound assignment operation, such as (a /=      * b), for numeric operands. */
name|DivideAssign
block|,
comment|/** A bitwise or logical XOR compound assignment operation,      * such as (a ^= b) in C#. */
name|ExclusiveOrAssign
block|,
comment|/** A bitwise left-shift compound assignment, such as (a<<=      * b). */
name|LeftShiftAssign
block|,
comment|/** An arithmetic remainder compound assignment operation,      * such as (a %= b) in C#. */
name|ModuloAssign
block|,
comment|/** A multiplication compound assignment operation, such as (a      * *= b), without overflow checking, for numeric operands. */
name|MultiplyAssign
block|,
comment|/** A bitwise or logical OR compound assignment, such as (a |=      * b) in C#. */
name|OrAssign
block|,
comment|/** A compound assignment operation that raises a number to a      * power, such as (a ^= b) in Visual Basic. */
name|PowerAssign
block|,
comment|/** A bitwise right-shift compound assignment operation, such      * as (a>>= b). */
name|RightShiftAssign
block|,
comment|/** A subtraction compound assignment operation, such as (a -=      * b), without overflow checking, for numeric operands. */
name|SubtractAssign
block|,
comment|/** An addition compound assignment operation, such as (a +=      * b), with overflow checking, for numeric operands. */
name|AddAssignChecked
block|,
comment|/** A multiplication compound assignment operation, such as (a      * *= b), that has overflow checking, for numeric operands. */
name|MultiplyAssignChecked
block|,
comment|/** A subtraction compound assignment operation, such as (a -=      * b), that has overflow checking, for numeric operands. */
name|SubtractAssignChecked
block|,
comment|/** A unary prefix increment, such as (++a). The object a      * should be modified in place. */
name|PreIncrementAssign
block|,
comment|/** A unary prefix decrement, such as (--a). The object a      * should be modified in place. */
name|PreDecrementAssign
block|,
comment|/** A unary postfix increment, such as (a++). The object a      * should be modified in place. */
name|PostIncrementAssign
block|,
comment|/** A unary postfix decrement, such as (a--). The object a      * should be modified in place. */
name|PostDecrementAssign
block|,
comment|/** An exact type test. */
name|TypeEqual
block|,
comment|/** A ones complement operation, such as (~a) in C#. */
name|OnesComplement
block|,
comment|/** A true condition value. */
name|IsTrue
block|,
comment|/** A false condition value. */
name|IsFalse
block|,  }
end_enum

begin_comment
comment|// End ExpressionType.java
end_comment

end_unit

