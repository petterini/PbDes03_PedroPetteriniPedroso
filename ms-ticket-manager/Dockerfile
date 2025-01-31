# Etapa 1: Build do projeto com Maven
FROM maven:3-openjdk-17 AS build

# Copia o código-fonte e o arquivo de dependências
COPY src /ms-ticket-manager/src
COPY pom.xml /ms-ticket-manager

# Define o diretório de trabalho e executa o build
WORKDIR /ms-ticket-manager
RUN mvn clean install -DskipTests

# Etapa 2: Imagem final para rodar a aplicação
FROM openjdk:17-alpine

# Copia o arquivo JAR gerado na etapa de build para a imagem final
COPY --from=build /ms-ticket-manager/target/ms-ticket-manager-0.0.1-SNAPSHOT.jar /app/ms-ticket-manager.jar

# Define o diretório de trabalho e expõe a porta
WORKDIR /app
EXPOSE 8081

# Comando para rodar a aplicação
CMD ["java", "-jar", "ms-ticket-manager.jar"]
