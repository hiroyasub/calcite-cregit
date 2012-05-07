begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * A collection of terms.  *  *<p>(This is not a real class. It is here so that terms which do not map to  * classes can be referenced in Javadoc.)</p>  *  * @author jhyde  * @version $Id$  * @since Nov 28, 2003  */
end_comment

begin_class
specifier|public
class|class
name|Glossary
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**      *<p>This table shows how and where the Gang of Four patterns are applied.      * The table uses information from the GoF book and from a course on      * advanced object design taught by Craig Larman.</p>      *      *<p>The patterns are in three groups depicting frequency of use. The *      * patterns in<b><font color="lime">light green</font></b> are used<i>      * frequently</i>. Those in<b><font color="#ffff00">yellow</font></b> have      *<i>moderate</i> use. Patterns in<b><font color="red">red</font></b> are      *<i>infrequently</i> used. The GoF column gives the original Gang Of Four      * category for the pattern. The Problem and Pattern columns are from      * Craig's refinement of the type of problems they apply to and a refinement      * of the original three pattern categories.</p>      *      *<table cellSpacing="0" cols="6" cellPadding="3" border="1">      *<caption align="bottom"><a      * href="http://www.onr.com/user/loeffler/java/references.html#gof"><b>Gang      * of Four Patterns</b></a></caption>      *<tr>      *<!-- Headers for each column -->      *      *<th>Pattern Name</th>      *<th align="middle"><a href="#category">GOF Category</a></th>      *<th align="middle">Problem</th>      *<th align="middle">Pattern</th>      *<th align="middle">Often Uses</th>      *<th align="middle">Related To</th>      *</tr>      *<!-- Frequently used patterns have a lime background -->      *<tr>      *<td bgColor="lime"><a href="#AbstractFactoryPattern">Abstract Factory</a>      *</td>      *<td bgColor="teal"><font color="white">Creational</font></td>      *<td>Creating Instances</td>      *<td>Class/Interface Definition plus Inheritance</td>      *<td><a href="#FactoryMethodPattern">Factory Method</a><br>      *<a href="#PrototypePattern">Prototype</a><br>      *<a href="#SingletonPattern">Singleton</a> with<a href="#FacadePattern">      * Facade</a></td>      *<td><a href="#FactoryMethodPattern">Factory Method</a><br>      *<a href="#PrototypePattern">Prototype</a><br>      *<a href="#SingletonPattern">Singleton</a></td>      *</tr>      *<tr>      *<td bgColor="lime"><a href="#ObjectAdapterPattern">Object Adapter</a>      *</td>      *<td bgColor="silver">Structural</td>      *<td>Interface</td>      *<td>Wrap One</td>      *<td align="middle">-</td>      *<td><a href="#BridgePattern">Bridge</a><br>      *<a href="#DecoratorPattern">Decorator</a><br>      *<a href="#ProxyPattern">Proxy</a></td>      *</tr>      *<tr>      *<td bgColor="lime"><a href="#CommandPattern">Command</a></td>      *<td bgColor="maroon"><font color="white">Behavioral</font></td>      *<td>Organization or Communication of Work<br>      * Action/Response</td>      *<td>Behavior Objects</td>      *<td><a href="#CompositePattern">Composite</a></td>      *<td><a href="#CompositePattern">Composite</a><br>      *<a href="#MementoPattern">Memento</a><br>      *<a href="#PrototypePattern">Prototype</a></td>      *</tr>      *<tr>      *<td bgColor="lime"><a href="#CompositePattern">Composite</a></td>      *<td bgColor="silver">Structural</td>      *<td>Structural Decomposition of Objects or Subsystems</td>      *<td>Wrap Many</td>      *<td align="middle">-</td>      *<td><a href="#DecoratorPattern">Decorator</a><br>      *<a href="#IteratorPattern">Iterator</a><br>      *<a href="#VisitorPattern">Visitor</a></td>      *</tr>      *<tr>      *<td bgColor="lime"><a href="#DecoratorPattern">Decorator</a></td>      *<td bgColor="silver">Structural</td>      *<td>Instance Behavior</td>      *<td>Wrap One</td>      *<td align="middle">-</td>      *<td><a href="#ObjectAdapterPattern">Object Adapter</a><br>      *<a href="#CompositePattern">Composite</a><br>      *<a href="#StrategyPattern">Strategy</a></td>      *</tr>      *<tr>      *<td bgColor="lime"><a href="#FacadePattern">Facade</a></td>      *<td bgColor="silver">Structural</td>      *<td>Access Control<br>      *&*nbsp;      *<hr>      *<p>Structural Decomposition of Objects or Subsystems</td>      *<td>Wrap Many</td>      *<td><a href="#SingletonPattern">Singleton</a> with<a      * href="#AbstractFactoryPattern">Abstract Factory</a></td>      *<td><a href="#AbstractFactoryPattern">Abstract Factory</a><br>      *<a href="#MediatorPattern">Mediator</a></td>      *</tr>      *<tr>      *<td bgColor="lime"><a href="#FlyweightPattern">Flyweight</a></td>      *<td bgColor="silver">Structural</td>      *<td>Shared Resource Handling</td>      *<td>Object State or Values</td>      *<td align="middle">-</td>      *<td><a href="#SingletonPattern">Singleton</a><br>      *<a href="#StatePattern">State</a><br>      *<a href="#StrategyPattern">Strategy</a><br>      * Shareable</td>      *</tr>      *<tr>      *<td bgColor="lime"><a href="#IteratorPattern">Iterator</a></td>      *<td bgColor="maroon"><font color="white">Behavioral</font></td>      *<td>Traversal Algorithm<br>      *&*nbsp;      *<hr>      *<p>Access Control</td>      *<td>Low Coupling</td>      *<td align="middle">-</td>      *<td><a href="#CompositePattern">Composite</a><br>      *<a href="#FactoryMethodPattern">Factory Method</a><br>      *<a href="#MementoPattern">Memento</a></td>      *</tr>      *<tr>      *<td bgColor="lime"><a href="#ObserverPattern">Observer</a></td>      *<td bgColor="maroon"><font color="white">Behavioral</font></td>      *<td>Event Response<br>      *&*nbsp;      *<hr>      *<p>Organization or Communication of Work</td>      *<td>Low Coupling</td>      *<td align="middle">-</td>      *<td><a href="#MediatorPattern">Mediator</a><br>      *<a href="#SingletonPattern">Singleton</a></td>      *</tr>      *<tr>      *<td bgColor="lime"><a href="#ProxyPattern">Proxy</a></td>      *<td bgColor="silver">Structural</td>      *<td>Access Control</td>      *<td>Wrap One</td>      *<td align="middle">-</td>      *<td><a href="#ObjectAdapterPattern">Adapter</a><br>      *<a href="#DecoratorPattern">Decorator</a></td>      *</tr>      *<tr>      *<td bgColor="lime"><a href="#SingletonPattern">Singleton</a></td>      *<td bgColor="teal"><font color="white">Creational</font></td>      *<td>Access Control</td>      *<td>Other</td>      *<td align="middle">-</td>      *<td><a href="#AbstractFactoryPattern">Abstract Factory</a><br>      *<a href="#BuilderPattern">Builder</a><br>      *<a href="#PrototypePattern">Prototype</a></td>      *</tr>      *<tr>      *<td bgColor="lime"><a href="#StatePattern">State</a></td>      *<td bgColor="maroon"><font color="white">Behavioral</font></td>      *<td>Instance Behavior</td>      *<td>Object State or Values</td>      *<td><a href="#FlyweightPattern">Flyweight</a></td>      *<td><a href="#FlyweightPattern">Flyweight</a><br>      *<a href="#SingletonPattern">Singleton</a></td>      *</tr>      *<tr>      *<td bgColor="lime"><a href="#StrategyPattern">Strategy</a></td>      *<td bgColor="maroon"><font color="white">Behavioral</font></td>      *<td>Single Algorithm</td>      *<td>Behavior Objects</td>      *<td align="middle">-</td>      *<td><a href="#FlyweightPattern">Flyweight</a><br>      *<a href="#StatePattern">State</a><br>      *<a href="#TemplateMethodPattern">Template Method</a></td>      *</tr>      *<tr>      *<td bgColor="lime"><a href="#TemplateMethodPattern">Template Method</a>      *</td>      *<td bgColor="maroon"><font color="white">Behavioral</font></td>      *<td>Single Algorithm</td>      *<td>Class or Interface Definition plus Inheritance</td>      *<td align="middle">-</td>      *<td><a href="#StrategyPattern">Strategy</a></td>      *</tr>      *<!-- Moderately use patterns have a yellow background -->      *<tr>      *<td bgColor="yellow"><a href="#ClassAdapterPattern">Class Adapter</a>      *</td>      *<td bgColor="silver">Structural</td>      *<td>Interface</td>      *<td>Class or Interface Definition plus Inheritance</td>      *<td align="middle">-</td>      *<td><a href="#BridgePattern">Bridge</a><br>      *<a href="#DecoratorPattern">Decorator</a><br>      *<a href="#ProxyPattern">Proxy</a></td>      *</tr>      *<tr>      *<td bgColor="yellow"><a href="#BridgePattern">Bridge</a></td>      *<td bgColor="silver">Structural</td>      *<td>Implementation</td>      *<td>Wrap One</td>      *<td align="middle">-</td>      *<td><a href="#AbstractFactoryPattern">Abstract Factory</a><br>      *<a href="#ClassAdapterPattern">Class Adaptor</a></td>      *</tr>      *<tr>      *<td bgColor="yellow"><a href="#BuilderPattern">Builder</a></td>      *<td bgColor="teal"><font color="white">Creational</font></td>      *<td>Creating Structures</td>      *<td>Class or Interface Definition plus Inheritance</td>      *<td align="middle">-</td>      *<td><a href="#AbstractFactoryPattern">Abstract Factory</a><br>      *<a href="#CompositePattern">Composite</a></td>      *</tr>      *<tr>      *<td bgColor="yellow"><a href="#ChainOfResponsibilityPattern">Chain of      * Responsibility</a></td>      *<td bgColor="maroon"><font color="white">Behavioral</font></td>      *<td>Single Algorithm<br>      *&*nbsp;      *<hr>      *<p>Organization or Communication of Work</td>      *<td>Low Coupling</td>      *<td align="middle">-</td>      *<td><a href="#CompositePattern">Composite</a></td>      *</tr>      *<tr>      *<td bgColor="yellow"><a href="#FactoryMethodPattern">Factory Method</a>      *</td>      *<td bgColor="teal"><font color="white">Creational</font></td>      *<td>Creating Instances</td>      *<td>Class or Interface Definition plus Inheritance</td>      *<td><a href="#TemplateMethodPattern">Template Method</a></td>      *<td><a href="#AbstractFactoryPattern">Abstract Factory</a><br>      *<a href="#TemplateMethodPattern">Template Method</a><br>      *<a href="#PrototypePattern">Prototype</a></td>      *</tr>      *<tr>      *<td bgColor="yellow"><a href="#MediatorPattern">Mediator</a></td>      *<td bgColor="maroon"><font color="white">Behavioral</font></td>      *<td>Interaction between Objects<br>      *&*nbsp;      *<hr>      *<p>Organization or Communication of Work</td>      *<td>Low Coupling</td>      *<td align="middle">-</td>      *<td><a href="#FacadePattern">Facade</a><br>      *<a href="#ObserverPattern">Observer</a></td>      *</tr>      *<tr>      *<td bgColor="yellow"><a href="#PrototypePattern">Prototype</a></td>      *<td bgColor="teal"><font color="white">Creational</font></td>      *<td>Creating Instances</td>      *<td>Other</td>      *<td align="middle">-</td>      *<td><a href="#PrototypePattern">Prototype</a><br>      *<a href="#CompositePattern">Composite</a><br>      *<a href="#DecoratorPattern">Decorator</a></td>      *</tr>      *<tr>      *<td bgColor="yellow"><a href="#VisitorPattern">Visitor</a></td>      *<td bgColor="maroon"><font color="white">Behavioral</font></td>      *<td>Single Algorithm</td>      *<td>Behavior Objects</td>      *<td align="middle">-</td>      *<td><a href="#CompositePattern">Composite</a><br>      *<a href="#VisitorPattern">Visitor</a></td>      *</tr>      *<!-- Seldom used patterns have a red background -->      *<tr>      *<td bgColor="red"><a href="#InterpreterPattern"><font color="white">      * Interpreter</font></a></td>      *<td bgColor="maroon"><font color="white">Behavioral</font></td>      *<td>Organization or Communication of Work</td>      *<td>Other</td>      *<td align="middle">-</td>      *<td><a href="#CompositePattern">Composite</a><br>      *<a href="#FlyweightPattern">Flyweight</a><br>      *<a href="#IteratorPattern">Iterator</a><br>      *<a href="#VisitorPattern">Visitor</a></td>      *</tr>      *<tr>      *<td bgColor="red"><a href="#MementoPattern"><font color="white">      * Memento</font></a></td>      *<td bgColor="maroon"><font color="white">Behavioral</font></td>      *<td>Instance Management</td>      *<td>Object State or Values</td>      *<td align="middle">-</td>      *<td><a href="#CommandPattern">Command</a><br>      *<a href="#IteratorPattern">Iterator</a></td>      *</tr>      *</table>      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|Pattern
init|=
literal|null
decl_stmt|;
comment|/**      * Provide an interface for creating families of related or dependent      * objects without specifying their concrete classes. (See<a      * href="http://c2.com/cgi/wiki?AbstractFactoryPattern">GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|AbstractFactoryPattern
init|=
literal|null
decl_stmt|;
comment|/**      * Separate the construction of a complex object from its representation so      * that the same construction process can create different representations.      * (See<a href="http://c2.com/cgi/wiki?BuilderPattern">GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|BuilderPattern
init|=
literal|null
decl_stmt|;
comment|/**      * Define an interface for creating an object, but let subclasses decide      * which class to instantiate. Lets a class defer instantiation to      * subclasses. (See<a href="http://c2.com/cgi/wiki?FactoryMethodPattern">      * GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|FactoryMethodPattern
init|=
literal|null
decl_stmt|;
comment|/**      * Specify the kinds of objects to create using a prototypical instance, and      * create new objects by copying this prototype. (See<a      * href="http://c2.com/cgi/wiki?PrototypePattern">GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|PrototypePattern
init|=
literal|null
decl_stmt|;
comment|/**      * Ensure a class only has one instance, and provide a global point of      * access to it. (See<a href="http://c2.com/cgi/wiki?SingletonPattern">      * GoF</a>.)      *      *<p>Note that a common way of implementing a singleton, the so-called<a      * href="http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html">      * double-checked locking pattern</a>, is fatally flawed in Java. Don't use      * it!</p>      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|SingletonPattern
init|=
literal|null
decl_stmt|;
comment|/**      * Convert the interface of a class into another interface clients expect.      * Lets classes work together that couldn't otherwise because of      * incompatible interfaces. (See<a      * href="http://c2.com/cgi/wiki?AdapterPattern">GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|AdapterPattern
init|=
literal|null
decl_stmt|;
comment|/**      * Decouple an abstraction from its implementation so that the two can very      * independently. (See<a href="http://c2.com/cgi/wiki?BridgePattern">      * GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|BridgePattern
init|=
literal|null
decl_stmt|;
comment|/**      * Compose objects into tree structures to represent part-whole hierarchies.      * Lets clients treat individual objects and compositions of objects      * uniformly. (See<a href="http://c2.com/cgi/wiki?CompositePattern">      * GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|CompositePattern
init|=
literal|null
decl_stmt|;
comment|/**      * Attach additional responsibilities to an object dynamically. Provides a      * flexible alternative to subclassing for extending functionality. (See<a      * href="http://c2.com/cgi/wiki?DecoratorPattern">GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|DecoratorPattern
init|=
literal|null
decl_stmt|;
comment|/**      * Provide a unified interface to a set of interfaces in a subsystem.      * Defines a higher-level interface that makes the subsystem easier to use.      * (See<a href="http://c2.com/cgi/wiki?FacadePattern">GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|FacadePattern
init|=
literal|null
decl_stmt|;
comment|/**      * Use sharing to support large numbers of fine-grained objects efficiently.      * (See<a href="http://c2.com/cgi/wiki?FlyweightPattern">GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|FlyweightPattern
init|=
literal|null
decl_stmt|;
comment|/**      * Provide a surrogate or placeholder for another object to control access      * to it. (See<a href="http://c2.com/cgi/wiki?ProxyPattern">GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|ProxyPattern
init|=
literal|null
decl_stmt|;
comment|/**      * Avoid coupling the sender of a request to its receiver by giving more      * than one object a chance to handle the request. Chain the receiving      * objects and pass the request along the chain until an object handles it.      * (See<a href="http://c2.com/cgi/wiki?ChainOfResponsibilityPattern">      * GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|ChainOfResponsibilityPattern
init|=
literal|null
decl_stmt|;
comment|/**      * Encapsulate a request as an object, thereby letting you parameterize      * clients with different requests, queue or log requests, and support      * undoable operations. (See<a      * href="http://c2.com/cgi/wiki?CommandPattern">GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|CommandPattern
init|=
literal|null
decl_stmt|;
comment|/**      * Given a language, define a representation for its grammar along with an      * interpreter that uses the representation to interpret sentences in the      * language. (See<a href="http://c2.com/cgi/wiki?InterpreterPattern">      * GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|InterpreterPattern
init|=
literal|null
decl_stmt|;
comment|/**      * Provide a way to access the elements of an aggregate object sequentially      * without exposing its underlying representation. (See<a      * href="http://c2.com/cgi/wiki?IteratorPattern">GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|IteratorPattern
init|=
literal|null
decl_stmt|;
comment|/**      * Define an object that encapsulates how a set of objects interact.      * Promotes loose coupling by keeping objects from referring to each other      * explicitly, and it lets you vary their interaction independently. (See<a      * href="http://c2.com/cgi/wiki?MediatorPattern">GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|MediatorPattern
init|=
literal|null
decl_stmt|;
comment|/**      * Without violating encapsulation, capture and externalize an objects's      * internal state so that the object can be restored to this state later.      * (See<a href="http://c2.com/cgi/wiki?MementoPattern">GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|MementoPattern
init|=
literal|null
decl_stmt|;
comment|/**      * Define a one-to-many dependency between objects so that when one object      * changes state, all its dependents are notified and updated automatically.      * (See<a href="http://c2.com/cgi/wiki?ObserverPattern">GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|ObserverPattern
init|=
literal|null
decl_stmt|;
comment|/**      * Allow an object to alter its behavior when its internal state changes.      * The object will appear to change its class. (See<a      * href="http://c2.com/cgi/wiki?StatePattern">GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|StatePattern
init|=
literal|null
decl_stmt|;
comment|/**      * Define a family of algorithms, encapsulate each one, and make them      * interchangeable. Lets the algorithm vary independently from clients that      * use it. (See<a href="http://c2.com/cgi/wiki?StrategyPattern">GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|StrategyPattern
init|=
literal|null
decl_stmt|;
comment|/**      * Define the skeleton of an algorithm in an operation, deferring some steps      * to subclasses. Lets subclasses redefine certain steps of an algorithm      * without changing the algorithm's structure. (See<a      * href="http://c2.com/cgi/wiki?TemplateMethodPattern">GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|TemplateMethodPattern
init|=
literal|null
decl_stmt|;
comment|/**      * Represent an operation to be performed on the elments of an object      * structure. Lets you define a new operation without changing the classes      * of the elements on which it operates. (See<a      * href="http://c2.com/cgi/wiki?VisitorPattern">GoF</a>.)      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|VisitorPattern
init|=
literal|null
decl_stmt|;
comment|/**      * The official SQL-92 standard (ISO/IEC 9075:1992). To reference this      * standard from methods that implement its rules, use the&#64;sql.92      * custom block tag in Javadoc comments; for the tag body, use the format      *<code>&lt;SectionId&gt; [ ItemType&lt;ItemId&gt; ]</code>, where      *      *<ul>      *<li><code>SectionId</code> is the numbered or named section in the table      * of contents, e.g. "Section 4.18.9" or "Annex A"      *<li><code>ItemType</code> is one of { Table, Syntax Rule, Access Rule,      * General Rule, or Leveling Rule }      *<li><code>ItemId</code> is a dotted path expression to the specific item      *</ul>      *      * For example,      *      *<pre><code>&#64;sql.92 Section 11.4 Syntax Rule 7.c      *</code></pre>      *      * is a well-formed reference to the rule for the default character set to      * use for column definitions of character type.      *      *<p>Note that this tag is a block tag (like&#64;see) and cannot be used      * inline.      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|Sql92
init|=
literal|null
decl_stmt|;
comment|/**      * The official SQL:1999 standard (ISO/IEC 9075:1999), which is broken up      * into five parts. To reference this standard from methods that implement      * its rules, use the&#64;sql.99 custom block tag in Javadoc comments; for      * the tag body, use the format<code>&lt;PartId&gt;&lt;SectionId&gt; [      * ItemType&lt;ItemId&gt; ]</code>, where      *      *<ul>      *<li><code>PartId</code> is the numbered part (up to Part 5)      *<li><code>SectionId</code> is the numbered or named section in the part's      * table of contents, e.g. "Section 4.18.9" or "Annex A"      *<li><code>ItemType</code> is one of { Table, Syntax Rule, Access Rule,      * General Rule, or Conformance Rule }      *<li><code>ItemId</code> is a dotted path expression to the specific item      *</ul>      *      * For example,      *      *<pre><code>&#64;sql.99 Part 2 Section 11.4 Syntax Rule 7.b      *</code></pre>      *      * is a well-formed reference to the rule for the default character set to      * use for column definitions of character type.      *      *<p>Note that this tag is a block tag (like&#64;see) and cannot be used      * inline.      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|Sql99
init|=
literal|null
decl_stmt|;
comment|/**      * The official SQL:2003 standard (ISO/IEC 9075:2003), which is broken up      * into numerous parts. To reference this standard from methods that      * implement its rules, use the&#64;sql.2003 custom block tag in Javadoc      * comments; for the tag body, use the format<code>&lt;PartId&gt;      *&lt;SectionId&gt; [ ItemType&lt;ItemId&gt; ]</code>, where      *      *<ul>      *<li><code>PartId</code> is the numbered part      *<li><code>SectionId</code> is the numbered or named section in the part's      * table of contents, e.g. "Section 4.11.2" or "Annex A"      *<li><code>ItemType</code> is one of { Table, Syntax Rule, Access Rule,      * General Rule, or Conformance Rule }      *<li><code>ItemId</code> is a dotted path expression to the specific item      *</ul>      *      * For example,      *      *<pre><code>&#64;sql.2003 Part 2 Section 11.4 Syntax Rule 10.b      *</code></pre>      *      * is a well-formed reference to the rule for the default character set to      * use for column definitions of character type.      *      *<p>Note that this tag is a block tag (like&#64;see) and cannot be used      * inline.      */
specifier|public
specifier|static
specifier|final
name|Glossary
name|Sql2003
init|=
literal|null
decl_stmt|;
block|}
end_class

begin_comment
comment|// End Glossary.java
end_comment

end_unit

