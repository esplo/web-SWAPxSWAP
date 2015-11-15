package models

import com.mongodb.casbah.query.Imports._
import com.mongodb.casbah.{MongoCollection, MongoClient, MongoClientURI}
import com.novus.salat.dao._
import esplo.currency.SwapInfoForDB
import models.mongoContext._
import play.api.Play.current


// Database accessor
object SwapInfoDAO extends SalatDAO[SwapInfoForDB, ObjectId](
  collection = {
    def getConfOrElse(name: String, default: String): String = {
      current.configuration.getString(name).getOrElse(default)
    }

    val uri = {
      val uriSetting = getConfOrElse("mongodb.default.uri", "")
      if (uriSetting != "") {
        println("========= " + uriSetting)
        MongoClientURI(uriSetting)
      }
      else {

        println("========= kkkk")
        val database = getConfOrElse("mongodb.default.database", "")
        val host = getConfOrElse("mongodb.default.host", "")
        val port = getConfOrElse("mongodb.default.port", "")
        val username = getConfOrElse("mongodb.default.username", "")
        val password = getConfOrElse("mongodb.default.password", "")

        MongoClientURI(s"""mongodb://${username}:${password}@${host}:${port}/${database}""")
      }
    }

    val db = if(uri.database.isDefined) {MongoClient(uri)(uri.database.get) } else MongoClient("")("")
    val colname = getConfOrElse("mongodb.default.collection", "")

    // exception is allowed
    if (!db.collectionExists(colname))
      db.createCollection(colname, DBObject())
    db(colname)
  }
)


object Swap {

  /**
   * get swap points corresponding single pair + multi brokers
   */
  def get(pair: Option[String], brokers: List[String]): List[SwapInfoForDB] = {

    def getPairOrQuery: DBObject = {
      if (pair.isDefined)
        "pair" $eq pair.get
      else
        DBObject()
    }
    def getBrokerOrQuery: DBObject = {
      if (brokers.nonEmpty)
        $or(brokers.map("broker" $eq _))
      else
        DBObject()
    }

    val query = $and(List(getPairOrQuery, getBrokerOrQuery))

    SwapInfoDAO
      .find(query)
      .sort(orderBy = MongoDBObject("date" -> 1))
      .toList
  }

  // get all pairs in the database
  def allPairs(): List[String] = {
    SwapInfoDAO
      .collection.distinct("pair")
      .map(_.toString)
      .toList
  }

  // get all brokersin the database
  def allBrokers(): List[String] = {
    SwapInfoDAO
      .collection.distinct("broker")
      .map(_.toString)
      .toList
  }
}