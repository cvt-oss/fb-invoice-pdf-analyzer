docker-compose down --volumes
mvn clean package 
docker-compose up --build -d
docker logs -f invoice-pdf-analyzer-container