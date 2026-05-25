@echo off
cd nodes
docker cp Category.csv neo4j:/var/lib/neo4j/import/Category.csv
docker cp Customer.csv neo4j:/var/lib/neo4j/import/Customer.csv
docker cp Employee.csv neo4j:/var/lib/neo4j/import/Employee.csv
docker cp Order.csv neo4j:/var/lib/neo4j/import/Order.csv
docker cp Product.csv neo4j:/var/lib/neo4j/import/Product.csv
docker cp Region.csv neo4j:/var/lib/neo4j/import/Region.csv
docker cp Shipper.csv neo4j:/var/lib/neo4j/import/Shipper.csv
docker cp Supplier.csv neo4j:/var/lib/neo4j/import/Supplier.csv
docker cp Territory.csv neo4j:/var/lib/neo4j/import/Territory.csv
cd ..\relations 
docker cp Customer-PURCHASED-Order.csv neo4j:/var/lib/neo4j/import/Customer-PURCHASED-Order.csv
docker cp Employee-IN_TERRITORY-Territory.csv neo4j:/var/lib/neo4j/import/Employee-IN_TERRITORY-Territory.csv
docker cp Employee-REPORTS_TO-Employee.csv neo4j:/var/lib/neo4j/import/Employee-REPORTS_TO-Employee.csv
docker cp Employee-SOLD-Order.csv neo4j:/var/lib/neo4j/import/Employee-SOLD-Order.csv
docker cp Order-CONTAINS-Product.csv neo4j:/var/lib/neo4j/import/Order-CONTAINS-Product.csv
docker cp Order-ORDERS-Product.csv neo4j:/var/lib/neo4j/import/Order-ORDERS-Product.csv
docker cp Product-PART_OF-Category.csv neo4j:/var/lib/neo4j/import/Product-PART_OF-Category.csv
docker cp Shipper-SHIPS-Order.csv neo4j:/var/lib/neo4j/import/Shipper-SHIPS-Order.csv
docker cp Supplier-SUPPLIES-Product.csv neo4j:/var/lib/neo4j/import/Supplier-SUPPLIES-Product.csv
docker cp Territory-IN_REGION-Region.csv neo4j:/var/lib/neo4j/import/Territory-IN_REGION-Region.csv
cd ..
pause