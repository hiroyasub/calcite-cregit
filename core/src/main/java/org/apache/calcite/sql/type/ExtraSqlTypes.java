begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
package|;
end_package

begin_comment
comment|/**  * Holds constants associated with SQL types introduced after the earliest  * version of Java supported by Farrago (this currently means anything  * introduced in JDK 1.6 or later).  *  * Allows us to deal sanely with type constants returned by newer JDBC  * drivers when running a version of Farrago compiled under an old  * version of the JDK (i.e. 1.5).  *  * By itself, the presence of a constant here doesn't imply that farrago  * fully supports the associated type.  This is simply a mirror of the  * missing constant values.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ExtraSqlTypes
block|{
comment|// From JDK 1.6
name|int
name|ROWID
init|=
operator|-
literal|8
decl_stmt|;
name|int
name|NCHAR
init|=
operator|-
literal|15
decl_stmt|;
name|int
name|NVARCHAR
init|=
operator|-
literal|9
decl_stmt|;
name|int
name|LONGNVARCHAR
init|=
operator|-
literal|16
decl_stmt|;
name|int
name|NCLOB
init|=
literal|2011
decl_stmt|;
name|int
name|SQLXML
init|=
literal|2009
decl_stmt|;
comment|// From JDK 1.8
name|int
name|REF_CURSOR
init|=
literal|2012
decl_stmt|;
name|int
name|TIME_WITH_TIMEZONE
init|=
literal|2013
decl_stmt|;
name|int
name|TIMESTAMP_WITH_TIMEZONE
init|=
literal|2014
decl_stmt|;
block|}
end_interface

begin_comment
comment|// End ExtraSqlTypes.java
end_comment

end_unit

