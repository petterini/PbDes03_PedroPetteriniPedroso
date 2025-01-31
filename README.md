# Desafio 3 - PB Compass - Pedro Petterini

O desafio implementa 2 microsserviços para gerenciamento de eventos e tickets.

## Microsserviços

- [ms-event-manager/](./ms-event-manager/)
- [ms-ticket-manager/](./ms-ticket-manager/)


## Execução

Para executar os serviços de forma plena pela primeira vez, deve-se executá-los através do docker na pasta raíz do projeto(atual), com o comando:

```
docker-compose up --build
```

A partir da segunda vez, se não houver mudanças no código, o programa pode ser executado com o comando:

```
docker-compose up -d
```

OBS¹: Para fazer requisições, o ms-event funciona na porta 8082, enquanto o ms-ticket funciona na porta 8081.

OBS²: Abaixo segue vídeo gravado para mostrar o funcionamento do mesmo na AWS. 

https://compasso-my.sharepoint.com/:v:/r/personal/pedro_pedroso_pb_compasso_com_br/Documents/Arquivos%20de%20Chat%20do%20Microsoft%20Teams/2025-01-31%2016-34-14.mkv?csf=1&web=1&e=MAgb4o
