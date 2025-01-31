# Microsserviço de Gerenciamento de Eventos

Esse microsserviço é responsável pela criação e gerenciamento de eventos na API.

## Requisitos

- Java 17
- SpringBoot 3.3.7
- MongoDB

## Rotas

| **Method** | **Base path**                       |  
|------------|-------------------------------------|  
| POST       | /create-event                       |  
| GET        | /get-event/{id}                     |
| GET        | /get-by-name/{eventName}            |
| GET        | /get-all-events                     |  
| GET        | /get-all-events/sorted              |  
| PUT        | /update-event/{id}                  |  
| DELETE     | /delete-event/{id}                  |  

## Como executar apenas este microsserviço

Para funcionamento pleno, o micro-serviço deve ser executado através do docker, utilizando o seguinte comando no terminal:

<small>OBS: A partir da pasta raíz do projeto</small>

```
docker-compose up --build
```

## Exemplos de requisições

### POST /create-event
```
POST http://localhost:8082/eventManagement/v1/create-event  
Content-Type: application/json  

{  
  "eventName": "Festa",  
  "dateTime": "2025-01-22T18:57:42.934Z",  
  "cep": "91790-017"  
}  
```

### GET /get-event/{id}

```
GET http://localhost:8082/eventManagement/v1/get-event/1
```

### GET /get-by-name/{name}

```
GET http://localhost:8082/eventManagement/v1/get-by-name/Festa
```

### GET /get-all-events

```
GET http://localhost:8082/eventManagement/v1/get-all-events  
```

### GET /get-all-events/sorted

```
GET http://localhost:8082/eventManagement/v1/get-all-events/sorted
```

### PUT /update-event/{id}

```
PUT http://localhost:8082/eventManagement/v1/update-event/1  
Content-Type: application/json  

{  
  "eventName": "Festa Atualizada",  
  "dateTime": "2025-01-23T20:00:00.000Z",  
  "cep": "91790-017"  
}  
```
