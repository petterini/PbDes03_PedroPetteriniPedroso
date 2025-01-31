# Etapa 1: Build do projeto com Maven
FROM maven:3-openjdk-17 AS build

# Copia o código-fonte e o arquivo de dependências
COPY src /ms-event-manager/src
COPY pom.xml /ms-event-manager

# Define o diretório de trabalho e executa o build
WORKDIR /ms-event-manager
RUN mvn clean install -DskipTests

# Etapa 2: Imagem final para rodar a aplicação
FROM openjdk:17-alpine

# Copia o arquivo JAR gerado na etapa de build para a imagem final
COPY --from=build /ms-event-manager/target/ms-event-manager-0.0.1-SNAPSHOT.jar /app/ms-event-manager.jar

# Define o diretório de trabalho e expõe a porta
WORKDIR /app
EXPOSE 8082

# Comando para rodar a aplicação
CMD ["java", "-jar", "ms-event-manager.jar"]
