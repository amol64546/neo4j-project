package org.example.neo4jproject;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class Neo4jDatabaseService {

  private static final Logger logger = LoggerFactory.getLogger(Neo4jDatabaseService.class);
  private final Driver driver;

  @Autowired
  public Neo4jDatabaseService(Driver driver) {
    this.driver = driver;
  }


  public void executeQueryWithParams(String databaseName, String query, Map<String, Object> parameters) {
    try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
      // Execute the Cypher query with parameters
      Result result = session.run(query, parameters);

      // Process the result
      while (result.hasNext()) {
        System.out.println(result.next());
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      driver.close();
    }
  }

  public void executeQuery(String databaseName, String query) {
    try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
      // Execute the Cypher query
      Result result = session.run(query);

      // Process the result (if needed)
      while (result.hasNext()) {
        System.out.println(result.next());
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      driver.close();
    }
  }

  public String ensureDatabaseExists(String databaseName) {
    try (Session session = driver.session(SessionConfig.forDatabase("system"))) {
      // Query to list all databases
      String showDatabasesQuery = "SHOW DATABASES";
      Result result = session.run(showDatabasesQuery);

      // Get the list of database names from the result
      boolean databaseExists = false;
      while (result.hasNext()) {
        org.neo4j.driver.Record record = result.next();
        String dbName = record.get("name").asString();
        if (dbName.equals(databaseName)) {
          databaseExists = true;
          break;
        }
      }

      // If the database doesn't exist, create it
      if (!databaseExists) {
        logger.info("Database '{}' does not exist. Creating it...", databaseName);
        String createDbQuery = "CREATE DATABASE `" + databaseName + "`";
        session.run(createDbQuery);
        logger.info("Database '{}' created successfully.", databaseName);
        return String.format("Database %s created successfully.", databaseName);
      } else {
        logger.info("Database '{}' already exists.", databaseName);
        return String.format("Database %s already exists.", databaseName);
      }
    } catch (Exception e) {
      logger.error("Error while ensuring database existence: ", e);
      throw new RuntimeException(e);
    }
  }

  // Create a new node
//    @Transactional
  public void createNode(String dbName) {
    ensureDatabaseExists(dbName);
    String cypherQuery = "CREATE (p:Person {name: 'John Doe', age: 30})";
    executeQuery(dbName, cypherQuery);
  }



  // Create a relationship
//    @Transactional
  public void createRelationship(String dbName) {
    ensureDatabaseExists(dbName);
    String cypherQuery = "MATCH (p:Person), (m:Movie) " +
        "WHERE p.name = 'John Doe' AND m.title = 'Inception' " +
        "CREATE (p)-[:ACTED_IN]->(m)";
    executeQuery(dbName, cypherQuery);
  }
}
