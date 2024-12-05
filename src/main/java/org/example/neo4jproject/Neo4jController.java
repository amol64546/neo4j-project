package org.example.neo4jproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/neo4j")
public class Neo4jController {

  private final Neo4jDatabaseService neo4jDatabaseService;

  @Autowired
  public Neo4jController(Neo4jDatabaseService neo4jDatabaseService) {
    this.neo4jDatabaseService = neo4jDatabaseService;
  }

  @PostMapping("/switchDatabase")
  public String switchDatabase(@RequestParam String dbName) {
    return neo4jDatabaseService.ensureDatabaseExists(dbName);
  }

  @PostMapping("/createNode")
  public String createNode(String dbName) {
    neo4jDatabaseService.createNode(dbName);
    return "Node created successfully!";
  }

  @PostMapping("/createRelationship")
  public String createRelationship(String dbName) {
    neo4jDatabaseService.createRelationship(dbName);
    return "Relationship created successfully!";
  }
}
