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
name|util
package|;
end_package

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
name|Lists
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|LineNumberReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayDeque
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
import|;
end_import

begin_comment
comment|/**  * Streaming XML output.  *  *<p>Use this class to write XML to any streaming source.  * While the class itself is unstructured and doesn't enforce any DTD  * specification, use of the class  * does ensure that the output is syntactically valid XML.</p>  */
end_comment

begin_class
specifier|public
class|class
name|XmlOutput
block|{
comment|// This Writer is the underlying output stream to which all XML is
comment|// written.
specifier|private
specifier|final
name|PrintWriter
name|out
decl_stmt|;
comment|// The tagStack is maintained to check that tags are balanced.
specifier|private
specifier|final
name|Deque
argument_list|<
name|String
argument_list|>
name|tagStack
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
comment|// The class maintains an indentation level to improve output quality.
specifier|private
name|int
name|indent
decl_stmt|;
comment|// The class also maintains the total number of tags written.  This
comment|// is used to monitor changes to the output
specifier|private
name|int
name|tagsWritten
decl_stmt|;
comment|// This flag is set to true if the output should be compacted.
comment|// Compacted output is free of extraneous whitespace and is designed
comment|// for easier transport.
specifier|private
name|boolean
name|compact
decl_stmt|;
comment|/** @see #setIndentString */
specifier|private
name|String
name|indentString
init|=
literal|"\t"
decl_stmt|;
comment|/** @see #setGlob */
specifier|private
name|boolean
name|glob
decl_stmt|;
comment|/**    * Whether we have started but not finished a start tag. This only happens    * if<code>glob</code> is true. The start tag is automatically closed    * when we start a child node. If there are no child nodes, {@link #endTag}    * creates an empty tag.    */
specifier|private
name|boolean
name|inTag
decl_stmt|;
comment|/** @see #setAlwaysQuoteCData */
specifier|private
name|boolean
name|alwaysQuoteCData
decl_stmt|;
comment|/** @see #setIgnorePcdata */
specifier|private
name|boolean
name|ignorePcdata
decl_stmt|;
comment|/**    * Private helper function to display a degree of indentation    * @param out the PrintWriter to which to display output.    * @param indent the degree of indentation.    */
specifier|private
name|void
name|displayIndent
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|int
name|indent
parameter_list|)
block|{
if|if
condition|(
operator|!
name|compact
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indent
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|print
argument_list|(
name|indentString
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Constructs a new XmlOutput based on any {@link Writer}.    *    * @param out the writer to which this XmlOutput generates results.    */
specifier|public
name|XmlOutput
parameter_list|(
name|Writer
name|out
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|out
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a new XmlOutput based on a {@link PrintWriter}.    *    * @param out the writer to which this XmlOutput generates results.    */
specifier|public
name|XmlOutput
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|indent
operator|=
literal|0
expr_stmt|;
name|tagsWritten
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Sets or unsets the compact mode.  Compact mode causes the generated    * XML to be free of extraneous whitespace and other unnecessary    * characters.    *    * @param compact true to turn on compact mode, or false to turn it off.    */
specifier|public
name|void
name|setCompact
parameter_list|(
name|boolean
name|compact
parameter_list|)
block|{
name|this
operator|.
name|compact
operator|=
name|compact
expr_stmt|;
block|}
specifier|public
name|boolean
name|getCompact
parameter_list|()
block|{
return|return
name|compact
return|;
block|}
comment|/**    * Sets the string to print for each level of indentation. The default is a    * tab. The value must not be<code>null</code>. Set this to the empty    * string to achieve no indentation (note that    *<code>{@link #setCompact}(true)</code> removes indentation<em>and</em>    * newlines).    */
specifier|public
name|void
name|setIndentString
parameter_list|(
name|String
name|indentString
parameter_list|)
block|{
name|this
operator|.
name|indentString
operator|=
name|indentString
expr_stmt|;
block|}
comment|/**    * Sets whether to detect that tags are empty.    */
specifier|public
name|void
name|setGlob
parameter_list|(
name|boolean
name|glob
parameter_list|)
block|{
name|this
operator|.
name|glob
operator|=
name|glob
expr_stmt|;
block|}
comment|/**    * Sets whether to always quote cdata segments (even if they don't contain    * special characters).    */
specifier|public
name|void
name|setAlwaysQuoteCData
parameter_list|(
name|boolean
name|alwaysQuoteCData
parameter_list|)
block|{
name|this
operator|.
name|alwaysQuoteCData
operator|=
name|alwaysQuoteCData
expr_stmt|;
block|}
comment|/**    * Sets whether to ignore unquoted text, such as whitespace.    */
specifier|public
name|void
name|setIgnorePcdata
parameter_list|(
name|boolean
name|ignorePcdata
parameter_list|)
block|{
name|this
operator|.
name|ignorePcdata
operator|=
name|ignorePcdata
expr_stmt|;
block|}
specifier|public
name|boolean
name|getIgnorePcdata
parameter_list|()
block|{
return|return
name|ignorePcdata
return|;
block|}
comment|/**    * Sends a string directly to the output stream, without escaping any    * characters.  Use with caution!    */
specifier|public
name|void
name|print
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|/**    * Starts writing a new tag to the stream.  The tag's name must be given and    * its attributes should be specified by a fully constructed AttrVector    * object.    *    * @param tagName the name of the tag to write.    * @param attributes an XMLAttrVector containing the attributes to include    *   in the tag.    */
specifier|public
name|void
name|beginTag
parameter_list|(
name|String
name|tagName
parameter_list|,
name|XMLAttrVector
name|attributes
parameter_list|)
block|{
name|beginBeginTag
argument_list|(
name|tagName
argument_list|)
expr_stmt|;
if|if
condition|(
name|attributes
operator|!=
literal|null
condition|)
block|{
name|attributes
operator|.
name|display
argument_list|(
name|out
argument_list|,
name|indent
argument_list|)
expr_stmt|;
block|}
name|endBeginTag
argument_list|(
name|tagName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|beginBeginTag
parameter_list|(
name|String
name|tagName
parameter_list|)
block|{
if|if
condition|(
name|inTag
condition|)
block|{
comment|// complete the parent's start tag
if|if
condition|(
name|compact
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
name|inTag
operator|=
literal|false
expr_stmt|;
block|}
name|displayIndent
argument_list|(
name|out
argument_list|,
name|indent
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|tagName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|endBeginTag
parameter_list|(
name|String
name|tagName
parameter_list|)
block|{
if|if
condition|(
name|glob
condition|)
block|{
name|inTag
operator|=
literal|true
expr_stmt|;
block|}
if|else if
condition|(
name|compact
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|tagStack
operator|.
name|push
argument_list|(
name|tagName
argument_list|)
expr_stmt|;
name|indent
operator|++
expr_stmt|;
name|tagsWritten
operator|++
expr_stmt|;
block|}
comment|/**    * Writes an attribute.    */
specifier|public
name|void
name|attribute
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|printAtt
argument_list|(
name|out
argument_list|,
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * If we are currently inside the start tag, finishes it off.    */
specifier|public
name|void
name|beginNode
parameter_list|()
block|{
if|if
condition|(
name|inTag
condition|)
block|{
comment|// complete the parent's start tag
if|if
condition|(
name|compact
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
name|inTag
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/**    * Completes a tag.  This outputs the end tag corresponding to the    * last exposed beginTag.  The tag name must match the name of the    * corresponding beginTag.    * @param tagName the name of the end tag to write.    */
specifier|public
name|void
name|endTag
parameter_list|(
name|String
name|tagName
parameter_list|)
block|{
comment|// Check that the end tag matches the corresponding start tag
name|String
name|x
init|=
name|tagStack
operator|.
name|pop
argument_list|()
decl_stmt|;
assert|assert
name|x
operator|.
name|equals
argument_list|(
name|tagName
argument_list|)
assert|;
comment|// Lower the indent and display the end tag
name|indent
operator|--
expr_stmt|;
if|if
condition|(
name|inTag
condition|)
block|{
comment|// we're still in the start tag -- this element had no children
if|if
condition|(
name|compact
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"/>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"/>"
argument_list|)
expr_stmt|;
block|}
name|inTag
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|displayIndent
argument_list|(
name|out
argument_list|,
name|indent
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|tagName
argument_list|)
expr_stmt|;
if|if
condition|(
name|compact
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**    * Writes an empty tag to the stream.  An empty tag is one with no    * tags inside it, although it may still have attributes.    *    * @param tagName the name of the empty tag.    * @param attributes an XMLAttrVector containing the attributes to    * include in the tag.    */
specifier|public
name|void
name|emptyTag
parameter_list|(
name|String
name|tagName
parameter_list|,
name|XMLAttrVector
name|attributes
parameter_list|)
block|{
if|if
condition|(
name|inTag
condition|)
block|{
comment|// complete the parent's start tag
if|if
condition|(
name|compact
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
name|inTag
operator|=
literal|false
expr_stmt|;
block|}
name|displayIndent
argument_list|(
name|out
argument_list|,
name|indent
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|tagName
argument_list|)
expr_stmt|;
if|if
condition|(
name|attributes
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|attributes
operator|.
name|display
argument_list|(
name|out
argument_list|,
name|indent
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|compact
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"/>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"/>"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|tagsWritten
operator|++
expr_stmt|;
block|}
comment|/**    * Writes a CDATA section.  Such sections always appear on their own line.    * The nature in which the CDATA section is written depends on the actual    * string content with respect to these special characters/sequences:    *<ul>    *<li><code>&amp;</code>    *<li><code>&quot;</code>    *<li><code>'</code>    *<li><code>&lt;</code>    *<li><code>&gt;</code>    *</ul>    * Additionally, the sequence<code>]]&gt;</code> is special.    *<ul>    *<li>Content containing no special characters will be left as-is.    *<li>Content containing one or more special characters but not the    * sequence<code>]]&gt;</code> will be enclosed in a CDATA section.    *<li>Content containing special characters AND at least one    *<code>]]&gt;</code> sequence will be left as-is but have all of its    * special characters encoded as entities.    *</ul>    * These special treatment rules are required to allow cdata sections    * to contain XML strings which may themselves contain cdata sections.    * Traditional CDATA sections<b>do not nest</b>.    */
specifier|public
name|void
name|cdata
parameter_list|(
name|String
name|data
parameter_list|)
block|{
name|cdata
argument_list|(
name|data
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Writes a CDATA section (as {@link #cdata(String)}).    *    * @param data string to write    * @param quote if true, quote in a<code>&lt;![CDATA[</code>    *        ...<code>]]&gt;</code> regardless of the content of    *<code>data</code>; if false, quote only if the content needs it    */
specifier|public
name|void
name|cdata
parameter_list|(
name|String
name|data
parameter_list|,
name|boolean
name|quote
parameter_list|)
block|{
if|if
condition|(
name|inTag
condition|)
block|{
comment|// complete the parent's start tag
if|if
condition|(
name|compact
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
name|inTag
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
name|data
operator|=
literal|""
expr_stmt|;
block|}
name|boolean
name|specials
init|=
literal|false
decl_stmt|;
name|boolean
name|cdataEnd
init|=
literal|false
decl_stmt|;
comment|// Scan the string for special characters
comment|// If special characters are found, scan the string for ']]>'
if|if
condition|(
name|stringHasXMLSpecials
argument_list|(
name|data
argument_list|)
condition|)
block|{
name|specials
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|data
operator|.
name|contains
argument_list|(
literal|"]]>"
argument_list|)
condition|)
block|{
name|cdataEnd
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|// Display the result
name|displayIndent
argument_list|(
name|out
argument_list|,
name|indent
argument_list|)
expr_stmt|;
if|if
condition|(
name|quote
operator|||
name|alwaysQuoteCData
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<![CDATA["
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"]]>"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
operator|!
name|specials
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stringEncodeXML
argument_list|(
name|data
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|tagsWritten
operator|++
expr_stmt|;
block|}
comment|/**    * Writes a String tag; a tag containing nothing but a CDATA section.    */
specifier|public
name|void
name|stringTag
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|data
parameter_list|)
block|{
name|beginTag
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|cdata
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|endTag
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Writes content.    */
specifier|public
name|void
name|content
parameter_list|(
name|String
name|content
parameter_list|)
block|{
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
block|{
name|indent
operator|++
expr_stmt|;
name|LineNumberReader
name|in
init|=
operator|new
name|LineNumberReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|content
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|displayIndent
argument_list|(
name|out
argument_list|,
name|indent
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|ex
argument_list|)
throw|;
block|}
name|indent
operator|--
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|tagsWritten
operator|++
expr_stmt|;
block|}
comment|/**    *  Write header. Use default version 1.0.    */
specifier|public
name|void
name|header
parameter_list|()
block|{
name|out
operator|.
name|println
argument_list|(
literal|"<?xml version=\"1.0\" ?>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|tagsWritten
operator|++
expr_stmt|;
block|}
comment|/**    * Write header, take version as input.    */
specifier|public
name|void
name|header
parameter_list|(
name|String
name|version
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<?xml version=\""
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"\" ?>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|tagsWritten
operator|++
expr_stmt|;
block|}
comment|/**    * Get the total number of tags written    * @return the total number of tags written to the XML stream.    */
specifier|public
name|int
name|numTagsWritten
parameter_list|()
block|{
return|return
name|tagsWritten
return|;
block|}
comment|/** Print an XML attribute name and value for string val */
specifier|private
specifier|static
name|void
name|printAtt
parameter_list|(
name|PrintWriter
name|pw
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|!=
literal|null
comment|/*&& !val.equals("") */
condition|)
block|{
name|pw
operator|.
name|print
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"=\""
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
name|escapeForQuoting
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Encode a String for XML output, displaying it to a PrintWriter.    * The String to be encoded is displayed, except that    * special characters are converted into entities.    * @param input a String to convert.    * @param out a PrintWriter to which to write the results.    */
specifier|private
specifier|static
name|void
name|stringEncodeXML
parameter_list|(
name|String
name|input
parameter_list|,
name|PrintWriter
name|out
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|input
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|input
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'<'
case|:
case|case
literal|'>'
case|:
case|case
literal|'"'
case|:
case|case
literal|'\''
case|:
case|case
literal|'&'
case|:
case|case
literal|'\t'
case|:
case|case
literal|'\n'
case|:
case|case
literal|'\r'
case|:
name|out
operator|.
name|print
argument_list|(
literal|"&#"
operator|+
operator|(
name|int
operator|)
name|c
operator|+
literal|";"
argument_list|)
expr_stmt|;
break|break;
default|default:
name|out
operator|.
name|print
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|String
name|escapeForQuoting
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|StringEscaper
operator|.
name|XML_NUMERIC_ESCAPER
operator|.
name|escapeString
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/**    * Returns whether a string contains any XML special characters.    *    *<p>If this function returns true, the string will need to be    * encoded either using the stringEncodeXML function above or using a    * CDATA section.  Note that MSXML has a nasty bug whereby whitespace    * characters outside of a CDATA section are lost when parsing.  To    * avoid hitting this bug, this method treats many whitespace characters    * as "special".</p>    *    * @param input the String to scan for XML special characters.    * @return true if the String contains any such characters.    */
specifier|private
specifier|static
name|boolean
name|stringHasXMLSpecials
parameter_list|(
name|String
name|input
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|input
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|input
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'<'
case|:
case|case
literal|'>'
case|:
case|case
literal|'"'
case|:
case|case
literal|'\''
case|:
case|case
literal|'&'
case|:
case|case
literal|'\t'
case|:
case|case
literal|'\n'
case|:
case|case
literal|'\r'
case|:
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Utility for replacing special characters    * with escape sequences in strings.    *    *<p>A StringEscaper starts out as an identity transform in the "mutable"    * state.  Call {@link #defineEscape} as many times as necessary to set up    * mappings, and then call {@link #makeImmutable} before    * actually applying the defined transform.  Or,    * use one of the global mappings pre-defined here.</p>    */
specifier|static
class|class
name|StringEscaper
implements|implements
name|Cloneable
block|{
specifier|private
name|ArrayList
argument_list|<
name|String
argument_list|>
name|translationVector
decl_stmt|;
specifier|private
name|String
index|[]
name|translationTable
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|StringEscaper
name|XML_ESCAPER
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|StringEscaper
name|XML_NUMERIC_ESCAPER
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|StringEscaper
name|HTML_ESCAPER
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|StringEscaper
name|URL_ARG_ESCAPER
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|StringEscaper
name|URL_ESCAPER
decl_stmt|;
comment|/**      * Identity transform      */
specifier|public
name|StringEscaper
parameter_list|()
block|{
name|translationVector
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**      * Map character "from" to escape sequence "to"      */
specifier|public
name|void
name|defineEscape
parameter_list|(
name|char
name|from
parameter_list|,
name|String
name|to
parameter_list|)
block|{
name|int
name|i
init|=
operator|(
name|int
operator|)
name|from
decl_stmt|;
if|if
condition|(
name|i
operator|>=
name|translationVector
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// Extend list by adding the requisite number of nulls.
specifier|final
name|int
name|count
init|=
name|i
operator|+
literal|1
operator|-
name|translationVector
operator|.
name|size
argument_list|()
decl_stmt|;
name|translationVector
operator|.
name|addAll
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|nCopies
argument_list|(
name|count
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|translationVector
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|to
argument_list|)
expr_stmt|;
block|}
comment|/**      * Call this before attempting to escape strings; after this,      * defineEscape may not be called again.      */
specifier|public
name|void
name|makeImmutable
parameter_list|()
block|{
name|translationTable
operator|=
name|translationVector
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|translationVector
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|translationVector
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Apply an immutable transformation to the given string.      */
specifier|public
name|String
name|escapeString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
literal|null
decl_stmt|;
name|int
name|n
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|escape
decl_stmt|;
comment|// codes>= 128 (e.g. Euro sign) are always escaped
if|if
condition|(
name|c
operator|>
literal|127
condition|)
block|{
name|escape
operator|=
literal|"&#"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|c
argument_list|)
operator|+
literal|";"
expr_stmt|;
block|}
if|else if
condition|(
name|c
operator|>=
name|translationTable
operator|.
name|length
condition|)
block|{
name|escape
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|escape
operator|=
name|translationTable
index|[
name|c
index|]
expr_stmt|;
block|}
if|if
condition|(
name|escape
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|sb
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|sb
operator|==
literal|null
condition|)
block|{
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|(
name|n
operator|*
literal|2
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|escape
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sb
operator|==
literal|null
condition|)
block|{
return|return
name|s
return|;
block|}
else|else
block|{
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|protected
name|StringEscaper
name|clone
parameter_list|()
block|{
name|StringEscaper
name|clone
init|=
operator|new
name|StringEscaper
argument_list|()
decl_stmt|;
if|if
condition|(
name|translationVector
operator|!=
literal|null
condition|)
block|{
name|clone
operator|.
name|translationVector
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|translationVector
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|translationTable
operator|!=
literal|null
condition|)
block|{
name|clone
operator|.
name|translationTable
operator|=
name|translationTable
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
return|return
name|clone
return|;
block|}
comment|/**      * Create a mutable escaper from an existing escaper, which may      * already be immutable.      */
specifier|public
name|StringEscaper
name|getMutableClone
parameter_list|()
block|{
name|StringEscaper
name|clone
init|=
name|clone
argument_list|()
decl_stmt|;
if|if
condition|(
name|clone
operator|.
name|translationVector
operator|==
literal|null
condition|)
block|{
name|clone
operator|.
name|translationVector
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|clone
operator|.
name|translationTable
argument_list|)
expr_stmt|;
name|clone
operator|.
name|translationTable
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|clone
return|;
block|}
static|static
block|{
name|HTML_ESCAPER
operator|=
operator|new
name|StringEscaper
argument_list|()
expr_stmt|;
name|HTML_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|'&'
argument_list|,
literal|"&amp;"
argument_list|)
expr_stmt|;
name|HTML_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|'"'
argument_list|,
literal|"&quot;"
argument_list|)
expr_stmt|;
comment|//      htmlEscaper.defineEscape('\'',"&apos;");
name|HTML_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|'\''
argument_list|,
literal|"&#39;"
argument_list|)
expr_stmt|;
name|HTML_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|'<'
argument_list|,
literal|"&lt;"
argument_list|)
expr_stmt|;
name|HTML_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|'>'
argument_list|,
literal|"&gt;"
argument_list|)
expr_stmt|;
name|XML_NUMERIC_ESCAPER
operator|=
operator|new
name|StringEscaper
argument_list|()
expr_stmt|;
name|XML_NUMERIC_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|'&'
argument_list|,
literal|"&#38;"
argument_list|)
expr_stmt|;
name|XML_NUMERIC_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|'"'
argument_list|,
literal|"&#34;"
argument_list|)
expr_stmt|;
name|XML_NUMERIC_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|'\''
argument_list|,
literal|"&#39;"
argument_list|)
expr_stmt|;
name|XML_NUMERIC_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|'<'
argument_list|,
literal|"&#60;"
argument_list|)
expr_stmt|;
name|XML_NUMERIC_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|'>'
argument_list|,
literal|"&#62;"
argument_list|)
expr_stmt|;
name|URL_ARG_ESCAPER
operator|=
operator|new
name|StringEscaper
argument_list|()
expr_stmt|;
name|URL_ARG_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|'?'
argument_list|,
literal|"%3f"
argument_list|)
expr_stmt|;
name|URL_ARG_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|'&'
argument_list|,
literal|"%26"
argument_list|)
expr_stmt|;
name|URL_ESCAPER
operator|=
name|URL_ARG_ESCAPER
operator|.
name|getMutableClone
argument_list|()
expr_stmt|;
name|URL_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|'%'
argument_list|,
literal|"%%"
argument_list|)
expr_stmt|;
name|URL_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|'"'
argument_list|,
literal|"%22"
argument_list|)
expr_stmt|;
name|URL_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|'\r'
argument_list|,
literal|"+"
argument_list|)
expr_stmt|;
name|URL_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|'\n'
argument_list|,
literal|"+"
argument_list|)
expr_stmt|;
name|URL_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|' '
argument_list|,
literal|"+"
argument_list|)
expr_stmt|;
name|URL_ESCAPER
operator|.
name|defineEscape
argument_list|(
literal|'#'
argument_list|,
literal|"%23"
argument_list|)
expr_stmt|;
name|HTML_ESCAPER
operator|.
name|makeImmutable
argument_list|()
expr_stmt|;
name|XML_ESCAPER
operator|=
name|HTML_ESCAPER
expr_stmt|;
name|XML_NUMERIC_ESCAPER
operator|.
name|makeImmutable
argument_list|()
expr_stmt|;
name|URL_ARG_ESCAPER
operator|.
name|makeImmutable
argument_list|()
expr_stmt|;
name|URL_ESCAPER
operator|.
name|makeImmutable
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** List of attribute names and values. */
specifier|static
class|class
name|XMLAttrVector
block|{
specifier|public
name|void
name|display
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|int
name|indent
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End XmlOutput.java
end_comment

end_unit

