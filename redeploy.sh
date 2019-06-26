docker-compose down --volumes
docker network rm cvt-oss 
docker network create cvt-oss 
mvn clean package 
docker-compose up --build -d
docker logs -f invoice-pdf-analyzer-container