# Microsserviço de Gerenciamento de Eventos

Esse microsserviço é responsável pela criação e gerenciamento de tickets na API.

## Requisitos

- Java 17
- SpringBoot 3.3.7
- MongoDB

## Rotas

| **Method** | **Base path**                                  |  
|------------|------------------------------------------------|  
| POST       | /create-ticket                                 |  
| GET        | /get-ticket/{id}                               |  
| PUT        | /update-ticket/{id}                            |  
| DELETE     | /cancel-ticket/{id}                            |  
| GET        | /check-tickets-by-event/{eventId}              |  

## Como executar apenas este microsserviço

Para funcionamento pleno, o micro-serviço deve ser executado através do docker, utilizando o seguinte comando no terminal:

<small>OBS: A partir da pasta raíz do projeto</small>

```
docker-compose up --build
```

## Exemplos de requisições

### POST /create-ticket
```
POST http://localhost:8081/ticketManagement/v1/create-ticket  
Content-Type: application/json  

{  
  "customerName": "Pedro Petterini",  
  "cpf": "04316593030",  
  "customerMail": "ppetterini12432@gmail.com",  
  "eventId": "679c2cd988610435a0848",  
  "eventName": "Festa1",  
  "brlAmount": "50.00",  
  "usdAmount": "10.00"  
}  

```

### GET /get-ticket/{id}

```
GET http://localhost:8081/ticketManagement/v1/get-ticket/1  
```

### GET /get-all-tickets

```
GET http://localhost:8081/ticketManagement/v1/get-all-tickets  
```

### GET /check-tickets-by-event/{eventId}

```
GET http://localhost:8081/ticketManagement/v1/get-tickets-by-event/679c2cd988610435a0848
```

### PUT /update-ticket/{id}

```
PUT http://localhost:8081/ticketManagement/v1/update-ticket/1  
Content-Type: application/json  

{  
  "customerName": "Pedro Petterini Jr.",  
  "cpf": "04316593030",  
  "customerMail": "ppetterini_updated@gmail.com",  
  "brlAmount": "60.00",  
  "usdAmount": "12.00"  
}  

```

## Cobertura de testes

![Cobertura de testes](test-coverage.png)
