/**
 * Mule Loggly Connector
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.loggly.test

import javax.ws.rs._
import javax.ws.rs.core._
import java.io.InputStream
import java.lang.String

@Path("/muletest.loggly.com/api")
class APIMock {
  @GET
  @Produces(Array("application/text"))
  @Path("/search")
  def search(@Context info: UriInfo) : InputStream = {
    println("search called with: " + info.getQueryParameters)
    this.getClass.getClassLoader.getResourceAsStream("search.json")
  }

  @GET
  @Produces(Array("application/text"))
  @Path("/facets/{facet_type}")
  def facet(@Context info: UriInfo, @PathParam("facet_type") facetType: String) : InputStream = {
    println("facet called with: " + info.getQueryParameters)
    this.getClass.getClassLoader.getResourceAsStream("facet.json")
  }

  @GET
  @Produces(Array("application/text"))
  @Path("/inputs/{id}")
  def input(@Context info: UriInfo, @PathParam("id") id : Int) : InputStream = {
    println("input called with: " + info.getQueryParameters)
    this.getClass.getClassLoader.getResourceAsStream("input.json")
  }

  @GET
  @Produces(Array("application/text"))
  @Path("/inputs")
  def input(@Context info: UriInfo) : InputStream = {
    val queryParams = info.getQueryParameters
    val name = queryParams.get("name")

    println("input called with: " + info.getQueryParameters)

    name match {
      case null => this.getClass.getClassLoader.getResourceAsStream("inputs.json")
      case _ => this.getClass.getClassLoader.getResourceAsStream("inputByName.json")
    }
  }

  @POST
  @Produces(Array("application/text"))
  @Path("/inputs/{id}/adddevice")
  def addCurrentDevice(@PathParam("id") id : Int ) : InputStream = {
    println("addCurrentDevice called with: " + id)
    this.getClass.getClassLoader.getResourceAsStream("empty.json")
  }

  @POST
  @Produces(Array("application/text"))
  @Path("inputs/{id}/add514/")
  def addCurrentDeviceSyslog514(@PathParam("id") id : Int ) : InputStream = {
    println("addCurrentDeviceSyslog514 called with: " + id)
    this.getClass.getClassLoader.getResourceAsStream("empty.json")
  }

  @POST
  @Produces(Array("application/text"))
  @Path("/inputs/{id}/removedevice")
  def removeDevice(@PathParam("id") id : Int ) : InputStream = {
    println("removeDevice called with: " + id)
    this.getClass.getClassLoader.getResourceAsStream("empty.json")
  }

  @POST
  @Produces(Array("application/text"))
  @Path("inputs/{id}/discover/")
  def enableDiscovery(@PathParam("id") id : Int) : InputStream = {
    println("enableDiscovery called with: " + id)
    this.getClass.getClassLoader.getResourceAsStream("inputPost.json")
  }

  @DELETE
  @Produces(Array("application/text"))
  @Path("inputs/{id}/discover/")
  def disableDiscovery(@PathParam("id") id : Int) : InputStream = {
    println("disableDiscovery called with: " + id)
    this.getClass.getClassLoader.getResourceAsStream("inputPost.json")
  }

  @POST
  @Produces(Array("application/text"))
  @Path("/inputs")
  def inputWithPost(formParams : MultivaluedMap[String, String] ) : InputStream = {
    println("inputWithPost called with: " + formParams)
    this.getClass.getClassLoader.getResourceAsStream("inputPost.json")
  }

  @GET
  @Produces(Array("application/text"))
  @Path("/devices")
  def devices(@Context info: UriInfo) : InputStream = {
    println("devices called with: " + info.getQueryParameters)
    this.getClass.getClassLoader.getResourceAsStream("devices.json")
  }

  @POST
  @Produces(Array("application/text"))
  @Path("/devices")
  def postDevices(@Context info: UriInfo) : InputStream = {
    println("postDevices called with: " + info.getQueryParameters)
    this.getClass.getClassLoader.getResourceAsStream("empty.json")
  }

  @GET
  @Produces(Array("application/text"))
  @Path("/devices/{id}")
  def getDevice(@PathParam("id") id: Int) : InputStream = {
    println("getDevice called with id: " + id)
    this.getClass.getClassLoader.getResourceAsStream("device.json")
  }

  @DELETE
  @Produces(Array("application/text"))
  @Path("/devices/{id}")
  def deleteDevice(@PathParam("id") id: String) : InputStream = {
    println("deleteDevice called with id: " + id)
    this.getClass.getClassLoader.getResourceAsStream("empty.json")
  }
}

@Path("/logs.loggly.com/")
class APIAsyncMock {
  @POST
  @Produces(Array("application/text"))
  @Path("/inputs/{id}")
  def getDevice(@PathParam("id") id: String) : InputStream = {
    println("logger called with id: " + id)
    this.getClass.getClassLoader.getResourceAsStream("log.json")
  }

}