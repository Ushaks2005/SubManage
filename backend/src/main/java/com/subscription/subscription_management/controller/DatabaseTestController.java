
package com.subscription.subscription_management;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Controller
public class DatabaseTestController {

    private final DataSource dataSource;

    public DatabaseTestController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/test-db")
    @ResponseBody
    public String testDatabase() {

        try (
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(
                "SELECT COUNT(*) FROM customers"
            )
        ) {
            result.next();

            int totalCustomers = result.getInt(1);

            return "Database connected successfully! Total customers: "
                    + totalCustomers;

        } catch (Exception e) {

            e.printStackTrace();

            return "Database connection failed. Check the VS Code terminal.";
        }
    }
}
