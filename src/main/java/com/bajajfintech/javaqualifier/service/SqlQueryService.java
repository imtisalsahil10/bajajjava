package com.bajajfintech.javaqualifier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SqlQueryService {

    private static final Logger logger = LoggerFactory.getLogger(SqlQueryService.class);

    /**
     * Determines which question to solve based on registration number
     * @param regNo Registration number
     * @return true if Question 1 (odd), false if Question 2 (even)
     */
    public boolean isQuestionOne(String regNo) {
        try {
            // Extract last two digits
            String lastTwoDigits = regNo.substring(regNo.length() - 2);
            int lastTwo = Integer.parseInt(lastTwoDigits);
            boolean isOdd = (lastTwo % 2) == 1;
            logger.info("RegNo: {}, Last two digits: {}, Is Question 1 (odd): {}", regNo, lastTwo, isOdd);
            return isOdd;
        } catch (Exception e) {
            logger.error("Error parsing regNo: {}", regNo, e);
            // Default to Question 2 if parsing fails
            return false;
        }
    }

    /**
     * Generates SQL query for Question 2:
     * For every department, calculate the average age of individuals with salaries exceeding 770,000,
     * and produce a concatenated string containing at most 10 of their names.
     */
    public String generateQuestion2Query() {
        logger.info("Generating SQL query for Question 2");
        
        // SQL query to solve Question 2 using SQL Server syntax
        String query = """
            WITH EmployeeTotalSalary AS (
                SELECT 
                    e.EMP_ID,
                    e.FIRST_NAME,
                    e.LAST_NAME,
                    e.DOB,
                    e.DEPARTMENT,
                    SUM(p.AMOUNT) AS TOTAL_SALARY
                FROM EMPLOYEE e
                INNER JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID
                GROUP BY e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, e.DOB, e.DEPARTMENT
                HAVING SUM(p.AMOUNT) > 770000
            ),
            EmployeeWithAge AS (
                SELECT 
                    ets.EMP_ID,
                    ets.FIRST_NAME,
                    ets.LAST_NAME,
                    ets.DEPARTMENT,
                    DATEDIFF(YEAR, ets.DOB, GETDATE()) AS AGE,
                    ROW_NUMBER() OVER (PARTITION BY ets.DEPARTMENT ORDER BY ets.FIRST_NAME, ets.LAST_NAME) AS RN
                FROM EmployeeTotalSalary ets
            ),
            DepartmentStats AS (
                SELECT 
                    d.DEPARTMENT_ID,
                    d.DEPARTMENT_NAME,
                    AVG(CAST(ewa.AGE AS DECIMAL(10, 2))) AS AVERAGE_AGE
                FROM DEPARTMENT d
                INNER JOIN EmployeeWithAge ewa ON d.DEPARTMENT_ID = ewa.DEPARTMENT
                GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME
            ),
            EmployeeList AS (
                SELECT DISTINCT
                    ewa.DEPARTMENT,
                    STUFF((
                        SELECT ', ' + e2.FIRST_NAME + ' ' + e2.LAST_NAME
                        FROM EmployeeWithAge e2
                        WHERE e2.DEPARTMENT = ewa.DEPARTMENT AND e2.RN <= 10
                        ORDER BY e2.FIRST_NAME, e2.LAST_NAME
                        FOR XML PATH(''), TYPE
                    ).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS EMPLOYEE_LIST
                FROM EmployeeWithAge ewa
                WHERE ewa.RN <= 10
            )
            SELECT 
                ds.DEPARTMENT_NAME,
                ds.AVERAGE_AGE,
                COALESCE(el.EMPLOYEE_LIST, '') AS EMPLOYEE_LIST
            FROM DepartmentStats ds
            LEFT JOIN EmployeeList el ON ds.DEPARTMENT_ID = el.DEPARTMENT
            ORDER BY ds.DEPARTMENT_ID DESC
            """;

        return query.trim();
    }

    /**
     * Generates SQL query for Question 1 (placeholder - would need actual question details)
     */
    public String generateQuestion1Query() {
        logger.info("Generating SQL query for Question 1");
        // Placeholder - would need actual question details from the provided link
        return "SELECT 'Question 1 - Implementation pending' AS result";
    }

    /**
     * Gets the appropriate SQL query based on the question number
     */
    public String getSqlQuery(String regNo) {
        if (isQuestionOne(regNo)) {
            return generateQuestion1Query();
        } else {
            return generateQuestion2Query();
        }
    }
}
